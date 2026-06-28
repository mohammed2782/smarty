package com.app.setup;

import smarty.core.CoreMgr;

public class SetupExtraCost extends CoreMgr {

	public SetupExtraCost(){
		MainSql = "select * From kbextracost";
		userDefinedGridCols.add("cost_id");
		userDefinedGridCols.add("cost_name");
		
		userDefinedColLabel.put("cost_id", "شفرة");
		userDefinedColLabel.put("cost_name", "نوع التكلفة");
		
		userDefinedColsMustFill.add("cost_name");
		userDefinedReadOnlyEditCols.add("cost_id");
		
		userDefinedNewCols.add("cost_name");
		
		canNew = true;
		canDelete = true;
		keyCol = "cost_id";
		mainTable ="kbextracost";
		
		userDefinedCaption = "أنواع المصاريف العامة";
	}
}
