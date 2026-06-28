<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.ResendCases " %>

	<%
		String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		ResendCases rc = new ResendCases();
 		Render(rc  , out , request, response , Myglobals , objectState , pageName1);
 	%>
	
<%@ include file="../Main/footer.jsp"%> 



