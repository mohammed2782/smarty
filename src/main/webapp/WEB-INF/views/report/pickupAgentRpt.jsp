 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.PickUpAgentRpt"%>
<%
String pageName1 = this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName();
	PickUpAgentRpt par = new PickUpAgentRpt();
	Render(par, out, request, response, Myglobals, objectState, pageName1);
%>
<%@ include file="../Main/footer.jsp"%> 
 