package com.app.setup;

import smarty.core.CoreMgr;

public class TikectsSubjects extends CoreMgr{
	public TikectsSubjects () {
		MainSql = "select * from kbgeneral where kbcat1= 'TICKET' and kbcat2 = 'SUBJECTS' ";
		
		canNew    = true;
		canDelete = true;
		canEdit   = true;
		mainTable = "kbgeneral";
		keyCol    = "kbid";
		
		userDefinedCaption = "عناوين التذاكر";
		userDefinedGridCols.add("kbcode");
		userDefinedGridCols.add("kbdesc");
		
		userDefinedReadOnlyNewCols.add("kbcat1");
		userDefinedReadOnlyNewCols.add("kbcat2");
		
		userDefinedNewColsDefualtValues.put("kbcat1", new String [] {"TICKET"});
		userDefinedNewColsDefualtValues.put("kbcat2", new String [] {"SUBJECTS"});
		
		userDefinedNewCols.add("kbcat1");
		userDefinedNewCols.add("kbcat2");
		userDefinedNewCols.add("kbcode");
		userDefinedNewCols.add("kbdesc");
		
		userDefinedEditCols.add("kbcode");
		userDefinedEditCols.add("kbdesc");
		
		userDefinedColsMustFill.add("kbcode");
		userDefinedColsMustFill.add("kbdesc");
		
	}
}
