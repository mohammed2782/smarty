<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.NewSpecialCases" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	NewSpecialCases npc = new NewSpecialCases(); 
 	Render(npc  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%>

<script>
var RCVno = 1;
var rowNum = 1;
var RCVtable = document.getElementById("rcv_dtls");
$('#add_rcv_dtls').click(function(){
	RCVno++;
	rowNum++;
	dataToSend = {};
	//var row  =RCVtable.insertRow(rowNum);
	$.get('../../MultiRowsSpecialCasesSRVL?loadRcvRow='+RCVno , dataToSend,function(data, status){ 
		if (status=='success'){
			
			$('#rcv_dtls > tbody:last-child').append(data);
			$(".select2").select2({
			    // the following code is used to disable x-scrollbar when click in select input and
			    // take 100% width in responsive also
			    dropdownAutoWidth: true,
			    width: '100%'
			  });
				  
			init_InputMask(); 
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
<%-- 	var destCity= $("#rcv_city_smartyNewRow_"+seq).val();
	var district= $("#rcv_district_smartyNewRow_"+seq).val();
	alert();
	var branchCode = <%=user.getBranchCode()%>;
	$.get('../../CalculateAgentShareWithNoAgentSRVL?state='+destCity+"&district="+district+"&barnchcode="+branchCode ,function(data, status){ 
		if (status=='success'){
			$("#c_agentshare_smartyNewRow_"+seq).val(data);
		}
		
	}); --%>
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

function disableF5(e) {
	  if ((e.which || e.keyCode) == 116){
		  e.preventDefault();
		  document.getElementById('save_new_form_com_dot_app_dot_cases_dot_NewSpecialCases').click();
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


