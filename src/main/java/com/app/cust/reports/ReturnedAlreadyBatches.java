package com.app.cust.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class ReturnedAlreadyBatches extends CoreMgr{
	public ReturnedAlreadyBatches() {
		
		MainSql = "select '' as showdel, acr_id,acr_mastercustid, acr_createdby, acr_rmk, '' as fake, "
				+ "acr_createddt from p_customer_return where acr_mastercustid={mastercustidlogin} and acr_closed='Y' "
				+ " order by acr_id desc ";
		
		keyCol = "acr_id";
		mainTable = "p_customer_return";
		
		userDefinedGridCols.add("acr_id");
		
		userDefinedGridCols.add("acr_createddt");
		userDefinedGridCols.add("acr_createdby");
		userDefinedGridCols.add("acr_rmk");
		userDefinedGridCols.add("fake");

		userDefinedCaption = "الرواجع للزبائن";
		userDefinedColLabel.put("acr_id", "رقم الأيصال");
		userDefinedColLabel.put("acr_createddt", "تاريخ الاستلام الفعلي");
		userDefinedColLabel.put("acr_rmk", " ملاحظات");
		userDefinedColLabel.put("acr_createdby", "أنشئ بواسطة ");
		userDefinedColLabel.put("fake", "طباعة ايصال الاستلام ");
		userModifyTD.put("fake", "printPmtReceipt({acr_id},{userbranch})");
		
		userDefinedLookups.put("acr_createdby", "select us_id, us_name from kbusers");
		

	}// end of constructor 
	
	public String printPmtReceipt(HashMap<String, String> hashy) {
		String userbranch = replaceVarsinString(" {userstorecode} ", arrayGlobals).trim();
		String btn = "<a href=\"../../CustomerBackedReturnSRVL?regulator=fromcustomer&acr_id="+hashy.get("acr_id")+"&userbranch="+userbranch+"\" "
				+ " class='btn btn-xs btn-warning' >طباعة أيصال الراجع <i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
	}

	

}
