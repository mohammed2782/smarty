
package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

public class SingleQueue_INIT_NEWINSTORE extends SingleQueue {
	int i =1;
	Utilities ut = new Utilities();
	boolean needToSmartlFindLiaisonAgent = false;
	public SingleQueue_INIT_NEWINSTORE() {
		super();
		MainSql  = "select q_comingfrombranch, c_branchcode,c_partial_return, "
				+ " (select GROUP_CONCAT(path_tobranch SEPARATOR ',') FROM kbpaths where path_state =c_rcv_state and path_frombranch = q_comingfrombranch) as pathinstore,  "
				+ " c_pickupagent, c_rtnreason, c_receiptamt, c_receiptamt_usd, date(c_createddt) as c_createddt, c_assignedagent, c_custreceiptnoori, "
				+ " c_rcv_name , c_qty,q_branch,'' as attempts, c_id , cust_name,  "
				+ " concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as address ,c_rcv_state,ifnull(c_rcv_district,'NA') as c_rcv_district,"
				+ " q_enterdate   , q_stage, q_step , stp_id , q_action,"
				+ " q_assigned_to , c_rmk ,"
				+ " case when cc_branchpmtid>0 or c_settled='FULL' or c_agentsharesettled='FULL' or c_pickupagentpmtid>0 then 'flase' else 'true' end as canedit"
				+ " from p_cases "
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join kbstate on (c_rcv_state = st_code and st_branch={userstorecode})  "
				+ " join kbcustomers on (c_custid = cust_id)"
				+ " left join p_caseschain on (c_id = cc_caseid and cc_tobranch = {userstorecode})"
				+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
				+ " where q_stage= 'INIT' and q_step='NEWINSTORE' and q_status !='CLS'"
				+ " and (q_branch={userstorecode} ) ";
		
		userDefinedFilterCols.add("c_rcv_district");
		userDefinedFilterCols.add("c_custid");
		userDefinedGridCols.clear();
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_receiptamt_usd");
		
		userDefinedGridCols.add("c_branchcode");
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("address");
		userDefinedColLabel.put("c_id", "كود الشحنة");
		userDefinedColLabel.put("q_branch", "الفرع");
		userDefinedColLabel.put("c_receiptamt","مبلغ الوصل د.ع");
		userDefinedColLabel.put("c_receiptamt_usd","مبلغ الوصل $");
		
		
		
		userDefinedFilterColsHtmlType.put("c_rcv_district", "DROPLIST");
		
		userDefinedColLabel.put("c_rcv_district", "المنطقة");
		
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("q_action");
		//userDefinedGridCols.add("pathinstore");
		
		userDefinedEditColsDefualtValues.put("q_action", new String[] {"ASSGN_AGENT"});
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("q_assigned_to");
		userDefinedGridCols.add("c_createddt");
		userDefinedColLabel.put("q_assigned_to", "مندوب الإرتباط");
		
		userDefinedLookups.put("c_assignedagent", "!select us_id , us_name from "
				+ " kbusers where us_id ={c_assignedagent} and us_branchcode ='{userstorecode}' union "
				+ " select us_id , us_name from kbusers where us_id in " + 
				"	(select us_id   from kbusers where us_rank = 'DLVAGENT' and us_to_state like '%{c_rcv_state}%'" + 
				"    and  us_id in (select agdi_usid from kbagent_district 	join kbcity_district on (cdi_stcode ='{c_rcv_state}' "
				+ " and agdi_districtcode = cdi_id)" + 
					"    where agdi_districtcode='{c_rcv_district}')" + 
				"    ) and us_active='Y' and us_branchcode ='{userstorecode}' ");
		
		userDefinedEditColsHtmlType.put("c_assignedagent", "CLASSIC_SELECT");
		
		userDefinedLookups.put("q_assigned_to", "!select distinct us_id, us_name from kbusers "
				+ " join kbpaths on (us_id = path_liaisonagent and path_state='{c_rcv_state}' "
				+ " and path_frombranch={q_comingfrombranch}  and path_frombranch !=path_tobranch) "
				+ " join kbbranches on (branch_id = path_tobranch and branch_active = 'Y')");
		
		userDefinedEditCols.add("c_assignedagent");
		userDefinedEditCols.add("q_assigned_to");
		userDefinedColLabel.put("c_assignedagent", "مندوب التوصيل");
		userDefinedLookups.put("q_branch", "select branch_id, branch_name from kbbranches");
		
		userModifyTD.put("q_action", "modifyAction({pathinstore}, {q_comingfrombranch},{c_partial_return})");
		userModifyTD.put("c_custreceiptnoori", "custReceiptNoOriEdit({c_id},{c_branchcode},{c_custreceiptnoori},{canedit})");
		UserDefinedPageRows = 100;
		userDefinedLookups.put("c_rcv_district",
				"!select cdi_id, cdi_name From kbcity_district where cdi_stcode ='{c_rcv_state}' ");
		
		userDefinedLookups.put("c_custid",
				"select cust_id, cust_name from kbcustomers where cust_id in ("
				+ "select distinct c_custid from p_cases where q_stage= 'INIT' and q_step='NEWINSTORE' "
				+ " and q_branch= {userstorecode}) ");
		
		userDefinedEditColsHtmlType.put("c_assignedagent", "CLASSIC_SELECT");
		userDefinedEditColsHtmlType.put("q_assigned_to", "CLASSIC_SELECT");
	}
	
	
	


