<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.returnables.AgentReturnedItemsReceived" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet,
com.app.returnables.AgentToBeReturnedShipments" %>
<%
	Connection conn1 = null;
	PreparedStatement pst = null;
	ResultSet rs = null; 
	Utilities ut = new Utilities(); 
	
	String agentAccountReturnProcess = (String)request.getParameter("agentAccountReturnProcess");
	if (agentAccountReturnProcess !=null){
		Myglobals.smartyGlobalsAssArr.put("agentAccountReturnProcess", (String)agentAccountReturnProcess);
	}else if (Myglobals.smartyGlobalsAssArr.containsKey("agentAccountReturnProcess") && Myglobals.smartyGlobalsAssArr.get("agentAccountReturnProcess")!=null){
		agentAccountReturnProcess = (String)Myglobals.smartyGlobalsAssArr.get("agentAccountReturnProcess");
	}
	LinkedHashMap<String,String> agentsList = new LinkedHashMap<String,String>();
	LinkedHashMap<String,String> agentInfo = new LinkedHashMap<String,String>();
	try{
		conn1 = mysql.getConn();
		agentsList = ut.getListOfAgents(conn1, (int)Myglobals.smartyGlobalsAssArr.get("userstorecode"));
		if (agentAccountReturnProcess!=null)
	agentInfo= ut.getAgentInfo(conn1, agentAccountReturnProcess); 
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		try{rs.close();}catch(Exception e){}
		try{pst.close();}catch(Exception e){}
		try{conn1.close();}catch(Exception e){}
	}
	AgentToBeReturnedShipments agl = new AgentToBeReturnedShipments();
%>
<!-- page content -->
<div class="row turquoise_div">
	<div class='col-12'>
			<form action="?myagentsearch=1" method="post" name="search_cust_form" class="form-horizontal form-label-left" >
				<div class='row col-12'>
					<div class="col-6 ">
						
						<select class='select2 form-control' id='agentAccountReturnProcess' style="width: 200px;"  name ='agentAccountReturnProcess' >
							<option value='' ></option>
                    		<%for (String agentId : agentsList.keySet()){
                    		  	if (agentAccountReturnProcess!=null && agentAccountReturnProcess.equalsIgnoreCase(agentId)){
                    		%>
                    			<option value='<%=agentId%>' selected><%=agentsList.get(agentId)%></option>
                    			<%}else{
                    		%>
                    			<option value='<%=agentId%>' ><%=agentsList.get(agentId)%></option>
                    			<%}
                    		}%>
                    	</select>
	                </div>
	                <div class='col-6'>
	                	<button type='submit' class="btn btn-dark btn-md" type="button">عرض  الرواجع  لمندوب التوصيل<i class="fa fa-search m-right-xs"></i></button>
	                </div>
                </div>
			</form>

	</div>
	<div class='col-12'>
	<hr>
	</div>
	<div class='col-12'>
<%
if (agentAccountReturnProcess!=null && !agentAccountReturnProcess.trim().equalsIgnoreCase("")){

        	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
         	Render(agl  , out , request, response , Myglobals , objectState , pageName1);
         	if (agl.getRecords() == 0){
         		out.println("<h4>لا توجد رواجع  حاليا</h4>");
         	}
        	 AgentReturnedItemsReceived ap = new AgentReturnedItemsReceived(); 
         	Render(ap  , out , request, response , Myglobals , objectState , pageName1);
         %>
	
<%} %>
	</div>
</div>
<%@ include file="../Main/footer.jsp"%>
<script>

function checkAllByTime(that, count){
	console.log(count);
	var check = false;
	if ($(that).prop("checked"))
		check = true;
	$("input[data-single-check-time-"+count+"]").each(function() {
		$(this).attr("checked", check);
	});
}

function changeToPrepairAll(){
	var check = false;
	if ($("#allprepair").prop("checked"))
		check = true;
	$('[id^=pmtcheck_]').each(function() {
		$(this).attr("checked", check);
	});

}
$('#barcode_checker').focus();
var input = document.getElementById("barcode_checker");
var old_receipt = '';
var scannedCounter = 1;
var inputKeyCodeArr = [];
input.addEventListener("keyup", function(event) {
  if (event.keyCode === 13) {
	  $(input).val(inputKeyCodeArr.join(""));
	  inputKeyCodeArr = [];
	  var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
	  if (barcodeScanned !== null && barcodeScanned !== undefined){
		  var reciept = document.getElementById(barcodeScanned);
		  $(reciept).parent().css({ 
	            "background-color": "rgba(194 , 214 , 245 , 0.7)", "border": "2px solid rgb(43 73 220)",});
		  var caseid = $(reciept).attr("caseid");
		  if (caseid){
			  $('html, body').stop().animate({scrollTop: $("#"+barcodeScanned).offset().top - 100}, 500);
			  $("#pmtcheck_"+caseid).attr("checked", true);
			  $(old_receipt).parent().css({"background-color": "rgba(249,210,179,0.37)", "border": "2px solid #dc2b2b",});
			  old_receipt = reciept; 
			  scannedCounter++;
		  }else{
			  generalErrorPrettyMsg(" هذا الوصل غير موجود "+barcodeScanned);
		  }
	  }	 
	  input.value = '';
	  $('#barcode_checker').focus();
  }else{
	  if (event.keyCode >= 48 && event.keyCode <= 57){
		  inputKeyCodeArr.push(String.fromCharCode((96 <= event.keyCode && event.keyCode <= 105)? event.keyCode-48 : event.keyCode));
	  }else if (event.keyCode >= 65 && event.keyCode <= 90){
		  inputKeyCodeArr.push(String.fromCharCode((96 <= event.keyCode && event.keyCode <= 105)? event.keyCode-48 : event.keyCode));
	   }else if (event.keyCode >= 96 && event.keyCode <= 105){
		   inputKeyCodeArr.push(String.fromCharCode((96 <= event.keyCode && event.keyCode <= 105)? event.keyCode-48 : event.keyCode));
	   }
  } 
});

$('#agent-prepare-return-form').submit(function(e){
	console.log('heeeeeer');
	var selectedCases ="";
	var receiptNo = "";
	var first = true;
	
	$('[id^=pmtcheck_]').each(function() {
		if ($(this).prop("checked") == true){
		    var number = this.id.split('_').pop();
		    if (!first){
		    	selectedCases +=",";
		    }
		    selectedCases +=number;
		    first = false;
		}
	});
	
	$("#selected_casesto_pay").val('');
	$("#selected_casesto_pay").val(selectedCases);
	if ($("#selected_casesto_pay").val().length>0){
		this.submit();
		
	}else{
		e.preventDefault();
		generalErrorPrettyMsg("لم يتم تحديد أي وصل");
	}
	
	//console.log("selected_casesto_pay---->"+$("#selected_casesto_pay").val());
});
$("#heading-elements-com_app_returnables_AgentToBeReturnedShipments").css("display","none");
</script>
        