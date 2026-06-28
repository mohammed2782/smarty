<%@page import="com.app.util.UtilitiesFeqar"%>
<%@ include file="../Main/Main-popup.jsp"%>
<%@ page autoFlush="true" buffer="2048kb"%>
<%@ page import="java.text.DecimalFormat,com.app.util.Utilities,
java.sql.PreparedStatement,java.sql.ResultSet, com.app.bussframework.*, com.app.bussframework.SingleQueue_AGENTOP_FollowedUp"%>
<%
String load = request.getParameter("load");
if (load != null) {
	Myglobals.smartyGlobalsAssArr.put("my_tab_load_bookmarked", (String) load);
} else if (Myglobals.smartyGlobalsAssArr.containsKey("my_tab_load_bookmarked")
		&& Myglobals.smartyGlobalsAssArr.get("my_tab_load_bookmarked") != null) {
	load = (String) Myglobals.smartyGlobalsAssArr.get("my_tab_load_bookmarked");
}
if (load!=null && load.equalsIgnoreCase("doLoad")){
	System.out.println("------------"+request.getParameter("load"));
	HashMap<String, StepBean> stepsAndActions = null;
	stepsAndActions = (HashMap<String, StepBean>) Myglobals.smartyGlobalsAssArr.get("stepsAndActions_Global");
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SingleQueue_AGENTOP_HaveBookMark singleQueue_AGENTOP_HaveBookMark = new SingleQueue_AGENTOP_HaveBookMark();
    singleQueue_AGENTOP_HaveBookMark.setStepsAndActions(stepsAndActions);
    Render(singleQueue_AGENTOP_HaveBookMark, out , request, response , Myglobals , objectState , pageName1);
}
%>
<%@ include file="../Main/footer-popup.jsp"%>
<script>


function forceDlv(caseId){
	$("#loading").css("display", "flex");
	Swal.fire({
		title: 'سبب اعتباره واصل',
		input: 'text',
	    showCancelButton: true,
	    //confirmButtonColor: "#1FAB45",
	    confirmButtonText: "اعتباره واصل",
	    cancelButtonText: "الغاء",
	    buttonsStyling: true,
		  inputAttributes: {
		    id: 'force_dlv_rmk'
		  },
	}).then((result)=> {
			if (result.isConfirmed){
				var aRmk = $('#force_dlv_rmk').val();
				if (aRmk.trim()) {
					console.log("aRmk--->"+aRmk);
					confirmedForceDlv (caseId, aRmk);
				}else{
					$('#force_dlv_rmk').addClass('is-invalid');
				    Swal.fire({title: 'يجب أدخال السبب',confirmButtonText: 'نعم'});
				}
			}else{
				;
			}
		}, 
	{});
}

function confirmedForceDlv(caseId, rmk){
	var userId = <%=user.getUsid()%>;
	var dataToSend = {caseid : caseId, q_rmk :  rmk };
	$.post('../../ForcePushToCompulsaryDlvSRVL' , dataToSend, function(data, status){ 
		if (status=='success'){
			$.alert({
			    title: '',
			    content: 'تم أعتباره واصل',
			});
			hideLoader();
		}else{
			alert("Error, please contact Mohammed Nafie");
			hideLoader();
		}
 	});
}

function change_q_actionColor(that, seq ){
	change_q_actionColor(that, seq , '');
}

