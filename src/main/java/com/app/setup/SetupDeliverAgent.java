package com.app.setup;

import java.io.InputStream;
import java.sql.Connection;
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
import smarty.core.smartyLogAndErrorHandling;
import smarty.db.mysql;

import com.app.util.Utilities;
import com.app.util.UtilitiesNafie;

public class SetupDeliverAgent extends CoreMgr{
	public SetupDeliverAgent(){
		MainSql = "select us_hp,'' as customer, '' as  popup, us_id,us_createddt, us_loginid, us_password, us_active,"
				+ " us_p_b4_enc, us_rank ,us_to_state as stt, us_agentsharerural, us_agentsharecenter ,"
				+ " us_to_state, us_name, ifnull(us_branchcode,'') as us_branchcode , us_img, us_img as showimg, "
				+ " us_identityimg, us_identityimg as idimage, us_residenceimg, us_residenceimg as residenceimg, "
				+ " us_contractfile, us_contractfile as contractfile, us_cartype, us_carplateno, us_dlvparentid "
				+ " from kbusers where us_rank in('DLVAGENT','SUB_DLVAGENT') and us_branchcode= {userstorecode}"
				+ " ";

		mainTable = "kbusers";
		keyCol   = "us_id";
		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
		canFilter =  true;
		canNew = true;
		canEdit = true;
		canDelete = true;

		userDefinedCaption     = "إعدادات مندوب التوصيل";
		userDefinedNewCaption  = "إضافة بيانات مندوب التوصيل";
		userDefinedEditCaption = "تعديل بيانات مندوب التوصيل";
				
//	    userDefinedGridCols.add("showimg");
//	    userDefinedGridCols.add("idimage");
//	    userDefinedGridCols.add("residenceimg");
//	    userDefinedGridCols.add("contractfile");
		userDefinedGridCols.add("us_loginid");
		userDefinedGridCols.add("us_name");
		userDefinedGridCols.add("us_rank");
		userDefinedGridCols.add("us_active");
		userDefinedGridCols.add("us_hp");
		userDefinedGridCols.add("us_cartype");
		userDefinedGridCols.add("us_carplateno");
		userDefinedGridCols.add("us_to_state");
		userDefinedGridCols.add("us_branchcode");
		userDefinedGridCols.add("popup");
		userDefinedGridCols.add("us_createddt");
		userDefinedGridCols.add("us_agentsharecenter");
		userDefinedGridCols.add("us_agentsharerural");
		//userDefinedGridCols.add("us_dlvparentid");
		
	
		userDefinedColLabel.put("us_agentsharecenter", "اجرة التوصيل مركز");
		userDefinedColLabel.put("us_agentsharerural", "أجرة التوصيل اطراف");
		userDefinedColLabel.put("us_dlvparentid", "مندوب التوصيل ألأب");
		
		userDefinedColLabel.put("us_loginid", "user id");
		userDefinedColLabel.put("us_name", "إسم المستخدم");
		
		userDefinedColLabel.put("us_from_state", "من محافظه");
		userDefinedColLabel.put("popup", " ");
		userDefinedColLabel.put("us_to_state", "إلى محافظه");
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
		userDefinedColLabel.put("us_carplateno", "رقم السيارة");
		userDefinedColLabel.put("us_cartype", "نوع ىالسيارة");
		userDefinedColLabel.put("us_rank", "المرتبة");
		/*
		 * showimg	idimage	residenceimg	contractfile	user id
		 */

		//userDefinedNewCols.add("us_branchcode");
		userDefinedNewCols.add("us_loginid");
		userDefinedNewCols.add("us_name");
		userDefinedNewCols.add("us_password");
		//userDefinedNewCols.add("us_rank");
		userDefinedNewCols.add("us_hp");
		userDefinedNewCols.add("us_cartype");
		userDefinedNewCols.add("us_carplateno");
		userDefinedNewCols.add("us_agentsharecenter");
		userDefinedNewCols.add("us_agentsharerural");
		userDefinedNewCols.add("us_rank");
		//userDefinedNewCols.add("us_dlvparentid");
		userDefinedNewCols.add("us_to_state");
//		userDefinedNewCols.add("us_img");
//		userDefinedNewCols.add("us_identityimg");
//		userDefinedNewCols.add("us_residenceimg");
//		userDefinedNewCols.add("us_contractfile");
		userDefinedNewColsHtmlType.put("us_password" , "PASSWORD");
		
		userDefinedColsMustFill.add("us_loginid");
		userDefinedColsMustFill.add("us_name");
		userDefinedColsMustFill.add("us_password");
		//userDefinedColsMustFill.add("us_rank");
		userDefinedColsMustFill.add("us_active");
		userDefinedColsMustFill.add("us_carplateno");
		userDefinedColsMustFill.add("us_cartype");
		userDefinedColsMustFill.add("us_rank");
		
		userDefinedFilterCols.add("us_loginid");
		//userDefinedFilterCols.add("us_rank");
		
		userDefinedEditCols.add("us_loginid");
		userDefinedEditCols.add("us_name");
		userDefinedEditCols.add("us_password");
		//userDefinedEditCols.add("us_rank");
		userDefinedEditCols.add("us_hp");
		userDefinedEditCols.add("us_cartype");
		userDefinedEditCols.add("us_carplateno");
		userDefinedEditCols.add("us_active");
		userDefinedEditCols.add("us_agentsharecenter");
		userDefinedEditCols.add("us_agentsharerural");
		userDefinedEditCols.add("us_rank");
//		userDefinedEditCols.add("us_dlvparentid");
//		userDefinedEditCols.add("us_img");
//		userDefinedEditCols.add("us_identityimg");
//		userDefinedEditCols.add("us_residenceimg");
//		userDefinedEditCols.add("us_contractfile");
		
		
		//userDefinedEditCols.add("us_from_state");
		userDefinedEditCols.add("us_to_state");
		//userDefinedEditCols.add("us_branchcode");
		//userDefinedEditColsHtmlType.put("us_password" , "PASSWORD");
		userDefinedLookups.put("us_branchcode", "select branch_id, branch_name from kbbranches where branch_active='Y' ");
		userDefinedLookups.put("us_active", "select 'Y' , 'Yes' from dual union select 'N' , 'No' from dual ");
		//userDefinedLookups.put("us_from_state", "select st_code, st_name_ar from kbstate ");
		userDefinedLookups.put("us_to_state", "select st_code, st_name_ar FROM kbstate");
		//userDefinedLookups.put("us_storecode", "select store_code, store_name from kbstores");
		userDefinedLookups.put("us_rank", "select rank_code ,rank_name_ar From kbrank where rank_code in('DLVAGENT','SUB_DLVAGENT')" );
		userDefinedLookups.put("us_dlvparentid", "select us_id ,us_name From kbusers where us_rank = 'DLVAGENT' "
				+ "and us_branchcode={userstorecode}" );
		userDefinedNewLookups.put("us_dlvparentid", "!select us_id ,us_name From kbusers where us_rank = 'DLVAGENT' "
				+ "and us_branchcode={userstorecode} and 'SUB_DLVAGENT'='{us_rank}'" );
		userDefinedEditLookups.put("us_dlvparentid", "!select us_id ,us_name From kbusers where us_rank = 'DLVAGENT' "
				+ "and us_branchcode={userstorecode} and 'SUB_DLVAGENT'='{us_rank}'" );
		userModifyTD.put("popup", "showLinkPopup({us_active},{us_rank},{us_id})");
		userDefinedNewColsHtmlType.put("us_to_state", "CHECKBOX");
		userDefinedNewColsDefualtValues.put("us_agentsharecenter", new String[] {"0"});
		userDefinedNewColsDefualtValues.put("us_agentsharerural", new String[] {"0"});
		//userDefinedNewColsDefualtValues.put("us_rank", new String[] {"DLVAGENT"});
		
		userDefinedColLabel.put("us_branchcode", "الفرع");
		
		userDefinedFilterCols.add("us_rank");
		
		userDefinedFilterCols.add("us_name");
		userDefinedFilterColsUsingLike.add("us_name");
		userDefinedFilterCols.add("us_dlvparentid");
		
		
		userDefinedNewColsHtmlType.put("us_dlvparentid", "DROPLIST");
		
		userModifyTD.put("showimg", "showImg({us_img}");
		userModifyTD.put("residenceimg", "showResidenceIdImg({us_residenceimg}");
		userModifyTD.put("idimage", "showIdentificationImg({us_identityimg}");
		userModifyTD.put("contractfile", "downloadContract({us_contractfile}");
		
		userDefinedNewFormColNo= 2;
		
	}
	
