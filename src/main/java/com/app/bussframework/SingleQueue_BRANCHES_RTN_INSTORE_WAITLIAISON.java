package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.cases.CaseInformation;
import com.app.util.Utilities;

public class SingleQueue_BRANCHES_RTN_INSTORE_WAITLIAISON extends SingleQueue {
	public SingleQueue_BRANCHES_RTN_INSTORE_WAITLIAISON() {

		MainSql = "select  cc_rtnwithliaisonagent, cc_frombranch, cc_tobranch ,  rtn_desc, "
				+ " c_receiptamt,c_rtnreason, c_receiptamt_usd, date(c_createddt) as c_createddt,c_branchcode, c_custreceiptnoori, "
				+ " c_id, cust_name,  "
				+ " concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) as address ,c_rcv_state,"
				+ " ifnull(c_rcv_district,'NA') as c_rcv_district," + " q_enterdate  , q_stage, q_step , q_action,"
				+ " q_assigned_to , c_rmk " + " from p_cases "
				+ " join p_caseschain on cc_caseid = c_id and  cc_tobranch = {userstorecode} "
				+ " left join kbrtn_reasons on (c_rtnreason = rtn_code) "
				+ " join kbstate on (c_rcv_state = st_code and st_branch={userstorecode})  "
				+ " join kbcustomers on (c_custid = cust_id )"
				+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) " + " where ("
				+ "  (q_branch={userstorecode} and q_stage= '{stg_code}' and q_step='{stp_code}' and q_status ='ACTV'))"
				+ " order by c_createddt desc ";

		userDefinedLookups.put("q_assigned_to",
				"!select path_liaisonagent, us_name from kbpaths join kbusers on us_id = path_liaisonagent"
						+ " where path_tobranch ={cc_tobranch} and path_frombranch ={cc_frombranch} ");
		userDefinedLookups.put("cc_frombranch", "select branch_id , branch_name from kbbranches");
		userDefinedGridCols.clear();
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("cc_frombranch");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("rtn_desc");
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("q_action");
		userDefinedGridCols.add("q_assigned_to");
		userDefinedEditCols.add("q_assigned_to");
		userDefinedEditCols.add("cc_frombranch");
		userDefinedColLabel.put("cc_frombranch", "إلى فرع");
		userDefinedColLabel.put("c_createddt", "تاريخ أنشاء الشحنة");
		userDefinedColLabel.put("q_assigned_to", "مندوب الأرتباط");
		userDefinedColLabel.put("rtn_desc", "سبب الراجع");
		userDefinedReadOnlyEditCols.add("cc_frombranch");
		userDefinedEditColsHtmlType.put("q_assigned_to", "DROPLIST");
		userDefinedEditColsHtmlType.put("cc_frombranch", "DROPLIST");
		
		userDefinedFilterCols.clear();
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("cc_frombranch");
		userDefinedColsMustFill.add("q_assigned_to");
		userDefinedFilterColsHtmlType.put("cc_frombranch", "DROPLIST");
	}

	@Override
	public String doUpdate(HttpServletRequest rqs, boolean commit) {
		int userid = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		try {
			parseInputs(rqs);
			processData(conn, userid, currentBranch, "BRANCHES","RTN_INSTORE_WAITLIAISON", new HashMap<Integer, String>());
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
	public void parseInputs(HttpServletRequest rqs) {
		keyVal = parseUpdateRqs(rqs);

		int rowsNo = 0;
		if (inputMap_ori.get("smartyhiddenmultieditrowsno") != null)
			rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);

		HashMap<Integer, Integer> liaisonAgent = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> backToBranch = new HashMap<Integer, Integer>();
		String action = "";
		int id = 0;
		for (int i = 1; i <= rowsNo; i++) {
			action = inputMap_ori.get("q_action_smartyrow_" + i)[0];
			if (action != null && !action.trim().isEmpty() && !action.trim().equalsIgnoreCase("")
					&& !action.trim().equalsIgnoreCase("null")) { // RTN_TOLIAISONAGENT
				id = Integer.parseInt(inputMap_ori.get(hiddenKeyCol + "_smartyrow_" + i)[0]);
				actionsMap.put(id, action);
				if (inputMap_ori.get("q_assigned_to_smartyrow_" + i) != null
						&& inputMap_ori.get("q_assigned_to_smartyrow_" + i)[0] != null
						&& inputMap_ori.get("q_assigned_to_smartyrow_" + i)[0].length() > 0)
					liaisonAgent.put(id, Integer.parseInt(inputMap_ori.get("q_assigned_to_smartyrow_" + i)[0]));
				if (inputMap_ori.get("cc_frombranch_smartyrow_" + i) != null
						&& inputMap_ori.get("cc_frombranch_smartyrow_" + i)[0] != null
						&& inputMap_ori.get("cc_frombranch_smartyrow_" + i)[0].length() > 0)
					backToBranch.put(id, Integer.parseInt(inputMap_ori.get("cc_frombranch_smartyrow_" + i)[0]));
				cIdList.add(id);

			}
		}
		queueActionsParamsBean.setBackToBranchMap(backToBranch);
		queueActionsParamsBean.setLiaisonAgentsMap(liaisonAgent);
	}

	@Override
	public void processData(Connection conn,int actionTakenBy, int currentBranch, String currentStage,
			String currentStep, HashMap<Integer, String> qRmk) throws Exception {
		FlowUtils fu = new FlowUtils();
		Utilities ut = new Utilities();

		CaseInformation caseInformation = new CaseInformation();
		for (int cid : cIdList) {
			caseInformation = ut.getSinglCaseInformationToBranch(conn, cid, currentBranch);
			if (caseInformation.getStepCode()!= null && caseInformation.getStepCode().equalsIgnoreCase("PART_SUCC")
					&& caseInformation.getStageCode().equalsIgnoreCase("DLV")) {
				if (actionsMap.get(cid).equalsIgnoreCase("RTN_READY_LIAISON")) {
					updateStageStepInChain(conn, caseInformation.getCurrentChainId(),
							caseInformation.getParentChainId(), "RTN_READY_LIAISON", actionTakenBy);
				}
			} else {
				fu.MoveDecisionStepNext(conn, cid, actionsMap.get(cid), actionTakenBy, currentStage, currentStep,
						qRmk.get(cid));
			}
		}

	}

	public void updateStageStepInChain(Connection conn, int chainId, int parentChainId, String action,
			int actionTakenBy) throws Exception {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("update p_caseschain set "
					+ " cc_qstatus_tobranch = 'CLS' ,  cc_qaction_tobranch=? , cc_qactiontakenby_tobranch=?,"
					+ " cc_qstatus_frombranch = 'ACTV' , cc_qstage_frombranch=?, cc_qstep_frombranch=?, cc_qenterdate_frombranch= now() "
					+ " where cc_id=? ");
			pst.setString(1, action);
			pst.setInt(2, actionTakenBy);
			pst.setString(3, "BRANCHES");
			pst.setString(4, "RTN_MANIFEST_LIAISON");
			pst.setInt(5, chainId);
			pst.executeUpdate();

		} catch (Exception e) {

		} finally {
			// try{rs.close();}catch(Exception eRoll){}
			try {
				pst.close();
			} catch (Exception eRoll) {
			}
		}
	}

}
