package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.cases.CaseInformation;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

public class SingleQueue_BRANCHES_RTN_MANIFEST_LIAISON extends SingleQueue{
	public SingleQueue_BRANCHES_RTN_MANIFEST_LIAISON() {
		
		MainSql  = "select us_name,concat(cc_liaisonagentid,'_myspecialkey_',cc_frombranch) as specialkey, cc_liaisonagentid ,cc_tobranch, cc_frombranch, q_branch,'' as newagent ,"
		+ "  count(*) as  totcases,  '' as  q_action, '' as printrtnmanifest, cc_rtnmanifestid, '' as checkbarcod , '{userRank}' userrank, "
		+ " sum(case when cc_liaisoncheckedbarcode = 'Y' then 1 else 0 end) as nocheckedbarcod "
		+ " from p_cases "
		+ " join p_caseschain on cc_caseid = c_id and  cc_tobranch = {userstorecode} "
		+ " left join kbusers on cc_liaisonagentid = us_id "
		+ " where q_branch={userstorecode} and q_stage= 'BRANCHES' "
		+ " and q_step= 'RTN_MANIFEST_LIAISON'  and q_status ='ACTV'"
		+ " group by cc_liaisonagentid, cc_tobranch, cc_frombranch ";
		mainTable = "p_cases";
		keyCol = "specialkey";
		userDefinedGridCols.clear();
		userDefinedGridCols.add("cc_frombranch");
		userDefinedGridCols.add("us_name");
		userDefinedGridCols.add("totcases");
		userDefinedGridCols.add("checkbarcod");
		userDefinedGridCols.add("nocheckedbarcod");
		userDefinedGridCols.add("printrtnmanifest");
		userDefinedGridCols.add("q_action");
		//userDefinedGridCols.add("newagent");
		canFilter = false;
		UserDefinedPageRows = 1000;
		
		userDefinedColLabel.put("cc_frombranch","الى فرع --");
		userDefinedColLabel.put("totcases","عدد الشحنات");
		userDefinedColLabel.put("us_name","المندوب");
		userDefinedColLabel.put("printrtnmanifest","طباعة المنفيست");
		userDefinedColLabel.put("newagent","تغيير المندوب");
		userDefinedColLabel.put("checkbarcod","");
		userDefinedColLabel.put("nocheckedbarcod","عدد الشحنات المستلمة باركود");
		
		
		//userDefinedEditCols.add("newagent");
		userModifyTD.put("printrtnmanifest", "printRtnManifest({cc_rtnmanifestid},{cc_frombranch},{cc_liaisonagentid})");
		userModifyTD.put("checkbarcod", "showLinkPopup({cc_frombranch},{userrank})");
		userDefinedLookups.put("newagent", "select us_id , us_name from kbusers where us_rank = 'LIAISONAGENT' ");
		userDefinedLookups.put("cc_frombranch", "select branch_id , branch_name from kbbranches ");
		
		
	}
	
	public String showLinkPopup(HashMap<String,String>hashy) {
		String html = "<td></td>";
				html = "<td>";
				html +="<button type=\"button\" style='margin-right:5px;' "
						+ "class=\"btn btn-xs btn-dark\" "
						+ "onclick=\"popitup ('../logistics/liaisonAgentCheckBarcod?popuprtnfrombranch_chain="+hashy.get("cc_frombranch")+"' , '' , 1000 ,600);\">استلام الشحنات باركود</button></br>";
				html +="</td>";
				return html;
	}

	public String showPopUp(HashMap<String,String>hashy) {
		String HTMLButton= "";
		String btnClass ="btn btn-sm btn-dark";
		String btnText = "عرض جميع الشحنات - العدد "+hashy.get("totcases");
			
		String url ="../logistics/showManifestShipmentPopUp?c_assignedagent="+hashy.get("c_assignedagent")+"&q_branch="+hashy.get("c_branchcode");
		HTMLButton+="<td align='center'><button type='button' class='"+btnClass+"' "
				+ " onclick=\"popitup ('"+url+"' , '' , 800 ,700);\" >"+btnText+"</button></td>";
		return HTMLButton;
	}
	public String printRtnManifest(HashMap<String,String> hashy) {
		
		String btn = "<a href=\"../../PrintLiaisonAgentReturnManifestSRVL?"
				+ "rtnmanifestid="+hashy.get("cc_rtnmanifestid")+"&frombranch="+hashy.get("cc_frombranch")+"&liaisonagentid="+hashy.get("cc_liaisonagentid")+"\" "
				+ " class='btn btn-xs btn-warning' >طباعة منفيست الراجع<i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
		
		/*button += "<a href='../../PrintDriverManifestExcelSRVL?genratemanifestid=true&driverid="+
				hashy.get("c_assignedagent")+"&stg_code=BRANCHES&stp_code=PRINTMANIFEST&storecode="+hashy.get("c_branchcode")+"'>"
				+"<input type='button'  class='btn btn-success btn-sm' value='طباعة المنفبست excel' /></a>";*/

	}
	
