package com.app.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class MasterCustomerDetails extends CoreMgr{
	
	public MasterCustomerDetails(){
		
		String masterCustButton = "concat ( concat(mcust_name, '<a href=\"../../PrintMasterCustManifestSRVL?stdate=ALL&mastercustid=',c_mastercustid,'&storecode=',c_branchcode,'\" style=\"padding-right:20px;\" >"
				+ " <input type=\"button\" value=\" طباعة مانفيست الشحنات \"   class=\"btn btn-dark btn-sm\" ></a>')) as masterCust ";
		
		
		MainSql = "select c_rcv_state, c_id, c_custreceiptnoori, c_mastercustid, mcust_name, cust_name, concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,''))  as address, "
				+ "c_rcv_hp1, c_receiptamt, c_receiptamt_usd , c_rmk, c_createddt, '' as fromDate, '' as toDate, " + masterCustButton + " ,"
						+ " c_shipment_cost , (c_receiptamt - c_shipment_cost) as net_iqd "
				+ "from p_cases "
				+ "join kb_mastercustomer on (c_mastercustid = mcust_id and mcust_branchcode = {userstorecode}) "
				+ "join kbcustomers on (c_custid = cust_id and cust_branch = {userstorecode}) "
				+ "join kbstate on (c_rcv_state = st_code and st_branch = {userstorecode}) "
				+ "left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
				+ "where 1=0 and c_branchcode = {userstorecode}  ";
				
		mainTable = "p_cases";

		canFilter = true;

		userDefinedCaption = "تفاصيل العملاء";
		UserDefinedPageRows = 1000; 
		
		userDefinedGroupColsOrderBy = " mcust_name ";
		userDefinedGroupSortMode = "ASC";
		userDefinedGroupByCol = "masterCust";

		userDefinedSumCols.add("c_receiptamt");
		userDefinedSumCols.add("c_receiptamt_usd");
		userDefinedSumCols.add("c_shipment_cost");
		userDefinedSumCols.add("net_iqd");
		groupSumCaption = "المجموع";
		
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("address");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("net_iqd");
		userDefinedGridCols.add("c_rmk");

		
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_mastercustid", "العميل");
		userDefinedColLabel.put("cust_name", "المتجر");
		userDefinedColLabel.put("address", "العنوان");
		userDefinedColLabel.put("c_rcv_hp1", "رقم هاتف الزبون");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل د.ع");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("fromDate", "من تاريخ");
		userDefinedColLabel.put("toDate", "الى تاريخ");
		userDefinedColLabel.put("c_rcv_state", "المحافظة");
		userDefinedColLabel.put("c_shipment_cost", "أجرة الشحن");
		userDefinedColLabel.put("net_iqd", "صافي د.ع");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
		
		userDefinedFilterCols.add("c_mastercustid");
		userDefinedFilterCols.add("fromDate");
		userDefinedFilterCols.add("toDate");
		userDefinedFilterCols.add("c_rcv_state");
		
		userDefinedFilterColsHtmlType.put("c_mastercustid", "DROPLIST");
		userDefinedFilterColsHtmlType.put("fromDate", "DATE");
		userDefinedFilterColsHtmlType.put("toDate", "DATE");
		userDefinedFilterColsHtmlType.put("c_rcv_state", "CLASSIC_MULTILIST");

		userDefinedColsMustFillFilter.add("c_mastercustid");
		userDefinedColsMustFillFilter.add("fromDate");
		
		userDefinedLookups.put("c_mastercustid","select mcust_id , mcust_name  From kb_mastercustomer where mcust_branchcode = {userstorecode} ");
		userDefinedLookups.put("c_rcv_state","select st_code , st_name_ar"
				+ "  From kbstate where  st_branch = {userstorecode} ");

	}// end of no-arg MasterCustomerDetails

	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		// build the search
		
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());

		String fromExpDt = "ALL", toExpDt = "ALL", mastercustId = "";
		boolean foundExpDt = false;
		String states = "";
		boolean firstState = true;

		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {
					if (parameter.equals("fromDate")) {
						fromExpDt = value;
						foundExpDt = true;
					}else if (parameter.equals("toDate")) {
						toExpDt = value ;
					}
					if (parameter.equals("c_mastercustid")) {
						mastercustId = value;
					}
					if (parameter.equals("c_rcv_state")) {
						
						if (!firstState) {
							states +=",";
						}
						states += value;
						firstState = false;
						
					}
				}
			}
		}
		// the next statement is important so we can skip the generating of
		// where clause from search_paramval			

			if (foundExpDt) {					
				if (toExpDt.equalsIgnoreCase("ALL") && !fromExpDt.equalsIgnoreCase("ALL")) {
					toExpDt = fromExpDt;
				}
				if (!fromExpDt.equalsIgnoreCase("ALL")) {
					String masterCustButton = "concat ( concat(mcust_name, '<a href=\"../../PrintMasterCustManifestSRVL?states=" +states+ "&stdate=" + fromExpDt + "&todate=" + toExpDt+ "&mastercustid=',c_mastercustid,'&storecode=',c_branchcode,'\" style=\"padding-right:20px;\" >"
							+ " <input type=\"button\" value=\" طباعة مانفيست الشحنات \"   class=\"btn btn-dark btn-sm\" ></a>')) as masterCust ";
									
			MainSql = "select c_rcv_state, c_id, c_custreceiptnoori, c_mastercustid, mcust_name, cust_name, concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,''))  as address, "
					+ "c_rcv_hp1, c_receiptamt , c_receiptamt_usd, c_rmk, c_createddt, '' as fromDate, '' as toDate, " + masterCustButton + " , "
							+ " c_shipment_cost ,  (c_receiptamt - c_shipment_cost) as net_iqd  "
					+ "from p_cases "
					+ "join kb_mastercustomer on (c_mastercustid = mcust_id and mcust_branchcode = " +currentBranch+ ") "
					+ "join kbcustomers on (c_custid = cust_id and cust_branch = " +currentBranch+ ") "
					+ "join kbstate on (c_rcv_state = st_code and st_branch = " +currentBranch+ ") "
					+ "left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where 1= 1 and c_branchcode = " +currentBranch+ " and c_mastercustid = " + mastercustId + " "
					+ " and " + " c_createddt >= DATE_FORMAT('" + fromExpDt + "', '%Y-%m-%d')" 
					+ " and " + " c_createddt < adddate(DATE_FORMAT('" + toExpDt + "', '%Y-%m-%d'), 1)" +  " ";					
				}
			}
		}

	@Override
	public StringBuilder genListing(){
		search_paramval.remove("fromDate");
		search_paramval.remove("toDate");
		return super.genListing();
	}//end of method genListing

}// end of class MasterCustomerDetails
