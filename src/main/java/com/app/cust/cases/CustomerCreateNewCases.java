package com.app.cust.cases;
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
import com.app.cases.CaseInformation;
import com.app.cases.MasterCaseInformation;
import smarty.core.CoreMgr;
import smarty.core.html.DropList;
import smarty.db.mysql;

import com.app.util.Utilities;

public class CustomerCreateNewCases extends CoreMgr{
	
	private LinkedList <CaseInformation> cases ;
	private int userStoreCode = 0;
	private int masterCustId = 0;
	
	private String shopsCommaSeperated;
	public int getMasterCustId() {
		return masterCustId;
	}
	public void setMasterCustId(int masterCustId) {
		this.masterCustId = masterCustId;
	}
	
	public CustomerCreateNewCases () {
		// I NEED TO CREATE MULTI-INSERT-FORM
		
		setDisplayMode("NEWSINGLE");
		MainSql =  "select '' as assignagent, "
				+ " '' as createddtls,"
				+ " '' as mastercustnameshowonly, '' as c_custid ,"
				+ " '' as c_branchcode , '' as c_pickup_city, '' as c_pickup_district , '' as c_pickup_more_location,"
				+ " '' as item1 , ''  "
				+ " from p_cases where 1=0"; 
		canNew = true;
		mainTable = "p_cases";
		cases = new LinkedList<CaseInformation>();
		userDefinedNewFormColNo = 4;
		userDefinedFieldSetCols.put("c_custid", "العميل");
		
		
	
		userDefinedNewCols.add("c_custid");
		
		//userDefinedNewColsHtmlType.put("newmastercustomerflag", "CHECKBOX");
		
		userDefinedNewColsHtmlType.put("c_cust_hp", "TEXT");
		userDefinedNewColsHtmlType.put("mastercustnameshowonly","TEXT");
		userDefinedNewColsHtmlType.put("c_branchcode","TEXT");
		userDefinedNewColsHtmlType.put("c_custid","DROPLIST");
		
		
		userDefinedLookups.put("rcv_broken_1", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		
		userDefinedColLabel.put("c_custid", "المتجر");
		userDefinedColLabel.put("mastercustnameshowonly", "العميل");
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
		userDefinedReadOnlyNewCols.add("mastercustnameshowonly");
		
		userDefinedColLabel.put("c_pickup_state","مخزن");
		userDefinedHiddenNewCols.add("c_pickup_state");
		userDefinedLookups.put("rcv_state_1", "select st_code , st_name_ar from kbstate where st_active='Y' order by st_order");
	
		userDefinedColsMustFill.add("c_custid");
		
		userDefinedNewCaption = "خلق شحنه جديده";
		 
	}
	@Override
	public void initialize(HashMap smartyStateMap){
		int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		int masterCustId = Integer.parseInt(replaceVarsinString("{mastercustidlogin}", arrayGlobals).trim());
		String shopsCommaSeperated = replaceVarsinString("{shopsCommaSeperated}", arrayGlobals).trim();
		String masterCustName  = replaceVarsinString("{username}", arrayGlobals).trim();
		userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers where cust_id in ("+shopsCommaSeperated+")  ");
		userDefinedLookups.put("c_mastercustid", "select mcust_id , mcust_name from kb_mastercustomer"
				+ "  where mcust_branchcode="+userstorecode+" and mcust_active='Y' ");
		
		this.userStoreCode = userstorecode;
		this.masterCustId = masterCustId;
		this.setShopsCommaSeperated(shopsCommaSeperated);
		super.initialize(smartyStateMap);
		sqlColsSizes.put("c_cust_hp", 11);
	 sqlColsSizes.put("c_mastercusthp", 11);
	 
	 userDefinedNewColsDefualtValues.put("mastercustnameshowonly", new String [] {masterCustName});
	}
	
	public StringBuilder getNewForm(){//Generte THe New Form
		if (userDefinedNewCols.isEmpty())//To check if the userDefinedNewCols Had not been Set
			return new StringBuilder("The New Columns List is Not Set");
		String labelName=null;
		int rowNum = 0 ;
		boolean required=false;
		jsmgr.userDefinedColsHtmlType = userDefinedNewColsHtmlType;
		StringBuilder newForm = new StringBuilder("");
		String  enctype="", BackGroundColor="";
		ArrayList<String> displayValue = new ArrayList<String>();
		HashMap<String , String> tipsList = new HashMap<String ,String>();
		String Readonly="";
		boolean Disabled = false , hidden =false;
		String myClassBeanNoDots = myClassBean.replace(".", "_dot_");
		String labelClass ="form-label";
		String inputClass ="form-control";
		String inputDivClass="col-md-12 col-sm-6";
		if (userDefinedNewFormColNo == 2)
			inputDivClass="col-md-6 col-sm-6";
		else if (userDefinedNewFormColNo ==3){
			inputDivClass="col-md-4 col-sm-6";
		}else if (userDefinedNewFormColNo >3){
			inputDivClass="col-md-3 col-sm-6";
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
		newForm.append("<div class='row'><!-- ROW -->");
		newForm.append("<div class='col-xl-12 col-md-10 col-sm-12 mx-auto'> <!-- col-12 -->"
				+ "	<h6 class='mb-0 text-uppercase'>"+userDefinedNewCaption+"</h6><hr>");
		newForm.append("<div class='card border-top border-0 border-4 border-white'> <!-- card -->"
				+ "<div class=\"card-body p-5\"> <!-- card body -->"
		+ "<form id='"+myClassBean.replace(".", "_dot_")+"' name='"+myClassBean+"' action='?myClassBean="+myClassBean+"&new=1' method='POST'"
		+ " data-parsley-validate class='row g-3' "+enctype+">");
		boolean startFieldSet = false;
	
		
		for (String removeFileNameCol : userDefinedStoreFileNameColumns.keySet()){
			if (userDefinedNewCols.contains(removeFileNameCol)){
				if (userDefinedNewCols.contains(userDefinedStoreFileNameColumns.get(removeFileNameCol)))
					userDefinedNewCols.remove(userDefinedStoreFileNameColumns.get(removeFileNameCol));
				else
					return new StringBuilder(removeFileNameCol+" does not have userDefinedStoreFileNameColumns column");
			}
		}
		String hiddenStyle= " display:none; ";
		for (String key :userDefinedNewCols){
			 if (!userDefinedHiddenNewCols.contains(key)){// if not hidden	
				 hiddenStyle= " margin-top:7px; ";
			 }else {
				 hiddenStyle=  " display:none; margin-top:7px; ";
			 }
				 if(userDefinedFieldSetCols!=null)
					 if (userDefinedFieldSetCols.containsKey(key)){
						// newForm.append("<fieldset class='scheduler-border'>");
						// newForm.append("<legend class='scheduler-border'>"+userDefinedFieldSetCols.get(key)+"</legend>");
						 startFieldSet = true;
					 }
				 /*if (userDefinedNewFormColNo==1){
					 newForm.append("<div class='form-group'>");
				 }else if (rowNum%userDefinedNewFormColNo==0 || rowNum==0){
					 newForm.append("<div class='form-group'>");
				 }	 */
				 rowNum++; 
				if (!userDefinedColLabel.containsKey(key))
					labelName = key;
				else
					labelName = userDefinedColLabel.get(key);

				required = false;
				if (userDefinedColsMustFill.contains(key)){//check for the must fill
					required = true;
					//HtmlInForm = HtmlInForm + " <font size='4' color='red'>*</font>";
					//jsValidatorMustFill= jsValidatorMustFill+jsmgr.genJSMustFill(key , userDefinedColLabel.get(key));
				}
				if (userDefinedNewColsHtmlType.get(key).equalsIgnoreCase("CHECKBOX"))
					newForm.append("<div class='col-md-2 col-sm-2' style='"+hiddenStyle+"'  div_fornew_input_smarty='smarty_newcol_"+key+"'>");
				else
					newForm.append("<div class='"+inputDivClass+"' style='"+hiddenStyle+"'  div_fornew_input_smarty='smarty_newcol_"+key+"'>");
				newForm.append("<label id='"+key+"_label' class='"+labelClass+"' >"+labelName);
				if(required)
					newForm.append("<span class='required'> *</span>");
				newForm.append("</label>");
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
						   userDefinedMinValMap		 , userDefinedMaxValMap,
						   userDefinedNewColHtmlAttr.get(key)));		
				newForm.append("</div>");
				/*if (userDefinedNewFormColNo==1){
					 newForm.append("</div>"); //end of form group	 
				 }else if ((rowNum)%userDefinedNewFormColNo==0 && rowNum>1){
					 newForm.append("</div>");
				 }*/
				 if (startFieldSet){
					 if (userDefinedFieldSetEndWithCols.contains(key)){
						 startFieldSet = false;
						 //newForm.append("</fieldset>");
						 newForm.append("<hr style='margin-top: 7px;margin-bottom: 7px;'>");
						 rowNum = userDefinedNewFormColNo;
					 }
				 }
			/*}else{// if hidden
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
*/			 newForm.append(genHotLookupsjs(key , required , false));
			 
		}//end of cols loop
		/*if (!((rowNum)%userDefinedNewFormColNo==0 && rowNum>1)){
			 newForm.append("</div>");
		}*/
		if (startFieldSet){
			newForm.append("<hr>");
		}
		
		//add the customized table here
		
		 newForm.append("<h6 class='mb-0 text-uppercase'>تفاصيل الشحنات</h6>");
		 //System.out.println("----->"+cases);
		 newForm.append("<table class='table table-bordered table-striped' id='rcv_dtls'>");
		 newForm.append(getRCVDetailsRow(1,0));
		 for (int c = 2 ; c<=cases.size(); c++)
			 newForm.append(getRCVDetailsRow(c,0));
		 newForm.append("</table>");
		 //newForm.append(getRCVTableDtls());
		 newForm.append("</br><button type='button' id='add_rcv_dtls' class='btn btn-warning col-xl-2 col-sm-3' >إضافة وصل آخر <li class=\"lni lni-circle-plus\"></li></button> ");
		
		
		
		newForm.append("<hr>");
		newForm.append("<div class='row'>");
		newForm.append("	<div class='col-6 offset-5'>");
		newForm.append("		<button type='submit' id='save_new_form_"+myClassBean+"' value='save' class='btn btn-success save_form_btn'>خلق الشحنات <i class=\"lni lni-exit-down\"></i></button>");
		newForm.append("	</div>");
		newForm.append("</div>");
		newForm.append("</form>");//End of Form
		
		newForm.append("<script> "); 
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
		newForm.append("</div><!-- card body -->"
				+ "</div><!-- card --></div>"
				+ "<!-- col 12 --></div><!-- row -->");
		return newForm;
	}
	
	
	public StringBuilder getRCVDetailsRow(int rcvSeq, int custId) {
		StringBuilder sb = new StringBuilder();
		userDefinedMultiNewRowExtension += "_"+rcvSeq;
		String stylediv = "style=''";
		Utilities ut = new Utilities();
		Connection conn1 = null;
		boolean ruralArea = false;
		int rcvDistrict = 0, rcvItemsDlts = 0;
		String defaultRcvName = "", fragile = "", destState="BGD", rural ="N", rmk="", locDtls="",  brinBackItmes="", receiptAmt="", 
				qty="1", shipmentCost = "0" , receiptNo="", labelClass="form-label", checked = "", divCLass="col-md-2 col-sm-6 col-xs-3" ;
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
				receiptAmt = ci.getReceiptAmt()+"";
				shipmentCost = ci.getShipmentCharge()+"";
				receiptNo = ci.getCustReceiptNoOri();
				smarty_new_row_seq = ci.getSmarty_new_row_seq();
				
				rcvDistrict  = ci.getDistrict();
				rcvItemsDlts = Integer.parseInt(ci.getProductCodes());
			}
			if (rural.equalsIgnoreCase("Y"))
				ruralArea = true;
			//shipmentCost = Double.toString(ut.calcShipmentChargesBasedOnDestCity(conn1,  destState, ruralArea, custName, userStoreCode));
			sb.append( "<tr id='"+userDefinedMultiNewRowExtension+"' rcv_no = '"+smarty_new_row_seq+"' style=\"border-bottom: 2px solid;\">");
			sb.append("<input type='hidden' id='smarty_new_row_seq__"+smarty_new_row_seq+"' name='smarty_new_row_seq__"+smarty_new_row_seq+"'  value='"+smarty_new_row_seq+"'/>");
		
