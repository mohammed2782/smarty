package com.app.bussframework;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import com.app.cases.CaseInformation;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

import smarty.core.CoreMgr;
import smarty.db.mysql;

public class DlvAgentManifestUpdateablePopUp extends CoreMgr{
	private int m_manifestId;
	private String m_userRank;
	int i = 1;
	String trStyleHidden = "display: none; padding-top: 5px;";
	protected HashMap<String, QueueActionsParamsBean> SingleQueueActionsParamsBean = new HashMap<String,QueueActionsParamsBean>();
	protected HashMap<String, ArrayList<Integer>> singleQueueCidList = new HashMap<String, ArrayList<Integer>>();
	protected HashMap<String,HashMap<Integer, String>> SingleQueueActionsMap = new HashMap<String,HashMap<Integer, String>>();
	protected HashMap<Integer, String> actionsMap = new HashMap<Integer, String>();
	protected ArrayList<Integer> cidList = new ArrayList<Integer>();
	private HashMap <Integer , Double> newReceiptAmtMap = new HashMap <Integer , Double> ();
	private HashMap <Integer , String> rtnReasonMap = new HashMap <Integer , String>();
	private HashMap <Integer , String> postponedOptionsMap  = new HashMap <Integer , String>();
	private HashMap <Integer , String> postponedDateTimeToMap = new HashMap <Integer , String>() ;
	HashMap<Integer, String> casesRmkMap = new HashMap<Integer, String>();
	private HashMap <Integer, Integer> partialQtyReturnMap = new HashMap <Integer,Integer>();
	Utilities ut = new Utilities();
	HashMap<String, String> returnReasonsList = new HashMap<String, String>();
	HashMap<String, String> potponedOptionsMap = new HashMap<String, String>();
	HashMap<String,StageBean> stagesMap = new  HashMap<String,StageBean>();
	public DlvAgentManifestUpdateablePopUp(int a_manifestId, String a_userRank) {
		m_manifestId = a_manifestId;
		m_userRank = a_userRank;
		Connection conn1 = null;
		try {
			conn1 = mysql.getConn();
			returnReasonsList = ut.getRtnReasons(conn1);
			potponedOptionsMap = ut.getPostponedOptionsMap(conn1);
			stagesMap = ut.getStagesStepsActionPerUser(conn1, a_userRank);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn1.close();
			} catch (Exception e) {
			}
		}
		MainSql = "select '' q_action , q_stage, q_step, concat(stg_name,'-', stp_name) as stagestep,  cust_phone1, cust_name, c_id,"
				+ "  c_rcv_name, c_rcv_hp1, c_rmk, stp_color, stp_fontcolor, "
				+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr , c_receiptamt, c_custreceiptnoori"
				+ " from p_cases "
				+ " join kbstage on q_stage = stg_code "
				+ " join kbstep on q_step = stp_code and q_stage = stp_stgcode "
				+ " left join kbcustomers on cust_id = c_custid "
				+ " left join kbstate on st_code = c_rcv_state and st_branch = {userstorecode}"
				+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
				+ " where c_dlvagent_manifestid="+m_manifestId+" order by c_rcv_district , c_id";
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("cust_phone1");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("stagestep");
		userDefinedGridCols.add("q_action");
		
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("addr", "العنوان");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف المستلم");
		userDefinedColLabel.put("cust_name", "المتجر");
		userDefinedColLabel.put("cust_phone1", "هاتف المتجر");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("stagestep", " ");
		userDefinedColLabel.put("q_action", " ");
		
		mainTable = "p_cases";
		canEdit = true;
		keyCol = "c_id";
		
		displayMode = "GRIDEDIT";
		
		userDefinedLookups.put("q_action", "!select stpd_code, stpd_desc from kbstep_decision where "
				+ " stpd_stpid in (select stp_id from kbstep where stp_code='{q_step}' and stp_stgcode='{q_stage}') and stpd_onlymbapp='N'"
				+ " and stpd_forrank like '%{userRank}%' ");
		
		userDefinedEditCols.add("q_action");
		userDefinedEditCols.add("c_rmk");
		userModifyTD.put("q_action", "modifyAction({q_stage}, {q_step})");
		userModifyTD.put("stagestep", "modifyStageStepName({stagestep}, {stp_color}, {stp_fontcolor})");
		
