package com.app.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class CustomersWithSpecialPricesDetailPopup extends CoreMgr {
	public CustomersWithSpecialPricesDetailPopup() {
		MainSql = "SELECT sp_id,sp_price, sp_rural_price,cust_name,cust_id,cust_branch,sp_statecode,st_name_ar,st_branch,sp_custid FROM kbcustomer_specialprices  JOIN kbcustomers ON (sp_custid = cust_id)  JOIN kbstate ON (sp_statecode = st_code) where cust_branch = '{userstorecode}' and st_branch = 1 and cust_id={cust_id} ";
		mainTable = "kbcustomer_specialprices";
	    keyCol = "sp_id";
		UserDefinedPageRows = 200;

		userDefinedColLabel.put("st_name_ar", "المحافظة");
		userDefinedColLabel.put("sp_price", "سعر التوصيل لداخل المدينة");
		userDefinedColLabel.put("sp_rural_price", "سعر التوصيل للاطراف");

		userDefinedGridCols.add("st_name_ar");
		userDefinedGridCols.add("sp_price");
		userDefinedGridCols.add("sp_rural_price");


	}

}
