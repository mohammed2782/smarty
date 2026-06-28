package com.app.cases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.bussframework.FlowUtils;
import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;
import smarty.db.mysql;

import com.app.util.Utilities;

public class NewCasesByState extends CoreMgr {

	private LinkedList<CaseInformation> cases;
	private int userStoreCode = 0;

	public NewCasesByState() {
		setDisplayMode("NEWSINGLE");

		MainSql = " select '' as rcv_city, '' as dummy " 
				+ " from p_cases "
				+ " where 1=0";

		mainTable = "p_cases";

		canNew = true;

		cases = new LinkedList<CaseInformation>();

		userDefinedNewCaption = "خلق شحنه جديدة - محافظة";
		userDefinedNewFormColNo = 2;

		userDefinedFieldSetCols.put("rcv_city", " ");
		userDefinedFieldSetEndWithCols.add("dummy");

		userDefinedColLabel.put("rcv_city", "المحافظة");
		userDefinedNewCols.add("rcv_city");
		userDefinedNewColsHtmlType.put("rcv_city", "DROPLIST");
		userDefinedColsMustFill.add("rcv_city");

	}

	@Override
	public void initialize(HashMap smartyStateMap) {
		userDefinedLookups.put("rcv_state_1", "select distinct path_state, st_name_ar from kbpaths join kbstate on path_state = st_code "
						     + "where path_frombranch =" + lu.getBranchCode() + " and st_active='Y' order by st_order" );
		userDefinedLookups.put("rcv_city", "select distinct path_state, st_name_ar from kbpaths join kbstate on path_state = st_code "
							 + "where path_frombranch =" + lu.getBranchCode() + " and st_active='Y' order by st_order" );

		userDefinedLookups.put("custid","select cust_id , cust_name from kbcustomers where cust_branch=" + lu.getBranchCode() + "  ");
		userDefinedLookups.put("mastercustid", "select mcust_id , mcust_name from kb_mastercustomer "
							 + " where mcust_branchcode=" + lu.getBranchCode() + " and mcust_active='Y' ");

		this.userStoreCode = lu.getBranchCode();
		super.initialize(smartyStateMap);
	}// end of method initialize

	public String userDefinedInjectCodeInNewFromBeforeSubmitButtonAndAFterFields() {
		StringBuilder sb = new StringBuilder("");
		sb.append("<h6 class='mb-1'>تفاصيل الشحنات</h6>");
		sb.append("<table class='table table-bordered table-striped' style='padding:0px;border-width: 0px; ' id='rcv_dtls'>");
		sb.append(getRCVDetailsRow(1, lu.getBranchCode(), "BGD"));
		for (int c = 2; c <= cases.size(); c++)
			sb.append(getRCVDetailsRow(c, lu.getBranchCode(), "BGD"));
		sb.append("</table>");
		sb.append("<div class='row'><div class='col-xl-2 col-sm-3'>"
				+ "<button type='button' id='add_rcv_dtls' class='btn btn-secondary btn-min-width btn-glow mr-1 mb-1 waves-effect waves-light' >إضافة وصل آخر <i class=\"ft-plus-circle\"></i>"
				+ "</button></div></div>");
		return sb.toString();
	}// end of method userDefinedInjectCodeInNewFromBeforeSubmitButtonAndAFterFields

