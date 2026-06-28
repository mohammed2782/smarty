package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.cases.CaseInformation;
import com.app.util.UtilitiesFeqar;

import smarty.core.CoreMgr;

public class ReceiveFromLiaisonAgentPopUp extends SingleQueue{
	int sequence =0;
	public ReceiveFromLiaisonAgentPopUp() {
		
		String printButton= "'<a href=\"../../PrintReturnedBetweenBranchesSRVL?cc_rtnmanifestid=',cc_rtnmanifestid,'\" "
		+ "class=\"btn btn-sm btn-danger\">طباعة  <i class=\"fa fa-file-pdf-o fa-lg\"></i>'";

		MainSql = "select  concat('راجع من فرع : ',branch_name, ' -  منفيست الارجاع : ',cc_rtnmanifestid, ' بتاريخ ', rlam_date, ' ' , "+printButton+") as groupingcol, '' as pmtCheckBox, "
				+ "cc_tobranch, rtn_desc, cc_frombranch, c_branchcode,  c_dlvagent_manifestid,  (case when path_id is null then 0 else 1 end) as pathinstore,  "
				+ " c_pickupagent, ifnull(c_rtnreason,' ') as c_rtnreason,  c_receiptamt, date(c_createddt) as c_createddt, c_assignedagent, c_custreceiptnoori, "
				+ "c_rcv_name , c_qty,q_branch,'' as attempts, c_id, cust_name,   concat(st_name_ar,' - ', "
				+ "ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) as address ,c_rcv_state, ifnull(c_rcv_district,'NA') as c_rcv_district,"
				+ " q_enterdate  , q_stage, q_step , stp_id , q_action, q_assigned_to , c_rmk, cc_rtnmanifestid, cc_liaisonagentid,"
				+ "  {userstorecode} as current_branch , c_branch_confirm_rtn_state_change  "
				+ " from p_cases  "
				+ " join p_caseschain on cc_caseid = c_id and  cc_frombranch = {userstorecode} "
				+ " left join p_rtnliaisonagent_manifest on rlam_id = cc_rtnmanifestid "
				+ " left join kbrtn_reasons on (c_rtnreason = rtn_code) "
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join kbstate on (c_rcv_state = st_code and st_branch={userstorecode})   "
				+ " join kbcustomers on (c_custid = cust_id ) "
				+ " left join kbpaths on (path_state = c_rcv_state and path_tobranch ={userstorecode} and path_frombranch = q_comingfrombranch) "
				+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
				+ " join kbbranches on (branch_id = cc_tobranch)"
				+ " where cc_tobranch = {tobranch_receiveRtnShipmentBarcodFromLiaisonPopUp} and ("
				+ "  (q_branch={userstorecode} and q_stage= 'BRANCHES' and q_step='RTN_WITHLIAISONAGENT' and q_status ='ACTV')"
				+ ")";	
		//System.out.println(MainSql);
		userDefinedGridCols.clear();
		canFilter = false;
		userDefinedGroupColsOrderBy = "cc_tobranch,  cc_rtnmanifestid";
		userDefined_x_panelclass = "account_x_panel";
		userDefinedGroupByCol = "groupingcol";
		
		UserDefinedPageRows = 5000;
		
		userDefinedGridCols.add("cust_name");
		//userDefinedGridCols.add("cc_rtnmanifestid");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("rtn_desc");
		userDefinedGridCols.add("c_branchcode");
		//userDefinedGridCols.add("cc_tobranch");
		userDefinedGridCols.add("cc_liaisonagentid");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("address");
		//userDefinedGridCols.add("c_rcv_name");
		//userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("q_action");
		userDefinedGridCols.add("pmtCheckBox");
		
		userDefinedColLabel.put("cc_tobranch", "راجع من فرع");
		userDefinedColLabel.put("rtn_desc", "سبب الراجع");
		userDefinedColLabel.put("cc_rtnmanifestid", "رقم المنفيست الارجاع");
		userDefinedColLabel.put("pmtCheckBox", " ");
		
		userDefinedLookups.put("cc_tobranch", "select branch_id, branch_name from kbbranches where branch_id != {userstorecode}");
		
		userDefinedFilterCols.add("cc_tobranch");
		userDefinedFilterCols.add("cc_liaisonagentid");
		userDefinedFilterCols.add("cc_rtnmanifestid");
		
		userDefinedFilterColsHtmlType.put("cc_rtnmanifestid", "INT");
		userDefinedFilterColsHtmlType.put("cc_tobranch", "DROPLIST");
		
		userModifyTD.put("pmtCheckBox", "displayCheckBox({c_id},{cc_tobranch}, {current_branch}, {c_rtnreason},{c_branch_confirm_rtn_state_change})");
		userModifyTD.put("q_action", "modifyAction({c_id},{cc_tobranch},  {current_branch}, {c_rtnreason},{c_branch_confirm_rtn_state_change})");
		userModifyTD.put("c_custreceiptnoori", "modifyReceiptNo({c_custreceiptnoori},{c_id})");
		
		userDefinedLookups.put("q_action", "select stpd_code, stpd_desc from kbstep_decision where "
				+ " stpd_stpid in (select stp_id from kbstep where stp_code='RTN_WITHLIAISONAGENT'"
				+ "  and stp_stgcode='BRANCHES') and stpd_onlymbapp='N'"
				+ " and stpd_forrank like '%{userRank}%' ");
		
	}
	
