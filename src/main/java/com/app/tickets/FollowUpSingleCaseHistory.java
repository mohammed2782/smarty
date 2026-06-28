package com.app.tickets;

import smarty.core.CoreMgr;

public class FollowUpSingleCaseHistory extends CoreMgr {
	public FollowUpSingleCaseHistory(int caseId) {
		MainSql = "select * from p_cases_followup where cf_caseid="+caseId+" order by cf_id desc";
		
		userDefinedGridCols.add("cf_decision_made");
		userDefinedGridCols.add("cf_notes");
		userDefinedGridCols.add("cf_userid");
		userDefinedGridCols.add("cf_createddt");
		
		userDefinedColLabel.put("cf_decision_made", "القرار");
		userDefinedColLabel.put("cf_notes", "الملاحظات");
		userDefinedColLabel.put("cf_userid", "الموظف");
		userDefinedColLabel.put("cf_createddt", "بتاريخ");
		
		userDefinedCaption = "المتابعة";
		
		userDefinedLookups.put("cf_decision_made", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'FOLLOWUP' and kbcat2 = 'ACTIONS'");
		userDefinedLookups.put("cf_userid", "select us_id, us_name from kbusers");
	}
}
