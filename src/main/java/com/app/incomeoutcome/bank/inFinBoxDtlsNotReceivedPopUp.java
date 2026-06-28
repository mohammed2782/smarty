package com.app.incomeoutcome.bank;

import smarty.core.CoreMgr;

public class inFinBoxDtlsNotReceivedPopUp extends CoreMgr{
	public inFinBoxDtlsNotReceivedPopUp() {
		MainSql = " select  * from p_agent_payments where  (ap_createdby  = '{inboxnotrcvcreatedby}' and ap_safeid = 0)"; 

		userDefinedGroupByCol = "ap_createdby";
		userDefinedGridCols.add("ap_amtreceived");
		userDefinedGridCols.add("ap_paymentdt");
		userDefinedGridCols.add("ap_agentid");
		//userDefinedGridCols.add("tranname");
		
		//userDefinedColLabel.put("ap_createdby", "المحاسب");
		userDefinedColLabel.put("ap_amtreceived", "المبلغ");
		userDefinedColLabel.put("ap_paymentdt", "تاريخ الاستلام");
		userDefinedColLabel.put("ap_agentid", "مندوب التوصيل");
		
		userDefinedSumCols.add("ap_amtreceived");
		
		userDefinedLookups.put("ap_agentid", "select us_id, us_name from kbusers");
		
		userDefinedCaption = "تفاصيل مبلغ الصندوق";
		UserDefinedPageRows = 1000;
		
		
	}
}
