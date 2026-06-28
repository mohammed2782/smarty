<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cust.cases.CustomerCreateNewCases" %> 

<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	CustomerCreateNewCases ccnc = new CustomerCreateNewCases(); 
 	Render(ccnc  , out , request, response , Myglobals , objectState , pageName1); 
 	 
%> 
<script>
var RCVno = 1;
var rowNum = 1;
var RCVtable = document.getElementById("rcv_dtls");
$('#add_rcv_dtls').click(function(){
	RCVno++;
	rowNum++;
	dataToSend = {};
	var shop = $("#c_custid").val();
	if (!shop.length)
		alert("يجب أختيار المتجر أولا");
	$.get('../../CustCreateCasesMultiRowsSRVLT?shopmultirows='+shop+'&loadRcvRow='+RCVno , dataToSend,function(data, status){ 
		if (status=='success'){
			
			$('#rcv_dtls > tbody:last-child').append(data);
			 $('.single-select').select2({
				theme: 'bootstrap4',
				width: $(this).data('width') ? $(this).data('width') : $(this).hasClass('w-100') ? '100%' : 'style',
				placeholder: $(this).data('placeholder'),
				allowClear: Boolean($(this).data('allow-clear')),
			});
			 $('.multiple-select').select2({
					theme: 'bootstrap4',
					width: $(this).data('width') ? $(this).data('width') : $(this).hasClass('w-100') ? '100%' : 'style',
					placeholder: $(this).data('placeholder'),
					allowClear: Boolean($(this).data('allow-clear')),
				});	  
			init_InputMask(); 
		}
		
	});
});


function remove_row( rownum){
	$('table#rcv_dtls tr#smartyNewRow_'+rownum).remove();
}
function calcShipmentCost(seq){
	var mastercustid = <%=user.getMasterCustId()%>;
	var custid = $('#c_custid').val();
	var branchCode = <%=user.getBranchCode()%>;
	var destState= $("#rcv_city_smartyNewRow_"+seq).val();
	var rcvdistrict = $("#rcv_district_smartyNewRow_"+seq).val();;
	var dataToSend = {"destState":destState, "rcvdistrict" :rcvdistrict, "custid":custid, "branchCode" : branchCode, "mastercustid":mastercustid};
	if(custid != undefined && destState != undefined && rcvdistrict != undefined && custid != '' && destState != '' && rcvdistrict != '')
		$.post('../../CalculateShipmentChargesSRVLT' , dataToSend, function(data, status){ 
				//alert("Data: " + data +", Status:" + status);
				if (status=='success'){ 
					$("#c_shipment_cost_smartyNewRow_"+seq).val(data);
				}else{
					alert("Error, please contact Alnafi3 soft");
				}
		 });
}
function districtChanged(seq){
	// not used now
}
// when state changes 
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





$(document).ready(function() {
	$("#newmastercustomerflag").on("change", function(){
		console.log($(this).prop("checked"));
		if($(this).prop("checked") == true){
			//$("[div_fornew_input_smarty=smarty_newcol_newmastercustomername]").css("display","block");
			$("[div_fornew_input_smarty=smarty_newcol_c_mastercustid]").css("display","none");
		
			if ($('#newcustomerflag').prop("checked") == true)
				;
			else
				$('#newcustomerflag').trigger('click');
		}else{
			$("[div_fornew_input_smarty=smarty_newcol_newmastercustomername]").css("display","none");
			$("[div_fornew_input_smarty=smarty_newcol_c_mastercustid]").css("display","block");
			
			
			if ($('#newcustomerflag').prop("checked") == true)
				$('#newcustomerflag').trigger('click');
		
				
		}
	});
	
	$("#newcustomerflag").on("change", function(){
		console.log($(this).prop("checked"));
		
			if($(this).prop("checked") == true){
				
				//$("[div_fornew_input_smarty=smarty_newcol_c_mastercustid]").css("display","block");
				$("[div_fornew_input_smarty=smarty_newcol_newcustomername]").css("display","block");
				$("[div_fornew_input_smarty=smarty_newcol_c_custid]").css("display","none");
				//$("[div_fornew_input_smarty=smarty_newcol_mastercustnameshowonly]").css("display","none");
				$("#c_custid").prop('required',false);
				$("#newcustomername").prop('required',true);
				//$("#c_mastercustid").prop('required',true);
			}else{
				//$("[div_fornew_input_smarty=smarty_newcol_c_mastercustid]").css("display","none");
				$("[div_fornew_input_smarty=smarty_newcol_newcustomername]").css("display","none");
				$("[div_fornew_input_smarty=smarty_newcol_c_custid]").css("display","block");
				//$("[div_fornew_input_smarty=smarty_newcol_mastercustnameshowonly]").css("display","block");
				$("#c_custid").prop('required',true);
				$("#newcustomername").prop('required',false);
				//$("#c_mastercustid").prop('required',false);
			}
		
	});
});

</script>
<%@ include file="../Main/footer.jsp"%>
