package com.app.cases;

import smarty.core.CoreMgr;

public class DisplaySingleCaseInfo extends CoreMgr{
	public DisplaySingleCaseInfo() {
		MainSql = "select * from p_cases where c_custreceiptnoori = {customerreceiptfromsearch}";
		canEdit = true;
		displayMode = "GRIDEDIT";
	}

}
