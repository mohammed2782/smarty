package com.app.reports;

import smarty.core.CoreMgr;
public class Items_rpt2 extends CoreMgr {
	public Items_rpt2 (){
		//MainSql = " select * From vw_itemsrpt ";
		MainSql= "select gname , subg_name, mbd_itemid, sum(mbd_remaining)as totrem" + 
				" from mbilldtl_in " + 
				" join kbgoods on mbd_itemid = g_id " + 
				" join kbsubgoods on (g_id = subg_gid and mbd_subitemid = subg_id)" + 
				" where mbd_available = 'Y' " + 
				" group by subg_name, gname, mbd_itemid";
		
		
		canFilter = true;
		userDefinedGridCols.add("subg_name");
		userDefinedGridCols.add("totrem");
		
		userDefinedColLabel.put("gname", "بضاعة");
		userDefinedColLabel.put("totrem", "موجود حاليا");
		userDefinedColLabel.put("subg_name", "صنف");
		userDefinedColLabel.put("mbd_itemid", "بضاعة");
		
		userDefinedLookups.put("mbd_itemid","select g_id, gname from kbgoods");
		
		userDefinedGroupByCol ="gname";
		userDefinedFilterCols.add("mbd_itemid");
		userDefinedSumCols.add("totrem");
		userDefinedCaption = "جرد المخزن";
		UserDefinedPageRows = 1000;
	}
}
