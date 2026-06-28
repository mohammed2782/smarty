package com.app.finreports;

import java.util.HashMap;
import java.util.regex.Pattern;

import smarty.core.CoreMgr;
public class ShipmentsReturn extends CoreMgr {
	public ShipmentsReturn () {
		MainSql = "select  '' as fromdt , '' as todt , COUNT(c_id) AS total_cases, branch_name, 'شحناتي' AS rpt_name"
				+ "  from  p_cases "
				+ " left join p_caseschain ON (c_id = cc_caseid AND cc_frombranch = {userstorecode} AND cc_rtnmanifestid = 0)  "
				+ "left join kbbranches ON branch_id = ifnull(cc_tobranch, {userstorecode}) "
				+ " where c_branchcode ={userstorecode} AND q_stage = 'CNCL' and 1=0 GROUP BY cc_tobranch ";
					
		userDefinedColLabel.put("total_cases", "عدد الشحنات الراجعة");
		userDefinedColLabel.put("branch_name", "الفرع");
		userDefinedColLabel.put("fromdt", "بتاريخ");
		userDefinedColLabel.put("todt", "إلى تاريخ");
		userDefinedGridCols.add("branch_name");
		userDefinedGridCols.add("total_cases");		
		canFilter = true;
		userDefinedFilterCols.add("fromdt");
		userDefinedFilterCols.add("todt");
		userDefinedFilterColsHtmlType.put("fromdt", "DATE");
		userDefinedFilterColsHtmlType.put("todt", "DATE");
	}
	
	@Override
	public void initialize(HashMap smartyStateMap){
		int currentBranch_G = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		super.initialize(smartyStateMap);
		String fromdt = "ALL";
		String todt = "ALL";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {
					if (parameter.equals("fromdt")) {
						fromdt =  value;
						foundSearch = true;
					} 
					if (parameter.equals("todt")) {
						todt =  value;
					} 
				}
			}
		}
		if (foundSearch) {
			if (todt.equalsIgnoreCase("ALL") && !fromdt.equalsIgnoreCase("ALL")) {
				todt = fromdt;
			}
			String whereClause=" (c_createddt>=date('"+fromdt+"')) "
					+ " and (c_createddt< date_add('"+todt+"', interval 1 day) ) ";
			MainSql = MainSql.replaceAll(Pattern.quote("{userstorecode}"), currentBranch_G+"")
					.replaceAll("1=0",  whereClause);
		}
		//System.out.println(MainSql);
	}
	@Override 
	public StringBuilder genListing() {
		//System.out.println("calling gen listing---------------------");
		search_paramval.remove("fromdt");
		search_paramval.remove("todt");
		return super.genListing();
	}
}
