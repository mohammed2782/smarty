 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.CustomerProfitForBranch"%>
<%
	String pageName1 = this.getClass().getPackage().getName() + "."
			+ this.getClass().getSimpleName();
CustomerProfitForBranch customerProfitForBranch = new CustomerProfitForBranch();
	Render(customerProfitForBranch, out, request, response, Myglobals, objectState,
			pageName1);
%>
<%@ include file="../Main/footer.jsp"%>
 





