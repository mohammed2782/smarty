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
import com.app.util.Utilities;
import com.app.util.Utilities_Old;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;
import smarty.db.mysql;

public class NewCasesOldSystem extends CoreMgr{
	public void setUserStoreCode(int userStoreCode) {
		this.userStoreCode = userStoreCode;
		userDefinedLookups.put("rcv_state_1",
				"select distinct path_state, st_name_ar from kbpaths join kbstate on path_state = st_code "
						+ " where path_frombranch =" + this.userStoreCode + " and st_active='Y' order by st_order");
	}
	private LinkedList<CaseInformation> cases;
	private int userStoreCode = 0;
	
	public NewCasesOldSystem() {
		setDisplayMode("NEWSINGLE");
		MainSql = "select '' as barcode, '' as assignagent, " + " '' as createddtls,"
				+ " '' as c_mastercustid, '' as newmastercustomerflag, '' as newmastercustomername, '' as c_mastercusthp, '' as mastercustnameshowonly, "
				+ " '' c_custid, '' as newcustomerflag , '' newcustomername, '' as c_cust_hp,"
				+ " '' as c_branchcode , '' as c_pickup_city, '' as c_pickup_district , '' as c_pickup_more_location,"
				+ " '' as item1 , ''  " + " from p_cases where 1=0";
		canNew = true;
		mainTable = "p_cases";
		cases = new LinkedList<CaseInformation>();
		userDefinedNewFormColNo = 4;
		
		// master customer
		userDefinedNewCols.add("barcode");

		userDefinedColLabel.put("c_custid", "المتجر");
		userDefinedColLabel.put("mastercustnameshowonly", "إسم العميل");
		userDefinedColLabel.put("c_mastercusthp", "هاتف العميل");
		userDefinedColLabel.put("c_branchcode", "الفرع");
		userDefinedColLabel.put("newmastercustomerflag", "عميل جديد؟");
		userDefinedColLabel.put("newcustomerflag", "متجر جديد؟");
		userDefinedColLabel.put("c_cust_hp", "هاتف المتجر");
		userDefinedColLabel.put("c_cust_hp", "هاتف المتجر");
		userDefinedReadOnlyNewCols.add("c_branchcode");

		userDefinedColLabel.put("c_pickup_state", "مخزن");
		userDefinedHiddenNewCols.add("c_pickup_state");

		userDefinedColsMustFill.add("c_cust_hp");
		userDefinedColsMustFill.add("c_custid");
		userDefinedNewCaption = "إنشاء شحنة باركود";
		
	}

	@Override
	public void initialize(HashMap smartyStateMap) {
		userDefinedLookups.put("c_mastercustid", "select mcust_id, mcust_name "
				+ " from kb_mastercustomer where mcust_branchcode="+ lu.getBranchCode() +" and mcust_active = 'Y'");
		
		userDefinedLookups.put("rcv_state_1",
				"select distinct path_state, st_name_ar from kbpaths join kbstate on path_state = st_code "
						+ " where path_frombranch =" + lu.getBranchCode() + " and st_active='Y' order by st_order");
		this.userStoreCode = lu.getBranchCode();
		super.initialize(smartyStateMap);
		sqlColsSizes.put("c_cust_hp", 11);
		sqlColsSizes.put("c_mastercusthp", 11);

	}

