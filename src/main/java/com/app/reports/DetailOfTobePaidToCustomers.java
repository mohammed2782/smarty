package com.app.reports;

import smarty.core.CoreMgr;

public class DetailOfTobePaidToCustomers extends CoreMgr {
	public DetailOfTobePaidToCustomers(){
		MainSql ="select '' as total, cust_name,c_custid, "
				+ "				SUM(c_receiptamt - c_shipment_cost) as netamt, "
				+ "				SUM(c_receiptamt_usd) as netamt_usd "
				+ "				from p_cases "
				+ "				left join kbcustomers on cust_id=c_custid "
				+ "				where c_settled !='FULL' "
				+ "				and c_allowcustpay = 'Y' "
				+ "				and c_branchcode = {branch_code_finanicalstatus_popup} "
				+ "				and c_pmtid=0 "
				+ "				group by cust_id";
				
//				
//				"select '' as total, mcust_name,"
//				+ " SUM(c_receiptamt - c_shipment_cost) as netamt, "
//				+ " SUM(c_receiptamt_usd) as netamt_usd "
//				+ "from p_cases "
//				+ "left join kb_mastercustomer on mcust_id=c_mastercustid "
//				+ "where c_settled !='FULL' "
//				+ "and c_allowcustpay = 'Y' "
//				+ "and c_branchcode = {branch_code_finanicalstatus_popup} "
//				+ "and c_pmtid=0 "
//				+ "group by c_mastercustid";
		
		userDefinedCaption = "دفوعات مستحقة للزبائن";
		userDefinedGridCols.add("cust_name");		
		userDefinedGridCols.add("netamt");
		userDefinedGridCols.add("netamt_usd");
		
		userDefinedColLabel.put("cust_name", "اسم العميل");
		userDefinedColLabel.put("netamt", "المبلغ دينار عراقي");
		userDefinedColLabel.put("netamt_usd", "المبلغ $");
		
		userDefinedSumCols.add("netamt");
		userDefinedSumCols.add("netamt_usd");
		UserDefinedPageRows = 1000;
		
		userDefinedGroupByCol="total";
	}
}
