package com.app.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.app.beans.BranchDeptToMyBranchBeen;
import com.app.beans.MasterCustomerShipmentBackBean;
import com.app.beans.MonthlyProfitDetailsBeen;
import com.app.cases.CaseInformation;
import com.app.financials.StandardFinCurrency;
import com.app.incomeoutcome.PickUpAgentPaymentBean;

import smarty.core.CoreUtilities;

public class UtilitiesFeqar extends Utilities{
	
	public PickUpAgentPaymentBean getPickUpAgentSharePaymentInfo (Connection conn, int pickUpAgentPmtId) throws Exception {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		PickUpAgentPaymentBean papb = new PickUpAgentPaymentBean();
		 ArrayList<CaseInformation>  casesList= new  ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement("select us_name, date(cspa_createddt) as cspa_createddt, cspa_amount_paid, cspa_rmk "
					+ " from p_payments_share_pickupagents join kbusers on us_id= cspa_pickupagentid  where cspa_id=?");
			pst.setInt(1, pickUpAgentPmtId);
			rs = pst.executeQuery();
			if (rs.next()) {
				papb.setPmtAmt(rs.getDouble("cspa_amount_paid"));
				papb.setPmtDate(rs.getString("cspa_createddt"));
				papb.setPickUpAgentName(rs.getString("us_name"));
				papb.setPmtRmk(rs.getString("cspa_rmk"));
			}
			try {rs.close();}catch(Exception e) {} 
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("select c_priceb4change, c_paidinadvance, c_changedprice, c_shipmentpaidbysender, cust_name, date(c_createddt) as c_createddt,  c_id as caseid, c_rcv_name, "
					+ " c_rcv_hp1,c_rcv_state, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr , "
					+ " c_qty, c_rmk,  c_partial_return ,"
					+" (q_stage) as status,"
					+ " c_custreceiptnoori, c_receiptamt, c_receiptamt_usd, c_shipment_cost,(c_receiptamt - c_shipment_cost)  as netamt, '' as totalnet, c_pickup_share "
					+ " from p_cases  "
					+ " join kbcustomers on cust_id= c_custid"
					+ " left join kbstate on st_code = c_rcv_state and st_branch = c_branchcode "
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where c_pickup_share_pmtid=?  order by cust_name,c_custreceiptnoori, c_createddt ");
			pst.setInt(1, pickUpAgentPmtId);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo= new CaseInformation();
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setCaseid(rs.getInt("caseid"));
				caseInfo.setReceiverName(rs.getString("c_rcv_name"));
				caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setAdvancedPaymentStatus(rs.getString("c_paidinadvance"));
				caseInfo.setReceiptAmtIqd(rs.getInt("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setChangedPrice(rs.getString("c_changedprice"));
				caseInfo.setReceiptAmtB4Change(rs.getDouble("c_priceb4change"));
				caseInfo.setStatus(rs.getString("status"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setShipmentChargesPaidBysender(rs.getString("c_shipmentpaidbysender"));
				caseInfo.setPickUpAgentShare(rs.getDouble("c_pickup_share"));
				casesList.add(caseInfo);
			}
			papb.setShipments(casesList);
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return papb;
		
	}
	
	public void reInsertPickUpAgentPartnerShareForAll(Connection conn, String pickupId, String storeCode, String partnerShare, String userId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {			
			pst = conn.prepareStatement("select mcust_id, mcust_pickup_partnershare from kb_mastercustomer where mcust_pickupagent=? and mcust_branchcode=? ");
			pst.setString(1, pickupId);
			pst.setString(2, storeCode);
			rs = pst.executeQuery();
			while(rs.next()) {
				CoreUtilities.logChanges(conn, 
						"KB_MASTERCUSTOMER", 
						"mcust_id", 
						rs.getInt("mcust_id"),	 
						"mcust_pickup_partnershare", 	
						rs.getString("mcust_pickup_partnershare"), 
						partnerShare, 
						"update", 
						"عملاء مندوب الإستلام", 
						Integer.parseInt(userId));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			pst = conn.prepareStatement("update kb_mastercustomer set mcust_pickup_partnershare=? where mcust_pickupagent=? and mcust_branchcode=?");
			pst.setString(1, partnerShare);
			pst.setString(2, pickupId);
			pst.setString(3, storeCode);
			pst.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	public double getAllDebtFromSafe(Connection conn, int currentBranch) throws Exception{
		double allDebt = 0.0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select sum(totdept) - sum(totpay) as total from("
					+ " select sum(case when saf_trantype = 'DB' then saf_amount_iqd else 0 end) as totdept, "
					+ " sum(case when saf_trantype = 'CR' then saf_amount_iqd else 0 end) as totpay"
					+ " from p_safe where saf_branchid=? and saf_tranname = 'CASH' group by saf_tranentity"
					+ " union "
					+ " select sum(case when saf_trantype = 'DB' then saf_amount_iqd else 0 end) as totdept,"
					+ " sum(case when saf_trantype = 'CR' then saf_amount_iqd else 0 end) as totpay"
					+ " from p_safe_hist where saf_branchid=? and saf_tranname = 'CASH' group by saf_tranentity"
					+ ")abc ");
			pst.setInt(1, currentBranch);
			pst.setInt(2, currentBranch);
			rs = pst.executeQuery();
			if(rs.next())
				allDebt = rs.getDouble(1);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		
		return allDebt;
	}
	
//	public void payDebtToSafe(Connection conn, int tranEntity, double payamt, String rmk, int actionTakenBy, int currentBranch) throws Exception{
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try {
//			pst = conn.prepareStatement("select (sum(totdept)-sum(totpay)) restamount from("
//					+ " select sum(case when saf_trantype = 'DB' then saf_amount_iqd else 0 end) as totdept,"
//					+ "  sum(case when saf_trantype = 'CR' then saf_amount_iqd else 0 end) as totpay "
//					+ " from p_safe where saf_branchid=? and saf_tranentity=? and saf_tranname = 'CASH' "
//					+ " union "
//					+ " select sum(case when saf_trantype = 'DB' then saf_amount_iqd else 0 end) as totdept,"
//					+ "  sum(case when saf_trantype = 'CR' then saf_amount_iqd else 0 end) as totpay "
//					+ " from p_safe_hist where saf_branchid=? and saf_tranentity=? and saf_tranname = 'CASH' "
//					+ ")abc ");
//			pst.setInt(1, currentBranch);
//			pst.setInt(2, tranEntity);
//			pst.setInt(3, currentBranch);
//			pst.setInt(4, tranEntity);
//			rs = pst.executeQuery();
//			if(rs.next()) {
//				if(payamt>rs.getDouble(1))
//					throw new Exception("In UtilitiesFeqar.payDebtToSafe payment greater than Dept");
//			}
//			try {rs.close();}catch(Exception e) {}
//			try {pst.close();}catch(Exception e) {}
//			
//			pst = conn.prepareStatement("insert into p_safe (saf_before_transaction, saf_amount_iqd, saf_trantype , saf_tranname    , saf_trandate, "
//														+ "	 saf_tranentity        , saf_createdby , saf_branchid , saf_createddt   , saf_rmk ) "
//												 + " values (?					   , ?			   , ?			  , ?			    , now()		 ,"
//												 		 + " ?					   , ?			   , ?			  ,	now()			,? )");
//			pst.setDouble(1, getSafeBalance(conn, currentBranch));
//			pst.setDouble(2, payamt);
//			pst.setString(3, "CR");
//			pst.setString(4, "CASH");
//			pst.setInt(5, tranEntity);
//			pst.setInt(6, actionTakenBy);
//			pst.setInt(7, currentBranch);
//			pst.setString(8, rmk);
//			pst.executeUpdate();
//		}catch (Exception e) {
//			e.printStackTrace();
//		}finally {
//			try {rs.close();}catch(Exception e) {}
//			try {pst.close();}catch(Exception e) {}
//		}
//	}
	
	/**
	 * Feqar
	 * @param conn
	 * @param tableName
	 * @param transSection
	 * @throws Exception
	 */
	public long getMoneyInOutBoxes (Connection conn, int currentBranch) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		long balance =0;
		try{
			pst = conn.prepareStatement("select sum(acb_currentbalunce) from p_accountantbox  where  acb_userbranchid =?");
			pst.setInt(1, currentBranch);
			rs = pst.executeQuery();
			if (rs.next()) {
				balance = rs.getLong(1);
			}

		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return balance;
	}
	
	public long getMoneyInInBoxes (Connection conn, int currentBranch) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		long balance =0;
		try{
			pst = conn.prepareStatement("select  sum(finiq) as finiq from("
					+ " select sum(ap_amtreceived) as finiq"
					+ " from p_agent_payments"
					+ " join kbusers on (ap_agentid = us_id and us_rank = 'DLVAGENT')"
					+ " where ap_safeid = 0 and us_branchcode=? and ap_safeoff = 'N'"
					+ " union"
					+ " select ifnull(sum(bp_receivedamt),0) as finiq from p_branch_payments"
					+ " where bp_received_branchid=? and bp_safeoff = 'N' and bp_received = 'Y' and bp_safeid = 0"
					+ " )abc");
			pst.setInt(1, currentBranch);
			pst.setInt(2, currentBranch);
			rs = pst.executeQuery();
			if (rs.next()) {
				balance = rs.getLong(1);
			}

		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return balance;
	}
	
	

	
	
	//used for load cols name from specific table to enter to translation table .
	public void getClosNamefromSpecificTable(Connection conn, String tableName, String transSection) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<String> arrayMapTableColsName = new ArrayList<String>();
		try {
			String sql = "select * from "+tableName+" limit 0";
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			for(int i=1 ; i<=rsmd.getColumnCount();i++) {
				arrayMapTableColsName.add(rsmd.getColumnName(i));
        	}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("insert into kbtranslator (trans_engdesc, trans_section) values (?,?) ");
			for (String cloName : arrayMapTableColsName){
				pst.setString(1, cloName);
				pst.setString(2, transSection);
				pst.executeUpdate();
				pst.clearParameters();
				//System.out.println("cloName = "+cloName+"   transSection = "+transSection);
			}
			conn.commit();
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	public CaseInformation getSinglCaseInformationFromBranch(Connection conn, int caseId, int currentBranch )throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		CaseInformation caseInfo = new CaseInformation();
		try {
			pst = conn.prepareStatement("select  q_stage, q_step, cc_id, cc_parentchainid, c_branchcode "
					+ " from p_cases join p_caseschain on cc_caseid = c_id and cc_frombranch=?   where c_id=? ");
			pst.setInt(1, currentBranch);
			pst.setInt(2, caseId);
			rs = pst.executeQuery();
			if (rs.next()) {
				caseInfo= new CaseInformation();
				caseInfo.setStepCode(rs.getString("q_step"));
				caseInfo.setStageCode(rs.getString("q_stage"));
				caseInfo.setCurrentChainId(rs.getInt("cc_id"));
				caseInfo.setParentChainId(rs.getInt("cc_parentchainid"));
				caseInfo.setFromBranchCode(rs.getInt("c_branchcode"));
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return caseInfo;
	}
	
	public int generateLiaisonAgentRtnManifest(Connection conn, int liaisonAgent, int fromBranch, int toBranch, String qStage, String qStep, int userId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		int rtnManifestId=0;
		try {
			//check first if there is manifest already
			rtnManifestId = getLiaisonAgentRtnManifestId(conn,  liaisonAgent,  fromBranch,  toBranch,  qStage,  qStep);
			if (rtnManifestId ==0) {
				pst = conn.prepareStatement( "insert into p_rtnliaisonagent_manifest "
						+ " (rlam_agentid , rlam_date, rlam_frombranch, rlam_tobranch, rlam_createdby )"
				+ " values	(?		      , now()	 , ?		      , ?			 , ?)", Statement.RETURN_GENERATED_KEYS);
		
				pst.setInt(1, liaisonAgent);
				pst.setInt(2, fromBranch);
				pst.setInt(3, toBranch);
				pst.setInt(4, userId);
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				rs.next();
				rtnManifestId = rs.getInt(1);
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return rtnManifestId;
	}
	
	public int getLiaisonAgentRtnManifestId(Connection conn, int liaisonAgent, int fromBranch, int toBranch, String qStage, String qStep) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		int rtnManifestId=0;
		int ctr = 0;
		try {
			//check first if there is path already
			pst = conn.prepareStatement("select distinct cc_rtnmanifestid as rtnmanifestid "
					+ " from p_cases "
					+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch=? and cc_tobranch=? ) "
					+ " where (q_branch=? and q_stage=? and q_step=? and q_status ='ACTV')");
			pst.setInt(1,fromBranch);
			pst.setInt(2,toBranch);
			pst.setInt(3,toBranch);
			pst.setString(4, qStage);
			pst.setString(5, qStep);
			rs = pst.executeQuery();
			int maxManifestId = 0;
			while (rs.next()) {
				if (rs.getInt("rtnmanifestid") >0) {
					rtnManifestId = rs.getInt("rtnmanifestid");
					ctr++;
					if (maxManifestId< rs.getInt("rtnmanifestid")) {
						maxManifestId =  rs.getInt("rtnmanifestid");
					}
				}
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			if (ctr >1) {
				pst = conn.prepareStatement(" update  "
						+ "  p_caseschain "
						+ " join p_cases on (c_id = cc_caseid and cc_frombranch=? and cc_tobranch=? )"
						+ " set cc_rtnmanifestid=? "
						+ " where (q_branch=? and q_stage=? and q_step=? and q_status ='ACTV')");
				pst.setInt(1,fromBranch);
				pst.setInt(2,toBranch);
				pst.setInt(3,maxManifestId);
				pst.setInt(4,toBranch);
				pst.setString(5, qStage);
				pst.setString(6, qStep);
				pst.executeUpdate();
				System.out.println("There are more than rtnManifestId in q_satge = "+qStage+" "
						+ " and q_step = "+qStep+" for liaizonId = "+liaisonAgent+", so update to the latest="+maxManifestId);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return rtnManifestId;
	}
	
	//calculate Shipment Profit
	/**
	 * FEQAR
	 * @param conn
	 * @param caseId
	 * @param fromBranch
	 * @throws Exception
	 */
	public void calcShipmentProfit(Connection conn, int caseId, int fromBranch) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		double pathCost = 0, agentShare = 0, shipmentCost = 0;
		int cc_id = 0;
		String qStep = "";
		try {
			pst = conn.prepareStatement("select ifnull(cc_pathcost,0) as cc_pathcost, c_agentshare,"
					+ " c_shipment_cost, q_step, ifnull(cc_id,0) as cc_id "
					+ " from p_cases "
					+ " left join p_caseschain on(c_id = cc_caseid and cc_frombranch = ?) "
					+ " where c_id = ?");
			pst.setInt(1, fromBranch);
			pst.setInt(2, caseId);
			rs = pst.executeQuery();
			if(rs.next()) {
				pathCost			= rs.getDouble("cc_pathcost");
				agentShare			= rs.getDouble("c_agentshare");
				shipmentCost		= rs.getDouble("c_shipment_cost");
				qStep				= rs.getString("q_step");
				cc_id				= rs.getInt("cc_id");
			}

			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			if(qStep.equalsIgnoreCase("ONWAY") && (pathCost>0 || agentShare>0)) {
				pst = conn.prepareStatement("select ifnull(cc_pathcost,0) as cc_pathcost, c_agentshare,"
						+ " c_shipment_cost"
						+ " from p_cases "
						+ " left join p_caseschain on(c_id = cc_caseid and cc_tobranch = ?) "
						+ " where c_id = ?");
				pst.setInt(1, fromBranch);
				pst.setInt(2, caseId);
				rs = pst.executeQuery();
				if(rs.next()) {
					pathCost			= rs.getDouble("cc_pathcost");
					agentShare			= rs.getDouble("c_agentshare");
					shipmentCost		= rs.getDouble("c_shipment_cost");
				}
				try {rs.close();}catch(Exception e) {/*ignore*/}
				try {pst.close();}catch(Exception e) {/*ignore*/}
				
				pst = conn.prepareStatement("update p_cases set c_shipmentprofit = ? where c_id = ?");
				if(pathCost>0)
					pst.setDouble(1, shipmentCost-pathCost);
				else
					pst.setDouble(1, shipmentCost-agentShare);
				pst.setInt(2, caseId);
				pst.executeUpdate();
			}else if(pathCost>0 && cc_id>0){
				pst = conn.prepareStatement("update p_caseschain set cc_shipmentprofit = ? where cc_id = ?");
				pst.setDouble(1, shipmentCost-pathCost);
				pst.setInt(2, cc_id);
				pst.executeUpdate();
			}else {
				//still shipment profit 0
				;
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		
	}
	
	public HashMap<String, Integer> getTotalShipmentsInfo(Connection conn, String custids) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String, Integer> totShipInfo = new HashMap<String, Integer>();
		
		try {
			String sql = "select sum(case when c_allowcustpay='Y'  and c_pmtid=0 then 1 else 0 end) as dlvd,"
					+ " sum(case when  c_cust_rtnid=0 and c_agentrtnid>0 and c_allowrtncustomer='Y' then 1 else 0 end) as cncl,"
					+ " sum(case when q_stage in ('CNCL', 'DLV') then 0 else 1 end) as underprocess"
					+ " from p_cases where c_custid in (?) and q_status = 'ACTV'";
			pst = conn.prepareStatement(sql);
			pst.setString(1, custids);
			rs = pst.executeQuery();
			if(rs.next()) {
				totShipInfo.put("dlvd", rs.getInt("dlvd"));
				totShipInfo.put("cncl", rs.getInt("cncl"));
				totShipInfo.put("underprocess", rs.getInt("underprocess"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return totShipInfo;
	}
	
	public boolean checkCaseFoundChangement(Connection conn, int caseId) throws Exception {
		boolean change = false;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select 1 from log_changes where log_table = 'P_CASES' and log_keycolname = 'c_id' and log_keycolid =?");
			pst.setInt(1, caseId);
			rs = pst.executeQuery();
			if(rs.next())
				change = true;
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return change;
	}
	
	public ArrayList<String> getCaseFinBranchesInfo(Connection conn, String caseId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<String> caseFinBranchesInfo = new ArrayList<String>(); 
		try {
			pst = conn.prepareStatement("select concat('محاسبة فرع : ',t1.branch_name, ' من فرع : ', t2.branch_name , ' برقم دفعة : ',cc_branchpmtid)  paymentdtls"
					+ "					 from p_cases  "
					+ "					 join p_caseschain on(c_id = cc_caseid)"
					+ "					 INNER join kbbranches t1 on(cc_frombranch=t1.branch_id)"
					+ "					 INNER join kbbranches t2 on(cc_tobranch=t2.branch_id)"
					+ "					 where c_id=? and cc_branchpmtid>0");
			pst.setString(1, caseId);
			rs = pst.executeQuery();
			while(rs.next()) {
				caseFinBranchesInfo.add(rs.getString("paymentdtls"));
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return caseFinBranchesInfo;
	}
	
	public boolean getSafeActiveCondition(Connection conn, int branchid)throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean active = false;
		try {
			pst = conn.prepareStatement("select kbcode from kbgeneral where kbcat1 = 'SAFE' and kbcat2 = 'ACTIVE' and kbcat3 = ?");
			pst.setInt(1, branchid);
			rs = pst.executeQuery();
			if(rs.next()) {
				if(rs.getString("kbcode").equalsIgnoreCase("YES"))
					active = true;
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return active;
	}
	
	public void changeSafeActiveCondition(Connection conn, boolean active, int usid, int branchid) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		String code = "NO", oldCode = "";
		int keyColId = 0;
		String disc = "لا";
		if(active) {
			code = "YES";
			disc = "نعم";
		}
		boolean switchFound = false;
		try {
			pst = conn.prepareStatement("select kbid, kbcode from kbgeneral where kbcat1 = 'SAFE' and kbcat2 = 'ACTIVE' and kbcat3 = ?");
			pst.setInt(1, branchid);
			rs = pst.executeQuery();
			if(rs.next()) {
				oldCode = rs.getString("kbcode");
				keyColId = rs.getInt("kbid");
				switchFound = true;
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			if(switchFound) {
				pst = conn.prepareStatement("update kbgeneral set kbcode = ?, kbdesc = ? where kbcat1 = 'SAFE' and kbcat2 = 'ACTIVE' and kbcat3 = ?");
				pst.setString(1, code);
				pst.setString(2, disc);
				pst.setInt(3, branchid);
				pst.executeUpdate();
			}else {
				pst = conn.prepareStatement("insert into kbgeneral (kbcat1, kbcat2, kbcode, kbdesc, kbcat3) values ('SAFE', 'ACTIVE', ?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
				pst.setString(1, code);
				pst.setString(2, disc);
				pst.setInt(3, branchid);
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				if (rs.next())
					keyColId = rs.getInt(1);
				else
					throw new Exception ("No general id generate");
			}
			CoreUtilities.logChanges(conn, "kbgeneral".toUpperCase(), "kbid", keyColId, "kbcode", oldCode, code, "update", "القاصة", usid);
		}catch (Exception e) {
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
			String sql = "select c_receiptamt_usd, cc_liaisonagentid, cc_id, cc_frombranch, cc_tobranch, c_custid, c_specialcase,  c_rural,"
					+ "  date(c_createddt) as c_createddt , cust_name, c_id, c_rcv_name, c_rcv_hp1,c_rcv_state, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr , q_stage, q_step, "
					+ " (case when c_partial_return='Y' then c_partial_qtyrtn else c_qty end) as c_qty, c_rmk, c_weight, c_shipment_cost,"
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
				caseInfo.setReceiptAmtIqd(rs.getInt("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getInt("c_receiptamt_usd"));
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
	
	
	public ArrayList<CaseInformation> getRtnItemsPerLiaisonAgentInRtnBranch(Connection conn, int driverid, int fromBranch, int toBranch)throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		 ArrayList<CaseInformation>  deliveryList= new  ArrayList<CaseInformation>();
		try {
//			String sql = "select cc_liaisonagentid, cc_id, cc_frombranch, cc_tobranch, c_custid, c_specialcase,  c_rural,"
//					+ "  date(c_createddt) as c_createddt , cust_name, c_id, c_rcv_name, c_rcv_hp1,c_rcv_state, "
//					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr , q_stage, q_step, "
//					+ " (case when c_partial_return='Y' then c_partial_qtyrtn else c_qty end) as c_qty, c_rmk, c_shipment_cost,"
//					+ "  c_receiptamt, c_receiptamt_usd, c_partial_return , c_custreceiptnoori"
//					+ " from p_cases  "
//					+ " join p_caseschain on (c_id = cc_caseid and cc_tobranch = ?) "
//					+ " left join kbcustomers on cust_id = c_custid "
//					+ " left join kbstate on st_code = c_rcv_state and st_branch=?"
//					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
//					+ " where ("
//					+ "  (q_branch=? and q_stage= 'BRANCHES' and q_step='RTN_MANIFEST_LIAISON' and q_status ='ACTV')"
//					+ "  or ( q_stage='DLV' and q_step='PART_SUCC' and cc_qstage_frombranch ='BRANCHES'"
//					+ "  and cc_qstep_frombranch='RTN_MANIFEST_LIAISON'"
//					+ " and cc_qstatus_frombranch = 'ACTV' )) and cc_frombranch=? ";
			
			String sql = "select cc_rtnmanifestid, cc_liaisonagentid, cc_id, cc_frombranch, cc_tobranch, c_custid, c_specialcase,  c_rural,"
					+ "  date(c_createddt) as c_createddt , cust_name, c_id, c_rcv_name, c_rcv_hp1,c_rcv_state, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr , q_stage, q_step, "
					+ " (case when c_partial_return='Y' then c_partial_qtyrtn else c_qty end) as c_qty, c_rmk, c_shipment_cost,"
					+ "  c_receiptamt, c_receiptamt_usd, c_partial_return , c_custreceiptnoori"
					+ " from p_cases  "
					+ " join p_caseschain on (c_id = cc_caseid and cc_tobranch = ?) "
					+ " left join kbcustomers on cust_id = c_custid "
					+ " left join kbstate on st_code = c_rcv_state and st_branch=?"
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where " 
					+ "  q_branch=? and q_stage= 'BRANCHES' and q_step='RTN_MANIFEST_LIAISON' and q_status ='ACTV' and cc_frombranch=? ";
			
		
			pst = conn.prepareStatement(sql);
			pst.setInt(1, toBranch);
			pst.setInt(2, toBranch);
			pst.setInt(3, toBranch);
			pst.setInt(4, fromBranch);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
//			
//			System.out.println("sql-->"+sql);
//			System.out.println("toBranch-->"+toBranch);
//			System.out.println("fromBranch-->"+fromBranch);
			
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
				caseInfo.setReceiptAmtUsd(rs.getLong("c_receiptamt_usd"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setRural(rs.getString("c_rural"));
				caseInfo.setSenderId(rs.getInt("c_custid"));
				caseInfo.setLatestChainId(rs.getInt("cc_id"));
				caseInfo.setLiaisonAgent(rs.getInt("cc_liaisonagentid"));
				caseInfo.setStageCode(rs.getString("q_stage"));
				caseInfo.setStepCode(rs.getString("q_step"));
				caseInfo.setParentChainId(rs.getInt("cc_liaisonagentid"));
				caseInfo.setCurrentBranchRtnManifestId(rs.getInt("cc_rtnmanifestid"));
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
	
	public boolean checkCasePaidToBranch(Connection conn, ArrayList<String> cidList, int fromBranch, int tobranch) throws Exception{
		boolean paid = false;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select cc_branchpmtid  from p_caseschain where cc_caseid = ? and cc_frombranch=? and cc_tobranch=? and cc_branchpmtid > 0");
			if(cidList.isEmpty())
				return paid;
			for (int i =0; i<cidList.size(); i++) { 
				pst.setString(1, cidList.get(i));
				pst.setInt(2, fromBranch);
				pst.setInt(3, tobranch);
				rs = pst.executeQuery();
				if(rs.next()) {
					if(rs.getInt("cc_branchpmtid")>0) {
						paid = true;
						break;
					}
				}
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return paid;
		
	}
	
	public boolean checkCasePaidToCust(Connection conn, ArrayList<String> cidList) throws Exception{
		boolean paid = false;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select c_pmtid  from p_cases where c_id = ? and c_pmtid>0");
			if(cidList.isEmpty())
				return paid;
			for (int i =0; i<cidList.size(); i++) { 
				pst.setString(1, cidList.get(i));
				rs = pst.executeQuery();
				if(rs.next()) {
					if(rs.getInt("c_pmtid")>0) {
						paid = true;
						break;
					}
				}
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return paid;
		
	}
	
	public boolean updateReturnQuantityInDB(Connection conn, int caseId, int rtnQty) throws Exception{
		boolean paid = false;
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("update p_cases set c_partial_qtyrtn=?, c_partial_return='Y' where c_id=?");
			pst.setInt(1, rtnQty);
			pst.setInt(2, caseId);
			pst.executeUpdate();
		}catch(Exception e) {
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {}
		}
		return paid;
		
	}
	
	public BranchDeptToMyBranchBeen getbranchDebtToMyBranchInfo (Connection conn, int senderBranch, int receiverBranch) throws Exception {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		BranchDeptToMyBranchBeen papb = new BranchDeptToMyBranchBeen();
		 ArrayList<CaseInformation>  casesList= new  ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement( "select sender.branch_name as senderbranch, receiver.branch_name as receiverbranch, count(*) totshipments,"
					+ " sum(case when (cc_pathcost>0)  then (c_receiptamt -  cc_pathcost) else  (c_receiptamt - c_shipment_cost)  end) as netamt"
					+ " from p_cases "
					+ " inner join kbbranches sender on(? = sender.branch_id)"
					+ " inner join kbbranches receiver on(? = receiver.branch_id)"
					+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch=? and cc_tobranch=? and cc_branchpmtid=0 and cc_branchrecievedpmt='N') "
					+ "	where q_stage = 'DLV' group by cc_frombranch ");
			pst.setInt(1, senderBranch);
			pst.setInt(2, receiverBranch);
			pst.setInt(3, receiverBranch);
			pst.setInt(4, senderBranch);
			rs = pst.executeQuery();
			if (rs.next()) {
				papb.setSenderBranchName(rs.getString("senderbranch"));
				papb.setReceiverBranchName(rs.getString("receiverbranch"));
				papb.setNetAmt(rs.getDouble("netamt"));
				papb.setTotShipments(rs.getInt("totshipments"));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			String sql = "select '' as netpaid,  '' as olddebt, '' as totshipmentcost, '' as selectedcases,'' as selectedcaseshidden, q_stage, q_step,'' status ,"
					+ " '' as pmtCheckBox,  'شحنات سلمت وراجعه فقط' as title, c_custid,c_custreceiptnoori,'' as totamt,'' as pmtrmk, '' as pmtdate, "
					+ " date(c_createddt) as c_createddt , concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr, c_id, "
					+ " c_rcv_name , c_rcv_hp1, '' as fromdate, '' as todate, c_changedprice, c_priceb4change, "
					+ " c_receiptamt, c_receiptamt_usd, cc_frombranch, cc_tobranch, c_rmk,  "
					+ " (case when (cc_pathcost>0)  then (c_receiptamt -  cc_pathcost) else  (c_receiptamt - c_shipment_cost)  end) as netamt,"
					+ " (case when (cc_pathcost>0) then cc_pathcost else c_shipment_cost end) as c_shipment_cost, "
					+ " cust_name, c_rcv_state, c_qty, date(cc_createddt) as cc_createddt"
					+ " from p_cases  "
					+ " join kbcustomers on cust_id= c_custid"
					+ " left join kbstate on st_code = c_rcv_state and st_branch=?"
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch=? and cc_tobranch=? and cc_branchpmtid=0 and cc_branchrecievedpmt='N') "
					+ "	where q_stage = 'DLV'  "; 
			
			pst = conn.prepareStatement(sql);
			pst.setInt(1, senderBranch);
			pst.setInt(2, receiverBranch);
			pst.setInt(3, senderBranch);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo= new CaseInformation();
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setReceiverName(rs.getString("c_rcv_name"));
				caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				
				caseInfo.setReceiptAmtIqd(rs.getInt("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getInt("c_receiptamt_usd"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setStatus(rs.getString("status"));
				caseInfo.setCreateddt(rs.getString("cc_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				casesList.add(caseInfo);
			}
			papb.setShipments(casesList);
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return papb;
	}
	
	public ArrayList<MonthlyProfitDetailsBeen> getMonthlyProfitPerDate(Connection conn, String branchCode, String fromdt, String todt) throws Exception{
		ArrayList<MonthlyProfitDetailsBeen> mpsb = new ArrayList<MonthlyProfitDetailsBeen>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("	select  sum(pr) as totinc, sum(ex) as totexp , (sum(pr)-sum(ex))  as netprofit, trandate , '' as fromdate, '' as todate from (" + 
					" select (case when trantype = 'shipprofit' then  amt else 0 end ) as pr," + 
					" (case when trantype = 'expense' then  amt else 0 end ) as ex," + 
					" trandate from (" + 
					" select 'shipprofit' as trantype, sum(case when q_branch=? then c_shipmentprofit else cc_shipmentprofit end) as amt ,"
					+ " date(c_createddt) trandate from p_cases"
					+ " left join p_caseschain on (c_id = cc_caseid and cc_frombranch=?)"
					+ " where ( q_stage = 'DLV' ) "  
					+ " and  c_createddt>=? and  c_createddt<=date(?) group by date(c_createddt) " + 
					" union " + 
					" select  'expense' as trantype , sum(ou_price) as amt , date(ou_date) trandate from p_outcomes"
					+ " where date(ou_date)>=date(?) and  date(ou_date)<=date(?)  group by date(ou_date) "
					+ "union "
					+ " select 'expense' as trantype, sum(saf_amount_iqd) as amt , date(saf_trandate) trandate "
					+ " from p_safe where date(saf_trandate)>=? and  date(saf_trandate)<=date(?) and"
							+ " saf_trantype='DB' and  saf_tranname='EXPANDITURE' group by date(saf_trandate)) lvl1) lvl2"
					+ " group by trandate");
			
			pst.setString(1, branchCode);
			pst.setString(2, branchCode);
			pst.setString(3, fromdt);
			pst.setString(4, todt);
			pst.setString(5, fromdt);
			pst.setString(6, todt);
			pst.setString(7, fromdt);
			pst.setString(8, todt);
			rs = pst.executeQuery();
			MonthlyProfitDetailsBeen pi;
			while (rs.next()) {
				pi = new MonthlyProfitDetailsBeen();
				pi.setProfitDate(rs.getString("trandate"));
				pi.setExpense(rs.getDouble("totexp"));
				pi.setIncome(rs.getDouble("totinc"));
				pi.setProfit(rs.getDouble("netprofit"));
				mpsb.add(pi);
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return mpsb;
	}
	
	public HashMap<String, Integer> calcMasterCustomerShipmentsInfo(Connection conn, String masterCustId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String, Integer> shipInfo = new HashMap<String, Integer>();
		
		try {
			String sql = "select sum(case when c_allowcustpay='Y' then 1 else 0 end) as dlvd,"
					+ " sum(case when c_allowrtncustomer='Y' then 1 else 0 end) as cncl,"
					+ " sum(case when q_stage not in ('CNCL', 'DLV') then 1 else 0 end) as underprocess"
					+ " from p_cases where c_mastercustid = ? and date(c_createddt) >= (current_date()-interval 30 day)";
			pst = conn.prepareStatement(sql);
			pst.setString(1, masterCustId);
			rs = pst.executeQuery();
			if(rs.next()) {
				shipInfo.put("dlvd", rs.getInt("dlvd"));
				shipInfo.put("cncl", rs.getInt("cncl"));
				shipInfo.put("underprocess", rs.getInt("underprocess"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return shipInfo;
	}
	
	public HashMap<String, Integer> calcBranchShipmentsInfo(Connection conn, String userBranch, String branchAcct) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String, Integer> shipInfo = new HashMap<String, Integer>();
		try {
			String sql = "select sum(case when q_stage = 'DLV' and cc_branchpmtid=0 and cc_branchrecievedpmt='N' then 1 else 0 end) as dlvd,"
					+ " sum(case when  cc_rtnmanifestid != 0 and c_allowrtncustomer='Y' then 1 else 0 end) as cncl,"
					+ " sum(case when q_stage in ('CNCL', 'DLV') then 0 when q_branch=? and c_branchcode=? then 1 else 0 end) as underprocess"
					+ " from p_cases "
					+ "join p_caseschain on (c_id = cc_caseid and cc_frombranch = ? and cc_tobranch = ? )"
					+ " where q_status = 'ACTV'";
			pst = conn.prepareStatement(sql);
			pst.setString(1, userBranch);
			pst.setString(2, branchAcct);
			pst.setString(3, branchAcct);
			pst.setString(4, userBranch);
			rs = pst.executeQuery();
			if(rs.next()) {
				shipInfo.put("dlvd", rs.getInt("dlvd"));
				shipInfo.put("cncl", rs.getInt("cncl"));
				shipInfo.put("underprocess", rs.getInt("underprocess"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return shipInfo;
	}
	
	public HashMap<String, Integer> calcDeptBranchToMyBranchShipmentsInfo(Connection conn, String userBranch, String branchAcct) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String, Integer> shipInfo = new HashMap<String, Integer>();
		try {
			String sql = "select sum(case when q_stage = 'DLV' and cc_branchpmtid=0 and cc_branchrecievedpmt='N' then 1 else 0 end) as dlvd,"
					+ " sum(case when  cc_rtnmanifestid = 0 and c_allowrtncustomer='Y' then 1 else 0 end) as cncl,"
					+ " sum(case when q_stage in ('CNCL', 'DLV') then 0 when q_branch=?  then 1 else 0 end) as underprocess"
					+ " from p_cases "
					+ "join p_caseschain on (c_id = cc_caseid and cc_frombranch = ? and cc_tobranch = ? )"
					+ " where q_status = 'ACTV'  ";
			pst = conn.prepareStatement(sql);
			pst.setString(1, branchAcct);
			pst.setString(2, userBranch);
			pst.setString(3, branchAcct);

			rs = pst.executeQuery();
			if(rs.next()) {
				shipInfo.put("dlvd", rs.getInt("dlvd"));
				shipInfo.put("cncl", rs.getInt("cncl"));
				shipInfo.put("underprocess", rs.getInt("underprocess"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return shipInfo;
	}
	
	public long getReceiverBranchBalanceWithSenderBranch(Connection conn, int senderBranch, int receiverBranch)throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		long balance = 0;
		try {
			pst = conn.prepareStatement("select sum(bp_debt) - sum(bp_credit) from p_branch_payments where bp_from_branchid=? "
					+ " and bp_received_branchid=? ");
			pst.setInt(1, receiverBranch);
			pst.setInt(2, senderBranch);
			rs = pst.executeQuery();
			if (rs.next())
				balance = rs.getLong(1);
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return balance;
	}
	
	public HashMap<String, Integer> calcPickUpAgentShipmentsInfo(Connection conn, String pickupAgentId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String, Integer> shipInfo = new HashMap<String, Integer>();
		
		try {
			String sql = "select sum(case when c_allowcustpay='Y' and c_settled !='FULL' then 1 else 0 end) as dlvd,"
					+ " sum(case when  c_cust_rtnid=0 and c_agentrtnid>0 and c_allowrtncustomer='Y' then 1 else 0 end) as cncl,"
					+ " sum(case when q_stage in ('CNCL', 'DLV') then 0 else 1 end) as underprocess"
					+ " from p_cases where c_pickupagent = ? and q_status = 'ACTV'";
			pst = conn.prepareStatement(sql);
			pst.setString(1, pickupAgentId);
			rs = pst.executeQuery();
			if(rs.next()) {
				shipInfo.put("dlvd", rs.getInt("dlvd"));
				shipInfo.put("cncl", rs.getInt("cncl"));
				shipInfo.put("underprocess", rs.getInt("underprocess"));
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return shipInfo;
	}
	
	public void changeNotificationControlActiveCondition(Connection conn, String kbId, String flag) throws Exception{
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("update kbgeneral set kbcode = ? where kbid = ?");
			pst.setString(1, flag);
			pst.setString(2, kbId);
			pst.executeUpdate();
		}catch (Exception e) {
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
	}
	
	public void reCalculateAgentShareBackDatePerAgent(Connection conn, int agentId) throws Exception{
		PreparedStatement pst = null, pstUpdateCases = null;
		ResultSet rs = null;
		try {
			boolean rural = false;
			double agentShare = 0;
			pstUpdateCases = conn.prepareStatement("update p_cases set c_agentshare=? where c_id=? ");
			pst = conn.prepareStatement("select c_id, q_branch, c_rcv_state, c_rcv_district, c_assignedagent "
					+ "  from p_cases where c_specialcase = 'N' and c_assignedagent=? and "
					+ " c_agentpmtid=0 and c_agentsharesettled='NO' ");
			pst.setInt(1, agentId);
			rs = pst.executeQuery();
			while(rs.next()){
				agentShare = calcAgentShipmentChargesShare(conn, rs.getInt("q_branch"), rs.getString("c_rcv_state"), 
						rs.getInt("c_rcv_district"), rural, rs.getString("c_assignedagent"));
				pstUpdateCases.setDouble(1, agentShare);
				pstUpdateCases.setInt(2, rs.getInt("c_id"));
				pstUpdateCases.executeUpdate();
				pstUpdateCases.clearParameters();
			}
		
			
			}catch(Exception e) {
				e.printStackTrace();
				throw e;
			}finally {
				try {rs.close();}catch(Exception e) {}
				try {pst.close();}catch(Exception e) {}
				try {pstUpdateCases.close();}catch(Exception e) {}
			}	
		
	}
	
	public boolean fixAllRuralPerState(Connection conn, String stateCode, int a_branchId)throws Exception{
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean ruralFlage = false;
		try {
			HashMap<Integer,String> districtRural = new HashMap<Integer,String>();
			pst = conn.prepareStatement("select cdi_id, ifnull(dbr_rural,'N') as dbr_rural from kbcity_district "
					+ "left join kbdistrict_branch_r on cdi_id = dbr_districtcode and dbr_branchid=? "
					+ " where cdi_stcode=? ");
			pst.setString(1, stateCode);
			pst.setInt(2, a_branchId);
			rs = pst.executeQuery();
			while (rs.next()) {
				districtRural.put(rs.getInt("cdi_id"), rs.getString("dbr_rural"));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("update p_cases set c_rural=? where c_rcv_state=? and c_rcv_district=?"
					+ " and !(c_pmtid >0  and  c_agentpmtid>0)");
			for (int districtId :  districtRural.keySet()) {
				pst.setString(1, districtRural.get(districtId));
				pst.setString(2, stateCode);
				pst.setInt(3, districtId);
				pst.addBatch();
				
			}
			pst.executeBatch();
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}

		
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return ruralFlage;
	}
	
	public void reCalculateShipmentChargeBackDatePerMasterCustomer(Connection conn, int masterCustId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			boolean rural = false;	
			pst = conn.prepareStatement("select c_rural, c_rcv_state, c_id, c_mastercustid, c_branchcode, c_custid  "
					+ " from p_cases where c_specialcase = 'N' and c_mastercustid=? and  c_pmtid=0 and c_settled='NO'");
			pst.setInt(1, masterCustId);
			rs = pst.executeQuery();
			while(rs.next()){
				rural = false;
				if (rs.getString("c_rural").equalsIgnoreCase("Y"))
						rural = true;
				
				updateShipmentCharges(conn,
						rs.getString("c_rcv_state"), 
						rural, 
						rs.getInt("c_id"), 
						rs.getInt("c_mastercustid"), 
						rs.getInt("c_custid"), 
						rs.getInt("c_branchcode"));
			}
			
			}catch(Exception e) {
				try {conn.rollback();}catch(Exception eRoll) {}
				e.printStackTrace();
				throw e;
			}finally {
				try {rs.close();}catch(Exception e) {}
				try {pst.close();}catch(Exception e) {}
				
			}	
		
	}
	
	private void updateShipmentCharges(Connection conn, String destState, boolean rural, int caseId, int masterCustid, int custId ,  int branchCode ) throws Exception{
		PreparedStatement pst = null;
		Double shipmentCost = 0.0;
		try {
			shipmentCost = calcShipmentChargesBasedOnDestCity(conn, destState, rural, masterCustid, custId, branchCode);
			pst = conn.prepareStatement("update p_cases set c_shipment_cost = ? where c_id=?");
			pst.setDouble(1, shipmentCost);
			pst.setInt(2, caseId);
			pst.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {pst.close();}catch(Exception e) {}
		}					
	}//end of method updateShipmentChargesShare
	
	
	public void updateRuralForSingleCase(Connection conn, int caseId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		int district = 0;
		boolean rural = false;
		
		try {
			int currentBranch = 0;
			pst = conn.prepareStatement("select c_rcv_district, q_branch from p_cases where c_id=?");
			pst.setInt(1, caseId);
			rs = pst.executeQuery();
			
			if(rs.next()) {
				district = rs.getInt("c_rcv_district");
				currentBranch = rs.getInt("q_branch");
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			if(district>0)
				rural = isRuralDistrict(conn, district, currentBranch);
			else
				rural = false;
			
			pst = conn.prepareStatement("update p_cases set c_rural = ? where c_id=?");
			if(rural)
				pst.setString(1, "Y");
			else
				pst.setString(1, "N");
			pst.setInt(2, caseId);
			pst.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}	
	}
	
	public void repairPcasesChainDeletedBySystemError (Connection conn )throws  Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			HashMap<String,CaseInformation> cids = new HashMap<String,CaseInformation>();
			CaseInformation cif ;
			pst = conn.prepareStatement("select c_id, q_branch, c_lastchainid, c_rcv_state from p_cases left join p_caseschain  on c_lastchainid = cc_id where q_branch != c_branchcode and cc_id is null");
			rs = pst.executeQuery();
			while (rs.next()) {
				cif = new CaseInformation();
				cif.setToBranchCode(rs.getInt("q_branch"));
				cif.setLatestChainId(rs.getInt("c_lastchainid"));
				cif.setCaseid(rs.getInt("c_id"));
				cif.setState(rs.getString("c_rcv_state"));
				cids.put(rs.getString("c_id"), cif);
				
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("select cc_id from p_caseschain join p_cases on(c_lastchainid = cc_id) "
					+ "where c_id = (select max(c_id) from p_cases join p_caseschain on (c_lastchainid = cc_id) where c_id<? and q_branch = ? and c_rcv_state = ?) and q_branch = ? and c_rcv_state = ? and c_lastchainid>0");
			
			HashMap<String, String> cidAndCahinId = new HashMap<String, String>();
			for (String cid:cids.keySet()) {
				pst.setString(1, cid);
				pst.setInt(2, cids.get(cid).getToBranchCode());
				pst.setString(3, cids.get(cid).getState());
				pst.setInt(4, cids.get(cid).getToBranchCode());
				pst.setString(5, cids.get(cid).getState());
				rs = pst.executeQuery();
				if(rs.next()) {
					cidAndCahinId.put(cid, rs.getString("cc_id"));
				}
				//System.out.println("c_id = "+cid);
				//System.out.println("cc_id = "+rs.getString("cc_id"));
				//System.out.println("---------------------------------------------");
				pst.clearParameters();
				try {rs.close();}catch(Exception e) {}
			}
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("select 1 from p_caseschain where cc_id = ?");
			int count = 0;
			int cc_id = 0;
			for (String cid:cids.keySet()) {
				pst.setInt(1, cids.get(cid).getLatestChainId());
				rs = pst.executeQuery();
				if(rs.next()) {
					cc_id = cids.get(cid).getLatestChainId();
					count ++;
				}
				if(count>=1)
					break;
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			if(count>=1)
				throw new Exception("cc_id found can not insert it where cc_id = "+cc_id);
			
			pst = conn.prepareStatement("insert into p_caseschain (cc_caseid, cc_frombranch, cc_tobranch, cc_liaisonagentid, cc_manifestid, "
					+ "cc_pathid, cc_pathcost, cc_createddt, cc_createdby, cc_qenterdate_tobranch, "
					+ "cc_shipmentprofit, cc_id)"
					+ " select ?, cc_frombranch, cc_tobranch, cc_liaisonagentid, cc_manifestid, "
					+ "cc_pathid, cc_pathcost, cc_createddt, cc_createdby, cc_qenterdate_tobranch, "
					+ "cc_shipmentprofit, ? from p_caseschain where cc_id =? ");
			
			for(String cid:cidAndCahinId.keySet()) {
				pst.setInt(1, Integer.parseInt(cid));
				pst.setInt(2, cids.get(cid).getLatestChainId() );
				pst.setString(3, cidAndCahinId.get(cid));
				pst.executeUpdate();
				pst.clearParameters();
			}
			
		}catch(Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {}
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}	
	}
	
	public MasterCustomerShipmentBackBean getpickUpAgentReturnBackedUpInfo (Connection conn, int pirId, int userBranchId) throws Exception {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		MasterCustomerShipmentBackBean cpb = new MasterCustomerShipmentBackBean();
		 ArrayList<CaseInformation>  casesList= new  ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement("select us_name, date(pir_createddt) as pir_createddt, pir_rmk "
					+ " from p_pickupagent_return join kbusers on pir_pickupagentid= us_id  where pir_id=?");
			pst.setInt(1, pirId);
			rs = pst.executeQuery();
			if (rs.next()) {
				cpb.setBackedDate(rs.getString("pir_createddt"));
				cpb.setPickupAgentName(rs.getString("us_name"));
				cpb.setBackedRmk(rs.getString("pir_rmk"));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("select c_parentid, c_usdpriceb4change, c_usdchangedprice, c_changedprice, c_priceb4change, cust_name, date(c_createddt) as c_createddt, c_id, c_rcv_name, c_rcv_hp1,c_rcv_state, "
			+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr , c_shipment_cost, c_receiptamt,  "
			+ " (case when c_partial_return='Y' then c_partial_qtyrtn else c_qty end) as qty, c_mastercustid, "
			+ " c_rmk, c_receiptamt, c_receiptamt_usd  , c_custreceiptnoori, q_stage, c_agentshare "
			+ " from p_cases  "
			+ " left join kbcustomers on cust_id = c_custid "
			+ " left join kbstate on st_code = c_rcv_state and st_branch=? "
			+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
			+ " where q_status != 'CLS' and c_pickupagent_rtnid=? order by c_custid, c_custreceiptnoori ");
			pst.setInt(1, userBranchId);
			pst.setInt(2, pirId);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo= new CaseInformation();
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setParentId(rs.getInt("c_parentid"));
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setMasterSenderId(rs.getInt("c_mastercustid"));
				caseInfo.setReceiptAmtIqd(rs.getInt("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setReceiptAmtB4Change(rs.getInt("c_priceb4change"));
				caseInfo.setReceiptAmtUsdB4Change(rs.getDouble("c_usdpriceb4change"));
				caseInfo.setChangedPrice(rs.getString("c_changedprice"));
				caseInfo.setChangedPriceUsd(rs.getString("c_usdchangedprice"));
				caseInfo.setStatus(rs.getString("q_stage"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setAgentShare(rs.getDouble("c_agentshare"));
				casesList.add(caseInfo);
			}
			cpb.setShipments(casesList);
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return cpb;
	}
	
	public boolean checkReturnbackedtoAgentOrCustomer(Connection conn, ArrayList<String> cidList) throws Exception {
		boolean backed = false;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			if (!cidList.isEmpty()) {
				pst = conn.prepareStatement("select 1 from p_cases where (c_pickupagent_rtnid != 0  or c_cust_rtnid != 0) and c_id = ?");
				for (int i =0; i<cidList.size(); i++) {
					pst.setString(1, cidList.get(i));
					rs = pst.executeQuery();
					if(rs.next()) {
						backed = true;
						break;
					}
					try {rs.close();}catch(Exception e) {}
					pst.clearParameters();
				}
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return backed;
		
	}
	
	
	
	public void changeCanPaytoDlvFlag(Connection conn, int caseId , int userId) throws Exception {
		PreparedStatement pst = null;
		try {
			  pst = conn.prepareStatement("update p_cases set  c_paytodlvcheck='Y', c_paytodlvcheckby=?, c_paytodlvcheckdt = now()  where c_id = ? ");  
			  pst.setInt(1, userId); 
			  pst.setInt(2, caseId);
			  pst.executeUpdate();			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {}
		}	
	
	}
	
	public boolean isFinalDestination(Connection conn, String stateCode,int comingFromBranch) throws Exception{
		boolean isFinal = true;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = conn.prepareStatement("select 1 from kbusers "
							+ " join kbpaths on (us_id = path_liaisonagent and path_state=? "
							+ " and path_frombranch=?  and path_frombranch !=path_tobranch) "
							+ " join kbbranches on (branch_id = path_tobranch and branch_active = 'Y')");
			pst.setString(1, stateCode);
			pst.setInt(2, comingFromBranch);
			rs = pst.executeQuery();
			if(rs.next()) {
				isFinal = false;
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return isFinal;
	}
	
	public HashMap<String, String> checkCaseEditeConditions(Connection conn, String fromScreen, int currentBranch, String caseid) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String, String> updateConditionsMap = new HashMap<String, String>();
		HashMap<String, String> updateConditionsMapFlag = new HashMap<String, String>();
		try {
			pst = conn.prepareStatement("select kbdesc, ifnull(kbdesc_ar,'') as kbdesc_ar, kbcat4, kbcode from kbgeneral where kbcat1='CASES' and kbcat2=? and kbcat3='UPDATECONDITION'");
			pst.setString(1, fromScreen);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("kbcode").equalsIgnoreCase("N") && rs.getString("kbdesc_ar").length()>0 )
					updateConditionsMap.put(rs.getString("kbcat4"), rs.getString("kbdesc_ar"));
				else if(rs.getString("kbcode").equalsIgnoreCase("Y"))
					updateConditionsMap.put(rs.getString("kbcat4"), rs.getString("kbdesc"));
			}
			if(!updateConditionsMap.isEmpty()) {
				for(String column:updateConditionsMap.keySet()) {
					//System.out.println("column = "+column+" ===============>"+updateConditionsMap.get(column));
					//System.out.println("currentBranch = "+currentBranch+" ===============>caseid = "+caseid);
					try {rs.close();} catch (Exception e) {}
					try {pst.close();} catch (Exception e) {}
					pst = conn.prepareStatement(updateConditionsMap.get(column));
					pst.setInt(1, currentBranch);
					pst.setString(2, caseid);
					rs = pst.executeQuery();
					if(rs.next()) { 
						//System.out.println("Found "+column);
						updateConditionsMapFlag.put(column, "Y");
					}else { 
						//System.out.println("Not Found "+column);
						updateConditionsMapFlag.put(column, "N");
					}
				}
			}

		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return updateConditionsMapFlag;
	}
	
	public boolean checkCaseBelongToMyBranch(Connection conn, int currentBranch, String state) throws Exception{
		boolean belongToMyBranch = false;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select 1 from kbpaths where path_state = ? and path_tobranch = path_frombranch = ?");
			pst.setString(1, state.trim());
			pst.setInt(2, currentBranch);
			rs = pst.executeQuery();
			if(rs.next()) {
				belongToMyBranch = true;
			}
			//System.out.println(belongToMyBranch);
			//System.out.println("State = "+state+"  _____ branch = "+currentBranch);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return belongToMyBranch;
	}
	public int getMnifestIdForPickupAgent(Connection conn, String caseids) throws Exception{
		int manifestId = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select distinct c_pickupmanifest from p_cases where c_id in(?)");
			pst.setString(1, caseids);
			rs = pst.executeQuery();
			int ctr = 0;
			while(rs.next()) {
				manifestId = rs.getInt("c_pickupmanifest");
				ctr++;
				if(ctr>1)
					break;
			}
			if(ctr>1 || ctr==0)
				throw new Exception("Error with pickup agent manifest ID");
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return manifestId;
	}
	
	public ArrayList<CaseInformation> getDeliveryOrdersInfo (Connection conn, String casesToPrint)throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<CaseInformation> casesList = new  ArrayList<CaseInformation>(); 
		try {
			String sql = "select c_id,c_rcv_hp1, c_custreceiptnoori, c_createddt, c_receiptamt, c_receiptamt_usd,"
					+ " concat (ifnull(st_name_ar,''),' ',ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk  ,"
					+ " cust_name, c_rmk from p_cases"
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " left join kbstate on st_code = c_rcv_state and st_branch = c_branchcode "
					+ " join kbcustomers on (cust_id = c_custid)"
					+ " where c_id in changeit order by c_custid, c_custreceiptnoori";
			
			sql = sql.replace("changeit", "("+casesToPrint+")");
			pst = conn.prepareStatement(sql);

			rs = pst.executeQuery();
			CaseInformation ci = new CaseInformation();
			while (rs.next()) {
				ci.setCaseid(rs.getInt("c_id"));
				ci.setReceiverHp1(rs.getString("c_rcv_hp1"));
				ci.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				ci.setCreateddt(rs.getString("c_createddt"));
				ci.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
				ci.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				ci.setReceiverAddress(rs.getString("c_rcv_addr_rmk"));
				ci.setSenderName(rs.getString("cust_name"));
				ci.setRmk(rs.getString("c_rmk"));
				casesList.add(ci);
				ci = new CaseInformation();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return casesList;
	}
	
	/**
	 * FEQAR
	 * @param conn
	 * @param driverid
	 * @param fromBranch
	 * @param toBranch
	 * @return
	 * @throws Exception
	 */
	public ArrayList<CaseInformation> getRtnItemsPerBranchRtnManifestId(Connection conn, int manifestId)throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		 ArrayList<CaseInformation>  deliveryList= new  ArrayList<CaseInformation>();
		try {
			String sql = "select cc_liaisonagentid, cc_id, cc_frombranch, cc_tobranch, c_custid, c_specialcase,  c_rural,"
					+ "  date(c_createddt) as c_createddt , cust_name, c_id, c_rcv_name, c_rcv_hp1,c_rcv_state, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr , q_stage, q_step, "
					+ " (case when c_partial_return='Y' then c_partial_qtyrtn else c_qty end) as c_qty, c_rmk, c_shipment_cost,"
					+ "  c_receiptamt, c_partial_return, c_custreceiptnoori, "
					+ " currentbranch.branch_name as currentbranchname, originatinbranch.branch_name as originatinbranchname"
					+ " from p_cases  "
					+ " join p_caseschain on (c_id = cc_caseid ) "
					+ " join kbbranches originatinbranch on (originatinbranch.branch_id = cc_frombranch)"
					+ " join kbbranches currentbranch on (currentbranch.branch_id = cc_tobranch)"
					+ " left join kbcustomers on cust_id = c_custid "
					+ " left join kbstate on st_code = c_rcv_state and st_branch=q_branch"
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where cc_rtnmanifestid=? ";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, manifestId);

			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo= new CaseInformation();
				caseInfo.setCurrentBranchName(rs.getString("currentbranchname"));
				caseInfo.setOriginatinBranchName(rs.getString("originatinbranchname"));
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
				caseInfo.setReceiptAmtIqd(rs.getInt("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getInt("c_receiptamt_usd"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
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
}
