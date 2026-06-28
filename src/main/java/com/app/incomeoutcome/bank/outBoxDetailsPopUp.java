package com.app.incomeoutcome.bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import smarty.core.CoreMgr;
import smarty.db.mysql;

public class outBoxDetailsPopUp extends CoreMgr{
	public outBoxDetailsPopUp() {
		
		userDefinedGridCols.add("abt_safe_impact");
		userDefinedGridCols.add("abt_accountbefore_transaction");
		userDefinedGridCols.add("abt_payment");
		userDefinedGridCols.add("abt_tabledesc");
		userDefinedGridCols.add("abt_createdby");
		userDefinedGridCols.add("abt_paymentid");
		userDefinedGridCols.add("abt_date");
		
		userDefinedColLabel.put("abt_accountbefore_transaction", "مبلغ الصندوق قبل المعاملة");
		userDefinedColLabel.put("abt_payment", "مبلغ المعاملة");
		userDefinedColLabel.put("acb_usid", "صندوق الدفع : ");
		userDefinedColLabel.put("abt_paymentid", "رقم الايداع او السحب");
		userDefinedColLabel.put("abt_tabledesc", "تمت المعاملة على");
		userDefinedColLabel.put("abt_safe_impact", "نوع المعاملة");
		userDefinedColLabel.put("abt_createdby", "تمت من خلال");
		userDefinedColLabel.put("acb_usid", "صندوق دفع");
		userDefinedColLabel.put("abt_date", "تاريخ العملية");
		
		userDefinedFilterLookups.put("acb_usid", "select us_id, us_name from kbusers where us_active = 'Y'");
		userDefinedLookups.put("abt_safe_impact", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'TRANSACTION' ");
		userDefinedLookups.put("abt_payment_table", "select tn_code, tn_desc_ar from kbtables_names");
		userDefinedLookups.put("abt_createdby", "select us_id, us_name from kbusers");

		userDefinedFilterColsHtmlType.put("acb_usid", "DROPLIST");
		userDefinedFilterColsHtmlType.put("fromdt", "DATE");
		userDefinedFilterColsHtmlType.put("todt", "DATE");
		
		userDefinedColsMustFillFilter.add("acb_usid");
		userDefinedColsMustFillFilter.add("fromdt");
		
		userModifyTD.put("abt_paymentid", "getCustomerName({abt_payment_table},{abt_paymentid},{abt_tabledesc},"
				+ "{abt_tablecolid},{abt_tablecoldesc},{abt_tablecolidval},{abt_table})");
		
		UserDefinedPageRows = 200;
		
		userDefinedCaption = "حركات صندوق الدفع";
	}
	public String getCustomerName(HashMap<String, String> hashy) {
		String html = "<td>"+hashy.get("abt_paymentid");
		Connection conn1 = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn1 = mysql.getConn();
			String sql = "select "+hashy.get("abt_tablecoldesc")+" from "+hashy.get("abt_table").toLowerCase()+""
					+ " where "+hashy.get("abt_tablecolid")+"="+hashy.get("abt_tablecolidval");
			//System.out.println(sql);
			pst = conn1.prepareStatement(sql);
			rs = pst.executeQuery();
			if(rs.next())
				html += " - "+rs.getString(hashy.get("abt_tablecoldesc"));
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn1.close();}catch(Exception e) {}
		}
		html +="</td>";
		return html;
		
	}
	@Override
	public void initialize(HashMap smartyStateMap){
		String safeid = replaceVarsinString(" {safeid} ", arrayGlobals).trim();
		Connection conn1 = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		int maxTransaction = 100000;
		int minTransaction = 0;
		int acctBoxId = 0;
		int branchId = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		try {
			conn1 = mysql.getConn();
			pst = conn.prepareStatement("select us_name, ifnull(acb_currentbalunce,0) as acb_currentbalunce  "
					+ "from p_accountantbox "
					+ "join kbusers on(us_id=acb_usid) where acb_usid = (select saf_tranentity from p_safe where saf_id = ?) and acb_userbranchid = ?");
			pst.setString(1, safeid);
			pst.setInt(2, branchId);
			rs = pst.executeQuery();
			if(rs.next()) 
				userDefinedCaption += " &nbsp;  _ صندوق دفع : "+rs.getString("us_name")+" 	/ 	المبلغ المتوفر في الصندوق حالياً : "+numFormat.format(rs.getDouble("acb_currentbalunce"));
				
			try {rs.close();}catch(Exception e) {/**/}
			try {pst.close();}catch(Exception e) {/**/}
			
			pst = conn.prepareStatement("select abt_id, abt_acctboxid from p_acctbox_transactions where abt_payment_table='P_SAFE' and abt_paymentid=? ");
			pst.setString(1, safeid);
			rs = pst.executeQuery();
			if(rs.next()) {
				minTransaction = rs.getInt("abt_id");
				acctBoxId = rs.getInt("abt_acctboxid");
			}
			try {rs.close();}catch(Exception e) {/**/}
			try {pst.close();}catch(Exception e) {/**/}
			
			pst = conn.prepareStatement("select ifnull(max(abt_id),0) from p_acctbox_transactions where abt_acctboxid = ? and abt_id>? and abt_payment_table = 'P_SAFE'");
			pst.setInt(1, acctBoxId);
			pst.setInt(2, minTransaction);
			rs = pst.executeQuery();
			if(rs.next()) {
				if(rs.getInt(1)>0)
					maxTransaction = rs.getInt(1);
			}
			try {rs.close();}catch(Exception e) {/**/}
			try {pst.close();}catch(Exception e) {/**/}
			//System.out.print("minTransaction = "+minTransaction+"   maxTransaction = "+maxTransaction);
			
			pst = conn.prepareStatement("select us_name, acb_currentbalunce from p_accountantbox join kbusers on(us_loginid=acb_usid) where acb_id = ?");
			pst.setInt(1, acctBoxId);
			rs = pst.executeQuery();
			if(rs.next()) {
				userDefinedCaption = "صندوق المحاسب : "+rs.getString("us_name")+" 	/ 	المبلغ المتوفر في الصندوق حالياً : "+numFormat.format(rs.getDouble("acb_currentbalunce"));
			}
			
			MainSql = "select * from p_acctbox_transactions where abt_id>="+minTransaction+" and abt_id<"+maxTransaction+" and abt_acctboxid="+acctBoxId+" order by 1 desc";
			super.initialize(smartyStateMap);
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {/**/}
			try {pst.close();}catch(Exception e) {/**/}
			try {conn1.close();}catch(Exception e) {/**/}
		}
	}

}
