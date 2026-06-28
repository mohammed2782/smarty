<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.returnables.OtherBranchsManifestSentReturns" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet" %>
<%
Connection connBranches = null; 
	String branchAccountReturnProcess = (String)request.getParameter("branchAccountReturnProcess");
	if (branchAccountReturnProcess !=null){
		Myglobals.smartyGlobalsAssArr.put("branchAccountReturnProcess", (String)branchAccountReturnProcess);
	}else if (Myglobals.smartyGlobalsAssArr.containsKey("branchAccountReturnProcess") && Myglobals.smartyGlobalsAssArr.get("branchAccountReturnProcess")!=null){
		branchAccountReturnProcess = (String)Myglobals.smartyGlobalsAssArr.get("branchAccountReturnProcess");
	}
	LinkedHashMap<String,String> branchesList = new LinkedHashMap<String,String>();
	LinkedHashMap<String,String> branchInfo = new LinkedHashMap<String,String>();
	try{
		connBranches = mysql.getConn();
		branchesList = Utilities.getListOfBranches(connBranches, (int)Myglobals.smartyGlobalsAssArr.get("userstorecode"));
		if (branchAccountReturnProcess!=null)
	branchInfo= Utilities.getBranchesInfo(connBranches, branchAccountReturnProcess); 
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		try{connBranches.close();}catch(Exception e){}
	};
%>
<form action="?myBranchSearch=1" method="post" name="search_branch_form"
	style='background-color: transparent; margin-bottom: 5px;'>
	<div class="row">
		<div class="col-3">
			<select class='select2' id='branchAccountReturnProcess' style="width: 200px;"  name ='branchAccountReturnProcess' >
				<option value='' ></option>
           		<%
           		for (String branchId : branchesList.keySet()){
           		           		  	if (branchAccountReturnProcess!=null && branchAccountReturnProcess.equalsIgnoreCase(branchId)){
           		%>
           			<option value='<%=branchId%>' selected><%=branchesList.get(branchId)%></option>
           			<%
           			}else{
           			%>
           			<option value='<%=branchId%>' ><%=branchesList.get(branchId)%></option>
           			<%
           			}
           			           		}
           			%>
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
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
%>
	<div class="row" style='margin-top:10px'>
		<div class="col-sm-1 col-sm-offset-1">
			<label>Barcode</label>
		</div>
		<div class="col-sm-6">
			<input type='text' style='color:#424242;background-color:#E9E5E5;' id ='barcode_checker_branches' />
		</div>
	</div>
<%
OtherBranchsManifestSentReturns bmr = new OtherBranchsManifestSentReturns(); 
	Render(bmr  , out , request, response , Myglobals , objectState , pageName1);
%>
</div>
<%} %>
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
	$("#selected_casesto_rtn_branches").val('');
	$("#selected_casesto_rtn_branches").val(selectedCases);
	if ($("#selected_casesto_rtn_branches").val().length>0){
		this.submit();
	}else{
		e.preventDefault();
		generalErrorPrettyMsg("لم يتم تحديد أي وصل");
	}
	
	//console.log("selected_casesto_rtn_branches---->"+$("#selected_casesto_rtn_branches").val());
});

$('#barcode_checker_branches').focus();
var input = document.getElementById("barcode_checker_branches");
var old_receipt = '';
var scannedCounter = 1;
//rgba(194 , 214 , 245 , 0.7)
//2px solid rgb(43 73 220)
input.addEventListener("keyup", function(event) {
	
  if (event.keyCode === 13) {
	  var barcodeScannedBranches = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
	 
	  if (barcodeScannedBranches !== null && barcodeScannedBranches !== undefined){
		  var reciept = document.getElementById(barcodeScannedBranches);
		  $(reciept).parent().css({ 
	            "background-color": "rgba(194 , 214 , 245 , 0.7)", 
	            "border": "2px solid rgb(43 73 220)",
	   		});
		  var caseid = $(reciept).attr("caseid");
		  if (caseid){
			  $('html, body').stop().animate({
		            scrollTop: $("#"+barcodeScannedBranches).offset().top - 100
		        }, 500);
			  
			  $("#pmtcheck_"+caseid).attr("checked", true);
			 
			  
			  $(old_receipt).parent().css({ 
		            "background-color": "rgba(249,210,179,0.37)", 
		            "border": "2px solid #dc2b2b",
		            
		   		});
			  old_receipt = reciept; 
			  scannedCounter++;
		  }else{
			  alert("لم يتم أيجاد هذا الوصل "+barcodeScannedBranches);
		  }
	  }	 
	  input.value = '';
	  $('#barcode_checker_branches').focus();
  } 
});



/* Close Manifest */
function handOverRtnManifestBtn(a_branchReturnManifestId){
	$("#loading").css("display", "flex");
	$.confirm({ 
	    title: 'إغلاق منفسيت الراجع',
	    content: 'سيتم أغلاق منفيست الراجع ويتعبر المنفيست تم تسليمه بشكل نهائي للفرع',
	    buttons: {
	        confirm:{
	        	text :'نعم',
	        	action : function () {
	        		confirmedHandOverBranchRtnManifest(a_branchReturnManifestId);
	        	}
	        },
	        cancel:{
	        	text :'لا',
	        	action : function () {
	        		hideLoader();
	        		}
	        }
	    }
	});
}
function confirmedHandOverBranchRtnManifest(a_branchReturnManifestId){
	var dataToSend = {"branchManifestRtnId":a_branchReturnManifestId };
	$.post('../../HandOverBranchReturnablesSRVL' , dataToSend, function(data, status){ 
		if (status=='success'){
			$.alert({
			    title: '',
			    content: 'تم إغلاق منفيست الراجع',
			});
			$("#td-close-branch-rtn-"+a_branchReturnManifestId).html("<span class=\"badge rounded-pill bg-secondary\">تم التسليم وأغلاق المنفيست</span>");
			hideLoader();
		}else{
			alert("Error, please contact Mohammed Nafie");
			hideLoader();
		}
 	});
}
</script>
        