			// start TD and DIV
			sb.append("<td style='padding-top:15px; width:95%'>"
					+ "<div class='row'>");
		
			
		
			// 1-Receipt no
			
			// 8- Receiver Name
			sb.append(
			"<div class='"+divCLass+"' style='"+divStyle+"'>" // "+stylediv+"
			+ "<label class='"+labelClass+"'>أسم الزبون</label>"
			+ "<div class=\"input-group\">"
			+ "<span class='input-group-text'><i class='bx bxs-user'></i></span>"
			+ "<input type='text' class='form-control border-start-0' style='text-align:right; ' name='rcv_name_"+userDefinedMultiNewRowExtension+"' "
			+ "id ='rcv_name_"+userDefinedMultiNewRowExtension+"' size='15'"
			+ "value='"+defaultRcvName+"'  /></div>"
			+"</div>");
						
			
			// 9-Receiver Phone 1
			sb.append(
			"<div class='"+divCLass+"' style='"+divStyle+"'>" // style='margin-left:1%;margin-right:10%;'
			+ "<label class='"+labelClass+"'>هاتف الزبون 1</label>"
			+ "<div class=\"input-group\">"
					+ "<span class='input-group-text'><i class='lni lni-phone'></i></span>"
			+ "<input type=\"tel\" style=\"text-align:right;  direction: ltr;\" name='rcv_phone1_"+userDefinedMultiNewRowExtension+"' "
			+ "id='rcv_phone1_"+userDefinedMultiNewRowExtension+"' class=\"form-control border-start-0\""
			+ "value='' required=\"required\" maxlength=\"11\" size=\"11\" pattern='[0-9\\u0660-\\u0669]{11}'>"
			+"</div></div>");
			
