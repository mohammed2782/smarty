<%@ include file="../Main/Main.jsp"%>
<%@ page import="java.sql.PreparedStatement, java.sql.ResultSet,com.app.bussframework.UpdateNewOnWayCasesBarcode,
 com.app.bussframework.SingleQueueFactory, com.app.bussframework.SingleQueue, java.sql.Connection" %>
<% 
	String stg_code = (String)request.getParameter("stg_code");
	String stp_code = (String)request.getParameter("stp_code"); 
	String stepName = "";
	String [] filterBy = null; String [] filterVal=null;
	String c_id = null;
	if (request.getParameter("c_id")!=null && !((String)request.getParameter("c_id")).trim().isEmpty())
		c_id = (String)request.getParameter("c_id");
	
	if (request.getParameter("filterby")!=null && !((String)request.getParameter("filterby")).trim().isEmpty())
		filterBy = request.getParameterValues("filterby");
	
	if (request.getParameter("filtervalue")!=null && !((String)request.getParameter("filtervalue")).trim().isEmpty())
		filterVal = request.getParameterValues("filtervalue");
	
	String queueName = "";
	if (stg_code !=null){
		Myglobals.smartyGlobalsAssArr.put("stg_code", (String)stg_code);
		Myglobals.smartyGlobalsAssArr.put("stp_code", (String)stp_code);
		if(c_id !=null)
			Myglobals.smartyGlobalsAssArr.put("c_id", (String)c_id);
		else
			Myglobals.smartyGlobalsAssArr.remove("c_id");
	}else if (Myglobals.smartyGlobalsAssArr.containsKey("stg_code") && Myglobals.smartyGlobalsAssArr.get("stg_code")!=null){
		stg_code = (String)Myglobals.smartyGlobalsAssArr.get("stg_code");
		stp_code = (String)Myglobals.smartyGlobalsAssArr.get("stp_code");
	}
	Connection conn1 = null;
 	PreparedStatement pst = null;
	ResultSet rs = null;
	try{
		conn1 = mysql.getConn();
		pst = conn1.prepareStatement("select stg_name , stp_name from kbstage join kbstep on (stg_code=stp_stgcode)"+
				 " where stg_code=? and stp_code =?");
		pst.setString(1, stg_code);
		pst.setString(2, stp_code);
		rs = pst.executeQuery();
		if (rs.next()){
			queueName = rs.getString("stg_name")+" - "+rs.getString("stp_name");
			stepName = rs.getString("stp_name");
		}
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		try{rs.close();}catch(Exception e){}
		try{pst.close();}catch(Exception e){}
		try{conn1.close();}catch(Exception e){}
	}

	if (stp_code.equalsIgnoreCase("with_agent")){
		%>
		<style>
			.jambo_table>tbody>tr>:nth-child(2){
				 font-weight :600;
				 font-size :12.5px;
				 border : 2px solid #000768;
				}
				
				.jambo_table>tbody>tr>:nth-child(4){
				 font-weight :600;
				 font-size :12.5px;
				 border : 2px solid #000768;
				}
		</style>
		<%
	}
%>
 <title><%=stepName%></title>
<div class="row">
	<div class="col-md-1">
		<a href='./alloperations' class="btn btn-info">رجوع</a>
	</div>
	<div class="col-md-10">
    	<div class="panel panel-warning" style="margin-bottom:0px;">
    		<div class="panel-heading">
            	<div class='row'>
	            	<div class='col-xs-12' style='text-align:center'>
	              		<h4><%=queueName%></h4>
	            	</div>
	            </div>
            </div>
        </div>
	</div>
