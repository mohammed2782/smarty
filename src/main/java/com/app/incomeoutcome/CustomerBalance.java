package com.app.incomeoutcome;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.cases.CaseInformation;
import com.app.financials.AccountantBoxBean;
import com.app.financials.FinOperationCategory;
import com.app.financials.FinOperationCode;
import com.app.financials.FinOperationEntity;
import com.app.financials.PaymentImpactOnSafe;
import com.app.financials.PaymentType;
import com.app.financials.SafePaymentTypeMetaInfoBean;
import com.app.financials.StandardTransactionBean;
import com.app.financials.UtilitiesFinancials;
import com.app.financials.UtilitiesSafeFinancials;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

/*
 * 
 */
public class CustomerBalance extends CoreMgr{
		private LinkedHashMap <String,String> repeatedNo = new LinkedHashMap<String,String>();
		Utilities util = new Utilities();
		private int records = 0;
		private long debtIqd = 0;
		private long debtUsd = 0;
		private long totReceiptAmtIqd = 0;
		private long totReceiptAmtUsd = 0;
		boolean errorFlag = false;
		private long totNetAmtIqd = 0;
		private long totNetAmtUsd = 0;
		
		private long totNetCasesAmtIqd = 0;
		private long totNetCasesAmtUsd = 0;
		
		private long totCalculatedReceiptsAmtToPayIqd = 0;
		private long totCalculatedReceiptsAmtToPayUsd = 0;
		private boolean caseNeedsConfirmation = false;
		
