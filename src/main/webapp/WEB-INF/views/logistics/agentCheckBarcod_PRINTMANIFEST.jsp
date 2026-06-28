<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="java.sql.PreparedStatement, java.sql.ResultSet,
 com.app.bussframework.DriverAgentBarcodeCheckerFromPrintManifest " %>
<% 

	String agintidassignedto = (String)request.getParameter("agintidassignedto");
	String comingfrombranch = (String)request.getParameter("comingfrombranch");
	String agentName = "";
	if (agintidassignedto !=null){
		Myglobals.smartyGlobalsAssArr.put("agintidassignedto", (String)agintidassignedto);
		Myglobals.smartyGlobalsAssArr.put("comingfrombranch", (String)comingfrombranch);
	}else if (Myglobals.smartyGlobalsAssArr.containsKey("agintidassignedto") && Myglobals.smartyGlobalsAssArr.get("agintidassignedto")!=null){
		agintidassignedto = (String)Myglobals.smartyGlobalsAssArr.get("agintidassignedto");
		comingfrombranch = (String)Myglobals.smartyGlobalsAssArr.get("comingfrombranch");
	}

	
	Connection conn = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	try{
		conn = mysql.getConn();
		pst = conn.prepareStatement("select us_name from kbusers where us_id=?");
		pst.setString(1, agintidassignedto);
		
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
            <div class="panel-body" style='padding:3px;'>
            	<div class='row'>
            		<div class='col-xs-6'><h6>شحنات المندوب : <%=agentName%></h6></div>
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
	DriverAgentBarcodeCheckerFromPrintManifest dabcfpm = new DriverAgentBarcodeCheckerFromPrintManifest();
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName(); 
	Render(dabcfpm  , out , request, response , Myglobals , objectState , pageName1);
%> 
<div class="row"><div class="col-md-offset-4 col-md-2 col-xs-offset-6 col-xs-3">
<input type="button" class="btn btn-danger" onclick="window.close();" value="غلق النافذه" /></div></div>
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
		  $('html, body').stop().animate({
	            scrollTop: $("#"+barcodeScanned).offset().top - 100
	        }, 500);
		  
		  $("#prepairmanifest_check_"+caseid).attr("checked", true);
		  
		  $(old_receipt).parent().css({ 
	            "background-color": "rgba(249,210,179,0.37)", 
	            "border": "2px solid #dc2b2b",
	            
	   		});
		  old_receipt = reciept; 
		  scannedCounter++;
	  }	 
	  input.value = '';
	  $('#barcode_checker').focus();
  } 
});

</script>