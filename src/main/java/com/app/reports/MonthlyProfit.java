package com.app.reports;
import java.util.HashMap;

import smarty.core.CoreMgr;

public class MonthlyProfit  extends CoreMgr{
	public MonthlyProfit(){
		MainSql = "	select 'profits' as dummygroupy, sum(pr) as totinc, sum(ex) as totexp , (sum(pr)-sum(ex))  as netprofit, trandate , '' as fromdate,'' as todate from (" + 
				" select (case when trantype = 'shipprofit' then  amt else 0 end ) as pr," + 
				" (case when trantype = 'expense' then  amt else 0 end ) as ex," + 
				" trandate from (" + 
				" select 'shipprofit' as trantype, sum(c_shipment_cost) - sum(c_agentshare) as amt , date(ap_paymentdt) trandate from p_cases"
				+ " join p_agent_payments on (c_agentpmtid = ap_id) "
				+ " where q_stage = 'DLV' and q_step ='DLEIVERD' and c_agentpmtid!=0 and c_branchcode={userstorecode}" 
				+ " group by date(ap_paymentdt)" + 
				" union " + 
				" select  'expense' as trantype , sum(ou_price) as amt , date(ou_date) trandate from p_outcomes group by date(ou_date)) lvl1) lvl2"
				+ " where 1=0 " + 
				" group by trandate" ;
		canFilter = true;
		userDefinedFilterCols.add("fromdate");
		userDefinedFilterCols.add("todate");
		userDefinedGroupColsOrderBy = "trandate";
		userDefinedGroupByCol = "dummygroupy";
		
		userDefinedNewColsHtmlType.put("fromdate", "DATE");
		userDefinedNewColsHtmlType.put("todate", "DATE");
		userDefinedColLabel.put("todate","  الى تاريخ");
		userDefinedColLabel.put("fromdate", "  من تاريخ");
		
		
		userDefinedColLabel.put("totexp","المصروفات");
		userDefinedColLabel.put("totinc","الايرادات ");
		userDefinedColLabel.put("netprofit","الربح ");
		userDefinedColLabel.put("trandate","تاريخ");

		userDefinedColsMustFillFilter.add("todate");
		userDefinedColsMustFillFilter.add("fromdate");
		
		userDefinedGridCols.add("trandate");
		userDefinedGridCols.add("totinc");
		userDefinedGridCols.add("totexp");
		userDefinedGridCols.add("netprofit");
		
		userDefinedSumCols.add("netprofit");
		userDefinedSumCols.add("totinc");
		userDefinedSumCols.add("totexp");
		
		UserDefinedPageRows = 1000;
		
		userDefinedCaption =" ";
	}
	
	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search
		int branchCode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());		String fromExpDt = "", toExpDt = "" ,from="",todate="";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null)
						&& (!value.equals(""))) {
					if (parameter.equals("fromdate")) {
						fromExpDt = " ap_paymentdt >= date('" + value
								+ "', '%Y-%m-%d')";
						from=value;
						foundSearch = true;
					} else if (parameter.equals("todate")) {
						toExpDt = " ap_paymentdt <= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						todate=value;
					}
				}
			}

		}
		// the next statement is important so we can skip the generating of
		// where clause from search_paramval
		userDefinedWhere = " having 1=1";
		if (foundSearch) {
			MainSql = "	select  'مدخولات ومنصرفات' as dummygroupy , sum(pr) as totinc, sum(ex) as totexp , (sum(pr)-sum(ex))  as netprofit, trandate , '' as fromdate, '' as todate from (" + 
					" select (case when trantype = 'shipprofit' then  amt else 0 end ) as pr," + 
					" (case when trantype = 'expense' then  amt else 0 end ) as ex," + 
					" trandate from (" + 
					" select 'shipprofit' as trantype, sum(c_shipment_cost) - sum(case when cc_pathcost>0 then cc_pathcost else c_agentshare end) as amt ,"
					+ " date(ap_paymentdt) trandate from p_cases"
					+ " join p_agent_payments on (c_agentpmtid = ap_id) "
					+ " left join p_caseschain on(cc_caseid = c_id and "+branchCode+"=cc_tobranch) "
					+ " where ( q_stage = 'DLV' and q_step ='DLEIVERD' and c_agentpmtid!=0 and c_branchcode="+branchCode+") "  
					+ " and  ap_paymentdt>='"+from+"' and  ap_paymentdt<=date('"+todate+"') group by date(ap_paymentdt) " + 
					" union " + 
					" select  'expense' as trantype , sum(ou_price) as amt2 , date(ou_date) trandate from p_outcomes"
					+ " where ou_date>='"+from+"' and  ou_date<=adddate(date('"+todate+"'),1)  group by date(ou_date)  ) lvl1) lvl2 " + 
					" group by trandate" ;

		}
	}
}
