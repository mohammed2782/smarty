package com.app.incomeoutcome;

import java.sql.PreparedStatement;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.app.financials.FinOperationCode;
import com.app.financials.SafePaymentTypeMetaInfoBean;
import com.app.financials.UtilitiesFinancials;
import com.app.financials.UtilitiesSafeFinancials;
import com.app.financials.UtilitiesStandardFinancials;

import smarty.core.CoreMgr;

public class PaymentsOfPaidCasesInAdvance extends CoreMgr {
	public PaymentsOfPaidCasesInAdvance() {
		MainSql = "select p_fin_transactions.*, '' as amt_iqd, '' as amt_usd, '' fromdate, '' todate "
				+ " from p_fin_transactions "
				+ " where trans_operationentity='CUSTOMER' and  trans_entity_id= 0 "
				+ " and trans_deleted='N' and trans_initiated_in_branch_id={userstorecode}  order by 1 desc";
		canNew  = true;
		canDelete = true;
		mainTable = "p_fin_transactions";
		keyCol = "trans_id";
		userDefinedNewCols.add("trans_entity_id");
		userDefinedNewCols.add("trans_operationcode");
		userDefinedNewCols.add("trans_rmk");
		userDefinedNewCols.add("trans_amount_iqd");
		userDefinedNewCols.add("trans_amount_usd");
		userDefinedLookups.put("trans_operationcode", "select kbcode, kbdesc from "
				+ "kbgeneral where kbcat1='CUSTOMER' and kbcat2='PMTTYPE' order by kbcat_seq ");
		
		userDefinedGridCols.add("trans_id");
		userDefinedGridCols.add("trans_operationcode");
		userDefinedGridCols.add("trans_amount_paid_actually_iqd");
		userDefinedGridCols.add("trans_rmk");
		userDefinedGridCols.add("trans_createdby");
		userDefinedGridCols.add("trans_createddt");
		
		userDefinedColLabel = UtilitiesFinancials.getFinancialTableColumnsName();
		userDefinedColLabel.put("amt_iqd", "المبلغ د.ع");
	
		userDefinedReadOnlyNewCols.add("trans_entity_id");

		userDefinedLookups.put("trans_createdby","select us_id, us_name from kbusers");
	
		
		myhtmlmgr.refreshPageOnDelete = true;
		UserDefinedPageRows = 500;
		userModifyTD.put("trans_id", "printPmtReceipt({trans_id})");
	}
	
	public String printPmtReceipt(HashMap<String, String> hashy) {
		String btn = "<a href=\"../../PaymentReceiptSRVL?trans_id="+hashy.get("trans_id")+"\" "
				+ " class='btn btn-sm btn-cyan' >طباعة أيصال دفع <i class=\"fa fa-print fa-lg\"></i> "+hashy.get("trans_id")+"</a>";
		return "<td>" + btn + "</td>";
	}

	@Override
	public String doDelete(HttpServletRequest rqs) {
		int transactionToBeDeleted = Integer.parseInt(rqs.getParameter(keyCol));
		PreparedStatement pst = null;
		int userId_G = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals));
		int branchId_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		try {
			SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = 
					UtilitiesSafeFinancials.getTransactionSafePaymentTypeMetaInfo(conn, transactionToBeDeleted);
			
			// if the payment is for cases, then back them up and zerofiy the payment
			if (safePaymentTypeMetaInfoBean.getFinOperationCode() == FinOperationCode.CASES ) {
				pst = conn.prepareStatement("insert into  p_deletedpayment_cases (dpc_dpid , dpc_cid, dpc_createdby) "
						+ " select c_pmtid , c_id , ? from p_cases where c_pmtid =?");
				pst.setInt(1, userId_G);
				pst.setInt(2, transactionToBeDeleted);
				pst.executeUpdate();
				try {pst.close();} catch (Exception e) {}
				
				pst = conn.prepareStatement("update p_cases set c_pmtid=0 , c_settled='NO'  where c_pmtid =? and c_paid_delivery_cost_in_advance='Y'");
				pst.setInt(1, transactionToBeDeleted);
				pst.executeUpdate();
				try {pst.close();} catch (Exception e) {}
			}
			//revertTransaction
			UtilitiesStandardFinancials.deleteTransaction(conn, transactionToBeDeleted, userId_G, branchId_G, "مسح عملية مالية - وصولات مدفوعة التوصيل مقدما");
			conn.commit();

		} catch (Exception e) {
			try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
			logErrorMsg = "";
			e.printStackTrace();
		} finally {
			try {pst.close();} catch (Exception e) {}
		}

		return "تم المسح";
	}// end of doDelete*/
}