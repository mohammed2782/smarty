/* class description: used to setup customers informations,
 * created by: lina - SMARTYJ FrameWork team member,
 * created date: 22/4/2018 7:26 AM.
 */
package com.app.cust.settings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;

public class setup_customers extends CoreMgr {
	
	public boolean getCanCreatNew(Connection conn, int userid, int masterCustId) throws Exception{
		boolean canNew = false;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			//System.out.println("masterCustId = "+masterCustId);
			pst = conn.prepareStatement("select count(*) as totcraeted "
					+ "from kbcustomers join kbusers on (cust_createdby = us_id and us_id = ? and us_mastercustid = cust_mastercustid) "
					+ " where cust_mastercustid = ?");
			pst.setInt(1, userid);
			pst.setInt(2, masterCustId);
			rs = pst.executeQuery();
			if (rs.next())
				if(rs.getInt("totcraeted")<10)
					canNew = true;
			//System.out.println(rs.getInt("totcraeted"));
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return canNew;
	}
	public setup_customers(){		
		
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */	
		MainSql ="select kbcustomers.* , '' actions From kbcustomers"
				+ " where cust_mastercustid={mastercustidlogin}";
		mainTable ="kbcustomers";
		keyCol = "cust_id";
		orderByCols = "cust_id";
		
		/*
		 * to define user grid view caption
		 */
		userDefinedCaption = "إعدادت المتاجر";
		userDefinedNewCaption = "إضافة بيانات متجر";
		userDefinedEditCaption = "تعديل بيانات متجر";
		
		/*
		 * to enable/disable basic operations 
		 */		
		search_paramval = null;
		
		canEdit = true;
		canDelete = false;
		canFilter = true;
		//clickableRow =true;

		/*
		 * to define grid view columns that want to show to user
		 */
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("cust_phone1");
		userDefinedGridCols.add("cust_phone2");
		//userDefinedGridCols.add("specialprice");
		userDefinedGridCols.add("cust_createdby");
		userDefinedGridCols.add("cust_createddt");
		//userDefinedGridCols.add("movecasestocustomer");
		
		userDefinedGridCols.add("actions");
		

		/*
		 * to define grid view label that want to show to user
		 */
		userDefinedColLabel.put("cust_name", "إسم المتجر");
		userDefinedColLabel.put("changeShipmetCost", "تغيير مبلغ الشحنات باٌثر رجعي");
		userDefinedColLabel.put("movecasestocustomer", "دمج");
	    userDefinedColLabel.put("cust_phone1", "رقم الهاتف");
	    userDefinedColLabel.put("cust_phone2", "رقم هاتف أخر");
	    userDefinedColLabel.put("cust_createdby", "أنشئ بواسطة");
	    userDefinedColLabel.put("cust_createddt", "تاريخ الإنشاء");
	    userDefinedColLabel.put("cust_branch", "تابع لمخزن");
	    userDefinedColLabel.put("cust_discount_bgd", "تخفيض (بغداد)");
	    userDefinedColLabel.put("cust_discount_otherstates", "تخفيض محافظات");
	    userDefinedColLabel.put("popUpBook", "طباعة فواتير");
	    userDefinedColLabel.put("actions", " ");
	    userDefinedColLabel.put("specialprice", "إسعار خاصه");
	   
	    userDefinedColLabel.put("cust_showoldreceipts", "أظهار الحسابات القديمه؟");
	    
	    /*userModifyTD.put("popUpBook", "ShowBooks({cust_id})");
	    userModifyTD.put("specialprice", "ShowSpecialPricesList({cust_id})");
	    userModifyTD.put("movecasestocustomer", "ShowMoveCasesPopUp({cust_id})");*/
	    //userModifyTD.put("actions", "showActions({cust_id},{cust_mastercustid})");
	    
	    /*
		 * to define user must fill columns 
		 */
	    userDefinedColsMustFill.add("cust_name");
	    userDefinedColsMustFill.add("cust_phone1");

	    
	    /*
		 * to define new columns for insert operation
		 */ 
	    userDefinedNewCols.add("cust_mastercustid");
		userDefinedNewCols.add("cust_name");
	    userDefinedNewCols.add("cust_phone1");
	    userDefinedNewCols.add("cust_phone2");
	    userDefinedNewCols.add("cust_createdby");
	    
	    userDefinedReadOnlyNewCols.add("cust_createdby");
	    userDefinedReadOnlyNewCols.add("cust_mastercustid");
	    
	    userDefinedLookups.put("cust_createdby", "select us_id, us_name from kbusers");
	    userDefinedLookups.put("cust_mastercustid", "select mcust_id, mcust_name from kb_mastercustomer");
	    
	    userDefinedColLabel.put("cust_mastercustid", "العميل");
	    
	    
		userDefinedNewColsDefualtValues.put("cust_createdby", new String[] {"{userid}"});
		userDefinedNewColsDefualtValues.put("cust_branch", new String[] {"{userstorecode}"});
		userDefinedNewColsDefualtValues.put("cust_mastercustid", new String[] {"{mastercustidlogin}"});
        userDefinedReadOnlyNewCols.add("cust_branch");
        
	    /*
		 * to define filter columns for search operation
		 */
		
        userDefinedNewColsHtmlType.put("cust_phone1", "PHONE");
        userDefinedNewColsHtmlType.put("cust_phone2", "PHONE");
        userDefinedNewColsHtmlType.put("cust_mastercustid", "DROPLIST");
        userDefinedNewColsHtmlType.put("cust_name", "TEXT");
		userDefinedFilterColsHtmlType.put("cust_name", "DROPLIST");
		userDefinedFilterCols.add("cust_phone1");
		userDefinedFilterCols.add("cust_phone2");
		
		userDefinedFilterCols.add("cust_name");
		
		userDefinedLookups.put("cust_name", "select cust_name, cust_name from kbcustomers where cust_mastercustid={mastercustidlogin}");
		/*
		 * to define edit columns for update operation
		 */	
		userDefinedEditCols.add("cust_name");
		userDefinedEditCols.add("cust_phone1");
		userDefinedEditCols.add("cust_phone2");
		
		
		/*
		 * to pop up sub menu for main menu
		 */
		//userDefinedGlobalClickRowID="cust_id";

	}//end of constructor setup_customers
	
