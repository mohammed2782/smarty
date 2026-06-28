package com.app.incomeoutcome.bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;

import smarty.core.CoreMgr;
import smarty.db.mysql;

public class OutBoxTransactions extends CoreMgr{
	public OutBoxTransactions() {
		MainSql ="select '' as dummy_group, abt_id, abt_createddt, abt_balance_iqd_before, abt_balance_iqd_after,"
		+ " abt_balance_usd_before, "
		+ " (case when abt_safe_impact='ADD_SAFE' then abt_payment_usd else  -1*abt_payment_usd end ) as abt_payment_usd, "
		+ " (case when abt_safe_impact='ADD_SAFE' then abt_payment_iqd else  -1*abt_payment_iqd end ) as abt_payment_iqd, "
		+ " abt_balance_usd_after , "
		+ " concat(abt_action_desc, ' ', abt_entity_full_name_trans_id) as full_desc, abt_safe_impact, "
		+ " abt_table, abt_table_pkcol, abt_table_pkval, abt_table_lookup_col , "
		+ " '' as fromdt, '' as todt "
		+ " from p_acctbox_transactions "
		+ " join p_accountantbox on (acb_id = abt_acctboxid and acb_userbranchid = abt_userbranchid)"
		+ "  where 1=0   ";
		
		canFilter = true;
		orderByCols = " abt_id ";
		userDefinedGroupByCol = "dummy_group";
		userDefinedFilterCols.add("acb_usid");
		userDefinedFilterCols.add("fromdt");
		userDefinedFilterCols.add("todt");
		
		userDefinedCaption = "تفاصيل مبلغ الصندوق";
		
		UserDefinedPageRows = 1000;
		
		userDefinedGridCols.add("abt_createddt");
		userDefinedGridCols.add("abt_balance_iqd_before");
		userDefinedGridCols.add("abt_payment_iqd");
		userDefinedGridCols.add("abt_balance_iqd_after");
		userDefinedGridCols.add("full_desc");
		userDefinedGridCols.add("abt_balance_usd_before");
		userDefinedGridCols.add("abt_payment_usd");
		userDefinedGridCols.add("abt_balance_usd_after");
		
		userDefinedColLabel.put("abt_createddt", "تاريخ العملية");
		userDefinedColLabel.put("abt_balance_iqd_before", "قبل العملية د.ع");
		userDefinedColLabel.put("abt_payment_iqd", "مبلغ العملية د.ع");
		userDefinedColLabel.put("abt_balance_iqd_after", "بعد العملية د.ع");
		userDefinedColLabel.put("full_desc", "العملية");
		userDefinedColLabel.put("abt_balance_usd_before", "قبل العملية $");
		userDefinedColLabel.put("abt_payment_usd", "مبلغ العملية $");
		userDefinedColLabel.put("abt_balance_usd_after", "بعد العملية $");
		userDefinedColLabel.put("fromdt", "بتاريخ");
		userDefinedColLabel.put("todt", "إلى تاريخ");
		userDefinedColLabel.put("acb_usid", "صندوق");
		
		userModifyTD.put("abt_payment_usd", "getTransactionAmountUsd({abt_payment_usd},{abt_safe_impact})");
		userModifyTD.put("abt_payment_iqd", "getTransactionAmountIqd({abt_payment_iqd},{abt_safe_impact})");
		
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
		
		userDefinedCaption = "حركات صندوق ";
		
		
		
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
		
		userDefinedSumCols.add("abt_payment_iqd");
		userDefinedSumCols.add("abt_payment_usd");
		
		UserDefinedPageRows = 1000;
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
			
			if(hashy.get("abt_payment_table").equalsIgnoreCase("P_OUTCOME")) {
				try {rs.close();}catch(Exception e) {}
				try {pst.close();}catch(Exception e) {}
				
				pst = conn.prepareStatement("select co_name from kbcost_type join p_outcome on(ou_paymenttype = co_id) where ou_id =?");
				pst.setString(1, hashy.get("abt_paymentid"));
				rs = pst.executeQuery();
				if(rs.next())
					html += " - "+rs.getString("co_name");
			}
				
				
			
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
		boolean foundSearch = false;
		int branchId = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		userDefinedFilterLookups.put("acb_usid", "select us_id, us_name "
				+ "from kbusers where us_id in "
				+ " (select acb_usid From p_accountantbox where acb_userbranchid ="+branchId+") ");
		super.initialize(smartyStateMap);
		
		String fromDate = "", toDate = "", usId = "";
		if (search_paramval !=null ) {
			if (search_paramval.get("todt")!=null && search_paramval.get("fromdt")!=null) {
				
				for (String parameter : search_paramval.keySet()) {
					for (String value : search_paramval.get(parameter)) {
						if (!parameter.equals("filter") && (value != null)
								&& (!value.equals(""))) {
							if (parameter.equals("fromdt")) {
								fromDate=value;
							} else if (parameter.equals("todt")) {
								toDate=value;
							}else if (parameter.equals("acb_usid")) {
								usId=value;
								foundSearch = true;
							}
						}
					}
				}
			}
		}
		if(toDate.isEmpty())
			toDate = fromDate;
		if(!fromDate.isEmpty()&&!toDate.isEmpty())
			userDefinedWhere = " and acb_userbranchid = "+branchId+" and acb_usid = ("+usId+") "
					+ "and abt_date >= date('"+fromDate+"') and abt_date<=date('"+toDate+"') ";
		if (foundSearch) {
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				pst = conn.prepareStatement("select us_name, ifnull(acb_balance_iqd,0) as acb_balance_iqd  "
						+ "from p_accountantbox "
						+ "join kbusers on(us_id=acb_usid) where acb_usid = ? and acb_userbranchid = ?");
				pst.setString(1, usId);
				pst.setInt(2, branchId);
				rs = pst.executeQuery();
				if(rs.next()) {
					userDefinedCaption += " &nbsp;  _ صندوق مالي : "+rs.getString("us_name")+" 	/ 	المبلغ المتوفر في الصندوق حالياً : "+numFormat.format(rs.getDouble("acb_balance_iqd"));
				}
			}catch (Exception e) {
				setInsertErrorFlag(true);
				try {conn.rollback();} catch (Exception ignoreE) {}
				e.printStackTrace();
			} finally {
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
			}
				
			MainSql  = MainSql.replaceAll("where 1=0", " where 1=1 ");
			
		}
	}
	@Override
	public StringBuilder genListing(){
		search_paramval.remove("fromdt");
		search_paramval.remove("todt");
		return super.genListing();
	}

	public String getTransactionAmountUsd(HashMap<String, String> hashy) {
		String tdStyle="";
		if(!hashy.get("abt_payment_usd").equalsIgnoreCase("0.0") && !hashy.get("abt_payment_usd").equalsIgnoreCase("0")) {
			if(hashy.get("abt_safe_impact").equalsIgnoreCase("ADD_SAFE")) {
				 tdStyle="background-color: #029ea8;color:white";
			}else if(hashy.get("abt_safe_impact").equalsIgnoreCase("DEDUCT_SAFE")) {
				 tdStyle="background-color: #bd1e03;color:white";
			}
		}
		String html = "<td style='"+tdStyle+"'>"+numFormat.format(Double.parseDouble(hashy.get("abt_payment_usd")));
		html +="</td>";
		return html;
	}
	
	public String getTransactionAmountIqd(HashMap<String, String> hashy) {
		String tdStyle="";
		if(!hashy.get("abt_payment_iqd").equalsIgnoreCase("0.0") && !hashy.get("abt_payment_iqd").equalsIgnoreCase("0")) {
			if(hashy.get("abt_safe_impact").equalsIgnoreCase("ADD_SAFE")) {
				 tdStyle="background-color: #029ea8; color:white";
			}else if(hashy.get("abt_safe_impact").equalsIgnoreCase("DEDUCT_SAFE")) {
				 tdStyle="background-color: #bd1e03;  color:white";
			}
		}
		String html = "<td style='"+tdStyle+"'>"+numFormat.format(Double.parseDouble(hashy.get("abt_payment_iqd")));
		html +="</td>";
		return html;
	}
}
