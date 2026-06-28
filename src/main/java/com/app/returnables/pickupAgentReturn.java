package com.app.returnables;

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

import com.app.bussframework.FlowUtils;
import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

public class pickupAgentReturn extends CoreMgr{	
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
	public pickupAgentReturn() {
		records = 0;
		MainSql = "select concat('المتجر : ',cust_name, '<div style=\"float: left;\"><input type=\"checkbox\" "
				+ " onclick=\"checkAllCust(this,',cust_id,') \" id=\"check-customer-',cust_id,'\"></div>') as groupingcol, "
				+ "c_rmk , '' as pmtCheckBox, '' as selectedcases,'' as selectedcaseshidden, cust_name, q_stage, q_step, "
				+ " '' as status , 'شحنات سلمت وراجعه ' as title, c_custid,c_custreceiptnoori,'' as pmtrmk, "
				+ " date(c_createddt) as c_createddt, concat(st_name_ar,' - ',ifnull(c_rcv_addr_rmk,'')) as addr, c_id,"
				+ " c_rcv_name , c_rcv_hp1,  '' as fromdate, '' as todate, (case when q_stage='DLV' then c_partial_qtyrtn else c_qty end) as qty, "
				+ " c_changedprice, c_priceb4change, c_receiptamt as currentreceiptprice, c_rural"
				+ " from p_cases "
				+ " left join p_caseschain on cc_caseid = c_id and  cc_frombranch = {userstorecode} "
				+ " left join kbcustomers on cust_id = c_custid "
				+ " left join kbstate on st_code = c_rcv_state and st_branch = {userstorecode}"
				+ " where c_pickupagent ={pickupAgentRtnShipments} and c_pickupagent_rtnid=0  and c_cust_rtnid=0 and c_agentrtnid>0 "
				+ " and c_allowrtncustomer='Y' and q_step in('RTN_INSTORE','PART_SUCC') and (q_branch = c_branchcode)";
		userDefinedGroupColsOrderBy = "c_custid, c_custreceiptnoori";
		UserDefinedPageRows = 50000;
		userDefined_x_panelclass = "account_x_panel";
		userDefinedGroupByCol = "groupingcol";
		
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("qty");
		userDefinedGridCols.add("c_id");
		userDefinedGridCols.add("c_createddt");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("addr");
		
		userDefinedGridCols.add("status");
		userDefinedGridCols.add("c_rmk");
		userDefinedGridCols.add("pmtCheckBox");
		
		userDefinedColLabel.put("qty", "القطع الراحعة");
		userDefinedColLabel.put("c_id", "رقم الشحنه");
		userDefinedColLabel.put("c_rmk", "ملاحظات");
		userDefinedColLabel.put("c_createddt", "تاريخ الإدخال");
		userDefinedColLabel.put("c_rcv_name", "إسم المستلم");
		userDefinedColLabel.put("addr", "العنوان");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("selectedcases", "عدد الوصولات المحددة");
		userDefinedColLabel.put("pmtCheckBox", " ");		
		userDefinedColLabel.put("c_custid", "المتجر");
		userDefinedColLabel.put("pmtdate", "تاريخ التسليم");
		userDefinedColLabel.put("pmtrmk", "ملاحظات");
		userDefinedColLabel.put("status", "الحاله");
		userDefinedColLabel.put("fromdate","من تاريخ");
		userDefinedColLabel.put("todate","إلى تاريخ");
		userDefinedColLabel.put("cust_name", "المتجر");
		userDefinedColLabel.put("c_mastercustid", "العميل");
		
		userDefinedNewColsHtmlType.put("fromdate", "DATE");
		userDefinedNewColsHtmlType.put("todate", "DATE");
		userDefinedPageFooterFunction = "myFooterFunction()";
		canNew = true;
		canFilter = true;
		mainTable = "p_cases";
		keyCol = "c_id";
		
		
		userDefinedFilterCols.add("c_custid");
		userDefinedFilterColsHtmlType.put("c_custid", "MULTILIST");

		userDefinedNewCols.add("selectedcases");
		userDefinedNewCols.add("pmtdate");
		userDefinedNewColsDefualtValues.put("pmtdate", new String[] {"%select now()"});
		userDefinedReadOnlyNewCols.add("pmtdate");
		userDefinedNewCols.add("pmtrmk");
		userDefinedNewCols.add("selectedcaseshidden");
		userDefinedHiddenNewCols.add("selectedcaseshidden");
		
		userModifyTD.put("c_custreceiptnoori", "modifyReceiptNo({c_custreceiptnoori},{c_id})");
		userModifyTD.put("c_rcv_hp1", "modifyRcvNo({c_rcv_hp1})");
		userModifyTD.put("status", "modifyStatus({c_changedprice},{c_priceb4change}, {currentreceiptprice},{q_stage}, {q_step},{c_rural})");
		
		userDefinedColsMustFill.add("c_pickupagent");
		userDefinedNewColsDefualtValues.put("c_pickupagent", new String[] {"{pickupAgentRtnShipments}"});
		userDefinedLookups.put("c_custid", "select cust_id, cust_name from kbcustomers where cust_assigned_pickup_agent={pickupAgentRtnShipments}");
			
		userDefinedNewColsHtmlType.put("fromdate", "DATE");
		userDefinedNewColsHtmlType.put("todate", "DATE");
		userDefinedNewColsHtmlType.put("pmtrmk", "TEXTAREA");
		userDefinedNewColsHtmlType.put("c_custid", "DROPLIST");
		userDefinedReadOnlyNewCols.add("c_custid");
		userDefinedNewColsHtmlType.put("selectedcases", "TEXT");
		userDefinedReadOnlyNewCols.add("selectedcases");
		userDefinedNewCaption = "تسليم الرواجع للزبون";
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");//to remove the comma
		userDefinedColsTypes.put("c_rcv_hp1", "VARCHAR");//to remove the comma
		//myhtmlmgr.tableClass = "table table-striped  table-bordered turquoise_table";
		userModifyTD.put("pmtCheckBox", "displayCheckBox({c_id},{c_custid})");
				
		userDefinedCaption = "تسليم الراجع للعميل";
				
		System.out.print(MainSql);
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
		int pickupAgent = Integer.parseInt(replaceVarsinString(" {pickupAgentRtnShipments} ", arrayGlobals).trim());
		int userStorCode = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		Connection conn2 = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String custRecieptNo ="";
		String custHp = "";
		userDefinedCaption= "<div class=\"row\" style='margin-bottom: 15px;'>"
				+ "				<div class=\"col-sm-1 col-sm-offset-1\">"
				+ "					<label>Barcode</label>"
				+ "				</div>"
				+ "				<div class=\"col-sm-4\">"
				+ "					<input type='text' style='color:#424242;background-color:#E9E5E5;' id ='barcode_checker' />"
				+ "				</div>"
				+ "			</div>";
		try {
			conn2 = mysql.getConn();
			pst = conn2.prepareStatement("select c_custreceiptnoori "
				+ " from p_cases "
				+ " where  c_pickupagent =?   "
				+ "  and c_allowrtncustomer='Y' and q_step in('RTN_INSTORE','PART_SUCC') and c_cust_rtnid=0 and c_agentrtnid>0 and q_branch=c_branchcode "
				+ "  order by c_custreceiptnoori");
			pst.setInt(1,pickupAgent);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getString("c_custreceiptnoori").equalsIgnoreCase(custRecieptNo))
					repeatedNo.put(custRecieptNo, custRecieptNo);
				
				custRecieptNo = rs.getString("c_custreceiptnoori");
			}
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
			pst = conn2.prepareStatement("select c_rcv_hp1 "
					+ " from p_cases "
					+ " where  c_pickupagent =? "
					+ " and c_allowrtncustomer='Y' and q_step in('RTN_INSTORE','PART_SUCC') and c_cust_rtnid=0 and c_agentrtnid>0 "
					+ "  order by c_rcv_hp1");
				pst.setInt(1,pickupAgent);
				rs = pst.executeQuery();
				while (rs.next()) {
					if (rs.getString("c_rcv_hp1").equalsIgnoreCase(custHp))
						repeatedHp.put(custHp, custHp);
					
					custHp = rs.getString("c_rcv_hp1");
				}
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}			
				String sql = "select c_custreceiptnoori "
						+ " from p_cases "
						+ " where c_allowrtncustomer='Y' and q_step in('RTN_INSTORE','PART_SUCC') and c_pickupagent =?  and c_cust_rtnid=0 and c_agentrtnid>0 and q_branch=c_branchcode and  c_id in (";
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
				int i =2, totCases = 0;
				pst.setInt(1, pickupAgent);
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
				userDefinedNewColsDefualtValues.put("selectedcases",  new String [] {casesToPayList.size()+""} );
				userDefinedNewCols.remove("fromdate");
				userDefinedNewCols.remove("todate");
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
		setRecords(getRecords() + 1);
		String s = "<td><input type=\"checkbox\" "
				+ " id=\"pmtcheck_"+hashy.get("c_id")+"\"  data-single-check-custid-"+hashy.get("c_custid")+" ='"+hashy.get("c_custid")+"'  onclick=\"checkBoxPmtClicked(this, "+hashy.get("c_id")+")\">";
		s +="</td>";
		return s;	
	}
	
	public String modifyStatus (HashMap<String,String> hashy) {
		String msg  ="";
		String html = "<td>";
		
		
		if (hashy.get("q_stage").equalsIgnoreCase("CNCL") ) {
			msg ="راجع";
			noOfRtnitems++;
		}else if (hashy.get("q_stage").equalsIgnoreCase("DLV") ) {
			if (hashy.get("q_step").equalsIgnoreCase("PART_SUCC"))
				msg ="تم التسليم - راجع جزئي";
			else {
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
	

	public String myFooterFunction(String colName) {
		if (colName.equalsIgnoreCase("c_custreceiptnoori") ) {
			if (!errorFlag) {
				return "<td colspan='3' stye='font-size: 15px;' align='center'>"
						+ "<form action=\"?\" id='pickup-prepare-return-form' method=\"post\" style=\"display: inline;\" ><input type=\"hidden\" name=\"op\" value=\"new\"><input type=\"hidden\""
						+ " name=\"myClassBean\" value=\"com.app.returnables.pickupAgentReturn\">"
						+ "<input type=\"hidden\" name=\"selected_casesto_pay\" value='' id='selected_casesto_pay'>"
						+ "<button type=\"submit\" class=\"btn btn-dark btn-sm\" name=\"smarty_newformbtn\" value=\"newform\">تسليم الراجع</button></form></td>";
			}else {
				return "<td colspan='3' stye='font-size: 15px;' align='center'>هنالك خطأ في النظام , لايمكن اجراء الاستلام الأن</td>";
			}
		}else if (colName.equalsIgnoreCase("c_id") ||  colName.equalsIgnoreCase("c_createddt")) {
			return "";
		}else
			return "<td></td>";
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
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autoCommit) {
		String statusMsg = " تم تسجيل الراجع ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String userLoginId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		int pickupAgentId = Integer.parseInt(replaceVarsinString(" {pickupAgentRtnShipments} ", arrayGlobals).trim());
		int userStorCode = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		UtilitiesFeqar ut = new UtilitiesFeqar();
		int pir_id = 0;
		boolean foundOpen = false;
		try {
			inputMap_ori = filterRequest(rqs);
			String rmk = inputMap_ori.get("pmtrmk")[0];
			String pmtDate = inputMap_ori.get("pmtdate")[0];
			// do validation for paidAmt must match the calcualted paidAmt and pickupAgentId must match the global
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			

			java.util.Date javaDate = formatter.parse(pmtDate);
			Date date = new Date(javaDate.getTime());
			//get list of cids
			ArrayList<String> cidList = new ArrayList<String>();
			if (inputMap_ori.containsKey("selectedcaseshidden") 
					&& inputMap_ori.get("selectedcaseshidden")[0]!=null
					&& !inputMap_ori.get("selectedcaseshidden")[0].equalsIgnoreCase("")) {
				cidList = Utilities.SplitStringToArrayList(inputMap_ori.get("selectedcaseshidden")[0] , ",");
				
			}/*else {
				 cidList = getCid(pickupAgentId, conn, userStorCode);
				 
			}*/
			
			if (!cidList.isEmpty()) {//make sure u have cases
				if(ut.checkReturnbackedtoAgentOrCustomer(conn, cidList))
					return "لقد تم ارجاع الشحنات مسبقاً يرجى المحاولة من جديد";
				ps = conn.prepareStatement("select pir_id from p_pickupagent_return where pir_closed = 'N' and pir_deleted='N' and pir_pickupagentid=? ");
				ps.setInt(1, pickupAgentId);
				rs = ps.executeQuery();
				if(rs.next()) {
					foundOpen = true;
					pir_id = rs.getInt("pir_id");
				}
				try {rs.close();} catch (Exception e) {}
				try {ps.close();} catch (Exception e) {}
				
				if (!foundOpen) {
					ps = conn.prepareStatement( "INSERT INTO p_pickupagent_return "
							+ " 		(pir_pickupagentid, pir_retuneddt, pir_createdby, pir_rmk)"
							+ "VALUES	(?		    , ?			   , ?		      , ?		)", Statement.RETURN_GENERATED_KEYS);
					ps.setInt(1, pickupAgentId);
					ps.setDate(2, (Date) date);
					ps.setString(3, userLoginId);
					ps.setString(4, rmk);
					ps.executeUpdate();
					rs = ps.getGeneratedKeys();
					rs.next();
					pir_id = rs.getInt(1);
					try {rs.close();} catch (Exception e) {}
					try {ps.close();} catch (Exception e) {}
				}
				ps = conn.prepareStatement("update p_cases set c_pickupagent_rtnid=?  where c_id=?");
				for (int i =0; i<cidList.size(); i++) {
					ps.setInt(1, pir_id);
					ps.setString(2, cidList.get(i));
					ps.executeUpdate();
					ps.clearParameters();

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
	
	/*private ArrayList<String> getCid(int custid, Connection conn, int userBranchId) throws Exception {
		ArrayList<String> cases = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String querycall = "select c_id From p_cases "
				+ " where q_status !='CLS' and c_pickupagent =?  "
				+ " and c_allowrtncustomer='Y' and q_step in('RTN_INSTORE','PART_SUCC')"; //any stage of cancel and delivered

			ps = conn.prepareStatement(querycall); // create a statement 
			ps.setInt(1, custid);
			rs = ps.executeQuery();
			while (rs.next()) {
				cases.add(rs.getString("c_id"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) { ignore }
			try {ps.close();} catch (Exception e) { ignore 
			}
		}
		return cases;
	}// end of get_cid
*/

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

