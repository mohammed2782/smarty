package com.app.reports;

import smarty.core.CoreMgr;

public class DetailOfMoneyWithAgents extends CoreMgr {
	public DetailOfMoneyWithAgents(){
		MainSql = "select  '' as total, sum(c_receiptamt) - sum(c_agentshare) as totreceiptamt"
				+ ",  sum(c_receiptamt_usd) as totreceiptamt_usd , us_name "
				+ "from p_cases "
				+ "join kbusers on us_id = c_assignedagent  and us_rank = 'DLVAGENT' "
				+ "where  us_branchcode= {branch_code_finanicalstatus_popup} and c_alllowagentpay = 'Y' "
				+ "and c_agentsharesettled !='FULL'  and c_agentpmtid = 0 group by us_id ";
		
		userDefinedCaption = "مبالغ عند المندوبين";
		userDefinedGridCols.add("us_name");		
		userDefinedGridCols.add("totreceiptamt");
		userDefinedGridCols.add("totreceiptamt_usd");
		userDefinedColLabel.put("us_name", "اسم المندوب");
		userDefinedColLabel.put("totreceiptamt", "المبلغ دينار عراقي");
		userDefinedColLabel.put("totreceiptamt_usd", "المبلغ $");
		
		
		userDefinedFilterCols.add("us_name");
		
		userDefinedSumCols.add("totreceiptamt");
		userDefinedSumCols.add("totreceiptamt_usd");
		UserDefinedPageRows = 1000;
		
		userDefinedGroupByCol="total";
	}
}
