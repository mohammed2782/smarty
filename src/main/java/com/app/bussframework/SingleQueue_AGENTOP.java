package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.financials.StandardFinCurrency;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;
import com.app.util.UtilitiesNafie;

import smarty.db.mysql;

public class SingleQueue_AGENTOP extends SingleQueue {
	int i = 1;
	String trStyleHidden = "display: none; padding-top: 5px;";
	Utilities ut = new Utilities();
	HashMap<String, String> returnReasonsList = new HashMap<String, String>();
	HashMap<String, String> potponedOptionsMap = new HashMap<String, String>();

	private StringBuilder agentsList = new StringBuilder();
	
	public SingleQueue_AGENTOP() {
		Connection conn1 = null;
		try {
			conn1 = mysql.getConn();
			returnReasonsList = ut.getRtnReasons(conn1);
			potponedOptionsMap = ut.getPostponedOptionsMap(conn1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn1.close();
			} catch (Exception e) {
			}
		}

		String dirverButton = "concat ( concat(us_name, '<a href=\"../../PrintDriverManifestSRVL?stdate=ALL&driverid=',c_assignedagent,'&stg_code=',q_stage,'&stp_code=',q_step,'&storecode=',q_branch,'\" style=\"padding-right:20px;\" >"
				+ " <input type=\"button\" value=\" طباعة كشف الشحنات \"   class=\"btn btn-dark btn-sm\" ></a>'))   as driver ";

		MainSql = "select q_postopnedto, q_postponedoption, c_dlvagent_manifestid, '' as fromdt, '' as todate, "
				+ " ifnull(c_mbapp_agent_status,'') as c_mbapp_agent_status, c_receiptamt_usd, "
				+ " c_receiptamt, date(c_createddt) as c_createddt,c_branchcode, c_rcv_name, c_custreceiptnoori ,"
				+ " c_qty,q_branch, c_id, cust_name, concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,''))  as address,"
				+ " c_rcv_state,c_rcv_district, q_enterdate , q_stage, q_step , stp_id , q_action, "
				+ " c_dategiventodlvagent, c_rtnreason,  c_rcv_hp1, q_assigned_to , c_assignedagent, c_rmk, " 
				+  dirverButton 
				+ " from p_cases "
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join kbstate on (c_rcv_state = st_code and st_branch={userstorecode})  "
				+ " join kbcustomers on (c_custid = cust_id)" 
				+ " left join kbusers on c_assignedagent = us_id "
				+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
				+ " where q_stage= '{stg_code}' and q_step='{stp_code}'   and (q_branch={userstorecode})"
				+ "   and q_status='ACTV' ";

		userDefinedGroupByCol = "driver";
		userDefinedGroupColsOrderBy = "driver, c_rcv_state, c_rcv_district , c_id";

		userDefinedColLabel.put("c_mbapp_agent_status", "اشعار المندوب");
		userDefinedLookups.put("c_mbapp_agent_status",
				"SELECT stpd_code, stpd_desc from kbstep_decision where stpd_deleted  = 'N' and stpd_onlymbapp = 'Y'");
		userDefinedFilterCols.add("c_assignedagent");
		userDefinedFilterCols.add("c_rcv_hp1");
		
		userDefinedEditCols.add("c_rmk");
		userModifyTD.put("q_action", "modifyAction()");

		userDefinedFilterCols.clear();
		userDefinedFilterCols.add("c_dlvagent_manifestid");
		userDefinedFilterCols.add("c_id");
		userDefinedFilterCols.add("c_custreceiptnoori");
		//userDefinedFilterCols.add("c_custid");
		userDefinedFilterCols.add("fromdt");
		userDefinedFilterCols.add("todate");
		userDefinedFilterCols.add("c_rcv_state");
		userDefinedFilterCols.add("c_assignedagent");
		userDefinedFilterCols.add("c_rcv_hp1");

		userDefinedColLabel.put("fromdt", "بتاريخ");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف المستلم");
		userDefinedColLabel.put("todate", "إلى تاريخ");
		userDefinedColLabel.put("q_postopnedto", "مؤجل إلى");
		userDefinedColLabel.put("q_postponedoption", "سبب التأجيل");
		userDefinedFilterColsHtmlType.put("fromdt", "DATE");
		userDefinedFilterColsHtmlType.put("todate", "DATE");

		userDefinedLookups.put("c_rtnreason", "select rtn_code , rtn_desc from kbrtn_reasons");
		userDefinedLookups.put("q_postponedoption", "select post_code, post_desc from kbpostponedoptions");
		UserDefinedPageRows = 100;
	}

