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

import com.app.bussframework.FlowUtils;
import com.app.cases.CaseInformation;
import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;

public class AgentToBeReturnedShipments extends CoreMgr {
	private LinkedHashMap <String,String> repeatedNo = new LinkedHashMap<String,String>();
	private LinkedHashMap <String,String> repeatedHp = new LinkedHashMap<String,String>();
	private int records = 0;
	private double totDueAmt = 0;
	private double totReceiptAmt = 0;
	private int noOfRtnitems = 0;
	boolean errorFlag = false;
	private String partialReturnTdBackgroundColor = "background-color:pink";
	
	private int rtnItemsPaidByCustomer = 0;
	private int rtnItemsPaidBySender = 0;
	private int dlvItmes =0;
	private int ruralAreaItems=0;
	public AgentToBeReturnedShipments () {
		records = 0;
		MainSql = "select c_parentid, c_branchcode, c_rmk , '' as pmtCheckBox, '' as selectedcases,'' as selectedcaseshidden, cust_name, q_stage, q_step,"
				+ " '' as status , 'شحنات سلمت وراجعه ' as title, c_custid,c_custreceiptnoori,'' as pmtrmk, c_rtnreason, "
				+ " date(c_createddt) as c_createddt, concat(st_name_ar,' - ',ifnull(c_rcv_addr_rmk,'')) as addr, c_id,"
				+ " c_rcv_name , c_rcv_hp1, c_assignedagent,  '' as fromdate, '' as todate, (case when q_stage='DLV' then c_partial_qtyrtn else c_qty end) as qty, "
				+ " c_changedprice, c_priceb4change, c_receiptamt, c_receiptamt_usd, c_rural"
				+ " from p_cases "
				+ " left join kbcustomers on cust_id = c_custid "
				+ " left join kbstate on st_code = c_rcv_state and st_branch={userstorecode}"
				+ " where c_assignedagent ={agentAccountReturnProcess} "
				+ " and c_allowrtnagent='Y' and c_agentrtnid=0 ";
		
		userDefinedGroupColsOrderBy = "c_createddt,c_id";
		UserDefinedPageRows = 50000;
		userDefined_x_panelclass = "account_x_panel";
		userDefinedGroupByCol = "c_createddt";
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("c_branchcode");
		userDefinedGridCols.add("c_id");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("qty");
		userDefinedGridCols.add("status");
		userDefinedGridCols.add("c_rtnreason");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("pmtCheckBox");
		
		userDefinedColLabel.put("c_rtnreason", "سبب الراجع");
		userDefinedColLabel.put("c_receiptamt", "مبلغ وصل د.ع");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ وصل دولار");
		userDefinedColLabel.put("c_branchcode", "من فرع");
		userDefinedColLabel.put("c_id", "كود الشحنه");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("c_createddt", "تاريخ الإدخال");
		userDefinedColLabel.put("c_rcv_name", "إسم المستلم");
		userDefinedColLabel.put("addr", "العنوان");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("selectedcases", "عدد الوصولات");
		userDefinedColLabel.put("pmtCheckBox", "استلام");		
		userDefinedColLabel.put("c_assignedagent", "مندوب التوصيل");
		userDefinedColLabel.put("pmtdate", "تاريخ الإستلام");
		userDefinedColLabel.put("pmtrmk", "ملاحظات");
		userDefinedColLabel.put("status", "الحاله");
		userDefinedColLabel.put("fromdate","من تاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		userDefinedColLabel.put("cust_name", "المتجر");
		userDefinedColLabel.put("qty", "القطع الراجعة");
		
		userDefinedPageFooterFunction = "myFooterFunction()";
		canNew = true;
		mainTable = "p_cases";
		keyCol = "c_id";
		
		userDefinedNewCols.add("selectedcases");
		//userDefinedNewCols.add("pmtdate");
		//userDefinedNewColsDefualtValues.put("pmtdate", new String[] {"%select date(now()+INTERVAL 9 HOUR)"});
		userDefinedReadOnlyNewCols.add("pmtdate");
		userDefinedNewCols.add("pmtrmk");
		userDefinedNewCols.add("selectedcaseshidden");
		userDefinedHiddenNewCols.add("selectedcaseshidden");
		
		userModifyTD.put("c_custreceiptnoori", "modifyReceiptNo({c_custreceiptnoori},{c_id})");
		userModifyTD.put("c_rcv_hp1", "modifyRcvNo({c_rcv_hp1})");
		userModifyTD.put("status", "modifyStatus({c_changedprice},{c_priceb4change},{c_parentid}, {c_receiptamt},{q_stage}, {q_step},{c_rural}, {q_stage},{q_step})");
		
		userDefinedColsMustFill.add("c_custid");
		
		userDefinedNewColsHtmlType.put("pmtrmk", "TEXTAREA");
		userDefinedReadOnlyNewCols.add("c_assignedagent");
		userDefinedNewColsHtmlType.put("selectedcases", "TEXT");
		userDefinedReadOnlyNewCols.add("selectedcases");
		userDefinedNewCaption = "استلام الرواجع من المندوب";
		userDefinedCaption= userDefinedNewCaption;
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		userDefinedColsTypes.put("c_rcv_hp1", "VARCHAR");//to remove the comma
		//myhtmlmgr.tableClass = "table table-striped  table-bordered turquoise_table";
		userModifyTD.put("pmtCheckBox", "displayCheckBox({c_id})");
		
		userDefinedLookups.put("c_rtnreason", "select rtn_code, rtn_desc from kbrtn_reasons");
		userDefinedLookups.put("c_branchcode", "select branch_id, branch_name from kbbranches");
	}
	
	@Override
	public void initialize(HashMap smartyStateMap){
		
		boolean checkBoxPayment = false;
		String selectedCasesForPayment = "";
		Map<String, String[]> parameters = httpSRequest.getParameterMap();
		ArrayList<String> casesToPayList = new ArrayList<String>();
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
		int agentId = Integer.parseInt(replaceVarsinString(" {agentAccountReturnProcess} ", arrayGlobals).trim());
		int userStorCode = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		Connection conn2 = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String custRecieptNo ="";
		String custHp = "";
		userDefinedCaption= "<div class=\"row\" style='margin-bottom: 15px;'>"
				+ "				<div class=\"col-sm-1 col-sm-offset-1\">"
				+ "					<label>Barcode</label>"
				+ "				</div>"
				+ "				<div class=\"col-sm-4\">"
				+ "					<input type='text' style='color:#424242;background-color:#E9E5E5;' id ='barcode_checker' />"
				+ "				</div>"
				+ " 			<div class=' col-sm-6'>" + 
				"                        <input style='float: left; margin-top: 5px; margin-right: 5px;' "
				+ " id=\"allprepair\" class=\"\" onclick='changeToPrepairAll();' type=\"checkbox\">" + 
				"               </div>"
				+ "			</div>";
		try {
			conn2 = mysql.getConn();
			pst = conn2.prepareStatement("select c_custreceiptnoori "
				+ " from p_cases "
				+ " where c_assignedagent =? "
				+ "  and c_allowrtnagent='Y' and c_agentrtnid=0 "
				+ "  order by c_custreceiptnoori");
			pst.setInt(1,agentId);
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
					+ " where c_agentrtnid=0 and c_assignedagent =? "
					+ " and c_allowrtnagent='Y' "
					+ "  order by c_rcv_hp1");
				pst.setInt(1,agentId);
				rs = pst.executeQuery();
				while (rs.next()) {
					if (rs.getString("c_rcv_hp1").equalsIgnoreCase(custHp))
						repeatedHp.put(custHp, custHp);
					
					custHp = rs.getString("c_rcv_hp1");
				}
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}			
				String sql = "select c_custreceiptnoori "
						+ " from p_cases "
						+ " where c_agentrtnid=0 and c_assignedagent=? and c_allowrtnagent='Y' and  c_id in (";
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
				int i =2, totCases = 0;
				pst.setInt(1, agentId);
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
				userDefinedNewColsDefualtValues.put("selectedcases",casesArray );
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
		String s = "<td><input type=\"checkbox\"  "
				+ " id=\"pmtcheck_"+hashy.get("c_id")+"\" >";
		s +="</td>";
		return s;	
	}
	
