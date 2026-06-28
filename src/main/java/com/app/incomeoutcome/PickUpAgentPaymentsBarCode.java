package com.app.incomeoutcome;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import com.app.util.Utilities;

public class PickUpAgentPaymentsBarCode extends CoreMgr {
	public PickUpAgentPaymentsBarCode() {

		/*
		 * to define main sql statement, key column and main table that do this
		 * operations
		 */
		MainSql = " select cppa_id ,cppa_pickupagentid,cppa_amount_paid ,cppa_paymentdt , cppa_createdby ,cppa_rmk, '' as fake "
				+ " from  p_customer_payments_pickupagents "
				+ " where cppa_pickupagentid = {pickupAgentAcctBarCode}  order by  cppa_id desc ";

		keyCol = "cppa_id";
		mainTable = "p_customer_payments_pickupagents";

		// ///////////////
		userDefinedGridCols.add("cppa_id");
		//userDefinedGridCols.add("cppa_pickupagentid");
		userDefinedGridCols.add("cppa_amount_paid");
		userDefinedGridCols.add("cppa_paymentdt");
		userDefinedGridCols.add("cppa_createdby");
		userDefinedGridCols.add("cppa_rmk");
		userDefinedGridCols.add("fake");

		// //////////////
		userDefinedCaption = "تسديدات الزبائن";
		userDefinedColLabel.put("cppa_id", "رقم الأيصال");
		userDefinedColLabel.put("cppa_amount_paid", "المبلغ المدفوع");
		userDefinedColLabel.put("cppa_pickupagentid", "مندوب الإستلام");
		userDefinedColLabel.put("cppa_paymentdt", "تاريخ الدفع");
		userDefinedColLabel.put("cppa_rmk", " ملاحظات");
		userDefinedColLabel.put("cppa_createdby", "أنشئ بواسطة ");
		userDefinedColLabel.put("fake", "طباعة ايصال الدفع ");
		

		canDelete = true;
		userModifyTD.put("fake", "printPmtReceipt({cppa_id})");
	
		//userDefinedLookups.put("cp_custid","select c_id ,c_name  From kbcustomers");
		myhtmlmgr.refreshPageOnDelete = true;
		
	}// end of constructor customer_payment

	
	public String printPmtReceipt(HashMap<String, String> hashy) {
		String btn = "<a href=\"../TLRPickUpAgentPaymentReceiptSRVL?cppa_id="+hashy.get("cppa_id")+"\" "
				+ " class='btn btn-xs btn-warning' >طباعة أيصال دفع <i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
	}
	
	@Override
	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		
		String rank = (String) arrayGlobals.get("userRank");
		String superItRank = (String) arrayGlobals.get("superItRank");
 		if (rank.equalsIgnoreCase("FIN_OP_MGR")  || superItRank.equalsIgnoreCase("Y")) {
			canDelete = true;
		}
			
	}
	
	@Override
	public String doDelete(HttpServletRequest rqs) {
		String keyVal = rqs.getParameter(keyCol);
		PreparedStatement pst = null;
		ResultSet rs = null;
		String userId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		try {
			// first backup the cases that had payment and the payment also
	
			pst = conn.prepareStatement("insert into  p_deletedpayment_cases (dpc_dpid , dpc_cid, dpc_createdby , dpc_from_pickupagent_acct) "
					+ " select c_pickupagentpmtid , c_id , ? , ? from p_cases where c_pickupagentpmtid =?");
			pst.setString(1, userId);
			pst.setString(2, "Y");
			pst.setString(3, keyVal);
			
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			pst = conn.prepareStatement("insert into p_customer_payments_pickupagents_dlt "
					+ " (cppa_id, cppa_pickupagentid, cppa_amount_paid, cppa_paymentdt, cppa_createdby, cppa_createddt, cppa_rmk, dpp_deletedby)" + 
					" SELECT cppa_id, cppa_pickupagentid, cppa_amount_paid, cppa_paymentdt, cppa_createdby, cppa_createddt, cppa_rmk, ? "
					+ "  from p_customer_payments_pickupagents  where cppa_id = ?");
			pst.setString(1, userId);
			pst.setString(2, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			// second update the cases back to NO, and c_pickupagentpmtid = 0
			pst = conn.prepareStatement("update p_cases set c_pickupagentpmtid=0 , c_settled='NO' where c_pickupagentpmtid =?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			
			//catch deleted Payment
			Double deletedPayment = 0.0;
			pst = conn.prepareStatement("select cppa_amount_paid from p_customer_payments_pickupagents  where cppa_id=?");
			pst.setString(1, keyVal);
			rs = pst.executeQuery();
			if(rs.next())
				deletedPayment = rs.getDouble("cppa_amount_paid");
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
			
			//check accountant box found
			Double acctCurrentBalunce = 0.0;
			int countOfBox = 0;
			int acctBoxId = 0;
			pst = conn.prepareStatement("select acb_currentbalunce, acb_id from p_accountantbox where acb_usid = ?");
			pst.setString(1, userId);
			rs = pst.executeQuery();
			while(rs.next()) {
				acctCurrentBalunce = rs.getDouble("acb_currentbalunce");
				acctBoxId = rs.getInt("acb_id");
				countOfBox +=1;
				
			}
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
			if(countOfBox>1) 
				return "حصل خطأ : هناك اكثر من صندوق لنفس المحاسب الرجاء الاتصال بسوفتيكا";
			if(countOfBox==0)
				return "لايوجد صندوق مالي للمستخدم";
			
			if(acctCurrentBalunce<=deletedPayment)
				return "لايوجد مبلغ مالي في الصندوق كافي لاسترداد المبلغ المحذوف";
			
			if(deletedPayment<0)
				if(acctCurrentBalunce<=-deletedPayment)
					return "لايوجد مبلغ مالي في الصندوق كافي لاسترداد المبلغ المحذوف";
			
			
			//back payment to the accountant box
			pst  = conn.prepareStatement("update p_accountantbox set acb_currentbalunce = acb_currentbalunce+? where acb_id=?");
			pst.setDouble(1, deletedPayment);
			pst.setInt(2, acctBoxId);
			pst.executeUpdate();	
			try {pst.close();}catch(Exception e) {}	
			
			
			// last thing , delete
			pst = conn.prepareStatement("delete from p_customer_payments_pickupagents  where cppa_id = ?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			Utilities ut = new Utilities();
			ut.acctBoxTransactions(conn, Integer.parseInt(keyVal), "p_customer_payments_pickupagents".toUpperCase(), "CR", userId, acctBoxId, acctCurrentBalunce, deletedPayment);
			conn.commit();

		} catch (Exception e) {
			try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
			logErrorMsg = "";
			e.printStackTrace();
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}

		return "";
	}// end of doDelete*/

	

}// end of class customer_payment
