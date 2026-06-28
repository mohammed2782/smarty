 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.MonthlyProfit" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	MonthlyProfit mp = new MonthlyProfit(); 
 	Render(mp  , out , request, response , Myglobals , objectState , pageName1); 
%>
<%@ include file="../Main/footer.jsp"%>    