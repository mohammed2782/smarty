package com.app.returnables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.bussframework.FlowUtils;
import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;

public class AgentReturnedItemsReceived extends CoreMgr{
		public AgentReturnedItemsReceived() {
			MainSql = "select '' as showdel, apr_id,apr_agentid, apr_createdby, apr_rmk, apr_barcode, "
					+ " '' as fake,'{userstorecode}' as userbranch, "
					+ " apr_createddt "
					+ " from p_agent_returns "
					+ " where apr_agentid={agentAccountReturnProcess} and apr_barcode='N' and apr_deleted='N' "
					+ " order by apr_id desc ";
			
			keyCol = "apr_id";
			mainTable = "p_agent_returns";
			
			//myhtmlmgr.tableClass = "table table-striped  table-bordered turquoise_table";

			// ///////////////
			userDefinedGridCols.add("apr_id");
			
			userDefinedGridCols.add("apr_createddt");
			userDefinedGridCols.add("apr_createdby");
			userDefinedGridCols.add("apr_rmk");
			userDefinedGridCols.add("fake");
			userDefinedGridCols.add("showdel");

			// //////////////
			userDefinedCaption = "الرواجع المستلمة من المندوب";
			userDefinedColLabel.put("apr_id", "رقم الأيصال");
			userDefinedColLabel.put("apr_createddt", "تاريخ الاستلام الفعلي");
			userDefinedColLabel.put("apr_rmk", " ملاحظات");
			userDefinedColLabel.put("apr_createdby", "أنشئ بواسطة ");
			userDefinedColLabel.put("fake", "طباعة ايصال الاستلام ");
			userDefinedColLabel.put("showdel", " ");
			//canDelete = true;
			userModifyTD.put("fake", "printPmtReceipt({apr_id},{userbranch})");
			userModifyTD.put("showdel", "showDel({apr_id})");
			//userModifyTD.put("receivedamount", "receivedAmount({apr_id})");
			userDefinedLookups.put("apr_createdby", "select us_id, us_name from kbusers");
			myhtmlmgr.refreshPageOnDelete = true;

		}// end of constructor customer_payment
		public String showDel (HashMap<String,String> hashy) {

			return "<td align='center' style='vertical-align: middle;'>"
			+"<button id='pmt_del_btn_"+hashy.get("saf_id")+"' type='button' "
			+ " onclick=\"link=false; "
			+ " var rs =doDeleteSmarty(this,'هل تريد حذف الراجع  المستلم ؟' ,'apr_id','"+hashy.get("apr_id")+"',"
					+ " 'com.app.returnables.AgentReturnedItemsReceived' ); return rs;\" class='btn btn-danger btn-xs'>"
			+ "<li class='fa fa-trash'></li></button></td>";
		}
		
		/*
		public String receivedAmount(HashMap<String, String> hashy) {
			Utilities ut = new Utilities();
			int agentPaidId = Integer.parseInt(hashy.get("apr_id"));
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
		public String printPmtReceipt(HashMap<String, String> hashy) {
			String btn = "<a href=\"../../AgentBackedPartialReturnSRVL?apr_id="+hashy.get("apr_id")+"&userbranch="+hashy.get("userbranch")+"\" "
					+ " class='btn btn-sm btn-warning' >طباعة أيصال المستلمات <i class=\"fa fa-print fa-lg\"></i></a>";
			return "<td>" + btn + "</td>";
		}

		@Override
		public String doDelete(HttpServletRequest rqs) {
			String keyVal = rqs.getParameter("apr_id");
			PreparedStatement pst = null;
			
			ResultSet rs = null;
			int userId = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
			int currentBranch = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
			String msg = "لا يمكن الحذف";
			FlowUtils fu = new FlowUtils();
			try {
				// check if cases is in CNCL stage or in (step = 'RTN_INSTORE_WAITLIAISON' and q_stage = 'BRANCHES')
				pst = conn.prepareStatement("select c_id , q_stage, q_step, q_branch , c_cust_rtnid from p_cases "
						+ " where  c_agentrtnid=?   ");
				pst.setString(1,keyVal);
				rs = pst.executeQuery();
				boolean casesMovedToOtherStagesOrBranches = false;
				while (rs.next()) {
					if (rs.getInt("c_cust_rtnid")>0) {
						casesMovedToOtherStagesOrBranches = true;
						continue;
					}
					if (rs.getInt("q_branch") != currentBranch) {
						casesMovedToOtherStagesOrBranches = true;
						continue;
					}
					if (rs.getString("q_stage").equalsIgnoreCase("BRANCHES") && rs.getString("q_step").equalsIgnoreCase("RTN_INSTORE_WAITLIAISON")) {
						fu.MoveDecisionStepNext(conn,rs.getInt("c_id"), "GOBACKRTNAGENT", userId, "BRANCHES", "RTN_INSTORE_WAITLIAISON", "");
					}else if (rs.getString("q_stage").equalsIgnoreCase("CNCL") && rs.getString("q_step").equalsIgnoreCase("RTN_INSTORE")) {
							fu.MoveDecisionStepNext(conn,rs.getInt("c_id"), "GO_RTN_WITH_AGENT", userId, "CNCL", "RTN_INSTORE", "");
					}else {
						// if the cases is not in one of the above stage, skip the update
						casesMovedToOtherStagesOrBranches = true;
						continue;
					}
					pst = conn.prepareStatement("update p_cases set  c_agentrtnid=0 where c_agentrtnid=? and c_id=?");
					pst.setString(1, keyVal);
					pst.setInt(2, rs.getInt("c_id"));
					pst.executeUpdate();
					pst.clearParameters();
					
			}
				
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
				if (!casesMovedToOtherStagesOrBranches) { // if all the cases in that manifest returned, then can delete
					pst = conn.prepareStatement("update p_agent_returns set apr_deleted='Y', apr_deleteddt=now(), apr_deletedby=? where apr_id=? ");
					pst.setInt(1, userId);
					pst.setString(2, keyVal);
					pst.executeUpdate();
					try {pst.close();} catch (Exception e) {}
				}
				
				
				conn.commit();

			} catch (Exception e) {
				try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
				logErrorMsg = "";
				e.printStackTrace();
			} finally {
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
			}

			return "";
		}// end of doDelete*/

	}
