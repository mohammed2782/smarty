package com.app.incomeoutcome;

import java.util.HashMap;

import com.app.core.CoreMgr;

public class ToBeReturnedShipments extends CoreMgr {
	public ToBeReturnedShipments () {
		MainSql = "select  q_stage, q_step,'' as status , cm_custid,c_custreceiptnoori,'' as totamt,'' as pmtrmk, '' as pmtdate, "
				+ " date(c_createddt) as c_createddt , c_weight, concat(ct_name_ar,' - ',ifnull(c_rcv_addr_rmk,'')) as addr, c_id, "
				+ " c_rcv_name , c_rcv_hp, '' as fromdate, '' as todate,"
				+ " c_receiptamt,"
				+ " c_sendmoney"
				+ " from p_cases join p_casesmaster on cm_id = c_cmid "
				+ " join p_queue on (c_id= q_caseid and q_status !='CLS')"
				+ " left join kbcity on ct_code = c_rcv_city"
				+ " where cm_custid ={customerAcct} "
				+ " and (q_stage='cncl' and q_step='return_to_cust')  and (c_branchcode='{userstorecode}' or '{superRank}'='Y') order by c_custreceiptnoori ";
		
		userDefinedGridCols.add("c_id");
		//userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_rcv_hp");
		userDefinedGridCols.add("c_weight");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		
		userDefinedGridCols.add("c_receiptamt");
		
		userDefinedGridCols.add("c_sendmoney");
		//userDefinedGridCols.add("netamt");
		//userDefinedGridCols.add("status");
		
		userDefinedColLabel.put("c_id", "رقم الشحنه");
		userDefinedColLabel.put("c_createddt", "تاريخ الشحنات");
		userDefinedColLabel.put("c_rcv_name", "إسم المستلم");
		userDefinedColLabel.put("c_weight", "الوزن");
		userDefinedColLabel.put("addr", "العنوان");
		userDefinedColLabel.put("c_rcv_hp", "هاتف");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("c_shipment_cost", "تكلفة الشحن");
		userDefinedColLabel.put("cm_custid", "الزبون");
		userDefinedColLabel.put("pmtdate", "تاريخ الدفع");
		userDefinedColLabel.put("pmtrmk", "ملاحظات");
		userDefinedColLabel.put("totamt", "المبلغ المطلوب المستحق للزبون");
		userDefinedColLabel.put("c_sendmoney", "مبلغ مرسل إلى المستلم");
		userDefinedColLabel.put("netamt", "الصافي للعميل");
		userDefinedColLabel.put("status", "الحاله");
		userDefinedColLabel.put("fromdate","من تاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		
		userDefinedCaption = "شحنات راجعه";
		
		UserDefinedPageRows = 1000;
	}
	@Override
	public void initialize(HashMap smartyStateMap){
		
		String custId = replaceVarsinString(" {customerAcct} ", arrayGlobals).trim();
		
		String printbtn = "<a href='../ClemancePrintReturnedItmesSRVL?cust_id="+custId+"'"
				+ " class='btn btn-sm btn-danger'>طباعة كشف بالمرتجعات <i class=\"fa fa-print fa-lg\"></i></a>";
		userDefinedCaption = userDefinedCaption+"</br></br>"+printbtn;
		
		
		super.initialize(smartyStateMap);
	}
}
