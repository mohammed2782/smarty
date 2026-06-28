package com.app.incomeoutcome;

import java.util.HashMap;

import smarty.core.CoreMgr;



public class IncomeOutcomeRpt extends CoreMgr{
	public double totCredit = 0;
	public double totDebit = 0;
	public long totNetIqd = 0;
	public long totNetUsd = 0;
	
	public IncomeOutcomeRpt () {
		MainSql = "select '' as trandate, '' as todate, '' as userid, '' as net_iqd, '' as  net_usd,"
				+ " ''  as trantype, '' as trans_operationentity, '' as trancode, '' as createdby from dual where 1=0";
		
		userDefinedGroupByCol = "userid";
		userDefinedGridCols.clear();
		
		userDefinedFilterCols.add("trandate");
		userDefinedFilterCols.add("todate");
		userDefinedFilterCols.add("userid");
		
		//userDefinedColsMustFillFilter.add("userid");
		userDefinedColsMustFillFilter.add("trandate");
		userDefinedColsMustFillFilter.add("todate");
		userDefinedNewColsHtmlType.put("trandate", "DATE");
		userDefinedNewColsHtmlType.put("todate", "DATE");
		
		userDefinedGridCols.add("trans_operationentity");
		userDefinedGridCols.add("net_iqd");
		//userDefinedGridCols.add("createdby");
		
		userDefinedColLabel.put("trans_operationentity", "العملية");
		userDefinedColLabel.put("net_iqd", "المبلغ");
		userDefinedColLabel.put("trandate", "من تاريخ");
		userDefinedColLabel.put("todate", "الى تاريخ");
		userDefinedColLabel.put("userid", "المستخدم");
		
		
		canFilter = true;
		
		userDefinedLookups.put("trans_operationentity", "select kbcode, kbdesc from kbgeneral "
				+ "where kbcat1='FIN_TRANS_ENTITY' and kbcat2 = 'DESCRIPTION'");
		
		userModifyTD.put("net_iqd", "modifyNetAmt({net_iqd}, {net_usd},"
				+ "{trans_operationentity},{trandate}, {userid}, {todate})");
		
		userDefinedPageFooterFunction = "thisFooter()";
	}
	
	public String thisFooter(String colName) {
		if (colName.equalsIgnoreCase("net_iqd"))
			return "<td dir='rtl' align='center'>"
			+"<div class='row'>"
			+ "<div class='col-5'>"
				+numFormat.format(totNetIqd) + " دينار عراقي"
			+ "</div>"
			+ "<div class='col-4'>"
				+numFormat.format(totNetUsd)+ " دولار أمريكي"
			+ "</div>"
			+"</td>";
		else 
			return "<td>المبلغ المتبقي</td>";
	}
	public String modifyNetAmt(HashMap<String,String> hashy) {
		String s = "<td><div class='row'>";
		String button="<button type=\"button\" class=\"btn btn-xs btn-info\""
		+ "  onclick=\"popitup ('showTransactionsDtlsPopUp?trancode="+hashy.get("trans_operationentity")+"&accttranuserid="
		+hashy.get("userid")+"&trandate="+hashy.get("trandate")+"&todate="+hashy.get("todate")+"' , '' , 1000 ,600);\">تفاصيل</button>";
		long amtIqd = Double.valueOf(hashy.get("net_iqd")).longValue();
		long amtUsd =  Double.valueOf(hashy.get("net_usd")).longValue();
		totNetIqd +=amtIqd;
		totNetUsd +=amtUsd;
		s +="<div class='col-2'>"+button+"</div>";
		s +="<div class='col-3'>"+numFormat.format(amtIqd)+" دينار عراقي </div>";
		s +="<div class='col-3'>"+numFormat.format(amtUsd)+" دولار أمريكي </div>";
		s +="</div></td>";
		return s;
	}
	
