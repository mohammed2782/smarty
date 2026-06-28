package com.app.tickets;

import java.util.HashMap;
import java.util.Map;

import smarty.core.CoreMgr;


public class ShowAllTickets extends CoreMgr {
	private int i =1;
	public ShowAllTickets() {
		//canEdit   = true;
		//canDelete = true;
		canFilter = true;
		userDefinedFilterCols.add("tkt_id");
		userDefinedFilterCols.add("tkt_priority");
		//userDefinedFilterCols.add("tkt_relatedcustomer");
		userDefinedFilterCols.add("tkt_relatedagent");
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("tkt_status");
		userDefinedFilterCols.add("tkt_assignedemp");
		
		MainSql = "select ifnull(c_custreceiptnoori,0) as c_custreceiptnoori, tkt_id, '' as ticketdesc, subjects.kbdesc as ticketsubject, tkt_subject, tkt_description, tkt_createdby, tkt_createddt, "
				+ " tkt_status, tkt_relatedcustomer,tkt_relatedshop , concat(mcust_name, ' - ', cust_name) as customer, ifnull(closers.us_name,'') as closedby, tkt_createfromsys, "
				+ " tkt_closedby , ifnull(tkt_closeddt, '') as tkt_closeddt, tkt_priority, tkt_ownerbranch, tkt_forcase, tkt_relatedagent, tkt_relatedbranch, tkt_assignedemp, tkt_closeremarks, "
				+ " (case    "
				+ "  when  tkt_creatortype in ('MASTERCUST' , 'MASTERCUSTOMER') then ( select mcust_name from kb_mastercustomer where mcust_id =  tkt_createdby)    "
				+ "  when  tkt_creatortype = 'CUSTOMER' then ( select cust_name from kbcustomers where cust_id =  tkt_createdby)  "
				+ " else  (select us_name from kbusers where us_id =  tkt_createdby) end) as createdby, tkt_creatortype, branch_name, statuses.kbdesc_ar as status_ar  "
				+ " from "
				+ " p_tickets "
				+ " join kbbranches on branch_id =  tkt_ownerbranch "
				+ " left join kbusers closers on closers.us_id = tkt_closedby and closers.us_rank not in ('DLVAGENT', 'MASTERCUSTOMER') "
				+ " left join kbgeneral subjects on subjects.kbcode = tkt_subject and subjects.kbcat1='TICKET' and subjects.kbcat2='SUBJECTS' "
				+ " left join kbgeneral statuses on statuses.kbcode = tkt_status and statuses.kbcat1='TICKET' and statuses.kbcat2='STATUS' "
				+ " left join kb_mastercustomer  on mcust_id = tkt_relatedcustomer  "
				+ " left join kbcustomers  on cust_id = tkt_relatedshop  "
				+ " left join p_cases on c_id = tkt_forcase where  tkt_createddt >= date_add(now(), interval -60 day)";
		
		orderByCols = "  tkt_id desc ";
		userDefinedFilterColsHtmlType.put("tkt_relatedcustomer", "DROPLIST");
		userDefinedFilterColsHtmlType.put("tkt_relatedagent", "DROPLIST");
	
		
		userModifyTD.put("ticketdesc", "showTicketInfo({ticketsubject}, {branch_name}, {tkt_createfromsys},{tkt_description},{tkt_priority}, "
				+ "{createdby},{tkt_creatortype}, {tkt_createddt},{tkt_forcase},{tkt_id} , {c_custreceiptnoori})");
		userModifyTD.put("tkt_status", "showStatusInfo({closedby},{tkt_closedby}, {tkt_closeddt}, {tkt_closeremarks}, {status_ar}, {tkt_status})");
		
		userDefinedLookups.put("tkt_relatedbranch", "select branch_id, branch_name from kbbranches");
		//SuserDefinedLookups.put("tkt_relatedcustomer", " select mcust_id , mcust_name from  kb_mastercustomer ");
		userDefinedLookups.put("tkt_relatedshop", " select cust_id , cust_name from  kbcustomers ");
		userDefinedLookups.put("tkt_createdby", " select us_id , us_name from  kbusers ");
		userDefinedLookups.put("tkt_relatedagent", " select us_id , us_name from  kbusers where us_rank = 'DLVAGENT' ");
		userDefinedLookups.put("tkt_priority", "select kbcode, kbdesc from kbgeneral where kbcat1='TICKET' and kbcat2 = 'PRIORITY' ");
		userDefinedLookups.put("tkt_status", "select kbcode, kbdesc_ar from kbgeneral where kbcat1='TICKET' and kbcat2 = 'STATUS' ");
		userDefinedLookups.put("tkt_ownerbranch", "select branch_id, branch_name from kbbranches ");
		userDefinedLookups.put("tkt_assignedemp", "select us_id, us_name from kbusers where us_rank in ('ITBOSS','CALL_CENTER') ");
		
		userDefinedColLabel.put("tkt_id", "رقم التذكرة");
		userDefinedColLabel.put("tkt_subject", "السبب");
		userDefinedColLabel.put("tkt_description", "وصف المشكلة");
	
		userDefinedColLabel.put("tkt_status", "الحالة");
		userDefinedColLabel.put("tkt_relatedcustomer", "عميل");
		userDefinedColLabel.put("tkt_relatedshop", "المتجر");
		userDefinedColLabel.put("tkt_relatedagent", "المندوب");
		userDefinedColLabel.put("tkt_relatedbranch", "الفرع ذو الصلة");
		userDefinedColLabel.put("tkt_ownerbranch", "أنشأ في فرع");
		userDefinedColLabel.put("c_custreceiptnoori","رقم الوصل");
		userDefinedColLabel.put("tkt_createddt", "تاريخ ووقت الأنشاء");
		userDefinedColLabel.put("ticketdesc", "الوصف");
		userDefinedColLabel.put("customer", "العميل");
		userDefinedColLabel.put("tkt_assignedemp", "موظف المتابعة");
		userDefinedColLabel.put("tkt_priority", "الأهمية");
		
		userDefinedGridCols.add("tkt_id");
		userDefinedGridCols.add("ticketdesc");
		userDefinedGridCols.add("tkt_status");
		userDefinedGridCols.add("customer");
		userDefinedGridCols.add("tkt_relatedagent");
		userDefinedGridCols.add("tkt_assignedemp");
		
		userDefinedEditCols.add("tkt_subject");
		userDefinedEditCols.add("tkt_description");
		userDefinedEditCols.add("tkt_status");
		userDefinedEditCols.add("closedby");
		userDefinedEditCols.add("tkt_closeremarks");
		userDefinedEditCols.add("tkt_priority");
		userDefinedEditCols.add("tkt_forcase");
		userDefinedEditCols.add("tkt_subject");
		userDefinedFilterColsHtmlType.put("tkt_assignedemp", "DROPLIST");
		UserDefinedPageRows = 200;
	}
	
