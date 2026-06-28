package com.app.cases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import com.app.util.Utilities;

import smarty.core.CoreMgr;
import smarty.db.mysql;

public class ViewOnlyAllCases extends CoreMgr{
	protected String QueueDataSpanClass = "QueueDataSpanClass";
	MasterCaseInformation caseMaster = new MasterCaseInformation();
	private int currentBranch_G = 0;
	private int userId_G = 0;
	boolean allowForceDlv = false;
	/**
	 * 
	 */
	public ViewOnlyAllCases (){
//(c_belongtostore='{userstorecode}' or '{superRank}'='Y')
		MainSql = "select mcust_name, q_rmk, c_parentid, c_paid_delivery_cost_in_advance, c_rtnreason, q_postponedoption, '' as del_edit, c_receiptfromsystem,"
				+ " ifnull(q_previous_action_taken_by,0)as q_previous_action_taken_by , c_createdby, q_stage, q_step, stp_name, "
				+ " date(c_createddt) as c_createddt , c_id , c_custid     , c_custhp		 , "
				  + " c_rcv_name 	 , c_rcv_hp1      , c_rcv_state		 , "
				  + " c_rural        , concat (st_name_ar, '-' ,ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk, c_rmk     		 , "
				  + " c_qty		     , c_receiptamt, c_receiptamt_usd  , c_shipment_cost   ,c_assignedagent , "
				  + " c_fragile	   , c_mastercustid, c_cust_rtnid, c_pickupagent_rtnid, c_pmtid, c_pickupagentpmtid, "
				  + " c_branchcode	 		 , c_custreceiptnoori, c_specialcase,q_branch, branch_name, c_allowrtnagent,"
				  + "  '' as others  , '' as custname    , "
				  + " '' as custprimaryphone, c_settled , c_rcv_district, stp_color, c_allowrtncustomer, rtn_desc "
				  + " from p_cases USE INDEX (idx_q_step_cases, idx_rcv_state_cases, idx_custreceiptnoori_cases) "
				  + " join kbstep on stp_code= q_step "
				  + " left join kb_mastercustomer on (c_mastercustid = mcust_id) "
				  + " left join kbbranches on q_branch = branch_id "
				  + " left join p_caseschain on cc_caseid = c_id  "
				  + " left join kbrtn_reasons on (rtn_code = c_rtnreason)"
				  + " left join kbstate on st_code = c_rcv_state and st_branch = c_branchcode "
				  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
				  + " where 1=0 ";
		
		mainTable = "p_cases";
		keyCol = "c_id";
		//orderByCols = "c_rcv_city, c_id desc";
		canFilter = true;
		//canEdit   = true;
		//canDelete = true;		
		canExport = true;
		pdfExport = true;
		userDefinedCaption = "عرض كل الشحنات";

		userDefinedExportCols.add("cust_name");
		userDefinedExportCols.add("c_createddt");
		userDefinedExportCols.add("c_custreceiptnoori");
		userDefinedExportCols.add("c_receiptamt");
		userDefinedExportCols.add("c_rcv_state");
		userDefinedExportCols.add("c_rcv_addr_rmk");
		userDefinedExportCols.add("c_rcv_hp1");
		userDefinedExportCols.add("c_rmk");
		
		
		userDefinedArabicCols.add("cust_name");
		userDefinedArabicCols.add("c_createddt");
		userDefinedArabicCols.add("c_custreceiptnoori");
		userDefinedArabicCols.add("c_rcv_state");
		userDefinedArabicCols.add("c_rcv_addr_rmk");
		userDefinedArabicCols.add("c_rcv_hp1");
		userDefinedArabicCols.add("c_assignedagent");
		userDefinedArabicCols.add("c_rmk");
		userDefinedExportLandScape = true;
		//userDefineda
				
		userDefinedGridCols.add("c_custreceiptnoori");
		
		userDefinedGridCols.add("stp_name");
		userDefinedGridCols.add("mcust_name");
		userDefinedGridCols.add("cust_name");
		//userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_branchcode");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		//userDefinedGridCols.add("stp_name");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("q_previous_action_taken_by");
		userDefinedGridCols.add("c_rcv_hp1");
		
		
		userDefinedGridCols.add("c_rural");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("c_receiptfromsystem");
		
		userDefinedGridCols.add("c_createdby");
		userDefinedGridCols.add("c_specialcase");
		//userDefinedGridCols.add("del_edit");
		
		userDefinedColLabel.put("custname", "إسم العميل");
		userDefinedColLabel.put("c_specialcase", "شحنه خاصه؟");
		
		userDefinedColLabel.put("mcust_name", "العميل");
		userDefinedColLabel.put("stp_name", "المرحله");
		userDefinedColLabel.put("c_custid", "المتجر");
		userDefinedColLabel.put("cust_name", "المتجر");
		userDefinedColLabel.put("c_rcv_name","المستلم");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف المستلم");
		userDefinedColLabel.put("c_rcv_state", "المحافظه");
		userDefinedColLabel.put("c_rcv_addr_rmk", "تفاصيل العنوان");
		userDefinedColLabel.put("c_rural", "أطراف");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("c_qty", "عدد");
		userDefinedColLabel.put("c_assignedagent","مندوب التوصيل ");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل د.ع");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
		userDefinedColLabel.put("c_shipment_cost","مبلغ الشحن");
		userDefinedColLabel.put("c_sendmoney","مبلغ مرسل");
		userDefinedColLabel.put("c_fragile","قابل للكسر");
		userDefinedColLabel.put("c_bringitemsback","جلب  بضاعه");
		userDefinedColLabel.put("c_branchcode","خلق في فرع");
		userDefinedColLabel.put("c_custreceiptnoori","رقم الوصل");
		userDefinedColLabel.put("c_id","رقم الشحنه");
		userDefinedColLabel.put("q_step","المرحله");
		userDefinedColLabel.put("c_createdby","المدخل");
		userDefinedColLabel.put("c_settled","تم التحاسب ؟");
		userDefinedColLabel.put("c_rcv_district", "المنطقة");
		userDefinedColLabel.put("c_agentshare", "مبلغ الشحن للمندوب");
		userDefinedColLabel.put("c_mastercustid", "العميل");
		//userDefinedColLabel.put("del_edit"," ");
		userDefinedColLabel.put("q_previous_action_taken_by", "اخر من حدث الحاله");
		userDefinedColLabel.put("c_receiptfromsystem", "متولد من النظام؟");
		userDefinedColLabel.put("c_receiptfromsystem","مولد من النظام");
		userDefinedColLabel.put("c_pickupagent","مندوب إستلام");
		
		
		userDefinedFilterCols.add("c_custid");
		userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers where cust_branch={userstorecode} order by cust_name asc");
		userDefinedLookups.put("c_createdby", "select us_id , us_name from kbusers");
		userDefinedLookups.put("c_rcv_district", "select cdi_id, cdi_name from kbcity_district");
		userDefinedLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userDefinedLookups.put("c_branchcode", "select branch_id, branch_name from kbbranches");
		//userModifyTD.put("del_edit", "showDel_Edit({c_id},{c_settled})");
		
		userDefinedFilterCols.add("c_rcv_hp1");
		userDefinedFilterCols.add("c_id");
		
		userDefinedColLabel.put("fromdt","بتاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		
		userDefinedFilterCols.add("c_rcv_state");
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("q_step");
		userDefinedFilterCols.add("c_settled");
		userDefinedFilterCols.add("c_receiptamt");
		userDefinedFilterCols.add("c_assignedagent");
		userDefinedFilterCols.add("fromdt");
		userDefinedFilterCols.add("todate");
		userDefinedFilterCols.add("c_pickupagent");
		
		userDefinedFilterCols.add("c_rtnreason");
		
		userDefinedFilterCols.add("q_postponedoption");
		userDefinedFilterColsHtmlType.put("fromdt", "DATE");
		userDefinedFilterColsHtmlType.put("todate", "DATE");
		
		userDefinedEditColsHtmlType.put("c_createddt", "DATE");
		userDefinedNewColsHtmlType.put("c_createddt", "TIMESTAMP");
		userDefinedColLabel.put("c_createddt","تاريخ الشحنه");

		userDefinedColLabel.put("c_rtnreason","سبب الراجع");
		userDefinedColLabel.put("q_postponedoption","سبب التأجيل");
		userDefinedReadOnlyEditCols.add("userDefinedEditCols");
		//userDefinedEditMockUpCols.put("custprimaryphone", "(select cm_cust_hp from p_casesmaster where cm_id = c_cmid)");
		//userDefinedLookups.put("c_branchcode", "select store_code , store_name from kbstores where store_deleted='N'");
		userDefinedLookups.put("c_rtnreason", "select rtn_code, rtn_desc from kbrtn_reasons");
		userDefinedLookups.put("q_postponedoption", "select post_code, post_desc from kbpostponedoptions");
		userDefinedLookups.put("custname", "select cust_id , cust_name from kbcustomers order by cust_name ASC");
		userDefinedLookups.put("custprimaryphone", "!select cust_phone1 as ph, cust_phone1 from kbcustomers where cust_id = '{custname}'");
		userDefinedLookups.put("c_rcv_state", "select st_code , st_name_ar from kbstate  order by st_order");
		//userDefinedEditLookups.put("c_rcv_city", "select ct_code , ct_name_ar from kbcity where ct_active='Y' order by ct_order");
		userDefinedLookups.put("c_fragile", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_rural", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_settled", "select kbcode , kbdesc from kbgeneral where kbcat1='SETTLED'");
		//userDefinedLookups.put("c_bringitemsback", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("q_step", "select stp_code , stp_name from kbstep");
		userDefinedLookups.put("c_assignedagent", "select us_id , us_name from kbusers  where us_rank = 'DLVAGENT'  ");
		userDefinedLookups.put("c_pickupagent", "select us_id , us_name from kbusers  where us_rank = 'PICKUPAGENT'  ");
		userDefinedLookups.put("c_createdby", "select us_id , us_name from kbusers");
		userDefinedLookups.put("q_previous_action_taken_by", "select us_id , us_name from kbusers");
		userDefinedLookups.put("c_mastercustid", "select mcust_id , mcust_name from kb_mastercustomer");
		
		userDefinedEditColsHtmlType.put("custname" , "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_pickupagent" , "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_custid", "DROPLISTBIGDATA");
		userDefinedFilterColsHtmlType.put("c_mastercustid", "DROPLISTBIGDATA");
		userDefinedFilterColsHtmlType.put("c_assignedagent" , "DROPLIST");
		userDefinedFilterColsHtmlType.put("q_step" , "MULTILIST");

		userDefinedEditColsHtmlType.put("custprimaryphone" , "TEXT");
		userDefinedEditColsHtmlType.put("c_fragile" , "RADIO");
		userDefinedEditColsHtmlType.put("c_bringitemsback" , "RADIO");
		userDefinedEditColsHtmlType.put("c_rural" , "DROPLIST");
		
		userDefinedEditColsHtmlType.put("c_qty" , "NUMBER");
		userDefinedEditColsHtmlType.put("c_rmk" , "TEXTAREA");
		userDefinedEditColsHtmlType.put("c_specialcase" , "TEXT");
		userDefinedEditColsHtmlType.put("c_rcv_addr_rmk" , "TEXTAREA");
		
		userDefinedFilterColsUsingIn.add("q_step");
		userDefinedReadOnlyEditCols.add("c_receiptfromsystem");
		//userDefinedReadOnlyEditCols.add("custname");
		//userDefinedReadOnlyEditCols.add("c_branchcode");

		userDefinedColsMustFill.add("custname");
		userDefinedColsMustFill.add("c_rcv_hp1");
		//userDefinedColsMustFill.add("c_rcv_name");
		userDefinedColsMustFill.add("c_qty");
		userDefinedColsMustFill.add("c_receiptamt");
		userDefinedColsMustFill.add("c_custreceiptnoori");
		userDefinedColsMustFill.add("c_shipment_cost");
		userDefinedColsMustFill.add("c_agentshare");
		userDefinedColsMustFill.add("c_rcv_state");
		
		UserDefinedPageRows = 50;
		userModifyTD.put("stp_name", "modifyStepName({c_parentid},{cc_branchpmtid},{c_agentsharesettled},{c_allowrtnagent},{c_allowrtncustomer},{rtn_desc},"
				+ "{c_custreceiptnoori},{c_id},{stp_name},{c_settled},{q_stage},{q_step},{stp_color},{branch_name},{c_cust_rtnid},{c_pickupagent_rtnid},{c_pmtid},"
				+ "{c_pickupagentpmtid},{inbranch},{q_postopnedto},{post_desc},{q_previous_action_taken_by},"
				+ " {q_rmk}, {q_branch} )");
		//userModifyTD.put("stp_name", "modifyStepName({stp_name},{c_settled},{q_stage},{q_step})");
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		//userDefinedEditMockUpCols.put("custname", "(select cm_custid from p_casesmaster where cm_id = c_cmid)");
		//userDefinedFilterColsHtmlType.put("c_custid", "DROPLIST");
		userDefinedEditColsHtmlType.put("c_custid", "DROPLIST");
		
		userDefinedUseDataTables = true;
		userModifyTD.put("c_rcv_hp1", "linkToWhatsApp({c_rcv_hp1})");
	}//end of no-arg constructor Updatecase
	
	public String linkToWhatsApp(HashMap<String,String> hashy) {
		//return "<td><a href='https://api.whatsapp.com/send?phone=00964"+hashy.get("c_rcv_hp1").replaceFirst("0", "")+"'>"+hashy.get("c_rcv_hp1")+"</a></td>";
		//return "<td><a href=\"https://api.whatsapp.com/send?phone=00964"+hashy.get("c_rcv_hp1").replaceFirst("0", "")+"\">"+hashy.get("c_rcv_hp1")+"</a></td>";
		return "<td><a target=\"_blank\"  href=\"https://wa.me/964"+hashy.get("c_rcv_hp1").replaceFirst("0", "")+"\">"+hashy.get("c_rcv_hp1")+"</a></td>";
	}
	@Override
	public void initialize(HashMap smartyStateMap){
		currentBranch_G = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		userId_G = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		if (getDisplayMode().equalsIgnoreCase("EDITSINGLE")) {
			//System.out.println("heelllooo");
			Connection conn2 = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				String caseid = httpSRequest.getParameter(keyCol);
				conn2 = mysql.getConn();
				pst = conn2.prepareStatement("select c_specialcase from p_cases where c_id = ?");
				pst.setString(1, caseid);
				rs = pst.executeQuery();
				if (rs.next()) {
					if (rs.getString("c_specialcase").equalsIgnoreCase("Y")) {
						userDefinedReadOnlyEditCols.remove("c_shipment_cost");
					}else {
						userDefinedEditCols.remove("c_agentshare");
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
				try {conn2.close();} catch (Exception e) {}
			}
		}
		super.initialize(smartyStateMap);
		if (search_paramval!=null && !search_paramval.isEmpty()) {
			MainSql = "select distinct '' as del_edit , mcust_name, q_rmk, c_parentid, c_paid_delivery_cost_in_advance, c_receiptfromsystem , c_createdby, q_stage, q_step, stp_name, "
					+ " date(c_createddt) as c_createddt , c_id , cust_name     , c_custhp ,	ifnull(q_previous_action_taken_by,0)as q_previous_action_taken_by 	 , "
					  + " c_rcv_name 	 , c_rcv_hp1      , c_rcv_state		 , "
					  + " c_rural        , concat (st_name_ar, '-' ,ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk, c_rmk     		 , "
					  + " c_qty		     , c_receiptamt, c_receiptamt_usd , c_shipment_cost   ,c_assignedagent , c_allowrtnagent, "
					  + " c_fragile	   , c_mastercustid,  c_cust_rtnid, c_pickupagent_rtnid, c_pmtid, c_pickupagentpmtid,"
					  + " c_branchcode	 		 , c_custreceiptnoori, c_specialcase,q_branch, branch_name, "
					  + "  '' as others  , '' as custname    , "
					  + " '' as custprimaryphone, c_settled , c_rcv_district, stp_color, c_allowrtncustomer, rtn_desc, c_rtnreason, q_postopnedto, q_postponedoption, post_desc, "
					  + " 0 as cc_branchpmtid, "
					  + " q_branch as inbranch " 
					  + " from p_cases USE INDEX (idx_q_step_cases, idx_rcv_state_cases, idx_custreceiptnoori_cases) "
					  + " join kbstep on stp_code= q_step "
					  + " left join kb_mastercustomer on (c_mastercustid = mcust_id) "
					  + " left join kbpostponedoptions on (q_postponedoption = post_code)"
					  + " left join p_caseschain chainfor_pmt on (chainfor_pmt.cc_caseid = c_id   and cc_tobranch="+currentBranch_G+") "
					  + " left join kbbranches on q_branch = branch_id "
					  + " left join kbrtn_reasons on (rtn_code = c_rtnreason)"
					  + " left join kbstate on st_code = c_rcv_state and st_branch = c_branchcode "
					  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					  + " join  kbcustomers on cust_id = c_custid "
					  + "  where (cc_tobranch="+currentBranch_G+"  or c_branchcode="+currentBranch_G+")  ";
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
		try {
			 allowForceDlv = 
					 Utilities.checkPermissionOfSpecialOperation(conn, "FORCE_DLV", lu, 
							 "ViewOnlyAllCases");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

		int previous_action_taken_by=Integer.parseInt(hashy.get("q_previous_action_taken_by"));
		String forceOrRestoreDlvBtn = "";
		String forceOrRestoreDlvReason="";
		if(hashy.get("q_stage").equalsIgnoreCase("DLV") ) {
			if (allowForceDlv
					&& hashy.get("q_step").equalsIgnoreCase("FORCE_DLV")
					&& previous_action_taken_by==userId_G ) {
				forceOrRestoreDlvBtn ="<button type=\"button\" onclick='restoreFromforceDlv("+hashy.get("c_id")+")' "
						+ " class=\"btn btn-info radius-30 btn-sm\" style='margin-top:5px;'>تراجع عن الواصل أجباري</button>"; 
				forceOrRestoreDlvReason="<hr/>سبب الواصل أجباري: "+hashy.get("q_rmk");					
			}
		}else {
			if (allowForceDlv) {
				forceOrRestoreDlvBtn ="<button type=\"button\" onclick='forceDlv("+hashy.get("c_id")+")' "
						+ " class=\"btn btn-warning radius-30 btn-sm\" style='margin-top:5px;'>واصل أجباري</button>";
			}
		}
		
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
			if (!hashy.get("c_parentid").equalsIgnoreCase("0")) {
				buttonText += "<br/><span style='font-size: 13px;background-color: #1ea27d;'>"+ "الجزء الراجع من الواصل الجزئي" +"</span>";
			}
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
		buttonText +=forceOrRestoreDlvReason;
		String buttons =""
			+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";font-size: 11.5px;' class='btn  btn-sm  waves-effect waves-light text-white'>"+buttonText+"</button>"
			+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";font-size: 11.5px;' class='btn btn-sm  dropdown-toggle waves-effect waves-light text-white' data-toggle='dropdown'"
			+ " aria-haspopup='true' aria-expanded='true'><span class='sr-only'>Toggle Dropdown</span></button>";
          
		
		String audit = "<a href='displaySingleCaseInfo?auditcaseid="+hashy.get("c_id")+"' "
				+ "class='dropdown-item' >تتبع <i class='fa fa-eye' style='font-size: 1.1rem;vertical-align: text-bottom;'/></a>";
		String actionMenu ="<div class='dropdown-menu '>";
				//+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";' class='btn btn-light dropdown-toggle btn-sm' type=\"button\" data-bs-toggle=\"dropdown\" aria-expanded=\"false\">"+buttonText+"</button>"
				
		
		actionMenu +=audit+"</div>";
		
		
		String html = "<td><div class='btn-group mr-1 mb-1'>"+buttons+actionMenu;
		
		
		
		html+= "</div>"+forceOrRestoreDlvBtn;
		html+= "</td>";
		
		return html;
	}

}//end of class Updatecase
