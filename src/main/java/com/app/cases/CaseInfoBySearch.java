package com.app.cases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import smarty.core.CoreMgr;
import smarty.db.mysql;

public class CaseInfoBySearch extends CoreMgr{
	public CaseInfoBySearch() {
		MainSql = "select c_parentid, c_paid_delivery_cost_in_advance, c_rtnreason, '' as fromdt, '' as todate , '' as del_edit, c_receiptfromsystem, q_previous_action_taken_by , c_createdby, q_stage, q_step, stp_name, date(c_createddt) as c_createddt , c_id , c_custid     , c_custhp		 , "
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
		  + " left join kbpostponedoptions on (q_postponedoption = post_code)"
		  + " left join p_caseschain on(cc_caseid = c_id and cc_frombranch = {userstorecode}) "
		  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
		  + " where 1=0";
		canFilter = true;
		canExport = true;
		pdfExport = true;
		userDefinedCaption = "عرض كل الشحنات";
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
				
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("stp_name");
		userDefinedGridCols.add("c_mastercustid");
		userDefinedGridCols.add("c_custid");
		//userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_branchcode");
		userDefinedGridCols.add("c_receiptamt");
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
				
		userDefinedColLabel.put("stp_name", "المرحله");
		userDefinedColLabel.put("c_custid", "المتجر");
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
		userDefinedColLabel.put("c_branchcode","خلق في فرع");
		userDefinedColLabel.put("c_weight","الوزن");
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
		
		
		userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers order by cust_name asc");
		userDefinedLookups.put("c_createdby", "select us_id , us_name from kbusers");
		userDefinedLookups.put("c_rcv_district", "select cdi_id, cdi_name from kbcity_district");
		userDefinedLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userDefinedLookups.put("c_branchcode", "select branch_id, branch_name from kbbranches");
		//userModifyTD.put("del_edit", "showDel_Edit({c_id},{c_settled})");
				
			userDefinedFilterCols.add("c_rcv_hp1");
			
			userDefinedColLabel.put("c_agentsharesettled","هل تم محاسبة المندوب");
			userDefinedColLabel.put("fromdt","بتاريخ");
			userDefinedColLabel.put("todate","إلى تاريخ");
			userDefinedFilterCols.add("c_custreceiptnoori");
			
			userDefinedFilterColsHtmlType.put("fromdt", "DATE");
			userDefinedFilterColsHtmlType.put("todate", "DATE");
			
			userDefinedEditColsHtmlType.put("c_createddt", "DATE");
			userDefinedNewColsHtmlType.put("c_createddt", "TIMESTAMP");
			userDefinedColLabel.put("c_createddt","تاريخ الشحنه");

			userDefinedColLabel.put("c_rtnreason","سبب الراجع");
			userDefinedColLabel.put("q_postponedoption","سبب التأجيل");
			userDefinedReadOnlyEditCols.add("userDefinedEditCols");
			userDefinedLookups.put("c_agentsharesettled", "select 'FULL' , 'نعم' from dual union select 'NO' , 'لا' from dual");
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
			userDefinedEditColsHtmlType.put("custprimaryphone" , "TEXT");
			
			UserDefinedPageRows = 50;
			userModifyTD.put("stp_name", "modifyStepName({cc_branchpmtid},{c_agentsharesettled},{c_allowrtnagent},{c_allowrtncustomer},{rtn_desc},"
					+ "{c_custreceiptnoori},{c_id},{stp_name},{c_settled},{q_stage},{q_step},{stp_color},{branch_name},{c_cust_rtnid},{c_pickupagent_rtnid},{c_pmtid},"
					+ "{c_pickupagentpmtid},{inbranch},{q_postopnedto},{post_desc})");
			//userModifyTD.put("stp_name", "modifyStepName({stp_name},{c_settled},{q_stage},{q_step})");
			userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
			//userDefinedEditMockUpCols.put("custname", "(select cm_custid from p_casesmaster where cm_id = c_cmid)");
			//userDefinedFilterColsHtmlType.put("c_custid", "DROPLIST");
			userDefinedEditColsHtmlType.put("c_custid", "DROPLIST");
			
			userDefinedUseDataTables = true;
				
		}//end of no-arg constructor Updatecase
			
			
		@Override
		public void initialize(HashMap smartyStateMap){
			int currentBranch_G = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
			super.initialize(smartyStateMap);
			boolean foundSearch = false;
			for (String parameter : search_paramval.keySet()) {
				for (String value : search_paramval.get(parameter)) {
					if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {
						if(parameter.equals("c_rcv_hp1") || parameter.equals("c_custreceiptnoori")) {
							foundSearch = true;
						}
					}
				}
			}
			if (foundSearch) {
				MainSql = "select c_parentid, c_paid_delivery_cost_in_advance, c_rtnreason, '' as del_edit, c_receiptfromsystem, q_previous_action_taken_by , c_createdby, q_stage, q_step, stp_name, "
				+ " date(c_createddt) as c_createddt , c_id , c_custid     , c_custhp		 , "
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
				  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
				  + " left join p_caseschain chain on(chain.cc_caseid = c_id and (chain.cc_qstatus_tobranch='ACTV' or chain.cc_qstatus_frombranch='ACTV') and q_step='PART_SUCC') "
				  + " left join p_caseschain chainfor_pmt on (chainfor_pmt.cc_caseid = c_id and chainfor_pmt.cc_frombranch="+currentBranch_G+" )"
				  + " where c_branchcode="+currentBranch_G+" ";
			}		
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
			
			String buttons =""
				+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";font-size: 11.5px;' class='btn  btn-sm  waves-effect waves-light text-white'>"+buttonText+"</button>"
				+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";font-size: 11.5px;' class='btn btn-sm  dropdown-toggle waves-effect waves-light text-white' data-toggle='dropdown'"
				+ " aria-haspopup='true' aria-expanded='true'><span class='sr-only'>Toggle Dropdown</span></button>";
	        
			String html = "<td><div class='btn-group mr-1 mb-1'>"+buttons;
			
			html+= "</div></td>";
			return html;
		}
	}//end of class Updatecase
