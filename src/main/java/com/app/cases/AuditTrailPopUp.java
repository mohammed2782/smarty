package com.app.cases;

import smarty.core.CoreMgr;

public class AuditTrailPopUp  extends CoreMgr{
	
	public AuditTrailPopUp (int caseId) {
		
		MainSql = 
				"select concat(concat(stg_name,' - '), stp_name) as que , q_enterdate, q_action_takenby, q_action, q_branch, q_action_datetime "
				+ " from (select  q_stage ,q_step  , q_enterdate, q_action_takenby, q_action , hist_id, q_branch, q_action_datetime  "
				+ " from p_queue_hist  where q_caseid = "+caseId+" "
				+ " union "
				+ " select  q_stage ,q_step, q_enterdate, q_action_takenby, q_action , 10000000000 as hist_id, q_branch, q_action_datetime  "
				+ " from p_cases where c_id = "+caseId+" ) fafsd  "
				+ " join kbstage on q_stage = stg_code  join kbstep on q_step = stp_code and q_stage = stp_stgcode  "
				+ " order by hist_id asc ";
		userDefinedGridCols.add("q_enterdate");
		userDefinedGridCols.add("que");
		userDefinedGridCols.add("q_action");
		userDefinedGridCols.add("q_branch");
		
		userDefinedGridCols.add("q_action_takenby");
		userDefinedGridCols.add("q_action_datetime");
		
		userDefinedColLabel.put("q_enterdate", "تاريخ و وقت الدخول لهذه المرحله");
		userDefinedColLabel.put("que", "المرحله");
		userDefinedColLabel.put("q_action", "العملية");
		userDefinedColLabel.put("q_action_takenby", "المستخدم الذي قام بالعملية");
		userDefinedColLabel.put("q_action_datetime", "تاريخ العميلية");
		userDefinedColLabel.put("q_branch", "في فرع");
		
		
		userDefinedLookups.put("q_action", "select stpd_code, stpd_desc from kbstep_decision");
		userDefinedLookups.put("q_action_takenby", "select us_id, us_name from kbusers");
		userDefinedLookups.put("q_branch", "select branch_id, branch_name from kbbranches");

		userDefinedCaption = "تسلسل الأحداث";
		UserDefinedPageRows = 500;
	}
}
