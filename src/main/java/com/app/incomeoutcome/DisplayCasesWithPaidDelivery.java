package com.app.incomeoutcome;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.financials.AccountantBoxBean;
import com.app.financials.FinOperationCategory;
import com.app.financials.FinOperationCode;
import com.app.financials.FinOperationEntity;
import com.app.financials.PaymentImpactOnSafe;
import com.app.financials.SafePaymentTypeMetaInfoBean;
import com.app.financials.StandardTransactionBean;
import com.app.financials.UtilitiesSafeFinancials;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.util.Utilities;

import smarty.core.CoreMgr;
import smarty.db.mysql;

public class DisplayCasesWithPaidDelivery extends CoreMgr{
	private LinkedHashMap <String,String> repeatedNo = new LinkedHashMap<String,String>();
	Utilities util = new Utilities();
	private int records = 0;
	boolean errorFlag = false;
	private long totNetAmtIqd = 0;
	private long totReceiptAmtIqd = 0;
	private long totCalculatedReceiptsAmtToPayIqd = 0;
	private boolean caseNeedsConfirmation = false;
	public DisplayCasesWithPaidDelivery() {
		MainSql = "select c_agentpmtid, c_created_date_only, us_name, "
		+ " c_priceb4change, c_receiptamt as currentreceiptprice, c_changedprice, c_paidinadvance,c_advancepmtid, "
		+ " '' as selectedcases,"
		+ "'' as selectedcaseshidden, q_stage, q_step,'' as newcustid, '' as status ,'' as pmtCheckBox, "
		+ " 'شحنات سلمت وراجعه فقط' as title, c_custid,c_custreceiptnoori,"
		+ "'' as totamt_iqd, '' as totamt_usd,'' as pmtrmk, '' as pmtdate, "
		+ " date(c_createddt) as c_createddt, c_paytodlvcheck, c_usdchangedprice , "
		+ "concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr, c_id, "
		+ " c_rcv_name , c_rcv_hp1, '' as fromdate, '' as todate, "
		+ " c_receiptamt, c_shipment_cost, (c_receiptamt - c_shipment_cost)  as netamtiqd , "
		+ " c_receiptamt_usd as netamtusd, c_receiptamt_usd, c_usdpriceb4change, '' as credit_iqd, '' as credit_usd, '' as totalnet_iqd, '' as totalnet_usd, c_branchcode "
		+ " from p_cases "
		+ " join kbcustomers on cust_id = c_custid "
		+ " left join kbusers on us_id = c_createdby "
		+ " left join kbstate on (st_code = c_rcv_state and st_branch = c_branchcode) "
		+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
		+ " where c_paid_delivery_cost_in_advance='Y' "
		+ " and c_pmtid=0 and c_pickupagentpmtid=0 and c_settled = 'NO' and c_branchcode={userstorecode} ";
		
		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
		userDefinedSumCols.add("c_receiptamt");
		userDefinedSumCols.add("c_shipment_cost");
		userDefined_x_panelclass = "account_x_panel";
		userDefinedSumCols.add("netamtiqd");
		userDefinedGroupColsOrderBy = "c_created_date_only, c_custreceiptnoori";
		UserDefinedPageRows = 3000;
		
		userDefinedGridCols.add("us_name");
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_shipment_cost");
		userDefinedGridCols.add("netamtiqd");
		userDefinedGridCols.add("status");
		userDefinedGridCols.add("pmtCheckBox");
		//Labels
		userDefinedColLabel.put("us_name", "المدخل");
		userDefinedColLabel.put("pmtCheckBox", " ");
		userDefinedColLabel.put("c_id", "رقم الشحنه");
		userDefinedColLabel.put("c_createddt", "تاريخ الشحنات");
		userDefinedColLabel.put("addr", "العنوان");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل د.ع");
		userDefinedColLabel.put("c_shipment_cost", "تكلفة الشحن");
		userDefinedColLabel.put("c_custid", "المتجر");
		userDefinedColLabel.put("pmtdate", "تاريخ الدفع");
		userDefinedColLabel.put("pmtrmk", "ملاحظات");
		userDefinedColLabel.put("totamt_iqd", "مبالغ الوصولات المحددة د.ع");
		userDefinedColLabel.put("totamt_usd", "مبالغ الوصولات المحددة $");
		userDefinedColLabel.put("netamtiqd", "الصافي للعميل");
		userDefinedColLabel.put("status", "الحاله");
		userDefinedColLabel.put("fromdate","من تاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		userDefinedColLabel.put("selectedcases", "عدد الوصولات المحددة");
		//userDefinedColLabel.put("c_custid", "العميل");
		userDefinedColLabel.put("totalnet", "المبلغ المسدد للمتجر");
		userDefinedColLabel.put("credit_iqd", "إستقطاع لسداد دين د.ع");
		userDefinedColLabel.put("credit_usd", "إستقطاع لسداد دين$");
		userDefinedColLabel.put("netamtusd", "صافي دولار ");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
		
		userDefinedPageFooterFunction = "myFooterFunction()";
		canNew = true;
		//canFilter = true;
		mainTable = "p_cases";
		keyCol = "";
		
		//newCols
		userDefinedNewCols.add("selectedcases");
		userDefinedNewCols.add("totamt_iqd");
		userDefinedNewCols.add("pmtrmk");
		userDefinedNewCols.add("selectedcaseshidden");
		userDefinedHiddenNewCols.add("selectedcaseshidden");
		userModifyTD.put("c_receiptamt", "modifyReceiptAmtIqd("
				+ "{c_usdchangedprice},{c_changedprice},{c_paytodlvcheck},{c_receiptamt}, {q_stage}, {q_step},{c_id})");
		userModifyTD.put("c_shipment_cost", "modifyShipmentCost({c_id}, {c_changedprice},{c_shipment_cost}, {q_stage}, {q_step},{c_paidinadvance},{c_advancepmtid})");
		
		userModifyTD.put("c_custreceiptnoori", "modifyRecieptNo({c_id},{c_branchcode},{c_custreceiptnoori},{canedit})");
		userModifyTD.put("netamtiqd", "sumNetAmtIqd({netamtiqd}, {c_id})");
		userModifyTD.put("c_receiptamt_usd", "modifyReceiptAmtUsd("
				+ "{netamtusd}, {c_receiptamt_usd}, {q_stage}, {q_step},{c_usdchangedprice},{c_changedprice},{c_paytodlvcheck},{c_id})");
		userModifyTD.put("pmtCheckBox", "displayCheckBox({c_id}, {c_custid}, {c_created_date_only})");
		
		userDefinedLookups.put("c_custid", "select cust_id, cust_name from kbcustomers "
				+ "where cust_branch={userstorecode}");
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		userDefinedColsMustFill.add("c_custid");
		userDefinedColsMustFill.add("totamt_iqd");
		userDefinedColsMustFill.add("totamt_usd");
	
		userDefinedNewColsHtmlType.put("totamt_iqd", "TEXT");
		userDefinedNewColsHtmlType.put("totamt_usd", "TEXT");
		userDefinedNewColsHtmlType.put("c_custid", "DROPLIST");
		userDefinedNewColsHtmlType.put("newcustid", "MULTILIST");
		
		userDefinedNewColsHtmlType.put("pmtrmk", "TEXTAREA");
		userDefinedReadOnlyNewCols.add("totamt_iqd");
		userDefinedNewColsHtmlType.put("selectedcases", "TEXT");
		userDefinedReadOnlyNewCols.add("selectedcases");
		userDefinedReadOnlyNewCols.add("totalnet_iqd");
		userDefinedNewColsHtmlType.put("totalnet_iqd", "NUMBER_WITH_COMMAS");
		userDefinedNewCaption = "دفع مستحقات عميل";
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
//		sb.append("<div class='col-5' style='display:contents;'><button type=\"button\" class=\"btn btn-purple btn-sm\" "
//				+ "onclick=\"popitup ('../logistics/editCaseFromStages?caneditfromstage="+hashy.get("canedit")+"&branchidfromstage="+hashy.get("c_branchcode")+""
//						+ "&caseidfromstage="+hashy.get("c_id")+"' , '' , 1000 ,600);\"><li class=\"fa fa-pencil\"></li></button></div>");
		sb.append("</div></td>");
		return sb.toString();
	}
	
	public String displayCheckBox (HashMap<String, String> hashy) {
		String s = "<td id='td-checkbox-caseid-"+hashy.get("c_id")+"'>";
		if (!caseNeedsConfirmation) {
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

	public String modifyReceiptAmtUsd (HashMap<String,String> hashy) {
		caseNeedsConfirmation = false;
//		caseNeedsConfirmation =  Utilities.isCaseNeedsConfirmation(
//				hashy.get("c_changedprice"), hashy.get("c_usdchangedprice"), hashy.get("c_paytodlvcheck"));
		double receiptUsd = Double.parseDouble(hashy.get("c_receiptamt_usd"));
		if (receiptUsd != 0) {
			return "<td style='"+Utilities.HTML_DOLLAR_COLOR_BG+"'>"+numFormat.format(receiptUsd)+"</td>";
		}else {
			return "<td>"+numFormat.format(receiptUsd)+"</td>";
		}
	}
	
	@Override
	public void initialize(HashMap smartyStateMap){
		boolean checkBoxPayment = false;
		String selectedCasesForPayment = "";
		Map<String, String[]> parameters = httpSRequest.getParameterMap();
		ArrayList<String> casesToPayList = new ArrayList<String>();
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
			"	</div></div>";
		String selectedAmounToPayIqd = "0";
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
					}
				}
			}
		}
		if (checkBoxPayment) {
			try {
				calculateTotaAmtToPay(conn, casesToPayList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			userDefinedNewColsDefualtValues.put("totamt_iqd", new String[] {totCalculatedReceiptsAmtToPayIqd+""});
			userDefinedNewColsDefualtValues.put("selectedcaseshidden", new String[] {selectedCasesForPayment});
			userDefinedNewColsDefualtValues.put("selectedcases", new String [] {casesToPayList.size()+""} );
		}
		super.initialize(smartyStateMap);
	}
	
	public String modifyReceiptAmtIqd (HashMap<String,String> hashy) {
		setRecords(getRecords() + 1);
		String addButton = "";
		double receipt = Double.parseDouble(hashy.get("c_receiptamt"));
		double doubleNumber = receipt/1000;
		int intPart = (int) doubleNumber;
		String bg = "badge-success";
		return "<td>"+numFormat.format(receipt)+"</td>";	
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
				double totalDueIqd = 0;
				return "<td colspan='7' stye='font-size: 15px;' align='center'>"
				+ "<div class='row'>"
				+ "<div class='col-3'>"
				+ " <label>مبلغ الوصولات د.ع</label>"
				+ "</div>"
				+ "<div class='col-3'>"
				+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
				+ " <span dir='ltr' style='text-align:right' id ='totalamountshouldbepaid_iqd'> "+numFormat.format(0)+"</span></strong>"
				+ "</div>"
				+ "<div class='col-2'></div>"
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
				
				+ "<form action=\"?\" id='cases-paid-delivery-balance-settle-form' method=\"post\" style=\"display: inline;\" >"
				+ " <input type=\"hidden\" name=\"op\" value=\"new\"><input type=\"hidden\""
				+ " name=\"myClassBean\" value=\"com.app.incomeoutcome.DisplayCasesWithPaidDelivery\">"
				+ "<input type=\"hidden\" name=\"selected_casesto_pay\" value='' id='selected_casesto_pay'>"
				+ "<button type=\"submit\" class=\"btn btn-danger btn-sm\" name=\"smarty_newformbtn\" value=\"newform\">إدفع الأن</button></form></td>";
				
			}else {
				return  "<td colspan='2' stye='font-size: 15px;' align='center'>هنالك خطأ في النظام الرجاء الأتصال </td>";
			}
		}else if (colName.equalsIgnoreCase("c_custid") ||  colName.equalsIgnoreCase("c_rcv_name")
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
		try {
			String rmk = inputMap_ori.get("pmtrmk")[0];
			long totalReceiptsAmtFormIqd = Long.parseLong(inputMap_ori.get("totamt_iqd")[0]);
			
			if (inputMap_ori.containsKey("selectedcaseshidden") 
					&& inputMap_ori.get("selectedcaseshidden")[0]!=null
					&& !inputMap_ori.get("selectedcaseshidden")[0].equalsIgnoreCase("")) {
				cidList = Utilities.SplitStringToArrayList(inputMap_ori.get("selectedcaseshidden")[0] , ",");
			}					
			if (cidList.size()<=0) {
				throw new Exception ("Error, No Receipts Found");
			}

			checkCasesSelected(conn, cidList);
			if (totalReceiptsAmtFormIqd ==0) {
				throw new Exception ("مبلغ الوصولات غير متوفر"); 
			}
			calculateTotaAmtToPay (conn, cidList);
			if (totCalculatedReceiptsAmtToPayIqd != totalReceiptsAmtFormIqd ) {
				throw new Exception ("Error, payemnt amount in form is ("+totalReceiptsAmtFormIqd+"),"
						+ "and calculated amount is ("+totCalculatedReceiptsAmtToPayIqd+") IQD  ");
			}
			
			// do validation for paidAmt must match the calcualted paidAmt and custid must match the global
			if (!cidList.isEmpty()) {//make sure u have cases
				//check if there is no payment made already
				ps = conn.prepareStatement("select c_pmtid from p_cases where c_id = ? ");
				for (int i =0; i<cidList.size(); i++) {
					ps.setString(1, cidList.get(i));
					rs = ps.executeQuery();
					if(rs.next()) {
						if(rs.getInt("c_pmtid")>0) {
							throw new Exception("الشحنه رقم "+ cidList.get(i)+", تم المحاسبة عليها مسبقا مع العميل ولهذا تم ايقاف عملية الدفع");
						}
					}else {
						throw new Exception("الشحنه رقم "+ cidList.get(i)+", لم يتم ايجادها");
					}
					try {rs.close();} catch (Exception e) {}
					ps.clearParameters(); 
				}
				
				long transactionAmtFormIqd =Long.parseLong(inputMap_ori.get("totamt_iqd")[0]);
				String pmtType  = "CASES";
				SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = 
						UtilitiesSafeFinancials.getSafePaymentTypeMetaInfoKbgeneral(conn, "CUSTOMER", "PMTTYPE" , pmtType);
				
				// do validation for paidAmt must match the calcualted paidAmt and custid must match the global
				long amtPaidActuallyIqd = 0, amtPaidActuallyUsd = 0;
				if (safePaymentTypeMetaInfoBean.getSafeImpact() == PaymentImpactOnSafe.DEDUCT_SAFE) {
					amtPaidActuallyIqd  = transactionAmtFormIqd;
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
							"وصولات مدفوعة التوصيل", 
							"",
							"", 
							0,
							"",
							"مدفوعة التوصيل",
							userId_G);
				}
				StandardTransactionBean standardTransactionBean = new StandardTransactionBean();
				standardTransactionBean.setEntity(FinOperationEntity.CUSTOMER);
				standardTransactionBean.setEntityId(0);
				standardTransactionBean.setCategory(FinOperationCategory.PMTTYPE);
				standardTransactionBean.setCode(FinOperationCode.valueOf(pmtType));
				standardTransactionBean.setInitiatedInBranchId(branchId_G);
				standardTransactionBean.setWhichScreen("حسابات وصولات مدفوعة التوصيل");
				
				standardTransactionBean.setReceiptsAmtIqd(totalReceiptsAmtFormIqd);
				standardTransactionBean.setTransactionAmountIqd(transactionAmtFormIqd);
				standardTransactionBean.setAmountPaidActuallyIqd(amtPaidActuallyIqd);
				standardTransactionBean.setPayerBox(accountantBoxBean.getBoxId());
				standardTransactionBean.setPayerBoxTransactionId(accountBoxTransactionId);
				standardTransactionBean.setRemarks(rmk);
				int standardStransactionId = 
						UtilitiesStandardFinancials.buildStandardTransaction(
								conn, 
								standardTransactionBean,
								branchId_G, 
								userId_G );
				ps = conn.prepareStatement("update p_cases set c_pmtid=? ,"
						+ " c_settled='FULL' where c_id = ? and c_pmtid = 0 and c_paid_delivery_cost_in_advance='Y' ");
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
		
	private void calculateTotaAmtToPay( Connection conn , ArrayList<String>caseList) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean first = true;
		try {
			String sql = "select sum(c_receiptamt - c_shipment_cost) as tot_amt_receipts_to_pay_iqd "
					+ " from  p_cases where c_pmtid=0 and c_pickupagentpmtid=0 "
					+ " and c_paid_delivery_cost_in_advance='Y'"
					+ " and c_id in (";
			
			for (String caseid : caseList) {
				if (!first) {
					sql += ",";
				}
				first = false;
				sql +="?";
			}
			sql +=")    ";
			pst = conn.prepareStatement(sql);
			int i =1;
			for (String caseid : caseList) {
				pst.setString(i, caseid);
				i++;
			}
			rs = pst.executeQuery();
			while (rs.next()) {
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
	
	
	private void checkCasesSelected( Connection conn,ArrayList<String> cidList) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean first = true;
		try {
			String sql = "select count(*) as tot, c_custid From p_cases where "
					+ "c_pmtid=0 and c_pickupagentpmtid=0 and c_paid_delivery_cost_in_advance='Y' and c_id in ( ";
			for (String caseid : cidList) {
				if (!first) {
					sql += ",";
				}
				first = false;
				sql +="?";
			}
			sql +=") ";
			pst = conn.prepareStatement(sql);
			int i =1;
			for (String caseid : cidList) {
				pst.setString(i, caseid);
				i++;
			}
			rs = pst.executeQuery();
			int tot = 0;
			while (rs.next()) {
				tot = rs.getInt("tot");
			}
			if (tot != cidList.size())
				throw new Exception ("عدد الوصولات غير متطابق");
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

}