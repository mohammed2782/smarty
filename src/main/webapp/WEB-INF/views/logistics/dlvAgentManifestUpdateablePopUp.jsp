<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.bussframework.DlvAgentManifestUpdateablePopUp, java.sql.PreparedStatement, java.sql.ResultSet,com.app.util.Utilities" %>
<%   

String dlvagent_manifestid = (String)request.getParameter("dlvagent_manifestid");

if (dlvagent_manifestid !=null){
	Myglobals.smartyGlobalsAssArr.put("dlvagentManifestUpdateablePopUp_manifestid", (String)dlvagent_manifestid);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("dlvagentManifestUpdateablePopUp_manifestid") && Myglobals.smartyGlobalsAssArr.get("dlvagentManifestUpdateablePopUp_manifestid")!=null){
	dlvagent_manifestid = (String)Myglobals.smartyGlobalsAssArr.get("dlvagentManifestUpdateablePopUp_manifestid");
}
/* Connection conn = null;
Utilities ut = new Utilities();
HashMap<String, String> info = new HashMap<String, String>();
try{
	conn = mysql.getConn();
	info = ut.getAgentInfo(conn, customersusid);
}catch(Exception e){
	e.printStackTrace();
}finally{
	try{conn.close();}catch(Exception e){}
} */
%>
<%-- <div class="row">
<div class="col-md-12">
          <div class="panel panel-warning">
            <div class="panel-heading">
            	<div class='row'>
		            <div class='col-xs-5'>
		              <h5><%=info.get("name") %></h5>
		            </div>
		            <div class='col-xs-5'>
		              <h5></h5>
		            </div>
		            
	            </div>
            </div>
        </div>
	</div>
</div> --%>
<%
String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
DlvAgentManifestUpdateablePopUp dlvAgentManifestUpdateablePopUp = new DlvAgentManifestUpdateablePopUp(Integer.parseInt(dlvagent_manifestid), user.getRank_code()); 
Render(dlvAgentManifestUpdateablePopUp  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />
<script>
function change_q_actionColor(that, seq){
	 var value = $("#q_action_smartyrow_"+seq).val();
	 $("#rtn_qty_smartyrow_"+seq ).prop( "required", false );
	 $("#rtn_qty_smartyrow_"+seq ).css('display','none');
	 $("#trreturnreasons_"+seq).css('display','none');//RTN REASON
	 $("#c_rtnreason_smartyrow_"+seq ).prop( "disabled", true );
	 $("#c_rtnreason_smartyrow_"+seq).prop('required',false);
	 
	 $("#trnew_receiptamtrtn_smartyrow_"+seq).css('display','none'); // receipt amt
	 $( "#new_receiptamtrtn_smartyrow_"+seq ).prop( "disabled", true );
	 $( "#new_receiptamtrtn_smartyrow_"+seq ).prop( "required", false );
	 
	 $("#trq_postponedto_smartyrow_"+seq).css('display','none'); // postponed 
	 $( "#q_postponedto_smartyrow_"+seq ).prop( "disabled", true );
	 $("#q_postponedto_smartyrow_"+seq).prop('required',false);
	 $( "#q_postponedoption_smartyrow_"+seq ).prop( "disabled", true );
	 $("#q_postponedoption_smartyrow_"+seq).prop('required',false);
	 
	 
	 if (value == 'SUCCDLV' || value == 'SUCS_DLV_CHANGEAMT' || value == 'PART_SUCC'){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#037656');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 if (value == 'SUCS_DLV_CHANGEAMT' || value == 'PART_SUCC'){ // if successfully delviered and receipt amount or partial delivered have to change then show field
			 $("#trnew_receiptamtrtn_smartyrow_"+seq).css('display','block');
			 $("#new_receiptamtrtn_smartyrow_"+seq ).prop( "disabled", false );
			 $( "#new_receiptamtrtn_smartyrow_"+seq ).prop( "required", true );
			 $("#q_action_smartyrow_"+seq).css('backgroundColor','#1ea57f');
			 if (value == 'PART_SUCC'){
				 $("#rtn_qty_smartyrow_"+seq).css('display','block');
				 $("#rtn_qty_smartyrow_"+seq ).prop( "required", true );
			 }
			 
		 }
	 }else if (value == 'RTN_WITHSHP_CHARGE_SNDR' || value == 'RTN_TOSTORE' || value=='RTN_WTIHAGENT'){
		
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#a70328');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 $("#trreturnreasons_"+seq).css('display','block');//RTN REASON
		 $("#c_rtnreason_smartyrow_"+seq ).prop( "disabled", false );
		 $("#c_rtnreason_smartyrow_"+seq).prop('required',true);
		 $("#returnreasons_"+seq+" td select").css('backgroundColor','rgb(247 249 168)');
		 
	 }else if (value =='POSTPONED'){

		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#05b6b1');
		 $("#q_action_smartyrow_"+seq).css('color','white');
		 $("#trq_postponedto_smartyrow_"+seq).css('display','block'); // postponed 
		 $( "#q_postponedto_smartyrow_"+seq ).prop( "disabled", false );
		 $( "#q_postponedto_smartyrow_"+seq ).prop( "required", true );
		 $( "#q_postponedoption_smartyrow_"+seq ).prop( "disabled", false );
		 $("#q_postponedoption_smartyrow_"+seq).prop('required',true);
	 }else if (value == 'RTN_FROMAGENTTOSTORE'){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#a70328');
		 $("#q_action_smartyrow_"+seq).css('color','white');
	 }else if (value = 'RETRYDLV'){
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#9D7302');
		 $("#q_action_smartyrow_"+seq).css('color','white');
	 }else {
		 $("#q_action_smartyrow_"+seq).css('backgroundColor','#f57b7b');
		 $("#q_action_smartyrow_"+seq).css('color','white');
	}
}

</script>