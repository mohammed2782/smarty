package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.db.mysql;


public class AssignBookReceiptsOld extends CoreMgr {
	
	public AssignBookReceiptsOld () {
		MainSql = "select br_groupid, br_custid, min(br_rcp_no)as startrange, max(br_rcp_no) as torange ,br_bid,'' as checkused "
				+ " from p_books_rcp where br_bid = {bookid} and br_branchid={userstorecode} group by br_groupid,br_custid order by min(br_id)";
		userDefinedGridCols.add("br_custid");
		userDefinedGridCols.add("startrange");
		userDefinedGridCols.add("torange");
		canNew = true;
		canDelete = true;
		
		keyCol    = "br_groupid";
		userDefinedNewCols.add("br_custid");
		userDefinedNewCols.add("startrange");
		userDefinedNewCols.add("torange");
		mainTable = "p_books_rcp";
		userDefinedColsMustFill.add("br_custid");
		userDefinedColsMustFill.add("startrange");
		userDefinedColsMustFill.add("torange");
		userDefinedLookups.put("br_custid", "select cust_id , cust_name from kbcustomers where cust_branch={userstorecode}");
		myhtmlmgr.refreshPageOnDelete = true;
		UserDefinedPageRows = 1000;
		
		userDefinedNewColsHtmlType.put("br_custid", "DROPLIST");
		myhtmlmgr.refreshPageOnDelete = true;
	}
	 @Override 
	 public String doInsert (HttpServletRequest rqs , boolean commit) {
		 Connection conn = null;
		 PreparedStatement pst = null;
		 ResultSet rs =null;
		 String bookid = replaceVarsinString(" {bookid} ", arrayGlobals).trim();
		 int maxGroup = 0;
		 try {
			conn = mysql.getConn();
			int noOfCustomerInSingleGroup = 0;
			int customer = 0;
			int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
			pst = conn.prepareStatement("select br_custid from p_books_rcp "
					+ "where br_bid=? and br_rcp_no>=? and br_rcp_no<=?  and br_branchid=?  group by br_custid");
			pst.setString(1, bookid);
			pst.setString(2, rqs.getParameter("startrange") );
			pst.setString(3, rqs.getParameter("torange") );
			pst.setInt(4, branchId_G );
			rs = pst.executeQuery();
			while (rs.next()) {
				noOfCustomerInSingleGroup ++;
				customer = rs.getInt("br_custid");
			}
			
			if (noOfCustomerInSingleGroup>1) {
				return "هذا المدى محجوز لزبون أخر";
			}
			if (customer !=0) {
				return "هذا المدى محجوز لزبون أخر";
			}
			
			maxGroup ++;
			try {rs.close();}catch(Exception e) {/**/}
			try {pst.close();}catch(Exception e) {/**/}
			
			pst = conn.prepareStatement("select max(br_groupid) from p_books_rcp where br_bid=?  and br_branchid=?");
			pst.setString(1, bookid);
			pst.setInt(2, branchId_G );
			rs = pst.executeQuery();
			if (rs.next())
				maxGroup = rs.getInt(1);
			maxGroup ++;
			try {rs.close();}catch(Exception e) {/**/}
			try {pst.close();}catch(Exception e) {/**/}
			 
			String updateSql = "update p_books_rcp "
					+ " set br_groupid=?, br_custid=? where br_rcp_no>=? and br_rcp_no<=? and br_bid=?"
					+ " and br_branchid=?";
//			System.out.println(updateSql);
//			System.out.println("maxGroup==>"+maxGroup);
//			System.out.println("rqs.getParameter(\"br_custid\")==>"+rqs.getParameter("br_custid"));
//			System.out.println("rqs.getParameter(\"startrange\")==>"+rqs.getParameter("startrange"));
//			System.out.println("rqs.getParameter(\"torange\")==>"+rqs.getParameter("torange"));
//			System.out.println("bookid==>"+bookid);
//			System.out.println("branchId_G==>"+branchId_G);
			pst = conn.prepareStatement(updateSql);
			pst.setInt(1, maxGroup );
			pst.setString(2, rqs.getParameter("br_custid") );
			pst.setString(3, rqs.getParameter("startrange") );
			pst.setString(4, rqs.getParameter("torange") );
			pst.setString(5, bookid);
			pst.setInt(6, branchId_G );
			pst.executeUpdate();
			conn.commit();
			 
		 }catch(Exception e) {
			 try {conn.rollback();}catch(Exception eRoll) {/**/}
			 e.printStackTrace();
		 }finally {
			 try {rs.close();}catch(Exception e) {/**/}
			 try {pst.close();}catch(Exception e) {/**/}
			 try {conn.close();}catch(Exception e) {/**/}
		 }
		 return "";
	 }
	 
 
	 @Override 
	 public String doDelete (HttpServletRequest rqs) {
		 System.out.println("here===");
		 String keyVal = rqs.getParameter(keyCol);
			PreparedStatement pst = null;
			String bookid = replaceVarsinString(" {bookid} ", arrayGlobals).trim();
			try {
				System.out.println("here===");
				int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
				pst = conn.prepareStatement("update p_books_rcp "
						+ "set br_groupid=0, br_custid=0 where br_bid=? "
						+ " and br_groupid=? and br_cid =0 and br_branchid=? ");
				System.out.println("here==="+branchId_G);
				pst.setString(1, bookid);
				pst.setString(2, keyVal );
				pst.setInt(3, branchId_G);
				pst.executeUpdate();
				conn.commit();
	
			} catch (Exception e) {
				try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
				logErrorMsg = "";
				e.printStackTrace();
			} finally {
				try {pst.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
			}
	
			return "";
	 }
}
