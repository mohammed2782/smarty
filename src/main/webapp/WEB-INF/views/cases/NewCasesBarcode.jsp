<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.NewCases" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet" %>
<%

 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	NewCases nc = new NewCases(); 
 	Render(nc  , out , request, response , Myglobals , objectState , pageName1); 
 	 
%> 
<script>
$("#newcustomerflag").focus();
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
			
			$('#rcv_dtls > tbody:last-child').append(data);
			$(".select2").select2({
			    // the following code is used to disable x-scrollbar when click in select input and
			    // take 100% width in responsive also
			    dropdownAutoWidth: true,
			    width: '100%'
			  });
				  
			init_InputMask();
			$("#c_custreceiptnoori_smartyNewRow_"+RCVno).focus();
		}
		
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
			$("[div_fornew_input_smarty=smarty_newcol_newmastercustomername]").css("display","block");
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
				
				$("[div_fornew_input_smarty=smarty_newcol_c_mastercustid]").css("display","block");
				$("[div_fornew_input_smarty=smarty_newcol_newcustomername]").css("display","block");
				$("[div_fornew_input_smarty=smarty_newcol_c_custid]").css("display","none");
				$("[div_fornew_input_smarty=smarty_newcol_mastercustnameshowonly]").css("display","none");
				$("#c_custid").prop('required',false);
				$("#newcustomername").prop('required',true);
				$("#c_mastercustid").prop('required',true);
			}else{
				$("[div_fornew_input_smarty=smarty_newcol_c_mastercustid]").css("display","none");
					$("[div_fornew_input_smarty=smarty_newcol_newcustomername]").css("display","none");
					$("[div_fornew_input_smarty=smarty_newcol_c_custid]").css("display","block");
					$("[div_fornew_input_smarty=smarty_newcol_mastercustnameshowonly]").css("display","block");
					$("#c_custid").prop('required',true);
					$("#newcustomername").prop('required',false);
					$("#c_mastercustid").prop('required',false);
				
			}
		
	});
});
</script>
<%@ include file="../Main/footer.jsp"%>
