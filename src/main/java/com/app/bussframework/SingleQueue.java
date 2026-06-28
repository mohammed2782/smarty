package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Notifications;
import com.app.util.UtilitiesNafie;
import com.app.beans.ActionsBean;
import com.app.cases.CaseInformation;

public  class SingleQueue extends CoreMgr {
	
	private int checkBoxSeq = 1;
	protected QueueActionsParamsBean queueActionsParamsBean = new QueueActionsParamsBean();
	protected ArrayList<Integer> cIdList = new ArrayList<Integer>();
	protected HashMap<Integer, String> actionsMap = new HashMap<Integer, String>();
	
	
	public QueueActionsParamsBean getQueueActionsParamsBean() {
		return queueActionsParamsBean;
	}

	public void setQueueActionsParamsBean(QueueActionsParamsBean queueActionsParamsBean) {
		this.queueActionsParamsBean = queueActionsParamsBean;
	}

	public ArrayList<Integer> getcIdList() {
		return cIdList;
	}

	public void setcIdList(ArrayList<Integer> cIdList) {
		this.cIdList = cIdList;
	}

	public HashMap<Integer, String> getActionsMap() {
		return actionsMap;
	}

	public void setActionsMap(HashMap<Integer, String> actionsMap) {
		this.actionsMap = actionsMap;
	}

	SingleQueue() {
		MainSql = "select c_branchcode,  c_dlvagent_manifestid,  (case when path_id is null then 0 else 1 end) as pathinstore,  "
		+ "c_pickupagent, c_rtnreason, c_receiptamt, c_receiptamt_usd, date(c_createddt) as c_createddt, "
		+ " c_assignedagent, c_custreceiptnoori,c_rcv_name, c_qty,q_branch,'' as attempts, c_id, cust_name,  "
		+ " concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) as address ,c_rcv_state,"
		+ " ifnull(c_rcv_district,'NA') as c_rcv_district,"
		+ " q_enterdate  , q_stage, q_step , stp_id , q_action," 
		+ " q_assigned_to , c_rmk " 
		+ " from p_cases "
		+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
		+ " join kbstate on (c_rcv_state = st_code and st_branch={userstorecode})  "
		+ " join kbcustomers on (c_custid = cust_id )"
		+ " left join kbpaths on (path_state = c_rcv_state and path_tobranch ={userstorecode} and path_frombranch = q_comingfrombranch)"
		+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
		+ " where q_stage= '{stg_code}' and  q_step='{stp_code}' and q_status !='CLS'"
		+ " and (q_branch={userstorecode})  ";

		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_branchcode");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("address");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("q_action");
		// userDefinedGridCols.add("c_branchcode");
		canEdit = true;
		canFilter = true;

		// userDefinedFilterCols.add("c_id");
		userDefinedFilterCols.add("c_custreceiptnoori");
		//userDefinedFilterCols.add("c_custid");
		userDefinedFilterCols.add("c_createddt");
		userDefinedFilterCols.add("c_rcv_state");

		userDefinedColLabel.put("c_dlvagent_manifestid", "منفيست مندوب التوصيل");
		userDefinedColLabel.put("c_createddt", "تاريخ الشحنه");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل د.ع");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
		userDefinedColLabel.put("c_branchcode", "أنشأ في فرع");
		userDefinedColLabel.put("cust_name", "المتجر");
		userDefinedColLabel.put("c_custid", "المتجر");
		userDefinedColLabel.put("c_rcv_name", "المستلم");
		userDefinedColLabel.put("q_action", "العمليه");
		userDefinedColLabel.put("address", "العنوان");
		userDefinedColLabel.put("c_qty", "عدد القطع");
		userDefinedColLabel.put("c_rcv_state", "المحافظة");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_assignedagent", "مندوب التوصيل");
		userDefinedColLabel.put("c_rcv_hp", "هاتف المستلم");
		userDefinedColLabel.put("c_rtnreason", "سبب الأرجاع");
		userDefinedColLabel.put("c_pickupagent", "مندوب الإستلام");
		userDefinedColLabel.put("cc_liaisonagentid", "مندوب ألأرتباط");
		userDefinedColLabel.put("cc_manifestid", "رقم المنفيست");
		userDefinedColLabel.put("cc_tobranch", "إلى فرع");
		userDefinedColLabel.put("c_dategiventodlvagent", "تاريخ الإعطاء للمندوب");
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");// to remove the comma
		// userDefinedColsTypes.put("q_enterdate", "DATE");
		userDefinedFilterColsHtmlType.put("c_createddt", "DATE");
		userDefinedNewColsHtmlType.put("c_createddt", "TIMESTAMP");
		userDefinedFilterColsHtmlType.put("cc_liaisonagentid", "DROPLIST");

		userDefinedLookups.put("q_assigned_to", "!select us_id , us_name from kbusers where us_id in "
				+ "	(select us_id   from kbusers where us_rank = 'DLVAGENT' and us_to_state like '%{c_rcv_state}%'"
				+ "    and  us_id in (select agdi_usid from kbagent_district 	join kbcity_district on (cdi_stcode like '%{c_rcv_state}%' and agdi_districtcode = cdi_id)"
				+ "    where agdi_districtcode='{c_rcv_district}')" + "	union"
				+ "	select us_id   from kbusers where us_rank = 'DLVAGENT'  and us_to_state like '%{c_rcv_state}%' and us_to_state not like '%BGD%'"
				+ "    ) and us_active='Y' ");
		userDefinedLookups.put("c_assignedagent", "select us_id , us_name from kbusers where us_rank = 'DLVAGENT' ");
		userDefinedLookups.put("c_pickupagent", "select us_id , us_name from kbusers");
		userDefinedLookups.put("c_rcv_state", "select st_code , st_name_ar from kbstate  order by st_order");
		userDefinedLookups.put("cc_liaisonagentid",
				"select us_id , us_name from kbusers where us_rank = 'LIAISONAGENT' "
				+ "	 and (us_id in  "
				+ "			(select distinct(path_liaisonagent) From kbpaths  where path_frombranch ={userstorecode} ) "
				+ "	  or  us_id in  "
				+ "			(select distinct(path_liaisonagent) From kbpaths  where path_tobranch ={userstorecode} ) "
				+ "		)");

		userDefinedLookups.put("c_rtnreason", "SELECT rtn_code, rtn_desc FROM kbrtn_reasons");
		userDefinedEditColsHtmlType.put("q_assigned_to", "DROPLIST");

		//userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers");

		userDefinedFilterColsHtmlType.put("c_custid", "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_assignedagent", "DROPLIST");

		userDefinedLookups.put("c_branchcode", "select branch_id, branch_name from kbbranches");

		userDefinedColLabel.put("c_id", "كود الشحنه");
		userDefinedColLabel.put("q_enterdate", "تاريخ ووقت الحاله");

		mainTable = "p_cases";
		keyCol = "c_id";
		displayMode = "GRIDEDIT";

		userDefinedEditCols.add("q_action");
		userDefinedEditColsHtmlType.put("q_action", "CLASSIC_SELECT");
		userDefinedLookups.put("q_action", "select stpd_code, stpd_desc from kbstep_decision where "
				+ " stpd_stpid in (select stp_id from kbstep where stp_code='{stp_code}' and stp_stgcode='{stg_code}') and stpd_onlymbapp='N'"
				+ " and stpd_forrank like '%{userRank}%' ");

		UserDefinedPageRows = 3000;

		userDefinedEditColsHtmlType.put("c_assignedagent", "DROPLIST");

		userModifyTD.put("checkboxconfirm", "showConfirmCaseCheckBox({c_id}, {c_custreceiptnoori})");
		userModifyTD.put("c_custreceiptnoori", "modifyRecieptNo({c_custreceiptnoori},{c_id})");
		userDefinedColLabel.put("checkboxconfirm", " ");
	}

