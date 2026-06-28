 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.SuccessFailedShipment"%>
<%
	String pageName1 = this.getClass().getPackage().getName() + "."
			+ this.getClass().getSimpleName();
	SuccessFailedShipment sp = new SuccessFailedShipment();
	Render(sp, out, request, response, Myglobals, objectState,
			pageName1);
%>
<%@ include file="../Main/footer.jsp"%>
 