		public CustomerBalance() {
			records = 0;
			MainSql = "select cust_name, q_rmk, c_agentpmtid, c_created_date_only, concat(c_created_date_only, '<div style=\"float: left;\"><input type=\"checkbox\" "
					+ " onclick=\"checkAllCust(this)\" data-val=\"',c_created_date_only,'\" id=\"check-customer-',c_created_date_only,'\"></div>') as groupingcol,"
					+ " c_priceb4change, c_receiptamt as currentreceiptprice, c_changedprice, c_paidinadvance,c_advancepmtid, "
					+ " '' as selectedcases,"
					+ "'' as selectedcaseshidden, q_stage, q_step,'' as newcustid, '' as status ,'' as pmtCheckBox, "
					+ " 'شحنات سلمت وراجعه فقط' as title, c_mastercustid,c_custreceiptnoori,"
					+ "'' as totamt_iqd, '' as totamt_usd,'' as pmtrmk, '' as pmtdate, "
					+ " date(c_createddt) as c_createddt, c_paytodlvcheck, c_usdchangedprice , "
					+ "concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr, c_id, "
					+ " c_rcv_name , c_rcv_hp1, '' as fromdate, '' as todate, ifnull(cc_branchpmtid,0) as cc_branchpmtid, "
					+ " c_receiptamt, c_shipment_cost, (c_receiptamt - c_shipment_cost)  as netamtiqd , "
					+ " c_receiptamt_usd as netamtusd, c_receiptamt_usd, c_usdpriceb4change, '' as credit_iqd, '' as credit_usd, '' as totalnet_iqd, '' as totalnet_usd, c_branchcode, "
					+ "  case when cc_branchpmtid>0 or c_settled='FULL' or c_pickupagentpmtid>0 then 'flase' else 'true' end as canedit"
					+ " from p_cases "
					+ " join kbcustomers  ON cust_id = c_custid "
					+ " left join kbstate on (st_code = c_rcv_state and st_branch = c_branchcode) "
					+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " left join p_caseschain on (c_id = cc_caseid and cc_frombranch = {userstorecode})"
					+ " where c_mastercustid ={CUSTOMER_ACCOUNT_FIN_G}  "
					+ " and c_allowcustpay='Y'  and c_pmtid=0 and c_pickupagentpmtid=0 "
					+ "   order by c_custreceiptnoori ";
			
			userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
			userDefinedSumCols.add("c_receiptamt");
			userDefinedSumCols.add("c_shipment_cost");
			userDefined_x_panelclass = "account_x_panel";
			userDefinedSumCols.add("netamtiqd");
			userDefinedSumCols.add("netamtusd");
			userDefinedGroupColsOrderBy = "c_created_date_only, c_custreceiptnoori";
			UserDefinedPageRows = 6000;
			
			userDefinedGroupByCol = "groupingcol";
			userDefinedGridCols.add("cust_name");
			userDefinedGridCols.add("c_rcv_hp1");
			userDefinedGridCols.add("addr");
			userDefinedGridCols.add("c_custreceiptnoori");
			userDefinedGridCols.add("c_receiptamt_usd");
			userDefinedGridCols.add("c_receiptamt");
			userDefinedGridCols.add("c_shipment_cost");
			userDefinedGridCols.add("netamtiqd");
			userDefinedGridCols.add("netamtusd");
			userDefinedGridCols.add("status");
			userDefinedGridCols.add("pmtCheckBox");
			//Labels
			userDefinedColLabel.put("pmtCheckBox", " ");
			userDefinedColLabel.put("c_id", "رقم الشحنه");
			userDefinedColLabel.put("c_createddt", "تاريخ الشحنات");
			userDefinedColLabel.put("addr", "العنوان");
			userDefinedColLabel.put("c_rcv_hp1", "هاتف");
			userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
			userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل د.ع");
			userDefinedColLabel.put("c_shipment_cost", "تكلفة الشحن");
			userDefinedColLabel.put("c_mastercustid", "المتجر");
			userDefinedColLabel.put("pmtdate", "تاريخ الدفع");
			userDefinedColLabel.put("pmtrmk", "ملاحظات");
			userDefinedColLabel.put("totamt_iqd", "مبالغ الوصولات المحددة د.ع");
			userDefinedColLabel.put("totamt_usd", "مبالغ الوصولات المحددة $");
			userDefinedColLabel.put("netamtiqd", "الصافي للعميل");
			userDefinedColLabel.put("status", "الحاله");
			userDefinedColLabel.put("fromdate","من تاريخ");
			userDefinedColLabel.put("todate","إلى تاريخ");
			userDefinedColLabel.put("selectedcases", "عدد الوصولات المحددة");
			//userDefinedColLabel.put("c_mastercustid", "العميل");
			userDefinedColLabel.put("totalnet", "المبلغ المسدد للمتجر");
			userDefinedColLabel.put("credit_iqd", "إستقطاع لسداد دين د.ع");
			userDefinedColLabel.put("credit_usd", "إستقطاع لسداد دين$");
			userDefinedColLabel.put("netamtusd", "صافي دولار ");
			userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
			userDefinedColLabel.put("cust_name", "المتجر" );
			userDefinedColLabel.put("totalnet_iqd", "الصافي للعميل د.ع" );
			userDefinedColLabel.put("totalnet_usd", "الصافي للعميل $" );
			userDefinedPageFooterFunction = "myFooterFunction()";
			canNew = true;
			//canFilter = true;
			mainTable = "p_cases";
			keyCol = "";
			
//			userDefinedFilterCols.add("c_mastercustid");
//			userDefinedFilterColsHtmlType.put("c_mastercustid", "MULTILIST");
			//newCols
			userDefinedNewCols.add("c_mastercustid");
			userDefinedNewCols.add("selectedcases");
			userDefinedNewCols.add("totamt_iqd");
			userDefinedNewCols.add("totamt_usd");
			userDefinedNewCols.add("credit_iqd");
			userDefinedNewCols.add("credit_usd");
			userDefinedNewCols.add("totalnet_iqd");
			userDefinedNewCols.add("totalnet_usd");
			userDefinedNewCols.add("pmtrmk");
			userDefinedNewCols.add("selectedcaseshidden");
			userDefinedHiddenNewCols.add("selectedcaseshidden");
			userModifyTD.put("c_receiptamt", "modifyReceiptAmtIqd("
					+ "{c_usdchangedprice},{c_changedprice},{c_paytodlvcheck},{c_receiptamt}, {q_stage}, {q_step},{c_id})");
			//userModifyTD.put("c_receiptamt", "modifyReceiptAmt({c_changedprice},{c_receiptamt}, {q_stage}, {q_step},{c_paidinadvance},{c_advancepmtid})");
			userModifyTD.put("c_shipment_cost", "modifyShipmentCost({c_id}, {c_changedprice},{c_shipment_cost}, {q_stage}, {q_step},{c_paidinadvance},{c_advancepmtid})");
			userModifyTD.put("status",
					"modifyStatus({q_stage}, {q_step}, {c_agentpmtid},  "
					+ " {c_changedprice}, {c_priceb4change}, {currentreceiptprice},"
					+ " {c_usdchangedprice}, {c_receiptamt_usd},{c_usdpriceb4change}, {cc_branchpmtid},"
					+ "{q_rmk}, {c_receiptamt})");
			userModifyTD.put("c_custreceiptnoori", "modifyRecieptNo({c_id},{c_branchcode},{c_custreceiptnoori},{canedit})");
			userModifyTD.put("netamtiqd", "sumNetAmtIqd({netamtiqd}, {c_id})");
			userModifyTD.put("netamtusd", "sumNetAmtUsd({netamtusd}, {c_id})");
			//userModifyTD.put("netamtusd", "({netamtiqd}, {c_id},{netamtusd})");
			userModifyTD.put("c_receiptamt_usd", "modifyReceiptAmtUsd("
					+ "{netamtusd}, {c_receiptamt_usd}, {q_stage}, {q_step},{c_usdchangedprice},{c_changedprice},{c_paytodlvcheck},{c_id})");
			userModifyTD.put("pmtCheckBox", "displayCheckBox({c_id}, {c_mastercustid}, {c_created_date_only},{q_stage},{q_step})");
			
			userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
			userDefinedColsMustFill.add("c_mastercustid");
			userDefinedColsMustFill.add("totamt_iqd");
			userDefinedColsMustFill.add("totamt_usd");
			userDefinedNewColsDefualtValues.put("c_mastercustid", new String[] {"{CUSTOMER_ACCOUNT_FIN_G}"});
			
			userDefinedLookups.put("c_mastercustid", "select mcust_id, mcust_name from kb_mastercustomer "
					+ "where mcust_id={CUSTOMER_ACCOUNT_FIN_G}");

			userDefinedNewColsHtmlType.put("totamt_iqd", "TEXT");
			userDefinedNewColsHtmlType.put("totamt_usd", "TEXT");
			userDefinedNewColsHtmlType.put("c_mastercustid", "DROPLIST");
			userDefinedNewColsHtmlType.put("newcustid", "MULTILIST");
			
			userDefinedNewColsHtmlType.put("pmtrmk", "TEXTAREA");
			userDefinedReadOnlyNewCols.add("totamt_iqd");
			userDefinedReadOnlyNewCols.add("totamt_usd");
			userDefinedReadOnlyNewCols.add("c_mastercustid");
			userDefinedNewColsHtmlType.put("selectedcases", "TEXT");
			userDefinedReadOnlyNewCols.add("selectedcases");
			userDefinedReadOnlyNewCols.add("totalnet_iqd");
			userDefinedReadOnlyNewCols.add("totalnet_usd");
			userDefinedNewColsHtmlType.put("totalnet_iqd", "NUMBER_WITH_COMMAS");
			userDefinedNewColsHtmlType.put("totalnet_usd", "NUMBER_WITH_COMMAS");
			userDefinedNewColsHtmlType.put("credit_iqd", "NUMBER_WITH_COMMAS");
			userDefinedNewColsHtmlType.put("credit_usd", "NUMBER_WITH_COMMAS");
			userDefinedNewColsHtmlType.put("totamt_iqd", "NUMBER_WITH_COMMAS");
			userDefinedNewColsHtmlType.put("totamt_usd", "NUMBER_WITH_COMMAS");
			userDefinedNewCaption = "دفع مستحقات عميل";
			userDefinedMinValMap.put("credit_iqd", "0");
			userDefinedMinValMap.put("credit_usd", "0");
			userDefinedNewFormColNo = 2;

			userDefinedTableHeadersClass = "text-white  bg-gradient-x-cyan";
			userDefinedGroupRowClass = "text-white  bg-gradient-x-cyan";
			userDefinedGroupSumColStyle = "bg-cyan bg-lighten-4";
			
		}
		
