<%@ include file="../Main/Main.jsp"%>
<%@ page import="java.sql.PreparedStatement, java.sql.ResultSet, com.app.cust.logistics.ReadyToPickUp" %>
	


<form id = 'PrintDeliveryOrder' action='../../PrintDeliveryOrderPdfSRVL' method='post'>
	<input type='hidden' id='casesToPrint' name='casesToPrint' value=''>
	<input type='hidden' id='printedby' name='printedby' value='<%=user.getUsid()%>'>
	<input type='hidden' id='mastercustomerid' name='mastercustomerid' value='<%=user.getMasterCustId()%>'>
</form>

<%
ReadyToPickUp rtpic = new ReadyToPickUp();
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	
	Render(rtpic  , out , request, response , Myglobals , objectState , pageName1);
%> 


<jsp:include page="../Main/footer.jsp" />

<script>
function sendAllBackToPrintStage(that){
	var row =0;
	//console.log("------------>"+this);
	$('select[id^="q_action_smartyrow_"]').each(function(){
		//console.log("------------>"+this);
		if ($(that).prop("checked")){
			$(this).val('backtoreadytoprint');
		}else{
			$(this).val('');
		}
		$(this).trigger('change');
	});
}

function globalSellBillPrintBtn (printButton, event){
	event.preventDefault();
	var caseIds ='';
	
	var first = true;
	//console.log("----------"+$("input[name=smartyhiddenmultieditrowsno]").val());
	if (!$("input[name=smartyhiddenmultieditrowsno]").val()){
		
		return;
	}
	$('input[name^="smarty_c_id_hidden_smartyrow_"]').each(function (){
		//console.log("---------->here"+this.value);
		if (!first){
			caseIds +=",";
		}else{
			first = false;
		}
		caseIds +=this.value;
	});
	
	if (caseIds){
		$("#casesToPrint").val(caseIds);
		$("#PrintDeliveryOrder").submit();
		
	}
	console.log(caseIds);
}

</script>