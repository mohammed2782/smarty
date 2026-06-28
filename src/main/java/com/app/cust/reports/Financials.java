package com.app.cust.reports;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;
import com.mysql.jdbc.Statement;

public class Financials extends CoreMgr{
	private LinkedHashMap <String,String> repeatedNo = new LinkedHashMap<String,String>();
	Utilities util = new Utilities();
	private int records = 0;
	private int debt;
	private double totReceiptAmt = 0;
	boolean errorFlag = false;
	private double totNetAmt = 0;
	
	public Financials() {
		records = 0;
		MainSql = "select cust_name, c_priceb4change, c_receiptamt as currentreceiptprice, c_changedprice, c_paidinadvance,c_advancepmtid,  '' as selectedcases,"
				+ "'' as selectedcaseshidden, q_stage, q_step,'' as newcustid, '' as status ,  'شحنات سلمت وراجعه فقط' as title, c_custid,c_custreceiptnoori,"
				+ "'' as totamt,'' as pmtrmk, '' as pmtdate, "
				+ " date(c_createddt) as c_createddt , concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr, c_id, "
				+ " c_rcv_name , c_rcv_hp1, '' as fromdate, '' as todate,"
				+ " (case when (q_stage='DLV'  and c_paidinadvance ='NO') then c_receiptamt"
				+ "       when ( q_stage='DLV'  and c_paidinadvance ='YES' and c_changedprice='Y' ) then (c_receiptamt - c_priceb4change) "
				+ "       when ( q_stage='DLV'  and c_paidinadvance ='YES' and c_changedprice='N' ) then 0 "
				+ "       when  ( q_stage='CNCL'  and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then c_receiptamt*-1 "
				+ "				else 0 end) as c_receiptamt,"
				+ "  (case when ( q_stage='DLV'  and c_paidinadvance ='YES' and c_changedprice='N' ) then 0  else c_shipment_cost end) as c_shipment_cost, "
				+ " (	case "
				+ "			when (q_stage='DLV'  and  c_paidinadvance ='NO')  then (c_receiptamt - c_shipment_cost) "
				+ " 		when ( q_stage='DLV'  and c_paidinadvance ='YES' and c_changedprice='Y' ) then c_receiptamt - c_priceb4change  "
				+ " 		when ( q_stage='DLV'  and c_paidinadvance ='YES' and c_changedprice='N' ) then 0  "
				+ "			when ( q_stage='CNCL'  and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then (c_shipment_cost - c_receiptamt) "
				+ " else (1*-c_shipment_cost)  end) as netamt, '' as credit, '' as totalnet "
				+ " from p_cases "
				+ " join kbcustomers on cust_id = c_custid "
				+ " left join kbstate on (st_code = c_rcv_state and st_branch = c_branchcode) "
				+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_id=c_rcv_state) "
				+ " where c_custid in({shopsCommaSeperated})  "
				+ " and c_allowcustpay='Y'  and c_pmtid=0 and (date(curdate()-14) <= date(q_enterdate))"
				+ "   order by c_custreceiptnoori ";
		
		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
		userDefinedSumCols.add("c_receiptamt");
		userDefinedSumCols.add("c_shipment_cost");
		userDefined_x_panelclass = "account_x_panel";
		userDefinedSumCols.add("netamt");
		userDefinedGroupColsOrderBy = "c_custid, c_custreceiptnoori";
		
		UserDefinedPageRows = 3000;
		groupSumCaption = "المجموع";
		
