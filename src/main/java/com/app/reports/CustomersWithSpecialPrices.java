package com.app.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class CustomersWithSpecialPrices extends CoreMgr {
	public CustomersWithSpecialPrices() {
		MainSql = "SELECT '' as actions,  sp_id,sp_price, sp_rural_price,cust_name,cust_id,cust_branch,cust_assigned_pickup_agent,sp_statecode,st_name_ar,st_branch,sp_custid FROM kbcustomer_specialprices  JOIN kbcustomers ON (sp_custid = cust_id)  JOIN kbstate ON (sp_statecode = st_code) where cust_branch = '{userstorecode}' and st_branch = 1 group by cust_id ";
		mainTable = "kbcustomer_specialprices";
	    keyCol = "sp_id";
		canFilter = true;
		UserDefinedPageRows = 200;

		userDefinedFilterCols.add("cust_id");
		userDefinedFilterColsHtmlType.put("cust_id", "DROPLIST");
		userDefinedFilterCols.add("cust_assigned_pickup_agent");
		userDefinedFilterColsHtmlType.put("cust_assigned_pickup_agent", "DROPLIST");
		userDefinedLookups.put("cust_id","select cust_id ,cust_name  From kbcustomers where  cust_branch = {userstorecode}");
		userDefinedLookups.put("cust_assigned_pickup_agent","select us_id ,us_name  From kbusers where  us_branchcode = {userstorecode} and us_rank = 'PICKUPAGENT' ");

		userDefinedColLabel.put("cust_id", "اسم الزبون");
		userDefinedColLabel.put("cust_assigned_pickup_agent", "اسم مندوب الاستلام");

	    userDefinedColLabel.put("actions", " ");

		userDefinedGridCols.add("cust_id");
		userDefinedGridCols.add("cust_assigned_pickup_agent");
		userDefinedGridCols.add("actions");

		 userModifyTD.put("actions", "showActions({cust_id},{cust_name})");

		userDefinedCaption = "الزبائن ذو اسعار خاصة";

	}

	
	public String showActions(HashMap<String,String> hashy) {
		String html = "";
		html = "<td><div class='row'>";
		html +="<div class='col-3'><button type=\"button\" class=\"btn btn-sm btn-success\" onclick=\"popitup ('CustomersWithSpecialPricesDetailPopup?cust_id="+hashy.get("cust_id")+"&cust_name="+hashy.get("cust_name")+"' , '' , 1000 ,600);\">الاسعار</button></div>";
		html +="</div></td>";
		return html;
	}
	
}
