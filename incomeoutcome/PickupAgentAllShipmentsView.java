package com.app.incomeoutcome;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class PickupAgentAllShipmentsView  extends CoreMgr{
	public PickupAgentAllShipmentsView () {
		MainSql ="select stp_name,q_step, c_id 		 , c_custid     , c_custhp		 , "
				  + " c_rcv_name 	 , c_rcv_hp      , c_rcv_state		 , "
				  + " c_rural        , concat (ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk, c_rmk     		 , "
				  + " c_qty		     , c_receiptamt  , c_shipment_cost   , "
				  + " c_sendmoney    , c_fragile	 , c_bringitemsback  , "
				  + " c_branchcode	 , c_weight		 , c_custreceiptnoori, "
				  + " c_settled, q_stage "
				  + " from p_cases "
				  + " join p_queue on c_id = q_caseid and q_status !='CLS'"
				  + " join kbstep on stp_code= q_step "
				  + " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
				+ " where c_pickupagent ={pickupAgentAcct} order by c_id desc ";
		
		canFilter = true;
//userDefinedGridCols.add("cm_custid");
		
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
		userDefinedGridCols.add("c_settled");
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		userDefinedColLabel.put("custname", "إسم العميل");
		userDefinedColLabel.put("c_custid", "إسم العميل");
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
		userDefinedColLabel.put("c_settled","تم التحاسب ؟");
		
		//userDefinedFilterCols.add("cm_custid");
		//userDefinedLookups.put("cm_custid", "select c_id , c_name from kbcustomers order by c_name asc");
		userDefinedFilterCols.add("c_rcv_hp");
		userDefinedFilterCols.add("c_id");
		userDefinedFilterCols.add("c_custreceiptnoori");

		UserDefinedPageRows = 50;
		
		//userDefinedEditMockUpCols.put("custprimaryphone", "(select cm_cust_hp from p_casesmaster where cm_id = c_cmid)");
		userDefinedLookups.put("c_branchcode", "select store_code , store_name from kbstores where store_deleted='N'");
		userDefinedLookups.put("custname", "select c_id , c_name from kbcustomers order by c_name ASC");
		userDefinedLookups.put("custprimaryphone", "!select c_primaryHP as ph, c_primaryHP from kbcustomers where c_id = '{custname}'");
		userDefinedLookups.put("c_rcv_state", "select st_code , st_name_ar from kbstate where st_active='Y' order by st_order");
		userDefinedLookups.put("c_fragile", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_rural", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_bringitemsback", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userModifyTD.put("c_settled", "modifySettled({c_settled},{q_stage},{q_step},{stp_name})");
		
		myhtmlmgr.tableClass = "table table-striped  table-bordered green_table";
	}
	
	public String modifySettled(HashMap<String,String>hashy) {
		String desc = hashy.get("stp_name");
		String color = "";
		if (hashy.get("q_stage").equalsIgnoreCase("cncl")){
			 
			 color = "background-color:red;color:white;";
		}else if(hashy.get("q_step").equalsIgnoreCase("delivered")) {
			if (hashy.get("c_settled").equalsIgnoreCase("FULL")) {
				desc += "</br>تم التحاسب";
				color = "background-color:green;color:white;";
			}else {
				desc += "</br>لم يتم التحاسب";
				color = "background-color:blue;color:white;";
			}
		}else {
			;
		}
		String html = "<td style='"+color+"'>"+desc;
		
		html+= "</td>";
		return html;
	}
}
