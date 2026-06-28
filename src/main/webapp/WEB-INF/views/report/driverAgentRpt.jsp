 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.DriverAgentRpt"%>
<%
String pageName1 = this.getClass().getPackage().getName() + "."
	+ this.getClass().getSimpleName();
DriverAgentRpt par = new DriverAgentRpt();
	Render(par, out, request, response, Myglobals, objectState,
	pageName1);
%>
<%@ include file="../Main/footer.jsp"%> 
 