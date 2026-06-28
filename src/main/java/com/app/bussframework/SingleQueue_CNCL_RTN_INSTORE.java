package com.app.bussframework;

import java.util.HashMap;
import java.util.Map;

import com.app.util.Utilities;

public class SingleQueue_CNCL_RTN_INSTORE extends SingleQueue{
	private int i = 1;
	Utilities ut = new Utilities();
	public SingleQueue_CNCL_RTN_INSTORE() {
		MainSql  = "select c_branchcode,  c_dlvagent_manifestid,  (case when path_id is null then 0 else 1 end) as pathinstore,  c_pickupagent, c_rtnreason, "
				+ " c_receiptamt, c_receiptamt_usd, date(c_createddt) as c_createddt, c_assignedagent, c_custreceiptnoori, "
				+ "c_rcv_name , c_qty,q_branch,'' as attempts, c_id, cust_name,  "
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
				+ " left join p_caseschain on cc_caseid = c_id and  cc_frombranch = {userstorecode} "
				+ " where ("
				+ "  (q_branch={userstorecode} and q_stage= '{stg_code}' and q_step='{stp_code}' and q_status ='ACTV')"
				+ ")  and c_pickupagent_rtnid=0  and c_cust_rtnid=0   ";
		
		userModifyTD.put("q_action", "modifyAction({c_id})");
	}
	
	public String modifyAction (HashMap<String,String> hashy) {
		boolean found = false;
		try {
			found = ut.checkIfCaseWenToChain(conn, Integer.parseInt(hashy.get("c_id")));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder("<td>");
		sb.append("<select class='form-control' onchange=\"change_q_actionColor(this, '"+i+"')\" "
				+ " id='q_action_smartyrow_"+i+"' name='q_action_smartyrow_"+i+"' "
				+ "style='text-align:right; background-color:#F0FFF0; padding: 0 10px 0 10px;  color: #424242; border: 1px solid #7dc6dd;'> "
		+"<option value=''></option>");
		Map <String , String> lookupsmap = colMapValues.get("q_action");
		String selectedItem="";
		if (lookupsmap !=null){
			if (!lookupsmap.isEmpty()){
				for (String code : lookupsmap.keySet()){
					selectedItem = "";
					if (found && code.equalsIgnoreCase("RTN_TO_AGENT"))
						;
					else
						sb.append("<option value='"+code+"' "+selectedItem+">"+lookupsmap.get(code)+"</option> \n");
				}
			}
		}
		sb.append("</td>");
		i++;
		return sb.toString();
	}

}
