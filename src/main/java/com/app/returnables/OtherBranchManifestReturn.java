package com.app.returnables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.bussframework.FlowUtils;

import smarty.core.CoreMgr;
import smarty.db.mysql;

public class OtherBranchManifestReturn extends CoreMgr{
	public OtherBranchManifestReturn() {

		
		userDefinedGroupByCol="groupingcol";
		userDefinedGroupColsOrderBy = "rlam_id";
		userDefinedGroupSortMode = "DESC";
		
		keyCol = "rlam_id";
		mainTable = "p_rtnliaisonagent_manifest";
		canFilter = true;
		
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("stp_name");
		userDefinedGridCols.add("c_mastercustid");
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_productinfo");
		userDefinedGridCols.add("c_branchcode");
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("q_previous_action_taken_by");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("c_rural");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("c_createdby");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_specialcase");

		userDefinedFilterCols.add("rlam_id");
		// //////////////
		userDefinedCaption = "الفروع رواجع الشركات";
		userDefinedColLabel.put("rlam_id", "رقم المنفيست");
		userDefinedColLabel.put("rlam_agentid", "مندوب الأرتباط");
		userDefinedColLabel.put("rlam_createddt", "تاريخ الاستلام الفعلي");
		userDefinedColLabel.put("rlam_rmk", " ملاحظات");
		userDefinedColLabel.put("rlam_noofshipments", "عدد الشحنات");
		userDefinedColLabel.put("rlam_createdby", "انشئ بواسطة");
		userDefinedColLabel.put("fake", "طباعة ايصال الاستلام ");
		userDefinedColLabel.put("c_mastercustid", "العميل");
		userDefinedColLabel.put("c_specialcase", "شحنه خاصه؟");
		userDefinedColLabel.put("c_productinfo", "تفاصيل البضاعة");
		userDefinedColLabel.put("stp_name", "المرحله");
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
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("c_shipment_cost","مبلغ الشحن");
		userDefinedColLabel.put("c_sendmoney","مبلغ مرسل");
		userDefinedColLabel.put("c_fragile","قابل للكسر");
		userDefinedColLabel.put("c_bringitemsback","جلب  بضاعه");
		userDefinedColLabel.put("c_branchcode","خلق في فرع");
		userDefinedColLabel.put("c_custreceiptnoori","رقم الوصل");
		userDefinedColLabel.put("c_id","كود الشحنة");
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
		userDefinedColLabel.put("fromdt","بتاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		userDefinedColLabel.put("c_createddt","تاريخ الشحنه");
		userDefinedColLabel.put("c_rtnreason","سبب الراجع");
		userDefinedColLabel.put("q_postponedoption","سبب التأجيل");

		
		userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers order by cust_name asc");
		userDefinedLookups.put("c_rcv_district", "select cdi_id, cdi_name from kbcity_district");
		userDefinedLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userDefinedEditLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userDefinedLookups.put("c_createdby", "select us_id , us_name from kbusers");
		userDefinedLookups.put("q_previous_action_taken_by", "select us_id , us_name from kbusers");
		userDefinedLookups.put("c_branchcode", "select branch_id, branch_name from kbbranches");
		
		userDefinedLookups.put("c_rtnreason", "select rtn_code, rtn_desc from kbrtn_reasons");
		userDefinedLookups.put("q_postponedoption", "select post_code, post_desc from kbpostponedoptions");
		userDefinedLookups.put("custname", "select cust_id , cust_name from kbcustomers order by cust_name ASC");
		userDefinedLookups.put("custprimaryphone", "!select cust_phone1 as ph, cust_phone1 from kbcustomers where cust_id = '{custname}'");
		userDefinedLookups.put("c_rcv_state", "select st_code , st_name_ar from kbstate  order by st_order");
		userDefinedLookups.put("c_fragile", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_rural", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_settled", "select kbcode , kbdesc from kbgeneral where kbcat1='SETTLED'");
		userDefinedLookups.put("q_step", "select stp_code , stp_name from kbstep");
		userDefinedLookups.put("c_assignedagent", "select us_id , us_name from kbusers  where us_rank = 'DLVAGENT'   ");
		userDefinedLookups.put("c_pickupagent", "select us_id , us_name from kbusers  where us_rank = 'PICKUPAGENT'  ");
		userDefinedLookups.put("c_mastercustid", "select mcust_id , mcust_name from kb_mastercustomer");
		
		UserDefinedPageRows = 100;
		userModifyTD.put("stp_name", "modifyStepName({cc_branchpmtid},{c_agentsharesettled},{c_allowrtnagent},{c_allowrtncustomer},{rtn_desc},"
				+ "{c_custreceiptnoori},{c_id},{stp_name},{c_settled},{q_stage},{q_step},{stp_color},{branch_name},{c_cust_rtnid},{c_pickupagent_rtnid},{c_pmtid},"
				+ "{c_pickupagentpmtid},{inbranch},{q_postopnedto},{post_desc})");
		
		userDefinedLookups.put("rlam_createdby", "select us_id, us_name from kbusers");
		userDefinedLookups.put("rlam_agentid", "select us_id, us_name from kbusers");
		
		userDefinedCaption = "منفيست راجع الفروع";

	}// end of constructor customer_payment
	
	public void initialize(HashMap smartyStateMap){
		int otherBranch = Integer.parseInt(replaceVarsinString("{otherBranchManifestReturn}", arrayGlobals));
		int storeCode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals));
		
