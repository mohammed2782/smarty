package com.app.incomeoutcome;

import smarty.core.CoreMgr;

public class UnderProcessInBranchShipmentPopUp extends CoreMgr{
	public UnderProcessInBranchShipmentPopUp() {
		MainSql = "select c_custreceiptnoori, q_branch, q_step, c_custid, c_receiptamt, c_rcv_hp1, c_createddt, c_rmk "
				+ " from p_cases "
				+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch = {shipmentUnderProcessToBranch} and cc_tobranch = {shipmentUnderProcessFromBranch} )"
				+ " where q_status = 'ACTV' and q_stage not in ('CNCL', 'DLV') and q_branch={shipmentUnderProcessFromBranch} and c_branchcode={shipmentUnderProcessToBranch} "
				+ "order by c_id asc";
		
		
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("q_step");
		//userDefinedGridCols.add("q_branch");
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("c_rmk");
		
		userDefinedColLabel.put("c_mastercustid", "العميل");
		userDefinedColLabel.put("stp_name", "المرحله");
		userDefinedColLabel.put("c_custid", "المتجر");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف المستلم");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("c_custreceiptnoori","رقم الوصل");
		userDefinedColLabel.put("q_step","المرحله");
		userDefinedColLabel.put("c_createddt","تاريخ الشحنه");
		userDefinedColLabel.put("q_branch","في فرع");

		userDefinedLookups.put("q_branch", "select branch_id, branch_name from kbbranches");
		userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers order by cust_name asc");
		userDefinedLookups.put("q_step", "select stp_code , stp_name from kbstep");
		userDefinedLookups.put("c_mastercustid", "select mcust_id , mcust_name from kb_mastercustomer where mcust_branchcode={userstorecode}");
		
		userDefinedCaption = "شحنات معلقة عند الفرع";
		
		UserDefinedPageRows = 1000;
	}

}
