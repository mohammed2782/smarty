package com.app.cases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;
import smarty.db.mysql;

import com.app.financials.StandardFinCurrency;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;
import com.app.util.UtilitiesNafie;

public class AllCasesPassedByBranch extends CoreMgr {
	protected String QueueDataSpanClass = "QueueDataSpanClass";
	MasterCaseInformation caseMaster = new MasterCaseInformation();
	private int caseId = 0;
	boolean allowForceDlv = false;
	private int currentBranch_G = 0;
	private int userId_G = 0;
	public AllCasesPassedByBranch (){

	MainSql = "select mcust_name,q_rmk, q_branch, '' as fromdt, '' as todate, c_receiptfromsystem, ifnull(q_previous_action_taken_by,0) as q_previous_action_taken_by , "
	+ "c_createdby, q_stage, q_step, stp_name, date(c_createddt) as c_createddt, c_id, cc_branchpmtid , c_custid, c_custhp, "
	+ " c_rcv_name, c_rcv_hp1, c_rcv_state, c_rural , concat (ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk,"
	+ " c_rmk  c_receiptamt, c_receiptamt_usd ,c_assignedagent , cc_pathcost, c_branchcode	, c_custreceiptnoori, "
	+ " c_specialcase, branch_name, c_allowrtnagent, '' as others  , '' as custname ,c_mastercustid, c_cust_rtnid, "
	+ " c_pickupagent_rtnid, c_pmtid, c_pickupagentpmtid,'' as custprimaryphone, c_settled , "
	+ " c_rcv_district, stp_color, c_allowrtncustomer, rtn_desc "
	  + " from p_cases "
	  + " join kbstep on stp_code= q_step "
	  + " left join kbbranches on q_branch = branch_id "
	  + " join p_caseschain on cc_caseid = c_id and cc_tobranch={userstorecode} "
	  + " left join kbrtn_reasons on (rtn_code = c_rtnreason)"
	  + " left join kb_mastercustomer on c_mastercustid = mcust_id "
	  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
	  + " where 1=0";
		

		keyCol = "c_id";
		canFilter = true;
		//canEdit   = true;
		//canDelete = true;		
		canExport = true;
		pdfExport = true;
		userDefinedCaption = "شحنات مرت على مخزني";
		userDefinedUpdateCaption = "تعديل بيانات شحنه";
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
		userDefinedReadOnlyEditCols.add("c_specialcase");
		userDefinedEditCols.add("c_rcv_hp1");
		userDefinedEditCols.add("c_rcv_state");
		userDefinedEditCols.add("c_rcv_district");
		userDefinedEditCols.add("c_rcv_addr_rmk");
		userDefinedEditCols.add("cc_pathcost");
		userDefinedEditCols.add("c_agentshare");
		userDefinedEditCols.add("c_receiptamt");
		userDefinedEditCols.add("c_receiptamt_usd");
		userDefinedEditCols.add("c_custreceiptnoori");
		//userDefinedEditCols.add("c_assignedagent");
		
		userDefinedReadOnlyEditCols.add("c_custid");
		userDefinedReadOnlyEditCols.add("c_rcv_state");
		
		//userDefinedGridCols.add("c_mastercustid");
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("mcust_name");

		userDefinedGridCols.add("c_branchcode");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("stp_name");
		userDefinedGridCols.add("q_previous_action_taken_by");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("c_rural");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("c_createdby");
		userDefinedGridCols.add("c_specialcase");

		userDefinedColLabel.put("custname", "إسم العميل");
		userDefinedColLabel.put("mcust_name", "العميل");

		userDefinedColLabel.put("c_specialcase", "شحنه خاصه؟");
		userDefinedColLabel.put("cc_pathcost", "اجور النقل");
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
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل د.ع");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
		userDefinedColLabel.put("c_sendmoney","مبلغ مرسل");
		userDefinedColLabel.put("c_bringitemsback","جلب  بضاعه");
		userDefinedColLabel.put("c_branchcode","أنشأ في فرع");
		userDefinedColLabel.put("c_custreceiptnoori","رقم الوصل");
		userDefinedColLabel.put("c_id","كود الشحنه");
		userDefinedColLabel.put("q_step","المرحله");
		userDefinedColLabel.put("c_createdby","المدخل");
		userDefinedColLabel.put("c_settled","تم التحاسب ؟");
		userDefinedColLabel.put("c_rcv_district", "المنطقة");
		userDefinedColLabel.put("c_agentshare", "حصة مندوب التوصيل");
		userDefinedColLabel.put("q_previous_action_taken_by", "اخر من حدث الحاله");
		userDefinedColLabel.put("c_receiptfromsystem", "متولد من النظام؟");
		userDefinedColLabel.put("c_receiptfromsystem","مولد من النظام");
		userDefinedColLabel.put("c_pickupagent","مندوب إستلام");
		userDefinedColLabel.put("c_mastercustid", "العميل");
		userDefinedColLabel.put("c_rcv_hp2", "2هاتف المستلم");
		
		//userDefinedFilterCols.add("c_mastercustid");
		userDefinedFilterCols.add("c_custid");
		userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers order by cust_name asc");
		userDefinedLookups.put("c_rcv_district", "select cdi_id, cdi_name from kbcity_district");
		userDefinedLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userDefinedLookups.put("c_createdby", "select us_id , us_name from kbusers");
		userDefinedLookups.put("q_previous_action_taken_by", "select us_id , us_name from kbusers");
		userDefinedLookups.put("c_branchcode", "select branch_id, branch_name from kbbranches");
		
		userDefinedFilterCols.add("c_rcv_hp1");
		userDefinedFilterCols.add("c_id");
		
		userDefinedColLabel.put("fromdt","بتاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		
		userDefinedFilterCols.add("c_rcv_state");
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("c_branchcode");
		userDefinedFilterCols.add("q_step");
		userDefinedFilterCols.add("c_settled");
		userDefinedFilterCols.add("c_receiptamt");
		userDefinedFilterCols.add("c_assignedagent");
		userDefinedFilterCols.add("fromdt");
		userDefinedFilterCols.add("todate");
		userDefinedFilterCols.add("c_pickupagent");
		userDefinedFilterCols.add("c_specialcase");
		userDefinedFilterColsHtmlType.put("fromdt", "DATE");
		userDefinedFilterColsHtmlType.put("todate", "DATE");
		
		userDefinedEditColsHtmlType.put("c_createddt", "DATE");
		userDefinedEditColsHtmlType.put("c_rcv_district", "DROPLIST");
		userDefinedNewColsHtmlType.put("c_createddt", "TIMESTAMP");
		userDefinedColLabel.put("c_createddt","تاريخ الشحنه");

		
		userDefinedReadOnlyEditCols.add("userDefinedEditCols");

		userDefinedLookups.put("custname", "select cust_id , cust_name from kbcustomers order by cust_name ASC");
		userDefinedLookups.put("custprimaryphone", "!select cust_phone1 as ph, cust_phone1 from kbcustomers where cust_id = '{custname}'");
		userDefinedLookups.put("c_rcv_state", "select st_code , st_name_ar from kbstate  order by st_order");
		userDefinedLookups.put("c_rural", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_settled", "select kbcode , kbdesc from kbgeneral where kbcat1='SETTLED'");
		userDefinedLookups.put("q_step", "select stp_code , stp_name from kbstep");
		userDefinedLookups.put("c_assignedagent", "select us_id , us_name from kbusers  where us_rank = 'DLVAGENT'  ");
		userDefinedLookups.put("c_pickupagent", "select us_id , us_name from kbusers  where us_rank = 'PICKUPAGENT'  ");
		userDefinedEditLookups.put("c_rcv_district", "!select cdi_id, cdi_name from kbcity_district where cdi_stcode='{c_rcv_state}'");
		userDefinedLookups.put("c_mastercustid", "select mcust_id , mcust_name from kb_mastercustomer");
		userDefinedEditLookups.put("c_assignedagent", "!select us_id , us_name from kbusers  where us_rank = 'DLVAGENT' and us_branchcode = {userstorecode} and "
				+ " us_id in (select distinct agdi_usid from kbagent_district where agdi_districtcode = {c_rcv_district} and us_active='Y')");
		
		userDefinedEditColsHtmlType.put("custname" , "DROPLIST");
		userDefinedEditColsHtmlType.put("c_mastercustid" , "DROPLISTBIGDATA");
		userDefinedEditColsHtmlType.put("c_custid" , "DROPLISTBIGDATA");
		userDefinedEditColsHtmlType.put("c_assignedagent" , "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_pickupagent" , "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_specialcase" , "DROPLIST");
		userDefinedEditColsHtmlType.put("c_specialcase" , "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_custid", "DROPLISTBIGDATA");
		userDefinedFilterColsHtmlType.put("c_mastercustid", "DROPLISTBIGDATA");
		userDefinedFilterColsHtmlType.put("c_assignedagent" , "DROPLIST");
		userDefinedFilterColsHtmlType.put("c_branchcode" , "DROPLIST");
		userDefinedEditColsHtmlType.put("custprimaryphone" , "TEXT");
		userDefinedEditColsHtmlType.put("c_bringitemsback" , "RADIO");
		userDefinedEditColsHtmlType.put("c_rural" , "DROPLIST");
		
		userDefinedEditColsHtmlType.put("c_qty" , "NUMBER");
		userDefinedEditColsHtmlType.put("c_rmk" , "TEXTAREA");
		userDefinedEditColsHtmlType.put("c_rcv_addr_rmk" , "TEXTAREA");
		

		userDefinedReadOnlyEditCols.add("c_receiptfromsystem");

		userDefinedColsMustFill.add("custname");
		userDefinedColsMustFill.add("c_rcv_hp1");
		userDefinedColsMustFill.add("c_qty");
		userDefinedColsMustFill.add("c_receiptamt");
		userDefinedColsMustFill.add("c_custreceiptnoori");
		userDefinedColsMustFill.add("c_agentshare");
		userDefinedColsMustFill.add("c_rcv_state");
		
		userDefinedColsMustFill.add("c_assignedagent");
		
		UserDefinedPageRows = 50;
		userModifyTD.put("stp_name", 
				"modifyStepName({cc_branchpmtid},{c_agentsharesettled},{c_allowrtnagent},{c_allowrtncustomer},"
				+ "{rtn_desc},{c_custreceiptnoori},{c_id},{stp_name},{c_settled},{q_stage},"
				+ "{q_step},{stp_color},{branch_name},{c_cust_rtnid},{c_pickupagent_rtnid},"
				+ "{c_pmtid},{c_pickupagentpmtid}, {q_previous_action_taken_by},{q_branch},{q_rmk})");		
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		userModifyTD.put("c_specialcase", "spitialCaseColor({c_specialcase},{cc_pathcost})");
		userDefinedEditColsHtmlType.put("c_custid", "DROPLIST");
		
		userDefinedUseDataTables = true;
		userDefinedTableHeadersClass = "text-white  bg-gradient-x-info";
	}//end of no-arg constructor
		
	
	@Override
	public void initialize(HashMap smartyStateMap){
		currentBranch_G = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		userId_G = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		mainTable = "p_cases join p_caseschain on cc_caseid = c_id and cc_tobranch = "+currentBranch_G;
		if(caseId>0) {
			if(replaceVarsinString("{caneditfromstage}",arrayGlobals).trim().equalsIgnoreCase("true"))
				canEdit = true;
			else {
				canEdit = false;
				userDefinedEditCaption = "لايمكن التعديل على الشحنة لانه تم محاسبة احد الاطراف";
			}
			displayMode = "EDITSINGLE";
			MainSql = "select  mcust_name,q_rmk, q_branch, ifnull(q_previous_action_taken_by,0) as q_previous_action_taken_by, c_receiptfromsystem, c_createdby, q_stage, q_step, stp_name, "
			+ " date(c_createddt) as c_createddt , c_id , c_custid     , c_custhp		 , "
			  + " c_rcv_name 	 , c_rcv_hp1      , c_rcv_state, cc_branchpmtid , "
			  + " c_rural        , concat (ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk, c_rmk , "
			  + " c_qty		     , c_receiptamt, c_receiptamt_usd    ,c_assignedagent , c_agentshare, "
			  + " c_branchcode	 , c_custreceiptnoori, c_specialcase, rtn_desc, c_allowrtnagent, "
			  + " cc_pathcost, '' as others  , '' as custname    , c_mastercustid, c_cust_rtnid, c_pickupagent_rtnid,"
			  + " c_pmtid, c_pickupagentpmtid, c_agentsharesettled,"
			  + " '' as custprimaryphone, c_settled , c_rcv_district, stp_color, branch_name, c_allowrtncustomer "
			  + " from p_cases "
			  + " join p_caseschain on cc_caseid = c_id and cc_tobranch="+currentBranch_G+" "
			  + " join kbstep on stp_code= q_step "
			  + " left join kbbranches on q_branch = branch_id "
			  + " left join kb_mastercustomer on c_mastercustid = mcust_id "
			  + " left join kbrtn_reasons on (rtn_code = c_rtnreason)"
			  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
			  + " where c_id = "+caseId;
		}
		if (getDisplayMode().equalsIgnoreCase("EDITSINGLE")) {
			Connection conn2 = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			UtilitiesFeqar ut = new UtilitiesFeqar();
			HashMap<String, String> updateConditionsMapFlag = new HashMap<String, String>();
			HashMap <String , String> dataMapFromDB = new HashMap<String, String>();
			try {
				String caseid = "";
				if(caseId>0)
					caseid = caseId+"";
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
	            if(!dataMapFromDB.isEmpty() && dataMapFromDB.get("q_step").equalsIgnoreCase("PRINTMANIFEST")) {
					userDefinedReadOnlyEditCols.add("c_rcv_state");
				}
				// remove edit cols if condition not success
				updateConditionsMapFlag = ut.checkCaseEditeConditions(conn, "THROWMYBRANCH", currentBranch_G, caseid);
				for(String colName: updateConditionsMapFlag.keySet()) { 
					if(updateConditionsMapFlag.get(colName).equalsIgnoreCase("N")) {
						if (!colName.equalsIgnoreCase("c_rcv_state"))
							userDefinedEditCols.remove(colName);
					}else {
						userDefinedReadOnlyEditCols.remove(colName);
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
			MainSql = "select mcust_name,q_rmk, q_branch, ifnull(q_previous_action_taken_by,0) as q_previous_action_taken_by, c_receiptfromsystem,  c_createdby, q_stage, q_step, "
			  + " stp_name, date(c_createddt) as c_createddt, c_id , c_custid, c_custhp, c_rcv_name, c_rcv_hp1, "
			  + " c_rcv_state, c_agentshare, c_rural, cc_branchpmtid, concat (ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk, "
			  + " c_rmk , c_qty, c_receiptamt, c_receiptamt_usd,c_assignedagent ,c_agentsharesettled, "
			  + " c_branchcode, c_custreceiptnoori, c_specialcase, rtn_desc, c_allowrtnagent, cc_pathcost, "
			  + "  '' as others  , '' as custname , c_mastercustid, c_cust_rtnid, c_pickupagent_rtnid, c_pmtid, "
			  + " c_pickupagentpmtid,'' as custprimaryphone, c_settled , c_rcv_district, stp_color, branch_name, "
			  + " c_allowrtncustomer "
			  + " from p_cases "
			  + " join p_caseschain on cc_caseid = c_id and cc_tobranch="+currentBranch_G+" "
			  + " join kbstep on stp_code= q_step "
			  + " left join kbbranches on q_branch = branch_id "
			  + " left join kbrtn_reasons on (rtn_code = c_rtnreason)"
			  + " left join kb_mastercustomer on c_mastercustid = mcust_id "
			  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) ";
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
				MainSql +=" where  (date(c_createddt)>='"+fromdt+"') and (date(c_createddt)<='"+todt+"' ) ";
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
		
	}
	@Override 
	public StringBuilder genListing() {
		//System.out.println("calling gen listing---------------------");
		search_paramval.remove("fromdt");
		search_paramval.remove("todate");
		return super.genListing();
	}
	
	public String spitialCaseColor(HashMap<String,String>hashy) {
		//{c_specialcase},{cc_pathcost}
		boolean spitialCase = hashy.get("c_specialcase").equalsIgnoreCase("Y");
		String style = "";
		String trans = "لا";
		if (spitialCase) {
			style = "background:#f5eaaf";
			trans = "نعم";
		}
		String html = "<td style="+style+">"+trans;
		if (spitialCase) {
			html+="<br />مبلغ الشحن: "+numFormat.format(Double.parseDouble(hashy.get("cc_pathcost")));
		}
		html +="</td>";
		
		return html;
	}
	public String modifyStepName(HashMap<String,String>hashy) {
		String buttonText = hashy.get("stp_name")+"<br /> في فرع - "+hashy.get("branch_name");
		String forceOrRestoreDlvBtn = "";
		String forceOrRestoreDlvReason="";
		int previous_action_taken_by=Integer.parseInt(hashy.get("q_previous_action_taken_by"));
		if(hashy.get("q_stage").equalsIgnoreCase("DLV")) {
			if (allowForceDlv
			   && previous_action_taken_by==userId_G ) {
				forceOrRestoreDlvBtn ="<button type=\"button\" onclick='restoreFromforceDlv("+hashy.get("c_id")+")' "
						+ " class=\"btn btn-info radius-30 btn-sm\" style='margin-top:5px;'>تراجع عن أحتسابه واصل</button>"; 
				if((hashy.get("q_rmk") == null)) {
					forceOrRestoreDlvReason="<hr/>سبب احتسابه واصل: لايوجد سبب";
				}else {
					forceOrRestoreDlvReason="<hr/>سبب احتسابه واصل: "+hashy.get("q_rmk");					
				}
			}
			if(Integer.parseInt(hashy.get("c_pmtid"))>0)
				buttonText += "<br /> رقم كشف حساب العميل "+hashy.get("c_pmtid");
			else if(Integer.parseInt(hashy.get("c_pickupagentpmtid"))>0)
				buttonText += "<br /> رقم كشف حساب مندوب الاستلام "+hashy.get("c_pickupagentpmtid");
			
		}else {
			if (hashy.get("q_branch").equalsIgnoreCase(currentBranch_G+"") && allowForceDlv) {
				forceOrRestoreDlvBtn ="<button type=\"button\" onclick='forceDlv("+hashy.get("c_id")+")' "
						+ " class=\"btn btn-warning radius-30 btn-sm\" style='margin-top:5px;'>احتسابه واصل</button>";
			}
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
		if(hashy.get("q_stage").equalsIgnoreCase("AGENTOP") && hashy.get("q_step").equalsIgnoreCase("POSTPONED")) {
			buttonText += "<br /> تاريخ التأجيل "+hashy.get("q_postopnedto");
			buttonText += "<br />"+hashy.get("post_desc");
		}
		buttonText +=forceOrRestoreDlvReason;
		//<div class="dropdown-menu">
		String buttons =""
			+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";font-size: 11.5px;' class='btn  btn-sm  waves-effect waves-light text-white'>"+buttonText+"</button>"
			+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";font-size: 11.5px;' class='btn btn-sm  dropdown-toggle waves-effect waves-light text-white' data-toggle='dropdown'"
			+ " aria-haspopup='true' aria-expanded='true'><span class='sr-only'>Toggle Dropdown</span></button>";
          
		
		String audit = "<a href='displaySingleCaseInfo?auditcaseid="+hashy.get("c_id")+"' "
				+ "class='dropdown-item' >تتبع <i class='fa fa-eye' style='font-size: 1.1rem;vertical-align: text-bottom;'></a>";
		String actionMenu ="<div class='dropdown-menu '>";
				//+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";' class='btn btn-light dropdown-toggle btn-sm' type=\"button\" data-bs-toggle=\"dropdown\" aria-expanded=\"false\">"+buttonText+"</button>"
				
		if (hashy.get("c_settled").equalsIgnoreCase("FULL") || hashy.get("c_agentsharesettled").equalsIgnoreCase("FULL")
				|| Integer.parseInt(hashy.get("cc_branchpmtid")) > 0) {
			actionMenu += "<a href='?myClassBean=com.app.cases.AllCasesPassedByBranch&c_id=" + hashy.get("c_id") + "&op=upd' "
					+ "class='dropdown-item'>تعديل <i class='fa fa-pencil' style='font-size: 1.1rem;vertical-align: text-bottom;'></i></a>";
		} else {
			actionMenu += "<a href='?myClassBean=com.app.cases.AllCasesPassedByBranch&c_id=" + hashy.get("c_id") + "&op=upd' "
					+ "class='dropdown-item'>تعديل <i class='fa fa-pencil' style='font-size: 1.1rem;vertical-align: text-bottom;'></i></a>";
		}
		actionMenu +=audit+"</div>";
		
		
		String html = "<td><div class='btn-group mr-1 mb-1'>"+buttons+actionMenu;
		html+= "</div>"+forceOrRestoreDlvBtn+"</td>";
		return html;
	}
		
//		
//		String buttonText = hashy.get("stp_name")+"<br /> في فرع - "+hashy.get("branch_name");
//		if(hashy.get("q_stage").equalsIgnoreCase("DLV")) {
//				if(Integer.parseInt(hashy.get("c_pmtid"))>0)
//					buttonText += "<br /> رقم كشف حساب العميل "+hashy.get("c_pmtid");
//				else if(Integer.parseInt(hashy.get("c_pickupagentpmtid"))>0)
//					buttonText += "<br /> رقم كشف حساب مندوب الاستلام "+hashy.get("c_pickupagentpmtid");
//			
//		}
//		if((hashy.get("c_allowrtncustomer").equalsIgnoreCase("Y") || hashy.get("c_allowrtnagent").equalsIgnoreCase("Y"))
//				&& (hashy.get("q_step").contains("RTN") || hashy.get("q_step").equalsIgnoreCase("PART_SUCC"))) {
//			if(hashy.get("q_step").contains("RTN"))
//				buttonText += "<br /> "+hashy.get("rtn_desc");
//			if(Integer.parseInt(hashy.get("c_cust_rtnid"))>0)
//				buttonText += "<br /> رقم كشف راجع العميل "+hashy.get("c_cust_rtnid");
//			else if(Integer.parseInt(hashy.get("c_pickupagent_rtnid"))>0)
//				buttonText += "<br /> رقم كشف راجع مندوب الاستلام "+hashy.get("c_pickupagent_rtnid");
//		}
//		String audit = "<a href='displaySingleCaseInfo?auditcaseid="+hashy.get("c_id")+"' "
//				+ "class='dropdown-item' >تتبع <i class='lni lni-eye' style='font-size: 1.1rem;vertical-align: text-bottom;'></i></a>";
//		String actionMenu ="<div class='dropdown'>"
//				+ "<button type='button' style='background-color:"+hashy.get("stp_color")+";' class='btn btn-light dropdown-toggle btn-sm' type=\"button\" data-bs-toggle=\"dropdown\" aria-expanded=\"false\">"+buttonText+"</button>"
//				+ " <ul class='dropdown-menu'>";
//		if (hashy.get("c_settled").equalsIgnoreCase("FULL"))
//			;
//		else {
//
//		actionMenu +="<li><a href='?myClassBean=com.app.cases.AllCasesPassedByBranch&c_id="+hashy.get("c_id")+"&op=upd' "
//				+ "class='dropdown-item'>تعديل <i class='lni lni-pencil' style='font-size: 1.1rem;vertical-align: text-bottom;'></i></a></li>";
//		}
//		actionMenu +="<li>"+audit+"</li></ul></div>";
//		
//
//		String html = "<td width='12%' ><div class='row'>"+actionMenu;
//		
//		html+= "</div></td>";
//		return html;
//	}
	@Override
	public String doUpdate(HttpServletRequest rqs, boolean autoCommit) {
		PreparedStatement pst = null; 
		ResultSet rs = null;
		String caseid = parseUpdateRqs(rqs);
		String msg = "تم التعديبل بنجاح";
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		int userid = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		double receiptAmtFromScreen = 0 ;
		HashMap <String , String> dataMapFromDB = new HashMap<String, String>();
		HashMap<String, String> updateConditionsMapFlag = new HashMap<String, String>();
		UtilitiesFeqar ut = new UtilitiesFeqar();
		boolean callChangeManifetId = false;
		try{           	
				
			//log changes
            pst = conn.prepareStatement("select * from p_cases "
            		+ "join p_caseschain on cc_caseid = c_id and cc_tobranch=? "
            		+ "where c_id =?");
            pst.setInt(1, currentBranch);
            pst.setString(2, caseid);
            rs = pst.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
            	for(int i=1 ; i<=rsmd.getColumnCount();i++) {
            		dataMapFromDB.put(rsmd.getColumnName(i), rs.getString(rsmd.getColumnName(i)));
            	}
            }
            try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception ex) {}
			
			updateConditionsMapFlag = ut.checkCaseEditeConditions(conn, "THROWMYBRANCH", currentBranch, caseid);
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
									"update", "شحنات مرت على مخزني", userid);
				}
			}
			
			String district = "";
			if (inputMap_ori.containsKey("c_rcv_district") && inputMap_ori.get("c_rcv_district")[0]!=null
					&&  !inputMap_ori.get("c_rcv_district")[0].trim().equalsIgnoreCase("")) {
				district = inputMap_ori.get("c_rcv_district")[0];
			}else {
				district = dataMapFromDB.get("c_rcv_district");
			}
			double agentShare = 0;
			if (inputMap_ori.containsKey("c_agentshare") && inputMap_ori.get("c_agentshare")!=null
					&& dataMapFromDB.get("c_specialcase").equalsIgnoreCase("Y")) {
				agentShare = Double.parseDouble(inputMap_ori.get("c_agentshare")[0]);
			}else{
				agentShare = Double.parseDouble(dataMapFromDB.get("c_agentshare"));
			}
			
			double cc_pathcost = 0;
			if (inputMap_ori.containsKey("cc_pathcost") && inputMap_ori.get("cc_pathcost")[0]!=null
					&&  !inputMap_ori.get("cc_pathcost")[0].trim().equalsIgnoreCase("") && dataMapFromDB.get("c_specialcase").equalsIgnoreCase("Y")) {
				cc_pathcost = Double.parseDouble(inputMap_ori.get("cc_pathcost")[0]);
			}else {
				cc_pathcost = Double.parseDouble(dataMapFromDB.get("cc_pathcost"));
			}
			//System.out.println("cc_pathcost = "+cc_pathcost);
			String rural = "N";
            pst = conn.prepareStatement("select cdi_rural from kbcity_district where cdi_id =? ");
            pst.setString(1, district);
            rs = pst.executeQuery();
            if (rs.next()) {
            	rural = rs.getString("cdi_rural");
            }
            try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception ex) {}
			
			if (receiptAmtIqdFromScreen != Double.parseDouble(dataMapFromDB.get("c_receiptamt")) 
					&& inputMap_ori.containsKey("c_receiptamt") && inputMap_ori.get("c_receiptamt") != null) {
	            if(!updateConditionsMapFlag.isEmpty() && updateConditionsMapFlag.get("c_receiptamt").equalsIgnoreCase("Y")) {
	            	Utilities.changeReceiptByCaseId(conn, Integer.parseInt(caseid),  receiptAmtIqdFromScreen,userid,"شحنات مرت على مخزني",
						StandardFinCurrency.IQD);
	            }
			}
			if (receiptAmtUsdFromScreen != Double.parseDouble(dataMapFromDB.get("c_receiptamt_usd")) 
					&& inputMap_ori.containsKey("c_receiptamt_usd") && inputMap_ori.get("c_receiptamt_usd") != null) {
				 if(!updateConditionsMapFlag.isEmpty() && updateConditionsMapFlag.get("c_receiptamt_usd").equalsIgnoreCase("Y")) {
					 Utilities.changeReceiptByCaseId(conn, Integer.parseInt(caseid), receiptAmtUsdFromScreen,userid, "شحنات مرت على مخزني",
						StandardFinCurrency.USD);
				 }
			}
			
			if(updateConditionsMapFlag.get("c_assignedagent").equalsIgnoreCase("Y") && inputMap_ori.containsKey("c_assignedagent") && inputMap_ori.get("c_assignedagent") != null) {
				if(Integer.parseInt(inputMap_ori.get("c_assignedagent")[0]) != Integer.parseInt(dataMapFromDB.get("c_assignedagent"))
						 && dataMapFromDB.get("q_step").equalsIgnoreCase("PRINTMANIFEST"))
					callChangeManifetId = true;
				//System.out.println("=====================callChangeManifetId===================");
			}
			String state = "";
			if (inputMap_ori.containsKey("c_rcv_state") && inputMap_ori.get("c_rcv_state")[0]!=null
					&&  !inputMap_ori.get("c_rcv_state")[0].trim().equalsIgnoreCase("")) {
				state = inputMap_ori.get("c_rcv_state")[0];
			}else {
				state = dataMapFromDB.get("c_rcv_state");
			}
            pst = conn.prepareStatement(
            		"update p_cases join p_caseschain on (c_id = cc_caseid and cc_tobranch = ?) set  "
            + " c_rcv_hp1 =?    , c_rcv_district=?, c_rural=?	   , c_rcv_addr_rmk=? , c_rmk=?, "
            + " c_agentshare = ?, cc_pathcost=?	  , c_specialcase=?, c_rcv_state=?, c_custreceiptnoori=?  "
            + " where c_id = ?");
            pst.setInt(1, currentBranch);
            pst.setString(2, inputMap_ori.get("c_rcv_hp1")[0]);
            pst.setString(3, district);
            pst.setString(4, rural);
            pst.setString(5, inputMap_ori.get("c_rcv_addr_rmk")[0]);
            if (inputMap_ori.containsKey("c_rmk")  && inputMap_ori.get("c_rmk")!=null && inputMap_ori.get("c_rmk")[0].length()>0) {
            	pst.setString(6, inputMap_ori.get("c_rmk")[0]);
            }else {
            	pst.setString(6, dataMapFromDB.get("c_rmk"));
            }
            pst.setDouble(7, agentShare);
            pst.setDouble(8, cc_pathcost);
            pst.setString(9, inputMap_ori.get("c_specialcase")[0]);
//            if (inputMap_ori.containsKey("c_assignedagent")  && inputMap_ori.get("c_assignedagent")!=null && inputMap_ori.get("c_assignedagent")[0].length()>0)
//            	pst.setString(10, inputMap_ori.get("c_assignedagent")[0]);
//            else
//            	pst.setString(10, dataMapFromDB.get("c_assignedagent"));
            pst.setString(10, state);
            if (inputMap_ori.containsKey("c_custreceiptnoori")  && inputMap_ori.get("c_custreceiptnoori")!=null
            		&& inputMap_ori.get("c_custreceiptnoori")[0].length()>0) {
            	pst.setString(11, inputMap_ori.get("c_custreceiptnoori")[0]);
            }else {
            	pst.setString(11, dataMapFromDB.get("c_custreceiptnoori"));
            }
            
            pst.setString(12, caseid);
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
            conn.commit();
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
		return caseId;
	}
	public void setCaseId(int caseId) {
		this.caseId = caseId;
	}
}

