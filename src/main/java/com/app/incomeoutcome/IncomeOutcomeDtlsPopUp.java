package com.app.incomeoutcome;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class IncomeOutcomeDtlsPopUp extends CoreMgr{
	public IncomeOutcomeDtlsPopUp () {
		MainSql = "select '' as trans_id,  '' as net_iqd, '' as net_usd, '' as trans_entity_id, '' as dummy from dual";
		userDefinedGroupByCol = "dummy";
		userDefinedGroupColsOrderBy = "trans_id";
		userDefinedGridCols.clear();
		userDefinedSumCols.add("net_iqd");
		userDefinedSumCols.add("net_usd");
		groupSumCaption = "المجموع";
		UserDefinedPageRows =1000;
		
		userDefinedGridCols.add("trans_createddt");
		userDefinedGridCols.add("trans_entity_id");
		userDefinedGridCols.add("net_iqd");
		userDefinedGridCols.add("net_usd");
		userDefinedGridCols.add("trans_id");
		
		userDefinedColLabel.put("net_iqd", "المبلغ د.ع");
		userDefinedColLabel.put("net_usd", "المبلغ $");
		userDefinedColLabel.put("trans_entity_id", " ");
		userDefinedColLabel.put("trans_id", "رقم الدفعة");
		userDefinedColLabel.put("trans_createddt", "وقت وتاريخ الحركة");
	}
	
	public void initialize(HashMap smartyStateMap) {
		String tranEntity = replaceVarsinString("{trancode}", arrayGlobals);
		int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		
		if (tranEntity.equalsIgnoreCase("CUSTOMER")){
			userDefinedLookups.put("trans_entity_id", 
					"select mcust_id, mcust_name from kb_mastercustomer where mcust_branchcode="+branchId_G+" " );
		}else if(tranEntity.equalsIgnoreCase("BRANCH")
			|| tranEntity.equalsIgnoreCase("RECEIVE_FROM_BRANCH")) {
			userDefinedLookups.put("trans_entity_id", 
					"select branch_id, branch_name from kbbranches" );
		}else if(tranEntity.equalsIgnoreCase("EXPENSES")) {
			userDefinedLookups.put("trans_entity_id", 
					"select branch_id, branch_name from kbbranches" );
		}else {// AGENT OR PICKUPAGENTS
			userDefinedLookups.put("trans_entity_id", 
					"select us_id, us_name from kbusers where us_branchcode = "+branchId_G+"" );
		}
		super.initialize(smartyStateMap);
		// build the search
		
		String trandate = replaceVarsinString("{trandate}", arrayGlobals);
		String todate = replaceVarsinString("{todate}", arrayGlobals);
		String accttranuserid = replaceVarsinString("{accttranuserid}", arrayGlobals);
		boolean userIdFound = true;
		if(accttranuserid.isEmpty()) {
			userIdFound = false;
		}
		
		
		if (!tranEntity.equalsIgnoreCase("") && userIdFound) {
			if (!tranEntity.equalsIgnoreCase("RECEIVE_FROM_BRANCH")) {
				MainSql = "select trans_createddt, trans_id, '' as dummy , trans_entity_id, "
						+ "trans_operationentity, trans_createdby, "
				+ " (case when kbcat4 = 'ADD_SAFE'  "
				+ "		then trans_amount_paid_actually_iqd  "
				+ "		else -1*trans_amount_paid_actually_iqd end) as net_iqd , "
				+ "	(case when kbcat4 = 'ADD_SAFE'  then trans_amount_paid_actually_usd  "
				+ "		else -1*trans_amount_paid_actually_usd end) as net_usd "
				+ "from p_fin_transactions "
				+ "	 join kbgeneral on kbcat1 =trans_operationentity  "
				+ "		and  kbcat2 = trans_operationcat and kbcode = trans_operationcode "
				+ "	 where  trans_initiated_in_branch_id = "+branchId_G+" and trans_deleted = 'N' "
				+ "	and trans_createdby = "+accttranuserid+" "
				+ " and trans_createddt >= '"+trandate + "' "
				+ " and trans_createddt < date_add('"+todate+"',interval 1 day )"
						+ " and trans_operationentity='"+tranEntity+"' ";
			}else {
				MainSql = "select trans_createddt, trans_id, '' as dummy , "
				+ " trans_initiated_in_branch_id as trans_entity_id, "
				+ " trans_operationentity, trans_createdby, "
				+ " trans_amount_received_actually_iqd as net_iqd , "
				+ "	trans_amount_received_actually_usd as net_usd "
				+ "from p_fin_transactions "
				+ "	 where  trans_entity_id = "+branchId_G+" and trans_deleted = 'N'"
						+ " and trans_did_branch_receive='Y' "
				+ "	and trans_received_by = "+accttranuserid+" "
				+ " and trans_receiveddt >= '"+trandate + "' "
				+ " and trans_receiveddt < date_add('"+todate+"',interval 1 day )"
				+ " and trans_operationentity='BRANCH' ";
				
			}
		}
	}
}
