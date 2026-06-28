package com.app.cases;


import smarty.core.CoreMgr;

public class SingleCaseInformation extends CoreMgr {
	public SingleCaseInformation() {
		MainSql= "select * from p_cases ";
		keyCol = "c_id";
		userDefinedEditFormColNo = 3;
		setDisplayMode("DISPLAYSINGLE");
		userDefinedFieldSetCols.put("c_custid", "معلومات المرسل");
		userDefinedFieldSetEndWithCols.add("c_custhp");
		
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_custhp");
		
		
		userDefinedFieldSetCols.put("c_id", "معلومات المستلم");
		userDefinedFieldSetEndWithCols.add("c_shipment_cost");
		
		userDefinedGridCols.add("c_id");
		
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_rcv_hp");
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("c_rcv_city");
	
		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("c_branchcode");
		userDefinedGridCols.add("c_createdby");
		userDefinedGridCols.add("c_createddt");
		
		userDefinedColLabel.put("c_cmid", "رقم الطلبيه");
		userDefinedColLabel.put("c_branchcode", "مخزن");
		userDefinedColLabel.put("c_id", "رقم الشحنه");
		userDefinedColLabel.put("c_custhp", "هاتف");
		userDefinedColLabel.put("c_createdby", "طلبت من خلال");
		userDefinedColLabel.put("c_custid", "المرسل");
		
		
		userDefinedColLabel.put("c_rcv_name", "أسم المستلم");
		userDefinedColLabel.put("c_rcv_hp", "هاتف");
		userDefinedColLabel.put("c_rcv_state", "المحافظه");
		userDefinedColLabel.put("c_rcv_city", "المدينه");
		
		userDefinedColLabel.put("c_rcv_addr_rmk", "وصف أدق لعنوان المستلم");
		
		userDefinedColLabel.put("c_shipment_cost", "مبلغ الشحن");
		userDefinedColLabel.put("c_createddt", "تاريخ ووقت طلب الشحنه");
		
		userDefinedLookups.put("c_custid", "SELECT c_id , c_name FROM kbcustomers");
		userDefinedLookups.put("c_branchcode", "select store_code , store_name from kbstores where store_deleted='N' ");
		userDefinedLookups.put("c_rcv_state", "SELECT st_code , st_name_ar FROM kbstate");
		userDefinedLookups.put("c_rcv_city", "SELECT ct_code , ct_name_ar FROM kbcity");
		
	}
}
