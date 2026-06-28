package com.app.incomeoutcome.bank;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class SafeCashTransactionsReport extends CoreMgr {
	public SafeCashTransactionsReport() {
		MainSql = "select *,'' fromdt, '' todt from p_safe where saf_tranname='CASH' and 1=0 ";
		userDefinedGroupByCol = "saf_tranentity";
		userDefinedSumCols.add("saf_amount_iqd");
		groupSumCaption = "المجموع";
		
		canFilter = true;
		
		userDefinedFilterCols.add("saf_trantype");
		userDefinedFilterCols.add("fromdt");
		userDefinedFilterCols.add("todt");
		userDefinedFilterCols.add("saf_tranentity");
		
		userDefinedGridCols.add("saf_before_transaction");
		userDefinedGridCols.add("saf_amount_iqd");
		userDefinedGridCols.add("saf_createdby");
		userDefinedGridCols.add("saf_trandate");
		userDefinedGridCols.add("saf_rmk");
		
		userDefinedColLabel.put("fromdt", "بتاريخ");
		userDefinedColLabel.put("todt", "الى تاريخ");
		userDefinedColLabel.put("saf_before_transaction", "الرصيد قبل المعاملة");
		userDefinedColLabel.put("saf_amount_iqd", "مبلغ المعاملة");
		userDefinedColLabel.put("saf_createdby", "تمت من خلال ");
		userDefinedColLabel.put("saf_trandate", "تاريخ المعاملة");
		userDefinedColLabel.put("saf_rmk", "الملاحظات");
		userDefinedColLabel.put("saf_trantype", "نوع المعاملة");
		userDefinedColLabel.put("saf_tranentity", "طرف المعاملة");
		
		userDefinedFilterColsHtmlType.put("saf_trantype", "DROPLIST");
		userDefinedFilterColsHtmlType.put("fromdt", "DATE");
		userDefinedFilterColsHtmlType.put("todt", "DATE");
		userDefinedFilterColsHtmlType.put("saf_tranentity", "DROPLIST");
		
		userDefinedColsMustFillFilter.add("saf_trantype");
		
		userDefinedLookups.put("saf_trantype", "select kbcode, kbdesc from kbgeneral where kbcat1='TRANSACTION' and kbcat2='TYPE'");
		userDefinedLookups.put("saf_tranentity", "select us_id, us_name from kbusers where us_id in(select saf_tranentity from p_safe where saf_tranname='CASH')");
		userDefinedLookups.put("saf_createdby", "select us_id, us_name from kbusers ");
		
		userDefinedCaption = "المبالغ النقدية المسحوبة من القاصة";
	}
	@Override
	public void initialize(HashMap smartyStateMap){
		boolean foundSearch = false;
		boolean dateFound = false;
		super.initialize(smartyStateMap);
		String fromDate = "", toDate = "";
		if (search_paramval !=null ) {
			if (search_paramval.get("saf_trantype")!=null) {
				
				for (String parameter : search_paramval.keySet()) {
					for (String value : search_paramval.get(parameter)) {
						if (!parameter.equals("filter") && (value != null)
								&& (!value.equals(""))) {
							if (parameter.equals("fromdt")) {
								fromDate=value;
								dateFound = true;
							} else if (parameter.equals("todt")) {
								toDate=value;
							}else if (parameter.equals("saf_trantype")) {
								foundSearch = true;
							}
						}
					}
				}
			}
		}
		if(toDate.isEmpty()&&!fromDate.isEmpty())
			toDate = fromDate;
			
		if (foundSearch) {
			MainSql = "select *, '' as fromdt, '' as todt from p_safe where saf_tranname='CASH' ";
			if(dateFound)
				MainSql +="and saf_trandate >= date('"+fromDate+"') and saf_trandate<adddate(date('"+toDate+"'),1)";
		}
	}
	@Override
	public StringBuilder genListing(){
		search_paramval.remove("fromdt");
		search_paramval.remove("todt");
		return super.genListing();
	}
}
