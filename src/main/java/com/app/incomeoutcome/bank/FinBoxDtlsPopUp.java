package com.app.incomeoutcome.bank;

import java.util.HashMap;
import smarty.core.CoreMgr;

public class FinBoxDtlsPopUp extends CoreMgr{
	public FinBoxDtlsPopUp() {
		MainSql = 
				"select abt_id, abt_createddt, abt_balance_iqd_before, abt_payment_iqd, abt_balance_iqd_after,"
				+ " abt_balance_usd_before, abt_payment_usd, abt_balance_usd_after , "
				+ " concat(abt_action_desc, ' ', abt_entity_full_name_trans_id) as full_desc, abt_safe_impact, "
				+ " abt_table, abt_table_pkcol, abt_table_pkval, abt_table_lookup_col "
				+ "  from p_acctbox_transactions where abt_acctboxid ={FIN_BOX_ACCT_ID_G}"
				+ " and abt_userbranchid = {userstorecode} order by 1 desc";
		
		userDefinedCaption = "تفاصيل مبلغ الصندوق";
		
		UserDefinedPageRows = 1000;
		
		userDefinedGridCols.add("abt_createddt");
		userDefinedGridCols.add("abt_balance_iqd_before");
		userDefinedGridCols.add("abt_payment_iqd");
		userDefinedGridCols.add("abt_balance_iqd_after");
		userDefinedGridCols.add("full_desc");
		userDefinedGridCols.add("abt_balance_usd_before");
		userDefinedGridCols.add("abt_payment_usd");
		userDefinedGridCols.add("abt_balance_usd_after");
		
		userDefinedColLabel.put("abt_createddt", "تاريخ العملية");
		userDefinedColLabel.put("abt_balance_iqd_before", "قبل العملية د.ع");
		userDefinedColLabel.put("abt_payment_iqd", "مبلغ العملية د.ع");
		userDefinedColLabel.put("abt_balance_iqd_after", "بعد العملية د.ع");
		userDefinedColLabel.put("full_desc", "العملية");
		userDefinedColLabel.put("abt_balance_usd_before", "قبل العملية $");
		userDefinedColLabel.put("abt_payment_usd", "مبلغ العملية $");
		userDefinedColLabel.put("abt_balance_usd_after", "بعد العملية $");
		
		userModifyTD.put("abt_payment_usd", "getTransactionAmountUsd({abt_payment_usd},{abt_safe_impact})");
		userModifyTD.put("abt_payment_iqd", "getTransactionAmountIqd({abt_payment_iqd},{abt_safe_impact})");
	}
	//background-color: #bd1e03;
	//background-color: #029ea8;
	
	public String getTransactionAmountUsd(HashMap<String, String> hashy) {
		String tdStyle="";
		if(!hashy.get("abt_payment_usd").equalsIgnoreCase("0.0") && !hashy.get("abt_payment_usd").equalsIgnoreCase("0")) {
			if(hashy.get("abt_safe_impact").equalsIgnoreCase("ADD_SAFE")) {
				 tdStyle="background-color: #029ea8;color:white";
			}else if(hashy.get("abt_safe_impact").equalsIgnoreCase("DEDUCT_SAFE")) {
				 tdStyle="background-color: #bd1e03;color:white";
			}
		}
		String html = "<td style='"+tdStyle+"'>"+numFormat.format(Double.parseDouble(hashy.get("abt_payment_usd")));
		html +="</td>";
		return html;
	}
	
	public String getTransactionAmountIqd(HashMap<String, String> hashy) {
		String tdStyle="";
		if(!hashy.get("abt_payment_iqd").equalsIgnoreCase("0.0") && !hashy.get("abt_payment_iqd").equalsIgnoreCase("0")) {
			if(hashy.get("abt_safe_impact").equalsIgnoreCase("ADD_SAFE")) {
				 tdStyle="background-color: #029ea8; color:white";
			}else if(hashy.get("abt_safe_impact").equalsIgnoreCase("DEDUCT_SAFE")) {
				 tdStyle="background-color: #bd1e03;  color:white";
			}
		}
		String html = "<td style='"+tdStyle+"'>"+numFormat.format(Double.parseDouble(hashy.get("abt_payment_iqd")));
		html +="</td>";
		return html;
	}
}
