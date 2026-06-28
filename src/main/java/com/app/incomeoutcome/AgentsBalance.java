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

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.cases.CaseInformation;
import com.app.financials.AccountantBoxBean;
import com.app.financials.FinOperationCategory;
import com.app.financials.FinOperationEntity;
import com.app.financials.FinOperationCode;
import com.app.financials.PaymentImpactOnSafe;
import com.app.financials.SafePaymentTypeMetaInfoBean;
import com.app.financials.StandardTransactionBean;
import com.app.financials.UtilitiesFinancials;
import com.app.financials.UtilitiesSafeFinancials;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

public class AgentsBalance extends CoreMgr {
	private int records = 0;
	private double totAgentShareAmt = 0;
	private double totReceiptAmtUsd = 0;
	private double totReceiptAmtIqd = 0;
	private double dlvAgentDebtIqd=0;
	private double dlvAgentDebtUsd=0;
	private int noOfRtnitems = 0;
	private double totDlvRecs = 0;
	boolean errorFlag = false;
	private int rtnItemsPaidByCustomer = 0;
	private int rtnItemsPaidBySender = 0;
	private int dlvItmes =0;
	private int ruralAreaItemsRtn=0;
	private int ruralItemsDlv=0;
	boolean canPay = true;
	private boolean caseNeedsConfirmation = false;
	public AgentsBalance () {
		/*
		 * "q_stage"), hashy.get("q_step"), Integer.parseInt(hashy.get("c_agentpmtid")), 
		hashy.get("c_changedprice") , hashy.get("c_priceb4change"), hashy.get("currentreceiptprice"), 
		hashy.get("c_usdchangedprice"), hashy.get("c_usdpriceb4change"), hashy.get("c_receiptamt_usd")
		 */
		records = 0;
		MainSql = "select q_rmk, c_agentpmtid, c_specialcase, q_previous_rmk, '' actualreceivedamt_iqd, '' as actualreceivedamt_usd, "
				+ " c_changedprice,"
				+ " c_priceb4change ,  c_usdchangedprice, c_usdpriceb4change,  '' nettobecollectedfromagent_iqd, '' as nettobecollectedfromagent_usd , '' as pmtCheckBox, "
				+ " '' as selectedcases,'' as selectedcaseshidden, c_rural, cust_name, q_stage, q_step,'' as status , 'شحنات سلمت وراجعه ' as title, "
				+ "	c_custid,c_custreceiptnoori,'' as totagentshare,'' as pmtrmk, '' as pmtdate, "
				+ " concat('منفيست رقم', dam_id, ', بتاريخ ', dam_manifest_date) as manifestiddate, "
				+ " concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) as addr, c_id,"
				+ " c_rcv_name , c_rcv_hp1, c_receiptamt, c_receiptamt_usd, c_agentshare,  c_shipment_cost , c_assignedagent,  "
				+ " '' as fromdate, '' as todate,"
				+ " st_charges , st_ruralcharges , q_previous_action_taken_by, c_paytodlvcheck "
				+ " from p_cases  "
				+ " left join p_dlvagentmanifest on dam_id = c_dlvagent_manifestid "
				+ " left join kbcustomers on cust_id = c_custid "
				+ " left join kbstate on (st_code = c_rcv_state and st_branch = {userstorecode}) "
				+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
				+ " where c_alllowagentpay = 'Y'  and c_agentsharesettled !='FULL' "
				+ " and c_assignedagent={agentAcct} and c_agentpmtid=0";
		
		
		userDefinedSumCols.add("c_agentshare");
		userDefinedSumCols.add("c_receiptamt");
		userDefinedGroupColsOrderBy = "c_id";
		UserDefinedPageRows = 50000;
		groupSumCaption = "المجموع";
		userDefined_x_panelclass = "account_x_panel";
		
		userDefinedGroupByCol = "manifestiddate";
		userDefinedGridCols.add("cust_name");
		//userDefinedGridCols.add("c_id");
		//userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_rcv_hp1");
		//userDefinedGridCols.add("c_weight");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_agentshare");
		userDefinedGridCols.add("status");
		userDefinedGridCols.add("q_previous_action_taken_by");
		userDefinedGridCols.add("pmtCheckBox");
		
		userDefinedColLabel.put("c_id", "رقم الشحنه");
		userDefinedColLabel.put("q_previous_action_taken_by", "أخر من حدث الحالة");
		userDefinedColLabel.put("c_createddt", "تاريخ الإدخال");
		userDefinedColLabel.put("c_rcv_name", "إسم المستلم");
		userDefinedColLabel.put("c_weight", "الوزن");
		userDefinedColLabel.put("addr", "العنوان");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل د.ع");
		userDefinedColLabel.put("c_receiptamt_usd", "مبلغ الوصل $");
		userDefinedColLabel.put("selectedcases", "أرقام الوصولات");
		userDefinedColLabel.put("pmtCheckBox", " ");
		userDefinedColLabel.put("nettobecollectedfromagent_iqd", "المبلغ المطلوب أستلامه من مندوب التوصيل د.ع");
		userDefinedColLabel.put("nettobecollectedfromagent_usd", "المبلغ المطلوب أستلامه من مندوب التوصيل $");
		userDefinedColLabel.put("actualreceivedamt_iqd", "المبلغ المستلم الفعلي د.ع");
		userDefinedColLabel.put("actualreceivedamt_usd", "المبلغ المستلم الفعلي $");
		
		userDefinedColLabel.put("fromdate", "من تاريخ");
		userDefinedColLabel.put("todate", "الى تاريخ");
		
		userDefinedColLabel.put("c_shipment_cost", "تكلفة الشحن");
		userDefinedColLabel.put("c_assignedagent", "مندوب التوصيل");
		userDefinedColLabel.put("pmtdate", "تاريخ الدفع");
		userDefinedColLabel.put("pmtrmk", "ملاحظات");
		userDefinedColLabel.put("totagentshare", "حصة المندوب");
		userDefinedColLabel.put("netamt", "الصافي لمندوب التوصيل");
		userDefinedColLabel.put("status", "الحاله");
		userDefinedColLabel.put("c_agentshare", "حصة مندوب التوصيل");
		userDefinedColLabel.put("cust_name", "العميل");
		 
		userDefinedNewColsHtmlType.put("fromdate", "DATE");
		userDefinedNewColsHtmlType.put("todate", "DATE");
		userDefinedPageFooterFunction = "myFooterFunction()";
		canNew = true;
		mainTable = "p_cases";
		keyCol = "c_id";
		
		userDefinedNewCols.add("c_assignedagent");
		
		userDefinedNewCols.add("totagentshare");
		userDefinedNewCols.add("nettobecollectedfromagent_iqd");
		userDefinedNewCols.add("nettobecollectedfromagent_usd");
		userDefinedNewCols.add("actualreceivedamt_iqd");
		userDefinedNewCols.add("actualreceivedamt_usd");
		
		userDefinedNewColsHtmlType.put("nettobecollectedfromagent_iqd", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("nettobecollectedfromagent_usd", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("actualreceivedamt_iqd", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("actualreceivedamt_usd", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("totagentshare", "NUMBER_WITH_COMMAS");
		
		userDefinedNewCols.add("pmtrmk");
		userDefinedNewCols.add("selectedcases");
		userDefinedNewCols.add("selectedcaseshidden");
		userDefinedHiddenNewCols.add("selectedcaseshidden");
		userModifyTD.put("c_agentshare", "modifyAgentAmt({c_agentshare},{c_id}, {c_specialcase})");
		userModifyTD.put("status", "modifyStatus({c_agentpmtid},{q_stage}, {q_step},"
				+ " {c_changedprice},{c_priceb4change},{c_receiptamt},"
				+ "{c_usdchangedprice}, {c_usdpriceb4change}, {c_receiptamt_usd},{q_rmk} )");
		userModifyTD.put("c_receiptamt", "modifyReceiptAmtIqd({c_id},{c_paytodlvcheck},{c_receiptamt}, {q_stage}, {q_step},{c_shipment_cost})");
		userModifyTD.put("c_receiptamt_usd", "modifyReceiptAmtUsd("
				+ "{c_id},{c_paytodlvcheck},{c_changedprice},{c_usdchangedprice},{c_receiptamt_usd}, {q_stage}, {q_step})");
		userModifyTD.put("c_custreceiptnoori", "modifyReceiptNo({c_custreceiptnoori},{c_id})");
		
		userDefinedColsMustFill.add("c_assignedagent");
		userDefinedColsMustFill.add("totagentshare");
		userDefinedColsMustFill.add("pmtdate");
		userDefinedColsMustFill.add("actualreceivedamt_iqd");
		userDefinedColsMustFill.add("actualreceivedamt_usd");
		userDefinedNewColsDefualtValues.put("c_assignedagent", new String[] {"{agentAcct}"});
		userDefinedLookups.put("c_assignedagent", "select us_id, us_name from kbusers where us_id='{agentAcct}'");
		userDefinedLookups.put("q_previous_action_taken_by", "select us_id, us_name from kbusers where us_rank not in ('MASTERCUSTOMER')");
		userDefinedNewColsDefualtValues.put("totagentshare", new String [] {"%select "
				+ "(sum(c_agentshare))  "
				+ " from p_cases "
				+ " where c_alllowagentpay = 'Y' "
				+ "and ("
				+ "		 c_paytodlvcheck ='Y'"
				+ "		or ("
				+ "		c_paytodlvcheck ='N' and c_changedprice = 'N' and c_usdchangedprice ='N')  "
				+ "		)"
				+ " and c_agentsharesettled !='FULL' "
				+ "and  c_assignedagent='{agentAcct}' "
				+ "and c_agentpmtid=0  "});
		userDefinedNewColsHtmlType.put("c_assignedagent", "DROPLIST");
		
		userDefinedNewColsHtmlType.put("pmtdate", "DATE");
		userDefinedNewColsHtmlType.put("pmtrmk", "TEXTAREA");
		
		userDefinedReadOnlyNewCols.add("totagentshare");
		userDefinedReadOnlyNewCols.add("nettobecollectedfromagent_iqd");
		userDefinedReadOnlyNewCols.add("nettobecollectedfromagent_usd");
		userDefinedReadOnlyNewCols.add("c_assignedagent");
		userDefinedNewColsHtmlType.put("selectedcases", " ");
		userDefinedReadOnlyNewCols.add("selectedcases");
		userDefinedNewCaption = "محاسبة مندوب توصيل";
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		
		userModifyTD.put("pmtCheckBox", "displayCheckBox({c_id},{q_step},{q_stage})");
		userDefinedNewColsDefualtValues.put("nettobecollectedfromagent_iqd", new String [] {"%select "
				+ "(sum(c_receiptamt)-sum(c_agentshare))  "
				+ " from p_cases "
				+ " where c_alllowagentpay='Y' "
				+ "and ("
				+ "		 c_paytodlvcheck ='Y'"
				+ "		or ("
				+ "		c_paytodlvcheck ='N' and c_changedprice = 'N' and c_usdchangedprice ='N')  "
				+ "		)"
				+ " and c_assignedagent='{agentAcct}' and c_agentpmtid=0  and c_agentsharesettled !='FULL'  "});
		userDefinedNewColsDefualtValues.put("nettobecollectedfromagent_usd", new String [] {"%select "
				+ "(sum(c_receiptamt_usd))  "
				+ " from p_cases "
				+ " where c_alllowagentpay='Y' "
				+ " and c_assignedagent='{agentAcct}' and c_agentpmtid=0  "
				+ "and c_agentsharesettled !='FULL'  "
				+ "and ("
				+ "		 c_paytodlvcheck ='Y'"
				+ "		or ("
				+ "		c_paytodlvcheck ='N' and c_changedprice = 'N' and c_usdchangedprice ='N')  "
				+ "		)"
				+ ""});
		userDefinedFormSizeClass = "col-xl-8 col-md-10 col-sm-12 mx-auto";
		userDefinedNewFormColNo = 2;
		userModifyTD.put("addr", "changeAddress({addr}, {c_id})");
		userDefinedTableHeadersClass = "bg-info bg-darken-3 white";
		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
		userDefinedTableHeadersClass = "text-white  bg-gradient-x-primary";
		userDefinedGroupRowClass = "text-white  bg-gradient-x-primary";
		userDefinedGroupSumColStyle = "bg-primary bg-lighten-4";
	}
	
	public String changeAddress (HashMap<String,String> hashy) {
		StringBuilder sb = new StringBuilder("<td>");
		
		sb.append(hashy.get("addr"));
//		sb.append("<div class='col-3'><button style=\"margin-right:10%;\" "
//				+ " type=\"button\" class=\"btn btn-sm btn-danger\""
//				+ " onclick=\"popitup ('../logistics/updateDistrictFromINIT_NEWINSTORE?caseidfromnewinstore="+hashy.get("c_id")+"' , '' , 1000 ,600);\">تعديل المنطقة</button>"
//			+ "</div>");
		sb.append("<a href=\"javascript::\"  style='float:left' "
				+ "onclick=\"popitup ('../logistics/updateDistrictFromINIT_NEWINSTORE?caseidfromnewinstore="+hashy.get("c_id")+"' , '' , 1000 ,600);\">"
				+ "	<i class=\"fa fa-pencil\" style=\"font-size: 1.1rem;vertical-align: "
				+ "text-bottom;margin-right: 10px;\"></i></a>");
		sb.append("</td>");

		return sb.toString();
	}
	
	@Override
	public void initialize(HashMap smartyStateMap){
		userDefinedCaption = "<div class='row'>"
				+ "<div class=\"col-sm-2\" "
				+ " style='padding-right: 10px;'>"
				+ "	<div class='position-relative'>"
				+ " <input type='text' id ='barcode_checker' class='form-control ps-5 radius-30' placeholder='بحث عن وصل'> <span class='position-absolute top-50 product-show translate-middle-y'>"
				+ "	<i class='bx bx-search'></i></span></div></div>" +
				 "          <div class=\"col-sm-1 offset-8\" style='padding-left: 0px;padding-top: 5px;'>" + 
					"				<label>أختيار الكل</label>" + 
					"			</div>" + 
					"			<div class=\"col-sm-1\" style='padding-right: 0px;padding-top: 7px;'>" + 
					"				<input type='checkbox' onclick='checkAll()' id='checkboxall' />" + 
					"			</div>"
					+ "</div>";
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
		
		int agentId_G = Integer.parseInt(replaceVarsinString(" {agentAcct} ", arrayGlobals).trim());
		Connection conn2 = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn2 = mysql.getConn();
			// check if the user selected boxes for payment, if yes then get the boxes values
			String sql = "select c_custreceiptnoori, "
			+ "	sum((case when (q_stage ='DLV') then c_agentshare else 0 end)) as totAgentShare, "
			+ " sum((case when (q_stage='DLV') then c_receiptamt else 0 end)) as totReceiptsAmtIqd, "
			+ " sum((case when (q_stage='DLV') then c_receiptamt_usd else 0 end)) as totReceiptsAmtUsd "
			+ " from p_cases "
			+ " where c_assignedagent=? and  c_agentsharesettled !='FULL'"
			+ " and ("
			+ "			c_paytodlvcheck ='Y'"
			+ "			or ("
			+ "			c_paytodlvcheck ='N' and c_changedprice = 'N' and c_usdchangedprice ='N'  "
			+ "			)"
			+ "		) "
			+ " and  c_id in (";
			boolean first = true;
			for (String caseid : casesToPayList) {
				if (!first) {
					sql += ",";
				}
				first = false;
				sql +="?";
			}
			sql +=")     group by c_custreceiptnoori ";
			if (checkBoxPayment) {
				pst = conn2.prepareStatement(sql);
				int i =2;
				pst.setInt(1, agentId_G);
				for (String caseid : casesToPayList) {
					pst.setString(i, caseid);
					i++;
				}
				rs = pst.executeQuery();
				long totAgentShare =0;
				long totReceiptsAmtIqd = 0, totReceiptsAmtUsd = 0;
				long netToBeCollectedFromAgentIqd = 0, netToBeCollectedFromAgentUsd=0;
				String [] casesArray = new String [casesToPayList.size()];
				i =0;
				while (rs.next()) {
					totAgentShare += rs.getDouble("totAgentShare");
					totReceiptsAmtIqd += rs.getDouble("totReceiptsAmtIqd");
					totReceiptsAmtUsd += rs.getDouble("totReceiptsAmtUsd");
					casesArray[i] = rs.getString("c_custreceiptnoori");
					i++;
				}
				netToBeCollectedFromAgentIqd = totReceiptsAmtIqd - totAgentShare;
				netToBeCollectedFromAgentUsd = totReceiptsAmtUsd;
				
				userDefinedNewColsDefualtValues.put("totagentshare", new String[] {totAgentShare+""});
				userDefinedNewColsDefualtValues.put("nettobecollectedfromagent_iqd", new String[] {netToBeCollectedFromAgentIqd+""});
				userDefinedNewColsDefualtValues.put("nettobecollectedfromagent_usd", new String[] {netToBeCollectedFromAgentUsd+""});
				userDefinedNewColsDefualtValues.put("selectedcaseshidden", new String[] {selectedCasesForPayment});
				//userDefinedNewColsDefualtValues.put("selectedcases",casesArray );
				
				userDefinedNewCols.remove("fromdate");
				userDefinedNewCols.remove("todate");
//				
//				System.out.println("totAgentShare----->"+totAgentShare);
//				System.out.println("netToBeCollectedFromAgentIqd----->"+netToBeCollectedFromAgentIqd);
//				System.out.println("netToBeCollectedFromAgentUsd----->"+netToBeCollectedFromAgentUsd);
//				System.out.println("selectedCasesForPayment----->"+selectedCasesForPayment);
				//System.out.println("casesArray----->"+casesArray.length);
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
		String s = "<td id='td-checkbox-caseid-"+hashy.get("c_id")+"'>";
		if (!caseNeedsConfirmation || 	(hashy.get("q_step").equalsIgnoreCase("FORCE_DLV") && hashy.get("q_stage").equalsIgnoreCase("DLV"))) {
			s+= "<input type=\"checkbox\" "
				+ " id=\"pmtcheck_"+hashy.get("c_id")+"\" "
						+ "onclick=\"checkBoxPmtClicked(this, "+hashy.get("c_id")+")\">";
		}
		else {
			s += " <div id='div-of-cases-needs-confirmation-"+hashy.get("c_id")+"' "
					+ " class=\"badge badge-warning\">مطلوب تأكيد مبلغ الوصل</div> "; 
		}
		
		s +="</td>";
		return s;	
	}
	
	public String modifyReceiptNo(HashMap<String, String> hashy) {
		String s = "";
		s = "<td  caseid='"+hashy.get("c_id")+"' id='"+hashy.get("c_custreceiptnoori")+"'>"+hashy.get("c_custreceiptnoori");
		s +="</td>";
		
		return s;	
	}
	
//	public String modifyStatus (HashMap<String,String> hashy) {
//		String html = "";
//		String bgColor = "";
//		if (hashy.get("q_stage").equalsIgnoreCase("DLV")  ) {
//			dlvItmes ++;
//			if (hashy.get("c_rural").equalsIgnoreCase("Y"))
//				ruralItemsDlv++;
//			if (hashy.get("q_step").equalsIgnoreCase("DLEIVERD")) {
//				html +="تم التسليم";
//			}else if (hashy.get("q_step").equalsIgnoreCase("SUCC_CHANGEPRICE")) {
//				html +="تم التسليم مع ";
//				html += Utilities.getChangedPriceMessage(
//						hashy.get("c_changedprice")   , hashy.get("c_usdchangedprice"), 
//						hashy.get("c_priceb4change")   , hashy.get("c_receiptamt"),
//						hashy.get("c_usdpriceb4change"), hashy.get("c_receiptamt_usd")
//						);
//				bgColor = "background-color: #c6a8f9";
//			}else if (hashy.get("q_step").equalsIgnoreCase("PART_SUCC")) {
//				html +="تسليم جزئي مع ";
//				html += Utilities.getChangedPriceMessage(
//						hashy.get("c_changedprice")   , hashy.get("c_usdchangedprice"), 
//						hashy.get("c_priceb4change")   , hashy.get("c_receiptamt"),
//						hashy.get("c_usdpriceb4change"), hashy.get("c_receiptamt_usd")
//						);
//				bgColor = "background-color: #ffd2d2";
//			}else if (hashy.get("q_step").equalsIgnoreCase("FORCE_DLV")) {
//				html += "<span class='badge rounded-pill bg-warning text-dark' style='font-size: 14px;'>أعتباره واصل, "+hashy.get("q_previous_rmk")+"</span>";
//			}
//		}else {
//			errorFlag = true;
//			html += "خطأ في النظام"+", "+ hashy.get("q_step");
//			System.out.println("خطأ في النظام agent balance class,modifyStatus method stage=>"+hashy.get("q_stage")+", step=>"+hashy.get("q_step"));
//		}
//		html = "<td style='"+bgColor+"'>"+html;
//		html+= "</td>";
//		
//		return html;
//		
//	}
	
	public String modifyStatus (HashMap<String,String> hashy) {
		CaseInformation ci = new CaseInformation();
		ci.setRmk(hashy.get("q_rmk"));
		return Utilities.getAccountingCaseStatusMessage(
		hashy.get("q_stage"), hashy.get("q_step"), Integer.parseInt(hashy.get("c_agentpmtid")), 
		hashy.get("c_changedprice") , hashy.get("c_priceb4change"), hashy.get("c_receiptamt"), 
		hashy.get("c_usdchangedprice"), hashy.get("c_usdpriceb4change"), hashy.get("c_receiptamt_usd"),0
		, ci);
	}
	
	public String modifyReceiptAmtIqd (HashMap<String,String> hashy) {
		String addButton = "";
		double receipt = Double.parseDouble(hashy.get("c_receiptamt"));
		totReceiptAmtIqd += receipt;
		double doubleNumber = receipt/1000;
		int intPart = (int) doubleNumber;
		String bg = "badge-success";
		if (hashy.get("c_paytodlvcheck").equalsIgnoreCase("Y")) {
			return "<td>"+numFormat.format(receipt)+"</td>";
		}
		
		if (hashy.get("q_step").equalsIgnoreCase("FORCE_DLV") && hashy.get("q_stage").equalsIgnoreCase("DLV")) {
			return "<td>"+numFormat.format(receipt)+"</td>";
		}
		
		String href = "\"javascript:doChangeReceiptAmt("+receipt+", "+hashy.get("c_id")+", 'IQD' , 'حسابات المندوبين' )\"";
		if((doubleNumber - intPart) < 1 && (doubleNumber - intPart) != 0.00) {
			if(hashy.get("c_paytodlvcheck").equalsIgnoreCase("N")) {
				//canPay = false;
				bg = " badge-warning";
				href = "\"javascript:changeCanPayFlag("+receipt+", "+hashy.get("c_id")+", 'IQD' ,  'حسابات المندوبين')\"";
			}
		}
		if (caseNeedsConfirmation) {
			bg = " badge-warning";
			href = "\"javascript:changeCanPayFlag("+receipt+", "+hashy.get("c_id")+", 'IQD' ,  'حسابات المندوبين')\"";
		}
		addButton = ("<a id='a-href-check-or-confirm-iqd-"+hashy.get("c_id")+"' href="+href+" >"
				 +"<div id='badge-caseid-iqd-"+hashy.get("c_id")+"' style='margin-right: 5px;' class='badge "+bg+"' style='font-size:0.5rem;'>"
				 + " <i class=\"la la-hand-peace-o\" style='font-size: 1.0rem;'></i></div></a>");
		String html = "<td><div style='display: flex;'>";
		html+= "<span id='receipt-amt-iqd-"+hashy.get("c_id")+"' data-val='"+receipt+"' >"+ numFormat.format(receipt)+"</span>";
		
		html+= addButton+"</div></td>";
		return html;
		
	}
	
	public String modifyReceiptAmtUsd (HashMap<String,String> hashy) {
		String addButton = "";
		caseNeedsConfirmation = false;
		caseNeedsConfirmation =  Utilities.isCaseNeedsConfirmation(
				hashy.get("c_changedprice"), hashy.get("c_usdchangedprice"), hashy.get("c_paytodlvcheck"));
		double receiptUsd = Double.parseDouble(hashy.get("c_receiptamt_usd"));
		setTotReceiptAmtUsd(getTotReceiptAmtUsd() + receiptUsd);
		if (hashy.get("q_step").equalsIgnoreCase("FORCE_DLV") && hashy.get("q_stage").equalsIgnoreCase("DLV")) {
			return "<td>"+numFormat.format(receiptUsd)+"</td>";
		}
		if (hashy.get("c_paytodlvcheck").equalsIgnoreCase("Y")) {
			return "<td>"+numFormat.format(receiptUsd)+"</td>";
		}
		String bg = "badge-success";
		String href = "\"javascript:doChangeReceiptAmt("+receiptUsd+", "+hashy.get("c_id")+", 'USD' ,  'حسابات المندوبين')\"";
		if (caseNeedsConfirmation) {
			bg = " badge-warning";
			href = "\"javascript:changeCanPayFlag("+receiptUsd+", "+hashy.get("c_id")+", 'USD' ,  'حسابات المندوبين')\"";
		}
		
		addButton = ("<a id='a-href-check-or-confirm-usd-"+hashy.get("c_id")+"' href="+href+">"
				 +"<div id='badge-caseid-usd-"+hashy.get("c_id")+"' style='margin-right: 5px;' class='badge "+bg+"' style='font-size:0.5rem;'>"
				 + " <i class=\"la la-hand-peace-o\" style='font-size: 1.0rem;'></i></div></a>");
		 
		String html = "<td><div style='display: flex;'>";
		html += "<span id='receipt-amt-usd-"+hashy.get("c_id")+"' data-val='"+receiptUsd+"'>"+ numFormat.format(receiptUsd)+"</span>";
		
		
		html+= addButton+"</div></td>";
		return html;
	}

	public String modifyAgentAmt (HashMap<String,String> hashy) {
		setRecords(getRecords() + 1);
		double dueAmt = Double.parseDouble(hashy.get("c_agentshare"));
		totAgentShareAmt +=dueAmt;
		String tdClassColor  = "";
		if (dueAmt <=0) {
			tdClassColor = "bg-warning";
		}else if (hashy.get("c_specialcase").equalsIgnoreCase("Y")) {
			tdClassColor = "bg-warning";
		}
		String html = "<td  "
				+ " id='agent-share-caseid-"+hashy.get("c_id")+"' "
						+ " class='"+tdClassColor+"' data-val='"+hashy.get("c_agentshare")+"'>";
		
		html += numFormat.format(dueAmt);
		html += "<a href=\"javascript:changeAgentShareCost("+hashy.get("c_id")+")\">"
			+ "<i class='fa fa-pencil' "
			+ "style='font-size: 1.1rem;vertical-align: text-bottom;margin-right: 10px;'></i></a>";
		if (hashy.get("c_specialcase").equalsIgnoreCase("Y")) {
			html += "شحنة خاصة";
		}
		
		html+= "</td>";
		return html;	
	}
	
	
	public String myFooterFunction(String colName) {
		if (colName.equalsIgnoreCase("c_custreceiptnoori") ) {
			if (!errorFlag) {
				double amountobereceivedfromagent_iqd = totReceiptAmtIqd - totAgentShareAmt;
				if (amountobereceivedfromagent_iqd <0) amountobereceivedfromagent_iqd = 0;
				String tdStyle= "padding: 15px 10px;";
				String html = 
				"<td colspan='12' style=' border-right: 6px solid white; border-bottom: 0px; border-top: 0px;' align='center'>"
		+"<table class='table-footer-summary'>"
			+"<tr>"
			+"<td style='"+tdStyle+"'><label class='text-white'>حصة مندوب التوصيل</label></td>"
			+"<td style='"+tdStyle+"' class='text-white' ><strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\"> "+numFormat.format(totAgentShareAmt)+" </strong></td>"
			+"<td style='"+tdStyle+"'  class='text-white'><label class='text-white'>المبلغ الكلي للشحنات د.ع</label></td>"
			+"<td style='"+tdStyle+"'  class='text-white'><strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\"> "+numFormat.format(totReceiptAmtIqd)+" </strong></td>"
			+"<td style='"+tdStyle+"'  class='text-white'><label class='text-white'>المبلغ الكلي للشحنات$</label></td>"
			+"<td style='"+tdStyle+"'  class='text-white'><strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\"> "+numFormat.format(totReceiptAmtUsd)+" </strong></td>"		
			+"</tr>"
			
			+ "<tr>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'></td>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'></td>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'><label class='text-white'>صافي مبلغ الوصولات د.ع</label></td>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'  class='text-white'><strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\"> "+numFormat.format(amountobereceivedfromagent_iqd)+" </strong></td>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'><label  class='text-white'>صافي مبلغ الوصولات $</label></td>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'  class='text-white'><strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\"> "+numFormat.format(totReceiptAmtUsd)+" </strong></td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'></td>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'></td>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'><label class='text-white'>ديــون ســـابــقــة د.ع</label></td>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'  class='text-white'><strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\"> "+numFormat.format(this.getDlvAgentDebtIqd())+" </strong></td>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'><label class='text-white'>ديــون ســـابــقــة $</label></td>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'  class='text-white'><strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\"> "+numFormat.format(this.getDlvAgentDebtUsd())+" </strong></td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'></td>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'></td>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'  class='text-white'><label class='text-white' style='font-size:15px;'>المطلوب إستلامه من المندوب د.ع</label></td>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'  class='text-white'><strong style=\"margin-right: 20px;margin-left: 20px; font-size:15px;\"> "+numFormat.format(this.getDlvAgentDebtIqd() + amountobereceivedfromagent_iqd )+" </strong></td>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'  class='text-white'><label class='text-white' style='font-size:15px;'>المطلوب إستلامه من المندوب $</label></td>"
			+ "<td style='"+tdStyle+";border-top: 1px solid #aaa;'  class='text-white'><strong style=\"margin-right: 20px;margin-left: 20px; font-size:15px;\"> "+numFormat.format(this.getDlvAgentDebtUsd() + totReceiptAmtUsd )+" </strong></td>"
			+ "</tr>";

				if(canPay) {
					html += "</table>"
							+ "<form action=\"?\" method=\"post\" class='col-md-3 offset-5' onsubmit=\"checkBoxPmtClicked(event)\"><input type=\"hidden\" name=\"op\" value=\"new\"><input type=\"hidden\""
							+ " name=\"myClassBean\" value=\"com.app.incomeoutcome.AgentsBalance\">"
							+ "<input type=\"hidden\" name=\"selected_casesto_pay\" value='' id='selected_casesto_pay'>"
							+ "<button type=\"submit\" class=\"btn btn-warning btn-lg\" name=\"smarty_newformbtn\" value=\"newform\">محاسبة المندوب</button></form></td>";
				}else {
					html += "<tr><td style='"+tdStyle+"'><button style='margin-right: 150%;margin-left: -200%;' href='#' class=\"btn btn-danger btn-lg\">يرجى التأكيد على مبالغ الوصولات</button></td></tr></table></td>";
				}
				return html;
			}else {
				return "<td colspan='11' stye='font-size: 15px;' align='center'>هنالك خطأ في النظام , لايمكن اجراء الدفع الأن</td>";
			}
		}else if(colName.equalsIgnoreCase("c_receiptamt") || colName.equalsIgnoreCase("c_shipment_cost"))
			return "";
		else
			return "";
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
	
	/* (non-Javadoc)
	 * @see com.app.core.CoreMgr#doInsert(javax.servlet.http.HttpServletRequest, boolean)
	 */
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autoCommit) {
		String statusMsg = " تم تسجيل الدفعه ";
		PreparedStatement ps = null;
		try {
			inputMap_ori = filterRequest(rqs);
			String rmk = inputMap_ori.get("pmtrmk")[0];
			int agentIdFromScreen = Integer.parseInt(inputMap_ori.get("c_assignedagent")[0]);
			int agentId_G = Integer.parseInt(replaceVarsinString(" {agentAcct} ", arrayGlobals).trim());
			if (agentIdFromScreen != agentId_G) {
				throw new Exception ("ليس نفس المندوب");
			}
			//get list of cids
			ArrayList<String> cidList = new  ArrayList<String>();
			if (inputMap_ori.containsKey("selectedcaseshidden") 
					&& inputMap_ori.get("selectedcaseshidden")[0]!=null
					&& !inputMap_ori.get("selectedcaseshidden")[0].equalsIgnoreCase("")) {
				cidList = Utilities.SplitStringToArrayList(inputMap_ori.get("selectedcaseshidden")[0] , ",");
				
			}
			
			if (!cidList.isEmpty()) {//make sure u have cases
				if (!areTheCasesSelectedStillTheSame(conn, agentId_G, cidList)) {
					throw new Exception ("حصل تغيير في الشحنات ولذلك لحمايتك تم الغاء العملية المالية");
				}
				long totAgentShare = (long) Double.parseDouble(inputMap_ori.get("totagentshare")[0]);
				long netToBeCollectedFromAgentIqd = (long) Double.parseDouble(inputMap_ori.get("nettobecollectedfromagent_iqd")[0]);
				long netToBeCollectedFromAgentUsd = (long) Double.parseDouble(inputMap_ori.get("nettobecollectedfromagent_usd")[0]);
				long actualReceivedAmtIqd = (long) Double.parseDouble(inputMap_ori.get("actualreceivedamt_iqd")[0]);
				long actualReceivedAmtUsd = (long) Double.parseDouble(inputMap_ori.get("actualreceivedamt_usd")[0]);
				long remainingAmtIqd = netToBeCollectedFromAgentIqd -  actualReceivedAmtIqd;
				long remainingAmtUsd = netToBeCollectedFromAgentUsd -  actualReceivedAmtUsd;

//				if (actualReceivedAmtIqd ==0 &&  actualReceivedAmtUsd==0) 
//					throw new Exception ("مبلغ العملية المالية غير متوفر"); 
				
				String pmtType = "CASES";
				SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = 
						UtilitiesSafeFinancials.getSafePaymentTypeMetaInfoKbgeneral(conn, "AGENT", "PMTTYPE" , pmtType);
				
				long debitIqd = 0, creditIqd = 0,  debitUsd = 0 , creditUsd = 0;
				if (remainingAmtIqd<0) { // means he paid more than what he owns, so it's credit
					creditIqd = remainingAmtIqd*-1;
				}else if (remainingAmtIqd>0) {
					debitIqd = remainingAmtIqd;
				}
				
				if (remainingAmtUsd<0) { // means he paid more than what he owns, so it's credit
					creditUsd = remainingAmtUsd*-1;
				}else if (remainingAmtUsd>0) {
					debitUsd = remainingAmtUsd;
				}
				int userId_G = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
				int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
				AccountantBoxBean accountantBoxBean= UtilitiesSafeFinancials.GetAccountantBox(conn, userId_G, branchId_G);
				int accountBoxTransactionId = 0;
			
				if(safePaymentTypeMetaInfoBean.getSafeImpact() != PaymentImpactOnSafe.NOSAFE) {
					String agentName = Utilities.getAgentName(conn, agentId_G);
					accountBoxTransactionId = UtilitiesSafeFinancials.createAcctBoxTransactions(
							conn, 
							0, 
							"p_fin_transactions".toUpperCase(), 
							safePaymentTypeMetaInfoBean.getSafeImpact(), 
							userId_G, 
							actualReceivedAmtIqd,
							actualReceivedAmtUsd,
							branchId_G,
							safePaymentTypeMetaInfoBean.getName() + " - " + "حسابات المندوب" , 
							"kbusers",
							"us_id",
							agentId_G,
							"us_name",
							agentName,
							userId_G);
				}
				// create payment record
				StandardTransactionBean standardTransactionBean = new StandardTransactionBean();
				standardTransactionBean.setEntity(FinOperationEntity.AGENT);
				standardTransactionBean.setEntityId(agentId_G);
				standardTransactionBean.setCategory(FinOperationCategory.PMTTYPE);
				standardTransactionBean.setCode(FinOperationCode.valueOf(pmtType));
				standardTransactionBean.setInitiatedInBranchId(branchId_G);
				standardTransactionBean.setWhichScreen("حسابات شحنات المندوب");
				standardTransactionBean.setEntityShareIqd(totAgentShare);
				
				standardTransactionBean.setTransactionAmountIqd(netToBeCollectedFromAgentIqd);
				standardTransactionBean.setTransactionAmountUsd(netToBeCollectedFromAgentUsd);
				
				standardTransactionBean.setReceiptsAmtIqd(netToBeCollectedFromAgentIqd);
				standardTransactionBean.setReceiptsAmtUsd(netToBeCollectedFromAgentUsd);
				
				standardTransactionBean.setAmountPaidActuallyIqd(actualReceivedAmtIqd);
				standardTransactionBean.setAmountPaidActuallyUsd(actualReceivedAmtUsd);
				
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
				
				int noOfCasesUpdate= 0;
				ps = conn.prepareStatement("update p_cases set c_agentpmtid=? , c_agentsharesettled='FULL' "
						+ " where c_id = ? and c_agentpmtid = 0 and c_assignedagent = ?");
				for (int i =0; i<cidList.size(); i++) {
					ps.setInt(1, standardStransactionId);
					ps.setString(2, cidList.get(i));
					ps.setInt(3,agentId_G);
					int impactedRows = ps.executeUpdate();
					noOfCasesUpdate +=impactedRows;
					ps.clearParameters(); 
				}
				
				if(noOfCasesUpdate != cidList.size() || noOfCasesUpdate==0) {
					throw new Exception ("خلل في تسجيل الدفعه");
				}
			conn.commit();
			}
		}catch (Exception e) {
			statusMsg = "Error at payment creation, error (" + e.getMessage()+ ")";
			setInsertErrorFlag(true);
			try {conn.rollback();} catch (Exception ignoreE) {}
			e.printStackTrace();
		} finally {
			try {ps.close();} catch (Exception e) {}
		}

		return statusMsg;
	}
	

	private boolean areTheCasesSelectedStillTheSame( Connection conn, int a_agentId, ArrayList<String> cidList) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean first = true;
		try {
			String sql = "select count(*) as how_many_cases "
			+ " from p_cases "
			+ " where c_assignedagent=? and  c_agentsharesettled !='FULL' and c_alllowagentpay = 'Y' "
			+ " and ("
			+ "			c_paytodlvcheck ='Y'"
			+ "			or ("
			+ "			c_paytodlvcheck ='N' and c_changedprice = 'N' and c_usdchangedprice ='N'  "
			+ "			)"
			+ "		) "
			+ " and  c_id in (";
			for (String caseid : cidList) {
				if (!first) {
					sql += ",";
				}
				first = false;
				sql +="?";
			}
			sql +=") ";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, a_agentId);
			int i =2;
			for (String caseid : cidList) {
				pst.setString(i, caseid);
				i++;
			}
			rs = pst.executeQuery();
			int howManyCases =0;
			if (rs.next()) {
				howManyCases = rs.getInt("how_many_cases");
			}
			if (howManyCases != cidList.size())
				return false;
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}
		return true;
	}// end of get_cid
	
	
	private ArrayList<String> getCid( Connection conn, int agentId) throws Exception {
		ArrayList<String> cases = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int userBranch = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		try {
				String querycall = "select c_id From p_cases "
					+ " left join kbstate on (st_code = c_rcv_state and st_branch=? )"
					+ " where c_alllowagentpay='Y' and c_assignedagent =? "
					+ " and c_agentpmtid=0 and c_agentsharesettled !='FULL'  ";
				
				ps = conn.prepareStatement(querycall); // create a statement
				ps.setInt(1, userBranch);
				ps.setInt(2, agentId); 
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
	
	/**
	 * @return the ruralAreaItemsRtn
	 */
	public int getRuralAreaItemsRtn() {
		return ruralAreaItemsRtn;
	}

	/**
	 * @param ruralAreaItemsRtn the ruralAreaItemsRtn to set
	 */
	public void setRuralAreaItemsRtn(int ruralAreaItemsRtn) {
		this.ruralAreaItemsRtn = ruralAreaItemsRtn;
	}

	/**
	 * @return the ruralItemsDlv
	 */
	public int getRuralItemsDlv() {
		return ruralItemsDlv;
	}

	/**
	 * @param ruralItemsDlv the ruralItemsDlv to set
	 */
	public void setRuralItemsDlv(int ruralItemsDlv) {
		this.ruralItemsDlv = ruralItemsDlv;
	}


	public double getTotDlvRecs() {
		return totDlvRecs;
	}


	public void setTotDlvRecs(double totDlvRecs) {
		this.totDlvRecs = totDlvRecs;
	}


	public double getTotReceiptAmtUsd() {
		return totReceiptAmtUsd;
	}


	public void setTotReceiptAmtUsd(double totReceiptAmtUsd) {
		this.totReceiptAmtUsd = totReceiptAmtUsd;
	}

	public double getDlvAgentDebtIqd() {
		return dlvAgentDebtIqd;
	}

	public void setDlvAgentDebtIqd(double dlvAgentDebtIqd) {
		this.dlvAgentDebtIqd = dlvAgentDebtIqd;
	}

	public double getDlvAgentDebtUsd() {
		return dlvAgentDebtUsd;
	}

	public void setDlvAgentDebtUsd(double dlvAgentDebtUsd) {
		this.dlvAgentDebtUsd = dlvAgentDebtUsd;
	}

	public double getTotReceiptAmtIqd() {
		return totReceiptAmtIqd;
	}

	public void setTotReceiptAmtIqd(double totReceiptAmtIqd) {
		this.totReceiptAmtIqd = totReceiptAmtIqd;
	}
}
