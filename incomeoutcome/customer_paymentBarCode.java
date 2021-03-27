package com.app.incomeoutcome;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;

public class customer_paymentBarCode extends CoreMgr{
	public customer_paymentBarCode() {

		/*
		 * to define main sql statement, key column and main table that do this
		 * operations
		 */
		MainSql = " select cp_id ,cp_custid,cp_amount_paid ,cp_paymentdt , cp_createdby ,cp_rmk, '' as fake "
				+ " from  p_customer_payments where cp_custid = '{customerAcctBarCode}' and cp_barcode='Y' order by cp_id desc ";

		keyCol = "cp_id";
		mainTable = "p_customer_payments";

		// ///////////////
		userDefinedGridCols.add("cp_id");
		userDefinedGridCols.add("cp_custid");
		userDefinedGridCols.add("cp_amount_paid");
		userDefinedGridCols.add("cp_paymentdt");
		userDefinedGridCols.add("cp_createdby");
		userDefinedGridCols.add("cp_rmk");
		userDefinedGridCols.add("fake");

		// //////////////
		userDefinedCaption = "تسديدات الزبائن";
		userDefinedColLabel.put("cp_id", "رقم الأيصال");
		userDefinedColLabel.put("cp_amount_paid", "المبلغ المدفوع");
		userDefinedColLabel.put("cp_custid", "الزبون");
		userDefinedColLabel.put("cp_paymentdt", "تاريخ الدفع");
		userDefinedColLabel.put("cp_rmk", " ملاحظات");
		userDefinedColLabel.put("cp_createdby", "أنشئ بواسطة ");
		userDefinedColLabel.put("fake", "طباعة ايصال الدفع ");
		userDefinedColLabel.put("price", "المبلغ المطلوب دفعه");

		canDelete = false;
		userModifyTD.put("fake", "printPmtReceipt({cp_id})");
	
		userDefinedLookups.put("cp_custid","select c_id ,c_name  From kbcustomers");
		myhtmlmgr.refreshPageOnDelete = true;
		
		UserDefinedPageRows = 10;
	}// end of constructor customer_payment

	
	public String printPmtReceipt(HashMap<String, String> hashy) {
		String btn = "<a href=\"../TLKPaymentReceiptSRVL?cp_id="+hashy.get("cp_id")+"\" "
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
			// first backup the cases that hadd payment and the payment also
	
			pst = conn.prepareStatement("insert into  p_deletedpayment_cases (dpc_dpid , dpc_cid, dpc_createdby) "
					+ " select c_pmtid , c_id , ? from p_cases where c_pmtid =?");
			pst.setString(1, userId);
			pst.setString(2, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			pst = conn.prepareStatement("insert into p_deletedpaymets "
					+ " (dp_pmtid, dp_custid, dp_pmtamt, dp_pmtdate, dp_pmtrmk, dp_pmtcreateddt, dp_barcode, dp_pmtcreatedby, dp_deletedby)" + 
					" SELECT cp_id, cp_custid,cp_amount_paid , cp_paymentdt, cp_rmk, cp_createddt, cp_barcode, cp_createdby, ?  from p_customer_payments " + 
					" where cp_id = ?");
			pst.setString(1, userId);
			pst.setString(2, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			// second update the cases back to NO, and c_pmtid = 0
			pst = conn.prepareStatement("update p_cases set c_pmtid=0 , "
					+ "  c_settled=(case when c_paidinadvance ='REFUNDED' then 'FULL' else 'NO' end),"
					+ "  c_paidinadvance=(case when c_paidinadvance ='REFUNDED' then 'YES' else c_paidinadvance end)"
					+ "  where c_pmtid =?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			// last thing , delete
			pst = conn.prepareStatement("delete from p_customer_payments  where cp_id = ?");
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

	

}// end of class customer_payment
