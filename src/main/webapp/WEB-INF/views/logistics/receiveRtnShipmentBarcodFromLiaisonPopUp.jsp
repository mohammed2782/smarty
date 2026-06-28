<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="java.sql.PreparedStatement, java.sql.ResultSet,
 com.app.bussframework.ReceiveFromLiaisonAgentPopUp " %>
<% 

	String tobranch_receiveRtnShipmentBarcodFromLiaisonPopUp = (String)request.getParameter("tobranch");
	if (tobranch_receiveRtnShipmentBarcodFromLiaisonPopUp !=null){
		Myglobals.smartyGlobalsAssArr.put("tobranch_receiveRtnShipmentBarcodFromLiaisonPopUp", (String)tobranch_receiveRtnShipmentBarcodFromLiaisonPopUp);
	}else if (Myglobals.smartyGlobalsAssArr.containsKey("tobranch_receiveRtnShipmentBarcodFromLiaisonPopUp") && Myglobals.smartyGlobalsAssArr.get("tobranch_receiveRtnShipmentBarcodFromLiaisonPopUp")!=null){
		tobranch_receiveRtnShipmentBarcodFromLiaisonPopUp = (String)Myglobals.smartyGlobalsAssArr.get("tobranch_receiveRtnShipmentBarcodFromLiaisonPopUp");
	}

	ReceiveFromLiaisonAgentPopUp rfla = new ReceiveFromLiaisonAgentPopUp();
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName(); 
	Render(rfla  , out , request, response , Myglobals , objectState , pageName1);
%> 
<div class="row"><div class="col-md-offset-4 col-md-2 col-xs-offset-6 col-xs-3">
<input type="button" class="btn btn-danger" onclick="window.close();"  value="غلق النافذه" /></div></div>
<jsp:include page="../Main/footer-popup.jsp" />
<script>
$(document).ready(function() {
	  $(window).keydown(function(event){
	    if(event.keyCode == 13) {
	      event.preventDefault();
	      return false;
	    }
	  });
	}); 
	
window.onunload = refreshParent;
function refreshParent() {window.opener.location.reload();}

$('#barcode_checker').focus();
var input = document.getElementById("barcode_checker");
var old_receipt = '';
var scannedCounter = 1;
input.addEventListener("keyup", function(event) {
  if (event.keyCode === 13) {
	  
	  var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
	 
	  if (barcodeScanned !== null && barcodeScanned !== undefined){
		  
		  var reciept = document.getElementById(barcodeScanned);
		  $(reciept).parent().css({ 
	            "background-color": "rgb(97 119 212 / 83%)", 
	            "border": "2px solid rgb(43 73 220)",
	   		});
		  var caseid = $(reciept).attr("caseid");
		  if (caseid){
			  $('html, body').stop().animate({
		            scrollTop: $("#"+barcodeScanned).offset().top - 100
		        }, 500);
			  
			  $("#pmtcheck_"+caseid).attr("checked", true);
			  $("#confirmCheckBox_"+caseid).attr("checked", true);
			  //confirmCheckBox_
			  $("#q_action_smartyrow_"+caseid).val("RTN_RCVDFROMLIAISON");
			 
			  
			  $(old_receipt).parent().css({ 
		            "background-color": "rgba(249,210,179,0.37)", 
		            "border": "2px solid #dc2b2b",
		            
		   		});
			  old_receipt = reciept; 
			  scannedCounter++;
		  }else{
			  generalErrorPrettyMsg("ÙÙ ÙØªÙ ØªØ­Ø¯ÙØ¯ Ø§Ù ÙØµÙ");
		  }
	  }	 
	  input.value = '';
	  $('#barcode_checker').focus();
  } 
});

</script>