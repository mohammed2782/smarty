package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.db.mysql;

public class DirectUpdateToDeliveredOnlyBarCode extends  SingleQueue_AGENTOP{
	public  DirectUpdateToDeliveredOnlyBarCode() {
		MainSql = "select p_cases.*, '' as edit, '' as netmoney, '' as fakecol  from "
				+ " p_cases where c_rcv_state = 'BAS' and q_status = 'ACTV' and 1=0";
	
		canEdit = true;
		mainTable = "p_cases";
		keyCol = "c_id";
		displayMode = "GRIDEDIT";
		canFilter = false;
		userDefinedGridCols.clear();
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("fakecol");
//		userDefinedGridCols.add("c_id");
//		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_rcv_hp");		
//		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("del");
	
		//userDefinedColLabel.put("c_assignedagent", "مندوب التوصيل");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_custid", "صاحب المحل");
		userDefinedColLabel.put("c_rcv_addr_rmk", "العنوان");
		userDefinedColLabel.put("fakecol", "الفرع صاحب الوصل");
		userDefinedColLabel.put("c_rcv_hp", "الهاتف");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("q_action", "العملية");
//		userDefinedColLabel.put("c_id", "رقم العملية");
//		userDefinedColLabel.put("c_qty", "العدد");
		userDefinedColLabel.put("c_createddt", "التاريخ");
		userDefinedColLabel.put("del", " ");
//		userDefinedColLabel.put("c_rcv_name", "اسم المستلم");
		userDefinedCaption = "تحديث وصولات واصل فقط";
	
		userDefinedEditCols.add("c_id");
		userDefinedReadOnlyEditCols.add("c_id");
	}

	@Override
	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		MainSql = "select p_cases.*, '' as edit, '' as netmoney, '' as fakecol  from "
				+ " p_cases where c_rcv_state = 'BAS' and q_status = 'ACTV' and 1=0";
		userDefinedGroupByCol = null;
		userDefinedGroupColsOrderBy = null;
		userDefinedGridCols.clear();
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("fakecol");
//		userDefinedGridCols.add("c_id");
//		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_rcv_hp");		
//		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("del");
		userDefinedCaption = "<div class='row'><div clas='col-md-3 col-sm-2 col-xs-2'>" + this.userDefinedCaption + "</div>"
				+ "<div class='col-md-3 col-sm-4 col-xs-4'>" 
				+ "<input type='hidden' style='color:#424242;background-color:#E9E5E5;' value=0 id='smartyhiddenmultieditrowsno' name='smartyhiddenmultieditrowsno' />"
				+ "</div>"
				+ " </div>";
	}
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		return super.doUpdate(rqs, false);
	}
}