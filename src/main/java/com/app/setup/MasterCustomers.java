package com.app.setup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;

import com.app.util.Utilities;

public class MasterCustomers extends CoreMgr{
	public MasterCustomers() {
		MainSql = "select kb_mastercustomer.*, '' as actions from kb_mastercustomer "
				+ "where (mcust_branchcode={userstorecode}) order by mcust_id desc ";
		canDelete = true;
		canEdit = true;
		canNew = true;
		canFilter = true;
		mainTable= "kb_mastercustomer";
		keyCol = "mcust_id";
		
		userDefinedFilterCols.add("mcust_id");
		userDefinedFilterCols.add("mcust_phone1");
		userDefinedFilterCols.add("mcust_active");
		userDefinedFilterCols.add("mcust_allowlogin");
		userDefinedFilterCols.add("mcust_pickupagent");
		
		userDefinedGridCols.add("mcust_name");
		userDefinedGridCols.add("mcust_phone1");
		userDefinedGridCols.add("mcust_createddt");
		userDefinedGridCols.add("mcust_createdby");
		userDefinedGridCols.add("mcust_active");
		userDefinedGridCols.add("mcust_allowlogin");
		userDefinedGridCols.add("mcust_pickupagent");
		userDefinedGridCols.add("actions");
		
		userDefinedLookups.put("mcust_id", "SELECT mcust_id, mcust_name FROM kb_mastercustomer where (mcust_branchcode={userstorecode})");
		userDefinedFilterColsHtmlType.put("mcust_id", "DROPLIST");
	
		userDefinedLookups.put("mcust_pickupagent", "SELECT us_id, us_name FROM kbusers where us_rank = 'PICKUPAGENT' "
				+ "and us_active = 'Y' and us_branchcode = {userstorecode}");
		userDefinedLookups.put("mcust_allowlogin", "select kbcode, kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("mcust_active", "select kbcode, kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedNewColsHtmlType.put("mcust_phone1", "PHONE");
		userDefinedNewColsHtmlType.put("mcust_pickupagent", "DROPLIST");
		
		userDefinedColLabel.put("mcust_id", "أسم العميل");
		userDefinedColLabel.put("mcust_name", "أسم العميل");
		userDefinedColLabel.put("mcust_phone1", "هاتف العميل");
		userDefinedColLabel.put("mcust_createddt", "تاريخ الإنشاء");
		userDefinedColLabel.put("mcust_createdby", "أنشأ بواسطة");
		userDefinedColLabel.put("mcust_active", "نشط");
		userDefinedColLabel.put("mcust_allowlogin", "يسمح له بالدخول للنظام");
		userDefinedColLabel.put("mcust_pickupagent", "مندوب الأستلام");
		userDefinedColLabel.put("mcust_pwd", "كلمة المرور");
		
		userDefinedEditCols.add("mcust_name");
		userDefinedEditCols.add("mcust_phone1");
		userDefinedEditCols.add("mcust_pwd");
		userDefinedEditCols.add("mcust_active");
		userDefinedEditCols.add("mcust_allowlogin");
		userDefinedEditCols.add("mcust_pickupagent");
		
		userDefinedColsMustFill.add("mcust_pwd");
		userDefinedNewColsHtmlType.put("mcust_pwd" , "PASSWORD");
		
		userDefinedNewCols.add("mcust_name");
		userDefinedNewCols.add("mcust_phone1");
		userDefinedNewCols.add("mcust_pwd");
		userDefinedNewCols.add("mcust_pickupagent");
		clickableRow = true;
		userDefinedGlobalClickRowID="mcust_id";
		
		userDefinedCaption = "العملاء";
		userModifyTD.put("actions", "showActions({mcust_id})");
	}
	public String showActions(HashMap<String,String> hashy) {
		String html = "";
		html = "<td><div class='row'>";
		html +="<div class='col-6' style='margin-top:5px;'><button type=\"button\" class=\"btn btn-sm btn-danger\""
				+ " onclick=\"popitup ('specialPricesMasterCustomersPopUp?mastercustspecialprice="+hashy.get("mcust_id")+"' , '' , 1000 ,600);\">إسعار خاصه</button></div>";
		html += "<div class='col-sm-4' style='margin-top:5px;'><button type=\"button\" class=\"btn btn-sm btn-warning\" onclick=\"popitup ('CustomerEmployeePopUp?mastercustidlogin="
				+ hashy.get("mcust_id") + "' , '' , 1000 ,600);\">الموظفين</button></div>";
		html +="</div></td>";
		return html;
	}
	@Override
	public String doDelete(HttpServletRequest rqs){
		String Msg ="";
		PreparedStatement pst = null;
		ResultSet rs = null;
		String keyVal= rqs.getParameter(keyCol);
		String userid = replaceVarsinString(" {usid} ", arrayGlobals).trim();
		boolean allowDelete = false;
		try {
			pst = conn.prepareStatement("select 1 from p_cases where c_mastercustid = ?");
			pst.setString(1, keyVal);
			rs = pst.executeQuery();
			if (rs.next()) 
				allowDelete = false;
			else
				allowDelete = true;
			
			try {pst.close();}catch(Exception e) {}
			if (!allowDelete)
				return "هذا العميل لا يمكن مسحه لأن لديه شحنات";
			else {
				pst = conn.prepareStatement("insert into deleted_mastercustomer select kb_mastercustomer.* , ?, now() from kb_mastercustomer where mcust_id = ?");
				pst.setString(1,userid );
				pst.setString(2,keyVal );
				pst.executeUpdate();
				try {pst.close();}catch(Exception e) {}
				pst = conn.prepareStatement("delete from kb_mastercustomer where mcust_id = ?");
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
	public String doInsert(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg= "User Created";
		Map <String , String[]> inputMap = filterRequest (rqs);
		PreparedStatement pst = null;
		ResultSet rs = null;
		String states = "";
		Utilities ut = new Utilities();
		int masterCustId = 0, masterUserId=0;
		int usid = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
		int userStoreCode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		int pickUpAgent= 0;
		try{
			if (inputMap.containsKey("mcust_pickupagent"))
				if (inputMap.get("mcust_pickupagent")[0]!= null && !inputMap.get("mcust_pickupagent")[0].equalsIgnoreCase("")) {
					pickUpAgent = Integer.parseInt(inputMap.get("mcust_pickupagent")[0]);
				}
			if(Utilities.checkPasswordSmallOrContainSpace(inputMap.get("mcust_pwd")[0]))
				throw new Exception ("قد يحوي الرمز على اقل من اربع احرف اويحوي مسافة فارغة");
			//creat the customer 
			pst = conn.prepareStatement("insert into kb_mastercustomer "
							+ "(mcust_name   	, mcust_phone1	 , mcust_pwd, mcust_pickupagent, mcust_pwdb4enc , "
							+ " mcust_branchcode, mcust_createdby ) "
					+ " values (?			 	, ?		 		 , MD5(?)	, ? 			   , ? 			 , "
					+ "         ?			 	, ?)", Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, inputMap.get("mcust_name")[0]);
			pst.setString(2, inputMap.get("mcust_phone1")[0]);
			pst.setString(3, inputMap.get("mcust_pwd")[0]);
			pst.setInt(4, pickUpAgent);
			pst.setString(5, inputMap.get("mcust_pwd")[0]);
			pst.setInt(6,userStoreCode);
			pst.setInt(7, usid);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			rs.next();
			masterCustId = rs.getInt(1);
			CoreUtilities.logChanges(conn, "KB_MASTERCUSTOMER", "mcust_id", masterCustId,	 "*", 	"NONE"	, "ALL", "insert", "العملاء", usid);
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
			// now create user
			pst = conn.prepareStatement("insert into kbusers "
					+ "(us_loginid, us_password, us_rank, us_branchcode, us_mastercustid , us_createdby) "
			+ " values (?		  , MD5(?)	   , ? 		, ? 		   , ?			 	 , ?)", Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, inputMap.get("mcust_phone1")[0]);
			pst.setString(2, inputMap.get("mcust_pwd")[0]);
			pst.setString(3, "MASTERCUSTOMER");
			pst.setInt(4, userStoreCode);
			pst.setInt(5,masterCustId);
			pst.setInt(6, usid);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			rs.next();
			masterUserId = rs.getInt(1);
			CoreUtilities.logChanges(conn, "KBUSERS", "us_id", masterUserId,	 "*", 	"NONE"	, "ALL", "insert", "العملاء", usid);
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
			// update the userid to the master customer table
			pst = conn.prepareStatement("update kb_mastercustomer set mucst_userid=? where mcust_id=?");
			pst.setInt(1, masterUserId);
			pst.setInt(2, masterCustId);
			pst.executeUpdate();
			
			conn.commit();
		}catch(Exception e){
			/*log error*/
			statusMsg = "Error at user creation, error ("+e.getMessage()+")";
			try{conn.rollback();}catch(Exception ignoreE){}
			e.printStackTrace();
		}finally{
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
		}
		
		return statusMsg;
	}
	
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean autoCommit) {
		String statusMsg= "تم تحديث معلومات العميل";
		String keyCol = parseUpdateRqs(rqs);
		PreparedStatement pst = null;
		ResultSet rs = null;
		Utilities ut = new Utilities();
		String oldPassword="" , newPassword_MD5="";
		String newPassword=inputMap_ori.get("mcust_pwd")[0];
		
		boolean userCreated = false;
		int userStoreCode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		int usid = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
		int userId = 0, pickUpAgent= 0;
		try{
			if (inputMap_ori.containsKey("mcust_pickupagent"))
				if (inputMap_ori.get("mcust_pickupagent")[0]!= null && !inputMap_ori.get("mcust_pickupagent")[0].equalsIgnoreCase("")) {
					pickUpAgent = Integer.parseInt(inputMap_ori.get("mcust_pickupagent")[0]);
				}
			pst = conn.prepareStatement("select us_id from kbusers where us_mastercustid=? and us_rank = 'MASTERCUSTOMER' and us_branchcode=?");
			pst.setString(1, keyCol);
			pst.setInt(2, userStoreCode);
			rs = pst.executeQuery();
			if (rs.next()) {
				userId = rs.getInt("us_id");
			
			}
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
			if(Utilities.checkPasswordSmallOrContainSpace(newPassword))
				throw new Exception ("قد يحوي الرمز على اقل من اربع احرف اويحوي مسافة فارغة");
			
			if (userId>0) {
				pst = conn.prepareStatement("select us_password , MD5(?) as newpass_MD5 from kbusers  where us_id=?");
				pst.setString(1, newPassword);
				pst.setInt(2, userId);
				rs = pst.executeQuery();
				if (rs.next()) {
					if (rs.getString("us_password").equalsIgnoreCase(rs.getString("newpass_MD5"))) {
						//passwordB4Enc = rs.getString("cust_pwdb4enc");
						newPassword_MD5 = rs.getString("us_password");
					}else {
					
						//password changed
						//passwordB4Enc = newPassword;
						newPassword_MD5 = rs.getString("newpass_MD5");
					}
				}
			}else {
				pst = conn.prepareStatement("insert into kbusers "
						+ "(us_loginid, us_password, us_rank, us_branchcode, us_mastercustid , us_createdby) "
				+ " values (?		  , ?	   , ? 		, ? 		   , ?			 	 , ?)", Statement.RETURN_GENERATED_KEYS);
				pst.setString(1,  inputMap_ori.get("mcust_phone1")[0]);
				pst.setString(2, newPassword_MD5);
				pst.setString(3, "MASTERCUSTOMER");
				pst.setInt(4, userStoreCode);
				pst.setString(5,keyCol);
				pst.setInt(6, usid);
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				rs.next();
				userId = rs.getInt(1);
				CoreUtilities.logChanges(conn, "KBUSERS", "us_id", userId,	 "*", 	"NONE"	, "ALL", "update", "العملاء", usid);
				userCreated = true;
			}
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
			
			if (!userCreated) {
				pst = conn.prepareStatement("update kbusers set us_password =?, us_loginid=?  where us_id = ? ");
				pst.setString(1, newPassword_MD5);
				pst.setString(2, inputMap_ori.get("mcust_phone1")[0]);
				pst.setInt(3, userId);
				pst.executeUpdate();
			
				try{rs.close();}catch(Exception e){/* ignore*/}
				try{pst.close();}catch(Exception e){/* ignore*/}
			}
			
			pst = conn.prepareStatement("update kb_mastercustomer set mcust_name =?, mcust_phone1 =? , mcust_pwd =? , mcust_active=? "
					+ " ,mcust_allowlogin=?, mcust_pickupagent=? where mcust_id=?");
			pst.setString(1, inputMap_ori.get("mcust_name")[0]);
			pst.setString(2, inputMap_ori.get("mcust_phone1")[0]);
			pst.setString(3, newPassword_MD5);
			pst.setString(4, inputMap_ori.get("mcust_active")[0]);
			pst.setString(5, inputMap_ori.get("mcust_allowlogin")[0]);
			pst.setInt(6, pickUpAgent);
			pst.setString(7, keyCol);
			pst.executeUpdate();
			conn.commit();
		}catch(Exception e){
			/*Log Error*/
			statusMsg = "خطأ في تعديل بيانات العميل "+e.getMessage();
			e.printStackTrace();
			try{
				conn.rollback();
			}catch(Exception eRoll){
				/*ignore*/
			}
		}finally{
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
		}
		return statusMsg;
	}
}
