package com.app.bussframework;

import smarty.core.CoreMgr;

public class StepsDecisions extends CoreMgr{
	public StepsDecisions() {
		MainSql = "select * from kbstep_decision where stpd_stpid='{stp_id}' and stpd_deleted='N'";
		userDefinedGridCols.add("stpd_desc");
		userDefinedGridCols.add("stpd_code");
		userDefinedGridCols.add("stpd_gotostep1");
		userDefinedGridCols.add("stpd_forrank");
		userDefinedGridCols.add("stpd_createddt"); 
		userDefinedGridCols.add("stpd_createdby");
		userDefinedGridCols.add("stpd_sendnotification");
		userDefinedGridCols.add("stpd_customer_title");
		userDefinedGridCols.add("stpd_customer_body");
		userDefinedEditColsHtmlType.put("stpd_customer_body", "TEXTAREA");
		
		userDefinedNewColsHtmlType.put("stpd_forrank", "CHECKBOX");
		userDefinedEditColsHtmlType.put("stpd_forrank", "CHECKBOX");
		
		userDefinedColLabel.put("stpd_desc", "Decision");
		userDefinedColLabel.put("stpd_code", "Code");
		userDefinedColLabel.put("stpd_forrank", "Rank");
		userDefinedColLabel.put("stpd_createddt", "Created Date");
		userDefinedColLabel.put("stpd_createdby", "Created By");
		userDefinedColLabel.put("stpd_gotostep1", "go to step");
		userDefinedColLabel.put("stpd_sendnotification", "أرسال أشعار للعميل");
		userDefinedColLabel.put("stpd_customer_title", "عنوان الأشعار");
		userDefinedColLabel.put("stpd_customer_body", "محتوى الأشعار");
		
		userDefinedNewCols.add("stpd_stpid");
		userDefinedNewCols.add("stpd_desc");
		userDefinedNewCols.add("stpd_code");
		userDefinedNewCols.add("stpd_gotostep1");
		userDefinedNewCols.add("stpd_forrank");
		
		userDefinedColsMustFill.add("stpd_desc");
		userDefinedColsMustFill.add("stpd_code");
		userDefinedColsMustFill.add("stpd_gotostep1");
		
		userDefinedNewColsHtmlType.put("stpd_gotostep1", "DROPLIST");
		
		userDefinedReadOnlyNewCols.add("stpd_stpid");
		userDefinedNewColsDefualtValues.put("stpd_stpid",new String[] {"{stp_id}"});
		
		userDefinedLookups.put("stpd_gotostep1", "select stp_id, stp_name from kbstep");
		
		userDefinedEditCols.add("stpd_desc");
		userDefinedEditCols.add("stpd_code");
		userDefinedEditCols.add("stpd_gotostep1");
		userDefinedEditCols.add("stpd_forrank");
		userDefinedEditCols.add("stpd_sendnotification");
		userDefinedEditCols.add("stpd_customer_title");
		userDefinedEditCols.add("stpd_customer_body");
		userDefinedLookups.put("stpd_forrank", "select rank_code, rank_name_en from kbrank");
		
		userDefinedLookups.put("stpd_sendnotification", "select kbcode, kbdesc from kbgeneral where kbcat1='YESNO' ");
		canNew = true;
		canDelete = true;
		canEdit = true;
		
		mainTable = "kbstep_decision";
		keyCol = "stpd_id";
		
	}
}