</div>
<%
	SingleQueueFactory sqf = new SingleQueueFactory();
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SingleQueue sq = sqf.getSingleQueuObj(stg_code, stp_code); 
	String className = sq.getClass().getCanonicalName();
	
	if (filterBy!=null && filterVal !=null){
		HashMap <String , String[]>  searchParamVal = new HashMap <String , String[]>();
		for (int i=0; i<filterBy.length; i++) {
			searchParamVal.put(filterBy[i], new String[] {filterVal[i]});
		}
		if (objectState.smartyStateMap.get(className)!=null && objectState.smartyStateMap.containsKey(className)){
			((HashMap<String,HashMap>) objectState.smartyStateMap.get(className)).put("filter", searchParamVal);
		}else{
			HashMap<String,HashMap> news = new HashMap<String,HashMap>();
			news.put("filter", searchParamVal);
			objectState.smartyStateMap.put(className,news);
		}
	}
		if(stp_code.equalsIgnoreCase("NEW_ONWAY")){
		%>
<div class="col-sm-2" style="padding-right: 10px;margin-top:20px;">
	<div class="position-relative">
		<input type="text" id ="barcode_checker_NEW_ONWAY" class="form-control ps-5 radius-30" placeholder="Ø¨Ø­Ø« Ø¹Ù ÙØµÙ"> 
			<span class="position-absolute top-50 product-show translate-middle-y">
				<i class="bx bx-search">
				</i>
			</span>
	</div>
</div>
		
		<%
		String pageName2 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		UpdateNewOnWayCasesBarcode uicb = new UpdateNewOnWayCasesBarcode(); 
		Render(uicb , out , request, response , Myglobals , objectState , pageName2);
	}else{
		sq.setUserDefinedCaption(" ");
		Render(sq  , out , request, response , Myglobals , objectState , pageName1);
	}
%> 
<script>
smarty_updatePageTitle = false;
</script>
<jsp:include page="../Main/footer.jsp" />
<script>

<% if (stp_code.equalsIgnoreCase("NEWINSTORE") && stg_code.equalsIgnoreCase("INIT")){%>

$(document).ready(function() {
	$('[id^=q_action_smartyrow_]').each(function() {
		 var number = this.id.split('_').pop();
		 change_instorage_action(number);
	});
	
});
<% if (user.getBranchCode() == 5 || user.getBranchCode() == 13 || user.getBranchCode()== 71){%>
	function change_instorage_action( seq){
		if ($('#q_action_smartyrow_'+seq).val()=='ASSIGN_LIASIONAGT'){
			$('#c_assignedagent_smartyrow_'+seq).attr("disabled", true); 
			$('#c_assignedagent_smartyrow_'+seq).css("display", "none");
			//$("#c_assignedagent_smartyrow_"+seq).next("span").css("display","none");
			$('#q_assigned_to_smartyrow_'+seq).removeAttr("disabled");
			$('#q_assigned_to_smartyrow_'+seq).css("display", "block"); 
		}else{
			// if ther eis agent then select it
			if($("#c_assignedagent_smartyrow_"+seq+" option:first").val()!=null
					&& $("#c_assignedagent_smartyrow_"+seq+" option:first").val()){
				console.log($("#c_assignedagent_smartyrow_"+seq+" option:first").val());
				$('#c_assignedagent_smartyrow_'+seq).removeAttr("disabled");
				$('#c_assignedagent_smartyrow_'+seq).css("display", "block"); 
				$('#q_assigned_to_smartyrow_'+seq).attr("disabled", true);
				$('#q_assigned_to_smartyrow_'+seq).css("display", "none");
				$("#q_assigned_to_smartyrow_"+seq).next("span").css("display","none");
				$('#q_action_smartyrow_'+seq).val('ASSGN_AGENT');
				
			}else{//else select the other path
				$('#c_assignedagent_smartyrow_'+seq).attr("disabled", true); 
				$('#c_assignedagent_smartyrow_'+seq).css("display", "none");
				//$("#c_assignedagent_smartyrow_"+seq).next("span").css("display","none");
				$('#q_assigned_to_smartyrow_'+seq).removeAttr("disabled");
				$('#q_assigned_to_smartyrow_'+seq).css("display", "block"); 
				$('#q_action_smartyrow_'+seq).val('ASSIGN_LIASIONAGT');	
			}
		}
	}
	<%}else{%>
	function change_instorage_action( seq){
		if ($('#q_action_smartyrow_'+seq).val()=='ASSIGN_LIASIONAGT'){
			$('#c_assignedagent_smartyrow_'+seq).attr("disabled", true); 
			$('#c_assignedagent_smartyrow_'+seq).css("display", "none");
			//$("#c_assignedagent_smartyrow_"+seq).next("span").css("display","none");
			$('#q_assigned_to_smartyrow_'+seq).removeAttr("disabled");
			$('#q_assigned_to_smartyrow_'+seq).css("display", "block"); 
		}else{
			$('#c_assignedagent_smartyrow_'+seq).removeAttr("disabled");
			$('#c_assignedagent_smartyrow_'+seq).css("display", "block"); 
			$('#q_assigned_to_smartyrow_'+seq).attr("disabled", true);
			$('#q_assigned_to_smartyrow_'+seq).css("display", "none");
			$("#q_assigned_to_smartyrow_"+seq).next("span").css("display","none");
			
		}
	}
	<%}%>
<%}%>
var returnedAll = 'N';
var deliverAll = 'N';
var archiveAllRtn = 'N';