	public void doUpdateStepActions(Connection conn, ArrayList<Integer> cIdList, HashMap<Integer, String> actionsMap,
			QueueActionsParamsBean queueActionsParamsBean, int actionTakenBy, int currentBranch, String currentStage,
			String currentStep, HashMap<Integer, String> qRmkMap) throws Exception {
		PreparedStatement pstUpdateCaseAgent = null;
		FlowUtils fu = new FlowUtils();
		UtilitiesFeqar utf = new UtilitiesFeqar();
		HashMap <Integer,Integer> dlvAgentsMap = queueActionsParamsBean.getDlvAgentMap();
		HashMap <Integer,Integer> liaisonAgentsMap = queueActionsParamsBean.getLiaisonAgentsMap();
		try {
			pstUpdateCaseAgent = conn.prepareStatement("update p_cases set c_dlvagent_manifestid=0, c_assignedagent=? where c_id=?");
			int assignedTo ;
			for (int cid :cIdList){
				assignedTo = 0;
				
				if (dlvAgentsMap!=null && dlvAgentsMap.containsKey(cid)  && dlvAgentsMap.get(cid) != null) {
					//if (ut.anyCasesForDlvAgentInStep(conn, "INIT", "PRINTMANIFEST", agentsMap.get(qid)))
					pstUpdateCaseAgent.setInt(1,dlvAgentsMap.get(cid) );
					pstUpdateCaseAgent.setInt(2, cid);
					pstUpdateCaseAgent.executeUpdate();
					pstUpdateCaseAgent.clearParameters();
					assignedTo = dlvAgentsMap.get(cid);
					
				}else if (liaisonAgentsMap != null &&  
						liaisonAgentsMap.containsKey(cid)  && liaisonAgentsMap.get(cid) != null) {
					PathBean pathBean= ut.getRightPathForCase(conn, cid, liaisonAgentsMap.get(cid));
					ut.createCasePath(conn, pathBean, actionTakenBy);
					assignedTo =  liaisonAgentsMap.get(cid) ;	
				}
				utf.updateRuralForSingleCase(conn, cid);
				fu.MoveDecisionStepNext(conn, cid, actionsMap.get(cid) , actionTakenBy ,currentBranch, assignedTo, 
						currentStage, currentStep, qRmkMap.get(cid));
				utf.calcShipmentProfit(conn, cid, currentBranch);
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {pstUpdateCaseAgent.close();}catch(Exception e) {}
		}
		
	}
	
	
	
	
	
	public String custReceiptNoOriEdit (HashMap<String,String> hashy) {
		StringBuilder sb = new StringBuilder("<td><div style='display:flex;'>");
		sb.append("<div class='col-7'>"+hashy.get("c_custreceiptnoori")+"</div>");
		sb.append("<div class='col-5' style='display:contents;'><button type=\"button\" class=\"btn btn-info btn-sm\" "
				+ "onclick=\"popitup ('editCaseFromStages?caneditfromstage="+hashy.get("canedit")+"&branchidfromstage="+hashy.get("c_branchcode")+""
						+ "&caseidfromstage="+hashy.get("c_id")+"' , '' , 1000 ,600);\"><li class=\"fa fa-pencil\"></li></button></div>");
		sb.append("</div></td>");
		return sb.toString();
	}
	
	
	
//	@Override
//	public void initialize(HashMap smartyStateMap) {
//		super.initialize(smartyStateMap);
//		boolean foundSearch = false;
//		String stateInSearch = "";
//		for (String parameter : search_paramval.keySet()) {
//			for (String value : search_paramval.get(parameter)) {
//				if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {
//					if (parameter.equals("c_rcv_state")) {
//						foundSearch = true;
//						stateInSearch = value;
//					}
//				}
//			}
//		}
//		int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
//		if (foundSearch) {
//			MainSql  = "select q_comingfrombranch, c_branchcode, "
//			+ " (select GROUP_CONCAT(path_tobranch SEPARATOR ',') FROM kbpaths where path_state =c_rcv_state and path_frombranch = q_comingfrombranch) as pathinstore,  "
//			+ " c_pickupagent, c_rtnreason, c_receiptamt, date(c_createddt) as c_createddt, c_assignedagent, c_custreceiptnoori, "
//			+ " c_rcv_name , c_qty,q_branch,'' as attempts, c_id , cust_name,  "
//			+ " concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) as address ,c_rcv_state,ifnull(c_rcv_district,'NA') as c_rcv_district,"
//			+ " q_enterdate   , q_stage, q_step , stp_id , q_action,"
//			+ " q_assigned_to , c_rmk ,"
//			+ " case when cc_branchpmtid>0 or c_settled='FULL' or c_agentsharesettled='FULL' or c_pickupagentpmtid>0 then 'flase' else 'true' end as canedit"
//			+ " from p_cases "
//			+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
//			+ " join kbstate on (c_rcv_state = st_code and st_branch="+branchId_G+")  "
//			+ " join kbcustomers on (c_custid = cust_id)"
//			+ " left join p_caseschain on (c_id = cc_caseid and cc_tobranch = "+branchId_G+")"
//			+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
//			+ " where q_stage= 'INIT' and q_step='NEWINSTORE' and q_status !='CLS'"
//			+ " and (q_branch="+branchId_G+" ) and 1=1 ";
//		}
//		
//		
//	}
	//adasds
	public String smartlySelectLiaisonAgent (HashMap<String,String> hashy) {
		StringBuilder sb = new StringBuilder("<td>");
		needToSmartlFindLiaisonAgent = false;
		sb.append("</td>");
		return sb.toString();
	}
	
	public String modifyAction (HashMap<String,String> hashy) {
		StringBuilder sb = new StringBuilder("<td>");
		boolean addedLiasion = false, addAgentOption = false;
		sb.append("<select class='form-control '  onchange=\"change_instorage_action('"+i+"')\" "
				+ " id='q_action_smartyrow_"+i+"' name='q_action_smartyrow_"+i+"' "
				+ "style='text-align:right; background-color:#F0FFF0; color: #424242; border: 1px solid #7dc6dd;'> ");
		ArrayList<String> paths = Utilities.SplitStringToArrayList(hashy.get("pathinstore"),",");
		
		if (paths.size()>0) {
			StringBuilder sbOptions = new StringBuilder("");
			for (String path : paths) {
				if (path.equalsIgnoreCase(hashy.get("q_comingfrombranch"))) {
					sbOptions.append("<option value='ASSGN_AGENT'>إسناد إلى مندوب</option> \n");
					addAgentOption = true;
				}else{
					if (!addedLiasion) {
						sbOptions.append("<option value='ASSIGN_LIASIONAGT' >تجهيز لمندوب الإرتباط</option> \n");
						addedLiasion = true;
					}
				}
			}
			if (addedLiasion && addAgentOption) {
				//get suitable paths when there is more than one option, based on district
				needToSmartlFindLiaisonAgent = true;
				sb.append("<option value='' ></option> \n");
			}
			sb.append(sbOptions);
		}else {			
			sb.append("<option value='ASSGN_AGENT'>أسناد إلى مندوب</option> \n");
		}
		//System.out.println(hashy.get("c_partial_return"));
		if(hashy.get("c_partial_return").equals("N")) {
			sb.append("</select></td>");
		}else {
			sb.append("</select><br><br><div style=\"background-color:red; padding:5px; \"><h5 align=\"center\" style=\"color:white;\"> راجع جزئي </h3></div></td>");
		}
		i++;
		return sb.toString();
	}

	
	@Override
	public void parseInputs(HttpServletRequest rqs) {
		keyVal = parseUpdateRqs(rqs);
		
		int rowsNo =0;
		if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
			rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);

		HashMap <Integer,Integer> dlvAgentsMap = new HashMap <Integer,Integer>();
		HashMap <Integer,Integer> liaisonAgentsMap = new HashMap <Integer,Integer>();
		String action = "";
		int id = 0;
		for (int i=1 ; i<=rowsNo ; i++){
			if (inputMap_ori.containsKey("q_action_smartyrow_"+i) &&
					inputMap_ori.get("q_action_smartyrow_"+i)!=null &&
					inputMap_ori.get("q_action_smartyrow_"+i)[0]!=null) {
				action = inputMap_ori.get("q_action_smartyrow_"+i)[0];
				if (action!=null && !action.trim().isEmpty() && !action.trim().equalsIgnoreCase("")
						&& !action.trim().equalsIgnoreCase("null")) {
					
					
					if (action!=null && action.length()>0 && !action.equalsIgnoreCase("")) {
						if (action.equalsIgnoreCase("ASSGN_AGENT")) {
							if (inputMap_ori.get("c_assignedagent_smartyrow_"+i) !=null 
								&& inputMap_ori.get("c_assignedagent_smartyrow_"+i)[0] !=null 
									&& !inputMap_ori.get("c_assignedagent_smartyrow_"+i)[0].trim().equalsIgnoreCase("")) {
								id =Integer.parseInt(inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0]);
								dlvAgentsMap.put(id, Integer.parseInt(inputMap_ori.get("c_assignedagent_smartyrow_"+i)[0]));
								actionsMap.put(id , action);
								cIdList.add(id);
								
							}
						}else if (action.equalsIgnoreCase("ASSIGN_LIASIONAGT")){
							if (inputMap_ori.get("q_assigned_to_smartyrow_"+i) !=null 
									&& inputMap_ori.get("q_assigned_to_smartyrow_"+i)[0] !=null 
										&& !inputMap_ori.get("q_assigned_to_smartyrow_"+i)[0].trim().equalsIgnoreCase("")) {
								id =Integer.parseInt(inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0]);
								liaisonAgentsMap.put(id, Integer.parseInt(inputMap_ori.get("q_assigned_to_smartyrow_"+i)[0]));
								actionsMap.put(id , action);
								cIdList.add(id);
							}
						}
					}
				}
			}
		}
		queueActionsParamsBean.setDlvAgentMap(dlvAgentsMap);
		queueActionsParamsBean.setLiaisonAgentsMap(liaisonAgentsMap);
	}
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		//System.out.println("inside the initinstorage");
		int userid = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		
		try{
			parseInputs(rqs);
			processData(conn, userid, userstorecode,  "INIT" , "NEWINSTORE", new  HashMap<Integer, String>());
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}
		return "تم الحفظ";
	}
	@Override
	public void processData(Connection conn, int actionTakenBy, int currentBranch, String currentStage,
			String currentStep, HashMap<Integer, String> qRmkMap) throws Exception {
		PreparedStatement pstUpdateCaseAgent = null;
		FlowUtils fu = new FlowUtils();
		UtilitiesFeqar utf = new UtilitiesFeqar();
		HashMap <Integer,Integer> dlvAgentsMap = queueActionsParamsBean.getDlvAgentMap();
		HashMap <Integer,Integer> liaisonAgentsMap = queueActionsParamsBean.getLiaisonAgentsMap();
		try {
			pstUpdateCaseAgent = conn.prepareStatement("update p_cases set c_dlvagent_manifestid=0, c_assignedagent=? where c_id=?");
			int assignedTo ;
			int caseChainId = 0;
			for (int cid :cIdList){
				assignedTo = 0;
				caseChainId = 0;
				if (dlvAgentsMap.containsKey(cid)  && dlvAgentsMap.get(cid) != null) {
					//if (ut.anyCasesForDlvAgentInStep(conn, "INIT", "PRINTMANIFEST", agentsMap.get(qid)))
					pstUpdateCaseAgent.setInt(1,dlvAgentsMap.get(cid) );
					pstUpdateCaseAgent.setInt(2, cid);
					pstUpdateCaseAgent.executeUpdate();
					pstUpdateCaseAgent.clearParameters();
					assignedTo = dlvAgentsMap.get(cid);
					
				}else if (liaisonAgentsMap.containsKey(cid)  && liaisonAgentsMap.get(cid) != null) {
					//1- find the right path 
					PathBean pathBean= ut.getRightPathForCase(conn, cid, liaisonAgentsMap.get(cid));
					//2- create path for this shipment, means insert into p_caseschain
					caseChainId = ut.createCasePath(conn, pathBean, actionTakenBy);
					assignedTo =  liaisonAgentsMap.get(cid) ;
				}
				utf.updateRuralForSingleCase(conn, cid);
				fu.MoveDecisionStepNext(conn, cid, actionsMap.get(cid) , actionTakenBy ,currentBranch, assignedTo, currentStage, currentStep, qRmkMap.get(cid));
				utf.calcShipmentProfit(conn, cid, currentBranch);
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {pstUpdateCaseAgent.close();}catch(Exception e) {}
		}
		
	}
}