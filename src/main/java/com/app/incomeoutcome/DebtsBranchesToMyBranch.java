package com.app.incomeoutcome;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;

public class DebtsBranchesToMyBranch extends CoreMgr{
	private LinkedHashMap <String,String> repeatedNo = new LinkedHashMap<String,String>();
	Utilities util = new Utilities();
	private int records = 0;
	private int searchedCustid = 0;
	private double totReceiptAmt = 0;
	
	
	private long debt;
	
	private double totShipmentCost = 0;
	private double totNetAmt = 0;
	private double totNetAmtUsd = 0;
	private double totMoneySent  = 0;
	boolean errorFlag = false;
	public DebtsBranchesToMyBranch() {
		records = 0;
		MainSql = "select '' as netpaid,  '' as olddebt, '' as totshipmentcost, '' as selectedcases,'' as selectedcaseshidden, q_stage, q_step,'' status ,"
				+ " '' as pmtCheckBox,  'شحنات سلمت وراجعه فقط' as title, c_custid,c_custreceiptnoori,'' as totamt,'' as pmtrmk, '' as pmtdate, "
				+ " date(c_createddt) as c_createddt , concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr, c_id, "
				+ " c_rcv_name , c_rcv_hp1, '' as fromdate, '' as todate, c_changedprice, c_priceb4change, "
				+ " c_receiptamt, c_receiptamt_usd , cc_frombranch, cc_tobranch, c_rmk,  "
				+ " (case when (cc_pathcost>0)  then (c_receiptamt -  cc_pathcost) else  (c_receiptamt - c_shipment_cost)  end) as netamt,"
				+ " (case when (cc_pathcost>0) then cc_pathcost else c_shipment_cost end) as c_shipment_cost"
				+ " from p_cases  "
				+ " left join kbstate on st_code = c_rcv_state and st_branch = {branchesAcctReport}"
				+ " left join kbcity_district on (cdi_id =c_rcv_district and cdi_stcode=c_rcv_state) "
				+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch = {userstorecode} and cc_tobranch = {branchesAcctReport} and cc_branchpmtid=0 and cc_branchrecievedpmt='N') "
				+ "	where q_stage = 'DLV'  "; 

		//and c_rcv_state = if ('{globalcustomeracct_perbranch}' = '', c_rcv_state, '{receiverState}')
		
	/*	canFilter = true;
		userDefinedFilterCols.add("c_custid");*/
		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
		userDefinedFilterColsHtmlType.put("c_custid", "DROPLIST");
		
		userDefinedSumCols.add("c_receiptamt_usd");
		userDefinedSumCols.add("c_receiptamt");
		userDefinedSumCols.add("c_shipment_cost");
		
		userDefinedSumCols.add("netamt");
		userDefinedGroupColsOrderBy = "c_createddt, c_custreceiptnoori";
		
		UserDefinedPageRows = 2000;
		groupSumCaption = "المجموع";
		userDefined_x_panelclass = "account_x_panel";
		userDefinedGroupByCol = "c_createddt";
		
		
		userDefinedGridCols.add("c_custid");
				
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_custreceiptnoori");
		
