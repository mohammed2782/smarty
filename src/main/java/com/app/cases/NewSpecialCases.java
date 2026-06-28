package com.app.cases;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.bussframework.FlowUtils;
import smarty.core.CoreMgr;
import smarty.core.html.DropList;
import smarty.db.mysql;

import com.app.util.Utilities;

public class NewSpecialCases extends CoreMgr{
	
	private LinkedList <CaseInformation> cases ;
	private int userStoreCode = 0;
	public NewSpecialCases () {
		// I NEED TO CREATE MULTI-INSERT-FORM
		
		setDisplayMode("NEWSINGLE");
		MainSql =  "select '' as assignagent, "
				+ " '' as createddtls,"
				+ " '' as c_mastercustid, '' as newmastercustomerflag, '' as newmastercustomername, '' as c_mastercusthp,  '' as mastercustnameshowonly, "
				+ " '' c_custid, '' as newcustomerflag , '' newcustomername, '' as c_cust_hp,"
				+ " '' as c_branchcode , '' as c_pickup_city, '' as c_pickup_district , '' as c_pickup_more_location,"
				+ " '' as item1 , ''  "
				+ " from p_cases where 1=0"; 
		canNew = true;
		mainTable = "p_cases";
		cases = new LinkedList<CaseInformation>();
		userDefinedNewFormColNo = 4;
		userDefinedFieldSetCols.put("newcustomerflag", "العميل");
		userDefinedFieldSetEndWithCols.add("mastercustnameshowonly");
		// master customer
		userDefinedNewCols.add("newcustomerflag");
		userDefinedNewCols.add("c_mastercustid");
		userDefinedNewCols.add("c_custid");
		userDefinedNewCols.add("newcustomername");
		userDefinedNewCols.add("c_cust_hp");
		userDefinedNewCols.add("mastercustnameshowonly");
		
		
		//userDefinedNewColsHtmlType.put("newmastercustomerflag", "CHECKBOX");
		userDefinedNewColsHtmlType.put("newcustomerflag", "CHECKBOX");
		userDefinedNewColsHtmlType.put("c_mastercusthp", "TEXT");
		userDefinedNewColsHtmlType.put("c_cust_hp", "TEXT");
		userDefinedNewColsHtmlType.put("c_cust_hp", "TEXT");
		userDefinedNewColsHtmlType.put("c_mastercustid","DROPLIST");
		userDefinedNewColsHtmlType.put("c_branchcode","TEXT");
		userDefinedNewColsHtmlType.put("c_custid","DROPLIST");
		userDefinedNewColsHtmlType.put("mastercustnameshowonly","TEXT");
		userDefinedDisabledNewCols.add("mastercustnameshowonly");
		
		userDefinedLookups.put("rcv_broken_1", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("newcustomerflag", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("newmastercustomerflag", "select 'Y','' from dual");
		userDefinedLookups.put("newcustomerflag", "select 'Y','' from dual");
		userDefinedLookups.put("c_cust_hp", "!select cust_phone1, cust_phone1 as ph  from kbcustomers where cust_id='{c_custid}' ");
		userDefinedLookups.put("mastercustnameshowonly", "!select mcust_name, mcust_name from kb_mastercustomer where "
				+ " mcust_id in (select cust_mastercustid from kbcustomers where cust_id='{c_custid}') ");
		userDefinedHiddenNewCols.add("c_mastercustid");
		userDefinedHiddenNewCols.add("newcustomername");
		
		userDefinedColLabel.put("mastercustnameshowonly", "إسم العميل");
		userDefinedColLabel.put("c_custid", "المتجر");
		userDefinedColLabel.put("c_mastercusthp", "هاتف العميل");
		userDefinedColLabel.put("c_branchcode", "الفرع");
		userDefinedColLabel.put("newmastercustomerflag", "عميل جديد؟");
		userDefinedColLabel.put("newcustomerflag", "متجر جديد؟");
		userDefinedColLabel.put("c_cust_hp", "هاتف المتجر");
		userDefinedColLabel.put("c_cust_hp", "هاتف المتجر");
		userDefinedColLabel.put("newcustomername", "المتجر *");
		userDefinedColLabel.put("newmastercustomername", "إسم العميل");
		userDefinedColLabel.put("c_mastercustid", "العميل *");
		userDefinedReadOnlyNewCols.add("c_branchcode");
		
		userDefinedColLabel.put("c_pickup_state","مخزن");
		userDefinedHiddenNewCols.add("c_pickup_state");
		
		userDefinedColsMustFill.add("c_cust_hp");
		userDefinedColsMustFill.add("c_custid");
		
		userDefinedNewCaption = "خلق شحنه جديده";
		 
	}
	@Override
	public void initialize(HashMap smartyStateMap){
		int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers where cust_branch="+userstorecode+"  ");
		userDefinedLookups.put("c_mastercustid", "select mcust_id , mcust_name from kb_mastercustomer"
				+ "  where mcust_branchcode="+userstorecode+" and mcust_active='Y' ");
		 userDefinedLookups.put("rcv_state_1", "select distinct path_state, st_name_ar from kbpaths join kbstate on path_state = st_code "
					+ " where path_frombranch ="+userstorecode+" and st_active='Y' order by st_order");
		 this.userStoreCode = userstorecode;
	 super.initialize(smartyStateMap);
	 sqlColsSizes.put("c_cust_hp", 11);
	 sqlColsSizes.put("c_mastercusthp", 11);
	 
	 
	}
	
	public String userDefinedInjectCodeInNewFromBeforeSubmitButtonAndAFterFields() {
		StringBuilder sb = new StringBuilder("");
		sb.append("<h6 class='mb-1'>تفاصيل الشحنات</h6>");
		sb.append(
				"<table class='table table-bordered table-striped' style='padding:0px;border-width: 0px; ' id='rcv_dtls'>");
		sb.append(getRCVDetailsRow(1, lu.getBranchCode()));
		for (int c = 2; c <= cases.size(); c++)
			sb.append(getRCVDetailsRow(c, lu.getBranchCode()));
		sb.append("</table>");
		sb.append(
				"<div class='row'><div class='col-xl-2 col-sm-3'>"
				+ "<button type='button' id='add_rcv_dtls' class='btn btn-secondary btn-min-width btn-glow mr-1 mb-1 waves-effect waves-light' >إضافة وصل آخر <i class=\"ft-plus-circle\"></i>"
				+ "</button></div></div>");
		return sb.toString();
	}

	
	public StringBuilder getRCVDetailsRow(int rcvSeq, int a_branchCode) {
		StringBuilder sb = new StringBuilder();
		userDefinedMultiNewRowExtension += "_"+rcvSeq;
		String stylediv = "style=''";
		Utilities ut = new Utilities();
		Connection conn1 = null;
		boolean ruralArea = false;
		int rcvDistrict = 0;
		String defaultRcvName = "", fragile = "", destState="BGD", rural ="N", rmk="", locDtls="",  brinBackItmes="", receiptAmt="", 
				qty="1", shipmentCost = "0" , receiptNo="", labelClass="form-label", checked = "", divCLass="col-md-2 col-sm-5 col-xs-3" ;
		int smarty_new_row_seq= rcvSeq;
		String divStyle= "margin-bottom:7px;";
		try {
			conn1 = mysql.getConn();
			if (!cases.isEmpty() && cases.get(rcvSeq-1)!=null) {
				CaseInformation ci = cases.get(rcvSeq-1);
				defaultRcvName = ci.getReceiverName();
				rural = ci.getRural();
				locDtls = ci.getLocationDetails();
				rmk = ci.getRmk();
				qty=ci.getQty()+"";
				fragile = ci.getFragile();
				receiptAmt = ci.getReceiptAmtIqd()+"";
				shipmentCost = ci.getShipmentCharge()+"";
				receiptNo = ci.getCustReceiptNoOri();
				smarty_new_row_seq = ci.getSmarty_new_row_seq();
				
				rcvDistrict = ci.getDistrict();
			}
			if (rural.equalsIgnoreCase("Y"))
				ruralArea = true;
			//shipmentCost = Double.toString(ut.calcShipmentChargesBasedOnDestCity(conn1,  destState, ruralArea, custName, userStoreCode));
			sb.append("<tr id='" + userDefinedMultiNewRowExtension + "' rcv_no = '" + smarty_new_row_seq
					+ "' style=\"border-bottom: 2px solid;\">");
			sb.append("<input type='hidden' id='smarty_new_row_seq__" + smarty_new_row_seq
					+ "' name='smarty_new_row_seq__" + smarty_new_row_seq + "'  value='" + smarty_new_row_seq + "'/>");

			// start TD and DIV
			sb.append("<td style='padding-top:10px;width:95%;border-width: 0px; border-bottom: 1px solid #00000054;'>"
					+ "<div class='row'>");
				
			// 1-Receipt no
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-left:1%;margin-right:1%;'
					+ "<label class='" + labelClass + "'>رقم الوصل</label>" +"<i class=\"ft-package\"></i>"
					+ "<input type='number' value='" + receiptNo
					+ "' class='form-control' min='0' style='text-align:right;background-color: white;' "
					+ "oninput=\"this.value = Math.abs(this.value)\" size='10' name='c_custreceiptnoori_"
					+ userDefinedMultiNewRowExtension + "' " + "id ='c_custreceiptnoori_"
					+ userDefinedMultiNewRowExtension + "' required /></div>");
			// end of receipt no

			// 2-Receiver Phone 1
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-left:1%;margin-right:10%;'
					+ "<label class='" + labelClass + "'> 1 هاتف المستلم</label>"+ "<i class=\"ft-phone-call\"></i>"
					+ "<input type=\"tel\" style=\"text-align:right;background-color: white;"
					+ " direction: ltr;\" name='rcv_phone1_"+ userDefinedMultiNewRowExtension + "' " 
					+ "id='rcv_phone1_" + userDefinedMultiNewRowExtension + "' class=\"form-control border-start-0\""
					+ "value='' required maxlength=\"11\" size=\"11\" pattern='[0]{1}[7]{1}[0-9]{9}'>"
					+ "</div>");

			// 3-Receiver Phone 2
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-left:1%;margin-right:10%;'
					+ "<label class='" + labelClass + "'> 2 هاتف المستلم</label>" + "<i class=\"ft-phone-call\"></i>" 
					+ "<input type=\"tel\" style=\"text-align:right;background-color: white;"
					+ " direction: ltr;\" name='rcv_phone2_"+ userDefinedMultiNewRowExtension + "' " 
					+ "id='rcv_phone2_" + userDefinedMultiNewRowExtension + "' class=\"form-control border-start-0\""
					+ "value=''  maxlength=\"11\" size=\"11\" pattern='[0]{1}[7]{1}[0-9]{9}'>"
					+ "</div>");

			// 4-Receipt Price
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-left:1%;margin-right:1%;'
					+ "	<label class='" + labelClass + "'>مبلغ الوصل</label>"+"<i class=\"ft-dollar-sign\"></i>"
					+ "<input type='text' value='" + receiptAmt
					+ "' class='form-control' style='text-align:right;background-color: white;' "
					+ " size='10' name='c_receiptamt_"
					+ userDefinedMultiNewRowExtension + "' " + "id ='c_receiptamt_"
					+ userDefinedMultiNewRowExtension + "' required onkeyup='formatMe(this);' />"
					+ "<script>$(function() {" + 
    				"    new AutoNumeric('#c_receiptamt_"+userDefinedMultiNewRowExtension+"', {" + 
    						"    unformatOnSubmit: AutoNumeric.options.unformatOnSubmit.unformat, "+
    						" allowDecimalPadding: false " +
    						"});" + 
    				"});</script>"
					+ "</div>");
			// end of receipt amount

