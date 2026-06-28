package com.app.reports;

import smarty.core.CoreMgr;

public class profit extends CoreMgr {
	public profit(){
		MainSql = "select sum(mbilldtl_out.mbd_profit)as tot_profit , mb_settled ,  mb_custid , mb_id , mb_billdt    from mbills_out"+ 
					" join mbilldtl_out on mb_id= mbd_bill_id"+
					" group by  mb_custid , mb_id , mb_billdt , mb_settled";
		userDefinedColLabel.put("mb_id", "رقم الفاتورة");
		userDefinedColLabel.put("mb_billdt", "تاربخ الفاتورة");
		userDefinedColLabel.put("mb_custid", "الزبون");
		userDefinedColLabel.put("tot_profit", "مجموع الربح");
		userDefinedColLabel.put("mb_settled", "تم دفع المبلغ كاملا");
		
		canFilter = true;
		userDefinedLookups.put("mb_custid", "select c_id , c_name from kbcustomers" );
		userDefinedLookups.put("mb_settled", "select 'Y' , 'نعم' from dual union select 'N' , 'كلا' from dual" );
		userDefinedNewColsHtmlType.put("mb_custid" , "DROPLIST");
		userDefinedFilterCols.add("mb_custid");
		userDefinedFilterCols.add("mb_id");
		userDefinedFilterCols.add("mb_billdt");
		clickableRow =true;
		keyCol ="mb_id";
		userDefinedGlobalClickRowID = "sellbillid_rpt";
		userDefinedCaption ="الأرباح";
	}
}
