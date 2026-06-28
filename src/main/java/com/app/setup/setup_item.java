package com.app.setup;

import smarty.core.CoreMgr;

public class setup_item extends CoreMgr{
	public setup_item(){
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */	
		
			MainSql = "select * from kbcost_type";
			mainTable = "kbcost_type";
			keyCol   = "co_id";
			
			/*
			 * to define item gridviews caption
			 */
			userDefinedCaption = "إعدادات انواع المصروفات";
			userDefinedNewCaption = "إضافة بيانات نوع المصروف";
			userDefinedEditCaption = "تعديل بيانات نوع المصروف";
			 
			/*
			 * to enable/disable basic operations 
			 */
			canNew = true;
			canFilter =  true;
			canEdit = true;
			canDelete = true;

			/*
			 * to define gridview columns that want to show to item
			 */
			userDefinedGridCols.add("co_id");
			userDefinedGridCols.add("co_name");
			
			userDefinedColsMustFill.add("co_name");

			
			/*
			 * to define gridview label that want to show to item
			 */
			userDefinedColLabel.put("co_id", "رقم نوع المصروف ");
			userDefinedColLabel.put("co_name", "إسم نوع المصروف");
		

			/*
			 * to define new columns for insert operation
			 */
			userDefinedNewCols.add("co_name");
			
			
			/*
			 * to define filter columns for search operation
			 */
			userDefinedFilterCols.add("co_name");
			
			/*
			 * to define edit coulmns for update opeartion
			 */
			userDefinedEditCols.add("co_name");
			
			
		}//end of constructor setup_item
}//end of class setup_item
