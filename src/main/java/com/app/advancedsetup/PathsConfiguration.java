package com.app.advancedsetup;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.db.mysql;

public class PathsConfiguration extends CoreMgr {
	public PathsConfiguration () {
		MainSql = "select * from kbpaths";
		
		userDefinedGridCols.add("path_state");
		//userDefinedGridCols.add("path_frombranch");
		userDefinedGridCols.add("path_tobranch");
		userDefinedGridCols.add("path_liaisonagent");
		userDefinedGridCols.add("path_cost");
		
		userDefinedNewCols.add("path_state");
		userDefinedNewCols.add("path_frombranch");
		userDefinedNewCols.add("path_tobranch");
		userDefinedNewCols.add("path_liaisonagent");
		userDefinedNewCols.add("path_cost");
		
		userDefinedLookups.put("path_state", "select st_code, st_name_ar from kbstate");
		userDefinedLookups.put("path_frombranch", "select branch_id, branch_name from kbbranches");
		userDefinedLookups.put("path_tobranch", "select branch_id, branch_name from kbbranches");
		userDefinedLookups.put("path_liaisonagent", "select us_id, us_name from kbusers where us_rank ='LIAISONAGENT' ");
		
		userDefinedColLabel.put("path_state", "إلى محافظة");
		userDefinedColLabel.put("path_frombranch", "من فرع");
		userDefinedColLabel.put("path_tobranch", "إلى فرع");
		userDefinedColLabel.put("path_liaisonagent", "مندوب الإرتياط");
		
		userDefinedColLabel.put("path_cost", "تكلفة المسار");
		
		canNew = true;
		canEdit = true;		
		canFilter = true;
		mainTable = "kbpaths";
		keyCol = "path_id";
		canDelete = true;
		
		userDefinedEditCols.add("path_liaisonagent");
		userDefinedEditCols.add("path_cost");
		userDefinedFilterCols.add("path_state");
		userDefinedFilterCols.add("path_frombranch");
		userDefinedFilterCols.add("path_tobranch");
		userDefinedFilterCols.add("path_liaisonagent");
		
		userDefinedNewColsHtmlType.put("path_state", "CHECKBOX");
		userDefinedNewColsHtmlType.put("path_frombranch", "DROPLIST");
		userDefinedNewColsHtmlType.put("path_tobranch", "DROPLIST");
		userDefinedNewColsHtmlType.put("path_liaisonagent", "DROPLIST");
		
		userDefinedColsMustFill.add("path_state");
		userDefinedColsMustFill.add("path_frombranch");
		userDefinedColsMustFill.add("path_tobranch");
		userDefinedColsMustFill.add("path_liaisonagent");
		userDefinedColsMustFill.add("path_cost");
		userDefinedCaption = "مسارات الفروع";
		userDefinedNewCaption = "مسار جديد";
		
		userDefinedGroupByCol = "path_frombranch";
		userDefinedFilterColsHtmlType.put("path_state", "DROPLIST");
		userDefinedNewColsDefualtValues.put("path_cost", new String [] {"0"});
		userDefinedMinValMap.put("path_cost","0");
		
		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
	}
	
	
	 @Override 
	 public String doInsert (HttpServletRequest rqs , boolean commit) {
		 Connection conn = null;
		 PreparedStatement pst = null;
		
		 String userId = replaceVarsinString(" {usid} ", arrayGlobals).trim();
		 try {
			 conn = mysql.getConn();
			 inputMap_ori = filterRequest(rqs);
			 pst = conn.prepareStatement("insert into kbpaths "
			 		+ "(path_state 	  , path_frombranch, path_tobranch, path_liaisonagent, path_cost, "
			 		+ "	path_createdby) "
			+ " values (? 			  , ? 			   , ? 			  , ?				 , ?,"
			+ "			? )");
			 for (String state : inputMap_ori.get("path_state")){
				 pst.setString(1, state);
				 pst.setString(2, rqs.getParameter("path_frombranch"));
				 pst.setString(3, rqs.getParameter("path_tobranch"));
				 pst.setString(4, rqs.getParameter("path_liaisonagent"));
				 pst.setString(5, rqs.getParameter("path_cost"));
				 pst.setString(6, userId);
				 pst.executeUpdate();
			 }
			 conn.commit();
			 
		 }catch(Exception e) {
			 try {conn.rollback();}catch(Exception eRoll) {/**/}
			 e.printStackTrace();
		 }finally {
			 try {pst.close();}catch(Exception e) {/**/}
			 try {conn.close();}catch(Exception e) {/**/}
		 }
		 return "";
	 }
	 
	 @Override 
	 public String doDelete (HttpServletRequest rqs) {
		 Connection conn = null;
		 PreparedStatement pst = null;
		
		 int userId = Integer.parseInt(replaceVarsinString(" {usid} ", arrayGlobals).trim());
		 try {
			 conn = mysql.getConn();
			 int pathIdToDelete = Integer.parseInt(rqs.getParameter(keyCol));
			 System.out.println("pathIdToDelete====>"+pathIdToDelete);
			 //backup
			 pst = conn.prepareStatement("insert into kbpaths_deleted"
			 		+ " select kbpaths.*, now(), ? from kbpaths where path_id=? ");
			 pst.setInt(1, userId);
			 pst.setInt(2, pathIdToDelete);
			 pst.executeUpdate();
			 try {pst.close();}catch(Exception e) {/**/}
			 //now delete
			 pst = conn.prepareStatement("delete from kbpaths where path_id=? ");
			 pst.setInt(1, pathIdToDelete);
			 pst.executeUpdate();
			 
			 conn.commit();
			 
		 }catch(Exception e) {
			 try {conn.rollback();}catch(Exception eRoll) {/**/}
			 e.printStackTrace();
		 }finally {
			 try {pst.close();}catch(Exception e) {/**/}
			 try {conn.close();}catch(Exception e) {/**/}
		 }
		 return "";
	 }
	 
}
