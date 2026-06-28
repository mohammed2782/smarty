package com.app.setup;

import smarty.core.CoreMgr;

public class setupSubCust extends CoreMgr{
	public setupSubCust(){
	
		MainSql =" select paddress.* from paddress join kbcustomers on a_cid = c_id where c_id={c_id} ";

		userDefinedCaption = "إعدادت  تفاصيل الزبائن";
		userDefinedNewCaption = "إضافة بيانات تفاصيل الزبائن";
		userDefinedEditCaption = "تعديل بيانات تفاصيل الزبائن";
		
		keyCol = "a_id";
		orderByCols = "a_id";
		mainTable ="paddress";
		
		search_paramval = null;
		canNew = true;
		canFilter = false;
		canEdit = true;
		canDelete = true;
		clickableRow = false;

		userDefinedGridCols.add("a_id");
		userDefinedGridCols.add("a_cid");
		userDefinedGridCols.add("a_sid");
		userDefinedGridCols.add("a_ciid");
		userDefinedGridCols.add("a_pid");
		userDefinedGridCols.add("a_path");
		userDefinedGridCols.add("a_street");
		userDefinedGridCols.add("a_home_num");

		
		userDefinedColLabel.put("a_id", "#");
		userDefinedColLabel.put("a_cid", "رقم الزبون");
	    userDefinedColLabel.put("a_sid", "محافظة");
	    userDefinedColLabel.put("a_ciid", "مدينة");
	    userDefinedColLabel.put("a_pid", "محلة");
	    userDefinedColLabel.put("a_path", "زقاق");
	    userDefinedColLabel.put("a_street", "شارع");
	    userDefinedColLabel.put("a_home_num", "رقم الدار");

	   
		userDefinedNewCols.add("a_cid");
		userDefinedNewCols.add("a_sid");
		userDefinedNewCols.add("a_ciid");
		userDefinedNewCols.add("a_pid");
		userDefinedNewCols.add("a_path");
		userDefinedNewCols.add("a_street");
		userDefinedNewCols.add("a_home_num");
		
		userDefinedReadOnlyNewCols.add("a_cid");
		userDefinedNewColsDefualtValues.put("a_cid",new String[] {"%select c_id , c_name from  kbcustomers where c_id={c_id}"});
		userDefinedLookups.put("a_sid", "select s_id , s_name From kbstate order by s_name asc" );
		userDefinedLookups.put("a_ciid", "select ct_id , ct_name From kbcity order by ct_name asc" );
		userDefinedLookups.put("a_pid", "select p_id , p_name From kbplace order by p_name asc" );

		userDefinedColsMustFill.add("a_sid");
		userDefinedColsMustFill.add("a_ciid");
		userDefinedColsMustFill.add("a_pid");
		userDefinedColsMustFill.add("a_path");
		userDefinedColsMustFill.add("a_street");
		userDefinedColsMustFill.add("a_home_num");

		userDefinedEditCols.add("a_cid");
		userDefinedEditCols.add("a_sid");
		userDefinedEditCols.add("a_ciid");
		userDefinedEditCols.add("a_pid");
		userDefinedEditCols.add("a_path");
		userDefinedEditCols.add("a_street");
		userDefinedEditCols.add("a_home_num");
		
		userDefinedReadOnlyEditCols.add("a_cid");								
		
    }
	
}
