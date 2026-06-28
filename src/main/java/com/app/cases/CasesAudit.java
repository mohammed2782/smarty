package com.app.cases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import com.app.util.UtilitiesFeqar;

public class CasesAudit extends smarty.core.CoreMgr{
	public CasesAudit() {
		MainSql = "select '' as fromdt, '' as todate ,   c_id, c_audited_status,c_createdby	, c_rcv_name	, c_rcv_hp1	  		, c_rcv_hp2		 , c_rcv_state," + 
				"	 c_rural		, c_rcv_addr_rmk, c_rmk 		 	, c_qty	  		 , c_receiptamt," + 
				"	c_shipment_cost, c_branchcode	, c_custreceiptnoori, c_rcv_district , c_custid	, " + 
				"	c_custhp		, c_mastercustid, c_mastercusthp    , c_pickupagent  , c_productinfo,"  
				+ " (case when cc_branchpmtid>0 or c_settled='FULL' or c_agentsharesettled='FULL' or c_pickupagentpmtid>0 then 'flase' else 'true' end) as canedit,"
				+"	c_createddt, c_receiptamt_usd "
				+ " From p_cases "
				+ " left join p_caseschain on (c_id = cc_caseid and cc_tobranch = {userstorecode})"
				+ "where c_branchcode ={userstorecode} "
				+ " and c_pmtid =0 and c_agentpmtid = 0 and c_createddt >=date_add(now(), interval -10 day)"
				+ " and 1=0";
		
		userDefinedLookups.put("custname", "select cust_id , cust_name from kbcustomers order by cust_name ASC");
		
		userDefinedLookups.put("c_audited_status", "select kbcode, kbdesc from kbgeneral where kbcat1='CASES' and kbcat2 = 'TAGS' and kbcat3='AUDIT'");
		userDefinedLookups.put("c_rcv_state", "select st_code , st_name_ar from kbstate  order by st_order");
		userDefinedLookups.put("c_mastercustid", "select mcust_id , mcust_name from kb_mastercustomer where mcust_branchcode={userstorecode}");
		userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers where cust_branch={userstorecode}");
		userDefinedLookups.put("c_rcv_district", "select cdi_id, cdi_name from kbcity_district  ");
		userModifyTD.put("c_audited_status", "showAuditTag({c_audited_status},{c_id})");
		//userModifyTD.put("c_custreceiptnoori", "custReceiptNoOriEdit({c_id},{c_branchcode},{c_custreceiptnoori},{canedit})");
		
		userDefinedColLabel.put("c_custreceiptnoori", "");
		userDefinedColLabel.put("c_mastercustid", "العميل");
		userDefinedColLabel.put("c_custid", "المتجر");
		userDefinedColLabel.put("c_rcv_name","المستلم");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف المستلم");
		userDefinedColLabel.put("c_rcv_hp2", "2هاتف المستلم");
		userDefinedColLabel.put("c_rcv_state", "المحافظه");
		userDefinedColLabel.put("c_rcv_addr_rmk", "تفاصيل العنوان");
		userDefinedColLabel.put("c_rural", "أطراف");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("c_qty", "عدد");
		userDefinedColLabel.put("c_assignedagent","مندوب التوصيل");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل د. ع.");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
		userDefinedColLabel.put("c_shipment_cost","مبلغ الشحن");
		userDefinedColLabel.put("c_sendmoney","مبلغ مرسل");
		userDefinedColLabel.put("c_fragile","قابل للكسر");
		userDefinedColLabel.put("c_bringitemsback","جلب  بضاعه");
		userDefinedColLabel.put("c_branchcode","خلق في فرع");
		userDefinedColLabel.put("c_weight","الوزن");
		userDefinedColLabel.put("c_custreceiptnoori","رقم الوصل");
		userDefinedColLabel.put("c_id","كود الشحنة");
		userDefinedColLabel.put("q_step","المرحله");
		userDefinedColLabel.put("c_createdby","المدخل");
		userDefinedColLabel.put("c_settled","تم التحاسب ؟");
		userDefinedColLabel.put("c_rcv_district", "المنطقة");
		userDefinedColLabel.put("c_agentshare", "مبلغ الشحن للمندوب");
		userDefinedColLabel.put("c_createddt", "تاريخ");
		userDefinedColLabel.put("c_audited_status", "");
		userDefinedColLabel.put("todate", "الى تاريخ");
		userDefinedColLabel.put("fromdt", "بتاريخ");
		userDefinedColLabel.put("c_audited_status", "التدقيق");
		
		userDefinedGridCols.add("c_audited_status");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_mastercustid");
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("c_rcv_hp2");
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("c_rcv_district");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("c_createdby");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_rmk");
		
		userDefinedLookups.put("c_createdby", "select us_id , us_name from kbusers where us_branchcode={userstorecode}");
		userDefinedCaption = "تدقيق الوصولات";
		
		canFilter = true;
		userDefinedFilterCols.add("c_rcv_state");
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("c_receiptamt");
		userDefinedFilterCols.add("fromdt");
		userDefinedFilterCols.add("todate");
		userDefinedFilterCols.add("c_audited_status");
		userDefinedFilterColsHtmlType.put("fromdt", "DATETIME");
		userDefinedFilterColsHtmlType.put("todate", "DATETIME");
		userDefinedFilterCols.add("c_createdby");
		//userDefinedFilterColsHtmlType.put("c_audited_status", "DROPLIST");
		
		UserDefinedPageRows = 1000;
		
	}
	
//	public String custReceiptNoOriEdit (HashMap<String,String> hashy) {
//		StringBuilder sb = new StringBuilder("<td><div style='display:flex;'>");
//		sb.append("<div class='col-7'>"+hashy.get("c_custreceiptnoori")+"</div>");
//		sb.append("<div class='col-5' style='display:contents;'><button type=\"button\" class=\"btn btn-info btn-sm\" "
//				+ "onclick=\"popitup ('../logistics/editCaseFromStages?caneditfromstage="+hashy.get("canedit")+"&branchidfromstage="+hashy.get("c_branchcode")+""
//						+ "&caseidfromstage="+hashy.get("c_id")+"' , '' , 1000 ,600);\"><li class=\"lni lni-pencil\"></li></button></div>");
//		sb.append("</div></td>");
//		return sb.toString();
//	}
	
