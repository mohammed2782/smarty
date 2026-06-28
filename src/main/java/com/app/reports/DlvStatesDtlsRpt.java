package com.app.reports;

import smarty.core.CoreMgr;

public class DlvStatesDtlsRpt extends CoreMgr{

	public DlvStatesDtlsRpt(){
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */
		MainSql = "SELECT cm_pickup_state, COUNT(*) AS value_occurrence FROM p_casesmaster "
				+ "GROUP BY cm_pickup_state ORDER BY value_occurrence DESC";
	
		/*
		 * to define user grid views caption
		 */
		userDefinedCaption = "تفاصيل الشحنات لكل محافظة";
		
		/*
		 * to enable/disable basic operations 
		 */
		canFilter = true;
		
		/*
		 * to define grid view columns that want to show to user
		 */
		userDefinedGridCols.add("cm_pickup_state");
		userDefinedGridCols.add("value_occurrence");

		/*
		 * to define grid view label that want to show to user
		 */
		userDefinedColLabel.put("cm_pickup_state", "إسم المحافظة علي حسب أكتر طلب توصيل");
		userDefinedColLabel.put("value_occurrence", "العدد الكلي لطلبيات شحنات التوصيل");

		/*
		 * to define filter columns for search operation
		 */
		userDefinedFilterCols.add("fromDate");
		userDefinedFilterCols.add("toDate");
		userDefinedFilterCols.add("cm_pickup_state");

		userDefinedFilterLookups.put("cm_pickup_state","select cm_pickup_state ,cm_pickup_state  From p_casesmaster");

		userDefinedNewColsHtmlType.put("fromDate", "DATE");
		userDefinedNewColsHtmlType.put("toDate", "DATE");
		
		userDefinedColLabel.put("fromDate", "من تاريخ");
		userDefinedColLabel.put("toDate", "الى تاريخ");
		userDefinedColLabel.put("cm_pickup_state", "إسم المحافظة علي حسب أكتر طلب توصيل");
		
		userDefinedColsMustFillFilter.add("toDate");
		userDefinedColsMustFillFilter.add("fromDate");		
	
	}//end of no-arg constructor DlvStatesDtlsRpt
}//end of class DlvStatesDtlsRpt