	public StringBuilder getRCVDetailsRow(int rcvSeq, int a_branchCode, String a_destState) {
		StringBuilder sb = new StringBuilder();
		userDefinedMultiNewRowExtension += "_" + rcvSeq;

		Utilities ut = new Utilities();
		Connection conn1 = null;

		int rcvDistrict = 0;
		String senderName = "";
		
		String rmk = "", locDtls = "", receiptAmt = "",  receiptNo = "",
				labelClass = "form-label",  divCLass = "form-group mb-1 col-sm-12 col-md-2";
		int smarty_new_row_seq = rcvSeq;
		String divStyle = "margin-bottom:7px;";

		try {
			conn1 = mysql.getConn();
			if (!cases.isEmpty() && cases.get(rcvSeq - 1) != null) {
				CaseInformation ci = cases.get(rcvSeq - 1);
				locDtls = ci.getLocationDetails();
				rmk = ci.getRmk();
				receiptAmt = ci.getReceiptAmtIqd() + "";
				receiptNo = ci.getCustReceiptNoOri();
				smarty_new_row_seq = ci.getSmarty_new_row_seq();
				rcvDistrict = ci.getDistrict();
				senderName = ci.getSenderName();

			}
			sb.append("<tr id='" + userDefinedMultiNewRowExtension + "' rcv_no = '" + smarty_new_row_seq
					+ "' style=\"border-bottom: 2px solid;\">");
			sb.append("<input type='hidden' id='smarty_new_row_seq__" + smarty_new_row_seq
					+ "' name='smarty_new_row_seq__" + smarty_new_row_seq + "'  value='" + smarty_new_row_seq + "'/>");

			// start TD and DIV
			sb.append("<td style='padding-top:10px;width:95%;border-width: 0px; border-bottom: 1px solid #00000054;'>"
					+ "<div class='row'>");

			
			// 1- Customer
			String style = "text-align:right;  padding: 0 10px 0 10px; border: 1px solid #7dc6dd; min-width:150px";
								
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>"
					+ "<label class='" + labelClass + "'>المتجر *</label> <i class=\"ft-map\"></i>"
					+ "<span id='span_custid'><select class='form-control select2' onchange='getCustomerHP(" +rcvSeq+ "); loadMasterCustomer(" +rcvSeq+ ");' "
					+ "id='custid_" + userDefinedMultiNewRowExtension + "' "
					+ "name='custid_" + userDefinedMultiNewRowExtension + "' style='" + style + "'>");
			if (colMapValues == null) {
				HashMap<String,String> lookupsToLoad = new HashMap<String,String>();
				lookupsToLoad.put("custid","select cust_id , cust_name from kbcustomers where cust_branch=" + a_branchCode + " ");
				colMapValues = mysqlmgr.loadAllLookups(conn1, lookupsToLoad);
			}
			Map<String, String> lookupsmap = colMapValues.get("custid");
			if (lookupsmap != null && !lookupsmap.isEmpty()) {
				sb.append("<option value='' selected></option> \n");
				for (String code : lookupsmap.keySet()) {
					if (senderName.equalsIgnoreCase(code))
						sb.append("<option value='" + code + "' selected>" + lookupsmap.get(code) + "</option> \n");
					else
						sb.append("<option value='" + code + "' >" + lookupsmap.get(code) + "</option> \n");
				}
			}
			sb.append("</select></span>");
			sb.append("<input type='text' class='form-control'  style='text-align:right; color: #424242;' "
					+ " name='newcustomer_"+userDefinedMultiNewRowExtension+"' id ='newcustomer_"+userDefinedMultiNewRowExtension+"' size='16' value='' />");
			sb.append("</div> \n");

			// end of Customer
			
			
			// 2- Customer phone
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" 
					+ "<label class='" + labelClass + "'>هاتف المتجر *</label>"+ "<i class=\"ft-phone-call\"></i>"
					+ "<input type=\"tel\" style=\"text-align:right;background-color: white;"
					+ " direction: ltr;\" name='custhp_"+ userDefinedMultiNewRowExtension + "' " 
					+ " id='custhp_" + userDefinedMultiNewRowExtension + "' class=\"form-control border-start-0\" "
					+ " value='' required maxlength=\"11\" readonly size=\"11\" pattern='[0-9\\u0660-\\u0669]{11}'>"
					+ "</div>");
			// end of Customer phone

//			
//			// 3- Mastercustomer
//			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>"
//					+ "<span id='span_mastercustnameshowonly'><label class='" + labelClass + "'> اسم العميل</label><i class=\"ft-map-pim\"></i>"
//					+ "<select class='form-control select2' id='mastercustnameshowonly_" + userDefinedMultiNewRowExtension + "' "
//					+ " name='mastercustnameshowonly_" + userDefinedMultiNewRowExtension + "' style='" + style + "'>");
//			sb.append("<option value='' selected></option> \n");
//			sb.append("</select></span>");
//			sb.append("<span id='span_mastercustid'><label class='" + labelClass + "'>العميل *</label><i class=\"ft-map-pim\"></i>"
//					+ "<select class='form-control select2' id='mastercustid_" + userDefinedMultiNewRowExtension + "' "
//					+ " name='mastercustid_" + userDefinedMultiNewRowExtension + "' style='" + style + "'>");
//			sb.append("<option value='' selected></option> \n");
//			sb.append("</select></span>");
//			sb.append("</div> \n");
//			// end of Mastercustomer
			
