package com.app.incomeoutcome.bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.bussframework.FlowUtils;
import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

public class ShiftFromBoxToSafe extends CoreMgr {
	public ShiftFromBoxToSafe () {
		/*
		 * MainSql =
		 * "select ap_id, sum(ap_amtreceived) as finiq,  ap_createdby,'' as ap_transferedtosafe , '' as btnpopup, '' as rmk "
		 * + "	from p_agent_payments " + " join kbusers on (ap_agentid = us_id)" +
		 * " where ap_safeid = 0 and us_branchcode = {userstorecode} and ap_safeoff = 'N'"
		 * + " group by ap_createdby ";
		 */
		MainSql = " select sum(finiq) as finiq, createdby,'' as ap_transferedtosafe , '' as btnpopup, '' as rmk from ("
				+ "select ap_id, ifnull(sum(ap_amtreceived),0) as finiq, ap_createdby as createdby "
				+ "	from p_agent_payments "
				+ " join kbusers on (ap_agentid = us_id and us_branchcode = {userstorecode} ) "
				+ " where ap_safeid = 0 and ap_safeoff = 'N'  group by ap_createdby "
				+ " union"
				+ " select bp_id, ifnull(sum(bp_receivedamt),0) as finiq, bp_receivedby as createdby "
				+ "	from p_branch_payments  "
				+ " where bp_safeid = 0 and bp_received = 'Y' and bp_received_branchid = {userstorecode} and bp_safeoff = 'N' group by bp_receivedby)abc "
				+ " group by createdby ";
		//System.out.println(MainSql);
		canEdit = true;
		mainTable = "p_agent_payments";
		keyCol = "createdby";
		displayMode = "GRIDEDIT";
		userDefinedGridCols.add("createdby");
		userDefinedGridCols.add("finiq");
		userDefinedGridCols.add("btnpopup");
		userDefinedGridCols.add("ap_transferedtosafe");
		userDefinedGridCols.add("rmk");
		
		userDefinedColLabel.put("createdby", "الصندوق");
		userDefinedColLabel.put("finiq", "المبلغ د.ع");
		userDefinedColLabel.put("btnpopup", "تفاصيل");
		userDefinedColLabel.put("ap_transferedtosafe", "ترحيل");
		userDefinedColLabel.put("rmk", "ملاحظات");
		
		
		userDefinedEditCols.add("ap_transferedtosafe");
		
		userDefinedEditCols.add("rmk");
		
		userDefinedEditColsHtmlType.put("rmk", "TEXTAREA");
		userDefinedEditColsHtmlType.put("ap_transferedtosafe", "DROPLIST");
		
		userModifyTD.put("btnpopup", "showDtlsOfFinBox({createdby})");

		userDefinedLookups.put("ap_transferedtosafe", "select 'Y' , 'نقل للقاصة'  from dual");
		userDefinedLookups.put("createdby", "select us_id, us_name from kbusers");
		
		userDefinedCaption = "ترحيل من الصناديق للقاصة";
	}
	
