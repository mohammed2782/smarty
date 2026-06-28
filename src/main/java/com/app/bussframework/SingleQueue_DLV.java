package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;

public class SingleQueue_DLV extends SingleQueue{
	
	public SingleQueue_DLV(){
		MainSql  = "select c_branchcode,  c_dlvagent_manifestid,  "
				+ " (case when path_id is null then 0 else 1 end) as pathinstore,  c_pickupagent, c_rtnreason, "
				+ " c_receiptamt, c_receiptamt_usd, date(c_createddt) as c_createddt, c_assignedagent, c_custreceiptnoori, "
				+ " c_rcv_name , c_qty,q_branch,'' as attempts, c_id, cust_name,  "
				+ " concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) as address ,c_rcv_state,"
				+ " ifnull(c_rcv_district,'NA') as c_rcv_district,"
				+ " q_enterdate  , q_stage, q_step , stp_id , q_action,"
				+ " q_assigned_to , c_rmk "
				+ " from p_cases "
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join kbstate on (c_rcv_state = st_code and st_branch={userstorecode})  "
				+ " join kbcustomers on (c_custid = cust_id )"
				+ " left join p_caseschain on (c_id=cc_caseid and cc_branchpmtid>0)"
				+ " left join kbpaths on (path_state = c_rcv_state and path_tobranch ={userstorecode} and path_frombranch = q_comingfrombranch)"
				+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
				+ " where q_stage= '{stg_code}' and  q_step='{stp_code}' and q_status !='CLS'"
				+ " and q_branch={userstorecode} and c_pmtid=0 and c_agentpmtid=0 and c_pickupagentpmtid=0 and cc_branchpmtid is null ";
		
	}
	public void processData (Connection conn, int actionTakenBy,
			int currentBranch, String currentStage, String currentStep, HashMap<Integer, String> qRmk) throws Exception {
			super.processData(conn, actionTakenBy, currentBranch, currentStage, currentStep, qRmk);
	}
	
}