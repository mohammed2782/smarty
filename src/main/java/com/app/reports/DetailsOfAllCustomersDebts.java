package com.app.reports;

import smarty.core.CoreMgr;

public class DetailsOfAllCustomersDebts extends CoreMgr {
	public DetailsOfAllCustomersDebts() {
		MainSql = "select debts.* , '' as dummygroup from (select mcust_name, sum(trans_debit_iqd - trans_credit_iqd ) as debt_iqd,"
				+ "  sum(trans_debit_usd - trans_credit_usd ) as debt_usd "
				+ " from p_fin_transactions "
				+ " left join kb_mastercustomer on mcust_id = trans_entity_id and mcust_branchcode = {branch_code_details_customer_debts} "
				+ "  where trans_operationentity ='CUSTOMER'  and trans_deleted = 'N' and trans_initiated_in_branch_id={branch_code_details_customer_debts}"
				+ " and (trans_credit_iqd !=0 or trans_debit_iqd !=0 or trans_debit_usd !=0 or trans_credit_usd !=0)  "
				+ " group by mcust_name) as debts where (debt_iqd !=0  or  debt_usd !=0)  ";
		
		userDefinedGroupByCol = "dummygroup";
		
		userDefinedSumCols.add("debt_iqd");
		userDefinedSumCols.add("debt_usd");
		
		userDefinedColLabel.put("mcust_name", "العميل");
		userDefinedColLabel.put("debt_iqd", "مبلغ الدين دينار عراقي");
		userDefinedColLabel.put("debt_usd", "مبلغ الدين دولار أمريكي");
		userDefinedCaption = "ديون على العملاء";
	}
}