	public void initialize(HashMap smartyStateMap) {
		int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		userDefinedLookups.put("userid", "select us_id, us_name "
				+ "from kbusers where us_id in "
				+ " (select acb_usid From p_accountantbox where acb_userbranchid ="+branchId_G+") ");
		super.initialize(smartyStateMap);
		boolean foundSearch = false;
		boolean userIdFound = false;
		String userid = "", trandate = "", todate = "";
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null)
						&& (!value.equals(""))) {
					if (parameter.equals("trandate")) {
						trandate=value;
						foundSearch = true;
					} else if (parameter.equals("todate")) {
						todate=value;
					} else if (parameter.equals("userid")) {
						userid=value;
						userIdFound = true;
					}
				}
			}
		}
	
		userDefinedFilterColsHtmlType.put("userid", "DROPLIST");
		// the next statement is important so we can skip the generating of
		// where clause from search_paramval
	
		if (foundSearch) {
			if(userIdFound) {
				MainSql = 
				"select '"+userid+"' as userid, '"+trandate+"' as trandate, '"+todate+"' as todate, trans_operationentity, "
				+ " trans_createdby, "
				+ " sum(case when kbcat4 = 'ADD_SAFE'  "
				+ "		then trans_amount_paid_actually_iqd  "
				+ "		else -1*trans_amount_paid_actually_iqd end) as net_iqd , "
				+ "	sum(case when kbcat4 = 'ADD_SAFE'  then trans_amount_paid_actually_usd  "
				+ "		else -1*trans_amount_paid_actually_usd end) as net_usd "
				+ "from p_fin_transactions "
				+ "	 join kbgeneral on kbcat1 =trans_operationentity  "
				+ "		and  kbcat2 = trans_operationcat and kbcode = trans_operationcode "
				+ "	 where  trans_initiated_in_branch_id = "+branchId_G+" and trans_deleted = 'N' "
				+ "	 and trans_createdby = "+userid+" "
				+ " and trans_createddt >= '"+trandate + "' "
				+ " and trans_createddt < date_add('"+todate+"',interval 1 day ) "
				+ " group by trans_operationentity "
				+ "union "
				+ "select '"+userid+"' as userid, '"+trandate+"' as trandate, '"+todate+"' as todate,"
				+ "'RECEIVE_FROM_BRANCH' as  trans_operationentity, "
				+ " trans_createdby, "
				+ " sum(trans_amount_received_actually_iqd) as net_iqd , "
				+ "	sum(trans_amount_received_actually_usd) as net_usd "
				+ "from p_fin_transactions "
				+ "	 where  trans_entity_id = "+branchId_G+" and trans_deleted = 'N'"
				+ " and trans_did_branch_receive='Y' "
				+ "	and trans_received_by = "+userid+" "
				+ " and trans_receiveddt >= '"+trandate + "' "
				+ " and trans_receiveddt < date_add('"+todate+"',interval 1 day )"
				+ " and trans_operationentity='BRANCH' "
				+ " group by trans_operationentity ";
			}else {
				MainSql = 
						"select trans_createdby as userid, '"+trandate+"' as trandate, '"+todate+"' as todate, trans_operationentity, "
						+ " trans_createdby, "
						+ " sum(case when kbcat4 = 'ADD_SAFE'  "
						+ "		then trans_amount_paid_actually_iqd  "
						+ "		else -1*trans_amount_paid_actually_iqd end) as net_iqd , "
						+ "	sum(case when kbcat4 = 'ADD_SAFE'  then trans_amount_paid_actually_usd  "
						+ "		else -1*trans_amount_paid_actually_usd end) as net_usd "
						+ "from p_fin_transactions "
						+ "	 join kbgeneral on kbcat1 =trans_operationentity  "
						+ "		and  kbcat2 = trans_operationcat and kbcode = trans_operationcode "
						+ "	 where  trans_initiated_in_branch_id = "+branchId_G+" and trans_deleted = 'N' "
						+ " and trans_createddt >= '"+trandate + "' "
						+ " and trans_createddt < date_add('"+todate+"',interval 1 day ) "
						+ " group by trans_operationentity, trans_createdby "
						+ "union "
						+ "select trans_received_by as userid, '"+trandate+"' as trandate, '"+todate+"' as todate,"
						+ "'RECEIVE_FROM_BRANCH' as  trans_operationentity, "
						+ " trans_createdby, "
						+ " sum(trans_amount_received_actually_iqd) as net_iqd , "
						+ "	sum(trans_amount_received_actually_usd) as net_usd "
						+ "from p_fin_transactions "
						+ "	 where  trans_entity_id = "+branchId_G+" and trans_deleted = 'N'"
						+ " and trans_did_branch_receive='Y' "
						+ " and trans_receiveddt >= '"+trandate + "' "
						+ " and trans_receiveddt < date_add('"+todate+"',interval 1 day )"
						+ " and trans_operationentity='BRANCH' "
						+ " group by trans_operationentity, trans_received_by ";
			}
		}
	}
	@Override 
	public StringBuilder genListing() {
		//System.out.println("calling gen listing---------------------");
		search_paramval.remove("trandate");
		search_paramval.remove("todate");
		search_paramval.remove("userid");
		return super.genListing();
	}
}
