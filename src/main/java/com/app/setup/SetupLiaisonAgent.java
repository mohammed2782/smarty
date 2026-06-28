package com.app.setup;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

import com.app.util.Utilities;
import com.app.util.UtilitiesNafie;

public class SetupLiaisonAgent extends CoreMgr{
	public SetupLiaisonAgent() {
		
		MainSql = "select us_hp,'' as customer, us_id,us_createddt, us_loginid, us_password, us_active,"
				+ " us_p_b4_enc, us_rank, us_img, us_img as showimg,   "
				+ " us_identityimg, us_identityimg as idimage, us_residenceimg, us_residenceimg as residenceimg, "
				+ " us_contractfile, us_contractfile as contractfile, us_name, us_branchcode , us_cartype, us_carplateno "
				+ " from kbusers where us_rank='LIAISONAGENT' and us_branchcode='{userstorecode}' ";

		mainTable = "kbusers";
		keyCol   = "us_id";
		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
		canNew = true;
		canFilter =  true;
		canEdit = true;
		canDelete = true;

	   userDefinedCaption = "إعدادات مندوب الإرتباط";
	   userDefinedNewCaption = "إضافة بيانات مندوب الإرتباط";
	   userDefinedEditCaption = "تعديل بيانات مندوب الإرتباط";
				
	   userDefinedGridCols.add("us_name");
		userDefinedGridCols.add("us_rank");
		userDefinedGridCols.add("us_cartype");
		userDefinedGridCols.add("us_carplateno");
		userDefinedGridCols.add("us_active");
		userDefinedGridCols.add("us_hp");
		userDefinedGridCols.add("us_branchcode");
		//userDefinedGridCols.add("customer");
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
		userDefinedColLabel.put("showimg", "صورة");
		userDefinedColLabel.put("idimage","هوية");
		userDefinedColLabel.put("residenceimg", "بطاقة سكن");
		userDefinedColLabel.put("contractfile", "العقد");
		userDefinedColLabel.put("us_loginid", "معرف الدخول");
		userDefinedColLabel.put("us_identityimg", "هوية");
		userDefinedColLabel.put("us_residenceimg", "بطاقة سكن");
		userDefinedColLabel.put("us_contractfile", "العقد");
		userDefinedColLabel.put("us_carplateno", "رقم السيارة");
		userDefinedColLabel.put("us_cartype", "نوع ىالسيارة");
		
		userDefinedNewCols.add("us_loginid");
		userDefinedNewCols.add("us_name");
		userDefinedNewCols.add("us_password");
		userDefinedNewCols.add("us_hp");
		userDefinedNewCols.add("us_cartype");
		userDefinedNewCols.add("us_carplateno");
		
		userDefinedNewColsHtmlType.put("us_password" , "PASSWORD");
		
		userDefinedColsMustFill.add("us_loginid");
		userDefinedColsMustFill.add("us_name");
		userDefinedColsMustFill.add("us_password");
		userDefinedColsMustFill.add("us_active");
		userDefinedColsMustFill.add("us_carplateno");
		userDefinedColsMustFill.add("us_cartype");
		
		userDefinedNewColsDefualtValues.put("us_branchcode", new String[] {"{userstorecode}"});
		userDefinedReadOnlyNewCols.add("us_branchcode");
		
		userDefinedNewColsDefualtValues.put("us_rank", new String[] {"LIAISONAGENT"});
		userDefinedReadOnlyNewCols.add("us_rank");
		
		userDefinedFilterCols.add("us_loginid");
		userDefinedFilterCols.add("us_rank");
		
		userDefinedEditCols.add("us_loginid");
		userDefinedEditCols.add("us_name");
		userDefinedEditCols.add("us_password");
		userDefinedEditCols.add("us_hp");
		userDefinedEditCols.add("us_cartype");
		userDefinedEditCols.add("us_carplateno");
		userDefinedEditCols.add("us_active");
		
		//userDefinedEditCols.add("us_from_state");
		userDefinedEditColsDefualtValues.put("us_branchcode", new String[] {"{userstorecode}"});
		userDefinedReadOnlyEditCols.add("us_branchcode");
		
		userDefinedNewColsHtmlType.put("us_branchcode", "DROPLIST");
		userDefinedEditColsHtmlType.put("us_branchcode", "DROPLIST");
		
		userDefinedEditColsDefualtValues.put("us_rank", new String[] {"LIASIONAGENT"});
		userDefinedReadOnlyEditCols.add("us_rank");

		//userDefinedEditColsHtmlType.put("us_password" , "PASSWORD");
		userDefinedLookups.put("us_branchcode", "select branch_id, branch_name from kbbranches where branch_active='Y' ");
		userDefinedLookups.put("us_active", "select 'Y' , 'Yes' from dual union select 'N' , 'No' from dual ");
		//userDefinedLookups.put("us_from_state", "select st_code, st_name_ar from kbstate ");
		//userDefinedLookups.put("us_storecode", "select store_code, store_name from kbstores");
		userDefinedLookups.put("us_rank", "select rank_code ,rank_name_ar From kbrank where rank_code='LIAISONAGENT' " );
				
		
		userDefinedFilterCols.add("us_name");
		userDefinedFilterColsUsingLike.add("us_name");
		
		userDefinedNewColsHtmlType.put("us_img", "IMAGE");
		userDefinedNewColsHtmlType.put("us_identityimg", "IMAGE");
		userDefinedNewColsHtmlType.put("us_residenceimg", "IMAGE");
		userDefinedNewColsHtmlType.put("us_contractfile", "IMAGE");
		userModifyTD.put("showimg", "showImg({us_img}");
		userModifyTD.put("residenceimg", "showResidenceIdImg({us_residenceimg}");
		userModifyTD.put("idimage", "showIdentificationImg({us_identityimg}");
		userModifyTD.put("contractfile", "downloadContract({us_contractfile}");
		
	}
	
	
	public String showImg(HashMap<String,String>hashy) {
		StringBuilder img = new StringBuilder("<td align='center'>");
		if (hashy.get("us_img")!=null && !hashy.get("us_img").equalsIgnoreCase("")  && !hashy.get("us_img").equalsIgnoreCase(""))
			img.append("<img src='/primeimg/agents/"+hashy.get("us_img")+"' class='rounded-circle p-1 bg-primary' onclick=\"window.open(this.src, '_blank');\" height='55' width='55' style='max-width:300px'/>");
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
		inputMap_ori = filterRequest(rqs);
		try{
			keyCol = inputMap_ori.get("smarty_us_id_hidden")[0];
			String newPassword=inputMap_ori.get("us_password")[0];
			pst = conn.prepareStatement("select * from kbusers where us_id=?");
			pst.setString(1, keyCol);
			rs = pst.executeQuery();
			rs.next();
			inputMap_ori.remove("smarty_us_id_hidden");
			int usid = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
			int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
			for(String key:inputMap_ori.keySet()) {
				dataFromDB = "";
				dataFromScreen = "";
				//System.out.println(key);
				if( rs.getString(key)!=null)
					dataFromDB = rs.getString(key).trim();
				if(inputMap_ori.get(key)[0]!=null)
					dataFromScreen = inputMap_ori.get(key)[0].trim();
        		if(!dataFromScreen.equalsIgnoreCase(dataFromDB))
        			CoreUtilities.logChanges(conn, "KBUSERS", "us_id", Integer.parseInt(keyCol), key, dataFromDB, dataFromScreen,
							"update", "إعدادات مندوب الإرتباط", usid);			
        		}
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
			if(Utilities.checkPasswordSmallOrContainSpace(inputMap_ori.get("us_password")[0]))
				throw new Exception ("قد يحوي الرمز على اقل من اربع احرف اويحوي مسافة فارغة");
			if(ut.checkUserLoginIdExistOrSmall(conn, inputMap_ori.get("us_loginid")[0],"UPDATE"))
				throw new Exception ("قد يحوي الاسم على اقل من اربع احرف اويحوي مسافة فارغة");
		
			pst = conn.prepareStatement("select us_password , MD5(?) as newpass_MD5, us_img, us_identityimg, us_residenceimg, us_contractfile from kbusers where us_id=?");
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
		
			pst = conn.prepareStatement("update kbusers "
			+ " set us_loginid =?, us_password =?, us_active=?, us_p_b4_enc=?, us_name=?, us_hp=?, "
			+ "  us_cartype=?, us_carplateno=? where us_id=?");
			pst.setString(1, inputMap_ori.get("us_loginid")[0]);
			pst.setString(2, newPassword);
			pst.setString(3, inputMap_ori.get("us_active")[0]);
			pst.setString(4, newPassword);
			pst.setString(5, inputMap_ori.get("us_name")[0]);
			pst.setString(6, inputMap_ori.get("us_hp")[0]);
			pst.setString(7,inputMap_ori.get("us_cartype")[0]);
			pst.setString(8,inputMap_ori.get("us_carplateno")[0] );
			pst.setString(9,keyCol);
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
		PreparedStatement pst = null;
		ResultSet rs = null;
		Utilities ut = new Utilities();
		inputMap_ori = filterRequest(rqs);
		try{
			if(Utilities.checkPasswordSmallOrContainSpace(inputMap_ori.get("us_password")[0]))
				throw new Exception ("قد يحوي الرمز على اقل من اربع احرف اويحوي مسافة فارغة");
			if(ut.checkUserLoginIdExistOrSmall(conn, inputMap_ori.get("us_loginid")[0],"INSERT"))
				throw new Exception ("قد يحوي الاسم على اقل من اربع احرف اويحوي مسافة فارغة او الاسم مستخدم مسبقاً");
			int usid = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
			int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
			pst = conn.prepareStatement("insert into kbusers "
				+ "(us_loginid, us_password, us_active 	  , us_p_b4_enc	, us_rank , "
				+ " us_name   , us_hp	   , us_branchcode, us_createdby, us_cartype, "
				+ " us_carplateno ) "
		+ " values (?		  , MD5(?)     , ?		 	  , ?			 , ? , "
		+ " 		? 		  , ?		   , ? 		 	  , ? 			 , ?, "
		+ "         ? )", Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, inputMap_ori.get("us_loginid")[0]);
			pst.setString(2, inputMap_ori.get("us_password")[0]);
			pst.setString(3, "Y");
			pst.setString(4, inputMap_ori.get("us_password")[0]);
			pst.setString(5, "LIAISONAGENT");
			pst.setString(6, inputMap_ori.get("us_name")[0]);
			pst.setString(7, inputMap_ori.get("us_hp")[0]);
			pst.setInt(8, userstorecode);
			pst.setInt(9, usid);
			pst.setString(10, inputMap_ori.get("us_cartype")[0]);
			pst.setString(11, inputMap_ori.get("us_carplateno")[0]);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			rs.next();
			CoreUtilities.logChanges(conn, "KBUSERS", "us_id", rs.getInt(1),	 "*", 	"NONE"	, "ALL", "insert", "إعدادات مندوب الإرتباط", usid);
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
		//System.out.println(keyCol);
		boolean creatRecordDelTabel = false;
		Utilities ut = new Utilities();
		int userid = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
		try {
			pst = conn.prepareStatement("insert into kbusers_deleted "
									+ "		select * , now(), ? from kbusers where us_id = ?");
			pst.setInt(1, userid);
			pst.setString(2, rqs.getParameter(keyCol));
			pst.executeUpdate();
			CoreUtilities.logChanges(conn, "KBUSERS", "us_id", Integer.parseUnsignedInt(rqs.getParameter(keyCol)), "*", "ALL", "NONE", "delete", "إعدادات مندوب الإرتباط", userid);
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