		userDefinedGridCols.add("c_receiptamt_usd");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_shipment_cost");
		//userDefinedGridCols.add("c_sendmoney");
		userDefinedGridCols.add("netamt");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("status");
		//userDefinedGridCols.add("pmtCheckBox");
		
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		//userDefinedColLabel.put("pmtCheckBox", "دفع");
		userDefinedColLabel.put("c_id", "رقم الشحنه");
		userDefinedColLabel.put("c_createddt", "تاريخ الشحنات");
		userDefinedColLabel.put("c_rcv_name", "إسم المستلم");
		userDefinedColLabel.put("addr", "العنوان");
		userDefinedColLabel.put("netamt", "الصافي للعميل");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل د.ع");
		userDefinedColLabel.put("c_receiptamt_usd","مبلغ الوصل دولار" );
		userDefinedColLabel.put("c_shipment_cost", "تكلفة الشحن");
		userDefinedColLabel.put("c_custid", "المتجر");
		userDefinedColLabel.put("pmtdate", "تاريخ الدفع");
		userDefinedColLabel.put("pmtrmk", "ملاحظات");
		userDefinedColLabel.put("totamt", "صافي مبلغ الوصولات المحددة");
		userDefinedColLabel.put("olddebt", "فروقات من دفعات سابقة");
		userDefinedColLabel.put("netpaid", "المبلغ الواجب تسديده للفرع");
		userDefinedColLabel.put("status", "الحاله");
		userDefinedColLabel.put("fromdate","من تاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		userDefinedColLabel.put("selectedcases", "أرقام الوصولات");
		userDefinedColLabel.put("cc_frombranch", "محاسبة فرع");
		userDefinedPageFooterFunction = "myFooterFunction()";
		//canNew = true;
		mainTable = "p_cases";
		keyCol = "c_id";
		
		userDefinedNewColsHtmlType.put("totamt", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("olddebt", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("netpaid", "NUMBER_WITH_COMMAS");
		
		userDefinedNewCols.add("cc_frombranch");
		userDefinedNewCols.add("selectedcases");
		userDefinedNewCols.add("totamt");
		userDefinedNewCols.add("olddebt");
		userDefinedNewCols.add("netpaid");
		userDefinedNewCols.add("pmtrmk");
		userDefinedNewCols.add("selectedcaseshidden");
		userDefinedHiddenNewCols.add("selectedcaseshidden");
		
		
		
		userModifyTD.put("c_receiptamt", "modifyReceiptAmt({c_receiptamt}, {q_stage}, {q_step})");
		userModifyTD.put("c_shipment_cost", "modifyShipmentCost({c_shipment_cost}, {q_stage}, {q_step}, {c_shipmentpaidbysender})");
		userModifyTD.put("status", "modifyStatus({q_stage}, {q_step}, {c_changedprice},{c_priceb4change},{c_receiptamt}, {c_receiptamt_usd})");
		//userModifyTD.put("pmtCheckBox", "displayCheckBox({c_id})");
		userModifyTD.put("c_custreceiptnoori", "modifyReceiptNo({c_custreceiptnoori},{c_id})");
		userModifyTD.put("netamt", "sumNetAmt({netamt}, {c_id})");
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		userDefinedColsMustFill.add("c_custid");
		userDefinedColsMustFill.add("totamt");
		userDefinedColsMustFill.add("pmtdate");
		userDefinedLookups.put("c_custid", "select cust_id, cust_name from kbcustomers ");
		
		
		userDefinedNewColsDefualtValues.put("cc_frombranch", new String[] {"{userstorecode}"});
		userDefinedLookups.put("cc_frombranch", "select branch_id, branch_name from kbbranches");
		userDefinedReadOnlyNewCols.add("cc_frombranch");
		userDefinedNewColsHtmlType.put("cc_frombranch", "DROPLIST");
	
		userDefinedNewColsHtmlType.put("pmtrmk", "TEXTAREA");
		userDefinedReadOnlyNewCols.add("totamt");
		userDefinedReadOnlyNewCols.add("olddebt");
		userDefinedReadOnlyNewCols.add("netpaid");
		userDefinedNewColsHtmlType.put("selectedcases", "TEXT");
		userDefinedReadOnlyNewCols.add("selectedcases");
		userDefinedCaption = "ديون مستحقة الدفع";
		userDefinedTableHeadersClass = "bg-info bg-lighten-1 white";
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
	
	public String displayCheckBox (HashMap<String, String> hashy) {
		
		String s = "<td><input type=\"checkbox\"  id=\"pmtcheck_"+hashy.get("c_id")+"\" onclick=\"checkBoxPmtClicked(this, "+hashy.get("c_id")+")\">";
		s +="</td>";
		return s;	
	}
	public String modifyReceiptNo(HashMap<String, String> hashy) {
		String s = "";
		s = "<td  caseid='"+hashy.get("c_id")+"' id='"+hashy.get("c_custreceiptnoori")+"'>"+hashy.get("c_custreceiptnoori");
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
		int senderBranch = Integer.parseInt(replaceVarsinString("{branchesAcctReport}", arrayGlobals).trim());
		int receiverBranch = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		boolean checkBoxPayment = false, useHotLookup = false;
		String selectedCasesForPayment = "";
		String selectedAmounToPay = "0";
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
					if (parameters.containsKey("amount_topay") && parameters.get("amount_topay")!=null
							&& !parameters.get("amount_topay")[0].equalsIgnoreCase("")) {
						selectedAmounToPay = parameters.get("amount_topay")[0];
					}
				}
			}
		}
				
		userDefinedLookups.put("c_custid", "select cust_id, cust_name from kbcustomers where cust_branch="+receiverBranch);
		String custRecieptNo ="";
		Connection conn2 = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn2 = mysql.getConn();
			if (!checkBoxPayment) {
				pst = conn2.prepareStatement("select c_custreceiptnoori "
					+ " from p_cases "
					+ " join p_caseschain on (c_id = cc_caseid and cc_frombranch=? and cc_tobranch=? and cc_branchpmtid=0) "
					+ " where q_status !='CLS' and c_settled !='FULL' "
					+ " and (q_stage = 'DLV' and q_step = 'DLEIVERD')  and c_receiptfromsystem='N'" 
					+ "  order by c_custreceiptnoori");
				pst.setInt(1,receiverBranch);
				pst.setInt(2,senderBranch);
				rs = pst.executeQuery();
				while (rs.next()) {
					if (rs.getString("c_custreceiptnoori").equalsIgnoreCase(custRecieptNo))
						repeatedNo.put(custRecieptNo, custRecieptNo);
					custRecieptNo = rs.getString("c_custreceiptnoori");
				}
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
			
			}else{// check if the user selected boxes for payment, if yes then get the boxes values
				BigInteger amountToPay =  BigInteger.valueOf(Integer.parseInt(selectedAmounToPay));
				BigInteger debtBig = BigInteger.valueOf(debt);
				BigInteger net = new BigInteger("0");
				
				if (debt >0)
					 net = amountToPay.add(debtBig);
				else
					 net = amountToPay.subtract(debtBig);
				
				userDefinedNewColsDefualtValues.put("totamt", new String[] {selectedAmounToPay});
				userDefinedNewColsDefualtValues.put("selectedcaseshidden", new String[] {selectedCasesForPayment});
				userDefinedNewColsDefualtValues.put("selectedcases",new String [] {casesToPayList.size()+""} );
				userDefinedNewColsDefualtValues.put("olddebt", new String[] {debt+""});
				userDefinedNewColsDefualtValues.put("netpaid", new String[] {net+""});
				userDefinedColLabel.put("olddebt", "لا يوجد دين سابق");
				if (debt>0) {
					userDefinedColLabel.put("olddebt", "مدين");
				}else if (debt<0) {
					userDefinedColLabel.put("olddebt", "دائن");
				}
				//userDefinedNewColsDefualtValues.put("totalnet", new String[] {net+""});
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
			try {conn2.close();} catch (Exception e) {}
			
		}
		
		super.initialize(smartyStateMap);
//		String printDebtBtn = "<a href='../../PrintDbtsBranchToMyBranchSRVL?receiverBranch="+receiverBranch+"&senderBranch="+senderBranch+"' type='input' "
//				+ " class='btn btn-xs btn-info' style='float: left;' >طباعة الكشف<i class=\"fa fa-print fa-lg\"></i></a>";
		userDefinedCaption ="<div class='row'>"
				+ "<div class='col-md-10 col-sm-2 col-xs-2'>"+this.userDefinedCaption+"</div>"
						+ "<div class='col-md-2 '>"+/*rintDebtBtn*/""+"</div></div>";
	}
	
