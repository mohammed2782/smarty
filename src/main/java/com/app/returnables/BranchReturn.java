package com.app.returnables;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.swing.InputMap;

import com.app.bussframework.FlowUtils;
import com.app.cases.CaseInformation;
import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;

public class BranchReturn extends CoreMgr {
	private LinkedHashMap <String,String> repeatedNo = new LinkedHashMap<String,String>();
	private LinkedHashMap <String,String> repeatedHp = new LinkedHashMap<String,String>();
	private int records = 0;
	private double totDueAmt = 0;
	private double totReceiptAmt = 0;
	private int noOfRtnitems = 0;
	boolean errorFlag = false;
	
	private int rtnItemsPaidByCustomer = 0;
	private int rtnItemsPaidBySender = 0;
	private int dlvItmes =0;
	private int ruralAreaItems=0;
	public BranchReturn() {
		records = 0;
		MainSql = "select c_rmk , '' as pmtCheckBox, '' as selectedcases,'' as selectedcaseshidden, cust_name, q_stage, q_step, "
				+ " '' as status , 'شحنات سلمت وراجعه ' as title, c_custid,c_custreceiptnoori,'' as rtnrmk, cc_liaisonagentid, "
				+ " date(c_createddt) as c_createddt, concat(st_name_ar,' - ',ifnull(c_rcv_addr_rmk,'')) as addr, c_id,"
				+ " c_rcv_name , c_rcv_hp1, cc_tobranch,  '' as fromdate, '' as todate, cust_id, cc_pathid, "
				+ " c_changedprice, c_priceb4change, c_receiptamt as currentreceiptprice, c_shipmentpaidbycustomer, c_shipmentpaidbysender, c_rural"
				+ " from p_cases "
				+ " left join kbcustomers on cust_id = c_custid "
				+ " left join kbstate on st_code = c_rcv_state and st_branch = {userstorecode}"
				+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch = {branchAccountReturnProcess} and cc_tobranch={userstorecode} ) "
				+ " where c_agentrtnid>0 and (q_branch={userstorecode} and q_stage= 'BRANCHES' and q_step='RTN_INSTORE_WAITLIAISON' and q_status ='ACTV')";
		

		userDefinedGroupColsOrderBy = "c_createddt,c_id";
		UserDefinedPageRows = 50000;
		userDefined_x_panelclass = "account_x_panel";
		userDefinedGroupByCol = "c_createddt";
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("c_id");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("status");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("pmtCheckBox");
		