	public String modifyAction(HashMap<String, String> hashy) {
		StringBuilder sb = new StringBuilder("<td><table class='col-12'><tr><td>");
		sb.append("<select class=\"form-control\" aria-hidden=\"true\" "
				+ " style=\"min-width: 120px; background-color:white;\"  onchange=\"change_q_actionColor(this, '" + i
				+ "')\" " + " id='q_action_smartyrow_" + i + "' name='q_action_smartyrow_" + i + "' "
				+ "style=\"width: 100%\"> " + "<option value=''></option>");
		Map<String, String> lookupsmap = colMapValues.get("q_action");
		String selectedItem = "";
		if (lookupsmap != null) {
			if (!lookupsmap.isEmpty()) {
				for (String code : lookupsmap.keySet()) {
					selectedItem = "";
					sb.append("<option value='" + code + "' " + selectedItem + ">" + lookupsmap.get(code)
							+ "</option> \n");
				}
			}
		}
		sb.append("</select></td></tr>");
		//AgentsList
		sb.append("<tr id='c_assignedagent_change_" + i + "' style='" + trStyleHidden + "'><td>");
		sb.append("<select class='form-control'  id='c_assignedagent_smartyrow_" + i + "'"
				+ " name='c_assignedagent_smartyrow_" + i + "' "
				+ "style='text-align:right; background-color:rgb(85 9 64); "
				+ " padding: 0 10px 0 10px;  color: white; border: 1px solid #7dc6dd;'> "
				);
		sb.append(agentsList.toString());
		sb.append("</td></tr>");

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
		sb.append("<input type='number' style='text-align: right;background-color: #fdfdfd; width: 9em;"
				+ " line-height: 20px; margin-right:5px;' placeholder='مبلغ الوصل د.ع'  name= 'new_receiptamtrtn_smartyrow_"
				+ i + "' " + " id = 'new_receiptamtrtn_smartyrow_" + i + "' >");
		// New receipt amount usd
		sb.append("<input type='number' style='text-align: right;background-color: #d2ffd9; width: 9em;"
				+ " line-height: 20px; margin-right:5px;' placeholder='مبلغ الوصل$'  name= 'new_receiptamt_usd_rtn_smartyrow_"
				+ i + "' " + " id = 'new_receiptamt_usd_rtn_smartyrow_" + i + "' >"
						+ "</td>");
		sb.append("</tr><tr><td><input type='number' style='text-align: right; background-color:rgb(245 225 201);"
				+ "  width: 8em; border:0px;display:none; line-height:10px; margin-top:3px;' placeholder='القطع الراجعة' name= 'rtn_qty_smartyrow_"
				+ i + "' " + " id = 'rtn_qty_smartyrow_" + i + "' minval =1 >");
		sb.append("</td></tr>");
		sb.append("</table></td>");
		i++;
		return sb.toString();
	}

	public void initialize(HashMap smartyStateMap) {
		String stpCode = replaceVarsinString("{stp_code}", arrayGlobals).trim();
		String stgCode = replaceVarsinString("{stg_code}", arrayGlobals).trim();

		int userStoreCode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		userDefinedLookups.put("c_assignedagent", "select us_id, us_name from kbusers where us_rank = 'DLVAGENT' and us_branchcode='"+userStoreCode+"' ");
		/*
		 * userDefinedCaption=
		 * "<div class='col-md-9 col-sm-12 col-xs-12'>"+this.userDefinedCaption+"</div>"
		 * + " <div class=\"checkbox checkbox-success\">" +
		 * "                        <input id=\"allreturned\" class=\"\" onclick=\"changeToRecievedAll('all_received');\" type=\"checkbox\">"
		 * + "                        <label for=\"allreturned\">" +
		 * "                            تم التسليم للكل" +
		 * "                        </label>" + "                    </div>";
		 */
		userDefinedCaption = "";
		super.initialize(smartyStateMap);
		userDefinedGridCols.clear();
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_branchcode");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("address");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_dategiventodlvagent");
		userDefinedGridCols.add("q_action");
		userDefinedGridCols.add("c_rmk");

