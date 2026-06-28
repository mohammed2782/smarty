<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.NewCasesByState3" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet" %>
<%

 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	NewCasesByState3 ncbs = new NewCasesByState3(); 
 	Render(ncbs  , out , request, response , Myglobals , objectState , pageName1); 

%> 
<%@ include file="../Main/footer.jsp"%>
<script>
var RCVno = 1;
var rowNum = 1;
var RCVtable = document.getElementById("rcv_dtls");
$("#custid_smartyNewRow_"+RCVno).focus(); 
$("#newcustomer_smartyNewRow_"+RCVno).css('display','none'); 
$('#mastercustid_smartyNewRow_'+RCVno).parent().hide();
$("#mastercustnameshowonly_smartyNewRow_"+RCVno).prop("disabled", true);
$(document).ready(function() {
	$('#add_rcv_dtls').click(function(){
		RCVno++;
		rowNum++;
		dataToSend = {};
		var state = $("#rcv_city").val();
		if(state === ''){
			state= "BGD";
		}
		//var row  =RCVtable.insertRow(rowNum);
		$.get('../../MultiRowsByStateSRVLT3?state='+state+'&loadRcvRow='+RCVno , dataToSend,function(data, status){ 
			if (status=='success'){
				
				$('#rcv_dtls > tbody:last-child').append(data);
				$("#newcustomerflag_smartyNewRow_"+RCVno).focus();
				$(".select2").select2({
				    // the following code is used to disable x-scrollbar when click in select input and
				    // take 100% width in responsive also
				    dropdownAutoWidth: true,
				    width: '100%'
				  });
					  
				$("#newcustomer_smartyNewRow_"+RCVno).css('display','none'); 
				$('#mastercustid_smartyNewRow_'+RCVno).parent().hide();
				$("#mastercustnameshowonly_smartyNewRow_"+RCVno).prop("disabled", true);
	
				init_InputMask();
				loadDistrict(rowNum);
			}
			
		});
	});
});


function remove_row( rownum){
	$('table#rcv_dtls tr#smartyNewRow_'+rownum).remove();
}
function calcShipmentCost(seq){
	// not used now
}
function districtChanged(seq){
	// not used now
}

//when state changes 
$(document).ready(function() {
	$('#rcv_city').on('change',function (){
		for (i=1; i<=rowNum; i++)
			loadDistrict(i);
	});
	
	
});


