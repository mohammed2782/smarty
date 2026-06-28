package com.app.incomeoutcome;

import java.util.HashMap;

import com.app.financials.FinOperationEntity;

import smarty.core.CoreMgr;
// the idea is to display the records as check list and select them using checkbox, then 
// make a record in p_fin_transactions.
public class PickUpAgentShareBalance extends CoreMgr {
	public PickUpAgentShareBalance() {
		MainSql = " select  trans_createddt, trans_entity_id, '' as dummygroupy, trans_amount_iqd, "
				+ " '' as tot_profit, '' as fromdate, '' as todate, '' as c_pickupagent, '' as grouptype "
				+ " from p_fin_transactions  "
				+ " where trans_operationentity ='~RANK_TO_REPLACE~' and trans_operationcode = 'CASES'  "
				+ " and trans_initiated_in_branch_id = {userstorecode}  and 1=0 "
				+ " group by trans_id";
		
		mainTable = "p_cases";
		canFilter = true;
		
		userDefinedFilterCols.add("fromdate");
		userDefinedFilterCols.add("todate");
		userDefinedFilterCols.add("grouptype");
		
		userDefinedGroupByCol = "trans_entity_id";
		userDefinedSumCols.add("trans_amount_iqd");
		userDefinedSumCols.add("total_cases");
		groupSumCaption = "المجموع";
		
		userDefinedGridCols.add("trans_entity_id");
		userDefinedGridCols.add("trans_createddt");
		userDefinedGridCols.add("trans_amount_iqd");
		
		userDefinedColLabel.put("fromdate", "بتاريخ");
		userDefinedColLabel.put("todate", "إلى تاريخ");
		userDefinedColLabel.put("trans_entity_id", "مندوب الأستلام");
		userDefinedColLabel.put("trans_createddt", "تاريخ محاسبة الوصولات");
		userDefinedColLabel.put("trans_amount_iqd", "مبلغ الوصولات دينار عراقي");
		
		userDefinedColLabel.put("grouptype", "ترتيب الدفعات");
		userDefinedColLabel.put("c_name", "العميل");
		
		userDefinedNewColsHtmlType.put("fromdate", "DATE");
		userDefinedNewColsHtmlType.put("todate", "DATE");
		userDefinedNewColsHtmlType.put("c_pickupagent", "DROPLIST");
		
		userDefinedColsMustFillFilter.add("grouptype");
		userDefinedColsMustFillFilter.add("fromdate");
		userDefinedColsMustFillFilter.add("todate");
		
		
		userDefinedLookups.put("grouptype", "select 'PICKUP','دفوعات مندوب إستلام' from dual union select 'CUST', 'دفعات عملاء' from dual");
		
		userDefinedCaption = "أرباح ألشريك";
		UserDefinedPageRows = 1000;
	}
	
	public void initialize(HashMap smartyStateMap) {
		boolean foundSearch = false;
		super.initialize(smartyStateMap);
		String fromDate = "", toDate = "", grouptype = "";
		if (search_paramval !=null ) {
			if (search_paramval.get("todate")!=null && search_paramval.get("fromdate")!=null && search_paramval.get("grouptype")!=null) {
				for (String parameter : search_paramval.keySet()) {
					for (String value : search_paramval.get(parameter)) {
						if (!parameter.equals("filter") && (value != null)
								&& (!value.equals(""))) {
							if (parameter.equals("fromdate")) {
								fromDate=value;
								foundSearch = true;
							} else if (parameter.equals("todate")) {
								toDate=value;
							} else if (parameter.equals("grouptype")) {
								grouptype=value;
							}
						}
					}
				}
			}
		}
		if(foundSearch) {
			String todt = toDate.replace("-", "");
			String fromdt = fromDate.replace("-", "");
			
			if(grouptype.equals("PICKUP")) {
				MainSql= MainSql.replaceAll("~RANK_TO_REPLACE~", FinOperationEntity.PICKUP_AGENT.toString());
				
			}else if(grouptype.equals("CUST")) {
				MainSql= MainSql.replaceAll("~RANK_TO_REPLACE~", FinOperationEntity.CUSTOMER.toString());
			}
			String dateSearch = "trans_createddt>=date('"+fromDate+"') and "
					+ " trans_createddt <adddate(date('"+toDate+"'),1) )";
			MainSql.replaceAll("1=0", dateSearch);
		}
	}
	@Override
	public StringBuilder genListing(){
		search_paramval.remove("fromdate");
		search_paramval.remove("todate");
		search_paramval.remove("grouptype");
		return super.genListing();
	}
}