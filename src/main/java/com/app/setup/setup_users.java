package com.app.setup;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;
import smarty.core.smartyLogAndErrorHandling;
import smarty.db.mysql;

import com.app.util.Utilities;
import com.app.util.UtilitiesNafie;

public class setup_users extends CoreMgr{
	public setup_users(){
		MainSql = "select us_hp,'' as customer, us_id,us_createddt, us_loginid, us_password, us_active,"
				+ " us_p_b4_enc, us_rank, us_img, us_img as showimg ,  "
				+ " us_identityimg, us_identityimg as idimage, us_residenceimg, us_residenceimg as residenceimg, "
				+ " us_contractfile, us_contractfile as contractfile, "
				+ " us_name, ifnull(us_branchcode,'') as us_branchcode from kbusers where "
				+ " us_rank not in ('DLVAGENT', 'LIAISONAGENT' , 'PICKUPAGENT','MASTERCUSTOMER','FOLLOWUP_EMP','SUPPLY_EMP',"
				+ "'ITBOSS', 'SYSMANAGER') "
				+ " and us_branchcode='{userstorecode}' ";
		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";		
		mainTable = "kbusers";
		keyCol   = "us_id";
		orderByCols = " us_id desc ";
		canNew = true;
		canFilter =  true;
		canEdit = true;
		canDelete = true;

	   userDefinedCaption = "إعدادات الموظفين";
	   userDefinedNewCaption = "إضافة بيانات موظف";
	   userDefinedUpdateCaption = "تعديل بيانات موظف";
			
	   userDefinedGridCols.add("us_loginid");
	   userDefinedGridCols.add("us_name");
	   userDefinedGridCols.add("us_rank");
	   userDefinedGridCols.add("us_active");
	   userDefinedGridCols.add("us_hp");
	   userDefinedGridCols.add("us_branchcode");
	   userDefinedGridCols.add("us_createddt");
	
	   userDefinedColLabel.put("us_loginid", "user id");
	   userDefinedColLabel.put("us_name", "إسم المستخدم");
		userDefinedColLabel.put("us_branchcode", "الفرع");
		userDefinedColLabel.put("us_from_state", "من محافظه");
		userDefinedColLabel.put("us_password", "كلمة المرور");
		userDefinedColLabel.put("us_storecode", "يعمل في مخزن");
		userDefinedColLabel.put("us_rank", "إسم المرتبة");
		userDefinedColLabel.put("us_active", "نشط");
		userDefinedColLabel.put("us_createddt", "تاريخ الإنشاء");
		userDefinedColLabel.put("us_lastlogindt", "تاريخ أخر دخول");
		userDefinedColLabel.put("us_hp", "هاتف");
		userDefinedColLabel.put("us_img", "صورة - الحجم لا يحب ان يتجاوز 300 م.ب");
		userDefinedColLabel.put("showimg", "صورة");
		userDefinedColLabel.put("idimage","هوية");
		userDefinedColLabel.put("residenceimg", "بطاقة سكن");
		userDefinedColLabel.put("contractfile", "العقد");
		userDefinedColLabel.put("us_loginid", "معرف الدخول");
		userDefinedColLabel.put("us_identityimg", "هوية");
		userDefinedColLabel.put("us_residenceimg", "بطاقة سكن");
		userDefinedColLabel.put("us_contractfile", "العقد");

		userDefinedNewCols.add("us_branchcode");
		userDefinedNewCols.add("us_loginid");
		userDefinedNewCols.add("us_name");
		userDefinedNewCols.add("us_password");
		userDefinedNewCols.add("us_rank");
		userDefinedNewCols.add("us_hp");
		userDefinedNewCols.add("us_active");
		
		userDefinedNewColsHtmlType.put("us_password" , "PASSWORD");
		
		userDefinedColsMustFill.add("us_loginid");
		userDefinedColsMustFill.add("us_name");
		userDefinedColsMustFill.add("us_password");
		userDefinedColsMustFill.add("us_rank");
		userDefinedColsMustFill.add("us_active");
		
		userDefinedNewColsDefualtValues.put("us_branchcode", new String[] {"{userstorecode}"});
		userDefinedReadOnlyNewCols.add("us_branchcode");
		
		userDefinedNewColsHtmlType.put("us_branchcode", "DROPLIST");
		userDefinedEditColsHtmlType.put("us_branchcode", "DROPLIST");
		
		userDefinedFilterCols.add("us_loginid");
		userDefinedFilterCols.add("us_rank");
		
		userDefinedEditCols.add("us_branchcode");
		userDefinedEditCols.add("us_loginid");
		userDefinedEditCols.add("us_name");
		userDefinedEditCols.add("us_password");
		userDefinedEditCols.add("us_rank");
		userDefinedEditCols.add("us_hp");
		userDefinedEditCols.add("us_active");
		//userDefinedEditCols.add("us_from_state");
		userDefinedEditColsDefualtValues.put("us_branchcode", new String[] {"{userstorecode}"});
		userDefinedReadOnlyEditCols.add("us_branchcode");
		
		userDefinedNewFormColNo = 2;
		userDefinedEditFormColNo = 2;
		
		userDefinedLookups.put("us_branchcode", "select branch_id, branch_name from kbbranches where branch_active='Y' ");
		userDefinedLookups.put("us_active", "select 'Y' , 'Yes' from dual union select 'N' , 'No' from dual ");
		userDefinedLookups.put("us_rank", "select rank_code ,rank_name_ar From kbrank where "
				+ " rank_code not in  ('DLVAGENT', 'LIAISONAGENT' , 'PICKUPAGENT','MASTERCUSTOMER','FOLLOWUP_EMP', 'SYSMANAGER','SUPPLY_EMP' , 'ITBOSS') " );
		
		userDefinedNewLookups.put("us_rank", "select rank_code ,rank_name_ar From kbrank where "
				+ " rank_code not in  ('DLVAGENT', 'LIAISONAGENT' , 'PICKUPAGENT','MASTERCUSTOMER','FOLLOWUP_EMP', 'SYSMANAGER','SUPPLY_EMP', 'ITBOSS') ");
		userDefinedEditLookups.put("us_rank", "select rank_code ,rank_name_ar From kbrank where "
				+ " rank_code not in  ('DLVAGENT', 'LIAISONAGENT' , 'PICKUPAGENT','MASTERCUSTOMER','FOLLOWUP_EMP', 'SYSMANAGER','SUPPLY_EMP', 'ITBOSS') ");
		
		userDefinedFilterCols.add("us_name");
		userDefinedFilterColsUsingLike.add("us_name");
		
		userModifyTD.put("showimg", "showImg({us_img}");
		userModifyTD.put("residenceimg", "showResidenceIdImg({us_residenceimg}");
		userModifyTD.put("idimage", "showIdentificationImg({us_identityimg}");
		userModifyTD.put("contractfile", "downloadContract({us_contractfile}");
		
	}
	
