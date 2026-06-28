<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.AssignReceiptsToCustomer" %>	

	<% 
		String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		AssignReceiptsToCustomer assignReceiptsToCustomer = new AssignReceiptsToCustomer(); 
		Render(assignReceiptsToCustomer  , out , request, response , Myglobals , objectState , pageName1);
	%> 
	

<jsp:include page="../Main/footer.jsp" />