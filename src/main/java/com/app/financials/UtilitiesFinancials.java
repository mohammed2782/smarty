package com.app.financials;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import com.app.util.Utilities;

public class UtilitiesFinancials extends Utilities{
	public  static HashMap<StandardFinCurrency, Long> getDlvAgentsDebts (Connection conn, int currentBranch) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<StandardFinCurrency, Long> moneyToBranches = new HashMap<StandardFinCurrency, Long>();
		try{
			pst = conn.prepareStatement("select sum(trans_debit_iqd - trans_credit_iqd ) as debt_iqd,"
					+ "  sum(trans_debit_usd - trans_credit_usd ) as debt_usd "
					+ " from p_fin_transactions where trans_operationentity ='AGENT'  "
					+ " and trans_deleted = 'N' and trans_initiated_in_branch_id=? and trans_deleted = 'N' ");
			pst.setInt(1, currentBranch);
			rs = pst.executeQuery();
			if (rs.next()) {
				moneyToBranches.put(StandardFinCurrency.IQD, rs.getLong("debt_iqd"));
				moneyToBranches.put(StandardFinCurrency.USD, rs.getLong("debt_usd"));
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return moneyToBranches;
	}
	
	public  static HashMap<StandardFinCurrency, Long> getCustomerDebts (Connection conn, int currentBranch) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<StandardFinCurrency, Long> moneyToBranches = new HashMap<StandardFinCurrency, Long>();
		try{
			pst = conn.prepareStatement("select sum(trans_debit_iqd - trans_credit_iqd ) as debt_iqd,"
					+ "  sum(trans_debit_usd - trans_credit_usd ) as debt_usd "
					+ " from p_fin_transactions where trans_operationentity ='CUSTOMER'  "
					+ " and trans_deleted = 'N' and trans_initiated_in_branch_id=? and trans_deleted = 'N' ");
			pst.setInt(1, currentBranch);
			rs = pst.executeQuery();
			if (rs.next()) {
				moneyToBranches.put(StandardFinCurrency.IQD, rs.getLong("debt_iqd"));
				moneyToBranches.put(StandardFinCurrency.USD, rs.getLong("debt_usd"));
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return moneyToBranches;
	}
	/*
	 * Money with DLV agents
	 */
	public static HashMap<StandardFinCurrency, Long> getMoneyWithDlvAgents (Connection conn, int currentBranch) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<StandardFinCurrency, Long> agentsBalance = new HashMap<StandardFinCurrency, Long>();
		try{
			pst = conn.prepareStatement("select  "
					+ " sum(c_receiptamt) - sum(c_agentshare) as totreceiptamt_iqd,"
					+ " sum(c_receiptamt_usd)  totreceiptamt_usd "
					+ " from p_cases "
					+ " join kbusers on us_id = c_assignedagent  and us_rank = 'DLVAGENT'  "
					+ "  where  us_branchcode= ? and c_alllowagentpay = 'Y' "
					+ " and c_agentsharesettled !='FULL'  and c_agentpmtid = 0");
			pst.setInt(1, currentBranch);
			rs = pst.executeQuery();
			if (rs.next()) {
				agentsBalance.put(StandardFinCurrency.IQD, rs.getLong("totreceiptamt_iqd"));
				agentsBalance.put(StandardFinCurrency.USD, rs.getLong("totreceiptamt_usd"));
			}

		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return agentsBalance;
	}
	
	public static HashMap<StandardFinCurrency, Long> getMoneyWithBranches(Connection conn, int currentBranch) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<StandardFinCurrency, Long> withBranchesBalance = new HashMap<StandardFinCurrency, Long>();
		try{
			pst = conn.prepareStatement("select  "
					+ "sum(c_receiptamt) - sum(cc_pathcost) as totreceiptamt_iqd,"
					+ "sum(c_receiptamt_usd) as totreceiptamt_usd"
					+ " from p_cases "
					+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch = ?"
					+ " and cc_branchpmtid=0 and cc_branchrecievedpmt='N')  "
					+ "     where q_stage='DLV' ");
			pst.setInt(1, currentBranch);
			rs = pst.executeQuery();
			if (rs.next()) {
				withBranchesBalance.put(StandardFinCurrency.IQD, rs.getLong("totreceiptamt_iqd"));
				withBranchesBalance.put(StandardFinCurrency.USD, rs.getLong("totreceiptamt_usd"));
			}

		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return withBranchesBalance;
	}
	
	public static HashMap<StandardFinCurrency, Long> getMoneytoBePaidToCustomers (Connection conn, int currentBranch) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<StandardFinCurrency, Long> customersBalance = new HashMap<StandardFinCurrency, Long>();
		try{
			pst = conn.prepareStatement(" select "
					+ "	SUM(c_receiptamt - c_shipment_cost) as netamt_iqd,  "
					+ "	SUM(c_receiptamt_usd) as netamt_usd "
					+ "	from p_cases "
					+ " where c_settled !='FULL'  and c_allowcustpay = 'Y' and c_branchcode = ? and c_pmtid=0");
			pst.setInt(1, currentBranch);
			rs = pst.executeQuery();
			if (rs.next()) {
				customersBalance.put(StandardFinCurrency.IQD, rs.getLong("netamt_iqd"));
				customersBalance.put(StandardFinCurrency.USD, rs.getLong("netamt_usd"));
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return customersBalance;
	}
	
	public  static HashMap<StandardFinCurrency, Long> getMonyToBranches (Connection conn, int currentBranch) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<StandardFinCurrency, Long> moneyToBranches = new HashMap<StandardFinCurrency, Long>();
		try{
			pst = conn.prepareStatement("select  "
					+ "sum(c_receiptamt) - sum(cc_pathcost) as totreceiptamt_iqd, "
					+ "sum(c_receiptamt_usd) as totreceiptamt_usd "
					+ " from p_cases "
					+ " join p_caseschain "
					+ "on (c_id = cc_caseid and cc_tobranch = ? and cc_branchpmtid=0 "
					+ " and cc_branchrecievedpmt='N')  "
					+ "     where q_stage='DLV' ");
			pst.setInt(1, currentBranch);
			rs = pst.executeQuery();
			if (rs.next()) {
				moneyToBranches.put(StandardFinCurrency.IQD, rs.getLong("totreceiptamt_iqd"));
				moneyToBranches.put(StandardFinCurrency.USD, rs.getLong("totreceiptamt_usd"));
			}

		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return moneyToBranches;
	}
	
	public long getDeptWithCompanies (Connection conn, int currentBranch) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		long balance = 0;
		try {
			pst = conn.prepareStatement("select sum(bp_debt) - sum(bp_credit) "
					+ " from p_branch_payments where"
					+ " bp_received_branchid=? ");
			pst.setInt(1, currentBranch);
			rs = pst.executeQuery();
			if (rs.next())
				balance = rs.getLong(1);
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return balance;
	}
	
	public static HashMap<String,String> getFinancialTableColumnsName(){
		HashMap<String,String> userDefinedColLabel = new HashMap<String,String>();
		userDefinedColLabel.put("trans_amount_iqd", "مبلغ العملية د.ع");
		userDefinedColLabel.put("trans_amount_usd", ",مبلغ العملية $");
		userDefinedColLabel.put("trans_amount_paid_actually_iqd", "مستلم/مدفوع د.ع");
		userDefinedColLabel.put("trans_amount_paid_actually_usd","مستلم/مدفوع $");
		userDefinedColLabel.put("trans_operationcode", "نوع العملية");
		userDefinedColLabel.put("trans_debit_iqd", "مدين د.ع");
		userDefinedColLabel.put("trans_credit_iqd", "دائن د.ع");
		userDefinedColLabel.put("trans_debit_usd", "مدين $");
		userDefinedColLabel.put("trans_credit_usd", "دائن $");
		userDefinedColLabel.put("trans_id", "رقم العملية");
		userDefinedColLabel.put("trans_rmk", "ملاحظات");
		userDefinedColLabel.put("trans_createddt", "بتاريخ");
		userDefinedColLabel.put("trans_createdby", "تمت من خلال");
		userDefinedColLabel.put("trans_entity_id", "طرف العملية");
		userDefinedColLabel.put("trans_receipts_amt_usd", "مبلغ الوصولات $");
		userDefinedColLabel.put("trans_receipts_amt_iqd", "مبلغ الوصولات د.ع");
		

		userDefinedColLabel.put("trans_received_by", "تم الإستلام؟");
		userDefinedColLabel.put("trans_receiver_rmk", "ملاحظات المستلم");
		userDefinedColLabel.put("trans_receiveddt", "تاريخ اللإستلام");
		userDefinedColLabel.put("trans_createddt", "تاريخ التسديد");
		userDefinedColLabel.put("trans_received_by", "أستلمت بواسطة");
		
		userDefinedColLabel.put("trans_amount_received_actually_iqd", "المستلم فعليا د.ع");
		userDefinedColLabel.put("trans_amount_received_actually_usd", "المستلم فعليا $");
		
		
		
		return userDefinedColLabel;
	}
	
	public static HashMap<String,String> getSafeTableColumnsName(){
		HashMap<String,String> userDefinedColLabel = new HashMap<String,String>();
		userDefinedColLabel.put("saf_id","رقم الدفعة" );
		userDefinedColLabel.put("saf_amount_iqd","مبلغ المعاملة د.ع" );
		userDefinedColLabel.put("saf_amount_usd","مبلغ المعاملة $" );
		userDefinedColLabel.put("saf_trantype","نوع المعاملة" );
		userDefinedColLabel.put("saf_tranname","أسم المعاملة" );
		userDefinedColLabel.put("saf_tranentity","طرف المعاملة" );
		userDefinedColLabel.put("saf_trandate","تاريخ المعاملة" );
		userDefinedColLabel.put("saf_createdby","تمت من خلال" );
		userDefinedColLabel.put("saf_iqd_before_transaction","الرصيد قبل د.ع" );
		userDefinedColLabel.put("saf_usd_before_transaction","الرصيد قبل $" );
		userDefinedColLabel.put("saf_iqd_after_transaction","الرصيد بعد د.ع" );
		userDefinedColLabel.put("saf_usd_after_transaction","الرصيد بعد $" );
		userDefinedColLabel.put("saf_rmk","ملاحظات" );
		userDefinedColLabel.put("saf_branchid","فرع" );
		userDefinedColLabel.put("details"," " );
		userDefinedColLabel.put("del"," " );
		return userDefinedColLabel;
	}
	
	public static HashMap<StandardFinCurrency, Long> getAccountantBoxBalance(Connection a_conn, int a_userId)throws Exception{
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<StandardFinCurrency, Long> balance = new HashMap<StandardFinCurrency, Long>();
		/*
		 * balance.put(StandardFinCurrency.IQD, 0L);
		 * balance.put(StandardFinCurrency.USD, 0L);
		 */
		try{
			String sql = "select acb_balance_iqd, acb_balance_usd from p_accountantbox where acb_usid=?";
			pst = a_conn.prepareStatement(sql);
			pst.setInt(1, a_userId);
			rs = pst.executeQuery();
			if(rs.next()){
				balance.put(StandardFinCurrency.IQD, rs.getLong("acb_balance_iqd"));
				balance.put(StandardFinCurrency.USD, rs.getLong("acb_balance_usd"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{rs.close();}catch(Exception e) {}
			try{pst.close();}catch(Exception e) {}
		}
		
		return balance;
	}//end of method getAcctAmtBox
}
