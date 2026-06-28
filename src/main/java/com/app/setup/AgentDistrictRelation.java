package com.app.setup;

import smarty.core.CoreMgr;

public class AgentDistrictRelation extends CoreMgr {
	public AgentDistrictRelation() {
		MainSql = "select * from kbagent_district "
				+ " join kbusers on us_id = agdi_usid "
				+ "  and agdi_branchcode= {userstorecode}"
				+ "  where agdi_districtcode= {districtIdPopUp}  ";
		canNew = true;
		canDelete = true;
		canEdit = true;
		
		keyCol = "agdi_id";
		mainTable = "kbagent_district";
		
		userDefinedEditCols.add("agdi_agentshare");
		
		userDefinedGridCols.add("agdi_usid");
		userDefinedGridCols.add("agdi_districtcode");
		userDefinedGridCols.add("agdi_agentshare");
		
		userDefinedNewLookups.put("agdi_usid", "select us_id, us_name from kbusers where us_rank = 'DLVAGENT' "
				+ " and  us_branchcode={userstorecode} and us_id not in (select agdi_usid from kbagent_district where agdi_districtcode= {districtIdPopUp}) ");
		
		userDefinedNewColsHtmlType.put("agdi_usid", "DROPLIST");
		
		userDefinedLookups.put("agdi_usid", "select us_id, us_name from kbusers where us_rank = 'DLVAGENT'  ");
		userDefinedColsMustFill.add("agdi_usid");
		userDefinedColsMustFill.add("agdi_agentshare");
	
		userDefinedColLabel.put("agdi_usid", "مندوب");
		userDefinedColLabel.put("agdi_agentshare", "أجرة المندوب لهذة المنطقة");
		userDefinedColLabel.put("agdi_districtcode", "المنطقة");
		
		userDefinedNewCols.add("agdi_districtcode");
		userDefinedNewCols.add("agdi_usid");
		userDefinedNewCols.add("agdi_agentshare");
		userDefinedNewCols.add("agdi_branchcode");
		userDefinedHiddenNewCols.add("agdi_branchcode");
		
		userDefinedReadOnlyNewCols.add("agdi_districtcode");
		userDefinedLookups.put("agdi_districtcode", "select cdi_id, cdi_name from kbcity_district where cdi_id={districtIdPopUp}");
		userDefinedNewColsDefualtValues.put("agdi_districtcode", new String[] {"{districtIdPopUp}"});
		userDefinedNewColsDefualtValues.put("agdi_branchcode", new String[] {"{userstorecode}"});
		userDefinedNewColsDefualtValues.put("agdi_agentshare", new String[] {"0"});
		userDefinedNewColsHtmlType.put("agdi_districtcode", "VARCHAR");
		
		userDefinedCaption = " ";
	}
}
