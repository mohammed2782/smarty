<%@ include file="../Main/Main.jsp"%>
<%@ page import="java.sql.PreparedStatement,com.app.util.Utilities,
 java.sql.ResultSet,com.app.bussframework.SingleQueue_ReceivingCasesFromDifferentStagesSteps,java.sql.Connection" %>

<div class="col-sm-2" style="padding-right: 10px;margin-top:20px;">
	<div class="position-relative">
		<input type="text" id ="barcode_checker" class="form-control ps-5 radius-30" 
		placeholder="رقم الوصل"> 
			<span class="position-absolute top-50 product-show translate-middle-y">
				<i class="bx bx-search">
				</i>
			</span>
	</div>
</div>
<%
	String pageName2 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SingleQueue_ReceivingCasesFromDifferentStagesSteps singleQueueCasesReceivingFromDifferentStagesSteps = new SingleQueue_ReceivingCasesFromDifferentStagesSteps();
	Render(singleQueueCasesReceivingFromDifferentStagesSteps , out , request, response , Myglobals , objectState , pageName2);
%> 
<script>
smarty_updatePageTitle = false;
</script>
<jsp:include page="../Main/footer.jsp" />
<script>
var myStateArray=[];


var rowNo = 1;
$('#c_custreceiptnoori').blur();
$('#barcode_checker').focus();
var input = document.getElementById("barcode_checker");
 
input.addEventListener("keyup",async  function(event) {
  if (event.keyCode === 13) {
	  getrcpNo();
	  $('#barcode_checker').focus();
  }
});
let custReceiptNoOris = [];
function getrcpNo(){
	var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
	$.get('../../GetSingleReceiptInfoJustForCasesReceivingSRVL?whichscreen=ReceiveAnyShipment&c_custreceiptnoori='+barcodeScanned ,
		
		function(data, status){
			if (status=='success'){
				scannedBefore = false;
				custReceiptNoOris.forEach(function(item, index, array) {
					if(item == barcodeScanned)
						scannedBefore = true;
	    		});
				
				if(scannedBefore){
					generalErrorPrettyMsg('تم جرد الوصل سابقا');
				}else{
					var dataLength = data.length;
					console.log(dataLength);
					console.log(data);
					if(dataLength == 0 ){
						generalErrorPrettyMsg('لا وجود لهذا الوصل '+barcodeScanned);
					}else if (data[0].currentBranch != <%=user.getBranchCode()%> ){
						var notFoundInRequiredStepsMsg = " "+data[0].receiptNo+", في فرع  " + data[0].currentBranchName;
						generalErrorPrettyMsg(notFoundInRequiredStepsMsg);
					}else if (dataLength==1 
							&& (data[0].stepCode !='LIAISONAGT_NEWONWAY'
									&& data[0].stepCode !='READYTOPICKUP'
									&& data[0].stepCode !='READYTOPRINT'
									&& data[0].stepCode !='NEW_ONWAY'
										&& data[0].stepCode !='NEWINSTORE')){
						var notFoundInRequiredStepsMsg = "الوصل رقم "+data[0].receiptNo+", غير قابل للأستلام لأنه في مرحلة "+data[0].stepName;
						generalErrorPrettyMsg(notFoundInRequiredStepsMsg);
					}else if (dataLength==1){
						var notFoundInRequiredStepsMsg = "الوصل رقم "+data[0].receiptNo+", غير قابل للأستلام لأنه لمحافظة ليست لك "+data[0].address;
						if (myStateArray && myStateArray.length>0 && !myStateArray.includes(data[0].state)){
							generalErrorPrettyMsg(notFoundInRequiredStepsMsg);
						}else{
							console.log(myStateArray.length);
							addRow(data[0], barcodeScanned);
						}
					}else if (dataLength>1){
						var table ='<form action="" id="listofcases" class="formName"><div class="table-responsive"><table class="table table-striped">';
						table +="<th>الفرع صاحب الوصل</th>";
						table +="<th>العميل - المتجر</th>";
						table +="<th>مبلغ الوصل</th>";
						table +="<th>رقم الوصل</th>";
						table +="<th>العنوان</th>";
						table +="<th></th>";
						for (i=0; i<(dataLength); i++){
							console.log(data[i].caseId);
							table +="<tr id='row_n_"+i+"'>";
							table +="<td>"+data[i].originatedInBranch+"</td>";
							table +="<td>"+data[i].custName+"</td>";
							table +="<td>"+data[i].receiptamt+"</td>";
							table +="<td>"+data[i].receiptNo+"</td>";
							table +="<td>"+data[i].address+"</td>";
							table +="<td><button type='button'"+ 
							"onclick='$(\"#chosencaseid\").val(\""+i+"\");$(\"form#listofcases\").submit();' class='btn btn-info px-5'><i class='fa fa-check-square'></i></button></td>";
							console.log();
							table +="</tr>";
						}
						table +="</table></div><input type='hidden'  id='chosencaseid'/></form>";
						$.confirm({
							boxWidth: '50%',
						    useBootstrap: false,
					    	title: '',
					    	content: table,
							height :'500',
					    	buttons: {
					        	formSubmit: {
					            	isHidden: true, // hide the button
					            	action: function () {
					            	addRow(data[ $("#chosencaseid").val() ] , barcodeScanned);
					            	}
					        	},
					        cancel: {
					        	text: 'إلغاء',
					        	action : function () {}
					        },
					    },
					    onContentReady: function () {
					        var jc = this;
					        this.$content.find('form').on('submit', function (e) {
					        	e.preventDefault();
					    		jc.$$formSubmit.trigger('click'); // reference the button and click it
							});
						}
					});
				}
			}
		}else{
			alert('error');
		}
	});
	input.value = '';
	$('#barcode_checker').focus();
}

