package com.app.incomeoutcome.bank;

import java.util.HashMap;

import smarty.core.CoreMgr;
import com.app.financials.UtilitiesFinancials;

public class SafeNewStartDtlsPopUp extends CoreMgr {
	public SafeNewStartDtlsPopUp() {
		MainSql = "select *, '' as details from p_safe_hist where hist_last_safid = '{SHOW_UP_TO_SAFEID_G}' order by 1 desc";
		
		userDefinedGridCols.add("saf_iqd_before_transaction");
		userDefinedGridCols.add("saf_amount_iqd");
		userDefinedGridCols.add("saf_amount_usd");
		userDefinedGridCols.add("saf_trantype");
		userDefinedGridCols.add("saf_tranname");
		userDefinedGridCols.add("saf_tranentity");
		userDefinedGridCols.add("saf_trandate");
		userDefinedGridCols.add("saf_createdby");
		userDefinedGridCols.add("saf_rmk");
		//userDefinedGridCols.add("details");
		userDefinedColLabel = UtilitiesFinancials.getSafeTableColumnsName();
		userDefinedLookups.put("saf_trantype", "select kbcode,kbdesc from kbgeneral where kbcat1='TRANSACTION' and kbcat2 ='TYPE' ");
		userDefinedLookups.put("saf_tranname", "!select kbcode,kbdesc from kbgeneral where kbcat1='{saf_trantype}' and kbcat2 ='NAME' ");
		
		//userModifyTD.put("details", "detailsPopUp({saf_tranname},{saf_id})");		

		userDefinedCaption = "تفاصيل معاملات القاصة قبل الجرد";
	}
	public String detailsPopUp(HashMap<String, String> hashy){
		String text = hashy.get("saf_tranname");
		if(text.equalsIgnoreCase("DEPOSIT_OUTBOX"))
			return "<td><a href='#' class='btn btn-xs btn-primary' "
			+ " onclick=\"popitup('./outBoxDtlsPopUp.jsp?safeid="+hashy.get("saf_id")+"', 'Transactions' , 1150,700)\">التفاصيل</a></td>";
		
		else if(!text.equalsIgnoreCase("TRANSFERFROMFINBOX"))
			return "<td></td>";
		
		return "<td><a href='#' class='btn btn-xs btn-primary' "
				+ " onclick=\"popitup('./InboxSafeDtlsPopUp.jsp?safeid="+hashy.get("saf_id")+"', 'Transactions' , 1150,700)\">التفاصيل</a></td>";
		
	}
	@Override
	public void initialize(HashMap smartyStateMap) {
		userDefinedLookups.put("saf_tranentity", "!select code,val from ("
				+ " select co_id as code ,co_name as val from kbcost_type where  '{saf_tranname}' = 'EXPANDITURE' and '{saf_trantype}' = 'DB' "
				+ " union"
				+ " select us_loginid as code, us_name as val from kbusers "
				+ " where '{saf_tranname}' !='EXPANDITURE' and us_active = 'Y' ) abc ");

		super.initialize(smartyStateMap);

	}

}
