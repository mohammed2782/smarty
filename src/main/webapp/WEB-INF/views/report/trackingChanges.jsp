 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.TrackingChanges"%>
<%
String pageName1 = this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName();
	TrackingChanges trackingChanges = new TrackingChanges();
	Render(trackingChanges, out, request, response, Myglobals, objectState, pageName1);
%>
<%@ include file="../Main/footer.jsp"%> 
