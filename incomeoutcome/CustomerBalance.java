package com.app.incomeoutcome;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.core.CoreMgr;
import com.app.db.mysql;
import com.app.util.Utilities;
import com.mysql.jdbc.Statement;
/*
 * 
 */
public class CustomerBalance extends CoreMgr{
		private LinkedHashMap <String,String> repeatedNo = new LinkedHashMap<String,String>();
		Utilities util = new Utilities();
		private int records = 0;
		private double totReceiptAmt = 0;
		public double getTotReceiptAmt() {
			return totReceiptAmt;
		}
		boolean errorFlag = false;
		private double totShipmentCost = 0;
		private double totMoneySent  = 0;
		private double totNetAmt = 0;
		public CustomerBalance() {
			records = 0;
			MainSql = "select c_paidinadvance,c_advancepmtid,  '' as selectedcases,'' as selectedcaseshidden, c_shipmentpaidbysender, q_stage, q_step,'' as status ,'' as pmtCheckBox,  'شحنات سلمت وراجعه فقط' as title, c_custid,c_custreceiptnoori,'' as totamt,'' as pmtrmk, '' as pmtdate, "
					+ " date(c_createddt) as c_createddt , concat(st_name_ar,' - ',ifnull(cdi_name,''),' ',ifnull(c_rcv_addr_rmk,'')) as addr, c_id, "
					+ " c_rcv_name , c_rcv_hp, '' as fromdate, '' as todate,"
					+ " (case when (q_stage='dlv_stg' and q_step='delivered') then c_receiptamt"
					+ "       when  ( q_stage='cncl' and q_status !='CLS' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then c_receiptamt*-1 "
					+ "				else 0 end) as c_receiptamt,"
					+ "  c_shipment_cost, "
					+ " (	case "
					+ "			when (q_stage='dlv_stg' and q_step='delivered')  then (c_receiptamt - c_shipment_cost) "
					+ "			when ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='N' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then (c_shipment_cost - c_receiptamt) "
					+ "			when ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='Y' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then (c_receiptamt*-1) "
					+ " 		else (1*-c_shipment_cost)  end) as netamt "
					+ " from p_cases "
					+ " join p_queue on (c_id= q_caseid and q_status !='CLS')"
					+ " left join kbstate on st_code = c_rcv_state"
					 + " left join kbcity_district on (cdi_code =c_rcv_district and cdi_stcode=c_rcv_state) "
					+ " where c_custid ={customerAcct}  and c_receiptfromsystem = 'N' "
					+ " and ( (q_stage='dlv_stg' and q_step='delivered' and c_settled ='NO')"
					+ " 		or ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='Y' and c_settled ='NO')"
					+ "         or ( q_stage='cncl' and q_status !='CLS' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL')) "
					+ "   order by c_custreceiptnoori ";
			
			
			userDefinedSumCols.add("c_receiptamt");
			userDefinedSumCols.add("c_shipment_cost");
			
			userDefinedSumCols.add("netamt");
			userDefinedGroupColsOrderBy = "c_createddt, c_custreceiptnoori";
			
			UserDefinedPageRows = 3000;
			groupSumCaption = "المجموع";
			userDefined_x_panelclass = "account_x_panel";
			userDefinedGroupByCol = "c_createddt";
			
			userDefinedGridCols.add("c_id");
			//userDefinedGridCols.add("c_createddt");
			userDefinedGridCols.add("c_rcv_name");
			userDefinedGridCols.add("c_rcv_hp");
			//userDefinedGridCols.add("c_weight");
			userDefinedGridCols.add("addr");
			userDefinedGridCols.add("c_custreceiptnoori");
			userDefinedGridCols.add("c_receiptamt");
			userDefinedGridCols.add("c_shipment_cost");
		
			userDefinedGridCols.add("netamt");
			userDefinedGridCols.add("status");
			userDefinedGridCols.add("pmtCheckBox");
			
			userDefinedColLabel.put("pmtCheckBox", "دفع");
			userDefinedColLabel.put("c_id", "رقم الشحنه");
			userDefinedColLabel.put("c_createddt", "تاريخ الشحنات");
			userDefinedColLabel.put("c_rcv_name", "إسم المستلم");
			
			userDefinedColLabel.put("addr", "العنوان");
			userDefinedColLabel.put("c_rcv_hp", "هاتف");
			userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
			userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
			userDefinedColLabel.put("c_shipment_cost", "تكلفة الشحن");
			userDefinedColLabel.put("c_custid", "الزبون");
			userDefinedColLabel.put("pmtdate", "تاريخ الدفع");
			userDefinedColLabel.put("pmtrmk", "ملاحظات");
			userDefinedColLabel.put("totamt", "المبلغ المطلوب المستحق للزبون");
			
			userDefinedColLabel.put("netamt", "الصافي للعميل");
			userDefinedColLabel.put("status", "الحاله");
			userDefinedColLabel.put("fromdate","من تاريخ");
			userDefinedColLabel.put("todate","إلى تاريخ");
			userDefinedColLabel.put("selectedcases", "أرقام الوصولات");
			userDefinedPageFooterFunction = "myFooterFunction()";
			canNew = true;
			mainTable = "p_cases";
			keyCol = "c_id";
			
			userDefinedNewCols.add("c_custid");
			//userDefinedNewCols.add("c_createddt");
			userDefinedNewCols.add("fromdate");
			userDefinedNewCols.add("selectedcases");
			userDefinedNewCols.add("todate");
			userDefinedNewCols.add("totamt");
			userDefinedNewCols.add("pmtdate");
			userDefinedNewCols.add("pmtrmk");
			userDefinedNewCols.add("selectedcaseshidden");
			userDefinedHiddenNewCols.add("selectedcaseshidden");
			
			userModifyTD.put("c_receiptamt", "modifyReceiptAmt({c_receiptamt}, {q_stage}, {q_step}, {c_shipmentpaidbysender},{c_paidinadvance},{c_advancepmtid})");
			userModifyTD.put("c_shipment_cost", "modifyShipmentCost({c_shipment_cost}, {q_stage}, {q_step}, {c_shipmentpaidbysender},{c_paidinadvance},{c_advancepmtid})");
			userModifyTD.put("status", "modifyStatus({q_stage}, {q_step}, {c_shipmentpaidbysender},{c_paidinadvance},{c_advancepmtid})");
			userModifyTD.put("c_custreceiptnoori", "modifyRecieptNo({c_custreceiptnoori})");
			userModifyTD.put("netamt", "sumNetAmt({netamt})");
			userModifyTD.put("pmtCheckBox", "displayCheckBox({c_id})");
			userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
			userDefinedColsMustFill.add("c_custid");
			userDefinedColsMustFill.add("totamt");
			userDefinedColsMustFill.add("pmtdate");
			userDefinedNewColsDefualtValues.put("c_custid", new String[] {"{customerAcct}"});
			userDefinedLookups.put("c_custid", "select c_id, c_name from kbcustomers where c_id='{customerAcct}'");
			
			userDefinedNewColsDefualtValues.put("totamt", new String [] {"%select "
					+ "sum( (	case "
					+ "			when (q_stage='dlv_stg' and q_step='delivered')  then (c_receiptamt - c_shipment_cost) "
					+ "			when ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='N' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then (c_shipment_cost - c_receiptamt) "
					+ "			when ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='Y' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then (c_receiptamt*-1) "
					+ " 		else (1*-c_shipment_cost)  end))"
					+ " from p_cases "
					+ " join p_queue on (c_id= q_caseid and q_status !='CLS')"
					+ " where c_custid ={customerAcct}   and c_receiptfromsystem = 'N'   "
					+ " and ( (q_stage='dlv_stg' and q_step='delivered' and c_settled ='NO')"
					+ " or ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='Y' and c_settled ='NO')"
					+ " or ( q_stage='cncl' and q_status !='CLS' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL')) "});
			
			
			
			
			userDefinedNewColsHtmlType.put("fromdate", "DATE");
			userDefinedNewColsHtmlType.put("todate", "DATE");
			
			
			userDefinedNewColsHtmlType.put("totamt", "TEXT");
			//userDefinedNewColsHtmlType.put("c_createddt", "DROPLIST");
			userDefinedNewColsHtmlType.put("pmtdate", "DATE");
			userDefinedNewColsHtmlType.put("pmtrmk", "TEXTAREA");
			userDefinedReadOnlyNewCols.add("totamt");
			userDefinedReadOnlyNewCols.add("c_custid");
			userDefinedNewColsHtmlType.put("selectedcases", "MULTILIST");
			userDefinedReadOnlyNewCols.add("selectedcases");
			newCaption = "دفع مستحقات زبون";
			
			userModifyTD.put("c_custreceiptnoori", "modifyReceiptNo({c_custreceiptnoori},{c_id})");
		}
		
