<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.returnables.IsolateReceiptsByBarcode" %> 
<div class="row"><div class="col-sm-2 col-sm-offset-1"><label>Barcode</label></div><div class="col-sm-9">
	<input type='text' style='color:#424242;background-color:#E9E5E5;' id ='barcode_checker' />
	</div></div>
<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	IsolateReceiptsByBarcode isolateReceiptsByBarcode = new IsolateReceiptsByBarcode(); 
	Render(isolateReceiptsByBarcode , out , request, response , Myglobals , objectState , pageName1);
%>
<%@ include file="../Main/footer.jsp"%>  
<script>
$('#barcode_checker').focus();
var input = document.getElementById("barcode_checker");
var rowNo = 1;
var currentBranch = <%=user.getBranchCode()%>;

input.addEventListener("keyup", function(event) {
  if (event.keyCode === 13) {
	  console.log(currentBranch);
	  var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
	  $.get('../../GetReturnableReceiptsSRVL?userBranchCode='+currentBranch+'&c_custreceiptnoori='+barcodeScanned ,function(data, status){
			if (status=='success'){
				console.log(data);
				var dataLength = data.length;
				if (dataLength==0){
					alert("error");
				}else if (dataLength==1){
					
					addRow(data[0]);
					
					
				}if (dataLength>1){
					var table ='<form action="" id="listofcases" class="formName"><div class="table-responsive"><table class="table table-striped">';
					
					table +="<th>الفرع صاحب الوصل</th>";
					table +="<th>العميل - المتجر</th>";
					table +="<th>مبلغ الوصل</th>";
					table +="<th>رقم الوصل</th>";
					table +="<th>العنوان</th>";
					table +="<th></th>";
					for (i=0; i<(dataLength); i++){
						console.log(data[i].caseId);
						table +="<tr id='row_n_"+i+"' >";
						table +="<td>"+data[i].originatingBranchName+"</td>";
						table +="<td>"+data[i].masterCustomerName+"</td>";
						table +="<td>"+data[i].receiptAmt+"</td>";
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
					            	addRow(data[ $("#chosencaseid").val() ]);
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
			}else{
				alert('error');
			}
		});
	  input.value = '';
	  $('#barcode_checker').focus();
  }
});

function addRow(singleCase){
	if (singleCase.agentrtnid ==0){
		generalErrorPrettyMsg( "لم يتم تصفيته مع المندوب ");
		return;
	}
	var backGroundColor = "";
	if (singleCase.stageCode=='DLV')
		backGroundColor = "background-color:#150a2b; ";
	var dataCell0 = "<td>"+rowNo+"</td>";
	var dataCell1 = "<td id = '"+singleCase.receiptNo+"' >"+singleCase.receiptNo+"</td>";
	var dataCell2 = "<td>"+singleCase.originatingBranchName+"</td>";
	var dataCell3 = "<td>"+singleCase.masterCustomerName+"</td>";
	var dataCell4 = "<td>"+singleCase.address+"</td>";
	var dataCell15 = "<td>"+singleCase.partialRtnQty+"</td>";
	
	var dataCell5 = "<td>"+singleCase.receiptAmt+"</td>";
	var dataCell6 ="<td><button type='button' style='float:left' onclick='remove_row(this)' class='btn btn-xs btn-danger'>"+
		"<i class=' fa fa-trash'></i></button>";
	dataCell6 +="</td>";
	var hiddenFields =  "<input type='hidden' name='c_id_row_"+rowNo+"' value='"+singleCase.caseId+"'/>";
	hiddenFields +=  "<input type='hidden' name='q_stagecode_row_"+rowNo+"' value='"+singleCase.stageCode+"'/>";
	hiddenFields +=  "<input type='hidden' name='q_stepcode_row_"+rowNo+"' value='"+singleCase.stepcode+"'/>";
	hiddenFields +=  "<input type='hidden' name='partialRtnCCToBranch_row_"+rowNo+"' value='"+singleCase.partialRtnCCToBranch+"'/>";
	$('#numberofrowsscanned').val(rowNo);
	console.log($('#numberofrowsscanned').val());
	var row = '<tr style=\"'+backGroundColor+' color:black;\" >'+dataCell0+dataCell1+dataCell2+dataCell3+dataCell4+dataCell15+dataCell5+dataCell6+hiddenFields+'</tr>';
	$("#smarty_table_com_dot_app_dot_returnables_dot_IsolateReceiptsByBarcode tr:first").after (row);
	rowNo ++;
}

function remove_row(that) {
	
	$(that).parent().parent().remove();

}

</script>