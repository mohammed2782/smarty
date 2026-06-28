package com.app.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class ResendCases extends CoreMgr{

	public ResendCases(){
		
		MainSql = "select c_branchcode, branch_name, q.q_action_takenby, ifnull(us_name,'الموظف لم يعد موجود')as us_name, '' as fromDate, '' as toDate, '' as dummy, "
				+ "sum(case when q_stage = 'DLV' and c_alllowagentpay = 'Y' and c_allowcustpay = 'Y' and c_settled = 'FULL' then 1 else 0 end ) as Dleiverd, "
				+ "concat(round(( (sum(case when q_stage = 'DLV' and c_alllowagentpay = 'Y' and c_allowcustpay = 'Y' and c_settled = 'FULL' then 1 else 0 end ))/count(*) *100),2),'%') as Dleiverd2,  "
				+ "sum(case when q_stage = 'CNCL' and q_step = 'RTN_INSTORE' and c_allowrtnagent = 'Y' and c_agentrtnid != 0 then 1 else 0 end ) as Canceled,  "
				+ "concat(round(( (sum(case when q_stage = 'CNCL' and q_step = 'RTN_INSTORE' and c_allowrtnagent = 'Y' and c_agentrtnid != 0 then 1 else 0 end ))/count(*) *100),2),'%')  as Canceled2,  "
				+ "sum(case when (q_stage = 'DLV' and c_settled != 'FULL') and  (q_stage != 'CNCL' and q_step != 'RTN_INSTORE') then 1 else 0 end ) as Underprocess,  "
				+ "concat(round(( (sum(case when (q_stage = 'DLV' and c_settled != 'FULL') and  (q_stage != 'CNCL' and q_step != 'RTN_INSTORE') then 1 else 0 end ))/count(*) *100),2),'%') as Underprocess2,  "
				+ "(sum(case when q_stage = 'DLV' and c_alllowagentpay = 'Y' and c_allowcustpay = 'Y' and c_settled = 'FULL' then 1 else 0 end ) +  "
				+ "sum(case when q_stage = 'CNCL' and q_step = 'RTN_INSTORE' and c_allowrtnagent = 'Y' and c_agentrtnid != 0  then 1 else 0 end ) +  "
				+ "sum(case when (q_stage = 'DLV' and c_settled != 'FULL') and  (q_stage != 'CNCL' and q_step != 'RTN_INSTORE') then 1 else 0 end )) as ttl  "
				+ "from p_cases c  "
				+ "join kbbranches on c.c_branchcode = branch_id  "
				+ "join (  "
				+ "select q_caseid, q_enterdate, q_action_takenby, us_name  "
				+ "from p_queue_hist  "
				+ "left join kbusers on (q_action_takenby = us_id)  "
				+ "where  q_action = 'RESEND') q on c.c_id = q.q_caseid  "
				+ "where 0=1 "
				+ "group by c_branchcode, q.q_action_takenby  ";


		canFilter = true;

		userDefinedCaption = "عدد الطلبات المعاد إرسالها";
		UserDefinedPageRows = 1000;
		
		userDefinedGroupColsOrderBy = "q_action_takenby";
		userDefinedGroupSortMode = "ASC";
		userDefinedGroupByCol = "dummy";
		userDefinedSumCols.add("ttl");
		userDefinedSumCols.add("Dleiverd");
		userDefinedSumCols.add("Canceled");
		userDefinedSumCols.add("Underprocess");
		groupSumCaption = "المجموع";
		
		userDefinedGridCols.add("us_name");
		userDefinedGridCols.add("branch_name");
		userDefinedGridCols.add("ttl");
		userDefinedGridCols.add("Dleiverd");
		userDefinedGridCols.add("Dleiverd2");
		userDefinedGridCols.add("Canceled");
		userDefinedGridCols.add("Canceled2");
		userDefinedGridCols.add("Underprocess");
		userDefinedGridCols.add("Underprocess2");

	

		userDefinedColLabel.put("q_action_takenby" , "الموظف");
		userDefinedColLabel.put("us_name" , "الموظف");
		userDefinedColLabel.put("c_branchcode" , "الفرع");
		userDefinedColLabel.put("branch_name" , "الفرع");
		userDefinedColLabel.put("fromDate" , "من تاريخ");
		userDefinedColLabel.put("toDate" , "إلي تاريخ");
		userDefinedColLabel.put("ttl" , "عدد الطلبات");
		userDefinedColLabel.put("Dleiverd", "الواصل");
		userDefinedColLabel.put("Dleiverd2", "بالنسبة %");
		userDefinedColLabel.put("Canceled" , "الراجع");
		userDefinedColLabel.put("Canceled2" , "بالنسبة %");
		userDefinedColLabel.put("Underprocess" , "قيد المعالجة");
		userDefinedColLabel.put("Underprocess2" , "بالنسبة %");
		
		
		userDefinedFilterCols.add("q_action_takenby");
		userDefinedFilterCols.add("c_branchcode");	
		userDefinedFilterCols.add("fromDate");
		userDefinedFilterCols.add("toDate");
		userDefinedFilterColsHtmlType.put("q_action_takenby", "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_branchcode", "DROPLIST");
		userDefinedFilterColsHtmlType.put("fromDate", "DATE");
		userDefinedFilterColsHtmlType.put("toDate", "DATE");

		
		userDefinedFilterLookups.put("q_action_takenby", "select us_id, us_name from kbusers where us_active='Y' ");
		userDefinedFilterLookups.put("c_branchcode", "select branch_id, branch_name from kbbranches where branch_active='Y' ");
		
	}//end of no-args constructor ResendCases
	
	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search
		
		String fromExpDt = "", toExpDt = "", branchCode = "", actiontakenby = "";
		boolean foundDate = false, foundbranchCode = false, foundactiontakenby = false;
		
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null)
						&& (!value.equals(""))) {
					if (parameter.equals("fromDate")) {
						fromExpDt = " q_enterdate >= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						foundDate = true;
					} else if (parameter.equals("toDate")) {
						toExpDt = " q_enterdate <= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
					}
					else if (parameter.equals("c_branchcode")) {
						branchCode = " c_branchcode = " + value + " ";
						foundbranchCode = true;
					}
					else if (parameter.equals("q_action_takenby")) {
						actiontakenby = " q_action_takenby = " + value + " ";
						foundactiontakenby = true;
					}
				}
			}

		}
		// the next statement is important so we can skip the generating of
		// where clause from search_paramval
		//userDefinedWhere = " having 1=1 "; 
		
		MainSql = "select c_branchcode, branch_name, q.q_action_takenby, ifnull(us_name,'الموظف لم يعد موجود')as us_name, '' as fromDate, '' as toDate, '' as dummy, "
				+ "sum(case when q_stage = 'DLV' and c_alllowagentpay = 'Y' and c_allowcustpay = 'Y' and c_settled = 'FULL' then 1 else 0 end ) as Dleiverd, "
				+ "concat(round(( (sum(case when q_stage = 'DLV' and c_alllowagentpay = 'Y' and c_allowcustpay = 'Y' and c_settled = 'FULL' then 1 else 0 end ))/count(*) *100),2),'%') as Dleiverd2,  "
				+ "sum(case when q_stage = 'CNCL' and q_step = 'RTN_INSTORE' and c_allowrtnagent = 'Y' and c_agentrtnid != 0 then 1 else 0 end ) as Canceled,  "
				+ "concat(round(( (sum(case when q_stage = 'CNCL' and q_step = 'RTN_INSTORE' and c_allowrtnagent = 'Y' and c_agentrtnid != 0 then 1 else 0 end ))/count(*) *100),2),'%')  as Canceled2,  "
				+ "sum(case when (q_stage = 'DLV' and c_settled != 'FULL') and  (q_stage != 'CNCL' and q_step != 'RTN_INSTORE') then 1 else 0 end ) as Underprocess,  "
				+ "concat(round(( (sum(case when (q_stage = 'DLV' and c_settled != 'FULL') and  (q_stage != 'CNCL' and q_step != 'RTN_INSTORE') then 1 else 0 end ))/count(*) *100),2),'%') as Underprocess2,  "
				+ "(sum(case when q_stage = 'DLV' and c_alllowagentpay = 'Y' and c_allowcustpay = 'Y' and c_settled = 'FULL' then 1 else 0 end ) +  "
				+ "sum(case when q_stage = 'CNCL' and q_step = 'RTN_INSTORE' and c_allowrtnagent = 'Y' and c_agentrtnid != 0  then 1 else 0 end ) +  "
				+ "sum(case when (q_stage = 'DLV' and c_settled != 'FULL') and  (q_stage != 'CNCL' and q_step != 'RTN_INSTORE') then 1 else 0 end )) as ttl  "
				+ "from p_cases c  "
				+ "join kbbranches on c.c_branchcode = branch_id  ";
				
	
		//Test them one by one if one is true
		if (foundDate & foundbranchCode & foundactiontakenby) { //Then test if 3 of filter are true
			MainSql += "join ( "
					+ "select q_caseid, q_enterdate, q_action_takenby, us_name "
					+ "from p_queue_hist "
					+ "left join kbusers on (q_action_takenby = us_id) "
					+ "where q_action = 'RESEND' and " + fromExpDt + " and " + toExpDt +  " and " + actiontakenby + ") q on c.c_id = q.q_caseid "
					+ "where " + branchCode + " "
					+ "group by c_branchcode, q.q_action_takenby ";
		}else if (foundDate & foundactiontakenby) { //Then test if 2 of filter are true except foundbranchCode
			MainSql += "join ( "
					+ "select q_caseid, q_enterdate, q_action_takenby, us_name "
					+ "from p_queue_hist "
					+ "left join kbusers on (q_action_takenby = us_id) "
					+ "where q_action = 'RESEND' and " + fromExpDt + " and " + toExpDt +  " and " + actiontakenby + ") q on c.c_id = q.q_caseid "
					+ "group by c_branchcode, q.q_action_takenby ";
		}else if (foundDate  & foundbranchCode) { //Then test if 2 of filter are true except foundactiontakenby
			MainSql += "join ( "
					+ "select q_caseid, q_enterdate, q_action_takenby, us_name "
					+ "from p_queue_hist "
					+ "left join kbusers on (q_action_takenby = us_id) "
					+ "where q_action = 'RESEND' and " + fromExpDt + " and " + toExpDt + ") q on c.c_id = q.q_caseid "
					+ "where " + branchCode + " "
					+ "group by c_branchcode, q.q_action_takenby ";
		}else if (foundbranchCode & foundactiontakenby) { //Then test if 2 of filter are true except foundDate
			MainSql += "join ( "
					+ "select q_caseid, q_enterdate, q_action_takenby, us_name "
					+ "from p_queue_hist "
					+ "left join kbusers on (q_action_takenby = us_id) "
					+ "where q_action = 'RESEND' and " + actiontakenby + ") q on c.c_id = q.q_caseid "
					+ "where " + branchCode + " "
					+ "group by c_branchcode, q.q_action_takenby ";
		}else if (foundDate) {
			MainSql += "join ( "
					+ "select q_caseid, q_enterdate, q_action_takenby, us_name "
					+ "from p_queue_hist "
					+ "left join kbusers on (q_action_takenby = us_id) "
					+ "where q_action = 'RESEND' and " + fromExpDt + " and " + toExpDt +  ") q on c.c_id = q.q_caseid "
					+ "group by c_branchcode, q.q_action_takenby ";
		}else if (foundbranchCode) {
			MainSql += "join ( "
					+ "select q_caseid, q_enterdate, q_action_takenby, us_name "
					+ "from p_queue_hist "
					+ "left join kbusers on (q_action_takenby = us_id) "
					+ "where q_action = 'RESEND') q on c.c_id = q.q_caseid "
					+ "where " + branchCode + " "
					+ "group by c_branchcode, q.q_action_takenby ";
		}else if (foundactiontakenby) {
			MainSql += "join ( "
					+ "select q_caseid, q_enterdate, q_action_takenby, us_name "
					+ "from p_queue_hist "
					+ "left join kbusers on (q_action_takenby = us_id) "
					+ "where q_action = 'RESEND' and " + actiontakenby + ") q on c.c_id = q.q_caseid "
					+ "group by c_branchcode, q.q_action_takenby ";
		}else {
			MainSql += "join ( "
					+ "select q_caseid, q_enterdate, q_action_takenby, us_name "
					+ "from p_queue_hist "
					+ "left join kbusers on (q_action_takenby = us_id) "
					+ "where  q_action = 'RESEND') q on c.c_id = q.q_caseid "
					+ "group by c_branchcode, q.q_action_takenby ";
		}
	}//end of method initialize
	
	@Override
	public StringBuilder genListing(){
		search_paramval.remove("fromDate");
		search_paramval.remove("toDate");
		search_paramval.remove("c_branchcode");
		search_paramval.remove("q_action_takenby");
		return super.genListing();
	}//end of method genListing
	
}//end of class ResendCases