		public String modifyRecieptNo  (HashMap<String, String> hashy) {
			String style= "";
			
			if (repeatedNo.containsKey(hashy.get("c_custreceiptnoori")))
				style = "background-color:red";

			StringBuilder sb = new StringBuilder("<td style='"+style+"' caseid='"+hashy.get("c_id")+"' id='"+hashy.get("c_custreceiptnoori")+"'><div style='display:flex;'>");
			sb.append("<div class='col-7'>"+hashy.get("c_custreceiptnoori")+"</div>");
			sb.append("<div class='col-5' style='display:contents;'><button type=\"button\" class=\"btn btn-purple btn-sm\" "
					+ "onclick=\"popitup ('../logistics/editCaseFromStages?caneditfromstage="+hashy.get("canedit")+"&branchidfromstage="+hashy.get("c_branchcode")+""
							+ "&caseidfromstage="+hashy.get("c_id")+"' , '' , 1000 ,600);\"><li class=\"fa fa-pencil\"></li></button></div>");
			sb.append("</div></td>");
			return sb.toString();
		}
		
		public String displayCheckBox (HashMap<String, String> hashy) {
			
			String s = "<td id='td-checkbox-caseid-"+hashy.get("c_id")+"'>";
			if (!caseNeedsConfirmation
					||
					(hashy.get("q_step").equalsIgnoreCase("FORCE_DLV") && hashy.get("q_stage").equalsIgnoreCase("DLV"))) {
				s+= "<input type=\"checkbox\" data-group-date-"+hashy.get("c_created_date_only")+"='"+hashy.get("c_created_date_only")+"' "
					+ " id=\"pmtcheck_"+hashy.get("c_id")+"\" "
							+ "onclick=\"checkBoxPmtClicked(this, "+hashy.get("c_id")+")\">";
			}else {
				s += " <div id='div-of-cases-needs-confirmation-"+hashy.get("c_id")+"' "
						+ " class=\"badge badge-warning\">مطلوب تأكيد مبلغ الوصل</div> "; 
			}
			
			s +="</td>";
			return s;		
		}
			
