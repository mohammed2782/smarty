package com.app.setup;

import smarty.core.CoreMgr;

public class SetupPlaceList extends CoreMgr{
	public SetupPlaceList(){
		MainSql ="select * from kbplace";

		userDefinedCaption = "إعدادت المحلة";
		userDefinedNewCaption = "إضافة بيانات المحلة";
		userDefinedEditCaption = "تعديل بيانات المحلة";
		
		keyCol = "p_id";
		mainTable ="kbplace";
		
		search_paramval = null;
		canNew = true;
		canFilter = true;
		canEdit = true;
		canDelete = true;
		clickableRow = false;
		
		userDefinedGridCols.add("p_id");
		userDefinedGridCols.add("p_name");
		
		userDefinedColLabel.put("p_id", "رقم المحلة");
		userDefinedColLabel.put("p_name", "إسم المحلة");

		userDefinedNewCols.add("p_name");

		userDefinedFilterCols.add("p_name");
		userDefinedFilterLookups.put("p_name", "select p_name , p_name from kbplace");

		userDefinedColsMustFill.add("p_name");

		userDefinedEditCols.add("p_name");
 }	
}


