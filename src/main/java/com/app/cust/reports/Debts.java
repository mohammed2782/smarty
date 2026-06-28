package com.app.cust.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import com.mysql.jdbc.Statement;

public class Debts extends CoreMgr{
	private double totbalance = 0.0; // this is the real value
	private double showTotBalance = 0.0; // this is what to show, because there is rounding
	private int masterCustomerId;
	public Debts() {
		MainSql = "select p_customer_payments.*, '' as amt, '' fromdate, '' todate from p_customer_payments where cp_mastercustid = {mastercustidlogin}  order by 1 desc";
		canNew  = false;
		canDelete = false;
		canFilter = true;
		
		userDefinedFilterCols.add("cp_pmttype");
		userDefinedFilterCols.add("fromdate");
		userDefinedFilterCols.add("todate");
		
		mainTable = "p_customer_payments";
		keyCol = "cp_id";
		
		userDefinedNewCols.add("cp_mastercustid");
		userDefinedNewCols.add("amt");
		userDefinedNewCols.add("cp_rmk");
		userDefinedNewCols.add("cp_pmttype");
		
	
		userDefinedLookups.put("cp_pmttype", "select kbcode, kbdesc from kbgeneral where kbcat1='CUSTOMER' and kbcat2='PMTTYPE' ");
		
		userDefinedGridCols.add("cp_id");
		userDefinedGridCols.add("cp_mastercustid");
		userDefinedGridCols.add("cp_totreceiptsamt");
		userDefinedGridCols.add("cp_amount_paid_actually");
		userDefinedGridCols.add("cp_debt");
		userDefinedGridCols.add("cp_credit");
		userDefinedGridCols.add("cp_pmttype");
		userDefinedGridCols.add("cp_createdby");
		userDefinedGridCols.add("cp_createddt");
		
		userDefinedLookups.put("cp_mastercustid", "select mcust_id, mcust_name from kb_mastercustomer where mcust_id={mastercustidlogin}");
		userDefinedColLabel.put("fromdate", "من تاريخ");
		userDefinedColLabel.put("todate", "إلى تاريخ");
		userDefinedColLabel.put("cp_mastercustid", "العميل");
		userDefinedColLabel.put("amt", "المبلغ");
		userDefinedColLabel.put("cp_rmk", "سبب الدين");
		userDefinedColLabel.put("cp_id", "رقم الوصل");
		userDefinedColLabel.put("cp_amount_paid_actually",  "المبلغ المسدد للعميل");
		userDefinedColLabel.put("cp_totreceiptsamt",  "مبلغ الوصولات");
		userDefinedColLabel.put("cp_createdby", "أنشأ بواسطة");
		
		userDefinedColLabel.put("cp_pmttype", "نوع الدفعه");
		userDefinedColLabel.put("cp_debt", "مدين");
		userDefinedColLabel.put("cp_credit", "دائن");
		userDefinedColLabel.put("cp_pmttype", "نوع الدفعه");
		userDefinedColLabel.put("cp_createddt", "أنشأت بواسطة");
		userDefinedColLabel.put("cp_createddt", "بتاريخ");
		
	
		userDefinedNewColsDefualtValues.put("cp_mastercustid", new String[] {"{mastercustidlogin}"});
		userDefinedReadOnlyNewCols.add("cp_mastercustid");

		userDefinedLookups.put("cp_createdby","select us_id, us_name from kbusers");
		userDefinedNewColsDefualtValues.put("cp_pmttype", new String [] {"GIVEDEBT"});
		
		userDefinedColsMustFill.add("cp_mastercustid");
		userDefinedColsMustFill.add("amt");
		userDefinedColsMustFill.add("cp_rmk");
		userDefinedColsMustFill.add("cp_pmttype");
		userDefinedNewColsHtmlType.put("cp_mastercustid", "DROPLIST");
		
		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
	
	}
	
	
	public void initialize(HashMap smartyStateMap) {
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		double totPmtForRange=0, totDueAmtForRange = 0, openingBalance = 0 , oldDebts = 0, diffDebts = 0, debitBillsTot = 0, debitOtherDebts = 0, 
				debitTotalRefunds = 0, creditReturnedItemsTot = 0, creditPaymentsBill = 0;
		String lastBalanceDay="";
		
		super.initialize(smartyStateMap);
		String fromDate="",toDate="2100-01-01";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null)
						&& (!value.equals(""))) {
					if (parameter.equals("startdate")) {
						fromDate=value;
						foundSearch = true;
					} else if (parameter.equals("enddate")) {
						toDate=value;
					}
				}
			}
		}
		
		if (foundSearch) {
			
			
			double openingBalance_debit = 0;
			double openingBalance_credit = 0;
			if (openingBalance>0)
				openingBalance_debit = openingBalance;
			else
				openingBalance_credit = openingBalance;

			
		}
	
		userDefinedCaption = "<div class='col-4'> تفاصيل حساب العميل ";
		String printBtn = "<a href=\"../../PrintMasterCustomerStatementSRVL?masterCustId="+masterCustomerId+"&fromdate="+fromDate+"&todate="+toDate+"\" class='btn btn-sm btn-danger' >طباعه "
				+ " <i class=\"fa fa-print fa-lg\"></i></a>";
		userDefinedCaption +=printBtn +"</div>";
		
	}
	


	public int getMasterCustomerId() {
		return masterCustomerId;
	}


	public void setMasterCustomerId(int masterCustomerId) {
		this.masterCustomerId = masterCustomerId;
	}
}
