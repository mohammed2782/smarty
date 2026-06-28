<%@page import="com.app.util.UtilitiesFeqar"%>
<%@ include file="../Main/Main.jsp"%>
<%@ page autoFlush="true" buffer="2048kb"%>
<%@ page import="java.text.DecimalFormat,com.app.util.Utilities,
java.sql.PreparedStatement,java.sql.ResultSet, com.app.bussframework.*, com.app.bussframework.SingleQueue_AGENTOP_FollowedUp"%>
<%
Connection conn1 = null;
PreparedStatement pst = null;
ResultSet rs = null;
DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
HashMap<String, StepBean> stepsAndActions = null;
LinkedHashMap<Integer, String> agentsSupervisorsLists = new LinkedHashMap<Integer, String>();
List<String> selectedUsersAccountslist = null;
String selectedAgentsSuperVisiorsCommaSeperated = user.getUsid()+"";
if(request.getParameter("superVisorsList")!=null 
	&& request.getParameter("superVisiorsSearch")!=null && request.getParameter("superVisiorsSearch").equalsIgnoreCase("1")){
	selectedUsersAccountslist = new ArrayList<>(Arrays.asList(request.getParameterValues("superVisorsList")));
	selectedAgentsSuperVisiorsCommaSeperated = user.getUsid()+","+String.join(",", request.getParameterValues("superVisorsList"));
}else{
	if (request.getParameter("superVisiorsSearch")!=null && request.getParameter("superVisiorsSearch").equalsIgnoreCase("1")){
		selectedUsersAccountslist = new ArrayList<>();
		selectedAgentsSuperVisiorsCommaSeperated = user.getUsid()+"";
	}
}
if (selectedAgentsSuperVisiorsCommaSeperated != null) {
	Myglobals.smartyGlobalsAssArr.put("selectedAgentsSuperVisiorsCommaSeperated", (String) selectedAgentsSuperVisiorsCommaSeperated);
} else if (Myglobals.smartyGlobalsAssArr.containsKey("selectedAgentsSuperVisiorsCommaSeperated")
		&& Myglobals.smartyGlobalsAssArr.get("selectedAgentsSuperVisiorsCommaSeperated") != null) {
	selectedAgentsSuperVisiorsCommaSeperated = (String) Myglobals.smartyGlobalsAssArr.get("selectedAgentsSuperVisiorsCommaSeperated");
}
if (selectedUsersAccountslist !=null){
	Myglobals.smartyGlobalsAssArr.put("selectedUsersAccountslist", (List<String>) selectedUsersAccountslist);
} else if (Myglobals.smartyGlobalsAssArr.containsKey("selectedUsersAccountslist")
		&& Myglobals.smartyGlobalsAssArr.get("selectedUsersAccountslist") != null) {
	selectedUsersAccountslist = (List<String>) Myglobals.smartyGlobalsAssArr.get("selectedUsersAccountslist");
}

if (stepsAndActions !=null){
	Myglobals.smartyGlobalsAssArr.put("stepsAndActions_Global", (HashMap<String, StepBean>) stepsAndActions);
} else if (Myglobals.smartyGlobalsAssArr.containsKey("stepsAndActions_Global")
		&& Myglobals.smartyGlobalsAssArr.get("stepsAndActions_Global") != null) {
	stepsAndActions = (HashMap<String, StepBean>) Myglobals.smartyGlobalsAssArr.get("stepsAndActions_Global");
}
try {
	conn1 = mysql.getConn();
	if (stepsAndActions==null){
		stepsAndActions =  Utilities.getStepsInStage(conn1, "AGENTOP", user.getRank_code());
		Myglobals.smartyGlobalsAssArr.put("stepsAndActions_Global", (HashMap<String, StepBean>) stepsAndActions);
	}
	agentsSupervisorsLists = Utilities.getAgentsSupervisorsList(conn1, user.getUsid());
} catch (Exception e) {
	e.printStackTrace();
} finally {
	try {rs.close();} catch (Exception e) {}
	try {pst.close();} catch (Exception e) {}
	try {conn1.close();} catch (Exception e) {}
}
%>
<form action="?superVisiorsSearch=1" method="post" name="search_superVisors_form">
	<div class='row'>
		<div class = 'col-5'>
			<select class='single-select' id='superVisorsList' class='multiple-select' multiple='multiple' 
			style="width: 200px;" name='superVisorsList'>
			<%for (Integer empId : agentsSupervisorsLists.keySet()) {
				if (selectedUsersAccountslist != null 
						&& selectedUsersAccountslist.contains(empId+"")) {%>
					<option value='<%=empId%>' selected><%=agentsSupervisorsLists.get(empId)%></option>
				<%} else {%>
					<option value='<%=empId%>'><%=agentsSupervisorsLists.get(empId)%></option>
				<%}%>
			<%}%>
			</select>
		</div>
		<div class = 'col-4'>
			<button type='submit' class="btn btn-primary btn-darken-4" style='margin-right: 10px; background: #623da5 !important;'
					type="button">عرض الشحنات الخاصة بالموظفين<i class="fa fa-search m-right-xs"></i>
			</button>
		</div>
