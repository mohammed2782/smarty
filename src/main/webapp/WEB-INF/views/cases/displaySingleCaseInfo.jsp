<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.Updatecase, com.app.cases.SingleCaseDisplay, com.app.util.UtilitiesFeqar, com.app.util.GeneralLookups, com.app.cases.CaseInformation, 
com.app.cases.DisplaySingleCaseMoreInfo, com.app.cases.AuditTrailPopUp, com.app.tickets.FollowUpSingleCaseHistory" %> 

<%	
	String globalSearch =  "";
	int caseId = 0, custReceiptNoOri = 0;
	ArrayList<CaseInformation> casesList = null;
	UtilitiesFeqar ut = new UtilitiesFeqar();
	GeneralLookups generalLookups = new GeneralLookups();
	Connection conn1 = null;
	boolean change = false;
	HashMap<String,String> caseInfo = new HashMap<String,String>();
	ArrayList<String> caseFinBranchesInfo = new ArrayList<String>();
	HashMap<String,String> casePossibleActions = new HashMap<String,String>();
	HashMap<String,String> followUpActions = new HashMap<String,String> ();
	try{
		conn1 = mysql.getConn();
		followUpActions = generalLookups.getFollowUpActions(conn1);
		if (ut.checkIfInRequest(request, "search_global")){
			globalSearch= request.getParameter("search_global");
			casesList   = ut.getCaseIdBasedOnGlobalSearch(conn1, globalSearch, user.getRank_code(), user.getShopsCommaSepereated());
			
		}
		if (ut.checkIfInRequest(request, "auditcaseid")){
			caseId = Integer.parseInt(request.getParameter("auditcaseid"));
			caseInfo = ut.getCaseInfo(conn1, caseId);
			caseFinBranchesInfo = ut.getCaseFinBranchesInfo(conn1, caseId+"");
			casePossibleActions = ut.getPossibleActions(conn1, caseInfo.get("q_stage"), caseInfo.get("q_step"), user.getRank_code());
		}else{
			if (casesList !=null && casesList.size()>0){
				if (casesList.size() == 1)
					caseId = casesList.get(0).getCaseid();
				else{
					for (CaseInformation ci : casesList){
						;
					}
				}
			}
		}
		if(caseId>0)
			change = ut.checkCaseFoundChangement(conn1, caseId);
	}catch (Exception e){
		e.printStackTrace();
	}finally{
		try{conn1.close();}catch(Exception e){}
	}
	%>
	<hr>
	
	<div class = 'row mb-2 justify-content-start'>
		<div class='col-4'>
			<span class="badge bg-secondary" style='font-size:19px;background-color:<%=caseInfo.get("stp_color")%> !important'>
			<%=caseInfo.get("stageStep") %> <i class="<%=caseInfo.get("stp_icon")%>"></i></span>
			<span class="badge bg-danger" style='font-size:19px;'><%=caseInfo.get("rtnReasonDesc")%></span>
		</div>
		<div class='col-7'>
			<div class='row'>
				<div class='col-3'>
					<select id='action_fromfollowup' class='select2 form-control' name ='action_fromfollowup'>
					
					<%for (String actionCode : casePossibleActions.keySet()){
						%>
						<option value='<%=actionCode%>' ><%=casePossibleActions.get(actionCode)%></option>
					<%}%>
					</select>
				</div>
				<div class='col-7'>
					<input type='text' maxlength='70' placeholder='ملاحظات تعرض للمندوب والعميل' style='font-size: 0.72rem;' class='form-control' id='rmks_fromfollowup'  name ='rmks_fromfollowup'/>
					<input type='hidden'  id='current_stage' value='<%=caseInfo.get("q_stage") %>'  name ='current_stage'/>
					<input type='hidden'  id='current_step'  value='<%=caseInfo.get("q_step") %>' name ='current_stage'/>
					<input type='hidden'  id='caseid'  value='<%=caseId %>' name ='caseid'/>
				</div>
				<div class='col-2'>
					<button type="button" onclick='submitActionFromAudit()' id='moveCaseThroughSteps' class="btn btn-sm  btn-info px-3">حفظ</button>
				</div>
			</div>
		</div>
	</div>
	<%
	if (caseId>0){
	 		String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	 		SingleCaseDisplay scd = new SingleCaseDisplay(caseId);
	 		Render(scd  , out , request, response , Myglobals , objectState , pageName1); 
 		
 	
			String pageName2 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
			AuditTrailPopUp auditTrailPopUp = new AuditTrailPopUp(caseId); 
			Render(auditTrailPopUp  , out , request, response , Myglobals , objectState , pageName2);
	
	 		String pageName3 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	 		DisplaySingleCaseMoreInfo dsm = new DisplaySingleCaseMoreInfo(caseId);
	 		Render(dsm  , out , request, response , Myglobals , objectState , pageName3); 
		
	}
	%>
	
<%@ include file="../Main/footer.jsp"%>


<script>

$('#main_row_com_app_tickets_FollowUpSingleCaseHistory > div > h6').remove();
$('#main_row_com_app_tickets_FollowUpSingleCaseHistory > div').next().find('ul').remove();

function submitActionFromAudit(){
	var aStage = $('#current_stage').val();
	var aStep = $('#current_step').val();
	var aCaseId = $('#caseid').val();
	var aAction = $('#action_fromfollowup').val();
	var aRmk = $('#rmks_fromfollowup').val();
	$('#moveCaseThroughSteps').prop('disabled', true);
	if (!aRmk.trim()) {
		$('#rmks_fromfollowup').addClass('is-invalid');
		$('#moveCaseThroughSteps').prop('disabled', false);
		return ;
	}
	var dataToSend = {caseId : aCaseId, stageCode:aStage , stepCode: aStep, action:aAction, remarks:aRmk};
	$.post('../../SubmitQueueActionFromAuditSRVL' , dataToSend, function(data, status){ 
		if (status=='success'){
			$.alert({
			    title: '',
			    content: 'تم تحديث الحالة',
			});
			location.reload();
		}else{
			alert("Error, please contact Mohammed Nafie");
			hideLoader();
		}
 	});
}

function submitFollowUpActionFromAudit(){
	var followUpAction = $('#followup-option').val();
	var followUpRemakrs = $('#followup-actions-remarks').val();
	var caseId = $('#caseid').val();
	$('#followUpActionButton').prop('disabled', true);
	if (followUpAction=='TRUE_STATUS'){
		;
	}else{
		if (!followUpRemakrs.trim()) {
			$('#followup-actions-remarks').addClass('is-invalid');
			$('#followUpActionButton').prop('disabled', false);
			return ;
		}
	}
	var dataToSend = {acaseId : caseId, aFollowUpAction: followUpAction , aFollowUpRemakrs: followUpRemakrs };
	$.post('../../FollowUpDoActionSRVL' , dataToSend, function(data, status){ 
		if (status=='success'){
			$.alert({
			    title: '',
			    content: 'تم تسجيل المعالجة'
			});
			  location.reload();
		}else{
			alert("Error, please contact Mohammed Nafie");
			hideLoader();
		}
 	});
}
</script>


