<%@ include file="../Main/Main.jsp"%> 
<%@ page import="com.app.bussframework.UpdateInstorgeCasesBarcode" %> 
<div class="row"><div class="col-sm-2 col-sm-offset-1"><label>Barcode</label></div><div class="col-sm-9">
	<input type='text' style='color:#424242;background-color:#E9E5E5;' id ='barcode_checker' />
	</div></div>
<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	UpdateInstorgeCasesBarcode uicb = new UpdateInstorgeCasesBarcode(); 
	Render(uicb , out , request, response , Myglobals , objectState , pageName1);
%> 
<%@ include file="../Main/footer.jsp"%>  
<script>
$('#barcode_checker').focus();
var input = document.getElementById("barcode_checker");
var rowNo = 1;
input.addEventListener("keyup", function(event) {
  if (event.keyCode === 13) {
	  getrcpNo();
	  $('#barcode_checker').focus();
  }
});
function dosearch (){
	getrcpNo();
}

function doGlobalSelectForAgents(){
	
	var globalAgentid = $("#globalagentselect").val();
	var globalAgentName = $('#globalagentselect').find(":selected").text();
	//alert(globalAgentName);
	
	$('[id^=c_assignedagent_smartyrow_]').each(function() {
		 var number = this.id.split('_').pop();
		 $('#c_assignedagent_smartyrow_'+number+' option:selected').text(globalAgentName);
		 $('#c_assignedagent_smartyrow_'+number+' option:selected').val(globalAgentid);
		 $('#c_assignedagent_smartyrow_'+number).attr("disabled", true); 
		 
	});
	
}

function doGlobalSelectForDistructs(){
	
	var globalDistructCod = $("#globaldistructselect").val();
	var globalDistructName = $('#globaldistructselect').find(":selected").text();
	//alert(globalAgentName);
	
	$('[id^=c_rcv_district_smartyrow_]').each(function() {
		 var number = this.id.split('_').pop();
		 $('#c_rcv_district_smartyrow_'+number+' option:selected').text(globalDistructName);
		 $('#c_rcv_district_smartyrow_'+number+' option:selected').val(globalDistructCod);
		 $('#c_rcv_district_smartyrow_'+number).attr("disabled", true); 
		 
	});
	
}

function getrcpNo(){

	var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
	 $.get('../../GetSingleReceiptInfoSRVL?stage=INIT&step=NEWINSTORE&c_custreceiptnoori='+barcodeScanned ,function(data, status){
			if (status=='success'){
				var globalDistructCod = $("#globaldistructselect").val();
				var globalDistructName = $('#globaldistructselect').find(":selected").text();
				var globalAgentid = $("#globalagentselect").val();
				var globalAgentName = $('#globalagentselect').find(":selected").text();
				
				console.log(data[0]);
				if ( 'NEWINSTORE' != data[0].stepcode){
					alert('هذا الوصل في مرحلة '+data[0].stepname);
				}else if (<%=user.getBranchCode()%> != data[0].current_branch){
					alert('لا وجود لهذا الوصل ');
				}else{
					if ($("#"+barcodeScanned).length<=0){
						var dataCell0 = "<td>"+rowNo+"</td>";
						var dataCell1 = "<td id = '"+barcodeScanned+"' >"+barcodeScanned+"</td>";
						var dataCell2 = "<td>"+data[0].custName+"</td>";
						var dataCell3 = "<td>"+
											"<select name ='c_assignedagent_smartyrow_"+rowNo+"' id='c_assignedagent_smartyrow_"+rowNo+"' style='width: 100px;height: 20px;' required  disabled>"+
												"<option  value='"+globalAgentid+"' >"+globalAgentName+"</option>"+
											"</select>"+
										"</td>";
						var dataCell4 = "<td>"+
											"<select name ='c_rcv_district_smartyrow_"+rowNo+"' id='c_rcv_district_smartyrow_"+rowNo+"' style='width: 100px;height: 20px;' required disabled>"+
												"<option  value='"+globalDistructCod+"' >"+globalDistructName+"</option>"+
											"</select>"+
										"</td>";
						var dataCell7 = "<td>"+data[0].hp+"</td>";
						var dataCell5 = "<td>"+data[0].receiptamt+"</td>";
						var dataCell6 = "<td><button type='button' style='float:left' onclick='remove_row(this)' class='btn btn-xs btn-danger'>"+
							"<i class=' fa fa-trash'></i></button>";
						dataCell6 +="</td>";
						var hiddenFields = 	"<input type='hidden' name='q_id_row_"+rowNo+"' value='"+data[0].q_id+"'/>"+
							 				"<input type='hidden' name='c_id_row_"+rowNo+"' value='"+data[0].caseid+"'/>";
						$('#numberofrowsscanned').val(rowNo);
						
						console.log('------------------------------------>'+$('#numberofrowsscanned').val());
						var row = '<tr>'+dataCell0+dataCell1+dataCell2+dataCell3+dataCell4+dataCell7+dataCell5+dataCell6+hiddenFields+'</tr>';
							$("#smarty_table_com_dot_app_dot_bussframework_dot_UpdateInstorgeCasesBarcode tr:last").after (row);
							rowNo ++;
					}else{
						alert('هذا الوصل تم قراءته مسبقا');
					}
				}
			}else{
				alert('error');
			}
		});
	  input.value = '';
}
function remove_row(that) {
	
	$(that).parent().parent().remove();

}
$(document).ready(function() {
	  $(window).keydown(function(event){
	    if(event.keyCode == 13) {
	      event.preventDefault();
	      return false;
	    }
	  });
	});
</script>