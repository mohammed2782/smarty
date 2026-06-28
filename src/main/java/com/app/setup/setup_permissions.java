/* class description: used to setup permissions,
 * created by: lina - smarty framework team member,
 * created date: 21/4/2018 11:20 PM.
 */
package com.app.setup;

import smarty.core.CoreMgr;

public class setup_permissions extends CoreMgr {
	public setup_permissions(){
		
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */
		MainSql = " select * from kbpermission where p_branchid ={userstorecode} "
				+ " and p_rank_code not in ('ITBOSS', 'DLVAGENT', 'LIAISONAGENT','MASTERCUSTOMER','PAGEADMIN','FOLLOWUP_EMP') ";
		mainTable = "kbpermission";
		keyCol = "p_id";
		
		
		userDefinedFilterCols.add("p_rank_code");
		userDefinedFilterColsHtmlType.put("p_rank_code", "DROPLIST");
		
		/*
		 * to define user gridviews caption
		 */
		userDefinedCaption = "إعدادات صلاحيات المستخدم";
		userDefinedNewCaption = "إضافة بيانات صلاحيات المستخدم";
		userDefinedEditCaption = "تعديل بيانات صلاحيات المستخدم";
		
		/*
		 * to enable/disable basic operations 
		 */
		search_paramval = null;
		canNew = true;
		canFilter = true;
		canEdit = true;
		canDelete = true;
		clickableRow = false;
		
		/*
		 * to define gridview columns that want to show to user
		 */		
		userDefinedGridCols.add("p_rank_code");
		userDefinedGridCols.add("p_menuid");
		userDefinedGridCols.add("p_submenuids");
		
		/*
		 * to define gridview label that want to show to user
		 */
		userDefinedColLabel.put("p_rank_code", "مرتبة المستخدم");
		userDefinedColLabel.put("p_menuid","أسم القائمة");
		userDefinedColLabel.put("p_submenuids", "القوائم الفرعية");
		
		/*
		 * to define new columns for insert operation
		 */
		userDefinedNewCols.add("p_rank_code");
		userDefinedNewCols.add("p_menuid");
		userDefinedNewCols.add("p_submenuids");
		userDefinedNewCols.add("p_branchid");
		userDefinedNewColsHtmlType.put("p_submenuids", "CHECKBOX");
		userDefinedNewColsHtmlType.put("p_menuid", "DROPLIST");
		userDefinedNewLookups.put("p_submenuids", "!select sm_id , "
				+ "sm_submenu_name from kbmenu_subtabs"
				+ " where sm_menucode = '{p_menuid}' and sm_menucode not in ('AdvancedSetup', 'setup') ");
		userDefinedNewLookups.put("p_menuid", "select mt_code, mt_name From kbmenu_tabs where mt_code not in ('AdvancedSetup', 'setup') ");
		
		userDefinedHiddenNewCols.add("p_branchid");
		userDefinedNewColsDefualtValues.put("p_branchid", new String[]{"{userstorecode}"});

		/*
		 * to define user must fill columns 
		 */
		userDefinedColsMustFill.add("p_rank_code");
		userDefinedColsMustFill.add("p_menuid");
		userDefinedColsMustFill.add("p_submenuids");
		
		/*
		 * to define edit columns for update operation
		 */
		userDefinedEditCols.add("p_rank_code");
		userDefinedEditCols.add("p_menuid");
		userDefinedEditCols.add("p_submenuids");
		
		
		/*
		 * to define lookup coulmns 
		 */
		userDefinedLookups.put("p_menuid", "select mt_code, mt_name From kbmenu_tabs ");
		userDefinedLookups.put("p_rank_code", "select rank_code , rank_name_ar from kbrank where rank_code"
				+ "  not in ('ITBOSS', 'DLVAGENT', 'LIAISONAGENT','MASTERCUSTOMER','PAGEADMIN','FOLLOWUP_EMP') ");
		userDefinedLookups.put("p_submenuids", "!select sm_id , sm_submenu_name from kbmenu_subtabs where sm_menucode = '{p_menuid}' ");
		
	}//end of constructor setup_permissions
}//end of class setup_permissions