function changeToArchiveAll(action){
	$('[id^=q_action_smartyrow_]').each(function() {
	    var number = this.id.split('_').pop();
	    if (archiveAllRtn =='N'){
	    	$('#q_action_smartyrow_'+number).val('ARCHV');
	    	
	    }else{
	    	$('#q_action_smartyrow_'+number).val('');
	    	
	    }
	});
	if (archiveAllRtn =='N'){
		archiveAllRtn = 'Y';
	}else{
		archiveAllRtn = 'N';
	}
}
function changeToRecivedAll(action){
	$('[id^=q_action_smartyrow_]').each(function() {
	    var number = this.id.split('_').pop();
	    if (archiveAllRtn =='N'){
	    	$('#q_action_smartyrow_'+number).val('RECEIVEDFROMLIAISON');
	    	
	    }else{
	    	$('#q_action_smartyrow_'+number).val('');
	    	
	    }
	});
	if (archiveAllRtn =='N'){
		archiveAllRtn = 'Y';
	}else{
		archiveAllRtn = 'N';
	}
}

function changeToRecievedAll(action){
	
	$('[id^=q_action_smartyrow_]').each(function() {
	    var number = this.id.split('_').pop();
	    if (deliverAll =='N'){
	    	$('#q_action_smartyrow_'+number).val('SUCCDLV');
	    	 $("#q_action_smartyrow_"+number).css('backgroundColor','#2f2fd2');
	    	 $("#q_action_smartyrow_"+number).css('color','white');
	    }else{
	    	$('#q_action_smartyrow_'+number).val('');
	    	 $("#q_action_smartyrow_"+number).css('backgroundColor','');
	    	 $("#q_action_smartyrow_"+number).css('color','');
	    }
	});
	if (deliverAll =='N'){
		deliverAll = 'Y';
	}else{
		deliverAll = 'N';
	}
}

