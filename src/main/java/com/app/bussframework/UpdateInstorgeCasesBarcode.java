package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;


import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

import smarty.core.CoreMgr;
import smarty.db.mysql;

public class UpdateInstorgeCasesBarcode extends CoreMgr {
	public UpdateInstorgeCasesBarcode() {
		MainSql = "select p_cases.*, '' as edit, '' as netmoney from "
				+ " p_cases where c_rcv_state = 'BAS' and q_status = 'ACTV' and 1=0";

		canEdit = true;
		mainTable = "p_cases";
		keyCol = "c_id";
		displayMode = "GRIDEDIT";

		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_assignedagent");

		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("c_rcv_hp");
		userDefinedGridCols.add("c_receiptamt");

		userDefinedGridCols.add("del");

		userDefinedColLabel.put("c_assignedagent", "مندوب التوصيل");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_custid", "صاحب المحل");
		userDefinedColLabel.put("c_rcv_addr_rmk", "العنوان");
		userDefinedColLabel.put("c_rcv_hp", "الهاتف");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("q_action", "العملية");
		userDefinedColLabel.put("c_id", "رقم العملية");
		userDefinedColLabel.put("del", " ");
		userDefinedCaption = "اسناد المندوبين في المخزن";

		userDefinedEditCols.add("c_id");
		userDefinedReadOnlyEditCols.add("c_id");
		userDefinedEditCols.add("mbo_id");
		userDefinedReadOnlyEditCols.add("mbo_id");

	}

	@Override
	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);

		StringBuilder DropDownAgentHtml = new StringBuilder("");
		StringBuilder DropDownDistructHtml = new StringBuilder("");
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		int userstorecode_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement(
					"select us_id , us_name from kbusers where us_rank = 'DLVAGENT' and us_active = 'Y'"
					+ " and us_branchcode = ?  order by us_name");
			pst.setInt(1, userstorecode_G);
			rs = pst.executeQuery();
			DropDownAgentHtml.append(
					"<select  style='height:33px;' class=' form-control select2' name='globalagentselect' id='globalagentselect' onchange='doGlobalSelectForAgents()' required>");
			DropDownAgentHtml.append("<option value=''></option> \n");
			while (rs.next()) {
				DropDownAgentHtml.append(
						"<option value='" + rs.getString("us_id") + "'>" + rs.getString("us_name") + "</option> \n");
			}
			DropDownAgentHtml.append("</select> \n");

			try {rs.close();} catch (Exception e) {/* ignore */}
			try {pst.close();} catch (Exception e) {/* ignore */}

			
			pst = conn.prepareStatement("select cdi_id, cdi_name From"
					+ " kbcity_district where cdi_stcode in"
					+ " (select branch_state from kbbranches where branch_id= ?)");
			pst.setInt(1, userstorecode_G);
			rs = pst.executeQuery();
			DropDownDistructHtml.append(
					"<select  style='height:33px;' class=' form-control select2' name='globaldistructselect' id='globaldistructselect' onchange='doGlobalSelectForDistructs()' required>");
			DropDownDistructHtml.append("<option value=''></option> \n");
			while (rs.next()) {
				DropDownDistructHtml.append("<option value='" + rs.getString("cdi_id") + "'>"
						+ rs.getString("cdi_name") + "</option> \n");
			}
			DropDownDistructHtml.append("</select> \n");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				conn.close();
			} catch (Exception e) {
				/* ignore */}
		}

		userDefinedCaption = "<div class='col-md-3 col-sm-2 col-xs-2'>" + this.userDefinedCaption + "</div><br/>"
				+ "<div class = 'row'>"
				+ "<div class='col-md-4 col-sm-4 col-xs-12'><label>مندوب التوصيل</label>"
				+ DropDownAgentHtml + " </div>"
				+ "<div class='col-md-4 col-sm-4 col-xs-12'><label>المنطقة</label>"
				+ DropDownDistructHtml + " "
				+ "<input type='hidden' style='color:#424242;background-color:#E9E5E5;' value=0 id='numberofrowsscanned' name='numberofrowsscanned' />"
				+ "</div></div><br/>";
	}

	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		PreparedStatement pstUpdatcases = null;
		keyVal = parseUpdateRqs(rqs);
		FlowUtils fu = new FlowUtils();
		boolean everyThingIsOk = true;
		int rowsNo =0;
		String globalAgent = "", globalDistruct = "";
		UtilitiesFeqar utf = new UtilitiesFeqar();
//		
//		 for(String key:inputMap_ori.keySet())
//		  System.out.println("key = "+key+" 	value = "+inputMap_ori.get(key)[0]);
//		 
//		
		rowsNo = Integer.parseInt(inputMap_ori.get("numberofrowsscanned")[0]);
		
		if(inputMap_ori.get("globaldistructselect")!=null && inputMap_ori.get("globalagentselect")!=null
				&& inputMap_ori.get("globaldistructselect")[0]!=null && inputMap_ori.get("globalagentselect")[0]!=null
					&& !inputMap_ori.get("globaldistructselect")[0].isEmpty() && !inputMap_ori.get("globalagentselect")[0].isEmpty()) {
			globalAgent    = inputMap_ori.get("globalagentselect")[0];
			globalDistruct = inputMap_ori.get("globaldistructselect")[0];
		}

		ArrayList<Integer> cIdList= new ArrayList<Integer>();
		int id = 0;
		for (int i=1 ; i<=rowsNo ; i++){
			if (inputMap_ori.containsKey("c_id_row_"+i)) {
				id =Integer.parseInt(inputMap_ori.get("c_id_row_"+i)[0]);
				cIdList.add(id);
			}
		}
		try{
			int userid_G = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
			int userstorecode_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
			pstUpdatcases = conn.prepareStatement("update p_cases set  c_rcv_district=?, c_assignedagent=? where c_id=?");
			for (int cid :cIdList){
				everyThingIsOk = true;
				pstUpdatcases.setString(1, globalDistruct);
				pstUpdatcases.setString(2, globalAgent);
				pstUpdatcases.setInt(3, cid);
				pstUpdatcases.executeUpdate();
				pstUpdatcases.clearParameters();
				
				if(!globalDistruct.isEmpty()&&!globalAgent.isEmpty()&&globalDistruct!=null&&globalAgent!=null) {
					everyThingIsOk = true;
				}else {
					everyThingIsOk = false;
				}	
				if (everyThingIsOk) {
					fu.MoveDecisionStepNext(conn, cid, "ASSGN_AGENT" , userid_G , 
							 "INIT" , "NEWINSTORE" , "");
					utf.calcShipmentProfit(conn, cid, userstorecode_G);
				}
			}
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}finally{
			try{pstUpdatcases.close();}catch(Exception e){}
		}
				
		return "Saved";
	}
}

