package com.app.incomeoutcome;

import java.util.HashMap;

import smarty.core.CoreMgr;
public class partnerShareProfiteReport extends CoreMgr{
	public partnerShareProfiteReport() {
		String PrintPartnerShareProfiteButton = " concat('تفاصيل الشحنات وألارباح', '<a href=\"../TLKPrintPartnerShareProfiteReportSRVL?partner=',c_pickupagent,'&fromdt=','&todt=','\" style=\"padding-right:20px;\" >"
				+ " <input type=\"button\" value=\" طباعة التقرير \"   class=\"btn btn-danger btn-sm\" ></a>') as dummygroupy ";
		MainSql = "select c_company_senderpmtid, cppc_amount_paid, ps_share_center, ps_share_rural, SUM(ifnull(c_shipmentprofit,0)-ifnull(c_partnershare,0)) as mashprofit, "
				+ " SUM(ifnull(c_shipmentprofit,0)) as shipmentprofit, "
				+ " SUM(ifnull(c_partnershare,0)) as partnershare , "
				+ " c_company_sender, '' as fromdate, '' as todate, c_pickupagent ,"
				+ " SUM(case when (c_rural='Y') then 1 else 0  end) as rural, "
				+ " SUM(case when (c_rural='N') then 1 else 0 end ) as center, "
				+ " '' as grouptype, c_custid,"
				+ " count(*) as allshipmentes, "+PrintPartnerShareProfiteButton
				+ " from p_cases "
				+ " join p_customer_payments_company on (cppc_id = c_company_senderpmtid and c_company_sender = cppc_companyid) "
				+ " join kbpartner_share on (ps_compid = c_company_sender and ps_userid = c_pickupagent)"
				+ " where 1=0 " 
				+ " group by c_company_senderpmtid, c_company_sender ";
		
		mainTable = "p_cases";
		
		canFilter = true;
		
		userDefinedFilterCols.add("c_pickupagent");
		userDefinedFilterCols.add("fromdate");
		userDefinedFilterCols.add("todate");
		userDefinedFilterCols.add("grouptype");
		
		userDefinedGroupByCol = "dummygroupy";
		userDefinedGroupColsOrderBy = "c_company_sender";
		userDefinedSumCols.add("cppc_amount_paid");
		userDefinedSumCols.add("rural");
		userDefinedSumCols.add("center");
		userDefinedSumCols.add("allshipmentes");
		userDefinedSumCols.add("shipmentprofit");
		userDefinedSumCols.add("partnershare");
		userDefinedSumCols.add("mashprofit");
		groupSumCaption = "المجموع";
		
		userDefinedGridCols.add("c_company_sender");
		userDefinedGridCols.add("c_company_senderpmtid");
		userDefinedGridCols.add("cppc_amount_paid");
		userDefinedGridCols.add("rural");
		userDefinedGridCols.add("ps_share_rural");
		userDefinedGridCols.add("center");
		userDefinedGridCols.add("ps_share_center");
		userDefinedGridCols.add("allshipmentes");
		userDefinedGridCols.add("shipmentprofit");
		userDefinedGridCols.add("partnershare");
		userDefinedGridCols.add("mashprofit");
		
		userDefinedColLabel.put("c_company_sender", "ألشركة المرسلة");
		userDefinedColLabel.put("c_company_senderpmtid", "رقم الايصال");
		userDefinedColLabel.put("cppc_amount_paid", "مبلغ الايصال");
		userDefinedColLabel.put("rural", "شحنات الاطراف");
		userDefinedColLabel.put("ps_share_rural", "نسبة الشريك للاطراف");
		userDefinedColLabel.put("ps_share_center", "نسبة الشريك للمركز");
		userDefinedColLabel.put("center", "شحنات المركز");
		userDefinedColLabel.put("allshipmentes", "كل الشحنات");
		userDefinedColLabel.put("shipmentprofit", "أرباح الشحنات");
		userDefinedColLabel.put("partnershare", "أرباح الشريك");
		userDefinedColLabel.put("mashprofit", "أرباح الشركة");
		userDefinedColLabel.put("fromdate", "من تاريخ");
		userDefinedColLabel.put("todate", "ألى تاريخ");
		userDefinedColLabel.put("c_pickupagent", "ألشريك");
		userDefinedColLabel.put("grouptype", "ترتيب الدفعات");
		userDefinedColLabel.put("c_name", "العميل");
		
		userDefinedNewColsHtmlType.put("fromdate", "DATE");
		userDefinedNewColsHtmlType.put("todate", "DATE");
		userDefinedNewColsHtmlType.put("c_pickupagent", "DROPLIST");
		
		userDefinedColsMustFillFilter.add("fromdate");
		userDefinedColsMustFillFilter.add("todate");
		userDefinedColsMustFillFilter.add("c_pickupagent");
		userDefinedColsMustFillFilter.add("grouptype");
		
		userDefinedLookups.put("grouptype", "select 'COMP','شركات' from dual union select 'CUST', 'زبائن' from dual");
		userDefinedLookups.put("c_pickupagent", "select us_id, us_name from kbusers where us_rank='PICKUPAGENT'");
		userDefinedLookups.put("c_company_sender", "select comp_id, comp_name from kbcompanies");
		
		userDefinedCaption = "أرباح ألشريك";
		UserDefinedPageRows = 500;
	}
	
