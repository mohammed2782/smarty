package com.app.reports;

import smarty.core.CoreMgr;

public class RcvStatesDtlsRpt extends CoreMgr{
	public RcvStatesDtlsRpt(){
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */
		MainSql = "SELECT `c_rcv_state`, COUNT(*) AS `value_occurrence` FROM `p_cases` "
				+ "GROUP BY `c_rcv_state` ORDER BY `value_occurrence` DESC";
	
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
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("value_occurrence");

		/*
		 * to define grid view label that want to show to user
		 */
		userDefinedColLabel.put("c_rcv_state", "إسم المحافظة علي حسب أكتر طلب إستلام");
		userDefinedColLabel.put("value_occurrence", "العدد الكلي لطلبيات شحنات الإستلام");

		/*
		 * to define filter columns for search operation
		 */
		userDefinedFilterCols.add("fromDate");
		userDefinedFilterCols.add("toDate");
		userDefinedFilterCols.add("c_rcv_state");

		userDefinedFilterLookups.put("c_rcv_state","select c_rcv_state ,c_rcv_state  From p_cases");

		userDefinedNewColsHtmlType.put("fromDate", "DATE");
		userDefinedNewColsHtmlType.put("toDate", "DATE");
		
		userDefinedColLabel.put("fromDate", "من تاريخ");
		userDefinedColLabel.put("toDate", "الى تاريخ");
		userDefinedColLabel.put("c_rcv_state", "إسم المحافظة علي حسب أكتر طلب إستلام");
		
		userDefinedColsMustFillFilter.add("toDate");
		userDefinedColsMustFillFilter.add("fromDate");		
	
	}//end of no-arg constructor RcvStatesDtlsRpt
}//end of class RcvStatesDtlsRpt
