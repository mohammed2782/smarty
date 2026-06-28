package com.app.bussframework;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;

public class LiaisonAgentShipmentsBranchesPopUp extends CoreMgr{
	int i = 1;
	public LiaisonAgentShipmentsBranchesPopUp() {
		
		MainSql= "select  cust_name, c_id, c_rcv_name, c_rcv_hp1,c_rcv_state, concat(st_name_ar,' - ',ifnull(cdi_name,''), ' - ', ifnull(c_rcv_addr_rmk,'')) as addr , "
				+ " c_qty, c_rmk, c_shipment_cost, c_receiptamt, c_fragile , c_custreceiptnoori, q_action, '' as manifestcheckbox "
				+ " from p_cases  "
				+ " left join kbcustomers on cust_id = c_custid "
				+ " join p_caseschain on c_id = cc_caseid and cc_frombranch = {userstorecode} and cc_tobranch = {TOBRANCH_BRANCHES_MANIFEST_SHIPMENTPOPUP}"
				+ " left join kbcity_district on cdi_id = c_rcv_district and cdi_stcode = c_rcv_state "
				+ " left join kbstate on st_code = c_rcv_state and st_branch = {userstorecode}"
				+ " where q_stage ='BRANCHES' and q_step='MANIFEST_BRANCHES' order by c_rcv_state ";
		
		//System.out.println(MainSql);
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_id");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("q_action");
		userDefinedGridCols.add("manifestcheckbox");
		
		mainTable = "p_cases";
		keyCol = "c_id";
		displayMode = "GRIDEDIT";
		
		userDefinedColLabel.put("cust_name", "اسم الزبون");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_id", "رقم الشحنة");
		userDefinedColLabel.put("c_rcv_name", "اسم المستلم");
		userDefinedColLabel.put("addr", "عنوان المستلم");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف المستلم");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("c_qty", "العدد");
		userDefinedColLabel.put("q_action", "العملية");
		userDefinedColLabel.put("manifestcheckbox", "");
		
		canEdit = true;
		
		userDefinedEditCols.add("q_action");
		userDefinedEditCols.add("manifestcheckbox");
		
		userModifyTD.put("manifestcheckbox", "displayCheckBox({c_id})");
		userModifyTD.put("c_custreceiptnoori", "modifyReceiptNo({c_custreceiptnoori},{c_id})");
		userModifyTD.put("q_action", "modifyAction({c_id})");
		
		userDefinedLookups.put("q_action", "select stpd_code, stpd_desc from kbstep_decision where "
				+ " stpd_stpid in (select stp_id from kbstep where stpd_code = 'TOINSTORE' and stpd_onlymbapp='N'"
				+ " and stpd_forrank like '%{userRank}%') ");
		userDefinedCaption = "عرض كل الشحنات في طباعة المنفيست للفروع";
		
		UserDefinedPageRows = 100;
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
		sb.append("<select class='form-control'   "
				+ " id='q_action_smartyrow_"+hashy.get("c_id")+"' name='q_action_smartyrow_"+i+"' "
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
		int rowsNo =0;
		try{
			if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
				rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
			
			for (int i=1 ; i<=rowsNo ; i++){
				if(inputMap_ori.get("q_action_smartyrow_"+i) != null && inputMap_ori.get("q_action_smartyrow_"+i)[0].length()>0)
					fu.MoveDecisionStepNext(conn, Integer.parseInt(inputMap_ori.get("smarty_c_id_hidden_smartyrow_"+i)[0]), 
							inputMap_ori.get("q_action_smartyrow_"+i)[0], userid, "BRANCHES", "MANIFEST_BRANCHES", "");
				
			}
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}	
		return "Saved";
	}
	
}