			/*// 10- No of Pieces //
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" + "<label class='" + labelClass
					+ "'>عدد القطع</label>" + "<div class=\"input-group\">"
					+ "<span class='input-group-text'><i class='fadeIn animated bx bx-cube-alt'></i></span>"
					+ "<input type='number' class='form-control border-start-0' min='1' value='" + qty
					+ "' size='1' style='text-align:right; '"
					+ " oninput=\"this.value = Math.abs(this.value)\"  name='rcv_qty_" + userDefinedMultiNewRowExtension
					+ "' " + "id ='rcv_qty_" + userDefinedMultiNewRowExtension + "' required='required' />"
					+ "</div></div>");*/

			// 5-state
			String style = "text-align:right;  padding: 0 10px 0 10px;"
					+ "   border: 1px solid #7dc6dd;min-width:150px";

			if (colMapValues == null) {
				HashMap<String,String> lookupsToLoad = new HashMap<String,String>();
				lookupsToLoad.put("rcv_state_1",
						"select distinct path_state, st_name_ar from kbpaths join kbstate on path_state = st_code "
								+ " where path_frombranch =" + a_branchCode + " and st_active='Y' order by st_order");
				colMapValues = mysqlmgr.loadAllLookups(conn1, lookupsToLoad);
			}
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>"
					+ "<label class='" + labelClass + "'>المحافظة</label> <i class=\"ft-map\"></i>" 
					+" <select class='form-control select2' onchange='calcShipmentCost("+ rcvSeq + ");loadDistrict(" + rcvSeq + ");' "
					+ "id='rcv_city_" + userDefinedMultiNewRowExtension + "' "
					+ " name='rcv_city_" + userDefinedMultiNewRowExtension + "' style='" + style + "' required>");
			Map<String, String> lookupsmap = colMapValues.get("rcv_state_1");
			if (lookupsmap != null && !lookupsmap.isEmpty()) {
				for (String code : lookupsmap.keySet()) {
					if (destState.equalsIgnoreCase(code))
						sb.append("<option value='" + code + "' selected>" + lookupsmap.get(code) + "</option> \n");
					else
						sb.append("<option value='" + code + "' >" + lookupsmap.get(code) + "</option> \n");
				}
			}
			sb.append("</select></div>");
			// end of state

			// 6- district inside state
			LinkedHashMap<Integer, String> district = new LinkedHashMap<Integer, String>();
			district = ut.getDistrictOfState(conn1, "BGD");

			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-right:7%;margin-left:2%;'
					+ "<label class='" + labelClass + "'>المنطقه</label><i class=\"ft-map-pim\"></i>"
					+ "<select class='form-control select2' onchange='districtChanged("
					+ rcvSeq + ");'  id='rcv_district_" + userDefinedMultiNewRowExtension + "' "
					+ " name='rcv_district_" + userDefinedMultiNewRowExtension + "' style='" + style + "' required >");
			sb.append("<option value='' selected></option> \n");
			for (int code : district.keySet()) {
				if (rcvDistrict == code)
					sb.append("<option value='" + code + "' selected>" + district.get(code) + "</option> \n");
				else
					sb.append("<option value='" + code + "' >" + district.get(code) + "</option> \n");
			}
			sb.append("</select></div> \n");

			// 5- location details
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" 
			+ "<label class='" + labelClass+ "'>تفاصيل العنوان</label>" +"<i class=\"ft-map-pim\"></i>" 
					+ "<textarea class='form-control' style=\"text-align:right;background-color: white;\" name='rcv_more_loc_"+ userDefinedMultiNewRowExtension + "' "
							+ "id ='rcv_more_loc_" + userDefinedMultiNewRowExtension + "'>"
					+ locDtls + "</textarea></div>");
			
			// 6- Notes  