			// 9-Receiver Phone 2
			sb.append(
			"<div class='"+divCLass+"' style='"+divStyle+"'>" // style='margin-left:1%;margin-right:10%;'
			+ "<label class='"+labelClass+"'>هاتف الزبون 2</label>"
			+ "<div class=\"input-group\">"
					+ "<span class='input-group-text'><i class='lni lni-phone'></i></span>"
			+ "<input type=\"tel\" style=\"text-align:right;   direction: ltr;\" name='rcv_phone2_"+userDefinedMultiNewRowExtension+"' "
			+ "id='rcv_phone2_"+userDefinedMultiNewRowExtension+"' class=\"form-control border-start-0\""
			+ "value='' maxlength=\"11\" size=\"11\" pattern='[0-9\\u0660-\\u0669]{11}'>"
			+"</div></div>");
			
			
			// 2-Receipt Price
			sb.append(
			"<div class='"+divCLass+"' style='"+divStyle+"'>" // style='margin-left:1%;margin-right:1%;'
			+ "	<label class='"+labelClass+"'>مبلغ الوصل</label>"
				+ "<div class=\"input-group\">"
					+ "<span class='input-group-text'><i class='fadeIn animated bx bx-money'></i></span>"
			+ " <input type='text' value='"+receiptAmt+"'  class='form-control border-start-0' style='text-align:right; ' "
			+ " name='c_receiptamt_"+userDefinedMultiNewRowExtension+"' id ='c_receiptamt_"+userDefinedMultiNewRowExtension+"' required onkeyup='formatMe(this);'/>"
			+"</div></div>");
			// end of receipt amount
			
