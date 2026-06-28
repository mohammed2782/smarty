/* class description: used to setup sub customers informations,
 * created by: lina - smarty framework team member,
 * created date: 22/4/2018 7:44 PM.
 */
package com.app.setup;

import smarty.core.CoreMgr;

public class setup_sub_customers extends CoreMgr{
	public setup_sub_customers(){		
			
		
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */
		MainSql =" select p_address.* from p_address join kbcustomers on a_cid = c_id where c_id={c_id} ";
		mainTable ="p_address";
		keyCol = "a_id";
		orderByCols = "a_id";

		/*
		 * to define user grid view caption
		 */
		userDefinedCaption = "إعدادات تفاصيل الزبائن - تفاصيل عنوان الزبون";
		userDefinedNewCaption = "إضافة بيانات تفاصيل الزبائن - تفاصيل عنوان الزبون";
		userDefinedEditCaption = "تعديل بيانات تفاصيل الزبائن - تفاصيل عنوان الزبون";
		
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
		userDefinedGridCols.add("a_cid");
		userDefinedGridCols.add("a_stid");
		userDefinedGridCols.add("a_ctid");
		userDefinedGridCols.add("a_place");
		userDefinedGridCols.add("a_path");
		userDefinedGridCols.add("a_street");
		userDefinedGridCols.add("a_home_num");

		/*
		 * to define grid view label that want to show to user
		 */
		userDefinedColLabel.put("a_cid", "رقم الزبون");
	    userDefinedColLabel.put("a_stid", "محافظة");
	    userDefinedColLabel.put("a_ctid", "مدينة");
	    userDefinedColLabel.put("a_place", "محلة");
	    userDefinedColLabel.put("a_path", "زقاق");
	    userDefinedColLabel.put("a_street", "شارع");
	    userDefinedColLabel.put("a_home_num", "رقم الدار");

	    /*
		 * to define user must fill columns 
		 */
		userDefinedColsMustFill.add("a_sid");
		userDefinedColsMustFill.add("a_ciid");
		userDefinedColsMustFill.add("a_place");
		userDefinedColsMustFill.add("a_path");
		userDefinedColsMustFill.add("a_street");
		userDefinedColsMustFill.add("a_home_num");
		
	    /*
		 * to define new columns for insert operation
		 */
		userDefinedNewCols.add("a_cid");
		userDefinedReadOnlyNewCols.add("a_cid");
		userDefinedNewColsDefualtValues.put("a_cid",new String[] {"%select c_id , c_name from  kbcustomers where c_id={c_id}"});
		userDefinedNewCols.add("a_stid");
		userDefinedNewCols.add("a_ctid");
		userDefinedNewCols.add("a_place");
		userDefinedNewCols.add("a_path");
		userDefinedNewCols.add("a_street");
		userDefinedNewCols.add("a_home_num");	
		
		/*
		 * to define edit columns for update operation
		 */
		userDefinedEditCols.add("a_cid");
		userDefinedReadOnlyEditCols.add("a_cid");								
		userDefinedEditCols.add("a_stid");
		userDefinedEditCols.add("a_ctid");
		userDefinedEditCols.add("a_place");
		userDefinedEditCols.add("a_path");
		userDefinedEditCols.add("a_street");
		userDefinedEditCols.add("a_home_num");
		
		/*
		 * to define lookup columns 
		 */
		userDefinedLookups.put("a_stid", "select st_id , st_name_ar From kbstate order by st_name_ar asc" );
		userDefinedLookups.put("a_ctid", "select ct_id , ct_name_ar From kbcity order by ct_name_ar asc" );

    }//end of constructor setup_sub_customers
}//end of class setup_sub_customers
