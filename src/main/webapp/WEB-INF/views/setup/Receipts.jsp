<%@ include file="../Main/Main.jsp"%>
<%@ page import=" com.app.setup.SetupReceipts" %>	

	<% 
		String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		SetupReceipts setupReceipts = new SetupReceipts(); 
		Render(setupReceipts  , out , request, response , Myglobals , objectState , pageName1);
	%> 
	

<jsp:include page="../Main/footer.jsp" />