</div>
</form>
<!-- <div class='row'>
	<div class='col-7'>
	</div>
	<div class='col-5'>
		<div class ='row'>
			<div class='col-6'>
			<span class="form-check-label" for="active_safe">كل المندوبين المسندين لي</span>
				<label style ='height:20px;' class="switch">	
						<input class="switch-input" type="checkbox" id="active_safe" onclick="toggleAllMyAssignedDlvAgent(this, '1');">	
						<span class="switch-label" data-on="On" data-off="Off"></span> 
						<span  style='height:18px;width:18px;' class="switch-handle"></span> 
						
				</label>
			</div>
			
			<div class='col-6'>
			<span class="form-check-label" for="active_safe">الكل</span>
				<label style ='height:20px;' class="switch">	
						<input class="switch-input" type="checkbox" id="active_safe" onclick="activeSafe(this, '1');">	
						<span class="switch-label" data-on="On" data-off="Off"></span> 
						<span  style='height:18px;width:18px;' class="switch-handle"></span> 
						
				</label>
			</div>
			
		</div>
		<div class ='row'>
			<div class='col-3'>
				<label style ='height:20px;' class="switch">	
						<input class="switch-input" type="checkbox" id="active_safe" onclick="activeSafe(this, '1');">	
						<span class="switch-label" data-on="On" data-off="Off"></span> 
						<span  style='height:18px;width:18px;' class="switch-handle"></span> 
						
				</label>
			</div>
			<div class='col-3'>
				<span class="form-check-label" for="active_safe">كل المندوبين المسندين لي</span>
			</div>
		
			<div class='col-3'>
				<label style ='height:20px;' class="switch">	
						<input class="switch-input" type="checkbox" id="active_safe" onclick="activeSafe(this, '1');">	
						<span class="switch-label" data-on="On" data-off="Off"></span> 
						<span  style='height:18px;width:18px;' class="switch-handle"></span> 
						
				</label>
			</div>
			<div class='col-3'>
				<span class="form-check-label" for="active_safe">كل الفروع</span>
			</div>
		</div>
	</div>
