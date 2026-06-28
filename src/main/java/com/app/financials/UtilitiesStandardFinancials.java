package com.app.financials;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;

import com.app.beans.BranchPaymentBean;

import smarty.core.CoreUtilities;



public class UtilitiesStandardFinancials extends UtilitiesFinancials{
	private static final String CREATE_STANDARD_TRANSACTION_SQL__DROP ="insert into p_fin_transaction"
			+ "(trans_operationentity, trans_operationcat, trans_operationcode, trans_branch_id, trans_which_table , trans_keycol_name, "
			+ " trans_keycol_value, trans_amount_iqd, trans_amount_usd, trans_creaatedby) values("+CoreUtilities.getQuestionMarks(10)+")";
	
	private static final String DELETE_TRANSACTION =
			" update p_fin_transactions set trans_deleted='Y' , trans_deleteddt = now(), trans_deletedby=? where trans_id=? ";
	
	private static final String DELETE_JOURNAL_SQL =
			"update p_fin_double_entry set fde_deleted='Y' where fde_transactionid = ?";
	
	private static final String CREATE_STANDARD_TRANSACTION_SQL =
			"insert into p_fin_transactions"
	+ "(trans_operationentity 		  	  , trans_operationcat	  		  , trans_operationcode, "
	+ " trans_initiated_in_branch_id  	  , trans_which_screen	  		  , trans_amount_iqd   , "
	+ " trans_amount_usd      		  	  , trans_receipts_amt_iqd		  , trans_receipts_amt_usd, "
	+ " trans_amount_paid_actually_iqd	  , trans_amount_paid_actually_usd, trans_credit_iqd,"
	+ " trans_debit_iqd				  	  , trans_credit_usd			  , trans_debit_usd,"
	+ " trans_payer_box				  	  , trans_payer_box_transactionid , trans_amount_received_actually_iqd,"
	+ " trans_amount_received_actually_usd, trans_receiver_box			  , trans_receiver_box_transactionid,"
	+ " trans_receiver_branch_id		  , trans_entity_id				  , trans_rmk,"
	+ "	trans_entity_share_iqd			  , trans_createdby				  ) "
	+ "values("+CoreUtilities.getQuestionMarks(26)+" )";
	
	private static final String CREATE_SINGLE_JOURNAL_SQL ="insert into p_fin_double_entry"
			+ "(fde_transactionid, fde_debitor, fde_creditor, fde_account, fde_account_full_id, fde_currency , "
			+ " fde_branch_id, fde_createdby) values("+CoreUtilities.getQuestionMarks(8)+")";
	
	private static final String GET_JOURNAL_SQL_FOR_FIN_OPERATION =
			"select fojam_fintype, fojam_main_accountnumber from kb_fin_operation_journal_accounts_map "
			+ " where fojam_operation_entity=?, fojam_operation_cat=? and fojam_operation_code=? and"
			+ " (fojam_fintype = ? or fojam_fintype = ? ) order by fojam_seq";
	
	private static final String VERIFY_JOURNAL_ENTRY_IS_CORRECT =
			"select (sum(fde_debitor) - sum(fde_creditor)) as tot_must_be_zero from p_fin_double_entry "
			+ " where fde_transactionid =? ";

