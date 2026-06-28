<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.SetupBranches , com.app.setup.SetupBranchesPickUpAgents" %>	

	<% 
		String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		SetupBranches cb = new SetupBranches(); 
		Render(cb  , out , request, response , Myglobals , objectState , pageName1);
	%> 
	

<jsp:include page="../Main/footer.jsp" />