	public String showConfirmCaseCheckBox(HashMap<String, String> hashy) {
		StringBuilder sb = new StringBuilder("<td>");
		sb.append("<input class='confirmCheckBoxclass_" + hashy.get("c_custreceiptnoori") + "'"
				+ "  type=\"checkbox\" value=\"\" id='confirmCheckBox_" + hashy.get("c_id") + "' data-check-seq = '"
				+ checkBoxSeq + "'>");
		sb.append("</td>");
		checkBoxSeq++;
		return sb.toString();
	}

	public String modifyRecieptNo(HashMap<String, String> hashy) {

		String s = "<td caseid='" + hashy.get("c_id") + "' id='" + hashy.get("c_custreceiptnoori") + "'>"
				+ hashy.get("c_custreceiptnoori");
		s += "</td>";
		return s;
	}

	@Override
	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		if (arrayGlobals.get("c_id") != null) {
			String q_caseid = (String) arrayGlobals.get("c_id");
			search_paramval.put("q_caseid", new String[] { q_caseid });
		}
		/*
		 * if (arrayGlobals.get("filterByCasesInQ")!=null &&
		 * arrayGlobals.get("filterValCasesInQ") !=null) { String [] filterBy = (String
		 * []) arrayGlobals.get("filterByCasesInQ"); String [] filterVal = (String [])
		 * arrayGlobals.get("filterValCasesInQ"); for (int i=0; i<filterBy.length; i++)
		 * { search_paramval.put(filterBy[i], new String[] {filterVal[i]}); } }
		 */
		String stp_code = replaceVarsinString("{stp_code}", arrayGlobals).trim();
		if (stp_code.equalsIgnoreCase("rtn_to_archv"))
			userDefinedCaption = "<div class='col-md-9 col-sm-12 col-xs-12'>" + this.userDefinedCaption + "</div>"
					+ " <div class=\"checkbox checkbox-success\">"
					+ "                        <input id=\"allreturned\" class=\"\" onclick=\"changeToArchiveAll('all_archived');\" type=\"checkbox\">"
					+ "                        <label for=\"allreturned\">" + "                            أرشف الكل"
					+ "                        </label>" + "                    </div>";
	}
	
	
	public void parseInputs(HttpServletRequest rqs) {
		keyVal = parseUpdateRqs(rqs);
		int noOfInputs = 0;
		if (inputMap_ori.get("smartyhiddenmultieditrowsno") != null)
			noOfInputs = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
		int id = 0;
		for (int i = 1; i <= noOfInputs; i++) {
			if (inputMap_ori.get("q_action_smartyrow_" + i) != null
					&& inputMap_ori.get("q_action_smartyrow_" + i)[0].length() > 0) {
				id = Integer.parseInt(inputMap_ori.get(hiddenKeyCol + "_smartyrow_" + i)[0]);
				actionsMap.put(id, inputMap_ori.get("q_action_smartyrow_" + i)[0]);
				cIdList.add(id);
			}
		}
	}
	
	@Override
	public String doUpdate(HttpServletRequest rqs, boolean commit) {

		int userid = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		String stage = replaceVarsinString("{stg_code}", arrayGlobals).trim();
		String step = replaceVarsinString("{stp_code}", arrayGlobals).trim();
		parseInputs (rqs);
		try {
			processData(conn, userid, userstorecode, stage, step, new HashMap<Integer, String>() );
			conn.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			try {conn.rollback();} catch (Exception eRoll) {/*ignore*/}
			return "Error";
		}
		return "تم الحفظ";
	}

	public void processData (Connection conn, int actionTakenBy,
	int currentBranch, String currentStage, String currentStep, HashMap<Integer, String> qRmk) throws Exception {
		FlowUtils fu = new FlowUtils();
		for (int cid : cIdList) {
			fu.MoveDecisionStepNext(conn, cid, actionsMap.get(cid), actionTakenBy, currentBranch, currentStage, currentStep, qRmk.get(cid));
		}
		new Thread(() -> {
			try {
				sendNotifications(cIdList, actionsMap, currentStage, currentStep, null, null, "CUSTOMER");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
	public void sendNotifications(ArrayList<Integer> cIdList, HashMap<Integer, String> actionsMap, String currentStage,
			String currentStep, HashMap<Integer, String> extraNotificationHeader,
			HashMap<Integer, String> extraNotificationBody, String sendToWhichRank) throws Exception {

		Connection conn = null;
		UtilitiesNafie utn = new UtilitiesNafie();
		ActionsBean actionBean;
		HashMap<String, String> caseInfo;
		try {
			Notifications notifications = new Notifications();
			conn = mysql.getConn();
			for (int cid : cIdList) {
				
				actionBean = utn.getActionFullInfo(conn, actionsMap.get(cid), currentStep, currentStage);
				if (actionBean.getSendNotifications().equalsIgnoreCase("Y")
						&& !actionsMap.get(cid).equalsIgnoreCase("")) {
					caseInfo = utn.getCaseInfo(conn, cid);
					String notificationTitle = "", notificationMsg = "";
					// get the list of users should send the notifications to
					HashMap<Integer, ArrayList<String>> usersIdOneSignalIdMap = notifications
							.getCustomerOneSignalInfoToSendNotificationToPerCases(cid);
					//System.out.println(" lin 270 ================== "+usersIdOneSignalIdMap);
					if (!usersIdOneSignalIdMap.isEmpty()) {
						// start the sending process
						/*System.out.println("postponedReasonDesc------>"+caseInfo.get("postponedReasonDesc"));
						System.out.println("rtnresaon------>"+caseInfo.get("rtnReasonDesc"));*/
						notificationTitle = actionBean.getNotificationTitleForCustomer().replace("{receipt}", caseInfo.get("c_custreceiptnoori"))
								.replace("{stepname}", caseInfo.get("stepName")).replace("{decision}", actionBean.getText())
								.replace("{rtnresaon}", caseInfo.get("rtnReasonDesc")).replace("{postponedreason}", caseInfo.get("postponedReasonDesc"));
						
						notificationMsg = actionBean.getNotificationBodyForCustomer().replace("{receipt}", caseInfo.get("c_custreceiptnoori"))
								.replace("{stepname}", caseInfo.get("stepName")).replace("{decision}", actionBean.getText())
								.replace("{rtnresaon}", caseInfo.get("rtnReasonDesc")).replace("{postponedreason}", caseInfo.get("postponedReasonDesc"));
						HashMap<String, String> extraDataMap = new HashMap<String, String>();
						extraDataMap.put("caseid", cid + "");
						
						notifications.sendNotificationToUser(usersIdOneSignalIdMap, sendToWhichRank, notificationTitle,
								notificationMsg, "caseid", cid, extraDataMap, Integer.parseInt(caseInfo.get("ownerbranchcode")));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}
}