		userDefinedGroupByCol = "cust_name";
		//userDefinedSlidingGroups = true;
		//userDefinedSlidingGroupValue = "c_custid";
		//Grid Cols
		//userDefinedGridCols.add("c_id");
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("netamt");
		userDefinedGridCols.add("status");
		//Lables
		userDefinedColLabel.put("c_id", "رقم الشحنه");
		userDefinedColLabel.put("c_createddt", "تاريخ الشحنات");
		userDefinedColLabel.put("c_rcv_name", "إسم المستلم");
		userDefinedColLabel.put("addr", "العنوان");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("c_shipment_cost", "تكلفة الشحن");
		userDefinedColLabel.put("c_custid", "المتجر");
		userDefinedColLabel.put("pmtdate", "تاريخ الدفع");
		userDefinedColLabel.put("pmtrmk", "ملاحظات");
		userDefinedColLabel.put("totamt", "مبالغ الوصولات المحددة");
		userDefinedColLabel.put("netamt", "الصافي للعميل");
		userDefinedColLabel.put("status", "الحاله");
		userDefinedColLabel.put("fromdate","من تاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		userDefinedColLabel.put("selectedcases", "عدد الوصولات المحددة");
		userDefinedColLabel.put("c_mastercustid", "العميل");
		userDefinedColLabel.put("totalnet", "المبلغ المسدد للعميل");
		userDefinedColLabel.put("credit", "إستقطاع لسداد دين");
		
		userDefinedPageFooterFunction = "myFooterFunction()";
		canNew = true;
		canFilter = true;
		mainTable = "p_cases";
		keyCol = "";
		
		userDefinedFilterCols.add("c_custid");
		userDefinedFilterColsHtmlType.put("c_custid", "MULTILIST");
		//newCols

		
		userModifyTD.put("c_receiptamt", "modifyReceiptAmt({c_changedprice},{c_receiptamt}, {q_stage}, {q_step},{c_paidinadvance},{c_advancepmtid})");
		userModifyTD.put("c_shipment_cost", "modifyShipmentCost( {c_changedprice},{c_shipment_cost}, {q_stage}, {q_step},{c_paidinadvance},{c_advancepmtid})");
		userModifyTD.put("status", "modifyStatus({c_priceb4change}, {currentreceiptprice}, {c_changedprice}, {q_stage}, {q_step},{c_paidinadvance},{c_advancepmtid})");
		userModifyTD.put("c_custreceiptnoori", "modifyRecieptNo({c_custreceiptnoori})");
		userModifyTD.put("netamt", "sumNetAmt({netamt}, {c_id})");
		userModifyTD.put("c_custreceiptnoori", "modifyReceiptNo({c_custreceiptnoori},{c_id})");
		
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		
	
		userDefinedLookups.put("c_custid", "select cust_id, cust_name from kbcustomers where cust_id in ({shopsCommaSeperated})");
		

		userDefinedNewCaption = "مستحقات عميل";
		
		
	}
	
	public String modifyReceiptNo  (HashMap<String, String> hashy) {

		String s = "<td caseid='"+hashy.get("c_id")+"' id='"+hashy.get("c_custreceiptnoori")+"'>"+hashy.get("c_custreceiptnoori");
		s +="</td>";
		return s;
	}
	
	public String displayCheckBox (HashMap<String, String> hashy) {
		String s = "<td><input type=\"checkbox\"  "
				+ " id=\"pmtcheck_"+hashy.get("c_id")+"\"  data-single-check-custid-"+hashy.get("c_custid")+" ='"+hashy.get("c_custid")+"' "
						+ " onclick=\"checkBoxPmtClicked(this, "+hashy.get("c_id")+")\">";
		s +="</td>";
		return s;	
	}
		