		userDefinedColLabel.put("c_id", "رقم الشحنه");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("c_createddt", "تاريخ الإدخال");
		userDefinedColLabel.put("c_rcv_name", "إسم المستلم");
		userDefinedColLabel.put("addr", "العنوان");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("selectedcases", "عدد الوصولات المحددة");
		userDefinedColLabel.put("pmtCheckBox", "تجهيز");		
		userDefinedColLabel.put("cc_liaisonagentid", "مندوب الارتباط");
		userDefinedColLabel.put("pmtdate", "تاريخ التجهيز");
		userDefinedColLabel.put("rtnrmk", "ملاحظات");
		userDefinedColLabel.put("status", "الحاله");
		userDefinedColLabel.put("fromdate","من تاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		userDefinedColLabel.put("cust_name", "صاحب المحل");
		userDefinedColLabel.put("cust_id", "إسم الزبون");
		
		userDefinedPageFooterFunction = "myFooterFunction()";
		canNew = true;
		mainTable = "p_cases";
		keyCol = "c_id";
		
		userDefinedNewCols.add("cc_pathid");
		userDefinedReadOnlyNewCols.add("cc_pathid");
		userDefinedNewCols.add("cc_liaisonagentid");
		userDefinedNewCols.add("selectedcases");
		userDefinedNewCols.add("pmtdate");
		userDefinedNewColsDefualtValues.put("pmtdate", new String[] {"%select date(now())"});
		userDefinedReadOnlyNewCols.add("pmtdate");
		userDefinedNewCols.add("rtnrmk");
		userDefinedNewCols.add("selectedcaseshidden");
		userDefinedHiddenNewCols.add("selectedcaseshidden");
		
		userModifyTD.put("c_custreceiptnoori", "modifyReceiptNo({c_custreceiptnoori},{c_id})");
		userModifyTD.put("c_rcv_hp1", "modifyRcvNo({c_rcv_hp1})");
		userModifyTD.put("status", "modifyStatus({c_changedprice},{c_priceb4change}, {currentreceiptprice},{q_stage}, {q_step}, {c_shipmentpaidbycustomer}, {c_shipmentpaidbysender},{c_rural})");
		
		userDefinedColsMustFill.add("c_custid");
		userDefinedColsMustFill.add("cc_liaisonagentid");
		userDefinedLookups.put("c_custid", "select cust_id, cust_name from kbcustomers");
		userDefinedNewColsDefualtValues.put("cc_liaisonagentid", new String[] {"%select cc_liaisonagentid from p_caseschain "
				+ "join p_cases on(c_id = cc_caseid and cc_frombranch = {branchAccountReturnProcess} and cc_tobranch={userstorecode} ) "
				+ " where (q_branch={userstorecode} and q_stage= 'BRANCHES' and q_step='RTN_INSTORE_WAITLIAISON' and q_status ='ACTV')"});
		userDefinedNewColsDefualtValues.put("cc_pathid", new String[] {"%select cc_pathid from p_caseschain "
				+ "join p_cases on(c_id = cc_caseid and cc_frombranch = {branchAccountReturnProcess} and cc_tobranch={userstorecode} ) "
				+ " where (q_branch={userstorecode} and q_stage= 'BRANCHES' and q_step='RTN_INSTORE_WAITLIAISON' and q_status ='ACTV') "});
		userDefinedHiddenNewCols.add("cc_pathid");

		userDefinedLookups.put("cc_liaisonagentid", "select us_id, us_name from kbusers where us_rank = 'LIAISONAGENT'");
		userDefinedNewColsHtmlType.put("cc_liaisonagentid", "DROPLIST");
		userDefinedNewColsHtmlType.put("rtnrmk", "TEXTAREA");
		userDefinedReadOnlyNewCols.add("cc_liaisonagentid");
		userDefinedNewColsHtmlType.put("selectedcases", "TEXT");
		userDefinedReadOnlyNewCols.add("selectedcases");
		userDefinedNewCaption = "تجهيز الراجع لمندوب الارتباط";
		userDefinedCaption= userDefinedNewCaption;
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		userDefinedColsTypes.put("c_rcv_hp1", "VARCHAR");//to remove the comma
		//myhtmlmgr.tableClass = "table table-striped  table-bordered turquoise_table";
		userModifyTD.put("pmtCheckBox", "displayCheckBox({c_id})");
		
		//canFilter = true;
		userDefinedFilterCols.add("cust_id");
		userDefinedFilterColsHtmlType.put("cust_id", "DROPLIST");
		userDefinedLookups.put("cust_id", "select cust_id, cust_name from kbcustomers");
		
	}
	
