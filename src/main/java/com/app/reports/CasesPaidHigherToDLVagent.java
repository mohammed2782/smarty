package com.app.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class CasesPaidHigherToDLVagent extends CoreMgr{

	public CasesPaidHigherToDLVagent(){

		MainSql = "select c_custreceiptnoori, c_assignedagent, us_name, c_agentshare,"
				+ " ifnull(cc_pathcost, c_shipment_cost) as income  , "
				+ " ifnull(cc_frombranch,{userstorecode}) as cc_frombranch, "
				+ " ifnull(cc_tobranch,{userstorecode}) as cc_tobranch,  "
				+ "(case when c_agentsharesettled = 'FULL' then 'تمت المحاسبة' "
				+ "else  'لم تتم المحاسبة' end) as c_agentsharesettled "
				+ "from p_cases  "
				+ "left join p_caseschain "
				+ " on  cc_caseid = c_id and (cc_tobranch = {userstorecode}) and c_agentshare > cc_pathcost "
				+ "join kbusers on c_assignedagent = us_id "
				+ " and (us_branchcode = {userstorecode}) and us_rank = 'DLVAGENT' "
				+ "where  ( c_agentshare > cc_pathcost "
				+ "        or c_agentshare > c_shipment_cost) "
				+ " and c_alllowagentpay = 'Y' and q_branch = {userstorecode}  ";    
		
		canFilter = true;

		userDefinedCaption = "طلبات تمت محاسبة المندوب بتكلفة اعلي";
		UserDefinedPageRows = 1000;
		
		
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("us_name");
		userDefinedGridCols.add("c_agentshare");
		userDefinedGridCols.add("cc_frombranch");
		userDefinedGridCols.add("cc_tobranch");
		userDefinedGridCols.add("income");
		userDefinedGridCols.add("c_agentsharesettled");
	
		
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_assignedagent" , "مندوب التوصيل");
		userDefinedColLabel.put("us_name" , "مندوب التوصيل");
		userDefinedColLabel.put("c_agentshare" , "حصة مندوب التوصيل");
		userDefinedColLabel.put("cc_frombranch" , "من فرع");
		userDefinedColLabel.put("cc_tobranch" , "الي فرع");
		userDefinedColLabel.put("income" , "أجرة التوصيل");
		userDefinedColLabel.put("c_agentsharesettled" , "محاسبة مندوب التوصيل");
	
		
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("c_assignedagent");
		userDefinedFilterCols.add("c_agentsharesettled");
		userDefinedFilterColsHtmlType.put("c_assignedagent", "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_agentsharesettled", "DROPLIST");

		
		userDefinedFilterLookups.put("c_agentsharesettled", "SELECT kbcode, kbdesc FROM kbgeneral where kbcat1='SETTLED' ");

		userDefinedLookups.put("cc_frombranch", "SELECT branch_id, branch_name FROM kbbranches ");
		userDefinedLookups.put("cc_tobranch", "SELECT branch_id, branch_name FROM kbbranches ");

		
	}//end of no-args constructor CasesPaidHigherToDLVagent
	
	@Override
	public void initialize(HashMap smartyStateMap){
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		userDefinedFilterLookups.put("c_assignedagent", "SELECT us_id, us_name FROM kbusers WHERE us_rank = 'DLVAGENT' and us_branchcode = " + currentBranch + " ");

		super.initialize(smartyStateMap);
	}//end of method initialize
		
}//end of class CasesPaidHigherToDLVagent

