package com.app.cust.cases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import com.app.cases.MasterCaseInformation;
import smarty.core.CoreMgr;
import smarty.db.mysql;


public class CustomerViewAllCases extends CoreMgr{
	protected String QueueDataSpanClass = "QueueDataSpanClass";
	MasterCaseInformation caseMaster = new MasterCaseInformation();

	/**
	 * 
	 */
	public CustomerViewAllCases (){
//(c_belongtostore='{userstorecode}' or '{superRank}'='Y')
		MainSql = "select  '' as del_edit, c_receiptfromsystem, q_previous_action_taken_by , c_createdby, q_stage, q_step, stp_name, "
				+ " date(c_createddt) as c_createddt , c_id , c_custid     , c_custhp		 , '' as postponed,  "
				  + " c_rcv_name 	 , c_rcv_hp1      , c_rcv_state		 , "
				  + " c_rural        , concat (ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk, c_rmk , c_productinfo, "
				  + " c_qty		     , c_receiptamt  , c_shipment_cost   ,c_assignedagent ,  c_agentsharesettled,"
				  + " c_fragile	   , c_cust_rtnid, c_pickupagent_rtnid,  c_pmtid, c_pickupagentpmtid,"
				  + " c_branchcode	 		 , c_custreceiptnoori, c_specialcase, branch_name, c_allowrtnagent, "
				  + "  '' as others  , '' as custname    , ifnull(chainfor_pmt.cc_branchpmtid,0) as cc_branchpmtid, c_mastercustid,  "
				  + " '' as custprimaryphone, c_settled , c_rcv_district, stp_color, c_allowrtncustomer, rtn_desc,"
				  + " (case "
				  + "	when (q_step='PART_SUCC' and chain.cc_qstatus_tobranch='ACTV') then chain.cc_tobranch "
				  + "	when (q_step='PART_SUCC' and chain.cc_qstatus_frombranch='ACTV') then chain.cc_frombranch "
				  + "	else c_branchcode end) as inbranch, c_rtnreason, "
				  + " DATE_FORMAT(q_postopnedto,'%Y-%m-%d %H:%i') as postopneddt,"
				  + " DATE_FORMAT(q_postopnedto,'%H:%i') as postopnedtime,"
				  + " q_postponedoption, post_desc " 
				  + " from p_cases "
				  + " join kbstep on stp_code= q_step "
				  + " left join kbpostponedoptions on (q_postponedoption = post_code)"
				  + " left join kbbranches on q_branch = branch_id "
				  + " left join kbrtn_reasons on (rtn_code = c_rtnreason)"
				  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
				  + " left join p_caseschain chain on(chain.cc_caseid = c_id and (chain.cc_qstatus_tobranch='ACTV' or chain.cc_qstatus_frombranch='ACTV') and q_step='PART_SUCC') "
				  + " left join p_caseschain chainfor_pmt on (chainfor_pmt.cc_caseid = c_id and chainfor_pmt.cc_frombranch={userstorecode} )"
				  + " where c_custid in ({shopsCommaSeperated})";
		
		mainTable = "p_cases";
		keyCol = "c_id";
		//orderByCols = "c_rcv_city, c_id desc";
		canFilter = true;
		//canEdit   = true;
		//canDelete = true;		
		canExport = true;
		pdfExport = true;
		userDefinedCaption = "عرض كل الشحنات";
		userDefinedEditCaption = "تعديل بيانات شحنه";
		
		userDefinedExportCols.add("c_custid");
		userDefinedExportCols.add("c_createddt");
		userDefinedExportCols.add("c_custreceiptnoori");
		userDefinedExportCols.add("c_rcv_state");
		userDefinedExportCols.add("c_rcv_addr_rmk");
		userDefinedExportCols.add("c_rcv_hp1");
		userDefinedExportCols.add("c_rmk");
		
		
		userDefinedArabicCols.add("c_custid");
		userDefinedArabicCols.add("c_createddt");
		userDefinedArabicCols.add("c_custreceiptnoori");
		userDefinedArabicCols.add("c_rcv_state");
		userDefinedArabicCols.add("c_rcv_addr_rmk");
		userDefinedArabicCols.add("c_rcv_hp1");
		userDefinedArabicCols.add("c_assignedagent");
		userDefinedArabicCols.add("c_rmk");
		userDefinedExportLandScape = true;
		//userDefineda
		
		userDefinedEditFormColNo = 3;
		userDefinedEditCols.add("c_custid");
		userDefinedEditCols.add("c_specialcase");
		userDefinedEditCols.add("c_rcv_name");
		userDefinedEditCols.add("c_rcv_hp1");
		userDefinedEditCols.add("c_rcv_state");
		userDefinedEditCols.add("c_rcv_district");
		//userDefinedEditCols.add("c_rural");
		userDefinedEditCols.add("c_rcv_addr_rmk");
		userDefinedEditCols.add("c_shipment_cost");
		userDefinedEditCols.add("c_rmk");
		userDefinedEditCols.add("c_qty");
		userDefinedEditCols.add("c_receiptamt");
		userDefinedEditCols.add("c_custreceiptnoori");
		
		
		
		//userDefinedReadOnlyEditCols.add("c_specialcase");
		//userDefinedEditCols.add("c_createddt");
		
		
		userDefinedGridCols.add("c_custreceiptnoori");
		//userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("stp_name");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("c_createddt");
		//userDefinedGridCols.add("q_previous_action_taken_by");
		userDefinedGridCols.add("c_rural");
		userDefinedGridCols.add("c_rmk");
		//userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_shipment_cost");
		//userDefinedGridCols.add("c_createdby");
		//userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_specialcase");
		//userDefinedGridCols.add("c_allowrtncustomer");
		
		userDefinedColLabel.put("custname", "إسم العميل");
		userDefinedColLabel.put("c_specialcase", "شحنه خاصه؟");
		userDefinedColLabel.put("c_allowrtncustomer", "راجع");
		userDefinedColLabel.put("stp_name", "المرحله");
		userDefinedColLabel.put("c_custid", "متجر");
		userDefinedColLabel.put("c_rcv_name","المستلم");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف المستلم");
		userDefinedColLabel.put("c_rcv_state", "المحافظه");
		userDefinedColLabel.put("c_rcv_addr_rmk", "تفاصيل العنوان");
		userDefinedColLabel.put("c_rural", "أطراف");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("c_qty", "عدد");
		userDefinedColLabel.put("c_assignedagent","مندوب التوصيل ");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("c_shipment_cost","مبلغ الشحن");
		userDefinedColLabel.put("c_sendmoney","مبلغ مرسل");
		userDefinedColLabel.put("c_fragile","قابل للكسر");
		userDefinedColLabel.put("c_bringitemsback","جلب  بضاعه");
		userDefinedColLabel.put("c_branchcode","مخزن");
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
		userDefinedColLabel.put("c_rtnreason","سبب الراجع");
		userDefinedColLabel.put("q_postponedoption","سبب التأجيل");
		userDefinedColLabel.put("postopneddt","تاريخ التأجيل");
		
		userModifyTD.put("del_edit", "showDel_Edit({c_id},{c_settled})");
		
		userDefinedFilterCols.add("c_custid");
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("c_rcv_hp1");
		userDefinedFilterCols.add("c_rcv_state");
		userDefinedFilterCols.add("c_receiptamt");
		//userDefinedFilterCols.add("c_assignedagent");
		userDefinedFilterCols.add("fromdt");
		userDefinedFilterCols.add("todate");
		userDefinedFilterCols.add("c_allowrtncustomer");
		userDefinedFilterCols.add("c_rtnreason");
		
		userDefinedFilterColsHtmlType.put("fromdt", "DATE");
		userDefinedFilterColsHtmlType.put("todate", "DATE");
		
		userDefinedEditColsHtmlType.put("c_createddt", "DATE");
		userDefinedNewColsHtmlType.put("c_createddt", "TIMESTAMP");
		userDefinedColLabel.put("c_createddt","تاريخ الشحنه");
		userDefinedColLabel.put("postponed","مؤجل");

		
		userDefinedReadOnlyEditCols.add("userDefinedEditCols");
		//userDefinedEditMockUpCols.put("custprimaryphone", "(select cm_cust_hp from p_casesmaster where cm_id = c_cmid)");
		//userDefinedLookups.put("c_branchcode", "select store_code , store_name from kbstores where store_deleted='N'");
		userDefinedLookups.put("custname", "select cust_id , cust_name from kbcustomers order by cust_name ASC");
		userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers order by cust_name ASC");
		userDefinedLookups.put("custprimaryphone", "!select cust_phone1 as ph, cust_phone1 from kbcustomers where cust_id = '{custname}'");
		userDefinedLookups.put("c_rcv_state", "select st_code , st_name_ar from kbstate  order by st_order");
		//userDefinedEditLookups.put("c_rcv_city", "select ct_code , ct_name_ar from kbcity where ct_active='Y' order by ct_order");
		userDefinedLookups.put("c_fragile", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_rural", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_settled", "select kbcode , kbdesc from kbgeneral where kbcat1='SETTLED'");
		//userDefinedLookups.put("c_bringitemsback", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedEditLookups.put("c_rcv_district", "!select cdi_id, cdi_name from kbcity_district where cdi_stcode = '{c_rcv_state}' ");
		userDefinedLookups.put("q_step", "select stp_code , stp_name from kbstep");
		userDefinedLookups.put("c_assignedagent", "select us_id , us_name from kbusers  where us_rank = 'DLVAGENT'  ");
		userDefinedLookups.put("c_pickupagent", "select us_id , us_name from kbusers  where us_rank = 'PICKUPAGENT'  ");
		userDefinedLookups.put("c_rcv_district", "select cdi_id, cdi_name from kbcity_district");
		userDefinedLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userDefinedEditLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userDefinedLookups.put("c_allowrtncustomer", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userDefinedLookups.put("c_rtnreason", "select rtn_code, rtn_desc from kbrtn_reasons");
		userDefinedLookups.put("q_postponedoption", "select post_code, post_desc from kbpostponedoptions");
		userDefinedLookups.put("postponed", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		
		userDefinedEditColsHtmlType.put("custname" , "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_allowrtncustomer" , "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_rtnreason" , "DROPLIST");
		userDefinedFilterColsHtmlType.put("postponed" , "DROPLIST");
		userDefinedFilterColsHtmlType.put("q_postponedoption" , "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_pickupagent" , "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_custid", "DROPLISTBIGDATA");
		userDefinedFilterColsHtmlType.put("c_assignedagent" , "DROPLIST");
		userDefinedEditColsHtmlType.put("custprimaryphone" , "TEXT");
		userDefinedEditColsHtmlType.put("c_fragile" , "RADIO");
		userDefinedEditColsHtmlType.put("c_bringitemsback" , "RADIO");
		userDefinedEditColsHtmlType.put("c_rural" , "DROPLIST");
		userDefinedEditColsHtmlType.put("c_qty" , "NUMBER");
		userDefinedEditColsHtmlType.put("c_rmk" , "TEXTAREA");
		userDefinedEditColsHtmlType.put("c_specialcase" , "DROPLIST");
		userDefinedEditColsHtmlType.put("c_rcv_district" , "DROPLIST");
		userDefinedEditColsHtmlType.put("c_rcv_addr_rmk" , "TEXTAREA");
		

		userDefinedReadOnlyEditCols.add("c_receiptfromsystem");
		//userDefinedReadOnlyEditCols.add("custname");
		//userDefinedReadOnlyEditCols.add("c_branchcode");

		userDefinedColsMustFill.add("c_custid");
		userDefinedColsMustFill.add("c_rcv_hp1");
		//userDefinedColsMustFill.add("c_rcv_name");
		userDefinedColsMustFill.add("c_qty");
		userDefinedColsMustFill.add("c_receiptamt");
		userDefinedColsMustFill.add("c_custreceiptnoori");
		userDefinedColsMustFill.add("c_shipment_cost");
		userDefinedColsMustFill.add("c_agentshare");
		userDefinedColsMustFill.add("c_rcv_state");
		userDefinedColsMustFill.add("c_rcv_district");
		userDefinedColsMustFill.add("c_specialcase");
		
		UserDefinedPageRows = 50;
		userModifyTD.put("stp_name", "modifyStepName({cc_branchpmtid},{c_agentsharesettled},{c_allowrtnagent},{c_allowrtncustomer},{rtn_desc},"
				+ "{c_custreceiptnoori},{c_id},{stp_name},{c_settled},{q_stage},{q_step},{stp_color},{branch_name},{c_cust_rtnid},{c_pickupagent_rtnid},{c_pmtid},"
				+ "{c_pickupagentpmtid},{inbranch},{postopneddt},{post_desc},{postopnedtime})");
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		//userDefinedEditMockUpCols.put("custname", "(select cm_custid from p_casesmaster where cm_id = c_cmid)");
		//userDefinedFilterColsHtmlType.put("c_custid", "DROPLIST");
		userDefinedEditColsHtmlType.put("c_custid", "DROPLIST");
		userDefinedUseDataTables = true;
	}//end of no-arg constructor Updatecase
	
	
	@Override
	public void initialize(HashMap smartyStateMap){
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		String shopsCommaSeperated = replaceVarsinString("{shopsCommaSeperated}",arrayGlobals).trim();
		userDefinedFilterLookups.put("c_custid", "select cust_id , cust_name from kbcustomers where cust_id in ("+shopsCommaSeperated+") order by cust_name ASC");
		
		super.initialize(smartyStateMap);
		
		if (search_paramval!=null && !search_paramval.isEmpty()) {
			MainSql = "select  '' as del_edit, c_receiptfromsystem, q_previous_action_taken_by , c_createdby, q_stage, q_step, stp_name, "
					+ " date(c_createddt) as c_createddt , c_id , c_custid     , c_custhp		 , '' as postponed,  "
					  + " c_rcv_name 	 , c_rcv_hp1      , c_rcv_state		 , "
					  + " c_rural        , concat (ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk, c_rmk , c_productinfo, "
					  + " c_qty		     , c_receiptamt  , c_shipment_cost   ,c_assignedagent ,  c_agentsharesettled,"
					  + " c_fragile	   , c_cust_rtnid, c_pickupagent_rtnid,  c_pmtid, c_pickupagentpmtid,"
					  + " c_branchcode	 		 , c_custreceiptnoori, c_specialcase, branch_name, c_allowrtnagent, "
					  + "  '' as others  , '' as custname    , ifnull(chainfor_pmt.cc_branchpmtid,0) as cc_branchpmtid, c_mastercustid,  "
					  + " '' as custprimaryphone, c_settled , c_rcv_district, stp_color, c_allowrtncustomer, rtn_desc,"
					  + " (case "
					  + "	when (q_step='PART_SUCC' and chain.cc_qstatus_tobranch='ACTV') then chain.cc_tobranch "
					  + "	when (q_step='PART_SUCC' and chain.cc_qstatus_frombranch='ACTV') then chain.cc_frombranch "
					  + "	else c_branchcode end) as inbranch, c_rtnreason, q_postponedoption, post_desc, "
					  + " DATE_FORMAT(q_postopnedto,'%Y-%m-%d') as postopneddt,"
					  + " DATE_FORMAT(q_postopnedto,'%H:%i') as postopnedtime"
					  + " from p_cases "
					  + " join kbstep on stp_code= q_step "
					  + " left join kbpostponedoptions on (q_postponedoption = post_code)"
					  + " left join kbbranches on q_branch = branch_id "
					  + " left join kbrtn_reasons on (rtn_code = c_rtnreason)"
					  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					  + " left join p_caseschain chain on(chain.cc_caseid = c_id and (chain.cc_qstatus_tobranch='ACTV' or chain.cc_qstatus_frombranch='ACTV') and q_step='PART_SUCC') "
					  + " left join p_caseschain chainfor_pmt on (chainfor_pmt.cc_caseid = c_id and chainfor_pmt.cc_frombranch="+currentBranch+" )"
					  + " where c_custid in ("+shopsCommaSeperated+") ";
		}
		
		String fromdt = "ALL";
		String todt = "ALL";
		String postponed = "ALL";
		String rtn = "ALL";
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
					if (parameter.equals("postponed")) {
						postponed =  value;
					} 
					if (parameter.equals("c_allowrtncustomer")) {
						rtn =  value;
					} 
				}
			}
		}
	
