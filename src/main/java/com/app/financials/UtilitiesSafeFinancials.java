package com.app.financials;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import smarty.core.CoreUtilities;


public class UtilitiesSafeFinancials extends UtilitiesFinancials{
	private static boolean isRevertFound(Connection a_conn,
			int a_finTransactionId,
			int a_accountantBoxId,
			int a_revertAccountBoxTransactionId)throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		if (a_revertAccountBoxTransactionId == 0) {
			return false;
		}
			
		try {
			pst = a_conn.prepareStatement("select 1 from p_acctbox_transactions"
					+ " where abt_paymentid = ? and abt_acctboxid=? and abt_revert_transaction=?");
			pst.setInt(1, a_finTransactionId);
			pst.setInt(2, a_accountantBoxId);
			pst.setInt(3, a_revertAccountBoxTransactionId);
			rs = pst.executeQuery();
			if (rs.next()) {
				return true;
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return false;
	}
	/**
	 * Retrieve account box info per user , if not found create new account
	 * @param a_conn
	 * @param a_userId
	 * @param a_branchId
	 * @return
	 * @throws Exception
	 */
	public static AccountantBoxBean GetAccountantBox (Connection a_conn, int a_boxForUser, int a_branchId) throws Exception{
		AccountantBoxBean accountantBoxBean = new AccountantBoxBean();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = a_conn.prepareStatement("select acb_id, acb_balance_iqd, acb_balance_usd from p_accountantbox where acb_usid=? and acb_userbranchid = ?");
			pst.setInt(1, a_boxForUser);
			pst.setInt(2, a_branchId);
			rs = pst.executeQuery();
			boolean found = false;
			int countOfBox = 0;
			while(rs.next()) {
				found = true;
				accountantBoxBean.setBoxId(rs.getInt("acb_id"));
				accountantBoxBean.setCurrentBalanceIqd(rs.getLong("acb_balance_iqd"));
				accountantBoxBean.setCurrentBalanceUsd(rs.getLong("acb_balance_usd"));
				countOfBox +=1;
				if(countOfBox>1) 
					break;
			}
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
			if(countOfBox>1) 
				throw new Exception ("حصل خطأ : هناك اكثر من صندوق لنفس المحاسب");
			
			if (!found){ // if no account box found, create one
				accountantBoxBean.setBoxId(createAccountantBox( a_conn, a_boxForUser, a_branchId));
				accountantBoxBean.setCurrentBalanceIqd(0);
				accountantBoxBean.setCurrentBalanceUsd(0);
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch (Exception e) {}
			try {pst.close();}catch (Exception e) {}
		}
		return accountantBoxBean;
	}
	
	
	public static AccountantBoxBean GetAccountantBoxForBoxId (Connection a_conn, int a_boxId, int a_branchId) throws Exception{
		AccountantBoxBean accountantBoxBean = new AccountantBoxBean();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = a_conn.prepareStatement("select acb_id, acb_balance_iqd, acb_balance_usd "
					+ "from p_accountantbox where acb_id=? and acb_userbranchid = ?");
			pst.setInt(1, a_boxId);
			pst.setInt(2, a_branchId);
			rs = pst.executeQuery();
			if(rs.next()) {
				accountantBoxBean.setBoxId(rs.getInt("acb_id"));
				accountantBoxBean.setCurrentBalanceIqd(rs.getLong("acb_balance_iqd"));
				accountantBoxBean.setCurrentBalanceUsd(rs.getLong("acb_balance_usd"));
			}
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch (Exception e) {}
			try {pst.close();}catch (Exception e) {}
		}
		return accountantBoxBean;
	}
	/**
	 * Retrieve account box info per user , if not found create new account
	 * @param a_conn
	 * @param a_userId
	 * @param a_branchId
	 * @return
	 * @throws Exception
	 */
	public static HashMap<StandardFinCurrency, Long> GetAllAccountantBoxesBalancesInBranch
	(Connection a_conn, int a_branchId) throws Exception{
		HashMap<StandardFinCurrency, Long> accountantBoxesBalance = new HashMap<StandardFinCurrency, Long>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = a_conn.prepareStatement("select sum(acb_balance_iqd)as tot_iqd,"
					+ "  sum(acb_balance_usd) as tot_usd "
					+ "from p_accountantbox where  acb_userbranchid = ?");
			pst.setInt(1, a_branchId);
			rs = pst.executeQuery();
			if(rs.next()) {
				accountantBoxesBalance.put(StandardFinCurrency.IQD,rs.getLong("tot_iqd"));
				accountantBoxesBalance.put(StandardFinCurrency.USD,rs.getLong("tot_usd"));
			}
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch (Exception e) {}
			try {pst.close();}catch (Exception e) {}
		}
		return accountantBoxesBalance;
	}
	
	public static int createAcctBoxTransactions(Connection a_conn,
			int a_paymentId, 
			String a_PaymentTable, 
			PaymentImpactOnSafe a_safeImpact, 
			int a_boxForUser,
			long a_paymentIqd,
			long a_paymentUsd,
			int a_branchId,
			String a_actionDescription,
			String a_lookUpTable,
			String a_lookupTablePkColName,
			int a_lookupTablePkValue,
			String a_lookupTableLookupColName,
			String a_transactionEntityName,
			int a_userDidThisTransaction
			) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int accountBoxTransactionId = 0;
		try {
			long amountIqd= a_paymentIqd;
			long amountUsd= a_paymentUsd;
			AccountantBoxBean accountantBoxBean= GetAccountantBox(a_conn, a_boxForUser, a_branchId);
			
//			if(a_paymentIqd<0 || a_paymentUsd<0)
//				throw new ValidationException ("مبلغ العملية لا يمكن أن يكون أقل من صفر"+", a_paymentUsd="+a_paymentUsd+", a_paymentIqd="+a_paymentIqd);
//			
			if (a_safeImpact == PaymentImpactOnSafe.DEDUCT_SAFE) {
				if(accountantBoxBean.getCurrentBalanceIqd()<a_paymentIqd)
					throw new ValidationException ( "لايوجد مبلغ مالي كافي في الصندوق"+", المبلغ في الصندوق = "+accountantBoxBean.getCurrentBalanceIqd()+", الميلغ المطلوب دفعه = "+a_paymentIqd);
				
				if(accountantBoxBean.getCurrentBalanceUsd()<a_paymentUsd)
					throw new ValidationException ( "لايوجد مبلغ مالي دولار كافي في الصندوق"+", المبلغ في الصندوق = "+accountantBoxBean.getCurrentBalanceUsd()+", الميلغ المطلوب دفعه = "+a_paymentUsd);
				
				amountIqd *=-1; // since its reduction
				amountUsd *=-1;
			}else if (a_safeImpact == PaymentImpactOnSafe.NOSAFE) {
				return accountBoxTransactionId;
			}		
			// update the box
			String a_safeImpactString  = a_safeImpact.name();
			pst  = a_conn.prepareStatement("update p_accountantbox "
					+ "set acb_balance_iqd = acb_balance_iqd+?,"
					+ "acb_balance_usd = acb_balance_usd+? "
					+ "where acb_id=? and acb_userbranchid = ?");
			pst.setLong(1, amountIqd);
			pst.setLong(2, amountUsd);
			pst.setInt(3, accountantBoxBean.getBoxId());
			pst.setInt(4, a_branchId);
			pst.executeUpdate();	
			try {pst.close();}catch(Exception e) {}	
			
			
			pst = a_conn.prepareStatement("insert into p_acctbox_transactions "
			+ "(abt_paymentid		  , abt_payment_table	, abt_safe_impact	   		   , abt_createdby		   , abt_acctboxid, "
			+ " abt_balance_iqd_before, abt_payment_iqd	 	, abt_balance_iqd_after		   , abt_balance_usd_before, abt_payment_usd, "
			+ " abt_balance_usd_after , abt_userbranchid 	, abt_action_desc	   		   , abt_table			   , abt_table_pkcol, "
			+ " abt_table_pkval       , abt_table_lookup_col, abt_entity_full_name_trans_id, abt_date		)"
			  + " values ("+CoreUtilities.getQuestionMarks(18)+", now() )", Statement.RETURN_GENERATED_KEYS);
			pst.setInt(1, a_paymentId);
			pst.setString(2, a_PaymentTable);
			pst.setString(3, a_safeImpactString);
			pst.setInt(4, a_userDidThisTransaction);
			pst.setInt(5, accountantBoxBean.getBoxId());
			pst.setLong(6, accountantBoxBean.getCurrentBalanceIqd());
			pst.setLong(7, a_paymentIqd);
			pst.setLong(8, accountantBoxBean.getCurrentBalanceIqd() + amountIqd);
			pst.setLong(9, accountantBoxBean.getCurrentBalanceUsd());
			pst.setLong(10, a_paymentUsd);
			pst.setLong(11, accountantBoxBean.getCurrentBalanceUsd() + amountUsd);
			pst.setInt(12, a_branchId);
			pst.setString(13, a_actionDescription);
			pst.setString(14, a_lookUpTable);
			pst.setString(15, a_lookupTablePkColName);
			pst.setInt(16, a_lookupTablePkValue);
			pst.setString(17, a_lookupTableLookupColName);
			pst.setString(18, a_transactionEntityName);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			rs.next();
			accountBoxTransactionId = rs.getInt(1);
		}catch(ValidationException ve) {
			throw ve;
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();} catch(Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return accountBoxTransactionId;
	}
	
	
	public static void revertAcctBoxTransaction(Connection a_conn,
			int a_boxTransactionIdToBeRevereted,
			int a_userId,
			int a_branchId,
			String a_reasonForRevert) throws Exception {
		PreparedStatement pst = null;
		try {
			long amountToRestoreIqd= 0;
			long amountToRestoreUsd= 0;
			String toBeRevertedSafeImpactCode = null;
			
			//get current Account box
			AccountantBoxBean accountantBoxBean= GetAccountantBox(a_conn, a_userId, a_branchId);
			//get transaction to be reverted
			AccountBoxTransactionBean accountBoxTransactionBean = getAccountBoxTransactionDetails(a_conn, a_boxTransactionIdToBeRevereted, a_branchId);
			
			if (accountBoxTransactionBean == null) { // our transaction box is not impacted by this
				return;
			}
			
			if (accountBoxTransactionBean.getPaymentImpactOnSafe() == PaymentImpactOnSafe.NOSAFE) {
				return;
			}
			if (accountBoxTransactionBean.getMovedToSafeId()>0) {
				throw new ValidationException("لقد تم نقل مبلغ هذة العملية الى القاصة وبذلك لا يمكن التراجع عنها");
			}
			
			if (accountBoxTransactionBean.getPaymentImpactOnSafe() == PaymentImpactOnSafe.ADD_SAFE) {
				amountToRestoreIqd= accountBoxTransactionBean.getPaymentIqd()*-1;
				amountToRestoreUsd= accountBoxTransactionBean.getPaymentUsd()*-1;
				
				if(accountBoxTransactionBean.getPaymentIqd() > accountantBoxBean.getCurrentBalanceIqd()) {
					throw new ValidationException ( "لايوجد مبلغ مالي كافي في الصندوق"+", المبلغ في الصندوق = "+accountantBoxBean.getCurrentBalanceIqd()+", الميلغ المطلوب استرداده = "+accountBoxTransactionBean.getPaymentIqd());
				}
				if(accountBoxTransactionBean.getPaymentUsd() > accountantBoxBean.getCurrentBalanceUsd()) {
					throw new ValidationException ( "لايوجد مبلغ مالي كافي في الصندوق"+", المبلغ في الصندوق = "+accountantBoxBean.getCurrentBalanceIqd()+", الميلغ المطلوب استرداده = "+accountBoxTransactionBean.getPaymentIqd());
				}
			}else if (accountBoxTransactionBean.getPaymentImpactOnSafe() == PaymentImpactOnSafe.DEDUCT_SAFE) {
				amountToRestoreIqd= accountBoxTransactionBean.getPaymentIqd();
				amountToRestoreUsd= accountBoxTransactionBean.getPaymentUsd();
			}
			if (accountBoxTransactionBean.getPaymentImpactOnSafe() == PaymentImpactOnSafe.DEDUCT_SAFE) {  // get the opposite of the reverted transaction
				toBeRevertedSafeImpactCode = PaymentImpactOnSafe.ADD_SAFE.name();
			}else {
				toBeRevertedSafeImpactCode = PaymentImpactOnSafe.DEDUCT_SAFE.name();
			}
			// update the box			
			pst  = a_conn.prepareStatement("update p_accountantbox "
					+ "set acb_balance_iqd = acb_balance_iqd+?,"
					+ "acb_balance_usd = acb_balance_usd+? "
					+ "where acb_id=? and acb_userbranchid = ?");
			pst.setLong(1, amountToRestoreIqd);
			pst.setLong(2, amountToRestoreUsd);
			pst.setInt(3, accountantBoxBean.getBoxId());
			pst.setInt(4, a_branchId);
			pst.executeUpdate();	
			try {pst.close();}catch(Exception e) {}	
			
			//check if transaction is reverted already
			if (isRevertFound(a_conn, 
					accountBoxTransactionBean.getPaymentIdInSource(),
					accountantBoxBean.getBoxId(),
					accountBoxTransactionBean.getId())){
				throw new Exception ("Transaction in accountant box is already reveted");
			}
			
			pst = a_conn.prepareStatement("insert into p_acctbox_transactions "
			+ "(abt_paymentid		  , abt_payment_table, abt_safe_impact		 , abt_createdby		 , abt_acctboxid, "
			+ " abt_balance_iqd_before, abt_payment_iqd	 , abt_balance_iqd_after , abt_balance_usd_before, abt_payment_usd, "
			+ " abt_balance_usd_after , abt_userbranchid , abt_action_desc		 , abt_table_pkcol 		 , abt_table_lookup_col, "
			+ "	abt_table_pkval	  	  , abt_table 		 , abt_revert_transaction, abt_entity_full_name_trans_id, abt_date)"
			  + " values ("+CoreUtilities.getQuestionMarks(19)+", now() )");
			pst.setInt(1, accountBoxTransactionBean.getPaymentIdInSource());
			pst.setString(2, accountBoxTransactionBean.getSourceTableOfPayment());
			pst.setString(3, toBeRevertedSafeImpactCode);
			pst.setInt(4, a_userId);
			pst.setInt(5, accountantBoxBean.getBoxId());
			pst.setLong(6, accountantBoxBean.getCurrentBalanceIqd());
			pst.setLong(7, accountBoxTransactionBean.getPaymentIqd());
			pst.setLong(8, accountantBoxBean.getCurrentBalanceIqd() + amountToRestoreIqd);
			pst.setLong(9, accountantBoxBean.getCurrentBalanceUsd());
			pst.setLong(10, accountBoxTransactionBean.getPaymentUsd());
			pst.setLong(11, accountantBoxBean.getCurrentBalanceUsd() + amountToRestoreUsd);
			pst.setInt(12, a_branchId);
			pst.setString(13, a_reasonForRevert);
			pst.setString(14, accountBoxTransactionBean.getLookUpPkColName());
			pst.setString(15, accountBoxTransactionBean.getLookupTableColumnDesc());
			pst.setInt(16, accountBoxTransactionBean.getLookUpPkColVal());
			pst.setString(17, accountBoxTransactionBean.getLookupTable());
			pst.setInt(18, accountBoxTransactionBean.getId());
			pst.setString(19, accountBoxTransactionBean.getEntityFullNameWithTransId());
			pst.executeUpdate();
		}catch(ValidationException ve) {
			throw ve;
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {pst.close();} catch (Exception e) {}
		}
	}
	
	public static void revertAcctBoxTransactionForSafe(Connection a_conn,
			int a_accountBoxId,
			int a_boxTransactionIdToBeRevereted,
			int a_userId,
			int a_branchId,
			String a_reasonForRevert) throws Exception {
		PreparedStatement pst = null;
		try {
			long amountToRestoreIqd= 0;
			long amountToRestoreUsd= 0;
			String toBeRevertedSafeImpactCode = null;
			
			//get current Account box
			AccountantBoxBean accountantBoxBean= GetAccountantBoxForBoxId(a_conn, a_accountBoxId, a_branchId);
			//get transaction to be reverted
			AccountBoxTransactionBean accountBoxTransactionBean = 
					getAccountBoxTransactionDetails(a_conn, a_boxTransactionIdToBeRevereted, a_branchId);
			
			if (accountBoxTransactionBean.getMovedToSafeId()>0) {
				throw new ValidationException("لقد تم نقل مبلغ هذة العملية الى القاصة وبذلك لا يمكن التراجع عنها");
			}
			
			if (accountBoxTransactionBean.getPaymentImpactOnSafe() == PaymentImpactOnSafe.NOSAFE) {
				return;
			}else if (accountBoxTransactionBean.getPaymentImpactOnSafe() == PaymentImpactOnSafe.ADD_SAFE) {
				amountToRestoreIqd= accountBoxTransactionBean.getPaymentIqd()*-1;
				amountToRestoreUsd= accountBoxTransactionBean.getPaymentUsd()*-1;
				
				if(accountBoxTransactionBean.getPaymentIqd() > accountantBoxBean.getCurrentBalanceIqd()) {
					throw new ValidationException ( "لايوجد مبلغ مالي كافي في الصندوق"+", المبلغ في الصندوق = "+accountantBoxBean.getCurrentBalanceIqd()+", الميلغ المطلوب استرداده = "+accountBoxTransactionBean.getPaymentIqd());
				}
				if(accountBoxTransactionBean.getPaymentUsd() > accountantBoxBean.getCurrentBalanceUsd()) {
					throw new ValidationException ( "لايوجد مبلغ مالي كافي في الصندوق"+", المبلغ في الصندوق = "+accountantBoxBean.getCurrentBalanceIqd()+", الميلغ المطلوب استرداده = "+accountBoxTransactionBean.getPaymentIqd());
				}
			}else if (accountBoxTransactionBean.getPaymentImpactOnSafe() == PaymentImpactOnSafe.DEDUCT_SAFE) {
				amountToRestoreIqd= accountBoxTransactionBean.getPaymentIqd();
				amountToRestoreUsd= accountBoxTransactionBean.getPaymentUsd();
			}
			if (accountBoxTransactionBean.getPaymentImpactOnSafe() == PaymentImpactOnSafe.DEDUCT_SAFE) {  // get the opposite of the reverted transaction
				toBeRevertedSafeImpactCode = PaymentImpactOnSafe.ADD_SAFE.name();
			}else {
				toBeRevertedSafeImpactCode = PaymentImpactOnSafe.DEDUCT_SAFE.name();
			}
			// update the box			
			pst  = a_conn.prepareStatement("update p_accountantbox "
					+ "set acb_balance_iqd = acb_balance_iqd+?,"
					+ "acb_balance_usd = acb_balance_usd+? "
					+ "where acb_id=? and acb_userbranchid = ?");
			pst.setLong(1, amountToRestoreIqd);
			pst.setLong(2, amountToRestoreUsd);
			pst.setInt(3, accountantBoxBean.getBoxId());
			pst.setInt(4, a_branchId);
			pst.executeUpdate();	
			try {pst.close();}catch(Exception e) {}	
			
			//check if transaction is reverted already
			if (isRevertFound(a_conn, 
					accountBoxTransactionBean.getPaymentIdInSource(),
					accountantBoxBean.getBoxId(),
					accountBoxTransactionBean.getId())){
				throw new Exception ("Transaction in accountant box is already reveted");
			}
			
			pst = a_conn.prepareStatement("insert into p_acctbox_transactions "
			+ "(abt_paymentid		  , abt_payment_table, abt_safe_impact		 , abt_createdby		 , abt_acctboxid, "
			+ " abt_balance_iqd_before, abt_payment_iqd	 , abt_balance_iqd_after , abt_balance_usd_before, abt_payment_usd, "
			+ " abt_balance_usd_after , abt_userbranchid , abt_action_desc		 , abt_table_pkcol 		 , abt_table_lookup_col, "
			+ "	abt_table_pkval	  	  , abt_table 		 , abt_revert_transaction, abt_entity_full_name_trans_id, abt_date)"
			  + " values ("+CoreUtilities.getQuestionMarks(19)+", now() )");
			pst.setInt(1, accountBoxTransactionBean.getPaymentIdInSource());
			pst.setString(2, accountBoxTransactionBean.getSourceTableOfPayment());
			pst.setString(3, toBeRevertedSafeImpactCode);
			pst.setInt(4, a_userId);
			pst.setInt(5, accountantBoxBean.getBoxId());
			pst.setLong(6, accountantBoxBean.getCurrentBalanceIqd());
			pst.setLong(7, accountBoxTransactionBean.getPaymentIqd());
			pst.setLong(8, accountantBoxBean.getCurrentBalanceIqd() + amountToRestoreIqd);
			pst.setLong(9, accountantBoxBean.getCurrentBalanceUsd());
			pst.setLong(10, accountBoxTransactionBean.getPaymentUsd());
			pst.setLong(11, accountantBoxBean.getCurrentBalanceUsd() + amountToRestoreUsd);
			pst.setInt(12, a_branchId);
			pst.setString(13, a_reasonForRevert);
			pst.setString(14, accountBoxTransactionBean.getLookUpPkColName());
			pst.setString(15, accountBoxTransactionBean.getLookupTableColumnDesc());
			pst.setInt(16, accountBoxTransactionBean.getLookUpPkColVal());
			pst.setString(17, accountBoxTransactionBean.getLookupTable());
			pst.setInt(18, accountBoxTransactionBean.getId());
			pst.setString(19, accountBoxTransactionBean.getEntityFullNameWithTransId());
			pst.executeUpdate();
		}catch(ValidationException ve) {
			throw ve;
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {pst.close();} catch (Exception e) {}
		}
	}
	
	public static AccountBoxTransactionBean getAccountBoxTransactionDetails(Connection a_conn, int a_transId, int a_branchId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		AccountBoxTransactionBean accountBoxTransactionBean = null;
		try {
			pst = a_conn.prepareStatement("select abt_moved_to_safe_withid, abt_paymentid,abt_payment_table, "
					+ "abt_payment_iqd, abt_payment_usd, abt_safe_impact, abt_action_desc  , abt_table_pkcol, abt_table_lookup_col,"
					+ "abt_table_pkval, abt_table, abt_entity_full_name_trans_id "
					+ "from p_acctbox_transactions where abt_id=? and abt_userbranchid=?");
			pst.setInt(1, a_transId);
			pst.setInt(2, a_branchId);
			rs = pst.executeQuery();
			if (rs.next()) {
				accountBoxTransactionBean = new AccountBoxTransactionBean();
				accountBoxTransactionBean.setId(a_transId);
				accountBoxTransactionBean.setPaymentIdInSource(rs.getInt("abt_paymentid"));
				accountBoxTransactionBean.setSourceTableOfPayment(rs.getString("abt_payment_table"));
				accountBoxTransactionBean.setPaymentIqd(rs.getLong("abt_payment_iqd"));
				accountBoxTransactionBean.setPaymentUsd(rs.getLong("abt_payment_usd"));
				accountBoxTransactionBean.setPaymentImpactOnSafe(PaymentImpactOnSafe.valueOf(rs.getString("abt_safe_impact")));
				accountBoxTransactionBean.setLookUpPkColName(rs.getString("abt_table_pkcol"));
				accountBoxTransactionBean.setLookupTableColumnDesc(rs.getString("abt_table_lookup_col"));
				accountBoxTransactionBean.setLookUpPkColVal(rs.getInt("abt_table_pkval"));
				accountBoxTransactionBean.setLookupTable(rs.getString("abt_table"));
				accountBoxTransactionBean.setPaymentDesc(rs.getString("abt_action_desc"));
				accountBoxTransactionBean.setEntityFullNameWithTransId(rs.getString("abt_entity_full_name_trans_id"));
				accountBoxTransactionBean.setMovedToSafeId(rs.getInt("abt_moved_to_safe_withid"));
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return accountBoxTransactionBean;
	}
	
	public static SafePaymentTypeMetaInfoBean getSafePaymentTypeMetaInfoKbgeneral(Connection a_conn, String a_cat1, String a_cat2, String a_code) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = null;
		try {
			pst = a_conn.prepareStatement("select kbdesc, kbcat3, kbcat4 from kbgeneral where kbcat1=? and kbcat2 = ? and kbcode = ?");
			pst.setString(1, a_cat1);
			pst.setString(2, a_cat2);
			pst.setString(3, a_code);
			rs = pst.executeQuery();
			safePaymentTypeMetaInfoBean = new SafePaymentTypeMetaInfoBean();
			if (rs.next()) {
				safePaymentTypeMetaInfoBean.setDbOrCr(PaymentType.valueOf(rs.getString("kbcat3")));
				safePaymentTypeMetaInfoBean.setSafeImpact(PaymentImpactOnSafe.valueOf(rs.getString("kbcat4")));
				safePaymentTypeMetaInfoBean.setName(rs.getString("kbdesc"));
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return safePaymentTypeMetaInfoBean;
	}
	
	
	public static SafePaymentTypeMetaInfoBean getTransactionSafePaymentTypeMetaInfo
	(Connection a_conn, int a_transaction) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = null;
		try {
			safePaymentTypeMetaInfoBean = new SafePaymentTypeMetaInfoBean();
			pst = a_conn.prepareStatement("select kbcat1 , kbcat2, kbcat3, kbcat4,  kbdesc , kbcode "
					+ " from p_fin_transactions join kbgeneral on trans_operationcode = kbcode and  trans_operationentity=kbcat1 "
					+ "and trans_operationcat =kbcat2   where trans_id=?");
			pst.setInt(1, a_transaction);
			rs = pst.executeQuery();
			if(rs.next()) {
				safePaymentTypeMetaInfoBean.setDbOrCr(PaymentType.valueOf(rs.getString("kbcat3")));
				safePaymentTypeMetaInfoBean.setSafeImpact(PaymentImpactOnSafe.valueOf(rs.getString("kbcat4")));
				safePaymentTypeMetaInfoBean.setName(rs.getString("kbdesc"));
				safePaymentTypeMetaInfoBean.setFinOperationCode(FinOperationCode.valueOf(rs.getString("kbcode")));
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return safePaymentTypeMetaInfoBean;
	}
	
	/**
	 * Create account box per user
	 * @param a_conn
	 * @param a_userId
	 * @param a_branchId
	 * @return
	 * @throws Exception
	 */
	public static int createAccountantBox(Connection a_conn, int a_createBoxForUser, int a_branchId)throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		int newcctBoxId = 0;
		try {
			pst = a_conn.prepareStatement("insert into p_accountantbox (acb_usid, acb_userbranchid) values (?,?)",Statement.RETURN_GENERATED_KEYS);
			pst.setInt(1, a_createBoxForUser);
			pst.setInt(2, a_branchId);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			rs.next();
			newcctBoxId = rs.getInt(1);
		}catch(Exception e) {
			
		}finally {
			try {rs.close();}catch (Exception e) {}
			try {pst.close();}catch (Exception e) {}
		}
		return newcctBoxId;
	}
	/**
	 * 
	 * @param a_conn
	 * @param a_safeId
	 * @param a_boxId the id of the account box
	 * @param a_accountBoxTransId transaction Id from p_acctbox_transactions
	 * @param a_branchId
	 * @throws Exception
	 */
	public static void markAccountBoxTransactionMovedToSafe (Connection a_conn,  int a_safeId, int a_boxId, int a_accountBoxTransId, int a_branchId)throws Exception{
		PreparedStatement pst = null;
		try {
			pst = a_conn.prepareStatement("update p_acctbox_transactions "
					+ " set abt_moved_to_safe_withid=?  "
					+ " where abt_acctboxid=?  and abt_id <=? and abt_userbranchid = ? and abt_moved_to_safe_withid = ?");
			pst.setInt(1, a_safeId);
			pst.setInt(2, a_boxId);
			pst.setInt(3, a_accountBoxTransId);
			pst.setInt(4, a_branchId);
			pst.setInt(5, 0);
			pst.executeUpdate();
		}catch(Exception e) {
			
		}finally {
			try {pst.close();}catch (Exception e) {}
		}
	}
	
	/**
	 * 
	 * @param a_conn
	 * @param a_safeId
	 * @param a_boxId
	 * @param a_branchId
	 * @throws Exception
	 */
	public static void unMarkAccountBoxTransactionMovedToSafe (Connection a_conn,  int a_safeId, int a_boxId, int a_branchId)throws Exception{
		PreparedStatement pst = null;
		try {
			pst = a_conn.prepareStatement("update p_acctbox_transactions "
					+ " set abt_moved_to_safe_withid=?  "
					+ " where abt_acctboxid=?  and abt_userbranchid = ? and abt_moved_to_safe_withid = ?");
			pst.setInt(1, 0);
			pst.setInt(2, a_boxId);
			pst.setInt(3, a_branchId);
			pst.setInt(4, a_safeId);
			pst.executeUpdate();
		}catch(Exception e) {
			
		}finally {
			try {pst.close();}catch (Exception e) {}
		}
	}
}
