package com.app.setup;

import smarty.core.CoreMgr;

public class SetupStateList extends CoreMgr{
	public SetupStateList(){
	
			
		MainSql ="select * from kbstate";

		userDefinedCaption = "إعدادت المحافظة";
		userDefinedNewCaption = "إضافة بيانات المحافظة";
		userDefinedEditCaption = "تعديل بيانات المحافظة";
		
		keyCol = "st_id";
		mainTable ="kbstate";
		
		search_paramval = null;
		canNew = true;
		//canFilter = true;
		canEdit = true;
		canDelete = true;
		clickableRow = false;
		
		userDefinedGridCols.add("st_id");
		userDefinedGridCols.add("st_name_ar");
		//userDefinedGridCols.add("st_name_en");
		userDefinedGridCols.add("st_code");
		userDefinedGridCols.add("st_active");
		userDefinedGridCols.add("st_order");
		
		userDefinedColLabel.put("st_id", "رقم المحافظة");
		userDefinedColLabel.put("st_name_ar", "إسم المحاافظة");
		userDefinedColLabel.put("st_code", "شفره");
		userDefinedColLabel.put("st_active", "نشط");
		userDefinedColLabel.put("st_order", "تسلسل");

		userDefinedNewCols.add("st_name_ar");
		userDefinedNewCols.add("st_code");
		userDefinedNewCols.add("st_active");
		userDefinedNewCols.add("st_order");

		//userDefinedFilterLookups.put("s_name", "select s_name , s_name from kbstate");

		userDefinedColsMustFill.add("st_name_ar");
		userDefinedColsMustFill.add("st_code");
		userDefinedColsMustFill.add("st_active");
		userDefinedColsMustFill.add("st_order");

		userDefinedEditCols.add("st_name_ar");
		userDefinedEditCols.add("st_code");
		userDefinedEditCols.add("st_active");
		userDefinedEditCols.add("st_order");
 }	
}
