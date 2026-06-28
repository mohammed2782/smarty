<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.bussframework.LiaisonAgentBarcodeCheckerToBeReturned" %> 
<% 	String popuprtnfrombranch_chain = (String)request.getParameter("popuprtnfrombranch_chain");
    if (popuprtnfrombranch_chain !=null ){
    	Myglobals.smartyGlobalsAssArr.put("popuprtnfrombranch_chain", (String)popuprtnfrombranch_chain);
    }else if (Myglobals.smartyGlobalsAssArr.containsKey("popuprtnfrombranch_chain") && Myglobals.smartyGlobalsAssArr.get("popuprtnfrombranch_chain")!=null){
 	   popuprtnfrombranch_chain = (String)Myglobals.smartyGlobalsAssArr.get("popuprtnfrombranch_chain");
    }
 %>

<div class="row" style='margin:15px;'>
	<div class="col-sm-1 col-sm-offset-1"><label>Barcode</label>
	</div>
	<div class="col-sm-6">
		<input type='text' style='color:#424242;background-color:#E9E5E5;' id ='barcode_checker' />
	</div>
</div>

<%
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
LiaisonAgentBarcodeCheckerToBeReturned lbc = new LiaisonAgentBarcodeCheckerToBeReturned(); 
	Render(lbc , out , request, response , Myglobals , objectState , pageName1);
%>
<script>
/* 
$('[id^=selected_caseto_return_smartyrow_]').each(function() {
	//console.log($(this).parent());
	$(this).attr('disabled',true);
});
  */
$('#barcode_checker').focus();

function checkBoxPmtClicked(){
	
}

var input = document.getElementById("barcode_checker");

input.addEventListener("keyup",async  function(event) {
  if (event.keyCode === 13) {
	  var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
	 
	  if (barcodeScanned !== null && barcodeScanned !== undefined){
		  var reciept = document.getElementById(barcodeScanned);
		  console.log(reciept);
		  $(reciept).parent().css({ 
	            "background-color": "rgb(79 80 86)", 
	            "border": "2px solid #dc2b2b",
	            
	   		});
		  var caseid = $(reciept).attr("caseid");
		  $('html, body').stop().animate({
	            scrollTop: $("#"+barcodeScanned).offset().top
	        }, 500);
		  
		  $("#printmanifestcheck_"+caseid).prop('checked', true);		 
	  }	 
	  input.value = '';
	  $('#barcode_checker').focus();
  }
});

window.onunload = refreshParent;
function refreshParent() {
    var loc = window.opener.location;
    window.opener.location = loc;
    //window.opener.location.reload();
}

</script>
<jsp:include page="../Main/footer-popup.jsp" />