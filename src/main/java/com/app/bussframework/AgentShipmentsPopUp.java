package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import com.app.util.Utilities;

public class AgentShipmentsPopUp extends CoreMgr {
	int i = 1;
	public AgentShipmentsPopUp() {
		MainSql= "select  cust_name, c_id, c_rcv_hp1,c_rcv_state, "
				+ "concat(st_name_ar,' - ',ifnull(cdi_name,''), ' - ', ifnull(c_rcv_addr_rmk,'')) as addr , "
				+ " c_rmk, c_shipment_cost, c_receiptamt , c_custreceiptnoori, q_action, '' as manifestcheckbox, '' as newagent "
				+ " from p_cases  "
				+ " left join kbcustomers on cust_id = c_custid "
				+ " left join kbcity_district on cdi_id = c_rcv_district and cdi_stcode = c_rcv_state "
				+ " left join kbstate on st_code = c_rcv_state and st_branch = {userstorecode}"
				+ " where q_stage ='INIT' and q_step='PRINTMANIFEST' and c_assignedagent={ASSIGNED_AGENT_MANIFEST_SHIPMENT_POPUP} "
				+ " and q_branch={userstorecode} order by c_rcv_state ";
		
		
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_id");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("q_action");
		userDefinedGridCols.add("newagent");
		userDefinedGridCols.add("manifestcheckbox");
		
		mainTable = "p_cases";
		keyCol = "c_id";
		displayMode = "GRIDEDIT";
		
		userDefinedColLabel.put("cust_name", "اسم الزبون");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_rcv_name", "اسم المستلم");
		userDefinedColLabel.put("addr", "عنوان المستلم");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف المستلم");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("c_qty", "العدد");
		userDefinedColLabel.put("q_action", "العملية");
		userDefinedColLabel.put("manifestcheckbox", "");
		userDefinedColLabel.put("newagent","تغيير المندوب");
		
		canEdit = true;
		
		userDefinedEditCols.add("q_action");
		userDefinedEditCols.add("manifestcheckbox");
		userDefinedEditCols.add("newagent");
		
		
		userModifyTD.put("manifestcheckbox", "displayCheckBox({c_id})");
		userModifyTD.put("c_custreceiptnoori", "modifyReceiptNo({c_custreceiptnoori},{c_id})");
		userModifyTD.put("q_action", "modifyAction({c_id})");
		
		userDefinedLookups.put("newagent", "select us_id , us_name from kbusers where us_rank = 'DLVAGENT' and us_branchcode = {userstorecode} ");
		userDefinedLookups.put("q_action", "select stpd_code, stpd_desc from kbstep_decision where "
				+ " stpd_stpid in (select stp_id from kbstep where stpd_code in ('RETURN_TO_STORE', 'CHNGE_AGENT')"
				+ " and stpd_onlymbapp='N' and stpd_forrank like '%{userRank}%') ");
		userDefinedCaption = "عرض كل الشحنات في طباعة المنفيست لمندوبين التوصيل";
		
		UserDefinedPageRows = 500;
	}
	
	public String modifyReceiptNo(HashMap<String, String> hashy) {
		String s = "";
		s = "<td caseid='"+hashy.get("c_id")+"' id='"+hashy.get("c_custreceiptnoori")+"'>"+hashy.get("c_custreceiptnoori");
		s +="</td>";
		
		return s;	
	}
	public String displayCheckBox (HashMap<String, String> hashy) {
		String s = "<td><input type=\"checkbox\" "
				+ " id=\"manifestcheck_"+hashy.get("c_id")+"\" onclick=\"checkBoxmanifestClicked("+hashy.get("c_id")+");\">";
		s +="</td>";
		i++;
		return s;	
	}
	
	public String modifyAction (HashMap<String,String> hashy) {
		StringBuilder sb = new StringBuilder("<td><table class='col-12'><tr><td>");
		sb.append("<select class='form-control'  onchange=\"change_q_action(this, "+i+")\" "
				+ " id='q_action_smartyrow_"+hashy.get("c_id")+"' name='q_action_smartyrow_"+i+"'  "
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
	
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		keyVal = parseUpdateRqs(rqs);
		FlowUtils fu = new FlowUtils();
		int userid = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		PreparedStatement pstUpdateCaseAgent = null, pst = null;
		int rowsNo =0;
		int currentDriver = 0;
		try{
			if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
				rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
			pstUpdateCaseAgent = conn.prepareStatement("update p_cases set c_assignedagent=?, c_dlvagent_manifestid=0, c_agentcheckedbarcode='N' where c_id=?");
			pst = conn.prepareStatement("update p_cases set c_agentcheckedbarcode='N' where c_id=?");
			for (int i=1 ; i<=rowsNo ; i++){
				if(inputMap_ori.get("q_action_smartyrow_"+i) != null && inputMap_ori.get("q_action_smartyrow_"+i)[0].length()>0 
						&& inputMap_ori.get("q_action_smartyrow_"+i)[0].equalsIgnoreCase("RETURN_TO_STORE")) {
					fu.MoveDecisionStepNext(conn, Integer.parseInt(inputMap_ori.get("smarty_c_id_hidden_smartyrow_"+i)[0]), 
							inputMap_ori.get("q_action_smartyrow_"+i)[0], userid, "INIT", "PRINTMANIFEST" , "");
					pst.setString(1, inputMap_ori.get("smarty_c_id_hidden_smartyrow_"+i)[0]);
					pst.executeUpdate();
					pst.clearParameters();
				}else if(inputMap_ori.get("q_action_smartyrow_"+i) != null && inputMap_ori.get("q_action_smartyrow_"+i)[0].length()>0 
						&& inputMap_ori.get("q_action_smartyrow_"+i)[0].equalsIgnoreCase("CHNGE_AGENT")) {
					currentDriver =  Integer.parseInt(inputMap_ori.get("newagent_smartyrow_"+i)[0]);
					
					if (currentDriver>0) {
						pstUpdateCaseAgent.setInt(1, currentDriver);
						pstUpdateCaseAgent.setString(2, inputMap_ori.get("smarty_c_id_hidden_smartyrow_"+i)[0]);
						pstUpdateCaseAgent.executeUpdate();
						pstUpdateCaseAgent.clearParameters();
					}else {
						throw new Exception ("يجب إختيار المندوب عند التغيير");
					}
				}	
			}
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}finally{
			try {pstUpdateCaseAgent.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
				
		return "Saved";
	}
	
}
