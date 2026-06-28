package com.app.setup;

import smarty.core.CoreMgr;

public class AgentDistricts extends CoreMgr{
	public AgentDistricts() {
		MainSql = "select * from kbagent_district where agdi_usid='{districtsusid}'";
		userDefinedCaption = "مناطق المندوب";
		
		canDelete = true;
		canNew = true;
		
		mainTable = "kbagent_district";
		keyCol = "agdi_id";
		
		userDefinedGridCols.add("agdi_usid");
		userDefinedGridCols.add("agdi_districtcode");
		userDefinedGridCols.add("agdi_agentshare");
		
		userDefinedNewCols.add("agdi_usid");
		userDefinedNewCols.add("agdi_districtcode");
		userDefinedNewCols.add("agdi_agentshare");
		userDefinedNewCols.add("agdi_branchcode");
		
		userDefinedNewColsDefualtValues.put("agdi_usid", new String[] {"{districtsusid}"});
		userDefinedNewColsDefualtValues.put("agdi_branchcode", new String[] {"{userstorecode}"});
		userDefinedNewColsDefualtValues.put("agdi_agentshare", new String[] {"0"});
		
		userDefinedReadOnlyNewCols.add("agdi_usid");
		userDefinedReadOnlyNewCols.add("agdi_branchcode");
		
		userDefinedColLabel.put("agdi_usid", "المندوب");
		userDefinedColLabel.put("agdi_districtcode", "المنطقه");
		userDefinedColLabel.put("agdi_agentshare", "حصة المندوب");
		
		userDefinedNewColsHtmlType.put("agdi_agentshare", "INT");
		userDefinedNewColsHtmlType.put("agdi_districtcode", "DROPLIST");
		userDefinedNewColsHtmlType.put("agdi_usid", "DROPLIST");
		
		userDefinedMinValMap.put("agdi_agentshare", "0");
		
		userDefinedLookups.put("agdi_usid", "select us_id, us_name from kbusers");
		//userDefinedNewLookups.put("agdi_districtcode", "select cdi_code, cdi_name from kbcity_district  ");
		userDefinedLookups.put("agdi_districtcode", "select cdi_id, cdi_name from kbcity_district");
		userDefinedNewLookups.put("agdi_districtcode", "select cdi_id, cdi_name "
				+ " from kbcity_district where cdi_stcode in "
				+ " (select branch_state from kbbranches where branch_id = {userstorecode}) ");
		
		userDefinedColsMustFill.add("agdi_usid");
		userDefinedColsMustFill.add("agdi_districtcode");
		userDefinedColsMustFill.add("agdi_agentshare");
	}
}
