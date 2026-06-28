package com.app.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class CreatorStats extends CoreMgr {
	public CreatorStats() {

		MainSql = "SELECT c_createdby, ifnull(us_name,c_createdby) as us_name, date(c_createddt) as crtd, count(*)as tot, '' as fromdate,'' as todate  "
				+ "from p_cases "
				+ "left join kbusers on c_createdby = us_id "
				+ "where 1=0 "
				+ "group by c_createdby, date(c_createddt) " ;
		
		canFilter = true;
		
		userDefinedGroupColsOrderBy = "crtd";
		userDefinedGroupByCol = "crtd";
		UserDefinedPageRows = 1000;
		userDefinedSumCols.add("tot");
		userDefinedCaption =" ";
		
		userDefinedGridCols.add("crtd");
		userDefinedGridCols.add("us_name");
		userDefinedGridCols.add("tot");
		
		userDefinedColLabel.put("fromdate", "من تاريخ و وقت");
		userDefinedColLabel.put("todate","الى تاريخ و وقت");
		userDefinedColLabel.put("tot","العدد");
		userDefinedColLabel.put("us_name","أدخل بواسطة ");
		userDefinedColLabel.put("crtd","تاريخ");
	
		userDefinedFilterCols.add("fromdate");
		userDefinedFilterCols.add("todate");
		userDefinedColsMustFillFilter.add("todate");
		userDefinedColsMustFillFilter.add("fromdate");
		
		userDefinedNewColsHtmlType.put("fromdate", "DATETIME");
		userDefinedNewColsHtmlType.put("todate", "DATETIME");
				
	}

	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search
	
		String fromExpDt = "", toExpDt = "" ,from="",todate="";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null)
						&& (!value.equals(""))) {
					if (parameter.equals("fromdate")) {
						fromExpDt = " c_createddt >= date('" + value
								+ "', '%Y-%m-%d')";
						from=value;
						foundSearch = true;
					} else if (parameter.equals("todate")) {
						toExpDt = " c_createddt <= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						todate=value;
					}
				}
			}
	
		}
		int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		// the next statement is important so we can skip the generating of
		// where clause from search_paramval
		userDefinedWhere = " having 1=1";
		if (foundSearch) {
			MainSql = "	SELECT c_createdby, ifnull(us_name,c_createdby) as us_name, date(c_createddt) as crtd, count(*)as tot, '' as fromdate,'' as todate  "
			+ "from p_cases "
			+ " left join kbusers on c_createdby = us_id"
			+ " where c_createddt>='"+from+"' "
			+ "and  c_createddt<='"+todate+"' and c_branchcode="+branchId_G+" "
					+ "group by c_createdby, date(c_createddt) ";
					
		}
		
	}
}
