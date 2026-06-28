package com.app.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class SuccessFailedShipment extends CoreMgr {
	public SuccessFailedShipment() {

		MainSql = "select '' as c_rcv_state, '' as fromDate, '' as toDate ,sum((case when  q_stage='DLV' then 1 else 0 end))as " +
				"success, c_branchcode, sum((case when  q_stage='CNCL' then 1 else 0 end))as" +
				" failed ,(sum(c_receiptamt)-sum(c_shipment_cost)) as profit,  sum((case when  (q_stage='CNCL') or (q_stage='DLV') then 0 else 1 end))as"
				+ " underprocess, c_custid, date(c_createddt) as c_createddt from p_cases  where 1=0 and c_branchcode={userstorecode} " +
				"group by DATE_FORMAT(c_createddt, '%Y-%m-%d') ";

		mainTable = "p_cases";
		
		userDefinedGroupByCol = "c_branchcode";
		
		userDefinedGridCols.add("success");
		userDefinedGridCols.add("failed");
		userDefinedGridCols.add("underprocess");
//		userDefinedGridCols.add("profit");

		canFilter = true;

		userDefinedFilterCols.add("fromDate");
		userDefinedFilterCols.add("toDate");
		userDefinedFilterCols.add("c_custid");
		userDefinedFilterCols.add("c_rcv_state");
		
		userDefinedNewColsHtmlType.put("fromDate", "DATE");
		userDefinedNewColsHtmlType.put("toDate", "DATE");
		userDefinedColLabel.put("toDate", "  الى تاريخ");
		userDefinedColLabel.put("fromDate", "  من تاريخ");
		userDefinedColLabel.put("c_createddt", "تاريخ الشحنة");
		userDefinedColLabel.put("profit", "الارباح بالدينار العراقي");

		userDefinedColLabel.put("c_custid", "المتجر");
		userDefinedCaption = " الشحنات الناجحة والراجعة  ";
		userDefinedColLabel.put("success", "عدد الشحنات الناحجة ");
		userDefinedColLabel.put("failed", "عدد الشحنات لراجعة");
		userDefinedColLabel.put("c_rcv_state", "المحافظات");
		userDefinedColLabel.put("underprocess", "عدد الشحنات قيد المعالجة");
		userDefinedColsMustFillFilter.add("toDate");
		userDefinedColsMustFillFilter.add("fromDate");

		userDefinedFilterColsHtmlType.put("c_rcv_state", "MULTILIST");
		userDefinedLookups.put("c_rcv_state","select st_code , st_name_ar From kbstate where  st_branch = {userstorecode} ");
		userDefinedLookups.put("c_custid", "select cust_id, cust_name from kbcustomers where cust_branch = {userstorecode}");
		userDefinedLookups.put("c_branchcode", "select branch_id, branch_name from kbbranches");
		userDefinedFilterColsHtmlType.put("c_custid", "DROPLIST");
		
		
	}

	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search
		String branchCode = replaceVarsinString("{userstorecode}", arrayGlobals);
		String fromExpDt = "", toExpDt = "" ,from="",to="";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null)
						&& (!value.equals(""))) {
					if (parameter.equals("fromDate")) {
						fromExpDt = " c_createddt >= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						from=value;
						foundSearch = true;
					} else if (parameter.equals("toDate")) {
						toExpDt = " c_createddt <= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						to=value;
					}
				}
			}

		}
		// the next statement is important so we can skip the generating of
		// where clause from search_paramval
		//userDefinedWhere = " having 1=1 "; // 
		if (foundSearch) {
			MainSql = "select '' as fromDate, '' as toDate, c_branchcode, c_custid, success, failed,profit, underprocess from("
					+ " select sum(case when q_stage='DLV' then 1 else 0 end)as success,"
					+ " sum(case when  q_stage='CNCL'  then 1 else 0 end)as  failed,  "
					+ " (sum(c_receiptamt)-sum(c_shipment_cost)) as profit,"
					+ " sum(case when  q_stage='CNCL' or q_stage='DLV' then 0 else 1 end)as  underprocess, "
					+ " c_custid, c_branchcode from p_cases "
					+ " where c_branchcode = "+branchCode
					+ " and "+ fromExpDt + " and " + toExpDt
					+ " union "
					+ " select concat(round(( (sum(case when q_stage='DLV' then 1 else 0 end))/count(*) *100),2),'%')as success,"
					+ " concat(round(( (sum(case when  q_stage='CNCL'  then 1 else 0 end))/count(*) *100),2),'%')as  failed,  "
					+ " '0' as  profit, "
					+ " concat(round(( (sum(case when  q_stage='CNCL' or q_stage='DLV' then 0 else 1 end))/count(*) *100),2),'%')as  underprocess, "
					+ " c_custid, c_branchcode from p_cases "
					+ " where c_branchcode = "+branchCode
					+ " and "+ fromExpDt + " and " + toExpDt
					+" )abc";
		}
	}
	@Override
	public StringBuilder genListing(){
		search_paramval.remove("fromDate");
		search_paramval.remove("toDate");
		return super.genListing();
	}

}
