package com.app.cust.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class Payments extends CoreMgr{
	public Payments() {
		MainSql = " select cp_id ,cp_mastercustid, cp_totreceiptsamt,cp_amount_paid_actually, cp_debt , cp_credit,  cp_createdby ,cp_rmk, '' as fake "
				+ " from  p_customer_payments where cp_mastercustid = {mastercustidlogin} and cp_pmttype='CASES' order by cp_id desc ";

		keyCol = "cp_id";
		mainTable = "p_customer_payments";

		// ///////////////
		userDefinedGridCols.add("cp_id");
		userDefinedGridCols.add("cp_mastercustid");
		userDefinedGridCols.add("cp_totreceiptsamt");
		userDefinedGridCols.add("cp_amount_paid_actually");
		userDefinedGridCols.add("cp_credit");
		userDefinedGridCols.add("cp_createdby");
		userDefinedGridCols.add("cp_createdby");
		userDefinedGridCols.add("cp_rmk");
		userDefinedGridCols.add("fake");
	
		// //////////////
		userDefinedCaption = "تسديدات للعملاء";
		userDefinedColLabel.put("cp_id", "رقم الأيصال");
		userDefinedColLabel.put("cp_amount_paid", "المبلغ المدفوع");
		userDefinedColLabel.put("cp_totreceiptsamt", "مبلغ الوصولات");
		userDefinedColLabel.put("cp_amount_paid_actually", "المبلغ المسدد للعميل");
		userDefinedColLabel.put("cp_credit", "أستقطاع دين");
		userDefinedColLabel.put("cp_mastercustid", "العميل");
		userDefinedColLabel.put("cp_createddt", "تاريخ الدفع");
		userDefinedColLabel.put("cp_rmk", " ملاحظات");
		userDefinedColLabel.put("cp_createdby", "أنشئ بواسطة ");
		userDefinedColLabel.put("fake", "طباعة ايصال الدفع ");
		userDefinedColLabel.put("price", "المبلغ المطلوب دفعه");

		canDelete = false;
		userModifyTD.put("fake", "printPmtReceipt({cp_id})");
	
		userDefinedLookups.put("cp_mastercustid","select mcust_id ,mcust_name  From kb_mastercustomer where mcust_id={mastercustidlogin}");
		userDefinedLookups.put("cp_createdby","select us_id, us_name from kbusers");
		myhtmlmgr.refreshPageOnDelete = true;
		
		UserDefinedPageRows = 10;
	}// end of constructor customer_payment  

	
	public String printPmtReceipt(HashMap<String, String> hashy) {
		String btn = "<a href=\"../../PaymentReceiptSRVL?cp_id="+hashy.get("cp_id")+"\" "
				+ " class='btn btn-sm btn-warning' >طباعة أيصال دفع <i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
	}



	

}// end of class customer_payment
