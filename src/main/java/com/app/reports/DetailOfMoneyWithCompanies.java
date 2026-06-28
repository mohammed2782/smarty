package com.app.reports;

import smarty.core.CoreMgr;

public class DetailOfMoneyWithCompanies extends CoreMgr {
	public DetailOfMoneyWithCompanies(){
		MainSql = "select  '' as total, branch_name, sum(c_receiptamt) - sum(cc_pathcost) as totreceiptamt "
				+ " , sum(c_receiptamt_usd) as totreceiptamt_usd "
				+ "from p_cases "
				+ "join p_caseschain on (c_id = cc_caseid and cc_frombranch = {branch_code_finanicalstatus_popup} and cc_branchpmtid=0 and cc_branchrecievedpmt='N') "
				+ "left join kbbranches on branch_id=cc_tobranch "
				+ "where q_stage='DLV' GROUP by cc_tobranch ";
		
		userDefinedCaption = "دفوعات مستحقة للفروع";
		userDefinedGridCols.add("branch_name");		
		userDefinedGridCols.add("totreceiptamt");
		userDefinedGridCols.add("totreceiptamt_usd");
		
		userDefinedColLabel.put("branch_name", "اسم الفرع");
		userDefinedColLabel.put("totreceiptamt", "المبلغ دينار عراقي");
		userDefinedColLabel.put("totreceiptamt_usd", "المبلغ $");
		
			
		userDefinedFilterCols.add("branch_name");
		
		userDefinedSumCols.add("totreceiptamt");
		userDefinedSumCols.add("totreceiptamt_usd");
		UserDefinedPageRows = 1000;
		
		userDefinedGroupByCol="total";
	}
}
