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
import com.app.util.Utilities;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;
import smarty.db.mysql;

public class NewCasesByCustomer3 extends CoreMgr {

	private LinkedList<CaseInformation> cases;
	private int userStoreCode = 0;

	public NewCasesByCustomer3() {
		setDisplayMode("NEWSINGLE");

		MainSql = " select '' as sender_customer, '' as dlvagent, '' as dummy " 
				+ " from p_cases "
				+ " where 1=0";

		mainTable = "p_cases";

		canNew = true;

		cases = new LinkedList<CaseInformation>();

		userDefinedNewCaption = "خلق شحنه جديدة - على أساس الزبون 3 ";
		userDefinedNewFormColNo = 2;

		userDefinedFieldSetCols.put("rcv_city", " ");
		userDefinedFieldSetEndWithCols.add("dummy");

		userDefinedColLabel.put("sender_customer", "المتجر");
		userDefinedColLabel.put("dlvagent", "مندوب التوصيل");
		userDefinedNewCols.add("sender_customer");
		userDefinedNewCols.add("dlvagent");
		userDefinedNewColsHtmlType.put("sender_customer", "DROPLIST");
		userDefinedNewColsHtmlType.put("dlvagent", "DROPLIST");
		userDefinedColsMustFill.add("sender_customer");

	}

	@Override
	public void initialize(HashMap smartyStateMap) {
		userDefinedLookups.put("rcv_state_1", "select distinct path_state, st_name_ar from kbpaths join kbstate on path_state = st_code "
						     + "where path_frombranch =" + lu.getBranchCode() + " and st_active='Y' order by st_order" );
		userDefinedLookups.put("rcv_city", "select distinct path_state, st_name_ar from kbpaths join kbstate on path_state = st_code "
							 + "where path_frombranch =" + lu.getBranchCode() + " and st_active='Y' order by st_order" );

		userDefinedLookups.put("sender_customer","select cust_id , cust_name from kbcustomers where cust_branch=" + lu.getBranchCode() + "  ");
		userDefinedLookups.put("dlvagent", "select us_id , us_name from kbusers "
							 + " where us_branchcode=" + lu.getBranchCode() + " and us_active='Y'"
							 		+ " and us_rank = 'DLVAGENT' ");

		this.userStoreCode = lu.getBranchCode();
		super.initialize(smartyStateMap);
	}// end of method initialize

