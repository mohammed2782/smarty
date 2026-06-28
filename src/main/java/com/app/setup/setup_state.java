/* class description: used to setup state,
 * created by: lina - smarty framework team member,
 * created date: 22/4/2018 12:11 AM.
 */
package com.app.setup;

import smarty.core.CoreMgr;

public class setup_state extends CoreMgr {
	public setup_state(){
	
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */
		
		MainSql ="select * from kbstate where st_branch= {userstorecode} order by st_order";
		mainTable ="kbstate";
		keyCol = "st_id";
			
		/*
		 * to define user grid views caption
		 */
		userDefinedCaption = "إعدادت المحافظة";
		userDefinedNewCaption = "إضافة بيانات المحافظة";
		userDefinedEditCaption = "تعديل بيانات المحافظة";
		
		/*
		 * to enable/disable basic operations 
		 */
		search_paramval = null;
		canNew = false;
		canFilter = false;
		canEdit = true;
		canDelete = false;
		clickableRow = false;
		
		/*
		 * to define grid view columns that want to show to user
		 */
		userDefinedGridCols.add("st_name_ar");
		userDefinedGridCols.add("st_name_en");
		userDefinedGridCols.add("st_code");
		userDefinedGridCols.add("st_active");
		userDefinedGridCols.add("st_order");
		userDefinedGridCols.add("st_charges");
		userDefinedGridCols.add("st_ruralcharges");
		userDefinedGridCols.add("st_agent_share");
		userDefinedGridCols.add("st_agent_share_rural");
		
		
		
		/*
		 * to define grid view label that want to show to user
		 */
		userDefinedColLabel.put("st_name_ar", "إسم المحافظة");
		userDefinedColLabel.put("st_name_en", "state name");
		userDefinedColLabel.put("st_code", "كود  المحافظة");
		userDefinedColLabel.put("st_active", "نشطة");
		userDefinedColLabel.put("st_order", "ترتيب  المحافظة");
		userDefinedColLabel.put("st_charges", "مبلغ الشحن");
		userDefinedColLabel.put("st_ruralcharges", "مبلغ الشحن  للأقضيه");
		userDefinedColLabel.put("st_agent_share","أجرة المندوب");
		userDefinedColLabel.put("st_agent_share_rural","أجرة  المندوب للأقضيه");

		/*
		 * to define user must fill columns 
		 */
		userDefinedColsMustFill.add("st_name_ar");
		userDefinedColsMustFill.add("st_name_en");
		userDefinedColsMustFill.add("st_code");
		userDefinedColsMustFill.add("st_active");
		userDefinedColsMustFill.add("st_charges");
		userDefinedColsMustFill.add("st_ruralcharges");
		userDefinedColsMustFill.add("st_agent_share");
		userDefinedColsMustFill.add("st_agent_share_rural");
		
		/*
		 * to define new columns for insert operation
		 */
		userDefinedNewCols.add("st_name_ar");
		userDefinedNewCols.add("st_name_en");
		userDefinedNewCols.add("st_code");
		userDefinedNewCols.add("st_active");
		userDefinedNewCols.add("st_order");
		userDefinedNewCols.add("st_charges");
		userDefinedNewCols.add("st_ruralcharges");
		userDefinedNewCols.add("st_agent_share");
		userDefinedNewCols.add("st_agent_share_rural");

		/*
		 * to define filter columns for search operation
		 */
		userDefinedFilterCols.add("st_name_ar");
		userDefinedFilterLookups.put("st_name_ar", "select st_name_ar , st_name_ar from kbstate");
		userDefinedFilterCols.add("st_name_en");
		userDefinedFilterLookups.put("st_name_en", "select st_name_en , st_name_en from kbstate");
		userDefinedFilterCols.add("st_code");
		userDefinedFilterLookups.put("st_code", "select st_code , st_code from kbstate");
		
		/*
		 * to define edit columns for update operation
		 */
		userDefinedEditCols.add("st_name_ar");
		userDefinedEditCols.add("st_name_en");
		userDefinedEditCols.add("st_code");
		userDefinedEditCols.add("st_active");
		userDefinedEditCols.add("st_order");
		userDefinedEditCols.add("st_charges");
		userDefinedEditCols.add("st_ruralcharges");
		userDefinedEditCols.add("st_agent_share");
		userDefinedEditCols.add("st_agent_share_rural");

		/*
		 * to define lookup columns 
		 */
		userDefinedLookups.put("st_active", "select 'Y' , 'Yes' from dual union select 'N' , 'No' from dual ");

		/*
		 * to pop up sub menu (city) for main menu (state)
		 */
		//userDefinedGlobalClickRowID="st_id";
		
 }//end of constructor setup_state
}//end of class setup_state
