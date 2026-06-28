<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.Updatecase" %> 


<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
    Updatecase uc = new Updatecase(); 
 	Render(uc  , out , request, response , Myglobals , objectState , pageName1); 

%>
<%@ include file="../Main/footer.jsp"%>
<script>
 
$("#c_rcv_state").on("change", calcShipmentCost, enableDesableAgent);
$("#c_rcv_district").on("change",calcShipmentCost);
$("#c_custid").on("change",calcShipmentCost);
$("#c_mastercustid").on("change",calcShipmentCost);

function calcShipmentCost(){
	var custid = $("#c_custid").val();
	var destCity= $("#c_rcv_state").val();
	var rcvDistrict = $("#c_rcv_district").val();
	var specialCase = $("#c_specialcase").val();
	var masterCustomer = $("#c_mastercustid").val();
	if(destCity === undefined)
		var destCity= $("#smarty_showonly_c_rcv_state").val();
	//alert("hi==destCity>"+destCity);
	var dataToSend = {"destState":destCity, "custid":custid, "mastercustid":masterCustomer, "rcvdistrict":rcvDistrict};
	if (specialCase !==undefined && specialCase!= null && specialCase !="Y"  ){
		$.post('../../CalculateShipmentChargesSRVLT' , dataToSend, function(data, status){ 
				//alert("Data: " + data +", Status:" + status);
				if (status=='success'){ 
					$("#c_shipment_cost").val(data);
					if($("#c_assignedagent").val() != undefined);
						calcAgentshare();
				}else{
					alert("Error, please contact MR.NAFIE");
				}
		 });
	}
}

function calcAgentshare(){
	var custid = $("#c_custid").val();
	var destCity= $("#c_rcv_state").val();
	var rcvDistrict = $("#c_rcv_district").val();
	var specialCase = $("#c_specialcase").val();
	var masterCustomer = $("#c_mastercustid").val();
	var agentid = $("#c_assignedagent").val();
	//alert("hi==specialCase>"+specialCase);
	//console.log(agentid);
	var dataToSend = {"destState":destCity, "custid":custid, "mastercustid":masterCustomer, "rcvdistrict":rcvDistrict, "agentid":agentid };
	if (specialCase !==undefined && specialCase!= null && specialCase !="Y" && agentid !==undefined && agentid>0 ){
		$.post('../../CalculateAgentShareWithNoAgentSRVL' , dataToSend, function(data, status){ 
				//alert("Data: " + data +", Status:" + status);
				if (status=='success'){ 
					$("#c_agentshare").val(data);
				}else{
					alert("Error, please contact MR.NAFIE");
				}
		 });
	}
}
 	
function enableDesableAgent(){
	if(document.getElementsByName("com.app.cases.Updatecase").length){
	//$('#c_assignedagent').removeAttr('required');
		var destCity= $("#c_rcv_state").val();
		
	 	if (destCity !== undefined && destCity != null ){
	 		var dataToSend = {"destState":destCity};
			$.post('../../HideAgentByEditeCaseSRVL' , dataToSend, function(data, status){ 
					//alert("Data: " + data +", Status:" + status);
					if (status=='success'){
						if(data === 'false'){
							$('#c_assignedagent').removeAttr('required');
						}else{
							$('#c_assignedagent').attr("required",true);
						}
							
					}else{
						alert("Error, please contact MR.NAFIE");
					}
			 });
		}
	}
}


function forcePayCheckN(caseId,c_custreceiptnoori){
	var c = confirm("هل انت متاكد من ازالة التاكيد عن هذا الوصل المرقم "+ c_custreceiptnoori + " ? ");
	if(c){
	var userId = <%=user.getUsid()%>;
	var dataToSend = {caseid : caseId};
	$.post('../../MakeCasePaytodlvcheckNoSRVL' , dataToSend, function(data, status){ 
		if (status=='success'){
			$.alert({
			    title: '',
			    content: 'تم ازالة تأكيد الوصل',
			});
			hideLoader();
			  var bu = document.getElementById("buttonpaytodlvcheck"+c_custreceiptnoori);
			  bu.style.display = 'none'; 
		}else{
			alert("Error, please contact Mohammed Nafie");
			hideLoader();
		}
 	});}
}


$('#c_custreceiptnoori').focus();
</script>