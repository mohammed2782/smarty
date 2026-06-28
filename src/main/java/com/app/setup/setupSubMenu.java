/* class description: used to setup menu,
 * created by: lina - smarty framework team member,
 * created date: 21/4/2018 6:28 PM.
 */
package com.app.setup;

import smarty.core.CoreMgr;

public class setupSubMenu extends CoreMgr{
	public setupSubMenu(){

		/*
		 * to define main sql statement, key column and main table that do this operations
		 */
		MainSql =" select kbmenu_subtabs.* from kbmenu_subtabs join kbmenu_tabs on sm_menucode = mt_code where mt_id={mt_id} ";
		mainTable = "kbmenu_subtabs";
		keyCol ="sm_id";
		
		/*
		 * to define user gridviews caption
		 */
		userDefinedCaption = "إعدادات القائمة الفرعية";
		userDefinedNewCaption = "إضافة بيانات القائمة الفرعية";
		userDefinedEditCaption = "تعديل بيانات القائمة الفرعية";		
		
		/*
		 * to enable/disable basic operations 
		 */
		search_paramval = null;
		canNew =true;
		canDelete = true;
		canEdit = true;
		clickableRow =false;
		canFilter = false;
		
		/*
		 * to define gridview columns that want to show to user
		 */
		userDefinedGridCols.add("sm_id");
		userDefinedGridCols.add("sm_menucode");
		userDefinedGridCols.add("sm_submenu_name");
		userDefinedGridCols.add("sm_submenucode");
		userDefinedGridCols.add("sm_seq");
		
		/*
		 * to define gridview label that want to show to user
		 */
		userDefinedColLabel.put("sm_id", "رقم القائمة الفرعية");
		userDefinedColLabel.put("sm_menucode", "كود القائمة الرئيسية");
		userDefinedColLabel.put("sm_submenu_name", "إسم القائمة الفرعية");
		userDefinedColLabel.put("sm_submenucode", "كود القائمة الفرعية");
		userDefinedColLabel.put("sm_seq", "تسلسل القائمة الفرعية");		

		/*
		 * to define new columns for insert operation
		 */
		userDefinedNewCols.add("sm_menucode");
		userDefinedNewCols.add("sm_submenu_name");
		userDefinedNewCols.add("sm_submenucode");
		userDefinedNewCols.add("sm_seq");
		userDefinedReadOnlyNewCols.add("sm_menucode");
		userDefinedNewColsDefualtValues.put("sm_menucode",new String[] {"%select mt_code , mt_name from  kbmenu_tabs where mt_id={mt_id}"});		
				
		/*
		 * to define user must fill columns 
		 */
		userDefinedColsMustFill.add("sm_submenu_name");
		userDefinedColsMustFill.add("sm_submenucode");
		userDefinedColsMustFill.add("sm_seq");
		
		/*
		 * to define edit coulmns for update opeartion
		 */	
		userDefinedEditCols.add("sm_menucode");
		userDefinedEditCols.add("sm_submenu_name");
		userDefinedEditCols.add("sm_submenucode");
		userDefinedEditCols.add("sm_seq");	
		userDefinedReadOnlyEditCols.add("sm_menucode");
		
	}//end of constructor setup_sub_menu	
}//end of class setup_sub_menu