			// 4-Receipt no
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-left:1%;margin-right:1%;'
					+ "<label class='" + labelClass + "'>رقم الوصل</label>" +"<i class=\"ft-package\"></i>"
					+ "<input type='number' value='" + receiptNo
					+ "' class='form-control' min='0' style='text-align:right;background-color: white;' "
					+ "oninput=\"this.value = Math.abs(this.value)\" size='10' name='c_custreceiptnoori_"
					+ userDefinedMultiNewRowExtension + "' " + "id ='c_custreceiptnoori_"
					+ userDefinedMultiNewRowExtension + "' required /></div>");
			// end of receipt no

			// 5-Receipt Price
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-left:1%;margin-right:1%;'
					+ "	<label class='" + labelClass + "'>مبلغ الوصل د.ع</label>"
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
					+ "</div> \n");
			// end of Receipt Price
			
			// 5-Receipt Price USD
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-left:1%;margin-right:1%;'
					+ "	<label class='" + labelClass + "'>مبلغ الوصل</label>"+"<i class=\"ft-dollar-sign\"></i>"
					+ "<input type='text' value='0' class='form-control' style='text-align:right;background-color: #e6ffe6;' "
					+ " size='10' name='c_receiptamt_usd_"
					+ userDefinedMultiNewRowExtension + "' " + "id ='c_receiptamt_usd_"
					+ userDefinedMultiNewRowExtension + "' required onkeyup='formatMe(this);'  />"
					+ "<script>$(function() {" + 
					"    new AutoNumeric('#c_receiptamt_usd_"+userDefinedMultiNewRowExtension+"', {" + 
							"    unformatOnSubmit: AutoNumeric.options.unformatOnSubmit.unformat, "+
							" allowDecimalPadding: false " +
							"});" + 
					"});</script>"
					+ "</div>");
						// end of receipt amount

			// 6-Receiver Phone 1
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-left:1%;margin-right:10%;'
					+ "<label class='" + labelClass + "'>هاتف المستلم</label>"+ "<i class=\"ft-phone-call\"></i>"
					+ "<input type=\"tel\" style=\"text-align:right;background-color: white;"
					+ " direction: ltr;\" name='rcv_phone1_"+ userDefinedMultiNewRowExtension + "' " 
					+ "id='rcv_phone1_" + userDefinedMultiNewRowExtension + "' class=\"form-control border-start-0\""
					+ "value='' required maxlength=\"11\" size=\"11\" pattern='[0]{1}[7]{1}[0-9]{9}'>"
					+ "</div>");

			// 8- district inside state
			LinkedHashMap<Integer, String> district = new LinkedHashMap<Integer, String>();
			district = ut.getDistrictOfState(conn1, "BGD");

			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-right:7%;margin-left:2%;'
					+ "<label class='" + labelClass + "'>المنطقه</label><i class=\"ft-map-pim\"></i>"
					+ "<select class='form-control select2' onchange='districtChanged(" + rcvSeq + ");'  "
					+ " id='rcv_district_" + userDefinedMultiNewRowExtension + "' "
					+ " name='rcv_district_" + userDefinedMultiNewRowExtension + "' style='" + style + "' required >");
			sb.append("<option value='' selected></option> \n");
			for (int code : district.keySet()) {
				if (rcvDistrict == code)
					sb.append("<option value='" + code + "' selected>" + district.get(code) + "</option> \n");
				else
					sb.append("<option value='" + code + "' >" + district.get(code) + "</option> \n");
			}
			sb.append("</select></div>");


			// 9- location details
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" 
					+ "<label class='" + labelClass+ "'>تفاصيل العنوان</label>" +"<i class=\"ft-map-pim\"></i>" 
					+ "<textarea class='form-control' tabindex='99999' style=\"text-align:right;background-color: white;\" name='rcv_more_loc_"+ userDefinedMultiNewRowExtension + "' "
					+ "id ='rcv_more_loc_" + userDefinedMultiNewRowExtension + "'>"
					+ locDtls + "</textarea></div>");

			// 10- Notes 
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-right:13%'
					+ "<label class='" + labelClass + "'>ملاحظات</label>" + "<i class=\"ft-message-square\"></i> "
					+ " <textarea class='form-control'  tabindex='99999' style=\"text-align:right;background-color: white;\" name='rcv_rmk_" + userDefinedMultiNewRowExtension
					+ "' id ='rcv_rmk_" + userDefinedMultiNewRowExtension + "'>" + rmk + "</textarea></div>");
			
			
			//list of agents per state
			LinkedHashMap<String,String> agents = new LinkedHashMap<String,String> ();
			agents  = ut.getListOfAgentsPerState(conn1,a_destState, a_branchCode);
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>"
					+ "<label class='"+labelClass+"'>مندوب التوصيل</label>"
			+ "<select class='form-control select2'   "
			+ " id='c_assignedagent_"+userDefinedMultiNewRowExtension+"' " 
			+ "  name='c_assignedagent_"+userDefinedMultiNewRowExtension+"'  tabindex='99999' style='"+style+"'  >");
			sb.append("<option value='' selected></option>");
			for (String code : agents.keySet()){
				sb.append("<option value='"+code+"' >"+agents.get(code)+"</option> \n");
			}
			sb.append("</select></div> \n");
			// -Receiver name
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-left:1%;margin-right:10%;'
					+ "<label class='" + labelClass + "'>أسم المستلم</label>"
					+ "<input type=\"tel\"  tabindex='99999'  style=\"text-align:right;background-color: white;"
					+ " direction: ltr;\" name='rcv_name_"+ userDefinedMultiNewRowExtension + "' " 
					+ "id='rcv_name_" + userDefinedMultiNewRowExtension + "' class=\"form-control border-start-0\""
					+ "value='' >"
					+ "</div>");
			sb.append("</div>");// end of dive row
			sb.append("</td>");
			
			sb.append("<td style='width:5%;vertical-align: bottom; padding: 0; border-width: 0px; border-bottom: 1px solid #00000054;'>"
					+ "<table style='border: 0px solid;' id='side_table_" + smarty_new_row_seq + "'><tr>"
				    + "<td style='border: 0;'>" + "<button type='button'  tabindex='99999'  onclick='remove_row("
					+ smarty_new_row_seq + ")' "
					+ " class='btn btn-danger btn-sm'><li class='fa fa-trash'></li></button>" + "</td>"
					+ "<td style='border: 0;'>"
					+ "<span class='badge bg-secondary rounded-pill text-white ' style='font-size: 17px;'>"
					+ smarty_new_row_seq + "</span>" + "</td>" + "</tr>" + "</table>" + "</td>");
			sb.append("</tr>");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {conn1.close();} catch (Exception e) {}
		}
		
		//System.out.println("sb ===> " + sb);
		return sb;
	}// end of method getRCVDetailsRow

	
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autocommit) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		int custId = 0;
		int pickUpAgent = 0;
		boolean ruralArea = false;
		String msg = " ";
		try {
			inputMap_ori = filterRequest(rqs);
			conn = mysql.getConn();
			
			MasterCaseInformation caseMaster = new MasterCaseInformation();
			LinkedList<Integer> availableCases = new LinkedList<Integer>();
			for (String key : inputMap_ori.keySet()) {// loop to get all the cases from the grid
				if (key.startsWith("smarty_new_row_seq__")) {
					availableCases.add(Integer.parseInt(inputMap_ori.get(key)[0]));
				}
			}
			caseMaster.setCity(inputMap_ori.get("rcv_city")[0]);// rcv_city
			// now for cases
			Utilities ut = new Utilities();
			int userId_G = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
			int currentBranch_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
			for (Integer j : availableCases) {
				CaseInformation ci = new CaseInformation();
				ci.setSmarty_new_row_seq(j);
				int dlvagentId = 0;
				try {
					dlvagentId =Integer.parseInt(inputMap_ori.get
							("c_assignedagent_" + userDefinedMultiNewRowExtension + "_" + j)[0]);
				}catch(Exception e) {
					/*ignore*/
				}
				ci.setDlvAgentId(dlvagentId);
				ci.setCurrentBranch(currentBranch_G);
				ci.setSenderId(Integer.parseInt(inputMap_ori.get("custid_" + userDefinedMultiNewRowExtension + "_" + j)[0]));
				pst = conn.prepareStatement(" select  cust_assigned_pickup_agent, cust_mastercustid from kbcustomers "
						+ " where cust_id=? and cust_branch=?");
				pst.setInt(1, ci.getSenderId());
				pst.setInt(2, currentBranch_G);
				rs = pst.executeQuery();
				if (rs.next()) {
					ci.setMasterSenderId(rs.getInt("cust_mastercustid"));
					ci.setPickupAgent(Utilities.getPickUpAgentForMasterCustomer(conn, ci.getMasterSenderId()));					

				}
				try {rs.close();} catch (Exception e) {/* ignore */}
				try {pst.close();} catch (Exception e) {/* ignore */}
				ci.setReceiverHp1(inputMap_ori.get("rcv_phone1_" + userDefinedMultiNewRowExtension + "_" + j)[0]); // rcv// phone// 1
				ci.setReceiverName(inputMap_ori.get("rcv_name_"+userDefinedMultiNewRowExtension+"_"+j)[0]);
				ci.setLocationDetails(inputMap_ori.get("rcv_more_loc_" + userDefinedMultiNewRowExtension + "_" + j)[0]);// location// dtls
				ci.setDistrict(Integer.parseInt(inputMap_ori.get("rcv_district_" + userDefinedMultiNewRowExtension + "_" + j)[0]));
				ci.setRmk(inputMap_ori.get("rcv_rmk_" + userDefinedMultiNewRowExtension + "_" + j)[0]); // remarks
				//System.out.println("====>"+inputMap_ori.get("c_receiptamt_" + userDefinedMultiNewRowExtension + "_" + j)[0]);
				ci.setReceiptAmtIqd(Double.parseDouble(inputMap_ori.get("c_receiptamt_" + userDefinedMultiNewRowExtension + "_" + j)[0].replace(",", "")));// c_goods_cost_
				//System.out.println("get receipt amt====>"+ci.getReceiptAmtIqd());
				ci.setReceiptAmtUsd(Double.parseDouble(inputMap_ori.get("c_receiptamt_usd_" + userDefinedMultiNewRowExtension + "_" + j)[0].replace(",", "")));// c_goods_cost_
				ci.setCustReceiptNoOri(inputMap_ori.get("c_custreceiptnoori_" + userDefinedMultiNewRowExtension + "_" + j)[0]);
				ci.setRural("N");
				ruralArea = false;
				if (ut.isRuralDistrict(conn, ci.getDistrict(), currentBranch_G)) {
					ci.setRural("Y");
					ruralArea = true;
				}
				// we calculate the shipment cost based on master customerid
				ci.setShipmentCharge(ut.calcShipmentChargesBasedOnDestCity(conn, caseMaster.getCity(), ruralArea, ci.getMasterSenderId(), ci.getSenderId(), currentBranch_G));
				
				// make sure the district is the inside this state 
				if (ut.isDistrictInThisState(conn, ci.getDistrict(), caseMaster.getCity())) {
					cases.add(ci);
				}else {
					msg += "يرجى إعادة أدخال الوصل رقم  "+ci.getCustReceiptNoOri()+"</br>";	
				}
				try {pst.close();} catch (Exception e) {	/* ignore */}// get the pickup agent id
			}
			
			pst = conn.prepareStatement("insert into p_cases "
			+ " (c_createdby	   , c_rcv_name			 , c_rcv_hp1	  , c_rcv_state	, c_rural, "
			+ "  c_rcv_addr_rmk    , c_rmk 				 , c_receiptamt   , c_shipment_cost, c_branchcode, "
			+ "  c_custreceiptnoori, c_rcv_district 	 , c_custid	   	  , c_mastercustid , c_pickupagent, "
			+ "	 c_receiptamt_usd  , c_creationstartpoint, c_assignedagent	 )"
			+ " values ("+CoreUtilities.getQuestionMarks(18)+")", Statement.RETURN_GENERATED_KEYS);
			CaseInformation ci = new CaseInformation();
			int i = 0;
			FlowUtils fu = new FlowUtils();
			for (i = 0; i < cases.size(); i++) {
				ci = cases.get(i);
				pst.setInt(1, userId_G);
				pst.setString(2, ci.getReceiverName());
				pst.setString(3, ci.getReceiverHp1());
				pst.setString(4, caseMaster.getCity());
				pst.setString(5, ci.getRural());
				pst.setString(6, ci.getLocationDetails());
				pst.setString(7, ci.getRmk());
				pst.setDouble(8, ci.getReceiptAmtIqd() * 1000);//so the user can insert without adding the 000
				pst.setDouble(9, ci.getShipmentCharge());
				pst.setInt(10, ci.getCurrentBranch());
				pst.setString(11, ci.getCustReceiptNoOri());
				pst.setInt(12, ci.getDistrict());
				pst.setInt(13, ci.getSenderId());
				pst.setInt(14, ci.getMasterSenderId());
				pst.setInt(15, ci.getPickupAgent());
				pst.setDouble(16, ci.getReceiptAmtUsd());
				pst.setString(17, "SYS-NewCasesByState");
				pst.setInt(18, ci.getDlvAgentId());
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				if (rs.next())
					ci.setCaseid(rs.getInt(1));
				else
					throw new Exception("No case id generate");

				fu.createNewCaseInQueue(conn, ci.getCaseid(), ci.getCurrentBranch());

				try {rs.close();} catch (Exception e) {/* ignore */}
				pst.clearParameters();
			}
			msg +=" عدد الشحنات المدخلة بنجاح "+i;
			conn.commit();
			cases.clear();
		} catch (Exception e) {
			try {conn.rollback();} catch (Exception eRoll) {/**/}
			e.printStackTrace();
			msg = "Error (" + e.getMessage() + ")";
			setInsertErrorFlag(true);
		} finally {
			try {rs.close();} catch (Exception e) {/* ignore */}
			try {pst.close();} catch (Exception e) {/* ignore */}
			try {conn.close();} catch (Exception e) {/* ignore */}
		}
		return msg;
	}
	
	public int getUserStoreCode() {
		return userStoreCode;
	}// end of method getUserStoreCode

	public void setUserStoreCode(int userStoreCode) {
		this.userStoreCode = userStoreCode;
		userDefinedLookups.put("rcv_state_1", "select distinct path_state, st_name_ar from kbpaths join kbstate on path_state = st_code "
							 + "where path_frombranch =" + this.userStoreCode + " and st_active='Y' order by st_order");
	}// end of method setUserStoreCode

}// end of class NewCasesByState
