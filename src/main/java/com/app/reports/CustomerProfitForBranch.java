package com.app.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class CustomerProfitForBranch extends CoreMgr {
	public CustomerProfitForBranch() {

		MainSql = "select mcust_name, c_mastercustid , count(*) as numberShipment, sum(c_shipment_cost  - (case when cc_pathcost is null then c_agentshare  else cc_pathcost end)) as profit "
				+ "from p_cases "
				+ "join kb_mastercustomer on c_mastercustid = mcust_id "
				+ "left join p_caseschain on cc_caseid = c_id and cc_frombranch = '{userstorecode}' "
				+ "where q_stage = 'DLV' and c_branchcode = '{userstorecode}'  "
				+ "group by c_mastercustid";

		mainTable = "p_cases";
		userDefinedCaption = "ارباح العميل بالنسبة للفرع";
		
		userDefinedGridCols.add("mcust_name");
		userDefinedGridCols.add("numberShipment");
		userDefinedGridCols.add("profit");
		
		canFilter = true;

		userDefinedFilterCols.add("fromDate");
		userDefinedFilterCols.add("toDate");
		userDefinedFilterCols.add("c_mastercustid");
		
		userDefinedNewColsHtmlType.put("fromDate", "DATE");
		userDefinedNewColsHtmlType.put("toDate", "DATE");
		userDefinedColLabel.put("toDate", "  الى تاريخ");
		userDefinedColLabel.put("fromDate", "  من تاريخ");
		userDefinedColLabel.put("mcust_name", "اسم الزبون");
		userDefinedColLabel.put("c_mastercustid", "اسم الزبون");
		
		userDefinedColLabel.put("profit", "الارباح");
		userDefinedColLabel.put("numberShipment", "عدد الشحنات الناجحة");

		userDefinedColsMustFillFilter.add("toDate");
		userDefinedColsMustFillFilter.add("fromDate");

		userDefinedLookups.put("c_mastercustid", "select mcust_id, mcust_name from kb_mastercustomer where mcust_branchcode = {userstorecode}");
		userDefinedFilterColsHtmlType.put("c_mastercustid", "DROPLIST");
		
		
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

		if (foundSearch) {

			MainSql = "select mcust_name, c_mastercustid , count(*) as numberShipment, sum(c_shipment_cost  - (case when cc_pathcost is null then c_agentshare  else cc_pathcost end)) as profit "
					+ "from p_cases "
					+ "join kb_mastercustomer on c_mastercustid = mcust_id "
					+ "left join p_caseschain on cc_caseid = c_id and cc_frombranch = "+branchCode+" "
					+ "where q_stage = 'DLV' and c_branchcode ="+branchCode+ " and "+ fromExpDt + " and " + toExpDt
					+ " group by c_mastercustid";

		}
	}
	@Override
	public StringBuilder genListing(){
		search_paramval.remove("fromDate");
		search_paramval.remove("toDate");
		return super.genListing();
	}

}
