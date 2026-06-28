package com.app.incomeoutcome;

import java.math.BigDecimal;
import java.math.BigInteger;
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

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.financials.AccountantBoxBean;
import com.app.financials.FinOperationCategory;
import com.app.financials.FinOperationCode;
import com.app.financials.FinOperationEntity;
import com.app.financials.PaymentImpactOnSafe;
import com.app.financials.SafePaymentTypeMetaInfoBean;
import com.app.financials.StandardTransactionBean;
import com.app.financials.UtilitiesFinancials;
import com.app.financials.UtilitiesSafeFinancials;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

public class PickUpAgentBalance extends CoreMgr {
	private LinkedHashMap <String,String> repeatedNo = new LinkedHashMap<String,String>();
	Utilities util = new Utilities();
	private int records = 0;
	private int debt;
	private double totReceiptAmt = 0;
	private long totReceiptAmtIqd = 0;
	boolean errorFlag = false;
	private long totNetAmt = 0;
	private long totNetAmtUsd=0;
	private double totShipmentCost = 0;
	private double totMoneySent  = 0;
	private boolean caseNeedsConfirmation = false;
//c_paytodlvcheck, c_usdchangedprice ,
	public PickUpAgentBalance() {
		/*
		 * hashy.get("q_stage"), hashy.get("q_step"), Integer.parseInt(hashy.get("c_agentpmtid")), 
		hashy.get("c_changedprice") , hashy.get("c_priceb4change"), hashy.get("c_receiptamt"), 
		hashy.get("c_usdchangedprice"), hashy.get("c_usdpriceb4change"), hashy.get("c_receiptamt_usd"));
		 */
		records = 0;
		MainSql = "select c_usdpriceb4change , c_agentpmtid, c_pickupagent, '' as selectedcases,'' as selectedcaseshidden, q_stage, q_step,'' as status ,'' as pmtCheckBox,  "
				+ " 'شحنات سلمت وراجعه فقط' as title, c_custid,c_custreceiptnoori,'' as pmtrmk, '' as pmtdate, c_priceb4change, c_receiptamt as currentreceiptprice,  "
				+ " date(c_createddt) as c_createddt , concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr, c_id, "
				+ " c_rcv_name , c_rcv_hp1 , c_advancepmtid , c_paidinadvance, c_changedprice, "
				+ " c_receiptamt_usd, c_receiptamt_usd as netamtusd, "
				+ "c_receiptamt,c_shipment_cost, (c_receiptamt - c_shipment_cost) as netamtiqd, "
				+ " '' as totalnet, '' as totalnet_usd, c_paytodlvcheck, c_usdchangedprice  "
				+ " from p_cases "
				+ " left join kbstate on st_code = c_rcv_state and st_branch = c_branchcode "
				+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
				+ " where c_pickupagent ={pickupAgentAcct} and c_settled !='FULL' "
				+ " and c_pmtid=0 and c_allowcustpay='Y' and c_pickupagentpmtid=0 ";
		
		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
		userDefinedSumCols.add("c_receiptamt");
		userDefinedSumCols.add("c_receiptamt_usd");
		userDefinedSumCols.add("c_shipment_cost");
		
		userDefinedSumCols.add("netamtiqd");
		userDefinedGroupColsOrderBy = "c_createddt, c_custreceiptnoori";
		
		UserDefinedPageRows = 5000;
		groupSumCaption = "المجموع";
		userDefined_x_panelclass = "account_x_panel";
		userDefinedGroupByCol = "c_createddt";
		
		userDefinedGridCols.add("c_id");
		userDefinedGridCols.add("c_custid");
		
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_shipment_cost");
		//userDefinedGridCols.add("c_sendmoney");
		userDefinedGridCols.add("netamtiqd");
		userDefinedGridCols.add("netamtusd");
		userDefinedGridCols.add("status");
		userDefinedGridCols.add("pmtCheckBox");
		
		userDefinedColLabel.put("pmtCheckBox", "دفع");
		userDefinedColLabel.put("netamtiqd", "صافي الوصل د.ع");
		userDefinedColLabel.put("netamtusd", "صافي الوصل $");
		userDefinedColLabel.put("c_id", "رقم الشحنه");
		userDefinedColLabel.put("c_createddt", "تاريخ الشحنات");
		userDefinedColLabel.put("c_rcv_name", "إسم المستلم");
		userDefinedColLabel.put("addr", "العنوان");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل د.ع");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
		userDefinedColLabel.put("c_shipment_cost", "تكلفة الشحن");
		userDefinedColLabel.put("c_custid", "المتجر");
		userDefinedColLabel.put("pmtrmk", "ملاحظات");
		userDefinedColLabel.put("totalnet", "المبلغ المستحق للوصولات د.ع");
		userDefinedColLabel.put("totalnet_usd", "المبلغ المستحق للوصولات $");
		userDefinedColLabel.put("status", "الحاله");
		userDefinedColLabel.put("selectedcases", "عدد الوصولات");
		userDefinedPageFooterFunction = "myFooterFunction()";
		canNew = true;
		mainTable = "p_cases";
		keyCol = "c_id";
		
		userDefinedNewCols.add("c_pickupagent");
		userDefinedNewCols.add("selectedcases");
		userDefinedNewCols.add("totalnet");
		userDefinedNewCols.add("totalnet_usd");
		
		userDefinedNewCols.add("pmtrmk");
		userDefinedNewCols.add("selectedcaseshidden");
		userDefinedHiddenNewCols.add("selectedcaseshidden");
		
		userModifyTD.put("c_receiptamt", "modifyReceiptAmtIqd("
				+ "{c_usdchangedprice},{c_changedprice},{c_paytodlvcheck},{c_receiptamt}, {q_stage}, {q_step},{c_id})");
		userModifyTD.put("c_receiptamt_usd", "modifyReceiptAmtUsd("
				+ "{netamtusd}, {c_receiptamt_usd}, {q_stage}, {q_step},{c_usdchangedprice},{c_changedprice},{c_paytodlvcheck},{c_id})");
		userModifyTD.put("c_shipment_cost", "modifyShipmentCost( {c_changedprice},{c_shipment_cost}, {q_stage}, {q_step},{c_paidinadvance},{c_advancepmtid})");
		userModifyTD.put("status", "modifyStatus({q_stage},{q_step},{c_agentpmtid},"
				+ " {c_changedprice}, {c_priceb4change}, {c_receiptamt}, {c_usdchangedprice},"
				+ " {c_usdpriceb4change}, {c_receiptamt_usd})");
		userModifyTD.put("c_custreceiptnoori", "modifyRecieptNo({c_custreceiptnoori})");
		userModifyTD.put("netamtiqd", "sumNetAmtIqd({netamtiqd}, {c_id})");
		userModifyTD.put("netamtusd", "sumNetAmtUsd({netamtusd}, {c_id})");
		userModifyTD.put("pmtCheckBox", "displayCheckBox({c_id}, {c_custid})");
		userModifyTD.put("c_custreceiptnoori", "modifyReceiptNo({c_custreceiptnoori},{c_id})");
		
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		userDefinedColsMustFill.add("c_pickupagent");
		userDefinedColsMustFill.add("totalnet");
		userDefinedColsMustFill.add("pmtdate");
		userDefinedNewColsDefualtValues.put("c_pickupagent", new String[] {"{pickupAgentAcct}"});
		userDefinedLookups.put("c_custid", "select cust_id, cust_name from kbcustomers ");
		userDefinedLookups.put("c_pickupagent", "select us_id, us_name from kbusers where us_rank='PICKUPAGENT'");
		
		userDefinedNewColsHtmlType.put("c_pickupagent", "DROPLIST");
		userDefinedNewColsHtmlType.put("selectedcases", "TEXT");
		userDefinedReadOnlyNewCols.add("c_pickupagent");
		userDefinedReadOnlyNewCols.add("selectedcases");
		userDefinedReadOnlyNewCols.add("totalnet");
		userDefinedReadOnlyNewCols.add("totalnet_usd");
		
		userDefinedNewColsHtmlType.put("totalnet", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("totalnet_usd", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("credit", "NUMBER_WITH_COMMAS");
		
		userDefinedNewCaption = "دفع مبالغ إلى مندوب أستلام";
		
		//
		
		userDefinedTableHeadersClass = "text-white  bg-gradient-x-blue-grey";
		userDefinedGroupRowClass = "text-white  bg-gradient-x-blue-grey";
		userDefinedGroupSumColStyle = "bg-blue-grey bg-lighten-4";
	}
	
	public String modifyShipmentCost (HashMap<String,String> hashy) {
		StringBuilder html = new StringBuilder("<td style=''>"
				+ "<span id='td-shipment-cost-caseid-"+hashy.get("c_id")+"' data-val='"+hashy.get("c_shipment_cost")+"' style='margin-left:5px;'>");
		String addButton = "";
		double shipmentCost = Double.parseDouble(hashy.get("c_shipment_cost"));
		html.append(numFormat.format(shipmentCost));
		html.append("</span>");
		addButton = ("<a href='javascript:changeShipmentCost("+hashy.get("c_id")+")'><li class=\"fa fa-pencil\"></li></a>");

		html.append(addButton + "</td>");
		return html.toString();
	}
	
	public String displayCheckBox (HashMap<String, String> hashy) {
//		String s = "<td><input type=\"checkbox\"  "
//				+ " id=\"pmtcheck_"+hashy.get("c_id")+"\"  data-single-check-custid-"+hashy.get("c_custid")+" ='"+hashy.get("c_custid")+"' "
//						+ " onclick=\"checkBoxPmtClicked(this, "+hashy.get("c_id")+")\">";
//		s +="</td>";
//		return s;	

		String s = "<td id='td-checkbox-caseid-"+hashy.get("c_id")+"'>";
		if (!caseNeedsConfirmation) {
			s+= "<input type=\"checkbox\" "
				+"id=\"pmtcheck_"+hashy.get("c_id")+"\"  "
						+ "data-single-check-custid-"+hashy.get("c_custid")+" ='"+hashy.get("c_custid")+"' "
						+ "onclick=\"checkBoxPmtClicked(this, "+hashy.get("c_id")+")\">";
		}else {
			s += " <div id='div-of-cases-needs-confirmation-"+hashy.get("c_id")+"' "
					+ " class=\"badge badge-warning\">مطلوب تأكيد مبلغ الوصل</div> "; 
		}
		
		s +="</td>";
		return s;	
	}
	public String modifyReceiptNo  (HashMap<String, String> hashy) {

		String s = "<td caseid='"+hashy.get("c_id")+"' id='"+hashy.get("c_custreceiptnoori")+"'>"+hashy.get("c_custreceiptnoori");
		s +="</td>";
		return s;
	}
	
//	public String sumNetAmt(HashMap<String, String> hashy) {
//		String s = "";
//		double netAmt = Double.parseDouble(hashy.get("netamt"));
//		double netAmtUsd = Double.parseDouble(hashy.get("c_receiptamt_usd"));
//		s +="<td dir='ltr' id = 'td_netamt_"+hashy.get("c_id")+"' data-netval='"+netAmt+"' style='text-align:right'>";
//		s +="<span id = 'td_netamt_usd_"+hashy.get("c_id")+"' data-netval-usd='"+netAmtUsd+"'></span>";
//		s += numFormat.format(netAmt);
//		s +="</td>";
//		totNetAmt +=netAmt;
//		totNetAmtUsd +=netAmtUsd;
//		return s;	
//	}
	public String sumNetAmtIqd(HashMap<String, String> hashy) {
		String s = "";
		double netAmtIqd = Double.parseDouble(hashy.get("netamtiqd"));
		BigDecimal bg = BigDecimal.valueOf(netAmtIqd);
		s +="<td dir='ltr' id = 'td_netamt_iqd_"+hashy.get("c_id")+"' data-netval='"+bg.longValue()
		+"' style='text-align:right'>";
		s += numFormat.format(netAmtIqd);
		s +="</td>";
		totNetAmt +=bg.longValue();
		return s;	
	}
	
	public String sumNetAmtUsd(HashMap<String, String> hashy) {
		String s = "";
		double netAmtUsd = Double.parseDouble(hashy.get("netamtusd"));
		s +="<td dir='ltr' id = 'td_netamt_usd_"+hashy.get("c_id")+"' data-netval='"+netAmtUsd+"' style='text-align:right'>";
		s += numFormat.format(netAmtUsd);
		s +="</td>";
		totNetAmtUsd +=netAmtUsd;
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
		records++;
		return s;	
	}
	
	@Override
	public void initialize(HashMap smartyStateMap){
		int pickUpAgent = Integer.parseInt(replaceVarsinString(" {pickupAgentAcct} ", arrayGlobals).trim());
		boolean checkBoxPayment = false;
		String selectedCasesForPayment = "", selectedCustomerForPayment="";
		
		String selectedAmounToPay = "0", selectedAmounToPayUsd="0";
		Map<String, String[]> parameters = httpSRequest.getParameterMap();
		ArrayList<String> casesToPayList = new ArrayList<String>();
		ArrayList<String> receiptsToPayList = new ArrayList<String>();
		ArrayList<String> customersToPayList = new ArrayList<String>();
		String custRecieptNo ="";
		Utilities ut = new Utilities();
		Connection conn2 = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		userDefinedCaption = "<div class='row'><div class=\"col-sm-2\" "
		+ " style='padding-right: 10px;'>"
		+ "	<div class='position-relative'>"
		+ " <input type='text' id='barcode_checker' class='form-control ps-5 radius-30' placeholder='بحث عن وصل'> <span class='position-absolute top-50 product-show translate-middle-y'>"
		+ "	<i class='bx bx-search'></i></span></div></div>" +
		 "          <div class=\"col-sm-1 offset-8\" style='padding-left: 0px;padding-top: 5px;'>" + 
			"				<label>أختيار الكل</label>" + 
			"			</div>" + 
			"			<div class=\"col-sm-1\" style='padding-right: 0px;padding-top: 7px;'>" + 
			"				<input type='checkbox' onclick='checkAll()' id='checkboxall' />" + 
			"			</div></div>";

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
					if (parameters.containsKey("amount_topay_usd") && parameters.get("amount_topay_usd")!=null
							&& !parameters.get("amount_topay_usd")[0].equalsIgnoreCase("")) {
						selectedAmounToPayUsd = parameters.get("amount_topay_usd")[0];
					}
				}
			}
		}
		try {
			conn2 = mysql.getConn();
			if (!checkBoxPayment) {
				pst = conn2.prepareStatement("select c_custreceiptnoori "
					+ " from p_cases "
					+ " where c_pickupagent =? and c_settled !='FULL' and c_allowcustpay='Y' ");
				pst.setInt(1,pickUpAgent);
				rs = pst.executeQuery();
				while (rs.next()) {
					if (rs.getString("c_custreceiptnoori").equalsIgnoreCase(custRecieptNo))
						repeatedNo.put(custRecieptNo, custRecieptNo);
					custRecieptNo = rs.getString("c_custreceiptnoori");
				}
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
			}else {	// check if the user selected boxes for payment, if yes then get the boxes values
				BigInteger amountToPay =  BigInteger.valueOf(Integer.parseInt(selectedAmounToPay));
				BigInteger amountToPayUsd =  BigInteger.valueOf(Integer.parseInt(selectedAmounToPayUsd));
				userDefinedNewColsDefualtValues.put("totalnet", new String[] {amountToPay+""});
				userDefinedNewColsDefualtValues.put("totalnet_usd", new String[] {amountToPayUsd+""});
				userDefinedNewColsDefualtValues.put("selectedcaseshidden", new String[] {selectedCasesForPayment});
				userDefinedNewColsDefualtValues.put("selectedcases", new String [] {casesToPayList.size()+""} );
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
	
	public String modifyStatus (HashMap<String,String> hashy) {
		return Utilities.getAccountingCaseStatusMessage(
		hashy.get("q_stage"), hashy.get("q_step"), Integer.parseInt(hashy.get("c_agentpmtid")), 
		hashy.get("c_changedprice") , hashy.get("c_priceb4change"), hashy.get("c_receiptamt"), 
		hashy.get("c_usdchangedprice"), hashy.get("c_usdpriceb4change"), hashy.get("c_receiptamt_usd"),0,null);
		
	
	}
	
//	public String modifyReceiptAmt (HashMap<String,String> hashy) {
//		setRecords(getRecords() + 1);
//		String html = "<td dir='ltr' style='text-align:right'>";
//		double goodsCost = Double.parseDouble(hashy.get("c_receiptamt"));
//		if (hashy.get("q_stage").equalsIgnoreCase("CNCL")) {
//			if (hashy.get("c_paidinadvance").equalsIgnoreCase("YES")) {
//				html += numFormat.format(goodsCost);
//			}else {
//				goodsCost = 0;
//				html += " - ";
//			}
//		}else {
//			html += numFormat.format(goodsCost);
//		}
//		html+= "</td>";
//		return html;
//		
//	}
	public String modifyReceiptAmtUsd (HashMap<String,String> hashy) {
		String addButton = "";
		caseNeedsConfirmation = false;
		caseNeedsConfirmation =  Utilities.isCaseNeedsConfirmation(
				hashy.get("c_changedprice"), hashy.get("c_usdchangedprice"), hashy.get("c_paytodlvcheck"));
		double receiptUsd = Double.parseDouble(hashy.get("c_receiptamt_usd"));
		if (hashy.get("q_step").equalsIgnoreCase("FORCE_DLV") && hashy.get("q_stage").equalsIgnoreCase("DLV")) {
			return "<td>"+numFormat.format(receiptUsd)+"</td>";
		}
		if (hashy.get("c_paytodlvcheck").equalsIgnoreCase("Y")) {
			return "<td>"+numFormat.format(receiptUsd)+"</td>";
		}
		String bg = "badge-success";
		String href = "\"javascript:doChangeReceiptAmt("+receiptUsd+", "+hashy.get("c_id")+", 'USD' ,  'حسابات مندوب الأستلام')\"";
		if (caseNeedsConfirmation) {
			bg = " badge-warning";
			href = "\"javascript:changeCanPayFlag("+receiptUsd+", "+hashy.get("c_id")+", 'USD',  'حسابات مندوب الأستلام')\"";
		}
		
		addButton = ("<a id='a-href-check-or-confirm-usd-"+hashy.get("c_id")+"' href="+href+">"
				 +"<div id='badge-caseid-usd-"+hashy.get("c_id")+"' style='margin-right: 5px;' class='badge "+bg+"' style='font-size:0.5rem;'>"
				 + " <i class=\"la la-hand-peace-o\" style='font-size: 1.0rem;'></i></div></a>");
		 
		String html = "<td><div style='display: flex;'>";
		html += "<span id='receipt-amt-usd-"+hashy.get("c_id")+"' data-val='"+receiptUsd+"'>"+ numFormat.format(receiptUsd)+"</span>";	
		html+= addButton+"</div></td>";
		return html;
	  }
	
	public String modifyReceiptAmtIqd (HashMap<String,String> hashy) {
		setRecords(getRecords() + 1);
		String addButton = "";
		double receipt = Double.parseDouble(hashy.get("c_receiptamt"));
		double doubleNumber = receipt/1000;
		int intPart = (int) doubleNumber;
		String bg = "badge-success";
		if (hashy.get("c_paytodlvcheck").equalsIgnoreCase("Y")) {
			return "<td>"+numFormat.format(receipt)+"</td>";
		}
		if (hashy.get("q_step").equalsIgnoreCase("FORCE_DLV") && hashy.get("q_stage").equalsIgnoreCase("DLV")) {
			return "<td>"+numFormat.format(receipt)+"</td>";
		}
		String href = "\"javascript:doChangeReceiptAmt("+receipt+", "+hashy.get("c_id")+", 'IQD' ,  'حسابات مندوب الأستلام')\"";
		if((doubleNumber - intPart) < 1 && (doubleNumber - intPart) != 0.00) {
			if(hashy.get("c_paytodlvcheck").equalsIgnoreCase("N")) {
				bg = " badge-warning";
				href = "\"javascript:changeCanPayFlag("+receipt+", "+hashy.get("c_id")+", 'IQD' , 'حسابات مندوب الأستلام')\"";
			}
		}
		if (caseNeedsConfirmation) {
			bg = " badge-warning";
			href = "\"javascript:changeCanPayFlag("+receipt+", "+hashy.get("c_id")+", 'IQD' , 'حسابات مندوب الأستلام')\"";
		}
		addButton = ("<a id='a-href-check-or-confirm-iqd-"+hashy.get("c_id")+"' href="+href+" >"
			 +"<div id='badge-caseid-iqd-"+hashy.get("c_id")+"' style='margin-right: 5px;' class='badge "+bg+"' style='font-size:0.5rem;'>"
			 + " <i class=\"la la-hand-peace-o\" style='font-size: 1.0rem;'></i></div></a>");
		String html = "<td><div style='display: flex;'>";
		html+= "<span id='receipt-amt-iqd-"+hashy.get("c_id")+"' data-val='"+receipt+"' >"+ numFormat.format(receipt)+"</span>";
		totReceiptAmtIqd += receipt;
		html+= addButton+"</div></td>";
		return html;
	}
	
//	public String modifyShipmentCost (HashMap<String,String> hashy) {
//		String html = "<td style=''>";
//		double shipmentCost = Double.parseDouble(hashy.get("c_shipment_cost"));
//		if (hashy.get("q_stage").equalsIgnoreCase("CNCL") ) { // if returned
//			if (hashy.get("c_paidinadvance").equalsIgnoreCase("YES")) { // paid in advance
//				html += numFormat.format(shipmentCost);
//			}else {
//				shipmentCost = 0;
//				html += " - ";	
//			}
//		}else{
//			if (hashy.get("c_paidinadvance").equalsIgnoreCase("YES") && hashy.get("c_changedprice").equalsIgnoreCase("Y")  ) { // paid in advance and changed price
//				html += "-";
//			}else	
//				html += numFormat.format(shipmentCost);
//		}
//		//totShipmentCost +=  shipmentCost;
//		
//		html+= "</td>";
//		return html;
//	}
	
	
	public String myFooterFunction(String colName) {
		if (colName.equalsIgnoreCase("c_custreceiptnoori") ) {
			if (!errorFlag) {
				long totalDue = totNetAmt - debt;
				return "<td colspan='12' stye='font-size: 15px;' align='center'>"
						+ "<div class='row'>"
							+ "<div class='col-3'>"
							+ " <label>مبلغ الوصولات دينار عراقي</label>"
							+ "</div>"
							+ "<div class='col-3'>"
							+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
							+ " <span dir='ltr' style='text-align:right' id ='totalamountshouldbepaid'> "+numFormat.format(totalDue)+"</span></strong>"
							+ "</div>"
							+ "<div class='col-3'>"
							+ " <label>مبلغ الوصولات دولار أمريكي</label>"
							+ "</div>"
							+ "<div class='col-3'>"
							+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
							+ " <span dir='ltr' style='text-align:right' id ='totalamountshouldbepaid_usd'> "+numFormat.format(totNetAmtUsd)+"</span></strong>"
							+ "</div>"
						+ "</div>"
						+ "<form action=\"?\" id='pickupagent-balance-settle-form' method=\"post\" style=\"display: inline;\" >"
						+ " <input type=\"hidden\" name=\"op\" value=\"new\"><input type=\"hidden\""
						+ " name=\"myClassBean\" value=\"com.app.incomeoutcome.PickUpAgentBalance\">"
						+ "<input type=\"hidden\" name=\"selected_casesto_pay\" value='' id='selected_casesto_pay'>"
						+ "<input type=\"hidden\" name=\"amount_topay\" value='0' id='amount_topay'>"
						+ "<input type=\"hidden\" name=\"amount_topay_usd\" value='0' id='amount_topay_usd'>"
						+ "<button type=\"submit\" class=\"btn btn-danger btn-sm\" name=\"smarty_newformbtn\" value=\"newform\">إدفع الأن</button></form></td>";
			}else {
				return  "<td colspan='2' stye='font-size: 15px;' align='center'>هنالك خطأ في النظام  </td>";
			}
		}else
			return "";
	}
	
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autoCommit) {
		String statusMsg = " تم تسجيل الدفعه ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		int pickUpAgentId = Integer.parseInt(replaceVarsinString(" {pickupAgentAcct} ", arrayGlobals).trim());
		inputMap_ori = filterRequest(rqs);
		ArrayList<String> cidList = new ArrayList<String>();
		
		try {	
			int pickUpAgentIdFromRequest = Integer.parseInt(inputMap_ori.get("c_pickupagent")[0]);
			String rmk = inputMap_ori.get("pmtrmk")[0];
			long totalNet = Long.parseLong(inputMap_ori.get("totalnet")[0]);
			long totalNetUsd = Long.parseLong(inputMap_ori.get("totalnet_usd")[0]);
			if (pickUpAgentIdFromRequest != pickUpAgentId) {
				throw new Exception ("Error, pickup agent in form is ("+pickUpAgentIdFromRequest+") and global pickup agent id is ("+pickUpAgentId+") are not the same");
			}
			if (inputMap_ori.containsKey("selectedcaseshidden") 
					&& inputMap_ori.get("selectedcaseshidden")[0]!=null
					&& !inputMap_ori.get("selectedcaseshidden")[0].equalsIgnoreCase("")) {
				cidList = Utilities.SplitStringToArrayList(inputMap_ori.get("selectedcaseshidden")[0] , ",");
			}
			if (cidList.size()<=0) {
				throw new Exception ("Error, No Receipts Found");
			}
			
			long calpmtAmt = getTotaAmtToPay (conn,cidList );
			if (calpmtAmt != totalNet)
				throw new Exception ("Error, payemnt amount in form is ("+totalNet+"),and calculated amount is ("+calpmtAmt+") are not the same");
			
			
			if (!cidList.isEmpty()) {//make sure u have cases
				String pmtType  = "CASES";
				SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = 
						UtilitiesSafeFinancials.getSafePaymentTypeMetaInfoKbgeneral(conn, "PICKUP_AGENT", "PMTTYPE" , pmtType);
				
				int userId_G = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
				int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
				AccountantBoxBean accountantBoxBean= UtilitiesSafeFinancials.GetAccountantBox(conn, userId_G, branchId_G);
				int accountBoxTransactionId = 0;
				if(safePaymentTypeMetaInfoBean.getSafeImpact() != PaymentImpactOnSafe.NOSAFE) {
					accountBoxTransactionId = UtilitiesSafeFinancials.createAcctBoxTransactions(
							conn, 
							0, 
							"p_fin_transactions".toUpperCase(), 
							safePaymentTypeMetaInfoBean.getSafeImpact(), 
							userId_G, 
							totalNet,
							totalNetUsd,
							branchId_G,
							safePaymentTypeMetaInfoBean.getName() + " - " + "حسابات مندوب الأستلام", 
							"kbusers",
							"us_id", 
							pickUpAgentId,
							"us_name",
							Utilities.getAgentName(conn, pickUpAgentId),
							userId_G);
				}
				StandardTransactionBean standardTransactionBean = new StandardTransactionBean();
				standardTransactionBean.setEntity(FinOperationEntity.PICKUP_AGENT);
				standardTransactionBean.setEntityId(pickUpAgentId);
				standardTransactionBean.setCategory(FinOperationCategory.PMTTYPE);
				standardTransactionBean.setCode(FinOperationCode.valueOf(pmtType));
				standardTransactionBean.setInitiatedInBranchId(branchId_G);
				standardTransactionBean.setWhichScreen("حسابات مندوب الأستلام");
				
				standardTransactionBean.setReceiptsAmtIqd(totalNet);
				standardTransactionBean.setReceiptsAmtUsd(totalNetUsd);
				
				standardTransactionBean.setTransactionAmountIqd(totalNet);
				standardTransactionBean.setTransactionAmountUsd(totalNetUsd);
				
				standardTransactionBean.setAmountPaidActuallyIqd(totalNet);
				standardTransactionBean.setAmountPaidActuallyUsd(totalNetUsd);
				standardTransactionBean.setDebitIqd(0);
				standardTransactionBean.setCreditIqd(0);
				standardTransactionBean.setDebitUsd(0);
				standardTransactionBean.setCreditUsd(0);
				standardTransactionBean.setPayerBox(accountantBoxBean.getBoxId());
				standardTransactionBean.setPayerBoxTransactionId(accountBoxTransactionId);
				standardTransactionBean.setRemarks(rmk);
				int standardStransactionId = 
						UtilitiesStandardFinancials.buildStandardTransaction(
								conn, 
								standardTransactionBean,
								branchId_G, 
								userId_G );
				ps = conn.prepareStatement("update p_cases set c_pickupagentpmtid=? , c_settled='FULL' where c_id = ? and "
						+ "c_settled !='FULL' and c_pickupagentpmtid= 0 and c_pmtid=0 ");
				for (int i =0; i<cidList.size(); i++) {
					ps.setInt(1, standardStransactionId);
					ps.setString(2, cidList.get(i));
					ps.addBatch();
				}
				int noOfCasesUpdate= ps.executeBatch().length;
				
				if(noOfCasesUpdate != cidList.size() || noOfCasesUpdate==0) {
					throw new Exception ("خلل في تسجيل الدفعه, اما عدد الشحنات غير مطابق او صفر");
				}
				conn.commit();
			}
			
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
	
		
	private long getTotaAmtToPay( Connection conn , ArrayList<String>caseList) throws Exception {
		long amt = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String sql = "select ifnull( (sum(c_receiptamt) - sum((c_shipment_cost)) - sum(c_sendmoney)),0)as totdue "
					+ " from  p_cases where c_id in (";
			boolean first = true;
			
			for (String caseid : caseList) {
				if (!first) {
					sql += ",";
				}
				first = false;
				sql +="?";
			}
			sql +=")   ";
			pst = conn.prepareStatement(sql);
			int i =1;
			for (String caseid : caseList) {
				pst.setString(i, caseid);
				i++;
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				amt += rs.getLong("totDue");
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
	
	public double getTotReceiptAmt() {
		return totReceiptAmt;
	}
	public int getRecords() {
		return records;
	}
	public void setRecords(int records) {
		this.records = records;
	}
}