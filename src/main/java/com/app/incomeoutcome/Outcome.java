package com.app.incomeoutcome;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;

import com.app.financials.AccountantBoxBean;
import com.app.financials.FinOperationCategory;
import com.app.financials.FinOperationCode;
import com.app.financials.FinOperationEntity;
import com.app.financials.PaymentImpactOnSafe;
import com.app.financials.SafePaymentTypeMetaInfoBean;
import com.app.financials.StandardTransactionBean;
import com.app.financials.UtilitiesSafeFinancials;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

public class Outcome extends CoreMgr{
	public Outcome() {
		MainSql = "select p_fin_transactions.*, '' fromdate, '' todate , '' as dummyggroup "
		+ "from p_fin_transactions where trans_initiated_in_branch_id ={userstorecode} "
		+ "and trans_deleted = 'N' and trans_operationentity = 'EXPENSES'  ";
		
		mainTable = "p_fin_transactions";
		keyCol = "trans_id";
		
		canNew = true;
		canDelete = true;
		canFilter = true;
		userDefinedGroupByCol = "dummyggroup";
		userDefinedGroupColsOrderBy ="trans_id";
		userDefinedGroupSortMode = "desc";
		
		userDefinedGridCols.add("trans_id");
		userDefinedGridCols.add("trans_entity_id");
		userDefinedGridCols.add("trans_amount_paid_actually_iqd");
		userDefinedGridCols.add("trans_amount_paid_actually_usd");
		userDefinedGridCols.add("trans_rmk");
		userDefinedGridCols.add("trans_createdby");
		userDefinedGridCols.add("trans_createddt");
		
		userDefinedNewCols.add("trans_entity_id");
		userDefinedNewCols.add("trans_amount_paid_actually_iqd");
		userDefinedNewCols.add("trans_amount_paid_actually_usd");
		userDefinedNewCols.add("trans_rmk");
		
		userDefinedColLabel.put("trans_entity_id", "رقم الحركة");
		userDefinedColLabel.put("trans_amount_paid_actually_iqd", "المبلغ د.ع");
		userDefinedColLabel.put("trans_amount_paid_actually_usd", "المبلغ $");
		userDefinedColLabel.put("trans_entity_id", "النوع");
		userDefinedColLabel.put("trans_rmk", "ملاحظات");
		userDefinedColLabel.put("fromdate", "من تاريخ");
		userDefinedColLabel.put("todate", "الى تاريخ");
		userDefinedColLabel.put("trans_createdby", "أنشأت بواسطة");
		userDefinedColLabel.put("trans_createddt", "تاريخ المصروف");
		
		userDefinedLookups.put("trans_entity_id", "select co_id, co_name from kbcost_type");
		userDefinedLookups.put("trans_createdby", "select us_id, us_name from kbusers where us_branchcode={userstorecode}"
				+ " and us_rank not "
				+ " in ('DLVAGENT', 'MASTERCUSTOMER', 'PICKUPAGENT', 'FOLLOWUP_EMP', 'LIAISONAGENT', 'SUPPLY_EMP') ");
		
		userDefinedNewColsHtmlType.put("trans_rmk", "TEXTAREA");
		userDefinedNewColsHtmlType.put("trans_amount_paid_actually_iqd", "NUMBER_WITH_COMMAS");	
		userDefinedNewColsHtmlType.put("trans_amount_paid_actually_usd", "NUMBER_WITH_COMMAS");
		
		userDefinedMinValMap.put("trans_amount_paid_actually_iqd", "0");
		userDefinedMinValMap.put("trans_amount_paid_actually_usd", "0");
		userDefinedNewColsDefualtValues.put("trans_amount_paid_actually_usd", new String[]{"0"});
		
		userDefinedFilterCols.add("trans_entity_id");
		userDefinedFilterCols.add("fromdate");
		userDefinedFilterCols.add("todate");
		
		userDefinedFilterColsHtmlType.put("fromdate", "DATE");
		userDefinedFilterColsHtmlType.put("todate", "DATE");
		userDefinedFilterColsHtmlType.put("trans_entity_id", "DROPLIST");
		userDefinedNewColsHtmlType.put("trans_entity_id", "DROPLIST");
		
		userDefinedCaption = "المصروفات";
		userDefinedNewCaption = "مصروف جديد";
		myhtmlmgr.refreshPageOnDelete = true;
		UserDefinedPageRows = 1000;
		
		userDefinedSumCols.add("trans_amount_paid_actually_iqd");
		userDefinedSumCols.add("trans_amount_paid_actually_usd");
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
		int customerAccount_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		if (foundSearch) {
			MainSql = "select p_fin_transactions.*, '' fromdate, '' todate from p_fin_transactions "
					+ " where trans_initiated_in_branch_id ="+customerAccount_G+" "
					+ " and trans_deleted = 'N' and trans_operationentity = 'EXPENSES'"
					+ " and trans_createddt >= date('"+fromDate+"') and trans_createddt<date(date_add('"+toDate+"',interval 1 day))"
					+ " order by 1 desc ";
		}
	}

