package com.app.incomeoutcome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.beans.BranchPaymentBean;

import com.app.financials.UtilitiesSafeFinancials;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.util.Utilities;

import smarty.core.CoreMgr;
import smarty.db.mysql;

public class ReceivedBranchPayments extends CoreMgr{
	public ReceivedBranchPayments() {
		MainSql = "select p_fin_transactions.*, '' as fake2,  '' as fake, '' as del "
				+ " from p_fin_transactions "
				+ " where trans_operationentity = 'BRANCH' and trans_entity_id={userstorecode}  "
				+ "and trans_initiated_in_branch_id ={BRANCH_TO_RECEIVE_FROM_G} and trans_deleted='N' order by trans_id desc ";
		mainTable = "p_fin_transactions";
		keyCol = "trans_id";
		userDefinedUpdateCaption = "الدفعات من الفروع";
		
		userDefinedEditColsDefualtValues.put("trans_received_by", new String[] {"{userid}"});
		userDefinedLookups.put("trans_did_branch_receive", "select kbcode, kbdesc from kbgeneral where kbcat1='YESNO'");
		
		userDefinedColLabel = UtilitiesStandardFinancials.getFinancialTableColumnsName();
		userDefinedColLabel.put("del", "");
		userDefinedColLabel.put("trans_amount_iqd", "مبلغ الوصولات المحاسب عليها د.ع");
		userDefinedColLabel.put("trans_amount_usd", "مبلغ الوصولات المحاسب عليها$");
		
		userDefinedGridCols.add("trans_id");
		userDefinedGridCols.add("trans_amount_iqd");
		userDefinedGridCols.add("trans_amount_usd");
		userDefinedGridCols.add("trans_createddt");
		userDefinedGridCols.add("trans_rmk");
		userDefinedGridCols.add("trans_received_by");
		userDefinedGridCols.add("trans_receiveddt");
		userDefinedGridCols.add("trans_amount_received_actually_iqd");
		userDefinedGridCols.add("trans_amount_received_actually_usd");
		userDefinedGridCols.add("trans_credit_iqd");
		userDefinedGridCols.add("trans_debit_iqd");
		userDefinedGridCols.add("trans_receiver_rmk");
		userDefinedGridCols.add("trans_credit_usd");
		userDefinedGridCols.add("trans_debit_usd");
	
		userDefinedGridCols.add("del");
		userDefinedCaption = "المستلمات من الفروع";
		
		userModifyTD.put("del", "showUnReceiveButton({trans_id})");
		userModifyTD.put("trans_id", "printPmtReceipt({trans_id}, {trans_entity_id})");
		
		//userDefinedLookups.put("bp_received", "select kbcode, kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("trans_received_by", "select us_id, us_name from kbusers");
		
		myhtmlmgr.refreshPageOnDelete = true;
		
	}// end of constructor customer_payment

	public String showUnReceiveButton(HashMap<String,String> hashy) {
		String html = "";
		html = "<td>";
		html +="<button type=\"button\" onclick=\"link=false; "
				+ "var rs =doDeleteSmarty(this,'هل تريد إلغاء أستلام الدفعة؟' ,'trans_id','"+hashy.get("trans_id")+"' , 'com.app.incomeoutcome.ReceivedBranchPayments' ); "
				+ "return rs;\" class=\"btn btn-danger btn-sm\"><li class=\"fa fa-trash\"></li></button>";
		html +="</td>";
	return html;
	}
	
	public String printPmtReceipt(HashMap<String, String> hashy) {
		String btn = "<a href=\"../../branchPaymentsReceiptSRVL?trans_id="+hashy.get("trans_id")+"&userbranch="+hashy.get("trans_entity_id")+"\""
				+ " class='btn btn-sm btn-warning' >طباعة تفاصيل الدفعة <i class=\"fa fa-print fa-lg\"></i>"+hashy.get("trans_id")+"</a>";
		return "<td>" + btn + "</td>";
	}
	
	@Override
	public String doDelete(HttpServletRequest rqs){
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String msg = "";
		try {
			int transactionIdToBeUnreceived = Integer.parseInt(rqs.getParameter("trans_id"));
			conn = mysql.getConn();
			int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
			pst = conn.prepareStatement("select trans_receiver_box_transactionid  "
					+ " from p_fin_transactions  where trans_id = ? and trans_entity_id=?  and trans_deleted='N' ");
			pst.setInt(1, transactionIdToBeUnreceived);
			pst.setInt(2, branchId_G);
			rs = pst.executeQuery();
			int receivedAccountBoxTransactionId= 0;
			if (rs.next()) {
				receivedAccountBoxTransactionId = rs.getInt("trans_receiver_box_transactionid");
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			int userId_G = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
			if (receivedAccountBoxTransactionId>0) {
				UtilitiesSafeFinancials.revertAcctBoxTransaction(conn, receivedAccountBoxTransactionId, userId_G, branchId_G, "الغاء إستلام مبلغ من فرع");
			}
			pst = conn.prepareStatement("update p_fin_transactions set "
					+ " trans_receiveddt=null, trans_did_branch_receive= 'N' , "
					+ " trans_receiver_branch_id=0, trans_received_by=0, trans_receiver_rmk='' 	  ,  "
					+ " trans_amount_received_actually_iqd=0, trans_amount_received_actually_usd=0 , "
					+ " trans_credit_iqd=0    , trans_debit_iqd=0 , "
					+ " trans_credit_usd=0    , trans_debit_usd=0 , "
					+ " trans_receiver_box =0 , trans_receiver_box_transactionid=0"
					+ " where trans_id = ? and trans_did_branch_receive='Y' and  trans_deleted='N'");
			pst.setInt(1, transactionIdToBeUnreceived);
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			
			conn.commit();
			msg = "تم الغاء الأستلام";
		}catch(Exception e) {
			e.printStackTrace();
			try {conn.rollback();}catch(Exception eRoll) {/**/}
			msg = "Error. "+e.getMessage();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		return msg; 
	}
}
