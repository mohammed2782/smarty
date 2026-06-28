package com.app.reports;

import smarty.core.CoreMgr;

public class UncollectedDebts extends CoreMgr {
	
	public UncollectedDebts(){
		MainSql = " select  mb_id , mb_custid , mb_billdt  , pmt_id , pmt_amt , pmt_rmk ,mb_creadtby, sum(mbd_totalamount) as tot_bill, " +
				  " ( sum(mbd_totalamount) - sum(ifnull(pmt_amt,0))) as tot_remain from mbills_out "+ 
				  " join mbilldtl_out on (mbd_bill_id =mb_id) "+ 
				  " left join m_payments_credit on pmt_billno = mb_id "+ 
				  " where mb_settled = 'N' "+
				  " group by mb_id , mb_custid , mb_billdt  , pmt_id , pmt_amt,pmt_rmk , mb_amt , mb_creadtby "+
				  " order by mb_custid,mb_billdt desc ";
		
		
		userDefinedColLabel.put("mb_billdt", "تاريخ");
		userDefinedColLabel.put("tot_bill", "مبلغ الفاتورة");
		userDefinedColLabel.put("mb_creadtby", "أدخل عن طريق");
		userDefinedColLabel.put("mb_custid", "زبون");
		userDefinedColLabel.put("pmt_rmk", "ملاحظات");
		userDefinedColLabel.put("pmt_amt" , "مبلغ مدفوع");
		userDefinedColLabel.put("tot_remain" , "مبلغ متبقي");
		userDefinedColLabel.put("mb_id", "رقم الفاتورة");
		
		
		userDefinedGridCols.add("mb_custid");
		userDefinedGridCols.add("mb_billdt");
		userDefinedGridCols.add("tot_bill");
		userDefinedGridCols.add("pmt_amt");
		userDefinedGridCols.add("tot_remain");
		userDefinedGridCols.add("pmt_rmk");
		userDefinedGridCols.add("mb_creadtby");
		userDefinedGridCols.add("mb_id");
		
		userDefinedLookups.put("mb_custid", "select c_id , c_name from kbcustomers" );
		userDefinedNewColsHtmlType.put("mb_custid" , "DROPLIST");
		userDefinedFilterCols.add("mb_custid");
		canFilter = true;
		UserDefinedPageRows=50;
		userDefinedCaption = "ديون لم يتم تحصيلها";
	}
}
