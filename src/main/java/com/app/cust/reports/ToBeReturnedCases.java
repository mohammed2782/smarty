package com.app.cust.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;

public class ToBeReturnedCases extends CoreMgr{
	
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
	public ToBeReturnedCases() {
		records = 0;
		MainSql = "select c_rmk , '' as pmtCheckBox, '' as selectedcases,'' as selectedcaseshidden, cust_name, q_stage, q_step, '' as totalrtn, "
				+ " '' as status , 'شحنات سلمت وراجعه ' as title, c_custid,c_custreceiptnoori,'' as pmtrmk, "
				+ " date(c_createddt) as c_createddt, concat(st_name_ar,' - ',ifnull(c_rcv_addr_rmk,'')) as addr, c_id,"
				+ " c_rcv_name , c_rcv_hp1,  '' as fromdate, '' as todate,"
				+ " c_changedprice, c_priceb4change, c_receiptamt as currentreceiptprice, c_shipmentpaidbycustomer, c_shipmentpaidbysender, c_rural, "
				+ " case when q_step='PART_SUCC' and cc_qstatus_tobranch='ACTV' then cc_tobranch  when q_step='PART_SUCC' and cc_qstatus_frombranch='ACTV' then cc_frombranch else q_branch end as inbranch "
				+ " from p_cases "
				+ " left join kbcustomers on cust_id = c_custid "
				+ " left join kbstate on st_code = c_rcv_state and st_branch={userstorecode}"
				+ " left join p_caseschain on(cc_caseid = c_id and cc_frombranch = c_branchcode) "
				+ " left join p_customer_return on (acr_id = c_cust_rtnid)"
				+ " where c_custid in ({shopsCommaSeperated}) and (c_cust_rtnid=0 or ( c_cust_rtnid>0 and acr_closed='N') ) "
				+ " and c_allowrtncustomer='Y' and q_branch=c_branchcode and  (date(curdate()-14) <= date(q_enterdate))";
		
		userDefinedGroupColsOrderBy = "c_createddt,c_id";
		UserDefinedPageRows = 50000;
		userDefined_x_panelclass = "account_x_panel";
		userDefinedGroupByCol = "c_createddt";
		userDefinedGridCols.add("c_custreceiptnoori");
		//userDefinedGridCols.add("c_id");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("addr");
		
		userDefinedGridCols.add("status");
		userDefinedGridCols.add("c_rmk");
		
		userDefinedColLabel.put("c_id", "رقم الشحنه");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("c_createddt", "تاريخ الإدخال");
		userDefinedColLabel.put("c_rcv_name", "إسم المستلم");
		userDefinedColLabel.put("addr", "العنوان");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("selectedcases", "أرقام الوصولات");
		userDefinedColLabel.put("c_custid", "مندوب التوصيل");
		userDefinedColLabel.put("pmtdate", "تاريخ الدفع");
		userDefinedColLabel.put("pmtrmk", "ملاحظات");
		userDefinedColLabel.put("status", "الحاله");
		userDefinedColLabel.put("fromdate","من تاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		userDefinedColLabel.put("totalrtn", "عدد الشحنات");
		userDefinedColLabel.put("cust_name", "صاحب المحل");
		
		canNew = false;
		mainTable = "p_cases";
		keyCol = "c_id";
		
		
		userModifyTD.put("c_custreceiptnoori", "modifyReceiptNo({c_custreceiptnoori},{c_id})");
		userModifyTD.put("c_rcv_hp1", "modifyRcvNo({c_rcv_hp1})");
		userModifyTD.put("status", "modifyStatus({inbranch},{c_changedprice},{c_priceb4change}, {currentreceiptprice},{q_stage}, {q_step}, {c_shipmentpaidbycustomer}, {c_shipmentpaidbysender},{c_rural})");
		
		userDefinedColsMustFill.add("c_custid");
		userDefinedLookups.put("c_custid", "select cust_id, cust_name from kbcustomers where cust_id in ({shopsCommaSeperated})");
		
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		userDefinedColsTypes.put("c_rcv_hp1", "VARCHAR");//to remove the comma
		//myhtmlmgr.tableClass = "table table-striped  table-bordered turquoise_table";
		
		userDefinedCaption = "رواجع غير مسلمة";
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
		String custIds = replaceVarsinString(" {shopsCommaSeperated} ", arrayGlobals).trim();
		int userStorCode = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		Connection conn2 = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String custRecieptNo ="";
		String custHp = "";
		try {
			conn2 = mysql.getConn();
			String sql ="select c_custreceiptnoori "
					+ " from p_cases "
					+ " where  c_custid in ("+custIds+")   "
					+ "  and c_allowrtncustomer='Y' and c_cust_rtnid=0 and q_branch=c_branchcode "
					+ "  order by c_custreceiptnoori";
			pst = conn2.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getString("c_custreceiptnoori").equalsIgnoreCase(custRecieptNo))
					repeatedNo.put(custRecieptNo, custRecieptNo);
				
				custRecieptNo = rs.getString("c_custreceiptnoori");
			}
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
			sql = "select c_rcv_hp1 "
					+ " from p_cases "
					+ " where  c_custid in ("+custIds+") "
					+ " and c_allowrtncustomer='Y' and c_cust_rtnid=0 "
					+ "  order by c_rcv_hp1";
			pst = conn2.prepareStatement(sql);
				rs = pst.executeQuery();
				while (rs.next()) {
					if (rs.getString("c_rcv_hp1").equalsIgnoreCase(custHp))
						repeatedHp.put(custHp, custHp);
					
					custHp = rs.getString("c_rcv_hp1");
				}
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}			
				sql = "select c_custreceiptnoori "
						+ " from p_cases "
						+ " where c_allowrtncustomer='Y' and c_custid in ("+custIds+")  and c_cust_rtnid=0 and q_branch=c_branchcode and  c_id in (";
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
				userDefinedNewColsDefualtValues.put("selectedcases",casesArray );
				userDefinedNewCols.remove("fromdate");
				userDefinedNewCols.remove("todate");
				userDefinedNewColsDefualtValues.put("totalrtn", new String [] {totCases+""});
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
		