			// 3-state
			String style = "text-align:right;  padding: 0 10px 0 10px;"+
	    			"   border: 1px solid #7dc6dd;min-width:150px";
		
			sb.append(
			"<div class='"+divCLass+"' style='"+divStyle+"'>" // style='margin-left:2%;margin-right:2%;'
			+ "<label class='"+labelClass+"'>المحافظة</label>"
				+ "<div class=\"input-group\">"
					+ "<span class='input-group-text'><i class='lni lni-map'></i></span>"
			+ "<div class='col-md-8 col-sm-8 col-xs-8' ><select class='form-control single-select'    onchange='loadDistrict("+rcvSeq+");' id='rcv_city_"+userDefinedMultiNewRowExtension+"' "
			+ " name='rcv_city_"+userDefinedMultiNewRowExtension+"' style='"+style+"' required>");
			if (colMapValues==null)
				colMapValues= mysqlmgr.loadAllLookups(conn1,userDefinedLookups);
			Map <String , String> lookupsmap = colMapValues.get("rcv_state_1");
			if (lookupsmap !=null && !lookupsmap.isEmpty()){
				for (String code : lookupsmap.keySet()){
					if (destState.equalsIgnoreCase(code))
						sb.append("<option value='"+code+"' selected>"+lookupsmap.get(code)+"</option> \n");
					else
						sb.append("<option value='"+code+"' >"+lookupsmap.get(code)+"</option> \n");
				}
			}
			sb.append("</select></div></div></div> \n");
			// end of state
			
