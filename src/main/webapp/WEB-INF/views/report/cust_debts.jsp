<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.CustDebts" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	CustDebts cd = new CustDebts(); 
 	Render(cd  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%> 
