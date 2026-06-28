<%@ include file="../Main/Main.jsp"%>
<%@ page import="java.sql.PreparedStatement, java.sql.ResultSet, com.app.cust.logistics.ReadyToPrint" %>
	


<form id = 'PrintAllSellBills' action='../../PrintAllSellBillsSRVL' method='post'>
	<input type='hidden' id='casesToPrint' name='casesToPrint' value=''>
	<input type='hidden' id='printedby' name='printedby' value='<%=user.getUsid()%>'>
</form>

<%
ReadyToPrint rtp = new ReadyToPrint();
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	
	Render(rtp  , out , request, response , Myglobals , objectState , pageName1);
%> 


<script>
smarty_submitButton_allow_disable = false;
var totalBoxesChecked = 0;
var userid = "<%out.print(user.getUserID());%>";

function checkAllCheckBoxes(that){
	var row =0;
	$('input[id^="confirmCheckBox_"]').each(function(){
		row = $(this).attr("data-check-seq");
		if ($(that).prop("checked")){
			$(this).prop("checked", true);
			$("#q_action_smartyrow_"+row).val('goreadypickup');
		}else{
			$(this).prop("checked", false);
			$("#q_action_smartyrow_"+row).val('');
		}
		$("#q_action_smartyrow_"+row).trigger('change');
	});
}


function changePrintBtnColor(cid){
	$("#printbtn_"+cid).removeClass("btn-warning").addClass("btn-success");  
	$("#printbtn_"+cid).html("طباعة الوصل <i class=\"fa fa-print fa-lg\"></i>");
}

function checkBoxChecked(that, row){

	if ($(that).prop("checked")){
		$("#q_action_smartyrow_"+row).val('goreadypickup');
		
	}else{
		$("#q_action_smartyrow_"+row).val('');
	}
	$("#q_action_smartyrow_"+row).trigger('change');
}

function globalSellBillPrintBtn (printButton, event){
	event.preventDefault();
	var cases ='';
	var first = true;
	
	$('input[id^="confirmCheckBox_"]').each(function (){
		
		if ($(this).prop("checked")){
			if (!first){
				cases +=",";
			}else{
				first = false;
			}
			changePrintBtnColor(this.id.replace("confirmCheckBox_",""));
			cases +=this.id.replace("confirmCheckBox_","");
	     }
	});
	
	if (cases){
		$("#casesToPrint").val(cases);
		$("#PrintAllSellBills").submit();
	}
	//console.log(cases);
}





</script>
<jsp:include page="../Main/footer.jsp" />