package com.app.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class DriverAgentRpt extends CoreMgr{
	public DriverAgentRpt () {
		MainSql = "select c_assignedagent, " + 
				"sum(case when (q_stage='DLV') then 1 else 0 end)as dlv, "+ 
				"sum(case when (q_stage='CNCL') then 1 else 0 end)as rtn " +
				" from p_cases  " +
				" where (q_stage='DLV' or q_stage ='cncl') and 1=0 "+
				" GROUP BY c_assignedagent ";

		mainTable = "p_cases";
		// keyCol = "q_id";

		canFilter = true;

		userDefinedFilterCols.add("fromDate");
		userDefinedFilterCols.add("toDate");
		userDefinedFilterCols.add("c_assignedagent");
		
		userDefinedFilterColsHtmlType.put("c_assignedagent", "DROPLIST");

		userDefinedLookups.put("c_assignedagent",
						"select us_id ,us_name  From kbusers where us_rank='DLVAGENT' and us_branchcode = {userstorecode}");
		
		

		userDefinedNewColsHtmlType.put("fromDate", "DATE");
		userDefinedNewColsHtmlType.put("toDate", "DATE");
		userDefinedColLabel.put("toDate", "  الى تاريخ");
		userDefinedColLabel.put("fromDate", "  من تاريخ");

		userDefinedColLabel.put("c_assignedagent", "مندوب التوصيل");
		userDefinedColLabel.put("rtn", "عدد الراجع ");
		userDefinedColLabel.put("dlv", "عدد التسليمات الناجحة ");

		userDefinedColsMustFillFilter.add("toDate");
		
		userDefinedColsMustFillFilter.add("fromDate");
		//userDefinedColsMustFillFilter.add("us_loginid");

		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("rtn");
		userDefinedGridCols.add("dlv");

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
						fromExpDt = " c_createddt >= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						from = value;
						foundSearch = true;
					} else if (parameter.equals("toDate")) {
						toExpDt = " c_createddt <= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						to = value;
					} 
					if (parameter.equals("c_assignedagent")) {
						// if(parameter.equals("us_loginid")){}
						us_loginid = value;
						
					}
					
				}
			}
		}
		// the next statement is important so we can skip the generating of
				// where clause from search_paramval
		userDefinedWhere = " having 1=0 "; // 
	
		String whereClause = "";
		if (foundSearch) {
			whereClause = " and "+fromExpDt+"and "+toExpDt+" "; // when you build the query yourself then empty this.
			if (us_loginid.equals("")) {
				;
				
			}
			else {
				whereClause +=" and c_assignedagent ='"+us_loginid+"' ";
			}
			// then use the new where clause inside the query 
			
			
			
			
			MainSql = "select c_assignedagent, " + 
					"sum(case when (q_stage='DLV') then 1 else 0 end)as dlv, "+ 
					"sum(case when (q_stage='CNCL') then 1 else 0 end)as rtn " +
					" from p_cases  " +
					" join kbusers on (us_id = c_assignedagent) and us_branchcode = "+currentBranch+
					" where q_stage='DLV' or q_stage ='CNCL' "+whereClause+
					" GROUP BY c_assignedagent ";

			
		
			userDefinedWhere = " having 1=1 "; // 
		}
		
		
	}

}
