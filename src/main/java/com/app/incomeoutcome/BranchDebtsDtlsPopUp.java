package com.app.incomeoutcome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;


import com.app.financials.AccountantBoxBean;
import com.app.financials.FinOperationCategory;
import com.app.financials.FinOperationCode;
import com.app.financials.FinOperationEntity;
import com.app.financials.PaymentImpactOnSafe;
import com.app.financials.SafePaymentTypeMetaInfoBean;
import com.app.financials.StandardTransactionBean;
//smarty.db.mysq;
//smarty.security.LoginUser
import com.app.financials.UtilitiesFinancials;
import com.app.financials.UtilitiesSafeFinancials;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.util.Utilities;

import smarty.core.CoreMgr;
import smarty.db.mysql;

public class BranchDebtsDtlsPopUp extends CoreMgr{
	String reportBranchDebtOnly = "Y";
	public BranchDebtsDtlsPopUp() {
		MainSql=  "select '' as del, '' as transamt , '' as transamt_usd, trans_receiver_box, "
				+ "  trans_did_branch_receive, trans_operationcode , trans_rmk, trans_createdby, "
				+ " trans_createddt, trans_id,  trans_debit_iqd, trans_credit_iqd, trans_debit_usd, "
				+ " trans_credit_usd "
				+ " from p_fin_transactions where  trans_operationentity = 'BRANCH' and"
				+ "  trans_initiated_in_branch_id= {userstorecode} and trans_receiver_branch_id= {otherBranchTransEntity}  "
				+ "  and ( trans_debit_iqd !=0 or trans_credit_iqd !=0 or trans_debit_usd !=0 or trans_credit_usd !=0) "
				+ " order by trans_id desc";
		mainTable = "p_fin_transactions";
		keyCol 	  = "trans_id";		
		userDefinedGridCols.add("trans_id");
		userDefinedGridCols.add("trans_operationcode");
		userDefinedGridCols.add("trans_createdby");
		userDefinedGridCols.add("trans_createddt");
		
		userDefinedGridCols.add("trans_debit_iqd");
		userDefinedGridCols.add("trans_credit_iqd");
		userDefinedGridCols.add("trans_credit_usd");
		userDefinedGridCols.add("trans_debit_usd");
		userDefinedGridCols.add("trans_rmk");
		userDefinedGridCols.add("del");
		
		userDefinedColLabel = UtilitiesFinancials.getFinancialTableColumnsName();
		userDefinedColLabel.put("transamt", "المبلغ د.ع");
		userDefinedColLabel.put("transamt_usd", "المبلغ $");
		
		userDefinedColLabel.put("del", "");
		userModifyTD.put("del", "showDel({trans_id},{trans_did_branch_receive},{trans_receiver_box},"
				+ " {trans_operationcode})");
		UserDefinedPageRows = 2000;
		
		userDefinedNewCols.add("trans_operationcode");
		userDefinedNewCols.add("transamt");
		userDefinedNewCols.add("transamt_usd");
		userDefinedNewCols.add("trans_rmk");
		
		userDefinedNewLookups.put("trans_operationcode", "select kbcode, kbdesc from kbgeneral where "
				+ " kbcat1 = 'BRANCH' and kbcat2= 'PMTTYPE' and kbcode ='ASSIGNDEBT' ");
		
		userDefinedLookups.put("trans_operationcode", "select kbcode, kbdesc from kbgeneral where "
				+ " kbcat1 = 'BRANCH' and kbcat2= 'PMTTYPE' ");
		
		userDefinedNewColsDefualtValues.put("bp_transtype", new String[] {"ASSIGNDEBT"});
		userDefinedLookups.put("trans_createdby", "select us_id, us_name from kbusers ");
		
		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
		userDefinedNewColsDefualtValues.put("trans_operationcode", new String[] {"ASSIGNDEBT"});

		userDefinedColsMustFill.add("trans_operationcode");
		userDefinedColsMustFill.add("transamt");
		userDefinedColsMustFill.add("transamt_usd");
		userDefinedColsMustFill.add("trans_rmk");
		
		userDefinedMinValMap.put("transamt", "0");
	}
	
	public String showDel(HashMap<String, String> hashy) {
		if (reportBranchDebtOnly.equalsIgnoreCase("N")) {
			String rank = (String) arrayGlobals.get("userRank");
			String superItRank = (String) arrayGlobals.get("superItRank");
			
			String showDelUpdate = "<td align='center' style='vertical-align: middle;'>";
			if (hashy.get("trans_operationcode").equalsIgnoreCase("ASSIGNDEBT")) {
				if((rank.equalsIgnoreCase("FIN_OP_MGR")  || superItRank.equalsIgnoreCase("Y")
						|| rank.equalsIgnoreCase("BRANCHMGR")) ) {
					showDelUpdate += "<button type='button' onclick=\"link=false; "
					+ "var rs =doDeleteSmarty(this,'هل تريد حذف هذه الدفعة ؟' ,'trans_id','"+hashy.get("trans_id")+"' , 'com.app.incomeoutcome.BranchDebtsDtlsPopUp' ); return rs;\" class='btn btn-danger btn-sm'> "
					+ "<li class='fa fa-trash'></li></button>";
					return showDelUpdate+"</td>";	
				}
			}
		}
		return "<td></td>";
	}
	
