 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.ShipmentsDelayInStoreReport"%>
<%
String pageName1 = this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName();
	ShipmentsDelayInStoreReport sdisr = new ShipmentsDelayInStoreReport();
	Render(sdisr, out, request, response, Myglobals, objectState, pageName1);
%>
<%@ include file="../Main/footer.jsp"%> 
 