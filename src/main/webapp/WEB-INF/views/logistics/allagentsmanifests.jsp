<%@ include file="../Main/Main.jsp"%>
<%@ page import="java.sql.PreparedStatement, java.sql.ResultSet,com.app.bussframework.AllDlvAagentManifests, java.sql.Connection" %>

<%
	AllDlvAagentManifests adamf = new AllDlvAagentManifests();
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	Render(adamf, out , request, response , Myglobals , objectState , pageName1);
%> 

<jsp:include page="../Main/footer.jsp" />
<script>
$(document).ready(function() {
	$('[id^=q_action_smartyrow_]').each(function() {
		 var number = this.id.split('_').pop();
		 change_instorage_action(number);
	});
	
});
function change_instorage_action( seq){
	if ($('#q_action_smartyrow_'+seq).val()=='ASSIGN_LIASIONAGT'){
		$('#c_assignedagent_smartyrow_'+seq).attr("disabled", true); 
		$('#c_assignedagent_smartyrow_'+seq).css("display", "none"); 
		$('#q_assigned_to_smartyrow_'+seq).removeAttr("disabled");
		$('#q_assigned_to_smartyrow_'+seq).css("display", "block"); 
	}else{
		$('#c_assignedagent_smartyrow_'+seq).removeAttr("disabled");
		$('#c_assignedagent_smartyrow_'+seq).css("display", "block"); 
		$('#q_assigned_to_smartyrow_'+seq).attr("disabled", true);
		$('#q_assigned_to_smartyrow_'+seq).css("display", "none"); 
	}
}

var returnedAll = 'N';
var deliverAll = 'N';
var archiveAllRtn = 'N';

function changeToArchiveAll(action){
	$('[id^=q_action_smartyrow_]').each(function() {
	    var number = this.id.split('_').pop();
	    if (archiveAllRtn =='N'){
	    	$('#q_action_smartyrow_'+number).val('ARCHV');
	    	
	    }else{
	    	$('#q_action_smartyrow_'+number).val('');
	    	
	    }
	});
	if (archiveAllRtn =='N'){
		archiveAllRtn = 'Y';
	}else{
		archiveAllRtn = 'N';
	}
}

function changeToRecievedAll(action){
	
	$('[id^=q_action_smartyrow_]').each(function() {
	    var number = this.id.split('_').pop();
	    if (deliverAll =='N'){
	    	$('#q_action_smartyrow_'+number).val('SUCS_DLV');
	    	 $("#q_action_smartyrow_"+number).css('backgroundColor','#2f2fd2');
	    	 $("#q_action_smartyrow_"+number).css('color','white');
	    }else{
	    	$('#q_action_smartyrow_'+number).val('');
	    	 $("#q_action_smartyrow_"+number).css('backgroundColor','');
	    	 $("#q_action_smartyrow_"+number).css('color','');
	    }
	});
	if (deliverAll =='N'){
		deliverAll = 'Y';
	}else{
		deliverAll = 'N';
	}
}

function changeActionReturnedAll(action){
	
	$('[id^=q_action_smartyrow_]').each(function() {
	    var number = this.id.split('_').pop();
	    if (returnedAll =='N'){
	    	$('#q_action_smartyrow_'+number).val('RETURNED_TO_SNDR');
	    }else{
	    	$('#q_action_smartyrow_'+number).val('');
	    }
	});
	if (returnedAll =='N'){
		returnedAll = 'Y';
	}else{
		returnedAll = 'N';
	}
}
$('#c_custreceiptnoori').focus();

function change_q_actionColor(that, seq){
	 var value = $("#q_action_smartyrow_"+seq).val();
	 if (value == 'SUCS_DLV' || value == 'SUCS_DLV_CHANGEAMT'){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#2f2fd2');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 if (value == 'SUCS_DLV_CHANGEAMT'){ // if successfully delviered and receipt amount have to change then show field
			 $("#new_receiptamt_smartyrow_"+seq).css('display','block');
			 $("#q_action_smartyrow_"+seq).css('backgroundColor',' rgb(170 173 16)');
		 }else{
			 $("#new_receiptamt_smartyrow_"+seq).css('display','none');
		 }
	 }else if (value ==''){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#F0FFF0');
		 $("#q_action_smartyrow_"+seq).css('color','black');
		 $("#new_receiptamt_smartyrow_"+seq).css('display','none');
	 }else {
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#f57b7b');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 $("#new_receiptamt_smartyrow_"+seq).css('display','none');
		}
	}


function getrcpNo(){

	var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
	$.get('../GetSingleReceiptInfoSRVL?stage=init&step=NEW_ONWAY&c_custreceiptnoori='+barcodeScanned ,function(data, status){
		if (status=='success'){
			if ( 0 >= data[0].q_id){
				alert('هذا الوصل غير متوفر في هذه المرحلة');
			}else{
				var dataCell10="<td><select class='form-control' required id='c_rural_smartyrow_"+rowNo+"' name='c_rural_smartyrow_"+rowNo+"'> ";
				dataCell10 +="<option value='N'>لا</option><option value='Y'>نعم</option></select>";
				dataCell10 +="<button type='button' style='float:left' onclick='remove_row(this)' class='btn btn-xs btn-danger'><i class=' fa fa-trash'></i></button>";
				dataCell10 +="</td>";
				var dataCell0 = "<td>"+rowNo+"</td>";
				var dataCell1 = "<td>"+data[0].custName+"</td>";
				var dataCell2 = "<td>"+data[0].receiptamt+"</td>";
				var dataCell3 = "<td>"+data[0].address+"</td>";
				var dataCell4 = "<td>"+data[0].caseid+"</td>";
				var dataCell5 = "<td>"+data[0].rcvname+"</td>";
				var dataCell6 = "<td>"+data[0].hp+"</td>";
				var dataCell7 = "<td>"+data[0].qty+"</td>";
				var dataCell8 = "<td>"+data[0].createddt+"</td>";
				var dataCell9 = "<td><input type='text' required readonly='readonly' name='barcodescanned_"+rowNo+"' value='"+barcodeScanned+"' /></td>";
				var hiddenFields = "<input type='hidden' name='smarty_q_id_hidden_smartyrow_"+rowNo+"' value='"+data[0].q_id+"'/>";
				$('#numberofrowsscanned').val(rowNo);
				console.log($('#numberofrowsscanned').val());
				var row = '<tr>'+dataCell0+dataCell1+dataCell2+dataCell3+dataCell4+dataCell5+dataCell6+dataCell7+dataCell8+dataCell9+dataCell10+hiddenFields+'</tr>';
				$("#smarty_table_com_dot_app_dot_bussframework_dot_SingleQueue_Init_NEW_ONWAY tr:first").after(row);
				rowNo ++;
			}
		}else{
			alert('error');
		}
	});
	  input.value = '';
}
function remove_row(that) {
	
	$(that).parent().parent().remove();
	rowNo --;
	$('#numberofrowsscanned').val(rowNo);
}

</script>