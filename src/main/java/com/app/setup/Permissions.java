package com.app.setup;
import smarty.core.CoreMgr;

public class Permissions extends CoreMgr {
	public Permissions(){
		MainSql = " select * from kbpermission ";
		mainTable = "kbpermission";
		keyCol = "p_id";
		canEdit = true;
		canNew = true;
		canDelete = true;
		userDefinedNewColsHtmlType.put("p_submenuids", "CHECKBOX");
		userDefinedNewColsHtmlType.put("p_menuid", "DROPLIST");
		userDefinedLookups.put("p_menuid", "select mt_code , mt_name from kbmenu_tabs");
		userDefinedLookups.put("p_rank_code", "select rank_code , rank_name_en from kbrank");
		userDefinedLookups.put("p_submenuids", "!select sm_id , sm_submenu_name from kbmenu_subtabs where sm_menucode = '{p_menuid}' ");
		userDefinedNewLookups.put("p_submenuids", "!select sm_id , sm_submenu_name from kbmenu_subtabs where sm_menucode = '{p_menuid}' ");
		
		
		userDefinedCaption = "Users Permissions";
		userDefinedNewCaption = "Add new permission";
		userDefinedEditCaption = "Edit Permission";
		
		
		
		userDefinedGridCols.add("p_rank_code");
		userDefinedGridCols.add("p_menuid");
		userDefinedGridCols.add("p_submenuids");
		
		userDefinedColLabel.put("p_rank_code", "Rank");
		userDefinedColLabel.put("p_menuid","Menu");
		userDefinedColLabel.put("p_submenuids", "Sub Menu");
		
		userDefinedNewCols.add("p_rank_code");
		userDefinedNewCols.add("p_menuid");
		userDefinedNewCols.add("p_submenuids");
		
		userDefinedEditCols.add("p_rank_code");
		userDefinedEditCols.add("p_menuid");
		userDefinedEditCols.add("p_submenuids");
		
		userDefinedColsMustFill.add("p_rank_code");
		userDefinedColsMustFill.add("p_menuid");
		userDefinedColsMustFill.add("p_submenuids");
		
	}

}
