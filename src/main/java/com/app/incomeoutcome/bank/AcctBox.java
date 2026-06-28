package com.app.incomeoutcome.bank;

import smarty.core.CoreMgr;

public class AcctBox extends CoreMgr{
	public AcctBox() {
		MainSql = "select *, 'مبالغ في الصناديق ' as dummy from p_accountantbox where acb_userbranchid = {userstorecode}";
		
		userDefinedGroupByCol = "dummy";
		userDefinedSumCols.add("acb_balance_iqd");
		userDefinedSumCols.add("acb_balance_usd");
		groupSumCaption = "المجموع";
		
		userDefinedGridCols.add("acb_usid");
		userDefinedGridCols.add("acb_balance_iqd");
		userDefinedGridCols.add("acb_balance_usd");
		
		userDefinedColLabel.put("acb_usid", "صندوق مالي");
		userDefinedColLabel.put("acb_balance_iqd", "المبلغ د.ع");
		userDefinedColLabel.put("acb_balance_usd", "المبلغ $");
		
		userDefinedLookups.put("acb_usid", "select us_id, us_name from kbusers");
		
		userDefinedCaption = "مبالغ في الصناديق مالية";
		
	}

}