function loadDistrict(seq){
	var destCity= $("#rcv_city").val();
	var keyCol = "rcv_district_smartyNewRow_"+seq;
	var req = "Y";
	var lookupSql = "select cdi_id, cdi_name from "
	+ "(select cdi_id, cdi_name from kbcity_district where  cdi_stcode = '"+destCity+"' and cdi_name='غير محدد' " 
	+ " union select cdi_id, cdi_name from kbcity_district where  cdi_stcode = '"+destCity+"'  and cdi_name !='بلا عنوان') ttt ";
	
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
function hideshowbasedoncheck(seq){	
	$("#newcustomerflag_smartyNewRow_"+seq).on("change", function(){
		console.log($(this).prop("checked"));
		
			if($(this).prop("checked") == true){
				
				$('#newcustomerflag_smartyNewRow_'+seq).val('Y');
				
				$('#custid_smartyNewRow_'+seq).parent().hide();
				$('#mastercustnameshowonly_smartyNewRow_'+seq).parent().hide();
				$("#newcustomer_smartyNewRow_"+seq).css('display','block');
				$('#mastercustid_smartyNewRow_'+seq).parent().show();
				
				$('#custid_smartyNewRow_'+seq).prop('required',false);
				$('#mastercustnameshowonly_smartyNewRow_'+seq).prop('required',false);
				$("#newcustomer_smartyNewRow_"+seq).prop('required',true);		
				$("#mastercustid_smartyNewRow_"+seq).prop('required',true);		

				$('#custid_smartyNewRow_'+seq).empty();
				$('#mastercustnameshowonly_smartyNewRow_'+seq).empty();
				$('#custhp_smartyNewRow_'+seq).val('');
				
				$("#newcustomer_smartyNewRow_"+RCVno).focus(); 
				
				loadMaster(seq);
			}else{
				
				$('#newcustomerflag_smartyNewRow_'+seq).val('N');

				$('#custid_smartyNewRow_'+seq).parent().show();
				$('#mastercustnameshowonly_smartyNewRow_'+seq).parent().show();
				$("#newcustomer_smartyNewRow_"+seq).css('display','none');
				$('#mastercustid_smartyNewRow_'+seq).parent().hide();
		
				$('#custid_smartyNewRow_'+seq).prop('required',true);
				$('#mastercustnameshowonly_smartyNewRow_'+seq).prop('required',true);
				$("#newcustomer_smartyNewRow_"+seq).prop('required',false);
				$("#mastercustid_smartyNewRow_"+seq).prop('required',false);		

				$("#newcustomer_smartyNewRow_"+seq).val('');
				$('#custhp_smartyNewRow_'+seq).val('');
				$('#mastercustid_smartyNewRow_'+seq).empty();

				loadCustomer(seq);
			  }				
	});		
}

//when customer changes
function getCustomerHP(seq){
	var custId= $("#custid_smartyNewRow_"+seq).val();
	
	var dataToSend = {"custId":custId};
	
	$.post('../../GetCustomerHPSRVLT' , dataToSend, function(data, status){ 
			//alert("Data: " + data +", Status:" + status);
			if (status=='success'){ 
				$("#custhp_smartyNewRow_"+seq).val(data);
			}else{
				alert("Error, please contact us!");
			}
	 });
}

function loadCustomer(seq){ 
	var branchCode = <%=user.getBranchCode()%>
	var keyCol = "custid_smartyNewRow_"+seq;
	var req = "N";
	var lookupSql = "select cust_id , cust_name from kbcustomers where cust_branch = '" + branchCode + " ' ";
			
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("custid_smartyNewRow_"+seq);
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

function loadMasterCustomer(seq){ 
	var custId= $("#custid_smartyNewRow_"+seq).val();
	var keyCol = "mastercustnameshowonly_smartyNewRow_"+seq;
	var req = "Y";
	var lookupSql = "select mcust_name, mcust_name from kb_mastercustomer "
		 		  + "where mcust_id in (select cust_mastercustid from kbcustomers where  cust_id = '"+custId+"') "
		 		  + " and mcust_branchcode='"+<%=user.getBranchCode()%>+"' ";
			
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("mastercustnameshowonly_smartyNewRow_"+seq);
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
					var masterCustId =  $("#mastercustnameshowonly_smartyNewRow_"+seq).val();
					if (masterCustId == 'بريد الكوت'){
						console.log($("#mastercustid_smartyNewRow_"+seq).parent());
						$("#mastercustid_smartyNewRow_"+seq).parent().parent().css("border"," 4px solid #0c9d79");	
					}else{
						$("#mastercustid_smartyNewRow_"+seq).parent().parent().css("border", " 0px solid #0c9d79");
					}
				}
		}
	});
}

