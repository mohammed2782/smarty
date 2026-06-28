package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.util.UtilitiesFeqar;

import smarty.db.mysql;

public class SingleQueue_BRANCHES_LIAISONAGT_NEWONWAY extends SingleQueue {
	int i = 1;
	public SingleQueue_BRANCHES_LIAISONAGT_NEWONWAY() {
		
		String dirverButton = "concat (us_name, ' (رقم المنفيست ', cc_manifestid, ')  (عدد الشحنات ', "
				+ "  (select count(*) from p_caseschain cchain where cchain.cc_manifestid= mainchain.cc_manifestid ), ')' )   as driver ";
		
		MainSql  = "select '' as fromdt, '' as todate , cc_frombranch, c_rcv_hp1, '' as checkboxconfirm,  cc_liaisonagentid, cc_manifestid, c_dlvagent_manifestid,  c_pickupagent, c_rtnreason, "
				+ " c_receiptamt, c_receiptamt_usd, date(c_createddt) as c_createddt,c_branchcode, c_assignedagent, c_custreceiptnoori, "
				+ "c_rcv_name , c_qty,q_branch,'' as attempts, c_id, cust_name, cc_id,  "
				+ " concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) as address ,c_rcv_state,"
				+ " ifnull(c_rcv_district,'NA') as c_rcv_district,"
				+ " q_enterdate  , q_stage, q_step , stp_id , q_action,"
				+ " q_assigned_to , c_rmk , "+dirverButton+" "
				+ " from p_cases "
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join p_caseschain as mainchain on (mainchain.cc_caseid=c_id and mainchain.cc_tobranch = {userstorecode})"
				+ " join kbstate on (c_rcv_state = st_code and st_branch={userstorecode})   "
				+ " join kbcustomers on (c_custid = cust_id )"
				+ " left join kbusers on us_id = cc_liaisonagentid"
				+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
				+ " where q_stage= '{stg_code}' and q_step='{stp_code}' and q_status !='CLS'"
				+ " and (q_branch={userstorecode})  ";
		//userDefinedGridCols.add("checkboxconfirm");
		
		userDefinedFilterCols.clear();
		//userDefinedGroupByCol = "driver";
		//userDefinedGroupColsOrderBy = "driver, c_rcv_state, c_rcv_district , c_id";
		userDefinedFilterCols.add("cc_liaisonagentid");
		userDefinedFilterCols.add("cc_manifestid");
		userDefinedFilterCols.add("c_rcv_district");
		userDefinedFilterCols.add("fromdt");
		userDefinedFilterCols.add("todate");
		//userDefinedGridCols.remove("checkboxconfirm");
		userDefinedGridCols.add("c_rcv_district");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("checkboxconfirm");
		userDefinedColLabel.put("checkboxconfirm", "<input id='checkAll' type='checkbox'>");
		userDefinedEditCols.add("c_assignedagent");
		userDefinedEditCols.add("c_rcv_district"); 
		userDefinedColLabel.put("c_rcv_district", "المنطقة");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف المستلم");
		userDefinedColLabel.put("fromdt", "بتاريخ");
		userDefinedColLabel.put("todate", "إلى تاريخ");
		UserDefinedPageRows = 3000;
		userDefinedLookups.put("c_assignedagent", "!select us_id , us_name from kbusers where us_id ={c_assignedagent} union "
				+ " select us_id , us_name from kbusers where us_id in " + 
				"	(select us_id   from kbusers where us_rank = 'DLVAGENT' and us_to_state like '%{c_rcv_state}%' and us_active='Y')"
				+ " and us_branchcode = {userstorecode} ");
		
		userDefinedEditColsHtmlType.put("c_assignedagent", "CLASSIC_SELECT");
		userDefinedEditColsHtmlType.put("c_rcv_district", "CLASSIC_SELECT");
		userDefinedFilterColsHtmlType.put("fromdt", "DATE");
		userDefinedFilterColsHtmlType.put("todate", "DATE");
		userModifyTD.put("q_action", "modifyAction({cc_frombranch})");
		
	}
	public String modifyAction(HashMap<String, String> hashy) {
		StringBuilder sb = new StringBuilder("<td>");
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
					if (code.equalsIgnoreCase("NOT_REACHED")) {
						if (hashy.get("cc_frombranch").equalsIgnoreCase("21")
							|| hashy.get("cc_frombranch").equalsIgnoreCase("31")){
							continue;
						}
					}
					sb.append("<option value='" + code + "' " + selectedItem + ">" + lookupsmap.get(code)
							+ "</option> \n");
				}
			}
		}
		sb.append("</select></td>");
		i++;
		return sb.toString();
	}
	


	@Override
	public StringBuilder getMultiEditGrid() {
		search_paramval.remove("fromdt");
		search_paramval.remove("todate");
		return super.getMultiEditGrid();
	}



	@Override
	public void initialize(HashMap smartyStateMap){
		int userStoreCode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		String stpCode = replaceVarsinString("{stp_code}", arrayGlobals).trim();
		String stgCode = replaceVarsinString("{stg_code}", arrayGlobals).trim();

		userDefinedLookups.put("c_rcv_district", "select cdi_id, cdi_name from kbcity_district "
				+ "where cdi_stcode = (select branch_state from kbbranches where branch_id  = {userstorecode}) ");
		super.initialize(smartyStateMap);
		String stp_code = replaceVarsinString("{stp_code}", arrayGlobals).trim();
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder DropDownHtml= new StringBuilder(""), districtDropDownHtml = new StringBuilder("");
		if (stp_code.equalsIgnoreCase("LIAISONAGT_NEWONWAY"))
			try {
				conn = mysql.getConn();
				pst = conn.prepareStatement("select us_id , us_name from kbusers where us_rank = 'DLVAGENT' and us_branchcode=? and us_active='Y' order by us_name");
				pst.setInt(1, userStoreCode);
				rs = pst.executeQuery();
				DropDownHtml.append("<select class='select2' id='globalagentselect' onchange=\"doGlobalSelectForAgents()\" name='globalagentselect' >");
				DropDownHtml.append("<option value=''></option> \n");
				while (rs.next()) {
					DropDownHtml.append("<option value='"+rs.getString("us_id")+"'>"+rs.getString("us_name")+"</option> \n");
				}
				DropDownHtml.append("</select> \n");
				try {rs.close();}catch(Exception e) {/*ignore*/}
				try {pst.close();}catch(Exception e) {/*ignore*/}
				
				
				pst = conn.prepareStatement("select cdi_id, cdi_name from kbcity_district "
						+ "	where cdi_stcode = (select branch_state from kbbranches where branch_id  =?)");
				pst.setInt(1, userStoreCode);
				rs = pst.executeQuery();
				districtDropDownHtml.append("<select class='select2' id='globalDistrictSelect' onchange=\"doGlobalSelectForDistrict()\" "
						+ "name='globalDistrictSelect' >");
				districtDropDownHtml.append("<option value=''></option> \n");
				while (rs.next()) {
					districtDropDownHtml.append("<option value='"+rs.getString("cdi_id")+"'>"+rs.getString("cdi_name")+"</option> \n");
				}
				districtDropDownHtml.append("</select> \n");
				try {rs.close();}catch(Exception e) {/*ignore*/}
				try {pst.close();}catch(Exception e) {/*ignore*/}
				
			userDefinedCaption= "<script>smarty_preventSingleSelectRender = true;</script>"
					+ "<div class='row'><div class='col-2'>	"
					+ " <label for='barcode_checker'>باركود</label>"
					+ "<input type='text'  class=\"form-control\"  "
					+ " style='display:inline; margin-right: 5px;width:auto;' placeholder='أكتب رقم الوصل..' id ='barcode_checker' />"
					+ "</div>"
					+ " <div class=\"col-2\" style='align-self: center;'>"
					+ " <label for=\"allreceived\">إدخال المحدد للمخزن</label>"
					+ " <input id=\"allreceived\"  onclick=\"changeActionAllGlobal(this, 'RECEIVEDFROMLIAISON' , 'allreceived_dlvagent' );\" type=\"checkbox\">" 
					+"  </div>"
			+ " <div class=\"col-2\" style='align-self: center;'>"
			+ " <label for=\"allreceived_dlvagent\">إسناد المحدد للمندوب </label>"
			+ " <input id=\"allreceived_dlvagent\" onclick=\"changeActionAllGlobal(this, 'RCV_DIRECT_ASSIGNAGENT' , 'allreceived');\" type=\"checkbox\">" 
			+"  </div>"
			+ " <div class=\"col-2\" style='align-self: center;'>"+DropDownHtml.toString()+" </div>"
			+ " <div class=\"col-2\" style='align-self: center;'>"+districtDropDownHtml.toString()+" </div></div>";
		
			
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
				
				String dirverButton = "concat (us_name, ' (رقم المنفيست ', cc_manifestid, ')  (عدد الشحنات ', "
						+ "  (select count(*) from p_caseschain cchain where cchain.cc_manifestid= mainchain.cc_manifestid ), ')' )   as driver ";
				
				MainSql  = "select  cc_frombranch, c_rcv_hp1, '' as checkboxconfirm,  cc_liaisonagentid, cc_manifestid, c_dlvagent_manifestid,  c_pickupagent, c_rtnreason, "
						+ " c_receiptamt, c_receiptamt_usd, date(c_createddt) as c_createddt,c_branchcode, c_assignedagent, c_custreceiptnoori, "
						+ "c_rcv_name , c_qty,q_branch,'' as attempts, c_id, cust_name, cc_id,  "
						+ " concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) as address ,c_rcv_state,"
						+ " ifnull(c_rcv_district,'NA') as c_rcv_district,"
						+ " q_enterdate  , q_stage, q_step , stp_id , q_action,"
						+ " q_assigned_to , c_rmk , "+dirverButton+" "
						+ " from p_cases "
						+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
						+ " join p_caseschain as mainchain on (mainchain.cc_caseid=c_id and mainchain.cc_tobranch = "+userStoreCode+") "
						+ " join kbstate on (c_rcv_state = st_code and st_branch= "+userStoreCode+") "
						+ " join kbcustomers on (c_custid = cust_id ) "
						+ " left join kbusers on us_id = cc_liaisonagentid"
						+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
						+ " where q_stage= '"+stgCode+"' and q_step='"+stpCode+"' and q_status !='CLS'"
						+ " and (q_branch="+userStoreCode+")  "
						+ " and (date(c_createddt)>='" + fromdt + "') and (date(c_createddt)<='" + todt + "' ) ";
				
				
				//System.out.println(MainSql);
			}


			
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			try {conn.close();}catch(Exception e) {/*ignore*/}
		}
		userDefinedEditColsHtmlType.put("q_action", "CLASSIC_SELECT");
	}
	
	@Override
	public void parseInputs(HttpServletRequest rqs) {
		keyVal = parseUpdateRqs(rqs);
		int rowsNo =0;
		if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
			rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
		
		HashMap <Integer,Integer> dlvAgentsMap = new HashMap <Integer,Integer>();
		HashMap <Integer,Integer> districtsMap = new HashMap <Integer,Integer>();
		String action = "";
		int id = 0;
		for (int i=1 ; i<=rowsNo ; i++){
			if (inputMap_ori.get("q_action_smartyrow_"+i)!=null && inputMap_ori.get("q_action_smartyrow_"+i)[0]!=null) {
				action = inputMap_ori.get("q_action_smartyrow_"+i)[0];
				if (action!=null && !action.trim().isEmpty() && !action.trim().equalsIgnoreCase("")
						&& !action.trim().equalsIgnoreCase("null")) {
					id =Integer.parseInt(inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0]);
					
					if (action!=null && action.length()>0 && !action.equalsIgnoreCase("")) {
						if (action.equalsIgnoreCase("RCV_DIRECT_ASSIGNAGENT")) {
							if (inputMap_ori.get("c_assignedagent_smartyrow_"+i) !=null 
								&& inputMap_ori.get("c_assignedagent_smartyrow_"+i)[0] !=null 
									&& inputMap_ori.get("c_assignedagent_smartyrow_"+i)[0].trim().length()>0) {
								dlvAgentsMap.put(id, Integer.parseInt(inputMap_ori.get("c_assignedagent_smartyrow_"+i)[0]));
								districtsMap.put(id, Integer.parseInt(inputMap_ori.get("c_rcv_district_smartyrow_"+i)[0]));
							}else
								continue;
						}else if (action.equalsIgnoreCase("RECEIVEDFROMLIAISON")) {
							if (inputMap_ori.get("c_rcv_district_smartyrow_"+i) !=null 
									&& inputMap_ori.get("c_rcv_district_smartyrow_"+i)[0] !=null 
										&& inputMap_ori.get("c_rcv_district_smartyrow_"+i)[0].trim().length()>0) {
								districtsMap.put(id, Integer.parseInt(inputMap_ori.get("c_rcv_district_smartyrow_"+i)[0]));
							}
						}
						actionsMap.put(id , action);
						cIdList.add(id);
					}
				}
			}
		}
		queueActionsParamsBean.setDlvAgentMap(dlvAgentsMap);
		queueActionsParamsBean.setDistrictsMap(districtsMap);
	}
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		int userid = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		try{
			parseInputs(rqs);
			processData(conn, userid, userstorecode,  "BRANCHES" , "LIAISONAGT_NEWONWAY", 
					new HashMap<Integer, String>());
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
			String currentStep, HashMap<Integer, String> qRmk) throws Exception {
		PreparedStatement pstUpdateCaseAgentDistrict = null,  pstUpdateCaseDistrict= null;
		FlowUtils fu = new FlowUtils();
		UtilitiesFeqar utf = new UtilitiesFeqar();
		HashMap <Integer,Integer> dlvAgentsMap = queueActionsParamsBean.getDlvAgentMap();
		HashMap <Integer,Integer> districtsMap = queueActionsParamsBean.getDistrictsMap();
		try {
			pstUpdateCaseAgentDistrict = conn.prepareStatement("update p_cases "
					+ "set c_dlvagent_manifestid=0, c_rcv_district=?, c_assignedagent=? where c_id=?");
			pstUpdateCaseDistrict = conn.prepareStatement("update p_cases "
					+ "set c_dlvagent_manifestid=0, c_rcv_district=? where c_id=?");
			int assignedTo ;
			
			for (int cid :cIdList){
				assignedTo = 0;
				if (actionsMap.get(cid).equalsIgnoreCase("RECEIVEDFROMLIAISON")) {
					if (districtsMap.containsKey(cid)  && districtsMap.get(cid) != null) {
						pstUpdateCaseDistrict.setInt(1,districtsMap.get(cid) );
						pstUpdateCaseDistrict.setInt(2, cid);
						pstUpdateCaseDistrict.executeUpdate();
						pstUpdateCaseDistrict.clearParameters();
					}
				}else {
					if (actionsMap.get(cid).equalsIgnoreCase("RCV_DIRECT_ASSIGNAGENT")){
						if (dlvAgentsMap.containsKey(cid)  && dlvAgentsMap.get(cid) != null) {
							pstUpdateCaseAgentDistrict.setInt(1,districtsMap.get(cid) );
							pstUpdateCaseAgentDistrict.setInt(2,dlvAgentsMap.get(cid) );
							pstUpdateCaseAgentDistrict.setInt(3, cid);
							pstUpdateCaseAgentDistrict.executeUpdate();
							pstUpdateCaseAgentDistrict.clearParameters();
							assignedTo = dlvAgentsMap.get(cid);
						}
					}else if (actionsMap.get(cid).equalsIgnoreCase("NOT_REACHED")){
						;
					}
				}
				utf.updateRuralForSingleCase(conn, cid);
				fu.MoveDecisionStepNext(conn, cid, actionsMap.get(cid) , actionTakenBy ,0, assignedTo, currentStage, currentStep, qRmk.get(cid));
				
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {pstUpdateCaseDistrict.close();}catch(Exception e) {}
			try {pstUpdateCaseAgentDistrict.close();}catch(Exception e) {}
		}
		
	}
}