function change_q_actionColor(that, seq , className){
	 var value = $("#q_action_smartyrow_"+seq+""+className).val();
	 $("#rtn_qty_smartyrow_"+seq+""+className ).prop( "required", false );
	 $("#rtn_qty_smartyrow_"+seq+""+className ).css('display','none');
	 $("#trreturnreasons_"+seq+""+className).css('display','none');//RTN REASON
	 $("#c_rtnreason_smartyrow_"+seq+""+className ).prop( "disabled", true );
	 $("#c_rtnreason_smartyrow_"+seq+""+className).prop('required',false);
	 
	 $("#trnew_receiptamtrtn_smartyrow_"+seq+""+className).css('display','none'); // receipt amt
	 $( "#new_receiptamtrtn_smartyrow_"+seq+""+className ).prop( "disabled", true );
	 $( "#new_receiptamtrtn_smartyrow_"+seq+""+className ).prop( "required", false );
	 
	 $("#trq_postponedto_smartyrow_"+seq+""+className).css('display','none'); // postponed 
	 $( "#q_postponedto_smartyrow_"+seq+""+className ).prop( "disabled", true );
	 $("#q_postponedto_smartyrow_"+seq+""+className).prop('required',false);
	 $( "#q_postponedoption_smartyrow_"+seq+""+className ).prop( "disabled", true );
	 $("#q_postponedoption_smartyrow_"+seq+""+className).prop('required',false);
	 
	 
	 if (value == 'SUCCDLV' || value == 'SUCS_DLV_CHANGEAMT' || value == 'PART_SUCC'){
		 $("#q_action_smartyrow_"+seq+""+className).css('backgroundColor','#037656');
		 $("#q_action_smartyrow_"+seq+""+className).css('color','white');
		 if (value == 'SUCS_DLV_CHANGEAMT' || value == 'PART_SUCC'){ // if successfully delviered and receipt amount or partial delivered have to change then show field
			 $("#trnew_receiptamtrtn_smartyrow_"+seq+""+className).css('display','block');
			 $("#new_receiptamtrtn_smartyrow_"+seq+""+className ).prop( "disabled", false );
			 $( "#new_receiptamtrtn_smartyrow_"+seq+""+className ).prop( "required", true );
			 $("#q_action_smartyrow_"+seq+""+className).css('backgroundColor','#1ea57f');
			 if (value == 'PART_SUCC'){
				 $("#rtn_qty_smartyrow_"+seq+""+className).css('display','block');
				 $("#rtn_qty_smartyrow_"+seq+""+className ).prop( "required", true );
			 }
			 
		 }
	 }else if (value == 'RTN_WITHSHP_CHARGE_SNDR' || value == 'RTN_TOSTORE' || value=='RTN_WTIHAGENT'){
		
		 $("#q_action_smartyrow_"+seq+""+className).css('backgroundColor','#a70328');
		 $("#q_action_smartyrow_"+seq+""+className).css('color','white');
		 $("#trreturnreasons_"+seq+""+className).css('display','block');//RTN REASON
		 $("#c_rtnreason_smartyrow_"+seq+""+className ).prop( "disabled", false );
		 $("#c_rtnreason_smartyrow_"+seq+""+className).prop('required',true);
		 $("#returnreasons_"+seq+""+className+" td select").css('backgroundColor','rgb(247 249 168)');
		 
	 }else if (value =='POSTPONED'){
		 $("#q_action_smartyrow_"+seq+""+className).css('backgroundColor','#05b6b1');
		 $("#q_action_smartyrow_"+seq+""+className).css('color','white');
		 $("#trq_postponedto_smartyrow_"+seq+""+className).css('display','block'); // postponed 
		 $( "#q_postponedto_smartyrow_"+seq+""+className ).prop( "disabled", false );
		 $( "#q_postponedto_smartyrow_"+seq+""+className ).prop( "required", true );
		 $( "#q_postponedoption_smartyrow_"+seq+""+className ).prop( "disabled", false );
		 $("#q_postponedoption_smartyrow_"+seq+""+className).prop('required',true);
	 }else if (value == 'RTN_FROMAGENTTOSTORE'){
		 $("#q_action_smartyrow_"+seq+""+className).css('backgroundColor','#a70328');
		 $("#q_action_smartyrow_"+seq+""+className).css('color','white');
	 }else if (value == 'RETRYDLV'){
		 $("#q_action_smartyrow_"+seq+""+className).css('backgroundColor','#9D7302');
		 $("#q_action_smartyrow_"+seq+""+className).css('color','white');
	 }else if (value =='CORRECT_STATUS'){
		 $("#q_action_smartyrow_"+seq+""+className).css('backgroundColor','rgb(10 45 121)');
		 $("#q_action_smartyrow_"+seq+""+className).css('color','white'); 
	 }else {
		 $("#q_action_smartyrow_"+seq+""+className).css('backgroundColor','rgb(5 108 138)');
		 $("#q_action_smartyrow_"+seq+""+className).css('color','white');
	}
}
</script>