	public String userDefinedInjectCodeInNewFromBeforeSubmitButtonAndAFterFields() {
		StringBuilder sb = new StringBuilder("");
		sb.append("<h6 class='mb-1'>تفاصيل الشحنات</h6>");
		sb.append(
				"<table class='table table-bordered table-striped' style='padding:0px;border-width: 0px; background-color:#ebeced ' id='rcv_dtls'>");
		
		sb.append("</table>");
		
		return sb.toString();
	}
	public StringBuilder getRCVDetailsRow(int rcvSeq, String a_receiptNo, int a_branchCode) throws Exception{
		StringBuilder sb = new StringBuilder();
		userDefinedMultiNewRowExtension += "_" + rcvSeq;
		Utilities ut = new Utilities();
		Utilities_Old utOld = new Utilities_Old();
		Connection conn1 = null;
		int rcvDistrict = 0;
		String  destState = "BGD",  rmk = "", locDtls = "",
				receiptAmt = "", 
				labelClass = "form-label", divCLass = "form-group mb-1 col-sm-12 col-md-2";
		int smarty_new_row_seq = rcvSeq;
		String divStyle = "margin-bottom:7px;";
		boolean receiptInSystem = false, receiptUsedBefore = false;
		int custId =0;
		try {
			conn1 = mysql.getConn();
			try {
				receiptInSystem = utOld.checkIfReceiptGeneratedFromSystem(conn1, a_receiptNo, a_branchCode);
				if (!receiptInSystem) {
					throw new Exception ("الوصل رقم "+a_receiptNo+" غير متولد من النظام");
				}
				receiptUsedBefore = utOld.checkIfReceiptUsedBefore(conn1, a_receiptNo, a_branchCode);
				if (receiptUsedBefore) {
					throw new Exception ("الوصل رقم "+a_receiptNo+" تم أستعماله سابقا");
				}
				custId = utOld.getOwnerOfReceipt(conn1, a_receiptNo, a_branchCode);
			} catch (Exception e) {
				throw e;
			}
			sb.append("<tr id='" + userDefinedMultiNewRowExtension + "' rcv_no = '" + smarty_new_row_seq
					+ "' style=\"border-bottom: 2px solid;\">");
			sb.append("<input type='hidden' id='smarty_new_row_seq__" + smarty_new_row_seq
					+ "' name='smarty_new_row_seq__" + smarty_new_row_seq + "'  value='" + smarty_new_row_seq + "'/>");

			// start TD and DIV
			sb.append("<td style='padding-top:10px;width:95%;border-width: 0px; border-bottom: 1px solid #00000054;'>"
					+ "<div class='row'>");
			
			
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
			// 1-Receipt no
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-left:1%;margin-right:1%;'
					+ "<label class='" + labelClass + "'>رقم الوصل</label>" +"<i class=\"ft-package\"></i>"
					+ "<input type='number' readonly value='" + a_receiptNo
					+ "' class='form-control' style='text-align:right;background-color: white;' "
					+ " size='10' name='c_custreceiptnoori_"
					+ userDefinedMultiNewRowExtension + "' " + "id ='c_custreceiptnoori_"
					+ userDefinedMultiNewRowExtension + "' required /></div>");
			// end of receipt no
			HashMap<String,String> lookupsToLoad = new HashMap<String,String>();
			if (custId>0) {
				String custName = ut.getCustomerName(conn1, custId);
				sb.append("<div class='form-group mb-1 col-sm-12 col-md-2' style='"+divStyle+"'><label class='" + labelClass + "'>الزبون</label>"
						+ "<input type='text'  class='form-control' readonly value='"+custName+"' style='text-align:right; background-color:#eee; color: #424242;width:12em;' "
								+ "  name='c_custnameshowonly_"+userDefinedMultiNewRowExtension+"' id ='c_custnameshowonly_"+userDefinedMultiNewRowExtension+"' />");
				sb.append("<input type='hidden' class='form-control'  name='c_custid_"+userDefinedMultiNewRowExtension+"' id ='c_custid_"+userDefinedMultiNewRowExtension+"'  "
								+ " value='"+custId+"'  /></div>");
			}else {
				//customer drop list
				sb.append("<div class='form-group mb-1 col-sm-12 col-md-2' style='"+divStyle+"'><label class='" + labelClass + "'>الزبون</label>"
							+ "<select class='form-control select2'  "
								+ "id='c_custid_"+userDefinedMultiNewRowExtension+"'" + 
								"  name='c_custid_"+userDefinedMultiNewRowExtension+"'  required>");
				
				lookupsToLoad.put("c_custid", "select cust_id, cust_name "
						+ " from kbcustomers where cust_branch="+ a_branchCode+" and cust_active = 'Y'");
				colMapValues = mysqlmgr.loadAllLookups(conn1, lookupsToLoad);
				Map <String , String> lookupsmap = colMapValues.get("c_custid");
				sb.append("<option value='' selected></option> \n");
				if (lookupsmap !=null){
					if (!lookupsmap.isEmpty()){
						for (String code : lookupsmap.keySet()){
							sb.append("<option value='"+code+"' >"+lookupsmap.get(code)+"</option> \n");
						}
					}
				}
				sb.append("</select></div> \n");
			}
			
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
			district = ut.getDistrictOfState(conn1, "BGD");

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
			
			// 2-Receiver Phone 1
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-left:1%;margin-right:10%;'
			+ "<label class='" + labelClass + "'> 1 هاتف المستلم</label>"+ "<i class=\"ft-phone-call\"></i>"
			+ "<input type=\"tel\" style=\"text-align:right;background-color: white;"
			+ " direction: ltr;\" name='rcv_phone1_"+ userDefinedMultiNewRowExtension + "' " 
			+ "id='rcv_phone1_" + userDefinedMultiNewRowExtension + "' class=\"form-control border-start-0\""
			+ "value='' required maxlength=\"11\" size=\"11\" pattern='[0]{1}[7]{1}[0-9]{9}'>"
			+ "</div>");