		public String modifyReceiptNo  (HashMap<String, String> hashy) {
			String style= "";
			if (repeatedNo.containsKey(hashy.get("c_custreceiptnoori")))
				style = "background-color:red";
			
			String s = "<td style='"+style+"' caseid='"+hashy.get("c_id")+"' id='"+hashy.get("c_custreceiptnoori")+"'>"+hashy.get("c_custreceiptnoori");
			s +="</td>";
			return s;
		}
		
		public String displayCheckBox (HashMap<String, String> hashy) {
			
			String s = "<td><input type=\"checkbox\" class=\"flat\" "
					+ " id=\"pmtcheck_"+hashy.get("c_id")+"\" onclick=\"checkBoxPmtClicked(this, "+hashy.get("c_id")+")\">";
			s +="</td>";
			return s;	
		}
		
		
		public String sumNetAmt(HashMap<String, String> hashy) {
			String s = "";
			double netAmt = Double.parseDouble(hashy.get("netamt"));
			s +="<td dir='ltr' style='text-align:right'>";
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
			
			String custId = replaceVarsinString(" {customerAcct} ", arrayGlobals).trim();
			String custRecieptNo ="";
			Connection conn2 = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				conn2 = mysql.getConn();
				pst = conn2.prepareStatement("select c_custreceiptnoori "
					+ " from p_cases  "
					+ " join p_queue on (c_id= q_caseid and q_status !='CLS')"
					+ " where c_custid =? and c_settled !='FULL'  and c_receiptfromsystem = 'N'  "
					+ " and (q_stage='dlv_stg' and q_step='delivered') "
					+ "  order by c_custreceiptnoori");
				pst.setString(1,custId);
				rs = pst.executeQuery();
				while (rs.next()) {
					if (rs.getString("c_custreceiptnoori").equalsIgnoreCase(custRecieptNo))
						repeatedNo.put(custRecieptNo, custRecieptNo);
					
					custRecieptNo = rs.getString("c_custreceiptnoori");
				}
				System.out.println(repeatedNo);
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
				
				// check if the user selected boxes for payment, if yes then get the boxes values
				
				String sql = "select c_custreceiptnoori ,ifnull(sum( (	case when (q_stage='dlv_stg' and q_step='delivered')  then (c_receiptamt - c_shipment_cost) "
						+ "	when ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='N' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then (c_shipment_cost - c_receiptamt) "
						+ " when ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='Y' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then (c_receiptamt*-1) "
						+ "	else (1*-c_shipment_cost)  end) ),0)as totdue "
						+ " from  p_cases join p_queue on (c_id = q_caseid and q_status!='CLS') where c_id in (";
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
					 
					String [] casesArray = new String [casesToPayList.size()];
					 i =0;
					while (rs.next()) {
						totDue += rs.getDouble("totDue");
						casesArray[i] = rs.getString("c_custreceiptnoori");
						//System.out.println(i+", "+casesArray[i]);
						i++;
					}
					userDefinedNewColsDefualtValues.put("totamt", new String[] {Double.toString(totDue)});
					userDefinedNewColsDefualtValues.put("selectedcaseshidden", new String[] {selectedCasesForPayment});
					
					userDefinedNewCols.remove("fromdate");
					userDefinedNewCols.remove("todate");
					
					//casesArray =  casesToPayList.toArray(casesArray);
					//System.out.println(casesArray[1]);
					userDefinedNewColsDefualtValues.put("selectedcases",casesArray );
				}else {
					userDefinedNewCols.remove("selectedcases");
					userDefinedNewCols.remove("selectedcaseshidden");
					
					userDefinedNewLookups.put("totamt", "!select ifnull( "
							+ "sum( (	case "
							+ "			when (q_stage='dlv_stg' and q_step='delivered')  then (c_receiptamt - c_shipment_cost) "
							+ "			when ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='N' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then (c_shipment_cost - c_receiptamt) "
							+ "			when ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='Y' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then (c_receiptamt*-1) "
							+ " 		else (1*-c_shipment_cost)  end)),0)"
							+ " from  p_cases "
							+ " join p_queue on (c_id = q_caseid and q_status !='CLS' )"
							+ " where c_custid='"+custId+"' and c_receiptfromsystem = 'N'  and  (c_createddt >= '{fromdate}' "
									+ " and c_createddt<=adddate(date('{todate}'),1))  "
									+ " and ( 		(q_stage='dlv_stg' and q_step='delivered' and c_settled ='NO') "
									+ "			or ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='Y' and c_settled ='NO') "
									+ " 		or ( q_stage='cncl' and q_status !='CLS' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL')) ");
					
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
			
			if (hashy.get("q_stage").equalsIgnoreCase("cncl") && hashy.get("c_shipmentpaidbysender").equalsIgnoreCase("Y") && hashy.get("c_paidinadvance").equalsIgnoreCase("NO")) {// if returned but the shipment cost must be paid by sender
				html ="<td style='background-color:#928700; color:white;'>راجع مع دفع اجور النقل من صاحب المحل";
			}else if (hashy.get("q_stage").equalsIgnoreCase("dlv_stg") && hashy.get("q_step").equalsIgnoreCase("delivered") ) { //successful dlv
				html +="تم التسليم";
			}else if (hashy.get("q_stage").equalsIgnoreCase("cncl") &&  hashy.get("c_paidinadvance").equalsIgnoreCase("YES")) { // if advanced pmt and returned.
				
				html ="<td style='background-color:#c3069e; color:white;'>تم المحاسبه عليها مقدما  (دفعه مقدمه رقم "+hashy.get("c_advancepmtid")+")"+" وهي راجعه الأن.";
				if (hashy.get("c_shipmentpaidbysender").equalsIgnoreCase("Y")) { // if the shipment is to be paid by sender
					html +="دفع اجور النقل من صاحب المحل";
				}
			
			}else {
				html = "<td style='background-color:red;color:white' >خطأ في النظام, أتصل بسوفتيكا رجاء";
				errorFlag = true;
			}
			html+= "</td>";
			return html;
			
		}
		
