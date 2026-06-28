package com.app.setup;

import smarty.core.CoreMgr;

public class PostponedOptions extends CoreMgr {
	public PostponedOptions() {
		
		MainSql = "select * from kbpostponedoptions";
		mainTable = "kbpostponedoptions";
		keyCol = "post_id";
		
		userDefinedCaption = "أسباب ألتأجيل";
		userDefinedNewCaption = "اضافة سبب ألتأجيل جديد";
		userDefinedEditCaption = "تعديل سبب ألتأجيل";
		
		canDelete = true;
		canEdit = true;
		canNew = true;
		
		userDefinedGridCols.add("post_code");
		userDefinedGridCols.add("post_desc");
		
		userDefinedColLabel.put("post_code", "كود ألتأجيل");
		userDefinedColLabel.put("post_desc", "سبب ألتأجيل");
		
		userDefinedEditCols.add("post_code");
		userDefinedEditCols.add("post_desc");
		
		userDefinedNewCols.add("post_code");
		userDefinedNewCols.add("post_desc");
		
		userDefinedColsMustFill.add("post_code");
		userDefinedColsMustFill.add("post_desc");
	}

}
