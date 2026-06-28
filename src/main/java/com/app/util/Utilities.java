package com.app.util;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.app.beans.BranchPaymentBean;
import com.app.beans.CustomerInfoBean;
import com.app.beans.MasterCustomerInfoBean;
import com.app.beans.MasterCustomerShipmentBackBean;
import com.app.beans.ReceiptsBookBean;
import com.app.beans.UserBean;
import com.app.bussframework.PathBean;
import com.app.bussframework.StageBean;
import com.app.bussframework.StepBean;
import com.app.bussframework.StepsDecisionsBean;
import com.app.cases.CaseInformation;
import com.app.financials.FinOperationCode;
import com.app.financials.FinOperationEntity;
import com.app.financials.StandardFinCurrency;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.incomeoutcome.AgentPaymentBean;
import com.app.incomeoutcome.CustomerPaymentBean;
import com.app.incomeoutcome.CustomerStatementBean;
import com.app.incomeoutcome.PickUpAgentPaymentBean;

import smarty.core.CoreUtilities;
import smarty.security.LoginUser;

public class Utilities {
	public static final int receiptsInBook = 50;
	public DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private HashMap<String, String> arbicToEnglishNumbers;
	private HashMap<String, String> englishToArabicNumbers;
	private final String LOAD_STEPS_ACTIONS_PER_USER_SQL = "select stpd_code, stpd_desc from kbstep_decision where "
			+ " stpd_stpid in (select stp_id from kbstep where stp_code=? and stp_stgcode=?) and stpd_onlymbapp='N'"
			+ " and stpd_forrank like ? ";
	private final String LOAD_ALL_STAGES_SQL = "select stg_code, stg_name from kbstage";
	private final String LOAD_ALL_STEPS_PER_STEP_SQL = "select stp_code, stp_name, stp_icon, stp_color"
			+ " from kbstep where stp_type='DECISION' and stp_stgcode=? and stp_rank like ? ";
	public static final String HTML_DOLLAR_COLOR_BG = "background-color: #e6ffe6;";
	
	
	public static HashMap<String,String> getStatesList(Connection a_conn)throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String,String> statesList= new HashMap<String,String>();
		try {
			pst = a_conn.prepareStatement("select st_code, st_name_ar from kbstate where st_branch =1");
			rs = pst.executeQuery();
			while(rs.next()) {
				statesList.put(rs.getString("st_code"), rs.getString("st_name_ar"));
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return statesList;
	}
	
	public static ArrayList<UserBean> getTicketsHelpDeskAgents(Connection a_conn, int a_currentUserId)throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<UserBean> usersList= new ArrayList<UserBean>();
		try {
			// check if user can transfer the ticket to
			pst = a_conn.prepareStatement("select us_can_transfer_tickets_to from kbusers where us_id=?");
			pst.setInt(1, a_currentUserId);
			rs = pst.executeQuery();
			boolean isItSeniorAgent = false;
			if (rs.next()) {
				if (rs.getString("us_can_transfer_tickets_to").equalsIgnoreCase("Y")) {
					isItSeniorAgent = true;
				}
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			String sql = "select us_id, us_name from kbusers where us_branchcode = 1 and us_rank ='CALL_CENTER'"
					+ "  and us_active = 'Y' ";
			if (!isItSeniorAgent) {
				sql += " and us_can_transfer_tickets_to = 'Y'";
			}
			pst = a_conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while(rs.next()) {
				UserBean ub = new UserBean();
				ub.setUserId(rs.getInt("us_id"));
				ub.setUserName(rs.getString("us_name"));
				usersList.add(ub);
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return usersList;
	}
	
	//// By ALi B
	public static  int getMasterCustomerIdFromCustomerId(Connection a_conn,int a_senderId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = a_conn.prepareStatement("select cust_mastercustid From kbcustomers where cust_id = ?");
			pst.setInt(1, a_senderId);
			rs = pst.executeQuery();
			if(rs.next()) {
				return rs.getInt("cust_mastercustid");
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try{rs.close();}catch (Exception e) {};
			try{pst.close();}catch (Exception e) {};
		}
		return 0;
	}
	
	public static List<CaseInformation> getSingleReceiptInfoInAnyStageStepRelatedToReceiving
	(Connection conn, String a_custreceiptnoori, int a_branchCode, int a_userId,
			String a_whichScreen) throws Exception {
	PreparedStatement pst = null;
	ResultSet rs = null;
	List<CaseInformation> casesInformationList = new ArrayList<CaseInformation>();
	try {
		String sql = "select c_custreceiptnoori, now() as scannedTimeStamp, c_id, q_stage, q_step,  "
		+ " cust_name , c_receiptamt, branch_name , st_name_ar, "
		+ " concat(st_name_ar, ' - ', cdi_name, ' - ', c_rcv_addr_rmk) as address ,"
		+ " c_rcv_hp1, c_qty, date(c_createddt) as c_createddt, c_rcv_state "
		+ " from p_cases "
		+ " join kbcustomers on cust_id = c_custid"
		+ " join kbbranches on branch_id = c_branchcode "
		+ " left join  kbstate on (c_rcv_state = st_code and st_branch = ?)"
		+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
		+ " where q_status ='ACTV' "
		+ " and q_stage in ('BRANCHES', 'NEWCUSTLOGI', 'INIT') "
		+ " and q_step in ('LIAISONAGT_NEWONWAY', 'READYTOPICKUP', 'READYTOPRINT', 'NEW_ONWAY', 'NEWINSTORE') "
		+ " and c_custreceiptnoori=? and q_branch=?";
//		System.out.println("sql==>"+sql);
//		System.out.println("a_branchCode==>"+a_branchCode);
//		System.out.println("a_custreceiptnoori==>"+a_custreceiptnoori);
		pst = conn.prepareStatement(sql);
		pst.setInt(1, a_branchCode);
		pst.setString(2, a_custreceiptnoori);
		pst.setInt(3, a_branchCode);
		rs = pst.executeQuery();
		while (rs.next()) {
			CaseInformation caseInformation= new CaseInformation();
			caseInformation.setWhenItWasScannedByBarCodel(rs.getString("scannedTimeStamp"));
			caseInformation.setStepCode(rs.getString("q_step"));
			caseInformation.setStageCode(rs.getString("q_stage"));
			if(caseInformation.getStageCode().equalsIgnoreCase("BRANCHES")) {
				if (caseInformation.getStepCode().equalsIgnoreCase("LIAISONAGT_NEWONWAY")) {// بين فرعين
					caseInformation.setAction("RECEIVEDFROMLIAISON");
				}
			}else if(caseInformation.getStageCode().equalsIgnoreCase("NEWCUSTLOGI")) {
				if (caseInformation.getStepCode().equalsIgnoreCase("READYTOPRINT")) {
					caseInformation.setAction("GO_DIRECTLY_INSTORE");
				}else if (caseInformation.getStepCode().equalsIgnoreCase("READYTOPICKUP")) {
					caseInformation.setAction("GO_DIRECTLY_INSTORE");
				}
			}else if(caseInformation.getStageCode().equalsIgnoreCase("INIT")) {
				if (caseInformation.getStepCode().equalsIgnoreCase("NEW_ONWAY")) { //قادمة في الطريق
					caseInformation.setAction("PUSH_TOSTORE");
				}
			}else if(caseInformation.getStageCode().equalsIgnoreCase("INIT")) {
				if (caseInformation.getStepCode().equalsIgnoreCase("NEWINSTORE")) { //داخل المخزن
					caseInformation.setAction("ASSGN_AGENT");
				}
			}
			caseInformation.setCaseid(rs.getInt("c_id"));
			caseInformation.setOriginatinBranchName(rs.getString("branch_name"));
			caseInformation.setSenderName(rs.getString("cust_name"));
			caseInformation.setReceiverHp1(rs.getString("c_rcv_hp1"));
			caseInformation.setQty(rs.getInt("c_qty"));
			caseInformation.setCurrentBranch(a_branchCode);
			caseInformation.setCreateddt(rs.getString("c_createddt"));
			caseInformation.setReceiptAmtIqd(rs.getLong("c_receiptamt"));
			caseInformation.setLocationDetails(rs.getString("address"));
			caseInformation.setState(rs.getString("c_rcv_state"));
			caseInformation.setStateName(rs.getString("st_name_ar"));
			caseInformation.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
			casesInformationList.add(caseInformation);
		}
	}catch(Exception e) {
		e.printStackTrace();
		throw e;
	}finally {
		try {rs.close();}catch(Exception e) {}
		try {pst.close();}catch(Exception e) {}
	}
	return casesInformationList;
}
	
	public static HashMap<String, PathBean> getSinglePathStatesMap(Connection a_conn, int a_currentBranch) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String, PathBean> statesWithSinglePathMap = new  HashMap<String, PathBean> ();
		try {
			pst = a_conn.prepareStatement("select st_code, path_liaisonagent, path_tobranch  "
					+ "from kbstate join kbpaths on st_code = path_state"
					+ " and path_frombranch=? and st_branch = ? "
					+ "group by st_code having count(*) <2");
			pst.setInt(1, a_currentBranch);
			pst.setInt(2, a_currentBranch);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getInt("path_tobranch")==a_currentBranch) {
					continue;
				}
				PathBean pathBean = new PathBean();
				pathBean.setLiasionId(rs.getInt("path_liaisonagent"));
				pathBean.setToStore(rs.getInt("path_tobranch"));
				statesWithSinglePathMap.put(rs.getString("st_code"), pathBean);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return statesWithSinglePathMap;
	}
	

	public static int getUnSpecifiedDistrictId(Connection a_conn, String a_stateCode)throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = a_conn.prepareStatement("select cdi_id From kbcity_district where cdi_name like '%غير محدد%' and cdi_stcode =?");
			pst.setString(1, a_stateCode);
			rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getInt("cdi_id");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {/* ignore */}
			try {pst.close();} catch (Exception e) {/* ignore */}
		}
		return 0;
	}
	
	
	public static List<CaseInformation> getRtnBetweenBranchesForCcRtnManifest (Connection a_conn, int a_ccRtnManfiestId, int a_branchCode)
			throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		CustomerPaymentBean cpb = new CustomerPaymentBean();
		List<CaseInformation> casesList = new ArrayList<CaseInformation>();
		try {
			String mainSql = "select  "
				+ " c_id, branch_name, cust_name, c_custreceiptnoori, c_dlvagent_manifestid, c_receiptamt,date(c_createddt) as c_createddt, "
				+ " c_assignedagent, c_custreceiptnoori, concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) as address " 
				+ " from p_cases  "
				+ " join p_caseschain on cc_caseid = c_id and  cc_frombranch =? "
				+ " join kbstep on (q_stage=stp_stgcode and q_step=stp_code) "
				+ " join kbstate on (c_rcv_state = st_code and st_branch=?)   "
				+ " join kbcustomers on (c_custid = cust_id ) "
				+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
				+ " join kbbranches on (branch_id = c_branchcode)"
				+ " where cc_rtnmanifestid =? and ("
				+ "  (q_branch=? and q_stage= 'BRANCHES' and q_step='RTN_WITHLIAISONAGENT' and q_status ='ACTV')"
				+ ")";	
			pst = a_conn.prepareStatement(mainSql);
			pst.setInt(1, a_branchCode);
			pst.setInt(2, a_branchCode);
			pst.setInt(3, a_ccRtnManfiestId);
			pst.setInt(4, a_branchCode);
			rs = pst.executeQuery();
			CaseInformation caseInfo;

			while (rs.next()) {
				caseInfo = new CaseInformation();
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setLocationDetails(rs.getString("address"));
				caseInfo.setReceiptAmtIqd(rs.getInt("c_receiptamt"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setOriginatinBranchName(rs.getString("branch_name"));
				casesList.add(caseInfo);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return casesList;
	}

	public static int getPickUpAgentForMasterCustomer(Connection a_conn, int a_masterCustId )throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = a_conn.prepareStatement("select mcust_pickupagent from kb_mastercustomer where mcust_id = ?");
			pst.setInt(1, a_masterCustId);
			rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getInt("mcust_pickupagent");
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return 0;
	}
	
	public static boolean shouldShowSenderHpInDlvManifest(Connection a_conn, int a_branchId) throws Exception{
		PreparedStatement pst =  null;
		ResultSet rs = null;
		try {
			pst = a_conn.prepareStatement("select kbcode from kbgeneral "
					+ "where kbcat1='BRANCHSETTINGS' and kbcat2 ='GENERAL' "
					+ " and kbcat3='SHOW_SENDERHP_IN_MANIFEST' and kbcat4=? ");
			pst.setInt(1, a_branchId);
			rs = pst.executeQuery();
			if(rs.next()) {
				if (rs.getString("kbcode").equalsIgnoreCase("N")) {
					return false;
				}
			}
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}

	public static void changeAgentWhenShipmentWithAgent(Connection a_conn, int a_caseId,
			int a_newAgent, int a_branchCode) throws Exception {
		PreparedStatement pst =  null;
		ResultSet rs = null;
		int newAgentLatestManifestId = 0;
		try {
			pst = a_conn.prepareStatement("select max(dam_id) "
					+ " From p_dlvagentmanifest where dam_agentid = ? and dam_branchid=? ");	
			pst.setInt(1, a_newAgent);
			pst.setInt(2, a_branchCode);
			rs = pst.executeQuery();
			if(rs.next()) {
				newAgentLatestManifestId = rs.getInt(1);
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
				if(newAgentLatestManifestId == 0){
			pst = a_conn.prepareStatement("insert into p_dlvagentmanifest (dam_agentid, dam_branchid) values (?, ?)", Statement.RETURN_GENERATED_KEYS);    
            pst.setInt(1, a_newAgent);
            pst.setInt(2, a_branchCode);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			if(rs.next()) {
				newAgentLatestManifestId = rs.getInt(1);
			}		
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			pst = a_conn.prepareStatement("update p_cases set c_allowrtnagent = 0, c_alllowagentpay=0 ,"
					+ " c_assignedagent =?, c_dlvagent_manifestid=? where c_id = ?");
			pst.setInt(1, a_newAgent);
			pst.setInt(2, newAgentLatestManifestId);
			pst.setInt(3, a_caseId);
			 pst.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	public static boolean checkPermissionOfSpecialOperation(Connection a_conn, 
			String a_operationCode, LoginUser lu, String a_screenName) throws Exception {
		PreparedStatement pst =  null;
		ResultSet rs = null;
		try {
			if (lu.getRank_code().equalsIgnoreCase("ITBOSS")) {	
				return true;
			}
			pst = a_conn.prepareStatement("select sup_screen_name from kb_special_operations_for_users "
					+ " where sup_operation_code=? and sup_userid = ?  ");	
			pst.setString(1, a_operationCode);
			pst.setInt(2, lu.getUsid());
			rs = pst.executeQuery();
			if(rs.next()) {
				if (rs.getString("sup_screen_name").equals(a_screenName)
						|| rs.getString("sup_screen_name").equals("ANY")) {
					return true;
				}else {
					return false;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return false;
	}
	
	public static boolean isThereAnyPaymentMadeForTheShipment(Connection a_conn, int a_caseId)throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean settledWithCustomer = false, isThereAgentPayment = false, isTherePickUpAgentPayment= false
				, anyBranchPayment= false;
		try {//c_settled='NO' and c_branchcode=? and c_id=?  and q_step not in ('FORCE_DLV')
			pst = a_conn.prepareStatement(
			"select c_pickupagentpmtid, c_pmtid, c_agentpmtid ,"
		+ " ifnull((select sum(case when cc_branchpmtid >0 then 1  else 0 end) from p_caseschain where cc_caseid = c_id),0) as no_of_branches_payemnt "
			+ "	 from p_cases where c_id = ?");
			pst.setInt(1, a_caseId);
			rs = pst.executeQuery();
			if(rs.next()) {
				if (rs.getInt("c_pmtid") >0 ) {
					settledWithCustomer = true;
				}
				if (rs.getInt("c_agentpmtid") >0 ) {
					isThereAgentPayment = true;
				}
				if (rs.getInt("c_pickupagentpmtid")>0) {
					isTherePickUpAgentPayment = true;
				}
				if (rs.getInt("no_of_branches_payemnt")>0) {
					anyBranchPayment = true;
				}
				
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			if (settledWithCustomer || isThereAgentPayment || isTherePickUpAgentPayment || anyBranchPayment) {
				return true;
			}
			
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return false;
	}
	
	public static int getMaxAllowedHoursForCasesWithAgent(Connection a_conn, int a_branch) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = a_conn.prepareStatement("select kbdesc from kbgeneral "
					+ "  	where kbcat1='BRANCHSETTINGS' and kbcat2='GENERAL' and kbcat3='DLVAGENT'"
					+ "  	and kbcat4=? and kbcode='HOURSLATE'");
			pst.setInt(1, a_branch);
			rs = pst.executeQuery();
			while (rs.next()) {
				return rs.getInt("kbdesc");
			}
			return 72;
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
	}

	public List<CaseInformation> getAllReceiptInfoOfSameReceiptNumberInQueue (Connection conn,
			String c_custreceiptnoori, String stage, String step,
		int a_branchCode, String a_whichScreen) throws Exception {
	PreparedStatement pst = null;
	ResultSet rs = null;
	List<CaseInformation> casesInformationList = new ArrayList<CaseInformation>();
	try {
		pst = conn.prepareStatement("select c_receiptamt_usd, c_id, us_name, q_step, q_stage,  "
				+ " cust_name , c_receiptamt ,st_name_ar   as address ,"
				+ " c_rcv_name, c_rcv_hp1, c_qty, date(c_createddt) as c_createddt, c_rural, c_rcv_state, branch_name "
				+ " from p_cases "
				+ " join kbcustomers on cust_id = c_custid"
				+ " left join kbusers on us_id = c_assignedagent "
				+ " left join  kbstate on (c_rcv_state = st_code and st_branch = ?)"
				+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
				+ " join kbbranches on branch_id = c_branchcode "
				+ " where q_status ='ACTV'  and q_stage=? and q_step=? and c_custreceiptnoori=? and q_branch=?");
		pst.setInt(1, a_branchCode);
		pst.setString(2, stage);
		pst.setString(3, step);
		pst.setString(4, c_custreceiptnoori);
		pst.setInt(5, a_branchCode);
		rs = pst.executeQuery();
		while (rs.next()) {
			CaseInformation caseInformation= new CaseInformation();
			caseInformation.setStepCode(rs.getString("q_step"));
			caseInformation.setStageCode(rs.getString("q_stage"));
			caseInformation.setCaseid(rs.getInt("c_id"));
			caseInformation.setSenderName(rs.getString("cust_name"));
			caseInformation.setReceiverName(rs.getString("c_rcv_name"));
			caseInformation.setReceiverHp1(rs.getString("c_rcv_hp1"));
			caseInformation.setQty(rs.getInt("c_qty"));
			caseInformation.setCreateddt(rs.getString("c_createddt"));
			caseInformation.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
			caseInformation.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
			caseInformation.setOriginatinBranchName(rs.getString("branch_name"));
			caseInformation.setLocationDetails(rs.getString("address"));
			caseInformation.setAssignedAgentName(rs.getString("us_name"));
			caseInformation.setRural(rs.getString("c_rural"));
			caseInformation.setState(rs.getString("c_rcv_state"));
			if (a_whichScreen.equalsIgnoreCase("assignToBranch")) {
				caseInformation.setAction("ASSIGN_LIASIONAGT");
			}else if (a_whichScreen.equalsIgnoreCase("assignToAgent")) {
				caseInformation.setAction("ASSGN_AGENT");
			}else if (a_whichScreen.equalsIgnoreCase("UpdateDlvDirectly")) {
				caseInformation.setAction("SUCCDLV");
			}
			casesInformationList.add(caseInformation);
			
		}
	}catch(Exception e) {
		e.printStackTrace();
		throw e;
	}finally {
		try {rs.close();}catch(Exception e) {}
		try {pst.close();}catch(Exception e) {}
	}
	return casesInformationList;
}
	
	public String writeToFileServer(InputStream inputStream, String fileName, String updDir) throws Exception {
        String qualifiedUploadFilePath = updDir + fileName;
        new File(updDir).mkdirs();
        Path folder = Paths.get(updDir);
        if (!Files.exists(folder)) {
        	throw new Exception ("folder does no existe-->"+folder);
        }
        
        Path file = Files.createTempFile(folder, "-"+fileName,fileName);
        qualifiedUploadFilePath = file.getFileName().toString();
        try (InputStream input = inputStream) {
		    Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
        return qualifiedUploadFilePath;
    }
	
	public static boolean shouldBranchPayAllDues(Connection a_conn, int a_branchId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = a_conn.prepareStatement("select branch_must_pay_all from kbbranches where branch_id= ?");
			pst.setInt(1, a_branchId);
			rs = pst.executeQuery();
			if (rs.next())
				if (rs.getString("branch_must_pay_all").equalsIgnoreCase("Y"))
					return true;
		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return false;
	}
	
	public static boolean isCaseInStageStep (Connection a_conn, int a_caseId, String a_stage, String a_step)throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = a_conn.prepareStatement("select 1 from p_cases where c_id = ? and q_stage=? and q_step=? limit 1");
			pst.setInt(1, a_caseId);
			pst.setString(2, a_stage);
			pst.setString(3, a_step);
			rs = pst.executeQuery();
			if (rs.next())
				if (rs.getInt(1) == 1)
					return true;
		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return false;
	}
	
	public static String getAccountingCaseStatusMessage (
			String a_stage, String a_step, int a_agentPmtId, 
			String a_changedPrice, String a_priceB4Change, String a_newIqdPrice,
			String a_usdChangedPrice, String a_usdPriceB4Change, String a_newUsdPrice,
			int a_branchPmtId,
			CaseInformation a_caseInfo
			) {
		String html = "<td>";
		if (a_changedPrice.equalsIgnoreCase("Y") || a_usdChangedPrice.equalsIgnoreCase("Y")){
			if (a_stage.equalsIgnoreCase("DLV")  ) {
				if (a_step.equalsIgnoreCase("PART_SUCC")  ) {
					html="<td style='background-color:#770404; color:white;'>تم التسليم جزئيا مع تغيير سعر من "
							+a_priceB4Change+" إلى "+a_newIqdPrice+
							", دولار من "+a_usdPriceB4Change+" الى "+a_newUsdPrice;
				}else {
					html="<td style='background-color:grey; color:white;'>تم التسليم مع تغيير سعر من "
							+a_priceB4Change+" إلى "+a_newIqdPrice+
							", دولار من "+a_usdPriceB4Change+" الى "+a_newUsdPrice;
				}
				if (a_agentPmtId >0) {
					html +=" <div  class=\"badge badge-info\">حساب مندوب ("+a_agentPmtId+")</div> "; 
				}
				if(a_branchPmtId>0) {
					html +=" <div  class=\"badge badge-dark\">حساب الفرع ("+a_branchPmtId+")</div> "; 
				}
			}
			html +="</td>";
			return html;
		}else {
			if (a_stage.equalsIgnoreCase("DLV")  ) { //successful dlv
				html +="تم التسليم";
				if (a_step.equalsIgnoreCase("FORCE_DLV")) {
					html +="<span style='background-color: #f9f7bf;'> واصل بأمر الأدارة ,"+a_caseInfo.getRmk()+"</span>";
				}
			}else {
				html = "<td style='background-color:red;color:white' >خطأ في النظام";
			}
		}
		if (a_agentPmtId >0) {
			html +=" <div  class=\"badge badge-info\">حساب مندوب ("+a_agentPmtId+")</div> "; 
		}
		if(a_branchPmtId>0) {
			html +=" <div  class=\"badge badge-dark\">حساب الفرع ("+a_branchPmtId+")</div> "; 
		}
		
		html+= "</td>";
		return html;
	}
	
	public static boolean isCaseNeedsConfirmation(String a_changedPriceIqd, String a_changedPriceUsd,
			String a_isConfirmedBefore) {
		if (a_isConfirmedBefore.equalsIgnoreCase("Y")) {
			return false;
		}
		if (a_changedPriceIqd.equalsIgnoreCase("Y") || a_changedPriceUsd.equalsIgnoreCase("Y")) {
			return true;
		}
		return false;
	}
	
	public static int getNumberOfCustomerWithoutPickUpAgent(Connection a_conn, int a_branchId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		int noOfCustomersWithoutPickUpAgent = 0;
		try {
			pst = a_conn.prepareStatement("select count(*)  "
					+ "from kbcustomers where cust_assigned_pickup_agent = 0 and cust_branch= ? ");
			pst.setInt(1, a_branchId);
			rs = pst.executeQuery();
			if (rs.next()) {
				noOfCustomersWithoutPickUpAgent = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return noOfCustomersWithoutPickUpAgent;
	}
	
	public static String getChangedPriceMessage(
			String changedPriceIqd	   , String changedPriceUsd,
			String priceBeforeChangeIqd, String priceAfterChangeIqd,
			String priceBeforeChangeUsd, String priceAfterChangeUsd) {
		StringBuilder sb = new StringBuilder("");
		if (changedPriceIqd.equalsIgnoreCase("Y") || changedPriceUsd.equalsIgnoreCase("Y")) {
			sb.append("تغير سعر الوصل من ");
		}
		if (changedPriceIqd.equalsIgnoreCase("Y")) {
			sb.append(priceBeforeChangeIqd+" إلى "+priceAfterChangeIqd+" دينار ");
		}
		if (changedPriceUsd.equalsIgnoreCase("Y")) {
			if (changedPriceIqd.equalsIgnoreCase("Y")) {
				sb.append(", ");
			}
			sb.append(priceBeforeChangeUsd+" إلى "+priceAfterChangeUsd+ " دولار");
		}
		return sb.toString();
	}
	
	public static String getBranchLogoForPrinting(Connection a_conn, int a_branchCode)throws Exception{
		String branchLogo = Utilities.getBranchesInfo(a_conn, a_branchCode+"").get("logo");
		
		if (branchLogo !=null && !branchLogo.isEmpty()) {
			return GlobalVars.PATH_LOGO_FOR_PRINTING+""+branchLogo;
		}
		return GlobalVars.DEFAULT_PATH_LOGO_FOR_PRINTING;
	}

//	public static int createNewCustomer (Connection a_conn, String a_custName, String a_hp) throws Exception {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try {
//			pst = a_conn.prepareStatement("insert into ")
//		
//			pst = a_conn.prepareStatement(
//					"insert into kbcustomers "
//					+ "	(cust_name, cust_phone1, cust_createdby, cust_branch, cust_mastercustid)"
//					+ " values("+CoreUtilities.getQuestionMarks(5)+")",Statement.RETURN_GENERATED_KEYS);
//				pst.setString(1, inputMap_ori.get("newcustomer_" + userDefinedMultiNewRowExtension + "_" + j)[0]);
//				pst.setString(2, ci.getSenderHp());
//				pst.setInt(3, userId_G);
//				pst.setInt(4, ci.getCurrentBranch());
//				pst.setInt(5, ci.getMasterSenderId());
//				pst.executeUpdate();
//				rs = pst.getGeneratedKeys();
//				rs.next();
//				custId = rs.getInt(1);
//				ci.setSenderId(custId);
//			try {rs.close();} catch (Exception e) {/* ignore */}
//			try {pst.close();} catch (Exception e) {/* ignore */}
//		}catch (Exception e) {
//				throw e;
//			} finally {
//				try {
//					rs.close();
//				} catch (Exception e) {
//				}
//				try {
//					rsSteps.close();
//				} catch (Exception e) {
//				}
//				try {
//					rsActions.close();
//				} catch (Exception e) {
//				}
//				try {
//					pstActions.close();
//				} catch (Exception e) {
//				}
//				try {
//					pstSteps.close();
//				} catch (Exception e) {
//				}
//				try {
//					pst.close();
//				} catch (Exception e) {
//				}
//	}
	
	public HashMap<String, StageBean> getStagesStepsActionPerUser(Connection a_conn, String a_userRank)
			throws Exception {
		PreparedStatement pst = null, pstSteps = null, pstActions = null;
		ResultSet rs = null, rsSteps = null, rsActions = null;
		HashMap<String, StageBean> stagesMap = new HashMap<String, StageBean>();
		try {

			pst = a_conn.prepareStatement(LOAD_ALL_STAGES_SQL);
			rs = pst.executeQuery();
			while (rs.next()) {
				StageBean stageBean = new StageBean();
				stageBean.setStageCode(rs.getString("stg_code"));
				stageBean.setStageName(rs.getString("stg_name"));
				stagesMap.put(rs.getString("stg_code"), stageBean);
			}
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}

			pstSteps = a_conn.prepareStatement(LOAD_ALL_STEPS_PER_STEP_SQL);
			pstActions = a_conn.prepareStatement(LOAD_STEPS_ACTIONS_PER_USER_SQL);

			for (String stageCode : stagesMap.keySet()) {
				pstSteps.setString(1, stageCode);
				pstSteps.setString(2, "%" + a_userRank + "%");
				rsSteps = pstSteps.executeQuery();
				HashMap<String, StepBean> stepsMap = new HashMap<String, StepBean>();
				while (rsSteps.next()) {
					StepBean stepBean = new StepBean();
					stepBean.setStepCode(rsSteps.getString("stp_code"));
					stepBean.setStepColor(rsSteps.getString("stp_color"));
					stepsMap.put(rsSteps.getString("stp_code"), stepBean);
				}
				stagesMap.get(stageCode).setStepsMap(stepsMap);
				try {
					rsSteps.close();
				} catch (Exception e) {
				}
				pstSteps.clearParameters();

				for (String stepCode : stagesMap.get(stageCode).getStepsMap().keySet()) {
					pstActions.setString(1, stepCode);
					pstActions.setString(2, stageCode);
					pstActions.setString(3, "%" + a_userRank + "%");
					rsActions = pstActions.executeQuery();
					ArrayList<StepsDecisionsBean> decisionsList = new ArrayList<StepsDecisionsBean>();
					while (rsActions.next()) {
						StepsDecisionsBean stepsDecisionsBean = new StepsDecisionsBean();
						stepsDecisionsBean.setActionCode(rsActions.getString("stpd_code"));
						stepsDecisionsBean.setActionDesc(rsActions.getString("stpd_desc"));
						decisionsList.add(stepsDecisionsBean);
					}
					try {
						rsActions.close();
					} catch (Exception e) {
					}
					pstActions.clearParameters();
					stagesMap.get(stageCode).getStepsMap().get(stepCode).setDescisionsList(decisionsList);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				rsSteps.close();
			} catch (Exception e) {
			}
			try {
				rsActions.close();
			} catch (Exception e) {
			}
			try {
				pstActions.close();
			} catch (Exception e) {
			}
			try {
				pstSteps.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return stagesMap;
	}

	public HashMap<String, String> getPossibleActions(Connection conn, String stageCode, String stepCode,
			String rankCode) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String, String> actions = new HashMap<String, String>();
		try {
			pst = conn.prepareStatement("select stpd_code, stpd_desc from kbstep_decision where "
					+ " stpd_stpid in (select stp_id from kbstep where stp_code=? and stp_stgcode=?) and stpd_onlymbapp='N'"
					+ " and stpd_forrank like ? and stpd_code in ('GO_BACK_TOSTORE_RESEND','MOVE_ONWAY','RES',"
					+ "'RESEND', 'RETURN_STORE_TRY_AGAIN', 'RTN_TO_AGENT','RTN_TO_AGENT')");
			pst.setString(1, stepCode);
			pst.setString(2, stageCode);
			pst.setString(3, "%" + rankCode.trim() + "%");
			rs = pst.executeQuery();
			while (rs.next()) {
				actions.put(rs.getString("stpd_code"), rs.getString("stpd_desc"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return actions;
	}

	/**
	 * Nafie
	 * 
	 * @param conn
	 * @param branchPmtId
	 * @param userBranch
	 * @return
	 * @throws Exception
	 */
	public BranchPaymentBean getbranchPaymentInfo(Connection a_conn, int a_branchPmtId, int a_userBranch) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		BranchPaymentBean papb = new BranchPaymentBean();
		ArrayList<CaseInformation> casesList = new ArrayList<CaseInformation>();
		try {
			papb.setStandardTransactionBean(UtilitiesStandardFinancials.getTransactionInfo(a_conn, a_branchPmtId));

			pst = a_conn.prepareStatement(
			"select cust_name, date(cc_createddt) as c_createddt,  c_id , c_rcv_name, c_rcv_hp1,c_rcv_state, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr , "
					+ " c_qty, c_rmk, (case when (cc_pathcost>0) then cc_pathcost else c_shipment_cost end) as c_shipment_cost"
					+ ", c_receiptamt, c_receiptamt_usd,"
					+ " (case when (q_stage='DLV')  then 'dlv' else 'canceled'  end) as status,"
					+ " c_custreceiptnoori" + " from p_cases  " + " join kbcustomers on cust_id= c_custid"
					+ " left join kbstate on st_code = c_rcv_state and st_branch=?"
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " join p_caseschain on (c_id = cc_caseid and cc_branchpmtid=?) "
					+ " order by cust_name,c_custreceiptnoori, c_createddt ");
			pst.setInt(1, a_userBranch);
			pst.setInt(2, a_branchPmtId);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo = new CaseInformation();
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setReceiverName(rs.getString("c_rcv_name"));
				caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));

				caseInfo.setReceiptAmtIqd(rs.getInt("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));

				caseInfo.setStatus(rs.getString("status"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				casesList.add(caseInfo);
			}
			papb.setShipments(casesList);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return papb;
	}

	public HashMap<String, String> getCaseInfo(Connection conn, int caseId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String, String> caseInfo = new HashMap<String, String>();
		try {
			pst = conn.prepareStatement("select c_branchcode, q_previous_action_taken_by, c_cust_rtnid, c_pickupagent_rtnid , "
					+ "  ifnull(c_followupby,0) as c_followupby, c_assignedagent, c_mastercustid, c_custid, stp_name, "
					+ " stp_icon, stp_color, concat(stg_name, ' - ',stp_name) as stageStep, q_stage,  q_step, branch_name, "
					+ " ifnull(us_name,' ') as us_name , ifnull(c_advancepmtid,'') as c_advancepmtid, c_custreceiptnoori, "
					+ " ifnull(c_agentpmtid,'') as c_agentpmtid, ifnull(c_pmtid,'')as c_pmtid , ifnull(c_pickupagentpmtid, '') as c_pickupagentpmtid,"
					+ " ifnull(rtn_desc,'') as rtn_desc , ifnull(post_desc,'') as post_desc " + " from p_cases"
					+ " join kbstage on q_stage =  stg_code "
					+ " join kbstep on stp_code = q_step and stp_stgcode = q_stage "
					+ " join kbbranches on branch_id = q_branch " + " left join kbusers on us_id = c_pickupagent "
					+ " left join kbrtn_reasons on rtn_code = c_rtnreason "
					+ " left join kbpostponedoptions on post_code = q_postponedoption " + " where c_id = ?");
			pst.setInt(1, caseId);
			rs = pst.executeQuery();
			if (rs.next()) {
				caseInfo.put("ownerbranchcode", rs.getString("c_branchcode"));
				caseInfo.put("pickupagent", rs.getString("us_name"));
				caseInfo.put("c_advancepmtid", rs.getString("c_advancepmtid"));
				caseInfo.put("c_agentpmtid", rs.getString("c_agentpmtid"));
				caseInfo.put("c_pmtid", rs.getString("c_pmtid"));
				caseInfo.put("c_pickupagentpmtid", rs.getString("c_pickupagentpmtid"));
				caseInfo.put("c_custreceiptnoori", rs.getString("c_custreceiptnoori"));
				caseInfo.put("stageStep", rs.getString("stageStep"));
				caseInfo.put("q_stage", rs.getString("q_stage"));
				caseInfo.put("q_step", rs.getString("q_step"));
				if (rs.getInt("c_cust_rtnid") > 0 || rs.getInt("c_pickupagent_rtnid") > 0) {
					if (caseInfo.get("q_stage").equalsIgnoreCase("CNCL")
							&& caseInfo.get("q_step").equalsIgnoreCase("RTN_INSTORE")) {
						caseInfo.put("stageStep", "راجع - تم تسليمه للعميل");
					}
				}

				caseInfo.put("stepName", rs.getString("stp_name"));
				caseInfo.put("q_previous_action_taken_by", rs.getString("q_previous_action_taken_by"));
				caseInfo.put("branch_name", rs.getString("branch_name"));
				caseInfo.put("stp_icon", rs.getString("stp_icon"));
				caseInfo.put("stp_color", rs.getString("stp_color"));
				caseInfo.put("c_assignedagent", rs.getString("c_assignedagent"));
				caseInfo.put("c_mastercustid", rs.getString("c_mastercustid"));
				caseInfo.put("c_custid", rs.getString("c_custid"));
				caseInfo.put("rtnReasonDesc", rs.getString("rtn_desc"));
				caseInfo.put("postponedReasonDesc", rs.getString("post_desc"));
				caseInfo.put("c_followupby", rs.getString("c_followupby"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return caseInfo;
	}

	public ArrayList<CaseInformation> getCaseIdBasedOnGlobalSearch(Connection conn, String searchParam, String rankCode,
			String shops) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int caseId = 0;
		boolean shopsFilter = false;
		ArrayList<CaseInformation> casesList = new ArrayList<CaseInformation>();
		try {
			if (rankCode != null && (rankCode.equalsIgnoreCase("SHOP") || rankCode.equalsIgnoreCase("MASTERCUSTOMER")
					|| rankCode.equalsIgnoreCase("PAGEADMIN") || rankCode.equalsIgnoreCase("FOLLOWUP_EMP")
					|| rankCode.equalsIgnoreCase("SUPPLY_EMP")))
				shopsFilter = true;
			arbicToEnglishNumbers = getArabicToEnglishNumbersMap();
			String oppositeValueTrans = "";
			if (arbicToEnglishNumbers.containsKey(searchParam.charAt(0) + "")) // then the input is arabic number
				for (int i = 0; i < searchParam.length(); i++) {
					oppositeValueTrans += arbicToEnglishNumbers.get(searchParam.charAt(i) + "");
				}
			else {
				englishToArabicNumbers = getEnglishToArabicNumbersMap();
				for (int i = 0; i < searchParam.length(); i++) {
					oppositeValueTrans += englishToArabicNumbers.get(searchParam.charAt(i) + "");
				}
			}
			String sql = " select c_id,c_rcv_hp1, c_rcv_hp2, c_custreceiptnoori, c_createddt, c_receiptamt, c_receiptamt_usd, c_productinfo,"
					+ " concat (ifnull(st_name_ar,''),' ',ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) c_rcv_addr_rmk  "
					+ " from p_cases"
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " left join kbstate on st_code = c_rcv_state and st_branch = c_branchcode "
					+ " where (c_rcv_hp1=? or c_rcv_hp1=? or c_custreceiptnoori=?)";
			if (shopsFilter)
				sql += " and c_custid in (?)";
			pst = conn.prepareStatement(sql);
			pst.setString(1, searchParam);
			pst.setString(2, oppositeValueTrans);
			pst.setString(3, searchParam);
			if (shopsFilter)
				pst.setString(4, shops);
			rs = pst.executeQuery();
			CaseInformation ci = new CaseInformation();
			while (rs.next()) {
				ci.setCaseid(rs.getInt("c_id"));
				ci.setReceiverHp1(rs.getString("c_rcv_hp1"));
				ci.setReceiverHp2(rs.getString("c_rcv_hp2"));
				ci.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				ci.setCreateddt(rs.getString("c_createddt"));
				ci.setProductInfo(rs.getString("c_productinfo"));
				ci.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
				ci.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				ci.setReceiverAddress(rs.getString("c_rcv_addr_rmk"));
				casesList.add(ci);
				ci = new CaseInformation();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return casesList;
	}

	public ArrayList<CaseInformation> getItemsPerMasterCustomer(Connection conn, int mastercustid, int storeCode,
			String fromdt, String todt, ArrayList<String> statesList) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<CaseInformation> masterCustList = new ArrayList<CaseInformation>();
		try {
			StringBuilder sb = new StringBuilder("");
			for (int i = 0; i < statesList.size(); i++) {
				sb.append("?,");
			}
			if (fromdt.equalsIgnoreCase("ALL")) {
				String sql = "select c_id, c_custreceiptnoori, cust_name, concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,''))  as address, "
						+ "c_rcv_hp1, c_receiptamt, c_receiptamt_usd, c_rmk, "
						+ " c_shipment_cost , (c_receiptamt - c_shipment_cost) as net_iqd "
						+ "from p_cases "
						+ "join kb_mastercustomer on c_mastercustid = mcust_id  "
						+ "join kbcustomers on c_custid = cust_id "
						+ "join kbstate on (c_rcv_state = st_code and st_branch = ?) "
						+ "left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
						+ "where  c_branchcode = ? and c_mastercustid = ? ";
				if (statesList.size() > 0)
					sql += "and c_rcv_state in (" + sb.deleteCharAt(sb.length() - 1).toString() + ")  ";
				sql += "order by  cust_name ";
				pst = conn.prepareStatement(sql);
				pst.setInt(1, storeCode);
				pst.setInt(2, storeCode);
				pst.setInt(3, mastercustid);
				int index = 4;
				for (String o : statesList) {
					pst.setString(index++, o);
				}
			} else {
				String sql = "select c_id, c_custreceiptnoori, cust_name, concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,''))  as address, "
						+ "c_rcv_hp1, c_receiptamt, c_receiptamt_usd, c_rmk, " 
						+ " c_shipment_cost , (c_receiptamt - c_shipment_cost) as net_iqd "
						+ "from p_cases "
						+ "join kb_mastercustomer on c_mastercustid = mcust_id  "
						+ "join kbcustomers on c_custid = cust_id "
						+ "join kbstate on (c_rcv_state = st_code and st_branch = ?) "
						+ "left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
						+ "where  c_branchcode = ? and c_mastercustid = ? and  (date(c_createddt)>=? ) and (date(c_createddt)<=? ) ";
				if (statesList.size() > 0)
					sql += "and c_rcv_state in (" + sb.deleteCharAt(sb.length() - 1).toString() + ")  ";
				sql += "order by  cust_name ";
				pst = conn.prepareStatement(sql);
				pst.setInt(1, storeCode);
				pst.setInt(2, storeCode);
				pst.setInt(3, mastercustid);
				pst.setString(4, fromdt);
				pst.setString(5, todt);
				int index = 6;
				for (String o : statesList) {
					pst.setString(index++, o);
				}
			}
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo = new CaseInformation();
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setLocationDetails(rs.getString("address"));
				caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInfo.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInfo.setShipmentCharge(rs.getDouble("c_shipment_cost"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				masterCustList.add(caseInfo);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return masterCustList;
	}

	public ArrayList<CaseInformation> getItemsPerDriver(Connection conn, String driverid, String stgCode,
			String stpCode, int storeCode, String fromdt, String todt) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<CaseInformation> deliveryList = new ArrayList<CaseInformation>();
		try {
			if (fromdt.equalsIgnoreCase("ALL")) {
				pst = conn.prepareStatement(
			"select cust_phone1, c_branchcode, c_dlvagent_manifestid, c_custid, c_specialcase,  c_rural,"
			+ "  date(c_dategiventodlvagent) as q_enterdate , cust_name, c_id,"
			+ " date(c_dategiventodlvagent) as c_dategiventodlvagent,"
					+ "  c_rcv_name, c_rcv_hp1,c_rcv_state, c_mastercustid, c_rcv_district, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr , "
					+ " c_qty, c_rmk, c_shipment_cost, c_receiptamt, c_receiptamt_usd, c_partial_return  , c_custreceiptnoori"
					+ " from p_cases"
					+ " left join kbcustomers on cust_id = c_custid "
					+ " left join kbstate on st_code = c_rcv_state and st_branch = ?"
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where q_stage =? and q_step=? and q_status = 'ACTV' "
					+ " and c_assignedagent=? order by c_rcv_district , c_id");
				pst.setInt(1, storeCode);
				pst.setString(2, stgCode);
				pst.setString(3, stpCode);
				pst.setString(4, driverid);
			} else {
				pst = conn.prepareStatement(
				"select cust_phone1, c_branchcode, c_dlvagent_manifestid, c_custid, c_specialcase, c_rural , "
				+ " date(c_dategiventodlvagent) as q_enterdate, "
				+ " date(c_dategiventodlvagent) as c_dategiventodlvagent,"
				+ " cust_name, c_id, c_rcv_name, c_rcv_hp1,c_rcv_state, "
						+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr ,"
						+ " c_rcv_district, c_qty, c_rmk, c_mastercustid, c_shipment_cost, c_receiptamt, c_receiptamt_usd,"
						+ " c_partial_return, c_custreceiptnoori "
						+ " from p_cases  " + " left join kbcustomers on cust_id = c_custid "
						+ " left join kbstate on st_code = c_rcv_state and st_branch=? "
						+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
						+ " where q_stage =? and q_step=? and q_status = 'ACTV' and  c_assignedagent=? and  (date(c_createddt)>=? ) and (date(c_createddt)<=? ) "
						+ " order by c_rcv_district , c_id  ");
				pst.setInt(1, storeCode);
				pst.setString(2, stgCode);
				pst.setString(3, stpCode);
				pst.setString(4, driverid);
				pst.setString(5, fromdt);
				pst.setString(6, todt);
			}
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo = new CaseInformation();
				caseInfo.setOrigintingBranch(rs.getInt("c_branchcode"));
				caseInfo.setSpecialCase(rs.getString("c_specialcase"));
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setSenderName(rs.getString("c_rcv_name"));
				caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setDistrict(rs.getInt("c_rcv_district"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setQueueEnterDate(rs.getString("q_enterdate"));
				caseInfo.setDlvAgentManifestDate(rs.getString("c_dategiventodlvagent"));
				
				caseInfo.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setRural(rs.getString("c_rural"));
				caseInfo.setSenderId(rs.getInt("c_custid"));
				caseInfo.setSenderHp(rs.getString("cust_phone1"));
				caseInfo.setMasterSenderId(rs.getInt("c_mastercustid"));
				caseInfo.setManifestId(rs.getInt("c_dlvagent_manifestid"));
				deliveryList.add(caseInfo);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return deliveryList;
	}

	public AgentPaymentBean getAgentPartialReturnBackedUpInfo(Connection conn, int aprId, int userBranchId)
			throws Exception {

		PreparedStatement pst = null, ps = null;
		ResultSet rs = null, psRs = null;
		AgentPaymentBean cpb = new AgentPaymentBean();
		ArrayList<CaseInformation> casesList = new ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement("select us_name, date(apr_createddt) as apr_createddt, apr_rmk "
					+ " from p_agent_returns join kbusers on apr_agentid= us_id  where apr_id=?");
			pst.setInt(1, aprId);
			rs = pst.executeQuery();
			if (rs.next()) {
				cpb.setPmtDate(rs.getString("apr_createddt"));
				cpb.setAgentName(rs.getString("us_name"));
				cpb.setPmtRmk(rs.getString("apr_rmk"));
			}
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
			ps = conn.prepareStatement(
					"SELECT date(max(q_enterdate)) as enterdate FROM p_queue_hist where q_caseid = ? and q_step = 'ONWAY' and q_assigned_to = ? ");
			pst = conn.prepareStatement(
					"select c_changedprice, c_priceb4change, cust_name, date(c_createddt) as c_createddt, c_id, c_rcv_name,"
							+ " c_rcv_hp1,c_rcv_state, c_assignedagent, c_productinfo, c_qty, "
							+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr , c_shipment_cost, c_receiptamt,  "
							+ " (case when c_partial_return='Y' then c_partial_qtyrtn else c_qty end) as qty,"
							+ " c_rmk, c_receiptamt, c_receiptamt_usd, c_fragile , "
							+ " c_custreceiptnoori, p_cases.q_stage, c_agentshare, c_parentid "
							+ " from p_cases  " + " left join kbcustomers on cust_id = c_custid "
							+ " left join kbstate on st_code = c_rcv_state and st_branch=? "
							+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
							+ " where  c_agentrtnid=? order by cust_name, c_custreceiptnoori ");
			pst.setInt(1, userBranchId);
			pst.setInt(2, aprId);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo = new CaseInformation();
				// System.out.println("caseid = "+rs.getInt("c_id"));
				ps.setInt(1, rs.getInt("c_id"));
				ps.setInt(2, rs.getInt("c_assignedagent"));
				psRs = ps.executeQuery();
				if (psRs.next())
					caseInfo.setCreateddt(psRs.getString("enterdate"));
				else
					throw new Exception(
							"Error in Pdf - Utilities - getAgentPartialReturnBackedUpInfo : q_enterdate for agent not found");
				ps.clearParameters();
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));

				caseInfo.setReceiptAmtIqd(rs.getInt("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setFragile(rs.getString("c_fragile"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setProductInfo(rs.getString("c_productinfo"));
				caseInfo.setReceiptAmtB4Change(rs.getInt("c_priceb4change"));
				caseInfo.setChangedPrice(rs.getString("c_changedprice"));

				caseInfo.setStatus(rs.getString("q_stage"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setAgentShare(rs.getDouble("c_agentshare"));
				caseInfo.setParentId(rs.getInt("c_parentid"));
				casesList.add(caseInfo);
			}
			cpb.setShipments(casesList);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				psRs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
		return cpb;
	}

	public void acctBoxTransactions(Connection conn, int paymentId, String PaymentTabel, String tranType,
			String userLoginId, int acctBoxId, double accountBeforTransaction, double payment, int branchId,
			String tableArabicDesc, String tableColId, String TableColName, int tableColIdValue, String lookUpTableName)
			throws Exception {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("insert into p_acctbox_transactions "
					+ "(abt_paymentid, abt_payment_table			, abt_safe_impact	  		, abt_createdby    , abt_date			, "
					+ "abt_acctboxid  , abt_accountbefore_transaction, abt_payment		, abt_userbranchid , abt_tabledesc		, "
					+ "abt_tablecolid , abt_tablecoldesc				, abt_tablecolidval	, abt_table 			)"
					+ " values ( ?		 ,?							    ,?					,?					, now()	, "
					+ "   ?		 ,?							 	,?			 		,?					, ?					, "
					+ "   ?		 ,? 							,?					,? )");
			pst.setInt(1, paymentId);
			pst.setString(2, PaymentTabel);
			pst.setString(3, tranType);
			pst.setString(4, userLoginId);
			pst.setInt(5, acctBoxId);
			pst.setDouble(6, accountBeforTransaction);
			pst.setDouble(7, payment);
			pst.setInt(8, branchId);
			pst.setString(9, tableArabicDesc);
			pst.setString(10, tableColId);
			pst.setString(11, TableColName);
			pst.setInt(12, tableColIdValue);
			pst.setString(13, lookUpTableName);
			pst.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				pst.close();
			} catch (Exception e) {
			}
		}

	}

	public long getSendBranchBalanceWithReceiverBranch(Connection conn, int senderBranch, int receiverBranch)
			throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		long balance = 0;
		try {
			pst = conn.prepareStatement(
					"select sum(bp_debt) - sum(bp_credit) from p_branch_payments where bp_from_branchid=? "
							+ " and bp_received_branchid=? ");
			pst.setInt(1, senderBranch);
			pst.setInt(2, receiverBranch);
			rs = pst.executeQuery();
			if (rs.next())
				balance = rs.getLong(1);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return balance;
	}

	public LinkedList<CustomerStatementBean> getMasterCustomerStatement(Connection conn, int masterCustId,
			String fromDate, String toDate) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean foundSearch = false;
		double openingBalance = 0;
		LinkedList<CustomerStatementBean> csbList = new LinkedList<CustomerStatementBean>();
		String mainSql = "";
		try {
			if (fromDate != null && !fromDate.trim().equalsIgnoreCase(""))
				foundSearch = true;
			if (foundSearch) {
				double debitBillsTot = 0, debitOtherDebts = 0, debitTotalRefunds = 0, creditReturnedItemsTot = 0,
						creditPaymentsBill = 0;
				try {
					openingBalance = getMasterCustomerBalance(conn, masterCustId, toDate);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						rs.close();
					} catch (Exception e) {
					}
					try {
						pst.close();
					} catch (Exception e) {
					}
				}
				double openingBalance_debit = 0;
				double openingBalance_credit = 0;
				if (openingBalance > 0)
					openingBalance_debit = openingBalance;
				else
					openingBalance_credit = openingBalance;

				mainSql = "select customerstatement.* , '' as startdate, '' as enddate , '' frombill, '' tobill  "
						+ "from (  " + "		 /* opening balance*/ "
						+ "		select '' as rmk, '0' as transactionid, 'openingbalance' as trantype, 0 as cp_totreceiptsamt , cp_amount_paid_actually,  "
						+ "		 '' as cp_pmttype,''  as pmtdesc,   'opening' as optype,concat('الرصيد الأفتتاحي') as tranname , "
						+ "		'0' as credit, '0' as debit,  " + "		'" + fromDate + "' as trandate, "
						+ openingBalance_debit + " as balance_debit, " + openingBalance_credit + " as balance_credit "
						+ " from dual " + "			union "
						+ "			select ifnull(cp_rmk,'') as rmk, cp_id as transactionid, 'payment' as trantype , cp_totreceiptsamt, cp_amount_paid_actually, "
						+ "			cp_pmttype,kbdesc as pmtdesc, concat('دفعه ماليه - وصل  رقم', ' - ' , cp_id)  as tranname ,   "
						+ "			 cp_credit credit,"
						+ " 			cp_debt as debit, cp_createddt as trandate, '0' as balance_debit, '0' as balance_credit   "
						+ "			from p_customer_payments   "
						+ "			left join kbgeneral  on cp_pmttype=kbcode and kbcat1='CUSTOMER' and kbcat2 = 'PMTTYPE'   "
						+ "			where cp_mastercustid=? "
						+ " 			and cp_createddt>=date(?) and cp_createddt<adddate(?,1)  "
						+ "	) customerstatement order by transactionid ";
				pst = conn.prepareStatement(mainSql);
				// System.out.println(mainSql);
				pst.setInt(1, masterCustId);
				pst.setString(2, fromDate);
				pst.setString(3, toDate);
				rs = pst.executeQuery();
			} else {
				mainSql = " select ifnull(cp_rmk,'') as rmk, cp_id as transactionid, 'payment' as trantype ,cp_totreceiptsamt , cp_amount_paid_actually,   "
						+ "	cp_pmttype,kbdesc as pmtdesc, concat('دفعه ماليه - وصل  رقم', ' - ' , cp_id)  as tranname ,   "
						+ "	cp_credit as credit,"
						+ " 	cp_debt as debit, cp_createddt as trandate, '0' as balance_debit, '0' as balance_credit   "
						+ "	from p_customer_payments   "
						+ "	left join kbgeneral  on cp_pmttype=kbcode and kbcat1='CUSTOMER' and kbcat2 = 'PMTTYPE'   "
						+ "	where cp_mastercustid=? order by transactionid ";

				pst = conn.prepareStatement(mainSql);
				pst.setInt(1, masterCustId);
				rs = pst.executeQuery();
			}
			CustomerStatementBean csb = new CustomerStatementBean();
			double balance = 0;
			String rmk = "";
			if (rs != null) {
				while (rs.next()) {
					csb = new CustomerStatementBean();
					csb.setAmtPaidActually(rs.getDouble("cp_amount_paid_actually"));
					csb.setTotalReceiptsAmt(rs.getDouble("cp_totreceiptsamt"));
					csb.setDebit(rs.getDouble("debit"));
					csb.setCredit(rs.getDouble("credit"));
					balance += ((csb.getCredit() + rs.getDouble("balance_credit"))
							- (csb.getDebit() + rs.getDouble("balance_debit")));
					csb.setTranDate(dateFormat.format(rs.getDate("trandate")));
					csb.setTranName(rs.getString("tranname") + ", " + rs.getString("pmtdesc"));
					csb.setBalance(balance);
					csb.setRmk(rs.getString("rmk"));
					csb.setBranchId(masterCustId);
					csbList.add(csb);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return csbList;
	}

	public int getMasterCustomerBalance(Connection conn, int masterCustId, String upToDate) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int balance = 0;
		try {
			pst = conn.prepareStatement(
					"select sum(cp_debt) - sum(cp_credit) from p_customer_payments where cp_mastercustid=? and cp_createddt<=? ");
			pst.setInt(1, masterCustId);
			pst.setString(2, upToDate);
			rs = pst.executeQuery();
			if (rs.next()) {
				balance = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return balance;
	}

//	public int getMasterCustomerBalance(Connection conn, int masterCustId, long a_UpToTransaction)throws Exception{
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		int balance = 0;
//		try {
//			pst = conn.prepareStatement("select sum(cp_debt) - sum(cp_credit) from p_customer_payments where cp_mastercustid=?"
//					+ " and cp_id < ? ");
//			
//			pst.setInt(1, masterCustId);
//			pst.setLong(2, a_UpToTransaction);
//			rs = pst.executeQuery();
//			if (rs.next()) {
//				balance = rs.getInt(1);
//			}
//		}catch(Exception e) {
//			throw e;
//		}finally {
//			try {rs.close();}catch(Exception e) {}
//			try {pst.close();}catch(Exception e) {}
//		}
//		return balance;
//	}

	public int getRtnNotReceived(Connection conn, int agentId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int rtnNotReceived = 0;
		try {
			pst = conn.prepareStatement("select count(*) from p_cases where c_allowrtnagent='Y' "
					+ " and c_assignedagent=? and c_agentrtnid=0");
			pst.setInt(1, agentId);
			rs = pst.executeQuery();
			if (rs.next()) {
				rtnNotReceived = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return rtnNotReceived;
	}

	public int getWithAgent(Connection conn, int agentId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int underDlv = 0;
		try {
			pst = conn.prepareStatement(
					"select count(*) from p_cases where q_stage = 'AGENTOP'  and c_allowrtnagent='N' "
							+ " and c_assignedagent=? and c_agentpmtid=0 and  q_step != 'POSTPONED'");
			pst.setInt(1, agentId);
			rs = pst.executeQuery();
			if (rs.next()) {
				underDlv = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return underDlv;
	}

	public int getWithAgentRedeliver(Connection conn, int agentId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int underDlv = 0;
		try {
			pst = conn.prepareStatement(
					"select count(*) from p_cases where q_stage = 'AGENTOP' and q_step='TRY_AGAIN' and c_allowrtnagent='N' "
							+ " and c_assignedagent=? and c_agentpmtid=0");
			pst.setInt(1, agentId);
			rs = pst.executeQuery();
			if (rs.next()) {
				underDlv = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return underDlv;
	}

	public int getWithAgentPostponed(Connection conn, int agentId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int underDlv = 0;
		try {
			pst = conn.prepareStatement(
					"select count(*) from p_cases where q_stage = 'AGENTOP' and q_step = 'POSTPONED' and c_allowrtnagent='N' "
							+ " and c_assignedagent=? and c_agentpmtid=0");
			pst.setInt(1, agentId);
			rs = pst.executeQuery();
			if (rs.next()) {
				underDlv = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return underDlv;
	}

	public int getAgentDeliveredNoPaid(Connection conn, int agentId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int deliverdNotPaid = 0;
		try {
			pst = conn.prepareStatement(
					"select count(*) from p_cases where c_alllowagentpay = 'Y'  and c_agentsharesettled !='FULL' "
							+ " and c_assignedagent=? and c_agentpmtid=0 and c_parentid=0");
			pst.setInt(1, agentId);
			rs = pst.executeQuery();
			if (rs.next()) {
				deliverdNotPaid = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return deliverdNotPaid;
	}

	public double getAgentDebtBalanceUpToSpecificPayment(Connection conn, int agentId, int pmtId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double debtBalance = 0;
		try {
			pst = conn.prepareStatement(
					"select sum(ap_amtremaining) - sum(case when ap_paymenttype = 'DEBT_SETTLE'  then ap_amtreceived else 0 end ) "
							+ " from p_agent_payments where ap_agentid=? "
							+ " and (  (ap_amtremaining !=0 and ap_paymenttype='CASES')  or (ap_paymenttype = 'DEBT_SETTLE')  or (ap_paymenttype = 'REG_DEBT'))"
							+ " and ap_id <? ");
			pst.setInt(1, agentId);
			pst.setInt(2, pmtId);
			rs = pst.executeQuery();
			if (rs.next()) {
				debtBalance = rs.getDouble(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return debtBalance;
	}

	public double getAgentDebtBalance(Connection conn, int agentId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double debtBalance = 0;
		try {
			pst = conn.prepareStatement(
					"select sum(ap_amtremaining) - sum(case when ap_paymenttype = 'DEBT_SETTLE'  then ap_amtreceived else 0 end ) "
							+ " from p_agent_payments where ap_agentid=? "
							+ " and (  (ap_amtremaining !=0 and ap_paymenttype='CASES')  or (ap_paymenttype = 'DEBT_SETTLE') or (ap_paymenttype = 'REG_DEBT') )");
			pst.setInt(1, agentId);
			rs = pst.executeQuery();
			if (rs.next()) {
				debtBalance = rs.getDouble(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return debtBalance;
	}

	public CaseInformation getSinglCaseInformationFromBranch(Connection conn, int caseId, int currentBranch)
			throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		CaseInformation caseInfo = new CaseInformation();
		try {
			pst = conn.prepareStatement("select  q_stage, q_step, cc_id, cc_parentchainid, q_branch "
					+ " from p_cases join p_caseschain on cc_caseid = c_id and cc_frombranch=?   where c_id=? ");
			pst.setInt(1, currentBranch);
			pst.setInt(2, caseId);
			rs = pst.executeQuery();
			if (rs.next()) {
				caseInfo = new CaseInformation();
				caseInfo.setStepCode(rs.getString("q_step"));
				caseInfo.setStageCode(rs.getString("q_stage"));
				caseInfo.setCurrentChainId(rs.getInt("cc_id"));
				caseInfo.setParentChainId(rs.getInt("cc_parentchainid"));
				caseInfo.setFromBranchCode(rs.getInt("q_branch"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return caseInfo;
	}

	public CaseInformation getSinglCaseInformationToBranch(Connection conn, int caseId, int currentBranch)
			throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		CaseInformation caseInfo = new CaseInformation();
		try {
			pst = conn.prepareStatement("select  q_stage, q_step, cc_id, cc_parentchainid "
					+ " from p_cases join p_caseschain on cc_caseid = c_id and cc_tobranch=?   where c_id=? ");
			pst.setInt(1, currentBranch);
			pst.setInt(2, caseId);
			rs = pst.executeQuery();
			if (rs.next()) {
				caseInfo = new CaseInformation();
				caseInfo.setStepCode(rs.getString("q_step"));
				caseInfo.setStageCode(rs.getString("q_stage"));
				caseInfo.setCurrentChainId(rs.getInt("cc_id"));
				caseInfo.setParentChainId(rs.getInt("cc_parentchainid"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return caseInfo;
	}

	public CaseInformation getSinglCaseInformation(Connection conn, String caseId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		CaseInformation caseInfo = new CaseInformation();
		try {
			pst = conn.prepareStatement(
					"select c_agentpmtid, c_pmtid, c_cust_rtnid, c_agentrtnid, c_pickupagent_rtnid, c_pickupagentpmtid,"
							+ "  c_custid, c_specialcase,  c_rural, date(c_createddt) as c_createddt , cust_name, c_id, c_rcv_name,"
							+ "  c_rcv_hp1, c_rcv_hp2 ,c_rcv_state, "
							+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr , "
							+ " c_qty, c_rmk,c_shipment_cost, c_receiptamt, c_receiptamt_usd, c_partial_return, ifnull(c_fragile,'N') as c_fragile  , "
							+ " c_sendmoney, c_custreceiptnoori , c_productinfo, cust_phone1, q_branch "
							+ " from p_cases  " + " left join kbcustomers on cust_id = c_custid "
							+ " left join kbstate on st_code = c_rcv_state and st_branch=c_branchcode"
							+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
							+ " where  c_id = ? ");
			pst.setString(1, caseId);
			rs = pst.executeQuery();

			if (rs.next()) {
				caseInfo = new CaseInformation();
				caseInfo.setSpecialCase(rs.getString("c_specialcase"));
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setReceiverName(rs.getString("c_rcv_name"));
				caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInfo.setReceiverHp2(rs.getString("c_rcv_hp2"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setReceiptAmtIqd(rs.getInt("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setFragile(rs.getString("c_fragile"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setRural(rs.getString("c_rural"));
				caseInfo.setSenderId(rs.getInt("c_custid"));
				caseInfo.setProductInfo(rs.getString("c_productinfo"));
				caseInfo.setSenderHp(rs.getString("cust_phone1"));
				caseInfo.setCurrentBranch(rs.getInt("q_branch"));

			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return caseInfo;
	}

	public CaseInformation getSingleCaseInfo2(Connection conn, int caseId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		CaseInformation caseInfo = new CaseInformation();
		try {
			pst = conn.prepareStatement(
					"select c_agentpmtid, c_pmtid, c_cust_rtnid, c_agentrtnid, c_pickupagent_rtnid, c_pickupagentpmtid,"
							+ "  q_stage, q_step  from p_cases  where  c_id = ? ");
			pst.setInt(1, caseId);
			rs = pst.executeQuery();

			if (rs.next()) {
				caseInfo = new CaseInformation();
				caseInfo.setDlvAgentPmtId(rs.getInt("c_agentpmtid"));
				caseInfo.setSenderPmtId(rs.getInt("c_pmtid"));
				caseInfo.setCustReturnId(rs.getInt("c_cust_rtnid"));
				caseInfo.setAgentRtnId(rs.getInt("c_agentrtnid"));
				caseInfo.setPickupAgentRtnId(rs.getInt("c_pickupagent_rtnid"));
				caseInfo.setPickUpAgentPmtId(rs.getInt("c_pickupagentpmtid"));
				caseInfo.setStageCode(rs.getString("q_stage"));
				caseInfo.setStepCode(rs.getString("q_step"));
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return caseInfo;
	}

	public String getProductName(Connection conn, int prodId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String prodName = "";
		try {
			pst = conn.prepareStatement("SELECT  cg_goodsdesc from kbcustomer_goods where cg_id =?");
			pst.setInt(1, prodId);
			rs = pst.executeQuery();
			if (rs.next()) {
				prodName = rs.getString("cg_goodsdesc");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return prodName;
	}

	public HashMap<Integer, String> getMasterCustomerGoodsList(Connection conn, int masterCustId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<Integer, String> goodsList = new LinkedHashMap<Integer, String>();
		try {
			pst = conn.prepareStatement("SELECT cg_id, cg_goodsdesc from kbcustomer_goods where cg_mastercustid = ?");
			pst.setInt(1, masterCustId);
			rs = pst.executeQuery();
			while (rs.next()) {
				goodsList.put(rs.getInt("cg_id"), rs.getString("cg_goodsdesc"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return goodsList;
	}

	public boolean isRuralDistrict(Connection conn, int district, int a_branchCode) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean rural = false;
		try {
			pst = conn.prepareStatement(
					"select dbr_rural from kbdistrict_branch_r where dbr_districtcode=? and dbr_branchid=? ");
			pst.setInt(1, district);
			pst.setInt(2, a_branchCode);
			rs = pst.executeQuery();
			if (rs.next()) {
				if (rs.getString("dbr_rural").equalsIgnoreCase("Y"))
					rural = true;
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return rural;

	}

	public boolean isDistrictInThisState(Connection a_conn, int a_district, String a_stateCode) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean found = false;
		try {
			pst = a_conn.prepareStatement("select 1 From kbcity_district where cdi_id = ? and cdi_stcode = ? ");
			pst.setInt(1, a_district);
			pst.setString(2, a_stateCode);
			rs = pst.executeQuery();
			if (rs.next()) {
				found = true;
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return found;

	}

	public int countCasesInQueue(Connection conn, String stageCode, String stepCode, int branchCode) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int ctr = 0;
		try {
			pst = conn.prepareStatement(
					"select count(*) " + "from ( select c_id from p_cases where q_stage=? and q_step=? "
							+ " and q_branch=?  "
							+ " and c_pickupagent_rtnid=0  and c_cust_rtnid=0 and "
							+ " c_settled !='FULL' limit 10000)ttt ");
			pst.setString(1, stageCode);
			pst.setString(2, stepCode);
			pst.setInt(3, branchCode);

			rs = pst.executeQuery();
			if (rs.next())
				ctr = rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return ctr;
	}

	public boolean checkIfCaseWenToChain(Connection conn, int caseId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean found = false;
		try {
			pst = conn.prepareStatement("SELECT 1 from p_caseschain where cc_caseid =? ");
			pst.setInt(1, caseId);
			rs = pst.executeQuery();
			if (rs.next())
				found = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return found;
	}

	public HashMap<String, String> getArabicToEnglishNumbersMap() {
		HashMap<String, String> hash = new HashMap<String, String>();
		hash.put("٠", "0");
		hash.put("١", "1");
		hash.put("٢", "2");
		hash.put("٣", "3");
		hash.put("٤", "4");
		hash.put("٥", "5");
		hash.put("٦", "6");
		hash.put("٧", "7");
		hash.put("٨", "8");
		hash.put("٩", "9");
		return hash;
	}

	public HashMap<String, String> getEnglishToArabicNumbersMap() {
		HashMap<String, String> hash = new HashMap<String, String>();
		hash.put("0", "٠");
		hash.put("1", "١");
		hash.put("2", "٢");
		hash.put("3", "٣");
		hash.put("4", "٤");
		hash.put("5", "٥");
		hash.put("6", "٦");
		hash.put("7", "٧");
		hash.put("8", "٨");
		hash.put("9", "٩");
		return hash;
	}

	/**
	 * Nafie use this to search in request for a parameter
	 */
	public boolean checkIfInRequest(HttpServletRequest request, String param) {
		boolean found = false;
		if (request.getParameterMap().containsKey(param))
			if (request.getParameter(param) != null && request.getParameter(param).length() > 0)
				found = true;
		return found;
	}

	/**
	 * Nafie
	 */
	public HashMap<String, String> getPostponedOptionsMap(Connection conn) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String, String> rtnMap = new HashMap<String, String>();
		try {
			pst = conn.prepareStatement("select post_code , post_desc from kbpostponedoptions");
			rs = pst.executeQuery();
			while (rs.next()) {
				rtnMap.put(rs.getString("post_code"), rs.getString("post_desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return rtnMap;
	}

	/**
	 * Nafie
	 */
	public HashMap<String, String> getRtnReasons(Connection conn) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String, String> rtnMap = new HashMap<String, String>();
		try {
			pst = conn.prepareStatement("select rtn_code , rtn_desc from kbrtn_reasons");
			rs = pst.executeQuery();
			while (rs.next()) {
				rtnMap.put(rs.getString("rtn_code"), rs.getString("rtn_desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return rtnMap;
	}

	/**
	 * Nafie
	 */
	public HashMap<String, StepBean> getStepsFromDB(Connection conn) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String, StepBean> stepsMap = new HashMap<String, StepBean>();
		StepBean stepBean = null;
		try {
			pst = conn.prepareStatement(
					"select stp_id, stp_code, stp_name, stp_stgcode, stp_order, stp_icon, stp_color from kbstep");
			rs = pst.executeQuery();
			while (rs.next()) {
				stepBean = new StepBean();
				stepBean.setStpId(rs.getInt("stp_id"));
				stepBean.setStepCode(rs.getString("stp_code"));
				stepBean.setStpName(rs.getString("stp_name"));
				stepBean.setStpSeq(rs.getInt("stp_order"));
				stepBean.setStepIcon(rs.getString("stp_icon"));
				stepBean.setStepColor(rs.getString("stp_color"));
				stepsMap.put(rs.getString("stp_code"), stepBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return stepsMap;
	}

	/**
	 * Nafie
	 */
	public LinkedHashMap<String, String> getStagesColors(Connection conn) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String, String> colorMap = new LinkedHashMap<String, String>();
		try {
			pst = conn.prepareStatement("select stg_code, stg_color from kbstage");
			rs = pst.executeQuery();
			while (rs.next()) {
				colorMap.put(rs.getString("stg_code"), rs.getString("stg_color"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return colorMap;
	}

	/**
	 * Nafie
	 */
	public void assignLiaisonManifestIdToCases(Connection conn, int manifestId, ArrayList<CaseInformation> cases)
			throws Exception {
		PreparedStatement pst = null;
		try {
			// check first if there is path already
			pst = conn.prepareStatement("update p_caseschain set cc_manifestid=? where cc_id=? ");
			for (CaseInformation ci : cases) {
				pst.setInt(1, manifestId);
				pst.setInt(2, ci.getLatestChainId());
				pst.addBatch();
			}
			pst.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {

			try {
				pst.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Nafie
	 */
	public void assignRTNLiaisonManifestIdToCases(Connection conn, int rtnmanifestId, 
			ArrayList<CaseInformation> cases, int a_goingBackToBranch)
			throws Exception {
		PreparedStatement pst = null;
		try {
			// check first if there is path already
			pst = conn.prepareStatement("update p_caseschain "
					+ " set cc_rtnmanifestid=? where cc_id=? and cc_frombranch=? ");
			for (CaseInformation ci : cases) {
				pst.setInt(1, rtnmanifestId);
				pst.setInt(2, ci.getLatestChainId());
				pst.setInt(3, a_goingBackToBranch);
				pst.addBatch();
			}
			pst.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {

			try {
				pst.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Nafie
	 */
	public void assignManifestIdToCases(Connection conn, int manifestId, ArrayList<CaseInformation> cases)
			throws Exception {
		PreparedStatement pst = null;
		try {
			// check first if there is path already
			pst = conn.prepareStatement("update p_cases set c_dlvagent_manifestid=? where c_id=? ");
			for (CaseInformation ci : cases) {
				pst.setInt(1, manifestId);
				pst.setInt(2, ci.getCaseid());
				pst.addBatch();
			}
			int updatedCases = pst.executeBatch().length;
			if (cases.size()!=updatedCases) {
				throw new Exception ("لم يتم اسناد رقم كشف للمندوب . الرجاء المحاولة مرة أخرى");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {

			try {
				pst.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Nafie
	 */
	public int createCasePath(Connection conn, PathBean pathBean, int createdBy) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean pathFound = false;
		int caseChainId = 0;
		try {
			// check first if there is path already
			pst = conn.prepareStatement("select cc_id from p_caseschain where cc_caseid=? and cc_pathid=?");
			pst.setInt(1, pathBean.getCaseId());
			pst.setInt(2, pathBean.getPathId());
			rs = pst.executeQuery();
			if (rs.next()) {
				pathFound = true;
				caseChainId = rs.getInt("cc_id");
			}
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
			// if we resened from branch to branch, may be we put a counter?
			if (!pathFound) {
				int parentChainId = 0;
				pst = conn.prepareStatement("select max(cc_id) from p_caseschain where cc_caseid=?");
				pst.setInt(1, pathBean.getCaseId());
				rs = pst.executeQuery();
				if (rs.next()) {
					parentChainId = rs.getInt(1);
				}
				try {
					rs.close();
				} catch (Exception e) {
				}
				try {
					pst.close();
				} catch (Exception e) {
				}

				pst = conn.prepareStatement("insert into p_caseschain"
						+ "(cc_caseid  , cc_frombranch   , cc_tobranch, cc_liaisonagentid, cc_pathid, "
						+ "cc_pathcost, cc_parentchainid, cc_createdby)"
						+ " values(?		  , ?			 	, ?		  	 , ?				, ?,"
						+ "		   ? 		  , ?				,?)", Statement.RETURN_GENERATED_KEYS);
				pst.setInt(1, pathBean.getCaseId());
				pst.setInt(2, pathBean.getFromStore());
				pst.setInt(3, pathBean.getToStore());
				pst.setInt(4, pathBean.getLiasionId());
				pst.setInt(5, pathBean.getPathId());
				pst.setDouble(6, pathBean.getCost());
				pst.setInt(7, parentChainId);
				pst.setInt(8, createdBy);
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				rs.next();
				caseChainId = rs.getInt(1);
				try {
					rs.close();
				} catch (Exception e) {
				}
				try {
					pst.close();
				} catch (Exception e) {
				}
			}
			if (caseChainId != 0) {
				pst = conn.prepareStatement("update p_cases set c_lastchainid=? where c_id = ?");
				pst.setInt(1, caseChainId);
				pst.setInt(2, pathBean.getCaseId());
				pst.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return caseChainId;
	}

	/**
	 * Nafie
	 */
	public PathBean getRightPathForCase(Connection conn, int caseId, int liaisonAgent) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int comingFromBranch = 0;
		String stateCode = "";
		PathBean pathBean = null;
		// System.out.println("caseId = "+caseId+" liaisonAgent = "+liaisonAgent);
		try {
			pst = conn.prepareStatement("select q_comingfrombranch, c_rcv_state from p_cases where c_id = ?");
			pst.setInt(1, caseId);
			rs = pst.executeQuery();
			if (rs.next()) {
				comingFromBranch = rs.getInt("q_comingfrombranch");
				stateCode = rs.getString("c_rcv_state");
			}
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
			// System.out.println("comingFromBranch = "+comingFromBranch+" stateCode =
			// "+stateCode);
			pst = conn.prepareStatement("select path_id, path_cost, path_tobranch, path_liaisonagent from kbpaths "
					+ " join kbbranches on (branch_id = path_tobranch and branch_active = 'Y') "
					+ " where path_state=? and path_frombranch=? and path_liaisonagent=?");
			pst.setString(1, stateCode);
			pst.setInt(2, comingFromBranch);
			pst.setInt(3, liaisonAgent);
			rs = pst.executeQuery();
			int counter = 0;
			while (rs.next()) {
				pathBean = new PathBean();
				pathBean.setPathId(rs.getInt("path_id"));
				pathBean.setToStore(rs.getInt("path_tobranch"));
				pathBean.setCost(rs.getDouble("path_cost"));
				pathBean.setFromStore(comingFromBranch);
				pathBean.setToState(stateCode);
				pathBean.setLiasionId(rs.getInt("path_liaisonagent"));
				pathBean.setCaseId(caseId);
				counter++;
			}
			if (counter == 0 || counter > 1) {
				try {
					rs.close();
				} catch (Exception e) {
				}
				try {
					pst.close();
				} catch (Exception e) {
				}
				pst = conn.prepareStatement("select c_custreceiptnoori from p_cases where c_id = ?");
				pst.setInt(1, caseId);
				rs = pst.executeQuery();
				rs.next();
				String custReceiptNoOri = rs.getString("c_custreceiptnoori");
				try {
					rs.close();
				} catch (Exception e) {
				}
				try {
					pst.close();
				} catch (Exception e) {
				}
				if (counter == 0)
					throw new Exception("لايوجد مسار للشحنة التي برقم وصل " + custReceiptNoOri);
				else
					throw new Exception("هناك اكثر من مسار لنفس الشحنة التي برقم وصل " + custReceiptNoOri);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return pathBean;
	}

	/**
	 * Nafie
	 */
	public boolean anyCasesForDlvAgentInStep(Connection conn, String stage, String step, String agentId)
			throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean available = false;
		try {
			pst = conn.prepareStatement("select 1 from p_cases "
					+ " where q_stage = ? and q_step=? and q_status = 'ACTV' and c_assignedagent=? limit 0,1");
			pst.setString(1, stage);
			pst.setString(2, step);
			pst.setString(3, agentId);
			rs = pst.executeQuery();
			if (rs.next())
				available = true;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return available;
	}

	/**
	 * Nafie
	 */
	public CaseInformation getSingleReceiptInfoInQueue(Connection conn, String caseId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		CaseInformation caseInformation = new CaseInformation();
		try {
			pst = conn.prepareStatement("select  c_id, us_name, q_step, q_stage,  "
					+ " cust_name , c_receiptamt, c_receiptamt_usd , concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk)  as address ,"
					+ " c_rcv_name, c_rcv_hp1, c_qty, date(c_createddt) as c_createddt, c_rural" + " from p_cases "
					+ " join kbcustomers on cust_id = c_custid" + " left join kbusers on us_id = c_assignedagent "
					+ " left join  kbstate on (c_rcv_state = st_code)"
					+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
					+ " where c_id = ?");
			pst.setString(1, caseId);
			rs = pst.executeQuery();
			if (rs.next()) {
				caseInformation.setStepCode(rs.getString("q_step"));
				caseInformation.setStageCode(rs.getString("q_stage"));
				caseInformation.setCaseid(rs.getInt("c_id"));
				caseInformation.setSenderName(rs.getString("cust_name"));
				caseInformation.setSenderName(rs.getString("c_rcv_name"));
				caseInformation.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInformation.setQty(rs.getInt("c_qty"));
				caseInformation.setCreateddt(rs.getString("c_createddt"));
				caseInformation.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
				caseInformation.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInformation.setLocationDetails(rs.getString("address"));
				caseInformation.setAssignedAgentName(rs.getString("us_name"));
				caseInformation.setRural(rs.getString("c_rural"));

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return caseInformation;
	}

	/**
	 * Nafie
	 */
	public CaseInformation getSingleReceiptInfoInQueue(Connection conn, String c_custreceiptnoori, String stage,
			String step) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		CaseInformation caseInformation = new CaseInformation();
		try {
			pst = conn.prepareStatement("select   c_id, us_name, q_step, q_branch,  "
					+ " cust_name , c_receiptamt, c_receiptamt_usd , concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk)  as address ,"
					+ " c_rcv_name, c_rcv_hp1, c_qty, date(c_createddt) as c_createddt, c_rural, c_rcv_state "
					+ " from p_cases " + " join kbcustomers on cust_id = c_custid"
					+ " left join kbusers on us_id = c_assignedagent "
					+ " left join  kbstate on (c_rcv_state = st_code)"
					+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
					+ " where q_status ='ACTV'  and q_stage=? and q_step=? and c_custreceiptnoori=?");
			pst.setString(1, stage);
			pst.setString(2, step);
			pst.setString(3, c_custreceiptnoori);
			rs = pst.executeQuery();
			if (rs.next()) {
				caseInformation.setStepCode(rs.getString("q_step"));
				caseInformation.setCaseid(rs.getInt("c_id"));
				caseInformation.setSenderName(rs.getString("cust_name"));
				caseInformation.setReceiverName(rs.getString("c_rcv_name"));
				caseInformation.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInformation.setQty(rs.getInt("c_qty"));
				caseInformation.setCreateddt(rs.getString("c_createddt"));
				caseInformation.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
				caseInformation.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInformation.setLocationDetails(rs.getString("address"));
				caseInformation.setAssignedAgentName(rs.getString("us_name"));
				caseInformation.setRural(rs.getString("c_rural"));
				caseInformation.setState(rs.getString("c_rcv_state"));
				caseInformation.setCurrentBranch(rs.getInt("q_branch"));
			;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return caseInformation;
	}

	/**
	 * Nafie
	 */
	public CaseInformation getSingleReceiptInfoInQueue(Connection conn,
			String c_custreceiptnoori, String stage,
			String step, int a_currentBranch) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		CaseInformation caseInformation = new CaseInformation();
		try {
			pst = conn.prepareStatement("select   c_id, us_name, q_step, q_branch,  "
					+ " cust_name , c_receiptamt, c_receiptamt_usd , concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk)  as address ,"
					+ " c_rcv_name, c_rcv_hp1, c_qty, date(c_createddt) as c_createddt, c_rural, c_rcv_state "
					+ " from p_cases " + " join kbcustomers on cust_id = c_custid"
					+ " left join kbusers on us_id = c_assignedagent "
					+ " left join  kbstate on (c_rcv_state = st_code)"
					+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
					+ " where q_status ='ACTV'  and q_stage=? and q_step=? and c_custreceiptnoori=?"
					+ " and q_branch =? ");
			pst.setString(1, stage);
			pst.setString(2, step);
			pst.setString(3, c_custreceiptnoori);
			pst.setInt(4, a_currentBranch);
			rs = pst.executeQuery();
			if (rs.next()) {
				caseInformation.setStepCode(rs.getString("q_step"));
				caseInformation.setCaseid(rs.getInt("c_id"));
				caseInformation.setSenderName(rs.getString("cust_name"));
				caseInformation.setReceiverName(rs.getString("c_rcv_name"));
				caseInformation.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInformation.setQty(rs.getInt("c_qty"));
				caseInformation.setCreateddt(rs.getString("c_createddt"));
				caseInformation.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
				caseInformation.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInformation.setLocationDetails(rs.getString("address"));
				caseInformation.setAssignedAgentName(rs.getString("us_name"));
				caseInformation.setRural(rs.getString("c_rural"));
				caseInformation.setState(rs.getString("c_rcv_state"));
				caseInformation.setCurrentBranch(rs.getInt("q_branch"));
			;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return caseInformation;
	}
	public CaseInformation getSingleReceiptInfoInNEW_ONWAY(Connection conn, String c_custreceiptnoori)
			throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		CaseInformation caseInformation = new CaseInformation();
		try {
			pst = conn.prepareStatement("select  c_id, "
					+ " cust_name , c_receiptamt, c_receiptamt_usd , concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk)  as address ,"
					+ " c_rcv_name, c_rcv_hp1, c_qty, date(c_createddt) as c_createddt, c_rural" + " from p_cases "
					+ " join kbcustomers on cust_id = c_custid" + " left join  kbstate on (c_rcv_state = st_code)"
					+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
					+ " where c_custreceiptnoori=? and q_status ='ACTV' and q_step = 'NEW_ONWAY' and q_stage='init'");
			pst.setString(1, c_custreceiptnoori);
			rs = pst.executeQuery();
			if (rs.next()) {
				caseInformation.setCaseid(rs.getInt("c_id"));
				caseInformation.setSenderName(rs.getString("cust_name"));
				caseInformation.setSenderName(rs.getString("c_rcv_name"));
				caseInformation.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInformation.setQty(rs.getInt("c_qty"));
				caseInformation.setCreateddt(rs.getString("c_createddt"));
				caseInformation.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
				caseInformation.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInformation.setLocationDetails(rs.getString("address"));
				caseInformation.setRural(rs.getString("c_rural"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return caseInformation;
	}

	public void updateAllCustomersPickUpAgentBackDated(Connection conn, String a_pickUpAgent, int a_branchCode)
			throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<String> customers = new ArrayList<String>();
		try {
			pst = conn.prepareStatement("select cust_id from kbcustomers  where cust_assigned_pickup_agent=?"
					+ " and cust_branch = ? ");
			pst.setString(1, a_pickUpAgent);
			pst.setInt(2, a_branchCode);
			rs = pst.executeQuery();
			while (rs.next()) {
				customers.add(rs.getString("cust_id"));
			}
			try {rs.close();} catch (Exception e) {/* ignore */}
			try {pst.close();} catch (Exception e) {/* ignore */}

			// check if the customer have discount
			pst = conn.prepareStatement("update p_cases set c_pickupagent=?"
					+ " where c_custid=? and c_settled ='NO' and c_pmtid=0 and c_branchcode=? and c_pickupagent=0 ");
			for (String cust : customers) {
				pst.setString(1, a_pickUpAgent);
				pst.setString(2, cust);
				pst.setInt(3, a_branchCode);
				pst.executeUpdate();
				//LocalTime before = LocalTime.now();
				//System.out.println("updating customer-------------------------->"+cust);
				updateAllCustomerShipmentsCost(conn, cust, a_branchCode);
				//System.out.println("end updating customer---------------------->"+cust+" it took ==>"+before.until(LocalTime.now(),ChronoUnit.SECONDS) );
				pst.clearParameters();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
	}

	public int getPickupAgentOfGeneratedReceipt(Connection conn, String generatedReceiptNo) throws Exception {
		int pickupAgent = 0;
		String branchOfReceipt = "";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			pst = conn.prepareStatement(
					"select b_bookbranch from p_books_rcp join p_books on br_bid =b_id and br_rcp_no=?");
			pst.setString(1, generatedReceiptNo);
			rs = pst.executeQuery();
			if (rs.next()) {
				branchOfReceipt = rs.getString("b_bookbranch");
			}
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}

			// check if the receipt is under another customer
			pst = conn.prepareStatement(
					"select kbdesc from kbgeneral where kbcat1 = 'BRANCH' and kbcat2='PICKUPAGENT' and kbcode=?");
			pst.setString(1, branchOfReceipt);
			rs = pst.executeQuery();
			if (rs.next()) {
				pickupAgent = rs.getInt("kbdesc");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return pickupAgent;
	}

	public static boolean allowChangeReceiptFinancials(Connection a_conn, int a_caseId)throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String currentStep = "";
		boolean settledWithCustomer = false, isThereAgentPayment = false, isTherePickUpAgentPayment= false
				, anyBranchPayment= false;
		try {//c_settled='NO' and c_branchcode=? and c_id=?  and q_step not in ('FORCE_DLV')
			pst = a_conn.prepareStatement(
			"select c_pickupagentpmtid, q_step, c_pmtid, c_agentpmtid , c_paytodlvcheck,"
		+ " ifnull((select sum(case when cc_branchpmtid >0 then 1  else 0 end) from p_caseschain where cc_caseid = c_id),0) as no_of_branches_payemnt "
			+ "	 from p_cases where c_id = ?");
			pst.setInt(1, a_caseId);
			rs = pst.executeQuery();
			boolean isReceiptPriceCheckedBefore = false;
			if(rs.next()) {
				if (rs.getInt("c_pmtid") >0 ) {
					settledWithCustomer = true;
				}
				if (rs.getInt("c_agentpmtid") >0 ) {
					isThereAgentPayment = true;
				}
				if (rs.getInt("c_pickupagentpmtid")>0) {
					isTherePickUpAgentPayment = true;
				}
				if (rs.getInt("no_of_branches_payemnt")>0) {
					anyBranchPayment = true;
				}
				currentStep = rs.getString("q_step");
				if (rs.getString("c_paytodlvcheck").equalsIgnoreCase("Y")) {
					isReceiptPriceCheckedBefore = true;
				}
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			if (settledWithCustomer || isThereAgentPayment || isTherePickUpAgentPayment || anyBranchPayment) {
				throw new Exception ("لا يمكن تغيير مبلغ الوصل لوجود حركات مالية");
			}
			if (isReceiptPriceCheckedBefore) {
				throw new Exception ("لا يمكن تغيير مبلغ الوصل لوجود تأكيد تم على الوصل");
			}
			if (currentStep.equalsIgnoreCase("FORCE_DLV") ) {
				throw new Exception ("لا يمكن تغيير مبلغ الوصل لان الوصل محتسب واصل من الأدارة");
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return true;
	}
	public static void changeReceiptByCaseId(Connection conn, int caseId, double newReceipt, 
			int userId, String fromWhere, StandardFinCurrency a_currency) throws Exception {
		allowChangeReceiptFinancials(conn, caseId);
		if (a_currency == StandardFinCurrency.IQD) {
			changeReceiptPriceIqd(conn, caseId, newReceipt, userId , fromWhere);
		}	
		if (a_currency == StandardFinCurrency.USD) {
			changeReceiptPriceUsd(conn, caseId, newReceipt, userId , fromWhere);
		}
	}
	
	private static void changeReceiptPriceIqd(Connection conn, int caseId, double newPriceIqd, int userId, String fromWhere)
			throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		double oldPriceIqd = 0.0;
		try {
			pst = conn.prepareStatement("select  c_receiptamt from p_cases where c_id=?");
			pst.setInt(1, caseId);
			rs = pst.executeQuery();
			rs.next();
			oldPriceIqd = rs.getDouble("c_receiptamt");
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
			// check Iqd change
			pst = conn.prepareStatement(
					"update p_cases set c_priceb4change = c_receiptamt , "
					+ "c_receiptamt=? , c_changedprice='Y',"
							+ " c_changedpriceby=? , c_changedpriceat=now() where c_id = ? ");
			pst.setDouble(1, newPriceIqd);
			pst.setInt(2, userId);
			pst.setInt(3, caseId);
			pst.executeUpdate();
			CoreUtilities.logChanges(conn, "p_cases".toUpperCase(), "c_id", caseId, "c_receiptamt", oldPriceIqd + "",
					newPriceIqd + "", "update", fromWhere, userId);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
	}

	private static void changeReceiptPriceUsd(Connection a_conn, int a_caseId, double a_newPriceUsd, int a_userId,
			String a_fromWhere) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double oldPriceUsd = 0.0;
		try {
			pst = a_conn.prepareStatement("select c_receiptamt_usd from p_cases where c_id=?");
			pst.setInt(1, a_caseId);
			rs = pst.executeQuery();
			rs.next();
			oldPriceUsd = rs.getDouble("c_receiptamt_usd");
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
			if (oldPriceUsd != a_newPriceUsd) {
				pst = a_conn.prepareStatement("update p_cases " + "set 	c_usdpriceb4change = c_receiptamt_usd ,"
						+ "		c_receiptamt_usd=? , " + "		c_usdchangedprice='Y',"
						+ " 	c_usdchangedpriceby=? , " + "		c_usdchangedpriceat=now(), "
						+ "		c_must_confirm_usd_change='Y', " + "		c_confirmed_usd_change='N',"
						+ " 	c_confirmed_usd_change_by=0 " + "		where c_id = ? ");
				pst.setDouble(1, a_newPriceUsd);
				pst.setInt(2, a_userId);
				pst.setInt(3, a_caseId);
				pst.executeUpdate();
				CoreUtilities.logChanges(a_conn, "p_cases".toUpperCase(), "c_id", a_caseId, "c_receiptamt_usd",
						oldPriceUsd + "", a_newPriceUsd + "", "update", a_fromWhere, a_userId);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
	}

	/**
	 * Nafie
	 */

	public String getOriginationSystemCodeOfCases(Connection conn, int caseid) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String originatingSystemCode = "";
		try {
			pst = conn.prepareStatement("select c_receivedfrom_system from p_cases where c_id=?");
			pst.setInt(1, caseid);
			rs = pst.executeQuery();
			if (rs.next()) {
				if (rs.getString("c_receivedfrom_system") != null
						&& !rs.getString("c_receivedfrom_system").trim().isEmpty()) {
					originatingSystemCode = rs.getString("c_receivedfrom_system");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return originatingSystemCode;
	}

	/**
	 * Nafie
	 * 
	 */
	public String getReceiptNoOri(Connection conn, String caseid) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String custReceiptNoOri = "";
		try {
			pst = conn.prepareStatement("select c_custreceiptnoori from p_cases where c_id=?");
			pst.setString(1, caseid);
			rs = pst.executeQuery();
			if (rs.next()) {
				custReceiptNoOri = rs.getString("c_custreceiptnoori");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return custReceiptNoOri;
	}

	public static boolean checkPasswordSmallOrContainSpace(String pass) throws Exception {
		boolean check = false;

		if (pass.isEmpty())
			return true;

		if (pass.length() < 4)
			return true;

		for (char c : pass.toCharArray()) {
			if (Character.isWhitespace(c)) {
				return true;
			}
		}
		return check;
	}

	public boolean checkUserLoginIdExistOrSmall(Connection conn, String userLogin, String actionName) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean check = false;
		try {
			if (userLogin.isEmpty())
				return true;

			if (userLogin.length() < 4)
				return true;

			for (char c : userLogin.toCharArray()) {
				if (Character.isWhitespace(c)) {
					return true;
				}
			}
			pst = conn.prepareStatement("select count(*) from kbusers where us_loginid=?");
			pst.setString(1, userLogin);
			rs = pst.executeQuery();
			if (rs.next()) {
				if (actionName.equalsIgnoreCase("INSERT")) {
					if (rs.getInt(1) > 0)
						return true;
				} else if (rs.getInt(1) > 1)
					return true;
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return check;

	}

	public CaseInformation getSingleReceiptInfoInQueuePerAgent(Connection conn, String c_custreceiptnoori, String stage,
			String step, String dlvAgent) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		CaseInformation caseInformation = new CaseInformation();
		try {
			pst = conn.prepareStatement("select  c_id, us_name, q_step,  "
					+ " cust_name , c_receiptamt, c_receiptamt_usd , concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk)  as address ,"
					+ " c_rcv_name, c_rcv_hp1, c_qty, date(c_createddt) as c_createddt, c_rural" + " from p_cases "
					+ " join kbcustomers on cust_id = c_custid" + " left join kbusers on us_id = c_assignedagent "
					+ " left join  kbstate on (c_rcv_state = st_code)"
					+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
					+ " where c_custreceiptnoori=? and c_assignedagent=? and q_status ='ACTV'  and q_stage=? and q_step=? ");
			pst.setString(1, stage);
			pst.setString(2, step);
			pst.setString(3, c_custreceiptnoori);
			pst.setString(4, dlvAgent);
			rs = pst.executeQuery();
			if (rs.next()) {
				caseInformation.setStepCode(rs.getString("q_step"));
				caseInformation.setCaseid(rs.getInt("c_id"));
				caseInformation.setSenderName(rs.getString("cust_name"));
				caseInformation.setReceiverName(rs.getString("c_rcv_name"));
				caseInformation.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInformation.setQty(rs.getInt("c_qty"));
				caseInformation.setCreateddt(rs.getString("c_createddt"));
				caseInformation.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
				caseInformation.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInformation.setLocationDetails(rs.getString("address"));
				caseInformation.setAssignedAgentName(rs.getString("us_name"));
				caseInformation.setRural(rs.getString("c_rural"));

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return caseInformation;
	}

	public static LinkedHashMap<String, String> getBranchesInfo(Connection conn, String branch_id) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String, String> branchesInfo = new LinkedHashMap<String, String>();
		try {
			pst = conn.prepareStatement("select ifnull(branch_logo_url,'') as logo, branch_showmainlogo, branch_name , st_name_ar, branch_state from kbbranches "
					+ " join kbstate on (branch_state=st_code)  where branch_id=?");
			pst.setString(1, branch_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				branchesInfo.put("name", rs.getString("branch_name"));
				branchesInfo.put("state", rs.getString("st_name_ar"));
				branchesInfo.put("stateCode", rs.getString("branch_state"));
				branchesInfo.put("logo", rs.getString("logo"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return branchesInfo;
	}

	/*
	 * get list of branches
	 */
	public static LinkedHashMap<String, String> getListOfBranches(Connection conn, int userBranch) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String, String> branchesList = new LinkedHashMap<String, String>();
		try {
			pst = conn.prepareStatement("select branch_id, branch_name"  
					+" from kbbranches where branch_active = 'Y' "  
					+" and (branch_id in  "
					+"		(select distinct(path_tobranch) From kbpaths  where path_frombranch =? ) "
					+"	  or  branch_id in  "
					+"		(select distinct(path_frombranch) From kbpaths  where path_tobranch =? ) "
					+"	or  branch_id in "
					+"		(select distinct(path_tobranch) From kbpaths_deleted  where path_frombranch =? ) "
					+"	or  branch_id in "
					+"		(select distinct(path_frombranch) From kbpaths_deleted  where path_tobranch =? ) "
					+"	)");
			pst.setInt(1, userBranch);
			pst.setInt(2, userBranch);
			pst.setInt(3, userBranch);
			pst.setInt(4, userBranch);
			rs = pst.executeQuery();
			while (rs.next()) {
				branchesList.put(rs.getString("branch_id"), rs.getString("branch_name"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return branchesList;
	}


	/*
	 * use the following method to collect the data of the payment from the pick up
	 * agent acct screen
	 */

	public PickUpAgentPaymentBean getPickUpAgentPaymentInfo(Connection a_conn, int a_transId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		PickUpAgentPaymentBean papb = new PickUpAgentPaymentBean();
		ArrayList<CaseInformation> casesList = new ArrayList<CaseInformation>();
		try {
			papb.setStandardTransactionBean(UtilitiesStandardFinancials.getTransactionInfo(a_conn, a_transId));
			pst = a_conn.prepareStatement(
			"select c_priceb4change, c_paidinadvance, c_changedprice, cust_name, date(c_createddt) as c_createddt, "
			+ "  c_id as caseid, c_rcv_name, c_rcv_hp1,c_rcv_state, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr , "
					+ " c_qty, c_rmk, c_partial_return, c_fragile , (q_stage) as status,"
					+ " c_custreceiptnoori, c_receiptamt, c_receiptamt_usd, c_shipment_cost, "
					+ " (c_receiptamt - c_shipment_cost) as netamt, '' as totalnet " 
					+ " from p_cases  "
					+ " join kbcustomers on cust_id= c_custid"
					+ " left join kbstate on st_code = c_rcv_state and st_branch = c_branchcode "
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where c_pickupagentpmtid=? order by cust_name,c_custreceiptnoori, c_createddt ");
			pst.setInt(1, a_transId);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo = new CaseInformation();
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
				casesList.add(caseInfo);
			}
			papb.setShipments(casesList);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return papb;

	}

	////////////////////////////////////////////////////////////////
	/////////////////////// FEQAR

	public LinkedHashMap<String, String> getListOfcustomers(Connection conn, int branchCode) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String, String> customersList = new LinkedHashMap<String, String>();
		try {
			pst = conn.prepareStatement(
					"select cust_id , cust_name from kbcustomers where cust_branch=?  order by cust_name");
			pst.setInt(1, branchCode);
			// pst.setString(1, branchCode);
			rs = pst.executeQuery();
			while (rs.next()) {
				customersList.put(rs.getString("cust_id"), rs.getString("cust_name"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return customersList;
	}

	public boolean isMainBranch(Connection conn, int branchCode) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean mainBranch = false;
		try {
			pst = conn.prepareStatement("select branch_main from kbbranches where branch_id=?");
			pst.setInt(1, branchCode);
			rs = pst.executeQuery();
			if (rs.next()) {
				if (rs.getString("branch_main").equalsIgnoreCase("Y"))
					mainBranch = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			;
			try {
				pst.close();
			} catch (Exception e) {
			}
			;
		}
		return mainBranch;
	}

//////NEW UTI////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	public CustomerPaymentBean getCustomerReturnedItems(Connection conn, int custId, int branchCode, String rtnDate)
			throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		CustomerPaymentBean cpb = new CustomerPaymentBean();
		ArrayList<CaseInformation> casesList = new ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement("select cust_name from kbcustomers  where cust_id=?");
			pst.setInt(1, custId);
			rs = pst.executeQuery();
			if (rs.next()) {
				cpb.setCustomerName(rs.getString("cust_name"));
			}
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}

			String MainSql = "select c_rmk, c_qty, q_stage, q_step,'' as status , c_custid,c_custreceiptnoori,'' as totamt,'' as pmtrmk, '' as pmtdate, "
					+ " date(c_createddt) as c_createddt , concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr, c_id, "
					+ " c_rcv_name , c_rcv_hp1, '' as fromdate, '' as todate, c_receiptamt, c_receiptamt_usd, c_shipment_cost " 
					+ " from p_cases  "
					+ " left join kbstate on st_code = c_rcv_state and  st_branch = c_branchcode"
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where c_custid =?"
					+ "  and q_stage = 'cncl' and q_step = 'delv_back_to_shipper' and q_status ='END'  and c_branchcode=? and  (q_enterdate >=STR_TO_DATE(?,'%Y-%m-%d') "
					+ "	 and q_enterdate<adddate(STR_TO_DATE(?,'%Y-%m-%d'),1)) order by  c_custid, c_custreceiptnoori ";
			pst = conn.prepareStatement(MainSql);
			pst.setInt(1, custId);
			pst.setInt(2, branchCode);
			pst.setString(3, rtnDate);
			pst.setString(4, rtnDate);
			rs = pst.executeQuery();
			CaseInformation caseInfo;

			while (rs.next()) {
				// System.out.println("found");
				caseInfo = new CaseInformation();
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setSenderName(rs.getString("c_rcv_name"));
				caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setReceiptAmtIqd(rs.getInt("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));

				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				casesList.add(caseInfo);
			}
			cpb.setShipments(casesList);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return cpb;

	}

	// get items per liaison agent
	public ArrayList<CaseInformation> getItemsPerLiaisonAgent(Connection conn, int driverid, String stgCode,
			String stpCode, int toBranch, int fromBranch) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<CaseInformation> deliveryList = new ArrayList<CaseInformation>();
		try {
			String sql = 
					"select cc_liaisonagentid, cc_id, cc_frombranch, cc_tobranch, c_custid, c_specialcase,  c_rural, date(c_createddt) as c_createddt , "
							+ " cust_name, c_id, c_rcv_hp1,c_rcv_state, "
							+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr , "
							+ " c_qty, c_rmk, c_shipment_cost, c_receiptamt, c_receiptamt_usd, c_partial_return,  c_custreceiptnoori"
							+ " from p_cases  " + " join p_caseschain on c_lastchainid = cc_id "
							+ " left join kbcustomers on cust_id = c_custid "
							+ " left join kbstate on st_code = c_rcv_state and st_branch=?"
							+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
							+ " where  q_stage =? and q_step=? and cc_frombranch=? and  cc_tobranch=? and q_status = 'ACTV' and cc_liaisonagentid=? "
							+ " order by c_mastercustid, c_custid ";
			
			pst = conn.prepareStatement(sql);
			pst.setInt(1, fromBranch);
			pst.setString(2, stgCode);
			pst.setString(3, stpCode);
			pst.setInt(4, fromBranch);
			pst.setInt(5, toBranch);
			pst.setInt(6, driverid);
			rs = pst.executeQuery();
//			System.out.println("sql--->"+sql);
//			System.out.println("fromBranch--->"+fromBranch);
//			System.out.println("stgCode--->"+stgCode);
//			System.out.println("stpCode--->"+stpCode);
//			System.out.println("fromBranch--->"+fromBranch);
//			System.out.println("toBranch--->"+toBranch);
//			System.out.println("driverid--->"+driverid);
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo = new CaseInformation();
				caseInfo.setSpecialCase(rs.getString("c_specialcase"));
				caseInfo.setToBranchCode(rs.getInt("cc_tobranch"));
				caseInfo.setFromBranchCode(rs.getInt("cc_frombranch"));
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setReceiptAmtIqd(rs.getInt("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));				
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setRural(rs.getString("c_rural"));
				caseInfo.setSenderId(rs.getInt("c_custid"));
				caseInfo.setLatestChainId(rs.getInt("cc_id"));
				caseInfo.setLiaisonAgent(rs.getInt("cc_liaisonagentid"));
				deliveryList.add(caseInfo);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return deliveryList;
	}

	/*
	 * get driver name
	 */

	public String getDriverName(Connection conn, String driverid) throws Exception {
		String driverName = "";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select us_name from kbusers  where us_id=? ");
			pst.setString(1, driverid);
			rs = pst.executeQuery();
			if (rs.next())
				driverName = rs.getString("us_name");

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return driverName;
	}

	/*
	 * get driver city destination
	 */

	public String getDriverCityDestination(Connection conn, String driverid) throws Exception {
		String cityName = "";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(
					"select st_name_ar from kbusers join kbstate on us_to_state = st_code where us_id=? ");
			pst.setString(1, driverid);
			rs = pst.executeQuery();
			if (rs.next())
				cityName = rs.getString("st_name_ar");

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return cityName;
	}

	/*
	 * get the customer name using the customer id
	 */
	public static String getMasterCustomerName(Connection conn, int mcustid) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String mcustName = "";

		try {
			pst = conn.prepareStatement("select mcust_name from kb_mastercustomer where mcust_id=?");
			pst.setInt(1, mcustid);
			rs = pst.executeQuery();
			if (rs.next())
				mcustName = rs.getString("mcust_name");
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return mcustName;
	}

	/*
	 * get the customer name using the customer id
	 */
	public static String getCustomerName(Connection conn, int custid) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String custName = "";
		try {
			pst = conn.prepareStatement("select cust_name from kbcustomers where cust_id=?");
			pst.setInt(1, custid);
			rs = pst.executeQuery();
			if (rs.next())
				custName = rs.getString("cust_name");
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return custName;
	}
	
	

	/*
	 * get the customer Phone number using the customer id
	 */
	public String getCustomerHP(Connection conn, int custid) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String custHp = "";
		try {
			pst = conn.prepareStatement("select cust_phone1 from kbcustomers where cust_id=?");
			pst.setInt(1, custid);
			rs = pst.executeQuery();
			if (rs.next())
				custHp = rs.getString("cust_phone1");
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return custHp;
	}

	public double getCustomerDebt(Connection conn, int custid) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double remAmt = 0.0;
		try {
			pst = conn.prepareStatement(" select sum(amt) from vw_custbalance where pcust =?");
			pst.setInt(1, custid);
			rs = pst.executeQuery();
			if (rs.next()) {
				remAmt = rs.getDouble(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return remAmt;
	}

	public double getTotCost(Connection conn, int cp_id) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double remAmt = 0.0;
		try {
			pst = conn.prepareStatement(" select cpay_pctot from p_custpay where cpay_pcid =?");
			pst.setInt(1, cp_id);
			rs = pst.executeQuery();
			if (rs.next()) {
				remAmt = rs.getDouble("cpay_pctot");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return remAmt;
	}

	/*
	 * I removed the kbcity table public String getStateOfCity(Connection conn,
	 * String city) throws Exception{ PreparedStatement pst = null; ResultSet rs =
	 * null; String stateCode=""; try{ //System.out.println("---->"+city); pst =
	 * conn.prepareStatement("SELECT ct_statecode FROM kbcity where ct_code=? ");
	 * pst.setString(1, city); rs = pst.executeQuery(); if (rs.next()) { stateCode =
	 * rs.getString("ct_statecode"); } }catch (Exception e) { throw e; }finally {
	 * try {rs.close();}catch(Exception e) {} try {pst.close();}catch(Exception e)
	 * {s} } return stateCode; }
	 */

	// get hashMap of district by state
	public LinkedHashMap<Integer, String> getDistrictOfState(Connection conn, String destState) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<Integer, String> dist = new LinkedHashMap<Integer, String>();
		try {
			pst = conn.prepareStatement(
					"SELECT cdi_id, cdi_name from kbcity_district where cdi_stcode=? order by cdi_name");
			pst.setString(1, destState);
			rs = pst.executeQuery();
			while (rs.next()) {
				dist.put(rs.getInt("cdi_id"), rs.getString("cdi_name"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return dist;
	}

	/*
	 * calculate the shipment charges
	 * 
	 * public double calcShipmentChargesBasedOnDestCity(Connection conn, String
	 * destState , boolean rural , String custName, int branchCode) throws
	 * Exception{ PreparedStatement pst = null; ResultSet rs = null; double
	 * shipmentCharge = 0.0; boolean custSpecialPiceFound = false; try{
	 * //System.out.println("----->"+destState+", rural="+rural+", custName=>"
	 * +custName); pst = conn.
	 * prepareStatement(" select st_charges, st_ruralcharges from kbstate where st_code =? and st_branch=?  and st_active='Y'"
	 * ); pst.setString(1, destState); pst.setInt(2, branchCode); rs =
	 * pst.executeQuery(); if (rs.next()) { if (rural ) shipmentCharge =
	 * rs.getDouble("st_ruralcharges"); else shipmentCharge =
	 * rs.getDouble("st_charges"); }
	 * 
	 * try {rs.close();}catch(Exception e) {ignore} try
	 * {pst.close();}catch(Exception e) {ignore} // check if the customer have
	 * discount if (custName!=null) { pst =
	 * conn.prepareStatement(" select sp_price, sp_rural_price " +
	 * " from kbcustomers join kb_special_prices on cust_id = sp_custid   where cust_name =? and sp_statecode=? and cust_branch=?"
	 * ); pst.setString(1, custName); pst.setString(2, destState); pst.setInt(3,
	 * branchCode); rs = pst.executeQuery();
	 * 
	 * if (rs.next()) { custSpecialPiceFound = true; if (rural) shipmentCharge =
	 * rs.getDouble("sp_rural_price"); else shipmentCharge =
	 * rs.getDouble("sp_price"); } try {rs.close();}catch(Exception e) {ignore} try
	 * {pst.close();}catch(Exception e) {ignore}
	 * 
	 * pst =
	 * conn.prepareStatement(" select sp_price, sp_rural_price , sp_superpriority  "
	 * +
	 * " from kbcustomers join kb_pickupagent_prices on sp_agentid = cust_assigned_pickup_agent"
	 * + " where cust_name=? and sp_statecode=? and cust_branch=? ");
	 * pst.setString(1, custName); pst.setString(2, destState); pst.setInt(3,
	 * branchCode); rs = pst.executeQuery(); if (rs.next()) { if
	 * (custSpecialPiceFound) { if
	 * (rs.getString("sp_superpriority").equalsIgnoreCase("Y")) { if (rural)
	 * shipmentCharge = rs.getDouble("sp_rural_price"); else shipmentCharge =
	 * rs.getDouble("sp_price"); } }else { if (rural) shipmentCharge =
	 * rs.getDouble("sp_rural_price"); else shipmentCharge =
	 * rs.getDouble("sp_price"); }
	 * 
	 * }
	 * 
	 * } }catch (Exception e) { throw e; }finally { try {rs.close();}catch(Exception
	 * e) {ignore} try {pst.close();}catch(Exception e) {ignore} } return
	 * shipmentCharge; }
	 */

	/*
	 * calculate the shipment charges
	 */
	public double calcShipmentChargesBasedOnDestCity(Connection conn, String destState, boolean rural, int masterCustid, int a_custId,
			int branchCode) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double shipmentCharge = 0.0;
		boolean specialPiceFound = false;

		try {
			// 1- if customer have special price
			
			  if (a_custId >0) { 
				  pst =conn.prepareStatement(" select sp_price, sp_rural_price " +
						  " from  kbcustomer_specialprices  where sp_custid =?  and sp_statecode=?");
				  pst.setInt(1, a_custId); 
				  pst.setString(2, destState); 
				  rs = pst.executeQuery();
				  if (rs.next()) { 
					  specialPiceFound = true; 
					  if (rural) 
						  shipmentCharge =rs.getDouble("sp_rural_price"); 
					  else shipmentCharge = rs.getDouble("sp_price"); 
				  } 
				  try {rs.close();}catch(Exception e) {} 
				  try {pst.close();}catch(Exception e) {} 
			} 
			/////////////////////////////////////////////////////////
			// 2- if no price found for page , then check the master
			if (!specialPiceFound) {
				pst = conn.prepareStatement(" select mcsp_price, mcsp_rural_price "
						+ " from  kbmastercustomer_specialprices  where mcsp_mastercustid =?  and mcsp_statecode=?");
				pst.setInt(1, masterCustid);
				pst.setString(2, destState);
				rs = pst.executeQuery();
				if (rs.next()) {
					specialPiceFound = true;
					if (rural)
						shipmentCharge = rs.getDouble("mcsp_rural_price");
					else
						shipmentCharge = rs.getDouble("mcsp_price");
				}
				try {
					rs.close();
				} catch (Exception e) {
					/* ignore */}
				try {
					pst.close();
				} catch (Exception e) {
					/* ignore */}
			}

			////////////////////////////////////////////////////////////
			// 3- if no price found for page , then check the pickup agent
			if (!specialPiceFound) {
				pst = conn.prepareStatement(" select sp_price, sp_rural_price , sp_superpriority  "
						+ " from kb_mastercustomer join kb_pickupagent_prices on sp_agentid = mcust_pickupagent"
						+ " where mcust_id=? and sp_statecode=? ");
				pst.setInt(1, masterCustid);
				pst.setString(2, destState);
				rs = pst.executeQuery();
				if (rs.next()) {
					specialPiceFound = true;
					if (rural)
						shipmentCharge = rs.getDouble("sp_rural_price");
					else
						shipmentCharge = rs.getDouble("sp_price");
				}
				try {
					rs.close();
				} catch (Exception e) {
					/* ignore */}
				try {
					pst.close();
				} catch (Exception e) {
					/* ignore */}
			}

			////////////////////////////////////////////////////////////
			// 4- if no price found for page , then get it from the stata
			if (!specialPiceFound) {
				pst = conn.prepareStatement(
						" select st_charges, st_ruralcharges from kbstate where st_code =? and st_branch=? and st_active='Y'");
				pst.setString(1, destState);
				pst.setInt(2, branchCode);

				rs = pst.executeQuery();
				if (rs.next()) {
					specialPiceFound = true;
					if (rural)
						shipmentCharge = rs.getDouble("st_ruralcharges");
					else
						shipmentCharge = rs.getDouble("st_charges");

					// System.out.println("in rs, shipmentCharge->"+shipmentCharge);
				}
				// System.out.println("here in calc");
				try {
					rs.close();
				} catch (Exception e) {
					/* ignore */}
				try {
					pst.close();
				} catch (Exception e) {
					/* ignore */}
			}

			if (!specialPiceFound) {
				throw new Exception("No Shipment charges found for destState->" + destState + ", rural->" + rural
						+ ", masterCustid->" + masterCustid +  ",branchCode->" + branchCode);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return shipmentCharge;
	}

	public double calcAgentShipmentChargesShare(Connection conn, int branchCode, String destState, int districtCode,
			boolean rural, String agentId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double agentshipmentCharge = 0.0;
		boolean agentShareFound = false;
		// System.out.println("branchCode = "+branchCode+" destState = "+destState+"
		// districtCode = "+districtCode+" rural = "+rural+" agentId = "+agentId);
		try {
			// first check agent district
			if (agentId != null && agentId.trim().length() > 0 && districtCode > 0) {
				pst = conn.prepareStatement("select ifnull(agdi_agentshare,0) as agentshare from kbagent_district "
						+ "where agdi_usid = ? and agdi_districtcode=? and agdi_agentshare>0 ");
				pst.setString(1, agentId);
				pst.setInt(2, districtCode);
				rs = pst.executeQuery();
				if (rs.next()) {
					agentshipmentCharge = rs.getDouble("agentshare");
					if (agentshipmentCharge > 0) // Validation confirm
						agentShareFound = true;
				}
				try {
					rs.close();
				} catch (Exception e) {
				}
				try {
					pst.close();
				} catch (Exception e) {
				}
				// System.out.println("agentShareFound in agent district = "+agentShareFound+"
				// agentshipmentCharge = "+agentshipmentCharge);

				if (agentShareFound)
					return agentshipmentCharge;
			}
			// second check district
			if (districtCode > 0 && destState != null && destState.trim().length() > 0) {
				pst = conn
						.prepareStatement(" select ifnull(dbr_agentshare,0) as dbr_agentshare from kbdistrict_branch_r"
								+ " where dbr_districtcode=?  and dbr_agentshare >0  and dbr_branchid=?");
				pst.setInt(1, districtCode);
				pst.setInt(2, branchCode);
				rs = pst.executeQuery();
				if (rs.next()) {
					agentshipmentCharge = rs.getDouble("dbr_agentshare");
					if (agentshipmentCharge > 0) // Validation confirm
						agentShareFound = true;
				}
				try {
					rs.close();
				} catch (Exception e) {
				}
				try {
					pst.close();
				} catch (Exception e) {
				}

				// System.out.println("agentShareFound in district = "+agentShareFound+"
				// agentshipmentCharge = "+agentshipmentCharge);
				if (agentShareFound)
					return agentshipmentCharge;
			}

			// third check agent share from kbusers
			if (agentId != null && agentId.trim().length() > 0) {
				pst = conn.prepareStatement("select ifnull(us_agentsharecenter,0) as us_agentsharecenter , "
						+ "ifnull(us_agentsharerural,0) as us_agentsharerural " + "  from kbusers where us_id = ?");
				pst.setString(1, agentId);
				rs = pst.executeQuery();
				if (rs.next()) {
					if (!rural) {
						if (rs.getInt("us_agentsharecenter") > 0)
							agentshipmentCharge = rs.getDouble("us_agentsharecenter");
					} else {
						if (rs.getInt("us_agentsharerural") > 0)
							agentshipmentCharge = rs.getDouble("us_agentsharerural");
					}
					if (agentshipmentCharge > 0)
						agentShareFound = true;
				}
				try {
					rs.close();
				} catch (Exception e) {
				}
				try {
					pst.close();
				} catch (Exception e) {
				}
				// System.out.println("agentShareFound in kbusers = "+agentShareFound+"
				// agentshipmentCharge = "+agentshipmentCharge);

				if (agentShareFound)
					return agentshipmentCharge;
			}

			// fourth check state
			if (branchCode > 0 && destState != null && destState.trim().length() > 0) {
				pst = conn.prepareStatement(
						" select st_agent_share, st_agent_share_rural from kbstate where st_branch =? and st_code=? and st_active='Y'");
				pst.setInt(1, branchCode);
				pst.setString(2, destState);
				rs = pst.executeQuery();
				if (rs.next()) {
					if (rural)
						agentshipmentCharge = rs.getDouble("st_agent_share_rural");
					else
						agentshipmentCharge = rs.getDouble("st_agent_share");

					agentShareFound = true;
				}
				// System.out.println("agentShareFound in state = "+agentShareFound+"
				// agentshipmentCharge = "+agentshipmentCharge);
				try {
					rs.close();
				} catch (Exception e) {
				}
				try {
					pst.close();
				} catch (Exception e) {
				}

				if (agentShareFound)
					return agentshipmentCharge;
			}
			if (agentshipmentCharge <= 0)
				throw new Exception(
						"Can not find agent share in calcAgentShipmentChargesShare, Please contact support team");

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return agentshipmentCharge;
	}

	/*
	 * get list of pick up agents
	 */

	public LinkedHashMap<String, String> getListOfPickUpAgents(Connection conn, int branchCode) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String, String> pickUpAgentsList = new LinkedHashMap<String, String>();
		try {
			pst = conn.prepareStatement(
					"select us_id , us_name from kbusers where us_rank = 'PICKUPAGENT' and us_branchcode=? order by us_name");
			pst.setInt(1, branchCode);
			rs = pst.executeQuery();
			while (rs.next()) {
				pickUpAgentsList.put(rs.getString("us_id"), rs.getString("us_name"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return pickUpAgentsList;
	}

	/*
	 * get list of agent alpha ordered
	 */
	public LinkedHashMap<String, String> getListOfAgentsPerState(Connection conn, String state, int branchCode)
			throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String, String> agentsList = new LinkedHashMap<String, String>();
		try {
			pst = conn.prepareStatement(
					"select us_id , us_name from kbusers where us_rank='DLVAGENT' "
					+ " and us_to_state like ?  and us_branchcode=? order by us_name");
			pst.setString(1, "%" + state + "%");
			pst.setInt(2, branchCode);
			rs = pst.executeQuery();
			while (rs.next()) {
				agentsList.put(rs.getString("us_id"), rs.getString("us_name"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return agentsList;
	}

	/*
	 * get agent info
	 */
	public LinkedHashMap<String, String> getAgentInfo(Connection conn, String agentId) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String, String> agentInfo = new LinkedHashMap<String, String>();
		try {
			pst = conn.prepareStatement("select us_name , us_createddt from kbusers where  us_id=?");
			pst.setString(1, agentId);
			rs = pst.executeQuery();
			while (rs.next()) {
				agentInfo.put("name", rs.getString("us_name"));
				// agentInfo.put("hp", rs.getString("c_phone1"));
				agentInfo.put("joineddate", rs.getString("us_createddt"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return agentInfo;
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
	public StringBuilder getSingleQuoteCommaSeperated(ArrayList<String> array) {
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

	public StringBuilder getDoubleQuoteCommaSeperated(ArrayList<String> array) {
		boolean first = true;
		StringBuilder sb = new StringBuilder("");
		for (String item : array) {
			if (!first)
				sb.append(",");

			sb.append("\"" + item + "\"");
			first = false;
		}
		return sb;
	}

	public StringBuilder getCommaSeperated(ArrayList<Integer> array) {
		boolean first = true;
		StringBuilder sb = new StringBuilder("");
		for (int item : array) {
			if (!first)
				sb.append(",");

			sb.append("" + item + "");
			first = false;
		}
		return sb;
	}

	public boolean checkGeneratedReceipt(Connection conn, String generatedReceiptNo, int custId) throws Exception {
		boolean ok = true;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			// check if the user exists
			pst = conn.prepareStatement("select 1 from p_books_rcp where br_rcp_no=?");
			pst.setString(1, generatedReceiptNo);
			rs = pst.executeQuery();
			if (rs.next()) {
				ok = true;
			} else {
				ok = false;
				throw new Exception("الوصل رقم " + generatedReceiptNo + " غير متولد من النظام");
			}
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}

			// check if the receipt is used before
			pst = conn.prepareStatement("select br_cid from p_books_rcp where br_rcp_no=? and br_cid >0");
			pst.setString(1, generatedReceiptNo);
			rs = pst.executeQuery();
			if (rs.next()) {
				ok = false;
				throw new Exception("الوصل رقم " + generatedReceiptNo + " تم أستعماله سابقا");
			}
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}

			// check if the receipt is under another customer
			pst = conn.prepareStatement("select br_custid from p_books_rcp where br_rcp_no=?");
			pst.setString(1, generatedReceiptNo);
			rs = pst.executeQuery();
			if (rs.next()) {

				if (rs.getInt("br_custid") > 0)
					if (custId != rs.getInt("br_custid")) {
						ok = false;
						throw new Exception("هذا الوصل " + generatedReceiptNo + " ملك لزبون أخر  ");
					}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return ok;
	}

	public int getMasterCustomerOwnerOfReceipt(Connection a_conn, String a_receiptNo) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		int masterCustId = 0;
		try {
			// check if the receipt is under another customer
			pst = a_conn
					.prepareStatement("select rec_assigned_master_cust from p_receipts where rec_full_receipt_id=?");
			pst.setString(1, a_receiptNo);
			rs = pst.executeQuery();
			if (rs.next()) {
				masterCustId = rs.getInt("rec_assigned_master_cust");
			} else {
				new Exception("الوصل رقم " + a_receiptNo + " لم يتولد من النظام");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {/* ignore */}
			try {pst.close();} catch (Exception e) {/* ignore */}
			}
		return masterCustId;
	}
	
	public int getCustomerOwnerOfReceipt(Connection a_conn, String a_receiptNo) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		int custId = 0;
		try {
			// check if the receipt is under another customer
			pst = a_conn
					.prepareStatement("select rec_assigned_customer from p_receipts where rec_full_receipt_id=?");
			pst.setString(1, a_receiptNo);
			rs = pst.executeQuery();
			if (rs.next()) {
				custId = rs.getInt("rec_assigned_customer");
			} else {
				new Exception("الوصل رقم " + a_receiptNo + " لم يتولد من النظام");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {/* ignore */}
			try {pst.close();} catch (Exception e) {/* ignore */}
		}
		return custId;
	}

	public boolean checkIfReceiptGeneratedFromSystem(Connection conn, String receiptNo, int a_branchCode) throws Exception {
		boolean ok = true;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select 1 from p_receipts join p_receipts_set on rs_full_prefix = rec_set_prefix "
					+ " where rs_branch = ? and rec_full_receipt_id=? ");
			pst.setInt(1, a_branchCode);
			pst.setString(2, receiptNo);
			rs = pst.executeQuery();
			if (rs.next()) {
				ok = true;
			} else {
				ok = false;
				throw new Exception("الوصل رقم " + receiptNo + " غير متولد من النظام");
			}
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return ok;
	}

	public boolean checkIfReceiptUsedBefore(Connection conn, String receiptNo) throws Exception {
		boolean ok = false;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = conn.prepareStatement(
					"select rec_caseid from p_receipts where rec_full_receipt_id=? and rec_caseid >0");
			pst.setString(1, receiptNo);
			rs = pst.executeQuery();
			if (rs.next()) {
				ok = true;
				throw new Exception("الوصل رقم " + receiptNo + " تم أستعماله سابقا");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return ok;
	}

	public void updateAllCustomerShipmentsCost(Connection conn, String custId, int a_branchCode) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<String> states = new ArrayList<String>();
		HashMap<String, Double> stateCost = new HashMap<String, Double>();
		HashMap<String, Double> stateCostRural = new HashMap<String, Double>();
		HashMap<String, Boolean> stateSpecialPriceFound = new HashMap<String, Boolean>();

		try {
			//LocalTime before = LocalTime.now();
			//System.out.println("fetch states info ---");
			pst = conn.prepareStatement(
					" select st_code, st_charges, st_ruralcharges from kbstate where st_active='Y' and st_branch=?");
			pst.setInt(1, a_branchCode);
			rs = pst.executeQuery();
			while (rs.next()) {
				states.add(rs.getString("st_code"));
				stateCost.put(rs.getString("st_code"), rs.getDouble("st_charges"));
				stateCostRural.put(rs.getString("st_code"), rs.getDouble("st_ruralcharges"));
			}
			try {rs.close();} catch (Exception e) {/* ignore */}
			try {pst.close();} catch (Exception e) {/* ignore */}
			//System.out.println("end fetch state info , it took ==>"+before.until(LocalTime.now(),ChronoUnit.SECONDS) );
			
			//before = LocalTime.now();
			//System.out.println("fetch customer special prices---");
			// check if the customer has special prices
			pst = conn.prepareStatement(" select sp_price, sp_rural_price " 
			+ "from kbcustomers "
			+ "join kbcustomer_specialprices on cust_id = sp_custid  "
			+ "where cust_id =? and sp_statecode=? and cust_branch = ?");
			for (String state : states) {
				pst.setString(1, custId);
				pst.setString(2, state);
				pst.setInt(3, a_branchCode);
				rs = pst.executeQuery();
				if (rs.next()) {
					stateCost.put(state, rs.getDouble("sp_price"));
					stateCostRural.put(state, rs.getDouble("sp_rural_price"));
					stateSpecialPriceFound.put(state, true);
				}
				pst.clearParameters();
				try {rs.close();} catch (Exception e) {/* ignore */}
			}
			try {rs.close();} catch (Exception e) {/* ignore */}
			try {pst.close();} catch (Exception e) {/* ignore */}
			//System.out.println("end fetch customer speical prices , it took ==>"+before.until(LocalTime.now(),ChronoUnit.SECONDS) );
			
			//before = LocalTime.now();
			//System.out.println("fetch pickup agent ---");
			// get pickup first from customer table
			int pickUpAgentId = 0;
			pst = conn.prepareStatement(" select cust_assigned_pickup_agent from kbcustomers where cust_id =? ");
			pst.setString(1, custId);
			rs = pst.executeQuery();
			if (rs.next()) {
				pickUpAgentId = rs.getInt("cust_assigned_pickup_agent");
			}
			try {rs.close();} catch (Exception e) {/* ignore */}
			try {pst.close();} catch (Exception e) {/* ignore */}
			//System.out.println("end fetch pick up agent , it took ==>"+before.until(LocalTime.now(),ChronoUnit.SECONDS) );
			
			//before = LocalTime.now();
			//System.out.println("fetch special prices of pickup agent ---");
			// check if the pickUpAgentId has discount
			pst = conn.prepareStatement(" select sp_price, sp_rural_price from kb_pickupagent_prices "
					+ "where sp_agentid=?  and sp_statecode=? ");
			for (String state : states) {
				pst.setInt(1, pickUpAgentId);
				pst.setString(2, state);
				rs = pst.executeQuery();
				if (rs.next()) {
					if (stateSpecialPriceFound.get(state) == null) {
						stateCost.put(state, rs.getDouble("sp_price"));
						stateCostRural.put(state, rs.getDouble("sp_rural_price"));
					}
				}
				pst.clearParameters();
				try {rs.close();} catch (Exception e) {/* ignore */}
			}
			try {rs.close();} catch (Exception e) {/* ignore */}
			try {pst.close();} catch (Exception e) {/* ignore */}
			//System.out.println("end fetch special prices of pickup agent, it took ==>"+before.until(LocalTime.now(),ChronoUnit.SECONDS) );
			

			//before = LocalTime.now();
			//System.out.println("update p_cases  ---");
			pst = conn.prepareStatement("update p_cases set c_shipment_cost=? where c_rcv_state=? and c_custid=? "
					+ "and c_rural=? and c_settled ='NO' and c_pmtid=0 and c_branchcode=? ");
			for (String state : stateCost.keySet()) {
				pst.setDouble(1, stateCost.get(state));
				pst.setString(2, state);
				pst.setString(3, custId);
				pst.setString(4, "N");
				pst.setInt(5, a_branchCode);
				pst.addBatch();

				pst.setDouble(1, stateCostRural.get(state));
				pst.setString(2, state);
				pst.setString(3, custId);
				pst.setString(4, "Y");
				pst.setInt(5, a_branchCode);
				pst.addBatch();
				
			}
			pst.executeBatch();
			//System.out.println("finished updating cases, it took ==>"+before.until(LocalTime.now(),ChronoUnit.SECONDS) );
			conn.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
	}

	/**
	 * Nafie
	 */
	public int generateLiaisonAgentManifestIdForCasesInPrintManifest(Connection conn, int liaisonAgent, int fromBranch,
			int toBranch, int userId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int manifestId = 0;
		int ctr = 0;
		try {
			// check first if there is path already
			pst = conn.prepareStatement("select distinct cc_manifestid as manifestid " + " from p_cases "
					+ " join p_caseschain on c_lastchainid = cc_id  "
					+ " where q_stage = 'BRANCHES' and q_step = 'MANIFEST_BRANCHES' and q_status ='ACTV' "
					+ "  and cc_liaisonagentid=? and cc_frombranch=? and cc_tobranch=? ");
			pst.setInt(1, liaisonAgent);
			pst.setInt(2, fromBranch);
			pst.setInt(3, toBranch);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getInt("manifestid") > 0) {
					manifestId = rs.getInt("manifestid");
					ctr++;
				}
			}
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}

			if (ctr > 1) {
				manifestId = 0;
			}
			if (manifestId == 0) {// generate Id
				pst = conn.prepareStatement(
						"insert into p_liaisonagent_manifest "
								+ "(lam_agentid	, lam_frombranch, lam_tobranch, lam_createdby , lam_date )"
								+ " values(?			 , ?		 , ?			 , ?		      ,  now())",
						Statement.RETURN_GENERATED_KEYS);
				pst.setInt(1, liaisonAgent);
				pst.setInt(2, fromBranch);
				pst.setInt(3, toBranch);
				pst.setInt(4, userId);
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				rs.next();
				manifestId = rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return manifestId;
	}

	public int generateLiaisonAgentManifestIdForCasesInPrintManifest(Connection conn, int pathId, int liaisonAgent,
			int fromBranch, int toBranch, int userId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int manifestId = 0;
		int ctr = 0;
		try {
			// check first if there is path already
			pst = conn.prepareStatement("select distinct cc_manifestid as manifestid " + " from p_cases "
					+ " join p_caseschain on c_lastchainid = cc_id  "
					+ " where q_stage = 'BRANCHES' and q_step = 'MANIFEST_BRANCHES' and q_status ='ACTV' and cc_pathid=? and cc_liaisonagentid=? ");
			pst.setInt(1, pathId);
			pst.setInt(2, liaisonAgent);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getInt("manifestid") > 0) {
					manifestId = rs.getInt("manifestid");
					ctr++;
				}
			}
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}

			if (ctr > 1) {
				manifestId = 0;
			}
			if (manifestId == 0) {// generate Id
				pst = conn.prepareStatement("insert into p_liaisonagent_manifest "
						+ "(lam_agentid	 , lam_pathid, lam_frombranch, lam_tobranch, lam_createdby, " + " lam_date )"
						+ " values(?			 , ?		 , ?			 , ?		   , ?, " + "		  now())",
						Statement.RETURN_GENERATED_KEYS);
				pst.setInt(1, liaisonAgent);
				pst.setInt(2, pathId);
				pst.setInt(3, fromBranch);
				pst.setInt(4, toBranch);
				pst.setInt(5, userId);
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				rs.next();
				manifestId = rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return manifestId;
	}

	/**
	 * Nafie
	 */
	public int generateDlvAgentManifestIdForCasesInPrintManifest(Connection conn, int dlvAgent, int userId,
			int branchId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int manifestId = 0;
		int ctr = 0;
		try {
			// check first if there is path already
			pst = conn.prepareStatement("select distinct c_dlvagent_manifestid as manifestid " + " from p_cases "
					+ " where c_assignedagent =? and c_dlvagent_manifestid != 0  and q_stage = 'INIT' and q_step = 'PRINTMANIFEST' and q_status ='ACTV'");
			pst.setInt(1, dlvAgent);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getInt("manifestid") > 0) {
					manifestId = rs.getInt("manifestid");
					ctr++;
				}
			}
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}

			if (ctr > 1) {
				throw new Exception("يوحد اكثر من رقم كشف لنفس الوصولات لنفس المندوب في مرحلة طباعة المنفيست");
			}
			if (manifestId == 0) {// generate Id
				pst = conn.prepareStatement("insert into p_dlvagentmanifest "
						+ "(dam_agentid, dam_manifest_date, dam_branchid,  dam_createdby) values(?,now(), ? , ?)",
						Statement.RETURN_GENERATED_KEYS);
				pst.setInt(1, dlvAgent);
				pst.setInt(2, branchId);
				pst.setInt(3, userId);

				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				rs.next();
				manifestId = rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return manifestId;
	}

	/*
	 * public int generateDlvAgentManifestIdForCasesInPrintManifest(Connection conn,
	 * int dlvAgent, int userId) throws Exception{ PreparedStatement pst = null;
	 * ResultSet rs = null; int manifestId=0; int ctr = 0; try { //check first if
	 * there is path already pst =
	 * conn.prepareStatement("select distinct c_dlvagent_manifestid as manifestid "
	 * + " from p_cases " +
	 * " where c_assignedagent =? and c_dlvagent_manifestid != 0  and q_stage = 'INIT' and q_step = 'PRINTMANIFEST' and q_status ='ACTV'"
	 * ); pst.setInt(1,dlvAgent); rs = pst.executeQuery(); while (rs.next()) { if
	 * (rs.getInt("manifestid")>0) { manifestId = rs.getInt("manifestid"); ctr++; }
	 * } try {rs.close();}catch(Exception e) {} try {pst.close();}catch(Exception e)
	 * {}
	 * 
	 * if (ctr >1) { throw new Exception
	 * ("يوحد اكثر من رقم منفيست لنفس الوصولات لنفس المندوب في مرحلة طباعة المنفيست"
	 * ); } if (manifestId == 0) {//generate Id pst =
	 * conn.prepareStatement("insert into p_dlvagentmanifest " +
	 * "(dam_agentid, dam_manifest_date, dam_createdby) values(?,"
	 * +sysDateTime+", ?)", Statement.RETURN_GENERATED_KEYS); pst.setInt(1,
	 * dlvAgent); pst.setInt(2, userId); pst.executeUpdate(); rs =
	 * pst.getGeneratedKeys(); rs.next(); manifestId = rs.getInt(1); }
	 * 
	 * }catch(Exception e) { e.printStackTrace(); throw e; }finally { try
	 * {rs.close();}catch(Exception e) {} try {pst.close();}catch(Exception e) {} }
	 * return manifestId; }
	 */
	 

	public static ArrayList<CaseInformation> getCustomerCasesUnderDelivery(
			Connection a_conn, 
			int a_customerId,
			String a_dateRequest,
			int a_branchCode) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<CaseInformation> deliveryList = new ArrayList<CaseInformation>();
		try {
			String sql = "select cust_name, c_id, c_rcv_hp1, c_custreceiptnoori, c_receiptamt,"
					+ " c_receiptamt_usd, c_shipment_cost, (c_receiptamt-c_shipment_cost) as net , "
					+ "c_created_date_only, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr,"
					+ " c_rmk "
					+ " from p_cases "
					+ " join kbcustomers on (cust_id = c_custid) "
					+ " left join kbstate on st_code = c_rcv_state and st_branch = c_branchcode "
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where c_mastercustid =? "
					+ " and q_stage not in ('CNCL', 'DLV') and c_branchcode=? and 1=1 order by c_id ";
			if(!a_dateRequest.equalsIgnoreCase("ALL")) {
				sql = sql.replace("1=1", "c_created_date_only=?");
			}
			pst = a_conn.prepareStatement(sql);
			
			pst.setInt(1, a_customerId);
			pst.setInt(2, a_branchCode);
			if(!a_dateRequest.equalsIgnoreCase("ALL")) {
				pst.setString(3, a_dateRequest);
			}
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo = new CaseInformation();
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setCreateddt(rs.getString("c_created_date_only"));
				caseInfo.setReceiptAmtIqd(rs.getInt("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setSenderName(rs.getString("cust_name"));
				deliveryList.add(caseInfo);
			}
			//System.out.println("----------------"+deliveryList.size()+", a_branchCode=>"+a_branchCode+", a_customerId =>"+a_customerId);
		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return deliveryList;
	}

		
	public CustomerPaymentBean getCustomerPaymentInfo(Connection a_conn, int a_transId) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		CustomerPaymentBean cpb = new CustomerPaymentBean();
		ArrayList<CaseInformation> casesList = new ArrayList<CaseInformation>();
		try {
			cpb.setStandardTransactionBean(UtilitiesStandardFinancials.getTransactionInfo(a_conn, a_transId));
			pst = a_conn.prepareStatement(
			"select c_branchcode, c_priceb4change, c_changedprice, date(c_createddt) as c_createddt, "
					+ " c_id, c_rcv_name, c_rcv_hp1,c_rcv_state, cust_name, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr , "
					+ " c_qty, c_rmk, c_receiptamt, c_receiptamt_usd, c_shipment_cost, " 
					+ " (c_receiptamt - c_shipment_cost) as netamt, "
					+ "c_partial_return,"
					+ " (case when (q_stage='DLV' ) then 'DLV' else 'CNCL'  end) as status,"
					+ " c_custreceiptnoori, q_step" 
					+ " from p_cases "
					+ " join kbcustomers on (cust_id = c_custid) "
					+ " left join kbstate on st_code = c_rcv_state and st_branch = c_branchcode "
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where c_pmtid=? order by c_custreceiptnoori ");
			pst.setInt(1, a_transId);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo = new CaseInformation();
				caseInfo.setChangedPrice(rs.getString("c_changedprice"));
				caseInfo.setReceiptAmtB4Change(rs.getDouble("c_priceb4change"));
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setReceiverName(rs.getString("c_rcv_name"));
				caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setReceiptAmtIqd(rs.getInt("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setNetPrice(rs.getDouble("netamt"));
				caseInfo.setStatus(rs.getString("status"));
				caseInfo.setCreateddt(rs.getString("c_createddt"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setStepCode(rs.getString("q_step"));
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setOrigintingBranch(rs.getInt("c_branchcode"));
				casesList.add(caseInfo);
			}
			cpb.setShipments(casesList);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return cpb;
	}

	// get items per liaison agent
	public ArrayList<CaseInformation> getItemsPerLiaisonAgent(Connection conn, int driverid, String stgCode,
			String stpCode, int toBranch, int fromBranch, int pathId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<CaseInformation> deliveryList = new ArrayList<CaseInformation>();
		try {

			pst = conn.prepareStatement(
					"select cc_liaisonagentid, cc_id, cc_frombranch, cc_tobranch, c_custid, c_specialcase,  c_rural, date(c_createddt) as c_createddt , cust_name, c_id, c_rcv_name, c_rcv_hp1,c_rcv_state, "
							+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr , "
							+ " c_qty, c_rmk, c_shipment_cost, c_receiptamt, c_receiptamt_usd, c_partial_return, ifnull(c_fragile,'N') as c_fragile  , c_sendmoney, c_custreceiptnoori"
							+ " from p_cases  " 
							+ " join p_caseschain on c_lastchainid = cc_id "
							+ " left join kbcustomers on cust_id = c_custid "
							+ " left join kbstate on st_code = c_rcv_state"
							+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
							+ " where  q_stage =? and q_step=? and q_status = 'ACTV' and cc_pathid=? and cc_liaisonagentid=? "
							+ " order by c_rcv_state ,c_rcv_district, c_id ");
			pst.setString(1, stgCode);
			pst.setString(2, stpCode);
			pst.setInt(3, pathId);
			pst.setInt(4, driverid);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo = new CaseInformation();
				caseInfo.setSpecialCase(rs.getString("c_specialcase"));
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
				caseInfo.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setFragile(rs.getString("c_fragile"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setRural(rs.getString("c_rural"));
				caseInfo.setSenderId(rs.getInt("c_custid"));
				caseInfo.setLatestChainId(rs.getInt("cc_id"));
				caseInfo.setLiaisonAgent(rs.getInt("cc_liaisonagentid"));
				deliveryList.add(caseInfo);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return deliveryList;
	}

	/*
	 * get list of agent alpha ordered
	 */

	// updateFeqar
	public LinkedHashMap<String, String> getListOfAgents(Connection conn, int branchCode) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String, String> agentsList = new LinkedHashMap<String, String>();
		try {
			pst = conn.prepareStatement(
					"select us_id , concat(us_name,' , ', ifnull(us_hp,'')) as us_name"
					+ " from kbusers where us_rank='DLVAGENT' and us_branchcode=?"
					+ " and us_active='Y' order by us_name");
			pst.setInt(1, branchCode);
			rs = pst.executeQuery();
			while (rs.next()) {
				agentsList.put(rs.getString("us_id"), rs.getString("us_name"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return agentsList;
	}

	/*
	 * get pickup agent info
	 */
	public LinkedHashMap<String, String> getPickUpAgentInfo(Connection conn, String c_id) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String, String> pickUpAgentInfo = new LinkedHashMap<String, String>();
		try {
			pst = conn.prepareStatement("select us_name , us_hp from kbusers   where us_id=?");
			pst.setString(1, c_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				pickUpAgentInfo.put("name", rs.getString("us_name"));
				pickUpAgentInfo.put("hp", rs.getString("us_hp"));
				// customerInfo.put("joineddate", rs.getString("joineddate"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return pickUpAgentInfo;
	}

	/*
	 * get customer info
	 */
	public LinkedHashMap<String, String> getcustomerInfo(Connection conn, String cust_id) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String, String> customerInfo = new LinkedHashMap<String, String>();
		try {
			pst = conn.prepareStatement(
					"select cust_name , cust_phone1, date(cust_createddt) as joineddate from kbcustomers  where cust_id=?");
			pst.setString(1, cust_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				customerInfo.put("name", rs.getString("cust_name"));
				customerInfo.put("hp", rs.getString("cust_phone1"));
				customerInfo.put("joineddate", rs.getString("joineddate"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return customerInfo;
	}

	/*
	 * get list of rcp numbers
	 */
	public ArrayList<String> getRcpNoList(Connection conn, int bookId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<String> rcpIdList = new ArrayList<String>();
		try {
			pst = conn.prepareStatement("select br_rcp_no from p_books_rcp where br_bid=?");
			pst.setInt(1, bookId);
			rs = pst.executeQuery();
			while (rs.next()) {
				rcpIdList.add(rs.getString("br_rcp_no"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return rcpIdList;
	}

////////////////////////////////////////////------------SAFE FINANCIALS -----------/////////////////////////////////////////////////////////////

//	public void acctBoxTransactions(Connection conn, int paymentId, String PaymentTabel, String tranType,
//			String userLoginId, int acctBoxId, double accountBeforTransaction, double payment) throws Exception {
//		PreparedStatement pst = null;
//		try {
//			pst = conn.prepareStatement(
//					"insert into p_acctbox_transactions (abt_paymentid, abt_payment_table, abt_safe_impact, abt_createdby, abt_date, abt_acctboxid, abt_accountbefore_transaction, abt_payment) "
//							+ "values (?,?,?,?, Date(NOW()),?,?,?)");
//			pst.setInt(1, paymentId);
//			pst.setString(2, PaymentTabel);
//			pst.setString(3, tranType);
//			pst.setString(4, userLoginId);
//			pst.setInt(5, acctBoxId);
//			pst.setDouble(6, accountBeforTransaction);
//			pst.setDouble(7, payment);
//			pst.executeUpdate();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		} finally {
//			try {
//				pst.close();
//			} catch (Exception e) {
//			}
//		}
//
//	}
	
	public static HashMap<StandardFinCurrency, Long> getSafeBalance (Connection conn, int branchid) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<StandardFinCurrency, Long> entityDebtBalance = new HashMap<StandardFinCurrency, Long>();
		entityDebtBalance.put(StandardFinCurrency.IQD,0L);
		entityDebtBalance.put(StandardFinCurrency.USD,0L);
		try{
			pst = conn.prepareStatement("select saf_iqd_after_transaction, saf_usd_after_transaction"
					+ " from p_safe where saf_branchid = ? order by saf_id desc limit 1");
			pst.setInt(1, branchid);
			rs = pst.executeQuery();
			if (rs.next()) {
				entityDebtBalance.put(StandardFinCurrency.IQD, rs.getLong("saf_iqd_after_transaction"));
				entityDebtBalance.put(StandardFinCurrency.USD, rs.getLong("saf_usd_after_transaction"));
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return entityDebtBalance;
	}

	public HashMap<Boolean, String> ArchiveAllSafeTransaction(Connection a_Conn, String a_UserId, int a_BranchId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int lastSafeId = 0;
		String msg = "تم الجرد";
		Boolean everythingOK = true;
		HashMap<Boolean, String> hashy = new HashMap<Boolean, String>();
		try {		
			HashMap<StandardFinCurrency, Long> balance = getSafeBalance(a_Conn, a_BranchId);
			if(balance.get(StandardFinCurrency.IQD)<0 || balance.get(StandardFinCurrency.USD)<0) {
				throw new Exception("Balance of safe less than zero you can't RESTARTNEW");
			}
			
			pst = a_Conn.prepareStatement("SELECT saf_tranname FROM p_safe "
					+ "where saf_id = (select max(saf_id) from p_safe where saf_branchid=?)");
			pst.setInt(1, a_BranchId);
			rs = pst.executeQuery();
			if(rs.next()) {
				if(rs.getString("saf_tranname").equalsIgnoreCase("RESTARTNEW"))
					throw new Exception("The last transaction is RESTARTNEW you can't RESTARTNEW again");
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			pst = a_Conn.prepareStatement("insert into "
					+ "p_safe (saf_iqd_before_transaction, saf_usd_before_transaction ,"
					+ " saf_iqd_after_transaction		 , saf_usd_after_transaction, "
					+ " saf_trantype					 , saf_tranname, "
					+ " saf_tranentity					 , saf_createdby, "
					+ "	saf_rmk		 					 , saf_branchid,"
					+ " saf_trandate ) "
					+ "values ("+CoreUtilities.getQuestionMarks(10)+", now() )",Statement.RETURN_GENERATED_KEYS);
			pst.setLong(1, balance.get(StandardFinCurrency.IQD));
			pst.setLong(2, balance.get(StandardFinCurrency.USD));
			pst.setLong(3, balance.get(StandardFinCurrency.IQD));
			pst.setLong(4, balance.get(StandardFinCurrency.USD));
			pst.setString(5, "CR_SAFE");
			pst.setString(6, "RESTARTNEW");
			pst.setString(7, a_UserId);
			pst.setString(8, a_UserId);
			pst.setString(9, "بداية جديدة بعد جرد القاصة");
			pst.setInt(10, a_BranchId);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			if (rs.next()) {
				lastSafeId = rs.getInt(1);
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			pst = a_Conn.prepareStatement("insert into p_safe_hist "
					+ "		select * , ?, now(), ? from p_safe where saf_tranname != 'RESTARTNEW' and saf_branchid = ?");
			pst.setString(1, a_UserId);
			pst.setInt(2, lastSafeId);
			pst.setInt(3, a_BranchId);
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			
			pst = a_Conn.prepareStatement("delete from p_safe where saf_tranname != 'RESTARTNEW' and saf_branchid = ? ");
			pst.setInt(1, a_BranchId);
			pst.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			everythingOK = false;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		hashy.put(everythingOK, msg);
		return hashy;
	}

//	public double getSafeBalance(Connection conn) throws Exception {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		double balance = 0.0;
//		try {
//			pst = conn.prepareStatement("select " + "	case " + "		when saf_trantype='CR' "
//					+ "        then (ifnull(saf_before_transaction,0)+saf_amount_iqd)"
//					+ "        else (ifnull(saf_before_transaction,0)-saf_amount_iqd)" + "	end as balunce"
//					+ "   from p_safe order by saf_id desc limit 1");
//			rs = pst.executeQuery();
//			if (rs.next()) {
//				balance = rs.getDouble(1);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		} finally {
//			try {
//				rs.close();
//			} catch (Exception e) {
//			}
//			try {
//				pst.close();
//			} catch (Exception e) {
//			}
//		}
//		return balance;
//	}

	public LinkedHashMap<String, String> getListOfMasterCustomers(Connection conn, int branchCode) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String, String> masterCustomersList = new LinkedHashMap<String, String>();
		try {
			pst = conn.prepareStatement(
					"select mcust_id , mcust_name from kb_mastercustomer where mcust_branchcode=? and mcust_active='Y' ");
			pst.setInt(1, branchCode);
			// pst.setString(1, branchCode);
			rs = pst.executeQuery();
			while (rs.next()) {
				masterCustomersList.put(rs.getString("mcust_id"), rs.getString("mcust_name"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return masterCustomersList;
	}

	public AgentPaymentBean getAgentPaymentInfo(Connection a_conn, int a_transId, int a_branchCode) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		AgentPaymentBean apb = new AgentPaymentBean();
		ArrayList<CaseInformation> casesList = new ArrayList<CaseInformation>();
		try {
			apb.setStandardTransactionBean(UtilitiesStandardFinancials.getTransactionInfo(a_conn, a_transId));
			apb.setAgentName(getAgentName(a_conn, apb.getStandardTransactionBean().getEntityId()));
			//apb.setBalanceBeforePayement(getAgentDebtBalanceUpToSpecificPayment(a_conn, apb.getAgentId(), pmtId));
			if (apb.getStandardTransactionBean().getCode() == FinOperationCode.CASES ) {
				pst = a_conn.prepareStatement(
				"select  q_stage, q_step, c_id, c_changedprice,c_priceb4change  , "
				+ " cust_name, date(c_createddt) as c_createddt,  c_id, c_rcv_name, c_rcv_hp1,c_rcv_state, "
				+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr ,"
				+ " c_rural,dam_manifest_date, c_dlvagent_manifestid, "
				+ " c_qty, c_rmk,c_receiptamt, c_usdchangedprice, c_receiptamt_usd ,c_usdpriceb4change, c_partial_return ,"
				+ " (case when (q_stage='DLV')  then 'DLV' else 'canceled'  end) as status,"
				+ " c_custreceiptnoori, "
				+ " (case when (q_stage='DLV') then c_agentshare else 0 end) as c_agentshare "
				+ " from p_cases  " 
				+ " left join p_dlvagentmanifest on dam_id = c_dlvagent_manifestid "
				+ " left join kbcustomers on cust_id = c_custid "
				+ " left join kbstate on st_code = c_rcv_state  and st_branch=?"
				+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
				+ " where c_agentpmtid=? and c_agentsharesettled ='FULL' order by c_rcv_state ");
				pst.setInt(1, a_branchCode);
				pst.setInt(2, a_transId);
				rs = pst.executeQuery();
				CaseInformation caseInfo;
				while (rs.next()) {
					caseInfo = new CaseInformation();
					caseInfo.setCaseid(rs.getInt("c_id"));
					caseInfo.setSenderName(rs.getString("cust_name"));
					caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
					caseInfo.setState(rs.getString("c_rcv_state"));
					caseInfo.setLocationDetails(rs.getString("addr"));
					caseInfo.setRural(rs.getString("c_rural"));
					caseInfo.setDlvAgentManifestDate(rs.getString("dam_manifest_date"));
					caseInfo.setDlvAgentManifestId(rs.getInt("c_dlvagent_manifestid"));
					caseInfo.setQty(rs.getInt("c_qty"));
					caseInfo.setRmk(rs.getString("c_rmk"));
					caseInfo.setReceiptAmtB4Change(rs.getDouble("c_priceb4change"));
					caseInfo.setChangedPrice(rs.getString("c_changedprice"));
					caseInfo.setChangedPriceUsd(rs.getString("c_usdchangedprice"));
					caseInfo.setReceiptAmtUsdB4Change(rs.getDouble("c_usdpriceb4change"));
					caseInfo.setStageCode(rs.getString("q_stage"));
					caseInfo.setStepCode(rs.getString("q_step"));
					caseInfo.setReceiptAmtIqd(rs.getInt("c_receiptamt"));
					caseInfo.setReceiptAmtUsd(rs.getInt("c_receiptamt_usd"));
					caseInfo.setStatus(rs.getString("status"));
					caseInfo.setCreateddt(rs.getString("c_createddt"));
					caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
					caseInfo.setAgentShare(rs.getDouble("c_agentshare"));
					casesList.add(caseInfo);
				}
				apb.setShipments(casesList);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return apb;

	}

	public MasterCustomerShipmentBackBean getCustReturnBackedUpInfo(Connection conn, int acrId, int userBranchId)
			throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		MasterCustomerShipmentBackBean cpb = new MasterCustomerShipmentBackBean();
		ArrayList<CaseInformation> casesList = new ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement("select mcust_name, date(acr_createddt) as acr_createddt, acr_rmk "
					+ " from p_customer_return join kb_mastercustomer on acr_mastercustid= mcust_id  where acr_id=?");
			pst.setInt(1, acrId);
			rs = pst.executeQuery();
			if (rs.next()) {
				cpb.setBackedDate(rs.getString("acr_createddt"));
				cpb.setMasterCustName(rs.getString("mcust_name"));
				cpb.setBackedRmk(rs.getString("acr_rmk"));
			}
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
			pst = conn.prepareStatement(
			"select c_usdpriceb4change , c_usdchangedprice , c_changedprice, c_priceb4change,"
			+ "  cust_name, date(c_createddt) as c_createddt, c_id,"
			+ "  c_rcv_name, c_rcv_hp1,c_rcv_state, "
					+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ' ,ifnull(c_rcv_addr_rmk,'')) as addr , "
					+ " c_shipment_cost, c_receiptamt,  c_receiptamt_usd, "
					+ " (case when c_partial_return='Y' then c_partial_qtyrtn else c_qty end) as qty,"
					+ " c_rmk, c_receiptamt, c_fragile , c_custreceiptnoori, q_stage, c_agentshare,"
					+ " c_parentid "
					+ " from p_cases  " 
					+ " left join kbcustomers on cust_id = c_custid "
					+ " left join kbstate on st_code = c_rcv_state and st_branch=? "
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where q_status != 'CLS' and c_cust_rtnid=? order by c_custid, c_custreceiptnoori ");
			pst.setInt(1, userBranchId);
			pst.setInt(2, acrId);
			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo = new CaseInformation();
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setParentId(rs.getInt("c_parentid"));
				caseInfo.setReceiptAmtIqd(rs.getInt("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getDouble("c_receiptamt_usd"));

				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setFragile(rs.getString("c_fragile"));

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
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return cpb;
	}

	public ArrayList<CaseInformation> getItemsPerManifest(Connection conn, String manifestId, String storeCode)
			throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<CaseInformation> itemsList = new ArrayList<CaseInformation>();

		try {
			pst = conn.prepareStatement(
			"select cust_phone1, c_branchcode, c_dlvagent_manifestid, c_custid, c_specialcase,  "
			+ " c_rural, date(c_dategiventodlvagent) as q_enterdate , cust_name, c_id, "
			+ " c_rcv_name, c_rcv_hp1,c_rcv_state, c_mastercustid, c_rcv_district, "
			+ " concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr , "
			+ " c_qty, c_rmk, c_shipment_cost, c_receiptamt, c_receiptamt_usd, c_partial_return, "
			+ " c_custreceiptnoori "
			+ " from p_cases  " + " left join kbcustomers on cust_id = c_custid "
			+ " left join kbstate on st_code = c_rcv_state and st_branch = ? "
			+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
			+ " where c_dlvagent_manifestid = ? and q_status = 'ACTV'  "
			+ " order by c_mastercustid, c_custid ");
			pst.setString(1, storeCode);
			pst.setString(2, manifestId);

			rs = pst.executeQuery();
			CaseInformation caseInfo;
			while (rs.next()) {
				caseInfo = new CaseInformation();
				caseInfo.setOrigintingBranch(rs.getInt("c_branchcode"));
				caseInfo.setSpecialCase(rs.getString("c_specialcase"));
				caseInfo.setCaseid(rs.getInt("c_id"));
				caseInfo.setSenderName(rs.getString("c_rcv_name"));
				caseInfo.setReceiverHp1(rs.getString("c_rcv_hp1"));
				caseInfo.setState(rs.getString("c_rcv_state"));
				caseInfo.setDistrict(rs.getInt("c_rcv_district"));
				caseInfo.setLocationDetails(rs.getString("addr"));
				caseInfo.setQty(rs.getInt("c_qty"));
				caseInfo.setRmk(rs.getString("c_rmk"));
				caseInfo.setQueueEnterDate(rs.getString("q_enterdate"));
				caseInfo.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
				caseInfo.setReceiptAmtUsd(rs.getInt("c_receiptamt_usd"));
				caseInfo.setShipmentCharge(rs.getInt("c_shipment_cost"));
				caseInfo.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				caseInfo.setSenderName(rs.getString("cust_name"));
				caseInfo.setRural(rs.getString("c_rural"));
				caseInfo.setSenderId(rs.getInt("c_custid"));
				caseInfo.setSenderHp(rs.getString("cust_phone1"));
				caseInfo.setMasterSenderId(rs.getInt("c_mastercustid"));
				caseInfo.setManifestId(rs.getInt("c_dlvagent_manifestid"));
				itemsList.add(caseInfo);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return itemsList;
	}

	public static MasterCustomerInfoBean getMasterCustomerInfo(Connection a_conn, int a_masterCustId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		MasterCustomerInfoBean masterCustomerInfoBean = new MasterCustomerInfoBean();
		try {
			pst = a_conn.prepareStatement("select mcust_pickupagent, mcust_id, mcust_name, "
					+ "mcust_phone1, mcust_branchcode from kb_mastercustomer where mcust_id = ? ");
			pst.setInt(1, a_masterCustId);
			rs = pst.executeQuery();
			if (rs.next()) {
				masterCustomerInfoBean.setBelongToBranch(rs.getInt("mcust_branchcode"));
				masterCustomerInfoBean.setId(a_masterCustId);
				masterCustomerInfoBean.setName(rs.getString("mcust_name"));
				masterCustomerInfoBean.setPhone1(rs.getString("mcust_phone1"));
				masterCustomerInfoBean.setPickUpagent(rs.getInt("mcust_pickupagent"));
				return masterCustomerInfoBean;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
	}
	
	public static CustomerInfoBean getCustomerInfo(Connection a_conn, int a_custId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		CustomerInfoBean customerInfoBean = new CustomerInfoBean();
		try {
			pst = a_conn.prepareStatement("select cust_mastercustid, cust_branch, cust_id, cust_name, "
					+ "cust_phone1, cust_assigned_pickup_agent from kbcustomers where cust_id = ? ");
			pst.setInt(1, a_custId);
			rs = pst.executeQuery();
			if (rs.next()) {
				customerInfoBean.setCustBelongToBranch(rs.getInt("cust_branch"));
				customerInfoBean.setCustId(a_custId);
				customerInfoBean.setCustName(rs.getString("cust_name"));
				customerInfoBean.setCustHp(rs.getString("cust_phone1"));
				customerInfoBean.setPickUpAgent(rs.getInt("cust_assigned_pickup_agent"));
				customerInfoBean.setMasterCustId(rs.getInt("cust_mastercustid"));
				return customerInfoBean;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
	}

	// select rbook_used, rbook_assinged_master_cust, rbook_setprefix, rbook_no from
	// p_receipts_books where rbook_id = 2
	public static ReceiptsBookBean getReceiptsBookInfo(Connection a_conn, int a_bookId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ReceiptsBookBean receiptsBookBean = new ReceiptsBookBean();
		try {
			pst = a_conn.prepareStatement("select rbook_used, rbook_assigned_master_cust, "
					+ "  rbook_assigned_customer, rbook_setprefix, "
					+ " rbook_no , rbook_setid " 
					 + "from p_receipts_books where rbook_id = ? ");
			pst.setInt(1, a_bookId);
			rs = pst.executeQuery();
			if (rs.next()) {
				receiptsBookBean.setId(a_bookId);
				receiptsBookBean.setBookNo(rs.getInt("rbook_no"));
				receiptsBookBean.setSetId(rs.getInt("rbook_setid"));
				receiptsBookBean.setSetPrefix(rs.getString("rbook_setprefix"));
				receiptsBookBean.setAssignedMasterCustomer(rs.getInt("rbook_assigned_master_cust"));
				receiptsBookBean.setAssignedCustomer(rs.getInt("rbook_assigned_customer"));
				if (rs.getString("rbook_used").equalsIgnoreCase("N")) {
					receiptsBookBean.setUsed(false);
				} else {
					receiptsBookBean.setUsed(true);
					receiptsBookBean.setTherePaymentMadeForAnyReceipt(isTherePaymentMadeForAnyReceipt(a_conn,a_bookId));
				}
				
				return receiptsBookBean;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
	}

	public static boolean isTherePaymentMadeForAnyReceipt(Connection a_conn, int a_bookId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = a_conn.prepareStatement("select 1 from p_cases where c_id in ("
					+ " select rec_caseid From p_receipts "
					+ " join p_receipts_books "
					+ "	on rec_set_prefix  = rbook_setprefix and rec_receipt_book_no = rbook_no"
					+ " where rbook_id =? and rec_caseid is not null)"
					+ " and c_pmtid >0 and c_pickupagentpmtid > 0"
					+ " limit 1");
			pst.setInt(1, a_bookId);
			rs = pst.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1)==1) {
					return true;
				}
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pst.close();
			} catch (Exception e) {
			}
		}
		return false;
	}
	
	public HashMap<StandardFinCurrency, Long> getEntityDebtBalanceUpToSpecificPayment(Connection a_conn,
			FinOperationEntity a_FinOperationEntity, int a_entityId, int a_pmtId, int a_initiatedInBranch) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<StandardFinCurrency, Long> entityDebtBalance = new HashMap<StandardFinCurrency, Long>();
		try {
			pst = a_conn.prepareStatement("select sum(trans_debit_iqd - trans_credit_iqd ) as debt_iqd,"
					+ "  sum(trans_debit_usd - trans_credit_usd ) as debt_usd "
					+ " from p_fin_transactions where trans_operationentity =? and trans_entity_id=? "
					+ " and  (trans_id <=? or 0 = ?) and trans_deleted = 'N' and trans_initiated_in_branch_id=? ");
			pst.setString(1, a_FinOperationEntity.name());
			pst.setInt(2, a_entityId);
			pst.setInt(3, a_pmtId);
			pst.setInt(4, a_pmtId);
			pst.setInt(5, a_initiatedInBranch);
			rs = pst.executeQuery();
			if (rs.next()) {
				entityDebtBalance.put(StandardFinCurrency.IQD, rs.getLong("debt_iqd"));
				entityDebtBalance.put(StandardFinCurrency.USD, rs.getLong("debt_usd"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return entityDebtBalance;
	}

	public static String getAgentName(Connection a_conn, int a_agentId) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = a_conn.prepareStatement("select us_name , us_createddt from kbusers where  us_id=?");
			pst.setInt(1, a_agentId);
			rs = pst.executeQuery();
			while (rs.next()) {
				return rs.getString("us_name");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return null;
	}

}