		UserDefinedPageRows = 200;
	}
	public String modifyStageStepName(HashMap<String, String> hashy) {
		StringBuilder sb = new StringBuilder("<td style='background-color:"+hashy.get("stp_color")+"; color:"+hashy.get("stp_fontcolor")+"'>");
		sb.append(hashy.get("stagestep"));
		sb.append("</td>");
		return sb.toString();
	}
	
	public String modifyAction(HashMap<String, String> hashy) {
		StringBuilder sb = new StringBuilder("<td ><table class='col-12'><tr><td>");
		sb.append("<select class=\"form-control\" aria-hidden=\"true\" "
				+ " style=\"min-width: 120px; background-color:white;\"  onchange=\"change_q_actionColor(this, '" + i
				+ "')\" " + " id='q_action_smartyrow_" + i + "' name='q_action_smartyrow_" + i + "' "
				+ "style=\"width: 100%\"> " + "<option value=''></option>");
		//System.out.println(colMapValues.get("q_action"));

		ArrayList<StepsDecisionsBean> lookupsmap = stagesMap.get(hashy.get("q_stage")).getStepsMap().get(hashy.get("q_step")).getDescisionsList();
		String selectedItem = "";
		if (lookupsmap != null && !lookupsmap.isEmpty()) {
			for (StepsDecisionsBean stepsDecisionsBean : lookupsmap) {
				selectedItem = "";
				sb.append("<option value='" + stepsDecisionsBean.getActionCode() + "' " + selectedItem + ">" + stepsDecisionsBean.getActionDesc()
						+ "</option> \n");
			
			}
		}
		sb.append("</select></td></tr>");
		// RTN Reasons
		sb.append("<tr id='trreturnreasons_" + i + "' style='" + trStyleHidden + "'><td>");
		sb.append("<select class='form-control'  id='c_rtnreason_smartyrow_" + i + "' name='c_rtnreason_smartyrow_" + i
				+ "' "
				+ "style='text-align:right; background-color:rgb(243 102 134); padding: 0 10px 0 10px;  color: white; border: 1px solid #7dc6dd;'> "
				+ "<option value='' disabled selected>سبب الراجع</option>");
		for (String rtncode : returnReasonsList.keySet()) {
			sb.append("<option value='" + rtncode + "' >" + returnReasonsList.get(rtncode) + "</option> \n");
		}
		sb.append("</td></tr>");

		// Postponed options
		sb.append("<tr id ='trq_postponedto_smartyrow_" + i + "' style='" + trStyleHidden + "'>"
				+ "<td><input type='datetime-local' id='q_postponedto_smartyrow_" + i
				+ "' name='q_postponedto_smartyrow_" + i + "'>");
		sb.append("<select class='form-control'  id='q_postponedoption_smartyrow_" + i
				+ "' name='q_postponedoption_smartyrow_" + i + "' "
				+ "style='text-align:right; background-color:#3b8281; padding: 0 10px 0 10px;  color: white; border: 1px solid #7dc6dd;'> "
				+ "<option value='' disabled selected>سبب التأجيل</option>");
		for (String post : potponedOptionsMap.keySet()) {
			sb.append("<option value='" + post + "' >" + potponedOptionsMap.get(post) + "</option> \n");
		}
		sb.append("</td></tr>");
		// New receipt amount
		sb.append("<tr id ='trnew_receiptamtrtn_smartyrow_" + i + "' style='" + trStyleHidden + "' ><td>");
		sb.append("<input type='number' style='text-align: right;" + "    background-color: #1ea57f;"
				+ "    color: white;" + "    width: 7em;"
				+ " line-height: 20px; margin-right:5px;' placeholder='مبلغ الوصل'  name= 'new_receiptamtrtn_smartyrow_"
				+ i + "' " + " id = 'new_receiptamtrtn_smartyrow_" + i + "' ></td>");
		sb.append("</tr><tr><td><input type='number' style='text-align: right;" + "    background-color: #1ea57f;"
				+ "    color: white;" + "    width: 8em;"
				+ " display:none; line-height: 20px; margin-top:3px;' placeholder='القطع الراجعة' name= 'rtn_qty_smartyrow_"
				+ i + "' " + " id = 'rtn_qty_smartyrow_" + i + "' minval =1 >");
		sb.append("</td></tr>");
		sb.append("</table></td>");
		i++;
		return sb.toString();
	}

	
	
	public void parseInputs(HttpServletRequest rqs) {
		
		keyVal = parseUpdateRqs(rqs);
		int rowsNo = 0;
		if (inputMap_ori.get("smartyhiddenmultieditrowsno") != null)
			rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
		String action = "";
		int id = 0;
		for (int i = 1; i <= rowsNo; i++) {
			action = inputMap_ori.get("q_action_smartyrow_" + i)[0];
			
			if (action != null && !action.trim().isEmpty() && !action.trim().equalsIgnoreCase("")
					&& !action.trim().equalsIgnoreCase("null")) {
				id = Integer.parseInt(inputMap_ori.get(hiddenKeyCol + "_smartyrow_" + i)[0]);
				if (inputMap_ori.get("c_rmk_smartyrow_" + i) !=null && inputMap_ori.get("c_rmk_smartyrow_" + i)[0] !=null
						&& !inputMap_ori.get("c_rmk_smartyrow_" + i)[0].isEmpty()){
							casesRmkMap.put(id, inputMap_ori.get("c_rmk_smartyrow_" + i)[0]);
						}
				actionsMap.put(id, action);
				if (action.equalsIgnoreCase("SUCS_DLV_CHANGEAMT") || action.equalsIgnoreCase("PART_SUCC")) {
					if (inputMap_ori.get("new_receiptamtrtn_smartyrow_" + i)[0] != null
							&& inputMap_ori.get("new_receiptamtrtn_smartyrow_" + i)[0].length() > 0)
						newReceiptAmtMap.put(id,
								Double.parseDouble(inputMap_ori.get("new_receiptamtrtn_smartyrow_" + i)[0]));
					if (action.equalsIgnoreCase("PART_SUCC")) {
						if (inputMap_ori.get("rtn_qty_smartyrow_" + i)[0] != null
								&& inputMap_ori.get("rtn_qty_smartyrow_" + i)[0].length() > 0)
							partialQtyReturnMap.put(id,
									Integer.parseInt(inputMap_ori.get("rtn_qty_smartyrow_" + i)[0]));
					}
				} else if (action.equalsIgnoreCase("RTN_WTIHAGENT") || action.equalsIgnoreCase("RTN_TOSTORE")) {
					rtnReasonMap.put(id, inputMap_ori.get("c_rtnreason_smartyrow_" + i)[0]);
				} else if (action.equalsIgnoreCase("POSTPONED")) {
					postponedOptionsMap.put(id, inputMap_ori.get("q_postponedoption_smartyrow_" + i)[0]);
					postponedDateTimeToMap.put(id, inputMap_ori.get("q_postponedto_smartyrow_" + i)[0]);
				}
				cidList.add(id);
			}

		}
		/*queueActionsParamsBean.setNewReceiptAmtMap(newReceiptsAmtMap);
		queueActionsParamsBean.setPostponedDateTimeToMap(postponedToMap);
		queueActionsParamsBean.setPostponedOptionsMap(postponedOptionMap);
		queueActionsParamsBean.setRtnReasonMap(rtnReasonMap);
		queueActionsParamsBean.setPartialQtyReturnMap(partialQtyReturnMap);*/
	}
	
	@Override
	public String doUpdate(HttpServletRequest rqs, boolean commit) {
	
		int userid = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		Connection conn = null;
		try {
			parseInputs(rqs);
			conn = mysql.getConn();
			SingleQueueFactory sqf = new SingleQueueFactory();
			for (int cid : cidList) {
				CaseInformation ci = ut.getSingleCaseInfo2(conn, cid);
				if (ci.getDlvAgentPmtId()==0 && ci.getPickUpAgentPmtId()==0 && ci.getAgentRtnId()==0
						&& ci.getSenderPmtId() == 0 && ci.getCustReturnId() ==0 && ci.getPickupAgentRtnId()==0) {
						SingleQueue sq = sqf.getSingleQueuObj(ci.getStageCode(), ci.getStepCode());
						//cidQList
						ArrayList<Integer>qcidList = new  ArrayList<Integer>();
						qcidList.add(cid);
						sq.setcIdList(qcidList);
						
						sq.setActionsMap( new HashMap<Integer,String>(){
							{
								put(cid,actionsMap.get(cid));
							}
						});
						
						QueueActionsParamsBean  queueActionsParamsBean = new QueueActionsParamsBean();
						queueActionsParamsBean.setNewReceiptAmtIqdMap(new HashMap<Integer, Double>() {
							{
								put(cid, newReceiptAmtMap.get(cid));
							}
						});
						queueActionsParamsBean.setPostponedDateTimeToMap(new HashMap<Integer, String>() {
							{
								put(cid, postponedDateTimeToMap.get(cid));
							}
						});
						queueActionsParamsBean.setPostponedOptionsMap(new HashMap<Integer, String>() {
							{
								put(cid, postponedOptionsMap.get(cid));
							}
						});
						queueActionsParamsBean.setRtnReasonMap(new HashMap<Integer, String>() {
							{
								put(cid, rtnReasonMap.get(cid));
							}
						});
						queueActionsParamsBean.setPartialQtyReturnMap(new HashMap<Integer, Integer>() {
							{
								put(cid, partialQtyReturnMap.get(cid));
							}
						});
						queueActionsParamsBean.setCasesRmk(new HashMap<Integer, String>() {
							{
								put(cid, casesRmkMap.get(cid));
							}
						});
						sq.setQueueActionsParamsBean(queueActionsParamsBean);
						
						sq.processData(conn, userid, 0, ci.getStageCode(),  ci.getStepCode(),new HashMap<Integer, String>());
				}
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception eRoll) {
			}
			return "Error";
		}
		return "Saved";
	}

	
}
