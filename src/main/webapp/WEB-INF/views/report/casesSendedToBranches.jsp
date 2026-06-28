<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.CasesSendedToBranches " %>

	<%
		String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		CasesSendedToBranches cstb = new CasesSendedToBranches(); 
 		Render(cstb  , out , request, response , Myglobals , objectState , pageName1);
 	%>
	
<%@ include file="../Main/footer.jsp"%> 