	public String showActions(HashMap<String,String> hashy) {
		String html = "";
		html = "<td><div class='row'>";
		html +="<div class='col-4'><button type=\"button\" class=\"btn btn-sm btn-dark\" onclick=\"changeCustomerShipmentsCostBackDated("+hashy.get("cust_id")+");\">تعديل إسعار النقل بأثر رجعي</button></div>";
		html +="<div class='col-3'><button type=\"button\" class=\"btn btn-sm btn-info\" onclick=\"popitup ('reassignCustomerCasesPopUp?custidreassign="+hashy.get("cust_id")+"&mastercustidreassign="+hashy.get("cust_mastercustid")+"' , '' , 1000 ,600);\">دمج مع متجر</button></div>";
		html +="<div class='col-3'><button type=\"button\" class=\"btn btn-sm btn-success\" onclick=\"popitup ('mergeCustWithAnotherMasterPopUp?mergCustIdWithAnotherMasterId="+hashy.get("cust_id")+"&masterCustOutOff="+hashy.get("cust_mastercustid")+"' , '' , 1000 ,600);\">دمج مع عميل</button></div>";
		html +="<div class='col-4' style='margin-top:5px;'><button type=\"button\" class=\"btn btn-sm btn-danger\" onclick=\"popitup ('specialPricesCustomersPopUp?custidspecialprice="+hashy.get("cust_id")+"' , '' , 1000 ,600);\">إسعار خاصه</button></div>";
		html +="<div class='col-4' style='margin-top:5px;'><button type=\"button\" class=\"btn btn-sm btn-warning\" onclick=\"popitup ('CustomerEmployeePopUp?custIdEmployee="+hashy.get("cust_id")+"' , '' , 1000 ,600);\">الموظفين</button></div>";
		
		
		html +="</div></td>";
		return html;
	}
	