		String printButton = " concat('منفيست راجع رقم: ',rlam_id, '<a href=\"../../PrintReturnBranchShipmentsByRtnManifestIdSRVL?liaisonagentid=',rlam_agentid,'&rtnmanifestid=',rlam_id,'\" style=\"padding-right:20px;\" >"
				+ " <input type=\"button\" value=\" طباعة مانفيست الراجع \"   class=\"btn btn-danger btn-sm\" ></a>') as groupingcol ";
		MainSql = "select c_rtnreason, '' as del_edit, c_receiptfromsystem, q_previous_action_taken_by , c_createdby, q_stage, q_step, stp_name, "
						+ " date(c_createddt) as c_createddt , c_id , c_custid     , c_custhp		 , "
						  + " c_rcv_name 	 , c_rcv_hp1      , c_rcv_state		 , "
						  + " c_rural        , concat (ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk, c_rmk , c_productinfo, "
						  + " c_qty		     , c_receiptamt  , c_shipment_cost   ,c_assignedagent ,  c_agentsharesettled,"
						  + " c_fragile	   , c_cust_rtnid, c_pickupagent_rtnid,  c_pmtid, c_pickupagentpmtid,"
						  + " c_branchcode	 		 , c_custreceiptnoori, c_specialcase, branch_name, c_allowrtnagent, "
						  + "  '' as others  , '' as custname    , ifnull(chainfor_pmt.cc_branchpmtid,0) as cc_branchpmtid, c_mastercustid,  "
						  + " '' as custprimaryphone, c_settled , c_rcv_district, stp_color, c_allowrtncustomer, rtn_desc, q_postopnedto, q_postponedoption, post_desc, "
						  + " (case "
						  + "	when (q_step='PART_SUCC' and chain.cc_qstatus_tobranch='ACTV') then chain.cc_tobranch "
						  + "	when (q_step='PART_SUCC' and chain.cc_qstatus_frombranch='ACTV') then chain.cc_frombranch "
						  + "	else c_branchcode end) as inbranch, " 
				+ "rlam_id,"
				+ " rlam_agentid, rlam_createdby, rlam_rmk, rlam_frombranch, rlam_tobranch,  "
				+ " rlam_createddt, rlam_noofshipments, "+printButton
				+ " from p_rtnliaisonagent_manifest"
				+ " join p_caseschain mainchain on (rlam_id=cc_rtnmanifestid)"
				+ " join p_cases on (c_id = mainchain.cc_caseid ) "
				  + " join kbstep on stp_code= q_step "
				  + " left join kbpostponedoptions on (q_postponedoption = post_code)"
				  + " left join kbbranches on q_branch = branch_id "
				  + " left join kbrtn_reasons on (rtn_code = c_rtnreason)"
				  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
				  + " left join p_caseschain chainfor_pmt on (chainfor_pmt.cc_caseid = c_id and chainfor_pmt.cc_frombranch="+storeCode+" )"
				  + " left join p_caseschain chain on(chain.cc_caseid = c_id and (chain.cc_qstatus_tobranch='ACTV' or chain.cc_qstatus_frombranch='ACTV') and q_step='PART_SUCC') "
				+ " where rlam_tobranch={userstorecode} "
				+ " and rlam_deleted='N' and rlam_frombranch = "+otherBranch
				+ " order by rlam_id desc ";
		
