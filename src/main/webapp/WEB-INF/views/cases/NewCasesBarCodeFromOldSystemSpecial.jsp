 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.NewCasesOldSystemSpecial" %> 
<div class="row" style="margin-right:0px;">
<div id="map" class="col-md-10" style="width:1100px;height:50px; margin-right:10px;"></div>

<div class="col-md-12" id="123">
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	NewCasesOldSystemSpecial  newCasesOldSystemSpecial= new NewCasesOldSystemSpecial(); 
 	Render(newCasesOldSystemSpecial  , out , request, response , Myglobals , objectState , pageName1); 
 	
%> 
</div>
</div>
 

<script charset="utf-8">
smarty_submitButton_allow_disable = false;


var input = document.getElementById("barcode");
$(input).focus();
input.addEventListener("keydown",  function(event) {
	console.log(event.keyCode);
	 if(event.keyCode == 13) {
		 var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
		 console.log(barcodeScanned);
		// 
	     event.preventDefault();
	     addrow(barcodeScanned);
	     $(input).val('');
	     $(input).focus();
	     return false;
	  }
});

var RCVno = 0;
var rowNum = 0;
var RCVtable = document.getElementById("rcv_dtls");

function getCustomerOfReceipt(value, rowNo){
	
	if ($('#custid_smartyNewRow_'+rowNo).find("option[value='"+value+"']").length) {
	    $('#custid_smartyNewRow_'+rowNo).val(value).trigger('change');
	  
	}
}

 function addrow(recieptNo){
	 var error = false;
	for (i = 1 ; i<=RCVno ; i++){
		if ($("#c_custreceiptnoori_smartyNewRow_"+i).val() == recieptNo){
			alert("هذا الوصل تم ادخاله سابقا");
			$("#c_custreceiptnoori_smartyNewRow_"+i).css( 'background-color','#f5bed8');
			error = true;
		}
	} 
	if (!error){
		RCVno++;
		rowNum++;
		dataToSend = {};
		$.get('../../MultiRowOldReceiptsSpecialSRVL?loadRcvRow='+RCVno+"&barCodeRcpNo="+recieptNo , dataToSend)
		.done(
			function(data, status){
				if (status=='success'){
					$('#rcv_dtls').append(data);
					$(".select2").select2({
					dropdownAutoWidth: true,
					width: '100%'
					});
					init_InputMask();
					$("#c_receiptamt_smartyNewRow_1").focus();
					RCVno++;
					rowNum++;
				}		
			 })
		.fail(function(xhr, status, error) {alert(xhr.responseText);});
	}
}
 
$("#com_dot_app_dot_cases_dot_NewSpecialCasesBarCode").submit(function(){
	
	var selectVal= $("#span_es_c_cust_name .es-list li.selected").attr("value");
	if (selectVal === undefined)
		selectVal = $("#span_es_c_cust_name #editable_c_cust_name").val();
	
	$("#c_cust_name").val(selectVal);
});

function remove_row( rownum){
	$('table#rcv_dtls tr#smartyNewRow_'+rownum).remove();
}



function loadDistrict(seq){
	var destCity= $("#rcv_city_smartyNewRow_"+seq).val();
	var keyCol = "rcv_district_smartyNewRow_"+seq;
	var req = "Y";
	var lookupSql = "select cdi_id, cdi_name from "
	+ "(select '' as cdi_id , '' as cdi_name from dual  union select cdi_id, cdi_name from kbcity_district where  cdi_stcode = '"+destCity+"')ttt ";
	
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("rcv_district_smartyNewRow_"+seq);
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'POST',
		cache: false,
		url:'../../myajax.jsp',  
		data: dataToSend,
		error:function(){ alert("some error occurred") },
		success: function(data, status){
				if (status=='success'){ 
					targetHTMLElement.innerHTML=data;
				}
		}
	});
}

$("#com_dot_app_dot_cases_dot_NewSpecialCasesBarCode").submit(function(event){
    var isValid = true;
	var errorMsg = '';
    // do all your validation if need here
    var phoneNo = '';
    var otherPhoneNo ='';
    var receiptNo ='';
    var otherReceiptNo ='';
    outerloop: for (i = 1; i<=RCVno; i++){
    	phoneNo = $("#rcv_phone_smartyNewRow_"+i).val();
    	//console.log('phoneNo-------'+phoneNo+', where i is '+i);
    	if (phoneNo === undefined)
    		continue;
    	for (j=i+1 ; j<=RCVno ; j++){
    		otherPhoneNo = $("#rcv_phone_smartyNewRow_"+j).val();
    		//console.log('otherPhoneNo-------'+otherPhoneNo+', where j is '+j);
    		if (otherPhoneNo === undefined)
    			continue;
    			
    		if (phoneNo === otherPhoneNo){
    			//console.log('otherPhoneNo-------'+otherPhoneNo+', is equal to '+phoneNo);
    			isValid = false;
    			errorMsg = 'هنالك تشابه بأرقام الهواتف';
    			$("#rcv_phone_smartyNewRow_"+j).css( 'background-color','#f5bed8');
    			$("#rcv_phone_smartyNewRow_"+i).css( 'background-color','#f5bed8');
    			break outerloop;
    		}else{
    			
    			$("#rcv_phone_smartyNewRow_"+i).css( 'background-color','#FFFFB8');
    			$("#rcv_phone_smartyNewRow_"+j).css( 'background-color','#FFFFB8');
    		}
    	}
    }
    
    if (!isValid) {
        event.preventDefault();
        alert(errorMsg);
    }
    isValid = true;
    errorMsg= '';
    outerloop: for (i = 1; i<=RCVno; i++){
    	receiptNo = $("#c_custreceiptnoori_smartyNewRow_"+i).val();
    	//console.log('phoneNo-------'+phoneNo+', where i is '+i);
    	
    	for (j=i+1 ; j<=RCVno ; j++){
    		otherReceiptNo = $("#c_custreceiptnoori_smartyNewRow_"+j).val();
    		//console.log('otherPhoneNo-------'+otherPhoneNo+', where j is '+j);
    		
    		if (otherReceiptNo === receiptNo){
    			//console.log('otherPhoneNo-------'+otherPhoneNo+', is equal to '+phoneNo);
    			isValid = false;
    			errorMsg = 'هنالك تشابه بأرقام الإيصالات';
    			$("#c_custreceiptnoori_smartyNewRow_"+j).css( 'background-color','#f5bed8');
    			$("#c_custreceiptnoori_smartyNewRow_"+i).css( 'background-color','#f5bed8');
    			break outerloop;
    		}
    	}
    }
    if (!isValid) {
        event.preventDefault();
        alert(errorMsg);
    }
});

function formatMe(xrcp){
	var z = xrcp.value.replace(/,/g,'');
	 var parts = z.toString().split(".");
	    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	    xrcp.value = parts.join(".");
    if (xrcp.value=='NaN')
    	xrcp.value = 0;
}

$('#editable_c_cust_name').focus();
$('#editable_c_cust_name').prop("autocomplete","off");

$('#editable_c_cust_name').on('change',function (){
	for (i=1; i<=rowNum; i++)
		calcShipmentCost (i);
});


function disableF1(e) {
	  if ((e.which || e.keyCode) == 112){
		  e.preventDefault();
		  document.getElementById('save_new_form_com_dot_app_dot_cases_dot_NewCasesOldSystemSpecial').click();
	  }
};
$(document).ready(function(){
	$(document).on("keydown", disableF1);
});
</script>

<%@ include file="../Main/footer.jsp"%>
