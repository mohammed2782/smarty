package com.app.reports;

import smarty.core.CoreMgr;

public class MasterCustomerDebt extends CoreMgr{
	
	public MasterCustomerDebt(){
			
		MainSql = "select mcust_id, mcust_name, '' as dummy, (sum(cp_debt) - sum(cp_credit)) as debt "
				+ "from p_customer_payments "
				+ "join kb_mastercustomer on (cp_mastercustid = mcust_id and mcust_branchcode = {userstorecode} ) "
				+ " group by mcust_name having  (sum(cp_debt) - sum(cp_credit))>0 ";
		
				
		mainTable = "p_customer_payments";

		canFilter = true;

		userDefinedCaption = "تفاصيل ديون العملاء";

		userDefinedGroupColsOrderBy = "debt";
		userDefinedGroupSortMode = "DESC";
		userDefinedGroupByCol = "dummy";

		userDefinedSumCols.add("debt");
		groupSumCaption = "المجموع";
		
		userDefinedGridCols.add("mcust_name");
		userDefinedGridCols.add("debt");

		
		userDefinedColLabel.put("mcust_id", "العميل");
		userDefinedColLabel.put("mcust_name", "العميل");
		userDefinedColLabel.put("debt", "مبلغ الدين");
		
		
		userDefinedFilterCols.add("mcust_id");
		
		userDefinedFilterColsHtmlType.put("mcust_id", "DROPLIST");
		
		userDefinedLookups.put("mcust_id","select mcust_id , mcust_name  From kb_mastercustomer where mcust_branchcode = {userstorecode} ");

	}// end of no-arg MasterCustomerDebt


}// end of class MasterCustomerDebt