	public void initialize(HashMap smartyStateMap) {
		boolean foundSearch = false;
		super.initialize(smartyStateMap);
		String fromDate = "", toDate = "", grouptype = "";
		if (search_paramval !=null ) {
			if (search_paramval.get("todate")!=null && search_paramval.get("fromdate")!=null && search_paramval.get("grouptype")!=null) {
				
				for (String parameter : search_paramval.keySet()) {
					for (String value : search_paramval.get(parameter)) {
						if (!parameter.equals("filter") && (value != null)
								&& (!value.equals(""))) {
							if (parameter.equals("fromdate")) {
								fromDate=value;
								foundSearch = true;
							} else if (parameter.equals("todate")) {
								toDate=value;
							} else if (parameter.equals("grouptype")) {
								grouptype=value;
							}
						}
					}
				}
			}
		}
		if(foundSearch) {
			String todt = toDate.replace("-", "");
			String fromdt = fromDate.replace("-", "");
			//System.out.println("todt = "+todt+"  fromdt = "+fromdt);
			String PrintPartnerShareProfiteButton = " concat('تغاصيل الشحنات وألارباح', '<a href=\"../TLKPrintPartnerShareProfiteReportSRVL?partner=',c_pickupagent,'&fromdt="+fromdt+"&todt="+todt+"&grouptype="+grouptype+"\" style=\"padding-right:20px;\" >"
					+ " <input type=\"button\" value=\" طباعة التقرير \"   class=\"btn btn-danger btn-sm\" ></a>') as dummygroupy ";
			if(grouptype.equals("COMP")) {
				userDefinedGridCols.set(0, "c_company_sender");
				
				MainSql = "select c_company_senderpmtid,"
						+ " ifnull(cppc_amount_paid,0) as cppc_amount_paid, "
						+ " ifnull(ps_share_center,0) as ps_share_center,"
						+ " ifnull(ps_share_rural,0) as ps_share_rural,"
						+ " SUM(ifnull(c_shipmentprofit,0)-ifnull(c_partnershare,0)) as mashprofit, "
						+ " SUM(ifnull(c_shipmentprofit,0)) as shipmentprofit, "
						+ " SUM(ifnull(c_partnershare,0)) as partnershare ,  "
						+ " c_company_sender, "
						+ " SUM(case when (c_rural='Y') then 1 else 0  end) as rural, "
						+ " SUM(case when (c_rural='N') then 1 else 0 end ) as center, "
						+ " count(*) as allshipmentes, "+PrintPartnerShareProfiteButton
						+ " from p_cases "
						+ " join kbcompanies on(comp_id = c_company_sender) "
						+ " join p_customer_payments_company on (cppc_id = c_company_senderpmtid and c_company_sender = cppc_companyid)"
						+ " left join kbpartner_share on (ps_compid = c_company_sender and ps_userid = c_pickupagent)"
						+ " where c_settled = 'FULL' and (ifnull(ps_share_center,0)!=0 ) "
						+ " and (ifnull(ps_share_rural,0)!=0 ) and"
						+ " ( date(cppc_createddt)>=date('"+fromDate+"') and  date(cppc_createddt)<=adddate(date('"+toDate+"'),1) )"
						+ " group by c_company_senderpmtid, c_company_sender order by comp_id";
		}else if(grouptype.equals("CUST")) {
				userDefinedGridCols.set(0, "c_name");
				userDefinedGroupColsOrderBy = "c_custid";
				MainSql = "select c_pmtid as c_company_senderpmtid,"
						+ " ifnull(cp_amount_paid,0) as cppc_amount_paid, "
						+ " ifnull(sp_price_share, 0) as ps_share_center, "
						+ "	ifnull(sp_rural_share, 0) as ps_share_rural,"
						+ " SUM(ifnull(c_shipmentprofit,0)-ifnull(c_partnershare,0)) as mashprofit, "
						+ " SUM(ifnull(c_shipmentprofit,0)) as shipmentprofit, "
						+ " SUM(ifnull(c_partnershare,0)) as partnershare ,  "
						+ " c_company_sender, c_name, c_custid, "
						+ " SUM(case when (c_rural='Y') then 1 else 0  end) as rural, "
						+ " SUM(case when (c_rural='N') then 1 else 0 end ) as center, "
						+ " count(*) as allshipmentes, "+PrintPartnerShareProfiteButton
						+ " from p_cases "
						+ " join kbcustomers on (c_custid = cust_id)"
						+ " join p_customer_payments on (cp_id = c_pmtid and cp_custid = c_custid)"
						+ " left join kb_special_prices on (sp_custid = c_custid and sp_statecode = c_rcv_state)"
						+ " where c_settled = 'FULL' and (ifnull(sp_price_share,0)!=0 or ifnull(sp_rural_share,0)!=0 ) and"
						+ " ( date(cp_createddt)>=date('"+fromDate+"') and date(cp_createddt)<=adddate(date('"+toDate+"'),1) )"
						+ " group by c_pmtid, c_custid order by c_custid";
			}
		}
	}
	@Override
	public StringBuilder genListing(){
		search_paramval.remove("fromdate");
		search_paramval.remove("todate");
		search_paramval.remove("grouptype");
		return super.genListing();
	}
}
