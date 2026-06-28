package com.app.incomeoutcome.bank;

import smarty.core.CoreMgr;

public class InboxSafeDtlsPopUp extends CoreMgr{
	public InboxSafeDtlsPopUp() {
		//MainSql = " select p_agent_payments.*, us_name from p_agent_payments join kbusers on (us_id = ap_createdby) where ap_safeid = '{safeid}'"; 
		MainSql = " select id, amtreceived, paymentdt, agentid, createdby from ( "
				+ " select ap_id as id, ap_amtreceived as amtreceived, ap_paymentdt as paymentdt, concat('مندوب التوصيل : ',us_name) as agentid, ap_createdby as createdby from p_agent_payments "
				+ " join kbusers on (us_id = ap_agentid and us_branchcode = {userstorecode})"
				+ " where ap_safeid > 0 and ap_safeoff='N' "
				+ " and ap_agentid in (select us_id from kbusers where us_rank='DLVAGENT' and us_branchcode = {userstorecode}) "
				+ " union"
				+ " select bp_id as id, bp_receivedamt as amtreceived, bp_receiveddt as paymentdt, concat('دفعة مستلمة من فرع : ',branch_name) as agentid, bp_receivedby as createdby from p_branch_payments "
				+ " join kbusers on (us_id = bp_receivedby) "
				+ " join kbbranches on (bp_from_branchid = branch_id)"
				+ " where bp_received_branchid = {userstorecode} and bp_safeid > 0 and bp_safeoff = 'N')abc "; 
		
		userDefinedGroupByCol = "createdby";
		userDefinedGridCols.add("id");
		userDefinedGridCols.add("amtreceived");
		userDefinedGridCols.add("paymentdt");
		userDefinedGridCols.add("agentid");
		//userDefinedGridCols.add("tranname");
		
		//userDefinedColLabel.put("ap_createdby", "المحاسب");
		userDefinedColLabel.put("amtreceived", "المبلغ");
		userDefinedColLabel.put("paymentdt", "تاريخ الاستلام");
		userDefinedColLabel.put("agentid", "المبلغ من خلال");
		userDefinedColLabel.put("id", "رقم الدفعة");
		
		userDefinedSumCols.add("amtreceived");
		
		//userDefinedLookups.put("ap_agentid", "select us_id, us_name from kbusers");
		userDefinedLookups.put("createdby", "select us_id, us_name from kbusers");
		
		userDefinedCaption = "تفاصيل مبلغ الصندوق";
		
		UserDefinedPageRows = 1000;
		
		
	}


}