	public String modifyStatus (HashMap<String,String> hashy) {
		String msg  ="";
		String style= "";
		if (hashy.get("q_stage").equalsIgnoreCase("AGENTOP") ) {
			if (hashy.get("q_step").equalsIgnoreCase("RTN_WITHAGENT")) {
				noOfRtnitems++;
				msg = "راجع";
				if (!hashy.get("c_parentid").equalsIgnoreCase("0")) {
					msg ="تم التسليم - راجع جزئي";
					style= partialReturnTdBackgroundColor;	
				}
			}else {
				errorFlag = true;
				msg = "خطأ "+hashy.get("q_step") ;
			}
		}else {
			errorFlag = true;
			msg += "خطأ في النظام"+", "+ hashy.get("q_step");
			System.out.println("خطأ في النظام AgentToBeReturnedShipments ,modifyStatus method stage=>"+hashy.get("q_stage")+", step=>"+hashy.get("q_step"));
		}
		String html = "<td style='"+style+"'>";
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
						+ "<form action=\"?\" id='agent-prepare-return-form' method=\"post\" style=\"display: inline;\" ><input type=\"hidden\" name=\"op\" value=\"new\"><input type=\"hidden\""
						+ " name=\"myClassBean\" value=\"com.app.returnables.AgentToBeReturnedShipments\">"
						+ "<input type=\"hidden\" name=\"selected_casesto_pay\" value='' id='selected_casesto_pay'>"
						+ "<button type=\"submit\" class=\"btn btn-dark btn-sm\" name=\"smarty_newformbtn\" value=\"newform\">إستلام الراجع</button></form></td>";
			}else {
				return "<td colspan='3' stye='font-size: 15px;' align='center'>هنالك خطأ في النظام , لايمكن اجراء الاستلام الأن</td>";
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
		int agentId = Integer.parseInt(replaceVarsinString("{agentAccountReturnProcess}", arrayGlobals).trim());
		int userStorCode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		FlowUtils fu = new FlowUtils();
		try {
			inputMap_ori = filterRequest(rqs);
			String rmk = inputMap_ori.get("pmtrmk")[0];
			int pmtId=0;

			//get list of cids
			ArrayList<String> cidList ;
			if (inputMap_ori.containsKey("selectedcaseshidden") 
					&& inputMap_ori.get("selectedcaseshidden")[0]!=null
					&& !inputMap_ori.get("selectedcaseshidden")[0].equalsIgnoreCase("")) {
				cidList = Utilities.SplitStringToArrayList(inputMap_ori.get("selectedcaseshidden")[0] , ",");
				
			}else {
				 String fromDate = null;// inputMap_ori.get("fromdate")[0];
			   	 String toDate = null;//inputMap_ori.get("todate")[0];
				 cidList = getCid(conn, agentId, userStorCode, fromDate, toDate );
			}
			if (!cidList.isEmpty()) {//make sure u have cases
				ps = conn.prepareStatement( "insert into p_agent_returns "
						+ " 		(apr_agentid, apr_createddt		, apr_createdby, apr_rmk)"
						+ " values	(?		    , now(), ?		      , ?	   )", Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, agentId);
				ps.setInt(2, userId);
				ps.setString(3, rmk);
				ps.executeUpdate();
				rs = ps.getGeneratedKeys();
				rs.next();
				pmtId = rs.getInt(1);
				try {rs.close();} catch (Exception e) {}
				try {ps.close();} catch (Exception e) {}
				
				CaseInformation caseInformation = new CaseInformation();
				ps = conn.prepareStatement("update p_cases set "
						+ " c_agentrtnid=?  where c_id=? and c_allowrtnagent='Y' and c_agentrtnid=0  ");
				for (int i =0; i<cidList.size(); i++) {
					caseInformation = ut.getSingleReceiptInfoInQueue(conn, cidList.get(i));
					ps.setInt(1, pmtId);
					ps.setString(2, cidList.get(i));
					ps.executeUpdate();
					ps.clearParameters();
					
					if (caseInformation.getStageCode().equalsIgnoreCase("AGENTOP")
							 && caseInformation.getStepCode().equalsIgnoreCase("RTN_WITHAGENT")) {
						fu.MoveDecisionStepNext(conn, Integer.parseInt(cidList.get(i)), "RTN_FROMAGENTTOSTORE", userId, "AGENTOP", "RTN_WITHAGENT", "");
					}
					
				}
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
	
	private ArrayList<String> getCid(Connection conn, int agentId, int branchCode ,String fromDate, String toDate) throws Exception {
		ArrayList<String> cases = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean dateIsNotNull = false;
		try {
			if (fromDate !=null && !fromDate.trim().equalsIgnoreCase("")
					 && toDate !=null && !toDate.trim().equalsIgnoreCase("")) {
				dateIsNotNull = true;
			}
			String querycall = "select c_id From p_cases where c_assignedagent =?  "
					+ " and c_allowrtnagent='Y' and c_agentrtnid= 0 ";
			if(dateIsNotNull)
				querycall += " and  (c_createddt >=? and c_createddt<adddate(date(?),1)) ";
				ps = conn.prepareStatement(querycall); // create a statement
				ps.setInt(1, agentId); 
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
