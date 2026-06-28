package com.app.reports;

import java.util.HashMap;
import java.util.regex.Pattern;

import smarty.core.CoreMgr;

public class ShipmentsDelayInStoreReport extends CoreMgr{
	public ShipmentsDelayInStoreReport() {
		MainSql = "select DATE_FORMAT(cc_createddt,'%Y-%m-%d %H:%i') as cc_createddt, c_receiptamt_usd, dlva.us_name, mcust_name, cust_name, c_custid, c_custreceiptnoori, c_branchcode, c_receiptamt, "
				+ " concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) as address, q_branch, "
				+ " c_rcv_name, c_qty, DATE_FORMAT(c_createddt,'%Y-%m-%d %H:%i') as c_createddt, '' as delay, "
				+ " q_enterdate, TIMESTAMPDIFF(HOUR,cc_createddt, now()) as delayhours "
				+ " , q_step, c_assignedagent, c_pickupagent, c_mastercustid "
				+ " from p_cases "
				+ " join p_caseschain on (cc_caseid = c_id and cc_frombranch={userstorecode}) "
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join kbstate on (c_rcv_state = st_code and st_branch={userstorecode})  "
				+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
				+ " join kbcustomers on (c_custid = cust_id)"
				+ " join kb_mastercustomer on (mcust_id = c_mastercustid)"
				+ " left join kbusers dlva on (c_assignedagent = dlva.us_id and dlva.us_rank = 'DLVAGENT')"
				+ " where q_status !='CLS' and 1=0 ";
		
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("q_step");
		userDefinedGridCols.add("q_branch");
		userDefinedGridCols.add("dlva.us_name");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("address");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("cc_createddt");
		userDefinedGridCols.add("delayhours");
		
		
		userDefinedColLabel.put("cc_createddt","تاريخ الأعطاء للفرع");
		userDefinedColLabel.put("dlva.us_name","مندوب التوصيل");
		userDefinedColLabel.put("mcust_name","العميل");
		userDefinedColLabel.put("q_branch","في فرع");
		userDefinedColLabel.put("cust_name", "المتجر");
		userDefinedColLabel.put("q_enterdate", "تاريخ ووقت الحاله");
		userDefinedColLabel.put("c_createddt","تاريخ الشحنه");
		userDefinedColLabel.put("c_custid","المتجر");
		userDefinedColLabel.put("c_assignedagent","مندوب التوصيل");
		userDefinedColLabel.put("c_mastercustid","العميل");
		userDefinedColLabel.put("c_pickupagent","مندوب الأستلام");
		userDefinedColLabel.put("c_rcv_name","المستلم");
		userDefinedColLabel.put("address","العنوان");
		userDefinedColLabel.put("delay","ساعات التأخير أكثر من");
		userDefinedColLabel.put("c_qty","عدد القطع");
		userDefinedColLabel.put("c_rcv_state","المحافظة");
		userDefinedColLabel.put("c_custreceiptnoori","رقم الوصل");
		userDefinedColLabel.put("c_receiptamt","مبلغ الوصل");
		userDefinedColLabel.put("delayhours","ساعات التأخير");
		userDefinedColLabel.put("q_step","المرحلة");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
		
		canFilter = true;
		mainTable = "p_cases";
		userDefinedFilterCols.add("delay");
		userDefinedFilterCols.add("q_step");
		userDefinedFilterCols.add("q_branch");
		userDefinedFilterCols.add("c_rcv_state");
		//userDefinedFilterCols.add("c_assignedagent");
		//userDefinedFilterCols.add("c_custid");
		
		userDefinedFilterColsHtmlType.put("c_rcv_state", "DROPLIST");
		userDefinedLookups.put("c_rcv_state", "select st_code, st_name_ar from kbstate where st_branch = 1");
		
		userDefinedExportLandScape = false;

		userDefinedFilterColsHtmlType.put("delay", "INT");
		
		userDefinedCaption = "تقرير الشحنات ألمعلقة في جميع المراحل لزمن محدد";
		
		userDefinedLookups.put("q_step", "select stp_code, stp_name from kbstep where stp_stgcode not in ('DLV', 'CNCL' , 'NEWCUSTLOGI') "
				+ " and stp_code not in ('NEW_ONWAY', 'MANIFEST_BRANCHES', 'RTN_WITHLIAISONAGENT')");
		userDefinedLookups.put("q_branch", "select branch_id, branch_name "
				+ " from kbbranches where branch_active = 'Y' "
				+ " and branch_id in (select distinct(path_tobranch) From kbpaths where path_frombranch = {userstorecode})");
		userDefinedLookups.put("c_assignedagent", "select us_id, us_name from kbusers where us_branchcode = {userstorecode} and us_rank = 'DLVAGENT'");
		userDefinedLookups.put("c_pickupagent", "select us_id, us_name from kbusers where us_branchcode = {userstorecode} and us_rank = 'PICKUPAGENT'");
		userDefinedLookups.put("c_mastercustid", "select mcust_id, mcust_name from kb_mastercustomer where mcust_branchcode = {userstorecode}");
		userDefinedLookups.put("c_custid", "select cust_id, cust_name from kbcustomers where cust_branch = {userstorecode}");
		
		userDefinedFilterColsHtmlType.put("q_step", "MULTILIST");
		//userDefinedFilterColsHtmlType.put("c_assignedagent", "DROPLIST");
		//userDefinedFilterColsHtmlType.put("c_pickupagent", "DROPLIST");
		//userDefinedFilterColsHtmlType.put("c_mastercustid", "DROPLIST");
		//userDefinedFilterColsHtmlType.put("c_custid", "DROPLIST");
		userDefinedFilterColsHtmlType.put("q_branch", "DROPLIST");
		
		userDefinedColsMustFillFilter.add("delay");
		userDefinedColsMustFillFilter.add("q_step");
		
		UserDefinedPageRows = 200;
		
		canExport = true;
		pdfExport = true;
	}
	
	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search
		int currentBranch_G = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		int delayInHours = 0;
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {
					if (parameter.equals("delay")) {
						delayInHours = Integer.parseInt(value);
						foundSearch = true;
					}
				}
			}
		}
		
		if (foundSearch && delayInHours>0) {
			String whereClause = " and cc_createddt <= date_add(now(), interval -"+delayInHours+" hour)";
			MainSql = MainSql.replaceAll(Pattern.quote("{userstorecode}"), currentBranch_G+"").replaceAll("and 1=0",  " ")+" "+whereClause;
			
		}
	}
	@Override
	public StringBuilder genListing(){
		search_paramval.remove("delay");
		return super.genListing();
	}
}
