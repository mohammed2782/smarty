package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;
import smarty.db.mysql;
import com.app.util.Utilities;

public class SetupBranches extends CoreMgr{
	public SetupBranches () {
		MainSql = "select * from kbbranches";
		mainTable = "kbbranches";
		keyCol = "branch_id";
		canNew = true;
		canFilter = true;
		canDelete = true;
		canEdit = true;
		
		userDefinedGridCols.add("branch_name");
		userDefinedGridCols.add("branch_active");
		userDefinedGridCols.add("branch_code");
		userDefinedGridCols.add("branch_state");
		userDefinedGridCols.add("branch_must_pay_all");
		userDefinedGridCols.add("branch_createdby");
		userDefinedGridCols.add("branch_createddt");
		
		userDefinedColLabel.put("branch_name", "اسم الفرع");
		userDefinedColLabel.put("branch_active", "نشط");
		userDefinedColLabel.put("branch_code", "كود الفرع");
		userDefinedColLabel.put("branch_state", "المحافظة");
		userDefinedColLabel.put("branch_createdby", "انشيء بواسطة");
		userDefinedColLabel.put("branch_createddt", "تاريخ الانشاء");
		userDefinedColLabel.put("branch_must_pay_all", "وجوب دفع كل الوصولات ؟");
		
		userDefinedNewCols.add("branch_name");
		userDefinedNewCols.add("branch_active");
		userDefinedNewCols.add("branch_code");
		userDefinedNewCols.add("branch_state");
		userDefinedNewCols.add("branch_createdby");
		userDefinedNewColsDefualtValues.put("branch_createdby", new String[] {"{usid}"});
		userDefinedReadOnlyNewCols.add("branch_createdby");
		
		userDefinedLookups.put("branch_active", "select 'Y' , 'نعم' from dual union select 'N' , 'كلا' from dual ");
		userDefinedLookups.put("branch_must_pay_all", "select 'Y' , 'نعم' from dual union select 'N' , 'كلا' from dual ");
		userDefinedLookups.put("branch_state", "select st_code, st_name_ar from kbstate where st_active='Y' order by st_order");
		
		userDefinedNewColsHtmlType.put("branch_name", "TEXT");
		userDefinedNewColsHtmlType.put("branch_code", "TEXT");
		
		userDefinedColsMustFill.add("branch_name");
		userDefinedColsMustFill.add("branch_active");
		userDefinedColsMustFill.add("branch_code");
		userDefinedColsMustFill.add("branch_state");
		
		userDefinedEditCols.add("branch_name");
		userDefinedEditCols.add("branch_active");
		userDefinedEditCols.add("branch_state");
		userDefinedEditCols.add("branch_must_pay_all");
		
		userDefinedFilterCols.add("branch_name");
		userDefinedFilterLookups.put("branch_name", "select branch_name, branch_name from kbbranches where branch_active='Y'");
		userDefinedFilterColsHtmlType.put("branch_name", "DROPLIST");
		userDefinedFilterCols.add("branch_state");
		userDefinedLookups.put("branch_createdby", "select us_id, us_name from kbusers");
		
		userDefinedCaption = "فروع";
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
			pst = conn.prepareStatement("select * from kbbranches where branch_id=?");
			pst.setString(1, keyVal);
            rs = pst.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
            	for(int i=1 ; i<=rsmd.getColumnCount();i++) {
            		CoreUtilities.logChanges(conn, "KBBRANCHES", "branch_id", Integer.parseInt(keyVal), rsmd.getColumnName(i), rs.getString(rsmd.getColumnName(i)), "*",
							"delete", "الفروع", usid);
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
	

	@Override
	 public String doInsert (HttpServletRequest rqs , boolean commit) {
		String statusMsg= "Record has been Created!";
      int branchId =0;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs=null;		
		int usid = Integer.parseInt(replaceVarsinString("{usid}", arrayGlobals).trim());
		int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());

		try{
			
			conn = mysql.getConn();
			
			pst = conn.prepareStatement("insert into kbbranches "
										+ "(branch_name, branch_active, branch_code, branch_state, branch_createdby ) "
										+ " values (?, ?, ?, ?, ?)" , Statement.RETURN_GENERATED_KEYS);
			
			pst.setString(1, rqs.getParameter("branch_name"));
			pst.setString(2, rqs.getParameter("branch_active"));
			pst.setString(3, rqs.getParameter("branch_code"));
			pst.setString(4, rqs.getParameter("branch_state"));			
			pst.setInt(5, usid);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			rs.next();
			branchId = rs.getInt(1);
			
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
			
			// copy prices
			pst = conn.prepareStatement("insert into  kbstate ( st_name_ar ,st_name_en ,st_code ,"
					+ "st_order  ,st_charges ,st_ruralcharges ,st_agent_share,st_agent_share_rural,"
					+ "st_branch)  "
					+ "SELECT st_name_ar ,st_name_en , st_code ,st_order,st_charges ,st_ruralcharges,"
					+ "st_agent_share,st_agent_share_rural ,"+branchId
					+ " FROM kbstate where st_branch =1 ");
			pst.executeUpdate();
			try{pst.close();}catch(Exception e){/* ignore*/}
			
			// copy permissions
			pst = conn.prepareStatement("insert into kbpermission (p_rank_code, p_menuid, p_submenuids, p_branchid) "
					+ " select  p_rank_code, p_menuid, p_submenuids, ? from kbpermission where p_branchid = 1");
			pst.setInt(1, branchId);
			pst.executeUpdate();

			conn.commit();
		}catch(Exception e){
			/*log error*/
			statusMsg = "Error at record creation, error ("+e.getMessage()+")";
			try{conn.rollback();}catch(Exception ignoreE){}
			e.printStackTrace();
		}finally{
			try{pst.close();}catch(Exception e){/* ignore*/}
			try{conn.close();}catch(Exception e){/* ignore*/}
		}
		
		return statusMsg;
	}


}