	public String showDtlsOfFinBox(HashMap<String,String>hashy) {
		String html ="<td>";
		html +="<a href='#' class='btn btn-xs btn-primary' "
				+ " onclick=\"popitup('./FinBoxDtlsPopUp?finboxacctid="+hashy.get("createdby")+"&safeoff=N', 'Transactions' , 800,600)\">التفاصيل</a>";
		
		html +="</td>";
		return html;
	}
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean autocommit) {
		Connection conn1 = null;
		PreparedStatement pstInsertIntoSafe = null, pstUpdateAgentPayments=null, pstTotAmt=null, pstUpdateBranchPayments = null;
		ResultSet rs = null;
		String userid = replaceVarsinString("{userid}", arrayGlobals).trim();
		keyVal = parseUpdateRqs(rqs);
		UtilitiesFeqar ut = new UtilitiesFeqar();
		int branchId = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		int rowsNo =0;
		double totAmtIqd = 0.0;
		String acctId = "";
		int safeId = 0;
		String msg = "تم الترحيل";
		/*
		for(String key:inputMap_ori.keySet())
			System.out.println("key = "+key+"	value = "+inputMap_ori.get(key)[0]);
		*/
		
		try {			
			conn1 = mysql.getConn();
			
			if(!ut.getSafeActiveCondition(conn1, branchId))
				return "حصل خطأ : القاصة مغلقة يرجى فتحها ثم اجراء عملية الترحيل";
			
			pstInsertIntoSafe = conn1.prepareStatement("insert into p_safe "
					+ " 		(saf_amount_iqd  , saf_trantype, saf_tranname, saf_tranentity, saf_branchid, "
					+ "  		 saf_createdby   , saf_rmk		 , saf_before_transaction, saf_trandate) "
					+ "values   (?				 ,  ?		     , ?			 , ?		 , ?		   , "
					+ "			 ?			 	 , ?			 , ?			 , now())",Statement.RETURN_GENERATED_KEYS);
			
			pstUpdateAgentPayments = conn1.prepareStatement("update p_agent_payments set  ap_safeid = ? where ap_createdby=? and ap_safeid=0  and ap_safeoff = 'N'");
			
			pstUpdateBranchPayments = conn1.prepareStatement("update p_branch_payments set  bp_safeid = ? where bp_receivedby=? and bp_received_branchid =? and bp_safeid=0  and bp_safeoff = 'N'");
			pstTotAmt = conn1.prepareStatement("select sum(finiq) from("
					+ " select ifnull(sum(ap_amtreceived),0) as finiq, ap_createdby as createdby from p_agent_payments "
					+ " join kbusers on (ap_agentid = us_id) where ap_safeid=0 and ap_createdby=? and us_branchcode = ?  and ap_safeoff = 'N' "
					+ " union"
					+ " select ifnull(sum(bp_receivedamt),0) as finiq, bp_receivedby as createdby  "
					+ "	from p_branch_payments  "
					+ " where bp_safeid = 0 and bp_received = 'Y' and bp_received_branchid=? and bp_safeoff = 'N' and bp_receivedby=?)abc ");
			
			
			if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
				rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
			
			for (int i=1 ; i<=rowsNo ; i++){
				acctId = inputMap_ori.get("smarty_createdby_hidden_smartyrow_"+i)[0];
				if(inputMap_ori.get("ap_transferedtosafe_smartyrow_"+i)[0].equalsIgnoreCase("Y")) {
					pstTotAmt.setString(1, acctId);
					pstTotAmt.setInt(2, branchId);
					pstTotAmt.setInt(3, branchId);
					pstTotAmt.setString(4, acctId);
					rs = pstTotAmt.executeQuery();
					if (rs.next()) 
						totAmtIqd = rs.getDouble(1);
					pstTotAmt.clearParameters();
					try {rs.close();}catch(Exception e) {}
					//System.out.println("totAmtIqd = "+totAmtIqd);
					if (totAmtIqd<=0)
						return "هناك ترحيل من صندوق قيمة المبلغ فيه = 0";
					//double balance = ut.getSafeBalance(conn1, branchId);
					
					pstInsertIntoSafe.setDouble(1, totAmtIqd);
					pstInsertIntoSafe.setString(2, "CR");
					pstInsertIntoSafe.setString(3, "TRANSFERFROMFINBOX");
					pstInsertIntoSafe.setString(4, acctId);
					pstInsertIntoSafe.setInt(5, branchId);
					pstInsertIntoSafe.setString(6, userid);
					pstInsertIntoSafe.setString(7, inputMap_ori.get("rmk_smartyrow_"+i)[0]);
					pstInsertIntoSafe.setDouble(8, 0);
					pstInsertIntoSafe.executeUpdate();
					rs = pstInsertIntoSafe.getGeneratedKeys();
					rs.next();
					safeId = rs.getInt(1);
					pstInsertIntoSafe.clearParameters();
					try {rs.close();}catch(Exception e) {}
					/////// agent payment
					pstUpdateAgentPayments.setInt(1, safeId);
					pstUpdateAgentPayments.setString(2, acctId);
					pstUpdateAgentPayments.executeUpdate();
					pstUpdateAgentPayments.clearParameters();
					/////// branch payment
					pstUpdateBranchPayments.setInt(1, safeId);
					pstUpdateBranchPayments.setString(2, acctId);
					pstUpdateBranchPayments.setInt(3, branchId);
					pstUpdateBranchPayments.executeUpdate();
					pstUpdateBranchPayments.clearParameters();
				}
				
			}
			conn1.commit();
			
		}catch (Exception e) {
			e.printStackTrace();
			try {conn1.rollback();}catch(Exception eRoll) {/**/}
			msg = "حصل خطأ";
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pstInsertIntoSafe.close();}catch(Exception e) {}
			try {pstUpdateAgentPayments.close();}catch(Exception e) {}
			try {pstTotAmt.close();}catch(Exception e) {}
			try {pstUpdateBranchPayments.close();}catch(Exception e) {}
			try {conn1.close();}catch(Exception e) {}
		
		}
		return msg;
	}

}