		super.initialize(smartyStateMap);
		
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
		if(hashy.get("q_step").equalsIgnoreCase("PART_SUCC")) {
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
		}
		if(hashy.get("q_stage").equalsIgnoreCase("AGENTOP") && hashy.get("q_step").equalsIgnoreCase("POSTPONED")) {
			buttonText += "<br /> تاريخ التأجيل "+hashy.get("q_postopnedto");
			buttonText += "<br />"+hashy.get("post_desc");
		}
		//<div class="dropdown-menu">
		String buttons =""
			+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";font-size: 11.5px;' class='btn  btn-sm  waves-effect waves-light text-white'>"+buttonText+"</button>"
			+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";font-size: 11.5px;' class='btn btn-sm  dropdown-toggle waves-effect waves-light text-white' data-toggle='dropdown'"
			+ " aria-haspopup='true' aria-expanded='true'><span class='sr-only'>Toggle Dropdown</span></button>";
          
		
		String audit = "<a href='../cases/displaySingleCaseInfo?auditcaseid="+hashy.get("c_id")+"' "
				+ "class='dropdown-item' >تتبع <i class='fa fa-eye' style='font-size: 1.1rem;vertical-align: text-bottom;'></a>";
		String actionMenu ="<div class='dropdown-menu '>";
				//+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";' class='btn btn-light dropdown-toggle btn-sm' type=\"button\" data-bs-toggle=\"dropdown\" aria-expanded=\"false\">"+buttonText+"</button>"
				
		if (hashy.get("c_settled").equalsIgnoreCase("FULL") || hashy.get("c_agentsharesettled").equalsIgnoreCase("FULL")
				|| Integer.parseInt(hashy.get("cc_branchpmtid")) > 0) {
			actionMenu += "<a href='?myClassBean=com.app.cases.Updatecase&c_id=" + hashy.get("c_id") + "&op=upd' "
					+ "class='dropdown-item'>تعديل <i class='fa fa-pencil' style='font-size: 1.1rem;vertical-align: text-bottom;'></i></a>";
		} else {
			/*
			 * actionMenu += "<a class='dropdown-item' onclick=\"link=false; " +
			 * " var rs =doDeleteSmarty(this,'هل تريد حذف هذه الشحنه ؟' ,'c_id','" +
			 * hashy.get("c_id") +
			 * "' , 'com.app.cases.Updatecase' ); return rs;\"  href='#'>" +
			 * "حذف<i class='fa fa-trash' style='font-size: 1.1rem;vertical-align: text-bottom;'></i></a>"
			 * ; actionMenu += "<a href='?myClassBean=com.app.cases.Updatecase&c_id=" +
			 * hashy.get("c_id") + "&op=upd' " +
			 * "class='dropdown-item'>تعديل <i class='fa fa-pencil' style='font-size: 1.1rem;vertical-align: text-bottom;'></i></a>"
			 * ;
			 */
		}
		actionMenu +=audit+"</div>";
		
		
		String html = "<td><div class='btn-group mr-1 mb-1'>"+buttons+actionMenu;
		
		html+= "</div></td>";
		return html;
	}

}
