package com.app.reports;
import smarty.core.CoreMgr;
public class profit_dtls extends CoreMgr {
	public profit_dtls(){
		MainSql = "select * from mbilldtl_out where mbd_bill_id = {sellbillid_rpt}";
		//userDefinedGridCols.add("mbd_settled");
		userDefinedGridCols.add("mbd_profit");
		userDefinedGridCols.add("mbd_totalamount");
		userDefinedGridCols.add("mbd_quantity");
		userDefinedGridCols.add("mbd_price");
		userDefinedGridCols.add("mbd_itemid");
		
		userDefinedColLabel.put("mbd_profit", "الربح");
		userDefinedColLabel.put("mbd_settled", "تم دفع المبلغ كاملا");
		userDefinedColLabel.put("mbd_totalamount", "المبلغ الكلي");
		userDefinedColLabel.put("mbd_quantity", "الكمية");
		userDefinedColLabel.put("mbd_price", "سعر البيع");
		userDefinedColLabel.put("mbd_itemid", "المنتج");
		
		userDefinedLookups.put("mbd_itemid", "select g_id , gname From kbgoods order by gname asc" );
		userDefinedCaption = "تفاصيل";
		
	}
}

