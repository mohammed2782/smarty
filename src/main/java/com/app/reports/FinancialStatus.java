package com.app.reports;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import smarty.core.CoreMgr;


public class FinancialStatus extends CoreMgr{
	boolean odd = false;
	
	private static final String sql ="select sum(saf_amount_iqd) as tot from p_safe_hist where "
			+ " saf_createddt between (?) and  date_add(last_day(?), interval 1 day) and saf_trantype=? and saf_tranname=? "
			+ " and saf_branchid=? ";
	private static final String sqlExpences ="select sum(tot) from (select sum(saf_amount_iqd) as tot from p_safe_hist where "
			+ " saf_createddt between (?) and  date_add(last_day(?), interval 1 day) and saf_trantype=? and saf_tranname=? "
			+ " and saf_branchid=?"
			+ " union "
			+ " select sum(ou_paymentamt) as tot from p_outcome where ou_createddt between (?) and  date_add(last_day(?), interval 1 day)"
			+ " and ou_deleted = 'N' and ou_branchid =?) as expenses ";
	private static final String sqlOutCome = " select sum(ou_paymentamt) as tot from p_outcome where ou_createddt between (?) and  date_add(last_day(?), interval 1 day)"
			+ " and ou_deleted = 'N' and ou_branchid =? ";
	private long net = 0;
	public FinancialStatus() {
		MainSql = "SELECT DATE_FORMAT(saf_createddt,'%Y-%m') as monthdate, year(saf_createddt) as years, month(saf_createddt) as months, "
				+ " '' as transferfrominbox, '' as cashcr, '' as cashdb, '' as expenses, '' as outbox, '' as net "
				+ " from p_safe_hist where saf_branchid = {userstorecode} " + 
				"group by year(saf_createddt), month(saf_createddt) ";
		userModifyTD.put("transferfrominbox", "showTransferFromInBoxCr({years},{months})");
		userModifyTD.put("expenses", "showExpenses({years},{months})");
		userModifyTD.put("cashdb", "showCashWithdraw({years},{months})");
		userModifyTD.put("cashcr", "showCashCredit({years},{months})");
		userModifyTD.put("outbox", "showOutBox({years},{months})");
		userModifyTD.put("net", "showNet({years},{months})");
		
		userDefinedGridCols.add("monthdate");
		userDefinedGridCols.add("transferfrominbox");
		//userDefinedGridCols.add("cashcr");
		userDefinedGridCols.add("cashdb");
		userDefinedGridCols.add("expenses");
		userDefinedGridCols.add("outbox");
		userDefinedGridCols.add("net");
		
		userDefinedColLabel.put("monthdate", "شهر - سنة");
		userDefinedColLabel.put("transferfrominbox", "مستلمات من المندوبين والفروع");
		userDefinedColLabel.put("cashcr", "مستلمات نقدية");
		userDefinedColLabel.put("cashdb", "سحوبات نقدية");
		userDefinedColLabel.put("expenses", "مصروفات");
		userDefinedColLabel.put("outbox", "دفوعات للعملاء والفروع");
		userDefinedColLabel.put("net", "الربح");
		
		userDefinedCaption = "موقف مالي";
	}
	public String getExpencesValue (String dateYearMonth, String tranType, String tranName, int currentBranch) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		long tot =0;
		String html = "";
		try {
			pst = conn.prepareStatement(sqlExpences);
			pst.setString(1, dateYearMonth);
			pst.setString(2, dateYearMonth);
			pst.setString(3, tranType);
			pst.setString(4, tranName);
			pst.setInt(5, currentBranch);
			pst.setString(6, dateYearMonth);
			pst.setString(7, dateYearMonth);
			pst.setInt(8, currentBranch);
			rs = pst.executeQuery();
			if (rs.next()) {
				tot = rs.getLong(1);
			}
			if (tranType.equalsIgnoreCase("CR")) {
				html += "<td style='"+changeTdColor("green")+"'>";
				net = net + tot;
			}else {
				net = net + (-1*tot);
				html += "<td style='"+changeTdColor("red")+"'>";
			}
			html += numFormat.format(tot);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		html +="</td>";
		return html;
	}
	public String getSafeValue (String dateYearMonth, String tranType, String tranName, int currentBranch) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		long tot =0;
		long outCome = 0;
		String html = "";
		try {
			if(tranName.equals("DEPOSIT_OUTBOX")) {
				pst = conn.prepareStatement(sqlOutCome);
				pst.setString(1, dateYearMonth);
				pst.setString(2, dateYearMonth);
				pst.setInt(3, currentBranch);
				rs = pst.executeQuery();
				if (rs.next()) {
					outCome = rs.getLong("tot");
				}
				try {rs.close();}catch(Exception e) {}
				try {pst.close();}catch(Exception e) {}
			}
			
			pst = conn.prepareStatement(sql);
			pst.setString(1, dateYearMonth);
			pst.setString(2, dateYearMonth);
			pst.setString(3, tranType);
			pst.setString(4, tranName);
			pst.setInt(5, currentBranch);
			rs = pst.executeQuery();
			if (rs.next()) {
				tot = rs.getLong("tot");
			}
			tot = tot - outCome;
			if (tranType.equalsIgnoreCase("CR")) {
				html += "<td style='"+changeTdColor("green")+"'>";
				net = net + tot;
			}else {
				net = net + (-1*tot);
				html += "<td style='"+changeTdColor("red")+"'>";
			}
			html += numFormat.format(tot);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		html +="</td>";
		return html;
	}
	public String showTransferFromInBoxCr (HashMap<String,String> hashy) {
		String dateToSearch = hashy.get("years")+"-"+hashy.get("months")+"-01";
		int currentBranch = (int) arrayGlobals.get("userstorecode");
		this.odd = !odd;
		return getSafeValue (dateToSearch, "CR", "TRANSFERFROMFINBOX", currentBranch);
	}
	public String showExpenses(HashMap<String,String> hashy) {
		String dateToSearch = hashy.get("years")+"-"+hashy.get("months")+"-01";
		int currentBranch = (int) arrayGlobals.get("userstorecode");
		return getExpencesValue (dateToSearch, "DB", "EXPANDITURE", currentBranch);
	}
	public String showCashWithdraw(HashMap<String,String> hashy) {
		String dateToSearch = hashy.get("years")+"-"+hashy.get("months")+"-01";
		int currentBranch = (int) arrayGlobals.get("userstorecode");
		return getSafeValue (dateToSearch, "DB", "CASH", currentBranch);
	}
	public String showCashCredit(HashMap<String,String> hashy) {
		String dateToSearch = hashy.get("years")+"-"+hashy.get("months")+"-01";
		int currentBranch = (int) arrayGlobals.get("userstorecode");
		return getSafeValue (dateToSearch, "CR", "CASH", currentBranch);
	}
	public String showOutBox(HashMap<String,String> hashy) {
		String dateToSearch = hashy.get("years")+"-"+hashy.get("months")+"-01";
		int currentBranch = (int) arrayGlobals.get("userstorecode");
		return getSafeValue (dateToSearch, "DB", "DEPOSIT_OUTBOX", currentBranch);
	}
	public String showNet(HashMap<String, String> hashy) {
		String html = "";
		if(net >0)
			html += "<td style='"+changeTdColor("green")+"'>";
		else if(net<0)
			html += "<td style='"+changeTdColor("red")+"'>";
		else
			html += "<td style='"+changeTdColor("yellow")+"'>";
		html += numFormat.format(net);
		net = 0;
		html +="</td>";
		return html;
	}
	
	private  String changeTdColor(String color) {
		String backGroungColor = "background-color:";
		switch(color) {
		case "green":
			if(odd)
				backGroungColor += "#4fd646";
			else
				backGroungColor += "#48c440";
			break;
			
		case "red":
			if(odd)
				backGroungColor += "#e8291c";
			else
				backGroungColor += "#d62519";
			break;
			
		case "yellow":
			if(odd)
				backGroungColor += "#e0b437";
			else
				backGroungColor += "#cca432";
			break;
				
		default:
			if(odd)
				backGroungColor += "#3fcbda";
			else
				backGroungColor += "#39b6c4";
		
		}
		
		return backGroungColor;
		
	}
}
