package com.app.reports;

import smarty.core.CoreMgr;

public class DetailsOfAllAgentsDebts extends CoreMgr {
	public DetailsOfAllAgentsDebts() {
		MainSql = "select debts.* , '' as dummygroup from (select us_name, sum(trans_debit_iqd - trans_credit_iqd ) as debt_iqd,"
				+ "  sum(trans_debit_usd - trans_credit_usd ) as debt_usd "
				+ " from p_fin_transactions "
				+ " join kbusers on us_id = trans_entity_id and us_branchcode = {branch_code_details_agents_debts} "
				+ "  where trans_operationentity ='AGENT'  and trans_deleted = 'N' and trans_initiated_in_branch_id={branch_code_details_agents_debts}"
				+ " and (trans_credit_iqd !=0 or trans_debit_iqd !=0 or trans_debit_usd !=0 or trans_credit_usd !=0)  "
				+ " group by us_name) as debts where (debt_iqd !=0  or  debt_usd !=0)  ";
		
		userDefinedGroupByCol = "dummygroup";
		userDefinedColLabel.put("us_name", "المندوب");
		userDefinedColLabel.put("debt_iqd", "مبلغ الدين دينار عراقي");
		userDefinedColLabel.put("debt_usd", "مبلغ الدين دولار أمريكي");
		userDefinedCaption = "ديون على مندوبين التوصيل";
		
		userDefinedSumCols.add("debt_iqd");
		userDefinedSumCols.add("debt_usd");
	}
}