function changeActionReturnedAll(action){
	
	$('[id^=q_action_smartyrow_]').each(function() {
	    var number = this.id.split('_').pop();
	    if (returnedAll =='N'){
	    	$('#q_action_smartyrow_'+number).val('RETURNED_TO_SNDR');
	    }else{
	    	$('#q_action_smartyrow_'+number).val('');
	    }
	});
	if (returnedAll =='N'){
		returnedAll = 'Y';
	}else{
		returnedAll = 'N';
	}
}
$('#c_custreceiptnoori').focus();
function change_q_actionColor(that, seq){
	 var value = $("#q_action_smartyrow_"+seq).val();
	 $("#rtn_qty_smartyrow_"+seq ).prop( "required", false );
	 $("#rtn_qty_smartyrow_"+seq ).css('display','none');
	 $("#trreturnreasons_"+seq).css('display','none');//RTN REASON
	 $("#c_rtnreason_smartyrow_"+seq ).prop( "disabled", true );
	 $("#c_rtnreason_smartyrow_"+seq).prop('required',false);
	 
	 $("#c_assignedagent_change_"+seq).css('display','none');//agent
	 $("#c_assignedagent_smartyrow_"+seq).prop('required',false);
	 $("#c_assignedagent_smartyrow_"+seq).prop('required',false);
	 
	 $("#trnew_receiptamtrtn_smartyrow_"+seq).css('display','none'); // receipt amt
	 $( "#new_receiptamtrtn_smartyrow_"+seq ).prop( "disabled", true );
	 $( "#new_receiptamtrtn_smartyrow_"+seq ).prop( "required", false );
	 
	 $("#trq_postponedto_smartyrow_"+seq).css('display','none'); // postponed 
	 $( "#q_postponedto_smartyrow_"+seq ).prop( "disabled", true );
	 $("#q_postponedto_smartyrow_"+seq).prop('required',false);
	 $( "#q_postponedoption_smartyrow_"+seq ).prop( "disabled", true );
	 $("#q_postponedoption_smartyrow_"+seq).prop('required',false);
	 
	 
	 if (value == 'SUCCDLV' || value == 'SUCS_DLV_CHANGEAMT' || value == 'PART_SUCC'){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#037656');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 if (value == 'SUCS_DLV_CHANGEAMT' || value == 'PART_SUCC'){ // if successfully delviered and receipt amount or partial delivered have to change then show field
			 $("#trnew_receiptamtrtn_smartyrow_"+seq).css('display','block');
			 $("#new_receiptamtrtn_smartyrow_"+seq ).prop( "disabled", false );
			 $( "#new_receiptamtrtn_smartyrow_"+seq ).prop( "required", true );
			 $("#q_action_smartyrow_"+seq).css('backgroundColor','#1ea57f');
			 if (value == 'PART_SUCC'){
				 $("#rtn_qty_smartyrow_"+seq).css('display','block');
				 $("#rtn_qty_smartyrow_"+seq ).prop( "required", true );
			 }
			 
		 }
	 }else if (value == 'RTN_WITHSHP_CHARGE_SNDR' || value == 'RTN_TOSTORE' || value=='RTN_WTIHAGENT'){
		
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#a70328');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 $("#trreturnreasons_"+seq).css('display','block');//RTN REASON
		 $("#c_rtnreason_smartyrow_"+seq ).prop( "disabled", false );
		 $("#c_rtnreason_smartyrow_"+seq).prop('required',true);
		 $("#returnreasons_"+seq+" td select").css('backgroundColor','rgb(247 249 168)');
		 
	 }else if (value =='POSTPONED'){

		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#05b6b1');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 $("#trq_postponedto_smartyrow_"+seq).css('display','block'); // postponed 
		 $( "#q_postponedto_smartyrow_"+seq ).prop( "disabled", false );
		 $( "#q_postponedto_smartyrow_"+seq ).prop( "required", true );
		 $( "#q_postponedoption_smartyrow_"+seq ).prop( "disabled", false );
		 $("#q_postponedoption_smartyrow_"+seq).prop('required',true);
	 }else if (value == 'RTN_FROMAGENTTOSTORE'){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#a70328');
		 $("#q_action_smartyrow_"+seq).css('color','white');
	 }else if (value == 'RETRYDLV'){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#9D7302');
		 $("#q_action_smartyrow_"+seq).css('color','white');
	 }else if (value == 'CHANGE_AGENT'){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','rgb(85 9 64)');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 $("#c_assignedagent_change_"+seq).css('display','block');//RTN REASON
		 $("#c_assignedagent_smartyrow_"+seq ).prop( "disabled", false );
		 $("#c_assignedagent_smartyrow_"+seq).prop('required',true);
		 $("#c_assignedagent_change_"+seq+" td select").css('backgroundColor','rgb(85 9 64)');
	 }else {
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#f57b7b');
		 $("#q_action_smartyrow_"+seq).css('color','white');
	}
}
/* old one may be we will need it later
function change_q_actionColor(that, seq){
	 var value = $("#q_action_smartyrow_"+seq).val();
	 if (value == 'SUCCDLV' || value == 'SUCS_DLV_CHANGEAMT'){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#2f2fd2');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 if (value == 'SUCS_DLV_CHANGEAMT'){ // if successfully delviered and receipt amount have to change then show field
			 $("#new_receiptamt_smartyrow_"+seq).css('display','block');
			 $("#q_action_smartyrow_"+seq).css('backgroundColor',' rgb(170 173 16)');
		 }else{
			 $("#new_receiptamt_smartyrow_"+seq).css('display','none');
		 }
	 }else if (value ==''){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#F0FFF0');
		 $("#q_action_smartyrow_"+seq).css('color','black');
		 $("#new_receiptamt_smartyrow_"+seq).css('display','none');
	 }else {
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#f57b7b');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 $("#new_receiptamt_smartyrow_"+seq).css('display','none');
		}
	} */
<% if (stp_code.equalsIgnoreCase("RTN_INSTORE_WAITLIAISON")){%>
$('[id*="q_assigned_to_smartyrow_"]').each(function(){
	   //console.log($(this));
	   var i =0;
	   var that;
	   $(this).children().each(function(){
		   	if (i==0){
		   		that = $(this);
		   	}
		  	i++;
	   	}
	   
	   );
	   console.log(i);
	   if(i<3){
		  $(that).remove();
	   }
});
<%}%>