</div> -->
<div class='row'>
<div class="col">
	<hr />
	<div class="card">
		<div class="card-body">
			
			<ul class="nav nav-pills mb-3" role="tablist" style='font-size: 17px;'>
				<li class="nav-item" role="presentation"><a class="nav-link"
					onclick='changeActiveTab("under-dlv");reloadIframeUnderRevision("under-dlv-iframe");' id='a-under-dlv'
					data-bs-toggle="pill" href="#under-dlv" role="tab"
					aria-selected="false">
						<div class="d-flex align-items-center">
							<div class="tab-icon">
								<i class='lni lni-car-alt font-18 me-1'></i>
							</div>
							<div class="tab-title">حاليا قيد التوصيل</div>
						</div>
				</a></li>
				<li class="nav-item" role="presentation"><a class="nav-link"
					onclick='changeActiveTab("under-revision");reloadIframeUnderRevision("under-revision-iframe");' id='a-under-revision'
					data-bs-toggle="pill" href="#under-revision" role="tab"
					aria-selected="true">
						<div class="d-flex align-items-center">
							<div class="tab-icon">
								<i class='lni lni-zoom-in font-18 me-1'></i>
							</div>
							<div class="tab-title">مطلوب تدقيقها</div>
						</div>
				</a></li>
				<li class="nav-item" role="presentation"><a class="nav-link"
					onclick='changeActiveTab("reviewed");reloadIframeUnderRevision("reviewed-iframe");' id='a-reviewed'
					data-bs-toggle="pill" href="#reviewed" role="tab"
					aria-selected="false">
						<div class="d-flex align-items-center">
							<div class="tab-icon">
								<i class='lni lni-checkmark-circle font-18 me-1'></i>
							</div>
							<div class="tab-title">تم تدقيقها</div>
						</div>
				</a></li>
				<li class="nav-item" role="presentation"><a class="nav-link"
					onclick='changeActiveTab("bookMarked");reloadIframeUnderRevision("bookMarked-iframe");' id='a-bookMarked'
					data-bs-toggle="pill" href="#bookMarked" role="tab"
					aria-selected="false">
						<div class="d-flex align-items-center">
							<div class="tab-icon">
								<i class='fa fa-bookmark-o font-18 me-1'></i>
							</div>
							<div class="tab-title">تحتوي أشارة</div>
						</div>
				</a></li>

			</ul>
		<div class="tab-content" id="pills-tabContent">
 			<div class="tab-pane fade" id="under-dlv" role="tabpanel">
 			<iframe id="under-dlv-iframe" height="1000vh" width="400"  
 			myUrl="./with-agent-underdlv" style="width: -webkit-fill-available;border: none;overflow: auto;min-height: 100vh;">
	     </iframe>
	     	
			</div>
 		<div class="tab-pane fade" id ='under-revision' role="tabpanel">
 		<iframe id="under-revision-iframe" height="100vh" width="400"  
 		myUrl="./under-revision" style="width: -webkit-fill-available;border: none;overflow: auto;min-height: 100vh;">
	     </iframe>
     	</div>
        <div class="tab-pane fade " id="reviewed" role="tabpanel">
		<iframe id="reviewed-iframe" height="1000vh" width="400"  myUrl="./reviewed" 
		style="width: -webkit-fill-available;border: none;overflow: auto;min-height: 100vh;">
	     </iframe>
		</div>
		<div class="tab-pane fade" id="bookMarked" role="tabpanel">
			<iframe id="bookMarked-iframe" height="1000vh" width="400"  
			myUrl="./book-marked" style="width: -webkit-fill-available;border: none;overflow: auto;min-height: 100vh;">
	     	</iframe>
     	</div>		
  </div>
			</div>
		</div>
	</div>
</div>
<%@ include file="../Main/footer.jsp"%>
<script>
function changeActiveTab(tab){
	 localStorage.setItem('activeTab_CasesFollowUp', tab);
	
}
$(function (){
	var selectedTab = localStorage.getItem('activeTab_CasesFollowUp');
	if(selectedTab){
        $('#'+selectedTab).addClass('active show');
        $('#a-'+selectedTab).addClass('active');
        reloadIframeUnderRevision(selectedTab+"-iframe");
    }else{
    	 $('#under-dlv').addClass('active show');
         $('#a-under-dlv').addClass('active');
    }
})

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
$(document).ready(function() {
	$(".wrapper").hasClass("toggled") ? ($(".wrapper").removeClass("toggled"), $(".sidebar-wrapper").unbind("hover")) : 
		($(".wrapper").addClass("toggled"), $(".sidebar-wrapper").hover(function() {
		$(".wrapper").addClass("sidebar-hovered")
	}, function() {
		$(".wrapper").removeClass("sidebar-hovered")
	}));
	new PerfectScrollbar('#under-revision-iframe');
});


function reloadIframeUnderRevision(id){
	//var url    = './under-revision';
	document.getElementById(id).src = $("#"+id).attr('myUrl')+"?load=doLoad";
}

</script>
