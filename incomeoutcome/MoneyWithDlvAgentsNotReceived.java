package com.app.incomeoutcome;

import com.app.core.CoreMgr;

public class MoneyWithDlvAgentsNotReceived extends CoreMgr{
	public MoneyWithDlvAgentsNotReceived() {
		MainSql = "select '' as dummy, c_assignedagent,"
				+ " sum((case when (q_stage='dlv_stg' and q_step='delivered') then c_receiptamt "
				+ "       when (q_stage='cncl' and c_shipmentpaidbycustomer='Y') then c_shipment_cost else 0 end)"
				+ " - "
				+ " (case when ((q_stage='dlv_stg' and q_step='delivered') "
				+ "				or  (q_stage ='cncl' and c_shipmentpaidbycustomer='Y') "
				+ "				or  (q_stage ='cncl' and c_shipmentpaidbysender='Y' )) then c_agentshare else 0 end))as tot"
				+ " from p_cases "
				+ " join p_queue on (c_id= q_caseid and q_status !='CLS')"
				+ " left join kbcustomers on kbcustomers.c_id = c_custid "
				+ " left join kbstate on st_code = c_rcv_state"
				+ " where c_agentsharesettled !='FULL' and c_receiptfromsystem = 'N'  "
				+ " and ( (q_stage='cncl')   or (q_stage='dlv_stg' and q_step='delivered') "
				+ " or (c_shipmentpaidbycustomer='Y' and q_stage='cncl') )"
				+ "group by c_assignedagent";
		
		userDefinedGroupByCol = "dummy";
		userDefinedColLabel.put("c_assignedagent", "مندوب التوصيل");
		
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("tot");
		
		userDefinedLookups.put("c_assignedagent", "select us_id, us_name from kbusers");
		userDefinedColLabel.put("tot", "المبلغ عند المندوب");
		
		userDefinedCaption = "مبالغ عند مندوبين التوصيل";
		
		userDefinedSumCols.add("tot");
	}
}