	public String modifyStatus (HashMap<String,String> hashy) {
		
		double amtUsd =  Double.parseDouble(hashy.get("c_receiptamt_usd"));
		totNetAmtUsd += amtUsd;
		String html = "<td>";
		if (hashy.get("q_stage").equalsIgnoreCase("DLV")  ) {
			if (hashy.get("q_step").equalsIgnoreCase("DLEIVERD"))
				html +="تم التسليم";
			else if (hashy.get("q_step").equalsIgnoreCase("SUCC_CHANGEPRICE"))
				html +="تم التسليم مع تغيير سعر الوصل من "+hashy.get("c_priceb4change")+" إلى "+hashy.get("c_receiptamt");
			else if (hashy.get("q_step").equalsIgnoreCase("PART_SUCC")) {
				html +="تسليم جزئي ";
				if (hashy.get("c_changedprice").equalsIgnoreCase("Y"))
					html +="مع تغيير سعر الوصل من "+hashy.get("c_priceb4change")+" إلى "+hashy.get("c_receiptamt");
			}else if (hashy.get("q_step").equalsIgnoreCase("FORCE_DLV")) {
				html += "<span class='badge rounded-pill bg-warning text-dark' style='font-size: 14px;'>أعتباره واصل</span>";
			}
		}else {
			errorFlag = true;
			html += "خطأ في النظام"+", "+ hashy.get("q_step");
			System.out.println("خطأ في النظام agent balance class,modifyStatus method stage=>"+hashy.get("q_stage")+", step=>"+hashy.get("q_step"));
		}
		html+= "</td>";
		
		return html;
		
	}
	
	
	
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
					double totalDue = totNetAmt - debt;
					return "<td colspan='11' stye='font-size: 15px;' align='center'>"
							+ "<div class='row'>"
							+ "<div class='col-6'>"
							+ " <label>فروقات من دفعات سابقة</label>"
							+ "</div>"
							+ "<div class='col-6'>"
							+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
							+ " <span dir='ltr' style='text-align:right' id ='cust-debt'> "+numFormat.format(debt)+"</span></strong>"
							+ "</div>"
							+ "<div class='col-6'>"
							+ " <label>المبلغ الكلي للوصولات دينار عراقي</label>"
							+ "</div>"
							+ "<div class='col-6'>"
							+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
							+ " <span dir='ltr' style='text-align:right' id ='cust-net'> "+numFormat.format(totNetAmt)+"</span></strong>"
							+ "</div>"
							+ "<div class='col-6'>"
							+ " <label>المبلغ الكلي للوصولات دولار أمريكي</label>"
							+ "</div>"
							+ "<div class='col-6'>"
							+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\">"
							+ " <span dir='ltr' style='text-align:right' id ='cust-net'> "+numFormat.format(totNetAmtUsd)+"</span></strong>"
							+ "</div>"
							+ "<form action=\"?\" id='branch-balance-settle-form' method=\"post\" style=\"display: inline;\" >"
							+ " <input type=\"hidden\" name=\"op\" value=\"new\"><input type=\"hidden\""
							+ " name=\"myClassBean\" value=\"com.app.incomeoutcome.BranchesBalance\">"
							+ "<input type=\"hidden\" name=\"selected_casesto_pay\" value='' id='selected_casesto_pay'>"
							+ "<input type=\"hidden\" name=\"amount_topay\" value='0' id='amount_topay'>";
				}else {
					return  "<td colspan='2' stye='font-size: 15px;' align='center'>هنالك خطأ في النظام الرجاء الأتصال </td>";
				}
			}else
				return "";
		}
	
	/* (non-Javadoc)
	 * @see com.app.core.CoreMgr#doInsert(javax.servlet.http.HttpServletRequest, boolean)
	 */
	

	
	public int getRecords() {
		return records;
	}

	public double getTotReceiptAmt() {
		return totReceiptAmt;
	}
	public void setRecords(int records) {
		this.records = records;
	}
	public long getDebt() {
		return debt;
	}

	public void setDebt(long debt) {
		this.debt = debt;
	}
}