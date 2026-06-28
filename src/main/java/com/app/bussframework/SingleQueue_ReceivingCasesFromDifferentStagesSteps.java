package com.app.bussframework;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import com.app.util.Utilities;

import smarty.core.CoreUtilities;
import smarty.db.mysql;

public class SingleQueue_ReceivingCasesFromDifferentStagesSteps extends SingleQueue_INIT_NEWINSTORE{
	private int userid_G;
	private int userstorecode_G;
	public SingleQueue_ReceivingCasesFromDifferentStagesSteps() {
		MainSql = "select p_cases.*, '' as edit, '' as netmoney  from "
			+ " p_cases where  1=0";
		userDefinedGridCols.clear();
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("c_branchcode");
		userDefinedGridCols.add("c_rcv_hp");	
		userDefinedGridCols.add("c_createddt");
		
		userDefinedGridCols.add("c_receiptamt");
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
		userDefinedColLabel.put("c_branchcode", "أنشأ في فرع");
		userDefinedEditCols.add("c_id");
		userDefinedReadOnlyEditCols.add("c_id");
		canFilter = false;
	}
	@Override
	public void initialize(HashMap smartyStateMap) {
		userid_G = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		userstorecode_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		super.initialize(smartyStateMap);
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder DropDownHtml= new StringBuilder("");
		StringBuilder DropDownLiaisonAgentHtml= new StringBuilder("");
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select us_id , us_name from kbusers where us_rank = 'DLVAGENT' and us_active='Y' and us_branchcode=?");
			pst.setInt(1, userstorecode_G);
			rs = pst.executeQuery();
			DropDownHtml.append("<select class='select2' id='globalagentselect' onchange='onGlobalDlvAgentSelect()' name='globalagentselect'>");
			DropDownHtml.append("<option value=''></option> \n");
			while (rs.next()) {
				DropDownHtml.append("<option value='"+rs.getString("us_id")+"'>"+rs.getString("us_name")+"</option> \n");
			}
			DropDownHtml.append("</select> \n");
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			pst = conn.prepareStatement("select us_id, us_name from kbusers where us_id in ("
					+ "select path_liaisonagent from kbpaths where path_frombranch =? and path_tobranch!=? "
					+ ")");
			pst.setInt(1, userstorecode_G);
			pst.setInt(2, userstorecode_G);
			rs = pst.executeQuery();
			DropDownLiaisonAgentHtml.append("<select class='select2'  id='globalLiaisonAgentSelect'"
					+ "  name='globalLiaisonAgentSelect' onchange='onGlobalLiaisonAgentSelect()'>");
			DropDownLiaisonAgentHtml.append("<option value=''></option> \n");
			while (rs.next()) {
				DropDownLiaisonAgentHtml.append("<option value='"+rs.getString("us_id")+"'>"+rs.getString("us_name")+"</option> \n");
			}
			DropDownLiaisonAgentHtml.append("</select> \n");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			try {conn.close();}catch(Exception e) {/*ignore*/}
		}
		this.userDefinedCaption = "أستلام شحنات عام ";
		userDefinedCaption = "<div class='row'><div class='col-md-3 col-sm-2 col-xs-2'>" + this.userDefinedCaption + "</div>"
				+ "<div class='col-md-4 col-sm-4 col-xs-4'>" 
				+ " <div class='col-md-6 col-sm-12 col-xs-12'>أختيار مندوب أرتباط "+DropDownLiaisonAgentHtml+" </div>"
				+ "<input type='hidden' style='color:#424242;background-color:#E9E5E5;' value=0 id='smartyhiddenmultieditrowsno' name='smartyhiddenmultieditrowsno' />"
				+ "</div>"
				+ " <div class='col-md-3 col-sm-12 col-xs-12'>أختيار مندوب توصيل للكل "+DropDownHtml+" </div></div>";
	}
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		
		try {
			keyVal = parseUpdateRqs(rqs);
			ArrayList<Integer> cIdList = new ArrayList<Integer>();
			HashMap<Integer, String> actionsMap = new HashMap<Integer, String>();
			HashMap<Integer, String> statesMap = new HashMap<Integer, String>();
			HashMap<Integer, String> stageMap = new HashMap<Integer, String>();
			HashMap<Integer, String> stepMap = new HashMap<Integer, String>();
			HashMap<Integer, String> whenShipmentWasScannedTimeStamp = new HashMap<Integer, String>();
			HashMap <Integer,Integer> dlvAgentsMap = new HashMap <Integer,Integer>();
			int id = 0;
			int rowsNo = 0;
			
			//first parse the data
			if (inputMap_ori.get("smartyhiddenmultieditrowsno") != null)
				rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
			
			int dlvAgentId  = 0;
			boolean assignedDlvAgentFound = false;
			try {
				dlvAgentId = Integer.parseInt(inputMap_ori.get("globalagentselect")[0]);
				if (dlvAgentId>0) {
					assignedDlvAgentFound = true;
				}
			}catch(Exception e) {
				assignedDlvAgentFound = false;
			}
			int liaisonAgent = 0;
			boolean assignedLiaisonAgentFound = false;
			try {
				liaisonAgent = Integer.parseInt(inputMap_ori.get("globalLiaisonAgentSelect")[0]);
				if (liaisonAgent>0) {
					assignedLiaisonAgentFound = true;
				}
			}catch(Exception e) {
				assignedLiaisonAgentFound = false;
			}
			if (assignedLiaisonAgentFound && assignedDlvAgentFound) {
				throw new Exception ("لا يمكن ان تحديد مندوب أستلام ومندوب إرتباط في نفس الوقت");
			}
			for (int i = 1; i <= rowsNo; i++) {
				if (inputMap_ori.get("q_action_smartyrow_" + i) != null
						&& inputMap_ori.get("q_action_smartyrow_" + i)[0].length() > 0) {
					id = Integer.parseInt(inputMap_ori.get("smarty_c_id_hidden_smartyrow_"+ i)[0]);
					actionsMap.put(id, inputMap_ori.get("q_action_smartyrow_" + i)[0]);
					stageMap.put(id, inputMap_ori.get("q_stage_smartyrow_" + i)[0]);
					stepMap.put(id, inputMap_ori.get("q_step_smartyrow_" + i)[0]);
					//statesMap.put(id, inputMap_ori.get("c_rcv_state_smartyrow_" + i)[0]);
					whenShipmentWasScannedTimeStamp.put(id, inputMap_ori.get("when_scanned_timestamp_smartyrow_" + i)[0]);
					cIdList.add(id);
				}
			}
			FlowUtils fu = new FlowUtils();
			HashMap<String, PathBean> onlyStatesWtihSinglePathMap = Utilities.getSinglePathStatesMap(conn,userstorecode_G);
			HashMap<Integer, String> newActionsMap = new HashMap<Integer, String>();
			HashMap <Integer,Integer> liaisonAgentsMap = new HashMap <Integer,Integer>();
			//loop through the cases and move to INSTORE, except the ones already in store
			ArrayList<Integer> casesToBeAssignedToMoveForward= new ArrayList<Integer>();
			for (int cid : cIdList) {
//				
//				updateCasesAsScannedBarCode(
//				conn, cid, whenShipmentWasScannedTimeStamp.get(cid), stageMap.get(cid), stepMap.get(cid));
				
				// except the ones in store currently , push them to instore
				if (!stepMap.get(cid).equalsIgnoreCase("NEWINSTORE")) {
					fu.MoveDecisionStepNext(conn, cid, actionsMap.get(cid), userid_G, userstorecode_G,
							stageMap.get(cid), stepMap.get(cid), "");
				}
				// Once every thing is in store then we kick
				// WATCH OUT THIS IS ONLY FOR CASES TO BE ASSIGNED TO DLV AGENTS
				if (assignedDlvAgentFound) {
					newActionsMap.put(cid, "ASSGN_AGENT");
					casesToBeAssignedToMoveForward.add(cid);
					dlvAgentsMap.put(cid, dlvAgentId);
				}else { 
					newActionsMap.put(cid, "ASSIGN_LIASIONAGT");
					if(assignedLiaisonAgentFound) {
						casesToBeAssignedToMoveForward.add(cid);
						liaisonAgentsMap.put(cid, liaisonAgent);
					}else {// check if auto assign can happen if there is one path then pass go forward
						statesMap.put(cid, ut.getSinglCaseInformation(conn, cid+"").getState());
						if(onlyStatesWtihSinglePathMap.containsKey(statesMap.get(cid))) {
							liaisonAgentsMap.put(cid,onlyStatesWtihSinglePathMap.get(statesMap.get(cid)).getLiasionId());
							casesToBeAssignedToMoveForward.add(cid);
						}
					}
				}
			}
			// if assigned to dlv agent, or assigned to liaison agent
			if(casesToBeAssignedToMoveForward !=null && !casesToBeAssignedToMoveForward.isEmpty() ) {
				QueueActionsParamsBean queueActionsParamsBean = new QueueActionsParamsBean ();
				if (dlvAgentsMap!=null && assignedDlvAgentFound) {
					queueActionsParamsBean.setDlvAgentMap(dlvAgentsMap);
				}else if (liaisonAgentsMap!=null && liaisonAgentsMap.size()>0) {
					queueActionsParamsBean.setLiaisonAgentsMap(liaisonAgentsMap);
				}
				doUpdateStepActions(conn, 
						casesToBeAssignedToMoveForward,
						newActionsMap,
						queueActionsParamsBean,
						userid_G,
						userstorecode_G,
						"INIT",
						"NEWINSTORE",
						new  HashMap<Integer, String>());
			}
			
			conn.commit();
		}catch (Exception e) {
			e.printStackTrace();
			try {conn.rollback();}catch(Exception eRoll) {}
			return "حصل خطأ " + e.getMessage();
		}
		return "تم الحفظ";
	}
	
//	private void updateCasesAsScannedBarCode (Connection a_conn, int a_caseId,
//			String a_shipmentScanningDateTime, String a_stage, String a_step  ) throws Exception {
//		PreparedStatement pst = null;
//		
//		try {
//			pst = conn.prepareStatement("insert into p_cases_scanned_timestamp"
//					+ " (cst_caseid		, cst_timestamp_when_scanned, cst_q_stage, cst_q_step, cst_inbranch, "
//					+ " cst_whichscreen , cst_createdby) "
//					+ "values("+CoreUtilities.getQuestionMarks(7)+")");
//			pst.setInt(1, a_caseId);
//			pst.setString(2, a_shipmentScanningDateTime);
//			pst.setString(3, a_stage);
//			pst.setString(4, a_step);
//			pst.setInt(5, userstorecode_G);
//			pst.setString(6, "ReceiveAnyShipment");
//			pst.setInt(7, userid_G);
//			pst.executeUpdate();
//		}catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}finally {
//			try {pst.close();}catch(Exception e) {}
//		}
//	}
}
