package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import com.app.cases.CaseInformation;
import com.app.util.SplitingCasesUtils;

public class ExecuteSpecialRoutinesInFLow{
	
	public CaseInformation getCaseInfo(Connection conn, String caseId)throws Exception{
		PreparedStatement pst =null;
		ResultSet rs = null;
		
		CaseInformation ci = new CaseInformation();
		try {
			pst = conn.prepareStatement("select q_branch, q_action, q_action_takenby from p_cases where c_id = ?");
			pst.setString(1, caseId);
			rs = pst.executeQuery();
			if (rs.next()) {
				ci.setCurrentBranch(rs.getInt("q_branch"));
				ci.setAction(rs.getString("q_action"));
				ci.setActionTakenBy(rs.getInt("q_action_takenby"));
			}
			if (ci.getCurrentBranch()==0)
				throw new Exception ("ExecuteSpecialRoutinesInFLow , can't find currentBranch for c_id ="+caseId);
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return ci;
	}
	
	public int getCaseCurrentBranch (Connection conn, String caseId)throws Exception{
		PreparedStatement pst =null;
		ResultSet rs = null;
		int currentBranch =0;
		try {
			pst = conn.prepareStatement("select q_branch from p_cases where c_id = ?");
			pst.setString(1, caseId);
			rs = pst.executeQuery();
			if (rs.next())
				currentBranch = rs.getInt("q_branch");
			if (currentBranch==0)
				throw new Exception ("ExecuteSpecialRoutinesInFLow , can't find currentBranch for c_id ="+caseId);
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return currentBranch;
	}
	
	
	public void removePathsWhenResned (Connection conn, HashMap<String,String> hashy) throws Exception{
		PreparedStatement pst =null;
		ResultSet rs = null;
		int currentBranch =0;
		int minCcId = 0;
		int newLastestCcId = 0;
		try {
			
			currentBranch = this.getCaseCurrentBranch(conn, hashy.get("c_id"));
			pst = conn.prepareStatement("select min(cc_id) from p_caseschain where cc_caseid = ? and cc_frombranch=?");
			pst.setString(1, hashy.get("c_id"));
			pst.setInt(2, currentBranch);
			rs = pst.executeQuery();
			if (rs.next())
				minCcId = rs.getInt(1);
			
			// the following statement is wrong, because the case might resend in the same branch 
			/*if (minCcId==0)
				throw new Exception ("ExecuteSpecialRoutinesInFLow , can't find minCcId for c_id ="+hashy.get("c_id"));*/
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			// now delete if there is minCCid
			if (minCcId>0) {
				// trap it
				pst = conn.prepareStatement("insert into p_caseschain_deleted select p_caseschain.* , ? , now() "
						+ " from p_caseschain where  cc_caseid=?  and cc_id >=?");
				pst.setString(1, "MAIN-removePathsWhenResned");
				pst.setString(2, hashy.get("c_id"));
				pst.setInt(3, minCcId);
				pst.executeUpdate();
				try {pst.close();}catch(Exception e) {}
				
				pst = conn.prepareStatement("delete from p_caseschain where  cc_caseid=?  and cc_id >=?");
				pst.setString(1, hashy.get("c_id"));
				pst.setInt(2, minCcId);
				pst.executeUpdate();
				try {pst.close();}catch(Exception e) {}
				
				pst = conn.prepareStatement("select max(cc_id) from p_caseschain  where cc_caseid = ?");
				pst.setString(1, hashy.get("c_id"));
				rs = pst.executeQuery();
				if (rs.next()) {
					newLastestCcId = rs.getInt(1);
				}
				try {rs.close();}catch(Exception e) {}
				try {pst.close();}catch(Exception e) {}
				
				pst = conn.prepareStatement("update p_cases set c_lastchainid=? where c_id = ?");
				pst.setInt(1, newLastestCcId);
				pst.setString(2, hashy.get("c_id"));
				pst.executeUpdate();
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	public void openNewQForPartialRtn (Connection conn, HashMap<String,String> hashy) throws Exception{
		PreparedStatement pst =null;
		ResultSet rs = null;
		int currentBranch =0;
		int ccId = 0;
		
		try {
			
			CaseInformation ci = this.getCaseInfo(conn, hashy.get("c_id"));
			pst = conn.prepareStatement("select cc_id from p_caseschain where cc_caseid = ? and cc_tobranch=?");
			pst.setString(1, hashy.get("c_id"));
			pst.setInt(2, ci.getCurrentBranch());
			rs = pst.executeQuery();
			if (rs.next())
				ccId = rs.getInt("cc_id");
		
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			// if there is ccid
			if (ccId>0) {
				pst = conn.prepareStatement("update p_caseschain set cc_qstage_tobranch=?, cc_qstep_tobranch=?, cc_qstatus_tobranch=?, cc_qenterdate_tobranch=now()   where  cc_id =?");
				pst.setString(1, "BRANCHES");
				pst.setString(2, "RTN_INSTORE_WAITLIAISON");
				pst.setString(3, "ACTV");
				pst.setInt(4, ccId);
				pst.executeUpdate();
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}

	public void openNewCaseForPartialRtn (Connection a_conn, HashMap<String,String> a_hashy) throws Exception{
		PreparedStatement pst =null;
		ResultSet rs = null;
		try {
			long caseId = Long.parseLong(a_hashy.get("c_id"));
			long newSplittedCaseId = SplitingCasesUtils.createNewCaseIdByCopy(a_conn, caseId);
			long ccId = 0;
//			pst = a_conn.prepareStatement("select cc_id from p_caseschain where cc_caseid = ? limit 1 ");
//			pst.setLong(1, newSplittedCaseId);
//			rs = pst.executeQuery();
//			if (rs.next()) {
//				ccId = rs.getInt("cc_id");
//			}
//			try {rs.close();}catch(Exception e) {}
//			try {pst.close();}catch(Exception e) {}
			
			pst = a_conn.prepareStatement("update p_cases "
					+ " set q_stage = ?, q_step =?, "
					+ " q_enterdate=now(), c_parentid=?, c_seperated ='Y',"
					+ " c_allowrtnagent = 'Y', c_allowrtncustomer ='Y' , "
					+ " c_havereturnitem='Y' , c_alllowagentpay='N' ,"
					+ " c_allowcustpay = 'N'    where  c_id =?");
			// if there is ccid
			if (ccId>0) {;
				//pst.setString(1, "BRANCHES");
				//pst.setString(2, "RTN_INSTORE_WAITLIAISON");
			;
			}else {
				pst.setString(1, "AGENTOP");
				pst.setString(2, "RTN_WITHAGENT");
			}
			pst.setLong(3, caseId);
			pst.setLong(4, newSplittedCaseId);
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			
			// now update the original case
			pst = a_conn.prepareStatement("update p_cases "
			+ " set c_parent_of=?, c_allowrtnagent = 'N', c_allowrtncustomer ='N' where  c_id =?");
			pst.setLong(1, newSplittedCaseId);
			pst.setLong(2, caseId);
			pst.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	public void removeSplittedCases (Connection a_conn, HashMap<String,String> a_hashy) throws Exception{
		PreparedStatement pst =null;
		try {
			long caseId = Long.parseLong(a_hashy.get("c_id"));
			pst = a_conn.prepareStatement("delete from p_cases where c_parentid=? "
					+ " and c_agentrtnid = 0 and c_cust_rtnid = 0 ");
			pst.setLong(1, caseId);
			int impactedCases = pst.executeUpdate();
			if (impactedCases == 0) {
				throw new Exception ("لا يمكن العوده الى قيد التوصيل لان الوصل الراجع الجزئي تم استلامه من المندوب او تسليمه للعميل ");
			}
			try {pst.close();}catch(Exception e) {}
			
			pst = a_conn.prepareStatement("update  p_cases set c_parent_of=0 where c_id=?");
			pst.setLong(1, caseId);
			pst.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {}
		}
	}
}