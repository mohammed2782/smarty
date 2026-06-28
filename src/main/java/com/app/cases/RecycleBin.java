package com.app.cases;

import javax.servlet.http.HttpServletRequest;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;


public class RecycleBin extends CoreMgr{
	protected String QueueDataSpanClass = "QueueDataSpanClass";
	MasterCaseInformation caseMaster = new MasterCaseInformation();

	/**
	 * 
	 */
	public RecycleBin (){
//(c_belongtostore='{userstorecode}' or '{superRank}'='Y')
		MainSql = "select  '' as restore , c_deletedby,c_deleteddt,  c_createdby, q_stage, q_step, stp_name, date(c_createddt) as c_createddt , c_id  , c_custid, "
					  + " c_rcv_name 	 , c_rcv_hp1      , c_rcv_state		 , "
					  + " c_rural        , concat (ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk, c_rmk     		 , "
					  + " c_qty		     , c_receiptamt  , c_shipment_cost   ,c_assignedagent , "
					  + " c_fragile	   , "
					  + " c_branchcode	 		 , c_custreceiptnoori, c_specialcase,"
					  + "  '' as others  , '' as custname    , "
					  + " '' as custprimaryphone, c_settled , c_rcv_district "
					  + " from p_cases_deleted "
					  + " join kbstep on stp_code= q_step "
					  + " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					  + " where q_status !='CLS' and c_branchcode = {userstorecode} ";
		

		mainTable = "p_cases_deleted";
		keyCol = "c_id";
		//orderByCols = "c_rcv_city, c_id desc";
		canFilter = true;
		//canEdit   = true;
		//canDelete = true;		
		//canExport = true;
		//pdfExport = true;
		userDefinedCaption = "عرض كل الشحنات الممسوحه";
		
		userDefinedGridCols.add("restore");
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_rcv_state");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("stp_name");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("c_rural");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_assignedagent");
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("c_fragile");
		userDefinedGridCols.add("c_createdby");
		userDefinedGridCols.add("c_specialcase");
		userDefinedGridCols.add("c_deletedby");
		userDefinedGridCols.add("c_deleteddt");
		
		
		userDefinedColLabel.put("custname", "إسم صاحب المحل");
		userDefinedColLabel.put("c_deletedby", "مسح من خلال");
		userDefinedColLabel.put("c_deleteddt", "تاريخ المسح");
		userDefinedColLabel.put("c_specialcase", "شحنه خاصه؟");
		userDefinedColLabel.put("stp_name", "المرحله");
		userDefinedColLabel.put("c_custid", "إسم صاحب المحل");
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
		userDefinedColLabel.put("c_id","رقم الشحنه");
		userDefinedColLabel.put("q_step","المرحله");
		userDefinedColLabel.put("c_createdby","المدخل");
		userDefinedColLabel.put("c_settled","تم التحاسب ؟");
		userDefinedColLabel.put("c_rcv_district", "المنظقه");
		userDefinedColLabel.put("c_agentshare", "مبلغ الشحن للمندوب");
		userDefinedColLabel.put("restore","إسترجاع");
		
