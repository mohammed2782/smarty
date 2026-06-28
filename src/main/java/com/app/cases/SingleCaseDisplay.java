package com.app.cases;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import smarty.core.CoreMgr;
import smarty.db.mysql;

public class SingleCaseDisplay extends CoreMgr {
	
	
	public SingleCaseDisplay(int caseId) {
		MainSql= "select '' as payment_between_branchs, c_id , mcust_name , mcust_phone1, cust_name, cust_phone1, c_custid, c_rcv_hp1 as hp,    "
				+ " concat (ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) as c_rcv_addr_rmk , agentinfo.us_name as dlvagentname, "
				+ " agentinfo.us_hp as dlvagenthp, "
				+ " concat ('<img src=\"/primeimg/staff/',agentinfo.us_img,'class=\"user-img\" alt=\"user avatar\">')  as dlvagentimage,"
				+ " agentinfo.us_carplateno as  dlvagentcar , c_custhp, c_rcv_name, c_rcv_state, ifnull(c_rcv_district,'') as c_rcv_district, c_createdby, c_createddt, "
				+ " c_qty	, c_receiptamt, c_receiptamt_usd  , c_shipment_cost   ,c_assignedagent , c_rmk,  " + 
				"	c_fragile, c_branchcode	 		 , c_custreceiptnoori, c_specialcase , c_mastercustid,"
				+ " c_pickupagentpmtid, c_agentpmtid, c_advancepmtid, c_pmtid, c_cust_rtnid , c_agentrtnid, c_pickupagent_rtnid "
				+ " from p_cases "
				+ " left join kb_mastercustomer on c_mastercustid = mcust_id "
				+ " join kbcustomers on cust_id = c_custid "
				+ " left join p_caseschain on c_id = cc_caseid  "
				+ " left join kbusers agentinfo on us_id = c_assignedagent and us_rank = 'DLVAGENT'  "
				+ " left join kbcity_district on cdi_id = c_rcv_district "
				+ "where c_id ="+caseId+""
				+ "  and ( c_branchcode={userstorecode} or cc_tobranch={userstorecode}) ";
		userDefinedEditFormColNo = 3;
		setDisplayMode("DISPLAYSINGLE");
		userDefinedFieldSetCols.put("mcust_name", "معلومات المرسل");
		//userDefinedFieldSetEndWithCols.add("c_custhp");
		userDefinedGridCols.add("mcust_name");
		userDefinedGridCols.add("mcust_phone1");
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("cust_phone1");
		
		userDefinedFieldSetCols.put("c_custreceiptnoori", "معلومات الشحنة");
		//userDefinedFieldSetEndWithCols.add("c_shipment_cost");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("hp");
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("c_rcv_district");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("c_branchcode");
		userDefinedGridCols.add("c_specialcase");
		userDefinedGridCols.add("c_createdby");
		userDefinedGridCols.add("c_createddt");
		
		userDefinedFieldSetCols.put("dlvagentimage", "معلومات مندوب التوصيل");
		userDefinedGridCols.add("dlvagentimage");
		userDefinedGridCols.add("dlvagentname");
		userDefinedGridCols.add("dlvagenthp");
		userDefinedGridCols.add("dlvagentcar");
		userDefinedGridCols.add("c_agentpmtid");
		
		userDefinedFieldSetCols.put("c_pickupagentpmtid", "معلومات مالية ومخزنية");
		userDefinedGridCols.add("c_pmtid");
		userDefinedGridCols.add("c_pickupagentpmtid");
		userDefinedGridCols.add("c_agentpmtid");
		userDefinedGridCols.add("c_cust_rtnid");
		userDefinedGridCols.add("c_agentrtnid");
		userDefinedGridCols.add("c_pickupagent_rtnid");
		
		userDefinedColLabel.put("c_agentrtnid", "رقم كشف  الراجع من مندوب التوصيل");
		userDefinedColLabel.put("c_cust_rtnid", "رقم كشف الراجع للزبون");
		userDefinedColLabel.put("c_pickupagent_rtnid", "رقم كشف الراجع لمندوب الأستلام");
		
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("dlvagentimage", " ");
		userDefinedColLabel.put("dlvagentname", "مندوب التوصيل");
		userDefinedColLabel.put("dlvagenthp", "هاتف مندوب التوصيل");
		userDefinedColLabel.put("dlvagentcar", "سيارة مندوب التوصيل");
		userDefinedColLabel.put("c_specialcase", "شحنة خاصة");
		userDefinedColLabel.put("mcust_name", "العميل");
		userDefinedColLabel.put("mcust_phone1", "هاتف العميل");
		userDefinedColLabel.put("c_branchcode", "أنشأ في فرع");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("cust_phone1", "هاتف المتجر");
		userDefinedColLabel.put("c_createdby", "طلبت من خلال");
		userDefinedColLabel.put("c_mastercustid", "العميل");
		userDefinedColLabel.put("cust_name", "المتجر");
		userDefinedColLabel.put("c_pickupagentpmtid", "دفعة مندوب أستلام");
		userDefinedColLabel.put("c_agentpmtid", "دفعة مندوب توصيل");
		userDefinedColLabel.put("c_advancepmtid", "دفعة مقدمة");
		userDefinedColLabel.put("c_pmtid", "دفعة عميل");
		
		userDefinedColLabel.put("c_rcv_name", "أسم المستلم");
		userDefinedColLabel.put("hp", "هاتف");
		userDefinedColLabel.put("c_rcv_state", "المحافظه");
		userDefinedColLabel.put("c_rcv_district", "المدينه");
		
		userDefinedColLabel.put("c_rcv_addr_rmk", "وصف أدق لعنوان المستلم");
		
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل د.ع");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
		userDefinedColLabel.put("c_createddt", "تاريخ ووقت طلب الشحنه");
		
		userDefinedLookups.put("c_custid", "SELECT cust_id , cust_name FROM kbcustomers");
		userDefinedLookups.put("c_branchcode", "select branch_id , branch_name from kbbranches");
		userDefinedLookups.put("c_rcv_state", "SELECT st_code , st_name_ar FROM kbstate");
		userDefinedLookups.put("c_rcv_district", "SELECT cdi_id , cdi_name FROM kbcity_district");
		userDefinedLookups.put("c_createdby", "SELECT us_id , us_name FROM kbusers");
		userDefinedLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1= 'YESNO'");
		//userDefinedLookups.put("c_mastercustid", "select mcust_id , mcust_name from kb_mastercustomer");
		
		userDefinedCaption = "تفاصيل الشحنة";
		userModifyTD.put("payment_between_branchs", "showPaymentbBetweenBranchs({c_id})");
		userDefinedFieldSetCols.put("payment_between_branchs", "تفاصيل الدفعات بين الفروع");
		userDefinedGridCols.add("payment_between_branchs");
		userModifyTD.put("payment_between_branchs", "showPaymentbBetweenBranchs({c_id})");
		userDefinedColLabel.put("payment_between_branchs", "#DO_NOT_DISPLAY_LABEL#");
		userModifyTD.put("hp", "linkToWhatsApp({hp})");
	}
	