	public String sumNetAmt(HashMap<String, String> hashy) {
		String s = "";
		double netAmt = Double.parseDouble(hashy.get("netamt"));
		s +="<td dir='ltr' id = 'td_netamt_"+hashy.get("c_id")+"' data-netval='"+netAmt+"' style='text-align:right'>";
		s += numFormat.format(netAmt);
		s +="</td>";
		totNetAmt +=netAmt;
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
		String custIds = replaceVarsinString(" {shopsCommaSeperated} ", arrayGlobals).trim();
		boolean checkBoxPayment = false;
		String selectedCasesForPayment = "", selectedCustomerForPayment="";
		String selectedAmounToPay = "0";
		Map<String, String[]> parameters = httpSRequest.getParameterMap();
		ArrayList<String> casesToPayList = new ArrayList<String>();
		ArrayList<String> receiptsToPayList = new ArrayList<String>();
		ArrayList<String> customersToPayList = new ArrayList<String>();
		String custRecieptNo ="";
		Utilities ut = new Utilities();
		Connection conn2 = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		userDefinedCaption = "<div class=\"col-sm-2\" "
		+ " style='padding-right: 10px;'>"
		+ "	<div class='position-relative'>"
		+ " <input type='text' class='form-control ps-5 radius-30' placeholder='بحث عن وصل'> <span class='position-absolute top-50 product-show translate-middle-y'>"
		+ "	<i class='bx bx-search'></i></span></div></div>";
		
		for(String parameter : parameters.keySet()) {
			if (!excludeKeyWords.contains(parameter)){
				if (parameter.equalsIgnoreCase("op") && parameters.get("op")!=null && parameters.get("op")[0].equalsIgnoreCase("new")) {
					if (parameters.containsKey("selected_casesto_pay") && parameters.get("selected_casesto_pay")!=null
							&& !parameters.get("selected_casesto_pay")[0].equalsIgnoreCase("")) {
						selectedCasesForPayment = parameters.get("selected_casesto_pay")[0];
						casesToPayList = Utilities.SplitStringToArrayList(selectedCasesForPayment , ",");
						checkBoxPayment = true;
					}
					if (parameters.containsKey("amount_topay") && parameters.get("amount_topay")!=null
							&& !parameters.get("amount_topay")[0].equalsIgnoreCase("")) {
						selectedAmounToPay = parameters.get("amount_topay")[0];
					}
					if (parameters.containsKey("customers_topay") && parameters.get("customers_topay")!=null
							&& !parameters.get("customers_topay")[0].equalsIgnoreCase("")) {
						selectedCustomerForPayment = parameters.get("customers_topay")[0];
						customersToPayList = Utilities.SplitStringToArrayList(selectedCustomerForPayment , ",");
						userDefinedLookups.put("c_custid", "select cust_id, cust_name from kbcustomers "
								+ " where cust_id in("+custIds+") and cust_is in ("+selectedCustomerForPayment+") ");
					}
				}
			}
		}
		try {
			if (!checkBoxPayment) {
				conn2 = mysql.getConn();
				String sql = "select c_custreceiptnoori from p_cases where c_custid in (?) and c_allowcustpay='Y' and c_pmtid=0 ";
				pst = conn2.prepareStatement(sql);
				pst.setString(1, custIds);
				rs = pst.executeQuery();
				while (rs.next()) {
					if (rs.getString("c_custreceiptnoori").equalsIgnoreCase(custRecieptNo))
						repeatedNo.put(custRecieptNo, custRecieptNo);
					
					custRecieptNo = rs.getString("c_custreceiptnoori");
				}
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
			}else {
				BigInteger amountToPay =  BigInteger.valueOf(Integer.parseInt(selectedAmounToPay));
				BigInteger debtBig = BigInteger.valueOf(debt);
				BigInteger net = amountToPay.subtract(debtBig);
				userDefinedNewColsDefualtValues.put("totamt", new String[] {selectedAmounToPay});
				userDefinedNewColsDefualtValues.put("credit", new String[] {debt+""});
				userDefinedNewColsDefualtValues.put("totalnet", new String[] {net+""});
				userDefinedNewColsDefualtValues.put("selectedcaseshidden", new String[] {selectedCasesForPayment});
				userDefinedNewColsDefualtValues.put("selectedcases", new String [] {casesToPayList.size()+""} );
				userDefinedLookups.put("totalnet", "!select replace('{totamt}',  ',', '') - replace('{credit}',  ',', '') from dual");
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
	/**
	 * @param hashy
	 * @return
	 */
	public String modifyStatus (HashMap<String,String> hashy) {
		String html = "<td>";
		if (hashy.get("c_changedprice").equalsIgnoreCase("Y")){
			 html="<td style='background-color:grey; color:white;'>تم التسليم مع تغيير سعر من "+hashy.get("c_priceb4change")+" إلى "+hashy.get("currentreceiptprice");
			if (hashy.get("c_paidinadvance").equalsIgnoreCase("YES"))
				html += " - تمت المحاسة عليه مقدما";
			html +="</td>";
		}else {
			if (hashy.get("q_stage").equalsIgnoreCase("DLV")  ) { //successful dlv
				if (hashy.get("c_paidinadvance").equalsIgnoreCase("YES"))
					html +="تم التسليم - مدفوع مقدما ";
				else
					html +="تم التسليم";
			}else if (hashy.get("q_stage").equalsIgnoreCase("CNCL") &&  hashy.get("c_paidinadvance").equalsIgnoreCase("YES")) { // if advanced pmt and returned.
				html ="<td style='background-color:#c3069e; color:white;'>تم المحاسبه عليها مقدما  (دفعه مقدمه رقم "+hashy.get("c_advancepmtid")+")"+" وهي راجعه الأن.";
			}else {
				html = "<td style='background-color:red;color:white' >خطأ في النظام";
				errorFlag = true;
			}
		}
		html+= "</td>";
		return html;
	}
	
	public String modifyReceiptAmt (HashMap<String,String> hashy) {
		setRecords(getRecords() + 1);
		String html = "<td dir='ltr' style='text-align:right'>";
		double goodsCost = Double.parseDouble(hashy.get("c_receiptamt"));
		if (hashy.get("q_stage").equalsIgnoreCase("CNCL")) {
			if (hashy.get("c_paidinadvance").equalsIgnoreCase("YES")) {
				html += numFormat.format(goodsCost);
			}else {
				goodsCost = 0;
				html += " - ";
			}
		}else {
			html += numFormat.format(goodsCost);
		}
		html+= "</td>";
		return html;
		
	}
	
	public String modifyShipmentCost (HashMap<String,String> hashy) {
		String html = "<td style=''>";
		double shipmentCost = Double.parseDouble(hashy.get("c_shipment_cost"));
		if (hashy.get("q_stage").equalsIgnoreCase("CNCL") ) { // if returned
			if (hashy.get("c_paidinadvance").equalsIgnoreCase("YES")) { // paid in advance
				html += numFormat.format(shipmentCost);
			}else {
				shipmentCost = 0;
				html += " - ";	
			}
		}else{
			if (hashy.get("c_paidinadvance").equalsIgnoreCase("YES") && hashy.get("c_changedprice").equalsIgnoreCase("Y")  ) { // paid in advance and changed price
				html += "-";
			}else	
				html += numFormat.format(shipmentCost);
		}
		//totShipmentCost +=  shipmentCost;
		
		html+= "</td>";
		return html;
	}
	
	public String myFooterFunction(String colName) {
		if (colName.equalsIgnoreCase("c_custreceiptnoori") ) {
			if (!errorFlag) {
				double totalDue = totNetAmt - debt;
				return "<td colspan='11' stye='font-size: 15px;' align='center'>"
						+ "<div class='row'>"
						+ "<div class='col-6'>"
						+ " <label>مبلغ الوصولات</label>"
						+ "</div>"
						+ "<div class='col-6'>"
						+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
						+ " <span dir='ltr' style='text-align:right' id ='totalamountshouldbepaid'> "+numFormat.format(totNetAmt)+"</span></strong>"
						+ "</div>"
						+ "<div class='col-6'>"
						+ " <label>مبلغ الدين</label>"
						+ "</div>"
						+ "<div class='col-6'>"
						+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
						+ " <span dir='ltr' style='text-align:right' id ='cust-debt'> "+numFormat.format(debt)+"</span></strong>"
						+ "</div>"
						+ "<div class='col-6'>"
						+ " <label>الصافي</label>"
						+ "</div>"
						+ "<div class='col-6'>"
						+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
						+ " <span dir='ltr' style='text-align:right' id ='cust-net'> "+numFormat.format(totalDue)+"</span></strong>"
						+ "</div></td>";
			}else {
				return  "<td colspan='2' stye='font-size: 15px;' align='center'>هنالك خطأ في النظام الرجاء الأتصال </td>";
			}
		}else
			return "";
	}
	
	/* (non-Javadoc)
	 * @see com.app.core.CoreMgr#doInsert(javax.servlet.http.HttpServletRequest, boolean)
	 */
		
	private double getTotaAmtToPay( Connection conn , int masterCustId, ArrayList<String>caseList) throws Exception {
		double amt = 0.0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean first = true;
		try {
			String sql = "select ifnull( sum( "
					+ "(case "
					+ " when (q_stage='DLV'   and c_paidinadvance ='NO')  then (c_receiptamt - c_shipment_cost) "
					+ " when ( q_stage='DLV'  and c_paidinadvance ='YES' and c_changedprice='Y' ) then c_receiptamt - c_priceb4change  "
					+ " when ( q_stage='CNCL'  and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then (c_shipment_cost - c_receiptamt) "
					+ " when ( q_stage='DLV'  and c_paidinadvance ='YES' and c_changedprice='N' ) then 0  "
					+ " else (1*-c_shipment_cost)  end)),0) as totdue "
					+ " from  p_cases where c_mastercustid =? and c_allowcustpay='Y' and c_pmtid=0 and c_id in (";
			
			for (String caseid : caseList) {
				if (!first) {
					sql += ",";
				}
				first = false;
				sql +="?";
			}
			sql +=")    ";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, masterCustId);
			int i =2;
			for (String caseid : caseList) {
				pst.setString(i, caseid);
				i++;
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				amt += rs.getDouble("totDue");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {/* ignore */}
			try {pst.close();} catch (Exception e) {/* ignore */
			}
		}
		return amt;
	}// end of get_cid
	
	
	private void checkCasesSelected( Connection conn, int masterCustId, ArrayList<String> cidList) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean first = true;
		try {
			
			String sql = "select count(*) as tot, c_mastercustid From p_cases where  c_allowcustpay='Y' and c_pmtid=0 and c_id in ( ";
			for (String caseid : cidList) {
				if (!first) {
					sql += ",";
				}
				first = false;
				sql +="?";
			}
			sql +=") group by  c_mastercustid  ";
			pst = conn.prepareStatement(sql);
			int i =1;
			for (String caseid : cidList) {
				pst.setString(i, caseid);
				i++;
			}
			rs = pst.executeQuery();
			int howManyCust =0;
			int tot = 0;
			while (rs.next()) {
				tot = rs.getInt("tot");
				if (rs.getInt("c_mastercustid") != masterCustId)
					throw new Exception ("لا يتطابق العميل مع الشحنات المحددة");
				howManyCust ++;
			}
			if (tot != cidList.size())
				throw new Exception ("عدد الوصولات غير متطابق");
			
			if (howManyCust>1)
				throw new Exception ("أكثر من عميل للوصولات المحددة");
		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
	}// end of get_cid
	
	
	public int getRecords() {
		return records;
	}


	public void setRecords(int records) {
		this.records = records;
	}

	public int getDebt() {
		return debt;
	}

	public void setDebt(int debt) {
		this.debt = debt;
	}
	public double getTotReceiptAmt() {
		return totReceiptAmt;
	}

}
