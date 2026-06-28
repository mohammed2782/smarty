package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

public class SingleQueue_BRANCHES_MANIFEST_BRANCHES extends SingleQueue {
	
	
	public SingleQueue_BRANCHES_MANIFEST_BRANCHES() {
		//cc_liaisonagentid
		MainSql  = "select us_name,concat(cc_liaisonagentid,'_myspecialkey_',cc_tobranch) as specialkey, cc_liaisonagentid ,cc_tobranch, cc_frombranch, q_branch,'' as newagent ,"
				+ "  count(*) as  totcases,  '' as  q_action, '' as printmanifest, "
				+ "  sum(case when cc_liaisonbar_printmanifest = 'Y' then 1 else 0 end) as nocheckedbarcod, '' as checkbarcod"
				+ " from p_cases "
				+ " join p_caseschain on c_id = cc_caseid and cc_frombranch = q_comingfrombranch "
				+ " left join kbusers on cc_liaisonagentid = us_id "
				+ " where q_stage= 'BRANCHES' and q_step='MANIFEST_BRANCHES' and q_status='ACTV' "
				+ " and (q_comingfrombranch = {userstorecode} )"
				+ " group by cc_liaisonagentid, cc_tobranch, cc_frombranch ";
		mainTable = "p_cases";
		keyCol = "specialkey";
		userDefinedGridCols.clear();
		userDefinedGridCols.add("cc_tobranch");
		userDefinedGridCols.add("us_name");
		userDefinedGridCols.add("totcases");
		userDefinedGridCols.add("printmanifest");
		userDefinedGridCols.add("checkbarcod");
		userDefinedGridCols.add("nocheckedbarcod");
		userDefinedGridCols.add("q_action");
		//userDefinedGridCols.add("newagent");
		canFilter = false;
		UserDefinedPageRows = 1000;
		
		userDefinedColLabel.put("cc_tobranch","الى فرع");
		userDefinedColLabel.put("totcases","عدد الشحنات");
		userDefinedColLabel.put("us_name","المندوب");
		userDefinedColLabel.put("printmanifest","طباعة المنفيست");
		userDefinedColLabel.put("newagent","تغيير المندوب");
		userDefinedColLabel.put("checkbarcod","");
		userDefinedColLabel.put("nocheckedbarcod","عدد الشحنات المستلمة باركود");
		
		//userDefinedEditCols.add("newagent");
		
		userModifyTD.put("printmanifest", "printManifest({cc_liaisonagentid},{cc_tobranch}, {cc_frombranch})");
		userDefinedLookups.put("newagent", "select us_id , us_name from kbusers where us_rank = 'LIAISONAGENT' ");
		userDefinedLookups.put("cc_tobranch", "select branch_id , branch_name from kbbranches ");
		userModifyTD.put("checkbarcod", "showLinkPopup({cc_liaisonagentid},{cc_tobranch})");
		userModifyTD.put("totcases", "showPopUp({totcases},{cc_tobranch},{cc_liaisonagentid})");
		
		
	}
	public String showLinkPopup(HashMap<String,String>hashy) {
		
		String html = "<td align='center'>";
				html +="<button type=\"button\" "
						+ "class=\"btn btn-sm btn-dark\" "
						+ "onclick=\"popitup ('liaisonBar_printManifest?liaisonfromManifest="+hashy.get("cc_liaisonagentid")+"&tobranchManifest="+hashy.get("cc_tobranch")+"' , '' , 1000 ,600);\">استلام الشحنات باركود</button>";
				html +="</td>";
				return html;
	}

	public String showPopUp(HashMap<String,String>hashy) {
		String HTMLButton= "";
		String btnClass ="btn btn-sm btn-warning";
		String btnText = "عرض جميع الشحنات - العدد "+hashy.get("totcases");
			
		String url ="showManifestBranchesShipmentPopUp?cc_liaisonagentid="+hashy.get("cc_liaisonagentid")+"&cc_tobranch="+hashy.get("cc_tobranch");
		HTMLButton+="<td align='center'><button type='button' class='"+btnClass+"' "
				+ " onclick=\"popitup ('"+url+"' , '' , 800 ,700);\" >"+btnText+"</button></td>";
		return HTMLButton;
	}
	public String printManifest(HashMap<String,String> hashy) {
		
		String button = "<td><a href='../../PrintLiaisonAgentManifestSRVL?genratemanifestid=true&liaisonagentid="+
		hashy.get("cc_liaisonagentid")+"&stg_code=BRANCHES&stp_code=MANIFEST_BRANCHES&frombranch="+hashy.get("cc_frombranch")+"&tobranch="+hashy.get("cc_tobranch")+"'>"
					+"<input type='button'  class='btn btn-danger btn-sm' value='PDF طباعة المنفبست' /></a> &nbsp;";
		
		/*button += "<a href='../../PrintDriverManifestExcelSRVL?genratemanifestid=true&driverid="+
				hashy.get("c_assignedagent")+"&stg_code=BRANCHES&stp_code=PRINTMANIFEST&storecode="+hashy.get("c_branchcode")+"'>"
				+"<input type='button'  class='btn btn-success btn-sm' value='طباعة المنفبست excel' /></a>";*/
		
		button+= "</td>";
		
		
		return button;
	}
	