	public String showAuditTag(HashMap<String,String>hashy) {
		String okClass = "btn btn-light btn-sm";
		String notOkClass= "btn btn-light btn-sm";
		String likeIconClass = "fa fa-solid fa-thumbs-up fa-lg me-0";
		String disLikeIconClass = "fa fa-solid fa-thumbs-down fa-lg me-0";
		if (hashy.get("c_audited_status").equalsIgnoreCase("OK")) {
			okClass = "btn btn-success btn-sm";
		}else if (hashy.get("c_audited_status").equalsIgnoreCase("NOTOK")) {
			notOkClass= "btn btn-danger btn-sm";
		}
		
		String button =  "<div class=\"col\">"
				+ "<button id='audit-status-btn-"+hashy.get("c_id")+"-__OKORNOTOK__'"
				+ " onclick=\"auditCheckStatusTagUntag(this,__val_when_clicked__);\"  data-this-col='c_audited_status' data-caseid='"+hashy.get("c_id")+"' "
						+ " data-val='"+hashy.get("c_audited_status")+"' "
						+ " type=\"button\" class='__buttonclass__'>"
				+ "<i class=\"__iconclass__\"></i></button></div>";
		
		
		String html = "<td width='12%' ><div class='row row-cols-auto g-2'>"
		+button.replace("__iconclass__", likeIconClass).replace("__OKORNOTOK__", "OK").replace("__buttonclass__", okClass).replace("__val_when_clicked__", "'OK'")
		+button.replace("__iconclass__", disLikeIconClass).replace("__OKORNOTOK__", "NOTOK").replace("__buttonclass__", notOkClass).replace("__val_when_clicked__", "'NOTOK'");
		html+= "</div></td>";
		return html;
	}
	
	
	@Override
	public void initialize(HashMap smartyStateMap){
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		super.initialize(smartyStateMap);

		if (search_paramval!=null && !search_paramval.isEmpty()) {
			MainSql = MainSql.replace("1=0", "1=1");
		}
		String fromdt = "ALL";
		String todt = "ALL";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {
					if (parameter.equals("fromdt")) {
						fromdt =  value;
						foundSearch = true;
					} 
					if (parameter.equals("todate")) {
						todt =  value;
					} 
				}
			}
		}
		if (foundSearch) {
			if (todt.equalsIgnoreCase("ALL") && !fromdt.equalsIgnoreCase("ALL")) {
				todt = fromdt;
			}
			if (!fromdt.equalsIgnoreCase("ALL")) {
				MainSql +=" and  (date(c_createddt)>='"+fromdt+"') and (date(c_createddt)<='"+todt+"' ) ";
			}
		}
		orderByCols ="  c_id desc ";		
	}
	@Override 
	public StringBuilder genListing() {
		//System.out.println("calling gen listing---------------------");
		search_paramval.remove("fromdt");
		search_paramval.remove("todate");
		return super.genListing();
	}
}
