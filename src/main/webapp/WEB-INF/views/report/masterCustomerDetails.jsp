 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.MasterCustomerDetails"%>
<%
	String pageName1 = this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName();
	MasterCustomerDetails mcd = new MasterCustomerDetails();
	Render(mcd, out, request, response, Myglobals, objectState, pageName1);
%>
<%@ include file="../Main/footer.jsp"%>