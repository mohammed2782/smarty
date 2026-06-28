package com.app.reports;

import smarty.core.CoreMgr;

public class ShipmentsDtlsRpt extends CoreMgr{

	public ShipmentsDtlsRpt(){
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */
		//userDefinedWhere = " and 1=0";
        // where 1=0 group by kbcustomers.c_id
		
		MainSql = "select c_name , count(distinct q_caseid) as ttlNumOfCases, (select count(distinct q_id) "
				+ "from p_queue where q_code ='delevired__final_stage' and q_caseid = p_cases.c_id "
				+ "and c_cmid = cm_id and cm_custid = kbcustomers.c_id) as ttlNumOfSuccessCases, "
				+ "(select count(distinct q_id) from p_queue where q_code ='in_store__store' and q_caseid = p_cases.c_id "
				+ "and c_cmid = cm_id and cm_custid = kbcustomers.c_id) as ttlNumOfPendCases , "
				+ "(select count(distinct q_id) from p_queue where q_code ='delv_back_to_shipper__cncl' "
				+ "and q_caseid = p_cases.c_id and c_cmid = cm_id and cm_custid = kbcustomers.c_id) as ttlNumOfBackCases "
				+ "FROM p_queue inner join p_cases on q_caseid = p_cases.c_id inner join p_casesmaster on c_cmid = cm_id "
				+ "inner join kbcustomers on cm_custid = kbcustomers.c_id group by kbcustomers.c_id";	
		/*
		 * to define user grid views caption
		 */
		userDefinedCaption = "تفاصيل الشحنات لكل زبون";
		
		/*
		 * to enable/disable basic operations 
		 */
		canFilter = true;
		
		/*
		 * to define grid view columns that want to show to user
		 */
		userDefinedGridCols.add("c_name");
		userDefinedGridCols.add("ttlNumOfCases");
		userDefinedGridCols.add("ttlNumOfSuccessCases");
		userDefinedGridCols.add("ttlNumOfPendCases");
		userDefinedGridCols.add("ttlNumOfBackCases");

		/*
		 * to define grid view label that want to show to user
		 */
		userDefinedColLabel.put("c_name", "إسم الزبون");
		userDefinedColLabel.put("ttlNumOfCases", "العدد الكلي للشحنات");
		userDefinedColLabel.put("ttlNumOfSuccessCases", "عدد الشحنات الناجحة");
		userDefinedColLabel.put("ttlNumOfPendCases", "عدد الشحنات المعلقة");
		userDefinedColLabel.put("ttlNumOfBackCases", "عدد الشحنات المرجعة");

		/*
		 * to define filter columns for search operation
		 */
		userDefinedFilterCols.add("fromDate");
		userDefinedFilterCols.add("toDate");
		userDefinedFilterCols.add("c_name");

		userDefinedFilterLookups.put("c_name","select c_name ,c_name  From kbcustomers");

		userDefinedNewColsHtmlType.put("fromDate", "DATE");
		userDefinedNewColsHtmlType.put("toDate", "DATE");
		
		userDefinedColLabel.put("fromDate", "من تاريخ");
		userDefinedColLabel.put("toDate", "الى تاريخ");
		userDefinedColLabel.put("us_loginid", "إسم الزبون");
		
		userDefinedColsMustFillFilter.add("toDate");
		userDefinedColsMustFillFilter.add("fromDate");
		
	}//end of no-arg constructor ShipmentsDtlsRpt
}//end of class ShipmentsDtlsRpt
