package com.app.reports;

import smarty.core.CoreMgr;
public class CustDebts extends CoreMgr{
	public CustDebts (){
		MainSql = " select * from vw_custDebts";
		canFilter   = true;
		userDefinedFilterCols.add("mb_custid");
		userDefinedNewColsHtmlType.put("mb_custid" , "DROPLIST");
		userDefinedLookups.put("mb_custid", "select c_id , c_name From kbcustomers order by c_name asc" );
		
		userDefinedGridCols.add("tot_remain");
		userDefinedGridCols.add("pmt_amt");
		userDefinedGridCols.add("tot_bill");
		userDefinedGridCols.add("mb_billdt");
		userDefinedGridCols.add("mb_id");
		
		userDefinedColLabel.put("mb_custid", "أسم الزبون");
		userDefinedColLabel.put("mb_billdt", "تاريخ الفاتورة");
		userDefinedColLabel.put("tot_bill", "مبلغ الفاتورة");
		userDefinedColLabel.put("pmt_amt", "المبلغ المدفوع من الفاتورة");
		userDefinedColLabel.put("tot_remain", "المبلغ المتبقي");
		userDefinedColLabel.put("mb_id", "رقم الفاتورة");
		
		userDefinedSumCols.add("tot_bill");
		userDefinedSumCols.add("pmt_amt");
		userDefinedSumCols.add("tot_remain");
		userDefinedGroupByCol = "mb_custid";
		
		userDefinedCaption = "ديون الزبائن";		
	}

}
