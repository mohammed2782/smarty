package com.app.incomeoutcome;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;
import com.app.db.mysql;
import com.app.util.Utilities;
import com.mysql.jdbc.Statement;

public class AgentsBalance extends CoreMgr {
	private int records = 0;
	private double totDueAmt = 0;
	private double totReceiptAmt = 0;
	private int noOfRtnitems = 0;
	boolean errorFlag = false;
	
	private int rtnItemsPaidByCustomer = 0;
	private int rtnItemsPaidBySender = 0;
	private int dlvItmes =0;
	private int ruralAreaItems=0;
	public AgentsBalance () {
		records = 0;
		MainSql = "select '' nettobecollectedfromagent , '' as pmtCheckBox, '' as selectedcases,'' as selectedcaseshidden, c_shipmentpaidbysender,c_rural, c_shipmentpaidbycustomer, c_name, q_stage, q_step,'' as status , 'شحنات سلمت وراجعه ' as title, c_custid,c_custreceiptnoori,'' as totamt,'' as pmtrmk, '' as pmtdate, "
				+ " date(c_createddt) as c_createddt, c_weight, concat(st_name_ar,' - ',ifnull(c_rcv_addr_rmk,'')) as addr, p_cases.c_id as c_id,"
				+ " c_rcv_name , c_rcv_hp,(case when (q_stage='dlv_stg' and q_step='delivered') then c_receiptamt else 0 end)  c_receiptamt, "
				+ " (case when ((q_stage='dlv_stg' and q_step='delivered') "
				+ "				or  (q_stage ='cncl' and c_shipmentpaidbycustomer='Y') "
				+ "				or  (q_stage ='cncl' and c_shipmentpaidbysender='Y' )) then c_agentshare else 0 end) as c_agentshare,"
				+ " (case when (q_stage='dlv_stg' and q_step='delivered' ) then c_shipment_cost  "
				+"        when (q_stage='cncl' and c_shipmentpaidbysender='Y' and c_shipmentpaidbycustomer='N' ) then c_shipment_cost  "
				+ "		  when (q_stage ='cncl' and c_shipmentpaidbycustomer ='Y' and c_shipmentpaidbysender='N' and c_rural='Y') then st_ruralcharges "
				+ "       when (q_stage ='cncl' and c_shipmentpaidbycustomer ='Y' and c_shipmentpaidbysender='N' and c_rural='N') then st_charges "
				+ "			 else '-' end )as c_shipment_cost, c_assignedagent,  '' as fromdate, '' as todate,"
				+ " st_charges , st_ruralcharges "
				+ " from p_cases "
				+ " join p_queue on (c_id= q_caseid and q_status !='CLS')"
				+ " left join kbcustomers on kbcustomers.c_id = c_custid "
				+ " left join kbstate on st_code = c_rcv_state"
				+ " where c_assignedagent ={agentAcct} and c_agentsharesettled !='FULL' and c_receiptfromsystem = 'N'  "
				+ " and ( (q_stage='cncl')   or (q_stage='dlv_stg' and q_step='delivered') "
				+ " or (c_shipmentpaidbycustomer='Y' and q_stage='cncl') )"
				+ " and (c_branchcode='{userstorecode}' or '{superRank}'='Y') ";
		
		
		
		userDefinedSumCols.add("c_agentshare");
		userDefinedSumCols.add("c_receiptamt");
		userDefinedGroupColsOrderBy = "c_id";
		UserDefinedPageRows = 50000;
		groupSumCaption = "المجموع";
		userDefined_x_panelclass = "account_x_panel";
		userDefinedGroupByCol = "title";
		userDefinedGroupByCol = "c_createddt";
		userDefinedGridCols.add("c_name");
		userDefinedGridCols.add("c_id");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_rcv_hp");
		//userDefinedGridCols.add("c_weight");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("c_agentshare");
		userDefinedGridCols.add("status");
		userDefinedGridCols.add("pmtCheckBox");
		
		userDefinedColLabel.put("c_id", "رقم الشحنه");
		userDefinedColLabel.put("c_createddt", "تاريخ الإدخال");
		userDefinedColLabel.put("c_rcv_name", "إسم المستلم");
		userDefinedColLabel.put("c_weight", "الوزن");
		userDefinedColLabel.put("addr", "العنوان");
		userDefinedColLabel.put("c_rcv_hp", "هاتف");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("selectedcases", "أرقام الوصولات");
		userDefinedColLabel.put("pmtCheckBox", "دفع");
		userDefinedColLabel.put("nettobecollectedfromagent", "المبلغ المطلوب أستلامه من مندوب التوصيل");
		
		
		userDefinedColLabel.put("fromdate", "من تاريخ");
		userDefinedColLabel.put("todate", "الى تاريخ");
		
		userDefinedColLabel.put("c_shipment_cost", "تكلفة الشحن");
		userDefinedColLabel.put("c_assignedagent", "مندوب التوصيل");
		userDefinedColLabel.put("pmtdate", "تاريخ الدفع");
		userDefinedColLabel.put("pmtrmk", "ملاحظات");
		userDefinedColLabel.put("totamt", "المبلغ  المستحق");
		userDefinedColLabel.put("c_sendmoney", "مبلغ مرسل إلى المستلم");
		userDefinedColLabel.put("netamt", "الصافي لمندوب التوصيل");
		userDefinedColLabel.put("status", "الحاله");
		userDefinedColLabel.put("c_agentshare", "حصة مندوب التوصيل من مبلغ الشحن");
		userDefinedColLabel.put("c_name", "صاحب المحل");
		 
		userDefinedNewColsHtmlType.put("fromdate", "DATE");
		userDefinedNewColsHtmlType.put("todate", "DATE");
		userDefinedPageFooterFunction = "myFooterFunction()";
		canNew = true;
		mainTable = "p_cases";
		keyCol = "c_id";
		
		userDefinedNewCols.add("c_assignedagent");
		userDefinedNewCols.add("selectedcases");
		userDefinedNewCols.add("fromdate");
		userDefinedNewCols.add("todate");
		userDefinedNewCols.add("totamt");
		userDefinedNewCols.add("nettobecollectedfromagent");
		userDefinedNewCols.add("pmtdate");
		userDefinedNewCols.add("pmtrmk");
		userDefinedNewCols.add("selectedcaseshidden");
		userDefinedHiddenNewCols.add("selectedcaseshidden");
		
		userModifyTD.put("c_agentshare", "modifyAgentAmt({c_agentshare}, {q_stage}, {q_step}, {c_shipmentpaidbycustomer},{c_shipmentpaidbysender})");
		userModifyTD.put("status", "modifyStatus({q_stage}, {q_step}, {c_shipmentpaidbycustomer}, {c_shipmentpaidbysender},{c_rural})");
		userModifyTD.put("c_receiptamt", "modifyReceiptAmt({c_receiptamt}, {q_stage}, {q_step}, {c_shipmentpaidbycustomer},{c_shipment_cost},{c_shipmentpaidbysender})");
		
		userDefinedColsMustFill.add("c_custid");
		userDefinedColsMustFill.add("totamt");
		userDefinedColsMustFill.add("pmtdate");
		userDefinedNewColsDefualtValues.put("c_assignedagent", new String[] {"{agentAcct}"});
		userDefinedLookups.put("c_assignedagent", "select us_id, us_name from kbusers where us_id='{agentAcct}'");
		
		userDefinedNewColsDefualtValues.put("totamt", new String [] {"%select "
				+ "(sum(c_agentshare))  "
				+ " from p_cases "
				+ " join p_queue on (c_id = q_caseid and q_status !='CLS' and ( (q_stage='dlv_stg' and q_step='delivered')"
				+ "  or (c_shipmentpaidbycustomer='Y' and q_stage='cncl')   or (c_shipmentpaidbysender='Y' and q_stage='cncl')) ) "
				+ " where c_assignedagent='{agentAcct}' and  c_receiptfromsystem = 'N'  and c_agentsharesettled !='FULL' "});
		userDefinedNewColsHtmlType.put("totamt", "TEXT");
		userDefinedNewColsHtmlType.put("pmtdate", "DATE");
		userDefinedNewColsHtmlType.put("pmtrmk", "TEXTAREA");
		userDefinedReadOnlyNewCols.add("totamt");
		userDefinedReadOnlyNewCols.add("nettobecollectedfromagent");
		userDefinedReadOnlyNewCols.add("c_assignedagent");
		userDefinedNewColsHtmlType.put("selectedcases", "MULTILIST");
		userDefinedReadOnlyNewCols.add("selectedcases");
		newCaption = "دفع مستحقات وكيل توصيل";
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		myhtmlmgr.tableClass = "table table-striped  table-bordered orange_table";
		userModifyTD.put("pmtCheckBox", "displayCheckBox({c_id})");
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
		
		
		Connection conn2 = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn2 = mysql.getConn();
			// check if the user selected boxes for payment, if yes then get the boxes values
			
			String sql = "select c_custreceiptnoori, "
			+ "	 sum("
			+ "		case when (q_stage ='cncl' and c_shipmentpaidbycustomer ='N' and c_shipmentpaidbysender='N') then 0 else c_agentshare end) as totDue, "
			+ " sum("
			+ "    (case when (q_stage='dlv_stg' and q_step='delivered') then c_receiptamt else 0 end)"
			+ "     + "
			+ "     (case when (q_stage ='cncl' and c_shipmentpaidbycustomer ='Y' and c_shipmentpaidbysender='N' and c_rural='Y') then st_ruralcharges "
			+ "         when (q_stage ='cncl' and c_shipmentpaidbycustomer ='Y' and c_shipmentpaidbysender='N' and c_rural='N') then st_charges "
			+ "			 else '0' end )) as agentcollected "
			+ " from p_cases "
			+ " join p_queue on (c_id = q_caseid and q_status !='CLS')"
			+ " left join kbstate on st_code = c_rcv_state"
			+ " where  c_agentsharesettled !='FULL' and  c_id in (";
			boolean first = true;
			
			for (String caseid : casesToPayList) {
				if (!first) {
					sql += ",";
				}
				first = false;
				sql +="?";
			}
			sql +=")   and c_receiptfromsystem = 'N'  group by c_custreceiptnoori ";
			
			if (checkBoxPayment) {
				pst = conn2.prepareStatement(sql);
				int i =1;
				for (String caseid : casesToPayList) {
					pst.setString(i, caseid);
					i++;
				}
				rs = pst.executeQuery();
				double totDue =0;
				double collectedByAgent = 0;
				double netToBeCollectedFromAgent = 0;
				String [] casesArray = new String [casesToPayList.size()];
				 i =0;
				
				while (rs.next()) {
					totDue += rs.getDouble("totDue");
					collectedByAgent += rs.getDouble("agentcollected");
					casesArray[i] = rs.getString("c_custreceiptnoori");
					
					i++;
				}
				netToBeCollectedFromAgent = collectedByAgent - totDue;
				
				userDefinedNewColsDefualtValues.put("totamt", new String[] {Double.toString(totDue)});
				userDefinedNewColsDefualtValues.put("nettobecollectedfromagent", new String[] {Double.toString(netToBeCollectedFromAgent)});
				userDefinedNewColsDefualtValues.put("selectedcaseshidden", new String[] {selectedCasesForPayment});
				userDefinedNewColsDefualtValues.put("selectedcases",casesArray );
				
				userDefinedNewCols.remove("fromdate");
				userDefinedNewCols.remove("todate");
			}else {
				String agentId = replaceVarsinString(" {agentAcct} ", arrayGlobals).trim();
				userDefinedNewLookups.put("totamt", "!select "
						+ "(sum(c_agentshare))  "
						+ " from p_cases "
						+ " join p_queue on (c_id = q_caseid and q_status !='CLS' and ( (q_stage='dlv_stg' and q_step='delivered')"
						+ "  or (c_shipmentpaidbycustomer='Y' and q_stage='cncl')   or (c_shipmentpaidbysender='Y' and q_stage='cncl')) ) "
						+ " where c_assignedagent='"+agentId+"'  "
						+ " and  c_receiptfromsystem = 'N'  and c_agentsharesettled !='FULL'  and  (c_createddt >= '{fromdate}'  and c_createddt<=adddate(date('{todate}'),1)) ");
				
				
			
				
				userDefinedNewCols.remove("selectedcases");
				userDefinedNewCols.remove("selectedcaseshidden");
				userDefinedNewCols.remove("nettobecollectedfromagent");
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
		
		String s = "<td><input type=\"checkbox\" "
				+ " id=\"pmtcheck_"+hashy.get("c_id")+"\" onclick=\"checkBoxPmtClicked(this, "+hashy.get("c_id")+")\">";
		s +="</td>";
		return s;	
	}
	
	public String modifyStatus (HashMap<String,String> hashy) {
		String html = "<td>";
		
		if (hashy.get("c_rural").equalsIgnoreCase("Y"))
			ruralAreaItems++;
		if (hashy.get("q_stage").equalsIgnoreCase("cncl") ) {
			
			
			if (	hashy.get("c_shipmentpaidbycustomer").equalsIgnoreCase("Y") 
					&& (hashy.get("c_shipmentpaidbysender").equalsIgnoreCase("N"))) {
				html ="<td style='background-color:blue;color:white'> راجع مع دفع أحور النقل";
				rtnItemsPaidByCustomer++;
			}else if (hashy.get("c_shipmentpaidbycustomer").equalsIgnoreCase("N")
					&& (hashy.get("c_shipmentpaidbysender").equalsIgnoreCase("Y"))) {
				html +="راجع مع دفع أحور النقل من صاحب المحل";
				rtnItemsPaidBySender++;
			}else if (hashy.get("c_shipmentpaidbycustomer").equalsIgnoreCase("Y")
					&& (hashy.get("c_shipmentpaidbysender").equalsIgnoreCase("Y")) ) {
					html ="<td style ='background-color:red; color:white'>خطأ في النظام أتصل بسوفتيكا";
					errorFlag = true;
			}
			else 
				html +="مرتجع";
			noOfRtnitems++;
		}else if (hashy.get("q_stage").equalsIgnoreCase("dlv_stg") && hashy.get("q_step").equalsIgnoreCase("delivered") ) {
			html +="تم التسليم";
			dlvItmes ++;
			
		}else {
			errorFlag = true;
			html += "خطأ في النظام"+", "+ hashy.get("q_step");
			System.out.println("خطأ في النظام agent balance class,modifyStatus method stage=>"+hashy.get("q_stage")+", step=>"+hashy.get("q_step"));
		}
		html+= "</td>";
		
		return html;
		
	}
	public String modifyReceiptAmt (HashMap<String,String> hashy) {
		
		String html = "<td>";
		double goodsCost = Double.parseDouble(hashy.get("c_receiptamt"));
		if (hashy.get("q_stage").equalsIgnoreCase("cncl")) {
			goodsCost = 0;
			html += " - ";
			//if the shipment is cancelled and the amount of shipment is paid we need to add it to the amount should be received from the agent
			if (hashy.get("c_shipmentpaidbycustomer").equalsIgnoreCase("Y")) 
				totReceiptAmt += Double.parseDouble(hashy.get("c_shipment_cost"));
		}else {
			html += numFormat.format(goodsCost);
			
		}
		totReceiptAmt +=goodsCost;
		html+= "</td>";
		return html;
		
	}
	
	public String modifyAgentAmt (HashMap<String,String> hashy) {
		setRecords(getRecords() + 1);
		String html = "<td>";
		double dueAmt = Double.parseDouble(hashy.get("c_agentshare"));
		if (hashy.get("q_stage").equalsIgnoreCase("cncl")) {
			if (hashy.get("c_shipmentpaidbycustomer").equalsIgnoreCase("Y") ||
					hashy.get("c_shipmentpaidbysender").equalsIgnoreCase("Y")) {
				html += numFormat.format(dueAmt);
			}else {
				dueAmt = 0;
				html += " - ";
			}
				
			
		}else {
			html += numFormat.format(dueAmt);
		}
		totDueAmt +=dueAmt;
		html+= "</td>";
		return html;
		
	}
	
	
	
	public String myFooterFunction(String colName) {
		if (colName.equalsIgnoreCase("c_custreceiptnoori") ) {
			if (!errorFlag) {
				double amountobereceivedfromagent = totReceiptAmt - totDueAmt;
				if (amountobereceivedfromagent <0) amountobereceivedfromagent = 0;
				return "<td colspan='3' stye='font-size: 15px;' align='center'><table><tr><td><label>المبلغ الكلي للشحنات </label></td>"
						+ "<td> <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\"> "+numFormat.format(totReceiptAmt)+" </strong></td></tr>"
								+ "<tr><td><label>حصة مندوب التوصيل </label></td>"
								+ "<td> <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\"> "+numFormat.format(totDueAmt)+" </strong></td></tr>"
										+ "<tr><td><label>المبلغ المطلوب أستلامه من مندوب التوصيل </label></td>"
										+ "<td><strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\"> "+numFormat.format(amountobereceivedfromagent)+" </strong></td></tr>"
										+ "<tr><td><label>عدد الشحنات الراجعه </label></td>"
										+ "<td><strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\"> "+numFormat.format(noOfRtnitems)+" </strong></td></tr></table>"
						+ "<form action=\"?\" method=\"post\" style=\"display: inline;\" onsubmit=\"checkBoxPmtClicked()\"><input type=\"hidden\" name=\"op\" value=\"new\"><input type=\"hidden\""
						+ " name=\"myClassBean\" value=\"com.app.incomeoutcome.AgentsBalance\">"
						+ "<input type=\"hidden\" name=\"selected_casesto_pay\" value='' id='selected_casesto_pay'>"
						+ "<button type=\"submit\" class=\"btn btn-dark btn-sm\" name=\"smarty_newformbtn\" value=\"newform\">إدفع حصة الوكيل</button></form></td>";
			}else {
				return "<td colspan='3' stye='font-size: 15px;' align='center'>هنالك خطأ في النظام , لايمكن اجراء الدفع الأن</td>";
			}
		}else if(colName.equalsIgnoreCase("c_receiptamt") || colName.equalsIgnoreCase("c_shipment_cost"))
			return "";
		else
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
		String statusMsg = " تم تسجيل الدفعه ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String userId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		String agentId = replaceVarsinString(" {agentAcct} ", arrayGlobals).trim();
		try {
			inputMap_ori = filterRequest(rqs);
			String rmk = inputMap_ori.get("pmtrmk")[0];
			String pmtDate = inputMap_ori.get("pmtdate")[0];
			double paidAmt = Double.parseDouble(inputMap_ori.get("totamt")[0]);
			// do validation for paidAmt must match the calcualted paidAmt and custid must match the global
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			int pmtId=0;
		
			
			java.util.Date javaDate = formatter.parse(pmtDate);
			Date date = new Date(javaDate.getTime());
			//get list of cids
			ArrayList<String> cidList ;
			if (inputMap_ori.containsKey("selectedcaseshidden") 
					&& inputMap_ori.get("selectedcaseshidden")[0]!=null
					&& !inputMap_ori.get("selectedcaseshidden")[0].equalsIgnoreCase("")) {
				cidList = Utilities.SplitStringToArrayList(inputMap_ori.get("selectedcaseshidden")[0] , ",");
				
			}else {
				String fromDate = inputMap_ori.get("fromdate")[0];
				String toDate = inputMap_ori.get("todate")[0];
				cidList = getCid(agentId, conn , fromDate, toDate);
			}
			if (!cidList.isEmpty()) {//make sure u have cases
				ps = conn.prepareStatement( "INSERT INTO p_agent_payments "
						+ " 		(ap_agentid, ap_amount_paid, ap_paymentdt, ap_createdby, ap_rmk)"
						+ "VALUES	(?		   , ?			 , ?		   , ?			   , ?)", Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, agentId);
				ps.setDouble(2, paidAmt);
				ps.setDate(3, (Date) date);
				ps.setString(4, userId);
				ps.setString(5, rmk);
				ps.executeUpdate();
				rs = ps.getGeneratedKeys();
				rs.next();
				pmtId = rs.getInt(1);
				try {rs.close();} catch (Exception e) {}
				try {ps.close();} catch (Exception e) {}
				
				ps = conn.prepareStatement("update p_cases set c_agentpmtid=? , c_agentsharesettled='FULL' where c_id = ?");
				for (int i =0; i<cidList.size(); i++) {
					ps.setInt(1, pmtId);
					ps.setString(2, cidList.get(i));
					ps.executeUpdate();
					ps.clearParameters();
					//throw new Exception("chjec");
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
	
	private ArrayList<String> getCid(String custid, Connection conn , String fromDate, String toDate) throws Exception {
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
					+ " join p_queue on (c_id= q_caseid and q_status !='CLS')"
					+ " left join kbstate on st_code = c_rcv_state"
					+ " where c_assignedagent =? and c_agentsharesettled !='FULL' and c_receiptfromsystem = 'N' "
					+ " and ( (q_stage='cncl')  or (q_stage='dlv_stg' and q_step='delivered')  or (c_shipmentpaidbycustomer='Y' and q_stage='cncl') or (c_shipmentpaidbysender='Y' and q_stage='cncl') ) "; //any stage of cancel and delivered
				if(dateIsNotNull)
					querycall += " and  (c_createddt >=? and c_createddt<=adddate(date(?),1)) ";
				
				ps = conn.prepareStatement(querycall); // create a statement
				ps.setString(1, custid); 
				if(dateIsNotNull) {
					ps.setString(2,fromDate);
					ps.setString(3,toDate);
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
