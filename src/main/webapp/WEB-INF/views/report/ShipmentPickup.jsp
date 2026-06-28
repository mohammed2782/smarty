 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.ShipmentPickup"%>
<%
	String pageName1 = this.getClass().getPackage().getName() + "."
			+ this.getClass().getSimpleName();
	ShipmentPickup sp = new ShipmentPickup();
	Render(sp, out, request, response, Myglobals, objectState,
			pageName1);
%>
<%@ include file="../Main/footer.jsp"%> 
