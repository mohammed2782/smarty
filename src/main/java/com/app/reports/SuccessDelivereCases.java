package com.app.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class SuccessDelivereCases extends  CoreMgr {
	public SuccessDelivereCases() {
				
		MainSql = "SELECT c_custreceiptnoori, c_assignedagent, us_name, c_receiptamt  "
				+ "from p_cases  "
				+ "join kbusers on (c_assignedagent = us_id and us_branchcode = {userstorecode})"
				+ "join p_dlvagentmanifest on dam_id = c_dlvagent_manifestid  "
				+ "left join kbcustomers on cust_id = c_custid  "
				+ "left join kbstate on (st_code = c_rcv_state and st_branch = {userstorecode}) "
				+ "where q_stage='DLV' and c_alllowagentpay = 'Y'  and c_agentsharesettled !='FULL'  "
				+ "and c_agentpmtid = 0 and c_receiptamt >= 100000 and (c_branchcode = {userstorecode})";
		
		canFilter = true;

		userDefinedCaption = "طلبات سلمت بنجاح ولم يتم محاسبة المندوب وتكلفتها أكبر من 100000";
		
		
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("us_name");
		userDefinedGridCols.add("c_receiptamt");
				
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_assignedagent" , "مندوب التوصيل");
		userDefinedColLabel.put("us_name" , "مندوب التوصيل ");
		userDefinedColLabel.put("c_receiptamt" , "مبلغ الوصل ");	
		
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("c_assignedagent");
		userDefinedFilterColsHtmlType.put("c_assignedagent", "DROPLIST");
		
	}//end of no-args constructor SuccessDelivereCases
	
	@Override
	public void initialize(HashMap smartyStateMap){
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		userDefinedFilterLookups.put("c_assignedagent", "SELECT us_id, us_name FROM kbusers WHERE us_rank = 'DLVAGENT' and us_branchcode = " + currentBranch + " ");

		super.initialize(smartyStateMap);
	}//end of method initialize
	
}//end of class SuccessDelivereCases
