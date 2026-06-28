<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.setup_ranks" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	setup_ranks sr = new setup_ranks(); 
 	Render(sr , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%>