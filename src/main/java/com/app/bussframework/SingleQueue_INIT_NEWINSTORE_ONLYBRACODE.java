package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;


import com.app.util.UtilitiesFeqar;

import smarty.db.mysql;

public class SingleQueue_INIT_NEWINSTORE_ONLYBRACODE extends  SingleQueue_INIT_NEWINSTORE {
	public  SingleQueue_INIT_NEWINSTORE_ONLYBRACODE() {
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
		userDefinedCaption = "أسناد مباشر للمندوبين";
	
		userDefinedEditCols.add("c_id");
		userDefinedReadOnlyEditCols.add("c_id");
	}

	@Override
	public void initialize(HashMap smartyStateMap) {
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		super.initialize(smartyStateMap);
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder DropDownHtml= new StringBuilder("");
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select us_id , us_name from kbusers where us_rank = 'DLVAGENT' and us_active='Y' and us_branchcode=?");
			pst.setInt(1, currentBranch);
			rs = pst.executeQuery();
			DropDownHtml.append("<select class='select2_single' id='globalagentselect' name='globalagentselect'>");
			DropDownHtml.append("<option value=''></option> \n");
			while (rs.next()) {
				DropDownHtml.append("<option value='"+rs.getString("us_id")+"'>"+rs.getString("us_name")+"</option> \n");
			}
			DropDownHtml.append("</select> \n");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			try {conn.close();}catch(Exception e) {/*ignore*/}
		}
	
		userDefinedCaption = "<div class='row'><div clas='col-md-3 col-sm-2 col-xs-2'>" + this.userDefinedCaption + "</div>"
				+ "<div class='col-md-3 col-sm-4 col-xs-4'>" 
				+ "<input type='hidden' style='color:#424242;background-color:#E9E5E5;' value=0 id='smartyhiddenmultieditrowsno' name='smartyhiddenmultieditrowsno' />"
				+ "</div>"
				+ " <div class='col-md-3 col-sm-12 col-xs-12'>أختيار مندوب للكل "+DropDownHtml+" </div></div>";
	}
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		return super.doUpdate(rqs, false);
	}
}