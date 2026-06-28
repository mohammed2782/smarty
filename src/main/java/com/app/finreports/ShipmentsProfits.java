package com.app.finreports;

import java.util.HashMap;
import java.util.regex.Pattern;

import smarty.core.CoreMgr;

public class ShipmentsProfits extends CoreMgr {
	public ShipmentsProfits () {
		MainSql = "select fin_profit.* , '' as fromdt , '' as todt From ( "
				+ "SELECT  "
				+ "        COUNT(c_id) AS total_cases, branch_name, "
				+ "            SUM(c_shipment_cost - (CASE  WHEN cc_pathcost IS NOT NULL AND cc_frombranch IS NOT NULL "
				+ "                THEN  cc_pathcost ELSE c_agentshare END)) AS profit, 'شحناتي' AS rpt_name "
				+ "    FROM "
				+ "        p_cases "
				+ "    LEFT JOIN p_caseschain ON (c_id = cc_caseid AND cc_frombranch = {userstorecode} AND cc_rtnmanifestid = 0) "
				+ "    LEFT JOIN kbbranches ON branch_id = ifnull(cc_tobranch, {userstorecode}) "
				+ "    WHERE c_branchcode ={userstorecode} AND q_stage = 'DLV' and 1=0 "
				+ " GROUP BY cc_tobranch "
				+ "UNION "
				+ "	select count(c_id) as total_cases, branch_name, "
				+ "	sum(to_me.cc_pathcost "
				+ "	- "
				+ "	(case when( q_branch ={userstorecode} and from_me.cc_pathcost is null) then c_agentshare else from_me.cc_pathcost end) ) as profit , "
				+ "    'مرت على مخزني' AS rpt_name "
				+ "	from p_cases "
				+ "	join p_caseschain to_me on (c_id = to_me.cc_caseid and to_me.cc_tobranch = {userstorecode}) "
				+ "	join kbbranches on branch_id =  to_me.cc_frombranch "
				+ "	left join p_caseschain from_me on (from_me.cc_caseid = c_id and from_me.cc_frombranch ={userstorecode}) "
				+ "	where  q_stage = 'DLV'  and 1=0 "
				+ "	group by to_me.cc_frombranch) as fin_profit";
		
		userDefinedColLabel.put("total_cases", "عدد الشحنات الواصلة");
		userDefinedColLabel.put("branch_name", "الفرع");
		userDefinedColLabel.put("profit", "الربح");
		userDefinedColLabel.put("fromdt", "بتاريخ");
		userDefinedColLabel.put("todt", "إلى تاريخ");
		
		userDefinedGridCols.add("branch_name");
		userDefinedGridCols.add("total_cases");
		userDefinedGridCols.add("profit");
		
		userDefinedGroupByCol = "rpt_name";
		
		userDefinedSumCols.add("profit");
		
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