	@Override
	public void initialize(HashMap smartyStateMap) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		reportBranchDebtOnly = replaceVarsinString(" {reportBranchDebtOnly} ", arrayGlobals).trim();
		int otherBranchTransEntity = Integer.parseInt(replaceVarsinString(" {otherBranchTransEntity} ", arrayGlobals).trim());
		int userBranch = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		String userBranchName = "", otherBranchTransEntityName = "";
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select branch_name from kbbranches where branch_id = ?");
			pst.setInt(1, otherBranchTransEntity);
			rs = pst.executeQuery();
			if(rs.next()) {
				otherBranchTransEntityName = rs.getString("branch_name");
			}
			try {rs.close();}catch(Exception e) {}
			pst.clearParameters();
			
			pst.setInt(1, userBranch);
			rs = pst.executeQuery();
			while(rs.next()) {
				userBranchName = rs.getString("branch_name");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		if (reportBranchDebtOnly.equalsIgnoreCase("Y")) {
			MainSql= "select '' as del, '' as transamt, '' as transamt_usd, trans_receiver_box, trans_did_branch_receive, trans_operationcode, trans_rmk, trans_createdby, "
			+ " trans_createddt, trans_id,  trans_debit_iqd, trans_credit_iqd, trans_debit_usd, trans_credit_usd "
			+ " from p_fin_transactions where  trans_operationentity = 'BRANCH' and"
			+ " trans_receiver_branch_id= {otherBranchTransEntity} "
			+ " and trans_initiated_in_branch_id  = {userstorecode} "
			+ " and ( trans_debit_iqd !=0 or trans_credit_iqd !=0 or trans_debit_usd !=0 or trans_credit_usd !=0)"
			+ " and trans_deleted = 'N' "
			+ " order by trans_id desc";
			canNew = false;
		}else {
			MainSql= "select '' as del,  '' as transamt , '' as transamt_usd, trans_receiver_box, trans_did_branch_receive, trans_operationcode, trans_rmk, trans_createdby, "
			+ " trans_createddt, trans_id, trans_debit_iqd, trans_credit_iqd, trans_debit_usd, trans_credit_usd "
			+ " from p_fin_transactions where trans_operationentity = 'BRANCH' and  "
			+ " trans_receiver_branch_id= {userstorecode} "
			+ " and trans_initiated_in_branch_id = {otherBranchTransEntity}  "
			+ " and ( trans_debit_iqd !=0 or trans_credit_iqd !=0 or trans_debit_usd !=0 or trans_credit_usd !=0) "
			+ " and trans_deleted = 'N' "
			+ " order by trans_id desc";
			canNew = true;
			//canEdit = true;
			//canDelete = true;
			
			myhtmlmgr.refreshPageOnDelete = true;
		}
		userDefinedCaption= "الفروقات المالية بين فرع: "+userBranchName+" وفرع: "+otherBranchTransEntityName;
		super.initialize(smartyStateMap);
	}
	
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autoCommit) {
		String statusMsg = " تم تسجيل الدين على الفرع ";
		int userBranch = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		int otherBranchTransEntity = Integer.parseInt(replaceVarsinString(" {otherBranchTransEntity} ", arrayGlobals).trim());
		inputMap_ori = filterRequest(rqs);
		try {
			if (userBranch == otherBranchTransEntity)
				throw new Exception ("الفرعين لهما نفس الرقم");
			String rmk = inputMap_ori.get("trans_rmk")[0];
			// now start the payment process
			String pmtType  = "ASSIGNDEBT";
			long transactionAmountIqd = Long.parseLong(inputMap_ori.get("transamt")[0]);
			long transactionAmountUsd = Long.parseLong(inputMap_ori.get("transamt_usd")[0]);
			
			int userId_G = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
			int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
			
			StandardTransactionBean standardTransactionBean = new StandardTransactionBean();
			standardTransactionBean.setEntity(FinOperationEntity.BRANCH);
			standardTransactionBean.setEntityId(branchId_G);
			standardTransactionBean.setCategory(FinOperationCategory.PMTTYPE);
			standardTransactionBean.setCode(FinOperationCode.valueOf(pmtType));
			standardTransactionBean.setReceiverBranchId(branchId_G);
			standardTransactionBean.setWhichScreen("ديون الفروع");
			
			standardTransactionBean.setReceiptsAmtIqd(0);
			standardTransactionBean.setReceiptsAmtUsd(0);
			
			standardTransactionBean.setTransactionAmountIqd(transactionAmountIqd);
			standardTransactionBean.setTransactionAmountUsd(transactionAmountUsd);
			
			standardTransactionBean.setDebitIqd(transactionAmountIqd);
			standardTransactionBean.setDebitUsd(transactionAmountUsd);
			
			standardTransactionBean.setAmountPaidActuallyIqd(0);
			standardTransactionBean.setAmountPaidActuallyUsd(0);
			
			standardTransactionBean.setPayerBox(0);
			standardTransactionBean.setPayerBoxTransactionId(0);
			standardTransactionBean.setRemarks(rmk);
			int standardStransactionId = 
					UtilitiesStandardFinancials.buildStandardTransaction(
							conn, 
							standardTransactionBean,
							otherBranchTransEntity, 
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
		try {
			SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = 
					UtilitiesSafeFinancials.getTransactionSafePaymentTypeMetaInfo(conn, transactionToBeDeleted);
			int userId_G = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals));
			// if the payment is for cases, then back them up and zerofiy the payment
			if (safePaymentTypeMetaInfoBean.getFinOperationCode() == FinOperationCode.ASSIGNDEBT ) {
				int branchId_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
				UtilitiesStandardFinancials.deleteTransaction(conn, transactionToBeDeleted, userId_G, 
						branchId_G, "مسح عملية مالية - ديون على الفروع ");
			}
			conn.commit();
		} catch (Exception e) {
			try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
			logErrorMsg = "";
			e.printStackTrace();
		}
		return "";
	}// end of doDelete*/

}


