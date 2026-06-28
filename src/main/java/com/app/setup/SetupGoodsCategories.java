package com.app.setup;

import smarty.core.CoreMgr;

public class SetupGoodsCategories extends CoreMgr{
	public SetupGoodsCategories() {
		MainSql = "select * from kbcategorygoods";
		canEdit = true;
		canNew = true;
		canDelete = true;
		mainTable = "kbcategorygoods";
		keyCol = "catg_id";
		
		userDefinedGridCols.add("catg_name");
		userDefinedGridCols.add("catg_createdby");
		
		userDefinedNewCols.add("catg_name");
		userDefinedNewCols.add("catg_createdby");
		
		userDefinedColLabel.put("catg_name", "الصنف");
		userDefinedColLabel.put("catg_createdby","أنشأ بواسطة");
		
		userDefinedLookups.put("catg_createdby", "select us_id , us_name from kbusers ");
		userDefinedNewColsDefualtValues.put("catg_createdby", new String [] {"{userid}"});
		userDefinedReadOnlyNewCols.add("catg_createdby");
		
		userDefinedEditCols.add("catg_name");
		
		UserDefinedPageRows = 100;
	}
}
