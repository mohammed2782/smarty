package com.app.bussframework;

import smarty.core.CoreMgr;

public class Stages extends CoreMgr{
	public  Stages() {
		MainSql = "select * from kbstage order by stg_order";
		canNew = true;
		canEdit = true;
		canDelete = true;
		
		mainTable = "kbstage";
		keyCol = "stg_code";
		
		clickableRow = true;
		userDefinedGlobalClickRowID = "stg_code";
		
		userDefinedGridCols.add("stg_code");
		userDefinedGridCols.add("stg_gotostg");
		userDefinedGridCols.add("stg_name");
		userDefinedGridCols.add("stg_order");
		
		userDefinedNewCols.add("stg_code");
		userDefinedNewCols.add("stg_name");
		userDefinedNewCols.add("stg_gotostg");
		userDefinedNewCols.add("stg_order");
		
		userDefinedColLabel.put("stg_code", "Code");
		userDefinedColLabel.put("stg_name", "Stage Name");
		userDefinedColLabel.put("stg_order", "Order");
		userDefinedColLabel.put("stg_gotostg", "Go to stage");
		
		userDefinedEditCols.add("stg_name");
		userDefinedEditCols.add("stg_order");
		userDefinedEditCols.add("stg_gotostg");
		userDefinedCaption = "Configure Stages";
		
		userDefinedColsMustFill.add("stg_code");
		userDefinedColsMustFill.add("stg_name");
		userDefinedColsMustFill.add("stg_order");
	}
}
