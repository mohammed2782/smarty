package com.app.incomeoutcome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.financials.FinOperationCode;
import com.app.financials.SafePaymentTypeMetaInfoBean;
import com.app.financials.UtilitiesFinancials;
import com.app.financials.UtilitiesSafeFinancials;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

import smarty.core.CoreMgr;
import smarty.db.mysql;


public class BranchesTransactions extends CoreMgr{
	public BranchesTransactions() {

		/*
		 * to define main sql statement, key column and main table that do this
		 * operations
		 */
		MainSql = " select p_fin_transactions.* , '' as fake, '' as  showdel "
				+ " from  p_fin_transactions "
				+ " where trans_operationentity='BRANCH' "
				+ " and trans_entity_id={BRANCH_TO_PAY_TO_G} "
				+ " and trans_initiated_in_branch_id = {userstorecode} and trans_deleted='N' order by trans_id desc ";

		keyCol = "trans_id";
		mainTable = "trans_operationentity";
		//canNew = true;
		
		// Grid columns
		userDefinedGridCols.add("trans_id");
		userDefinedGridCols.add("trans_operationcode");
		userDefinedGridCols.add("trans_amount_iqd");
		userDefinedGridCols.add("trans_amount_usd");
		userDefinedGridCols.add("trans_amount_paid_actually_iqd");
		userDefinedGridCols.add("trans_amount_paid_actually_usd");
		userDefinedGridCols.add("trans_debit_iqd");
		userDefinedGridCols.add("trans_credit_iqd");
		userDefinedGridCols.add("trans_debit_usd");
		userDefinedGridCols.add("trans_credit_usd");
		userDefinedGridCols.add("trans_rmk");
		userDefinedGridCols.add("trans_createdby");
		userDefinedGridCols.add("trans_createddt");
		
		userDefinedGridCols.add("showdel");
		
		userDefinedColLabel = UtilitiesFinancials.getFinancialTableColumnsName();
		userDefinedColLabel.put("trans_amount_paid_actually_iqd", "مدفوع د.ع");
		userDefinedColLabel.put("trans_amount_paid_actually_usd", "مدفوع $");
		userDefinedLookups.put("trans_createdby", "select us_id, us_name from kbusers");
		userDefinedLookups.put("trans_operationcode", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'BRANCH' and kbcat2= 'PMTTYPE' ");
		userDefinedNewColsHtmlType.put("trans_createddt", "DATE");
		
		userModifyTD.put("trans_id", "printPmtReceipt({trans_id})");
		userModifyTD.put("showdel", "showDel({trans_id}, {trans_did_branch_receive},{trans_operationcode})");
		myhtmlmgr.refreshPageOnDelete = true;
		userDefinedTableHeadersClass = "bg-purple bg-lighten-1 white";
		//userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
	}// end of constructor customer_payment

	
	@Override
	public void initialize(HashMap smartyStateMap) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		int branchReceiver = Integer.parseInt(replaceVarsinString(" {BRANCH_TO_PAY_TO_G} ", arrayGlobals).trim());
		int branchId = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		String branchName = "", receiverBranchName = "";
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select branch_name from kbbranches where branch_id = ?");
			pst.setInt(1, branchId);
			rs = pst.executeQuery();
			if(rs.next()) {
				branchName = rs.getString("branch_name");
			}
			try {rs.close();}catch(Exception e) {}
			pst.clearParameters();
			
			pst.setInt(1, branchReceiver);
			rs = pst.executeQuery();
			while(rs.next()) {
				receiverBranchName = rs.getString("branch_name");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		
		super.initialize(smartyStateMap);
		userDefinedCaption = "الحركات المالية بين فرع: "+branchName+" وفرع: "+receiverBranchName;
	}

	
	public String printPmtReceipt(HashMap<String, String> hashy) {
		String btn ="<a href='../../branchPaymentsReceiptSRVL?trans_id="+hashy.get("trans_id")+"'"
				+ " class='btn btn-xs btn-warning' >طباعة أيصال دفع "+hashy.get("trans_id")+"<i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
	}
	

	public String showDel(HashMap<String, String> hashy) {
		String rank = (String) arrayGlobals.get("userRank");
		String superItRank = (String) arrayGlobals.get("superItRank");
		String showDel = "<td align='center' style='vertical-align: middle;'>";
		if((rank.equalsIgnoreCase("FIN_OP_MGR")  || superItRank.equalsIgnoreCase("Y") 
				|| rank.equalsIgnoreCase("BRANCHMGR") 
				|| rank.equalsIgnoreCase("SYSMANAGER")
				|| rank.equalsIgnoreCase("GA")) 
			&& hashy.get("trans_did_branch_receive").equalsIgnoreCase("N") 
			&& hashy.get("trans_operationcode").equalsIgnoreCase("CASES")) {
			showDel += "<button type='button' onclick=\"link=false; "
						+ "var rs =doDeleteSmarty(this,'هل تريد حذف هذه الدفعة ؟' ,'trans_id','"+hashy.get("trans_id")+"' , 'com.app.incomeoutcome.BranchesTransactions' ); return rs;\" class='btn btn-danger btn-sm'> "
								+ "<li class='fa fa-trash'></li></button></td>";
				
			return showDel;	
		}
		return "<td></td>";
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
				// second update the cases back to cc_branchpmtid = 0
				pst = conn.prepareStatement("update p_caseschain set cc_branchpmtid=0  where cc_branchpmtid =?");
				pst.setInt(1, transactionToBeDeleted);
				pst.executeUpdate();
				try {pst.close();} catch (Exception e) {}
				
				int branchId_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
				UtilitiesStandardFinancials.deleteTransaction(conn, transactionToBeDeleted, userId_G, branchId_G, "مسح عملية مالية - حسابات الفروع ");
			}
			
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
}// end of class
