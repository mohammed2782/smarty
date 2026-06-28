package com.app.cases;

import smarty.core.CoreMgr;

public class DisplaySingleCaseMoreInfo extends CoreMgr{
	public DisplaySingleCaseMoreInfo(int caseId) {
		
		MainSql = "select * from log_changes where log_table = 'P_CASES' and log_keycolname = 'c_id' and log_keycolid = "+caseId;
		
		userDefinedLookups.put("log_actionby", "select us_id, us_name from kbusers order by us_name");
		
		userDefinedGridCols.add("log_id");
		userDefinedGridCols.add("log_table");
		userDefinedGridCols.add("log_keycolname");
		userDefinedGridCols.add("log_keycolid");
		userDefinedGridCols.add("log_colnamechanged");
		userDefinedGridCols.add("log_old_value");
		userDefinedGridCols.add("log_new_value");
		userDefinedGridCols.add("log_actioname");
		userDefinedGridCols.add("log_screenname");
		userDefinedGridCols.add("log_actionby");
		userDefinedGridCols.add("log_action_timestamp");
		
		userDefinedColLabel.put("fromdt", "بتاريخ");
		userDefinedColLabel.put("todt", "الى تاريخ");
		userDefinedColLabel.put("log_actionby", "اسم المستخدم");
		userDefinedColLabel.put("log_action_timestamp", "الوقت والتاريخ");
		userDefinedColLabel.put("log_screenname", "اسم النافذة");
		userDefinedColLabel.put("log_actioname", "نوع التعديل");
		userDefinedColLabel.put("log_old_value", "القيمة قبل التعديل");
		userDefinedColLabel.put("log_new_value", "القيمة بعد التعديل");
		userDefinedColLabel.put("log_colnamechanged", "العامود المعدل عليه");
		userDefinedColLabel.put("log_keycolid", "KeyColId");
		userDefinedColLabel.put("log_keycolname", "KeyColName");
		userDefinedColLabel.put("log_table", "اسم الجدول");
		userDefinedColLabel.put("log_id", "LogId");
		
		userDefinedCaption = "تتبع التغييرات";
		
		userDefinedLookups.put("log_actioname", "select trans_engdesc, trans_arabdesc from kbtranslator where trans_section = 'log_actioname'");
		userDefinedLookups.put("log_table", "select trans_engdesc, trans_arabdesc from kbtranslator where trans_section = 'log_table'");
		userDefinedLookups.put("log_keycolname", "select trans_engdesc, trans_arabdesc from kbtranslator where trans_section = 'log_keycolname'");
		
		
	}

}
