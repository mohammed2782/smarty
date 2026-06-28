package com.app.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class CustomerBalance extends CoreMgr {
	public CustomerBalance(){
		MainSql = " select * from vw_custbalance where pcust is not null";
		
		userDefinedFilterCols.add("pcust");
		userDefinedNewColsHtmlType.put("pcust" , "DROPLIST");
		userDefinedLookups.put("pcust", "select c_id , c_name From kbcustomers order by c_name asc" );
		
		userDefinedLookups.put("type", "select 'pmt' , 'مجموع المبلغ المدفوع' from dual union select 'debt' , 'مجموع قيمة الفواتير' from dual union select 'debt_b72017' , 'ديون قبل نوفمبر 2017' from dual" );
		
		userDefinedGridCols.add("type");
		userDefinedGridCols.add("amt");
		
		//userDefinedGridCols.add("pcust");
		
		userDefinedColLabel.put("amt","المبلغ" );
		userDefinedColLabel.put("type","العملية" );
		userDefinedColLabel.put("pcust","الزبون" );
		
		userDefinedSumCols.add("amt");
		userDefinedGroupByCol = "pcust";
		//userDefinedGroupColsExtraOrderBy = "type";//so the order will be pcust , type
		
		userDefinedCaption = "حسابات الزبائن";
		
		clickableRow =true;
		canFilter = true;
		userDefinedGlobalClickRowID = "transrptcustid";
		keyCol = "pcust";
		userModifyTD.put("amt", "changeColor({type},{amt})");
		groupSumCaption =" المجموع";
	}
	public String changeColor(HashMap<String,String> hashy){
		if (hashy.get("type").equals("pmt"))
			return "<td><span style='color:green'>"+numFormat.format(Double.parseDouble(hashy.get("amt")))+"</span></td>";
		else
			return "<td><span style='color:red'>"+numFormat.format(-1*(Double.parseDouble(hashy.get("amt"))))+"</span></td>";
	}

}
