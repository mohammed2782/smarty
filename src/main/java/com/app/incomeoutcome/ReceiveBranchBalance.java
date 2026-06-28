package com.app.incomeoutcome;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.swing.InputMap;

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

public class ReceiveBranchBalance extends CoreMgr {
		private LinkedHashMap <String,String> repeatedNo = new LinkedHashMap<String,String>();
		Utilities util = new Utilities();
		private int records = 0;
		private double totReceiptAmt = 0;
		public double getTotReceiptAmt() {
			return totReceiptAmt;
		}
		private double totShipmentCost = 0;
		private double totMoneySent  = 0;
		boolean errorFlag = false;
	public ReceiveBranchBalance() {
		records = 0;
		MainSql = "select '' as totshipmentcost, '' as selectedcases,'' as selectedcaseshidden, q_stage, q_step,'تم التسليم' as status ,'' as pmtCheckBox,  'شحنات سلمت وراجعه فقط' as title, c_custid,c_custreceiptnoori,'' as totamt,'' as rcvrmk, '' as rcvdate, "
				+ " date(c_createddt) as c_createddt , concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr, c_id, "
				+ " c_rcv_name , c_rcv_hp, '' as fromdate, '' as todate,"
				+ " c_receiptamt, c_sendmoney, cc_frombranch, cc_tobranch, cc_branchpmtid,  "
				+ " (case when (cc_pathcost>0)  then (c_receiptamt -  cc_pathcost) else  (c_receiptamt - c_shipment_cost)  end) as netamt,"
				+ " (case when (cc_pathcost>0) then cc_pathcost else c_shipment_cost end) as c_shipment_cost"
				+ " from p_cases  "
				+ " left join kbstate on st_code = c_rcv_state and st_branch = {userstorecode}"
				+ " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
				+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch = {userstorecode} and cc_tobranch = {branchesAcctToReceive} and cc_branchpmtid!=0 and cc_branchrecievedpmt='N') "
				+ "	where q_stage = 'DLV' and q_step = 'DLEIVERD' and c_settled !='FULL' and c_receiptfromsystem='N' and q_status !='CLS'"; 
		userDefinedSumCols.add("c_receiptamt");
		userDefinedSumCols.add("c_shipment_cost");
		
		userDefinedSumCols.add("netamt");
		userDefinedGroupColsOrderBy = "c_createddt, c_custreceiptnoori";
		
		UserDefinedPageRows = 2000;
		groupSumCaption = "المجموع";
		userDefined_x_panelclass = "account_x_panel";
		userDefinedGroupByCol = "c_createddt";
		
		userDefinedGridCols.add("c_id");
		userDefinedGridCols.add("c_custid");
		
		userDefinedGridCols.add("c_rcv_hp");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_shipment_cost");
		//userDefinedGridCols.add("c_sendmoney");
		userDefinedGridCols.add("netamt");
		userDefinedGridCols.add("status");
		//userDefinedGridCols.add("pmtCheckBox");
		
		userDefinedColLabel.put("pmtCheckBox", "دفع");
		userDefinedColLabel.put("c_id", "رقم الشحنه");
		userDefinedColLabel.put("c_createddt", "تاريخ الشحنات");
		userDefinedColLabel.put("c_rcv_name", "إسم المستلم");
		userDefinedColLabel.put("addr", "العنوان");
		userDefinedColLabel.put("c_rcv_hp", "هاتف");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("c_shipment_cost", "تكلفة الشحن");
		userDefinedColLabel.put("c_custid", "إسم صاحب المحل");
		userDefinedColLabel.put("rcvdate", "تاريخ الإستلام");
		userDefinedColLabel.put("rcvrmk", "ملاحظات");
		userDefinedColLabel.put("totamt", "المبلغ المطلوب إستلامه");
		//userDefinedColLabel.put("c_sendmoney", "مبلغ مرسل إلى المستلم");
		userDefinedColLabel.put("netamt", "الصافي للعميل");
		userDefinedColLabel.put("status", "الحاله");
		userDefinedColLabel.put("fromdate","من تاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		userDefinedColLabel.put("selectedcases", "أرقام الوصولات");
		userDefinedColLabel.put("cc_frombranch", "محاسبة فرع");
		userDefinedPageFooterFunction = "myFooterFunction()";
		canNew = true;
		mainTable = "p_cases";
		keyCol = "c_id";
		
		userDefinedNewCols.add("cc_frombranch");
		userDefinedNewCols.add("selectedcases");
		userDefinedNewCols.add("totamt");
		userDefinedNewCols.add("rcvdate");
		userDefinedNewCols.add("rcvrmk");
		userDefinedNewCols.add("selectedcaseshidden");
		userDefinedHiddenNewCols.add("selectedcaseshidden");
		userDefinedNewCols.add("cc_branchpmtid");
		userDefinedNewColsDefualtValues.put("cc_branchpmtid", new String[] {"%select cc_branchpmtid"
				+ " from p_cases "
				+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch = {userstorecode} and cc_tobranch = {branchesAcctToReceive} and cc_branchpmtid!=0 and cc_branchrecievedpmt='N') "
				+ "	where q_stage = 'DLV' and q_step = 'DLEIVERD' and c_settled !='FULL' and c_receiptfromsystem='N' and q_status !='CLS'"});
		userDefinedHiddenNewCols.add("cc_branchpmtid");
			
