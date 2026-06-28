package com.app.incomeoutcome;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.financials.FinOperationCode;
import com.app.financials.SafePaymentTypeMetaInfoBean;
import com.app.financials.UtilitiesFinancials;
import com.app.financials.UtilitiesSafeFinancials;
import com.app.financials.UtilitiesStandardFinancials;

import smarty.core.CoreMgr;

public class AgentsPayments extends CoreMgr{
		public AgentsPayments() {
			MainSql= "select p_fin_transactions.* , '' as fake, '' as  showdel "
					+ " from p_fin_transactions where trans_operationentity='AGENT' "
					+ " and trans_entity_id= {agentAcct} and trans_deleted='N' order by trans_id desc";
			keyCol = "trans_id";
			mainTable = "p_fin_transactions";
			
			// Grid columns
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
			userDefinedGridCols.add("fake");
			userDefinedGridCols.add("showdel");

			// //////////////
			userDefinedCaption = "تسديدات المندوبين";
			userDefinedColLabel = UtilitiesFinancials.getFinancialTableColumnsName();
			userDefinedColLabel.put("trans_entity_id", "مندوب التوصيل");
			userDefinedColLabel.put("fake", "طباعة ايصال الدفع ");
			userDefinedColLabel.put("showdel", " ");
			//canDelete = true;
			userModifyTD.put("fake", "printPmtReceipt({trans_id},{branchcode})");
			userModifyTD.put("showdel", "showDel({trans_id})");

			myhtmlmgr.refreshPageOnDelete = true;
			
			userDefinedLookups.put("trans_createdby", "select us_id, us_name from kbusers");
			userDefinedLookups.put("trans_operationcode", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'AGENT' and kbcat2= 'PMTTYPE' ");
			userDefinedNewColsHtmlType.put("trans_createddt", "DATE");
			userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
			
			userDefinedTableHeadersClass = "text-white  bg-gradient-x-primary";
			
		}// end of constructor customer_payment

		public String showDel (HashMap<String,String> hashy) {
			return "<td align='center' style='vertical-align: middle;'>"
				+"<button id='pmt_del_btn_"+hashy.get("trans_id")+"' type='button' "
				+ " onclick=\"link=false; "
				+ " var rs =doDeleteSmarty(this,'هل تريد حذف هذه الدفعة ؟' ,'trans_id','"+hashy.get("trans_id")+"' , 'com.app.incomeoutcome.AgentsPayments' ); return rs;\" class='btn btn-danger btn-sm'>"
				+ "<li class='fa fa-trash'></li></button></td>";
		}
		public String printPmtReceipt(HashMap<String, String> hashy) {
			String btn = "<a href=\"../../AgentPaymentReceiptSRVL?trans_id="+hashy.get("trans_id")+"&branchcode="+hashy.get("trans_initiated_in_branch_id")+"\" "
					+ " class='btn btn-sm btn-warning' >طباعة<i class=\"fa fa-print fa-lg\"></i></a>";
			
			return "<td>" + btn +"</td>";
		}
		
		@Override
		public String doDelete(HttpServletRequest rqs) {
			int transactionToBeDeleted = Integer.parseInt(rqs.getParameter(keyCol));
			PreparedStatement pst = null;
			String msg = "تم المسح";
			try {
				SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = 
						UtilitiesSafeFinancials.getTransactionSafePaymentTypeMetaInfo(conn, transactionToBeDeleted);
				int userId_G = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals));
				// if the payment is for cases, then back them up and zerofiy the payment
				if (safePaymentTypeMetaInfoBean.getFinOperationCode() == FinOperationCode.CASES ) {
					// first backup the cases that had payment and the payment also
					pst = conn.prepareStatement("insert into  del_agent_payment_cases (dapc_paymentid , dapc_cid, dapc_deletedby) "
							+ " select c_agentpmtid , c_id , ? from p_cases where c_agentpmtid =?");
					pst.setInt(1, userId_G);
					pst.setInt(2, transactionToBeDeleted);
					pst.executeUpdate();
					try {pst.close();} catch (Exception e) {}
					
					// second update the cases back to NO, and agent pmt id = 0
					pst = conn.prepareStatement("update p_cases set c_agentpmtid=0 , c_agentsharesettled='NO' where c_agentpmtid =?");
					pst.setInt(1, transactionToBeDeleted);
					pst.executeUpdate();
					try {pst.close();} catch (Exception e) {}
				}
				
				int branchId_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
				UtilitiesStandardFinancials.deleteTransaction(conn, transactionToBeDeleted, userId_G, branchId_G, "مسح عملية مالية - حسابات المندوب");
				conn.commit();
			} catch (Exception e) {
				try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
				logErrorMsg = "";
				msg = "خطأ, "+e.getMessage();
				e.printStackTrace();
			} finally {
				try {pst.close();} catch (Exception e) {}
			}

			return msg;
		}// end of doDelete*/
}
