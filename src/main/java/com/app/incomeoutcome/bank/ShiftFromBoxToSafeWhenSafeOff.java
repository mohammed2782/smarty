package com.app.incomeoutcome.bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;

public class ShiftFromBoxToSafeWhenSafeOff extends CoreMgr{
	public ShiftFromBoxToSafeWhenSafeOff() {
		
		MainSql = " select sum(finiq) as finiq, createdby,'' as ap_transferedtosafe , '' as btnpopup, '' as rmk from ("
				+ "select ap_id, ifnull(sum(ap_amtreceived),0) as finiq, ap_createdby as createdby "
				+ "	from p_agent_payments "
				+ " join kbusers on (ap_agentid = us_id and us_branchcode = {userstorecode} ) "
				+ " where ap_safeid = 0 and ap_safeoff = 'Y'  group by ap_createdby "
				+ " union"
				+ " select bp_id, ifnull(sum(bp_receivedamt),0) as finiq, bp_receivedby as createdby "
				+ "	from p_branch_payments  "
				+ " where bp_safeid = 0 and bp_received = 'Y' and bp_received_branchid = {userstorecode} and bp_safeoff = 'Y' group by bp_receivedby)abc "
				+ " group by createdby ";
		
		mainTable = "p_agent_payments";
		keyCol = "createdby";
		userDefinedGridCols.add("createdby");
		userDefinedGridCols.add("finiq");
		userDefinedGridCols.add("btnpopup");
		//userDefinedGridCols.add("ap_transferedtosafe");
		//userDefinedGridCols.add("rmk");
		
		userDefinedColLabel.put("createdby", "الصندوق");
		userDefinedColLabel.put("finiq", "المبلغ د.ع");
		userDefinedColLabel.put("btnpopup", "تفاصيل");
		userDefinedColLabel.put("ap_transferedtosafe", "ترحيل");
		userDefinedColLabel.put("rmk", "ملاحظات");
		
		
		userDefinedEditCols.add("ap_transferedtosafe");
		
		userDefinedEditCols.add("rmk");
		
		userDefinedEditColsHtmlType.put("rmk", "TEXTAREA");
		userDefinedEditColsHtmlType.put("ap_transferedtosafe", "DROPLIST");
		
		userModifyTD.put("btnpopup", "showDtlsOfFinBox({createdby})");
		
		userDefinedLookups.put("ap_transferedtosafe", "select 'Y' , 'نقل للقاصة'  from dual");
		userDefinedLookups.put("createdby", "select us_id, us_name from kbusers");
		
		userDefinedCaption = "المبالغ المستلمة والقاصة مغلقة";
	}
	
	public String showDtlsOfFinBox(HashMap<String,String>hashy) {
		String html ="<td>";
		html +="<a href='#' class='btn btn-xs btn-primary' "
				+ " onclick=\"popitup('./FinBoxDtlsPopUp?finboxacctid="+hashy.get("createdby")+"&safeoff=Y', 'Transactions' , 800,600)\">التفاصيل</a>";
		
		html +="</td>";
		return html;
	}


}
