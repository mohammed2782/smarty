package com.app.agent.home;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;
import com.app.util.UtilitiesNafie;
import smarty.db.mysql;
import com.app.bussframework.FlowUtils;
import com.app.bussframework.QueueActionsParamsBean;
import com.app.bussframework.SingleQueue_AGENTOP;

public class SingleAGENTOP_ALLOPS extends SingleQueue_AGENTOP {
	
	public SingleAGENTOP_ALLOPS() {
		
		userDefinedLookups.put("q_action", "select stpd_code, stpd_desc from kbstep_decision where "
				+ " stpd_stpid in (select stp_id from kbstep where stp_code='{stp_code}' and stp_stgcode='AGENTOP') and stpd_onlymbapp='N'"
				+ " and stpd_forrank like '%DLVAGENT%' ");
		
		
	}
	public void initialize(HashMap smartyStateMap) {
		String stpCode = replaceVarsinString("{stp_code}", arrayGlobals).trim();
		String stgCode = "AGENTOP";
		
		int userid = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());

		userDefinedCaption = "";
		super.initialize(smartyStateMap);
		
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
		String dirverButton = "concat ( concat(us_name, '<a href=\"../../PrintDriverManifestSRVL?stdate=" + fromdt
				+ "&todate=" + todt
				+ "&driverid=',c_assignedagent,'&stg_code=',q_stage,'&stp_code=',q_step,'&storecode=',q_branch,'\" style=\"padding-right:20px;\" >"
				+ " <input type=\"button\" value=\" طباعة مانفيست الشحنات \"   class=\"btn btn-dark btn-sm\" ></a>'))   as driver ";

		MainSql = "select  c_receiptamt_usd , q_postopnedto, q_postponedoption,c_dlvagent_manifestid, '' as fromdt, '' as todate, ifnull(c_mbapp_agent_status,'') as c_mbapp_agent_status, c_receiptamt, "
				+ " date(c_createddt) as c_createddt,c_branchcode, c_rcv_name, c_custreceiptnoori , c_qty,q_branch, c_id, cust_name,  "
				+ "  concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,''))  as address ,c_rcv_state,c_rcv_district, "
				+ " q_enterdate , q_stage, q_step , stp_id , q_action, c_dategiventodlvagent, c_rtnreason, c_rcv_hp1, "
				+ " q_assigned_to , c_assignedagent, c_rmk, " + dirverButton + " " + " from p_cases "
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join kbstate on (c_rcv_state = st_code and st_branch= " +userstorecode+ " )  "
				+ " join kbcustomers on (c_custid = cust_id)" + " left join kbusers on c_assignedagent = us_id "
				+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
				+ " where c_assignedagent = " +userid+ " and q_stage='" + stgCode + "' and q_step='" + stpCode + "' and q_status='ACTV' "
				+ " and  c_pmtid=0  ";

		if (foundSearch) {
			if (todt.equalsIgnoreCase("ALL") && !fromdt.equalsIgnoreCase("ALL")) {
				todt = fromdt;
			}
			MainSql += " and (date(c_createddt)>='" + fromdt + "') and (date(c_createddt)<='" + todt + "' ) ";
		}

	}// end of method initialize

	@Override
	public StringBuilder getMultiEditGrid() {
		// System.out.println("calling gen listing---------------------");
		search_paramval.remove("fromdt");
		search_paramval.remove("todate");
		return super.getMultiEditGrid();
	}


}