	public String showTicketInfo (HashMap<String,String> hashy) {
		
		i++;
		String creatorType=  "", sysIcon = "<i class=\"fadeIn animated bx bx-laptop\"></i>";
		String priority = "<span class='badge rounded-pill bg-danger' style='margin-right: 5px;'>عاجل</span>";
		String caseId = " ";
		String chatBtn = "<button type=\"button\" "
				+ " onclick=\"popitup ('chatbox?chatTicketId="+hashy.get("tkt_id")+"' , '' , 1100 ,600);\""
				+ " class=\"btn btn-transparent\" style='padding:2px;margin-right:7px;padding-right:5px;'><i class=\"bx bx-comment mr-1\"></i></button>";
		//<i class="fadeIn animated bx bx-chat"></i>
		if  (hashy.get("tkt_createfromsys").equalsIgnoreCase("MBAPP")) {
			sysIcon = "<i class=\"fadeIn animated bx bx-mobile-vibration\"></i>";
		}
		if (hashy.get("tkt_creatortype").equalsIgnoreCase("STAFF") ) {
			;
		}else if (hashy.get("tkt_creatortype").equalsIgnoreCase("AGENT")){
			creatorType = "مندوب توصيل - ";
		}else if (hashy.get("tkt_creatortype").equalsIgnoreCase("MASTERCUST")) {
			creatorType = "عميل - ";
		}else if (hashy.get("tkt_creatortype").equalsIgnoreCase("CUSTOMER")) {
			creatorType = "متجر - ";
		}
		if (!hashy.get("tkt_forcase").equalsIgnoreCase("0") && hashy.get("c_custreceiptnoori")!=null && !hashy.get("c_custreceiptnoori").equalsIgnoreCase("null") ) {
			caseId = "<span class=\"badge rounded-pill bg-dark\" style='margin-right:7px;'>رقم الوصل "+hashy.get("c_custreceiptnoori")+"</span>";
		}
		if (hashy.get("tkt_priority").equalsIgnoreCase("MED")) {
			priority = "<span class='badge rounded-pill bg-warning text-dark' style='margin-right: 5px;'>متوسطة</span>";
		}else if (hashy.get("tkt_priority").equalsIgnoreCase("LOW")) {
			priority ="<span class='badge rounded-pill bg-primary' style='margin-right: 5px;'>عادية</span>";
		}
	
		creatorType += hashy.get("createdby");
		StringBuilder sb = new StringBuilder("<td><div class='row g-0'><div class=\"col-md-12\"><div class=\"card-body\" style='padding: 0.2rem 0.2rem;'>");
		sb.append("<h6 class=\"card-title\">"+hashy.get("ticketsubject")+priority+caseId+chatBtn+"</h6>");
		
		sb.append("<div class='d-flex gap-3 py-3'  style=\"padding-top: 0.1rem!important; padding-bottom: 0.2rem!important;font-size: 11.5px;\">");
		sb.append(" <div>"+sysIcon+" "+creatorType+"</div>");
		sb.append(" <div><i class=\"fadeIn animated bx bx-git-branch\"></i> فرع "+hashy.get("branch_name")+"</div>");
		sb.append("<div><i class=\"fadeIn animated bx bx-calendar-edit\"></i>"+hashy.get("tkt_createddt")+"</div>");
		sb.append("</div></hr>");	
		  
		//sb.append("<div class='col-11'><span class='badge rounded-pill bg-danger' style='font-size:17px'>");
		
		sb.append("<p class=\"card-text fs-7\">"+hashy.get("tkt_description")+"</p>");
		
		
		sb.append("</div></div></div></td>");
		return sb.toString();
	}
	
	public String showStatusInfo (HashMap<String,String> hashy) {
		
		StringBuilder sb;
		if (hashy.get("tkt_status").equalsIgnoreCase("NEW")) {
			sb = new StringBuilder("<td style='background-color:#00D6D0; color:black '>");
		}else if  (hashy.get("tkt_status").equalsIgnoreCase("OPEN")) {
			sb = new StringBuilder("<td style='background-color:#ffc107; color:black'>");
		}else {
			sb = new StringBuilder("<td style='background-color:green; font-color:white'>");
		}
		sb.append(hashy.get("closedby")+"</br>"+hashy.get("tkt_closeddt")+"</br>"+hashy.get("status_ar"));
		sb.append("</td>");
		return sb.toString();
	}
	
}