/*			sb.append(
			"<div class='"+divCLass+"' style='"+divStyle+"'>" // style='margin-right:13%'
			+ "<label class='"+labelClass+"'>تفاصيل البضاعة</label>"
			+ "<div class=\"input-group\">"
					+ "<span class='input-group-text'><i class='fadeIn animated bx bx-box'></i></span>"
			+" <textarea required class='form-control border-start-0 border-start-0' name='rcv_itemsdlts_"+userDefinedMultiNewRowExtension+"' id ='rcv_itemsdlts_"+userDefinedMultiNewRowExtension+"'>"
			+rmk+"</textarea></div></div>");*/
						
			// 6- Notes //
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-right:13%'
					+ "<label class='" + labelClass + "'>ملاحظات</label>" + "<i class=\"ft-message-square\"></i> "
					+ " <textarea class='form-control' style=\"text-align:right;background-color: white;\" name='rcv_rmk_" + userDefinedMultiNewRowExtension
					+ "' id ='rcv_rmk_" + userDefinedMultiNewRowExtension + "'>" + rmk + "</textarea></div>");
			//sb.append("</div>");// end of dive row

			
			/*//7 - rural areas // سيف كال اخفيها لانو ما احتاجها
			if (rural.equalsIgnoreCase("Y")) 
				checked="checked";
			sb.append(
			"<div class='"+divCLass+"' style='"+divStyle+"'>" // "+stylediv+"
			+ "<label class='"+labelClass+"'>أطراف</label>"
			+ "<input type='checkbox' class='form-check-input' name='c_rural_"+userDefinedMultiNewRowExtension+"' "
			+ "id ='c_rural_"+userDefinedMultiNewRowExtension+"' "+checked+" value='Y' onclick=\"calcShipmentCost("+rcvSeq+");\" />"
			+"</div>");*/
		
			// 8- Receiver Name
			/*sb.append(
			"<div class='"+divCLass+"' style='"+divStyle+"'>" // "+stylediv+"
			+ "<label class='"+labelClass+"'>المستلم</label>"
			+ "<div class=\"input-group\">"
			+ "<span class='input-group-text'><i class='bx bxs-user'></i></span>"
			+ "<input type='text' class='form-control border-start-0' style='text-align:right; ' name='rcv_name_"+userDefinedMultiNewRowExtension+"' "
			+ "id ='rcv_name_"+userDefinedMultiNewRowExtension+"' size='15'"
			+ "value='"+defaultRcvName+"'  /></div>"
			+"</div>");*/
			
			// 7-agent share cost	
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-left:1%;margin-right:1%;'
					+ "<label class='" + labelClass + "'>أجور المندوب</label>" +"<i class=\"ft-package\"></i>"
					+ "<input type='text' value='" + defaultRcvName
					+ "' class='form-control' min='0' style='text-align:right;background-color: white;' "
					+ " size='10' name='c_agentshare_"
					+ userDefinedMultiNewRowExtension + "' " + "id ='c_agentshare_"
					+ userDefinedMultiNewRowExtension + "' required />"
					+ "<script>$(function() {" + 
    				"    new AutoNumeric('#c_agentshare_"+userDefinedMultiNewRowExtension+"', {" + 
    						"    unformatOnSubmit: AutoNumeric.options.unformatOnSubmit.unformat, "+
    						" allowDecimalPadding: false " +
    						"});" + 
    				"});</script>"
					+ "</div>");
			// end of agent share cost


			// 8-shipment cost	
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-left:1%;margin-right:1%;'
					+ "<label class='" + labelClass + "'>مبلغ الشحن</label>" +"<i class=\"ft-dollar-sign\"></i>"
					+ "<input type='text' value='' class='form-control' min='0' style='text-align:right;background-color: white;' "
					+ " size='10' name='c_shipment_cost_"
					+ userDefinedMultiNewRowExtension + "' " + "id ='c_shipment_cost_"
					+ userDefinedMultiNewRowExtension + "' required />"
					+ "<script>$(function() {" + 
    				"    new AutoNumeric('#c_shipment_cost_"+userDefinedMultiNewRowExtension+"', {" + 
    						"    unformatOnSubmit: AutoNumeric.options.unformatOnSubmit.unformat, "+
    						" allowDecimalPadding: false " +
    						"});" + 
    				"});</script>"
					+ "</div>");
			// end of shipment cost

			
			// agent share cost	