			// 4- district inside state
			LinkedHashMap<Integer,String> district = new LinkedHashMap<Integer,String> ();
			district  = ut.getDistrictOfState(conn1,"BGD");
			
			sb.append(
			"<div class='"+divCLass+"' style='"+divStyle+"'>" // style='margin-right:7%;margin-left:2%;'
			+ "<label class='"+labelClass+"'>المنطقه</label>"
			+ "<div class=\"input-group\">"
					+ "<span class='input-group-text'><i class='lni lni-map-marker'></i></span>"
			+ "<div class='col-md-9 col-sm-9 col-xs-9' ><select class='form-control border-start-0 single-select' onchange='districtChanged("+rcvSeq+");calcShipmentCost("+rcvSeq+");'  id='rcv_district_"+userDefinedMultiNewRowExtension+"' "
			+ " name='rcv_district_"+userDefinedMultiNewRowExtension+"' style='"+style+"' required >");
			sb.append("<option value='' selected></option> \n");
			for (int code : district.keySet()){
				if (rcvDistrict == code)
					sb.append("<option value='"+code+"' selected>"+district.get(code)+"</option> \n");
				else
					sb.append("<option value='"+code+"' >"+district.get(code)+"</option> \n");
			}
			sb.append("</select></div></div></div> \n");
			// end of district
			
			// 5- location details
			sb.append(
			"<div class='"+divCLass+"' style='"+divStyle+"'>"
			+ "<label class='"+labelClass+"'>تفاصيل العنوان</label>"
			+ "<div class=\"input-group\">"
					+ "<span class='input-group-text'><i class='lni lni-map-marker'></i></span>"
			+"<textarea class='form-control border-start-0' name='rcv_more_loc_"+userDefinedMultiNewRowExtension+"' id ='rcv_more_loc_"+userDefinedMultiNewRowExtension+"'>"+
			locDtls+"</textarea></div></div>");
			
