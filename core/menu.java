package com.app.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import com.app.core.smartyLogAndErrorHandling;
import com.app.db.mysql;
import com.app.site.security.LoginUser;


public class menu {
	private LoginUser user;
	public LoginUser getUser() {
		return user;
	}
	public void setUser(LoginUser user) {
		this.user = user;
	}
	public StringBuilder loadMenu(String mainProjPath){
		StringBuilder sb = new StringBuilder();
		Connection conn = null;
		PreparedStatement pst = null , pstSub=null;
		ResultSet rsMenu = null , rsSubMenu = null;
		sb.append("<ul class=\"nav side-menu\">"); 
		try{
			 conn = mysql.getConn();
			 pst = conn.prepareStatement("select mt_name,mt_code , p_submenuids,mt_iconclass " +
			 		"FROM kbmenu_tabs join kbpermission on p_menuid = mt_code and p_rank_code=?"+
			 		" order by mt_seq ");
			 pst.setString(1,user.getRank_code());
			 rsMenu = pst.executeQuery();
			 while (rsMenu.next()){
				sb.append("<li><a>"
						+ " <i class='"+rsMenu.getString("mt_iconclass")+"'></i>"
						+ " "+rsMenu.getString("mt_name")+"</a>");
				sb.append("<ul class='nav child_menu'>");
				String [] subMenuIdPrems = rsMenu.getString("p_submenuids").split(":");
				List<String> listsubMenuIdPerms = Arrays.asList(subMenuIdPrems);
				if (rsMenu.getString("p_submenuids")!=null){
					 pstSub = conn.prepareStatement("select sm_id, sm_submenu_name , sm_submenucode   " +
						 		"from kbmenu_subtabs where sm_menucode =? order by sm_seq asc");
					 pstSub.setString(1,rsMenu.getString("mt_code")); 
					 rsSubMenu = pstSub.executeQuery();
						 while (rsSubMenu.next()){
							 if (listsubMenuIdPerms.contains(rsSubMenu.getString("sm_id"))) {
								 sb.append("<li><a href='../"+rsMenu.getString("mt_code")+"/"+rsSubMenu.getString("sm_submenucode")+".jsp'>"
										 +rsSubMenu.getString("sm_submenu_name")+"</a></li>");
							 }
						 }
						 try{rsSubMenu.close();}catch(Exception e){/**/}
						 pstSub.clearParameters();
					 }
				sb.append("</ul></li>");
			 }
		sb.append("</ul>");
		}catch(Exception e){
			String logErrorMsg = "class=>"+menu.class.getName()+",Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName(), 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			logErrorMsg = "";
			System.out.println("Error in loading Menu");
			e.printStackTrace();
		}finally{
			try{rsSubMenu.close();}catch(Exception e){/**/}
			try{pstSub.close();}catch(Exception e){/**/}
			try{rsMenu.close();}catch(Exception e){/**/}
			try{pst.close();}catch(Exception e){/**/}
			try{conn.close();}catch(Exception e){/**/}
		}
		return sb;
		
	}
	
	
}
