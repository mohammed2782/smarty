package com.app.cases;

import javax.servlet.http.HttpServletRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;
import smarty.db.mysql;

import com.app.financials.StandardFinCurrency;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

public class Updatecase extends CoreMgr{
	protected String QueueDataSpanClass = "QueueDataSpanClass";
	MasterCaseInformation caseMaster = new MasterCaseInformation();
	private int m_caseId = 0;
	boolean allowForceDlv = false;
	private int currentBranch_G = 0;
	private int userId_G = 0;
	
	public Updatecase (){
		
		MainSql = "select mcust_name,c_parentid, q_rmk,c_paytodlvcheck, q_branch,  c_paid_delivery_cost_in_advance, c_rtnreason, '' as fromdt, '' as todate , '' as del_edit, c_receiptfromsystem, "
		+ "ifnull(q_previous_action_taken_by,0) as q_previous_action_taken_by , c_createdby, q_stage, q_step, stp_name, date(c_createddt) as c_createddt , c_id , c_custid     , c_custhp		 , "
		  + " c_rcv_name 	 , c_rcv_hp1      , c_rcv_state		 , "
		  + " c_rural        , concat (ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk, c_rmk , c_productinfo, "
		  + " c_qty		     , c_receiptamt, c_receiptamt_usd, c_shipment_cost   ,c_assignedagent , c_agentsharesettled, "
		  + " c_fragile	   , c_mastercustid, c_cust_rtnid, c_pickupagent_rtnid, c_pmtid, c_pickupagentpmtid,  "
		  + " c_branchcode	 		 , c_custreceiptnoori, c_specialcase, branch_name, c_allowrtnagent, "
		  + "  '' as others  , '' as custname    , ifnull(cc_branchpmtid,0) as cc_branchpmtid, "
		  + " case when q_step='PART_SUCC' and cc_qstatus_tobranch='ACTV' then cc_tobranch  when q_step='PART_SUCC' and cc_qstatus_frombranch='ACTV' then cc_frombranch else {userstorecode} end as inbranch  ," 
		  + " '' as custprimaryphone, c_settled , c_rcv_district, stp_color, c_allowrtncustomer, rtn_desc, q_postopnedto, q_postponedoption, post_desc "
		  + " from p_cases "
		  + " join kbstep on stp_code= q_step "
		  + " left join kbrtn_reasons on (rtn_code = c_rtnreason)"
		  + " left join kbbranches on q_branch = branch_id "
		  + " left join kb_mastercustomer on c_mastercustid = mcust_id "
		  + " left join kbpostponedoptions on (q_postponedoption = post_code)"
		  + " left join p_caseschain on(cc_caseid = c_id and cc_frombranch = {userstorecode}) "
		  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
		  + " where 1=0";
		

		mainTable = "p_cases";
		keyCol = "c_id";
		//orderByCols = "c_rcv_city, c_id desc";
		canFilter = true;
		//canEdit   = true;
		//canDelete = true;		
		canExport = true;
		pdfExport = true;
		userDefinedCaption = "شحناتي";
		userDefinedEditCaption = "تعديل بيانات شحنه";
		
		userDefinedExportCols.add("c_custid");
		userDefinedExportCols.add("c_createddt");
		userDefinedExportCols.add("c_custreceiptnoori");
		userDefinedExportCols.add("c_receiptamt");
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
		
		userDefinedEditCols.add("c_rcv_hp1");
		userDefinedEditCols.add("c_rcv_state");
		userDefinedEditCols.add("c_rcv_district");
		userDefinedEditCols.add("c_rcv_addr_rmk");
		userDefinedEditCols.add("c_shipment_cost");
		userDefinedEditCols.add("c_receiptamt");
		userDefinedEditCols.add("c_receiptamt_usd");
		userDefinedEditCols.add("c_agentshare");
		userDefinedEditCols.add("c_rmk");
		userDefinedEditCols.add("c_custreceiptnoori");
		userDefinedEditCols.add("c_paid_delivery_cost_in_advance");
		
		
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("stp_name");
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("mcust_name");

		userDefinedGridCols.add("c_branchcode");
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("q_previous_action_taken_by");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("c_rural");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("c_createdby");
		userDefinedGridCols.add("c_specialcase");
		//userDefinedGridCols.add("del_edit");
		
		userDefinedColLabel.put("c_mastercustid", "العميل");
		userDefinedColLabel.put("c_specialcase", "شحنه خاصه؟");
		userDefinedColLabel.put("c_productinfo", "تفاصيل البضاعة");
		userDefinedColLabel.put("stp_name", "المرحله");
		userDefinedColLabel.put("c_custid", "المتجر");
		userDefinedColLabel.put("mcust_name", "العميل");
		userDefinedColLabel.put("c_rcv_name","المستلم");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف المستلم");
		userDefinedColLabel.put("c_rcv_hp2", "2هاتف المستلم");
		userDefinedColLabel.put("c_rcv_state", "المحافظه");
		userDefinedColLabel.put("c_rcv_addr_rmk", "تفاصيل العنوان");
		userDefinedColLabel.put("c_rural", "أطراف");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("c_qty", "عدد");
		userDefinedColLabel.put("c_assignedagent","مندوب التوصيل");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل د.ع");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
		userDefinedColLabel.put("c_shipment_cost","مبلغ الشحن");
		userDefinedColLabel.put("c_sendmoney","مبلغ مرسل");
		userDefinedColLabel.put("c_fragile","قابل للكسر");
		userDefinedColLabel.put("c_bringitemsback","جلب  بضاعه");
		userDefinedColLabel.put("c_branchcode","من فرع");
		userDefinedColLabel.put("c_custreceiptnoori","رقم الوصل");
		userDefinedColLabel.put("c_id","كود الشحنة");
		userDefinedColLabel.put("q_step","المرحله");
		userDefinedColLabel.put("c_createdby","المدخل");
		userDefinedColLabel.put("c_settled","تم التحاسب ؟");
		userDefinedColLabel.put("c_rcv_district", "المنطقة");
		userDefinedColLabel.put("c_agentshare", "حصة المندوب");
		userDefinedColLabel.put("del_edit"," ");
		userDefinedColLabel.put("q_previous_action_taken_by", "اخر من حدث الحاله");
		userDefinedColLabel.put("c_receiptfromsystem", "متولد من النظام؟");
		userDefinedColLabel.put("c_receiptfromsystem","مولد من النظام");
		userDefinedColLabel.put("c_pickupagent","مندوب إستلام");
		userDefinedColLabel.put("c_paid_delivery_cost_in_advance","مدفوع أجور التوصيل ؟");
		
		//userDefinedFilterCols.add("c_mastercustid");
		userDefinedFilterCols.add("c_custid");
		userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers where cust_branch = {userstorecode} ");
		userDefinedLookups.put("c_rcv_district", "select cdi_id, cdi_name from kbcity_district");
		userDefinedLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userDefinedEditLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userDefinedEditLookups.put("c_paid_delivery_cost_in_advance", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		
		userDefinedLookups.put("c_createdby", "select us_id , us_name from kbusers");
		userDefinedLookups.put("q_previous_action_taken_by", "select us_id , us_name from kbusers");
		userDefinedLookups.put("c_branchcode", "select branch_id, branch_name from kbbranches");
		//userDefinedEditLookups.put("c_custid", "!select cust_id , cust_name from kbcustomers where cust_mastercustid ={c_mastercustid} order by cust_name asc");
		userDefinedEditLookups.put("c_custid", "select cust_id , cust_name from kbcustomers where cust_branch = {userstorecode} ");
		
		userDefinedEditColsHtmlType.put("c_paid_delivery_cost_in_advance", "DROPLIST");
		userDefinedEditColsHtmlType.put("c_rcv_district", "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_rcv_hp1", "PHONE");
		
		
		userDefinedFilterCols.add("c_rcv_hp1");
		userDefinedFilterCols.add("c_id");
		
		userDefinedColLabel.put("fromdt","بتاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		
		userDefinedFilterCols.add("c_rcv_state");
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("q_step");
		userDefinedFilterCols.add("c_settled");
		userDefinedFilterCols.add("c_receiptamt");
		userDefinedFilterCols.add("c_receiptamt_usd");
		userDefinedFilterCols.add("c_assignedagent");
		userDefinedFilterCols.add("fromdt");
		userDefinedFilterCols.add("todate");
		userDefinedFilterCols.add("q_previous_action_taken_by");
		
		userDefinedFilterCols.add("c_rtnreason");
		
		userDefinedFilterCols.add("q_postponedoption");
		
		userDefinedFilterColsHtmlType.put("fromdt", "DATE");
		userDefinedFilterColsHtmlType.put("todate", "DATE");
		userDefinedFilterColsHtmlType.put("q_previous_action_taken_by", "DROPLIST");
		
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
		userDefinedLookups.put("c_assignedagent", "select us_id , us_name from kbusers  where us_rank = 'DLVAGENT'   ");
		userDefinedLookups.put("c_pickupagent", "select us_id , us_name from kbusers  where us_rank = 'PICKUPAGENT'  ");
		//userDefinedLookups.put("c_mastercustid", "select mcust_id , mcust_name from kb_mastercustomer where mcust_branchcode={userstorecode}");
		userDefinedEditLookups.put("c_rcv_district", "!select cdi_id, cdi_name from kbcity_district where cdi_stcode = '{c_rcv_state}' ");
		userDefinedEditLookups.put("c_assignedagent", "!select us_id , us_name from kbusers  where us_rank = 'DLVAGENT' and us_branchcode = {userstorecode} and "
				+ " us_id in (select distinct agdi_usid from kbagent_district where agdi_districtcode = {c_rcv_district} and us_active='Y')");
		
		userDefinedEditColsHtmlType.put("custname" , "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_pickupagent" , "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_custid", "DROPLISTBIGDATA");
		userDefinedFilterColsHtmlType.put("c_mastercustid", "DROPLISTBIGDATA");
		userDefinedFilterColsHtmlType.put("c_assignedagent" , "DROPLIST");
		userDefinedEditColsHtmlType.put("custprimaryphone" , "TEXT");
		userDefinedEditColsHtmlType.put("c_fragile" , "RADIO");
		userDefinedEditColsHtmlType.put("c_bringitemsback" , "RADIO");
		
		userDefinedEditColsHtmlType.put("c_qty" , "NUMBER");
		userDefinedEditColsHtmlType.put("c_rmk" , "TEXTAREA");
		userDefinedEditColsHtmlType.put("c_specialcase" , "DROPLIST");
		userDefinedEditColsHtmlType.put("c_mastercustid" , "DROPLISTBIGDATA");
		userDefinedEditColsHtmlType.put("c_rcv_addr_rmk" , "TEXTAREA");
		userDefinedEditColsHtmlType.put("c_assignedagent" , "DROPLIST");
		userDefinedEditColsHtmlType.put("c_rcv_district" , "DROPLIST");

		userDefinedReadOnlyEditCols.add("c_receiptfromsystem");
		

		userDefinedColsMustFill.add("custname");
		userDefinedColsMustFill.add("c_rcv_hp1");
		userDefinedColsMustFill.add("c_qty");
		userDefinedColsMustFill.add("c_receiptamt");
		userDefinedColsMustFill.add("c_receiptamt_usd");
		userDefinedColsMustFill.add("c_custreceiptnoori");
		userDefinedColsMustFill.add("c_shipment_cost");
		userDefinedColsMustFill.add("c_agentshare");
		userDefinedColsMustFill.add("c_rcv_state");
		userDefinedColsMustFill.add("c_rcv_district");
		userDefinedColsMustFill.add("c_rural");
		userDefinedColsMustFill.add("c_assignedagent");
		
		UserDefinedPageRows = 100;
		userModifyTD.put("stp_name", "modifyStepName({c_parentid},{c_paytodlvcheck},{cc_branchpmtid},{c_agentsharesettled},{c_allowrtnagent},{c_allowrtncustomer},{rtn_desc},"
				+ "{c_custreceiptnoori},{c_id},{stp_name},{c_settled},{q_stage},{q_step},{stp_color},"
				+ "{branch_name},{c_cust_rtnid},{c_pickupagent_rtnid},{c_pmtid}, {q_previous_action_taken_by},"
				+ "{c_pickupagentpmtid},{inbranch},{q_postopnedto},{post_desc},{c_paid_delivery_cost_in_advance},"
				+ "{q_branch}, {q_rmk})");
		//userModifyTD.put("stp_name", "modifyStepName({stp_name},{c_settled},{q_stage},{q_step})");
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
	
		userDefinedEditColsHtmlType.put("c_custid", "DROPLIST");
		userDefinedUseDataTables = true;
		
		userDefinedTableHeadersClass = "text-white  bg-gradient-x-warning";
	}//end of no-arg constructor Updatecase
	
	@Override
	public void initialize(HashMap smartyStateMap){
		UtilitiesFeqar ut = new UtilitiesFeqar();
		currentBranch_G = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		userId_G = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		if(m_caseId>0) {
			if(replaceVarsinString("{caneditfromstage}",arrayGlobals).trim().equalsIgnoreCase("true"))
				canEdit = true;
			else {
				canEdit = false;
				userDefinedEditCaption = "لايمكن التعديل على الشحنة لانه تم محاسبة احد الاطراف";
			}
			displayMode = "EDITSINGLE";
			MainSql = "select c_parentid,c_paytodlvcheck, q_rmk, q_branch, c_paid_delivery_cost_in_advance,  c_rtnreason, q_postponedoption, "
					+ "'' as del_edit, c_receiptfromsystem, ifnull(q_previous_action_taken_by,0) as q_previous_action_taken_by"
					+ " , c_createdby, q_stage, q_step, stp_name, "
			+ " date(c_createddt) as c_createddt , c_id , c_custid     , c_custhp	, "
			  + " c_rcv_name 	 , c_rcv_hp1      , c_rcv_state		 , "
			  + " c_rural        , concat (ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk, c_rmk , c_productinfo, "
			  + " c_qty		     , c_receiptamt, c_receiptamt_usd  , c_shipment_cost   ,c_assignedagent ,  c_agentsharesettled,"
			  + " c_fragile	   , c_cust_rtnid, c_pickupagent_rtnid,  c_pmtid, c_pickupagentpmtid, cc_qstep_frombranch,"
			  + " c_branchcode	 		 , c_custreceiptnoori, c_specialcase, branch_name, c_allowrtnagent, "
			  + "  '' as others  , '' as custname    , ifnull(cc_branchpmtid,0) as cc_branchpmtid, c_mastercustid,  "
			  + " '' as custprimaryphone, c_settled , c_rcv_district, stp_color, c_allowrtncustomer, rtn_desc ,q_postopnedto, post_desc, "
			  + " case when q_step='PART_SUCC' and cc_qstatus_tobranch='ACTV' then cc_tobranch  when q_step='PART_SUCC' and cc_qstatus_frombranch='ACTV' then cc_frombranch else "+currentBranch_G+" end as inbranch  " 
			  + " from p_cases "
			  + " join kbstep on stp_code= q_step "
			  + " left join kbpostponedoptions on (q_postponedoption = post_code)"
			  + " left join kbbranches on q_branch = branch_id "
			  + " left join kbrtn_reasons on (rtn_code = c_rtnreason)"
			  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
			  + " left join p_caseschain on(cc_caseid = c_id and cc_frombranch="+currentBranch_G+") "
			  + " where c_branchcode="+currentBranch_G+" and c_id = "+m_caseId;
		}
		
		userDefinedFilterLookups.put("c_assignedagent", "select us_id , us_name from kbusers  where us_rank = 'DLVAGENT' and us_branchcode="+currentBranch_G);
		if (getDisplayMode().equalsIgnoreCase("EDITSINGLE")) {
			//System.out.println("heelllooo");
			Connection conn2 = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			HashMap<String, String> updateConditionsMapFlag = new HashMap<String, String>();
			HashMap <String , String> dataMapFromDB = new HashMap<String, String>();
			try {
				String caseid = "";
				if(m_caseId>0)
					caseid = m_caseId+"";
				else
					caseid = httpSRequest.getParameter(keyCol);
				conn2 = mysql.getConn();
				
	            pst = conn.prepareStatement("select q_step from p_cases where c_id =?");
	            pst.setString(1, caseid);
	            rs = pst.executeQuery();
	            ResultSetMetaData rsmd = rs.getMetaData();
	            if (rs.next()) {
	            	for(int i=1 ; i<=rsmd.getColumnCount();i++) {
	            		dataMapFromDB.put(rsmd.getColumnName(i), rs.getString(rsmd.getColumnName(i)));
	            	}
	            }
				
				// remove edit cols if condition not success
				updateConditionsMapFlag = ut.checkCaseEditeConditions(conn, "UPDATECASE", currentBranch_G, caseid);
				for(String colName: updateConditionsMapFlag.keySet()) { 
					if(updateConditionsMapFlag.get(colName).equalsIgnoreCase("N"))
						userDefinedEditCols.remove(colName);
				}
				if(!dataMapFromDB.isEmpty() && dataMapFromDB.get("q_step").equalsIgnoreCase("PRINTMANIFEST")) {
					userDefinedReadOnlyEditCols.add("c_rcv_state");
				}
			}catch(Exception e) {
				e.printStackTrace();
				if(!updateConditionsMapFlag.isEmpty()) {
					for(String colName:updateConditionsMapFlag.keySet()) {
						userDefinedEditCols.remove(colName);
					}
				}
			}finally {
				try {rs.close();}catch(Exception e) {}
				try {pst.close();}catch(Exception e) {}
				try {conn2.close();} catch (Exception e) {}
			}
		}
		super.initialize(smartyStateMap);

		if (search_paramval!=null && !search_paramval.isEmpty()) {
			MainSql = "select mcust_name,c_parentid, q_rmk, q_branch, c_paid_delivery_cost_in_advance, c_rtnreason, '' as del_edit, "
					+ "c_receiptfromsystem, ifnull(q_previous_action_taken_by,0) as q_previous_action_taken_by , c_createdby, q_stage, q_step, stp_name, "
					+ " date(c_createddt) as c_createddt , c_id , c_custid     , c_custhp	,	c_paytodlvcheck , "
					  + " c_rcv_name 	 , c_rcv_hp1      , c_rcv_state		 , "
					  + " c_rural        , concat (ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk, c_rmk , c_productinfo, "
					  + " c_qty		     , c_receiptamt, c_receiptamt_usd  , c_shipment_cost   ,c_assignedagent ,  c_agentsharesettled,"
					  + " c_fragile	   , c_cust_rtnid, c_pickupagent_rtnid,  c_pmtid, c_pickupagentpmtid,"
					  + " c_branchcode	 		 , c_custreceiptnoori, c_specialcase, branch_name, c_allowrtnagent, "
					  + "  '' as others  , '' as custname    , ifnull(chainfor_pmt.cc_branchpmtid,0) as cc_branchpmtid, c_mastercustid,  "
					  + " '' as custprimaryphone, c_settled , c_rcv_district, stp_color, c_allowrtncustomer, rtn_desc, q_postopnedto, q_postponedoption, post_desc, "
					  + " (case "
					  + "	when (q_step='PART_SUCC' and chain.cc_qstatus_tobranch='ACTV') then chain.cc_tobranch "
					  + "	when (q_step='PART_SUCC' and chain.cc_qstatus_frombranch='ACTV') then chain.cc_frombranch "
					  + "	else c_branchcode end) as inbranch " 
					  + " from p_cases "
					  + " join kbstep on stp_code= q_step "
					  + " left join kbpostponedoptions on (q_postponedoption = post_code)"
					  + " left join kbbranches on q_branch = branch_id "
					  + " left join kbrtn_reasons on (rtn_code = c_rtnreason)"
					  + " left join kb_mastercustomer on c_mastercustid = mcust_id "
					  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					  + " left join p_caseschain chain on(chain.cc_caseid = c_id and (chain.cc_qstatus_tobranch='ACTV' or chain.cc_qstatus_frombranch='ACTV') and q_step='PART_SUCC') "
					  + " left join p_caseschain chainfor_pmt on (chainfor_pmt.cc_caseid = c_id and chainfor_pmt.cc_frombranch="+currentBranch_G+" )"
					  + " where c_branchcode="+currentBranch_G+" ";
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
				MainSql +=" and  (c_createddt>='"+fromdt+"') and (c_createddt<=date_add('"+todt+"', interval 1 day) ) ";
			}
		}
		try {
			 allowForceDlv = 
					 Utilities.checkPermissionOfSpecialOperation(conn, "FORCE_DLV", lu, 
							 "casespassedbybranch");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//orderByCols ="  q_enterdate desc ";		
	}
	@Override 
	public StringBuilder genListing() {
		//System.out.println("calling gen listing---------------------");
		search_paramval.remove("fromdt");
		search_paramval.remove("todate");
		return super.genListing();
	}
	
	@Override
	public String doDelete(HttpServletRequest rqs){
		String Msg ="";
		PreparedStatement pst = null;
		ResultSet rs = null;
		String keyVal= rqs.getParameter(keyCol);
		int userId = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		Connection conn = null;
		Utilities ut = new Utilities();
		try {
			conn = mysql.getConn();
			//bakcup first
			pst = conn.prepareStatement("update p_cases set c_deletedby = ?, c_deleteddt=now() where c_id = ?");
			pst.setInt(1,userId );
			pst.setString(2,keyVal );
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			//bakcup first
			pst = conn.prepareStatement("insert into p_cases_deleted select * from p_cases where c_id = ?");
			pst.setString(1,keyVal );
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			//free the receipt
			pst = conn.prepareStatement("update p_receipts set rec_caseid = 0, rec_used='N', rec_assigned_master_cust=0,rec_assigned_customer=0"
					+ " where rec_caseid=?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			
			
			pst = conn.prepareStatement("delete from p_cases where c_id = ?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			//log changes
			CoreUtilities.logChanges(conn, "P_CASES", "c_id", Integer.parseInt(keyVal), "*", "ALL", "NONE", "delete", "عرض كل الشحنات", userId);
			conn.commit();
		}catch (Exception e) {
			try{conn.rollback();}catch (Exception eRollBack){eRollBack.printStackTrace();}
			e.printStackTrace();
		    Msg = "Error";
		    deleteErrorFlag = true;
		    
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		return Msg;
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
		if (hashy.get("c_paid_delivery_cost_in_advance").equalsIgnoreCase("Y")) {
			buttonText += "<br /></br><span style='background: #117e8d;'>مدفوع أجور التوصيل</span>";
		}
		//<div class="dropdown-menu">
		String buttons =""
			+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";font-size: 11.5px;' class='btn  btn-sm  waves-effect waves-light text-white'>"+buttonText+"</button>"
			+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";font-size: 11.5px;' class='btn btn-sm  dropdown-toggle waves-effect waves-light text-white' data-toggle='dropdown'"
			+ " aria-haspopup='true' aria-expanded='true'><span class='sr-only'>Toggle Dropdown</span></button>";

		if(hashy.get("c_paytodlvcheck").equalsIgnoreCase("Y") && lu.getRank_code().equalsIgnoreCase("ITBOSS")) {
			buttons += "<br><br><div><button"+" id='buttonpaytodlvcheck"+ hashy.get("c_custreceiptnoori") +"' type=\"button\" onclick='forcePayCheckN("+hashy.get("c_id")+","+hashy.get("c_custreceiptnoori")+")'"
					+ " class=\"btn btn-warning radius-30 btn-sm\" style='margin-top:5px;'>ازالة تأكيد الوصل</button></div><br>";
			}
		
		String audit = "<a href='displaySingleCaseInfo?auditcaseid="+hashy.get("c_id")+"' "
				+ "class='dropdown-item' >تتبع <i class='fa fa-eye' style='font-size: 1.1rem;vertical-align: text-bottom;'></i></a>";
		String actionMenu ="<div class='dropdown-menu '>";
				//+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";' class='btn btn-light dropdown-toggle btn-sm' type=\"button\" data-bs-toggle=\"dropdown\" aria-expanded=\"false\">"+buttonText+"</button>"
				
		if (hashy.get("c_settled").equalsIgnoreCase("FULL") || hashy.get("c_agentsharesettled").equalsIgnoreCase("FULL")
				|| Integer.parseInt(hashy.get("cc_branchpmtid")) > 0) {
			actionMenu += "<a href='?myClassBean=com.app.cases.Updatecase&c_id=" + hashy.get("c_id") + "&op=upd' "
					+ "class='dropdown-item'>تعديل <i class='fa fa-pencil' style='font-size: 1.1rem;vertical-align: text-bottom;'></i></a>";
		} else {
			if (hashy.get("c_parentid").equalsIgnoreCase("0")) {
				actionMenu += "<a class='dropdown-item' onclick=\"link=false; "
					+ " var rs =doDeleteSmarty(this,'هل تريد حذف هذه الشحنه ؟' ,'c_id','" + hashy.get("c_id")
					+ "' , 'com.app.cases.Updatecase' ); return rs;\"  href='#'>"
					+ "حذف<i class='fa fa-trash' style='font-size: 1.1rem;vertical-align: text-bottom;'></i></a>";
			}
			actionMenu += "<a href='?myClassBean=com.app.cases.Updatecase&c_id=" + hashy.get("c_id") + "&op=upd' "
					+ "class='dropdown-item'>تعديل <i class='fa fa-pencil' style='font-size: 1.1rem;vertical-align: text-bottom;'></i></a>";
		}
		actionMenu +=audit+"</div>";
		
		String html = "<td><div class='btn-group mr-1 mb-1'>"+buttons+actionMenu;
		html+= "</div></br><a href=\"../../PrintSellBillSRVL?c_id="+hashy.get("c_id")+"\" class=\"btn btn-sm btn-success\"><i class=\"fa fa-print fa-sm\"></i></a></td>";
		return html;
	}
	@Override
	public String doUpdate(HttpServletRequest rqs, boolean autoCommit) {
		PreparedStatement pst = null; 
		ResultSet rs = null;
		String caseid = parseUpdateRqs(rqs);
		String msg = "تم التعديل بنجاح";
		String custid = "";
		int userid = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		
		HashMap <String , String> dataMapFromDB = new HashMap<String, String>();
		HashMap<String, String> updateConditionsMapFlag = new HashMap<String, String>();
		UtilitiesFeqar ut = new UtilitiesFeqar();
		boolean callChangeManifetId = false;
		try{
            pst = conn.prepareStatement("select * from p_cases where c_id =?");
            pst.setString(1, caseid);
            rs = pst.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
            	for(int i=1 ; i<=rsmd.getColumnCount();i++) {
            		dataMapFromDB.put(rsmd.getColumnName(i), rs.getString(rsmd.getColumnName(i)));
            	}
            }
            try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception ex) {}
			
			updateConditionsMapFlag = ut.checkCaseEditeConditions(conn, "UPDATECASE", currentBranch, caseid);
			for(String colName: updateConditionsMapFlag.keySet()) { 
				if(updateConditionsMapFlag.get(colName).equalsIgnoreCase("N"))
					inputMap_ori.remove(colName);
			}
			double receiptAmtIqdFromScreen = 0 ;
            if(inputMap_ori.containsKey("c_receiptamt") && inputMap_ori.get("c_receiptamt") != null ) {
            	receiptAmtIqdFromScreen = Double.parseDouble(inputMap_ori.get("c_receiptamt")[0]);
            }else {
            	receiptAmtIqdFromScreen = Double.parseDouble(dataMapFromDB.get("c_receiptamt"));
            }
            
            double receiptAmtUsdFromScreen = 0 ;
            if(inputMap_ori.containsKey("c_receiptamt_usd") && inputMap_ori.get("c_receiptamt_usd") != null ) {
            	receiptAmtUsdFromScreen = Double.parseDouble(inputMap_ori.get("c_receiptamt_usd")[0]);
            }else {
            	receiptAmtUsdFromScreen = Double.parseDouble(dataMapFromDB.get("c_receiptamt_usd"));
            }
            
			//log changes
			String dataFromDB;
			String dataFromScreen;
			for(String key: userDefinedEditCols) {
				if(updateConditionsMapFlag.get(key) !=null && !updateConditionsMapFlag.isEmpty() && updateConditionsMapFlag.get(key).equalsIgnoreCase("N"))
					continue;
				dataFromDB = "";
				dataFromScreen = "";
				
				if (dataMapFromDB.containsKey(key) && inputMap_ori.containsKey(key)) {
					if (dataMapFromDB.get(key)!=null)
						dataFromDB = dataMapFromDB.get(key);
					if(inputMap_ori.get(key)[0]!=null)
						dataFromScreen = inputMap_ori.get(key)[0].trim();
	        		if(!dataFromScreen.trim().equalsIgnoreCase(dataFromDB.trim()) && !key.equalsIgnoreCase("c_receiptamt"))
	        			CoreUtilities.logChanges(conn, "P_CASES", "c_id", Integer.parseInt(caseid), key, dataFromDB, dataFromScreen,
									"update", "شحناتي", userid);
				}
			}
			
			String state="";
			
			String district = "";
			if (inputMap_ori.containsKey("c_rcv_state") && inputMap_ori.get("c_rcv_state")[0]!=null
					&&  !inputMap_ori.get("c_rcv_state")[0].trim().equalsIgnoreCase("")) {
				state = inputMap_ori.get("c_rcv_state")[0];
			}else {
				state = dataMapFromDB.get("c_rcv_state");
			}
			if (inputMap_ori.containsKey("c_rcv_district") && inputMap_ori.get("c_rcv_district")[0]!=null
					&&  !inputMap_ori.get("c_rcv_district")[0].trim().equalsIgnoreCase("")) {
				district = inputMap_ori.get("c_rcv_district")[0];
			}else {
				district = dataMapFromDB.get("c_rcv_district");
			}
			boolean ruralArea = false;
			ruralArea = ut.isRuralDistrict(conn, Integer.parseInt(district), currentBranch);
			
			double agentShareAmt = 0.0;
			if (inputMap_ori.containsKey("c_agentshare")  && inputMap_ori.get("c_agentshare")[0]!=null) {
				agentShareAmt = Double.parseDouble(inputMap_ori.get("c_agentshare")[0]);
			}else {
				agentShareAmt = ut.calcAgentShipmentChargesShare(conn, 
																 currentBranch, 
																 state, 
																 Integer.parseInt(district), 
																 ruralArea, 
																 dataMapFromDB.get("c_assignedagent"));
			}
			if (inputMap_ori.containsKey("c_custid") && inputMap_ori.get("c_custid")[0]!=null
					&&  !inputMap_ori.get("c_custid")[0].trim().equalsIgnoreCase("")) {
				custid = inputMap_ori.get("c_custid")[0];
			}else {
				custid = dataMapFromDB.get("c_custid");
			}
			int custMasterId = 0, pickUpAgent = 0;
			pst = conn.prepareStatement("select ifnull(cust_mastercustid,0) as mastercustid , cust_assigned_pickup_agent "
					+ " from kbcustomers where cust_id = ? ");
			pst.setString(1, custid);
			rs = pst.executeQuery();
			if(rs.next()) {
				custMasterId = rs.getInt("mastercustid");
				pickUpAgent = rs.getInt("cust_assigned_pickup_agent");
			}
            try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception ex) {}
			
			pickUpAgent=	Utilities.getPickUpAgentForMasterCustomer(conn, custMasterId);
			
			
			double shipmentCost = 0.0;
			if (inputMap_ori.containsKey("c_specialcase")  
					&& inputMap_ori.get("c_specialcase")[0] !=null
					&& inputMap_ori.get("c_specialcase")[0].equalsIgnoreCase("Y")
					&& inputMap_ori.containsKey("c_shipment_cost")  
					&& inputMap_ori.get("c_shipment_cost")[0] !=null) {
				shipmentCost = Double.parseDouble(inputMap_ori.get("c_shipment_cost")[0]);
				
			}else {
				try {
					shipmentCost = ut.calcShipmentChargesBasedOnDestCity(conn, state,ruralArea, custMasterId, Integer.parseInt(custid), currentBranch);
				}catch (Exception e) {
					shipmentCost = Double.parseDouble(dataMapFromDB.get("c_shipment_cost"));
				}
			}
			String paidDeliveryOnlyReceiptWithZeroReceiptAmount = "";
			 if (inputMap_ori.containsKey("c_paid_delivery_cost_in_advance")  
	            		&& inputMap_ori.get("c_paid_delivery_cost_in_advance")!=null
	            		&& inputMap_ori.get("c_paid_delivery_cost_in_advance")[0].length()>0) {
				 paidDeliveryOnlyReceiptWithZeroReceiptAmount = 
				 inputMap_ori.get("c_paid_delivery_cost_in_advance")[0];
			 }else {
	            	paidDeliveryOnlyReceiptWithZeroReceiptAmount  = 
	            	dataMapFromDB.get("c_paid_delivery_cost_in_advance");
			 }
			 if (paidDeliveryOnlyReceiptWithZeroReceiptAmount.equalsIgnoreCase("Y")) {
				 if (receiptAmtIqdFromScreen>0 || receiptAmtUsdFromScreen>0) {
					 throw new Exception ("لا يمكن تحديد الوصل مدفوع توصيل فقط اذا كان مبلغ الوصل اكبر من صفر");
				 }
			 }
			if (receiptAmtIqdFromScreen != Double.valueOf(dataMapFromDB.get("c_receiptamt")).longValue() 
					&& inputMap_ori.containsKey("c_receiptamt") && inputMap_ori.get("c_receiptamt") != null) {
	            if(!updateConditionsMapFlag.isEmpty() 
	            		&& updateConditionsMapFlag.get("c_receiptamt").equalsIgnoreCase("Y")) {
	            	Utilities.changeReceiptByCaseId(conn, Integer.parseInt(caseid),  receiptAmtIqdFromScreen,userid, "شحناتي",
						StandardFinCurrency.IQD);
	            }
			}
			if (receiptAmtUsdFromScreen != Double.valueOf(dataMapFromDB.get("c_receiptamt")).longValue()  
					&& inputMap_ori.containsKey("c_receiptamt_usd") && inputMap_ori.get("c_receiptamt_usd") != null) {
				 if(!updateConditionsMapFlag.isEmpty() 
						 && updateConditionsMapFlag.get("c_receiptamt_usd").equalsIgnoreCase("Y")) {
					 Utilities.changeReceiptByCaseId(conn, Integer.parseInt(caseid), receiptAmtUsdFromScreen,userid, "شحناتي",
						StandardFinCurrency.USD);
				 }
			}
			
//			if(updateConditionsMapFlag.get("c_assignedagent").equalsIgnoreCase("Y") 
//					&& inputMap_ori.containsKey("c_assignedagent")
//					&& inputMap_ori.get("c_assignedagent") != null
//					&& inputMap_ori.get("c_assignedagent")[0].length()>0) {
//				if(Integer.parseInt(inputMap_ori.get("c_assignedagent")[0]) != Integer.parseInt(dataMapFromDB.get("c_assignedagent"))
//						 && dataMapFromDB.get("q_step").equalsIgnoreCase("PRINTMANIFEST"))
//					callChangeManifetId = true;
//				//System.out.println("=====================callChangeManifetId===================");
//			}
			
			pst = conn.prepareStatement("update p_cases set "
    		+ " c_rcv_hp1=?	   	  , c_rcv_state=? 	    , c_rcv_district=?, c_rcv_addr_rmk=?, c_rmk=?,  "
    		+ " c_shipment_cost=? , c_custreceiptnoori=?, c_agentshare=?  , c_custid=?		, c_pickupagent= ?, "
    		+ " c_mastercustid=?  , c_specialcase = ?, c_paid_delivery_cost_in_advance=? "
    		+ " where c_id = ?");
            pst.setString(1, inputMap_ori.get("c_rcv_hp1")[0]);
            pst.setString(2, state);
            pst.setString(3, district);
            pst.setString(4, inputMap_ori.get("c_rcv_addr_rmk")[0]);
            if (inputMap_ori.containsKey("c_rmk")  && inputMap_ori.get("c_rmk")!=null && inputMap_ori.get("c_rmk")[0].length()>0) {
            	pst.setString(5, inputMap_ori.get("c_rmk")[0]);
            }else {
            	pst.setString(5, dataMapFromDB.get("c_rmk"));
            }
            if(!updateConditionsMapFlag.isEmpty() && updateConditionsMapFlag.get("c_shipment_cost").equalsIgnoreCase("N")) {
            	pst.setString(6, dataMapFromDB.get("c_shipment_cost"));
            }else {
            	pst.setDouble(6, shipmentCost);
            }
            if (inputMap_ori.containsKey("c_custreceiptnoori")  && inputMap_ori.get("c_custreceiptnoori")!=null && inputMap_ori.get("c_custreceiptnoori")[0].length()>0)
            	pst.setString(7, inputMap_ori.get("c_custreceiptnoori")[0]);
            else
            	pst.setString(7, dataMapFromDB.get("c_custreceiptnoori"));
            if(!updateConditionsMapFlag.isEmpty() && updateConditionsMapFlag.get("c_agentshare").equalsIgnoreCase("N"))
            	pst.setString(8, dataMapFromDB.get("c_agentshare"));
            else
            	pst.setDouble(8, agentShareAmt);
            pst.setString(9, custid);
            pst.setInt(10, pickUpAgent);
            pst.setInt(11, custMasterId);
            if (inputMap_ori.containsKey("c_specialcase")  && inputMap_ori.get("c_specialcase")!=null && inputMap_ori.get("c_specialcase")[0].length()>0)
            	pst.setString(12, inputMap_ori.get("c_specialcase")[0]);
            else
            	pst.setString(12, dataMapFromDB.get("c_specialcase"));
//            if (inputMap_ori.containsKey("c_assignedagent")  && inputMap_ori.get("c_assignedagent")!=null
//            		&& inputMap_ori.get("c_assignedagent")[0].length()>0) {
//            	pst.setString(13, inputMap_ori.get("c_assignedagent")[0]);
//            }else {
//            	pst.setString(13, dataMapFromDB.get("c_assignedagent"));
//            }
//           
            pst.setString(13, paidDeliveryOnlyReceiptWithZeroReceiptAmount);
            pst.setString(14, caseid);
            pst.executeUpdate();
          
            if(callChangeManifetId) {
    			try {pst.close();}catch(Exception ex) {}	
    			pst = conn.prepareStatement("Update p_cases set c_dlvagent_manifestid = ? where c_id = ?");
    			pst.setInt(1, ut.generateDlvAgentManifestIdForCasesInPrintManifest(conn, 
    																			   Integer.parseInt(inputMap_ori.get("c_assignedagent")[0]), 
    																			   userid, 
    																			   currentBranch));
    			pst.setString(2, caseid);
    			pst.executeUpdate();
            }
            try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception ex) {}	
            // if the case in dlv stage, and the receipt price is changed, then we need to flag it, Nafie 17-jan-2022
			if (inputMap_ori.containsKey("c_receiptamt") && inputMap_ori.get("c_receiptamt")[0]!=null) {
				if (!inputMap_ori.get("c_receiptamt")[0].equalsIgnoreCase(dataMapFromDB.get("c_receiptamt"))) {
					pst = conn.prepareStatement("update p_cases set c_changed_receiptprice_after_dlv='Y' "
							+ " where q_stage ='DLV' and c_id = ?");
					pst.setString(1, caseid);
					pst.executeUpdate();
				}
			}
            
            conn.commit();
            setUpdateErrorFlag(false);
		}catch(Exception e){
			e.printStackTrace();
			msg = "Error at updated data, error("+ e.getMessage() +") ";
			 try{conn.rollback();}catch(Exception ex){}//end of inner catch
			 setUpdateErrorFlag(true);
		}finally{
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception ex) {}		
		}//end of finally
		
		return msg;
	}
	
	public int getCaseId() {
		return m_caseId;
	}


	public void setCaseId(int caseId) {
		this.m_caseId = caseId;
	}

}