	public String linkToWhatsApp(HashMap<String,String> hashy) {
		//return "<td><a href=\"https://api.whatsapp.com/send?phone=00964"+hashy.get("c_rcv_hp1").replaceFirst("0", "")+"\">"+hashy.get("c_rcv_hp1")+"</a></td>";
		return "<td><a target=\"_blank\"  href=\"https://wa.me/964"+hashy.get("hp").replaceFirst("0", "")+"\">"+hashy.get("hp")+"</a></td>";
	}
	
	public String showPaymentbBetweenBranchs (HashMap<String,String> hashy) {
		Connection conn2 = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String branch_name="";
		String payment_id="";
		String rtnmanifestid="";
		String modifyTable = "<div class='col-xl-12' style='margin-top: 5px;'>"
								+"<div class='col' smarty_singledispalycol='smarty_singledispalycol_mcust_name_coldiv'>";
		int count=0;
		try {
			conn2 = mysql.getConn();
			pst = conn2.prepareStatement("SELECT branch_name,cc_branchpmtid,cc_rtnmanifestid FROM p_caseschain "
										+"left join kbbranches on branch_id=cc_tobranch "
										+"WHERE cc_caseid = ? ");
			pst.setString(1, hashy.get("c_id"));
			rs = pst.executeQuery();
			while(rs.next()) {
				count++;
				if(count==1) {
					modifyTable+="<div class='row mb-2' smarty_singledispalycol='smarty_singledispalycol_mcust_phone1_rowdiv'>"
							+"<div class='col-sm-5'><h6 class='mb-0' style='font-size: 1rem;'>الفرع</h6></div>"
							+"<div class='col-sm-4'><h6 class='mb-0' style='font-size: 1rem;'>رقم الدفعة</h6></div>"
							+"<div class='col-sm-3'><h6 class='mb-0' style='font-size: 1rem;'>رقم كشف الراجع</h6></div>"
						+"</div>";
				}
				//System.out.println("hhere is ");
				branch_name = rs.getString("branch_name");
				payment_id = rs.getString("cc_branchpmtid");
				rtnmanifestid = rs.getString("cc_rtnmanifestid");
				modifyTable+="<div class='col' smarty_singledispalycol='smarty_singledispalycol_mcust_name_coldiv'>";
				modifyTable+="<div class='row mb-2' smarty_singledispalycol='smarty_singledispalycol_mcust_phone1_rowdiv'>"
								+"<div class='col-sm-5'><h6 class='mb-0' style='font-size: 1rem;'>"+branch_name+"</h6></div>"
								+"<div class='col-sm-4'><h6 class='mb-0' style='font-size: 1rem;'>"+payment_id+"</h6></div>"
								+"<div class='col-sm-3'><h6 class='mb-0' style='font-size: 1rem;'>"+rtnmanifestid+"</h6></div>"
							+"</div>";
				modifyTable+="</div>";
			}
			if(count==0) {
				modifyTable+="<div class='row mb-2' smarty_singledispalycol='smarty_singledispalycol_mcust_phone1_rowdiv'>"
								+"<div class='col-sm-12'><h6 class='mb-0' style='font-size: 1rem;'>لا توجد تفاصيل دفعات بين الفروع</h6></div>"
							+"</div>";
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn2.close();}catch(Exception e) {}
		}
		modifyTable+="</div></div>";
		return modifyTable ;
	}
}