function loadMaster(seq){ 
	var branchCode = <%=user.getBranchCode()%>
	var keyCol = "mastercustid_smartyNewRow_"+seq;
	var req = "N";
	var lookupSql = "select mcust_id , mcust_name from kb_mastercustomer where mcust_branchcode = '" + branchCode + "' and mcust_active='Y' ";
			
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("mastercustid_smartyNewRow_"+seq);
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

function disableF5(e) {
	  if ((e.which || e.keyCode) == 116){
		  e.preventDefault();
		  document.getElementById('save_new_form_com_dot_app_dot_cases_dot_NewCasesByState3').click();
	  }
	  
	};

	function disableF1(e) {
		  if ((e.which || e.keyCode) == 112 ){
			  e.preventDefault();
			  document.getElementById('add_rcv_dtls').click();
		  }
	};

	$(document).ready(function(){
		$(document).on("keydown", disableF5);
		$(document).on("keydown", disableF1);
	});


</script>


<%-- <script charset="utf-8">
var RCVno = 1;
var rowNum = 1;
var RCVtable = document.getElementById("rcv_dtls");
$('#add_rcv_dtls').click(function(){
	RCVno++;
	rowNum++;
	dataToSend = {};
	//var row  =RCVtable.insertRow(rowNum);
	$.get('../../MultiRowsSRVLT?loadRcvRow='+RCVno , dataToSend,function(data, status){ 
		if (status=='success'){
			$('#rcv_dtls tr:last').after(data);
			 $(".select2_single").select2({
				    placeholder: "Type to Search",
				    allowClear: true
				  });
				  
			init_InputMask();
		}
		
	});
	
	
	//$('#smarty_new_row_seq').val(RCVno);
    	
});
$("#com_dot_app_dot_cases_dot_NewCases").submit(function(){
	
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
	if (destCity !== null && destCity=='BGD'){
		$("#rcv_district_smartyNewRow_"+seq).attr("required", "");
		$("#c_assignedagent_smartyNewRow_"+seq).attr("required", "");
		$("#rcv_district_smartyNewRow_"+seq).removeAttr("disabled");
		$("#c_assignedagent_smartyNewRow_"+seq).removeAttr("disabled");
	}else{
		$("#rcv_district_smartyNewRow_"+seq).removeAttr("required");
		$("#rcv_district_smartyNewRow_"+seq).attr("disabled" ,"true");
		$("#c_assignedagent_smartyNewRow_"+seq).removeAttr("required");
		$("#c_assignedagent_smartyNewRow_"+seq).attr("disabled" ,"true");
	}
}
function calcShipmentCost(seq){
	
	var custName = $('#editable_c_cust_name').val();
	var branchCode = <%=user.getBranchCode()%>
	var destCity= $("#rcv_city_smartyNewRow_"+seq).val();
	var originState = $("#c_pickup_state").val();;
	//var weight = $("#c_weight_smartyNewRow_"+seq).val();
	var rural = "N";
	if ($('#c_rural_smartyNewRow_'+seq).is(":checked")){
		rural = "Y";
	}
	var dataToSend = {"destState":destCity, "originState" :originState, "rural":rural, "custName":custName, "branchCode" : branchCode};
	
		$.post('../../CalculateShipmentChargesSRVLT' , dataToSend, function(data, status){ 
				//alert("Data: " + data +", Status:" + status);
				if (status=='success'){ 
					$("#c_shipment_cost_smartyNewRow_"+seq).val(data);
				}else{
					alert("Error, please contact Softecha");
				}
		 });
	
}

function districtChanged(seq){
	var destDistrict= $("#rcv_district_smartyNewRow_"+seq).val();
	var keyCol = "c_assignedagent_smartyNewRow_"+seq;
	var req = "Y";
	var lookupSql = "SELECT us_id, us_name FROM kbagent_district join kbusers on us_id = agdi_usid where agdi_districtcode = '"+destDistrict+"'";
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("c_assignedagent_smartyNewRow_"+seq);
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

$("#com_dot_app_dot_cases_dot_NewCases").submit(function(event){
    var isValid = true;
	var errorMsg = '';
    // do all your validation if need here
    var phoneNo = '';
    var otherPhoneNo ='';
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
    			errorMsg = 'أرقام هواتف متشابهه';
    			$("#rcv_phone_smartyNewRow_"+j).css( 'background-color','#f5bed8');
    			$("#rcv_phone_smartyNewRow_"+i).css( 'background-color','#f5bed8');
    			$("#save_new_form_com.app.cases.NewCases").removeAttr( 'disabled' );
    			
    			 $("@com_dot_app_dot_cases_dot_NewCases").onsubmit(function() {
    					
    					return true;
    				});
    			break outerloop;
    		}else{
    			
    			$("#rcv_phone_smartyNewRow_"+i).css( 'background-color','#FFFFB8');
    			$("#rcv_phone_smartyNewRow_"+j).css( 'background-color','#FFFFB8');
    		}
    	}
    }
    //rcv_phone_smartyNewRow_1
	//alert(RCVno);
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
</script> --%>
