<%@ include file="../Main/Main.jsp"%>
<%@ page
	import="com.app.bussframework.CasesInQueue, com.app.bussframework.StagesStepsDashBoard, com.app.bussframework.*"%>
<%
Connection connStages = null;
StagesStepsDashBoard stagesStepsDashBoard = new StagesStepsDashBoard();
ArrayList<StageBean> stageList = null;
try{
	connStages =  mysql.getConn();
	stagesStepsDashBoard.setConn(connStages);
	stageList = stagesStepsDashBoard.getAllStages(user.getRank_code());
}catch(Exception e){
	e.printStackTrace();
}finally{
	try{connStages.close();}catch(Exception e){}
}
%>
<div class="row">
	<%
	boolean first = true;
	StringBuilder sb = new StringBuilder("");
	for (StageBean stageBean : stageList){
	%>
	<div class="col-3">
		<div class="card">
			<div class="card-header card-head-inverse"
				style='background-color:<%=stageBean.getStageColor()%>'>
				<h4 class="card-title text-white"><%=stageBean.getStageName()%></h4>
			</div>
			<div class="card-content collapse show">
				<div class="card-body">
					<%for(StepBean stepBean : stageBean.getStepsList()){ %>
					<div
						class="bs-callout-warning callout-round callout-bordered callout-transparent mt-1">
						<div class="media align-items-stretch">
							<div class="media-left media-middle p-2"
								style='padding: 1.0rem !important;background-color:<%=stepBean.getStepColor()%>'>
								<i class="<%=stepBean.getStepIcon()%>"
									style='font-size: 13px; color: white;'></i>
							</div>
							<div class="media-body p-1">
								<strong><a
									href='casesinqueue?stg_code=<%=stageBean.getStageCode()%>&stp_code=<%=stepBean.getStepCode()%>'><%=stepBean.getStpName()%></a>
								</strong>
								<div class="badge badge-pill"
								id='<%=stageBean.getStageCode()%>_<%=stepBean.getStepCode()%>_<%=Myglobals.smartyGlobalsAssArr.get("userstorecode")%>_ctr' 
								 style='float: left;background-color:<%=stepBean.getStepColor()%>'></div>
							</div>
						</div>
					</div>
					<%
					out.println("<script>"
							+" $(document).ready(function() {"
               				+ " showStageCtr('"+stageBean.getStageCode()+"','"+stepBean.getStepCode()+"','"+Myglobals.smartyGlobalsAssArr.get("userstorecode")+"');"
               				+" });</script>");
					} %>
				</div>
			</div>
		</div>
	</div>
	<%} %>
</div>


<%@ include file="../Main/footer.jsp"%>

<script>
function showStageCtr(stage, step, branch){
	//console.log("stage="+stage+", step="+step+", branch="+branch);
	 $.ajax({
		 async : true,
			url :  '../../StepsCasesCounterSRVL?stageCode='+stage+'&stepCode='+step+'&branchCode='+branch ,
			success : function(result){
				//alert(result);
			if (result==='10000')
				$("#"+stage+"_"+step+"_"+branch+"_ctr").html(" > "+result);
			else
				$("#"+stage+"_"+step+"_"+branch+"_ctr").html(result);
			
			
			$("#"+stage+"_"+step+"_"+branch+"_spin").css("display","none");
		}
	 });	
}


function changeActiveTab( tab){
	 localStorage.setItem('activeTab', tab);
	
}
 $(function (){
	var selectedTab = localStorage.getItem('activeTab');
	if(selectedTab){
       $('#'+selectedTab).addClass('active show');
       $('#a-'+selectedTab).addClass('active');
   }
})
</script>

