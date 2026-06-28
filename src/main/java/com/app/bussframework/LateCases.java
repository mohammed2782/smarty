package com.app.bussframework;

import java.util.ArrayList;
import java.util.HashMap;

import com.app.util.Utilities;

import smarty.core.CoreMgr;


public class LateCases extends CoreMgr {
	public LateCases() {
		MainSql = "select  c_assignedagent , count(*) as tot "
				+ " from  p_cases  "
				+ " left join kbgeneral "
				+ "  	on kbcat1='BRANCHSETTINGS' and kbcat2='GENERAL' and kbcat3='DLVAGENT'"
				+ "  	and kbcat4='{userstorecode}' and kbcode='HOURSLATE'"
				+ " where q_status='ACTV' " 
				+ " and q_stage = 'AGENTOP'"
				+ " and c_dategiventodlvagent < date_add(now(), interval -(ifnull(kbdesc,72)) hour)"
				+ " and q_branch = {userstorecode} "
				+ " group by c_assignedagent ";
		
		
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("tot");
		
		
		userDefinedColLabel.put("c_assignedagent", "المندوب");
		userDefinedColLabel.put("tot" , "عدد الشحنات");
		
		
		userModifyTD.put("tot", "getPopUpLink({tot},{c_assignedagent})");
		userDefinedLookups.put("c_assignedagent", "select us_id , us_name from kbusers where us_rank='DLVAGENT'");
	}
	
	public String getPopUpLink(HashMap<String,String>hashy) {
		String html = "";
			html = "<td>";
			html +="<button type=\"button\" class=\"btn btn-sm btn-danger\""
					+ " onclick=\"popitup ('lateCasesDetailsPerAgent?agentIdLateCases="+hashy.get("c_assignedagent")+"' , '' , 1000 ,600);\">"+hashy.get("tot")+"</button>";
			html +="</td>";
			return html;
	}
	@Override
	public void initialize(HashMap smartyStateMap){
		int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		try {
			userDefinedCaption = "طلبات  عند المندوب منذ "+Utilities.getMaxAllowedHoursForCasesWithAgent(conn, userstorecode)+" ساعه ";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.initialize(smartyStateMap);
	}
}
