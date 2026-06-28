package com.app.reports;

import smarty.core.CoreMgr;

public class SuppBalance extends CoreMgr {
	public SuppBalance(){
		MainSql = " select * from vw_suppbalance where psupp is not null  order by psupp , type ";
		
		userDefinedFilterCols.add("psupp");
		userDefinedNewColsHtmlType.put("psupp" , "DROPLIST");
		userDefinedLookups.put("psupp", "select supp_id , supp_name from kbsupplier" );
		
		userDefinedLookups.put("type", "select 'pmt' , 'مجموع المبلغ المدفوع' from dual union select 'debt' , 'مجموع قيمة الفواتير' from dual" );
		
		userDefinedGridCols.add("type");
		userDefinedGridCols.add("amt");
		
		//userDefinedGridCols.add("pcust");
		
		userDefinedColLabel.put("amt","المبلغ" );
		userDefinedColLabel.put("type","العملية" );
		userDefinedColLabel.put("psupp","المجهز" );
		
		userDefinedSumCols.add("amt");
		userDefinedGroupByCol = "psupp";
		userDefinedGroupColsOrderBy = "psupp , type";
		
		userDefinedCaption = "حسابات المجهزين";
		groupSumCaption =" المجموع";
		clickableRow =true;
		canFilter = true;
		userDefinedGlobalClickRowID = "transrptsuppid";
		keyCol = "psupp";
		//slidingGroups = true;
	}

}
