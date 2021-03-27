package com.app.incomeoutcome;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;

public class CustomerPaymentsInAdvanceBarCode  extends CoreMgr{
	public CustomerPaymentsInAdvanceBarCode() {

		/*
		 * to define main sql statement, key column and main table that do this
		 * operations
		 */
		MainSql = " select advpmt_id ,advpmt_custid,advpmt_amt ,advpmt_date , advpmt_createdby ,advpmt_rmk, '' as fake "
				+ " from  p_inadvance_cust_pmt  where advpmt_custid = '{customerAcctBarCode}' and advpmt_barcode='Y' order by advpmt_id desc ";

		keyCol = "advpmt_id";
		mainTable = "p_inadvance_cust_pmt";

		// ///////////////
		userDefinedGridCols.add("advpmt_id");
		userDefinedGridCols.add("advpmt_custid");
		userDefinedGridCols.add("advpmt_amt");
		userDefinedGridCols.add("advpmt_date");
		userDefinedGridCols.add("advpmt_createdby");
		userDefinedGridCols.add("advpmt_rmk");
		userDefinedGridCols.add("fake");

		// //////////////
		userDefinedCaption = "تسديدات  مقدما للزبائن";
		userDefinedColLabel.put("advpmt_id", "رقم الأيصال");
		userDefinedColLabel.put("advpmt_amt", "المبلغ المدفوع");
		userDefinedColLabel.put("advpmt_custid", "الزبون");
		userDefinedColLabel.put("advpmt_date", "تاريخ الدفع");
		userDefinedColLabel.put("advpmt_rmk", " ملاحظات");
		userDefinedColLabel.put("advpmt_createdby", "أنشئ بواسطة ");
		userDefinedColLabel.put("fake", "طباعة ايصال الدفع ");
		userDefinedColLabel.put("price", "المبلغ المطلوب دفعه");

		canDelete = false;
		userModifyTD.put("fake", "printPmtReceipt({advpmt_id})");
	
		userDefinedLookups.put("advpmt_custid","select c_id ,c_name  From kbcustomers");
		myhtmlmgr.refreshPageOnDelete = true;
		
		UserDefinedPageRows = 10;
		userDefined_x_panelclass = " advancepmttable";
	}// end of constructor customer_payment

	
	public String printPmtReceipt(HashMap<String, String> hashy) {
		String btn = "<a href=\"../TLKAdvancedPaymentReceiptSRVL?advpmt_id="+hashy.get("advpmt_id")+"\" "
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
	
			pst = conn.prepareStatement("insert into  p_deletedinadvance_payment_cases (dpc_dpid , dpc_cid, dpc_createdby) "
					+ " select c_pmtid , c_id , ? from p_cases where c_pmtid =?");
			pst.setString(1, userId);
			pst.setString(2, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			pst = conn.prepareStatement("insert into p_inadvance_cust_pmt_deleted "
					+ " 	(advpmt_id, advpmt_custid,advpmt_amt , advpmt_date, advpmt_rmk, advpmt_barcode, advpmt_createddt, advpmt_createdby, advpmt_deletedby)" + 
					" SELECT advpmt_id, advpmt_custid,advpmt_amt , advpmt_date, advpmt_rmk, advpmt_barcode, advpmt_createddt, advpmt_createdby, ?  "
					+ "from p_inadvance_cust_pmt " + 
					" where advpmt_id = ?");
			pst.setString(1, userId);
			pst.setString(2, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			// second update the cases back to NO, and c_pmtid = 0
			pst = conn.prepareStatement("update p_cases set c_advancepmtid=0 , c_settled='NO' , c_paidinadvance='NO' "
					+ " where c_advancepmtid =? and c_paidinadvance ='YES' ");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			// last thing , delete
			pst = conn.prepareStatement("delete from p_inadvance_cust_pmt  where advpmt_id = ?");
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