			// 6- Goods
			HashMap<Integer,String > goodsMap = ut.getMasterCustomerGoodsList(conn1, masterCustId);
			sb.append(
			"<div class='"+divCLass+"' style='"+divStyle+"'>" // style='margin-right:13%'
			+ "<label class='"+labelClass+"'>تفاصيل البضاعة</label>"
			+ "<div class=\"input-group\">"
					+ "<span class='input-group-text'><i class='fadeIn animated bx bx-box'></i></span>"
			+ "<div class='col-md-8 col-sm-8 col-xs-8' ><select name='rcv_itemsdlts_"+userDefinedMultiNewRowExtension+"' id ='rcv_itemsdlts_"+userDefinedMultiNewRowExtension+"' class=\"multiple-select\" data-placeholder=\"Choose anything\" multiple=\"multiple\">");
			for (int code : goodsMap.keySet()){
				if (rcvDistrict == code)
					sb.append("<option value='"+code+"' selected>"+goodsMap.get(code)+"</option> \n");
				else
					sb.append("<option value='"+code+"' >"+goodsMap.get(code)+"</option> \n");
			}
			sb.append("</select></div></div></div> \n");


			//10- No of Pieces // 
			sb.append(
			"<div class='"+divCLass+"' style='"+divStyle+"'>"
			+ "<label class='"+labelClass+"'>عدد القطع</label>"
			+ "<div class=\"input-group\">"
					+ "<span class='input-group-text'><i class='fadeIn animated bx bx-cube-alt'></i></span>"
			+ "<input type='number' class='form-control border-start-0' min='1' value='"+qty+"' size='1' style='text-align:right; '"
			+ " oninput=\"this.value = Math.abs(this.value)\"  name='rcv_qty_"+userDefinedMultiNewRowExtension+"' "
					+ "id ='rcv_qty_"+userDefinedMultiNewRowExtension+"' required='required' />"
			+ "</div></div>");

			
			
			// 6- Notes // 
			sb.append(
			"<div class='"+divCLass+"' style='"+divStyle+"'>" // style='margin-right:13%'
			+ "<label class='"+labelClass+"'>ملاحظات</label>"
			+ "<div class=\"input-group\">"
					+ "<span class='input-group-text'><i class='lni lni-library'></i></span>"
			+" <textarea class='form-control border-start-0' name='rcv_rmk_"+userDefinedMultiNewRowExtension+"' id ='rcv_rmk_"+userDefinedMultiNewRowExtension+"'>"
			+rmk+"</textarea></div></div>");
			
			/*//7 - rural areas // سيف كال اخفيها لانو ما احتاجها
			if (rural.equalsIgnoreCase("Y")) 
				checked="checked";
			sb.append(
			"<div class='"+divCLass+"' style='"+divStyle+"'>" // "+stylediv+"
			+ "<label class='"+labelClass+"'>أطراف</label>"
			+ "<input type='checkbox' class='form-check-input' name='c_rural_"+userDefinedMultiNewRowExtension+"' "
			+ "id ='c_rural_"+userDefinedMultiNewRowExtension+"' "+checked+" value='Y' onclick=\"calcShipmentCost("+rcvSeq+");\" />"
			+"</div>");*/
		
			
			
		
			Connection conn2 = null;
			try {
				conn2 = mysql.getConn();
				if (rural.equalsIgnoreCase("Y"))
					ruralArea = true;
				shipmentCost = Double.toString(ut.calcShipmentChargesBasedOnDestCity(conn2,  destState, ruralArea, masterCustId, custId, userStoreCode));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}finally {
				try {conn2.close();}catch(Exception e) {}
			}
			
		
			sb.append(
			"<div class='"+divCLass+"' style='"+divStyle+"' >" // style='margin-left:2%;margin-right:2%;'
			+ "<label class='"+labelClass+"'>مبلغ الشحن</label>"
			+ "<div class=\"input-group\">"
			+ "<span class='input-group-text'><i class='fadeIn animated bx bx-money'></i></span>"
			+ "<input type='number' min='0' readonly  class='form-control border-start-0' value='"+shipmentCost+"' style='width:8em;'  size='10' "
					+ "name='c_shipment_cost_"+userDefinedMultiNewRowExtension+"' id ='c_shipment_cost_"+userDefinedMultiNewRowExtension+"' required  />"
			+ "</div></div>");
			
