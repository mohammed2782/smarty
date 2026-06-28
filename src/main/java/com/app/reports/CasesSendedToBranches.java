package com.app.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class CasesSendedToBranches extends CoreMgr{
	
	public CasesSendedToBranches(){
		
		MainSql = "select cc_frombranch, cc_tobranch,'' as fromDate, '' as toDate, '' as dummy, "
				+ "sum(case when q_stage = 'DLV' and c_alllowagentpay = 'Y' and c_allowcustpay = 'Y' and c_settled = 'FULL' then 1 else 0 end ) as Dleiverd,  "
				+ "concat(round(( (sum(case when q_stage = 'DLV' and c_alllowagentpay = 'Y' and c_allowcustpay = 'Y' and c_settled = 'FULL' then 1 else 0 end ))/count(*) *100),2),'%') as Dleiverd2,  " 
				+ "sum(case when q_stage = 'CNCL' and q_step = 'RTN_INSTORE' and c_allowrtnagent = 'Y' and c_agentrtnid != 0 then 1 else 0 end ) as Canceled,  " 
				+ "concat(round(( (sum(case when q_stage = 'CNCL' and q_step = 'RTN_INSTORE' and c_allowrtnagent = 'Y' and c_agentrtnid != 0 then 1 else 0 end ))/count(*) *100),2),'%')  as Canceled2,  " 
				+ "sum(case when (q_stage = 'DLV' and c_settled != 'FULL')  and  (q_stage != 'CNCL' and q_step != 'RTN_INSTORE') then 1 else 0 end ) as Underprocess,  " 
				+ "concat(round(( (sum(case when (q_stage = 'DLV' and c_settled != 'FULL')  and  (q_stage != 'CNCL' and q_step != 'RTN_INSTORE') then 1 else 0 end ))/count(*) *100),2),'%') as Underprocess2,  " 
				+ "count(*) as ttl  "
				+ "from p_caseschain  "
				+ "join p_cases on cc_caseid = c_id  "
				+ "join kbbranches on c_branchcode = branch_id   "
				+ "where 0=1 "
				+ "group by cc_frombranch, cc_tobranch ";
		
		
		canFilter = true;

		userDefinedCaption = "الطلبات المرسلة الى الفروع";
		UserDefinedPageRows = 1000;
		
		userDefinedGroupColsOrderBy = "cc_frombranch";
		userDefinedGroupSortMode = "ASC";
		userDefinedGroupByCol = "dummy";
		userDefinedSumCols.add("ttl");
		userDefinedSumCols.add("Dleiverd");
		userDefinedSumCols.add("Canceled");
		userDefinedSumCols.add("Underprocess");
		groupSumCaption = "المجموع";
		
		userDefinedGridCols.add("cc_frombranch");
		userDefinedGridCols.add("cc_tobranch");
		userDefinedGridCols.add("ttl");
		userDefinedGridCols.add("Dleiverd");
		userDefinedGridCols.add("Dleiverd2");
		userDefinedGridCols.add("Canceled");
		userDefinedGridCols.add("Canceled2");
		userDefinedGridCols.add("Underprocess");
		userDefinedGridCols.add("Underprocess2");

	
		userDefinedColLabel.put("cc_frombranch" , "من فرع");
		userDefinedColLabel.put("cc_tobranch" , "الى فرع");
		userDefinedColLabel.put("fromDate" , "من تاريخ");
		userDefinedColLabel.put("toDate" , "إلي تاريخ");
		userDefinedColLabel.put("ttl" , "عدد الطلبات المرسلة");
		userDefinedColLabel.put("Dleiverd", "الواصل");
		userDefinedColLabel.put("Dleiverd2", "بالنسبة %");
		userDefinedColLabel.put("Canceled" , "الراجع");
		userDefinedColLabel.put("Canceled2" , "بالنسبة %");
		userDefinedColLabel.put("Underprocess" , "قيد المعالجة");
		userDefinedColLabel.put("Underprocess2" , "بالنسبة %");
		
		
		userDefinedFilterCols.add("cc_frombranch");
		userDefinedFilterCols.add("cc_tobranch");	
		userDefinedFilterCols.add("fromDate");
		userDefinedFilterCols.add("toDate");
		userDefinedFilterColsHtmlType.put("cc_frombranch", "DROPLIST");
		userDefinedFilterColsHtmlType.put("cc_tobranch", "DROPLIST");
		userDefinedFilterColsHtmlType.put("fromDate", "DATE");
		userDefinedFilterColsHtmlType.put("toDate", "DATE");

	
		userDefinedLookups.put("cc_frombranch", "select branch_id, branch_name from kbbranches ");
		userDefinedLookups.put("cc_tobranch", "select branch_id, branch_name from kbbranches ");

		
	}// end of no-args constructor CasesSendedToBranches

	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search
		
		String fromExpDt = "", toExpDt = "", frombranch = "", tobranch = "";
		boolean foundDate = false, foundfrombranch = false, foundtobranch = false;
		
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null)
						&& (!value.equals(""))) {
					if (parameter.equals("fromDate")) {
						fromExpDt = " cc_createddt >= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						foundDate = true;
					} else if (parameter.equals("toDate")) {
						toExpDt = " cc_createddt <= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
					}
					else if (parameter.equals("cc_frombranch")) {
						frombranch = " cc_frombranch = " + value + " ";
						foundfrombranch = true;
					}
					else if (parameter.equals("cc_tobranch")) {
						tobranch = " cc_tobranch = " + value + " ";
						foundtobranch = true;
					}
				}
			}

		}
		// the next statement is important so we can skip the generating of
		// where clause from search_paramval
		//userDefinedWhere = " having 1=1 "; 
		
		MainSql = "select cc_frombranch, cc_tobranch,'' as fromDate, '' as toDate, '' as dummy, "
				+ "sum(case when q_stage = 'DLV' and c_alllowagentpay = 'Y' and c_allowcustpay = 'Y' and c_settled = 'FULL' then 1 else 0 end ) as Dleiverd,  "
				+ "concat(round(( (sum(case when q_stage = 'DLV' and c_alllowagentpay = 'Y' and c_allowcustpay = 'Y' and c_settled = 'FULL' then 1 else 0 end ))/count(*) *100),2),'%') as Dleiverd2,  " 
				+ "sum(case when q_stage = 'CNCL' and q_step = 'RTN_INSTORE' and c_allowrtnagent = 'Y' and c_agentrtnid != 0 then 1 else 0 end ) as Canceled,  " 
				+ "concat(round(( (sum(case when q_stage = 'CNCL' and q_step = 'RTN_INSTORE' and c_allowrtnagent = 'Y' and c_agentrtnid != 0 then 1 else 0 end ))/count(*) *100),2),'%')  as Canceled2,  " 
				+ "sum(case when (q_stage = 'DLV' and c_settled != 'FULL')  and  (q_stage != 'CNCL' and q_step != 'RTN_INSTORE') then 1 else 0 end ) as Underprocess,  " 
				+ "concat(round(( (sum(case when (q_stage = 'DLV' and c_settled != 'FULL')  and  (q_stage != 'CNCL' and q_step != 'RTN_INSTORE') then 1 else 0 end ))/count(*) *100),2),'%') as Underprocess2,  " 
				+ "count(*) as ttl  "
				+ "from p_caseschain  "
				+ "join p_cases on cc_caseid = c_id  "
				+ "join kbbranches on c_branchcode = branch_id  ";
				
	
		//Test them one by one if one is true
		if (foundDate & foundfrombranch & foundtobranch) { //Then test if 3 of filter are true
			MainSql += "where " + fromExpDt + " and " + toExpDt +  " and " + frombranch + " and " + tobranch + " "
					+ "group by cc_frombranch, cc_tobranch ";
		}else if (foundDate & foundfrombranch) { //Then test if 2 of filter are true except foundtobranch
			MainSql += "where " + fromExpDt + " and " + toExpDt +  " and " + frombranch + " "
					+ "group by cc_frombranch, cc_tobranch ";
		}else if (foundDate  & foundtobranch) { //Then test if 2 of filter are true except foundfrombranch
			MainSql += "where " + fromExpDt + " and " + toExpDt +  " and " + tobranch + " "
					+ "group by cc_frombranch, cc_tobranch ";
		}else if (foundfrombranch & foundtobranch) { //Then test if 2 of filter are true except foundDate
			MainSql += "where " + frombranch + " and " + tobranch + " "
					+ "group by cc_frombranch, cc_tobranch ";
		}else if (foundDate) {
			MainSql += "where " + fromExpDt + " and " + toExpDt +  " "
					+ "group by cc_frombranch, cc_tobranch ";
		}else if (foundfrombranch) {
			MainSql += "where " + frombranch + " "
					+ "group by cc_frombranch, cc_tobranch ";
		}else if (foundtobranch) {
			MainSql += "where " + tobranch + " "
					+ "group by cc_frombranch, cc_tobranch ";
		}else {
			MainSql += "group by cc_frombranch, cc_tobranch ";
		}
	}//end of method initialize
	
	@Override
	public StringBuilder genListing(){
		search_paramval.remove("fromDate");
		search_paramval.remove("toDate");
		search_paramval.remove("cc_frombranch");
		search_paramval.remove("cc_tobranch");
		return super.genListing();
	}//end of method genListing
	
}// end of class CasesSendedToBranches