		String s = "<td><input type=\"checkbox\" class=\"flat\" "
				+ " id=\"pmtcheck_"+hashy.get("c_id")+"\" onclick=\"checkBoxPmtClicked(this, "+hashy.get("c_id")+")\">";
		s +="</td>";
		return s;	
	}
	
	public String modifyStatus (HashMap<String,String> hashy) {
		String msg  ="";
		String html = "<td>";
		
		
		if (hashy.get("q_stage").equalsIgnoreCase("CNCL") ) {
			noOfRtnitems++;
			msg ="راجع";
		}else if (hashy.get("q_stage").equalsIgnoreCase("DLV") ) {
			if (hashy.get("q_step").equalsIgnoreCase("PART_SUCC")) {
				msg ="تم التسليم - راجع جزئي";
				Connection conn2 = null;
				PreparedStatement pst = null;
				ResultSet rs = null;
				String inBranch = "";
				try {
					conn2 = mysql.getConn();
					pst = conn2.prepareStatement("select branch_name from kbbranches where branch_id = ?");
					pst.setString(1, hashy.get("inbranch"));
					rs = pst.executeQuery();
					if(rs.next())
						inBranch = rs.getString("branch_name");
				}catch (Exception e) {
					e.printStackTrace();
				}finally {
					try {rs.close();}catch(Exception e) {}
					try {pst.close();}catch(Exception e) {}
					try {conn2.close();}catch(Exception e) {}
				}
				msg += "<br /> الجزء الراجع في فرع "+inBranch;
				noOfRtnitems++;
			}else {
				errorFlag = true;
				msg = "خطأ "+hashy.get("q_step") ;
			}
			
		}else {
			errorFlag = true;
			html += "خطأ في النظام"+", "+ hashy.get("q_step");
			System.out.println("خطأ في النظام agent balance class,modifyStatus method stage=>"+hashy.get("q_stage")+", step=>"+hashy.get("q_step"));
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
	
	private ArrayList<String> getCid(int custid, Connection conn, String fromDate, String toDate, int userBranchId) throws Exception {
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
				+ " where q_status !='CLS' and c_custid =?  "
				+ " and c_allowrtncustomer='Y' "; //any stage of cancel and delivered
			if(dateIsNotNull)
				querycall += " and  (c_createddt >=? and c_createddt<adddate(date(?),1)) ";
				ps = conn.prepareStatement(querycall); // create a statement 
				ps.setInt(1, custid);
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

