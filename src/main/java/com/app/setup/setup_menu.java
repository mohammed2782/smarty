/* class description: used to setup menu,
 * created by: lina - smarty framework team member,
 * created date: 21/4/2018 6:06 PM.
 */
package com.app.setup;

import java.sql.*;
import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.core.smartyLogAndErrorHandling;

public class setup_menu extends CoreMgr {
	public setup_menu(){
	
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */	
		MainSql =" select * From kbmenu_tabs ";
		mainTable ="kbmenu_tabs";
		keyCol = "mt_id";
		
		/*
		 * to define user gridviews caption
		 */
		userDefinedCaption = "إعدادات القائمة الرئيسية";
		userDefinedNewCaption = "إضافة بيانات القائمة الرئيسية";
		userDefinedEditCaption = "تعديل بيانات القائمة الرئيسية";
		
		/*
		 * to enable/disable basic operations 
		 */
		search_paramval = null;
		canNew = true;
		canFilter = true;
		canEdit = true;
		canDelete = true;
		clickableRow = true;
	
		/*
		 * to define gridview columns that want to show to user
		 */
		userDefinedGridCols.add("mt_id");
		userDefinedGridCols.add("mt_name");
		userDefinedGridCols.add("mt_code");
		userDefinedGridCols.add("mt_seq");
		userDefinedGridCols.add("mt_iconclass");

		/*
		 * to define gridview label that want to show to user
		 */
		userDefinedColLabel.put("mt_id", "رقم القائمة الرئيسية");
		userDefinedColLabel.put("mt_name", "إسم القائمة الرئيسية");
		userDefinedColLabel.put("mt_code", "كود القائمة الرئيسية");
		userDefinedColLabel.put("mt_seq", "تسلسل القائمة الرئيسية");
		userDefinedColLabel.put("mt_iconclass", "الصورة");

		/*
		 * to define new columns for insert operation
		 */
		userDefinedNewCols.add("mt_name");
		userDefinedNewCols.add("mt_code");
		userDefinedNewCols.add("mt_seq");
			
		/*
		 * to define user must fill columns 
		 */
		userDefinedColsMustFill.add("mt_name");
		userDefinedColsMustFill.add("mt_code");
		userDefinedColsMustFill.add("mt_seq");
						
		/*
		 * to define filter columns for search operation
		 */
		userDefinedFilterCols.add("mt_name");				
		userDefinedFilterLookups.put("mt_name", "select mt_name , mt_name from kbmenu_tabs");

		/*
		 * to define edit coulmns for update opeartion
		 */							
		userDefinedReadOnlyEditCols.add("mt_id");
		
		/*
		 * to pop up sub menu for main menu
		 */
		userDefinedGlobalClickRowID="mt_id";
		
	}//end of constructor setup_menu  
	
	/*
	 * to do delete opeartion
	 */
	@Override
	public String doDelete(HttpServletRequest rqs){
		String sqlDelete;
		String msg = "";
		PreparedStatement pst = null;
		try{
			String keyVal= rqs.getParameter(keyCol);
			sqlDelete = "delete from kbmenu_subtabs where sm_menucode in (select mt_code from kbmenu_tabs where mt_id=?)";
			pst = conn.prepareStatement(sqlDelete);
		    pst.setString(1, keyVal);
		    pst.executeUpdate();
		    try{pst.close();}catch(Exception e){/*ignore*/}
		    sqlDelete = "delete from "+mainTable+" where "+keyCol+"=?";
		    pst = conn.prepareStatement(sqlDelete);
		    pst.setString(1, keyVal);
		    pst.executeUpdate();
		   conn.commit();
	    }catch (Exception e){
		    System.out.println("erorr in deletion,myClassBean=>"+myClassBean);
		    smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", "CoreMgr", myClassBean, null, e);
		    e.printStackTrace();
	    }finally{
	    	try{pst.close();}catch(Exception e){/*ignore*/}
	    }
		return msg;
	}//end of method doDelete
	
}//end of class setup_menu