	@Override
	public void initialize(HashMap smartyStateMap){
		
		boolean checkBoxPayment = false;
		String selectedCasesForPayment = "";
		Map<String, String[]> parameters = httpSRequest.getParameterMap();
		ArrayList<String> casesToPayList = new ArrayList<String>();
		userDefinedCaption= "<div class='col-md-10 col-sm-12 col-xs-12'>"+this.userDefinedCaption+"</div>"
				+ " <div class='col-md-2 col-sm-12 col-xs-12'>" + 
				"                        <input id=\"allprepair\" class=\"\" onclick=\"changeToPrepairAll('all_prepair');\" type=\"checkbox\">" + 
				"                        <label for=\"allprepair\">" + 
				"                           تجهيز الكل" + 
				"                        </label>" + 
				"                    </div>";
		for(String parameter : parameters.keySet()) {
			if (!excludeKeyWords.contains(parameter)){
			
				if (parameter.equalsIgnoreCase("op") && parameters.get("op")!=null && parameters.get("op")[0].equalsIgnoreCase("new")) {
					if (parameters.containsKey("selected_casesto_pay") && parameters.get("selected_casesto_pay")!=null
							&& !parameters.get("selected_casesto_pay")[0].equalsIgnoreCase("")) {
						selectedCasesForPayment = parameters.get("selected_casesto_pay")[0];
						casesToPayList = Utilities.SplitStringToArrayList(selectedCasesForPayment , ",");	
						checkBoxPayment = true;
						
					}
				}
			}
		}
		int branchId = Integer.parseInt(replaceVarsinString(" {branchAccountReturnProcess} ", arrayGlobals).trim());
		int userStorCode = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		Connection conn2 = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String custRecieptNo ="";
		String custHp = "";
		try {
			conn2 = mysql.getConn();
			pst = conn2.prepareStatement("select c_custreceiptnoori "
				+ " from p_cases "
				+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch=? and cc_tobranch = ? ) "
				+ " where (q_branch=? and q_stage= 'BRANCHES' and q_step='RTN_INSTORE_WAITLIAISON' and q_status ='ACTV')");
			pst.setInt(1,branchId);
			pst.setInt(2, userStorCode);
			pst.setInt(3, userStorCode);
			
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getString("c_custreceiptnoori").equalsIgnoreCase(custRecieptNo))
					repeatedNo.put(custRecieptNo, custRecieptNo);
				