	public String userDefinedInjectCodeInNewFromBeforeSubmitButtonAndAFterFields() {
		StringBuilder sb = new StringBuilder("");
		sb.append("<h6 class='mb-1'>تفاصيل الشحنات</h6>");
		sb.append(
				"<table class='table table-bordered table-striped' style='padding:0px;border-width: 0px; background-color:#ebeced ' id='rcv_dtls'>");
		try {
			sb.append(getRCVDetailsRow(1, lu.getBranchCode()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int c = 2; c <= cases.size(); c++)
			try {
				sb.append(getRCVDetailsRow(c, lu.getBranchCode()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		sb.append("</table>");
		sb.append("<div class='row'><div class='col-xl-2 col-sm-3'>"
				+ "<button type='button' id='add_rcv_dtls' class='btn btn-secondary btn-min-width btn-glow mr-1 mb-1 waves-effect waves-light' >إضافة وصل آخر <i class=\"ft-plus-circle\"></i>"
				+ "</button></div></div>");
		return sb.toString();
	}// end of method userDefinedInjectCodeInNewFromBeforeSubmitButtonAndAFterFields

	public StringBuilder getRCVDetailsRow(int rcvSeq, int a_branchCode) {
		StringBuilder sb = new StringBuilder();
		userDefinedMultiNewRowExtension += "_" + rcvSeq;
		Utilities ut = new Utilities();
		Connection conn1 = null;
		int rcvDistrict = 0;
		String  destState = lu.getBranchStateCode(),  rmk = "", locDtls = "",
				receiptAmt = "", 
				labelClass = "form-label", divCLass = "form-group mb-1 col-sm-12 col-md-2";
		int smarty_new_row_seq = rcvSeq;
		String divStyle = "margin-bottom:7px;";
		try {
			conn1 = mysql.getConn();
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
					+ "<input type='number'  value='' "
					+ " class='form-control' style='text-align:right;background-color: white;' "
					+ " size='10' name='c_custreceiptnoori_"
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
			
			
HashMap<String,String> lookupsToLoad = new HashMap<String,String>();
			
			// 5-state
			String style = "text-align:right;  padding: 0 10px 0 10px;"
					+ "   border: 1px solid #7dc6dd;min-width:150px";
			lookupsToLoad = new HashMap<String,String>();
			lookupsToLoad.put("rcv_state_1",
					"select distinct path_state, st_name_ar from kbpaths join kbstate on path_state = st_code "
							+ " where path_frombranch =" + a_branchCode + " and st_active='Y' order by st_order");
			colMapValues = mysqlmgr.loadAllLookups(conn1, lookupsToLoad);
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>"
					+ "<label class='" + labelClass + "'>المحافظة</label> <i class=\"ft-map\"></i>" 
					+" <select class='form-control select2' onchange='loadDistrict(" + rcvSeq + ");' "
					+ "id='rcv_city_" + userDefinedMultiNewRowExtension + "' "
					+ " name='rcv_city_" + userDefinedMultiNewRowExtension + "' style='" + style + "' required>");
			Map<String, String> lookupsmap = colMapValues.get("rcv_state_1");
			if (lookupsmap != null && !lookupsmap.isEmpty()) {
				for (String code : lookupsmap.keySet()) {
					if (destState.equalsIgnoreCase(code)) {
						sb.append("<option value='" + code + "' selected>" + lookupsmap.get(code) + "</option> \n");
					}else {
						sb.append("<option value='" + code + "' >" + lookupsmap.get(code) + "</option> \n");
					}
				}
			}
			sb.append("</select></div>");
			// end of state

			// 6- district inside state
			LinkedHashMap<Integer, String> district = new LinkedHashMap<Integer, String>();
			district = ut.getDistrictOfState(conn1, lu.getBranchStateCode());

			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-right:7%;margin-left:2%;'
					+ "<label class='" + labelClass + "'>المنطقه</label><i class=\"ft-map-pim\"></i>"
					+ "<select class='form-control select2'   id='rcv_district_" + userDefinedMultiNewRowExtension + "' "
					+ " name='rcv_district_" + userDefinedMultiNewRowExtension + "' style='" + style + "' required >");
			sb.append("<option value='' selected></option> \n");
			for (int code : district.keySet()) {
				if (rcvDistrict == code)
					sb.append("<option value='" + code + "' selected>" + district.get(code) + "</option> \n");
				else
					sb.append("<option value='" + code + "' >" + district.get(code) + "</option> \n");
			}
			sb.append("</select></div> \n");
						
			// 4-Receipt Price IQD 
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-left:1%;margin-right:1%;'
					+ "	<label class='" + labelClass + "'>مبلغ الوصل د.ع</label>"+"</i>"
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
			
			
			
			
			// 5- location details
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" 
			+ "<label class='" + labelClass+ "'>تفاصيل العنوان</label>" +"<i class=\"ft-map-pim\"></i>" 
					+ "<textarea class='form-control'  tabindex='99999'  style=\"text-align:right;background-color: white;\" name='rcv_more_loc_"+ userDefinedMultiNewRowExtension + "' "
							+ "id ='rcv_more_loc_" + userDefinedMultiNewRowExtension + "'>"
					+ locDtls + "</textarea></div>");

			// 6- Notes //
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-right:13%'
					+ "<label class='" + labelClass + "'>ملاحظات</label>" + "<i class=\"ft-message-square\"></i> "
					+ " <textarea class='form-control'  tabindex='99999'  style=\"text-align:right;background-color: white;\" name='rcv_rmk_" + userDefinedMultiNewRowExtension
					+ "' id ='rcv_rmk_" + userDefinedMultiNewRowExtension + "'>" + rmk + "</textarea></div>");
			
			// 5-Receipt Price USD
						sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-left:1%;margin-right:1%;'
								+ "	<label class='" + labelClass + "'>مبلغ الوصل</label>"+"<i class=\"ft-dollar-sign\"></i>"
								+ "<input type='text' value='0' tabindex='99999'  class='form-control' style='text-align:right;background-color: #e6ffe6;' "
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
						
			
			// 7- shipment cost paid in advance
//			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>"
//					+ "<label class='" + labelClass + "'>مدفوع مبلغ التوصيل مقدما؟ </br> مبلغ الوصل يجب أن يكون صفر</label>" + " "
//			 +"<input type=\"checkbox\"  "
//			+ " id=\"shipment_cost_paid_check_"+userDefinedMultiNewRowExtension+"\" "
//			+ " name=\"shipment_cost_paid_check_"+userDefinedMultiNewRowExtension+"\" "
//					+ "onclick=\"checkShipmentCostPaid(this, '"+rcvSeq+"')\">");
//			sb.append("</div>");// end of dive row
//			
//			//list of agents per state
//			LinkedHashMap<String,String> agents = new LinkedHashMap<String,String> ();
//			agents  = ut.getListOfAgentsPerState(conn1,destState, a_branchCode);
//			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>"
//					+ "<label class='"+labelClass+"'>مندوب التوصيل</label>"
//			+ "<select class='form-control select2'   "
//			+ " id='c_assignedagent_"+userDefinedMultiNewRowExtension+"' " 
//			+ "  name='c_assignedagent_"+userDefinedMultiNewRowExtension+"' style='"+style+"' >");
//			sb.append("<option value='' selected></option>");
//			for (String code : agents.keySet()){
//				sb.append("<option value='"+code+"' >"+agents.get(code)+"</option> \n");
//			}
//			sb.append("</select></div> \n");

			sb.append("</div>");// end of dive row
			sb.append("</td>");
			sb.append(
					"<td style='width:5%;vertical-align: bottom; padding: 0; border-width: 0px; border-bottom: 1px solid #00000054;'>"
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
		return sb;
	}// end of method getRCVDetailsRow

	
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autocommit) {
		Connection conn = null;
		PreparedStatement pst = null, pstCustomerInfo = null;
		ResultSet rs = null;
		int custId = 0;
		boolean ruralArea = false;
		String msg = " ";
		try {
			inputMap_ori = filterRequest(rqs);
			conn = mysql.getConn();
			
			LinkedList<Integer> availableCases = new LinkedList<Integer>();
			for (String key : inputMap_ori.keySet()) {// loop to get all the cases from the grid
				if (key.startsWith("smarty_new_row_seq__")) {
					availableCases.add(Integer.parseInt(inputMap_ori.get(key)[0]));
				}
			}
			custId = Integer.parseInt(inputMap_ori.get("sender_customer")[0]);
			int dlvagentId = 0;
			try {
				dlvagentId = Integer.parseInt(inputMap_ori.get("dlvagent")[0]);
			}catch(Exception e) {
				/*ignore*/
			}
			// now for cases
			Utilities ut = new Utilities();
			int userId_G = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
			int currentBranch_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
			pstCustomerInfo = conn.prepareStatement(" select  cust_assigned_pickup_agent, cust_mastercustid from kbcustomers "
					+ " where cust_id=? and cust_branch=?");
			for (Integer j : availableCases) {
				CaseInformation ci = new CaseInformation();
				ci.setSmarty_new_row_seq(j);
				ci.setCurrentBranch(currentBranch_G);
				ci.setSenderId(custId);
				ci.setState(inputMap_ori.get("rcv_city_" + userDefinedMultiNewRowExtension + "_" + j)[0]);
				pstCustomerInfo.setInt(1, ci.getSenderId());
				pstCustomerInfo.setInt(2, currentBranch_G);
				rs = pstCustomerInfo.executeQuery();
				if (rs.next()) {
					ci.setMasterSenderId(rs.getInt("cust_mastercustid"));
					ci.setPickupAgent(Utilities.getPickUpAgentForMasterCustomer(conn, ci.getMasterSenderId()));					

				}
				try {rs.close();} catch (Exception e) {/* ignore */}
//				if(inputMap_ori.containsKey(
//						("c_assignedagent_" + userDefinedMultiNewRowExtension + "_" + j))
//					&&
//						inputMap_ori.get
//								("c_assignedagent_" + userDefinedMultiNewRowExtension + "_" + j)[0] != null
//					&& !inputMap_ori.get
//					("c_assignedagent_" + userDefinedMultiNewRowExtension + "_" + j)[0].trim().isEmpty()) {
//					ci.setDlvAgentId(
//						Integer.parseInt(inputMap_ori.get
//								("c_assignedagent_" + userDefinedMultiNewRowExtension + "_" + j)[0]));
//				}else {
//					ci.setDlvAgentId(0);
//				}
				
				ci.setReceiverHp1(inputMap_ori.get("rcv_phone1_" + userDefinedMultiNewRowExtension + "_" + j)[0]); // rcv// phone// 1
				ci.setLocationDetails(inputMap_ori.get("rcv_more_loc_" + userDefinedMultiNewRowExtension + "_" + j)[0]);// location// dtls
				ci.setDistrict(Integer.parseInt(inputMap_ori.get("rcv_district_" + userDefinedMultiNewRowExtension + "_" + j)[0]));
				ci.setRmk(inputMap_ori.get("rcv_rmk_" + userDefinedMultiNewRowExtension + "_" + j)[0]); // remarks
				ci.setReceiptAmtIqd(Double.parseDouble(inputMap_ori.get("c_receiptamt_" + userDefinedMultiNewRowExtension + "_" + j)[0].replace(",", "")));// c_goods_cost_
				ci.setReceiptAmtUsd(Double.parseDouble(inputMap_ori.get("c_receiptamt_usd_" + userDefinedMultiNewRowExtension + "_" + j)[0].replace(",", "")));// c_goods_cost_
				ci.setCustReceiptNoOri(inputMap_ori.get("c_custreceiptnoori_" + userDefinedMultiNewRowExtension + "_" + j)[0]);
				ci.setRural("N");
				ruralArea = false;
				if (ut.isRuralDistrict(conn, ci.getDistrict(), currentBranch_G)) {
					ci.setRural("Y");
					ruralArea = true;
				}
				if (inputMap_ori.containsKey("shipment_cost_paid_check_" + userDefinedMultiNewRowExtension + "_" + j)
						&&	inputMap_ori.get("shipment_cost_paid_check_" + userDefinedMultiNewRowExtension + "_" + j)[0]!=null
						&& inputMap_ori.get("shipment_cost_paid_check_" + userDefinedMultiNewRowExtension + "_" + j)[0].contentEquals("on")
					   ) {
						ci.setPaidDeliveryCostInAdvance("Y");
					}else {
						ci.setPaidDeliveryCostInAdvance("N");
					}
				if (ci.getPaidDeliveryCostInAdvance().equalsIgnoreCase("Y")) {
					if (ci.getReceiptAmtIqd()>0 && ci.getReceiptAmtUsd()>0) {
						throw new Exception ("لا يمكن تحديد الوصل مدفوع توصيل فقط اذا كان مبلغ الوصل اكبر من صفر");
					}
				}
				// we calculate the shipment cost based on master customerid
				ci.setShipmentCharge(ut.calcShipmentChargesBasedOnDestCity(conn, ci.getState(), ruralArea, ci.getMasterSenderId(), ci.getSenderId(), currentBranch_G));
				
				// make sure the district is the inside this state 
				if (ut.isDistrictInThisState(conn, ci.getDistrict(), ci.getState())) {
					cases.add(ci);
				}else {
					msg += "يرجى إعادة أدخال الوصل رقم  "+ci.getCustReceiptNoOri()+"</br>";	
				}
				try {pst.close();} catch (Exception e) {	/* ignore */}// get the pickup agent id
			}
			
			pst = conn.prepareStatement("insert into p_cases "
			+ " (c_createdby	   , c_rcv_name			 , c_rcv_hp1	  , c_rcv_state			, c_rural, "
			+ "  c_rcv_addr_rmk    , c_rmk 				 , c_receiptamt   , c_shipment_cost		, c_branchcode, "
			+ "  c_custreceiptnoori, c_rcv_district 	 , c_custid	   	  , c_mastercustid 		, c_pickupagent, "
			+ "	 c_receiptamt_usd  , c_creationstartpoint, c_assignedagent, c_paid_delivery_cost_in_advance	 )"
			+ " values ("+CoreUtilities.getQuestionMarks(19)+")", Statement.RETURN_GENERATED_KEYS);
			CaseInformation ci = new CaseInformation();
			int i = 0;
			FlowUtils fu = new FlowUtils();
			for (i = 0; i < cases.size(); i++) {
				ci = cases.get(i);
				pst.setInt(1, userId_G);
				pst.setString(2, ci.getReceiverName());
				pst.setString(3, ci.getReceiverHp1());
				pst.setString(4, ci.getState());
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
				pst.setString(17, "SYS-NewCasesByCustomer");
				pst.setInt(18, dlvagentId);
				pst.setString(19, ci.getPaidDeliveryCostInAdvance());
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