	public String modifyAction (HashMap<String,String> hashy) {
		sequence++;
//		if (hashy.get("c_rtnreason").equalsIgnoreCase("RCV_DIFFERENT_STATE") && hashy.get("current_branch").equalsIgnoreCase("1")
//				&& hashy.get("c_branch_confirm_rtn_state_change").equalsIgnoreCase("N") ){
//			return "<td></td>";
//		}
		StringBuilder sb = new StringBuilder("<td><table class='col-12'><tr><td>");
		sb.append("<select class='form-control' "
				+ " id='q_action_smartyrow_"+hashy.get("c_id")+"' data-single-drop-rtnbranch-"+hashy.get("cc_tobranch")+" ='"+hashy.get("cc_tobranch")+"'  "
						+ "name='q_action_smartyrow_"+sequence+"' "
				+ "style=\"width: 100%\"> "
		+"<option value=''></option>");
		Map <String , String> lookupsmap = colMapValues.get("q_action");
		String selectedItem="";
		if (lookupsmap !=null){
			if (!lookupsmap.isEmpty()){
				for (String code : lookupsmap.keySet()){
					selectedItem = "";
					sb.append("<option value='"+code+"' "+selectedItem+">"
					+lookupsmap.get(code)+"</option> \n");
				}
			}
		}
		sb.append("</select></td></tr>");
		sb.append("</table></td>");
		
		return sb.toString();
	}
	
	public String displayCheckBox (HashMap<String, String> hashy) {
		
		String wrapperDiv = "<div id ='div_wrapper_checkbox_"+hashy.get("c_id")+"'>";
		String checkBoxStyle = " ";
//		if (hashy.get("c_rtnreason").equalsIgnoreCase("RCV_DIFFERENT_STATE") && hashy.get("current_branch").equalsIgnoreCase("1")
//				&& hashy.get("c_branch_confirm_rtn_state_change").equalsIgnoreCase("N")){
//			 wrapperDiv+=  "الزبون في غير محافظة "+"<a href='javascript:changeAcknowlegeRtnChangeStateFlag("+hashy.get("c_id")+")'>"
//			 		+ "<div style='margin-right: 5px;' id='unlock_change_state_"+hashy.get("c_id")+"' class='color-indigator-item bg-danger'></div></a>";
//			 checkBoxStyle = " style='display:none' ";
//		}
		String s = "<input type=\"checkbox\" class=\"flat\" "+checkBoxStyle+" "
				+ " id=\"pmtcheck_"+hashy.get("c_id")+"\"  data-single-check-rtnbranch-"+hashy.get("cc_tobranch")+" ='"+hashy.get("cc_tobranch")+"'  "
						+ "onclick=\"checkBoxRecivedClicked(this, "+hashy.get("c_id")+");\">";
		wrapperDiv +=s;
		wrapperDiv +="</div>";
		return "<td>"+wrapperDiv+"</td>";	
	}
	
