package com.app.bussframework;

import smarty.core.CoreMgr;

public class AgentLateCases extends CoreMgr {
	public AgentLateCases() {
		MainSql = "select c_custreceiptnoori,c_rcv_hp1, c_receiptamt, c_receiptamt_usd, cust_name, stp_name ,c_dategiventodlvagent "
		+ " from  p_cases join kbcustomers on c_custid = cust_id "
		+ " join kbstep on q_step = stp_code "
		+ " left join kbgeneral "
		+ "  	on kbcat1='BRANCHSETTINGS' and kbcat2='GENERAL' and kbcat3='DLVAGENT'"
		+ "  	and kbcat4='{userstorecode}' and kbcode='HOURSLATE'"
		+ " where q_status='ACTV' " 
		+ " and q_stage = 'AGENTOP'"
		+ " and c_dategiventodlvagent < date_add(now(), interval -(ifnull(kbdesc,72)) hour)"
				+ " and q_branch = {userstorecode} and c_assignedagent={agentIdLateCases} ";
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("stp_name");
		userDefinedGridCols.add("c_dategiventodlvagent");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("cust_name", "المتجر");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل د.ع");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
		userDefinedColLabel.put("stp_name", "الحالة");
		userDefinedColLabel.put("c_rcv_hp1" , "رقم الهاتف");
		userDefinedColLabel.put("c_dategiventodlvagent", "تاريخ الأعطاء للمندوب");
		
		UserDefinedPageRows = 1000;
		
		canExport = true;
		pdfExport = true;
		
		
		
	}
}
