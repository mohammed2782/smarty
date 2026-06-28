<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.setup_permissions" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	setup_permissions sp = new setup_permissions(); 
 	Render(sp  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%> 