package com.app.reports;

import smarty.core.CoreMgr;

public class RepeatedReceiptsNumber extends CoreMgr{
	public RepeatedReceiptsNumber () {
		MainSql = "select c_createddt, c_custreceiptnoori, c_custid,c_pmtid , c_settled , c_rcv_state,c_rcv_district, c_rcv_hp1" + 
				" from p_cases where c_custreceiptnoori in (select  c_custreceiptnoori from p_cases group by c_custreceiptnoori, c_rcv_hp1 having count(*) >1)" + 
				" order by c_id ";
		
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_pmtid");
		
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("c_rcv_district");
		userDefinedGridCols.add("c_rcv_hp1");
		
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_createddt", "تاريخ الشحنة");
		userDefinedColLabel.put("c_custid", "الزبون");
		userDefinedColLabel.put("c_pmtid", "رقم الدفعة");
		userDefinedColLabel.put("c_rcv_state", "المحافظة");
		userDefinedColLabel.put("c_rcv_district", "المنطقة");
		userDefinedColLabel.put("c_rcv_hp1", "رقم هاتف المستلم");
		
		userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers");
		userDefinedLookups.put("c_rcv_state", "select st_code, st_name_ar from kbstate");
		userDefinedLookups.put("c_rcv_district", "select cdi_id, cdi_name from kbcity_district");
	
		UserDefinedPageRows = 2000;
		
		userDefinedCaption = "شحنات مشتبهه بتكرارها";
	}

}
