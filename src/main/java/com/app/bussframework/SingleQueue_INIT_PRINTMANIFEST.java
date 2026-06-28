package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.cases.CaseInformation;
import com.app.util.IntegrationFactory;
import com.app.util.IntegrationUtil;
import com.app.util.SystemsIntegration;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

public class SingleQueue_INIT_PRINTMANIFEST extends SingleQueue {
	
	public SingleQueue_INIT_PRINTMANIFEST() {
		
		MainSql  = "select '' as newagent,  us_name, c_branchcode, q_branch, c_assignedagent, "
				+ " concat(c_assignedagent,'_myspecialkey_',q_branch) as specialkey , count(*) as  totcases,  '' as  q_action, '' as printmanifest, "
				+ "  sum(case when c_agentcheckedbarcode = 'Y' then 1 else 0 end) as nocheckedbarcod, '' as checkbarcod"
				+ " from p_cases "
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join kbstate on (c_rcv_state = st_code and st_branch={userstorecode})  "
				+ " left join kbusers on c_assignedagent = us_id "
				+ " where q_stage= 'INIT' and q_step='PRINTMANIFEST' and q_status='ACTV' "
				+ " and (q_comingfrombranch = {userstorecode} )"
				+ " group by c_assignedagent ";
		mainTable = "p_cases";
		keyCol = "specialkey";
		userDefinedGridCols.clear();
		userDefinedGridCols.add("us_name");
		userDefinedGridCols.add("totcases");
		userDefinedGridCols.add("printmanifest");
		userDefinedGridCols.add("checkbarcod");
		userDefinedGridCols.add("nocheckedbarcod");
		userDefinedGridCols.add("q_action");
		userDefinedGridCols.add("newagent");
		canFilter = false;
		UserDefinedPageRows = 1000;
		
		userDefinedColLabel.put("totcases","عدد الشحنات");
		userDefinedColLabel.put("us_name","المندوب");
		userDefinedColLabel.put("printmanifest","طباعة الكشف");
		userDefinedColLabel.put("newagent","تغيير المندوب");
		userDefinedColLabel.put("checkbarcod","");
		userDefinedColLabel.put("nocheckedbarcod","عدد الشحنات المستلمة باركود");
		
		userDefinedEditCols.add("newagent");
		
		userModifyTD.put("printmanifest", "printManifest({c_assignedagent},{q_branch})");
		userModifyTD.put("totcases", "showPopUp({totcases},{q_branch},{c_assignedagent})");
		userModifyTD.put("checkbarcod", "showLinkPopup({c_assignedagent},{q_comingfrombranch})");
		
		userDefinedLookups.put("newagent", "!select us_id , us_name from kbusers where us_rank = 'DLVAGENT' and us_branchcode={userstorecode}  ");
		
		
	}
	
	public String showLinkPopup(HashMap<String,String>hashy) {
		
		String html = "<td align='center'>";
				html +="<button type=\"button\" "
						+ "class=\"btn btn-sm btn-dark\" "
						+ "onclick=\"popitup ('agentCheckBarcod_PRINTMANIFEST?agintidassignedto="+hashy.get("c_assignedagent")+"' , '' , 1000 ,600);\">استلام الشحنات باركود</button>";
				html +="</td>";
				return html;
	}

	public String showPopUp(HashMap<String,String>hashy) {
		String HTMLButton= "";
		String btnClass ="btn btn-sm btn-warning";
		String btnText = "عرض جميع الشحنات - العدد "+hashy.get("totcases");
			
		String url ="showManifestShipmentPopUp?c_assignedagent="+hashy.get("c_assignedagent")+"&q_branch="+hashy.get("q_branch");
		HTMLButton+="<td align='center'><button type='button' class='"+btnClass+"' "
				+ " onclick=\"popitup ('"+url+"' , '' , 1000 ,800);\" >"+btnText+"</button></td>";
		return HTMLButton;
	}
	public String printManifest(HashMap<String,String> hashy) {
		
		String button = "<td><a href='../../PrintDriverManifestSRVL?genratemanifestid=true&driverid="+
		hashy.get("c_assignedagent")+"&stg_code=INIT&stp_code=PRINTMANIFEST&storecode="+hashy.get("q_branch")+"'>"
					+"<input type='button'  class='btn btn-danger btn-sm' value='PDF طباعة الكشف' /></a> &nbsp;";
		button+= "</td>";
		
		
		return button;
	}
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		PreparedStatement pstUpdateCaseAgent=null, pstUpdateAgentShare = null;
		int userid = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		HashMap<String, ArrayList<String>> integrationSystemCases = new HashMap<String, ArrayList<String>> ();
		String integrationSystemCode = "";
		IntegrationUtil iu = new IntegrationUtil();
		