		public String modifyReceiptAmt (HashMap<String,String> hashy) {
			setRecords(getRecords() + 1);
			String html = "<td dir='ltr' style='text-align:right'>";
			double goodsCost = Double.parseDouble(hashy.get("c_receiptamt"));
			if (hashy.get("q_stage").equalsIgnoreCase("cncl")) {
				if (hashy.get("c_paidinadvance").equalsIgnoreCase("YES")) {
					
					html += numFormat.format(goodsCost);
				}else {
					goodsCost = 0;
					html += " - ";
				}
				
			}else {
				html += numFormat.format(goodsCost);
			}
			//System.out.println()
			//totReceiptAmt +=goodsCost;
			html+= "</td>";
			return html;
			
		}
		
		public String modifyShipmentCost (HashMap<String,String> hashy) {
			String html = "<td style=''>";
			double shipmentCost = Double.parseDouble(hashy.get("c_shipment_cost"));
			if (hashy.get("q_stage").equalsIgnoreCase("cncl") ) { // if returned
				if (hashy.get("c_paidinadvance").equalsIgnoreCase("YES")) { // paid in advance
					if (hashy.get("c_shipmentpaidbysender").equalsIgnoreCase("Y")) { // if shipment is paid by sender , then he should return the whole receipt amount
						shipmentCost = 0;
						html += " - ";
					}else {
						html += numFormat.format(shipmentCost);
					}
				}else {
					if (hashy.get("c_shipmentpaidbysender").equalsIgnoreCase("Y"))  {
						html += numFormat.format(shipmentCost);
					}else {
						shipmentCost = 0;
						html += " - ";
					}
					
				}
			}else{
				html += numFormat.format(shipmentCost);
			}
			//totShipmentCost +=  shipmentCost;
			
			html+= "</td>";
			return html;
		}
		
	
		