			sb.append("</div>");// end of dive row
			sb.append("</td>");
			sb.append("<td style='width:5%;vertical-align: bottom; padding: 0;'>"
					+ "<table style='border: 0px solid;' id='side_table_"+smarty_new_row_seq+"'><tr>"
					
					+ "<td>"
					+ "<button type='button' onclick='remove_row("+smarty_new_row_seq+")' "
							+ " class='btn btn-danger btn-sm'><li class='fa fa-trash'></li></button>"
					+ "</td>"
					+ "<td>"
					+ "<span class='badge bg-secondary rounded-pill text-white ' style='font-size: 17px;'>"+smarty_new_row_seq+"</span>"
					+ "</td>"
					+ "</tr>"
					+ "</table>"
					+ "</td>");
			sb.append( "</tr>");
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
		PreparedStatement pst = null, pstUpdateReceiptNo= null;
		ResultSet rs = null;
		int userId = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		int masterCustId = Integer.parseInt(replaceVarsinString("{mastercustidlogin}", arrayGlobals).trim());
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		String msg = "تم خلق الطلبيه بنجاح";
		Utilities ut = new Utilities();
		boolean ruralArea = false;
		
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
			caseMaster.setBranch(currentBranch);
			caseMaster.setMasterCustomerId(masterCustId);
			caseMaster.setCustId(Integer.parseInt(inputMap_ori.get("c_custid")[0]));
			
			
			/*pst = conn.prepareStatement("select cust_mastercustid, mcust_phone1 from kbcustomers "
					+ " join kb_mastercustomer on cust_mastercustid = mcust_id  where cust_id=?");
			pst.setInt(1, caseMaster.getCustId());
			rs = pst.executeQuery();
			if (rs.next()) {
				caseMaster.setMasterCustHp1(rs.getString("mcust_phone1"));
			}
			try {rs.close();}catch(Exception e) {ignore}
			try {pst.close();}catch(Exception e) {ignore}
			*/
			// now for cases
			String itemsArr [] = new String [] {};
			String items = "", itemsDesc = "";
			for (Integer j : availableCases) {
			
				CaseInformation ci = new CaseInformation();
				ci.setSmarty_new_row_seq(j);
				ci.setReceiverName(inputMap_ori.get("rcv_name_"+userDefinedMultiNewRowExtension+"_"+j)[0]); //rcv name
				ci.setReceiverHp1(inputMap_ori.get("rcv_phone1_"+userDefinedMultiNewRowExtension+"_"+j)[0]); // rcv phone 1
				ci.setReceiverHp2(inputMap_ori.get("rcv_phone2_"+userDefinedMultiNewRowExtension+"_"+j)[0]); // rcv phone 1
				ci.setState(inputMap_ori.get("rcv_city_"+userDefinedMultiNewRowExtension+"_"+j)[0]);// rcv city
				ci.setDistrict(Integer.parseInt(inputMap_ori.get("rcv_district_"+userDefinedMultiNewRowExtension+"_"+j)[0]));
				
				
				ci.setLocationDetails(inputMap_ori.get("rcv_more_loc_"+userDefinedMultiNewRowExtension+"_"+j)[0]);//location dtls
				ci.setRmk(inputMap_ori.get("rcv_rmk_"+userDefinedMultiNewRowExtension+"_"+j)[0]); // remarks
				ci.setProductInfo(inputMap_ori.get("rcv_itemsdlts_"+userDefinedMultiNewRowExtension+"_"+j)[0]); // remarks
				ci.setQty(Integer.parseInt(inputMap_ori.get("rcv_qty_"+userDefinedMultiNewRowExtension+"_"+j)[0])); //no of items
				ci.setReceiptAmt(Double.parseDouble(inputMap_ori.get("c_receiptamt_"+userDefinedMultiNewRowExtension+"_"+j)[0].replace(",","")));//c_goods_cost_
				itemsArr = inputMap_ori.get("rcv_itemsdlts_"+userDefinedMultiNewRowExtension+"_"+j);
				for (int i=0; i <itemsArr.length ; i++) {
					if (i>0) 
						itemsDesc += ", ";
					
					items +=itemsArr[i];
					items += ":";
					itemsDesc += ut.getProductName(conn, Integer.parseInt(itemsArr[i]));
					
				}
				ci.setProductCodes(items);
				ci.setProductInfo(itemsDesc);
				if (ut.isRuralDistrict(conn,ci.getDistrict())) {
					ci.setRural("Y");
					ruralArea = true;
				}else {
					ci.setRural("N");
					ruralArea = false;
				}	
				ci.setShipmentCharge(ut.calcShipmentChargesBasedOnDestCity(conn, ci.getState(),ruralArea,caseMaster.getMasterCustomerId(), caseMaster.getCustId(), currentBranch));
				cases.add(ci);
			}
			
			
			// get the pickup agent id
			int pickUpAgent = 0;
			pst =conn.prepareStatement("select mcust_pickupagent from kb_mastercustomer where mcust_id=?");
			pst.setInt(1, caseMaster.getMasterCustomerId());
			rs = pst.executeQuery();
			if (rs.next())
				pickUpAgent = rs.getInt("mcust_pickupagent");
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			pstUpdateReceiptNo = conn.prepareStatement("update p_cases set c_custreceiptnoori=? where c_id = ?");
			pst = conn.prepareStatement("insert into p_cases "
					+ " (c_createdby	, c_rcv_name	, c_rcv_hp1	  	, c_rcv_hp2		 	  , c_rcv_state, "
					+ "	 c_rural		, c_rcv_addr_rmk, c_rmk 		, c_qty	  		 	  , c_receiptamt, "
					+ "  c_shipment_cost, c_branchcode	, c_rcv_district, c_custid		 	  , c_mastercustid, "
					+ "  c_pickupagent  , c_productinfo , c_products 	, c_creationstartpoint, c_createddt	 )"
			+ " values  (?			    , ?			    , ?		 		, ?					  ,?, "
			+ "			 ?			    , ?			    , ?		 		, ?					  ,?, "
			+ "			 ?			    , ?             , ?		 		, ?					  ,?, "
			+ "			 ?			 	, ?		 		, ?		 		, 'SYS-CustomerCreateNewCases', "+(Utilities.sysDateTime)+"	 )",
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
					pst.setDouble(10, ci.getReceiptAmt());//this is special req, so the user can insert without adding the 000
					pst.setDouble(11, ci.getShipmentCharge());
					pst.setInt(12, caseMaster.getBranch());
					pst.setInt(13, ci.getDistrict());
					pst.setInt(14, caseMaster.getCustId());
					pst.setInt(15, caseMaster.getMasterCustomerId());
					pst.setInt(16, pickUpAgent);
					pst.setString(17, ci.getProductInfo());
					pst.setString(18, ci.getProductCodes());
					pst.executeUpdate();
	
					rs = pst.getGeneratedKeys();
					if (rs.next())
						ci.setCaseid(rs.getInt(1));
					else
						throw new Exception ("No case id generate");
					
					
					pstUpdateReceiptNo.setInt(1, ci.getCaseid());
					pstUpdateReceiptNo.setInt(2, ci.getCaseid());
					pstUpdateReceiptNo.executeUpdate();
					
					 
					fu.createNewCaseInQueue(conn, ci.getCaseid(), "NEWCUSTLOGI", "READYTOPRINT" , caseMaster.getBranch());
					
					
					try {rs.close();}catch(Exception e) {/*ignore*/}
					pstUpdateReceiptNo.clearParameters();
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
			try {pstUpdateReceiptNo.close();}catch(Exception e) {/*ignore*/}
			
			try {conn.close();}catch(Exception e) {/*ignore*/}
		}
		
		 return msg;
	}
	public int getUserStoreCode() {
		return userStoreCode;
	}
	public void setUserStoreCode(int userStoreCode) {
		this.userStoreCode = userStoreCode;
		
	}
	public String getShopsCommaSeperated() {
		return shopsCommaSeperated;
	}
	public void setShopsCommaSeperated(String shopsCommaSeperated) {
		this.shopsCommaSeperated = shopsCommaSeperated;
	}
	
}
