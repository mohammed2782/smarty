package com.app.incomeoutcome;

import com.app.core.CoreMgr;

public class AllAgentsShipmentsViewBarCode extends CoreMgr {
	public AllAgentsShipmentsViewBarCode () {
		MainSql = "select c_id 		 , c_custid     , c_custhp		 , "
				  + " c_rcv_name 	 , c_rcv_hp      , c_rcv_state		 , "
				  + " c_rural        , c_rcv_addr_rmk, c_rmk     		 , "
				  + " c_qty		     , c_receiptamt  , c_shipment_cost   , "
				  + " c_sendmoney    , c_fragile	 , c_bringitemsback  , "
				  + " c_branchcode	 , c_weight		 , c_custreceiptnoori  "
				  + " from p_cases "
				+ " where c_assignedagent ={agentAcctBarCode} order by c_id desc ";
		
		myhtmlmgr.tableClass = "table table-striped  table-bordered orange_table";
	
		
		canFilter = true;
userDefinedGridCols.add("c_custid");
		
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_rcv_hp");
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("c_rural");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("c_sendmoney");
		userDefinedGridCols.add("c_fragile");
		userDefinedGridCols.add("c_bringitemsback");
		
		userDefinedGridCols.add("c_weight");
		userDefinedGridCols.add("c_custreceiptnoori");
	
		userDefinedColLabel.put("custname", "إسم صاحب المحل");
		userDefinedColLabel.put("c_custid", "إسم صاحب المحل");
		userDefinedColLabel.put("c_rcv_name","المستلم");
		userDefinedColLabel.put("c_rcv_hp", "هاتف المستلم");
		userDefinedColLabel.put("c_rcv_state", "المدينه");
		userDefinedColLabel.put("c_rcv_addr_rmk", "تفاصيل العنوان");
		userDefinedColLabel.put("c_rural", "أطراف");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("c_qty", "عدد");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("c_shipment_cost","مبلغ الشحن");
		userDefinedColLabel.put("c_sendmoney","مبلغ مرسل");
		userDefinedColLabel.put("c_fragile","قابل للكسر");
		userDefinedColLabel.put("c_bringitemsback","جلب  بضاعه");
		userDefinedColLabel.put("c_branchcode","مخزن");
		userDefinedColLabel.put("c_weight","الوزن");
		userDefinedColLabel.put("c_custreceiptnoori","رقم الوصل");
		userDefinedColLabel.put("c_id","رقم الشحنه");
		userDefinedColLabel.put("c_id","رقم الشحنه");
		
		userDefinedFilterCols.add("c_custid");
		userDefinedLookups.put("c_custid", "select c_id , c_name from kbcustomers order by c_name asc");
		userDefinedFilterCols.add("c_rcv_hp");
		userDefinedFilterCols.add("c_id");
		userDefinedFilterCols.add("c_custreceiptnoori");

		UserDefinedPageRows = 10000;
		
		//userDefinedEditMockUpCols.put("custprimaryphone", "(select cm_cust_hp from p_casesmaster where cm_id = c_cmid)");
		userDefinedLookups.put("c_branchcode", "select store_code , store_name from kbstores where store_deleted='N'");
		userDefinedLookups.put("custname", "select c_id , c_name from kbcustomers order by c_name ASC");
		userDefinedLookups.put("custprimaryphone", "!select c_primaryHP as ph, c_primaryHP from kbcustomers where c_id = '{custname}'");
		userDefinedLookups.put("c_rcv_state", "select st_code , st_name_ar from kbstate where st_active='Y' order by st_order");
		userDefinedLookups.put("c_fragile", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_rural", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		//userDefinedLookups.put("c_bringitemsback", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		
	}
}