				custRecieptNo = rs.getString("c_custreceiptnoori");
			}
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
			pst = conn2.prepareStatement("select c_rcv_hp1 "
					+ " from p_cases "
					+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch=? and cc_tobranch = ?) "
					+ " where (q_branch=? and q_stage= 'BRANCHES' and q_step='RTN_INSTORE_WAITLIAISON' and q_status ='ACTV') "
					+ "  order by c_rcv_hp1");
				pst.setInt(1, branchId);
				pst.setInt(2, userStorCode);
				pst.setInt(3, userStorCode);
				
				rs = pst.executeQuery();
				while (rs.next()) {
					if (rs.getString("c_rcv_hp1").equalsIgnoreCase(custHp))
						repeatedHp.put(custHp, custHp);
					
					custHp = rs.getString("c_rcv_hp1");
				}
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}			
				String sql = "select c_custreceiptnoori "
						+ " from p_cases where  c_id in (";
						boolean first = true;
						
						for (String caseid : casesToPayList) {
							if (!first) {
								sql += ",";
							}
							first = false;
							sql +="?";
						}
						sql +=")    group by c_custreceiptnoori ";
			if (checkBoxPayment) {
				pst = conn2.prepareStatement(sql);
				int i =1, totCases = 0;
				for (String caseid : casesToPayList) {
					pst.setString(i, caseid);
					i++;
					totCases++;
				}
				rs = pst.executeQuery();
				String [] casesArray = new String [casesToPayList.size()];
				 i =0;
				
				while (rs.next()) {
					casesArray[i] = rs.getString("c_custreceiptnoori");
					
					i++;
				}
				userDefinedNewColsDefualtValues.put("selectedcaseshidden", new String[] {selectedCasesForPayment});
				userDefinedNewColsDefualtValues.put("selectedcases",  new String [] {casesToPayList.size()+""} );
			}else {
				userDefinedNewCols.remove("selectedcases");
				userDefinedNewCols.remove("selectedcaseshidden");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
			try {conn2.close();} catch (Exception e) {}
			
		}
		super.initialize(smartyStateMap);	
	}
	
	public String displayCheckBox (HashMap<String, String> hashy) {
		setRecords(getRecords() + 1);
		String s = "<td><input type=\"checkbox\" "
				+ " id=\"pmtcheck_"+hashy.get("c_id")+"\" onclick=\"checkBoxPmtClicked(this, "+hashy.get("c_id")+")\">";
		s +="</td>";
		return s;	
	}
	
	public String modifyStatus (HashMap<String,String> hashy) {
		String msg  ="";
		String html = "<td>";
		if (hashy.get("q_stage").equalsIgnoreCase("BRANCHES") || hashy.get("q_step").equalsIgnoreCase("RTN_INSTORE_WAITLIAISON")) {
			noOfRtnitems++;
		}else if (hashy.get("q_stage").equalsIgnoreCase("DLV") ) {
			if (hashy.get("q_step").equalsIgnoreCase("PART_SUCC")) {
				msg ="تم التسليم - راجع جزئي";
				noOfRtnitems++;
			}else {
				errorFlag = true;
				msg = "خطأ "+hashy.get("q_step") ;
			}
		}else {
			errorFlag = true;
			html += "خطأ في النظام"+", "+ hashy.get("q_step");
			System.out.println("خطأ في النظام AgentToBeReturnedShipments ,modifyStatus method stage=>"+hashy.get("q_stage")+", step=>"+hashy.get("q_step"));
		}
		html +=msg;
		html+= "</td>";
		
		return html;
		
	}
	public String modifyReceiptNo(HashMap<String, String> hashy) {
		String s = "";
		String style= "";
		if (repeatedNo.containsKey(hashy.get("c_custreceiptnoori")))
			style = "background-color:red";
		s = "<td style='"+style+"' caseid='"+hashy.get("c_id")+"' id='"+hashy.get("c_custreceiptnoori")+"'>"+hashy.get("c_custreceiptnoori");
		s +="</td>";
		
		return s;	
	}

	public String modifyRcvNo(HashMap<String, String> hashy) {
		String s = "";
		String style= "";
		
		if (repeatedHp.containsKey(hashy.get("c_rcv_hp1")))
			style = "background-color:orange";
		
		s +="<td style='"+style+"'>";
		s +=hashy.get("c_rcv_hp1");
		s +="</td>";
		return s;	
	}
	

	public String myFooterFunction(String colName) {
		if (colName.equalsIgnoreCase("c_custreceiptnoori") ) {
			if (!errorFlag) {
				return "<td colspan='3' stye='font-size: 15px;' align='center'>"
						+ "<form action=\"?\" id='liaison-agent-prepare-return-form' method=\"post\" style=\"display: inline;\"><input type=\"hidden\" name=\"op\" value=\"new\"><input type=\"hidden\""
						+ " name=\"myClassBean\" value=\"com.app.returnables.BranchReturn\">"
						+ "<input type=\"hidden\" name=\"selected_casesto_pay\" value='' id='selected_casesto_pay'>"
						+ "<button type=\"submit\" class=\"btn btn-dark btn-sm\" name=\"smarty_newformbtn\" id=\"smarty_newformbtn\" value=\"newform\">تجهيز الراجع لمندوب الارتباط</button></form></td>";
			}else {
				return "<td colspan='3' stye='font-size: 15px;' align='center'>هنالك خطأ في النظام , لايمكن اجراء التجهيز الأن</td>";
			}
		}else if (colName.equalsIgnoreCase("status") || colName.equalsIgnoreCase("c_rmk")) {
			return "";
		}else
			return "<td></td>";
	}
	
	public double getTotReceiptAmt() {
		return totReceiptAmt;
	}
	public void setTotReceiptAmt(double totReceiptAmt) {
		this.totReceiptAmt = totReceiptAmt;
	}
	public int getNoOfRtnitems() {
		return noOfRtnitems;
	}
	public void setNoOfRtnitems(int noOfRtnitems) {
		this.noOfRtnitems = noOfRtnitems;
	}
	public boolean isErrorFlag() {
		return errorFlag;
	}
	public void setErrorFlag(boolean errorFlag) {
		this.errorFlag = errorFlag;
	}
	public int getRtnItemsPaidByCustomer() {
		return rtnItemsPaidByCustomer;
	}
	public void setRtnItemsPaidByCustomer(int rtnItemsPaidByCustomer) {
		this.rtnItemsPaidByCustomer = rtnItemsPaidByCustomer;
	}
	public int getRtnItemsPaidBySender() {
		return rtnItemsPaidBySender;
	}
	public void setRtnItemsPaidBySender(int rtnItemsPaidBySender) {
		this.rtnItemsPaidBySender = rtnItemsPaidBySender;
	}
	public int getDlvItmes() {
		return dlvItmes;
	}
	public void setDlvItmes(int dlvItmes) {
		this.dlvItmes = dlvItmes;
	}
	public int getRuralAreaItems() {
		return ruralAreaItems;
	}
	public void setRuralAreaItems(int ruralAreaItems) {
		this.ruralAreaItems = ruralAreaItems;
	}
	/* (non-Javadoc)
	 * @see com.app.core.CoreMgr#doInsert(javax.servlet.http.HttpServletRequest, boolean)
	 */
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autoCommit) {
		String statusMsg = " تم تسجيل الراجع ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		Utilities ut = new Utilities();
		int userId = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		int branchId = Integer.parseInt(replaceVarsinString("{branchAccountReturnProcess}", arrayGlobals).trim());
		int userStorCode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		FlowUtils fu = new FlowUtils();
		try {
			inputMap_ori = filterRequest(rqs);
			
			String rmk = inputMap_ori.get("rtnrmk")[0];
			String pmtDate = inputMap_ori.get("pmtdate")[0];
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			int rtnManifestId=0;

			java.util.Date javaDate = formatter.parse(pmtDate);
			Date date = new Date(javaDate.getTime());
			//get list of cids
			ArrayList<String> cidList ;
			if (inputMap_ori.containsKey("selectedcaseshidden") 
					&& inputMap_ori.get("selectedcaseshidden")[0]!=null
					&& !inputMap_ori.get("selectedcaseshidden")[0].equalsIgnoreCase("")) {
				cidList = Utilities.SplitStringToArrayList(inputMap_ori.get("selectedcaseshidden")[0] , ",");
				
			}else {
				 String fromDate = null;// inputMap_ori.get("fromdate")[0];
			   	 String toDate = null;//inputMap_ori.get("todate")[0];
				 cidList = getCid(conn, branchId, userStorCode, fromDate, toDate );
			}
			if (!cidList.isEmpty()) {//make sure u have cases
				//1- check rtnManifestId if found in RTN_MANIFEST_LIAISON and we have one or more than one
				ps = conn.prepareStatement("select ifnull(cc_rtnmanifestid,0) as rtnmanifestid  from p_cases"
						+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch = ? and cc_tobranch=? ) "
						+ " where (q_branch=? and q_stage= 'BRANCHES' and q_step='RTN_MANIFEST_LIAISON' and q_status ='ACTV') by cc_rtnmanifestid ");
				ps.setInt(1, branchId);
				ps.setInt(2, userStorCode);
				ps.setInt(3, userStorCode);
				rs = ps.executeQuery();
				int ctr = 0;
				ArrayList<Integer> rtnManifestidList = new ArrayList<Integer>() ;
				while(rs.next()) {
					ctr += 1;
					rtnManifestId = rs.getInt("rtnmanifestid");
					rtnManifestidList.add(rs.getInt("rtnmanifestid"));
				}
				try {rs.close();} catch (Exception e) {}
				try {ps.close();} catch (Exception e) {}
				
				//2- if we have more than one rtnManifestId or we have not
				if(ctr>1 || ctr == 0) {
					ps = conn.prepareStatement( "insert into p_rtnliaisonagent_manifest "
					+ " (rlam_agentid , rlam_date		, rlam_pathid	, rlam_frombranch, rlam_tobranch,"
					+ " rlam_createddt, rlam_createdby  , rlam_rmk		, rlam_noofshipments)"
					+ " values	(?		      , ?			    , ?		        , ?				 , ?			,"
				   + " now(), ?		    	, ?				, ?		)", Statement.RETURN_GENERATED_KEYS);
					
					ps.setString(1, inputMap_ori.get("cc_liaisonagentid")[0]);
					ps.setDate(2, (Date) date);
					ps.setString(3, inputMap_ori.get("cc_pathid")[0]);
					ps.setInt(4, userStorCode);
					ps.setInt(5, branchId);
					ps.setInt(6, userId);
					ps.setString(7, rmk);
					ps.setString(8, inputMap_ori.get("selectedcases")[0]);
					ps.executeUpdate();
					rs = ps.getGeneratedKeys();
					rs.next();
					rtnManifestId = rs.getInt(1);
					try {rs.close();} catch (Exception e) {}
					try {ps.close();} catch (Exception e) {}
						
				}
				
				if(ctr>1) {
					ps = conn.prepareStatement("update p_caseschain "
							+ " join p_cases on (c_id = cc_caseid and cc_frombranch = ? and cc_tobranch=? ) "
							+ " set cc_rtnmanifestId = ?"
							+ " where (q_branch=? and q_stage= 'BRANCHES' and q_step='RTN_MANIFEST_LIAISON' and q_status ='ACTV')");
					ps.setInt(1, branchId);
					ps.setInt(2, userStorCode);
					ps.setInt(3, rtnManifestId);
					ps.setInt(4, userStorCode);
					ps.executeUpdate();
					try {ps.close();} catch (Exception e) {}
					
					ps = conn.prepareStatement("update p_rtnliaisonagent_manifest set rlam_deleted = 'Y' where rlam_id = ?");
					for (int i =0; i<rtnManifestidList.size(); i++) {
						ps.setInt(1, rtnManifestidList.get(i));
						ps.executeUpdate();
						ps.clearParameters();
					}
					try {ps.close();} catch (Exception e) {}
				}
				if(rtnManifestId>0) {
					CaseInformation caseInformation = new CaseInformation();
					ps = conn.prepareStatement("update p_caseschain "
							+ " join p_cases on (c_id = cc_caseid and cc_frombranch=? and cc_tobranch = ? ) "
							+ " set cc_rtnmanifestId=?"
							+ " where c_id=? and (q_branch=? and q_stage= 'BRANCHES' and q_step='RTN_INSTORE_WAITLIAISON' and q_status ='ACTV') "
							+ " ");
					boolean evryThinkIsOk = true;
					int caseId = 0;
					for (int i =0; i<cidList.size(); i++) {
						caseInformation = ut.getSinglCaseInformationFromBranch(conn, Integer.parseInt(cidList.get(i)), branchId);
						ps.setInt(1, branchId);
						ps.setInt(2, userStorCode);
						ps.setInt(3, rtnManifestId);
						ps.setString(4, cidList.get(i));
						ps.setInt(5, userStorCode);
						ps.executeUpdate();
						ps.clearParameters();
						
						if (caseInformation.getStepCode().equalsIgnoreCase("PART_SUCC") && caseInformation.getStageCode().equalsIgnoreCase("DLV")) {
							updateStageStepInOneChain(conn, caseInformation.getCurrentChainId(), userId);
						}else if (caseInformation.getStepCode().equalsIgnoreCase("RTN_INSTORE_WAITLIAISON") && caseInformation.getStageCode().equalsIgnoreCase("BRANCHES")) {
							fu.MoveDecisionStepNext(conn , Integer.parseInt(cidList.get(i)), "RTN_READY_LIAISON", userId , 
									userStorCode, "BRANCHES", "RTN_INSTORE_WAITLIAISON", "");
						}else {
							caseId = Integer.parseInt(cidList.get(i));
							evryThinkIsOk = false;
							break;
						}
						
					}
					if(evryThinkIsOk) {
						try {rs.close();} catch (Exception e) {}
						try {ps.close();} catch (Exception e) {}
						int totCases = 0;
						ps = conn.prepareStatement("select count(*)  from p_caseschain where cc_rtnmanifestid = ?");
						ps.setInt(1, rtnManifestId);
						rs = ps.executeQuery();
						if(rs.next()) {
							totCases = rs.getInt(1);
						}
						try {rs.close();} catch (Exception e) {}
						try {ps.close();} catch (Exception e) {}
						if(totCases>0 && totCases != Integer.parseInt(inputMap_ori.get("selectedcases")[0])) {
							ps = conn.prepareStatement("update p_rtnliaisonagent_manifest set rlam_noofshipments = ? where rlam_id = ?");
							ps.setInt(1, totCases);
							ps.setInt(2, rtnManifestId);
							ps.executeUpdate();
							
						}
					}else if(!evryThinkIsOk) {
						try {ps.close();} catch (Exception e) {}
						throw new Exception("BranchReturn, CaseId  = "+caseId+" in wrong step 'RTN_INSTORE_WAITLIAISON' please call Mr.nafi ");
					}
				}
			}else {
				throw new Exception("Return manifest ID  = "+rtnManifestId+"  call Mr.nafi ");
			}
			conn.commit();
		} catch (Exception e) {
			statusMsg = "Error at payment creation, error (" + e.getMessage()+ ")";
			setInsertErrorFlag(true);
			try {conn.rollback();} catch (Exception ignoreE) {}
			e.printStackTrace();
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {ps.close();} catch (Exception e) {}
		}

		return statusMsg;
	}
	
	public void updateStageStepInOneChain(Connection conn, int chainId, int actionTakenBy) throws Exception{
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("update p_caseschain "
					+ " set cc_qaction_tobranch=? , cc_qactiontakenby_tobranch=?, cc_qstep_tobranch=?, cc_qenterdate_tobranch= now(), cc_rtnmanifestliaison_actiontakenby=? where cc_id=? ");
			pst.setString(1, "RTN_READY_LIAISON");
			pst.setInt(2, actionTakenBy);
			pst.setString(3, "RTN_MANIFEST_LIAISON");
			pst.setInt(4, actionTakenBy);
			pst.setInt(5, chainId);
			int check = pst.executeUpdate();
			if (check == 0)
				throw new Exception("BranchReturn, update not work ");
			
		}catch(Exception e) {
			throw e;
		}finally {
			try{pst.close();}catch(Exception eRoll){}
		}
	}
	
	
	private ArrayList<String> getCid(Connection conn, int branchId, int userStorCode ,String fromDate, String toDate) throws Exception {
		ArrayList<String> cases = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean dateIsNotNull = false;
		try {
			if (fromDate !=null && !fromDate.trim().equalsIgnoreCase("")
					 && toDate !=null && !toDate.trim().equalsIgnoreCase("")) {
				dateIsNotNull = true;
			}
			String querycall = "select c_id From p_cases "
					+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch=? and cc_tobranch = ? ) "
					+ " where (q_branch=? and q_stage= 'BRANCHES' and q_step='RTN_INSTORE_WAITLIAISON' and q_status ='ACTV')";
			if(dateIsNotNull)
				querycall += " and  (c_createddt >=? and c_createddt<adddate(date(?),1)) ";
				ps = conn.prepareStatement(querycall); // create a statement
				ps.setInt(1, branchId);
				ps.setInt(2, userStorCode); 
				ps.setInt(3, userStorCode); 
				if(dateIsNotNull) {
					ps.setString(3,fromDate);
					ps.setString(4,toDate);
				}
				rs = ps.executeQuery();
				while (rs.next()) {
					cases.add(rs.getString("c_id"));
				}
		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {/* ignore */}
			try {ps.close();} catch (Exception e) {/* ignore */
			}
		}
		return cases;
	}// end of get_cid


	public int getRecords() {
		return records;
	}


	public void setRecords(int records) {
		this.records = records;
	}
	public double getTotDueAmt() {
		return totDueAmt;
	}
	public void setTotDueAmt(double totDueAmt) {
		this.totDueAmt = totDueAmt;
	}
	
}