	/*
	///////////
	BIG IMPORATN THING TO KNOW: IF YOU RETURN FROM THIS STEP TO NEWINSTORE STEP YOU MUST DELETE THE LATEST PATH, SO IMPORTANT
	
	
	
	*/////
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		int userid = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		int toBranch = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		keyVal = parseUpdateRqs(rqs);
		FlowUtils fu = new FlowUtils();
		UtilitiesFeqar ut = new UtilitiesFeqar();
		int rowsNo =0;
		try{
			if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
				rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
			
			HashMap<String, String> toBranchliaisonagentActionsMap  = new HashMap<String, String>();
			HashMap<String,Integer> keyAgentMap = new HashMap<String,Integer>();
			HashMap<String,Integer> keyFromBranchMap = new HashMap<String,Integer>();
			//HashMap<String, Integer> newDriversMap  = new HashMap<String, Integer>();
			String action = "";
			String specialKey ="";
			for (int i=1 ; i<=rowsNo ; i++){
				action = inputMap_ori.get("q_action_smartyrow_"+i)[0];
				
				if (action!=null && !action.trim().isEmpty() && !action.trim().equalsIgnoreCase("")
						&& !action.trim().equalsIgnoreCase("null")) {
					specialKey = inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0];
					String  [] agentPath= specialKey.split("_myspecialkey_");
					keyFromBranchMap.put(specialKey, Integer.parseInt(agentPath[1]));
					keyAgentMap.put(specialKey, Integer.parseInt(agentPath[0]));
					toBranchliaisonagentActionsMap.put(specialKey, action);
				}
			}

			//pstUpdateChainCase = conn.prepareStatement("update p_caseschain set cc_liaisonagentid=?  where cc_id=?");
			//pstUpdateAgentShare = conn.prepareStatement("update p_cases set c_agentshare=?,  c_shipment_cost=? where c_id=(select q_caseid from p_queue where q_id=?)");
			ArrayList<CaseInformation> dlvs = new ArrayList<CaseInformation>();
			int rtnManifestId = 0;
			for (String spKey :keyFromBranchMap.keySet()){
				rtnManifestId = 0;
				rtnManifestId =  ut.generateLiaisonAgentRtnManifest(conn, keyAgentMap.get(spKey), keyFromBranchMap.get(spKey), toBranch, "BRANCHES", "RTN_MANIFEST_LIAISON" , userid);
				dlvs = ut.getRtnItemsPerLiaisonAgentInRtnBranch(conn, keyAgentMap.get(spKey), keyFromBranchMap.get(spKey) , toBranch);
				if (toBranchliaisonagentActionsMap.get(spKey).equalsIgnoreCase("RTN_TOLIAISONAGENT")) {
					ut.assignRTNLiaisonManifestIdToCases(conn, rtnManifestId, dlvs , keyFromBranchMap.get(spKey));
				}
				
				for (CaseInformation ci  : dlvs){
					//System.out.println("step = "+ci.getStepCode()+"  stage = "+ci.getStageCode());
					if(toBranchliaisonagentActionsMap.get(spKey).equalsIgnoreCase("RTN_TOLIAISONAGENT")) {
						if(ci.getStepCode().equalsIgnoreCase("RTN_MANIFEST_LIAISON") && ci.getStageCode().equalsIgnoreCase("BRANCHES")) {
							fu.MoveDecisionStepNext(conn, ci.getCaseid(), toBranchliaisonagentActionsMap.get(spKey), userid, ci.getFromBranchCode(),
									keyAgentMap.get(spKey),ci.getStageCode(),ci.getStepCode(), "");
						}else if(ci.getStepCode().equalsIgnoreCase("PART_SUCC") && ci.getStageCode().equalsIgnoreCase("DLV")) {
							updateStageStepInOneChain(conn, ci.getLatestChainId(), userid);
								
						}else
							throw new Exception("SingleQueue_BRANCHES_RTN_MANIFEST_LIAISON, caseId = "+ci.getCaseid()+" in wrong step 'RTN_MANIFEST_LIAISON', step of case = "+ci.getStepCode()+" please call Mr.nafi");
					}
				}
			}
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}
		return  "تم الحفظ";
	}
	public void updateStageStepInOneChain(Connection conn, int chainId, int actionTakenBy) throws Exception{
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("update p_caseschain set cc_qstatus_tobranch = 'CLS', cc_qaction_tobranch=? , cc_qactiontakenby_tobranch=?, cc_qstep_tobranch=?, cc_qenterdate_tobranch= now()"
					+ "  ,cc_qstatus_frombranch = 'ACTV',  cc_qactiontakenby_tobranch=?, cc_qstage_frombranch=?, cc_qstep_frombranch=?, cc_qenterdate_frombranch= now() where cc_id=? ");
			ps.setString(1, "RTN_TOLIAISONAGENT");
			ps.setInt(2, actionTakenBy);
			ps.setString(3, "RTN_WITHLIAISONAGENT");
			ps.setInt(4, actionTakenBy);
			ps.setString(5, "BRANCHES" );
			ps.setString(6, "RTN_WITHLIAISONAGENT");
			ps.setInt(7, chainId);
			int check = ps.executeUpdate();
			if (check == 0)
				throw new Exception("SingleQueue_BRANCHES_RTN_MANIFEST_LIAISON, update not work ");
			try{ps.close();}catch(Exception eRoll){}		
		}catch(Exception e) {
			throw e;
			
		}finally {
			try{ps.close();}catch(Exception eRoll){}
		}
	}
}