		if(!postponed.equalsIgnoreCase("ALL")) {
			if(postponed.contentEquals("Y")) {
				MainSql +=" and q_step = 'POSTPONED'";
				//userDefinedGridCols.add("q_postopnedto");
			}else if(postponed.contentEquals("N"))
				MainSql +=" and q_step != 'POSTPONED'";
		}
		if(!rtn.equalsIgnoreCase("ALL")) {
			if(rtn.contentEquals("Y"))
				MainSql +=" and q_stage = 'CNCL'";
			else if(rtn.contentEquals("N"))
				MainSql +=" and q_stage != 'CNCL'";
		}
		if (foundSearch) {
			if (todt.equalsIgnoreCase("ALL") && !fromdt.equalsIgnoreCase("ALL")) {
				todt = fromdt;
			}
			if (!fromdt.equalsIgnoreCase("ALL")) {
				MainSql +=" and  (date(c_createddt)>='"+fromdt+"') and (date(c_createddt)<='"+todt+"' ) ";
			}
		}
		
		MainSql +=" order by q_enterdate desc";		
	}
	@Override 
	public StringBuilder genListing() {
		//System.out.println("calling gen listing---------------------");
		search_paramval.remove("fromdt");
		search_paramval.remove("todate");
		search_paramval.remove("postponed");
		return super.genListing();
	}
	
	
	
	public String modifyStepName(HashMap<String,String>hashy) {
		//return "<td></td>";
		String buttonText = hashy.get("stp_name")+"<br> في فرع - "+hashy.get("branch_name");
		if(hashy.get("q_stage").equalsIgnoreCase("DLV")) {
				if(Integer.parseInt(hashy.get("c_pmtid"))>0)
					buttonText += "<br> رقم كشف حساب العميل "+hashy.get("c_pmtid");
				else if(Integer.parseInt(hashy.get("c_pickupagentpmtid"))>0)
					buttonText += "<br> رقم كشف حساب مندوب الاستلام "+hashy.get("c_pickupagentpmtid");
			
		}
		if((hashy.get("c_allowrtncustomer").equalsIgnoreCase("Y") || hashy.get("c_allowrtnagent").equalsIgnoreCase("Y"))
				&& (hashy.get("q_step").contains("RTN") || hashy.get("q_step").equalsIgnoreCase("PART_SUCC"))) {
			if(hashy.get("q_step").contains("RTN"))
				buttonText += "<br> "+hashy.get("rtn_desc");
			Connection conn2 = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			if(hashy.get("q_step").equalsIgnoreCase("PART_SUCC") && Integer.parseInt(hashy.get("c_pickupagent_rtnid"))==0 && Integer.parseInt(hashy.get("c_cust_rtnid"))==0 ) {
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
				buttonText += "<br> الجزء الراجع في فرع "+inBranch;
			}
		}
		if(hashy.get("q_stage").equalsIgnoreCase("AGENTOP") && hashy.get("q_step").equalsIgnoreCase("POSTPONED")) {
			buttonText += "<br> تاريخ التأجيل "+hashy.get("postopneddt");
			buttonText += "<br>"+hashy.get("postopnedtime");
			buttonText += "<br>"+hashy.get("post_desc");
		}

		String buttons =""
				+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";font-size: 11.5px;' class='btn  btn-sm  waves-effect waves-light text-white'>"+buttonText+"</button>";
		/*String audit = "<a href='displaySingleCaseInfo?auditcaseid="+hashy.get("c_id")+"' "
				+ "class='dropdown-item' >تتبع <i class='lni lni-eye' style='font-size: 1.1rem;vertical-align: text-bottom;'></i></a>";
		String actionMenu ="<div class='dropdown'>"
				+ "<button type='button' style='background-color:"+hashy.get("stp_color")+"; font-size:10px;' class='btn btn-light dropdown-toggle btn-sm' type=\"button\" data-bs-toggle=\"dropdown\" aria-expanded=\"false\">"+buttonText+"</button>"
				+ " <ul class='dropdown-menu'>";
		if (hashy.get("c_settled").equalsIgnoreCase("FULL"))
			;
		else if(hashy.get("q_step").equalsIgnoreCase("READYTOPRINT") || hashy.get("q_step").equalsIgnoreCase("READYTOPICKUP")){
			actionMenu +="<li><a class='dropdown-item' onclick=\"link=false; "
					+ " var rs =doDeleteSmarty(this,'هل تريد حذف هذه الشحنه ؟' ,'c_id','"+hashy.get("c_id")+"' , 'com.app.cust.cases.CustomerViewAllCases' ); return rs;\"  href='#'>"
							+ "حذف<i class='lni lni-trash' style='font-size: 1.1rem;vertical-align: text-bottom;'></i></a></li>";
			actionMenu +="<li><a href='?myClassBean=com.app.cust.cases.CustomerViewAllCases&c_id="+hashy.get("c_id")+"&op=upd' "
				+ "class='dropdown-item'>تعديل <i class='lni lni-pencil' style='font-size: 1.1rem;vertical-align: text-bottom;'></i></a></li>";
		}
		actionMenu +="<li>"+audit+"</li></ul></div>";*/
		
		
		//String html = "<td width='12%' ><div class='row'>"+actionMenu;
		String html = "<td><div class='btn-group mr-1 mb-1'>"+buttons;
		
		html+= "</div></td>";
		return html;
	}
	
}