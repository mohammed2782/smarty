package smarty.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;

import com.app.tablesbeans.CasesBean;

public class CoreUtilities {
	public static String getQuestionMarks(int aHowMany) {
		StringBuilder sb = new StringBuilder("");
		for(int i=1; i<=aHowMany; i++) {
			if (i>1) {
				sb.append(",");
			}		
			sb.append("?");
		}
		return sb.toString();
	}
	
	public static String getUpdateColumnsWithQuestionMarksMarks(LinkedList<String> colNamesList) {
		StringBuilder sb = new StringBuilder("");
		int i = 1;
		for(String colName : colNamesList) {
			if (i>1) {
				sb.append(",");
			}		
			sb.append(" "+colName+"=?");
			i++;
		}
		return sb.toString();
	}
	
	public void updateSingleDataOnTheFly(Connection a_conn, String a_tableName, String a_pkCol, int a_pkValue, String a_whichColumn, String a_newValue, String a_fromWhichPlace, int a_userId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String oldValue="";
		try {
			pst = a_conn.prepareStatement("select "+a_whichColumn+" from "+a_tableName+" where "+a_pkCol+" = ?");
			pst.setInt(1, a_pkValue);
			rs = pst.executeQuery();
			
			if(rs.next()) {
				oldValue = rs.getString(a_whichColumn);				
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			

			pst = a_conn.prepareStatement("update "+a_tableName+" set "+a_whichColumn+" = ?  where "+a_pkCol+" = ? ");
			pst.setString(1, a_newValue);
			pst.setInt(2, a_pkValue);
			pst.executeUpdate();
			// to record the update operation on log table
			logChanges(a_conn,a_tableName, a_pkCol, a_pkValue, a_whichColumn, oldValue, a_newValue, "update", a_fromWhichPlace, a_userId);
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	public static void logChanges(Connection a_conn, String tabelName, String keyColName,int keyColId, String colChanged, 
			String oldValue, String newValue, String action, String screenName, int usid) throws Exception {
		PreparedStatement pst = null;
		//System.out.println("usid = "+usid);
		try {
			pst = a_conn.prepareStatement("insert into log_changes (log_table, log_keycolname, log_keycolid, log_colnamechanged, "
					+ " log_old_value, log_new_value, log_actioname, log_screenname, log_actionby, log_action_timestamp) "
					+ " values (?,?,?,?,?,?,?,?,?,now())");
			pst.setString(1, tabelName);
			pst.setString(2, keyColName);
			pst.setInt(3, keyColId);
			pst.setString(4, colChanged);
			if(oldValue == null || oldValue.isEmpty())
				pst.setNull(5, java.sql.Types.NULL); 
			else
				pst.setString(5, oldValue);
			if(newValue == null || newValue.isEmpty())
				pst.setNull(6, java.sql.Types.NULL);
			else
				pst.setString(6, newValue);
			pst.setString(7, action);
			pst.setString(8, screenName);
			pst.setInt(9, usid);
			pst.executeUpdate();
		}catch (Exception e) {
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
	}
	
	/*
	 * @ changes comma seperated by seperator like : or , to list new method added
	 * on 15/Mar/2017 by Nafie
	 */
	public static ArrayList<String> SplitStringToArrayList(String StrWithSeperator, String seperator) {
		ArrayList<String> convertedList = new ArrayList<String>();
		if (StrWithSeperator != null && StrWithSeperator.trim() != null && !StrWithSeperator.trim().equals("")) {
			// System.out.println("StrWithSeperator===>"+StrWithSeperator);
			String[] myArr = StrWithSeperator.split(seperator.trim());
			for (int i = 0; i < myArr.length; i++)
				convertedList.add(myArr[i]);
		}
		return convertedList;
	}

	/*
	 * @ change array list string to 'str1','str2',..etc_
	 */
	public static StringBuilder getSingleQuoteCommaSeperated(ArrayList<String> array) {
		boolean first = true;
		StringBuilder sb = new StringBuilder("");
		for (String item : array) {
			if (!first)
				sb.append(",");

			sb.append("'" + item + "'");
			first = false;
		}
		return sb;
	}
}