	public String changeCustomerShipmentsBackDated(HashMap<String,String> hashy) {
		String html = "";
		html = "<td>";
		html +="<button type=\"button\" class=\"btn btn-sm btn-dark\" onclick=\"changeCustomerShipmentsCostBackDated("+hashy.get("cust_id")+");\">تعديل إسعار النقل بأثر رجعي</button>";
		html +="</td>";
		return html;
	}
	public String ShowMoveCasesPopUp(HashMap<String, String>hashy) {
		String html = "";
		html = "<td>";
		html +="<button type=\"button\" class=\"btn btn-sm btn-info\" onclick=\"popitup ('reassignCustomerCasesPopUp?custidreassign="+hashy.get("cust_id")+"' , '' , 1000 ,600);\">دمج مع متجر</button>";
		html +="</td>";
		return html;
	}
	
	public String ShowBooks(HashMap<String, String>hashy) {
		String html = "";
		
			html = "<td>";
			html +="<button type=\"button\" class=\"btn btn-sm btn-warning\" onclick=\"popitup ('custBookPopUp?custidbook="+hashy.get("cust_id")+"' , '' , 1000 ,600);\">الإيصالات</button>";
			html +="</td>";
			return html;
		
	}
	
	public String ShowSpecialPricesList(HashMap<String, String>hashy) {
		String html = "";
		
			html = "<td>";
			html +="<button type=\"button\" class=\"btn btn-sm btn-danger\" onclick=\"popitup ('specialPricesCustomersPopUp?custidspecialprice="+hashy.get("cust_id")+"' , '' , 1000 ,600);\">إسعار خاصه</button>";
			html +="</td>";
			return html;
		
	}
	
	@Override
	public void initialize(HashMap smartyStateMap) {
		int masterCustId = Integer.parseInt(replaceVarsinString(" {mastercustidlogin} ", arrayGlobals).trim());
		int userid = Integer.parseInt(replaceVarsinString(" {usid} ", arrayGlobals).trim());
		Connection conn = null;
		String masterCustName = "";
		Utilities ut = new Utilities();
		try {
			conn = mysql.getConn();
			masterCustName = ut.getMasterCustomerName(conn, masterCustId);
			canNew = getCanCreatNew(conn, userid, masterCustId);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {}
		}
		userDefinedCaption = "متاجر العميل - "+masterCustName;
		super.initialize(smartyStateMap);
	}
	
	@Override
	public String doDelete(HttpServletRequest rqs){
		String Msg ="";
		PreparedStatement pst = null;
		ResultSet rs = null;
		String keyVal= rqs.getParameter(keyCol);
		String userid = replaceVarsinString(" {userid} ", arrayGlobals).trim();
		Connection conn = null;
		boolean allowDelete = false;
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select 1 from p_cases where cust_custid = ?");
			pst.setString(1, keyVal);
			rs = pst.executeQuery();
			if (rs.next()) 
				allowDelete = false;
			else
				allowDelete = true;
			
			
			try {pst.close();}catch(Exception e) {}
			if (!allowDelete)
				return "هذا الزبون لا يمكن مسحه لأن لديه شحنات";
			else {
				pst = conn.prepareStatement("insert into deleted_customers select kbcustomers.* , ?, now() from kbcustomers where cust_id = ?");
				pst.setString(1,userid );
				pst.setString(2,keyVal );
				pst.executeUpdate();
				try {pst.close();}catch(Exception e) {}
				pst = conn.prepareStatement("delete from kbcustomers where cust_id = ?");
				pst.setString(1, keyVal);
				pst.executeUpdate();
				conn.commit();
			}
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
	
	@Override
	public String doUpdate(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg= "User Updated";
		String keyCol = parseUpdateRqs(rqs);
		PreparedStatement pst = null;	
		try{
			
			pst = conn.prepareStatement("update kbcustomers set cust_name =?, cust_phone1 =? , cust_phone2 =?  where cust_id=?");
			pst.setString(1, inputMap_ori.get("cust_name")[0]);
			pst.setString(2, inputMap_ori.get("cust_phone1")[0]);
			pst.setString(3, inputMap_ori.get("cust_phone2")[0]);
			pst.setString(4, keyCol);
			pst.executeUpdate();
			conn.commit();
		}catch(Exception e){
			/*Log Error*/
			statusMsg = "Error at updating User "+e.getMessage();
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){
				
			}
		}finally{
			try{pst.close();}catch(Exception e){/* ignore*/}
		}
		return statusMsg;
	}
}//end of class setup_customers