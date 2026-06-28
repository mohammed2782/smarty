package com.app.tickets;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

import com.app.util.Utilities;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;
import smarty.db.mysql;

public class SetupGroupHelpDeskEmpOnStates extends CoreMgr{
	public SetupGroupHelpDeskEmpOnStates(){
		MainSql = "select * from kb_helpdesk_staff_states_grouping where hssg_branchcode='{userstorecode}'";
		userDefinedCaption = "تقسيم خدمة العملاء على اساس المحافظات";
		
		canNew = true;
		canFilter =  true;
		canDelete = true;
		
		mainTable = "kb_helpdesk_staff_states_grouping";
		keyCol = "hssg_id";
		userDefinedGroupByCol = "hssg_empid";

		userDefinedGridCols.add("hssg_state");
		
		userDefinedNewCols.add("hssg_empid");
		userDefinedNewCols.add("hssg_state");
							
		userDefinedColLabel.put("hssg_empid", "الموظف");
		userDefinedColLabel.put("hssg_branchcode","فرع الموظف");
		userDefinedColLabel.put("hssg_state", "المحافظة المنوطة بالموظف");
		
		userDefinedNewColsHtmlType.put("hssg_empid", "DROPLIST");
		userDefinedNewColsHtmlType.put("hssg_state", "CHECKBOX");
		
		userDefinedNewColsDefualtValues.put("hssg_branchcode", new String[] {"{userstorecode}"});
	  
		userDefinedFilterCols.add("hssg_empid");
		userDefinedFilterCols.add("hssg_state");
		
		userDefinedLookups.put("hssg_state", "select st_code, st_name_ar From kbstate");
		userDefinedLookups.put("hssg_empid", "select us_id, us_name from kbusers"
				+ " where  us_rank in ('CALL_CENTER') and us_branchcode='{userstorecode}'"
				+ "  and us_active = 'Y' ");
		
		userDefinedColsMustFill.add("usagmo_us_id");
		userDefinedColsMustFill.add("usagmo_branchcode_ag");
		userDefinedColsMustFill.add("usagmo_ag_id");
	}
	
	@Override 
	 public String doInsert (HttpServletRequest rqs , boolean commit) {
		 Connection conn = null;
		 PreparedStatement pst = null;
		 int userId = Integer.parseInt(replaceVarsinString(" {usid} ", arrayGlobals).trim());
		 int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		 try {
			 conn = mysql.getConn();
			 inputMap_ori = filterRequest(rqs);
			 pst = conn.prepareStatement("insert into kb_helpdesk_staff_states_grouping "
			 + "(hssg_empid, hssg_state, hssg_branchcode, hssg_createdby) "
			 + " values ("+CoreUtilities.getQuestionMarks(4)+")");
			 for (String state : inputMap_ori.get("hssg_state")){
				 pst.setInt(1, Integer.parseInt(rqs.getParameter("hssg_empid")));
				 pst.setString(2, state);
				 pst.setInt(3, userstorecode);
				 pst.setInt(4, userId);
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
	public String doDelete(HttpServletRequest rqs) {
		String Msg = "";
		PreparedStatement pst = null;
		String keyVal = rqs.getParameter(keyCol);
		int userId = Integer.parseInt(replaceVarsinString(" {usid} ", arrayGlobals).trim());
		Utilities ut = new Utilities();
		try {
			pst = conn.prepareStatement("delete from kbuser_agent_mont where usagmo_id = ?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			//log
			System.out.println("keyVal----->"+keyVal);
			ut.logChanges(conn, "kbuser_agent_mont", "usagmo_id",
					Integer.parseInt(keyVal), "*", "ALL", "NONE", "delete", userDefinedCaption, userId);
			conn.commit();
		} catch (Exception e) {
			try {conn.rollback();} catch (Exception eRollBack) {eRollBack.printStackTrace();}
			e.printStackTrace();
			Msg = "Error";
			deleteErrorFlag = true;
		} finally {
			try {pst.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
		return Msg;
	}
}