package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

public class UpdateNewOnWayCasesBarcode extends CoreMgr {
	public UpdateNewOnWayCasesBarcode() {
		MainSql = "select p_cases.*, '' as edit, '' as netmoney from "
				+ " p_cases where c_rcv_state = 'BAS' and q_status = 'ACTV' and 1=0";

		canEdit = true;
		mainTable = "p_cases";
		keyCol = "c_id";
		displayMode = "GRIDEDIT";
		
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("c_id");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_rcv_hp");		
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("del");

		//userDefinedColLabel.put("c_assignedagent", "مندوب التوصيل");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_custid", "صاحب المحل");
		userDefinedColLabel.put("c_rcv_addr_rmk", "العنوان");
		userDefinedColLabel.put("c_rcv_hp", "الهاتف");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("q_action", "العملية");
		userDefinedColLabel.put("c_id", "رقم العملية");
		userDefinedColLabel.put("c_qty", "العدد");
		userDefinedColLabel.put("c_createddt", "التاريخ");
		userDefinedColLabel.put("del", " ");
		userDefinedColLabel.put("c_rcv_name", "اسم المستلم");
		userDefinedCaption = "نقل الى مرحلة داخل المخزن";

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

		userDefinedCaption = "<div class='col-md-3 col-sm-2 col-xs-2'>" + this.userDefinedCaption + "</div>"
				+ "<div class='col-md-3 col-sm-4 col-xs-4'>" 
				+ "<input type='hidden' style='color:#424242;background-color:#E9E5E5;' value=0 id='numberofrowsscanned' name='numberofrowsscanned' />"
				+ "</div>"
				+ " <div class=\"col-2\" style='align-self: center;'>"
				+ " <label for=\"allreceived_dlvagent\">إسناد المحدد للمندوب </label>"
				+ " <input id=\"push-toagent-directly\" name ='push-toagent-directly' class=\"form-check-input\"  type=\"checkbox\">" 
				+"  </div>"
				+ " <div class='col-md-3 col-sm-12 col-xs-12'>أختيار مندوب للكل "+DropDownHtml+" </div>";
	}
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		int userid = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		UtilitiesFeqar utf = new UtilitiesFeqar();
		keyVal = parseUpdateRqs(rqs);
		FlowUtils fu = new FlowUtils();
		boolean pushToAgentManifest = false;
		HashMap<String,String> queueColsToUpdate = new HashMap<String,String>();
		if (inputMap_ori.containsKey("push-toagent-directly") && inputMap_ori.get("push-toagent-directly")[0]!=null  
				&& inputMap_ori.get("push-toagent-directly")[0].equalsIgnoreCase("on")) {
			queueColsToUpdate.put("c_assignedagent", inputMap_ori.get("globalagentselect")[0]);
			pushToAgentManifest = true;
		}
		
		try{
			int caseId = 0;
			String stateCode = "";
			for(String key:inputMap_ori.keySet()) {
				if(inputMap_ori.get(key) != null && key != null &&  key.contains("c_id_row_")) {
					caseId = Integer.parseInt(inputMap_ori.get(key)[0]);
					stateCode = inputMap_ori.get("state_row_"+key.replace("c_id_row_", ""))[0];
					//System.out.println("stateCode--->"+stateCode);
					if (pushToAgentManifest && stateCode.equalsIgnoreCase("BGD")) {
						utf.updateRuralForSingleCase(conn, caseId );
						fu.MoveDecisionStepNext(conn, caseId, "DIRECT_AGENT_ASSGN", userid, queueColsToUpdate, 0, "INIT", "NEW_ONWAY", "");
						utf.calcShipmentProfit(conn, caseId, userstorecode);
					}else
						fu.MoveDecisionStepNext(conn, caseId, "PUSH_TOSTORE", userid, "INIT", "NEW_ONWAY", "");
				}
			}
			conn.commit();
		
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}	
		return "Saved";
	}
}

