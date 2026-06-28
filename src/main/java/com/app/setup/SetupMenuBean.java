package com.app.setup;

import java.sql.*;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.core.smartyLogAndErrorHandling;


public class SetupMenuBean extends CoreMgr {
	
	public SetupMenuBean(){		
		MainSql =" select * From kbmenu_tabs ";
		
		userDefinedCaption = "إعدادات القائمة الرئيسية";
		userDefinedNewCaption = "إضافة بيانات القائمة الرئيسية";
		userDefinedEditCaption = "تعديل بيانات القائمة الرئيسية";
		
		keyCol = "mt_id";
		mainTable ="kbmenu_tabs";
		
		search_paramval = null;
		canNew = true;
		canFilter = true;
		canEdit = true;
		canDelete = true;
		clickableRow =true;
	
		userDefinedGridCols.add("mt_id");
		userDefinedGridCols.add("mt_name");
		userDefinedGridCols.add("mt_code");
		userDefinedGridCols.add("mt_seq");
		userDefinedGridCols.add("mt_iconclass");

		userDefinedColLabel.put("mt_id", "رقم القائمة الرئيسية");
		userDefinedColLabel.put("mt_name", "إسم القائمة الرئيسية");
		userDefinedColLabel.put("mt_code", "كود القائمة الرئيسية");
		userDefinedColLabel.put("mt_seq", "تسلسل القائمة الرئيسية");
		userDefinedColLabel.put("mt_iconclass", "الصورة");

		userDefinedNewCols.add("mt_name");
		userDefinedNewCols.add("mt_code");
		userDefinedNewCols.add("mt_seq");
				
		userDefinedColsMustFill.add("mt_name");
		userDefinedColsMustFill.add("mt_code");
		userDefinedColsMustFill.add("mt_seq");
						
		userDefinedFilterCols.add("mt_name");
				
		userDefinedFilterLookups.put("mt_name", "select mt_name , mt_name from kbmenu_tabs");
										
		userDefinedReadOnlyEditCols.add("mt_id");
		
		userDefinedGlobalClickRowID="mt_id";				

	}  
	
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
	}
	
	public void overRideCellData(){
		System.out.println("this is me overRideCellData");
	}
}
