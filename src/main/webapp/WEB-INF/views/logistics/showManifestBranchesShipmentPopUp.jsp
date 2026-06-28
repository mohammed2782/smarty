<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="java.sql.PreparedStatement, java.sql.ResultSet,
 com.app.bussframework.LiaisonAgentShipmentsBranchesPopUp " %>
<% 

	String cc_liaisonagentid = (String)request.getParameter("cc_liaisonagentid");
	String cc_tobranch = (String)request.getParameter("cc_tobranch");
	String agentName = "";
	if (cc_liaisonagentid !=null){
		Myglobals.smartyGlobalsAssArr.put("LIAISONAGENT_BRANCHES_MANIFEST_SHIPMENTPOPUP", (String)cc_liaisonagentid);
		Myglobals.smartyGlobalsAssArr.put("TOBRANCH_BRANCHES_MANIFEST_SHIPMENTPOPUP", (String)cc_tobranch);
	}else if (Myglobals.smartyGlobalsAssArr.containsKey("TOBRANCH_BRANCHES_MANIFEST_SHIPMENTPOPUP") && Myglobals.smartyGlobalsAssArr.get("TOBRANCH_BRANCHES_MANIFEST_SHIPMENTPOPUP")!=null){
		cc_liaisonagentid = (String)Myglobals.smartyGlobalsAssArr.get("LIAISONAGENT_BRANCHES_MANIFEST_SHIPMENTPOPUP");
		cc_tobranch = (String)Myglobals.smartyGlobalsAssArr.get("TOBRANCH_BRANCHES_MANIFEST_SHIPMENTPOPUP");
	}
	 
	Connection conn = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	try{
		conn = mysql.getConn();
		pst = conn.prepareStatement("select us_name from kbusers where us_id=?");
		pst.setString(1, cc_liaisonagentid);
		
		rs = pst.executeQuery();
		if (rs.next())
			agentName = rs.getString("us_name");
		
		}catch(Exception e){
		e.printStackTrace();
	}finally{
		try{rs.close();}catch(Exception e){}
		try{pst.close();}catch(Exception e){}
		try{conn.close();}catch(Exception e){}
	}

%>
<div class="row">
<div class="col-md-12">
          <div class="panel panel-warning">
            <div class="panel-heading">
            	<div class='row'>
	            	<div class='col-xs-12'>
	              		<h5></h5>
	            	</div>
	            </div>
            </div>
            <div class="panel-body" style='padding:20px;'>
            	<div class='row'>
            		<div class='col-xs-6'><h6>شحنات مندوب الأرتباط : <%=agentName%></h6></div>
            		<div class='col-xs-6'><h6></h6></div>
            	  </div>
            	
            </div>
            <div class="row" style='margin-right:10px;'>
				<div class="col-sm-1 col-sm-offset-1">
					<label>Barcode</label>
				</div>
			<div class="col-sm-6">
				<input type='text' style='color:#424242;background-color:#E9E5E5;' id ='barcode_checker' />
			</div>
		</div>
        </div>
	</div>
</div>
<%
	LiaisonAgentShipmentsBranchesPopUp lasbp = new LiaisonAgentShipmentsBranchesPopUp();
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName(); 
	Render(lasbp  , out , request, response , Myglobals , objectState , pageName1);
%> 
<div class="row"><div class="col-md-offset-4 col-md-2 col-xs-offset-6 col-xs-3">
<input type="button" class="btn btn-danger" onclick="window.close();" value="ØºÙÙ Ø§ÙÙØ§ÙØ°Ù" /></div></div>
<jsp:include page="../Main/footer-popup.jsp" />
<script>
window.onunload = refreshParent;
function refreshParent() {window.opener.location.reload();}


$('#barcode_checker').focus();
var input = document.getElementById("barcode_checker");
var old_receipt = '';
var scannedCounter = 1;
//rgba(194 , 214 , 245 , 0.7)
//2px solid rgb(43 73 220)
input.addEventListener("keyup", function(event) {
	
  if (event.keyCode === 13) {
	  var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
	 
	  if (barcodeScanned !== null && barcodeScanned !== undefined){
		  
		  var reciept = document.getElementById(barcodeScanned);
		  $(reciept).parent().css({ 
	            "background-color": "rgba(194 , 214 , 245 , 0.7)", 
	            "border": "2px solid rgb(43 73 220)",
	   		});
		  var caseid = $(reciept).attr("caseid");
		  if (caseid){
			  $('html, body').stop().animate({
		            scrollTop: $("#"+barcodeScanned).offset().top - 100
		        }, 500);
		 
		 
			  if ($("#manifestcheck_"+caseid).attr("checked")=='checked')
				  alert("هذا الوصل تم جرده سابقا");
			  else{ 
				  $("#manifestcheck_"+caseid).attr("checked", true);
				  checkBoxmanifestClicked(caseid);
				  $(old_receipt).parent().css({ 
			            "background-color": "rgba(249,210,179,0.37)", 
			            "border": "2px solid #dc2b2b",
			   		});
				  old_receipt = reciept; 
				  scannedCounter++;
			  }
		  }else{
			  alert("هذا الوصل غير موجود");
		  }
	  }	 
	  input.value = '';
	  $('#barcode_checker').focus();
  } 
});

function checkBoxmanifestClicked(caseid){
	if ($('#manifestcheck_'+caseid).prop("checked")) {
		$("#q_action_smartyrow_"+caseid).val("TOINSTORE");
	}else{
		$("#q_action_smartyrow_"+caseid).val("");
	}
}
</script>