package com.app.bussframework;

import java.util.HashMap;

public class SingeQueue_BRANCHES_RTN_WITHLIAISONAGENT extends SingleQueue {
	public SingeQueue_BRANCHES_RTN_WITHLIAISONAGENT() {
		
		MainSql = "select cc_tobranch, cc_rtnmanifestid, cc_liaisonagentid, count(*) noofshipments, c_id, '' as receive "
				+ " from p_cases  "
				+ " join p_caseschain on cc_caseid = c_id and  cc_frombranch = {userstorecode} "
				+ " where ("
				+ "  (q_branch={userstorecode} and q_stage= 'BRANCHES' and q_step='RTN_WITHLIAISONAGENT' and q_status ='ACTV')"
				+ ") group by cc_tobranch order by cc_tobranch, cc_rtnmanifestid";
		
		displayMode = "LIST";
		canEdit = false;
		userDefinedGridCols.clear();
		userDefinedFilterCols.clear();
		
		userDefined_x_panelclass = "account_x_panel";
		
		UserDefinedPageRows = 5000;
		
		userDefinedGridCols.add("cc_liaisonagentid");
		userDefinedGridCols.add("cc_tobranch");
		userDefinedGridCols.add("receive");
		userDefinedColsTypes.put("cc_rtnmanifestid", "VARCHAR");
		
		userDefinedColLabel.put("cc_rtnmanifestid", "رقم المنفيست الارجاع");
		userDefinedColLabel.put("cc_tobranch", "قادمة من فرع");
		userDefinedColLabel.put("receive", " ");
		
		userDefinedLookups.put("cc_tobranch", "select branch_id, branch_name from kbbranches where branch_id != {userstorecode}");
		
		userDefinedFilterCols.add("cc_tobranch");
		userDefinedFilterCols.add("cc_liaisonagentid");
		userDefinedFilterCols.add("cc_rtnmanifestid");
		
		userDefinedFilterColsHtmlType.put("cc_rtnmanifestid", "INT");
		userDefinedFilterColsHtmlType.put("cc_tobranch", "DROPLIST");
		
		userModifyTD.put("receive", "displayReceiveButton({cc_tobranch}, {noofshipments})");
		
	}
	
	public String displayReceiveButton(HashMap<String,String>hashy) {
		
		String html = "<td align='center'>";
				html +="<button type=\"button\" "
						+ "class=\"btn btn-sm btn-dark\" "
						+ "onclick=\"popitup ('receiveRtnShipmentBarcodFromLiaisonPopUp?tobranch="+hashy.get("cc_tobranch")+"' , '' , 1000 ,600);\">استلام الشحنات باركود - عدد الشحنات : "+hashy.get("noofshipments")+"</button>";
				html +="</td>";
				return html;
	}

}
