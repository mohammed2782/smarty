<%@ include file="../Main/Main.jsp"%>
<%@ page import="java.sql.PreparedStatement, java.sql.ResultSet,com.app.bussframework.SingleQueue_INIT_NEWINSTORE_ONLYBRACODE,
java.sql.Connection" %>

<div class="col-sm-2" style="padding-right: 10px;margin-top:20px;">
	<div class="position-relative">
		<input type="text" id ="barcode_checker_NEW_INSTORE" class="form-control ps-5 radius-30" placeholder="بحث عن وصل"> 
			<span class="position-absolute top-50 product-show translate-middle-y">
				<i class="bx bx-search">
				</i>
			</span>
	</div>
</div>
<%
	String pageName2 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SingleQueue_INIT_NEWINSTORE_ONLYBRACODE singleQueue_INIT_NEWINSTORE_ONLYBRACODE = new SingleQueue_INIT_NEWINSTORE_ONLYBRACODE();
	Render(singleQueue_INIT_NEWINSTORE_ONLYBRACODE , out , request, response , Myglobals , objectState , pageName2);
%> 
<script>
smarty_updatePageTitle = false;
</script>
<jsp:include page="../Main/footer.jsp" />
<script>


var rowNo = 1;
$('#c_custreceiptnoori').blur();
$('#barcode_checker_NEW_INSTORE').focus();
var input = document.getElementById("barcode_checker_NEW_INSTORE");
 
input.addEventListener("keyup",async  function(event) {
  if (event.keyCode === 13) {
	  getrcpNo();
	  $('#barcode_checker_NEW_INSTORE').focus();
  }
});



let custReceiptNoOris = [];
function getrcpNo(){

	var barcodeScanned_NEW_INSTORE = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
	$.get('../../getAllReceiptInfoOfSameReceiptNumberInQueueSRVL?ReceiveAnyShipment=assignToAgent&stage=INIT&step=NEWINSTORE&c_custreceiptnoori='+barcodeScanned_NEW_INSTORE ,function(data, status){
		if (status=='success'){
			check = false;
			custReceiptNoOris.forEach(function(item, index, array) {
				if(item == barcodeScanned_NEW_INSTORE)
					check = true;
	    	});
			//console.log(data);
			if (data !== undefined && data.length > 0) {
				if ( 'NEWINSTORE' != data[0].stepCode){
					alert('هذا الوصل ليس في هذه المرحلة');
					
				}else if(check){
					alert('لقد تم ادخال الوصل مسبقا');
				}else{
					var dataLength = data.length;
					if (dataLength==0){
						alert("error");
					}else if (dataLength==1){
						addRow(data[0], barcodeScanned_NEW_INSTORE);
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
							table +="<td>"+barcodeScanned_NEW_INSTORE+"</td>";
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
					            	addRow(data[ $("#chosencaseid").val() ] , barcodeScanned_NEW_INSTORE);
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
			alert('هذا الوصل ليس في هذه المرحلة');
		}
	}else{
		alert('error');
	}
});
input.value = '';
$('#barcode_checker').focus();
}

function addRow(data, barcodeScanned_NEW_INSTORE){
	custReceiptNoOris.push(barcodeScanned_NEW_INSTORE);
	var dataCell10="<td>";
	dataCell10 +="<button type='button' style='float:left' onclick='remove_row(this,"+barcodeScanned_NEW_INSTORE+")' class='btn btn-xs btn-danger'><i class=' fa fa-trash'></i></button>";
	dataCell10 +="</td>";
	var dataCell0 = "<td>"+rowNo+"</td>";
	var dataCell1 = "<td>"+data.custName+"</td>";
	var dataCell2 = "<td>"+data.receiptamt+"</td>";
	var dataCell3 = "<td>"+data.address+"</td>";
	var dataCell4 = "<td>"+data.originatedInBranch+"</td>";
	var dataCell6 = "<td>"+data.hp+"</td>";
	var dataCell7 = "<td>"+data.qty+"</td>";
	var dataCell8 = "<td>"+data.createddt+"</td>";
	var dataCell9 = "<td><input type='text' required readonly='readonly' name='barcodeScanned_NEW_INSTORE"+rowNo+"' value='"+barcodeScanned_NEW_INSTORE+"' /></td>";
	
	var hiddenFields = "<input type='hidden' name='smarty_c_id_hidden_smartyrow_"+rowNo+"' value='"+data.caseid+"'/>";
	hiddenFields += "<input type='hidden'  id='c_assignedagent_smartyrow_"+rowNo+"' name='c_assignedagent_smartyrow_"+rowNo+"' value=''/>";
	hiddenFields += "<input type='hidden' name='q_action_smartyrow_"+rowNo+"' value='ASSGN_AGENT'/>";
	$('#smartyhiddenmultieditrowsno').val(rowNo);
	console.log($('#smartyhiddenmultieditrowsno').val());
	var row = '<tr>'+dataCell0+dataCell1+dataCell2+dataCell3+dataCell4+dataCell6+dataCell8+dataCell9+dataCell10+hiddenFields+'</tr>';
	$("#smarty_table_com_dot_app_dot_bussframework_dot_SingleQueue_INIT_NEWINSTORE_ONLYBRACODE tr:first").after(row);
	rowNo ++;
	input.value = '';
}
function remove_row(that, barcodeScanned_NEW_INSTORE) {
	custReceiptNoOris.forEach(function(item, index, array) {
		if(item == barcodeScanned_NEW_INSTORE)
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
$(document).ready(function() {
	$("#globalagentselect").on('change', function() {
		var globalAgentid = $("#globalagentselect").val();
		for (i = 1 ; i <= $('#smartyhiddenmultieditrowsno').val() ; i++){
			$('#c_assignedagent_smartyrow_'+i).val(globalAgentid);
		}
	});
});

</script>