<% if (stp_code.equalsIgnoreCase("return_to_cust")){%>
function changeDropListRtn(that, seq){
	 var value = $("#q_action_smartyrow_"+seq).val();
	 if (value == 'RESEND'){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#2f2fd2');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 $("#agentlist_"+seq).css('display','block');
		 $("#agentlist_"+seq+" td select").css('backgroundColor','rgb(247 249 168)');

	 }else {
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#F0FFF0');
		 $("#q_action_smartyrow_"+seq).css('color','#424242');
		 $("#agentlist_"+seq).css('display','none');
	}
}
<%}%>

<% if (stp_code.equalsIgnoreCase("NEWINSTORE")){%>
	$('[id*="c_assignedagent_smartyrow_"]').each(function(){
		   console.log($(this));
		   var i =0;
		   var that;
		   $(this).children().each(function(){
			   	if (i==0){
			   		that = $(this);
			   	}
			  	i++;
		   	}
		   
		   );
		   console.log(i);
		   if(i<3){
			  $(that).remove();
		   }
	});
	
	$('[id*="q_assigned_to_smartyrow_"]').each(function(){
		   //console.log($(this));
		   var i =0;
		   var that;
		   $(this).children().each(function(){
			   	if (i==0){
			   		that = $(this);
			   	}
			  	i++;
		   	}
		   
		   );
		   console.log(i);
		   if(i<3){
			  $(that).remove();
		   }
	});
<%}%>



<% if (stp_code.equalsIgnoreCase("NEW_ONWAY")){%>
var rowNo = 1;
$('#c_custreceiptnoori').blur();
$('#barcode_checker_NEW_ONWAY').focus();
var input = document.getElementById("barcode_checker_NEW_ONWAY");
 
input.addEventListener("keyup",async  function(event) {
  if (event.keyCode === 13) {
	  getrcpNo();
	  $('#barcode_checker_NEW_ONWAY').focus();
  }
});



let custReceiptNoOris = [];
function getrcpNo(){

	var barcodeScanned_new_onway = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
	$.get('../../GetSingleReceiptInfoSRVL?stage=INIT&step=NEW_ONWAY&c_custreceiptnoori='+barcodeScanned_new_onway ,function(data, status){
		if (status=='success'){
			check = false;
			custReceiptNoOris.forEach(function(item, index, array) {
				if(item == barcodeScanned_new_onway)
					check = true;
	    	});
			if ( 'NEW_ONWAY' != data[0].stepcode){
				alert('ÙØ°Ø§ Ø§ÙÙØµÙ ÙÙØ³ ÙÙ ÙØ°Ù Ø§ÙÙØ±Ø­ÙØ©');
				
			}else if(check){
				alert('ÙÙØ¯ ØªÙ Ø§Ø¯Ø®Ø§Ù Ø§ÙÙØµÙ ÙØ³Ø¨ÙØ§');
			}else{
				
				custReceiptNoOris.push(barcodeScanned_new_onway);
				var dataCell10="<td>";
				dataCell10 +="<button type='button' style='float:left' onclick='remove_row(this,"+barcodeScanned_new_onway+")' class='btn btn-xs btn-danger'><i class=' fa fa-trash'></i></button>";
				dataCell10 +="</td>";
				var dataCell0 = "<td>"+rowNo+"</td>";
				var dataCell1 = "<td>"+data[0].custName+"</td>";
				var dataCell2 = "<td>"+data[0].receiptamt+"</td>";
				var dataCell3 = "<td>"+data[0].address+"</td>";
				var dataCell4 = "<td>"+data[0].caseid+"</td>";
				var dataCell5 = "<td>"+data[0].rcvname+"</td>";
				var dataCell6 = "<td>"+data[0].hp+"</td>";
				var dataCell7 = "<td>"+data[0].qty+"</td>";
				var dataCell8 = "<td>"+data[0].createddt+"</td>";
				var dataCell9 = "<td><input type='text' required readonly='readonly' name='barcodeScanned_new_onway_"+rowNo+"' value='"+barcodeScanned_new_onway+"' /></td>";
				var hiddenFields = "<input type='hidden' name='c_id_row_"+rowNo+"' value='"+data[0].caseid+"'/>";
				hiddenFields += "<input type='hidden' name='state_row_"+rowNo+"' value='"+data[0].state+"'/>";
				$('#numberofrowsscanned').val(rowNo);
				console.log($('#numberofrowsscanned').val());
				var row = '<tr>'+dataCell0+dataCell1+dataCell2+dataCell3+dataCell4+dataCell5+dataCell6+dataCell7+dataCell8+dataCell9+dataCell10+hiddenFields+'</tr>';
				$("#smarty_table_com_dot_app_dot_bussframework_dot_UpdateNewOnWayCasesBarcode tr:first").after(row);
				rowNo ++;
			}
		}else{
			alert('error');
		}
	});
	  input.value = '';
}
function remove_row(that, barcodeScanned_new_onway) {
	custReceiptNoOris.forEach(function(item, index, array) {
		if(item == barcodeScanned_new_onway)
			array.splice(index, 1);
	});
	$(that).parent().parent().remove();
	rowNo --;
	$('#numberofrowsscanned').val(rowNo);
	
}

$(document).ready(function() {
	  $(window).keydown(function(event){
	    if(event.keyCode == 13) {
	      event.preventDefault();
	      return false;
	    }
	  });
	});


<%}%>

