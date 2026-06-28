package com.app.setup;

import smarty.core.CoreMgr;

public class whatsapp_support extends CoreMgr{
	public whatsapp_support(){
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */	
		
			MainSql = "select * from kb_whatsapp_support where ws_branch = {userstorecode}";
			mainTable = "kb_whatsapp_support";
			keyCol="ws_id";
			userDefinedCaption = "واتساب الدعم الفني";
			userDefinedNewCaption = "اضافة رقم واتساب الدعم الفني";
			userDefinedEditCaption = "تعديل رقم واتساب الدعم الفني";
			 
			canNew = true;
			canFilter =  true;
			canEdit = true;
			canDelete = true;

			userDefinedGridCols.add("ws_name");
			userDefinedGridCols.add("ws_phone");
			userDefinedGridCols.add("ws_complain_phone");
			userDefinedGridCols.add("ws_state");

			userDefinedColsMustFill.add("ws_name");
			userDefinedColsMustFill.add("ws_phone");
			userDefinedColsMustFill.add("ws_complain_phone");
			userDefinedColsMustFill.add("ws_state");

			userDefinedColLabel.put("ws_name", "الاسم");
			userDefinedColLabel.put("ws_phone", "رقم هاتف الواتساب");
			userDefinedColLabel.put("ws_complain_phone", "رقم هاتف الشكاوي");
			userDefinedColLabel.put("ws_state", "المحافظة");
			userDefinedColLabel.put("ws_branch", "الفرغ");
			
			userDefinedNewCols.add("ws_name");
			userDefinedNewCols.add("ws_phone");
			userDefinedNewCols.add("ws_complain_phone");
			userDefinedNewCols.add("ws_branch");
			userDefinedNewColsDefualtValues.put("ws_branch", new String[] {"{userstorecode}"});
			userDefinedNewCols.add("ws_state");
	        userDefinedReadOnlyNewCols.add("ws_branch");

			userDefinedFilterCols.add("ws_state");

			userDefinedEditCols.add("ws_name");
			userDefinedEditCols.add("ws_phone");
			userDefinedEditCols.add("ws_complain_phone");
			userDefinedEditCols.add("ws_branch");
			userDefinedNewColsDefualtValues.put("ws_branch", new String[] {"{userstorecode}"});
			userDefinedEditCols.add("ws_state");
	        userDefinedReadOnlyEditCols.add("ws_branch");

			
			userDefinedNewColsHtmlType.put("ws_branch", "DROPLIST");
			userDefinedEditColsHtmlType.put("ws_branch", "DROPLIST");

			userDefinedNewColsHtmlType.put("ws_state", "DROPLIST");
			userDefinedEditColsHtmlType.put("ws_state", "DROPLIST");
			
			userDefinedLookups.put("ws_branch", "select branch_id, branch_name from kbbranches  ");
			userDefinedLookups.put("ws_state", "select st_code,st_name_ar  from kbstate where st_branch =  {userstorecode} ");
			
			
			
		}//end of constructor setup_item
}//end of class setup_item