	@Override
	public StringBuilder genListing(){
		search_paramval.remove("fromdate");
		search_paramval.remove("todate");
		return super.genListing();
	}
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autoCommit) {
		String statusMsg = " تم تسجيل الدفعه ";
		int userId_G = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		inputMap_ori = filterRequest(rqs);
		try {
			long paidAmountIqd = Long.parseLong(inputMap_ori.get("trans_amount_paid_actually_iqd")[0]);
			long paidAmountUsd = Long.parseLong(inputMap_ori.get("trans_amount_paid_actually_usd")[0]);
			int expenseCode = Integer.parseInt(inputMap_ori.get("trans_entity_id")[0]);
			AccountantBoxBean accountantBoxBean= UtilitiesSafeFinancials.GetAccountantBox(conn, userId_G, branchId_G);
			int accountBoxTransactionId = UtilitiesSafeFinancials.createAcctBoxTransactions(
					conn, 
					0, 
					"p_fin_transactions".toUpperCase(), 
					PaymentImpactOnSafe.DEDUCT_SAFE, 
					userId_G, 
					paidAmountIqd,
					paidAmountUsd,
					branchId_G,
					" - " + "مصروفات ", 
					"kbcost_type",
					"co_id",
					expenseCode,
					"co_name",
					"",
					userId_G);
			
			
			StandardTransactionBean standardTransactionBean = new StandardTransactionBean();
			standardTransactionBean.setEntity(FinOperationEntity.EXPENSES);
			standardTransactionBean.setEntityId(expenseCode);
			standardTransactionBean.setCategory(FinOperationCategory.PMTTYPE);
			standardTransactionBean.setCode(FinOperationCode.EXPENSES);
			standardTransactionBean.setInitiatedInBranchId(branchId_G);
			standardTransactionBean.setWhichScreen("المصروفات");
			standardTransactionBean.setTransactionAmountIqd(paidAmountIqd);
			standardTransactionBean.setTransactionAmountUsd(paidAmountUsd);
			
			standardTransactionBean.setAmountPaidActuallyIqd(paidAmountIqd);
			standardTransactionBean.setAmountPaidActuallyUsd(paidAmountUsd);
			standardTransactionBean.setPayerBox(accountantBoxBean.getBoxId());
			standardTransactionBean.setPayerBoxTransactionId(accountBoxTransactionId);
			standardTransactionBean.setRemarks(inputMap_ori.get("trans_rmk")[0]);
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
		int userId_G = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals));
		int branchId_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		try {
			//revertTransaction
			UtilitiesStandardFinancials.deleteTransaction(conn, transactionToBeDeleted, userId_G, branchId_G, "مسح عملية مالية - المصروفات");
			conn.commit();

		} catch (Exception e) {
			try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
			logErrorMsg = "";
			e.printStackTrace();
		}
		return "تم المسح";
	}// end of doDelete*/
}