//			sb.append(
//			"<div class='"+divCLass+"' style='"+divStyle+"'>" // "+stylediv+"
//			+ "<label class='"+labelClass+"'>أجور المندوب</label>"
//			+ "<div class=\"input-group\">"
//			+ "<span class='input-group-text'><i class='fadeIn animated bx bx-car'></i></span>"
//			+ "<input type='number'  min='0' class='form-control border-start-0' style='text-align:right; ' name='c_agentshare_"+userDefinedMultiNewRowExtension+"' "
//			+ "id ='c_agentshare_"+userDefinedMultiNewRowExtension+"' size='15' required "
//			+ " value='"+defaultRcvName+"'  /></div>"
//			+"</div>");
//		
//			// shipment cost	
//			
//			sb.append(
//			"<div class='"+divCLass+"' style='"+divStyle+"'>" // "+stylediv+"
//			+ "<label class='"+labelClass+"'>مبلغ الشحن</label>"
//			+ "<div class=\"input-group\">"
//			+ "<span class='input-group-text'><i class='fadeIn animated bx bx-money'></i></span>"
//			+ "<input type='number'  min='0' class='form-control border-start-0' style='text-align:right; ' name='c_shipment_cost_"+userDefinedMultiNewRowExtension+"' "
//			+ "id ='c_shipment_cost_"+userDefinedMultiNewRowExtension+"' size='15' required "
//			+ " value='"+defaultRcvName+"'  /></div>"
//			+"</div>");
			
			

			
			//11-shipment cost, saif dont want it on meeting 8 - may - 2021
			/*sb.append(
			"<div class='"+divCLass+"' >" // style='margin-left:2%;margin-right:2%;'
			+ "<label class='"+labelClass+"'>مبلغ الشحن</label>"
			+ "<input type='number' min='0' readonly  class='form-control' value='"+shipmentCost+"' style='width:8em;'  size='10' "
					+ "name='c_shipment_cost_"+userDefinedMultiNewRowExtension+"' id ='c_shipment_cost_"+userDefinedMultiNewRowExtension+"' required  />"
			+ "</div>");*/
			
			sb.append("</div>");// end of div row
			sb.append("</td>");
			sb.append("<td style='width:5%;vertical-align: bottom; padding: 0; border-width: 0px; border-bottom: 1px solid #00000054;'>"
					+ "<table style='border: 0px solid;' id='side_table_" + smarty_new_row_seq + "'><tr>"

					+ "<td style='border: 0;'>" + "<button type='button' onclick='remove_row("
					+ smarty_new_row_seq + ")' "
					+ " class='btn btn-danger btn-sm'><li class='fa fa-trash'></li></button>" + "</td>"
					+ "<td style='border: 0;'>"
					+ "<span class='badge bg-secondary rounded-pill text-white ' style='font-size: 17px;'>"
					+ smarty_new_row_seq + "</span>" + "</td>" + "</tr>" + "</table>" + "</td>");
			sb.append("</tr>");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn1.close();}catch(Exception e) {}
		}
		return sb;
	}
	
	
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autocommit) { 
		FlowUtils fu = new FlowUtils();
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean createNewCustomer = false;
		int custId = 0;
		int userId = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		String msg = "تم خلق الطلبيه بنجاح";
		Utilities ut = new Utilities();
		boolean ruralArea = false;
		double agentShareAmt = 0;
		try{
			inputMap_ori = filterRequest(rqs);
			conn = mysql.getConn();
			MasterCaseInformation caseMaster = new MasterCaseInformation();
			LinkedList <Integer> availableCases= new LinkedList <Integer>();
			for (String key:inputMap_ori.keySet()){//loop to get all the cases from the grid
			    if(key.startsWith("smarty_new_row_seq__")) {
			    	availableCases.add(Integer.parseInt(inputMap_ori.get(key)[0]));
			    	
				}
			}
			caseMaster.setHp(inputMap_ori.get("c_cust_hp")[0]);
			caseMaster.setBranch(currentBranch);
			if (inputMap_ori.containsKey("newcustomerflag") && inputMap_ori.get("newcustomerflag")[0] !=null && inputMap_ori.get("newcustomerflag")[0].equalsIgnoreCase("Y") ) {
				if (inputMap_ori.get("newcustomername")[0] !=null && inputMap_ori.get("newcustomername")[0].trim().length()>0 )
					if (inputMap_ori.get("c_mastercustid")[0] !=null && inputMap_ori.get("c_mastercustid")[0].trim().length()>0 ) {
						caseMaster.setMasterCustomerId(Integer.parseInt(inputMap_ori.get("c_mastercustid")[0]));
						createNewCustomer = true;
					}
			}else {
				//if (inputMap_ori.get("c_custid")[0]==null ||inputMap_ori.get("c_custid")[0].trim().equalsIgnoreCase("")) {
				caseMaster.setCustId(Integer.parseInt(inputMap_ori.get("c_custid")[0]));
				createNewCustomer = false;
			}
			
			if(createNewCustomer) {//create new customer
				pst = conn.prepareStatement("insert into kbcustomers "
						+ "		 (cust_name, cust_phone1, cust_createdby, cust_branch, cust_mastercustid)"
						+ "values(?		   , ?			, ?				, ?		  	 , ?)", Statement.RETURN_GENERATED_KEYS);
				pst.setString(1, inputMap_ori.get("newcustomername")[0]);
				pst.setString(2, caseMaster.getHp());
				pst.setInt(3, userId);
				pst.setInt(4, caseMaster.getBranch());
				pst.setInt(5, caseMaster.getMasterCustomerId());
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				rs.next();
				custId = rs.getInt(1);
				caseMaster.setCustId(custId);
				try {rs.close();}catch(Exception e) {/*ignore*/}
				try {pst.close();}catch(Exception e) {/*ignore*/}
			}
			pst = conn.prepareStatement("select cust_mastercustid, mcust_phone1 from kbcustomers join kb_mastercustomer on cust_mastercustid = mcust_id  where cust_id=?");
			pst.setInt(1, caseMaster.getCustId());
			rs = pst.executeQuery();
			if (rs.next()) {
				caseMaster.setMasterCustomerId(rs.getInt("cust_mastercustid"));
				caseMaster.setMasterCustHp1(rs.getString("mcust_phone1"));
			}
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			// now for cases
			for (Integer j : availableCases) {
				agentShareAmt = 0;
				CaseInformation ci = new CaseInformation();
				ci.setSmarty_new_row_seq(j);
				//ci.setReceiverName(inputMap_ori.get("rcv_name_"+userDefinedMultiNewRowExtension+"_"+j)[0]); //rcv name
				ci.setReceiverHp1(inputMap_ori.get("rcv_phone1_"+userDefinedMultiNewRowExtension+"_"+j)[0]); // rcv phone 1
				ci.setReceiverHp2(inputMap_ori.get("rcv_phone2_"+userDefinedMultiNewRowExtension+"_"+j)[0]); // rcv phone 1
				ci.setState(inputMap_ori.get("rcv_city_"+userDefinedMultiNewRowExtension+"_"+j)[0]);// rcv city
				ci.setDistrict(Integer.parseInt(inputMap_ori.get("rcv_district_"+userDefinedMultiNewRowExtension+"_"+j)[0]));
				ci.setLocationDetails(inputMap_ori.get("rcv_more_loc_"+userDefinedMultiNewRowExtension+"_"+j)[0]);//location dtls
				ci.setRmk(inputMap_ori.get("rcv_rmk_"+userDefinedMultiNewRowExtension+"_"+j)[0]); // remarks
				//ci.setProductInfo(inputMap_ori.get("rcv_itemsdlts_"+userDefinedMultiNewRowExtension+"_"+j)[0]); // remarks
				//ci.setQty(Integer.parseInt(inputMap_ori.get("rcv_qty_"+userDefinedMultiNewRowExtension+"_"+j)[0])); //no of items
				ci.setReceiptAmtIqd(Double.parseDouble(inputMap_ori.get("c_receiptamt_"+userDefinedMultiNewRowExtension+"_"+j)[0].replace(",","")));//c_goods_cost_
				ci.setCustReceiptNoOri(inputMap_ori.get("c_custreceiptnoori_"+userDefinedMultiNewRowExtension+"_"+j)[0]);
				ci.setAgentShare(Double.parseDouble(inputMap_ori.get("c_agentshare_"+userDefinedMultiNewRowExtension+"_"+j)[0]));
				ci.setShipmentCharge(Double.parseDouble(inputMap_ori.get("c_shipment_cost_"+userDefinedMultiNewRowExtension+"_"+j)[0]));
				
				if (ut.isRuralDistrict(conn,ci.getDistrict(), currentBranch)) {
					ci.setRural("Y");
					ruralArea = true;
				}else {
					ci.setRural("N");
					ruralArea = false;
				}	
				//ci.setShipmentCharge(ut.calcShipmentChargesBasedOnDestCity(conn, ci.getState(),ruralArea, caseMaster.getMasterCustomerId(),caseMaster.getCustId(), currentBranch));
				cases.add(ci);
			}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			// get the pickup agent id
			int pickUpAgent = 0;
			pst =conn.prepareStatement("select mcust_pickupagent from kb_mastercustomer where mcust_id=?");
			pst.setInt(1, caseMaster.getMasterCustomerId());
			rs = pst.executeQuery();
			if (rs.next())
			pickUpAgent = Utilities.getPickUpAgentForMasterCustomer(conn, caseMaster.getMasterCustomerId());
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			
			pst = conn.prepareStatement("insert into p_cases "
					+ " (c_createdby	, c_rcv_name	, c_rcv_hp1	  		, c_rcv_hp2		 , c_rcv_state, "
					+ "	 c_rural		, c_rcv_addr_rmk, c_rmk 		 	, c_qty	  		 , c_receiptamt, "
					+ "  c_shipment_cost, c_branchcode	, c_custreceiptnoori, c_rcv_district , c_custid	, "
					+ "  c_custhp		, c_mastercustid, c_mastercusthp    , c_pickupagent  , c_productinfo,"
					+ "  c_agentshare   , c_specialcase , c_creationstartpoint, c_createddt	 )"
			+ " values  (?			    , ?			     , ?		 		, ?					,?, "
			+ "			 ?			    , ?			     , ?		 		, ?					,?, "
			+ "			 ?			    , ?              , ?		 		, ?					,?, "
			+ "			 ?			 	, ?		 		 , ?		 		, ?					, ?, "
			+ "			 ?				, 'Y'			 , 'SYS-NewSpecialCases', now()	 )",
			Statement.RETURN_GENERATED_KEYS);
			CaseInformation ci = new CaseInformation ();
			
			for (int i =0; i<cases.size(); i++) {
					ci = cases.get(i);
					pst.setInt(1,userId);
					pst.setString(2, ci.getReceiverName());
					pst.setString(3, ci.getReceiverHp1());
					pst.setString(4, ci.getReceiverHp2());
					pst.setString(5, ci.getState());
					pst.setString(6, ci.getRural());
					pst.setString(7, ci.getLocationDetails());
					pst.setString(8, ci.getRmk());
					pst.setInt(9, ci.getQty());
					pst.setDouble(10, ci.getReceiptAmtIqd()*1000);//this is special req, so the user can insert without adding the 000
					pst.setDouble(11, ci.getShipmentCharge());
					pst.setInt(12, caseMaster.getBranch());
					pst.setString(13, ci.getCustReceiptNoOri());
					pst.setInt(14, ci.getDistrict());
					pst.setInt(15, caseMaster.getCustId());
					pst.setString(16, caseMaster.getHp());
					pst.setInt(17, caseMaster.getMasterCustomerId());
					pst.setString(18, caseMaster.getMasterCustHp1());
					pst.setInt(19, pickUpAgent);
					pst.setString(20, ci.getProductInfo());
					pst.setDouble(21, ci.getAgentShare());
					pst.executeUpdate();
	
					rs = pst.getGeneratedKeys();
					if (rs.next())
						ci.setCaseid(rs.getInt(1));
					else
						throw new Exception ("No case id generate");
					
					fu.createNewCaseInQueue(conn,ci.getCaseid(), caseMaster.getBranch());
					
					try {rs.close();}catch(Exception e) {/*ignore*/}
					pst.clearParameters();
				
			}
			conn.commit();
			cases.clear();
		}catch(Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {/**/}
			e.printStackTrace();
			msg = "Error ("+e.getMessage()+")";
			setInsertErrorFlag(true);
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			try {conn.close();}catch(Exception e) {/*ignore*/}
		}
		
		 return msg;
	}
	public int getUserStoreCode() {
		return userStoreCode;
	}
	public void setUserStoreCode(int userStoreCode) {
		this.userStoreCode = userStoreCode;
		userDefinedLookups.put("rcv_state_1", "select distinct path_state, st_name_ar from kbpaths join kbstate on path_state = st_code "
				+ " where path_frombranch ="+this.userStoreCode+" and st_active='Y' order by st_order");
	}
}












/////////////////////////// old code