	public String showImg(HashMap<String,String>hashy) {
		StringBuilder img = new StringBuilder("<td align='center'>");
		if (hashy.get("us_img")!=null && !hashy.get("us_img").equalsIgnoreCase("")  && !hashy.get("us_img").equalsIgnoreCase(""))
			img.append("<img src='/primeimg/staff/"+hashy.get("us_img")+"' class='rounded-circle p-1 bg-primary' onclick=\"window.open(this.src, '_blank');\" height='55' width='55' style='max-width:300px'/>");
		img.append("</td>");
		return img.toString();
	}
	public String showResidenceIdImg(HashMap<String,String>hashy) {
		StringBuilder img = new StringBuilder("<td align='center'>");
		if (hashy.get("us_residenceimg")!=null && !hashy.get("us_residenceimg").equalsIgnoreCase("")  && !hashy.get("us_residenceimg").equalsIgnoreCase(""))
			img.append("<img src='/primeimg/residence/"+hashy.get("us_residenceimg")+"' onclick=\"window.open(this.src, '_blank');\"  height='55' width='55' style='max-width:300px'/>");
		img.append("</td>");
		return img.toString();
	}
	public String showIdentificationImg(HashMap<String,String>hashy) {
		StringBuilder img = new StringBuilder("<td align='center'>");
		if (hashy.get("us_identityimg")!=null && !hashy.get("us_identityimg").equalsIgnoreCase("")  && !hashy.get("us_identityimg").equalsIgnoreCase(""))
			img.append("<img src='/primeimg/Identities/"+hashy.get("us_identityimg")+"'  onclick=\"window.open(this.src, '_blank');\" height='55' width='55' style='max-width:300px'/>");
		img.append("</td>");
		return img.toString();
	}
	public String downloadContract(HashMap<String,String>hashy) {
		StringBuilder img = new StringBuilder("<td align='center'>");
		if (hashy.get("us_contractfile")!=null && !hashy.get("us_contractfile").equalsIgnoreCase("")  && !hashy.get("us_contractfile").equalsIgnoreCase(""))
			img.append("<a href='/primefiles/contract/"+hashy.get("us_contractfile")+"' class='btn btn-light' download><i class='fadeIn animated bx bx-download'></i></>");
		img.append("</td>");
		return img.toString();
	} 
	