	@Override
	public void initialize(HashMap smartyStateMap){
		String userRank = replaceVarsinString(" {userRank} ", arrayGlobals).trim();
		if(userRank.equalsIgnoreCase("ITBOSS") || 
				userRank.equalsIgnoreCase("MANGER") || userRank.equalsIgnoreCase("BRANCHMGR")
				|| userRank.equalsIgnoreCase("SYSMANAGER") ) {
			;
		}else {
			userDefinedGridCols.remove("us_agentsharecenter");
			userDefinedGridCols.remove("us_agentsharerural");
			
			userDefinedReadOnlyNewCols.add("us_agentsharecenter");
			userDefinedReadOnlyNewCols.add("us_agentsharerural");
			
			userDefinedReadOnlyEditCols.add("us_agentsharecenter");
			userDefinedReadOnlyEditCols.add("us_agentsharerural");
		}
		super.initialize(smartyStateMap);
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
	
	public String showLinkPopup(HashMap<String,String>hashy) {
		String html = "<td></td>";
		if (hashy.get("us_active").equalsIgnoreCase("N") || !hashy.get("us_rank").equalsIgnoreCase("DLVAGENT"))
			if (hashy.get("us_rank").equalsIgnoreCase("PICKUPAGENT")) {
				html = "<td>";
				html +="<button type=\"button\" class=\"btn btn-xs btn-info\" onclick=\"popitup ('agentCustomersPopUp?customersusid="+hashy.get("us_id")+"' , '' , 1000 ,600);\">زبائن</button>";
				html +="<button type=\"button\" style='margin-right:5px;' "
						+ "class=\"btn btn-xs btn-dark\" "
						+ "onclick=\"popitup ('agentSpecialPricesPopUp?dlvagentidpopup="+hashy.get("us_id")+"' , '' , 1000 ,600);\">أسعار نقل خاصه للمندوب</button></br>";
				html +="<button type=\"button\" class=\"btn btn-xs btn-danger\" "
						+ " onclick=\"tieCustomersWithPickupAgentAndChangeShipmentsCostBackDated("+hashy.get("us_id")+");\">ربط الزبائن وتعديل إسعار النقل بأثر رجعي</button>";
				html +="</td>";
				return html;
			}else {
				return html;
			}
		else {
			html = "<td>";
			html +="<button type=\"button\" class=\"btn btn-xs btn-warning\" onclick=\"popitup ('agentDistrictPopUp?districtsusid="+hashy.get("us_id")+"' , '' , 1000 ,600);\">مناطق</button>";
			html +="</td>";
			return html;
		}
	}
	
	
	
	@Override
	public String doUpdate(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg= "User Updated";
		PreparedStatement pst = null;
		ResultSet rs = null;
		String oldPassword="" , newPassword_MD5="";
		Utilities ut = new Utilities();
		String dataFromDB ="", dataFromScreen="";
		int usid = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
		int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		String usId = parseUpdateRqs(rqs);
		try{
			keyCol = usId;			
			
			pst = conn.prepareStatement("select * from kbusers where us_id=?");
			pst.setString(1, keyCol);
			rs = pst.executeQuery();
			rs.next();
			inputMap_ori.remove("smarty_us_id_hidden");
			String newPassword=inputMap_ori.get("us_password")[0];
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
							"update", "إعدادات مندوب التوصيل", usid);			
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
			String states = "";
			 
			if (inputMap_ori.containsKey("us_to_state") && inputMap_ori.get("us_to_state") !=null ) {
				System.out.println("inputMap_ori.get(\"us_to_state\").length;====>"+inputMap_ori);
				for (String state : inputMap_ori.get("us_to_state")){
					states +=state+":";
					System.out.println("states==?>"+states);
				}
			}
			
			pst = conn.prepareStatement("update kbusers set "
			+ " us_loginid =?, us_password =? , us_active=? 		, us_p_b4_enc=?, us_to_state=?,  "
			+ " us_name=? 	 , us_hp=? 		  , us_agentsharerural=?, us_agentsharecenter=?, us_branchcode=?, "
			+ " us_cartype=? , us_carplateno=?, us_rank=?  "
			+ " where us_id=?");
			pst.setString(1, inputMap_ori.get("us_loginid")[0]);
			pst.setString(2, newPassword);
			pst.setString(3, inputMap_ori.get("us_active")[0]);
			pst.setString(4, newPassword);
			pst.setString(5, states);
			pst.setString(6, inputMap_ori.get("us_name")[0]);
			pst.setString(7, inputMap_ori.get("us_hp")[0]);
			pst.setString(8, (inputMap_ori.get("us_agentsharerural")[0]==null) ? "0" : inputMap_ori.get("us_agentsharerural")[0]);
			pst.setString(9, (inputMap_ori.get("us_agentsharecenter")[0]==null) ? "0" : inputMap_ori.get("us_agentsharecenter")[0]);
			pst.setInt(10, userstorecode);
//			pst.setString(11, uploadedFileName);
//			pst.setString(12, uploadedIdFileName);
//			pst.setString(13, uploadedResidenceIdFileName);
//			pst.setString(14, uploadedContractFileName);
			pst.setString(11, inputMap_ori.get("us_cartype")[0]);
			pst.setString(12, inputMap_ori.get("us_carplateno")[0]);
		//	pst.setInt(17, dlvParentId);
			pst.setString(13, inputMap_ori.get("us_rank")[0]);
			pst.setString(14, keyCol);
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
		String states = "";
		Utilities ut = new Utilities();
		int usid = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
		int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		inputMap_ori = filterRequest(rqs);
		try{
			if (inputMap_ori.containsKey("us_to_state") && inputMap_ori.get("us_to_state") !=null ) {
				for (int i = 0; i<inputMap_ori.get("us_to_state").length; i++)
					states +=inputMap_ori.get("us_to_state")[i]+":";
			}
			
			
			if(Utilities.checkPasswordSmallOrContainSpace(inputMap_ori.get("us_password")[0]))
				throw new Exception ("قد يحوي الرمز على اقل من اربع احرف اويحوي مسافة فارغة");
			if(ut.checkUserLoginIdExistOrSmall(conn, inputMap_ori.get("us_loginid")[0],"INSERT"))
				throw new Exception ("قد يحوي الاسم على اقل من اربع احرف اويحوي مسافة فارغة او الاسم مستخدم مسبقاً");
	
			pst = conn.prepareStatement("insert into kbusers "
			+ "(us_loginid   , us_password , us_active	  , us_p_b4_enc	      , us_rank , "
			+ " us_to_state  , us_name 	   , us_hp		  , us_agentsharerural, us_agentsharecenter, "
			+ " us_branchcode, us_createdby, us_cartype	  , us_carplateno) "
	+ " values (?			 , MD5(?)	   , ?		 	  , ?				  , ? , "
	+ " 		? 			 , ?		   , ? 		 	  , ? 				  , ? , "
	+ "         ?			 , ?		   , ?		 	  , ?)", Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, inputMap_ori.get("us_loginid")[0]);
			pst.setString(2, inputMap_ori.get("us_password")[0]);
			pst.setString(3, "Y");
			pst.setString(4, inputMap_ori.get("us_password")[0]);
			pst.setString(5,  inputMap_ori.get("us_rank")[0]);
			pst.setString(6,states);
			pst.setString(7, inputMap_ori.get("us_name")[0]);
			pst.setString(8, inputMap_ori.get("us_hp")[0]);
			pst.setString(9, (inputMap_ori.get("us_agentsharerural")[0]==null) ? "0" : inputMap_ori.get("us_agentsharerural")[0]);
			pst.setString(10, (inputMap_ori.get("us_agentsharecenter")[0]==null) ? "0" : inputMap_ori.get("us_agentsharecenter")[0]);
			pst.setInt(11, userstorecode);
			pst.setInt(12, usid);
//			pst.setString(13, uploadedFileName);
//			pst.setString(14, uploadedIdFileName);
//			pst.setString(15, uploadedResidenceIdFileName);
//			pst.setString(16, uploadedContractFileName);
			pst.setString(13, inputMap_ori.get("us_cartype")[0]);
			pst.setString(14, inputMap_ori.get("us_carplateno")[0]);
		//	pst.setInt(19, dlvParentId);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			rs.next();
			CoreUtilities.logChanges(conn, "KBUSERS", "us_id", rs.getInt(1),	 "*", 	"NONE"	, "ALL", "insert", "إعدادات مندوب التوصيل", usid);
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
		ResultSet rs = null;
		PreparedStatement pst = null;
		boolean allowDelete = true;
		Utilities ut = new Utilities();
		int userid = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
		int deletedUserId = Integer.parseUnsignedInt(rqs.getParameter(keyCol));
		String msg = "";
		try {
			pst = conn.prepareStatement("select 1 from p_cases where c_assignedagent=? limit 1");
			pst.setInt(1, deletedUserId);
			rs = pst.executeQuery();
			if (rs.next()) {
				allowDelete = false;
			}
			try{rs.close();}catch(Exception e){/*ignore*/}
			try{pst.close();}catch(Exception e){/*ignore*/}
			if (allowDelete) {
				pst = conn.prepareStatement("insert into kbusers_deleted "
										+ "		select * ,  now(), ? from kbusers where us_id = ?");
				pst.setInt(1, userid);
				pst.setInt(2, deletedUserId);
				pst.executeUpdate();
				try{pst.close();}catch(Exception e){/*ignore*/}
				
				CoreUtilities.logChanges(conn, "KBUSERS", "us_id", deletedUserId, "*", "ALL", "NONE", "delete", "إعدادات مندوب التوصيل", userid);
				
				pst = conn.prepareStatement("delete from kbusers where us_id =? ");
				pst.setInt(1, deletedUserId);
				pst.executeUpdate();
				conn.commit();
			}else {
				msg = "هذا المندوب لا يمكن مسحه لوجود شحنات لديه";
			}
		}catch (Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {}
			e.printStackTrace();
		}finally{
			try{rs.close();}catch(Exception e){/*ignore*/}
			try{pst.close();}catch(Exception e){/*ignore*/}
		}
		return msg;
	}
}