function addRow(data, barcodeScanned){		
	custReceiptNoOris.push(barcodeScanned);
	var dataCell10="<td>";
	dataCell10 +="<button type='button' style='float:left' onclick='remove_row(this,"+barcodeScanned+")' class='btn btn-xs btn-danger'><i class=' fa fa-trash'></i></button>";
	dataCell10 +="</td>";
	var dataCell0 = "<td>"+rowNo+"</td>";
	var dataCell1 = "<td>"+data.custName+"</td>";
	var dataCell2 = "<td>"+data.receiptamt+"</td>";
	var dataCell3 = "<td>"+data.address+"</td>";
	var dataCell4 = "<td>"+data.originatedInBranch+"</td>";
	var dataCell6 = "<td>"+data.hp+"</td>";
	var dataCell7 = "<td>"+data.qty+"</td>";
	var dataCell8 = "<td>"+data.createddt+"</td>";
	var dataCell9 = "<td><input type='text' required readonly='readonly' name='barcodeScanned_"+rowNo+"' value='"+barcodeScanned+"' /></td>";
	var hiddenFields = "<input type='hidden' name='smarty_c_id_hidden_smartyrow_"+rowNo+"' value='"+data.caseid+"'/>";
	hiddenFields += "<input type='hidden' id='q_action_smartyrow_"+rowNo+"' name='q_action_smartyrow_"+rowNo+"' value='"+data.actionNeeded+"'/>";
	hiddenFields += "<input type='hidden' id='q_stage_smartyrow_"+rowNo+"' name='q_stage_smartyrow_"+rowNo+"' value='"+data.stageCode+"'/>";
	hiddenFields += "<input type='hidden' id='q_step_smartyrow_"+rowNo+"' name='q_step_smartyrow_"+rowNo+"' value='"+data.stepCode+"'/>";
	hiddenFields += "<input type='hidden' id='when_scanned_timestamp_smartyrow_"+rowNo+"' name='when_scanned_timestamp_smartyrow_"+rowNo+"' value='"+data.whenItWasScanned+"'/>";
	$('#smartyhiddenmultieditrowsno').val(rowNo);
	console.log($('#smartyhiddenmultieditrowsno').val());
	var row = '<tr>'+dataCell0+dataCell1+dataCell9+dataCell3+dataCell4+dataCell6+dataCell8+dataCell2+dataCell10+hiddenFields+'</tr>';
	$("#main_row_com_app_bussframework_SingleQueue_ReceivingCasesFromDifferentStagesSteps tr:first").after(row);
	rowNo ++;
	input.value = '';
}

function remove_row(that, barcodeScanned) {
	custReceiptNoOris.forEach(function(item, index, array) {
		if(item == barcodeScanned)
			array.splice(index, 1);
	});
	$(that).parent().parent().remove();
	rowNo --;
	$('#smartyhiddenmultieditrowsno').val(rowNo);
	
}

$(document).ready(function() {
	  $(window).keydown(function(event){
	    if(event.keyCode == 13) {
	      event.preventDefault();
	      return false;
	    }
	  });
});

function onGlobalLiaisonAgentSelect(){
	 $('#globalagentselect').val('');
}

function onGlobalDlvAgentSelect(){
	 $('#globalLiaisonAgentSelect').val('');
}

</script>