package com.app.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.app.beans.ActionsBean;
import com.app.beans.BranchPaymentBean;
import com.app.bussframework.FlowUtils;
import com.app.bussframework.QueueActionsParamsBean;
import com.app.bussframework.SingleQueue;
import com.app.bussframework.SingleQueueFactory;
import com.app.cases.CaseInformation;
import com.app.financials.AccountantBoxBean;
import com.app.financials.PaymentImpactOnSafe;
import com.app.financials.UtilitiesSafeFinancials;
import com.app.tickets.ChatBean;
import com.app.tickets.ChatMsgBean;

public class UtilitiesNafie extends Utilities {
	public DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
	private FlowUtils fu = new FlowUtils();
	private static final int HOURS_TO_MINUTES = 60;
	public void restoreCasesForcedDeliveredByManagement(Connection conn, int caseId, int userId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean alreadyPaymentMade = false;
		boolean wasReceivedFromAgent = false;
		try {
			// check if no payment made for customer, pickupagent, branch or agent.
			pst = conn.prepareStatement("select c_agentrtnid , " + 
					"(case " + 
					" when  c_pmtid>0 or c_pickupagentpmtid >0 or c_agentpmtid >0 or sum((case when cc_branchpmtid > 0 then 1 else 0 end))  > 0 " + 
					" then 'PAYMENT_MADE' " + 
					" else " + 
					" 'NO_PAYMENT' " + 
					" end) as pmt_made " + 
					"from p_cases join p_caseschain on cc_caseid = c_id "
					+ " where c_id = ? and q_stage = 'DLV' " + 
					"group by  c_pmtid, c_pickupagentpmtid, c_agentpmtid, c_id ");
			pst.setInt(1, caseId);
			rs = pst.executeQuery();
			if(rs.next()) {
				if(rs.getString("pmt_made").equalsIgnoreCase("PAYMENT_MADE"))
				alreadyPaymentMade = true;
				if(rs.getInt("c_agentrtnid")>0)
					wasReceivedFromAgent= true;
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			if (alreadyPaymentMade) {
				System.out.println("already made payment");
				;//throw error
			}else {
				// get previous state and stage
				pst = conn.prepareStatement("select q_stage, q_step , q_branch from p_queue_hist  where q_caseid = ? order by hist_id desc limit 1 ");
				pst.setInt(1, caseId);
				rs = pst.executeQuery();
				String prevStage="", prevStep = "";
				int prevBranch=0;
				if(rs.next()) {
					prevStage = rs.getString("q_stage");
					prevStep = rs.getString("q_step");
					prevBranch = rs.getInt("q_branch");
				}
				// check previous stage if RTN then allowrtnagent = 'Y', allowrtncustomer='Y'
				String allowrtnagent="N",  allowrtncustomer="N";
				if (prevStage.equalsIgnoreCase("RTN") || wasReceivedFromAgent) {
					allowrtnagent="Y";  
					allowrtncustomer="Y";
				}
				pst = conn.prepareStatement("update p_cases set c_alllowagentpay='N',"
						+ " c_allowcustpay='N', c_allowrtnagent =?, c_allowrtncustomer=?,c_paytodlvcheck = 'N'   where c_id=? ");
				pst.setString(1, allowrtnagent);
				pst.setString(2, allowrtncustomer);
				pst.setInt(3, caseId);
				pst.executeUpdate();
				// restore previous stage and step
				fu.forceCasesToQueue(conn, caseId, "RESTORE_PREV", userId, prevStage, prevStep, prevBranch , "");
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}
	public  static ArrayList<CaseInformation> getBranchRtnManifestCasesFullInfo(Connection conn, int a_manifestId)throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		 ArrayList<CaseInformation>  deliveryList= new  ArrayList<CaseInformation>();
		try {
			String sql = "select cc_liaisonagentid, cc_id, cc_frombranch, cc_tobranch, c_custid, c_specialcase,  c_rural,"
					+ "  date(c_createddt) as c_createddt , cust_name, c_id, c_rcv_name, c_rcv_hp1,c_rcv_state, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr , q_stage, q_step, "
					+ " (case when c_partial_return='Y' then c_partial_qtyrtn else c_qty end) as c_qty, c_rmk, c_shipment_cost,"
					+ "  c_receiptamt, c_partial_return, ifnull(c_fragile,'N') as c_fragile  , c_sendmoney, c_custreceiptnoori"
					+ " from p_cases  "
					+ " join p_caseschain on (c_id = cc_caseid ) "
					+ " left join kbcustomers on cust_id = c_custid "
					+ " left join kbstate on st_code = c_rcv_state and st_branch= cc_frombranch"
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where  cc_rtnmanifestId = ? ";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, a_manifestId);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo= new CaseInformation();
				caseInfo.setSpecialCase(rs.getString("c_specialcase"));
				caseInfo.setCurrentChainId(rs.getInt("cc_id"));
				caseInfo.setToBranchCode(rs.getInt("cc_tobranch"));
				caseInfo.setFromBranchCode(rs.getInt("cc_frombranch"));
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setSenderName(rs.getString("c_rcv_name"));
				caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setReceiptAmtIqd(rs.getLong("c_receiptamt"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setFragile(rs.getString("c_fragile"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setRural(rs.getString("c_rural"));
				caseInfo.setSenderId(rs.getInt("c_custid"));
				caseInfo.setLatestChainId(rs.getInt("cc_id"));
				caseInfo.setLiaisonAgent(rs.getInt("cc_liaisonagentid"));
				caseInfo.setStageCode(rs.getString("q_stage"));
				caseInfo.setStepCode(rs.getString("q_step"));
				caseInfo.setParentChainId(rs.getInt("cc_liaisonagentid"));
				deliveryList.add(caseInfo);
			}
			
			
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return deliveryList;
	}
	
	public void registerFollowUpAction(Connection aConn, int aCaseId, int aUserId, String aAction, String aRemark) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean sameEmp = false;
		try {
			pst = aConn.prepareStatement("select c_followupby from p_cases where c_followupby=? and c_id =?");
			pst.setInt(1, aUserId);
			pst.setInt(2, aCaseId);
			rs = pst.executeQuery();
			if (rs.next()) {
				if (rs.getInt("c_followupby") == aUserId)
					sameEmp = true;
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			if (sameEmp) {
				pst = aConn.prepareStatement("insert into p_cases_followup "
						+ "(cf_caseid, cf_userid, cf_decision_made, cf_notes)"
				+ " values (?		 , ?		, ?				  , ? )");
				pst.setInt(1, aCaseId);
				pst.setInt(2, aUserId);
				pst.setString(3, aAction);
				pst.setString(4, aRemark);
				pst.executeUpdate();
				
				try {rs.close();}catch(Exception e) {}
				try {pst.close();}catch(Exception e) {}
				
				pst = aConn.prepareStatement("update p_cases set c_provided_followup='Y' where c_followupby=? and c_id =?");
				pst.setInt(1, aUserId);
				pst.setInt(2, aCaseId);
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
	
	public int assignFollowUpCaseToEmp(Connection aConn, int aCaseId, int aUserId) throws Exception{
		PreparedStatement pst = null;
		int rowsUpdated = 0;
		try {
			pst = aConn.prepareStatement("update p_cases set c_followupby=?, c_started_followup_at=now() where c_id = ? and c_followupby=0");
			pst.setInt(1, aUserId);
			pst.setInt(2, aCaseId);
			rowsUpdated = pst.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {}
		}
		return rowsUpdated;
	}
	
	public String getTimeAgo(long aTimeInMinutes) {
		// Time difference in seconds
		
		long hours   = aTimeInMinutes/HOURS_TO_MINUTES;
		if (hours >= 24 )
			return null;
		
	
		if (aTimeInMinutes <60) {
			if (aTimeInMinutes<1)
				return "الأن";
			else if (aTimeInMinutes < 2)
	    	   return "قبل دقيفة واحدة";
			else 
	    	   return "قبل "+aTimeInMinutes+" دقيقة";
		}else if (hours<24) {
			if (hours < 2)
		    	   return "قبل ساعة واحدة";
		       else 
		    	   return "قبل "+hours+" ساعة";
		}
		return null;
		  
	}
	
	public String getCustomerStepName(Connection aConn, String aStageCode, String aStepCode) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		String stepName = "";
		try {
			pst = aConn.prepareStatement("select (case when stp_nameformobile is not null then stp_nameformobile else stp_name end) from kbstep"
					+ " where stp_stgcode = ? and stp_code = ?");
			pst.setString(1, aStageCode);
			pst.setString(2, aStepCode);
			rs = pst.executeQuery();
			if (rs.next()) {
				stepName = rs.getString(1);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return stepName;
	}
	
	
	public void makeCasePayCheckNoUtil(Connection conn, int caseId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
				pst = conn.prepareStatement("update p_cases set "
						+ " c_paytodlvcheck = 'N' where c_id =? ");
				pst.setInt(1, caseId);
				pst.executeUpdate();
				try {pst.close();}catch(Exception e) {}	
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	public ActionsBean getActionFullInfo (Connection conn, String actionCode, String stepCode, String stageCode) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		ActionsBean actionsBean = new ActionsBean();
		try {
			pst = conn.prepareStatement("select stpd_desc, ifnull(stpd_sendnotification,'N') as stpd_sendnotification"
					+ " , stpd_customer_title, stpd_customer_body from kbstep_decision "
					+ " join kbstep on stpd_stpid = stp_id where stpd_code=? and stp_code =? and stp_stgcode = ? ");
			pst.setString(1, actionCode);
			pst.setString(2, stepCode);
			pst.setString(3, stageCode);
			/*
			 * System.out.println("actionCode--->"+actionCode);
			 * System.out.println("stepCode--->"+stepCode);
			 * System.out.println("stageCode--->"+stageCode);
			 */
			rs = pst.executeQuery();
			while(rs.next()) {
				actionsBean.setText(rs.getString("stpd_desc"));
				actionsBean.setSendNotifications(rs.getString("stpd_sendnotification"));
				actionsBean.setNotificationBodyForCustomer(rs.getString("stpd_customer_body"));
				actionsBean.setNotificationTitleForCustomer(rs.getString("stpd_customer_title"));
				
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return actionsBean;
	}
	
	public void moveCaseInsideQueueFromAudit(Connection aConn, int aCaseId, String aStageCode, String aStepCode, 
			String aAction, int aUserId, int aUserBranchCode, String aQrmk) throws Exception{
		Utilities ut = new Utilities();
		try {
			/*CaseInformation ci = ut.getSinglCaseInformation(aConn, aCaseId+"");
			
			if (aUserBranchCode != ci.getCurrentBranch())
				return;
			*/
			SingleQueueFactory sqf = new SingleQueueFactory();
			SingleQueue sq = sqf.getSingleQueuObj(aStageCode, aStepCode);
			QueueActionsParamsBean queueActionsParamsBean = new QueueActionsParamsBean ();
			HashMap<Integer, String> actionsMap = new HashMap<Integer, String> ();
			HashMap<Integer,String> aQrmkMap = new HashMap<Integer,String>();
			aQrmkMap.put(aCaseId, aQrmk);
			ArrayList<Integer> cIdList = new ArrayList<Integer>();
			cIdList.add(aCaseId);
			actionsMap.put(aCaseId, aAction);
			sq.setActionsMap(actionsMap);
			sq.setcIdList(cIdList);
			sq.setQueueActionsParamsBean(queueActionsParamsBean);
			sq.processData(aConn, aUserId, 0, aStageCode, aStepCode, aQrmkMap);
			Notifications notifications = new Notifications();
			HashMap<String,String> caseInfo = ut.getCaseInfo(aConn, aCaseId);
			int agentId = Integer.parseInt(caseInfo.get("c_assignedagent"));
			HashMap<String,String> extraDataMap = new HashMap<String,String>();
			extraDataMap.put("caseid", aCaseId+"");
			//send notification to customer
			new Thread(() -> {
				try {
					sq.sendNotifications(cIdList, actionsMap, aStageCode, aStepCode,  new HashMap<Integer, String>(),
							 new HashMap<Integer, String>(), "CUSTOMER");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).start();
			
			//now send notification to agent
			ActionsBean actionBean = getActionFullInfo(aConn,aAction, aStepCode, aStageCode);
			HashMap<Integer, ArrayList<String>> usersIdOneSignalIdMap = 
					notifications.getDLVAgentOneSignalInfoToSendNotificationToPerCases(aCaseId);
			String title = "خدمة العملاء ("+actionBean.getText()+")";
			String msgBody = aQrmk;
			new Thread(() -> {
				try {
					notifications.sendNotificationToUser(usersIdOneSignalIdMap, "DLVAGENT", 
							title, msgBody,"caseid", aCaseId, extraDataMap
							,Integer.parseInt(caseInfo.get("ownerbranchcode")));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}).start();
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	public void forceCasesToBeDelivered(Connection conn, int caseId, int userId, String aRmk, int branchCode_) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean alreadyDelivered = false;
		int currentBranch = 0;
		try {
			// check if not in delivered
			pst = conn.prepareStatement("select q_stage, q_branch from p_cases where c_id =?");
			pst.setInt(1, caseId);
			rs = pst.executeQuery();
			if(rs.next()) {
				if (rs.getString("q_stage").equalsIgnoreCase("DLV")) {
					alreadyDelivered = true;
				}
				currentBranch = rs.getInt("q_branch");
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			if (!alreadyDelivered) {
				// if there is manifest id then remove it
				pst = conn.prepareStatement("update p_caseschain set "
						+ " cc_rtnmanifestid = 0 where cc_caseid =? ");
				pst.setInt(1, caseId);
				pst.executeUpdate();
				try {pst.close();}catch(Exception e) {}
				
				// if given to agent then agent must pay also
				pst = conn.prepareStatement("update p_cases set "
				+ "c_alllowagentpay=(case when c_dlvagent_manifestid>0 then 'Y' else 'N' end), "
				+ "c_allowcustpay='Y', c_allowrtnagent ='N', c_paytodlvcheck = 'Y'   where c_id=? ");
				pst.setInt(1, caseId);
				pst.executeUpdate();
				fu.forceCasesToQueue(conn, caseId, "FORCE_DLV", userId, "DLV", "FORCE_DLV", currentBranch , aRmk);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	public void closeCustomerRtnManifest (Connection conn, int manifestRtnId, int userId)throws Exception {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("update p_customer_return set acr_closed='Y', acr_closedby=? , acr_closeddate=now()  where acr_id=?");
			pst.setInt(1, userId);
			pst.setInt(2, manifestRtnId);
			pst.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {}
			
		}
	}
	
	public void closePickUpAgentRtnManifest (Connection conn, int manifestRtnId, int userId)throws Exception {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("update p_pickupagent_return set pir_closed='Y', pir_closedby=? , pir_closeddate=now()  where pir_id=?");
			pst.setInt(1, userId);
			pst.setInt(2, manifestRtnId);
			pst.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	
	public ArrayList<CaseInformation> getCasesAdvancedSearch (Connection conn, HashMap<String,String> searchParams)throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<CaseInformation> casesList = new ArrayList<CaseInformation>();
		if (searchParams.size()==0) return casesList;
		try {
			LinkedList<String> searchValsList = new LinkedList<String>();
			StringBuilder  sql = new StringBuilder(" select c_cust_rtnid, c_pickupagent_rtnid, q_branch, branch_name , "
					+ " stp_stgcode, stg_name, stp_name, c_id,"
			+ " DATE_FORMAT(dam_manifest_date,'%Y-%m-%e %r') as date_giventoagent, DATE_FORMAT(dam_manifest_date,'%W') as weekdayen, "
			+ " c_rcv_name, cust_name, c_allowrtncustomer,  "
			+ " c_custreceiptnoori, concat(st_name_ar,' - ', ifnull(cdi_name,'') ,c_rcv_addr_rmk) as address, c_agentshare,"
			+ " c_rcv_hp1, c_rcv_hp2 , c_receiptamt, c_receiptamt_usd, c_rmk, q_stage, q_step, stp_mobilestatuscolor ,c_qty,"
			+ "  (case when q_stage = 'DLV' then c_partial_qtyrtn else c_qty end) as rtnqty, DATE(c_createddt) AS c_createddt  "
			+ " from p_cases "
			+ " join kbbranches  on q_branch = branch_id "
			+ " join p_dlvagentmanifest on c_dlvagent_manifestid = dam_id "
			+ " join kbcustomers on cust_id = c_custid "
			+ " join kbstate on (c_rcv_state = st_code and st_branch = c_branchcode)  "
			+ " left join kbcity_district on cdi_id = c_rcv_district  "
			+ " left join kbstep on q_step = stp_code and stp_stgcode = q_stage "
			+ " left join kbstage on q_stage = stg_code  "
			+ " where 1=1 ");
			for (String key : searchParams.keySet()){
				sql.append(" and "+key+"=?");
			}
			sql.append(" order by c_id ");
			pst = conn.prepareStatement(sql.toString());
			int i = 1;
			for (String key : searchParams.keySet()){
				pst.setString(i, searchParams.get(key));
				i++;
			}
			rs = pst.executeQuery();
			CaseInformation ci ;
			String status="";
			while (rs.next()) {
				ci = new CaseInformation();
				status="";
				if (rs.getString("c_allowrtncustomer").equalsIgnoreCase("Y") ) {
					status ="راجع";
				}else if (rs.getString("q_stage").equalsIgnoreCase("DLV") ) {
					if (rs.getString("q_step").equalsIgnoreCase("PART_SUCC"))
						status ="تم التسليم - راجع جزئي";
				}
				ci.setStatus(status);
				ci.setCaseid(rs.getInt("c_id"));
				ci.setRmk(rs.getString("c_rmk"));
				ci.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
				ci.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				ci.setReceiverName(rs.getString("c_rcv_name"));
				ci.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				ci.setSenderName(rs.getString("cust_name"));
				ci.setLocationDetails(rs.getString("address"));
				ci.setReceiverHp1(rs.getString("c_rcv_hp1"));
				ci.setReceiverHp2(rs.getString("c_rcv_hp2"));
				
				ci.setStepCode(rs.getString("q_step"));
				ci.setStepName(rs.getString("stp_name"));
				ci.setStageName(rs.getString("stg_name"));
				ci.setBranchCode(rs.getInt("q_branch")+"");
				ci.setCurrentBranchName(rs.getString("branch_name"));
				ci.setCustReturnId(rs.getInt("c_cust_rtnid"));
				ci.setPickupAgentRtnId(rs.getInt("c_pickupagent_rtnid"));
				ci.setQty(rs.getInt("c_qty"));
				ci.setRtnQty(rs.getInt("rtnqty"));
				ci.setCreateddt(rs.getString("c_createddt"));
				ci.setAllowRtnCustRtn(rs.getString("c_allowrtncustomer"));
				casesList.add(ci);
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		return casesList;
	}
	
	
	public void updateSyncRtnCases (Connection conn, int acrId)throws Exception {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("update p_customer_return set acr_integrationsync='Y' where acr_id=? ");
			pst.setInt(1, acrId);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {	
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	public LinkedList <Integer> getRtnCasesPerPickUpAgentRtnId (Connection conn, int acrId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedList <Integer> rtnCasesIntegrationList = new LinkedList <Integer>();
		try {
			pst = conn.prepareStatement("select c_id from p_cases where c_pickupagent_rtnid=?");
			pst.setInt(1, acrId);
			rs = pst.executeQuery();
			while (rs.next()) {
				rtnCasesIntegrationList.add(rs.getInt("c_id"));
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return rtnCasesIntegrationList;
	}
	
	public LinkedList <Integer> getRtnCasesPerId (Connection conn, int acrId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedList <Integer> rtnCasesIntegrationList = new LinkedList <Integer>();
		try {
			pst = conn.prepareStatement("select c_id from p_cases where c_cust_rtnid=?");
			pst.setInt(1, acrId);
			rs = pst.executeQuery();
			while (rs.next()) {
				rtnCasesIntegrationList.add(rs.getInt("c_id"));
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return rtnCasesIntegrationList;
	}
	
	public String writeToFileServer(InputStream inputStream, String fileName, String updDir) throws Exception {
        String qualifiedUploadFilePath = updDir + fileName;
        Path folder = Paths.get(updDir);
        if (!Files.exists(folder))
        	System.out.println("does no existe-->"+folder);
        	
        	
        Path file = Files.createTempFile(folder, "-"+fileName,fileName);
        qualifiedUploadFilePath = file.getFileName().toString();
           // outputStream = new FileOutputStream(new File(qualifiedUploadFilePath));
            try (InputStream input = inputStream) {
			    Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}
        return qualifiedUploadFilePath;
    }
	

	
	public static ArrayList<BranchPaymentBean> getBranchPaymentsNotReceivedYet(Connection conn, int receiverBranch) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		ArrayList<BranchPaymentBean> branchPaymentList = new ArrayList<BranchPaymentBean>();
		BranchPaymentBean branchPaymentBean = new BranchPaymentBean();
		try {
			pst = conn.prepareStatement("select trans_rmk, branch_name, trans_id,"
					+ "  trans_initiated_in_branch_id, "
					+ " trans_amount_paid_actually_usd, trans_amount_paid_actually_iqd, "
					+ " trans_receipts_amt_iqd, trans_receipts_amt_usd , date(trans_createddt) as trans_createddt "
					+ " from p_fin_transactions "
					+ " join kbbranches on branch_id = trans_initiated_in_branch_id "
					+ " where trans_operationentity='BRANCH' and trans_entity_id=? "
					+ " and trans_operationcode in ('DEBT_SETTLE' , 'CASES') "
					+ " and trans_did_branch_receive='N' and trans_deleted='N' ");
			pst.setInt(1, receiverBranch);
			rs = pst.executeQuery();
			while (rs.next()) { 
				branchPaymentBean.setPaymentId(rs.getInt("trans_id"));
				branchPaymentBean.setPayerBranch(rs.getInt("trans_initiated_in_branch_id"));
				branchPaymentBean.setPayerBranchName(rs.getString("branch_name"));
				branchPaymentBean.setPaidAmountIqd(rs.getLong("trans_amount_paid_actually_iqd"));
				branchPaymentBean.setPaidAmountUsd(rs.getLong("trans_amount_paid_actually_usd"));
				branchPaymentBean.setReceiptsAmountIqd(rs.getLong("trans_receipts_amt_iqd"));
				branchPaymentBean.setReceiptsAmountUsd(rs.getLong("trans_receipts_amt_usd"));
				branchPaymentBean.setPaymentDate(rs.getString("trans_createddt"));
				branchPaymentBean.setPayerBranchRmk(rs.getString("trans_rmk"));
				branchPaymentList.add(branchPaymentBean);
				branchPaymentBean = new BranchPaymentBean();
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return branchPaymentList;
	}
	
	/**
	 * Nafie
	 */
	public int generatePickUpManifestIdForCases(Connection conn,int masterCustId, int userId, int branchId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		int manifestId=0;
		int ctr = 0;
		try {
			//check first if there is path already
			pst = conn.prepareStatement("select distinct c_pickupmanifest as manifestid "
					+ " from p_cases "
					+ " where c_mastercustid =? and c_pickupmanifest != 0  and q_stage = 'NEWCUSTLOGI' "
					+ " and q_step = 'READYTOPICKUP' and q_status ='ACTV'");
			pst.setInt(1,masterCustId);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getInt("manifestid")>0) {
					manifestId = rs.getInt("manifestid");
					ctr++;
				}
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			if (ctr >1) {
				throw new Exception ("يوحد اكثر من رقم منفيست لنفس الوصولات  في مرحلة جاهز للاستلام");
			}
			if (manifestId == 0) {//generate Id
				pst = conn.prepareStatement("insert into p_pickupmanifests "
						+ "  (pmf_mastercustomer, pmf_branchid, pmf_manifestdate, pmf_createdby)"
				   + " values(?					, ?			  , now()			, ?)", 
						Statement.RETURN_GENERATED_KEYS);
				pst.setInt(1, masterCustId);
				pst.setInt(2, branchId);
				pst.setInt(3, userId);
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				rs.next();
				manifestId = rs.getInt(1);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return manifestId;
	}
	
	/**
	 * Nafie
	 */
	public void assignPickUpManifestIdToCases (Connection conn, int pickUpManifestId,  ArrayList<Integer> cases) throws Exception {
		PreparedStatement pst = null;
		try {
			//check first if there is path already
			pst = conn.prepareStatement("update p_cases set c_pickupmanifest=? where c_id=? ");
			for (int caseId : cases) {
				pst.setInt(1, pickUpManifestId);
				pst.setInt(2, caseId);
				pst.addBatch();
			}
			pst.executeBatch();
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	/**
	 * Nafie
	 */
	public ArrayList<CaseInformation> getReturnableSingleReceiptInfoInQueue (Connection conn, String c_custreceiptnoori, int branchCode) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		CaseInformation caseInformation = null;
		boolean foundCaseInStore = false;
		ArrayList<CaseInformation> ciList = new ArrayList<CaseInformation>();
		try {
			String sql = "select c_partial_qtyrtn, cc_frombranch, c_branchcode, branch_name, q_stage, q_step, "
					+ " c_id,cust_name, c_agentrtnid, c_receiptamt, c_receiptamt_usd ,c_custreceiptnoori, q_branch,  "
					+ " concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) as address "
					+ " from p_cases  "
					+ " join kbstate on (c_rcv_state = st_code and st_branch=?)   "
					+ " join kbcustomers on cust_id = c_custid "
					+ " join kbbranches on c_branchcode = branch_id "
					+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district)   "
					+ " left join p_caseschain on cc_caseid = c_id and  cc_tobranch =?  "
					+ " where q_branch=? and q_stage in ('BRANCHES', 'CNCL')"
					+ "  and q_step in ('RTN_INSTORE', 'RTN_INSTORE_WAITLIAISON') "
					+ "  and q_status ='ACTV'  "
					+ " and c_custreceiptnoori=? and c_cust_rtnid=0  ";
			/*
			 * System.out.println(sql); System.out.println(branchCode);
			 * System.out.println(c_custreceiptnoori);
			 */
			pst = conn.prepareStatement(sql);
			pst.setInt(1, branchCode);
			pst.setInt(2, branchCode);
			pst.setInt(3, branchCode);
			pst.setString(4, c_custreceiptnoori);
			rs = pst.executeQuery();
			while (rs.next()) {
				foundCaseInStore = true;
				caseInformation = new CaseInformation();
				caseInformation.setOrigintingBranch(rs.getInt("c_branchcode"));
				caseInformation.setOriginatinBranchName(rs.getString("branch_name"));
				caseInformation.setStageCode(rs.getString("q_stage"));
				caseInformation.setStepCode(rs.getString("q_step"));
				caseInformation.setCaseid(rs.getInt("c_id"));
				caseInformation.setSenderName(rs.getString("cust_name"));
				caseInformation.setPartialRtn_Qty(rs.getInt("c_partial_qtyrtn"));
				caseInformation.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
				caseInformation.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInformation.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInformation.setCurrentBranch(rs.getInt("q_branch"));
				caseInformation.setLocationDetails(rs.getString("address"));
				caseInformation.setAgentRtnId(rs.getInt("c_agentrtnid"));
				caseInformation.setPartialRtnCCToBranch(rs.getInt("cc_frombranch"));
				ciList.add(caseInformation);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return ciList;
	}
	
	
	
	public void doUpdateSuccessCasesCustomerPmtFsm(Connection conn, String paymentBillId, String userId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<CaseInformation> receiptsList = new ArrayList<CaseInformation>();
		String pmtDate = "";
		
		try {
			pst = conn.prepareStatement("select c_custreceiptnoori, date(cp_createddt) as cp_createddt, c_receiptamt, c_rcv_state  "
					+ " from p_cases join p_customer_payments on cp_id = c_pmtid "
					+ "  where c_pmtid  = ?");
			pst.setString(1, paymentBillId); 
			rs = pst.executeQuery();
			CaseInformation ci = null;
			while(rs.next()) {
				ci = new CaseInformation();
				ci.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				ci.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
				ci.setState(rs.getString("c_rcv_state"));
				receiptsList.add(ci);
				pmtDate = rs.getString("cp_createddt");
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			if (receiptsList.size()>0) {
				IntegrationFactory integrationFactory = new IntegrationFactory();
				SystemsIntegration si = integrationFactory.getSystemsIntegrationClass("FSM");
				si.updatePaidCasesInSource(conn, receiptsList, paymentBillId, pmtDate);
			}
				
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	
	public void doUpdateSuccessCasesPickupAgentFsm(Connection conn, String paymentBillId, String userId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<CaseInformation> receiptsList = new ArrayList<CaseInformation>();
		String pmtDate = "";
		try {
			pst = conn.prepareStatement("select c_custreceiptnoori, cppa_paymentdt, c_receiptamt, c_rcv_state, cppa_paymentdt from p_cases "
					+ " join p_customer_payments_pickupagents on cppa_id = c_pickupagentpmtid"
					+ "  where cppa_id = ?");
			pst.setString(1, paymentBillId);
			rs = pst.executeQuery();
			CaseInformation ci = null;
			while(rs.next()) {
				ci = new CaseInformation();
				ci.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				ci.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
				ci.setState(rs.getString("c_rcv_state"));
				receiptsList.add(ci);
				pmtDate = rs.getString("cppa_paymentdt");
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			if (receiptsList.size()>0) {
				IntegrationFactory integrationFactory = new IntegrationFactory();
				SystemsIntegration si = integrationFactory.getSystemsIntegrationClass("FSM");
				si.updatePaidCasesInSource(conn, receiptsList, paymentBillId, pmtDate);
			}
				
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	/**
	 * Naife
	 * Get the player id for notifications
	 */
	public ArrayList<String> getSingleUserPlayerIds(Connection conn, int aUserId)throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<String> listOfPlayerIds = new ArrayList<String>();
		try {
			//String InParmterizedString = PlaceQuestionMarksInSql(aUserList.size());
			pst = conn.prepareStatement("select upod_playerid from kbusers_onesignalplayerid where upod_userid = ? ");
			pst.setInt(1, aUserId);
			rs = pst.executeQuery();
			while(rs.next()) {
				listOfPlayerIds.add(rs.getString("upod_playerid"));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return listOfPlayerIds;
	}
	public  String PlaceQuestionMarksInSql(final int params) {
	    // Create a comma-delimited list based on the number of parameters.
	    final StringBuilder sb = new StringBuilder(
	        String.join(", ", Collections.nCopies(params, "?")));

	    return sb.toString();
	}
}