		public String myFooterFunction(String colName) {
			if (colName.equalsIgnoreCase("c_custreceiptnoori") ) {
				if (!errorFlag) {
					
					double totalDue = totNetAmt;
					return "<td colspan='2' stye='font-size: 15px;' align='center'><label> المبلغ الصافي للعميل </label>"
							+ " <strong style=\"margin-right: 20px;margin-left: 20px;font-size:15px;\"><span dir='ltr' style='text-align:right'> "+numFormat.format(totalDue)+"</span></strong>"
							+ "<form action=\"?\" method=\"post\" style=\"display: inline;\" onsubmit=\"checkBoxPmtClicked()\"><input type=\"hidden\" name=\"op\" value=\"new\"><input type=\"hidden\""
							+ " name=\"myClassBean\" value=\"com.app.incomeoutcome.CustomerBalance\">"
							+ "<input type=\"hidden\" name=\"selected_casesto_pay\" value='' id='selected_casesto_pay'>"
							+ "<button type=\"submit\" class=\"btn btn-danger btn-sm\" name=\"smarty_newformbtn\" value=\"newform\">إدفع الأن</button></form></td>";
				}else {
					return  "<td colspan='2' stye='font-size: 15px;' align='center'>هنالك خطأ في النظام الرجاء الأتصال بسوفتيكا</td>";
				}
			}else if (colName.equalsIgnoreCase("c_goods_cost") || colName.equalsIgnoreCase("c_shipment_cost") )
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
			ResultSet rs = null;
			String userId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
			String custId = replaceVarsinString(" {customerAcct} ", arrayGlobals).trim();
			inputMap_ori = filterRequest(rqs);
			//System.out.println(inputMap_ori);
			boolean checkBoxPayment = false;
			ArrayList<String> cidList = new ArrayList<String>();
			
			try {
				
				String custIdFromRequest = inputMap_ori.get("c_custid")[0];
				String rmk = inputMap_ori.get("pmtrmk")[0];
				String pmtDate = inputMap_ori.get("pmtdate")[0];
				if (inputMap_ori.containsKey("selectedcaseshidden") 
						&& inputMap_ori.get("selectedcaseshidden")[0]!=null
						&& !inputMap_ori.get("selectedcaseshidden")[0].equalsIgnoreCase("")) {
					cidList = Utilities.SplitStringToArrayList(inputMap_ori.get("selectedcaseshidden")[0] , ",");
					checkBoxPayment = true;
				}					
				double paidAmtForm = 0;

				if (! custIdFromRequest.equalsIgnoreCase(custId))
					throw new Exception ("Error, customer in form is ("+custIdFromRequest+") and global customer id is ("+custId+") are not the same");
				
				double calpmtAmt = 0.0;
				if (!checkBoxPayment) {
					String fromDate = inputMap_ori.get("fromdate")[0];
					String toDate = inputMap_ori.get("todate")[0];
					calpmtAmt = getTotAmtToPay (conn , custId , fromDate, toDate);
					//get list of cids
					cidList = getCid(conn , custId , fromDate, toDate);
				}else {
					calpmtAmt = getTotaAmtToPay (conn,cidList );
				}
				
				try {
					paidAmtForm = Double.parseDouble(inputMap_ori.get("totamt")[0]);
					if (paidAmtForm ==0 && cidList.isEmpty())
						throw new Exception ("لا توجد شحنات للتحاسب");
				}catch(NumberFormatException num) {
					throw new Exception ("مبلغ الدفع غير متوفر");
				}
				
				if (calpmtAmt != paidAmtForm)
					throw new Exception ("Error, payemnt amount in form is ("+paidAmtForm+"),and calculated amount is ("+calpmtAmt+") are not the same");
				
				// do validation for paidAmt must match the calcualted paidAmt and custid must match the global
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				int pmtId=0;

				java.util.Date javaDate = formatter.parse(pmtDate);
				Date date = new Date(javaDate.getTime());
				
				if (!cidList.isEmpty()) {//make sure u have cases
					ps = conn.prepareStatement( "INSERT INTO p_customer_payments "
							+ " 		(cp_custid, cp_amount_paid, cp_paymentdt, cp_createdby,cp_rmk)"
							+ "VALUES	(?		 , ?			 , ?		   , ?			 ,?)", Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, custId);
					ps.setDouble(2, calpmtAmt);
					ps.setDate(3, (Date) date);
					ps.setString(4, userId);
					ps.setString(5, rmk);
					ps.executeUpdate();
					rs = ps.getGeneratedKeys();
					rs.next();
					pmtId = rs.getInt(1);
					try {rs.close();} catch (Exception e) {}
					try {ps.close();} catch (Exception e) {}
					
					ps = conn.prepareStatement("update p_cases set c_pmtid=? , c_settled='FULL'  , c_paidinadvance=(case when c_paidinadvance='YES' then 'REFUNDED' else 'NO' end)   where c_id = ?  ");
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
		
			
		private double getTotaAmtToPay( Connection conn , ArrayList<String>caseList) throws Exception {
			double amt = 0.0;
			PreparedStatement pst = null;
			ResultSet rs = null;

			try {
				String sql = "select ifnull( sum( (	case when (q_stage='dlv_stg' and q_step='delivered')  then (c_receiptamt - c_shipment_cost) "
						+ " when ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='N' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then (c_shipment_cost - c_receiptamt) "
						+ " when ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='Y' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then (c_receiptamt*-1) "
						+ " else (1*-c_shipment_cost)  end)),0) as totdue "
						+ " from  p_cases join p_queue on (c_id= q_caseid and q_status !='CLS') where c_id in (";
				boolean first = true;
				
				for (String caseid : caseList) {
					if (!first) {
						sql += ",";
					}
					first = false;
					sql +="?";
				}
				sql +=")  and c_receiptfromsystem = 'N'  ";
				pst = conn.prepareStatement(sql);
				int i =1;
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
		
		
		private double getTotAmtToPay( Connection conn , String custid, String fromDate, String toDate) throws Exception {
			double amt = 0.0;
			PreparedStatement ps = null;
			ResultSet rs = null;
			boolean dateIsNotNull = false;
			try {
				if (fromDate !=null && !fromDate.trim().equalsIgnoreCase("")
						 && toDate !=null && !toDate.trim().equalsIgnoreCase("")) {
					dateIsNotNull = true;
				}

				String querycall = "select ifnull( "
						+ "	sum( (	case "
						+ "			when (q_stage='dlv_stg' and q_step='delivered')  then (c_receiptamt - c_shipment_cost) "
						+ "			when ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='N' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then (c_shipment_cost - c_receiptamt) "
						+ "			when ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='Y' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL') then (c_receiptamt*-1) "
						+ " 		else (1*-c_shipment_cost)  end)),0) "
						+ " from p_cases "
						+ " join p_queue on (c_id = q_caseid and q_status !='CLS' )"
						+ " where c_custid=?  and c_receiptfromsystem = 'N'  "
						+ " and ( 	   (q_stage='dlv_stg' and q_step='delivered' and c_settled ='NO') "
						+ "			or ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='Y' and c_settled ='NO') "
						+ " 		or ( q_stage='cncl' and q_status !='CLS' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL')) ";
					
				if(dateIsNotNull)
					querycall += "  and  (c_createddt >=? and c_createddt<=adddate(date(?),1))  ";
					
				ps = conn.prepareStatement(querycall); // create a statement
				ps.setString(1, custid); 
				if(dateIsNotNull) {
					ps.setString(2,fromDate);
					ps.setString(3,toDate);
				}
				rs = ps.executeQuery();
				while (rs.next()) {
					amt = rs.getDouble(1);
				}
			} catch (Exception e) {
				throw e;
			} finally {
				try {rs.close();} catch (Exception e) {/* ignore */}
				try {ps.close();} catch (Exception e) {/* ignore */
				}
			}
			return amt;
		}// end of get_cid


		private ArrayList<String> getCid( Connection conn , String custid, String fromDate, String toDate) throws Exception {
			ArrayList<String> cases = new ArrayList<String>();
			PreparedStatement ps = null;
			ResultSet rs = null;
			boolean dateIsNotNull = false;
			try {
				if (fromDate !=null && !fromDate.trim().equalsIgnoreCase("")
						 && toDate !=null && !toDate.trim().equalsIgnoreCase("")) {
					dateIsNotNull = true;
				}
				String querycall = "select c_id From p_cases  "
						+ " join p_queue on (c_id = q_caseid and q_status !='CLS' )"
						+ " where c_custid=?  and c_receiptfromsystem = 'N'  "
						+ " and ( 	   (q_stage='dlv_stg' and q_step='delivered' and c_settled ='NO') "
						+ "			or ( q_stage='cncl' and q_status !='CLS' and c_shipmentpaidbysender='Y' and c_settled ='NO') "
						+ " 		or ( q_stage='cncl' and q_status !='CLS' and  c_paidinadvance='YES' and c_advancepmtid>0 and c_settled ='FULL')) ";
					
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
					//System.out.println("this is one case--"+rs.getString("c_id"));
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