			// 5- location details
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" 
			+ "<label class='" + labelClass+ "'>تفاصيل العنوان</label>" +"<i class=\"ft-map-pim\"></i>" 
					+ "<textarea class='form-control' style=\"text-align:right;background-color: white;\" name='rcv_more_loc_"+ userDefinedMultiNewRowExtension + "' "
							+ "id ='rcv_more_loc_" + userDefinedMultiNewRowExtension + "'>"
					+ locDtls + "</textarea></div>");

			// 6- Notes //
			sb.append("<div class='" + divCLass + "' style='" + divStyle + "'>" // style='margin-right:13%'
					+ "<label class='" + labelClass + "'>ملاحظات</label>" + "<i class=\"ft-message-square\"></i> "
					+ " <textarea class='form-control' style=\"text-align:right;background-color: white;\" name='rcv_rmk_" + userDefinedMultiNewRowExtension
					+ "' id ='rcv_rmk_" + userDefinedMultiNewRowExtension + "'>" + rmk + "</textarea></div>");
			

			

			sb.append("</div>");// end of dive row
			sb.append("</td>");
			sb.append(
					"<td style='width:5%;vertical-align: bottom; padding: 0; border-width: 0px; border-bottom: 1px solid #00000054;'>"
							+ "<table style='border: 0px solid;' id='side_table_" + smarty_new_row_seq + "'><tr>"

							+ "<td style='border: 0;'>" + "<button type='button' onclick='remove_row("
							+ smarty_new_row_seq + ")' "
							+ " class='btn btn-danger btn-sm'><li class='fa fa-trash'></li></button>" + "</td>"
							+ "<td style='border: 0;'>"
							+ "<span class='badge bg-secondary rounded-pill text-white ' style='font-size: 17px;'>"
							+ smarty_new_row_seq + "</span>" + "</td>" + "</tr>" + "</table>" + "</td>");
			sb.append("</tr>");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {conn1.close();} catch (Exception e) {}
		}
		return sb;
	}
	
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autocommit) { 
		FlowUtils fu = new FlowUtils();
		Connection conn = null;
		PreparedStatement pst = null, pstUpdateBook = null, pstUpdateReciept=null;
		ResultSet rs = null;
		String msg = "تم إنشاء الشحنة بنجاح";
		Utilities ut = new Utilities();
		
		try {
			inputMap_ori = filterRequest(rqs);
			
			LinkedList<Integer> availableCases = new LinkedList<Integer>();
			for (String key : inputMap_ori.keySet()) {// loop to get all the cases from the grid
				if (key.startsWith("smarty_new_row_seq__")) {
					availableCases.add(Integer.parseInt(inputMap_ori.get(key)[0]));
				}
			}
			conn = mysql.getConn();
			int currentBranch_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
			pst = conn.prepareStatement(
					" select cust_mastercustid, mcust_phone1 , cust_assigned_pickup_agent , cust_phone1 "
					+ " from kbcustomers join kb_mastercustomer on cust_mastercustid = mcust_id  where cust_id=? and cust_branch=?");
			boolean ruralArea = false;
			for (Integer j : availableCases) {
				CaseInformation ci = new CaseInformation();
				ci.setSmarty_new_row_seq(j);
				ci.setSenderId(Integer.parseInt(inputMap_ori.get("c_custid_" + userDefinedMultiNewRowExtension + "_" + j)[0]));
				ci.setReceiverHp1(inputMap_ori.get("rcv_phone1_" + userDefinedMultiNewRowExtension + "_" + j)[0]); // rcv// phone// 1
				ci.setState(inputMap_ori.get("rcv_city_" + userDefinedMultiNewRowExtension + "_" + j)[0]);// rcv city
				ci.setDistrict(Integer.parseInt(inputMap_ori.get("rcv_district_" + userDefinedMultiNewRowExtension + "_" + j)[0]));
				ci.setLocationDetails(inputMap_ori.get("rcv_more_loc_" + userDefinedMultiNewRowExtension + "_" + j)[0]);// location// dtls
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
				pst.setInt(1, ci.getSenderId());
				pst.setInt(2, currentBranch_G);
				rs = pst.executeQuery();
				if (rs.next()) {
					ci.setMasterSenderHp1(rs.getString("mcust_phone1"));
					ci.setMasterSenderId(rs.getInt("cust_mastercustid"));
					ci.setSenderHp(rs.getString("cust_phone1"));
					ci.setPickupAgent(Utilities.getPickUpAgentForMasterCustomer(conn, ci.getMasterSenderId()));
					
				}
				try {rs.close();} catch (Exception e) {/* ignore */}
				pst.clearParameters();
				
				// we calculate the shipment cost based on master customerid
				ci.setShipmentCharge(ut.calcShipmentChargesBasedOnDestCity(conn, ci.getState(), ruralArea,
						ci.getMasterSenderId(), ci.getSenderId(), currentBranch_G));
				cases.add(ci);
			}
			try {
				pst.close();} catch (Exception e) {	/* ignore */}// get the pickup agent id
				pst = conn.prepareStatement("insert into p_cases "
						+ " (c_createdby     , c_rcv_hp1	 , c_rcv_state	  , c_rural	     , c_rcv_addr_rmk    , "
						+ "  c_rmk 		     , c_receiptamt  , c_shipment_cost, c_branchcode , c_custreceiptnoori, "
						+ "  c_rcv_district  , c_mastercustid, c_mastercusthp , c_pickupagent, c_creationstartpoint,"
						+ "	 c_receiptamt_usd, c_custid 	 , c_custhp		  , c_receiptfromsystem)"
						+ " values ("+CoreUtilities.getQuestionMarks(18)+", 'Y')", Statement.RETURN_GENERATED_KEYS);
				
			pstUpdateReciept 	= conn.prepareStatement("update p_books_rcp set br_cid=?, br_custid=? where br_rcp_no=? and br_branchid=? ");
			
			String booksTable = "p_books_tlk";
			if(currentBranch_G == 34) {
				booksTable = "p_books_tlr";
			}
			pstUpdateBook	 	= conn.prepareStatement("update "+booksTable+" set b_usedinsystem='Y' where b_id in"
													+ "  (select br_bid from p_books_rcp where br_rcp_no=?) ");
			
			CaseInformation ci = new CaseInformation();
			int userId_G = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
			for (int i = 0; i < cases.size(); i++) {
				ci = cases.get(i);
				pst.setInt(1, userId_G);
				pst.setString(2, ci.getReceiverHp1());
				pst.setString(3, ci.getState());
				pst.setString(4, ci.getRural());
				pst.setString(5, ci.getLocationDetails());
				pst.setString(6, ci.getRmk());
				pst.setDouble(7, ci.getReceiptAmtIqd() * 1000);//so the user can insert without adding the 000
				pst.setDouble(8, ci.getShipmentCharge());
				pst.setInt(9, currentBranch_G);
				pst.setString(10, ci.getCustReceiptNoOri());
				pst.setInt(11, ci.getDistrict());
				pst.setInt(12, ci.getMasterSenderId());
				pst.setString(13, ci.getMasterSenderHp1());
				pst.setInt(14, ci.getPickupAgent());
				pst.setString(15, "SYS-NewCases");
				pst.setDouble(16, ci.getReceiptAmtUsd());
				pst.setInt(17, ci.getSenderId());
				pst.setString(18, ci.getSenderHp());
				pst.executeUpdate();

				rs = pst.getGeneratedKeys();
				if (rs.next())
					ci.setCaseid(rs.getInt(1));
				else
					throw new Exception("No case id generate");

				fu.createNewCaseInQueue(conn, ci.getCaseid(), currentBranch_G);

				try {rs.close();} catch (Exception e) {/* ignore */}
				pst.clearParameters();
				
				pstUpdateBook.setString(1, ci.getCustReceiptNoOri());
				pstUpdateBook.executeUpdate();
				pstUpdateBook.clearParameters();
				
				//update the receipt to map with caseid
				pstUpdateReciept.setInt(1, ci.getCaseid());
				pstUpdateReciept.setInt(2, ci.getSenderId());
				pstUpdateReciept.setString(3, ci.getCustReceiptNoOri());
				pstUpdateReciept.setInt(4, currentBranch_G);
				pstUpdateReciept.executeUpdate();
				pstUpdateReciept.clearParameters();
			}
			conn.commit();
			cases.clear();
		} catch (Exception e) {
			try {conn.rollback();} catch (Exception eRoll) {/**/}
			e.printStackTrace();
			msg = "Error (" + e.getMessage() + ")";
			setInsertErrorFlag(true);
		} finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			try {pstUpdateReciept.close();}catch(Exception e) {/*ignore*/}
			try {pstUpdateBook.close();}catch(Exception e) {/*ignore*/}
			try {conn.close();}catch(Exception e) {/*ignore*/}
		}

		return msg;
	}
}