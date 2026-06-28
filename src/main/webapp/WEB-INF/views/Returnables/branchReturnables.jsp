<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.returnables.BranchManifestReturn" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet"%>
<%
Connection conn1 = null;
	PreparedStatement pst = null;
	ResultSet rs = null; 
	Utilities ut = new Utilities(); 

	String branchAccountReturnProcess = (String)request.getParameter("branchAccountReturnProcess");
	if (branchAccountReturnProcess !=null){
		Myglobals.smartyGlobalsAssArr.put("branchAccountReturnProcess", (String)branchAccountReturnProcess);
	}else if (Myglobals.smartyGlobalsAssArr.containsKey("branchAccountReturnProcess") && Myglobals.smartyGlobalsAssArr.get("branchAccountReturnProcess")!=null){
		branchAccountReturnProcess = (String)Myglobals.smartyGlobalsAssArr.get("branchAccountReturnProcess");
	}
	LinkedHashMap<String,String> branchesList = new LinkedHashMap<String,String>();
	LinkedHashMap<String,String> branchInfo = new LinkedHashMap<String,String>();
	try{
		conn1 = mysql.getConn();
		branchesList =Utilities.getListOfBranches(conn1, (int)Myglobals.smartyGlobalsAssArr.get("userstorecode"));
		if (branchAccountReturnProcess!=null)
	branchInfo= ut.getBranchesInfo(conn1, branchAccountReturnProcess); 
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		try{rs.close();}catch(Exception e){}
		try{pst.close();}catch(Exception e){}
		try{conn1.close();}catch(Exception e){}
	}
	
%>
<form action="?myBranchSearch=1" method="post" name="search_branch_form"
	style='background-color: transparent; margin-bottom: 5px;'>
	<div class="row">
		<div class="col-3">
			<select class='select2' id='branchAccountReturnProcess' style="width: 200px;"  name ='branchAccountReturnProcess' >
				<option value='' ></option>
           		<%for (String branchId : branchesList.keySet()){
           		  	if (branchAccountReturnProcess!=null && branchAccountReturnProcess.equalsIgnoreCase(branchId)){
           		%>
           			<option value='<%=branchId%>' selected><%=branchesList.get(branchId)%></option>
           			<%}else{
           		%>
           			<option value='<%=branchId%>' ><%=branchesList.get(branchId)%></option>
           			<%}
           		}%>
           	</select>
	    </div>
	   <div class="col-3">
	    	<button type='submit' class="btn btn-dark btn-md" type="button">عرض  كشوفات الرواجع للفرع<i class="fa fa-search m-right-xs"></i></button>
	   </div>
    </div>
</form>
<div class="row">
	<div class="col-12">
<%
if (branchAccountReturnProcess!=null && !branchAccountReturnProcess.trim().equalsIgnoreCase("")){
%>
         <%
        	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
         	BranchManifestReturn bmr = new BranchManifestReturn(); 
         	Render(bmr  , out , request, response , Myglobals , objectState , pageName1); 
         %>

<%} %>
	</div>
</div>
<%@ include file="../Main/footer.jsp"%>
<script>

function changeToPrepairAll(){
	var check = false;
	if ($("#allprepair").prop("checked"))
		check = true;
	$('[id^=pmtcheck_]').each(function() {
		$(this).attr("checked", check);
	});

}

$('#liaison-agent-prepare-return-form').submit(function(e){
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
		  $(reciept).parent().css({ "background-color": "rgba(194 , 214 , 245 , 0.7)", "border": "2px solid rgb(43 73 220)",});
		  var caseid = $(reciept).attr("caseid");
		  $('html, body').stop().animate({scrollTop: $("#"+barcodeScanned).offset().top - 100}, 500);
		  $("#pmtcheck_"+caseid).attr("checked", true);
		  $(old_receipt).parent().css({ 
	            "background-color": "rgba(249,210,179,0.37)", 
	            "border": "2px solid #dc2b2b",});
		  old_receipt = reciept; 
		  scannedCounter++;
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
</script>
        