		userDefinedFilterCols.add("c_custid");
		userDefinedFilterColsHtmlType.put("c_custid", "DROPLIST");
		userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers order by cust_name asc");
		userDefinedLookups.put("c_rcv_district", "select cdi_id, cdi_name from kbcity_district");
		userDefinedLookups.put("c_specialcase", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' ");
		userDefinedLookups.put("c_deletedby", "select us_id, us_name from kbusers");
		userModifyTD.put("restore", "showRestoreBtn({c_id})");
		
		userDefinedFilterCols.add("c_rcv_hp1");
		userDefinedFilterCols.add("c_id");
		
		userDefinedFilterCols.add("c_createddt");
		userDefinedFilterCols.add("c_rcv_state");
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("q_step");
		userDefinedFilterCols.add("c_settled");
		
		userDefinedFilterColsHtmlType.put("c_createddt", "DATE");
		userDefinedColLabel.put("c_createddt","تاريخ الشحنه");

		userDefinedLookups.put("custname", "select cust_id , cust_name from kbcustomers order by cust_name ASC");
		userDefinedLookups.put("custprimaryphone", "!select c_primaryHP as ph, c_primaryHP from kbcustomers where cust_id = '{custname}'");
		userDefinedLookups.put("c_rcv_state", "select st_code , st_name_ar from kbstate  order by st_order");
		
		userDefinedLookups.put("c_fragile", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_rural", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("c_settled", "select kbcode , kbdesc from kbgeneral where kbcat1='SETTLED'");
		
		userDefinedLookups.put("q_step", "select stp_code , stp_name from kbstep");
		userDefinedLookups.put("c_assignedagent", "select us_id , us_name from kbusers  ");
		
		userDefinedColsMustFill.add("custname");
		userDefinedColsMustFill.add("c_rcv_hp1");
		
		userDefinedColsMustFill.add("c_qty");
		userDefinedColsMustFill.add("c_receiptamt");
		userDefinedColsMustFill.add("c_custreceiptnoori");
		userDefinedColsMustFill.add("c_shipment_cost");
		userDefinedColsMustFill.add("c_agentshare");
		userDefinedColsMustFill.add("c_rcv_state");
		
		UserDefinedPageRows = 50;
		userModifyTD.put("stp_name", "modifyStepName({stp_name},{c_settled},{q_stage},{q_step})");
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		userDefinedUseDataTables = true;
		
	}//end of no-arg constructor Updatecase
	
	
	@Override
	public void initialize(HashMap smartyStateMap){
		if (httpSRequest.getParameter("dorestore")!=null 
				&& httpSRequest.getParameter("dorestore").equalsIgnoreCase("restore")) {
			Connection conn2 = null;
			PreparedStatement pst = null;
			
			try {
				conn2 = mysql.getConn();
				String c_id = httpSRequest.getParameter("c_id");
				pst = conn2.prepareStatement("insert into p_cases "
						+ " select * from p_cases_deleted where c_id =?");
				pst.setString(1, c_id);
				pst.executeUpdate();
				try {pst.close();} catch (Exception e) {}
				
				pst = conn2.prepareStatement("delete from p_cases_deleted where c_id =?");
				pst.setString(1, c_id);
				pst.executeUpdate();
				conn2.commit();
			}catch(Exception e) {
				
				e.printStackTrace();
				try {conn2.rollback();}catch(Exception eRoll) {}
			}finally {
				
				try {pst.close();} catch (Exception e) {}
				try {conn2.close();} catch (Exception e) {}
				
			}
		}
		
		
		super.initialize(smartyStateMap);
	}
	
	
	public String showRestoreBtn (HashMap<String,String> hashy) {
		
		return "<td align='center' style='vertical-align: middle;'>"
				+ "<a href='?myClassBean=com.app.cases.RecycleBin&c_id="+hashy.get("c_id")+"&dorestore=restore' class='btn btn-edit btn-xs'>"
					+ "<li class='fa fa-undo'></li></a></td>";
	}
	
	
	public String modifyStepName(HashMap<String,String>hashy) {
		String desc = hashy.get("stp_name");
		String color = "";
		if (hashy.get("q_stage").equalsIgnoreCase("cncl")){
			 
			 color = "background-color:red;color:white;";
		}else if(hashy.get("q_step").equalsIgnoreCase("delivered")) {
			if (hashy.get("c_settled").equalsIgnoreCase("FULL")) {
				desc += "</br>تم التحاسب";
				color = "background-color:green;color:white;";
			}else {
				desc += "</br>لم يتم التحاسب";
				color = "background-color:blue;color:white;";
			} 
		}else {
			;
		}
		String html = "<td style='"+color+"'>"+desc;
		
		html+= "</td>";
		return html;
	}
	
}//end of class RecycleBin