		if (stpCode.equalsIgnoreCase("POSTPONED")) {
			userDefinedGridCols.add("q_postopnedto");
			userDefinedGridCols.add("q_postponedoption");
		} else if (stpCode.equalsIgnoreCase("RTN_WITHAGENT") || stpCode.equalsIgnoreCase("TRY_AGAIN")) {
			userDefinedGridCols.add("c_rtnreason");
		} else {
			userDefinedGridCols.add("c_mbapp_agent_status");
		}

		String fromdt = "ALL";
		String todt = "ALL";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {
					if (parameter.equals("fromdt")) {
						fromdt = value;
						foundSearch = true;
					}
					if (parameter.equals("todate")) {
						todt = value;
						// foundSearch = true;
					}
				}
			}
		}

		if (foundSearch) {
			if (todt.equalsIgnoreCase("ALL") && !fromdt.equalsIgnoreCase("ALL")) {
				todt = fromdt;
			}
			String dirverButton = "concat ( concat(us_name, '<a href=\"../../PrintDriverManifestSRVL?stdate=" + fromdt
					+ "&todate=" + todt
					+ "&driverid=',c_assignedagent,'&stg_code=',q_stage,'&stp_code=',q_step,'&storecode=',q_branch,'\" style=\"padding-right:20px;\" >"
					+ " <input type=\"button\" value=\" طباعة كشف الشحنات \"   class=\"btn btn-dark btn-sm\" ></a>'))   as driver ";

			/*
			 * dirverButton +=
			 * ", concat( '<a href=\"../../PrintDriverManifestExcelSRVL?stdate="+fromdt+
			 * "&todate="+
			 * todt+"&driverid=',c_assignedagent,'&stg_code=',q_stage,'&stp_code=',q_step,'&storecode=',q_branch,'\" style=\"padding-right:20px;\" >"
			 * +
			 * " <input type=\"button\" value=\" Excel طباعة مانفيست الشحنات \"   class=\"btn btn-success btn-sm\" ></a>')) as driver "
			 * ;
			 */

			MainSql = "select q_postopnedto, q_postponedoption,c_dlvagent_manifestid, '' as fromdt, '' as todate, ifnull(c_mbapp_agent_status,'') as c_mbapp_agent_status,"
					+ " c_receiptamt, c_receiptamt_usd, "
					+ " date(c_createddt) as c_createddt,c_branchcode, c_rcv_name, c_custreceiptnoori , c_qty,q_branch, c_id, cust_name,  "
					+ "  concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,''))  as address ,c_rcv_state,c_rcv_district, "
					+ " q_enterdate , q_stage, q_step , stp_id , q_action, c_dategiventodlvagent, c_rtnreason, concat(c_rcv_hp1,' , ',c_rcv_hp2) as c_rcv_hp1, "
					+ " q_assigned_to , c_assignedagent, c_rmk, " + dirverButton + " " + " from p_cases "
					+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
					+ " join kbstate on (c_rcv_state = st_code and st_branch= " +userStoreCode+ " )  "
					+ " join kbcustomers on (c_custid = cust_id)" + " left join kbusers on c_assignedagent = us_id "
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where q_stage='" + stgCode + "' and q_step='" + stpCode + "' and q_status='ACTV' "
					+ " and (date(c_createddt)>='" + fromdt + "') and (date(c_createddt)<='" + todt + "' ) "
					+ " and  q_branch="+userStoreCode+" ";
		}
		HashMap<String, String> agentsMap;
		try {
			agentsMap = ut.getListOfAgents(conn, userStoreCode);
			for (String agentId : agentsMap.keySet() ) {
				agentsList.append("<option value='" + agentId + "' >" + agentsMap.get(agentId)
				+ "</option> \n");
			}
			agentsList.append("</select>");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}// end of method initialize

	@Override
	public StringBuilder getMultiEditGrid() {
		search_paramval.remove("fromdt");
		search_paramval.remove("todate");
		return super.getMultiEditGrid();
	}

	@Override
	public void parseInputs(HttpServletRequest rqs) {
		
		keyVal = parseUpdateRqs(rqs);
		int rowsNo = 0;
		if (inputMap_ori.get("smartyhiddenmultieditrowsno") != null)
			rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);

		HashMap<Integer, Double> newReceiptsAmtIqdMap = new HashMap<Integer, Double>();
		HashMap<Integer, Double> newReceiptsAmtUsdMap = new HashMap<Integer, Double>();
		HashMap<Integer, String> rtnReasonMap = new HashMap<Integer, String>();
		HashMap<Integer, String> postponedOptionMap = new HashMap<Integer, String>();
		HashMap<Integer, String> postponedToMap = new HashMap<Integer, String>();
		HashMap<Integer, Integer> partialQtyReturnMap = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> newAssignedAgentMap = new HashMap<Integer, Integer>();
		HashMap<Integer, String> casesRmkMap = new HashMap<Integer, String>();
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
							&& inputMap_ori.get("new_receiptamtrtn_smartyrow_" + i)[0].length() > 0) {
						newReceiptsAmtIqdMap.put(id,
								Double.parseDouble(inputMap_ori.get("new_receiptamtrtn_smartyrow_" + i)[0]));
					}
					if (inputMap_ori.get("new_receiptamt_usd_rtn_smartyrow_" + i)[0] != null && inputMap_ori.get("new_receiptamt_usd_rtn_smartyrow_" + i)[0].length() > 0) {
						newReceiptsAmtUsdMap.put(id, Double.parseDouble(inputMap_ori.get("new_receiptamt_usd_rtn_smartyrow_" + i)[0]));
					}
					if (action.equalsIgnoreCase("PART_SUCC")) {
						if (inputMap_ori.get("rtn_qty_smartyrow_" + i)[0] != null
								&& inputMap_ori.get("rtn_qty_smartyrow_" + i)[0].length() > 0)
							partialQtyReturnMap.put(id,
									Integer.parseInt(inputMap_ori.get("rtn_qty_smartyrow_" + i)[0]));
					}
				} else if (action.equalsIgnoreCase("RTN_WTIHAGENT") || action.equalsIgnoreCase("RTN_TOSTORE")) {
					rtnReasonMap.put(id, inputMap_ori.get("c_rtnreason_smartyrow_" + i)[0]);
				} else if (action.equalsIgnoreCase("POSTPONED")) {
					postponedOptionMap.put(id, inputMap_ori.get("q_postponedoption_smartyrow_" + i)[0]);
					postponedToMap.put(id, inputMap_ori.get("q_postponedto_smartyrow_" + i)[0]);
				}else if (action.equalsIgnoreCase("CHANGE_AGENT")) {
					newAssignedAgentMap.put(id, Integer.parseInt(inputMap_ori.get("c_assignedagent_smartyrow_" + i)[0]));
				}
				cIdList.add(id);
			}

		}
		queueActionsParamsBean.setCasesRmk(casesRmkMap);
		queueActionsParamsBean.setNewReceiptAmtIqdMap(newReceiptsAmtIqdMap);
		queueActionsParamsBean.setNewReceiptAmtUsdMap(newReceiptsAmtUsdMap);
		queueActionsParamsBean.setPostponedDateTimeToMap(postponedToMap);
		queueActionsParamsBean.setPostponedOptionsMap(postponedOptionMap);
		queueActionsParamsBean.setRtnReasonMap(rtnReasonMap);
		queueActionsParamsBean.setPartialQtyReturnMap(partialQtyReturnMap);
		queueActionsParamsBean.setAssignedAgentsMap(newAssignedAgentMap);
	}
	
	@Override
	public String doUpdate(HttpServletRequest rqs, boolean commit) {
	
		int userid = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		String currentStep = replaceVarsinString("{stp_code}", arrayGlobals).trim();
		try {
			parseInputs(rqs);
			processData(conn, userid, 0, "AGENTOP", currentStep,new HashMap<Integer, String>());
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

	@Override
	public void processData(Connection conn,  int actionTakenBy, int currentBranch, String currentStage,
			String currentStep, HashMap<Integer, String> qRmkMap) throws Exception {
		
		FlowUtils fu = new FlowUtils();
		UtilitiesFeqar ut = new UtilitiesFeqar();
		boolean everyThingIsOk = true;
		HashMap<Integer, Double> newReceiptsAmtIqdMap = queueActionsParamsBean.getNewReceiptAmtIqdMap();
		HashMap<Integer, Double> newReceiptsAmtUsdMap = queueActionsParamsBean.getNewReceiptAmtUsdMap();
		HashMap<Integer, String> rtnReasonMap = queueActionsParamsBean.getRtnReasonMap();
		HashMap<Integer, String> postponedOptionMap = queueActionsParamsBean.getPostponedOptionsMap();
		HashMap<Integer, String> postponedToMap = queueActionsParamsBean.getPostponedDateTimeToMap();
		HashMap<String, String> queueColsToUpdate = new HashMap<String, String>();
		HashMap<Integer, Integer> partialQtyReturnMap = queueActionsParamsBean.getPartialQtyReturnMap();
		HashMap<Integer, String> casesToRmkMap = queueActionsParamsBean.getCasesRmk();
		HashMap<Integer, Integer> casesToNewAgentMap = queueActionsParamsBean.getAssignedAgentsMap();
		try {
			for (int cid : cIdList) {
				queueColsToUpdate = new HashMap<String, String>();
				everyThingIsOk = true;
				// update the action is take by who

				// when returned but the shipment cost is paid by the sender
				if (actionsMap.get(cid).equalsIgnoreCase("SUCS_DLV_CHANGEAMT")
						|| actionsMap.get(cid).equalsIgnoreCase("PART_SUCC")) {
					if (newReceiptsAmtIqdMap != null && newReceiptsAmtIqdMap.get(cid) != null) {
						Utilities.changeReceiptByCaseId(conn, cid,  newReceiptsAmtIqdMap.get(cid),actionTakenBy, "عند المندوب",
								StandardFinCurrency.IQD);
						Utilities.changeReceiptByCaseId(conn, cid,newReceiptsAmtUsdMap.get(cid),actionTakenBy, "عند المندوب",
								StandardFinCurrency.USD);
						if (actionsMap.get(cid).equalsIgnoreCase("PART_SUCC"))
							ut.updateReturnQuantityInDB(conn, cid, partialQtyReturnMap.get(cid));
					} else {
						everyThingIsOk = false;
					}
				} else if (actionsMap.get(cid).equalsIgnoreCase("RTN_TOSTORE")
						|| actionsMap.get(cid).equalsIgnoreCase("RTN_WTIHAGENT")) {
					queueColsToUpdate.put("c_rtnreason", rtnReasonMap.get(cid));

				} else if (actionsMap.get(cid).equalsIgnoreCase("POSTPONED")) {
					queueColsToUpdate.put("q_postopnedto", postponedToMap.get(cid));
					queueColsToUpdate.put("q_postponedoption", postponedOptionMap.get(cid));
				} else if (actionsMap.get(cid).equalsIgnoreCase("CHANGE_AGENT")) {
					Utilities.changeAgentWhenShipmentWithAgent(conn, cid, casesToNewAgentMap.get(cid), lu.getBranchCode());
				}
				if (casesToRmkMap.containsKey(cid)) {
					queueColsToUpdate.put("c_rmk", casesToRmkMap.get(cid));
				}
				if (everyThingIsOk) {
					fu.MoveDecisionStepNext(conn, cid, actionsMap.get(cid), actionTakenBy, queueColsToUpdate, 0,
							currentStage, currentStep, qRmkMap.get(cid));
				}
			}
			String myCurrentStep = currentStep;
			new Thread(() -> {
				try {
					sendNotifications(cIdList, actionsMap, "AGENTOP", myCurrentStep,  new HashMap<Integer, String>(),
							 new HashMap<Integer, String>(), "CUSTOMER");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).start();

		} catch (Exception e) {
			throw e;
		}
	}
}