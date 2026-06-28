package com.app.advancedsetup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;
import smarty.db.mysql;

import com.app.util.Utilities;

public class usersBranchesPermissions extends CoreMgr{
	public usersBranchesPermissions() {
		MainSql = "select * from kbusers_branches_r";
		mainTable = "kbusers_branches_r";
		keyCol = "ubr_id";
		
		canNew = true;
		canFilter = true;
		canDelete = true;
		
		userDefinedFilterCols.add("ubr_userid");
		userDefinedFilterCols.add("ubr_branchid");
		userDefinedFilterColsHtmlType.put("ubr_branchid", "DROPLIST");
		userDefinedFilterColsHtmlType.put("ubr_userid", "DROPLIST");
		
		userDefinedNewCols.add("ubr_userid");
		userDefinedNewCols.add("ubr_branchid");
		userDefinedNewCols.add("ubr_userrank");
		userDefinedNewCols.add("ubr_createdby");
		
		userDefinedNewColsHtmlType.put("ubr_branchid", "DROPLIST");
		userDefinedNewColsHtmlType.put("ubr_userrank", "DROPLIST");
		userDefinedNewColsHtmlType.put("ubr_userid", "DROPLIST");
		
		
		userDefinedNewColsDefualtValues.put("ubr_createdby", new String[] {"{usid}"});
		userDefinedHiddenNewCols.add("ubr_createdby");
		
		userDefinedGridCols.add("ubr_userid");
		userDefinedGridCols.add("ubr_branchid");
		userDefinedGridCols.add("ubr_userrank");
		userDefinedGridCols.add("ubr_createdby");
		userDefinedGridCols.add("ubr_createddt");
		
		
		userDefinedColLabel.put("ubr_userid", "اسم المستخدم");
		userDefinedColLabel.put("ubr_branchid", "الفرع");
		userDefinedColLabel.put("ubr_userrank", "المرتبة");
		userDefinedColLabel.put("ubr_createdby", "انشأ بواسطة");
		userDefinedColLabel.put("ubr_createddt", "");
		
		
		userDefinedLookups.put("ubr_userrank", "select rank_code, rank_name_ar from kbrank");
		userDefinedNewLookups.put("ubr_userrank", "!select rank_code, rank_name_ar from kbrank where '{ubr_userid}'!=''");
		userDefinedNewLookups.put("ubr_userid", "select us_id, us_name from kbusers where us_rank not in('DLVAGENT', 'PICKUPAGENT','MASTERCUSTOMER')"
												+ " and us_active='Y'");
		userDefinedLookups.put("ubr_userid", "select us_id, us_name from kbusers where us_rank not in('DLVAGENT', 'PICKUPAGENT','MASTERCUSTOMER')"
				+ " and us_active='Y'");
		userDefinedNewLookups.put("ubr_branchid", "!select branch_id, branch_name from kbbranches  where branch_active='Y' "
				+ "and branch_id not in(select ubr_branchid from kbusers_branches_r where ubr_userid='{ubr_userid}' union select us_branchcode from kbusers where us_id='{ubr_userid}' )");
		userDefinedLookups.put("ubr_branchid", "select branch_id, branch_name from kbbranches");
		userDefinedLookups.put("ubr_createdby", "select us_id, us_name from kbusers");
		
		userDefinedCaption = "صلاحيات المستخدم للفروع";
		
		userDefinedColsMustFill.add("ubr_userid");
		userDefinedColsMustFill.add("ubr_branchid");
		userDefinedColsMustFill.add("ubr_userrank");
	}
	 @Override 
	 public String doInsert (HttpServletRequest rqs , boolean commit) {
		 Connection conn = null;
		 PreparedStatement pst = null;
		 ResultSet rs = null;
		 boolean userInBranch = false;
		 String userId = replaceVarsinString(" {usid} ", arrayGlobals).trim();
		 String Msg = "";
		 try {
			 conn = mysql.getConn();
			 inputMap_ori = filterRequest(rqs);
			 Msg = "المستخدم موجود فعلاً في هذا الفرع";
			 pst = conn.prepareStatement("select 1 from kbusers_branches_r where ubr_userid=? and ubr_branchid=? "
			 		+ "union "
			 		+ " select 1 from kbusers where us_id=? and us_branchcode=? ");
			 pst.setString(1, inputMap_ori.get("ubr_userid")[0]);
			 pst.setString(2, inputMap_ori.get("ubr_branchid")[0]);
			 pst.setString(3, inputMap_ori.get("ubr_userid")[0]);
			 pst.setString(4, inputMap_ori.get("ubr_branchid")[0]);
			 rs = pst.executeQuery();
			 if(rs.next()) {
				 userInBranch = true;
			 }
			 try {rs.close();}catch(Exception e) {/**/}
			 try {pst.close();}catch(Exception e) {/**/}
			 if(!userInBranch) {
				 pst = conn.prepareStatement("insert into kbusers_branches_r (ubr_userid , ubr_branchid, ubr_createdby, ubr_userrank) "
					 		+ " values (? , ? , ? , ?)");
				 pst.setString(1, inputMap_ori.get("ubr_userid")[0]);
				 pst.setString(2, inputMap_ori.get("ubr_branchid")[0]);
				 pst.setString(3, inputMap_ori.get("ubr_createdby")[0]);
				 pst.setString(4, inputMap_ori.get("ubr_userrank")[0]);
				 pst.executeUpdate();
				 conn.commit();
				 Msg = "تم الحفظ";
			 }
			 
		 }catch(Exception e) {
			 try {conn.rollback();}catch(Exception eRoll) {/**/}
			 e.printStackTrace();
		 }finally {
			 try {rs.close();}catch(Exception e) {/**/}
			 try {pst.close();}catch(Exception e) {/**/}
			 try {conn.close();}catch(Exception e) {/**/}
		 }
		 return Msg;
	 }
		@Override
		public String doDelete(HttpServletRequest rqs) {
			PreparedStatement pst = null;
			//System.out.println(keyCol);
			boolean creatRecordDelTabel = false;
			Utilities ut = new Utilities();
			keyVal = rqs.getParameter(keyCol);
			int usid = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
			ResultSet rs = null;
			try {
				pst = conn.prepareStatement("select * from kbusers_branches_r where ubr_id=?");
				pst.setString(1, keyVal);
	            rs = pst.executeQuery();
	            ResultSetMetaData rsmd = rs.getMetaData();
	            if (rs.next()) {
	            	for(int i=1 ; i<=rsmd.getColumnCount();i++) {
	            		CoreUtilities.logChanges(conn, "KBUSERS_BRANCHES_R", "ubr_id", Integer.parseInt(keyVal), rsmd.getColumnName(i), rs.getString(rsmd.getColumnName(i)), "*",
								"delete", "صلاحية المستخدم للفروع", usid);
	            	}
	            	//agentId = rs.getString("c_assignedagent");
	            	//receiptAmtDb = rs.getDouble("c_receiptamt");
	            }
	            try {rs.close();}catch(Exception e) {}
				try {pst.close();}catch(Exception ex) {}
				
				conn.commit();
				creatRecordDelTabel = true;
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				try{rs.close();}catch(Exception e){/*ignore*/}
				try{pst.close();}catch(Exception e){/*ignore*/}
			}
			if(creatRecordDelTabel)
				return super.doDelete(rqs);
			else
				return "Error";
		}
	

}
