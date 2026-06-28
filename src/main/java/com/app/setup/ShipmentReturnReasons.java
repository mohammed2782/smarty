package com.app.setup;

import smarty.core.CoreMgr;

public class ShipmentReturnReasons extends CoreMgr{
	public ShipmentReturnReasons(){
		MainSql = "select * from kbrtn_reasons";
		mainTable = "kbrtn_reasons";
		keyCol = "rtn_id";
		
		userDefinedCaption = "أسباب الارجاع";
		userDefinedNewCaption = "اضافة سبب ارجاع جديد";
		userDefinedEditCaption = "تعديل سبب الارجاع";
		
		canDelete = true;
		canEdit = true;
		canNew = true;
		
		userDefinedGridCols.add("rtn_code");
		userDefinedGridCols.add("rtn_desc");
		
		userDefinedColLabel.put("rtn_code", "كود الأرجاع");
		userDefinedColLabel.put("rtn_desc", "سبب الارجاع");
		
		userDefinedEditCols.add("rtn_code");
		userDefinedEditCols.add("rtn_desc");
		
		userDefinedNewCols.add("rtn_code");
		userDefinedNewCols.add("rtn_desc");
		
		userDefinedColsMustFill.add("rtn_code");
		userDefinedColsMustFill.add("rtn_desc");
	}

}