	@Override
	public String doUpdate(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg= "User Updated";
		PreparedStatement pst = null;
		ResultSet rs = null;
		String oldPassword="" , newPassword_MD5="";
		Utilities ut = new Utilities();
		String dataFromDB ="", dataFromScreen="";
		keyCol = parseUpdateRqs(rqs);
		int usid_G = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
		try{
			//keyCol = inputMap_ori.get("smarty_us_id_hidden")[0];
			//System.out.println("usId--->"+keyCol);
			String newPassword=inputMap_ori.get("us_password")[0];
			HashMap <String , String> dataMapFromDB = new HashMap<String, String>();
			pst = conn.prepareStatement("select * from kbusers where us_id =?");
	        pst.setString(1, keyCol);
	        rs = pst.executeQuery();
	        ResultSetMetaData rsmd = rs.getMetaData();
	        if (rs.next()) {
	        	for(int i=1 ; i<=rsmd.getColumnCount();i++) {
	        		dataMapFromDB.put(rsmd.getColumnName(i), rs.getString(rsmd.getColumnName(i)));
	        	}
	        }
	        try {rs.close();}catch(Exception e) {}
	        try {pst.close();}catch(Exception ex) {}
	        for(String key:inputMap_ori.keySet()) {
	        	if (dataMapFromDB.containsKey(key)) {
					dataFromDB =dataMapFromDB.get(key);
					dataFromScreen=inputMap_ori.get(key)[0];
					//System.out.println("dataFromScreen---->"+dataFromScreen);
					//System.out.println("dataFromDB---->"+dataFromDB);
	        		if(!dataFromScreen.equalsIgnoreCase(dataFromDB))
	        			CoreUtilities.logChanges(conn, "KBUSERS", "us_id", Integer.parseInt(keyCol), key.replace("smartyhiddenedit_ori_", ""), dataFromDB, dataFromScreen,"update", "مستخدمون النظام", usid_G);
		        	}
	        }
			//CoreUtilities
			if(Utilities.checkPasswordSmallOrContainSpace(inputMap_ori.get("us_password")[0]))
				throw new Exception ("قد يحوي الرمز على اقل من اربع احرف اويحوي مسافة فارغة");
			if(ut.checkUserLoginIdExistOrSmall(conn, inputMap_ori.get("us_loginid")[0],"UPDATE"))
				throw new Exception ("قد يحوي الاسم على اقل من اربع احرف اويحوي مسافة فارغة");
			
			pst = conn.prepareStatement("select us_password , MD5(?) as newpass_MD5  from kbusers where us_id=?");
			pst.setString(1, newPassword);
			pst.setString(2, keyCol);
			rs = pst.executeQuery();
			if(rs.next()){
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
			pst = conn.prepareStatement("update kbusers set us_loginid =?, us_password =? , us_active=? ,"
					+ "  us_p_b4_enc=?, us_rank=? , us_name=? , us_hp=? where us_id=?");
			pst.setString(1, inputMap_ori.get("us_loginid")[0]);
			pst.setString(2, newPassword);
			pst.setString(3, inputMap_ori.get("us_active")[0]);
			pst.setString(4, newPassword);
			pst.setString(5, inputMap_ori.get("us_rank")[0]);
			pst.setString(6, inputMap_ori.get("us_name")[0]);
			pst.setString(7, inputMap_ori.get("us_hp")[0]);
			pst.setString(8, keyCol);
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
		FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(300000);
        List<FileItem> items = null;
       
		PreparedStatement pst = null;
		ResultSet rs = null;
		String states = "";
		Utilities ut = new Utilities();
		UtilitiesNafie utn = new UtilitiesNafie();
		int usid = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
		int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		inputMap_ori = filterRequest(rqs);
		try{
			
			if(Utilities.checkPasswordSmallOrContainSpace(inputMap_ori.get("us_password")[0]))
				throw new Exception ("قد يحوي الرمز على اقل من اربع احرف اويحوي مسافة فارغة");
			if(ut.checkUserLoginIdExistOrSmall(conn, inputMap_ori.get("us_loginid")[0],"INSERT"))
				throw new Exception ("قد يحوي الاسم على اقل من اربع احرف اويحوي مسافة فارغة او الاسم مستخدم مسبقاً");
			
			
			pst = conn.prepareStatement("insert into kbusers "
				+ "(us_loginid, us_active	 , us_p_b4_enc , us_rank , us_name, "
				+ " us_hp	  , us_branchcode, us_createdby, us_password) "
		+ " values ("+CoreUtilities.getQuestionMarks(8)+", MD5(?))", Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, inputMap_ori.get("us_loginid")[0]);
			pst.setString(2, "Y");
			pst.setString(3, inputMap_ori.get("us_password")[0]);
			pst.setString(4, inputMap_ori.get("us_rank")[0]);
			pst.setString(5, inputMap_ori.get("us_name")[0]);
			pst.setString(6, inputMap_ori.get("us_hp")[0]);
			pst.setInt(7, userstorecode);
			pst.setInt(8, usid);
			pst.setString(9, inputMap_ori.get("us_password")[0]);
//			pst.setString(10, uploadedFileName);
//			pst.setString(11, uploadedIdFileName);
//			pst.setString(12, uploadedResidenceIdFileName);
//			pst.setString(13, uploadedContractFileName);
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
		boolean creatRecordDelTabel = false;
		int userid = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
		try {
			pst = conn.prepareStatement("insert into kbusers_deleted "
									+ "		select * , now(), ? from kbusers where us_id = ?");
			pst.setInt(1, userid);
			pst.setString(2, rqs.getParameter(keyCol));
			pst.executeUpdate();
			CoreUtilities.logChanges(conn, "KBUSERS", "us_id", Integer.parseUnsignedInt(rqs.getParameter(keyCol)), "*", "ALL", "NONE", "delete", "مستخدمون النظام", userid);
			conn.commit();
			creatRecordDelTabel = true;
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{pst.close();}catch(Exception e){/*ignore*/}
		}
		if(creatRecordDelTabel)
			return super.doDelete(rqs);
		else
			return "Error";
	}
}
