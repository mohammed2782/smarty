<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.NewCases" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet" %>
<%

 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	NewCases nc = new NewCases(); 
 	Render(nc  , out , request, response , Myglobals , objectState , pageName1); 
 	 
%> 
<script>
// read barcode
var input = document.getElementById("barcode");
$(input).focus();
var inputKeyCodeArr = [];
input.addEventListener("keydown",  function(event) {
	//console.log(event.keyCode);
	 if(event.keyCode == 13) {
		 $(input).val(inputKeyCodeArr.join(""));
		 var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
		 //console.log(barcodeScanned);
	     event.preventDefault();
	     //console.log(inputKeyCodeArr.join(""));
	     //console.log("----"+String.fromCharCode(inputKeyCodeArr.join(", ")));
	     inputKeyCodeArr = [];
	      addrow(barcodeScanned);
	     $(input).val('');
	     $(input).focus();
	     return false;
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

var RCVno = 1;
var rowNum = 1;
var RCVtable = document.getElementById("rcv_dtls");
// 
function addrow(recieptNo){
	var error = false;
	for (i = 1 ; i<=RCVno ; i++){
		if ($("#c_custreceiptnoori_smartyNewRow_"+i).val() == recieptNo){
			alert("هذا الوصل تم ادخاله سابقا");
			$("#c_custreceiptnoori_smartyNewRow_"+i).css( 'background-color','#f5bed8');
			error = true;
		}
	} 
	if (!error){
		dataToSend = {};
		//var row  =RCVtable.insertRow(rowNum);
		$.get('../../MultiRowsBarCodeSRVL?loadRcvRow='+RCVno+"&barCodeRcpNo="+recieptNo , dataToSend)
		.done(
			function(data, status){ 
				if (status=='success'){
					$('#rcv_dtls').append(data);
					$(".select2").select2({
					dropdownAutoWidth: true,
					width: '100%'
					});
					init_InputMask();
					$("#c_receiptamt_smartyNewRow_"+RCVno).focus();
					RCVno++;
					rowNum++;
				}		
			 })
		.fail(function(xhr, status, error) {alert(xhr.responseText);});
	}
}

function remove_row( rownum){
	$('table#rcv_dtls tr#smartyNewRow_'+rownum).remove();
}

// when state changes 
function loadDistrict(seq){
	var destCity= $("#rcv_city_smartyNewRow_"+seq).val();
	var keyCol = "rcv_district_smartyNewRow_"+seq;
	var req = "Y";
	var lookupSql = "select cdi_id, cdi_name from "
	+ "(select '' as cdi_id , '' as cdi_name from dual  union select cdi_id, cdi_name from kbcity_district where  cdi_stcode = '"+destCity+"')ttt ";
	
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("rcv_district_smartyNewRow_"+seq);
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'POST',
		cache: false,
		url:'../../myajax.jsp',  
		data: dataToSend,
		error:function(){ alert("some error occurred") },
		success: function(data, status){
				if (status=='success'){ 
					targetHTMLElement.innerHTML=data;
				}
		}
	});
}



</script>

<%@ include file="../Main/footer.jsp"%>