	/*
	///////////
	BIG IMPORATNT THING TO KNOW: IF YOU RETURN FROM THIS STEP TO NEWINSTORE STEP YOU MUST DELETE THE LATEST PATH, SO IMPORTANT
	*/////
	
	@Override
	public void parseInputs(HttpServletRequest rqs) {
		// TODO Auto-generated method stub
		super.parseInputs(rqs);
	}
	
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		PreparedStatement pstUpdateChainCase=null;
		int userid = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		int fromBranch = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		keyVal = parseUpdateRqs(rqs);
		FlowUtils fu = new FlowUtils();
		Utilities ut = new Utilities();
		UtilitiesFeqar utf = new UtilitiesFeqar();
		int rowsNo =0;
		try{
			if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
				rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
			
			HashMap<String, String> toBranchliaisonagentActionsMap  = new HashMap<String, String>();
			HashMap<String,Integer> keyAgentMap = new HashMap<String,Integer>();
			HashMap<String,Integer> keyToBranchMap = new HashMap<String,Integer>();
			HashMap<String, Integer> newDriversMap  = new HashMap<String, Integer>();
			String action = "";
			String specialKey ="";
			for (int i=1 ; i<=rowsNo ; i++){
				action = inputMap_ori.get("q_action_smartyrow_"+i)[0];
				
				if (action!=null && !action.trim().isEmpty() && !action.trim().equalsIgnoreCase("")
						&& !action.trim().equalsIgnoreCase("null")) {
					specialKey = inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0];
					String  [] agentPath= specialKey.split("_myspecialkey_");
					keyToBranchMap.put(specialKey, Integer.parseInt(agentPath[1]));
					keyAgentMap.put(specialKey, Integer.parseInt(agentPath[0]));
					toBranchliaisonagentActionsMap.put(specialKey, action);
					if (action.equalsIgnoreCase("CHNGE_LIAISONAGENT")) {
						newDriversMap.put(specialKey,Integer.parseInt(inputMap_ori.get("newagent_smartyrow_"+i)[0]));
					}	
				}
			}

			pstUpdateChainCase = conn.prepareStatement("update p_caseschain set cc_liaisonagentid=?  where cc_id=?");
			//pstUpdateAgentShare = conn.prepareStatement("update p_cases set c_agentshare=?,  c_shipment_cost=? where c_id=(select q_caseid from p_queue where q_id=?)");
			ArrayList<CaseInformation> dlvs = new ArrayList<CaseInformation>();
			int manifestId = 0;
			for (String spKey :keyToBranchMap.keySet()){
				manifestId = 0;
				dlvs = ut.getItemsPerLiaisonAgent(conn, keyAgentMap.get(spKey) ,  "BRANCHES",  "MANIFEST_BRANCHES", keyToBranchMap.get(spKey), fromBranch);
				if (toBranchliaisonagentActionsMap.get(spKey).equalsIgnoreCase("GAVETOLIAISONAGENT")) {
					manifestId =  ut.generateLiaisonAgentManifestIdForCasesInPrintManifest(conn, keyAgentMap.get(spKey), fromBranch, keyToBranchMap.get(spKey), userid);
					ut.assignLiaisonManifestIdToCases(conn, manifestId, dlvs);
				}
					
				for (CaseInformation ci  : dlvs){
					
					if (toBranchliaisonagentActionsMap.get(spKey).equalsIgnoreCase("CHNGE_LIAISONAGENT")) {
						pstUpdateChainCase.setInt(1, newDriversMap.get(spKey));
						pstUpdateChainCase.setInt(2, ci.getLatestChainId());
						pstUpdateChainCase.executeUpdate();
						pstUpdateChainCase.clearParameters();
						ci.setLiaisonAgent(newDriversMap.get(spKey));
					}else if (toBranchliaisonagentActionsMap.get(spKey).equalsIgnoreCase("TOINSTORE")) {
						ci.setLiaisonAgent(0);
						ci.setToBranchCode(fromBranch);
					}
					fu.MoveDecisionStepNext(conn, ci.getCaseid(), toBranchliaisonagentActionsMap.get(spKey), userid,
							ci.getToBranchCode(), ci.getLiaisonAgent(), "BRANCHES", "MANIFEST_BRANCHES", "");
					utf.calcShipmentProfit(conn, ci.getCaseid(), fromBranch);
				}
			}
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}finally{
			try{pstUpdateChainCase.close();}catch(Exception e){}
			
		}			
		return  "تم الحفظ";
	}
}