		public String sumNetAmtIqd(HashMap<String, String> hashy) {
			String s = "";
			double netAmtIqd = Double.parseDouble(hashy.get("netamtiqd"));
			BigDecimal bg = BigDecimal.valueOf(netAmtIqd);
			s +="<td dir='ltr' id = 'td_netamt_iqd_"+hashy.get("c_id")+"' data-netval='"+bg.longValue()
			+"' style='text-align:right'>";
			s += numFormat.format(netAmtIqd);
			s +="</td>";
			totNetAmtIqd +=bg.longValue();
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

		public String modifyReceiptAmtUsd (HashMap<String,String> hashy) {
			String addButton = "";
			caseNeedsConfirmation = false;
			caseNeedsConfirmation =  Utilities.isCaseNeedsConfirmation(
					hashy.get("c_changedprice"), hashy.get("c_usdchangedprice"), hashy.get("c_paytodlvcheck"));
			double receiptUsd = Double.parseDouble(hashy.get("c_receiptamt_usd"));
			if (hashy.get("q_step").equalsIgnoreCase("FORCE_DLV") && hashy.get("q_stage").equalsIgnoreCase("DLV")) {
				if (receiptUsd != 0) {
					return "<td style='"+Utilities.HTML_DOLLAR_COLOR_BG+"'>"+numFormat.format(receiptUsd)+"</td>";
				}else {
					return "<td>"+numFormat.format(receiptUsd)+"</td>";
				}
			}
			if (hashy.get("c_paytodlvcheck").equalsIgnoreCase("Y")) {
				if (receiptUsd != 0) {
					return "<td style='"+Utilities.HTML_DOLLAR_COLOR_BG+"'>"+numFormat.format(receiptUsd)+"</td>";
				}else {
					return "<td>"+numFormat.format(receiptUsd)+"</td>";
				}
			}
			String bg = "badge-success";
			String href = "\"javascript:doChangeReceiptAmt("+receiptUsd+", "+hashy.get("c_id")+", 'USD' ,  'محاسبة العميل')\"";
			if (caseNeedsConfirmation) {
				bg = " badge-warning";
				href = "\"javascript:changeCanPayFlag("+receiptUsd+", "+hashy.get("c_id")+", 'USD',  'محاسبة العميل')\"";
			}
			
			addButton = ("<a id='a-href-check-or-confirm-usd-"+hashy.get("c_id")+"' href="+href+">"
					 +"<div id='badge-caseid-usd-"+hashy.get("c_id")+"' style='margin-right: 5px;' class='badge "+bg+"' style='font-size:0.5rem;'>"
					 + " <i class=\"la la-hand-peace-o\" style='font-size: 1.0rem;'></i></div></a>");
			String html = "";
			if (receiptUsd != 0) {
				html += "<td style='"+Utilities.HTML_DOLLAR_COLOR_BG+"'>";
			}else {
				html += "<td>";
			}
			html +="<div style='display: flex;'>";
			html += "<span id='receipt-amt-usd-"+hashy.get("c_id")+"' data-val='"+receiptUsd+"'>"+ numFormat.format(receiptUsd)+"</span>";	
			html+= addButton+"</div></td>";
			return html;
		  }
		
		
		@Override
		public void initialize(HashMap smartyStateMap){
			String custId_G = replaceVarsinString("{CUSTOMER_ACCOUNT_FIN_G}", arrayGlobals).trim();
			boolean checkBoxPayment = false;
			String selectedCasesForPayment = "";
			Map<String, String[]> parameters = httpSRequest.getParameterMap();
			ArrayList<String> casesToPayList = new ArrayList<String>();
			String custRecieptNo ="";
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
			
			String selectedAmounToPayIqd = "0", selectedAmounToPayUsd = "0";
			for(String parameter : parameters.keySet()) {
				if (!excludeKeyWords.contains(parameter)){
					if (parameter.equalsIgnoreCase("op") && parameters.get("op")!=null && parameters.get("op")[0].equalsIgnoreCase("new")) {
						if (parameters.containsKey("selected_casesto_pay") && parameters.get("selected_casesto_pay")!=null
								&& !parameters.get("selected_casesto_pay")[0].equalsIgnoreCase("")) {
							selectedCasesForPayment = parameters.get("selected_casesto_pay")[0];
							casesToPayList = Utilities.SplitStringToArrayList(selectedCasesForPayment , ",");
							checkBoxPayment = true;
						}
						if (parameters.containsKey("amount_topay_iqd") && parameters.get("amount_topay_iqd")!=null
								&& !parameters.get("amount_topay_iqd")[0].equalsIgnoreCase("")) {
							selectedAmounToPayIqd = parameters.get("amount_topay_iqd")[0];
							selectedAmounToPayUsd = parameters.get("amount_topay_usd")[0];
						}
						if (parameters.containsKey("customers_topay") && parameters.get("customers_topay")!=null
								&& !parameters.get("customers_topay")[0].equalsIgnoreCase("")) {
							userDefinedLookups.put("c_mastercustid", "select cust_id, cust_name from kbcustomers "
									+ " where cust_id = "+custId_G+" ");
						}
					}
				}
			}
			try {
				if (!checkBoxPayment) {
					conn2 = mysql.getConn();
					pst = conn2.prepareStatement("select c_custreceiptnoori from p_cases "
							+ "where c_mastercustid =? and c_allowcustpay='Y' and c_pmtid=0 and c_pickupagentpmtid=0 ");
					pst.setString(1,custId_G);
					rs = pst.executeQuery();
					while (rs.next()) {
						if (rs.getString("c_custreceiptnoori").equalsIgnoreCase(custRecieptNo))
							repeatedNo.put(custRecieptNo, custRecieptNo);
						
						custRecieptNo = rs.getString("c_custreceiptnoori");
					}
					try {rs.close();} catch (Exception e) {}
					try {pst.close();} catch (Exception e) {}
				}else {
					//Iqd
					BigInteger amountToPayIqd =  BigInteger.valueOf(Integer.parseInt(selectedAmounToPayIqd));
					BigInteger debtIqdBig = BigInteger.valueOf(debtIqd);
					BigInteger netIqd = amountToPayIqd.subtract(debtIqdBig);
					//Usd
					BigInteger amountToPayUsd=  BigInteger.valueOf(Integer.parseInt(selectedAmounToPayUsd));
					BigInteger debtUsdBig = BigInteger.valueOf(debtUsd);
					BigInteger netUsd = amountToPayUsd.subtract(debtUsdBig);
					userDefinedNewColsDefualtValues.put("totamt_iqd", new String[] {selectedAmounToPayIqd});
					userDefinedNewColsDefualtValues.put("totamt_usd", new String[] {selectedAmounToPayUsd});
					userDefinedNewColsDefualtValues.put("credit_iqd", new String[] {debtIqd+""});
					userDefinedNewColsDefualtValues.put("credit_usd", new String[] {debtUsd+""});
					userDefinedNewColsDefualtValues.put("totalnet_iqd", new String[] {netIqd+""});
					userDefinedNewColsDefualtValues.put("totalnet_usd", new String[] {netUsd+""});
					userDefinedNewColsDefualtValues.put("selectedcaseshidden", new String[] {selectedCasesForPayment});
					userDefinedNewColsDefualtValues.put("selectedcases", new String [] {casesToPayList.size()+""} );
					userDefinedLookups.put("totalnet_iqd", "!select replace('{totamt_iqd}',  ',', '') - replace('{credit_iqd}',  ',', '') from dual");
					userDefinedLookups.put("totalnet_usd", "!select replace('{totamt_usd}',  ',', '') - replace('{credit_usd}',  ',', '') from dual");
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
			CaseInformation ci = new CaseInformation();
			ci.setRmk(hashy.get("q_rmk"));
			return Utilities.getAccountingCaseStatusMessage(
					hashy.get("q_stage"), hashy.get("q_step"), Integer.parseInt(hashy.get("c_agentpmtid")), 
					hashy.get("c_changedprice") , hashy.get("c_priceb4change"), hashy.get("c_receiptamt"), 
					hashy.get("c_usdchangedprice"), hashy.get("c_usdpriceb4change"), 
					hashy.get("c_receiptamt_usd"),0, ci);
		
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
			String href = "\"javascript:doChangeReceiptAmt("+receipt+", "+hashy.get("c_id")+", 'IQD' ,  'محاسبة العميل')\"";
			if((doubleNumber - intPart) < 1 && (doubleNumber - intPart) != 0.00) {
				if(hashy.get("c_paytodlvcheck").equalsIgnoreCase("N")) {
					bg = " badge-warning";
					href = "\"javascript:changeCanPayFlag("+receipt+", "+hashy.get("c_id")+", 'IQD' , 'محاسبة العميل')\"";
				}
			}
			if (caseNeedsConfirmation) {
				bg = " badge-warning";
				href = "\"javascript:changeCanPayFlag("+receipt+", "+hashy.get("c_id")+", 'IQD' ,  'محاسبة العميل')\"";
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
		
		public String myFooterFunction(String colName) {
			if (colName.equalsIgnoreCase("c_custreceiptnoori") ) {
				if (!errorFlag) {
					double totalDueIqd = totNetAmtIqd - debtIqd;
					double totalDueUsd = totNetAmtUsd - debtUsd;
					return "<td colspan='7' stye='font-size: 15px;' align='center'>"
					+ "<div class='row'>"
					+ "<div class='col-3'>"
					+ " <label>مبلغ الوصولات د.ع</label>"
					+ "</div>"
					+ "<div class='col-3'>"
					+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
					+ " <span dir='ltr' style='text-align:right' id ='totalamountshouldbepaid_iqd'> "+numFormat.format(totNetAmtIqd)+"</span></strong>"
					+ "</div>"
					+ "<div class='col-2'></div>"
					+ "<div class='col-2'>"
					+ " <label>مبلغ الوصولات $</label>"
					+ "</div>"
					+ "<div class='col-2'>"
					+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
					+ " <span dir='ltr' style='text-align:right' id ='totalamountshouldbepaid_usd'> "+numFormat.format(totNetAmtUsd)+"</span></strong>"
					+ "</div>"
					+ "<div class='col-3'>"
					+ " <label>مبلغ الدين د.ع</label>"
					+ "</div>"
					+ "<div class='col-3'>"
					+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
					+ " <span dir='ltr' style='text-align:right' id ='cust-debt'> "+numFormat.format(debtIqd)+"</span></strong>"
					+ "</div>"
					+ "<div class='col-2'></div>"
					+ "<div class='col-2'>"
					+ " <label>مبلغ الدين $</label>"
					+ "</div>"
					+ "<div class='col-2'>"
					+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
					+ " <span dir='ltr' style='text-align:right' id ='cust-debt'> "+numFormat.format(debtUsd)+"</span></strong>"
					+ "</div>"
					+ "<div class='col-3'>"
					+ " <label>الصافي د.ع</label>"
					+ "</div>"
					+ "<div class='col-3'>"
					+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
					+ " <span dir='ltr' style='text-align:right' id ='cust-net'> "+numFormat.format(totalDueIqd)+"</span></strong>"
					+ "</div>"
					+ "<div class='col-2'></div>"
					+ "<div class='col-2'>"
					+ " <label>الصافي $</label>"
					+ "</div>"
					+ "<div class='col-2'>"
					+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
					+ " <span dir='ltr' style='text-align:right' id ='cust-net'> "+numFormat.format(totalDueUsd)+"</span></strong>"
					+ "</div>"
					+ "<form action=\"?\" id='customer-balance-settle-form' method=\"post\" style=\"display: inline;\" >"
					+ " <input type=\"hidden\" name=\"op\" value=\"new\"><input type=\"hidden\""
					+ " name=\"myClassBean\" value=\"com.app.incomeoutcome.CustomerBalance\">"
					+ "<input type=\"hidden\" name=\"selected_casesto_pay\" value='' id='selected_casesto_pay'>"
					+ "<input type=\"hidden\" name=\"amount_topay_iqd\" value='0' id='amount_topay_iqd'>"
					+ "<input type=\"hidden\" name=\"amount_topay_usd\" value='0' id='amount_topay_usd'>"
					+ "<button type=\"submit\" class=\"btn btn-danger btn-sm\" name=\"smarty_newformbtn\" value=\"newform\">إدفع الأن</button></form></td>";
					
				}else {
					return  "<td colspan='2' stye='font-size: 15px;' align='center'>هنالك خطأ في النظام الرجاء الأتصال </td>";
				}
			}else if (colName.equalsIgnoreCase("c_mastercustid") ||  colName.equalsIgnoreCase("c_rcv_name")
					|| colName.equalsIgnoreCase("status") ||  colName.equalsIgnoreCase("pmtCheckBox")) { 
				return "<td></td>";
			}else {
				return "";
			}
		}
		
		/* (non-Javadoc)
		 * @see com.app.core.CoreMgr#doInsert(javax.servlet.http.HttpServletRequest, boolean)
		 */
		@Override
		public String doInsert(HttpServletRequest rqs, boolean autoCommit) {
			String statusMsg = " تم تسجيل الدفعه ";
			PreparedStatement ps = null;
			ResultSet rs = null;
			inputMap_ori = filterRequest(rqs);
			ArrayList<String> cidList = new ArrayList<String>();
			Utilities ut = new Utilities();
			try {
				String rmk = inputMap_ori.get("pmtrmk")[0];
				int masterIdScreen = Integer.parseInt(inputMap_ori.get("c_mastercustid")[0]);
				long totalReceiptsAmtFormIqd = Long.parseLong(inputMap_ori.get("totamt_iqd")[0]);
				long totalReceiptsAmtFormUsd = Long.parseLong(inputMap_ori.get("totamt_usd")[0]);
				long creditIqdForm = Long.parseLong(inputMap_ori.get("credit_iqd")[0]);
				long creditUsdForm = Long.parseLong(inputMap_ori.get("credit_usd")[0]);
				int custId_G = Integer.parseInt(replaceVarsinString(" {CUSTOMER_ACCOUNT_FIN_G} ", arrayGlobals).trim());
				if (masterIdScreen != custId_G) {
					throw new Exception ("Error, customer in form is ("+masterIdScreen+") "
							+ "and global customer id is ("+custId_G+") are not the same");
				}
				if (inputMap_ori.containsKey("selectedcaseshidden") 
						&& inputMap_ori.get("selectedcaseshidden")[0]!=null
						&& !inputMap_ori.get("selectedcaseshidden")[0].equalsIgnoreCase("")) {
					cidList = Utilities.SplitStringToArrayList(inputMap_ori.get("selectedcaseshidden")[0] , ",");
				}					
				if (cidList.size()<=0) {
					throw new Exception ("Error, No Receipts Found");
				}

				checkCasesSelected(conn, custId_G, cidList);
				if (totalReceiptsAmtFormIqd ==0 &&  totalReceiptsAmtFormUsd==0) {
					throw new Exception ("مبلغ الوصولات غير متوفر"); 
				}
				calculateTotaAmtToPay (conn,custId_G, cidList);
				if (totCalculatedReceiptsAmtToPayIqd != totalReceiptsAmtFormIqd || totCalculatedReceiptsAmtToPayUsd != totalReceiptsAmtFormUsd) {
					throw new Exception ("Error, payemnt amount in form is ("+totalReceiptsAmtFormIqd+"),"
							+ "and calculated amount is ("+totCalculatedReceiptsAmtToPayIqd+") IQD , "
									+ " payemnt amount in form is ("+totalReceiptsAmtFormUsd+"),"
									+ "and calculated amount is ("+totCalculatedReceiptsAmtToPayUsd+") USD are not the same");
				}
				
				// do validation for paidAmt must match the calcualted paidAmt and custid must match the global
				if (!cidList.isEmpty()) {//make sure u have cases
					//check if there is no payment made already
					ps = conn.prepareStatement("select c_pmtid, c_pickupagentpmtid from p_cases where c_id = ? ");
					for (int i =0; i<cidList.size(); i++) {
						ps.setString(1, cidList.get(i));
						rs = ps.executeQuery();
						if(rs.next()) {
							if(rs.getInt("c_pmtid")>0 ) {
								throw new Exception("الشحنه رقم "+ cidList.get(i)+", تم المحاسبة عليها مسبقا مع العميل ولهذا تم ايقاف عملية الدفع");
							}
							if(rs.getInt("c_pickupagentpmtid")>0 ) {
								throw new Exception("الشحنه رقم "+ cidList.get(i)+", تم المحاسبة عليها مسبقا مع مندوب الأستلام ولهذا تم ايقاف عملية الدفع");
							}
						}else {
							throw new Exception("الشحنه رقم "+ cidList.get(i)+", لم يتم ايجادها");
						}
						try {rs.close();} catch (Exception e) {}
						ps.clearParameters(); 
					}
					
					long transactionAmtFormIqd = totalReceiptsAmtFormIqd - creditIqdForm;
					long transactionAmtFormUsd = totalReceiptsAmtFormUsd - creditUsdForm;
					
//					if (transactionAmtFormIqd <0 ||  transactionAmtFormUsd<0) {
//						throw new Exception ("مبلغ العملية المالية لا يمكن ان يكون اقل من الصفر"); 
//					}
					String pmtType  = "CASES";
					SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = 
							UtilitiesSafeFinancials.getSafePaymentTypeMetaInfoKbgeneral(conn, "CUSTOMER", "PMTTYPE" , pmtType);
					
					// do validation for paidAmt must match the calcualted paidAmt and custid must match the global
					long debitIqd = 0, creditIqd = 0,  debitUsd = 0 , creditUsd = 0, amtPaidActuallyIqd = 0, amtPaidActuallyUsd = 0;
					if (safePaymentTypeMetaInfoBean.getSafeImpact() == PaymentImpactOnSafe.DEDUCT_SAFE) {
						amtPaidActuallyIqd  = transactionAmtFormIqd;
						amtPaidActuallyUsd  = transactionAmtFormUsd;
						creditUsd = totalReceiptsAmtFormUsd - transactionAmtFormUsd;
						creditIqd = totalReceiptsAmtFormIqd - transactionAmtFormIqd;
						
					}else {
						throw new Exception ("Error, Transaction type is not DB, it's "+safePaymentTypeMetaInfoBean.getDbOrCr());
					}
				
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
								amtPaidActuallyIqd,
								amtPaidActuallyUsd,
								branchId_G,
								safePaymentTypeMetaInfoBean.getName() + " - " + "حسابات الزبون", 
								"kb_mastercustomer",
								"mcust_id", 
								custId_G,
								"mcust_name",
								Utilities.getMasterCustomerName(conn, custId_G),
								userId_G);
					}
					StandardTransactionBean standardTransactionBean = new StandardTransactionBean();
					standardTransactionBean.setEntity(FinOperationEntity.CUSTOMER);
					standardTransactionBean.setEntityId(custId_G);
					standardTransactionBean.setCategory(FinOperationCategory.PMTTYPE);
					standardTransactionBean.setCode(FinOperationCode.valueOf(pmtType));
					standardTransactionBean.setInitiatedInBranchId(branchId_G);
					standardTransactionBean.setWhichScreen("حسابات العميل");
					
					standardTransactionBean.setReceiptsAmtIqd(totalReceiptsAmtFormIqd);
					standardTransactionBean.setReceiptsAmtUsd(totalReceiptsAmtFormUsd);
					
					standardTransactionBean.setTransactionAmountIqd(transactionAmtFormIqd);
					standardTransactionBean.setTransactionAmountUsd(transactionAmtFormUsd);
					
					standardTransactionBean.setAmountPaidActuallyIqd(amtPaidActuallyIqd);
					standardTransactionBean.setAmountPaidActuallyUsd(amtPaidActuallyUsd);
					standardTransactionBean.setDebitIqd(debitIqd);
					standardTransactionBean.setCreditIqd(creditIqd);
					standardTransactionBean.setDebitUsd(debitUsd);
					standardTransactionBean.setCreditUsd(creditUsd);
					standardTransactionBean.setPayerBox(accountantBoxBean.getBoxId());
					standardTransactionBean.setPayerBoxTransactionId(accountBoxTransactionId);
					standardTransactionBean.setRemarks(rmk);
					int standardStransactionId = 
							UtilitiesStandardFinancials.buildStandardTransaction(
									conn, 
									standardTransactionBean,
									branchId_G, 
									userId_G );
					ps = conn.prepareStatement("update p_cases set c_pmtid=? , c_settled='FULL' where c_id = ? and c_pmtid = 0 and c_pickupagentpmtid=0 ");
					for (int i =0; i<cidList.size(); i++) {
						ps.setInt(1, standardStransactionId);
						ps.setString(2, cidList.get(i));
						ps.addBatch();
					}
					int noOfCasesUpdate= ps.executeBatch().length;
					if(noOfCasesUpdate != cidList.size() || noOfCasesUpdate==0) {
						throw new Exception ("خلل في تسجيل الدفعه, اما عدد الشحنات غير مطابق او صفر");
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
			
		private void calculateTotaAmtToPay( Connection conn , int masterCustId, ArrayList<String>caseList) throws Exception {
			PreparedStatement pst = null;
			ResultSet rs = null;
			boolean first = true;
			try {
				String sql = "select sum(c_receiptamt - c_shipment_cost) as tot_amt_receipts_to_pay_iqd, sum(c_receiptamt_usd) as tot_amt_receipts_to_pay_usd "
						+ " from  p_cases where c_mastercustid =? and c_allowcustpay='Y' and c_pmtid=0 and c_pickupagentpmtid=0 and c_id in (";
				
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
					totCalculatedReceiptsAmtToPayUsd += rs.getLong("tot_amt_receipts_to_pay_usd");
					totCalculatedReceiptsAmtToPayIqd += rs.getLong("tot_amt_receipts_to_pay_iqd");
				}
			} catch (Exception e) {
				throw e;
			} finally {
				try {rs.close();} catch (Exception e) {/* ignore */}
				try {pst.close();} catch (Exception e) {/* ignore */
				}
			}
		}// end of get_cid
		
		
		private void checkCasesSelected( Connection conn, int a_CustId, ArrayList<String> cidList) throws Exception {
			PreparedStatement pst = null;
			ResultSet rs = null;
			boolean first = true;
			try {
				String sql = "select count(*) as tot, c_mastercustid From p_cases where  c_allowcustpay='Y' "
						+ " and c_pmtid=0 and c_pickupagentpmtid=0 and c_id in ( ";
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
					if (rs.getInt("c_mastercustid") != a_CustId)
						throw new Exception ("لا يتطابق المتجر مع الشحنات المحددة");
					howManyCust ++;
				}
				if (tot != cidList.size())
					throw new Exception ("عدد الوصولات غير متطابق");
				
				if (howManyCust>1)
					throw new Exception ("أكثر من متجر للوصولات المحددة");
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

		public long getDebtIqd() {
			return debtIqd;
		}
		public void setDebtIqd(long debtIqd) {
			this.debtIqd = debtIqd;
		}

		public long getDebtUsd() {
			return debtUsd;
		}
		public void setDebtUsd(long debtUsd) {
			this.debtUsd = debtUsd;
		}
}
