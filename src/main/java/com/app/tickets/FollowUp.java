package com.app.tickets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import com.app.util.UtilitiesFeqar;

import smarty.core.CoreMgr;

public class FollowUp extends CoreMgr{
	public FollowUp() {
		MainSql = "select c_pickupagent, q_enterdate,  c_provided_followup, c_followupby,  c_rtnreason, '' as fromdt, '' as todate , c_createdby, q_stage, q_step, stp_name, date(c_createddt) as c_createddt , "
				+ " c_id , cust_name     , c_custhp		 , "
				  + " c_rcv_name 	 , c_rcv_hp1      , c_rcv_state		 , "
				  + " c_rural        , concat (ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk, c_rmk , c_productinfo, "
				  + " c_qty		     , c_receiptamt  , c_shipment_cost   ,c_assignedagent , c_agentsharesettled, "
				  + " c_fragile	   , c_mastercustid, c_cust_rtnid, c_pickupagent_rtnid, c_pmtid, c_pickupagentpmtid,  "
				  + " c_branchcode	 		 , c_custreceiptnoori, c_specialcase, branch_name, c_allowrtnagent, "
				  + "  '' as others  , '' as custname    ,"
				  + " '' as custprimaryphone, c_settled , c_rcv_district, stp_color, c_allowrtncustomer, rtn_desc, q_postopnedto, q_postponedoption, post_desc "
				  + " from p_cases "
				  + " left join kbstep on stp_code= q_step "
				  + " left join kbrtn_reasons on (rtn_code = c_rtnreason)"
				  + " left join kbbranches on q_branch = branch_id "
				  + " left join kbcustomers on (cust_id = c_custid)"
				  + " left join kbpostponedoptions on (q_postponedoption = post_code)"	
				  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
				  + " where 1=0 and q_step not in ('DLEIVERD', 'RTN_WITHLIAISONAGENT') " + 
				  "	 and c_cust_rtnid =0 and  c_pmtid=0  and c_pickupagentpmtid=0 and c_pickupagent_rtnid=0"
				  + "   and c_created_date_only >=date_add(now(), interval -5 day) ";
	
		canFilter = true;
		userDefinedCaption = "شحنات للمتابعة";
		
		userDefinedFilterColsHtmlType.put("c_branchcode", "DROPLIST");
		
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("stp_name");
		userDefinedGridCols.add("q_enterdate");
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_productinfo");
		userDefinedGridCols.add("c_branchcode");
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		//userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("c_rural");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_createdby");
		
		userDefinedColLabel.put("c_mastercustid", "العميل");
		userDefinedColLabel.put("c_specialcase", "شحنه خاصه؟");
		userDefinedColLabel.put("c_productinfo", "تفاصيل البضاعة");
		userDefinedColLabel.put("stp_name", "المرحله");
		userDefinedColLabel.put("cust_name", "المتجر");
		userDefinedColLabel.put("c_rcv_name","المستلم");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف المستلم");
		userDefinedColLabel.put("c_rcv_hp2", "2هاتف المستلم");
		userDefinedColLabel.put("c_rcv_state", "المحافظه");
		userDefinedColLabel.put("c_rcv_addr_rmk", "تفاصيل العنوان");
		userDefinedColLabel.put("c_rural", "أطراف");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("c_qty", "عدد");
		userDefinedColLabel.put("c_assignedagent","مندوب التوصيل");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("c_shipment_cost","مبلغ الشحن");
		userDefinedColLabel.put("c_sendmoney","مبلغ مرسل");
		userDefinedColLabel.put("c_fragile","قابل للكسر");
		userDefinedColLabel.put("c_bringitemsback","جلب  بضاعه");
		userDefinedColLabel.put("c_branchcode","خلق في فرع");
		userDefinedColLabel.put("c_weight","الوزن");
		userDefinedColLabel.put("c_custreceiptnoori","رقم الوصل");
		userDefinedColLabel.put("c_id","كود الشحنة");
		userDefinedColLabel.put("q_enterdate", "وقت أخر تحديث");
		userDefinedColLabel.put("q_step","المرحله");
		userDefinedColLabel.put("c_createdby","المدخل");
		userDefinedColLabel.put("c_settled","تم التحاسب ؟");
		userDefinedColLabel.put("c_rcv_district", "المنطقة");
		userDefinedColLabel.put("c_agentshare", "مبلغ الشحن للمندوب");
		userDefinedColLabel.put("del_edit"," ");
		userDefinedColLabel.put("q_previous_action_taken_by", "اخر من حدث الحاله");
		userDefinedColLabel.put("c_receiptfromsystem", "متولد من النظام؟");
		userDefinedColLabel.put("c_receiptfromsystem","مولد من النظام");
		userDefinedColLabel.put("c_pickupagent","مندوب إستلام");
		userDefinedColLabel.put("c_provided_followup","توجد معالجة");
		//userDefinedFilterCols.add("c_mastercustid");
		//userDefinedFilterCols.add("c_custid");
		//userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers order by cust_name asc");
		userDefinedLookups.put("c_rcv_district", "select cdi_id, cdi_name from kbcity_district");
		userDefinedLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userDefinedEditLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userDefinedLookups.put("c_createdby", "select us_id , us_name from kbusers");
		userDefinedLookups.put("q_previous_action_taken_by", "select us_id , us_name from kbusers");
		userDefinedLookups.put("c_branchcode", "select branch_id, branch_name from kbbranches");
		//userDefinedEditLookups.put("c_custid", "!select cust_id , cust_name from kbcustomers where cust_mastercustid ={c_mastercustid} order by cust_name asc");
		userDefinedLookups.put("c_pickupagent", "select us_id , us_name from kbusers  where us_rank = 'PICKUPAGENT'  ");
	
		userDefinedFilterColsHtmlType.put("c_rcv_hp1", "PHONE");
		
	
		userDefinedFilterCols.add("c_rcv_hp1");
		//userDefinedFilterCols.add("c_id");
		
		userDefinedColLabel.put("fromdt","بتاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		
		userDefinedFilterCols.add("c_rcv_state");
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("q_step");
		//userDefinedFilterCols.add("c_settled");
		userDefinedFilterCols.add("c_receiptamt");
		userDefinedFilterCols.add("c_assignedagent");
		userDefinedFilterCols.add("fromdt");
		userDefinedFilterCols.add("todate");
		userDefinedFilterCols.add("q_previous_action_taken_by");
		userDefinedFilterCols.add("c_rtnreason");
		userDefinedFilterCols.add("q_postponedoption");
		userDefinedFilterCols.add("c_provided_followup");
		userDefinedFilterCols.add("c_pickupagent");
		userDefinedFilterCols.add("c_branchcode");
		
		userDefinedFilterColsHtmlType.put("fromdt", "DATE");
		userDefinedFilterColsHtmlType.put("todate", "DATE");
		userDefinedFilterColsHtmlType.put("q_previous_action_taken_by", "DROPLIST");
		
		
		userDefinedColLabel.put("c_createddt","تاريخ الشحنه");
		userDefinedColLabel.put("c_rtnreason","سبب الراجع");
		userDefinedColLabel.put("q_postponedoption","سبب التأجيل");
		
		userDefinedLookups.put("c_branchcode", "select branch_id, branch_name from kbbranches");
		userDefinedLookups.put("c_rtnreason", "select rtn_code, rtn_desc from kbrtn_reasons");
		userDefinedLookups.put("q_postponedoption", "select post_code, post_desc from kbpostponedoptions");
		userDefinedLookups.put("custname", "select cust_id , cust_name from kbcustomers order by cust_name ASC");
		userDefinedLookups.put("custprimaryphone", "!select cust_phone1 as ph, cust_phone1 from kbcustomers where cust_id = '{custname}'");
		userDefinedLookups.put("c_rcv_state", "select st_code , st_name_ar from kbstate  order by st_order");
		
		
		userDefinedLookups.put("c_rural", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_provided_followup", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_settled", "select kbcode , kbdesc from kbgeneral where kbcat1='SETTLED'");
		
		userDefinedLookups.put("q_step", "select stp_code , stp_name from kbstep");
		userDefinedLookups.put("c_assignedagent", "select us_id , us_name from kbusers  where us_rank = 'DLVAGENT'   ");
		userDefinedLookups.put("c_pickupagent", "select us_id , us_name from kbusers  where us_rank = 'PICKUPAGENT'  ");
		//userDefinedLookups.put("c_mastercustid", "select mcust_id , mcust_name from kb_mastercustomer");
		
		userDefinedFilterColsHtmlType.put("c_pickupagent" , "DROPLIST");
		//userDefinedFilterColsHtmlType.put("c_custid", "DROPLISTBIGDATA");
		userDefinedFilterColsHtmlType.put("c_mastercustid", "DROPLISTBIGDATA");
		//userDefinedFilterColsHtmlType.put("c_assignedagent" , "DROPLIST");
		
		UserDefinedPageRows = 50;
		
		userModifyTD.put("c_rcv_hp1", "modifyHp1({c_id},{c_followupby}, {c_rcv_hp1}, {c_custreceiptnoori}, {c_provided_followup})");
		
		userModifyTD.put("stp_name", "modifyStepName({c_agentsharesettled},{c_allowrtnagent},{c_allowrtncustomer},{rtn_desc},"
				+ "{c_custreceiptnoori},{c_id},{stp_name},{c_settled},{q_stage},{q_step},{stp_color},{branch_name},{c_cust_rtnid},{c_pickupagent_rtnid},{c_pmtid},"
				+ "{c_pickupagentpmtid},{inbranch},{q_postopnedto},{post_desc})");
		//userModifyTD.put("stp_name", "modifyStepName({stp_name},{c_settled},{q_stage},{q_step})");
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		//userDefinedEditMockUpCols.put("custname", "(select cm_custid from p_casesmaster where cm_id = c_cmid)");
		//userDefinedFilterColsHtmlType.put("c_custid", "DROPLIST");
		//userDefinedUseDataTables = true;
	}//end of no-arg constructor Updatecase

	public String modifyHp1(HashMap<String,String>hashy) {
		
		String html = "<td>";
		String buttonText =  "<a href='tel:"+hashy.get("c_rcv_hp1")+"'>"+hashy.get("c_rcv_hp1")+"</a>";
		String followUpLink = "<div class=\"color-indigators d-flex align-items-center gap-2\">"
				+ "<div><a  style='' id='followup-displaycaseinfo-div-"+hashy.get("c_id")+"' class=\"nav-link active\" "
				+ " onclick=\"popitup ('../cases/displaySingleCaseInfo?smarty_PlayPageInPopUpMode=true&auditcaseid="+hashy.get("c_id")+"' , '' , 1350 ,700);\" "
				+ " data-bs-toggle='pill' href='javascript:;'><div class='font-20'><i class='lni lni-eye'></i></div></a></div>";
		if (hashy.get("c_followupby").equalsIgnoreCase("0")) {
			buttonText = "<button type='button' id='btn-start-followup-"+hashy.get("c_id")+"' class='btn btn-info btn-sm' onclick ='openForFollowUp("+hashy.get("c_id")+", "+hashy.get("c_custreceiptnoori")+")' type=\"button\" >بدء المتابعة</button>";
			buttonText += "<span style='display:none;' id='hidden_rcv_hp1_"+hashy.get("c_id")+"'>"+"<a href='tel:"+hashy.get("c_rcv_hp1")+"'>"+hashy.get("c_rcv_hp1")+"</a>"+"</span>";
			followUpLink = followUpLink.replace("style=''", "style='display:none'");
		}
	
		if (hashy.get("c_provided_followup").equalsIgnoreCase("Y")) {
			followUpLink +="<div class=\"color-indigator-item bg-primary\"></div>";
		}
		buttonText += followUpLink+"</div>";
		html +=buttonText;
		html+= "</td>";
		return html;
	}

	@Override
	public void initialize(HashMap smartyStateMap){
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		int userId = Integer.parseInt(replaceVarsinString("{usid}",arrayGlobals).trim());
		String  userRank = replaceVarsinString("{userRank}",arrayGlobals).trim();
		userDefinedFilterLookups.put("c_assignedagent", "select us_id , us_name from kbusers  where us_rank = 'DLVAGENT' and us_branchcode="+currentBranch+"   ");
		
		super.initialize(smartyStateMap);
		MainSql = "select c_pickupagent , q_enterdate, c_provided_followup, c_followupby, c_rtnreason, '' as del_edit, c_receiptfromsystem, q_previous_action_taken_by , c_createdby, q_stage, q_step, stp_name, "
				+ " date(c_createddt) as c_createddt , c_id , cust_name     , c_custhp		 , "
				  + " c_rcv_name 	 , c_rcv_hp1      , c_rcv_state		 , "
				  + " c_rural        , concat (ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk, c_rmk , c_productinfo, "
				  + " c_qty		     , c_receiptamt  , c_shipment_cost   ,c_assignedagent ,  c_agentsharesettled,"
				  + " c_fragile	   , c_cust_rtnid, c_pickupagent_rtnid,  c_pmtid, c_pickupagentpmtid,"
				  + " c_branchcode	 		 , c_custreceiptnoori, c_specialcase, branch_name, c_allowrtnagent, "
				  + "  '' as others  , '' as custname    ,  c_mastercustid,  "
				  + " '' as custprimaryphone, c_settled , c_rcv_district, stp_color, c_allowrtncustomer, rtn_desc, q_postopnedto, q_postponedoption, post_desc "
				  + " from p_cases "
				  + " left join kbstep on stp_code= q_step "
				  + " left join kbpostponedoptions on (q_postponedoption = post_code)"
				  + " left join kbcustomers on (cust_id = c_custid)"
				  + " left join kbbranches on q_branch = branch_id "
				  + " left join kbrtn_reasons on (rtn_code = c_rtnreason)"
				  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "					 
				  + " where (q_branch="+currentBranch+" or c_branchcode ="+currentBranch+")  and q_step not in ('DLEIVERD', 'RTN_WITHLIAISONAGENT')"
				  		+ " and c_cust_rtnid =0 and c_pmtid=0  and c_pickupagentpmtid=0  and c_pickupagent_rtnid=0"
				  		+ "   and c_created_date_only >=date_add(now(), interval -5 day) ";
		if (search_paramval!=null && !search_paramval.isEmpty()) {
		
					  
				/*if (userRank.equalsIgnoreCase("IT") || userRank.equalsIgnoreCase("IT") || userRank.equalsIgnoreCase("IT") ||
						userRank.equalsIgnoreCase("IT") )
					MainSql += " and (c_followupby = 0 or c_followupby ="+userId+") ";*/
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
						//search_paramval.remove("fromdt");
					} 
					if (parameter.equals("todate")) {
						todt =  value;
						//search_paramval.remove("todate");
						//foundSearch = true;
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
		orderByCols ="  q_enterdate desc ";		
	}
	@Override 
	public StringBuilder genListing() {
		//System.out.println("calling gen listing---------------------");
		search_paramval.remove("fromdt");
		search_paramval.remove("todate");
		return super.genListing();
	}

	public String modifyStepName(HashMap<String,String>hashy) {
		String buttonText = hashy.get("stp_name")+"<br /> في فرع - "+hashy.get("branch_name");
		if(hashy.get("q_stage").equalsIgnoreCase("DLV")) {
				if(Integer.parseInt(hashy.get("c_pmtid"))>0)
					buttonText += "<br /> رقم كشف حساب العميل "+hashy.get("c_pmtid");
				else if(Integer.parseInt(hashy.get("c_pickupagentpmtid"))>0)
					buttonText += "<br /> رقم كشف حساب مندوب الاستلام "+hashy.get("c_pickupagentpmtid");
			
		}
		if((hashy.get("c_allowrtncustomer").equalsIgnoreCase("Y") || hashy.get("c_allowrtnagent").equalsIgnoreCase("Y"))
				&& (hashy.get("q_step").contains("RTN") || hashy.get("q_step").equalsIgnoreCase("PART_SUCC"))) {
			if(hashy.get("q_step").contains("RTN"))
				buttonText += "<br /> "+hashy.get("rtn_desc");
			if(Integer.parseInt(hashy.get("c_cust_rtnid"))>0)
				buttonText += "<br /> رقم كشف راجع العميل "+hashy.get("c_cust_rtnid");
			else if(Integer.parseInt(hashy.get("c_pickupagent_rtnid"))>0)
				buttonText += "<br /> رقم كشف راجع مندوب الاستلام "+hashy.get("c_pickupagent_rtnid");
		}
		/*if(hashy.get("q_step").equalsIgnoreCase("PART_SUCC")) {
			if(Integer.parseInt(hashy.get("c_cust_rtnid"))==0 && Integer.parseInt(hashy.get("c_pickupagent_rtnid"))==0) {
				Connection conn2 = null;
				PreparedStatement pst = null;
				ResultSet rs = null;
				String inBranch = "";
				try {
					conn2 = mysql.getConn();
					pst = conn2.prepareStatement("select branch_name from kbbranches where branch_id = ?");
					pst.setString(1, hashy.get("inbranch"));
					rs = pst.executeQuery();
					if(rs.next())
						inBranch = rs.getString("branch_name");
				}catch (Exception e) {
					e.printStackTrace();
				}finally {
					try {rs.close();}catch(Exception e) {}
					try {pst.close();}catch(Exception e) {}
					try {conn2.close();}catch(Exception e) {}
				}
				buttonText += "<br /> الجزء الراجع في فرع "+inBranch;
			}
		}*/
		if(hashy.get("q_stage").equalsIgnoreCase("AGENTOP") && hashy.get("q_step").equalsIgnoreCase("POSTPONED")) {
			buttonText += "<br /> تاريخ التأجيل "+hashy.get("q_postopnedto");
			buttonText += "<br />"+hashy.get("post_desc");
		}
		
		/*String audit = "<a href='displaySingleCaseInfo?auditcaseid="+hashy.get("c_id")+"' "
				+ "class='dropdown-item' >تتبع <i class='lni lni-eye' style='font-size: 1.1rem;vertical-align: text-bottom;'></i></a>";
		String actionMenu ="<div class='dropdown'>"
				+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";' class='btn btn-light dropdown-toggle btn-sm' type=\"button\" data-bs-toggle=\"dropdown\" aria-expanded=\"false\">"+buttonText+"</button>"
				+ " <ul class='dropdown-menu'>";
		
		actionMenu +="<li>"+audit+"</li></ul></div>";*/
		
		
		String html = "<td width='12%' >";
		html+="<button type='button' style='background-color:"+hashy.get("stp_color")+";' class='btn btn-light dropdown-toggle btn-sm' type=\"button\" data-bs-toggle=\"dropdown\" aria-expanded=\"false\">"+buttonText+"</button>";
		
		html+= "</td>";
		return html;
	}
}
