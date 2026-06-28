package com.app.returnables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.bussframework.FlowUtils;
import smarty.core.CoreMgr;
import smarty.db.mysql;

public class OtherBranchsManifestSentReturns extends CoreMgr{
	public OtherBranchsManifestSentReturns() {
		MainSql = "select '' as showdel, rlam_id,rlam_agentid, rlam_createdby, rlam_rmk, '' as fake, rlam_frombranch, rlam_tobranch,  "
		+ " rlam_createddt, rlam_noofshipments "
		+ " from p_rtnliaisonagent_manifest where rlam_tobranch= {userstorecode} "
		+ " and rlam_deleted='N' and rlam_frombranch = {branchAccountReturnProcess}  "
		+ " order by rlam_id desc ";
		
		//myhtmlmgr.tableClass = "table table-striped  table-bordered turquoise_table";

		// ///////////////
		userDefinedGridCols.add("rlam_id");
		
		userDefinedGridCols.add("rlam_createddt");
		userDefinedGridCols.add("rlam_createdby");
		userDefinedGridCols.add("rlam_noofshipments");
		userDefinedGridCols.add("rlam_rmk");
		userDefinedGridCols.add("rlam_agentid");
		userDefinedGridCols.add("fake");
		userDefinedGridCols.add("showdel");

		// //////////////
		userDefinedCaption = "ارشيف منفيستات الرواجع المرسلة الى الفروع";
		userDefinedColLabel.put("rlam_id", "رقم كشف الراجع");
		userDefinedColLabel.put("rlam_agentid", "مندوب الأرتباط");
		userDefinedColLabel.put("rlam_createddt", "تاريخ الاستلام الفعلي");
		userDefinedColLabel.put("rlam_rmk", " ملاحظات");
		userDefinedColLabel.put("rlam_noofshipments", "عدد الشحنات");
		userDefinedColLabel.put("rlam_createdby", "انشئ بواسطة");
		userDefinedColLabel.put("fake", "طباعة ايصال الاستلام ");
		userDefinedColLabel.put("showdel", " ");
		//canDelete = true;
		userModifyTD.put("fake", "printRtnManifest({rlam_id},{rlam_tobranch},{rlam_agentid})");
		//userModifyTD.put("showdel", "showDel({rlam_id},{rlam_frombranch})");
		//userModifyTD.put("receivedamount", "receivedAmount({rlam_id})");

		//myhtmlmgr.refreshPageOnDelete = true;
		
		userDefinedLookups.put("rlam_createdby", "select us_id, us_name from kbusers");
		userDefinedLookups.put("rlam_agentid", "select us_id, us_name from kbusers");

	}// end of constructor customer_payment
//	public String showDel (HashMap<String,String> hashy) {
//		
//		boolean showDelete = true;
//		Connection conn = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try {
//			conn = mysql.getConn();
//			pst = conn.prepareStatement("select 1 from p_caseschain join p_cases on(c_id = cc_caseid) "
//					+ "where cc_rtnmanifestid = ? and ((q_branch != ?) or ( cc_qstatus_tobranch != 'ACTV')) limit 1 ");
//			pst.setString(1, hashy.get("rlam_id"));
//			pst.setString(2, hashy.get("rlam_frombranch"));
//			rs = pst.executeQuery();
//			if(rs.next())
//				showDelete = false;
//		}catch(Exception e) {
//			e.printStackTrace();
//			try {conn.rollback();}catch(Exception eRoll) {/**/}
//		}finally {
//			try {rs.close();}catch(Exception e) {}
//			try {pst.close();}catch(Exception e) {}
//			try {conn.close();}catch(Exception e) {}
//		}
//		if (showDelete)
//			return "<td align='center' style='vertical-align: middle;'>"
//					+"<button id='pmt_del_btn_"+hashy.get("rlam_id")+"' type='button' "
//					+ " onclick=\"link=false; "
//					+ " var rs =doDeleteSmarty(this,'هل تريد حذف الراجع المجهز لمندوب الارتباط ؟' ,'rlam_id','"+hashy.get("rlam_id")+"',"
//							+ " 'com.app.returnables.BranchManifestReturn' ); return rs;\" class='btn btn-danger btn-xs'>"
//					+ "<li class='fa fa-trash'></li></button></td>";
//		else
//			return "<td></td>";
// 
//	}
	
