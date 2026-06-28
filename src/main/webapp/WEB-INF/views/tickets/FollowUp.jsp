<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.tickets.FollowUp" %> 


<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	FollowUp followUp = new FollowUp(); 
 	Render(followUp  , out , request, response , Myglobals , objectState , pageName1); 

%>
<%@ include file="../Main/footer.jsp"%>
<script>
 

$('#c_custreceiptnoori').focus();

function openForFollowUp(caseId, receiptNo ){
	 $.ajax({
	        type: "POST",
	        url: "../../FollowUpAssginEmpSRVL",
	        data: {'caseId':caseId},
	        cache: false,
	        success: function(data) {
	        	console.log(data);
	        	if (data != '0'){
	        		$("#followup-displaycaseinfo-div-"+caseId).css("display","block");
	        		$("#hidden_rcv_hp1_"+caseId).css("display","block");
	        		$("#btn-start-followup-"+caseId).css("display","none");
	        	}else{
	        		
	        		generalErrorPrettyMsg("تم أستلام الشحنة من موظف أخر للمتابعة");
	        	}
	        },
	        error: function () {
	        	Swal.fire(
	            "Internal Error",
	            "Oops, your note was not saved.",
	            "error"
	            )
	        }
	    });
}

</script>