
package com.app.incomeoutcome;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class CompanyDebts_StageAgent extends CoreMgr {
	public CompanyDebts_StageAgent() {
		MainSql = "select  c_name, c_custid, " + 
				" '' as todate, '' as fromdate,  c_receiptamt,c_createddt,c_custreceiptnoori " + 
				"	c_sendmoney , " + 
				"	c_shipment_cost, " + 
				"	(c_receiptamt -  c_sendmoney - c_shipment_cost) as netamt " + 
				" from p_cases join p_queue on (c_id= q_caseid and q_status !='CLS') " + 
				" join kbcustomers on kbcustomers.c_id = c_custid where c_settled !='FULL'  and (q_stage='dlv_stg' and q_step='with_agent') " + 
				" and (c_branchcode='BGHD_1' or 'Y'='Y') and 1=0";
		
		UserDefinedPageRows = 1000;
		canFilter = true;
		userDefinedFilterCols.add("fromdate");
		userDefinedFilterCols.add("todate");
		
		userDefinedColsMustFillFilter.add("fromdate");
		
		userDefinedColLabel.put("todate","الى تاريخ");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصولات");
		userDefinedColLabel.put("c_shipment_cost", " مجموع  مبلغ الشحن ");
		userDefinedColLabel.put("netamt", "مجموع المبلغ الصافي لأصحاب المحلات ");
		userDefinedColLabel.put("c_name", "أسم صاحب المحل");
		userDefinedColLabel.put("c_custid", "أسم العميل");
		userDefinedColLabel.put("fromdate", "من تاريخ");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("fromdate", "من تاريخ");
		
		userDefinedSumCols.add("c_receiptamt");
		userDefinedSumCols.add("c_shipment_cost");
		userDefinedSumCols.add("netamt");
		
		userDefinedGridCols.add("c_custreceiptnoori");
		//userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_receiptamt"); 
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("netamt");
		
		userDefinedGroupByCol = "c_name";
		
		userDefinedFilterCols.add("c_custid");
		userDefinedNewColsHtmlType.put("todate", "DATE");
		userDefinedNewColsHtmlType.put("fromdate", "DATE");
		userDefinedLookups.put("c_custid", "select c_id , c_name from kbcustomers");
		userDefinedFilterColsHtmlType.put("c_custid","DROPLIST");
	}

	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search

		String todate=null, fromDate="";
		boolean foundSearch = false;
		String custid = null;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null)
						&& (!value.equals(""))) {
					if (parameter.equals("fromdate")) {
						/*toExpDt = " c_createddt <= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						*/
						fromDate=value;
						foundSearch = true;
					}else if (parameter.equals("todate")) {
						/*toExpDt = " c_createddt <= DATE_FORMAT('" + value
						+ "', '%Y-%m-%d')";
						*/
						todate=value;
					}  
					if (parameter.equals("c_custid")) {
						/*toExpDt = " c_createddt <= DATE_FORMAT('" + value
								+ "', '%Y-%m-%d')";
						*/
						custid=value;
						
					} 
				}
			}

		}
		// the next statement is important so we can skip the generating of
		// where clause from search_paramval
		
		userDefinedWhere = " having 1=1";
		if (custid!=null)
			userDefinedWhere += " and c_custid='"+custid+"'";
		if (todate!=null) {
			userDefinedWhere += " and c_createddt<=adddate(date('"+todate+"'),1)";
		}
		if (foundSearch) {
			MainSql = "select c_name, c_custid, c_createddt,c_custreceiptnoori, " + 
					" '' as todate,  c_receiptamt, " + 
					" c_sendmoney , " + 
					" c_shipment_cost, " + 
					"(c_receiptamt -  c_sendmoney - c_shipment_cost) as netamt " + 
					" from p_cases  join p_queue on (c_id= q_caseid and q_status !='CLS') " + 
					" join kbcustomers on kbcustomers.c_id = c_custid where c_settled !='FULL'  and (q_stage='dlv_stg' and q_step='with_agent') " + 
					" and (c_branchcode='BGHD_1' or 'Y'='Y') and c_createddt>=date('"+fromDate+"')" ;
			
			
		}
		System.out.println(MainSql);
		//System.out.println(fromExpDt);
		//System.out.println(toExpDt);

	}
}
