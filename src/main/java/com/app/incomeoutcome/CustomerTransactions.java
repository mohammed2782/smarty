package com.app.incomeoutcome;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;

import com.app.financials.AccountantBoxBean;
import com.app.financials.FinOperationCategory;
import com.app.financials.FinOperationCode;
import com.app.financials.FinOperationEntity;
import com.app.financials.PaymentImpactOnSafe;
import com.app.financials.PaymentType;
import com.app.financials.SafePaymentTypeMetaInfoBean;
import com.app.financials.StandardTransactionBean;
import com.app.financials.UtilitiesSafeFinancials;

import com.app.financials.UtilitiesFinancials;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.util.Utilities;

public class CustomerTransactions extends CoreMgr{
	private int customerId;
	public int getCustomerId() {
		return customerId;
	}


	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}


	public CustomerTransactions() {
		MainSql = "select p_fin_transactions.*, '' as amt_iqd, '' as amt_usd, '' fromdate, '' todate from p_fin_transactions "
				+ " where trans_operationentity='CUSTOMER' and  trans_entity_id= {CUSTOMER_ACCOUNT_FIN_G} "
				+ " and trans_deleted='N' order by 1 desc";
		canNew  = true;
		canDelete = true;
		canFilter = true;
		
		userDefinedFilterCols.add("fromdate");
		userDefinedFilterCols.add("todate");
		
		userDefinedFilterColsHtmlType.put("fromdate", "DATE");
		userDefinedFilterColsHtmlType.put("todate", "DATE");
		
		
		
		mainTable = "p_fin_transactions";
		keyCol = "trans_id";
		
		userDefinedNewCols.add("trans_entity_id");
		userDefinedNewCols.add("trans_operationcode");
		userDefinedNewCols.add("trans_rmk");
		userDefinedNewCols.add("trans_amount_iqd");
		userDefinedNewCols.add("trans_amount_usd");
		
		userDefinedLookups.put("trans_operationcode", "select kbcode, kbdesc from kbgeneral where kbcat1='CUSTOMER' and kbcat2='PMTTYPE' order by kbcat_seq ");
		userDefinedNewLookups.put("trans_operationcode", "select kbcode, kbdesc from kbgeneral where kbcat1='CUSTOMER' "
				+ "and kbcat2='PMTTYPE' and kbcode !='CASES' order by kbcat_seq ");
		
		userDefinedGridCols.add("trans_id");
		userDefinedGridCols.add("trans_operationcode");
		userDefinedGridCols.add("trans_receipts_amt_iqd");
		userDefinedGridCols.add("trans_receipts_amt_usd");
		userDefinedGridCols.add("trans_amount_paid_actually_iqd");
		userDefinedGridCols.add("trans_amount_paid_actually_usd");
		userDefinedGridCols.add("trans_debit_iqd");
		userDefinedGridCols.add("trans_credit_iqd");
		userDefinedGridCols.add("trans_debit_usd");
		userDefinedGridCols.add("trans_credit_usd");
		userDefinedGridCols.add("trans_rmk");
		userDefinedGridCols.add("trans_createdby");
		userDefinedGridCols.add("trans_createddt");
		
		
		userDefinedLookups.put("trans_entity_id", "select mcust_id, mcust_name from kb_mastercustomer where mcust_id={CUSTOMER_ACCOUNT_FIN_G}");
		userDefinedColLabel = UtilitiesFinancials.getFinancialTableColumnsName();
		userDefinedColLabel.put("amt_iqd", "المبلغ د.ع");
		userDefinedColLabel.put("amt_usd", "المبلغ $");
		userDefinedColLabel.put("fromdate", "من تاريخ");
		userDefinedColLabel.put("todate", "الى تاريخ");
	
		userDefinedNewColsDefualtValues.put("trans_entity_id", new String[] {"{CUSTOMER_ACCOUNT_FIN_G}"});
		userDefinedReadOnlyNewCols.add("trans_entity_id");

		userDefinedLookups.put("trans_createdby","select us_id, us_name from kbusers");
	
		userDefinedColsMustFill.add("trans_entity_id");
		userDefinedColsMustFill.add("trans_amount_iqd");
		userDefinedColsMustFill.add("trans_amount_usd");
		
		userDefinedColsMustFill.add("trans_rmk");
		userDefinedColsMustFill.add("trans_operationcode");
		userDefinedNewColsHtmlType.put("trans_entity_id", "DROPLIST");
		userDefinedMinValMap.put("amt", "0");
		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
		userDefinedNewCaption = "أضافة عملية مالية";
		myhtmlmgr.refreshPageOnDelete = true;
		userDefinedNewColsHtmlType.put("trans_amount_iqd", "NUMBER_WITH_COMMAS");	
		userDefinedNewColsHtmlType.put("trans_amount_usd", "NUMBER_WITH_COMMAS");	
		userDefinedMinValMap.put("trans_amount_iqd", 0+"");	
		userDefinedMinValMap.put("trans_amount_usd", 0+"");	
		UserDefinedPageRows = 500;
		userModifyTD.put("trans_id", "printPmtReceipt({trans_id})");
	}
	
	public String printPmtReceipt(HashMap<String, String> hashy) {
		String btn = "<a href=\"../../PaymentReceiptSRVL?trans_id="+hashy.get("trans_id")+"\" "
				+ " class='btn btn-sm btn-danger' >طباعة أيصال دفع <i class=\"fa fa-print fa-lg\"></i> "+hashy.get("trans_id")+"</a>";
		btn += "<br><a href=\"../../CustomerPaymentExcelSRVL?trans_id="+hashy.get("trans_id")+"\" "
				+ " class='btn btn-sm btn-success' >طباعة أيصال دفع <i class=\"fa fa-file-excel-o fa-lg\"></i> "+hashy.get("trans_id")+"</a>";
		
		
		return "<td>" + btn + "</td>";
	}
	
	public void initialize(HashMap smartyStateMap) {
		
		super.initialize(smartyStateMap);
		String fromDate="",toDate="2100-01-01";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null)
						&& (!value.equals(""))) {
					if (parameter.equals("fromdate")) {
						fromDate=value;
						foundSearch = true;
					} else if (parameter.equals("todate")) {
						toDate=value;
						
					}
				}
			}
		}
		int customerAccount_G = Integer.parseInt(replaceVarsinString(" {CUSTOMER_ACCOUNT_FIN_G} ", arrayGlobals).trim());
		if (foundSearch) {
			
			MainSql = "select p_fin_transactions.*, '' as amt_iqd, '' as amt_usd, '' fromdate, '' todate from p_fin_transactions "
					+ " where trans_operationentity='CUSTOMER' and  trans_entity_id= "+customerAccount_G+" "
					+ " and trans_deleted='N' "
					+ " and trans_createddt >= date('"+fromDate+"') and trans_createddt<date(date_add('"+toDate+"',interval 1 day))"
					+ "order by 1 desc";
		}
		
		
		userDefinedCaption = "<div class='col-4'> تفاصيل حساب العميل ";
		String printBtn = "<a href=\"../../PrintMasterCustomerStatementSRVL?masterCustId="+customerAccount_G+"&fromdate="+fromDate+"&todate="+toDate+"\" class='btn btn-sm btn-danger' >طباعه "
				+ " <i class=\"fa fa-print fa-lg\"></i></a>";
		userDefinedCaption +=printBtn +"</div>";
		
	}
	
	@Override
	public StringBuilder genListing(){
		search_paramval.remove("fromdate");
		search_paramval.remove("todate");
		return super.genListing();
	}

	
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autoCommit) {
		String statusMsg = " تم تسجيل الدفعة ";
		int userId_G = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		int custId_G = Integer.parseInt(replaceVarsinString(" {CUSTOMER_ACCOUNT_FIN_G} ", arrayGlobals).trim());
		int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		inputMap_ori = filterRequest(rqs);
		long transactionAmtFormIqd = 0, transactionAmtFormUsd =0, amtPaidActuallyIqd = 0 , amtPaidActuallyUsd=0;
		int pmtId = 0;
		try {
			String rmk = inputMap_ori.get("trans_rmk")[0];
			String pmtType  = inputMap_ori.get("trans_operationcode")[0];
			int masterIdScreen = Integer.parseInt(inputMap_ori.get("trans_entity_id")[0]);
			transactionAmtFormIqd =Long.parseLong(inputMap_ori.get("trans_amount_iqd")[0]);
			transactionAmtFormUsd =Long.parseLong(inputMap_ori.get("trans_amount_usd")[0]);
			if (masterIdScreen != custId_G)
				throw new Exception ("Error, customer in form is ("+masterIdScreen+") and global customer id is ("+custId_G+") are not the same");

			if (transactionAmtFormIqd ==0 &&  transactionAmtFormUsd==0) 
				throw new Exception ("مبلغ العملية المالية غير متوفر"); 
			
			SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = 
					UtilitiesSafeFinancials.getSafePaymentTypeMetaInfoKbgeneral(conn, "CUSTOMER", "PMTTYPE" , pmtType);
			
			// do validation for paidAmt must match the calcualted paidAmt and custid must match the global
			long debitIqd = 0, creditIqd = 0,  debitUsd = 0 , creditUsd = 0;
			amtPaidActuallyIqd = transactionAmtFormIqd;
			amtPaidActuallyUsd = transactionAmtFormUsd;
			if (safePaymentTypeMetaInfoBean.getDbOrCr() == PaymentType.DB) {
				debitIqd = transactionAmtFormIqd;
				debitUsd = transactionAmtFormUsd;
			}else if (safePaymentTypeMetaInfoBean.getDbOrCr() == PaymentType.CR) {
				creditUsd = transactionAmtFormUsd;
				creditIqd = transactionAmtFormIqd;
			}else if (safePaymentTypeMetaInfoBean.getDbOrCr() == PaymentType.BAL) {
				creditIqd = debitIqd = transactionAmtFormIqd;
				creditUsd = debitUsd = transactionAmtFormUsd;
			}
			
			// when no real money impacting the safe. we don't pay actually
			if (safePaymentTypeMetaInfoBean.getSafeImpact() == PaymentImpactOnSafe.NOSAFE) {
				amtPaidActuallyIqd = 0;
				amtPaidActuallyUsd = 0;
			}
			AccountantBoxBean accountantBoxBean= UtilitiesSafeFinancials.GetAccountantBox(conn, userId_G, branchId_G);
			int accountBoxTransactionId = 0;
			if(safePaymentTypeMetaInfoBean.getSafeImpact() != PaymentImpactOnSafe.NOSAFE) {
				accountBoxTransactionId = UtilitiesSafeFinancials.createAcctBoxTransactions(
						conn, 
						pmtId, 
						"p_fin_transactions".toUpperCase(), 
						safePaymentTypeMetaInfoBean.getSafeImpact(), 
						userId_G, 
						amtPaidActuallyIqd,
						amtPaidActuallyUsd,
						branchId_G,
						safePaymentTypeMetaInfoBean.getName() + " - " + "حسابات العميل", 
						"kb_mastercustomer",
						"mcust_id",
						custId_G,
						"mcust_name",
						Utilities.getMasterCustomerName(conn, custId_G),
						userId_G);
			}
			
			StandardTransactionBean standardTransactionBean = new StandardTransactionBean();
			standardTransactionBean.setEntity(FinOperationEntity.CUSTOMER);
			standardTransactionBean.setEntityId(custId_G);
			standardTransactionBean.setCategory(FinOperationCategory.PMTTYPE);
			standardTransactionBean.setCode(FinOperationCode.valueOf(pmtType));
			standardTransactionBean.setInitiatedInBranchId(branchId_G);
			standardTransactionBean.setWhichScreen("حسابات الزبائن");
			standardTransactionBean.setTransactionAmountIqd(transactionAmtFormIqd);
			standardTransactionBean.setTransactionAmountUsd(transactionAmtFormUsd);
			
			standardTransactionBean.setAmountPaidActuallyIqd(amtPaidActuallyIqd);
			standardTransactionBean.setAmountPaidActuallyUsd(amtPaidActuallyUsd);
			standardTransactionBean.setDebitIqd(debitIqd);
			standardTransactionBean.setCreditIqd(creditIqd);
			standardTransactionBean.setDebitUsd(debitUsd);
			standardTransactionBean.setCreditUsd(creditUsd);
			standardTransactionBean.setPayerBox(accountantBoxBean.getBoxId());
			standardTransactionBean.setPayerBoxTransactionId(accountBoxTransactionId);
			standardTransactionBean.setRemarks(rmk);
			int standardStransactionId = 
					UtilitiesStandardFinancials.buildStandardTransaction(
							conn, 
							standardTransactionBean,
							branchId_G, 
							userId_G );
			conn.commit();
		} catch (Exception e) {
			statusMsg = "Error at payment creation, error (" + e.getMessage()+ ")";
			setInsertErrorFlag(true);
			try {conn.rollback();} catch (Exception ignoreE) {}
			e.printStackTrace();
		}
		return statusMsg;
	}
	
	@Override
	public String doDelete(HttpServletRequest rqs) {
		int transactionToBeDeleted = Integer.parseInt(rqs.getParameter(keyCol));
		PreparedStatement pst = null;
		ResultSet rs = null;
		int userId_G = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals));
		int branchId_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		try {
			
			SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = 
					UtilitiesSafeFinancials.getTransactionSafePaymentTypeMetaInfo(conn, transactionToBeDeleted);
			
			if (branchId_G==21 
					&& safePaymentTypeMetaInfoBean.getFinOperationCode() != FinOperationCode.CASES 
					&& userId_G != 18) {
				return "لا يمكن اجراء المسح";
			}
			// if the payment is for cases, then back them up and zerofiy the payment
			if (safePaymentTypeMetaInfoBean.getFinOperationCode() == FinOperationCode.CASES ) {
				pst = conn.prepareStatement("insert into  p_deletedpayment_cases (dpc_dpid , dpc_cid, dpc_createdby) "
						+ " select c_pmtid , c_id , ? from p_cases where c_pmtid =?");
				pst.setInt(1, userId_G);
				pst.setInt(2, transactionToBeDeleted);
				pst.executeUpdate();
				try {pst.close();} catch (Exception e) {}
				
				pst = conn.prepareStatement("update p_cases set c_pmtid=0 , c_settled='NO'  where c_pmtid =?");
				pst.setInt(1, transactionToBeDeleted);
				pst.executeUpdate();
				try {pst.close();} catch (Exception e) {}
			}
			//revertTransaction
			UtilitiesStandardFinancials.deleteTransaction(conn, transactionToBeDeleted, userId_G, branchId_G, "مسح عملية مالية - حسابات العميل");
			conn.commit();

		} catch (Exception e) {
			try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
			logErrorMsg = "";
			e.printStackTrace();
		} finally {
			try {pst.close();} catch (Exception e) {}
		}

		return "تم المسح";
	}// end of doDelete*/
}