	public static final String FULL_STANDARD_TRANSACTION_INFO =  "select "
+ "trans_id					       , trans_operationentity , trans_entity_id 			   , trans_operationcat, trans_operationcode, "
+ "trans_initiated_in_branch_id    , trans_which_screen    , trans_amount_iqd			   , trans_amount_usd  , trans_entity_share_iqd,"
+ "trans_receipts_amt_iqd	       , trans_receipts_amt_usd, trans_amount_paid_actually_iqd, trans_amount_paid_actually_usd,"
+ "trans_credit_iqd			       , trans_debit_iqd	   , trans_credit_usd			   , trans_debit_usd   , trans_payer_box,"
+ "trans_payer_box_transactionid   , trans_amount_received_actually_iqd, trans_amount_received_actually_usd, trans_receiver_box, "
+ "trans_receiver_box_transactionid, trans_did_branch_receive, trans_receiver_branch_id	   , trans_receiver_rmk, trans_received_by, "
+ "trans_receiveddt				   , trans_createdby		 , trans_createddt			   , trans_rmk		   , trans_deleted, "
+ "trans_deletedby				   , trans_deleteddt from p_fin_transactions where trans_id = ?";
	
//	private static final String GET_STANDARD_TRANSACTION_INFO = 
//			 "select trans_amount_iqd, trans_amount_usd, trans_receipts_amt_iqd, trans_receipts_amt_usd,"
//			 + " trans_amount_paid_actually_iqd, trans_amount_paid_actually_usd, trans_entity_share_iqd, trans_operationentity,"
//			 + " trans_entity_id, trans_operationcat, trans_operationcode, trans_rmk, "
//			 + " trans_which_screen , trans_credit_iqd, trans_debit_iqd, trans_credit_usd, trans_debit_usd  "
//			 + " from p_fin_transactions where trans_id = ?";
	