		//super.doUpdate(rqs,false);
		keyVal = parseUpdateRqs(rqs);
		FlowUtils fu = new FlowUtils();
		Utilities ut = new Utilities();
		UtilitiesFeqar utf = new UtilitiesFeqar();
		int rowsNo =0;
		try{
			if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
				rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
			
			HashMap<Integer, String> driverList  = new HashMap<Integer, String>();
			HashMap<Integer, String> driverActionsMap  = new HashMap<Integer, String>();
			HashMap<Integer, Integer> newDriversMap  = new HashMap<Integer, Integer>();
			String action = "";
			String specialKey ="";
			for (int i=1 ; i<=rowsNo ; i++){
				action = inputMap_ori.get("q_action_smartyrow_"+i)[0];
				
				if (action!=null && !action.trim().isEmpty() && !action.trim().equalsIgnoreCase("")
						&& !action.trim().equalsIgnoreCase("null")) {
					
					specialKey = inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0];
					String  [] agentBranch= specialKey.split("_myspecialkey_");
					driverList.put(Integer.parseInt(agentBranch[0]), agentBranch[1]);
					driverActionsMap.put(Integer.parseInt(agentBranch[0]), action);
					if (action.equalsIgnoreCase("CHNGE_AGENT")) {
						newDriversMap.put(Integer.parseInt(agentBranch[0]),Integer.parseInt(inputMap_ori.get("newagent_smartyrow_"+i)[0]));
					}
				}
			}
			
			pstUpdateCaseAgent = conn.prepareStatement("update p_cases set c_assignedagent=? , c_dlvagent_manifestid=0 where c_id=?");
			pstUpdateAgentShare = conn.prepareStatement("update p_cases set c_agentshare=?,  c_shipment_cost=? where c_id=? ");
			ArrayList<CaseInformation> dlvs = new ArrayList<CaseInformation>();
			boolean rural  = false;
			double agentShare = 0.0, shipmentCharges=0.0;
			int currentDriver = 0, manifestId=0;
			for (int driverId :driverList.keySet()){
				manifestId = 0;
				dlvs = ut.getItemsPerDriver(conn,  driverId+"",  "INIT",  "PRINTMANIFEST", Integer.parseInt(driverList.get(driverId)),"ALL", "ALL");
				currentDriver = driverId;
				
				if (driverActionsMap.get(driverId).equalsIgnoreCase("MOVETOAGENT")) {
					manifestId = ut.generateDlvAgentManifestIdForCasesInPrintManifest(conn, currentDriver, userid, currentBranch);
					ut.assignManifestIdToCases(conn, manifestId, dlvs);
					if (manifestId==0) {
						throw new Exception ("لم يتم توليد رقم منفيست للمندوب الرجاء أعادة المحاولة");
					}
				}
				for (CaseInformation ci  : dlvs){
					
					agentShare = 0.0;
					
					if (driverActionsMap.get(driverId).equalsIgnoreCase("CHNGE_AGENT")) {
						currentDriver =  newDriversMap.get(driverId);
						
						if (currentDriver>0) {
							pstUpdateCaseAgent.setInt(1, currentDriver);
							pstUpdateCaseAgent.setInt(2, ci.getCaseid());
							pstUpdateCaseAgent.executeUpdate();
							pstUpdateCaseAgent.clearParameters();
						}else {
							throw new Exception ("يجب إختيار المندوب عند التغيير");
						}
					}
					rural = false;
					if ( ci.getRural().equalsIgnoreCase("Y"))
						rural = true;
					if(ci.getSpecialCase().equalsIgnoreCase("N")) {
						agentShare = ut.calcAgentShipmentChargesShare(conn, currentBranch, ci.getState(), ci.getDistrict() , rural , currentDriver+"" );
						shipmentCharges = ut.calcShipmentChargesBasedOnDestCity(conn, ci.getState(),rural, ci.getMasterSenderId() , ci.getSenderId(),ci.getOrigintingBranch() );
						
						pstUpdateAgentShare.setDouble(1, agentShare);
						pstUpdateAgentShare.setDouble(2, shipmentCharges);
						pstUpdateAgentShare.setInt(3, ci.getCaseid());
						pstUpdateAgentShare.executeUpdate();
						pstUpdateAgentShare.clearParameters();
					}
					if (currentDriver>0) {
						fu.MoveDecisionStepNext(conn, ci.getCaseid(), driverActionsMap.get(driverId), userid, 0 , currentDriver, "INIT" ,"PRINTMANIFEST","");
						utf.calcShipmentProfit(conn, ci.getCaseid(), currentBranch);
					}
					
				}
			}
			
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}finally{
			try {pstUpdateAgentShare.close();}catch(Exception e) {}
			try {pstUpdateCaseAgent.close();}catch(Exception e){}
			
		}
				
		return "Saved";
	}
}
