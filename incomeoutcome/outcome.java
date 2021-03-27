package com.app.incomeoutcome;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;

public class outcome extends CoreMgr {
	public outcome(){
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */	
		
			MainSql = "select * from p_outcomes";
			mainTable = "p_outcomes";
			keyCol   = "ou_id";
			
			/*
			 * to define outcomes gridviews caption
			 */
			userDefinedCaption = " المصروفات";
			newCaption = "إضافة  مصروف";
			updCaption = "تعديل بيانات مصروف";
			 
			/*
			 * to enable/disable basic operations 
			 */
			canNew = true;
			canFilter =  true;
			canEdit = true;
			canDelete = true;

			/*
			 * to define gridview columns that want to show to outcome
			 */
			//userDefinedGridCols.add("ou_id");
			userDefinedGridCols.add("ou_item");
			userDefinedGridCols.add("ou_price");
			userDefinedGridCols.add("ou_date");
			//userDefinedGridCols.add("ou_entrydt");
			userDefinedGridCols.add("ou_rmk");
			
			
			/*
			 * to define gridview label that want to show to outcomes
			 */
			userDefinedColLabel.put("ou_id", "رقم المصروف ");
			userDefinedColLabel.put("ou_item", "إسم المصروف ");
			userDefinedColLabel.put("ou_price", "المبلغ");
			userDefinedColLabel.put("ou_date", "تاريخ الدفع");
			//userDefinedColLabel.put("ou_entrydt", "تاريخ الدفع");
			userDefinedColLabel.put("ou_rmk", "ملاحظات");
			

			/*
			 * to define new columns for insert operation
			 */
			userDefinedNewCols.add("ou_item");
			userDefinedNewCols.add("ou_price");
			userDefinedNewCols.add("ou_date");
			//userDefinedNewCols.add("ou_entrydt");
			userDefinedNewCols.add("ou_rmk");
			
			
			userDefinedColsMustFill.add("ou_item");
			userDefinedColsMustFill.add("ou_price");
			userDefinedColsMustFill.add("ou_date");

			
			/*
			 * to define filter columns for search operation
			 */
			userDefinedFilterCols.add("ou_item");
			//userDefinedFilterCols.add("ou_price");
			//userDefinedFilterCols.add("ou_date");
			
			/*
			 * to define edit coulmns for update opeartion
			 */
			userDefinedEditCols.add("ou_item");
			userDefinedEditCols.add("ou_price");
			userDefinedEditCols.add("ou_date");
			userDefinedEditCols.add("ou_rmk");

			userDefinedNewColsHtmlType.put("ou_rmk", "TEXTAREA");

			
			/*
			 * to define lookup coulmns 
			 */
			userDefinedLookups.put("ou_item", "select co_id,co_name From kbcost_type order by co_name desc ");
			
		}//end of constructor outcome
		
				
	}//end of class outcome
