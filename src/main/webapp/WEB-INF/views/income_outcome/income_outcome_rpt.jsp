<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.IncomeOutcomeRpt" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	IncomeOutcomeRpt ior = new IncomeOutcomeRpt(); 
 	Render(ior  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%>
 
