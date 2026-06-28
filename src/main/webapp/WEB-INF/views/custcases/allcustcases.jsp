<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cust.cases.CustomerViewAllCases" %> 


<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
CustomerViewAllCases uc = new CustomerViewAllCases(); 
 	Render(uc  , out , request, response , Myglobals , objectState , pageName1); 

%>
<%@ include file="../Main/footer.jsp"%>