/*package com.app.cases;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.bussframework.FlowUtils;
import com.app.core.CoreMgr;
import com.app.db.mysql;
import com.app.util.Utilities;
import com.mysql.jdbc.Statement;
public class NewSpecialCases extends CoreMgr{
	
	private LinkedList <CaseInformation> cases ;
	private int userStoreCode = 0;
	
	public NewSpecialCases () {
		// I NEED TO CREATE MULTI-INSERT-FORM
		
		setDisplayMode("NEWSINGLE");
		MainSql =  "select '' as createddtls, '' as c_cust_hp, '' as c_cust_name, '' as c_pickup_lat ,'' as c_pickup_longt , '' as usemap, "
				+ " '' as c_branchcode , '' as c_pickup_city, '' as c_pickup_district , '' as c_pickup_more_location,"
				+ " '' as item1 , ''  "
				+ " from p_cases where 1=0"; 
		canNew = true;
		mainTable = "p_cases";
		cases = new LinkedList<CaseInformation>();
		userDefinedNewFormColNo = 2;
		userDefinedNewColsDefualtValues.put("c_branchcode", new String[] {"{userstorecode}"});
		userDefinedLookups.put("c_branchcode", "select branch_id , branch_name from kbbranches");
		
		userDefinedNewCols.add("c_cust_name");
		userDefinedNewCols.add("c_cust_hp");
		userDefinedNewCols.add("c_branchcode");
		userDefinedColLabel.put("c_branchcode", "الفرع");
		userDefinedReadOnlyNewCols.add("c_branchcode");
		userDefinedNewColsHtmlType.put("c_cust_hp", "TEXT");
		userDefinedNewColsHtmlType.put("c_cust_name","EDITABLE_SELECT");
		
		userDefinedLookups.put("rcv_state_1", "select st_code , st_name_ar from kbstate where st_active='Y' order by st_order");
		userDefinedLookups.put("rcv_broken_1", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		
		userDefinedLookups.put("c_cust_hp", "!select cust_phone1, cust_phone1 as ph  from kbcustomers where cust_id='{c_cust_name}' ");
		
		userDefinedLookups.put("c_branchcode", "select branch_id , branch_name from kbbranches");
		
		userDefinedColLabel.put("c_cust_name", "أسم العميل");
		userDefinedColLabel.put("c_cust_hp", "رقم الهاتف");
		
		
		userDefinedColLabel.put("c_pickup_state","مخزن");
		userDefinedHiddenNewCols.add("c_pickup_state");
		
		
		userDefinedColsMustFill.add("c_cust_name");
		userDefinedColsMustFill.add("c_cust_hp");
		userDefinedColsMustFill.add("c_branchcode");
		
		newCaption = "خلق شحنه جديده";
	}
	@Override
	public void initialize(HashMap smartyStateMap){
		int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		 userDefinedLookups.put("c_cust_name", "select cust_id , cust_name from kbcustomers where cust_branch="+userstorecode+" ");
		 this.setUserStoreCode(userstorecode);
		 super.initialize(smartyStateMap);
		 sqlColsSizes.put("c_cust_hp", 11);
	 
	}
	
	public StringBuilder getNewForm(){//Generte THe New Form
		if (userDefinedNewCols.isEmpty())//To check if the userDefinedNewCols Had not been Set
			return new StringBuilder("The New Columns List is Not Set");
		String labelName=null;
		int rowNum = 0 ;
		boolean required=false;
		jsmgr.userDefinedColsHtmlType = userDefinedNewColsHtmlType;
		StringBuilder newForm = new StringBuilder("");
		String jsValidatorNumeric="" , enctype="", BackGroundColor="";
		ArrayList<String> displayValue = new ArrayList<String>();
		HashMap<String , String> tipsList = new HashMap<String ,String>();
		String Readonly="";
		boolean Disabled = false , hidden =false;
		String labelClass ="control-label col-md-2 col-sm-2 col-xs-12";
		String inputClass ="col-md-4 col-sm-4 col-xs-12";
		if (userDefinedNewFormColNo>=3){
			labelClass="control-label col-md-1 col-sm-1 col-xs-12";
			inputClass = "col-md-3 col-sm-3 col-xs-12";
		}
		for (String key :userDefinedNewCols){
			 if (blobList.contains(allSqlColsTypes.get(key)) || blobList.contains(userDefinedNewColsHtmlType.get(key))){
				 if (!userDefinedStoreFileNameColumns.containsKey(key))
					 return new StringBuilder("Col=>"+key+", is a blob , and does not have symetric col to save the name"
					 		+ "</br> please set the userDefinedStoreFileNameColumns");
				 enctype = "enctype='multipart/form-data'";
				 break;
			 }
		}
		newForm.append("<div class='row'><div class='col-md-12 col-sm-12 col-xs-12' style='padding-left:0px;padding-right:0px;'><div class='x_panel'>");
		newForm.append("<div class='x_title'> <h2>"+newCaption+"</h2><div class='clearfix'></div></div>");
		newForm.append("<div class='x_content'><br />"
		+ "<form id='"+myClassBean.replace(".", "_dot_")+"' name='"+myClassBean+"' action='?myClassBean="+myClassBean+"&new=1' method='POST'"
		+ " data-parsley-validate class='form-horizontal form-label-left' "+enctype+">");
		boolean startFieldSet = false;
	
		
		for (String removeFileNameCol : userDefinedStoreFileNameColumns.keySet()){
			if (userDefinedNewCols.contains(removeFileNameCol)){
				if (userDefinedNewCols.contains(userDefinedStoreFileNameColumns.get(removeFileNameCol)))
					userDefinedNewCols.remove(userDefinedStoreFileNameColumns.get(removeFileNameCol));
				else
					return new StringBuilder(removeFileNameCol+" does not have userDefinedStoreFileNameColumns column");
			}
		}
		
		for (String key :userDefinedNewCols){
			 if (!userDefinedHiddenNewCols.contains(key)){// if not hidden	
				 
				 if(userDefinedFieldSetCols!=null)
					 if (userDefinedFieldSetCols.containsKey(key)){
						 newForm.append("<fieldset class='scheduler-border'>");
						 newForm.append("<legend class='scheduler-border'>"+userDefinedFieldSetCols.get(key)+"</legend>");
						 startFieldSet = true;
					 }
				 if (userDefinedNewFormColNo==1){
					 newForm.append("<div class='form-group'>");
				 }else if (rowNum%userDefinedNewFormColNo==0 || rowNum==0){
					 newForm.append("<div class='form-group'>");
				 }	 
				 rowNum++; 
				if (!userDefinedColLabel.containsKey(key))
					labelName = key;
				else
					labelName = userDefinedColLabel.get(key);

				if (numberList.contains(allSqlColsTypes.get(key)))
					jsValidatorNumeric = jsValidatorNumeric +jsmgr.genJSNumericValidation(key , userDefinedColLabel.get(key));
				
				required = false;
				if (userDefinedColsMustFill.contains(key)){//check for the must fill
					required = true;
					//HtmlInForm = HtmlInForm + " <font size='4' color='red'>*</font>";
					//jsValidatorMustFill= jsValidatorMustFill+jsmgr.genJSMustFill(key , userDefinedColLabel.get(key));
				}
				newForm.append("<label id='"+key+"_label' class='"+labelClass+"' >"+labelName);
				if(required)
					newForm.append("<span class='required'> *</span>");
				newForm.append("</label>");
				newForm.append("<div class='"+inputClass+"' div_fornew_input_smarty='smarty_newcol_"+key+"'>");
				if (userColHintEDIT !=null){
					if (userColHintEDIT.containsKey(key)){
						    tipsList.put(key, "a_left_"+key);
						    newForm.append("<a id='a_left_"+key+"' href='#' >"+
									"<img src='../img/help.jpg' height =17 width=15 border=0></img></a>"+
									"<div id='tip1_left_"+key+"' style='display:none;'>"+
									"<pre class='tip'>"+userColHintEDIT.get(key)+"</pre></div>");
					}
				}
				displayValue.clear();
				if (userDefinedNewColsDefualtValues.containsKey(key))
					if(userDefinedNewColsDefualtValues.get(key)!=null)
						for (String val : userDefinedNewColsDefualtValues.get(key))
							displayValue.add(val);
				
				Readonly="";
				if(userDefinedReadOnlyNewCols.contains(key))
					Readonly = "readonly";
				else
					Readonly = "";
					
				if (userDefinedDisabledNewCols.contains(key))
					Disabled = true;
				else
					Disabled = false;
				
				hidden = false;
				
				if (userDefinedColsMustFill.contains(key)){//check for the must fill
					BackGroundColor="#FFFFB8";
				}else{
					BackGroundColor="#FFFAFF";
				}
				newForm.append(myhtmlmgr.GetHtmlInput(userDefinedNewColsHtmlType, colMapValues,
						   key						 , displayValue , 
						   sqlColsSizes 			 , Readonly , 
						   Disabled				     , userDefinedLookups,
						   BackGroundColor			 , hidden,
						   null						 , required,
						   false					 , 0, 
						   userDefinedMinValMap		 , userDefinedMaxValMap));		
				newForm.append("</div>");
				if (userDefinedNewFormColNo==1){
					 newForm.append("</div>"); //end of form group	 
				 }else if ((rowNum)%userDefinedNewFormColNo==0 && rowNum>1){
					 newForm.append("</div>");
				 }
				 if (startFieldSet){
					 if (userDefinedFieldSetEndWithCols.contains(key)){
						 startFieldSet = false;
						 newForm.append("</fieldset>");
						 rowNum = userDefinedNewFormColNo;
					 }
				 }
			}else{// if hidden
				displayValue.clear();
				if (userDefinedNewColsDefualtValues.containsKey(key))
					if(userDefinedNewColsDefualtValues.get(key)!=null)
						for (String val : userDefinedNewColsDefualtValues.get(key))
							displayValue.add(val);
				hidden = true;
				newForm.append(myhtmlmgr.GetHtmlInput(userDefinedNewColsHtmlType, colMapValues,
						   key						 , displayValue , 
						   sqlColsSizes 			 , Readonly , 
						   Disabled				     , userDefinedLookups,
						   BackGroundColor			 , hidden,
						   null						 , required,
						   false					 , 0, 
						   userDefinedMinValMap		 , userDefinedMaxValMap));		
			}//end of hidden
			 newForm.append(genHotLookupsjs(key , required , false));
			 
		}//end of cols loop
		if (!((rowNum)%userDefinedNewFormColNo==0 && rowNum>1)){
			 newForm.append("</div>");
		}
		if (startFieldSet){
			newForm.append("</fieldset>");
		}
		
		//add the customized table here
		 newForm.append("<fieldset class='scheduler-border' style='width:99%'>");
		 newForm.append("<legend class='scheduler-border'>تفاصيل الشحنات</legend>");
		 //System.out.println("----->"+cases);
		 newForm.append("<table class='table table-bordered table-striped' id='rcv_dtls'>");
		 newForm.append(getRCVDetailsRow(1));
		 for (int c = 2 ; c<=cases.size(); c++)
			 newForm.append(getRCVDetailsRow(c));
		 newForm.append("</table>");
		 //newForm.append(getRCVTableDtls());
		 newForm.append("</br><button type='button' id='add_rcv_dtls' class='btn btn-warning' >إضافة مستلم</button> ");
		 newForm.append("</fieldset>");
		
		
		newForm.append("<div class='ln_solid'></div>");
		newForm.append(" <div class='form-group'>");
			newForm.append("<div class='col-md-6 col-sm-6 col-xs-12 col-md-offset-5'>");
				
				newForm.append("<button type='submit' id='save_new_form_"+myClassBean+"' value='save' class='btn btn-success save_form_btn'>خلق الشحنه</button>");
		newForm.append("</div>");
		newForm.append("</div>");
		newForm.append("</form>");//End of Form
		
		newForm.append("<script> var frmvalidator  = new Validator('"+myClassBean+"');\n");  
		newForm.append(jsValidatorNumeric);
		for (String param : jsmgr.colsUsedInLookupsForOtherCols.keySet()){
			newForm.append(jsmgr.getHotLookupCallingScript(param));
		}
		
		newForm.append("</script>");
		if (tipsList !=null){
			newForm.append("<script>$(document).ready(function() {");
			for (String key : tipsList.keySet()){
			//"a1_left_"+key
				newForm.append("$('#"+tipsList.get(key)+"').bubbletip($('#tip1_left_"+key+"'), {"+
							"deltaDirection: 'right',"+
							"animationDuration: 100,"+
							"offsetLeft: -20"+
						"});");
			}
			newForm.append("});</script>");
		}
		newForm.append("</div></div></div>");
		return newForm;
	}
	// to return table representing the recevier and shipments details
	
	
	public StringBuilder getRCVDetailsRow(int rcvSeq) {
		StringBuilder sb = new StringBuilder();
		userDefinedMultiNewRowExtension += "_"+rcvSeq;
		
		String stylediv = "style=''";
		Utilities ut = new Utilities();
		
		
		//Receiver Name
		String defaultRcvName = "", fragile = "" , rcvPhone="07", destState="BGD", rcvDistrict="", rural ="N" , rmk="", locDtls="", 
				 receiptAmt="", qty="1", shipmentCost = "" , receiptNo="", agentShare="" ;
		
		
		int smarty_new_row_seq= rcvSeq;
		if (!cases.isEmpty() && cases.get(rcvSeq-1)!=null) {
			CaseInformation ci = cases.get(rcvSeq-1);
			defaultRcvName = ci.getName();
			rcvPhone = ci.getHp();
			
			rural = ci.getRural();
			locDtls = ci.getLocationDetails();
			rmk = ci.getRmk();
			
			qty=ci.getQty()+"";
			fragile = ci.getFragile();

			receiptAmt = ci.getReceiptAmt()+"";
			shipmentCost = ci.getShipmentCharge()+"";
			agentShare   = ci.getAgentShare()+"";
			receiptNo = ci.getCustReceiptNoOri();
			smarty_new_row_seq = ci.getSmarty_new_row_seq();
			
			rcvDistrict = ci.getDistrict();
		}
		
		Connection conn2 = null;
		try {
			conn2 = mysql.getConn();
			
			//shipmentCost = Double.toString(ut.calcShipmentChargesBasedOnDestCity(conn2,  destState, ruralArea, custName));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally {
			try {conn2.close();}catch(Exception e) {}
		}
		sb.append( "<tr id='"+userDefinedMultiNewRowExtension+"' rcv_no = '"+smarty_new_row_seq+"' style=\"border-bottom: 2px solid;\">");
		sb.append("<input type='hidden' id='smarty_new_row_seq__"+smarty_new_row_seq+"' name='smarty_new_row_seq__"+smarty_new_row_seq+"'  value='"+smarty_new_row_seq+"'/>");
		
		sb.append("<div class='form-horizontal form-label-right'>");
		
		// goods cost
				sb.append("<td style='padding-top:15px;'>"
						+ "<div class='form-group'>"
						+ " <div class='col-md-1 col-sm-1 col-xs-3' style='margin-left:1%;margin-right:1%;'><label class='control-label'>مبلغ الوصل</label>"
								+ "<input type='text' value='"+receiptAmt+"'  class='form-control'  "
										+ "style='text-align:right; background-color:#FFFFB8; color: #424242;width:8em;' name='c_receiptamt_"+userDefinedMultiNewRowExtension+"'"
										+ " id ='c_receiptamt_"+userDefinedMultiNewRowExtension+"' required onkeyup='formatMe(this);'/>"
												+ ""
												+ "</div>");
		// reciept no	
				sb.append("<div class='col-md-1 col-sm-1 col-xs-3 ' style='margin-left:2%;margin-right:2%;'><label class='control-label'>رقم الوصل</label>"
								+ "<input type='number'  class='form-control' value='"+receiptNo+"' min='0' style='text-align:right; background-color:#FFFFB8; color: #424242;width:8em;' "
										+ " oninput=\"this.value = Math.abs(this.value)\" size='10' name='c_custreceiptnoori_"+userDefinedMultiNewRowExtension+"' "
												+ "id ='c_custreceiptnoori_"+userDefinedMultiNewRowExtension+"' required /></div>");
		
		//state
		String style = "text-align:right; background-color:#FFFFB8; padding: 0 10px 0 10px;"+
    			"  color: #424242; border: 1px solid #7dc6dd;min-width:150px";
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3 ' style='margin-left:2%;margin-right:2%;'><label class='control-label'>المدينه</label>"
				+ ""
				+ "<select class='form-control select2_single'  onchange='loadDistrict("+rcvSeq+");' id='rcv_city_"+userDefinedMultiNewRowExtension+"'" + 
				"  name='rcv_city_"+userDefinedMultiNewRowExtension+"' style='"+style+"' required>");
		
		Connection conn1 = null;
		try {
			if (colMapValues==null) {
				conn1 = mysql.getConn();
				colMapValues= mysqlmgr.loadAllLookups(conn1,userDefinedLookups);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn1.close();}catch(Exception e) {}
		}
		Map <String , String> lookupsmap = colMapValues.get("rcv_state_1");
		if (lookupsmap !=null){
			if (!lookupsmap.isEmpty()){
				for (String code : lookupsmap.keySet()){
					if (destState.equalsIgnoreCase(code))
						sb.append("<option value='"+code+"' selected>"+lookupsmap.get(code)+"</option> \n");
					else
						sb.append("<option value='"+code+"' >"+lookupsmap.get(code)+"</option> \n");
				}
			}
		}
		sb.append("</select></div> \n");
		
		// district inside state
		LinkedHashMap<String,String> district = new LinkedHashMap<String,String> ();
		try {
			conn1 = mysql.getConn();
			district  = ut.getDistrictOfState(conn1,"BGD");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {conn1.close();}catch(Exception e) {}
		}
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' style='margin-right:7%;margin-left:2%;'><label class='control-label'>المنطقه</label>"
				+ "<select class='form-control select2_single'   id='rcv_district_"+userDefinedMultiNewRowExtension+"'" + 
				"  name='rcv_district_"+userDefinedMultiNewRowExtension+"' style='"+style+"' required >");
		
		sb.append("<option value='' selected></option> \n");
		for (String code : district.keySet()){
			
			if (rcvDistrict.equalsIgnoreCase(code))
				sb.append("<option value='"+code+"' selected>"+district.get(code)+"</option> \n");
			else
				sb.append("<option value='"+code+"' >"+district.get(code)+"</option> \n");
		}
		sb.append("</select></div> \n");
				
		//location details
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3 col-md-offset-1' ><label class='control-label'>تفاصيل العنوان</label>"
				+"<textarea  class='form-control'   name='rcv_more_loc_"+userDefinedMultiNewRowExtension+"' "
						+ "id ='rcv_more_loc_"+userDefinedMultiNewRowExtension+"'>"+locDtls+"</textarea></div>");

		//Notes
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' style='margin-right:13%'><label class='control-label'>ملاحظات</label>"
				+ "<textarea  class='form-control'  name='rcv_rmk_"+userDefinedMultiNewRowExtension+"' "
						+ "id ='rcv_rmk_"+userDefinedMultiNewRowExtension+"'>"+rmk+"</textarea></div></div>");
		
		String checked = "";
		//Fragile
		if (fragile.equalsIgnoreCase("Y"))
			checked="checked";
		sb.append("<div class='form-group'><div class='col-md-1 col-sm-1 col-xs-3' ><label class='control-label'>قابل للكسر</label>"
						+ "</br>"
						+ "<input type='checkbox' "+checked+" class='form-check-input' value='Y' name='c_fragile_"+userDefinedMultiNewRowExtension+"' id ='c_fragile_"+userDefinedMultiNewRowExtension+"' /></div>");
		//get back items
		
		 for TransportLine we don't need this 
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' "+stylediv+"><label class='control-label'>جلب  بضاعه</label>"+ "</br>"
				+ "<input type='checkbox'  "+checked+" class='form-check-input' value='Y' name='c_bringitemsback_"+userDefinedMultiNewRowExtension+"' id ='c_bringitemsback_"+userDefinedMultiNewRowExtension+"' /></div>");
						
		//rural areas
		checked = "";
		if (rural.equalsIgnoreCase("Y"))
			checked="checked";
		
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' "+stylediv+"><label class='control-label'>أطراف</label>"
				+ "</br>"
				+ "<input type='checkbox' class='form-check-input' name='c_rural_"+userDefinedMultiNewRowExtension+"' "
						+ "id ='c_rural_"+userDefinedMultiNewRowExtension+"' "+checked+" value='Y' /></div>");
		
		
		// send money
		
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' style='margin-left:2%;margin-right:2%;'><label class='control-label'>إرسال مبلغ</label>"
						+ "<input type='number'  class='form-control' value='"+amountToSend+"' min='0'  style='width:8em;' "
								+ " oninput=\"this.value = Math.abs(this.value)\" size='10' name='c_sendmoney_"+userDefinedMultiNewRowExtension+"' id ='c_sendmoney_"+userDefinedMultiNewRowExtension+"'  /></div>");
		

		sb.append("<div class='col-md-1 col-sm-2 col-xs-3' "+stylediv+"><label class='control-label'>المستلم</label>"
				+ ""
				+ "<input type='text' class='form-control'  style='text-align:right; color: #424242;' "
				+ " name='rcv_name_"+userDefinedMultiNewRowExtension+"' id ='rcv_name_"+userDefinedMultiNewRowExtension+"' size='15'  "
						+ " value='"+defaultRcvName+"'  /></div>");

		//Receiver Phone
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3 ' style='margin-left:1%;margin-right:10%;'><label class='control-label'>هاتف</label>"
				+ "<input type=\"tel\" style=\"text-align:right; background-color:#FFFFB8; color: #424242;  direction: ltr;\" name='rcv_phone_"+userDefinedMultiNewRowExtension+"' "
				+ "id='rcv_phone_"+userDefinedMultiNewRowExtension+"' class=\"form-control\""
				+ " value='' required=\"required\" maxlength=\"11\" size=\"11\" pattern='[0-9\\u0660-\\u0669]{11}'></div>");

		
		//No of Pieces
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3 col-md-offset-1'><label class='control-label'>عدد القطع</label>"
				+ ""
				+ "<input type='number' class='form-control' min='1' value='"+qty+"' size='1' style='width:7em;text-align:right; background-color:#FFFFB8; color: #424242;'"
				+ " oninput=\"this.value = Math.abs(this.value)\"  name='rcv_qty_"+userDefinedMultiNewRowExtension+"' id ='rcv_qty_"+userDefinedMultiNewRowExtension+"' required='required' /></div>");
		
		
		
		//list of agents per state
		LinkedHashMap<String,String> agents = new LinkedHashMap<String,String> ();
		try{
			conn1 = mysql.getConn();
			agents  = ut.getListOfAgentsPerState(conn1,destState);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {conn1.close();}catch(Exception e) {}
		}
		sb.append("<div class='col-md-2 col-sm-1 col-xs-3' style=''><label class='control-label'>مندوب التوصيل بغداد فقط</label>"
				+ "<select class='form-control select2_single'   id='c_assignedagent_"+userDefinedMultiNewRowExtension+"'" + 
				"  name='c_assignedagent_"+userDefinedMultiNewRowExtension+"' style='"+style+"' required >");
		sb.append("<option value='' selected></option> \n");
		for (String code : agents.keySet()){
			sb.append("<option value='"+code+"' >"+agents.get(code)+"</option> \n");
		}
		sb.append("</select></div> \n");
		// agent share cost	
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' style=''><label class='control-label'>أجور المندوب</label>"
						+ "<input type='number' min='0'   class='form-control' readonly value='5000' style='width:8em;background-color:#e7e7df;'  size='10' name='c_agentshare_"+userDefinedMultiNewRowExtension+"' "
								+ "id ='c_agentshare_"+userDefinedMultiNewRowExtension+"' required  /></div>");
				
		// shipment cost	
		sb.append("<div class='col-md-1 col-sm-1 col-xs-3' style='margin-right:1%;'><label class='control-label'>مبلغ الشحن</label>"
						+ "<input type='number' min='0'   class='form-control' value='"+shipmentCost+"' style='width:8em;background-color:#FFFFB8;'  size='10' name='c_shipment_cost_"+userDefinedMultiNewRowExtension+"' "
								+ "id ='c_shipment_cost_"+userDefinedMultiNewRowExtension+"' required  /></div>");
			
		sb.append("</div>");
			
		sb.append("</div>");
		sb.append("<td style='width:4em;vertical-align:top;'><label class='control-label'>"+smarty_new_row_seq+"</label></br></br><button type='button' onclick='remove_row("+smarty_new_row_seq+")' class='btn btn-danger btn-xs'><li class='fa fa-trash'></li></button></td></tr>");
		return sb;
	}
	
	
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autocommit) { 
		FlowUtils fu = new FlowUtils();
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean custFound = false;
		int custid = 0;
		int userId = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		String msg = "تم خلق الطلبيه بنجاح";
		//Utilities ut = new Utilities();
		
		try{
			inputMap_ori = filterRequest(rqs);
			//System.out.println(inputMap_ori);
			MasterCaseInformation caseMaster = new MasterCaseInformation();
			LinkedList <Integer> availableCases= new LinkedList <Integer>();
			for (String key:inputMap_ori.keySet()){//loop to get all the cases from the grid
			    if(key.startsWith("smarty_new_row_seq__")) {
			    	availableCases.add(Integer.parseInt(inputMap_ori.get(key)[0]));
			    	
				}
			}
			if (inputMap_ori.get("c_cust_name")[0]==null)
				caseMaster.setCustName("");
			else
				caseMaster.setCustName(inputMap_ori.get("c_cust_name")[0]);
			
			caseMaster.setHp(inputMap_ori.get("c_cust_hp")[0]);
			caseMaster.setBranch(Integer.parseInt(inputMap_ori.get("smarty_showonly_c_branchcode")[0]));
			
			conn = mysql.getConn();
			// get cust info
			pst = conn.prepareStatement("select 1 from kbcustomers where cust_id=?");
			pst.setString(1, inputMap_ori.get("c_cust_name")[0]);
			rs = pst.executeQuery();
			if (rs.next())
				if(rs.getString(1).equalsIgnoreCase("1")) {
					custFound = true;
					custid = Integer.parseInt(inputMap_ori.get("c_cust_name")[0]);
				}
			try {rs.close();}catch(Exception e) {ignore}
			try {pst.close();}catch(Exception e) {ignore}
			// when there is error and we reload the page the custoemr id will turn into name so check it also.
			if (!custFound) {
				pst = conn.prepareStatement("select cust_id from kbcustomers where cust_name=?");
				pst.setString(1, inputMap_ori.get("c_cust_name")[0]);
				rs = pst.executeQuery();
				if (rs.next())
					if( rs.getInt("cust_id")>0) {
						custFound = true;
						custid = rs.getInt("cust_id");
						//System.out.println("found by the name===>"+inputMap_ori.get("c_cust_name")[0]);
					}
			}
			
			if(!custFound) {//create new customer
				pst = conn.prepareStatement("insert into kbcustomers (cust_name, cust_phone1, cust_createdby, cust_branch)"
						+ "values(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				pst.setString(1, caseMaster.getCustName());
				pst.setString(2, caseMaster.getHp());
				pst.setInt(3, userId);
				pst.setInt(4, caseMaster.getBranch());
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				rs.next();
				custid = rs.getInt(1);
			}
			try {rs.close();}catch(Exception e) {ignore}
			try {pst.close();}catch(Exception e) {ignore}
			
			
			
			
			double agentShareAmt = 0;
			for (Integer j : availableCases) {
				agentShareAmt = 0;
				CaseInformation ci = new CaseInformation();
				ci.setSmarty_new_row_seq(j);
				ci.setCustName(caseMaster.getCustName());
				ci.setName(inputMap_ori.get("rcv_name_"+userDefinedMultiNewRowExtension+"_"+j)[0]); //rcv name
				ci.setHp(inputMap_ori.get("rcv_phone_"+userDefinedMultiNewRowExtension+"_"+j)[0]); // rcv phone
				
				ci.setState(inputMap_ori.get("rcv_city_"+userDefinedMultiNewRowExtension+"_"+j)[0]);// rcv city
				//ci.setDistrict(inputMap_ori.get("rcv_district_"+userDefinedMultiNewRowExtension+"_"+j)[0]);
				ci.setLocationDetails(inputMap_ori.get("rcv_more_loc_"+userDefinedMultiNewRowExtension+"_"+j)[0]);//location dtls
				ci.setRmk(inputMap_ori.get("rcv_rmk_"+userDefinedMultiNewRowExtension+"_"+j)[0]); // remarks
				ci.setQty(Integer.parseInt(inputMap_ori.get("rcv_qty_"+userDefinedMultiNewRowExtension+"_"+j)[0])); //no of items
				//ci.setShipmentCharge(Double.parseDouble(inputMap_ori.get("c_shipment_cost_"+userDefinedMultiNewRowExtension+"_"+j)[0]));//shipment cost
				ci.setReceiptAmt(Double.parseDouble(inputMap_ori.get("c_receiptamt_"+userDefinedMultiNewRowExtension+"_"+j)[0].replace(",","")));//c_goods_cost_
				
				//ci.setSentMonyToReceiver(Double.parseDouble(inputMap_ori.get("c_sendmoney_"+userDefinedMultiNewRowExtension+"_"+j)[0]));// send money to receiver
				
				ci.setCustReceiptNoOri(inputMap_ori.get("c_custreceiptnoori_"+userDefinedMultiNewRowExtension+"_"+j)[0]);
				if (inputMap_ori.containsKey("c_rural_"+userDefinedMultiNewRowExtension+"_"+j)&&(inputMap_ori.get("c_rural_"+userDefinedMultiNewRowExtension+"_"+j)[0] !=null))
					ci.setRural("Y"); // fragile
				else
					ci.setRural("N");
				
				if (inputMap_ori.containsKey("c_fragile_"+userDefinedMultiNewRowExtension+"_"+j)&&(inputMap_ori.get("c_fragile_"+userDefinedMultiNewRowExtension+"_"+j)[0] !=null))
					ci.setFragile("Y"); // fragile
				else
					ci.setFragile("N");
				
				
				
				//ci.setShipmentCharge(ut.calcShipmentChargesBasedOnDestCity(conn, ci.getState(),ruralArea,custid));
				ci.setAgentShare(Double.parseDouble(inputMap_ori.get("c_agentshare_"+userDefinedMultiNewRowExtension+"_"+j)[0]));
				ci.setShipmentCharge(Double.parseDouble(inputMap_ori.get("c_shipment_cost_"+userDefinedMultiNewRowExtension+"_"+j)[0]));
				ci.setRemainingAmt(ci.getReceiptAmt()-ci.getShipmentCharge());//we use this to set the balance same as goods cost
				
				
				if (ci.getState().equalsIgnoreCase("BGD")) {
					ci.setDistrict(inputMap_ori.get("rcv_district_"+userDefinedMultiNewRowExtension+"_"+j)[0]);
					if (inputMap_ori.get("c_assignedagent_"+userDefinedMultiNewRowExtension+"_"+j) !=null 
							&& inputMap_ori.get("c_assignedagent_"+userDefinedMultiNewRowExtension+"_"+j)[0]!=null
								&& inputMap_ori.get("c_assignedagent_"+userDefinedMultiNewRowExtension+"_"+j)[0].trim().length()>0)
						ci.setAssignedDLVAgent(inputMap_ori.get("c_assignedagent_"+userDefinedMultiNewRowExtension+"_"+j)[0]);
				}else {
					ci.setAssignedDLVAgent("0");
				}
				cases.add(ci);
			}
			try {pst.close();}catch(Exception e) {ignore}
			
		
			
			// get the pickup agent id
			int pickUpAgent = 0;
			pst =conn.prepareStatement("select cust_assigned_pickup_agent from kbcustomers where cust_id=?");
			pst.setInt(1, custid);
			rs = pst.executeQuery();
			if (rs.next())
				pickUpAgent = rs.getInt("cust_assigned_pickup_agent");
			try {rs.close();}catch(Exception e) {ignore}
			try {pst.close();}catch(Exception e) {ignore}
			
			
			pst = conn.prepareStatement("insert into p_cases "
					+ " (c_createdby	, c_rcv_name	 	, c_rcv_hp	  		, c_rcv_state, "
					+ "  c_rural		, c_rcv_addr_rmk 	, c_rmk 		 	, c_qty	  		 	, c_receiptamt, "
					+ "  c_shipment_cost, c_goodscostbalance, c_fragile  		, c_branchcode		, c_custreceiptnoori, "
					+ "	 c_agentshare	, c_rcv_district	, c_custid		 	, c_custhp			, c_specialcase	    ,"
					+ "	 c_pickupagent	, c_assignedagent	, c_createddt	 )"
			+ " values  (?				, ?			     	, ?		 			, ?,"
			+ "			 ?				, ?				    , ?			     	, ?		 			, ?,"
			+ "          ?				, ?				    , ?              	, ?		 			, ?,"
			+ "			 ?				, ?				 	, ?		 		 	, ?		 			, ?,"
			+ "			 ?				, ?					, (now()+INTERVAL 8 HOUR) 	 )",
			Statement.RETURN_GENERATED_KEYS);
			CaseInformation ci = new CaseInformation ();
			
			for (int i =0; i<cases.size(); i++) {
					ci = cases.get(i);
					pst.setInt(1,userId);
					pst.setString(2, ci.getName());
					pst.setString(3, ci.getHp());
					pst.setString(4, ci.getState());
					pst.setString(5, ci.getRural());
					pst.setString(6, ci.getLocationDetails());
					pst.setString(7, ci.getRmk());
					pst.setInt(8, ci.getQty());
					pst.setDouble(9, ci.getReceiptAmt()*1000);//this is special req, so the user can insert without adding the 000
					pst.setDouble(10, ci.getShipmentCharge());
					pst.setDouble(11, ci.getRemainingAmt()*1000);
					pst.setString(12, ci.getFragile());
					pst.setInt(13, caseMaster.getBranch());
					pst.setString(14, ci.getCustReceiptNoOri());
					pst.setDouble(15, ci.getAgentShare());
					pst.setString(16, ci.getDistrict());
					pst.setInt(17, custid);
					pst.setString(18, caseMaster.getHp());
					pst.setString(19, "Y");
					pst.setInt(20, pickUpAgent);
					pst.setString(21, ci.getAssignedDLVAgent());
					pst.executeUpdate();

					rs = pst.getGeneratedKeys();
					if (rs.next())
						ci.setCaseid(rs.getInt(1));
					else
						throw new Exception ("No case id generate");
					
					fu.createNewCaseInQueue(conn,ci.getCaseid(), caseMaster.getBranch());
					try {rs.close();}catch(Exception e) {ignore}
					pst.clearParameters();
				
			}
			conn.commit();
			cases.clear();
		}catch(Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {}
			e.printStackTrace();
			msg = "Error ("+e.getMessage()+")";
			setInsertErrorFlag(true);
		}finally {
			try {rs.close();}catch(Exception e) {ignore}
			try {pst.close();}catch(Exception e) {ignore}
			try {conn.close();}catch(Exception e) {ignore}
		}
		
		 return msg;
	}
	public int getUserStoreCode() {
		return userStoreCode;
	}
	public void setUserStoreCode(int userStoreCode) {
		this.userStoreCode = userStoreCode;
	}
}
*/