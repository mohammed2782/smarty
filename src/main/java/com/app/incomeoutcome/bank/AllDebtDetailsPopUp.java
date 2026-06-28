package com.app.incomeoutcome.bank;

import smarty.core.CoreMgr;

public class AllDebtDetailsPopUp extends CoreMgr{
	public AllDebtDetailsPopUp() {
		MainSql = "select saf_amount_iqd, saf_createddt, '' as dummy from p_safe where saf_trantype = 'DB' and saf_branchid={userstorecode} "
				+ " and saf_tranname = 'CASH' and saf_tranentity={tranentitysafe}"
				+ " union "
				+ " select saf_amount_iqd, saf_createddt, '' as dummy  from p_safe_hist where saf_trantype = 'DB' and saf_branchid={userstorecode} "
					+ " and saf_tranname = 'CASH' and saf_tranentity={tranentitysafe}";
		
		userDefinedGroupByCol = "dummy";
		userDefinedSumCols.add("saf_amount_iqd");
		groupSumCaption = "المجموع";
		
		userDefinedGridCols.add("saf_amount_iqd");
		userDefinedGridCols.add("saf_createddt");
		
		userDefinedColLabel.put("saf_amount_iqd", "مبلغ الدين");
		userDefinedColLabel.put("saf_createddt", "تاريخ الاعطاء");
		
		UserDefinedPageRows = 1000;
		
		userDefinedCaption = "ديون";
	}
}
