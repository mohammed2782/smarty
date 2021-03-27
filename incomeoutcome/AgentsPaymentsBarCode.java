package com.app.incomeoutcome;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;

public class AgentsPaymentsBarCode extends CoreMgr{
	public AgentsPaymentsBarCode() {
		MainSql = "select p_agent_payments.* , '' as fake from p_agent_payments where ap_agentid={agentAcctBarCode} and ap_barcode='Y' order by ap_id desc ";
		
		keyCol = "ap_id";
		mainTable = "p_agent_payments";
		
		myhtmlmgr.tableClass = "table table-striped  table-bordered orange_table";

		// ///////////////
		userDefinedGridCols.add("ap_id");
		
		userDefinedGridCols.add("ap_amount_paid");
		userDefinedGridCols.add("ap_paymentdt");
		userDefinedGridCols.add("ap_createdby");
		userDefinedGridCols.add("ap_rmk");
		userDefinedGridCols.add("fake");

		// //////////////
		userDefinedCaption = "تسديدات الزبائن";
		userDefinedColLabel.put("ap_id", "رقم الأيصال");
		userDefinedColLabel.put("ap_amount_paid", "المبلغ المدفوع");
		userDefinedColLabel.put("ap_paymentdt", "تاريخ الدفع");
		userDefinedColLabel.put("ap_rmk", " ملاحظات");
		userDefinedColLabel.put("ap_createdby", "أنشئ بواسطة ");
		userDefinedColLabel.put("fake", "طباعة ايصال الدفع ");
		canDelete = true;
		userModifyTD.put("fake", "printPmtReceipt({ap_id})");
		
		myhtmlmgr.refreshPageOnDelete = true;

	}// end of constructor customer_payment

	
	public String printPmtReceipt(HashMap<String, String> hashy) {
		String btn = "<a href=\"../TLKAgentPaymentReceiptSRVL?ap_id="+hashy.get("ap_id")+"\" "
				+ " class='btn btn-xs btn-warning' >طباعة أيصال دفع <i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
	}
	
	@Override
	public String doDelete(HttpServletRequest rqs) {
		String keyVal = rqs.getParameter(keyCol);
		PreparedStatement pst = null;
		ResultSet rs = null;
		String userId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		try {
			// first backup the cases that hadd payment and the payment also
	
			pst = conn.prepareStatement("insert into  p_deletedpayment_agents (dpa_dpid , dpa_cid, dpa_createdby) "
					+ " select c_agentpmtid , c_id , ? from p_cases where c_pmtid =?");
			pst.setString(1, userId);
			pst.setString(2, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			pst = conn.prepareStatement("insert into p_agentsdeletedpaymets "
					+ " (adp_pmtid, adp_custid, adp_pmtamt	  , adp_pmtdate , adp_pmtrmk, adp_barcode , adp_pmtcreateddt, adp_pmtcreatedby, adp_deletedby)" + 
				" SELECT ap_id	  , ap_agentid,ap_amount_paid , ap_paymentdt, ap_rmk	, ap_barcode  , ap_createddt	, ap_createdby	  , ?  from p_agent_payments " + 
					" where ap_id = ?");
			pst.setString(1, userId);
			pst.setString(2, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			// second update the cases back to NO, and c_pmtid = 0
			pst = conn.prepareStatement("update p_cases set c_agentpmtid=0 , c_agentsharesettled='NO' where c_agentpmtid =?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			// last thing , delete
			pst = conn.prepareStatement("delete from p_agent_payments  where ap_id = ?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			conn.commit();

		} catch (Exception e) {
			try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
			logErrorMsg = "";
			e.printStackTrace();
		} finally {
			try {pst.close();} catch (Exception e) {}
		}

		return "";
	}// end of doDelete*/

}