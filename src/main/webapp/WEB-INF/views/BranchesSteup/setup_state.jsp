<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.setup_state" %> 

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	setup_state ssl = new setup_state(); 
	Render(ssl , out , request, response , Myglobals , objectState , pageName1);

%>
<%@ include file="../Main/footer.jsp"%> 