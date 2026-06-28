package com.app.incomeoutcome;
import javax.servlet.http.HttpServletRequest;

import com.app.financials.AccountantBoxBean;
import com.app.financials.FinOperationCategory;
import com.app.financials.FinOperationCode;
import com.app.financials.FinOperationEntity;
import com.app.financials.PaymentImpactOnSafe;
import com.app.financials.PaymentType;
import com.app.financials.SafePaymentTypeMetaInfoBean;
import com.app.financials.StandardTransactionBean;
import com.app.financials.UtilitiesFinancials;
import com.app.financials.UtilitiesSafeFinancials;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.util.Utilities;

import smarty.core.CoreMgr;

public class AgentDebtsDtlsPopUp extends CoreMgr{
	public AgentDebtsDtlsPopUp() {

		MainSql= "select * "
				+ " from p_fin_transactions where trans_operationentity='AGENT' "
				+ " and trans_entity_id= {agentaccountdebts} "
				+ " and ( "
					+ "		(  "
					+ "			(trans_credit_iqd !=0 or trans_debit_usd !=0 "
					+ "				or trans_credit_iqd !=0 or trans_debit_iqd !=0"
					+ "			)"
					+ " 		and trans_operationcode='CASES'"
					+ "		)  "
				+ " 		or (trans_operationcode in ('DEBT_SETTLE','REG_DEBT', 'GIVE_DEBT') )"
				+ "		) and trans_deleted = 'N' order by trans_id desc";
		
		canNew = true;
		mainTable = "p_agent_payments";
		
		userDefinedCaption = "تفاصيل ديون مندوب التوصيل";
		

		//Grid columns
		userDefinedGridCols.add("trans_id");
		userDefinedGridCols.add("trans_amount_iqd");
		userDefinedGridCols.add("trans_amount_usd");
		userDefinedGridCols.add("trans_amount_paid_actually_iqd");
		userDefinedGridCols.add("trans_amount_paid_actually_usd");
		userDefinedGridCols.add("trans_debit_iqd");
		userDefinedGridCols.add("trans_credit_iqd");
		userDefinedGridCols.add("trans_debit_usd");
		userDefinedGridCols.add("trans_credit_usd");
		userDefinedGridCols.add("trans_operationcode");
		userDefinedGridCols.add("trans_rmk");
		userDefinedGridCols.add("trans_createdby");
		userDefinedGridCols.add("trans_createddt");
		
		userDefinedReadOnlyNewCols.add("trans_entity_id");
		//userDefinedReadOnlyNewCols.add("ap_paymenttype");

		
		userDefinedColLabel = UtilitiesFinancials.getFinancialTableColumnsName();
		userDefinedColLabel.put("trans_entity_id", "مندوب التوصيل");
		
		userDefinedNewColsHtmlType.put("trans_entity_id", "DROPLIST");
		UserDefinedPageRows = 2000;
		
		userDefinedNewCols.add("trans_entity_id");
		userDefinedNewCols.add("trans_operationcode");
		userDefinedNewCols.add("trans_amount_iqd");
		userDefinedNewCols.add("trans_amount_usd");
		userDefinedNewCols.add("trans_rmk");
		userDefinedLookups.put("trans_createdby", "select us_id, us_name from kbusers");
		userDefinedLookups.put("trans_entity_id", "select us_id, us_name from kbusers where us_rank  = 'DLVAGENT'");
		userDefinedLookups.put("trans_operationcode", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'AGENT' and kbcat2= 'PMTTYPE'");

		userDefinedNewLookups.put("trans_operationcode", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'AGENT' and kbcat2= 'PMTTYPE'"
				+ " and kbcode not in ('CASES') ");
		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
		userDefinedNewColsDefualtValues.put("trans_entity_id", new String[] {"{agentaccountdebts}"});
		//userDefinedNewColsDefualtValues.put("ap_paymenttype");
		
		userDefinedNewCaption = " ديون مندوب توصيل";
		UserDefinedPageRows = 1000;
	}
	
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autoCommit) {
		String statusMsg = " تم تسجيل العملية ";
		inputMap_ori = filterRequest(rqs);
		try {
			String rmk = inputMap_ori.get("trans_rmk")[0];
			String pmtType  = inputMap_ori.get("trans_operationcode")[0];
			long transactionAmtFormIqd  = Long.parseLong(inputMap_ori.get("trans_amount_iqd")[0]);
			long transactionAmtFormUsd  = Long.parseLong(inputMap_ori.get("trans_amount_usd")[0]);
			int agentIdScreen = Integer.parseInt(inputMap_ori.get("trans_entity_id")[0].trim());
			int agentId_G = Integer.parseInt(replaceVarsinString("{agentaccountdebts}", arrayGlobals).trim());
			if (agentIdScreen != agentId_G)
				throw new Exception ("Error, agent in form is ("+agentIdScreen+") and global agent id is ("+agentId_G+") are not the same");

			SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = 
					UtilitiesSafeFinancials.getSafePaymentTypeMetaInfoKbgeneral(conn, "AGENT", "PMTTYPE" , pmtType);
			
			if (transactionAmtFormIqd ==0 &&  transactionAmtFormUsd==0) 
				throw new Exception ("مبلغ العملية المالية غير متوفر"); 
			long debitIqd = 0, creditIqd = 0,  debitUsd = 0 , creditUsd = 0;
			long amtPaidActuallyIqd = transactionAmtFormIqd;
			long amtPaidActuallyUsd = transactionAmtFormUsd;
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
			int userId_G = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
			int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
			AccountantBoxBean accountantBoxBean= UtilitiesSafeFinancials.
					GetAccountantBox(conn, userId_G, branchId_G);
			int accountBoxTransactionId = 0;
			if(safePaymentTypeMetaInfoBean.getSafeImpact() != PaymentImpactOnSafe.NOSAFE) {
				String agentName = Utilities.getAgentName(conn, agentId_G);
				accountBoxTransactionId = UtilitiesSafeFinancials.createAcctBoxTransactions(
						conn, 
						0, 
						"p_fin_transactions".toUpperCase(), 
						safePaymentTypeMetaInfoBean.getSafeImpact(), 
						userId_G, 
						amtPaidActuallyIqd,
						amtPaidActuallyUsd,
						branchId_G,
						safePaymentTypeMetaInfoBean.getName() + " - "+ "حسابات المندوب", 
						"kbusers",
						"us_id",
						agentId_G,
						"us_name",
						agentName,
						userId_G);
			}
			StandardTransactionBean standardTransactionBean = new StandardTransactionBean();
			standardTransactionBean.setEntity(FinOperationEntity.AGENT);
			standardTransactionBean.setEntityId(agentId_G);
			standardTransactionBean.setCategory(FinOperationCategory.PMTTYPE);
			standardTransactionBean.setCode(FinOperationCode.valueOf(pmtType));
			standardTransactionBean.setInitiatedInBranchId(branchId_G);
			standardTransactionBean.setWhichScreen("ديون المندوب");
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
	

}
