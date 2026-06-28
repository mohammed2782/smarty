 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.RepeatedReceiptsNumber" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	RepeatedReceiptsNumber rrn = new RepeatedReceiptsNumber(); 
 	Render(rrn  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%>  