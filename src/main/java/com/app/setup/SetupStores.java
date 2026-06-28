package com.app.setup;

import smarty.core.CoreMgr;

public class SetupStores extends CoreMgr {
	public SetupStores () {
		MainSql = "select * from kbstores";
		
		canNew = true;
		canEdit = true;
		canDelete = true;
		mainTable = "kbstores";
		keyCol = "store_id";
		
		userDefinedGridCols.add("store_code");
		userDefinedGridCols.add("store_name");
		
		userDefinedColLabel.put("store_code", "Code");
		userDefinedColLabel.put("store_name", "Store Name");
		
		userDefinedNewCols.add("store_code");
		userDefinedNewCols.add("store_name");
		
		userDefinedEditCols.add("store_code");
		userDefinedEditCols.add("store_name");
		
		userDefinedColsMustFill.add("store_code");
		userDefinedColsMustFill.add("store_name");
		
		userDefinedCaption = "مخازن";
		
	}
}