	public String modifyReceiptNo(HashMap<String, String> hashy) {
		String s = "";
		s = "<td caseid='"+hashy.get("c_id")+"' id='"+hashy.get("c_custreceiptnoori")+"'>"+hashy.get("c_custreceiptnoori");
		s +="</td>";
		
		return s;	
	}
	
	
	@Override
	public void initialize(HashMap smartyStateMap){
		userDefinedCaption= "<div class=\"row\" style='margin-right:10px;'><div class=\"col-sm-1 col-sm-offset-1\"><label>Barcode</label>"
				+ "</div><div class=\"col-sm-6\"><input type='text' style='color:#424242;background-color:#E9E5E5;' id ='barcode_checker' /></div>";
		super.initialize(smartyStateMap);
	}
	
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		int userid = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		super.parseInputs(rqs);
		try{
			processData(conn, userid,  currentBranch, "BRANCHES", "RTN_WITHLIAISONAGENT", new HashMap<Integer, String> ());
			conn.commit();
		
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}	
		return "Saved";
	}
	
	@Override
	public void processData(
			Connection conn, 
			int actionTakenBy, 
			int currentBranch, 
			String currentStage,
			String currentStep,
			HashMap<Integer, String> qRmk) throws Exception {
		FlowUtils fu = new FlowUtils();
		UtilitiesFeqar ut = new UtilitiesFeqar();
		CaseInformation caseInformation = new CaseInformation();
		for (int cid :cIdList) {
			caseInformation = ut.getSinglCaseInformationFromBranch(conn, cid, currentBranch);
			if (caseInformation.getStepCode().equalsIgnoreCase("PART_SUCC") && caseInformation.getStageCode().equalsIgnoreCase("DLV")) {
				if (actionsMap.get(cid).equalsIgnoreCase("RTN_RCVDFROMLIAISON")) { 
					
					updateStageStepInChain
					(conn,caseInformation.getCurrentChainId(), caseInformation.getParentChainId(), 
							actionsMap.get(cid) , actionTakenBy, currentBranch, caseInformation.getFromBranchCode());
				}
			}else {
				fu.MoveDecisionStepNext(conn, cid, actionsMap.get(cid),actionTakenBy,  currentBranch, currentStage, currentStep, qRmk.get(cid));
			}
		}
		
	}
	
	public void updateStageStepInChain(Connection conn, int chainId, int parentChainId, String action, int actionTakenBy, int userBranch, int qBranch) throws Exception{
		PreparedStatement pst = null;
		try {
			if(userBranch == qBranch) {
				pst = conn.prepareStatement("update p_caseschain "
						+ " set cc_qstatus_frombranch = 'ACTV' ,  cc_qaction_frombranch=? , cc_qactiontakenby_frombranch=?, cc_qstage_frombranch=?, cc_qstep_frombranch=? where cc_id=? ");
				pst.setString(1, action);
				pst.setInt(2, actionTakenBy);
				pst.setString(3, "CNCL");
				pst.setString(4, "RTN_INSTORE");
				pst.setInt(5, chainId);
				pst.executeUpdate();
				try{pst.close();}catch(Exception eRoll){}
			}else {
				pst = conn.prepareStatement("update p_caseschain "
						+ " set cc_qstatus_frombranch = 'CLS' ,  cc_qaction_frombranch=? , cc_qactiontakenby_frombranch=? where cc_id=? ");
				pst.setString(1, action);
				pst.setInt(2, actionTakenBy);
				pst.setInt(3, chainId);
				pst.executeUpdate();
				try{pst.close();}catch(Exception eRoll){}
			
				if (parentChainId >0) {
					pst = conn.prepareStatement("update p_caseschain "
							+ " set cc_qstatus_tobranch = 'ACTV' , cc_qstage_tobranch=?, cc_qstep_tobranch=?, cc_qenterdate_tobranch= now() where cc_id=?");
					pst.setString(1, "BRANCHES" );
					pst.setString(2, "RTN_INSTORE_WAITLIAISON");
					pst.setInt(3,parentChainId );
					pst.executeUpdate();
				}
			}
			
			
		}catch(Exception e) {
			
		}finally {
			//try{rs.close();}catch(Exception eRoll){}
			try{pst.close();}catch(Exception eRoll){}
		}
	}

}
