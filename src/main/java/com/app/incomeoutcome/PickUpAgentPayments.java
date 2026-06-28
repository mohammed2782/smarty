package com.app.incomeoutcome;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;

import com.app.financials.FinOperationCode;
import com.app.financials.SafePaymentTypeMetaInfoBean;
import com.app.financials.UtilitiesFinancials;
import com.app.financials.UtilitiesSafeFinancials;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.util.UtilitiesFeqar;

public class PickUpAgentPayments extends CoreMgr{
	public PickUpAgentPayments() {

		/*
		 * to define main sql statement, key column and main table that do this
		 * operations
		 */
		MainSql = " select p_fin_transactions.*, '' as fake "
				+ " from  p_fin_transactions "
				+ " where trans_operationentity='PICKUP_AGENT' and  trans_entity_id= {pickupAgentAcct} "
				+ " and trans_deleted='N' order by 1 desc";
		
		canDelete = true;
		mainTable = "p_fin_transactions";
		keyCol = "trans_id";
		// ///////////////
		userDefinedLookups.put("trans_operationcode", "select kbcode, kbdesc from kbgeneral where kbcat1='PICKUP_AGENT' and kbcat2='PMTTYPE' order by kbcat_seq ");
	
		userDefinedGridCols.add("trans_id");
		userDefinedGridCols.add("trans_operationcode");
		userDefinedGridCols.add("trans_amount_iqd");
		userDefinedGridCols.add("trans_amount_usd");
		userDefinedGridCols.add("trans_amount_paid_actually_iqd");
		userDefinedGridCols.add("trans_amount_paid_actually_usd");
		userDefinedGridCols.add("trans_rmk");
		userDefinedGridCols.add("trans_createdby");
		userDefinedGridCols.add("trans_createddt");
		userModifyTD.put("trans_id", "printPmtReceipt({trans_id})");
		
		
		userDefinedLookups.put("trans_entity_id", "select cust_id, cust_name from kbcustomers where cust_id={pickupAgentAcct}");
		userDefinedColLabel = UtilitiesFinancials.getFinancialTableColumnsName();
		userDefinedColLabel.put("amt_iqd", "المبلغ د.ع");
		userDefinedColLabel.put("amt_usd", "المبلغ $");
		userDefinedColLabel.put("fromdate", "من تاريخ");
		userDefinedColLabel.put("todate", "الى تاريخ");
		// //////////////
		userDefinedCaption = "تسديدات مندوب الأستلام";
		userDefinedColLabel.put("cppa_id", "رقم الأيصال");
		userDefinedColLabel.put("cppa_amount_paid", "المبلغ المدفوع");
		userDefinedColLabel.put("cppa_pickupagentid", "مندوب الإستلام");
		userDefinedColLabel.put("cppa_createddt", "تاريخ الدفع");
		userDefinedColLabel.put("cppa_rmk", " ملاحظات");
		userDefinedColLabel.put("cppa_createdby", "أنشئ بواسطة ");
		userDefinedColLabel.put("fake", "طباعة ايصال الدفع ");
		
		
		myhtmlmgr.refreshPageOnDelete = true;
	
	}// end of constructor customer_payment

	public String printPmtReceipt(HashMap<String, String> hashy) {
		String btn = "<a href=\"../../PickUpAgentPaymentReceiptSRVL?trans_id="+hashy.get("trans_id")+"\" "
				+ " class='btn btn-sm btn-info' >طباعة أيصال دفع <i class=\"fa fa-print fa-lg\"></i> "+hashy.get("trans_id")+"</a>";
		return "<td>" + btn + "</td>";
	}
	
	@Override
	public String doDelete(HttpServletRequest rqs) {
		int transactionToBeDeleted = Integer.parseInt(rqs.getParameter(keyCol));
		PreparedStatement pst = null;
		ResultSet rs = null;
		int userId_G = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals));
		int branchId_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		try {
			SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = 
					UtilitiesSafeFinancials.getTransactionSafePaymentTypeMetaInfo(conn, transactionToBeDeleted);
			
			// if the payment is for cases, then back them up and zerofiy the payment
			if (safePaymentTypeMetaInfoBean.getFinOperationCode() == FinOperationCode.CASES ) {
				pst = conn.prepareStatement("insert into  p_deletedpayment_cases (dpc_dpid , dpc_cid, dpc_createdby) "
						+ " select c_pmtid , c_id , ? from p_cases where c_pickupagentpmtid =?");
				pst.setInt(1, userId_G);
				pst.setInt(2, transactionToBeDeleted);
				pst.executeUpdate();
				try {pst.close();} catch (Exception e) {}
				
				pst = conn.prepareStatement("update p_cases set c_pickupagentpmtid=0 , c_settled='NO' where c_pickupagentpmtid =?");
				pst.setInt(1, transactionToBeDeleted);
				pst.executeUpdate();
				try {pst.close();} catch (Exception e) {}
			}
			//revertTransaction
			UtilitiesStandardFinancials.deleteTransaction(conn, transactionToBeDeleted, userId_G, branchId_G, "مسح عملية مالية - حسابات مندوب الأستلام");
			conn.commit();

		} catch (Exception e) {
			try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
			logErrorMsg = "";
			e.printStackTrace();
		} finally {
			try {rs.close();}catch(Exception e) {/**/}
			try {pst.close();} catch (Exception e) {}
		}

		return "";
	}// end of doDelete*/

	

}// end of class customer_payment
