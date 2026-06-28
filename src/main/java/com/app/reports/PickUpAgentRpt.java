package com.app.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class PickUpAgentRpt extends CoreMgr {
	public PickUpAgentRpt() {
		MainSql = "select c_pickupagent, " + 
				"sum(case when (q_stage='DLV') then 1 else 0 end)as dlv, "+ 
				"sum(case when (q_stage='CNCL') then 1 else 0 end)as rtn, " +
				"sum(case when (q_stage='AGENTOP' and q_step='ONWAY') then 1 else 0 end)as underDlv, " +
				"sum(case when (q_stage='AGENTOP' and q_step='POSTPONED') then 1 else 0 end)as postPan " +
				" from p_cases  " +
				" where  1=0 "+
				" GROUP BY c_pickupagent ";

		mainTable = "p_cases";
		// keyCol = "q_id";

		canFilter = true;

		userDefinedFilterCols.add("fromDate");
		userDefinedFilterCols.add("toDate");
		userDefinedFilterCols.add("c_pickupagent");
		
		userDefinedFilterColsHtmlType.put("c_pickupagent", "DROPLIST");

		userDefinedLookups.put("c_pickupagent",
						"select us_id ,us_name  From kbusers where us_rank='PICKUPAGENT' and us_branchcode = {userstorecode}");
		
		

		userDefinedNewColsHtmlType.put("fromDate", "DATE");
		userDefinedNewColsHtmlType.put("toDate", "DATE");
		userDefinedColLabel.put("toDate", "  الى تاريخ");
		userDefinedColLabel.put("fromDate", "  من تاريخ");

		userDefinedColLabel.put("c_pickupagent", "مندوب الإستلام");
		userDefinedColLabel.put("rtn", "عدد الراجع ");
		userDefinedColLabel.put("dlv", "عدد التسليمات الناجحة ");
		userDefinedColLabel.put("underDlv", "قيد التوصيل");
		userDefinedColLabel.put("postPan", "مؤجلة");

		userDefinedColsMustFillFilter.add("toDate");
		
		userDefinedColsMustFillFilter.add("fromDate");
		//userDefinedColsMustFillFilter.add("us_loginid");

		userDefinedGridCols.add("c_pickupagent");
		userDefinedGridCols.add("rtn");
		userDefinedGridCols.add("dlv");
		userDefinedGridCols.add("underDlv");
		userDefinedGridCols.add("postPan");

		userDefinedCaption = "أداء مندوبين التوصيل";

	}// end of con shipments_pickup

	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		String fromExpDt = "", toExpDt = "", from = "", to = "", us_loginid = "";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {
					if (parameter.equals("fromDate")) {
						from = value;
						fromExpDt = " c_createddt >= ('" + from + "')";
						foundSearch = true;
					} else if (parameter.equals("toDate")) {
						to = value;
						toExpDt = " c_createddt < date_add('" + to+ "', interval 1 day)";
					} 
					if (parameter.equals("c_pickupagent")) {
						us_loginid = value;
					}
				}
			}
		}
		// the next statement is important so we can skip the generating of where clause from search_paramval
		userDefinedWhere = " having 1=0 ";
		String whereClause = "";
		if (foundSearch) {
			whereClause = "  "+fromExpDt+"and "+toExpDt+" "; // when you build the query yourself then empty this.
			if (!us_loginid.equals("")) {
				whereClause +=" and c_pickupagent ='"+us_loginid+"' ";
			}
			// then use the new where clause inside the query 
			MainSql = "select c_pickupagent, " + 
					"sum(case when (q_stage='DLV') then 1 else 0 end)as dlv, "+ 
					"sum(case when (q_stage='CNCL') then 1 else 0 end)as rtn, " +
					"sum(case when (q_stage='AGENTOP' and q_step='ONWAY') then 1 else 0 end)as underDlv, " +
					"sum(case when (q_stage='AGENTOP' and q_step='POSTPONED') then 1 else 0 end)as postPan " +
					" from p_cases  " +
					" join kbusers on (us_id = c_pickupagent) and us_branchcode = "+currentBranch+
					" where "+whereClause+
					" GROUP BY c_pickupagent ";
			userDefinedWhere = " having 1=1 "; // 
		}
	}
}