	/*
	public String receivedAmount(HashMap<String, String> hashy) {
		Utilities ut = new Utilities();
		int agentPaidId = Integer.parseInt(hashy.get("rlam_id"));
		Connection conn2 = null;
		double paidAmount = 0;
		try {
			conn2 = mysql.getConn();
			paidAmount = ut.calcAgentPaidAmount(conn2, agentPaidId);
		}catch (Exception e1) {
			e1.printStackTrace();
		}finally {
			try {conn2.close();}catch(Exception e) {}
		}
		
		return "<td>" + numFormat.format(paidAmount) + "</td>";
	}

	*/
	public String printRtnManifest(HashMap<String, String> hashy) {
	//../../PrintLiaisonAgentReturnManifestSRVL?rtnmanifestid=1399&frombranch=34&liaisonagentid=1321
	String btn = 
	"<a href=\"../../PrintLiaisonAgentReturnManifestSRVL?rtnmanifestid="+hashy.get("rlam_id")+"&frombranch="+hashy.get("rlam_tobranch")+"&liaisonagentid="+hashy.get("rlam_agentid")+"\" "
	+ " class='btn btn-xs btn-warning' >طباعة منفيست الراجع<i class=\"fa fa-print fa-lg\"></i></a>";
	return "<td>" + btn + "</td>";
	}

//	
//	@Override
//	public String doDelete(HttpServletRequest rqs) {
//		String keyVal = rqs.getParameter("rlam_id");
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		int userId = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
//		int userStorCode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
//		FlowUtils fu = new FlowUtils();
//		try {
//			// first backup the cases that hadd payment and the payment also
//	
//			pst = conn.prepareStatement("update p_rtnliaisonagent_manifest set rlam_deleted='Y', rlam_deleteddt=now(), rlam_deletedby=? where rlam_id=? ");
//			pst.setInt(1, userId);
//			pst.setString(2, keyVal);
//			pst.executeUpdate();
//			try {pst.close();} catch (Exception e) {}
//			
//			pst = conn.prepareStatement("select c_id, cc_id, q_step, q_stage from p_caseschain join p_cases on(cc_caseid = c_id ) "
//					+ " where cc_rtnmanifestid = ?");
//			pst.setString(1, keyVal);
//			rs = pst.executeQuery();
//			boolean evryThinkIsOk = true;
//			int caseId = 0;
//			while(rs.next()) {
//				if (rs.getString("q_step").equalsIgnoreCase("PART_SUCC") && rs.getString("q_stage").equalsIgnoreCase("DLV")) {
//					previousStageStepInChain(conn, rs.getInt("cc_id"), userId);
//				}else if (rs.getString("q_step").equalsIgnoreCase("RTN_MANIFEST_LIAISON") && rs.getString("q_stage").equalsIgnoreCase("BRANCHES")) {
//					fu.MoveDecisionStepNext(conn , rs.getInt("c_id"), "RTN_PREV_INSTORE_WAITLIAISON", userId , userStorCode,
//							"BRANCHES", "RTN_MANIFEST_LIAISON", "");
//				}else {
//					caseId = rs.getInt("c_id");
//					evryThinkIsOk = false;
//					break;	
//				}
//				
//			}
//			if(!evryThinkIsOk) {
//				throw new Exception("CaseId  = "+caseId+" in wrong step = "+rs.getString("q_step")+" please call Mr.nafi ");
//			}
//			
//			pst = conn.prepareStatement("update p_caseschain set  cc_rtnmanifestid=0, cc_rtnmanifestliaison_actiontakenby=0 where cc_rtnmanifestid=?");
//			pst.setString(1, keyVal);
//			pst.executeUpdate();
//			
//			conn.commit();
//
//		} catch (Exception e) {
//			try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
//			logErrorMsg = "";
//			e.printStackTrace();
//		} finally {
//			try {rs.close();} catch (Exception e) {}
//			try {pst.close();} catch (Exception e) {}
//		}
//
//		return "";
//	}// end of doDelete*/
	
	public void previousStageStepInChain(Connection conn, int chainId, int actionTakenBy) throws Exception{
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("update p_caseschain "
					+ " set cc_qaction_tobranch=? , cc_qactiontakenby_tobranch=?, cc_qstep_tobranch=?, cc_qenterdate_tobranch= now() where cc_id=? ");
			pst.setString(1, "RTN_PREV_INSTORE_WAITLIAISON");
			pst.setInt(2, actionTakenBy);
			pst.setString(3, "RTN_INSTORE_WAITLIAISON");
			pst.setInt(4, chainId);
			int check = pst.executeUpdate();
			if (check == 0)
				throw new Exception("BranchManifestReturn, update not work ");
			
		}catch(Exception e) {
			throw e;
		}finally {
			//try{rs.close();}catch(Exception eRoll){}
			try{pst.close();}catch(Exception eRoll){}
		}
	}

}