	public static StandardTransactionBean getTransactionInfo(Connection a_conn, int a_transId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		StandardTransactionBean standardTransactionBean = new StandardTransactionBean();
		try {
			pst = a_conn.prepareStatement(FULL_STANDARD_TRANSACTION_INFO);
			pst.setInt(1, a_transId);
			rs = pst.executeQuery();
			if(rs.next()) {
				standardTransactionBean.setId(a_transId);
				standardTransactionBean.setEntity(FinOperationEntity.valueOf(rs.getString("trans_operationentity")));
				standardTransactionBean.setEntityId(rs.getInt("trans_entity_id"));
				standardTransactionBean.setCategory(FinOperationCategory.valueOf(rs.getString("trans_operationcat")));
				standardTransactionBean.setCategoryString(rs.getString("trans_operationcat"));
				standardTransactionBean.setCode(FinOperationCode.valueOf(rs.getString("trans_operationcode")));
				standardTransactionBean.setCodeString(rs.getString("trans_operationcode"));
				standardTransactionBean.setInitiatedInBranchId(rs.getInt("trans_initiated_in_branch_id"));
				standardTransactionBean.setWhichScreen(rs.getString("trans_which_screen"));
				standardTransactionBean.setTransactionAmountIqd(rs.getLong("trans_amount_iqd"));
				standardTransactionBean.setTransactionAmountUsd(rs.getLong("trans_amount_usd"));
				standardTransactionBean.setEntityShareIqd(rs.getLong("trans_entity_share_iqd"));
				standardTransactionBean.setReceiptsAmtIqd(rs.getLong("trans_receipts_amt_iqd"));
				standardTransactionBean.setReceiptsAmtUsd(rs.getLong("trans_receipts_amt_usd"));
				standardTransactionBean.setAmountPaidActuallyIqd(rs.getLong("trans_amount_paid_actually_iqd"));
				standardTransactionBean.setAmountPaidActuallyUsd(rs.getLong("trans_amount_paid_actually_usd"));
				standardTransactionBean.setCreditIqd(rs.getLong("trans_credit_iqd"));
				standardTransactionBean.setDebitIqd(rs.getLong("trans_debit_iqd"));
				standardTransactionBean.setCreditUsd(rs.getLong("trans_credit_usd"));
				standardTransactionBean.setDebitUsd(rs.getLong("trans_debit_usd"));
				standardTransactionBean.setPayerBox(rs.getInt("trans_payer_box"));
				standardTransactionBean.setPayerBoxTransactionId(rs.getInt("trans_payer_box_transactionid"));
				standardTransactionBean.setCreatedBy(rs.getInt("trans_createdby"));
				standardTransactionBean.setCreatedDateTime(rs.getString("trans_createddt"));
				standardTransactionBean.setRemarks(rs.getString("trans_rmk"));
				//receiving information
				standardTransactionBean.setAmountRecievedActuallyIqd(rs.getLong("trans_amount_received_actually_iqd"));
				standardTransactionBean.setAmountRecievedActuallyUsd(rs.getLong("trans_amount_received_actually_usd"));
				standardTransactionBean.setReceiverBox(rs.getInt("trans_receiver_box"));
				standardTransactionBean.setReceiverBoxTransactionId(rs.getInt("trans_receiver_box_transactionid"));
				if (rs.getString("trans_did_branch_receive").equalsIgnoreCase("Y")) {
					standardTransactionBean.setBranchReceivedPayment(true);
				}else {
					standardTransactionBean.setBranchReceivedPayment(false);
				}
				standardTransactionBean.setReceiverBranchId(rs.getInt("trans_receiver_branch_id"));
				standardTransactionBean.setReceiverRemarks(rs.getString("trans_receiver_rmk"));
				standardTransactionBean.setReceivedBy(rs.getInt("trans_received_by"));
				standardTransactionBean.setReceivedDateTime(rs.getString("trans_receiveddt"));
				// deleted information
				if (rs.getString("trans_deleted").equalsIgnoreCase("Y")) {
					standardTransactionBean.setDeleted(true);
					standardTransactionBean.setDeletedBy(rs.getInt("trans_deletedby"));
					standardTransactionBean.setDeletedDateTime(rs.getString("trans_deleteddt"));
				}else {
					standardTransactionBean.setDeleted(false);
				}
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return standardTransactionBean;
	}
	
	/**
	 * Deleted the transaction but not real deleted only changing the flag.
	 * @param a_conn
	 * @param a_transId
	 * @param a_deletedBy
	 * @throws Exception
	 */
	public static void deleteTransaction(Connection a_conn, int a_transId, int a_deletedBy, int a_branchId, String a_deleteFromWhere) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			// get the box transaction Id
			int boxTransactionid = 0;
			pst = a_conn.prepareStatement("select trans_payer_box_transactionid from p_fin_transactions where trans_id=?");
			pst.setInt(1, a_transId);
			rs = pst.executeQuery();
			if(rs.next()) {
				boxTransactionid = rs.getInt("trans_payer_box_transactionid");
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			// delete the transaction
			pst = a_conn.prepareStatement(DELETE_TRANSACTION);
			pst.setInt(1, a_deletedBy);
			pst.setInt(2, a_transId);
			pst.executeUpdate();
			
			// now deleted the double entry
			try {pst.close();}catch(Exception e) {}
			pst = a_conn.prepareStatement(DELETE_JOURNAL_SQL);
			pst.setInt(1, a_transId);
			pst.executeUpdate();
			
			// now revert the safe box transaction
			UtilitiesSafeFinancials.revertAcctBoxTransaction(a_conn, boxTransactionid, a_deletedBy, a_branchId, a_deleteFromWhere);
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	/**
	 * Create the standard fin transaction
	 */
	public static int createStandardFinTransaction(Connection a_conn, 
			StandardTransactionBean a_transactionBean, 
			int a_userId,
			int a_initiatedInBranch
			)throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		int transId =0;
		try {
//			if (a_transactionBean.getTransactionAmountIqd()<0||a_transactionBean.getTransactionAmountUsd()<0) {
//				throw new Exception("لا يسمح بأدخال أي مبلغ بالسالب");
//			}
			pst = a_conn.prepareStatement(CREATE_STANDARD_TRANSACTION_SQL, Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, a_transactionBean.getEntity().name());
			pst.setString(2, a_transactionBean.getCategory().name());
			pst.setString(3, a_transactionBean.getCode().name());
			pst.setInt(4, a_transactionBean.getInitiatedInBranchId());
			pst.setString(5, a_transactionBean.getWhichScreen());
			pst.setLong(6, a_transactionBean.getTransactionAmountIqd());
			pst.setLong(7, a_transactionBean.getTransactionAmountUsd());
			pst.setLong(8, a_transactionBean.getReceiptsAmtIqd());
			pst.setLong(9, a_transactionBean.getReceiptsAmtUsd());
			pst.setLong(10, a_transactionBean.getAmountPaidActuallyIqd());
			pst.setLong(11, a_transactionBean.getAmountPaidActuallyUsd());
			pst.setLong(12, a_transactionBean.getCreditIqd());
			pst.setLong(13, a_transactionBean.getDebitIqd());
			pst.setLong(14, a_transactionBean.getCreditUsd());
			pst.setLong(15, a_transactionBean.getDebitUsd());
			pst.setInt(16, a_transactionBean.getPayerBox());
			pst.setInt(17, a_transactionBean.getPayerBoxTransactionId());
			pst.setLong(18, a_transactionBean.getAmountRecievedActuallyIqd());
			pst.setLong(19, a_transactionBean.getAmountRecievedActuallyUsd());
			pst.setInt(20, a_transactionBean.getReceiverBox());
			pst.setInt(21, a_transactionBean.getReceiverBoxTransactionId());
			pst.setInt(22, a_transactionBean.getReceiverBranchId());
			pst.setInt(23, a_transactionBean.getEntityId());
			pst.setString(24, a_transactionBean.getRemarks());
			pst.setLong(25, a_transactionBean.getEntityShareIqd());
			pst.setInt(26, a_userId);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			rs.next();
			transId = rs.getInt(1);
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return transId;
	}
	
	/**
	 * For every financial operation, there should be two accounts, or more may be, 
	 * this is the map for them
	 */
	public static LinkedList<StandardJournalAccountsMapBean> getStandardFinJournalAccountsMapForOperation
	(Connection a_conn, 
			String a_onEntity, 
			String a_opeationCat, 
			String a_operationCode, 
			StandardFinType a_FinType,
			int a_branchId,
			int a_createdBy) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedList<StandardJournalAccountsMapBean> journalAccountsList = new LinkedList<StandardJournalAccountsMapBean>();
		try {
			pst = a_conn.prepareStatement(GET_JOURNAL_SQL_FOR_FIN_OPERATION);
			pst.setString(1, a_onEntity);
			pst.setString(2, a_opeationCat);
			pst.setString(3, a_operationCode);
			if (a_FinType == StandardFinType.ALL) {
				pst.setString(4, "CREDITOR");
				pst.setString(5, "DEBITOR");
			}else if (a_FinType == StandardFinType.CREDITOR) {
				pst.setString(4, "CREDITOR");
				pst.setString(5, "CREDITOR");
			}else {
				pst.setString(4, "DEBITOR");
				pst.setString(5, "DEBITOR");
			}
			rs = pst.executeQuery();
			while(rs.next()) {
				StandardJournalAccountsMapBean journalAccountBean= new StandardJournalAccountsMapBean();
				if (rs.getString("fojam_fintype").equalsIgnoreCase("CREDITOR")) {
					journalAccountBean.setFinType(StandardFinType.CREDITOR);
				}else {
					journalAccountBean.setFinType(StandardFinType.DEBITOR);
				}
					
				journalAccountBean.setAccountNumber(rs.getString("fojam_main_accountnumber"));
				journalAccountBean.setAmount(0);
				journalAccountBean.setOperationEntity(a_onEntity);
				journalAccountBean.setOperationCat(a_opeationCat);
				journalAccountBean.setOperationCode(a_operationCode);
				journalAccountBean.setBranchId(a_branchId);
				journalAccountBean.setCreatedBy(a_createdBy);
				journalAccountsList.add(journalAccountBean);
			}
		}catch(Exception e) {
			
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return journalAccountsList;
	}
	
	/*
	 * create multi transaction in one place for any financial operation
	 */
	public static void createSingleStandardJournalEntry(
			Connection a_conn,
			int a_transactionId,
			StandardJournalAccountsMapBean a_journalAccountBean,
			int a_userId, 
			int a_branchId)throws Exception{
		PreparedStatement pst = null;
		try {
			pst = a_conn.prepareStatement(CREATE_SINGLE_JOURNAL_SQL);
			pst.setInt(1, a_transactionId);
			if (a_journalAccountBean.getFinType() ==  StandardFinType.CREDITOR) {
				pst.setLong(2, 0);
				pst.setLong(3, a_journalAccountBean.getAmount());
			}else {
				pst.setLong(2, a_journalAccountBean.getAmount());
				pst.setLong(3, 0);
			}
			pst.setString(4, a_journalAccountBean.getAccountNumber());
			pst.setString(5, a_journalAccountBean.getAccountNumber()+"-"+ a_branchId);
			pst.setString(6, a_journalAccountBean.getCurrency().name());
			pst.setInt(7, a_branchId);
			pst.setInt(8, a_userId);
			pst.executeUpdate();
		}catch(Exception e) {
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	public static int buildStandardTransaction(
			Connection a_conn, 
			StandardTransactionBean a_standardTransactionBean,
			int a_initiatedInBranch,
			int a_createdBy)throws Exception{
		int standardTransactionId = 0;
		//first create the transaction
		a_standardTransactionBean.setInitiatedInBranchId(a_initiatedInBranch);
		standardTransactionId = createStandardFinTransaction(a_conn, a_standardTransactionBean, a_createdBy, a_initiatedInBranch );
		
		// then get the multi entry accounts
		LinkedList<StandardJournalAccountsMapBean> standardAccounts = 
				getStandardFinJournalAccountsMapForOperation(a_conn, 
						a_standardTransactionBean.getEntity().name(), 
						a_standardTransactionBean.getCategory().name(), 
						a_standardTransactionBean.getCode().name(), 
						StandardFinType.ALL,
						a_initiatedInBranch,
						a_createdBy);
		//loop through accounts to create the multi entry
		if (a_standardTransactionBean.getTransactionAmountIqd()!=0) {
			for (StandardJournalAccountsMapBean standardJournalAccountMapBean :standardAccounts) {
				standardJournalAccountMapBean.setAmount(a_standardTransactionBean.getTransactionAmountIqd());
				standardJournalAccountMapBean.setCurrency(StandardFinCurrency.IQD);
				createSingleStandardJournalEntry(a_conn, standardTransactionId, standardJournalAccountMapBean, a_initiatedInBranch, a_createdBy);
				
			}
		}
		if (a_standardTransactionBean.getTransactionAmountUsd()!=0) {
			for (StandardJournalAccountsMapBean standardJournalAccountMapBean :standardAccounts) {
				standardJournalAccountMapBean.setAmount(a_standardTransactionBean.getTransactionAmountUsd());
				standardJournalAccountMapBean.setCurrency(StandardFinCurrency.USD);
				createSingleStandardJournalEntry(a_conn, standardTransactionId, standardJournalAccountMapBean, a_initiatedInBranch, a_createdBy);
			}
		}
		if(!verifyTransactionIsCorrect(a_conn, standardTransactionId)) {
			throw new Exception("Double Entry Trsansaction Is not correct");
		}
		// update safe box transaction record
		PreparedStatement pst = null;
		pst = a_conn.prepareStatement("update p_acctbox_transactions set abt_paymentid=?, "
				+ " abt_entity_full_name_trans_id=concat(abt_entity_full_name_trans_id,' - ',?) where abt_id=?");
		pst.setInt(1, standardTransactionId);
		pst.setInt(2, standardTransactionId);
		pst.setInt(3, a_standardTransactionBean.getPayerBoxTransactionId());
		pst.executeUpdate();
		try {pst.close();}catch(Exception e) {}
		return standardTransactionId;
	}
	
	private static boolean verifyTransactionIsCorrect(Connection a_conn, int a_standardTransactionId)throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean isCorrect = false;
		try{
			pst = a_conn.prepareStatement(VERIFY_JOURNAL_ENTRY_IS_CORRECT);
			pst.setInt(1, a_standardTransactionId);
			rs = pst.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1)==0) {
					isCorrect = true;
				}
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return isCorrect;
	}
	
	public static void receivePmtFromBranch(Connection a_conn, int a_currentBranch, BranchPaymentBean a_branchPaymentBean )throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = a_conn.prepareStatement("select trans_receipts_amt_iqd, trans_receipts_amt_usd,"
					+ " trans_initiated_in_branch_id from "
					+ "p_fin_transactions where trans_id=?  and trans_operationentity ='BRANCH' and "
					+ " trans_entity_id = ? ");
			pst.setInt(1,a_branchPaymentBean.getPaymentId());
			pst.setInt(2,a_currentBranch);
			rs = pst.executeQuery();
			if (rs.next()) {
				a_branchPaymentBean.setReceiptsAmountIqd(rs.getLong("trans_receipts_amt_iqd"));
				a_branchPaymentBean.setReceiptsAmountUsd(rs.getLong("trans_receipts_amt_usd"));
				a_branchPaymentBean.setReceiverBranchId(a_currentBranch);
				a_branchPaymentBean.setPayerBranch(rs.getInt("trans_initiated_in_branch_id"));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			long diffIqd = a_branchPaymentBean.getReceivedAmountIqd() - a_branchPaymentBean.getReceiptsAmountIqd();
			long diffUsd = a_branchPaymentBean.getReceivedAmountUsd() - a_branchPaymentBean.getReceiptsAmountUsd();
			long creditIqd = 0, debitIqd = 0, creditUsd = 0, debitUsd = 0;
			
			if (diffIqd < 0)
				debitIqd = -1*diffIqd;
			else if (diffIqd>0)
				creditIqd = diffIqd;
			
			if (diffUsd < 0)
				debitUsd = -1*diffUsd;
			else if (diffUsd>0)
				creditUsd = diffUsd;
			
			AccountantBoxBean receiverAccountantBoxBean= UtilitiesSafeFinancials.GetAccountantBox(a_conn,
					a_branchPaymentBean.getReceivedBy(), a_currentBranch);
			int receiverAccountBoxTransactionId = 0;
			receiverAccountBoxTransactionId = UtilitiesSafeFinancials.createAcctBoxTransactions(
						a_conn, 
						a_branchPaymentBean.getPaymentId(), 
						"p_fin_transactions".toUpperCase(), 
						PaymentImpactOnSafe.ADD_SAFE, 
						a_branchPaymentBean.getReceivedBy(), 
						a_branchPaymentBean.getReceivedAmountIqd(),
						a_branchPaymentBean.getReceivedAmountUsd(),
						a_currentBranch,
						"أستلام مبالغ مالية من الفروع -", 
						"kbbranches",
						"branch_id", 
						a_branchPaymentBean.getPayerBranch(),
						"branch_name",
						getBranchesInfo(a_conn, a_branchPaymentBean.getPayerBranch()+"").get("name"),
						a_branchPaymentBean.getReceivedBy());
			
			
			pst = a_conn.prepareStatement("update p_fin_transactions set "
					+ " trans_receiveddt=now(), trans_did_branch_receive= 'Y' , "
					+ " trans_receiver_branch_id=?, trans_received_by=?, trans_receiver_rmk=? 	  ,  "
					+ " trans_amount_received_actually_iqd=?, trans_amount_received_actually_usd=? , "
					+ " trans_credit_iqd=?    , trans_debit_iqd=? , "
					+ " trans_credit_usd=?    , trans_debit_usd=? , "
					+ " trans_receiver_box =? , trans_receiver_box_transactionid=?"
					+ " where trans_id = ? and trans_did_branch_receive='N'");
			pst.setInt(1, a_currentBranch);
			pst.setInt(2, a_branchPaymentBean.getReceivedBy());
			pst.setString(3,a_branchPaymentBean.getReceiverBranchRmk() );
			pst.setLong(4,a_branchPaymentBean.getReceivedAmountIqd());
			pst.setLong(5,a_branchPaymentBean.getReceivedAmountUsd());
			pst.setLong(6,creditIqd );
			pst.setLong(7,debitIqd );
			pst.setLong(8,creditUsd );
			pst.setLong(9,debitUsd );
			pst.setInt(10, receiverAccountantBoxBean.getBoxId());
			pst.setInt(11, receiverAccountBoxTransactionId);
			pst.setInt(12,a_branchPaymentBean.getPaymentId() );
			pst.executeUpdate();
			
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}
}
