package com.app.reports;

import smarty.core.CoreMgr;

public class rpt_items_report extends CoreMgr {
	public rpt_items_report(){
		MainSql =" select * from item_rpt ";
		canFilter = true;
		userDefinedFilterCols.add("item");
		userDefinedNewColsHtmlType.put("item","DROPLIST");
		userDefinedLookups.put("item", "select g_id , gname From kbgoods order by gname asc" );
		
		userDefinedGridCols.add("expiarydate");
		userDefinedGridCols.add("startdate");
		userDefinedGridCols.add("totremainSetup");
		userDefinedGridCols.add("totsold");
		userDefinedGridCols.add("totalbought");
		userDefinedGridCols.add("item");
		
		userDefinedColLabel.put("item", "المادة");
		userDefinedColLabel.put("totalbought", "الكمية المشتراة");
		userDefinedColLabel.put("totsold", "الكمية المباعة");
		userDefinedColLabel.put("totremain", " الكمية المتبقية");
		userDefinedColLabel.put("startdate", "تاريخ الأنتاج ");
		userDefinedColLabel.put("expiarydate","تاريخ أنتهاء الصلاحية");
		
		userDefinedCaption = " مواد";

		
	}
}
