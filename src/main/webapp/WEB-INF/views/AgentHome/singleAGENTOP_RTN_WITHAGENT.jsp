<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.agent.home.SingleAGENTOP_ALLOPS" %> 

<%	
Myglobals.smartyGlobalsAssArr.put("stp_code", (String)"RTN_WITHAGENT");
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
SingleAGENTOP_ALLOPS sar = new SingleAGENTOP_ALLOPS();
 	Render(sar  , out , request, response , Myglobals , objectState , pageName1); 
%> 
<%@ include file="../Main/footer.jsp"%>

<script>
$('#c_custreceiptnoori').focus();
function change_q_actionColor(that, seq){
	 var value = $("#q_action_smartyrow_"+seq).val();
	 $("#rtn_qty_smartyrow_"+seq ).prop( "required", false );
	 $("#rtn_qty_smartyrow_"+seq ).css('display','none');
	 $("#trreturnreasons_"+seq).css('display','none');//RTN REASON
	 $("#c_rtnreason_smartyrow_"+seq ).prop( "disabled", true );
	 $("#c_rtnreason_smartyrow_"+seq).prop('required',false);
	 
	 $("#trnew_receiptamtrtn_smartyrow_"+seq).css('display','none'); // receipt amt
	 $("#new_receiptamtrtn_smartyrow_"+seq ).prop( "disabled", true );
	 $("#new_receiptamtrtn_smartyrow_"+seq ).prop( "required", false );
	 	 
	 
	 if (value == 'SUCCDLV' || value == 'SUCS_DLV_CHANGEAMT' || value == 'PART_SUCC'){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#037656');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 if (value == 'SUCS_DLV_CHANGEAMT' || value == 'PART_SUCC'){ // if successfully delviered and receipt amount or partial delivered have to change then show field
			 $("#trnew_receiptamtrtn_smartyrow_"+seq).css('display','block');
			 $("#new_receiptamtrtn_smartyrow_"+seq ).prop( "disabled", false );
			 $("#new_receiptamtrtn_smartyrow_"+seq ).prop( "required", true );
			 $("#q_action_smartyrow_"+seq).css('backgroundColor','#1ea57f');
			 if (value == 'PART_SUCC'){
				 $("#rtn_qty_smartyrow_"+seq).css('display','block');
				 $("#rtn_qty_smartyrow_"+seq ).prop( "required", true );
			 }
			 
		 }
	 }else {
		$("#q_action_smartyrow_"+seq).css('backgroundColor','#f57b7b');
		$("#q_action_smartyrow_"+seq).css('color','white');
	}
}


</script>