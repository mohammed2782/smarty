 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.MoneyWithDlvAgentsNotReceived" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
MoneyWithDlvAgentsNotReceived ecm = new MoneyWithDlvAgentsNotReceived(); 
 	Render(ecm  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%> 

