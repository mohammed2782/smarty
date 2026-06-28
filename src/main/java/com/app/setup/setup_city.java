/* class description: used to setup city,
 * created by: lina - smarty framework team member,
 * created date: 22/4/2018 12:45 AM.
 */
package com.app.setup;

import smarty.core.CoreMgr;

public class setup_city extends CoreMgr{
	public setup_city(){
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */
		MainSql =" select kbcity.* from kbcity join kbstate on ct_stid = st_id where st_id={st_id} ";
		mainTable ="kbcity";
		keyCol = "ct_id";

		/*
		 * to define user grid view caption
		 */
		userDefinedCaption = "إعدادت المدينة";
		userDefinedNewCaption = "إضافة بيانات المدينة";
		userDefinedEditCaption = "تعديل بيانات المدينة";
		
		/*
		 * to enable/disable basic operations 
		 */
		search_paramval = null;
		canNew = true;
		canFilter = false;
		canEdit = true;
		canDelete = true;
		clickableRow = false;
		
		/*
		 * to define grid view columns that want to show to user
		 */
		userDefinedGridCols.add("ct_name_ar");
		userDefinedGridCols.add("ct_name_en");
		userDefinedGridCols.add("ct_code");
		userDefinedGridCols.add("ct_active");
		userDefinedGridCols.add("ct_order");
		userDefinedGridCols.add("ct_stid");

		/*
		 * to define grid view label that want to show to user
		 */
		userDefinedColLabel.put("ct_name_ar", "إسم المدينة");
		userDefinedColLabel.put("ct_name_en", "city name");
		userDefinedColLabel.put("ct_code", "كود  المدينة");
		userDefinedColLabel.put("ct_active", "نشطة");
		userDefinedColLabel.put("ct_order", "ترتيب  المدينة");
		userDefinedColLabel.put("ct_stid", "إسم المحافظة التي تتبع لها المدينة");

		/*
		 * to define user must fill columns 
		 */
		userDefinedColsMustFill.add("ct_name_ar");
		userDefinedColsMustFill.add("ct_name_en");
		userDefinedColsMustFill.add("ct_code");
		userDefinedColsMustFill.add("ct_active");

		/*
		 * to define new columns for insert operation
		 */
		userDefinedNewCols.add("ct_name_ar");
		userDefinedNewCols.add("ct_name_en");
		userDefinedNewCols.add("ct_code");
		userDefinedNewCols.add("ct_active");
		userDefinedNewCols.add("ct_order");
		
		/*
		 * to define filter columns for search operation
		 */
		userDefinedFilterCols.add("ct_name_ar");
		userDefinedFilterLookups.put("ct_name_ar", "select ct_name_ar , ct_name_ar from kbcity");
		userDefinedFilterCols.add("ct_name_en");
		userDefinedFilterLookups.put("ct_name_en", "select ct_name_en , ct_name_en from kbcity");
		userDefinedFilterCols.add("ct_code");
		userDefinedFilterLookups.put("ct_code", "select ct_code , ct_code from kbcity");

		/*
		 * to define edit columns for update operation
		 */
		userDefinedEditCols.add("ct_name_ar");
		userDefinedEditCols.add("ct_name_en");
		userDefinedEditCols.add("ct_code");
		userDefinedEditCols.add("ct_active");
		userDefinedEditCols.add("ct_order");
				
		/*
		 * to define lookup columns 
		 */
		userDefinedLookups.put("ct_stid", "select st_id , st_name_ar From kbstate order by st_name_ar asc" );
		userDefinedLookups.put("ct_active", "select 'Y' , 'Yes' from dual union select 'N' , 'No' from dual ");

 }//end of constructor setup_city
}//end of class setup_city