<% if ( (!stp_code.equalsIgnoreCase("NEW_ONWAY")) && (!stp_code.equalsIgnoreCase("LIAISONAGT_NEWONWAY")) ){%>



function changeToRecivedAll(that,actionCode){
	$("input[data-check-seq]").each(function(){
		var seq = $(this).attr("data-check-seq");
		if ($(this).prop("checked")){
			if ($(that).prop("checked"))
				$("#q_action_smartyrow_"+seq).val(actionCode);
			else
				$("#q_action_smartyrow_"+seq).val('');
			
			$('#q_action_smartyrow_'+seq).select2().trigger('change');
		}
	});
}
	

$('#barcode_checker').focus();
var input = document.getElementById("barcode_checker");
var old_receipt = '';
var scannedCounter = 1;
input.addEventListener("keyup", function(event) {
  if (event.keyCode === 13) {
	  
	  var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
	 
	  if (barcodeScanned !== null && barcodeScanned !== undefined){
		  
		  var reciept = document.getElementById(barcodeScanned);
		  $(reciept).parent().css({ 
	            "background-color": "rgb(1 17 85 / 83%)", 
	            "border": "2px solid rgb(43 73 220)",
	   		});
		  var caseid = $(reciept).attr("caseid");
		  if (caseid){
			  $('html, body').stop().animate({
		            scrollTop: $("#"+barcodeScanned).offset().top - 100
		        }, 500);
			  
			  $("#pmtcheck_"+caseid).attr("checked", true);
			  $("#confirmCheckBox_"+caseid).attr("checked", true);
			  //confirmCheckBox_
			  $("#q_action_smartyrow_"+caseid).val("RTN_RCVDFROMLIAISON");
			 
			  
			  $(old_receipt).parent().css({ 
		            "background-color": "rgba(249,210,179,0.37)", 
		            "border": "2px solid #dc2b2b",
		            
		   		});
			  old_receipt = reciept; 
			  scannedCounter++;
		  }else{
			  generalErrorPrettyMsg("ÙÙ ÙØªÙ ØªØ­Ø¯ÙØ¯ Ø§Ù ÙØµÙ");
		  }
	  }	 
	  input.value = '';
	  $('#barcode_checker').focus();
  } 
});
 
<%}%>
<% if (stp_code.equalsIgnoreCase("LIAISONAGT_NEWONWAY") && stg_code.equalsIgnoreCase("BRANCHES")){%>
$(document).ready(function() {
	$("#checkAll").click(function(){
		var check = false;
		if ($("#checkAll").prop("checked") == true)
			check = true;
		$('[id^=confirmCheckBox_]').each(function() {
			
			$(this).attr("checked", check);
		});
	});
});
function changeActionAllGlobal(that,actionCode , switchOtherOff){
	
	$("#"+switchOtherOff).prop("checked", false);
	$("input[data-check-seq]").each(function(){
		var seq = $(this).attr("data-check-seq");
		if ($(this).prop("checked")){
			if ($(that).prop("checked"))
				$("#q_action_smartyrow_"+seq).val(actionCode);
			else
				$("#q_action_smartyrow_"+seq).val('');
		}
	});
}
	function checkAllRecived(that, rtnBranch){
		console.log(rtnBranch);
		var check = false;
		if ($(that).prop("checked"))
			check = true;
		$("input[data-single-check-rtnbranch-"+rtnBranch+"]").each(function() {
			$(this).attr("checked", check);
		});
		if(check){
			$("select[data-single-drop-rtnbranch-"+rtnBranch+"]").each(function() {
				$(this).val("RTN_RCVDFROMLIAISON");
			});
		}else{
			$("select[data-single-drop-rtnbranch-"+rtnBranch+"]").each(function() {
				$(this).val("");
			});
		}
	}
	
	function changeToRecivedAll(){
		var check = false;
		if ($("#allprepair").prop("checked"))
			check = true;
		$('[id^=pmtcheck_]').each(function() {
			$(this).attr("checked", check);
		});
		$('[id^=check-recived-]').each(function() {
			$(this).attr("checked", check);
		});
		if(check){
			$('[id^=q_action_smartyrow_]').each(function() {
				$(this).val("RTN_RCVDFROMLIAISON");
			});
		}else{
			$('[id^=q_action_smartyrow_]').each(function() {
				$(this).val("");
			});
		}
	
	}
	
	function checkBoxRecivedClicked(that,caseid){
		if ($('#pmtcheck_'+caseid).prop("checked")) {
			$("#q_action_smartyrow_"+caseid).val("RTN_RCVDFROMLIAISON");
		}else{
			$("#q_action_smartyrow_"+caseid).val("");
		}
	}

<%}%>
$(document).ready(function() {
	  $(window).keydown(function(event){
	    if(event.keyCode == 13) {
	      event.preventDefault();
	      return false;
	    }
	  });
	});


