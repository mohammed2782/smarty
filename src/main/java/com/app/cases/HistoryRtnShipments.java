package com.app.cases;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class HistoryRtnShipments extends CoreMgr {
	public HistoryRtnShipments() {
		MainSql = "select  date(q_enterdate) as rtndate, count(*) as cases,c_branchcode, c_name,c_custid, '' as printbtn from p_queue " + 
				"join p_cases on (c_id = q_caseid)  " + 
				"join kbcustomers on (c_custid = kbcustomers.c_id) " + 
				"where q_stage = 'cncl' and q_step = 'delv_back_to_shipper' and q_status ='END'"
				+ " group by date(q_enterdate), c_name, c_branchcode order by q_enterdate desc";
		
		userDefinedColLabel.put("rtndate", "تاريخ الإرجاع");
		userDefinedColLabel.put("cases" , "عدد الشحنات");
		userDefinedColLabel.put("c_name" , "أسم العميل");
		userDefinedColLabel.put("c_custid" , "أسم العميل");
		userDefinedColLabel.put("printbtn" , "طباعة المنفيست للراجع");
		
		
		userDefinedGridCols.add("c_name");
		userDefinedGridCols.add("rtndate");
		userDefinedGridCols.add("cases");
		userDefinedGridCols.add("printbtn");
		
		userDefinedFilterCols.add("c_custid");
		
		UserDefinedPageRows = 100;
		
		userModifyTD.put("printbtn", "displayPrintButton({rtndate},{c_custid}, {c_branchcode})");
		
		userDefinedCaption = "قوائم المرتجعات";
		
		canFilter = true;
		userDefinedLookups.put("c_custid","select c_id , c_name from kbcustomers");
		userDefinedFilterColsHtmlType.put("c_custid", "DROPLIST");
	}
	
	public String displayPrintButton(HashMap<String, String> hashy) {
		String btn = "<a href=\"../TLRPrintReturnedItmesPerDateSRVL?branchCode="
	+hashy.get("c_branchcode")+"&cust_id="+hashy.get("c_custid")+"&rtndate="+hashy.get("rtndate")+"\" "
				+ " class='btn btn-xs btn-danger' >طباعة المنفيست للراجع<i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
	}
}
