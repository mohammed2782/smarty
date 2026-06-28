package com.app.setup;

import smarty.core.CoreMgr;

public class SetupBranchesPickUpAgents extends CoreMgr{
	public SetupBranchesPickUpAgents () {
		MainSql = "select kbid, kbcode, kbdesc, kbcat1,kbcat2  from kbgeneral where kbcat1='BRANCH'  and kbcat2='PICKUPAGENT'";
		
		userDefinedGridCols.add("kbcode");
		userDefinedGridCols.add("kbdesc");
		
		userDefinedNewCols.add("kbcat1");
		userDefinedNewCols.add("kbcat2");
		userDefinedNewCols.add("kbcode");
		userDefinedNewCols.add("kbdesc");
		
		userDefinedLookups.put("kbcode", "SELECT kbcode,kbdesc FROM kbgeneral where kbcat1='RCPBOOKS' and kbcat2='BRANCH'");
		userDefinedLookups.put("kbdesc", "select us_id, us_name from kbusers where us_rank= 'PICKUPAGENT' ");
		userDefinedColLabel.put("kbcode", "الفرع");
		userDefinedColLabel.put("kbdesc", "مندوب الإستلام");
		
		userDefinedNewColsDefualtValues.put("kbcat1", new String[] {"BRANCH"});
		userDefinedNewColsDefualtValues.put("kbcat2", new String[] {"PICKUPAGENT"});
		
		userDefinedReadOnlyNewCols.add("kbcat1");
		userDefinedReadOnlyNewCols.add("kbcat2");
		
		canNew = true;
		canEdit = true;
		canDelete = true;
		
		mainTable = "kbgeneral";
		keyCol = "kbid";
		
		userDefinedCaption = "مندوبين إستلام";
	}
}
