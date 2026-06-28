<%@page import="com.app.util.UtilitiesFeqar, java.text.DecimalFormat"%>
<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.PickUpAgentPayments" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet,
com.app.incomeoutcome.PickUpAgentBalance" %>
<% 
Connection conn1 = null;
PreparedStatement pst = null;
ResultSet rs = null; 
UtilitiesFeqar ut = new UtilitiesFeqar();

String pickupAgentAcct = (String)request.getParameter("pickupAgentAcct");
int denomanator = 0;
DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
if (pickupAgentAcct !=null){
	Myglobals.smartyGlobalsAssArr.put("pickupAgentAcct", (String)pickupAgentAcct);
	
}else if (Myglobals.smartyGlobalsAssArr.containsKey("pickupAgentAcct") && Myglobals.smartyGlobalsAssArr.get("pickupAgentAcct")!=null){
	pickupAgentAcct = (String)Myglobals.smartyGlobalsAssArr.get("pickupAgentAcct");
}
LinkedHashMap<String,String> pickUpAgentList = new LinkedHashMap<String,String>();
LinkedHashMap<String,String> pickUpAgentInfo = new LinkedHashMap<String,String>();
HashMap<String, Integer> shipInfo = new HashMap<String, Integer>();

try{
	conn1 = mysql.getConn();
	pickUpAgentList = ut.getListOfPickUpAgents(conn1, (int)Myglobals.smartyGlobalsAssArr.get("userstorecode"));
	if (pickupAgentAcct!=null){
		pickUpAgentInfo= ut.getPickUpAgentInfo(conn1, pickupAgentAcct); 
		
		shipInfo = ut.calcPickUpAgentShipmentsInfo(conn1, pickupAgentAcct);
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
	try{rs.close();}catch(Exception e){}
	try{pst.close();}catch(Exception e){}
	try{conn1.close();}catch(Exception e){}
}
PickUpAgentBalance pab = new PickUpAgentBalance(); 
%>
<div class="row">
	 <form action="?mypickupagentsearch=1" method="post" name="search_cust_form" class="form-horizontal form-label-left" >
		<div class='row col-12'>
			<div class="col-6 ">
	        	<select class='select2' id='pickupAgentAcct' style="width: 200px;" name ='pickupAgentAcct' >
	            	<option value='' ></option>
	                <%	for (String pickUpAgentId : pickUpAgentList.keySet()){
                    	if (pickupAgentAcct!=null && pickupAgentAcct.equalsIgnoreCase(pickUpAgentId)){  %>
	                   		<option value='<%=pickUpAgentId%>' selected><%=pickUpAgentList.get(pickUpAgentId)%></option>
	                   	<%
	                   	}else{
	                   	%>
	                   	<option value='<%=pickUpAgentId%>' ><%=pickUpAgentList.get(pickUpAgentId)%></option>
	                   	<%
	                   	}
	                  }%>
	              </select>
			</div>
	        <div class='col-6'>
	        	<button type='submit' class="btn btn-blue-grey px-5" style='margin-right:10px' type="button"> عرض التفاصيل الماليه لمندوب الأستلام <i class="fa fa-search m-right-xs"></i></button>
	        </div>
	   	</div>  
	</form>
</div>
<%
if (pickupAgentAcct!=null && !pickupAgentAcct.trim().equalsIgnoreCase("")){

      String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
     	
     	Render(pab  , out , request, response , Myglobals , objectState , pageName1);

     	if (pab.getRecords() == 0){
     		%>
        		<div class="alert border-0 border-start border-5 border-white alert-dismissible fade show py-2">
     			<div class="d-flex align-items-center">
     				<div class="font-35 text-dark"><i class="bx bx-info-square"></i>
     				</div>
     				<div class="ms-3">
     					<h6 class="mb-0 text-dark">لاتوجد وصولات للمحاسبة</h6>
     					<div class="font-35 text-dark">كل الشحنات  الواصلة تمت المحاسبة عليها</div>
     				</div>
     			</div>
     		</div>
     		<script>
     			$("#main_row_com_app_incomeoutcome_PickUpAgentBalance").css("display","none");
     		</script>
        		<%
     	}
     	
     	PickUpAgentPayments pap = new PickUpAgentPayments(); 
      	Render(pap  , out , request, response , Myglobals , objectState , pageName1);
      %>
	        
<%} %>

<%@ include file="../Main/footer.jsp"%>
<script>
$(document).ready(function() {
	$("#heading-elements-com_app_incomeoutcome_PickUpAgentBalance").css("display","none");
});


var sumOfSelectedCases = 0;
function checkAll(){
	var check = false;
	if ($("#checkboxall").prop("checked"))
		check = true;
	$('[id^=pmtcheck_]').each(function() {
		$(this).attr("checked", check);
	});
	$('[id^=pmtcheck_]').each(function() {
		$(this).attr("checked", check);
	});
	$('[id^=check-customer-]').each(function() {
		console.log(this.id);
		$(this).attr("checked", check);
	});
	calculateSumOfSelectedCases();
}

function calculateSumOfSelectedCases(){
	var totalNetAmt = 0, totNetAmtUsd=0;
	$('[id^=pmtcheck_]').each(function() {
		if ($(this).prop("checked") == true){
		    var caseid = this.id.split('_').pop();
		    totalNetAmt += parseInt($("#td_netamt_iqd_"+caseid).attr("data-netval"));
		    totNetAmtUsd += parseInt($("#td_netamt_usd_"+caseid).attr("data-netval"));
		}
	});
	
	showTotalSelectedAmtForCustomers(totalNetAmt.toLocaleString()+" دينار "+"</br> "+ totNetAmtUsd.toLocaleString()+" دولار ");
	
	$("#amount_topay").val(totalNetAmt);
	$("#amount_topay_usd").val(totNetAmtUsd);
	$("#totalamountshouldbepaid").html(totalNetAmt.toLocaleString());
	$("#totalamountshouldbepaid_usd").html(totNetAmtUsd.toLocaleString());
}

function checkBoxPmtClicked(){
	calculateSumOfSelectedCases();
}

$('#pickupagent-balance-settle-form').submit(function(e){
	var selectedCases ="";
	var receiptNo = "";
	var first = true;
	
	$('[id^=pmtcheck_]').each(function() {
		if ($(this).prop("checked") == true){
		    var number = this.id.split('_').pop();
		    if (!first){
		    	selectedCases +=",";
		    }
		    selectedCases +=number;
		    first = false;
		}
	});
	
	$("#selected_casesto_pay").val('');
	$("#selected_casesto_pay").val(selectedCases);
	if ($("#selected_casesto_pay").val().length>0){
		this.submit();
		
	}else{
		e.preventDefault();
		generalErrorPrettyMsg("لم يتم تحديد أي وصل");
	}
	
	//console.log("selected_casesto_pay---->"+$("#selected_casesto_pay").val());
});


function changeActiveTab( tab){
	 localStorage.setItem('activeTab', tab);
	
}
$(function (){
	var selectedTab = localStorage.getItem('activeTab');
	if(selectedTab){
       $('#'+selectedTab).addClass('active show');
       $('#a-'+selectedTab).addClass('active');
   }else{
   	 $('#pickupagent-balance').addClass('active show');
        $('#a-pickupagent-balance').addClass('active');
   }
})





function syncPickUpAgentPaymentsWithSource(pickupAgentPmtId){
	$("#loading").css("display", "flex");
	
	$.confirm({ 
	    title: 'مزامنة دفعة مندوب الأستلام مع نظام الزبون',
	    content: 'ستم مزامنة كل الشحنات في هذه الدفعة مع نظام العملاء الخاصين بمندوب الإستلام هذا. اختر نعم للتاكيد',
	    buttons: {
	        confirm:{
	        	text :'نعم',
	        	action : function () {
	        		confirmredSyncPickUpAgentPaymentsWithSource (pickupAgentPmtId);
	        	}
	        },
	        cancel:{
	        	text :'لا',
	        	action : function () {
	        		hideLoader();
	        		}
	        }
	    }
	});
}
function confirmredSyncPickUpAgentPaymentsWithSource(pickupAgentPmtId){
	
	var dataToSend = {fromcustomerpmt:'N',pmtId: pickupAgentPmtId, actionUserId:userId };
	$.post('../../UpdateSuccessInOriginSystemSRVL' , dataToSend, function(data, status){ 
		if (status=='success'){
			$.alert({
			    title: '',
			    content: 'تمت المزامنة',
			});
			hideLoader();
		}else{
			alert("Error, please contact Mohammed Nafie");
			hideLoader();
		}
 	});
	
}

$('#barcode_checker').focus();
var input = document.getElementById("barcode_checker");
var old_receipt = '';
var scannedCounter = 1;
var sumOfSelectedCases = 0;
var inputKeyCodeArr = [];
input.addEventListener("keyup", function(event) {
	  if (event.keyCode === 13) {
		  $(input).val(inputKeyCodeArr.join(""));
		  inputKeyCodeArr = [];
		  var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
		  if (barcodeScanned !== null && barcodeScanned !== undefined){
			  var reciept = document.getElementById(barcodeScanned);
			  $(reciept).parent().css({ 
		            "background-color": "rgba(194 , 214 , 245 , 0.7)", 
		            "border": "2px solid rgb(43 73 220)",
		   		});
			  var caseid = $(reciept).attr("caseid");
			  if (caseid){
				  $('html, body').stop().animate({
			            scrollTop: $("#"+barcodeScanned).offset().top - 80
			        }, 500);
				  $("#pmtcheck_"+caseid).prop('checked', true);
				  checkBoxPmtClicked(this, caseid);
				  $(old_receipt).parent().css({ 
			            "background-color": "rgba(249,210,179,0.37)", 
			            "border": "2px solid #dc2b2b",
			            
			   		});
				  old_receipt = reciept; 
				  scannedCounter++;
			  }else{
			  	generalErrorPrettyMsg(" هذا الوصل غير موجود "+barcodeScanned);
		  	  }
		  input.value = '';
		  $('#barcode_checker').focus();
	  }else{
		  if (event.keyCode >= 48 && event.keyCode <= 57){
			  inputKeyCodeArr.push(String.fromCharCode((96 <= event.keyCode && event.keyCode <= 105)? event.keyCode-48 : event.keyCode));
		  }else if (event.keyCode >= 65 && event.keyCode <= 90){
			  inputKeyCodeArr.push(String.fromCharCode((96 <= event.keyCode && event.keyCode <= 105)? event.keyCode-48 : event.keyCode));
		   }else if (event.keyCode >= 96 && event.keyCode <= 105){
			   inputKeyCodeArr.push(String.fromCharCode((96 <= event.keyCode && event.keyCode <= 105)? event.keyCode-48 : event.keyCode));
		   }
	  }  
	}
});



function changeShipmentCost(caseId){
 	Swal.fire({
		title: 'تعديل تكلفة الشحن (يرجى ادخال الاصفار)',
		input: 'number',
	    showCancelButton: true,
	    confirmButtonText: "موافق",
	    cancelButtonText: "الغاء",
	    buttonsStyling: true,
		  inputAttributes: {
		    id: 'newShipmentCost'
		  },
	}).then((result)=> {

		if (result.isConfirmed) {
			var newShipmentCost = $("#newShipmentCost").val();
			if(newShipmentCost && $("#newShipmentCost").val()>=0){
			    $.ajax({
			        type: "POST",
			        url: "../../ChangeSingleCaseSingleInformationSRVL",
			        data: { 'p_newValue': newShipmentCost, 'p_caseId':caseId , 'p_screenName':'حسابات مندوب الأستلام', 'p_columnToChange' :'shipment_cost'},
			        cache: false,
			        success: function(data) {
			        	console.log(data);
			        	Swal.fire(
			            "تمت العملية بنجاح!",
			            "تم الحفظ!",
			            "success"
			        	).then((result) => {
			        		
				        		$("#td-shipment-cost-caseid-"+caseId).html(newShipmentCost);
				        		$("#td-shipment-cost-caseid-"+caseId).digits();
				        		$("#td-shipment-cost-caseid-"+caseId).attr("data-val", newShipmentCost);
				        		$("#receipt-amt-iqd-"+caseId).attr("data-val");
				        		var newNetVal = $("#receipt-amt-iqd-"+caseId).attr("data-val") - newShipmentCost;
				        		//console.log("newNetVal--->"+newNetVal);
				        		$("#td_netamt_iqd_"+caseId).html(newNetVal);
				        		$("#td_netamt_iqd_"+caseId).digits();
				        		$("#td_netamt_iqd_"+caseId).attr("data-netval", newNetVal);
				        		calculateSumOfSelectedCases();
			        	});
			            
			        },
			        error: function () {
			        	Swal.fire(
			            "Internal Error",
			            "Oops, your update was not saved.",
			            "error"
			            )
			        }
			    });
			}else{
				Swal.fire({
				      title: 'تم الالغاء',
				   	  confirmButtonText: 'نعم'
				    });
			}
		}
	}, 
	{
	}) 
}
</script>
