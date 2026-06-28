package com.app.cust.settings;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;

import com.app.util.Utilities;

public class MasterCustomerEmp extends CoreMgr{
	public MasterCustomerEmp () {
		MainSql = "select * from kbusers  where us_mastercustid ={mastercustidlogin} and us_rank !='MASTERCUSTOMER' ";
		canEdit = true;
		canNew = true;
		canDelete = true;
		mainTable = "kbusers";
		keyCol = "us_id";
		
		userDefinedGridCols.add("us_loginid");
		userDefinedGridCols.add("us_name");
		userDefinedGridCols.add("us_rank");
		userDefinedGridCols.add("us_active");
		userDefinedGridCols.add("us_hp");
		userDefinedGridCols.add("us_workingoncustomers");
		userDefinedGridCols.add("us_createddt");
		
		userDefinedNewCols.add("us_loginid");
		userDefinedNewCols.add("us_name");
		userDefinedNewCols.add("us_password");
		userDefinedNewCols.add("us_rank");
		userDefinedNewCols.add("us_active");
		userDefinedNewCols.add("us_hp");
		userDefinedNewCols.add("us_workingoncustomers");
		
		userDefinedNewColHtmlAttr.put("us_loginid", " onfocusout='checkLoginId(\"INSERT\")' ");
		userDefinedEditColHtmlAttr.put("us_loginid", " onfocusout='checkLoginId(\"UPDATE\")' ");
		
		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
		
		userDefinedNewColsHtmlType.put("us_workingoncustomers", "CHECKBOX");
		//userDefinedNewColsDefualtValues.put("us_loginid", new String [] {" "});
		
		userDefinedLookups.put("us_active", "select kbcode, kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("us_rank", "select rank_code, rank_name_ar from kbrank where rank_formastercustomeremp='Y'");
		
		userDefinedNewColsHtmlType.put("us_rank", "DROPLIST");
		userDefinedNewColsHtmlType.put("us_active", "DROPLIST");
		userDefinedNewColsHtmlType.put("us_password", "PASSWORD");
		
		userDefinedColLabel.put("us_loginid", "معرف الدخول");
		userDefinedColLabel.put("us_name", "الإسم");
		userDefinedColLabel.put("us_rank", "المرتبة");
		userDefinedColLabel.put("us_active", "نشط");
		userDefinedColLabel.put("us_hp", "هاتف");
		userDefinedColLabel.put("us_workingoncustomers", "المتاجر التي يعمل عليها");
		userDefinedColLabel.put("us_createddt", "تاريخ الإنشاء");
		
		userDefinedEditCols.add("us_loginid");
		userDefinedEditCols.add("us_password");
		userDefinedEditCols.add("us_name");
		userDefinedEditCols.add("us_rank");
		userDefinedEditCols.add("us_active");
		userDefinedEditCols.add("us_hp");
		userDefinedEditCols.add("us_workingoncustomers");
	}
	
	@Override 
	public void initialize(HashMap smartyStateMap) {
		int masterCustIdLogin = Integer.parseInt(replaceVarsinString("{mastercustidlogin}", arrayGlobals).trim());
		userDefinedLookups.put("us_workingoncustomers", "select cust_id , cust_name from kbcustomers where cust_mastercustid="+masterCustIdLogin);
		userDefinedNewCaption  = "إنشاء موظف جديد";
		userDefinedEditCaption = "تعديل بيانات موظف";
		userDefinedCaption= "الموظفون";
		super.initialize(smartyStateMap);
	}
	
	@Override
	public String doUpdate(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg= "User Updated";
		String keyCol = parseUpdateRqs(rqs);
		PreparedStatement pst = null;
		ResultSet rs = null;
		String oldPassword="" , newPassword_MD5="";
		Utilities ut = new Utilities();
		String dataFromDB ="", dataFromScreen="";
		int masterCustIdLogin = Integer.parseInt(replaceVarsinString("{mastercustidlogin}", arrayGlobals).trim());
		int usid = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
		int userStoreCode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		String newPassword= inputMap_ori.get("us_password")[0];
		String userLoginId = "";
		try{
			userLoginId = inputMap_ori.get("us_loginid")[0];
			for(String key:inputMap_ori.keySet()) {
				dataFromDB =""; dataFromScreen="";
				if (key.startsWith("smartyhiddenedit_ori_")) {
					dataFromDB = inputMap_ori.get(key)[0];
					if (inputMap_ori.get(key.replace("smartyhiddenedit_ori_", ""))!=null
							&& inputMap_ori.get(key.replace("smartyhiddenedit_ori_", ""))[0]!=null)
						dataFromScreen = inputMap_ori.get(key.replace("smartyhiddenedit_ori_", ""))[0];
				}
        		if(!dataFromScreen.equalsIgnoreCase(dataFromDB))
        			CoreUtilities.logChanges(conn, "KBUSERS", "us_id", Integer.parseInt(keyCol), key.replace("smartyhiddenedit_ori_", ""), dataFromDB, dataFromScreen,"update", "مستخدمون النظام", usid);
			}
			if(Utilities.checkPasswordSmallOrContainSpace(inputMap_ori.get("us_password")[0]))
				throw new Exception ("قد يحوي الرمز على اقل من اربع احرف اويحوي مسافة فارغة");
			if(ut.checkUserLoginIdExistOrSmall(conn, userLoginId,"UPDATE"))
				throw new Exception ("قد يحوي  معرف الدخول  على اقل من اربع احرف اويحوي مسافة فارغة");
			
			pst = conn.prepareStatement("select us_password , MD5(?) as newpass_MD5 from kbusers where us_id=?");
			pst.setString(1, newPassword);
			pst.setString(2, keyCol);
			rs = pst.executeQuery();
			while(rs.next()){
				oldPassword = rs.getString("us_password");
				newPassword_MD5 = rs.getString("newpass_MD5");
			}
			if (!oldPassword.equals(newPassword)){
				newPassword = newPassword_MD5;
			}else{
				newPassword = oldPassword;
			}
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
			String custs = "";
			if (inputMap_ori.containsKey("us_workingoncustomers") && inputMap_ori.get("us_workingoncustomers") !=null ) {
				for (int i = 0; i<inputMap_ori.get("us_workingoncustomers").length; i++)
					custs +=inputMap_ori.get("us_workingoncustomers")[i]+":";
			}
			pst = conn.prepareStatement("update kbusers set us_loginid =?, us_password =? , us_active=? , us_p_b4_enc=?, us_rank=? ,"
					+ " us_workingoncustomers=? ,  us_name=? , us_hp=? , us_branchcode=? where us_id=?");
			pst.setString(1, userLoginId);
			pst.setString(2, newPassword);
			pst.setString(3, inputMap_ori.get("us_active")[0]);
			pst.setString(4, newPassword);
			pst.setString(5, inputMap_ori.get("us_rank")[0]);
			pst.setString(6, custs);
			pst.setString(7, inputMap_ori.get("us_name")[0]);
			pst.setString(8, inputMap_ori.get("us_hp")[0]);
			pst.setInt(9, userStoreCode);
			pst.setString(10, keyCol);
			pst.executeUpdate();
			conn.commit();
		}catch(Exception e){
			/*Log Error*/
			statusMsg = "Error at updating User "+e.getMessage();
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
	
	@Override
	public String doInsert(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg= "User Created";
		Map <String , String[]> inputMap = filterRequest (rqs);
		PreparedStatement pst = null;
		ResultSet rs = null;
		String custs = "";
		String userLoginId = "";
		Utilities ut = new Utilities();
		int masterCustIdLogin = Integer.parseInt(replaceVarsinString("{mastercustidlogin}", arrayGlobals).trim());
		int userStoreCode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		int usid = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
		if (inputMap.containsKey("us_workingoncustomers") && inputMap.get("us_workingoncustomers") !=null ) {
			for (int i = 0; i<inputMap.get("us_workingoncustomers").length; i++)
				custs +=inputMap.get("us_workingoncustomers")[i]+":";
		}
		try{
			userLoginId =inputMap.get("us_loginid")[0];
			if(Utilities.checkPasswordSmallOrContainSpace(inputMap.get("us_password")[0]))
				throw new Exception ("قد يحوي الرمز على اقل من اربع احرف اويحوي مسافة فارغة");
			if(ut.checkUserLoginIdExistOrSmall(conn, userLoginId,"INSERT"))
				throw new Exception ("قد يحوي الاسم على اقل من اربع احرف اويحوي مسافة فارغة او الاسم مستخدم مسبقاً");
			
			pst = conn.prepareStatement("insert into kbusers "
							+ "(us_loginid   		 , us_password, us_active, us_p_b4_enc	, us_rank , "
							+ " us_workingoncustomers, us_name 	  , us_hp	 , us_branchcode, us_mastercustid, "
							+ " us_createdby ) "
					+ " values (?			 		 , MD5(?)	  , ?		 , ?			, ? , "
					+ " 		? 			 		 , ?		  , ? 		 , ? 			, ?, "
					+ "         ?)", Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, userLoginId);
			pst.setString(2, inputMap.get("us_password")[0]);
			pst.setString(3, inputMap.get("us_active")[0]);
			pst.setString(4, inputMap.get("us_password")[0]);
			pst.setString(5, inputMap.get("us_rank")[0]);
			pst.setString(6, custs);
			pst.setString(7, inputMap.get("us_name")[0]);
			pst.setString(8, inputMap.get("us_hp")[0]);
			pst.setInt(9, userStoreCode);
			pst.setInt(10, masterCustIdLogin);
			pst.setInt(11, usid);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			rs.next();
			CoreUtilities.logChanges(conn, "KBUSERS", "us_id", rs.getInt(1),	 "*", 	"NONE"	, "ALL", "insert", "مستخدمون النظام", usid);
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
	public String doDelete(HttpServletRequest rqs) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean creatRecordDelTabel = false;
		boolean allowDelete = false;
		int userid = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
		int masterCustIdLogin = Integer.parseInt(replaceVarsinString("{mastercustidlogin}", arrayGlobals).trim());
		try {
			pst = conn.prepareStatement("select 1 from kbusers where us_id= ? and us_mastercustid=?");
			pst.setString(1, rqs.getParameter(keyCol));
			pst.setInt(2, masterCustIdLogin);
			rs= pst.executeQuery();
			if (rs.next()) {
				allowDelete = true;
			}
			try{pst.close();}catch(Exception e){/*ignore*/}
			try{pst.close();}catch(Exception e){/*ignore*/}
			if (allowDelete) {
				pst = conn.prepareStatement("insert into kbusers_deleted "
										+ "		select * , now(), ? from kbusers where us_id = ?");
				pst.setInt(1, userid);
				pst.setString(2, rqs.getParameter(keyCol));
				pst.executeUpdate();
				CoreUtilities.logChanges(conn, "KBUSERS", "us_id", Integer.parseUnsignedInt(rqs.getParameter(keyCol)), "*", "ALL", "NONE", "delete", "مستخدمون النظام", userid);
				conn.commit();
				creatRecordDelTabel = true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{pst.close();}catch(Exception e){/*ignore*/}
		}
		if(creatRecordDelTabel && allowDelete)
			return super.doDelete(rqs);
		else
			return "Error";
	}
}
