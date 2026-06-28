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
import com.app.financials.SafePaymentTypeMetaInfoBean;
import com.app.financials.StandardTransactionBean;
import com.app.financials.UtilitiesFinancials;
import com.app.financials.UtilitiesSafeFinancials;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;


public class BranchesBalance extends CoreMgr{
	private boolean foundRepeated = false;
	private LinkedHashMap <String,String> repeatedNo = new LinkedHashMap<String,String>();
	Utilities util = new Utilities();
	private int records = 0;
	private int searchedCustid = 0;
	private long totReceiptAmtIqd = 0;
	private long totReceiptAmtUsd = 0;
	private long debtIqd;
	private long debtUsd;
	
	private long totShipmentCost = 0;
	private long totReceiptsNetAmtIqd = 0;
	private long totReceiptsNetAmtUsd = 0;
	private long totMoneySent  = 0;
	
	private long totalCalculatedReceiptsAmtToPayUsd;
	private long totalCalculatedReceiptsAmtToPayIqd;
	private boolean caseNeedsConfirmation = false;
	boolean errorFlag = false;
	boolean branchMustPayAll = true;
	public BranchesBalance() {
		records = 0;
		/*
		 * hashy.get("q_stage"), hashy.get("q_step"), Integer.parseInt(hashy.get("c_agentpmtid")), 
		hashy.get("c_changedprice") , hashy.get("c_priceb4change"), hashy.get("currentreceiptprice"), 
		hashy.get("c_usdchangedprice"), hashy.get("c_usdpriceb4change"), hashy.get("c_receiptamt_usd"));
		 */
		MainSql = "select q_rmk, c_agentpmtid,  concat(date(c_createddt), "
				+ "'<div style=\"float: left;\"><input type=\"checkbox\" "
		+ " onclick=\"checkAllDate(this);\"  attr-value=\"',date(c_createddt),'\" id=\"check-date-',date(c_createddt),'\"></div>') as groupingcol, "
		+ "c_created_date_only, q_previous_rmk, c_specialcase, cc_id, "
		+ "'' as net_to_be_paid_iqd, '' as net_to_be_paid_usd, '' as olddebt_iqd, '' as olddebt_usd, '' as totshipmentcost, "
		+ "'' as selectedcases,'' as selectedcaseshidden, q_stage, q_step,'' status ,"
		+ " '' as pmtCheckBox,  'شحنات سلمت وراجعه فقط' as title, c_custid,c_custreceiptnoori,"
		+ " '' as total_selected_receipts_amt_iqd, '' as total_selected_receipts_amt_usd, "
		+ " '' as pmtrmk, '' as pmtdate, c_paytodlvcheck, c_usdpriceb4change,  "
		+ " date(c_createddt) as c_createddt , concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr, c_id, "
		+ " c_rcv_hp1, '' as fromdate, '' as todate, c_changedprice, c_usdchangedprice, c_priceb4change, "
		+ " c_receiptamt, c_receiptamt_usd, cc_frombranch, cc_tobranch, c_rmk, cust_name, "
		+ " round(case when (cc_pathcost>0)  then (c_receiptamt -  cc_pathcost) "
		+ " else  (c_receiptamt - c_shipment_cost)  end) as netamt_iqd,"
		+ " c_receiptamt_usd as netamt_usd, c_shipment_cost as shipment_cost_from_customer, "
		+ " (case when (cc_pathcost>0) then cc_pathcost else c_shipment_cost end) as c_shipment_cost, c_changed_receiptprice_after_dlv"
		+ " from p_cases  "
		+ " left join kbstate on st_code = c_rcv_state and st_branch = {userstorecode}"
		+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
		+ " join kbcustomers on (cust_id = c_custid)"
		+ " join p_caseschain "
		+ " on (c_id = cc_caseid and cc_frombranch = {BRANCH_TO_PAY_TO_G} and cc_tobranch = {userstorecode} and cc_branchpmtid=0 and cc_branchrecievedpmt='N') "
		+ "	where q_stage = 'DLV'  "; 

		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
		userDefinedFilterColsHtmlType.put("c_custid", "DROPLIST");
		
		userDefinedSumCols.add("c_receiptamt");
		userDefinedSumCols.add("c_receiptamt_usd");
		userDefinedSumCols.add("c_shipment_cost");
		
		userDefinedSumCols.add("netamt_iqd");
		userDefinedSumCols.add("netamt_usd");
		userDefinedGroupColsOrderBy = "c_created_date_only";
		
		UserDefinedPageRows = 5000;
		groupSumCaption = "المجموع";
		userDefined_x_panelclass = "account_x_panel";
		userDefinedGroupByCol = "groupingcol";
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("c_receiptamt");
		
		userDefinedGridCols.add("c_shipment_cost");
		
		userDefinedGridCols.add("netamt_iqd");
		userDefinedGridCols.add("netamt_usd");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("status");
		userDefinedGridCols.add("pmtCheckBox");
		
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("pmtCheckBox", "دفع");
		userDefinedColLabel.put("c_id", "رقم الشحنه");
		userDefinedColLabel.put("c_createddt", "تاريخ الشحنات");
		userDefinedColLabel.put("c_rcv_name", "إسم المستلم");
		userDefinedColLabel.put("netamt_iqd", "صافي د.ع");
		userDefinedColLabel.put("netamt_usd", "صافي $");
		userDefinedColLabel.put("addr", "العنوان");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل د.ع");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
		userDefinedColLabel.put("c_shipment_cost", "تكلفة الشحن");
		userDefinedColLabel.put("c_custid", "المتجر");
		userDefinedColLabel.put("cust_name", "المتجر");
		userDefinedColLabel.put("pmtdate", "تاريخ الدفع");
		userDefinedColLabel.put("pmtrmk", "ملاحظات");
		userDefinedColLabel.put("total_selected_receipts_amt_iqd", "مبلغ الوصولات المحددة د.ع");
		userDefinedColLabel.put("total_selected_receipts_amt_usd", "مبلغ الوصولات المحددة $");
		userDefinedColLabel.put("olddebt_iqd", "ديون سابقة د.ع");
		userDefinedColLabel.put("olddebt_usd", "ديون سابقة $");
		userDefinedColLabel.put("net_to_be_paid_iqd", "المبلغ الواجب تسديده للفرع د.ع");
		userDefinedColLabel.put("net_to_be_paid_usd", "المبلغ الواجب تسديده للفرع $");
		userDefinedColLabel.put("status", "الحاله");
		userDefinedColLabel.put("fromdate","من تاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		userDefinedColLabel.put("selectedcases", "عدد الوصولات");
		userDefinedColLabel.put("cc_frombranch", "محاسبة فرع");
		userDefinedPageFooterFunction = "myFooterFunction()";
		
		userModifyTD.put("c_receiptamt", "modifyReceiptAmtIqd("
				+ "{c_usdchangedprice},{c_changedprice},{c_paytodlvcheck},{c_receiptamt}, {q_stage}, {q_step},{c_id})");
		userModifyTD.put("c_receiptamt_usd", "modifyReceiptAmtUsd("
				+ "{c_receiptamt_usd}, {q_stage}, {q_step},{c_usdchangedprice},{c_changedprice},{c_paytodlvcheck},{c_id})");
		userModifyTD.put("c_shipment_cost", "modifyShipmentCost("
				+ "{shipment_cost_from_customer},{c_shipment_cost}, {q_stage}, {q_step}, {c_shipmentpaidbysender},{cc_id}, {c_id}, {c_specialcase})");
		userModifyTD.put("status", "modifyStatus({q_stage},{q_step},{c_agentpmtid},"
				+ " {c_changedprice}, {c_priceb4change}, {c_receiptamt}, {c_usdchangedprice},"
				+ " {c_usdpriceb4change}, {c_receiptamt_usd},{q_rmk})");
		userModifyTD.put("pmtCheckBox", "displayCheckBox({c_id}, {c_created_date_only},{cc_tobranch},{q_stage},{q_step})");
		userModifyTD.put("c_custreceiptnoori", "modifyReceiptNo({c_custreceiptnoori},{c_id})");
		userModifyTD.put("netamt_iqd", "sumNetAmtIqd({netamt_iqd}, {c_id})");
		userModifyTD.put("netamt_usd", "sumNetAmtUsd({netamt_usd}, {c_id})");
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		userDefinedLookups.put("cc_frombranch", "select branch_id, branch_name from kbbranches");
		
		canNew = true;
		mainTable = "p_cases";
		keyCol = "c_id";
		userDefinedNewCols.add("cc_frombranch");
		userDefinedNewCols.add("selectedcases");
		userDefinedNewCols.add("total_selected_receipts_amt_iqd");
		userDefinedNewCols.add("total_selected_receipts_amt_usd");
		userDefinedNewCols.add("olddebt_iqd");
		userDefinedNewCols.add("olddebt_usd");
		userDefinedNewCols.add("net_to_be_paid_iqd");
		userDefinedNewCols.add("net_to_be_paid_usd");
		userDefinedNewCols.add("pmtrmk");
		userDefinedNewCols.add("selectedcaseshidden");
		
		userDefinedHiddenNewCols.add("selectedcaseshidden");
		//html type new columns
		userDefinedNewColsHtmlType.put("cc_frombranch", "DROPLIST");
		userDefinedNewColsHtmlType.put("selectedcases", "TEXT");
		userDefinedNewColsHtmlType.put("total_selected_receipts_amt_iqd", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("total_selected_receipts_amt_usd", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("olddebt_iqd", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("olddebt_usd", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("net_to_be_paid_iqd", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("net_to_be_paid_usd", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("pmtrmk", "TEXTAREA");
		
		// cols must fill
		userDefinedColsMustFill.add("cc_frombranch");
		userDefinedColsMustFill.add("total_selected_receipts_amt_iqd");
		userDefinedColsMustFill.add("total_selected_receipts_amt_usd");
		userDefinedColsMustFill.add("olddebt_iqd");
		userDefinedColsMustFill.add("olddebt_usd");
		
		//Read only columns
		userDefinedReadOnlyNewCols.add("cc_frombranch");
		userDefinedReadOnlyNewCols.add("total_selected_receipts_amt_iqd");
		userDefinedReadOnlyNewCols.add("total_selected_receipts_amt_usd");
		userDefinedReadOnlyNewCols.add("olddebt_iqd");
		userDefinedReadOnlyNewCols.add("olddebt_usd");
		userDefinedReadOnlyNewCols.add("net_to_be_paid_iqd");
		userDefinedReadOnlyNewCols.add("net_to_be_paid_usd");
		userDefinedReadOnlyNewCols.add("selectedcases");
		userDefinedNewCaption = "دفع مستحقات فرع";
		userDefinedNewFormColNo = 2;
			
		userDefinedTableHeadersClass = "bg-purple bg-lighten-1 white";
	}
	
	public String sumNetAmtIqd(HashMap<String, String> hashy) {
		String s = "";
		double netAmtIqd = Double.parseDouble(hashy.get("netamt_iqd"));
		BigDecimal bg = BigDecimal.valueOf(netAmtIqd);
		s +="<td dir='ltr' id = 'td_netamt_iqd_"+hashy.get("c_id")+"' data-netval='"+bg.longValue()
		+"' style='text-align:right'>";
		s += numFormat.format(netAmtIqd);
		s +="</td>";
		totReceiptsNetAmtIqd +=bg.longValue();
		return s;	
	}
	
	public String sumNetAmtUsd(HashMap<String, String> hashy) {
		String s = "";
		double netAmtUsd = Double.parseDouble(hashy.get("netamt_usd"));
		s +="<td dir='ltr' id = 'td_netamt_usd_"+hashy.get("c_id")+"' data-netval='"+netAmtUsd+"' style='text-align:right'>";
		s += numFormat.format(netAmtUsd);
		s +="</td>";
		totReceiptsNetAmtUsd +=netAmtUsd;
		return s;	
	}
	
	public String displayCheckBox (HashMap<String, String> hashy) {
		String s = "<td id='td-checkbox-caseid-"+hashy.get("c_id")+"'>";
		if (!caseNeedsConfirmation
				||
				(hashy.get("q_step").equalsIgnoreCase("FORCE_DLV") && hashy.get("q_stage").equalsIgnoreCase("DLV"))) {
			if (!branchMustPayAll) {
				s = "<td><input type=\"checkbox\"  id=\"pmtcheck_"+hashy.get("c_id")+"\" data-single-check-date-"+hashy.get("c_created_date_only")+" ='"+hashy.get("c_created_date_only")+"' "
				  + "onclick=\"checkBoxPmtClicked(this, "+hashy.get("c_id")+")\">";
			}else {
				
				s = "<td><input type=\"checkbox\"  id=\"pmtcheck_"+hashy.get("c_id")+"\" "
						+ "checked ='true' onclick=\"return false;\" \">";
			}
		}else {
			s += " <div id='div-of-cases-needs-confirmation-"+hashy.get("c_id")+"' "
					+ " class=\"badge badge-warning\">مطلوب تأكيد مبلغ الوصل</div> "; 
		}
		
		s +="</td>";
		return s;	
	}
	
	
	public String modifyReceiptNo(HashMap<String, String> hashy) {
		String s = "";
		String style= "";
		String data = hashy.get("c_custreceiptnoori");
		if (repeatedNo.containsKey(hashy.get("c_custreceiptnoori"))) {
			style = "background-color:red";
			data +=" أنتبه مشتبه بتكراره ";
			foundRepeated = true;
		}else {
			repeatedNo.put(hashy.get("c_custreceiptnoori"), hashy.get("c_custreceiptnoori"));
		}
		s = "<td  style='"+style+"' caseid='"+hashy.get("c_id")+"' id='"+hashy.get("c_custreceiptnoori")+"'>"+data;
		s +="</td>";		
		return s;		
	}

	@Override
	public void initialize(HashMap smartyStateMap){
		int branchToPayTo_G = Integer.parseInt(replaceVarsinString("{BRANCH_TO_PAY_TO_G}", arrayGlobals).trim());
		boolean checkBoxPayment = false;
		String selectedCasesForPayment = "";
		String selectedReceiptsAmounToPayIqd = "0", selectedReceiptsAmounToPayUsd="0";
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
					if (parameters.containsKey("receipts_amount_topay_iqd") && parameters.get("receipts_amount_topay_iqd")!=null
							&& !parameters.get("receipts_amount_topay_iqd")[0].equalsIgnoreCase("")) {
						selectedReceiptsAmounToPayIqd = parameters.get("receipts_amount_topay_iqd")[0];
					}
					if (parameters.containsKey("receipts_amount_topay_usd") && parameters.get("receipts_amount_topay_usd")!=null
							&& !parameters.get("receipts_amount_topay_usd")[0].equalsIgnoreCase("")) {
						selectedReceiptsAmounToPayUsd = parameters.get("receipts_amount_topay_usd")[0];
					}
				}
			}
		}
		int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		try {
			branchMustPayAll = Utilities.shouldBranchPayAllDues(conn, branchId_G);
		} catch (Exception e) {
			e.printStackTrace();
		}
		userDefinedCaption = ""
				+ "<div class=\"row\">"
				+ "<div class=\"col-sm-2\" "
				+ " style='padding-right: 10px;'>"
				+ "	<div class='position-relative'>"
				+ " <input type='text' id= 'barcode_checker' class='form-control ps-5 radius-30' placeholder='بحث عن وصل'> "
				+ "<span class='position-absolute top-50 product-show translate-middle-y'>"
				+ "	<i class='bx bx-search'></i></span></div></div>" +
				 "          <div class=\"col-sm-1 offset-8\" style='padding-left: 0px;padding-top: 5px;'>" + 
					"				<label>أختيار الكل</label>" + 
					"			</div>"+
					"			<div class=\"col-sm-1\" style='padding-right: 0px;padding-top: 7px;'>" ;
		if (!branchMustPayAll) {
			userDefinedCaption += "<input type='checkbox' onclick='checkAll()' id='checkboxall' />" ;
		}
					
			userDefinedCaption+=	"			</div></div>";
				
userDefinedLookups.put("c_custid", "select cust_id, cust_name from kbcustomers where cust_branch="+branchToPayTo_G);
		
		if (checkBoxPayment) {
			long receiptsAmountToPayIqd = Long.parseLong(selectedReceiptsAmounToPayIqd);
			long receiptsAmountToPayUsd = Long.parseLong(selectedReceiptsAmounToPayUsd);
			long netIqd = 0, netUsd = 0;
			netIqd = receiptsAmountToPayIqd + debtIqd;
			netUsd = receiptsAmountToPayUsd + debtUsd;
			
			userDefinedNewColsDefualtValues.put("cc_frombranch", new String[] {"{BRANCH_TO_PAY_TO_G}"});
			userDefinedNewColsDefualtValues.put("selectedcaseshidden", new String[] {selectedCasesForPayment});
			userDefinedNewColsDefualtValues.put("selectedcases",new String [] {casesToPayList.size()+""} );
			
			userDefinedNewColsDefualtValues.put("total_selected_receipts_amt_iqd", 
					new String[] {receiptsAmountToPayIqd+""});
			userDefinedNewColsDefualtValues.put("total_selected_receipts_amt_usd", 
					new String[] {receiptsAmountToPayUsd+""});
			userDefinedNewColsDefualtValues.put("olddebt_iqd", new String[] {debtIqd+""});
			userDefinedNewColsDefualtValues.put("olddebt_usd", new String[] {debtUsd+""});
			userDefinedNewColsDefualtValues.put("net_to_be_paid_iqd", new String[] {netIqd+""});
			userDefinedNewColsDefualtValues.put("net_to_be_paid_usd", new String[] {netUsd+""});
//			
//			userDefinedColLabel.put("olddebt", "لا يوجد دين سابق");
//			if (debtIqd>0) {
//				userDefinedColLabel.put("olddebt_iqd", "مدين");
//			}else if (debtIqd<0) {
//				userDefinedColLabel.put("olddebt_iqd", "دائن");
//			}
//			
//			if (debtUsd>0) {
//				userDefinedColLabel.put("olddebt_usd", "مدين");
//			}else if (debtUsd<0) {
//				userDefinedColLabel.put("olddebt_usd", "دائن");
//			}
			//userDefinedNewColsDefualtValues.put("totalnet", new String[] {net+""});
		}
		
		super.initialize(smartyStateMap);
	}
	
	public String modifyStatus (HashMap<String,String> hashy) {
		CaseInformation ci = new CaseInformation();
		ci.setRmk(hashy.get("q_rmk"));
		return Utilities.getAccountingCaseStatusMessage(
		hashy.get("q_stage"), hashy.get("q_step"), Integer.parseInt(hashy.get("c_agentpmtid")), 
		hashy.get("c_changedprice") , hashy.get("c_priceb4change"), hashy.get("c_receiptamt"), 
		hashy.get("c_usdchangedprice"), hashy.get("c_usdpriceb4change"), hashy.get("c_receiptamt_usd"),0,ci);
	
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
		String href = "\"javascript:doChangeReceiptAmt("+receipt+", "+hashy.get("c_id")+", 'IQD' ,  'محاسبة الفروع')\"";
		if((doubleNumber - intPart) < 1 && (doubleNumber - intPart) != 0.00) {
			if(hashy.get("c_paytodlvcheck").equalsIgnoreCase("N")) {
				bg = " badge-warning";
				href = "\"javascript:changeCanPayFlag("+receipt+", "+hashy.get("c_id")+", 'IQD' , 'محاسبة الفروع')\"";
			}
		}
		if (caseNeedsConfirmation) {
			bg = " badge-warning";
			href = "\"javascript:changeCanPayFlag("+receipt+", "+hashy.get("c_id")+", 'IQD' ,  'محاسبة الفروع')\"";
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
		String href = "\"javascript:doChangeReceiptAmt("+receiptUsd+", "+hashy.get("c_id")+", 'USD' ,  'محاسبة الفروع')\"";
		if (caseNeedsConfirmation) {
			bg = " badge-warning";
			href = "\"javascript:changeCanPayFlag("+receiptUsd+", "+hashy.get("c_id")+", 'USD',  'محاسبة الفروع')\"";
		}
		
		addButton = ("<a id='a-href-check-or-confirm-usd-"+hashy.get("c_id")+"' href="+href+">"
				 +"<div id='badge-caseid-usd-"+hashy.get("c_id")+"' style='margin-right: 5px;' class='badge "+bg+"' style='font-size:0.5rem;'>"
				 + " <i class=\"la la-hand-peace-o\" style='font-size: 1.0rem;'></i></div></a>");
		 
		String html = "<td><div style='display: flex;'>";
		html += "<span id='receipt-amt-usd-"+hashy.get("c_id")+"' data-val='"+receiptUsd+"'>"+ numFormat.format(receiptUsd)+"</span>";	
		
		totReceiptAmtUsd +=receiptUsd; 
		html+= addButton+"</div></td>";
		return html;
	  }
	 
	public String modifyShipmentCost (HashMap<String,String> hashy) { 
		
		double shipmentCost =Double.parseDouble(hashy.get("c_shipment_cost")); 
		String tdClassColor  = "";
		if (hashy.get("c_specialcase").equalsIgnoreCase("Y")) {
			tdClassColor = "bg-warning";
			
		}
		String html = "<td style='' data-val='"+hashy.get("c_shipment_cost")+"'  class='"+tdClassColor+"' "
				+ "id = 'td-shipment-cost-caseid-"+hashy.get("c_id")+"'"
						+ " data-val-max-allowed='"+hashy.get("shipment_cost_from_customer")+"' >"; 
		if (hashy.get("c_specialcase").equalsIgnoreCase("Y")) {
			
			html += "<a href=\"javascript:changeBranchShareCost("+hashy.get("cc_id")+" , "+hashy.get("c_id")+")\">"
					+ "<i class='fa fa-pencil' "
					+ "style='font-size: 1.1rem;vertical-align: text-bottom;margin-right: 10px;'></i></a>";
		}
		html += numFormat.format(shipmentCost); 
		totShipmentCost +=shipmentCost;
		html+= "</td>"; 
		return html; 
	}
	
	 
	public String myFooterFunction(String colName) {
		if (colName.equalsIgnoreCase("c_custreceiptnoori") ) {
			if (!errorFlag) {
				return "<td colspan='12' stye='font-size: 15px;' align='center'>"
						+ "<div class='row'>"
						// receipts amt IQD
						+ "<div class='col-3'>"
						+ " <label>مبلغ الوصولات المحددة دينار عراقي</label>"
						+ "</div>"
						+ "<div class='col-3'>"
						+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
						+ " <span dir='ltr' style='text-align:right' id ='total_receipts_amount_to_be_paid_iqd'> "+numFormat.format(totReceiptsNetAmtIqd)+"</span></strong>"
						+ "</div>"
						//receipt amt usd
						+ "<div class='col-3'>"
						+ " <label>مبلغ الوصولات المحددة دولار أمريكي $</label>"
						+ "</div>"
						+ "<div class='col-3'>"
						+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
						+ " <span dir='ltr' style='text-align:right' id ='total_receipts_amount_to_be_paid_usd'> "+numFormat.format(totReceiptsNetAmtUsd)+"</span></strong>"
						+ "</div>"
						// debt iqd
						+ "<div class='col-3'>"
						+ " <label>فروقات من دفعات سابقة</label>"
						+ "</div>"
						+ "<div class='col-3'>"
						+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
						+ " <span dir='ltr' style='text-align:right' id ='branch-debt-iqd'> "+numFormat.format(debtIqd)+"</span></strong>"
						+ "</div>"
						//debt usd
						+ "<div class='col-3'>"
						+ " <label>فروقات من دفعات سابقة</label>"
						+ "</div>"
						+ "<div class='col-3'>"
						+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
						+ " <span dir='ltr' style='text-align:right' id ='branch-debt-usd'> "+numFormat.format(debtUsd)+"</span></strong>"
						+ "</div>"
						
						// net iqd
						+ "<div class='col-3'>"
						+ " <label>المطلوب من فرعي د.ع</label>"
						+ "</div>"
						+ "<div class='col-3'>"
						+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
						+ " <span dir='ltr' style='text-align:right' id ='receipts_and_debt_amount_topay_iqd'> "+numFormat.format(debtIqd)+"</span></strong>"
						+ "</div>"
						//net usd
						+ "<div class='col-3'>"
						+ " <label>المطلوب من فرعي $</label>"
						+ "</div>"
						+ "<div class='col-3'>"
						+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
						+ " <span dir='ltr' style='text-align:right' id ='receipts_and_debt_amount_topay_usd'> "+numFormat.format(debtUsd)+"</span></strong>"
						+ "</div>"
						
						+ "<form action=\"?\" id='branch-balance-settle-form' method=\"post\" style=\"display: inline;\" >"
						+ " <input type=\"hidden\" name=\"op\" value=\"new\"><input type=\"hidden\""
						+ " name=\"myClassBean\" value=\"com.app.incomeoutcome.BranchesBalance\">"
						+ "<input type=\"hidden\" name=\"selected_casesto_pay\" value='' id='selected_casesto_pay'>"
						+ "<input type=\"hidden\" name=\"receipts_amount_topay_iqd\" value='0' id='receipts_amount_topay_iqd'>"
						+ "<input type=\"hidden\" name=\"receipts_amount_topay_usd\" value='0' id='receipts_amount_topay_usd'>"
						+ "<button type=\"submit\" class=\"btn btn-danger btn-sm\" name=\"smarty_newformbtn\" value=\"newform\">إدفع الأن</button></form></td>";
			}else {
				return  "<td colspan='2' stye='font-size: 15px;' align='center'>هنالك خطأ في النظام الرجاء الأتصال </td>";
			}
		}else
			return "";
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
			try {
				int branchIdToPayToForm =Integer.parseInt(inputMap_ori.get("cc_frombranch")[0]);
				ArrayList<String> cidList = new ArrayList<String>();
				if (inputMap_ori.containsKey("selectedcaseshidden") 
						&& inputMap_ori.get("selectedcaseshidden")[0]!=null
						&& !inputMap_ori.get("selectedcaseshidden")[0].equalsIgnoreCase("")) {
					cidList = Utilities.SplitStringToArrayList(inputMap_ori.get("selectedcaseshidden")[0] , ",");
				}
				//VALIDATION
				// if cases not found
				if (cidList.size()<=0) {
					throw new Exception ("Error, No Receipts Found");
				}
				// if not the same branch on form
				int receiverBranch_G = Integer.parseInt(replaceVarsinString(" {BRANCH_TO_PAY_TO_G} ", arrayGlobals).trim());
			   if (branchIdToPayToForm!=receiverBranch_G) 
				   throw new Exception
				   		("Error, Branch to pay to in form is ("+branchIdToPayToForm+") and global customer id is ("+receiverBranch_G+") are not the same");
			   
			   // if total receipts money are less than 0
			   long totalSelectedReceiptsAmtIqdForm = Long.parseLong(inputMap_ori.get("total_selected_receipts_amt_iqd")[0]);
				long totalSelectedReceiptsAmtUsdForm = Long.parseLong(inputMap_ori.get("total_selected_receipts_amt_usd")[0]);
			   if (totalSelectedReceiptsAmtIqdForm <0 ||  totalSelectedReceiptsAmtUsdForm<0) {
					throw new Exception ("مبلغ العملية المالية لا يمكن ان يكون اقل من الصفر"); 
				}
			   
			   // do calculate in database to match the numbers from screen
			   int senderBranch_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
			   calculateTotalAmtToPay (conn, senderBranch_G, receiverBranch_G, cidList );
			   if (totalCalculatedReceiptsAmtToPayIqd != totalSelectedReceiptsAmtIqdForm)
					throw new Exception ("Error, receipts amount IQD "
							+ "in form is ("+totalSelectedReceiptsAmtIqdForm+"),and calculated receipts "
									+ "amount is ("+totalCalculatedReceiptsAmtToPayIqd+") are not the same");
			   
			   if (totalCalculatedReceiptsAmtToPayUsd != totalSelectedReceiptsAmtUsdForm)
					throw new Exception ("Error, receipts amount USD "
							+ "in form is ("+totalSelectedReceiptsAmtUsdForm+"),and calculated receipts "
									+ "amount is ("+totalCalculatedReceiptsAmtToPayUsd+") are not the same");

				if (!cidList.isEmpty()) {//make sure u have cases
					//check if there is no payment made already
					ps = conn.prepareStatement("select cc_branchpmtid from p_caseschain where "
							+ " cc_caseid = ? and cc_frombranch=? and cc_tobranch=? ");
					for (int i =0; i<cidList.size(); i++) {
						ps.setString(1, cidList.get(i));
						ps.setInt(2, receiverBranch_G);
						ps.setInt(3, senderBranch_G);
						rs = ps.executeQuery();
						if(rs.next()) {
							if(rs.getInt("cc_branchpmtid")>0) {
								throw new Exception("الشحنه رقم "+ cidList.get(i)+", تم المحاسبة عليها مسبقا مع الفرع ولهذا تم ايقاف عملية الدفع");
							}
						}else {
							throw new Exception("الشحنه رقم "+ cidList.get(i)+", لم يتم ايجادها");
						}
						try {rs.close();} catch (Exception e) {}
						ps.clearParameters(); 
					}
					
					// now start the payment process
					String pmtType  = "CASES";
					SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = 
							UtilitiesSafeFinancials.getSafePaymentTypeMetaInfoKbgeneral(conn, "BRANCH", "PMTTYPE" , pmtType);
					
					if (safePaymentTypeMetaInfoBean.getSafeImpact() != PaymentImpactOnSafe.DEDUCT_SAFE) {
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
								totalSelectedReceiptsAmtIqdForm ,
								totalSelectedReceiptsAmtUsdForm ,
								branchId_G,
								safePaymentTypeMetaInfoBean.getName() + " - " + "حسابات الفروع - دفع مبلغ مالي -", 
								"kbbranches",
								"branch_id", 
								receiverBranch_G,
								"branch_name",
								Utilities.getBranchesInfo(conn, receiverBranch_G+"").get("name"),
								userId_G);
					}
					StandardTransactionBean standardTransactionBean = new StandardTransactionBean();
					standardTransactionBean.setEntity(FinOperationEntity.BRANCH);
					standardTransactionBean.setEntityId(receiverBranch_G);
					standardTransactionBean.setCategory(FinOperationCategory.PMTTYPE);
					standardTransactionBean.setCode(FinOperationCode.valueOf(pmtType));
					standardTransactionBean.setInitiatedInBranchId(branchId_G);
					standardTransactionBean.setWhichScreen("دفع مبالغ للفروع");
					
					standardTransactionBean.setReceiptsAmtIqd(totalSelectedReceiptsAmtIqdForm);
					standardTransactionBean.setReceiptsAmtUsd(totalSelectedReceiptsAmtUsdForm);
					
					standardTransactionBean.setTransactionAmountIqd(totalSelectedReceiptsAmtIqdForm);
					standardTransactionBean.setTransactionAmountUsd(totalSelectedReceiptsAmtUsdForm );
					
					standardTransactionBean.setAmountPaidActuallyIqd(totalSelectedReceiptsAmtIqdForm);
					standardTransactionBean.setAmountPaidActuallyUsd(totalSelectedReceiptsAmtUsdForm );
					
					standardTransactionBean.setPayerBox(accountantBoxBean.getBoxId());
					standardTransactionBean.setPayerBoxTransactionId(accountBoxTransactionId);
					standardTransactionBean.setRemarks(inputMap_ori.get("pmtrmk")[0]);
					int standardStransactionId = 
							UtilitiesStandardFinancials.buildStandardTransaction(
									conn, 
									standardTransactionBean,
									branchId_G, 
									userId_G );
					checkCasesSelected(conn, receiverBranch_G,senderBranch_G , cidList);
					ps = conn.prepareStatement("update p_caseschain   "
							+ " set cc_branchpmtid=? where cc_caseid = ? and cc_frombranch=? "
							+ " and cc_tobranch=? and cc_branchpmtid =0");
					for (int i =0; i<cidList.size(); i++) {
						ps.setInt(1, standardStransactionId);
						ps.setString(2, cidList.get(i));
						ps.setInt(3, receiverBranch_G);
						ps.setInt(4, senderBranch_G);
						ps.addBatch();
					}
					int noOfCasesUpdate= ps.executeBatch().length;
					if(noOfCasesUpdate != cidList.size() || noOfCasesUpdate==0) {
						throw new Exception ("خلل في تسجيل الدفعه");
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
		
	  private void checkCasesSelected(Connection conn, int a_fromBranch, int a_toBranch, ArrayList<String> cidList) throws Exception {
			PreparedStatement pst = null;
			ResultSet rs = null;
			boolean first = true;
			try {
				String sql = "select count(*) as tot from p_caseschain where   "
				+ "  cc_caseid in ( ";
				for (String caseid : cidList) {
					if (!first) {
						sql += ",";
					}
					first = false;
					sql +="?";
				}
				sql +=")  and cc_frombranch=? and  cc_tobranch=? and cc_branchpmtid =0  ";
				pst = conn.prepareStatement(sql);
				int i =1;
				for (String caseid : cidList) {
					pst.setString(i, caseid);
					i++;
				}
				pst.setInt(i++, a_fromBranch);
				pst.setInt(i++, a_toBranch);
				rs = pst.executeQuery();
				
				int tot = 0;
				while (rs.next()) {
					tot = rs.getInt("tot");
				}
				if (tot != cidList.size()) {
					throw new Exception ("عدد الوصولات غير متطابق");
				}
			} catch (Exception e) {
				throw e;
			} finally {
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
			}
		}// end of get_cid
		private void calculateTotalAmtToPay( Connection conn , int payerBranch, int receiverBranch,  ArrayList<String>caseList) throws Exception {
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				String sql = "select "
				+ "ifnull( sum(c_receiptamt) - sum((case when (cc_pathcost>0) then cc_pathcost else c_shipment_cost end)),0)as tot_amt_receipts_to_pay_iqd, "
				+ "ifnull( sum(c_receiptamt_usd),0)as tot_amt_receipts_to_pay_usd "
				+ " from  p_cases "
				+ " join p_caseschain "
				+ " on (c_id = cc_caseid and cc_branchpmtid=0 and cc_branchrecievedpmt='N'"
				+ "  and cc_tobranch=? and cc_frombranch=?) "
				+ " where c_id in (" ;
				boolean first = true;
				
				for (String caseid : caseList) {
					if (!first) {
						sql += ",";
					}
					first = false;
					sql +="?";
				}
				sql +=")  and cc_branchpmtid = 0  ";
				pst = conn.prepareStatement(sql);
				pst.setInt(1, payerBranch);
				pst.setInt(2, receiverBranch);
				int i =3;
				for (String caseid : caseList) {
					pst.setString(i, caseid);
					i++;
				}
				rs = pst.executeQuery();
				if (rs.next()) {
					totalCalculatedReceiptsAmtToPayUsd += rs.getLong("tot_amt_receipts_to_pay_usd");
					totalCalculatedReceiptsAmtToPayIqd += rs.getLong("tot_amt_receipts_to_pay_iqd");
				}
			} catch (Exception e) {
				throw e;
			} finally {
				try {rs.close();} catch (Exception e) {/* ignore */}
				try {pst.close();} catch (Exception e) {/* ignore */
				}
			}
		}// end of get_cid
		
		public int getRecords() {
			return records;
		}
		private void setRecords(int a_recoreds) {
			this.records= a_recoreds;
		}
		public int getSearchedCustid() {
			return searchedCustid;
		}
		public void setSearchedCustid(int searchedCustid) {
			this.searchedCustid = searchedCustid;
		}
		public long getTotReceiptAmtIqd() {
			return totReceiptAmtIqd;
		}

		public void setTotReceiptAmtIqd(long totReceiptAmtIqd) {
			this.totReceiptAmtIqd = totReceiptAmtIqd;
		}

		public long getTotReceiptAmtUsd() {
			return totReceiptAmtUsd;
		}

		public void setTotReceiptAmtUsd(long totReceiptAmtUsd) {
			this.totReceiptAmtUsd = totReceiptAmtUsd;
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

		public long getTotShipmentCost() {
			return totShipmentCost;
		}

		public void setTotShipmentCost(long totShipmentCost) {
			this.totShipmentCost = totShipmentCost;
		}

		public long getTotNetAmtIqd() {
			return totReceiptsNetAmtIqd;
		}

		public void setTotNetAmtIqd(long totNetAmtIqd) {
			this.totReceiptsNetAmtIqd = totNetAmtIqd;
		}

		public long getTotNetAmtUsd() {
			return totReceiptsNetAmtUsd;
		}

		public void setTotNetAmtUsd(long totNetAmtUsd) {
			this.totReceiptsNetAmtUsd = totNetAmtUsd;
		}

		public long getTotMoneySent() {
			return totMoneySent;
		}

		public void setTotMoneySent(long totMoneySent) {
			this.totMoneySent = totMoneySent;
		}

		public boolean isErrorFlag() {
			return errorFlag;
		}

		public void setErrorFlag(boolean errorFlag) {
			this.errorFlag = errorFlag;
		}

		public boolean isFoundRepeated() {
			return foundRepeated;
		}

		public void setFoundRepeated(boolean foundRepeated) {
			this.foundRepeated = foundRepeated;
		}
}