		userModifyTD.put("c_receiptamt", "modifyReceiptAmt({c_receiptamt}, {q_stage}, {q_step})");
		userModifyTD.put("c_shipment_cost", "modifyShipmentCost({c_shipment_cost}, {q_stage}, {q_step}, {c_shipmentpaidbysender})");
		//userModifyTD.put("status", "modifyStatus({q_stage}, {q_step})");
		userModifyTD.put("c_custreceiptnoori", "modifyRecieptNo({c_custreceiptnoori})");
		userModifyTD.put("pmtCheckBox", "displayCheckBox({c_id})");
		//userModifyTD.put("c_custreceiptnoori", "displayAsString({c_custreceiptnoori})");
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		userDefinedColsMustFill.add("c_custid");
		userDefinedColsMustFill.add("totamt");
		userDefinedColsMustFill.add("rcvdate");
		userDefinedLookups.put("c_custid", "select cust_id, cust_name from kbcustomers ");
		userDefinedNewColsDefualtValues.put("totamt", new String [] {"%select "
				+ " sum(c_receiptamt) - sum((case when (cc_pathcost>0) then cc_pathcost else c_shipment_cost end))"  
				+ " from p_cases "
				+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch = {userstorecode} and cc_tobranch = {branchesAcctToReceive} and cc_branchpmtid!=0 and cc_branchrecievedpmt='N' and cc_branchrecievedpmt='N') "
				+ " where q_stage='DLV' and q_step='DLEIVERD' and c_settled !='FULL' and c_receiptfromsystem='N' and q_status !='CLS' and q_status !='CLS' "});
		
		
		userDefinedNewColsHtmlType.put("totamt", "TEXT");
		userDefinedNewColsDefualtValues.put("cc_frombranch", new String[] {"{branchesAcctToReceive}"});
		userDefinedLookups.put("cc_frombranch", "select branch_id, branch_name from kbbranches");
		userDefinedReadOnlyNewCols.add("cc_frombranch");
		userDefinedNewColsHtmlType.put("cc_frombranch", "DROPLIST");
		userDefinedNewColsHtmlType.put("rcvdate", "DATE");
		userDefinedNewColsHtmlType.put("rcvrmk", "TEXTAREA");
		userDefinedReadOnlyNewCols.add("totamt");
		userDefinedNewColsHtmlType.put("selectedcases", "MULTILIST");
		userDefinedReadOnlyNewCols.add("selectedcases");
		userDefinedNewCaption = "إستلام مستحقات الزبائن";
		userDefinedTableHeadersClass = "bg-success bg-lighten-1 white";
	}
	
	public String displayCheckBox (HashMap<String, String> hashy) {
		
		String s = "<td><input type=\"checkbox\" class=\"flat\" id=\"pmtcheck_"+hashy.get("c_id")+"\" onclick=\"checkBoxPmtClicked(this, "+hashy.get("c_id")+")\">";
		s +="</td>";
		return s;	
	}
	
	public String modifyRecieptNo(HashMap<String, String> hashy) {
		String s = "";
		String style= "";
		
		if (repeatedNo.containsKey(hashy.get("c_custreceiptnoori")))
			style = "background-color:red";
		
		s +="<td style='"+style+"'>";
		s +=hashy.get("c_custreceiptnoori");
		s +="</td>";
		return s;	
	}
	
	@Override
	public void initialize(HashMap smartyStateMap){
		int userBranch = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		int otherBranch = Integer.parseInt(replaceVarsinString("{branchesAcctToReceive}", arrayGlobals).trim());
		String selectedCasesForReceive = "";
		Map<String, String[]> parameters = httpSRequest.getParameterMap();
		ArrayList<String> casesToReceiveList = new ArrayList<String>();
		for(String parameter : parameters.keySet()) {
			if (!excludeKeyWords.contains(parameter)){
				if (parameter.equalsIgnoreCase("op") && parameters.get("op")!=null && parameters.get("op")[0].equalsIgnoreCase("new")) {
					if (parameters.containsKey("selected_casesto_receive") && parameters.get("selected_casesto_receive")!=null
							&& !parameters.get("selected_casesto_receive")[0].equalsIgnoreCase("")) {
						selectedCasesForReceive = parameters.get("selected_casesto_receive")[0];
						casesToReceiveList = Utilities.SplitStringToArrayList(selectedCasesForReceive , ",");
					}
					
				}
			}
		}
		
		String custRecieptNo ="";
		Connection conn2 = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn2 = mysql.getConn();
			ps = conn2.prepareStatement("select c_custreceiptnoori "
				+ " from p_cases "
				+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch=? and cc_tobranch=? and cc_branchpmtid!=0 and cc_branchrecievedpmt='N') "
				+ " where q_status !='CLS' and c_settled !='FULL' "
				+ " and (q_stage = 'DLV' and q_step = 'DLEIVERD' and c_settled !='FULL')  and c_receiptfromsystem='N' and q_status !='CLS'" 
				+ "  order by c_custreceiptnoori");
			ps.setInt(1,otherBranch);
			ps.setInt(2,userBranch);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString("c_custreceiptnoori").equalsIgnoreCase(custRecieptNo))
					repeatedNo.put(custRecieptNo, custRecieptNo);
				
				custRecieptNo = rs.getString("c_custreceiptnoori");
			}
			try {rs.close();} catch (Exception e) {}
			try {ps.close();} catch (Exception e) {}
			
			// check if the user selected boxes for payment, if yes then get the boxes values
			String buildInClause = "";
			String sql = "select c_custreceiptnoori ,ifnull( (sum(c_receiptamt) - sum(case when (cc_pathcost>0) then cc_pathcost else c_shipment_cost end) ),0)as totdue "
					+ " from  p_cases "
					+ " join p_caseschain on (c_id = cc_caseid and cc_branchpmtid!=0 and cc_branchrecievedpmt='N') "
					+ "where c_id in (";
			boolean first = true;
			
			for (String caseid : casesToReceiveList) {
				if (!first) {
					sql += ",";
				}
				first = false;
				sql +="?";
			}
			sql +=") and c_receiptfromsystem='N' and q_status !='CLS'  group by c_custreceiptnoori ";
			
			userDefinedNewCols.remove("selectedcases");
			userDefinedNewCols.remove("selectedcaseshidden");
	
			userDefinedNewLookups.put("totamt", "!select ifnull(sum(c_receiptamt) - sum(case when (cc_pathcost>0) then cc_pathcost else c_shipment_cost end),0)  "
					+ " from  p_cases "
					+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch = "+otherBranch+" and cc_tobranch = "+userBranch+" and cc_branchpmtid!=0 and cc_branchrecievedpmt='N') "
					+ " where q_status !='CLS' and  )"
					+ " and c_settled !='FULL' and c_receiptfromsystem='N' and q_status !='CLS'  and q_stage = 'DLV' and q_step = 'DLEIVERD' and c_settled !='FULL' "
					+ " and (c_createddt >= '{fromdate}' and c_createddt<=adddate(date('{todate}'),1)) ");		
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {ps.close();} catch (Exception e) {}
			try {conn2.close();} catch (Exception e) {}
			
		}
		super.initialize(smartyStateMap);
	}
	
	
	
	/**
	 * @param hashy
	 * @return
	 */
	/*
	 * public String modifyStatus (HashMap<String,String> hashy) { String html =
	 * "<td>";
	 * 
	 * if (hashy.get("q_stage").equalsIgnoreCase("cncl") &&
	 * hashy.get("c_shipmentpaidbysender").equalsIgnoreCase("Y")) { html
	 * ="<td style='background-color:#928700; color:white;'>راجع مع دفع اجور النقل من صاحب المحل"
	 * ; }else if (hashy.get("q_stage").equalsIgnoreCase("dlv_stg") &&
	 * hashy.get("q_step").equalsIgnoreCase("delivered") ) { html +="تم التسليم";
	 * }else { html =
	 * "<td style='background-color:red;color:white' >خطأ في النظام, أتصل بسوفتيكا رجاء"
	 * ; errorFlag = true; } html+= "</td>"; return html;
	 * 
	 * }
	 */
	
	
	  public String modifyReceiptAmt (HashMap<String,String> hashy) {
	  setRecords(getRecords() + 1);
	  String html = "<td>"; 
	  double goodsCost =  Double.parseDouble(hashy.get("c_receiptamt")); 
	  if (hashy.get("q_stage").equalsIgnoreCase("cncl")) { 
		  goodsCost = 0; 
		  html += " - "; 
	  }else { 
		  html += numFormat.format(goodsCost);
	  } 
	  totReceiptAmt +=goodsCost; 
	  html+= "</td>"; 
	  return html;
	  
	  }
	 
	
	
	  public String modifyShipmentCost (HashMap<String,String> hashy) { 
		  String html = "<td style=''>"; 
		  double shipmentCost =Double.parseDouble(hashy.get("c_shipment_cost")); 
		  if(hashy.get("q_stage").equalsIgnoreCase("cncl") ) { 
			  if (hashy.get("c_shipmentpaidbysender").equalsIgnoreCase("Y")) { 
				  html += numFormat.format(shipmentCost); 
				  }else { 
					  shipmentCost =0; 
					  html += " - "; 
				  }
			  }else{ html += numFormat.format(shipmentCost); } 
		  totShipmentCost +=shipmentCost;
	  
	  html+= "</td>"; return html; }
	 
	
	public String myFooterFunction(String colName) {
		if (colName.equalsIgnoreCase("c_custreceiptnoori") ) {
			if (!errorFlag) {
				double totalDue = (totReceiptAmt - totShipmentCost);
				return "<td colspan='2' stye='font-size: 15px;' align='center'><label> المبلغ الصافي لأستلامه </label>"
						+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\"> "+numFormat.format(totalDue)+" </strong>"
						+ "<form action=\"?\" method=\"post\" style=\"display: inline;\" onsubmit=\"checkBoxPmtClicked()\"><input type=\"hidden\" name=\"op\" value=\"new\"><input type=\"hidden\""
						+ " name=\"myClassBean\" value=\"com.app.incomeoutcome.ReceiveBranchBalance\">"
						+ "<input type=\"hidden\" name=\"selected_casesto_receive\" value='' id='selected_casesto_receive'>"
						+ "<button type=\"submit\" class=\"btn btn-danger btn-sm\" name=\"smarty_newformbtn\" value=\"newform\">إستلم الأن</button></form></td>";
			}else {
				return  "<td colspan='2' stye='font-size: 15px;' align='center'>هنالك خطأ في النظام الرجاء الأتصال بسوفتيكا</td>";
			}
		}else if (colName.equalsIgnoreCase("c_goods_cost") || colName.equalsIgnoreCase("c_shipment_cost") || colName.equalsIgnoreCase("c_sendmoney"))
			return "";
		else
			return "<td></td>";
	}
	
	/* (non-Javadoc)
	 * @see com.app.core.CoreMgr#doInsert(javax.servlet.http.HttpServletRequest, boolean)
	 */
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autoCommit) {
		String statusMsg = " تم تسجيل الدفعه ";
		PreparedStatement ps = null;
		int userId = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		int otherBranch = Integer.parseInt(replaceVarsinString(" {branchesAcctToReceive} ", arrayGlobals).trim());
		int userBranch = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		inputMap_ori = filterRequest(rqs);
		UtilitiesFeqar utf = new UtilitiesFeqar();
		ArrayList<String> cidList = new ArrayList<String>();
		//Utilities ut = new Utilities();
		try {
			String rmk = inputMap_ori.get("rcvrmk")[0];
			String rcvdate = inputMap_ori.get("rcvdate")[0];
			int pmtId = Integer.parseInt(inputMap_ori.get("cc_branchpmtid")[0]) ;
			
			int branchIdFromRequest =Integer.parseInt(inputMap_ori.get("smarty_showonly_cc_frombranch")[0]);
			double receivedAmtForm = 0; 
			if (branchIdFromRequest!=otherBranch) 
				throw new Exception
				   		("Error, customer in form is ("+branchIdFromRequest+") and global customer id is ("+otherBranch+") are not the same");
			  
			double calpmtAmt = 0.0; 
			   
			calpmtAmt = getTotaAmtToPayForBranche (conn , branchIdFromRequest, userBranch); //get list of cids 
			cidList = getCid(conn , branchIdFromRequest, userBranch); 
			  
			try { 
				  receivedAmtForm = Double.parseDouble(inputMap_ori.get("totamt")[0]); 
				  if(receivedAmtForm ==0 && cidList.isEmpty())
					  throw new Exception("لا توجد شحنات للتحاسب"); 
				  }catch(NumberFormatException num) { 
					  throw new Exception ("مبلغ الدفع غير متوفر"); 
					  } 
			if (calpmtAmt != receivedAmtForm) throw new Exception ("Error, payemnt amount in form is ("
					  +receivedAmtForm+"),and calculated amount is ("+calpmtAmt+") are not the same");
			
			 
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date javaDate = formatter.parse(rcvdate);
			Date date = new Date(javaDate.getTime());
			String safeOff = "Y";
			if(utf.getSafeActiveCondition(conn, userBranch))
				safeOff = "N";


			ps = conn.prepareStatement("update p_caseschain set cc_branchrecievedpmt='Y' where cc_branchpmtid=?");
			ps.setInt(1, pmtId);
			ps.executeUpdate();
			try {ps.close();} catch (Exception e) {}
			
			
			ps = conn.prepareStatement("update p_branch_payments set bp_receivedby=?, bp_receiveddt=?, pb_receivedrmk=?, "
					+ "bp_received_createddt=now(), bp_safeoff =? where bp_id=?");
			ps.setInt(1, userId);
			ps.setDate(2, (Date) date);
			ps.setString(3, rmk);
			ps.setString(4, safeOff);
			ps.setInt(5, pmtId);
			ps.executeUpdate();
			
			conn.commit();
		} catch (Exception e) {
			statusMsg = "Error at payment creation, error (" + e.getMessage()+ ")";
			setInsertErrorFlag(true);
			try {conn.rollback();} catch (Exception ignoreE) {}
			e.printStackTrace();
		} finally {
			try {ps.close();} catch (Exception e) {}
		}
		return statusMsg;
	}
	
		
	

	private double getTotaAmtToPayForBranche( Connection conn , int otherBranch, int userBranch) throws Exception {
		double amt = 0.0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String querycall = "select (sum(c_receiptamt) - sum(case when (cc_pathcost>0) then cc_pathcost else c_shipment_cost end)) " + 
					" from p_cases "
					+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch = ? and cc_tobranch = ? and cc_branchpmtid!=0 and cc_branchrecievedpmt='N' and cc_branchrecievedpmt='N' ) "
					+ "	where q_stage = 'DLV' and q_step = 'DLEIVERD' and c_settled !='FULL' and c_receiptfromsystem='N' and q_status !='CLS' and q_status !='CLS'";
				
				
			ps = conn.prepareStatement(querycall); // create a statement
			ps.setInt(1, userBranch); 
			ps.setInt(2, otherBranch); 
			rs = ps.executeQuery();
			while (rs.next()) {
				amt = rs.getDouble(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {ps.close();} catch (Exception e) {}
		}
		return amt;
	}// end of get_cid


	private ArrayList<String> getCid( Connection conn , int otherBranch, int userBranch) throws Exception {
		ArrayList<String> cases = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean dateIsNotNull = false;
		try {
			String querycall = "select c_id From p_cases  "
					+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch=? and cc_tobranch=? and cc_branchpmtid!=0 and cc_branchrecievedpmt='N') "
					+ "	where q_stage = 'DLV' and q_step = 'DLEIVERD' and c_settled !='FULL' and c_receiptfromsystem='N' and q_status !='CLS' and q_status !='CLS'"; 			if(dateIsNotNull)
				querycall += " and  (c_createddt >=? and c_createddt<=adddate(date(?),1)) ";
				
			ps = conn.prepareStatement(querycall); // create a statement
			ps.setInt(1, otherBranch); 
			ps.setInt(2, userBranch);
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
}
