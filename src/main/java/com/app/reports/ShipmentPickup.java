package com.app.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class ShipmentPickup extends CoreMgr {
	public ShipmentPickup() {

		MainSql = "select us_loginid,(select count(q_id) from p_queue  where q_code ='delv_to_dest__final_stage' "
				+ "and q_status ='CLS' and us_id=q_assigned_to )as dlv,(select count(q_id) from p_queue "
				+ " where q_code in('init__ord_rqs','in_store__store','return_to_cust__cncl')and q_status ='CLS' "
				+ "and us_id=q_assigned_to) as pickup from  p_queue ,kbusers where 1!=1 and  us_id=q_assigned_to "
				+ "GROUP BY q_assigned_to";

		mainTable = "p_queue";
		// keyCol = "q_id";

		canFilter = true;

		userDefinedFilterCols.add("fromDate");
		userDefinedFilterCols.add("toDate");
		userDefinedFilterCols.add("us_loginid");

		userDefinedFilterLookups
				.put("us_loginid",
						"select us_id ,us_loginid  From p_queue ,kbusers where us_id=q_assigned_to group by q_assigned_to");

		userDefinedNewColsHtmlType.put("fromDate", "DATE");
		userDefinedNewColsHtmlType.put("toDate", "DATE");
		userDefinedColLabel.put("toDate", "  الى تاريخ");
		userDefinedColLabel.put("fromDate", "  من تاريخ");

		userDefinedColLabel.put("us_loginid", "اسم عميل التوصيل");
		userDefinedColLabel.put("pickup", "عدد التوصيلات ");
		userDefinedColLabel.put("dlv", "عدد التسليمات  ");

		userDefinedColsMustFillFilter.add("toDate");
		userDefinedColsMustFillFilter.add("fromDate");
		//userDefinedColsMustFillFilter.add("us_loginid");

		userDefinedGridCols.add("us_loginid");
		userDefinedGridCols.add("pickup");
		userDefinedGridCols.add("dlv");

		userDefinedCaption = "تسلميات و توصيلات الوكلاء";

	}// end of con shipments_pickup

	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search

		String fromExpDt = "", toExpDt = "", from = "", to = "", us_loginid = "";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null)
						&& (!value.equals(""))) {

					if (parameter.equals("fromDate")) {
						fromExpDt = " q_enterdate >= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						from = value;
						foundSearch = true;
					} else if (parameter.equals("toDate")) {
						toExpDt = " q_enterdate <= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						to = value;
					}  if (parameter.equals("us_loginid")) {
						// if(parameter.equals("us_loginid")){}
						us_loginid = value;
					}
				}
			}

		}
		// the next statement is important so we can skip the generating of
		// where clause from search_paramval
		userDefinedWhere = " and 1=0";
		if (foundSearch) {
			if (us_loginid.equals("")) {

				MainSql = "select us_loginid,(select count(q_id) from p_queue " +
						"where q_code ='delv_to_dest__final_stage' and q_status ='CLS' and us_id=q_assigned_to )as dlv" +
						",(select count(q_id) from p_queue where q_code in('init__ord_rqs','in_store__store','return_to_cust__cncl')" +
						"and q_status ='CLS' and us_id=q_assigned_to) as pickup from p_queue ,kbusers where us_id=q_assigned_to " +
						"and "+fromExpDt+"and "+toExpDt+" GROUP BY q_assigned_to ";
			}
			else {
				MainSql = "select us_loginid,(select count(q_id) from p_queue " +
						"where q_code ='delv_to_dest__final_stage' and q_status ='CLS' and us_id=q_assigned_to )as dlv" +
						",(select count(q_id) from p_queue where q_code in('init__ord_rqs','in_store__store','return_to_cust__cncl')" +
						"and q_status ='CLS' and us_id=q_assigned_to) as pickup from p_queue ,kbusers where us_id=q_assigned_to " +
						"and "+fromExpDt+"and "+toExpDt+" and q_assigned_to ='"+us_loginid+"' GROUP BY q_assigned_to ";
			}
			userDefinedWhere = " and 1=1";

		}
	}

}