function doGlobalSelectForAgents(){
	
	var globalAgentid = $("#globalagentselect").val();
	$("input[data-check-seq]").each(function(){
		var seq = $(this).attr("data-check-seq");
		if ($(this).prop("checked")){
			$('#c_assignedagent_smartyrow_'+seq).val(globalAgentid);
		}
	});
}
function doGlobalSelectForDistrict(){
	var globalDistrictId = $("#globalDistrictSelect").val();
	$("input[data-check-seq]").each(function(){
		var seq = $(this).attr("data-check-seq");
		if ($(this).prop("checked")){
			$('#c_rcv_district_smartyrow_'+seq).val(globalDistrictId);
		}
	});
}
	/* $('[id^=c_assignedagent_smartyrow_]').each(function() {
		 var number = this.id.split('_').pop();
		 console.log("-->"+number);
		 */
		 //$('#c_assignedagent_smartyrow_'+number).trigger('change');
	//});
	


<%if (stp_code.equalsIgnoreCase("LIAISONAGT_NEWONWAY") && stg_code.equalsIgnoreCase("BRANCHES")){%>
	$('#barcode_checker').focus();
	var input = document.getElementById("barcode_checker");
	var old_receipt = '';
	var scannedCounter = 1;
	input.addEventListener("keyup", function(event) {
		
	  if (event.keyCode === 13) {
		  var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
		 
		  if (barcodeScanned !== null && barcodeScanned !== undefined){
			  
			  var reciept = document.getElementById(barcodeScanned);
			  $(reciept).parent().css({ 
		            "background-color": "rgb(4 61 80)", 
		            "border": "2px solid rgb(107 211 241)",
		            "color" : "white"
		   		});
			  var caseid = $(reciept).attr("caseid");
			 /*  $('html, body').stop().animate({
		            scrollTop: $("#"+barcodeScanned).offset().top - 100
		        }, 500);
			   */
			  $("#pmtcheck_"+caseid).attr("checked", true);
			  $("#confirmCheckBox_"+caseid).attr("checked", true);
			  var tr  = "<tr>"+$("[smartykeycolval="+caseid+"]").html()+"</tr>";
			  $("[smartykeycolval="+caseid+"]").remove();
			  console.log(tr);
			  $(tr).prependTo('#smarty_table_com_dot_app_dot_bussframework_dot_SingleQueue_BRANCHES_LIAISONAGT_NEWONWAY');
			 // $('#smarty_table_com_dot_app_dot_bussframework_dot_SingleQueue_BRANCHES_LIAISONAGT_NEWONWAY >tbody tr:first' ).before(tr);
			  var reciept = document.getElementById(barcodeScanned);
			  $(reciept).parent().css({ 
				  "background-color": "rgb(4 61 80)", 
		            "border": "2px solid rgb(107 211 241)",
		            "color" : "white"
		   		});
			  scannedCounter++;
		  }	 
		  input.value = '';
		  $('#barcode_checker').focus();
	  } 
	});	
<%}%>	

</script>