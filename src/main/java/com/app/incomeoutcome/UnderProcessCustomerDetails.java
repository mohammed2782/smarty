package com.app.incomeoutcome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.app.util.Utilities;

import smarty.core.CoreMgr;
import smarty.db.mysql;

public class UnderProcessCustomerDetails extends CoreMgr {
	public UnderProcessCustomerDetails() {
		String buttonPintPerText = 
		"concat(c_created_date_only, '<a href=\"../../CustomerUnderDeliveryShipmentsPDFSRVL?c_created_date_only=',c_created_date_only,'&customerId=',c_mastercustid,'\" "
		+ "class=\"btn btn-sm btn-danger\">طباعة  <i class=\"fa fa-file-pdf-o fa-lg\"></i>') as date_group";
		MainSql = "select "+buttonPintPerText+", c_custreceiptnoori, c_receiptamt,"
				+ " c_receiptamt_usd, stp_name, c_shipment_cost, (c_receiptamt-c_shipment_cost) as net , c_created_date_only, "
				+" cdi_name, c_rcv_state "
				+ " from p_cases "
				+ " left join kbstep on stp_code = q_step and stp_stgcode = q_stage "
				+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
				+ " where c_mastercustid ={CUSTOMER_ACCOUNT_FIN_G}"
				+ " and q_stage not in ('CNCL', 'DLV') order by c_id ";
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("cdi_name");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("net");
		userDefinedGridCols.add("stp_name");
		userDefinedGroupByCol = "date_group";
		
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل د.ع");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
		userDefinedColLabel.put("c_shipment_cost", "تكلفة الشحن");
		userDefinedColLabel.put("c_rcv_state", "المحافظة");
		userDefinedColLabel.put("cdi_name", "المنطقة");
		userDefinedColLabel.put("net", "الصافي للعميل د.ع");
		userDefinedColLabel.put("stp_name", "الحالة الحالية");
		userDefinedLookups.put("c_rcv_state", "select st_code , st_name_ar from kbstate  order by st_order");
		userDefinedSumCols.add("c_receiptamt");
		userDefinedSumCols.add("c_shipment_cost");
	
		UserDefinedPageRows = 2000;
	}
	//CustomerUnderDeliveryShipmentsExcelSRVL
	@Override
	public void initialize(HashMap smartyStateMap){
		int customerId_G = Integer.parseInt(replaceVarsinString(" {CUSTOMER_ACCOUNT_FIN_G} ", arrayGlobals).trim());
		userDefinedCaption = "<a href=\"../../CustomerUnderDeliveryShipmentsExcelSRVL?customerId="+customerId_G+"\" "
				+ "class=\"btn btn-sm btn-success\">طباعة  <i class=\"fa fa-file-excel-o fa-lg\"></i></a>";
		userDefinedCaption += "</br><a href=\"../../CustomerUnderDeliveryShipmentsPDFSRVL?customerId="+customerId_G+"\" "
		+ "class=\"btn btn-sm btn-danger\">طباعة  <i class=\"fa fa-file-pdf-o fa-lg\"></i></a>";
		super.initialize(smartyStateMap);
	}
}
