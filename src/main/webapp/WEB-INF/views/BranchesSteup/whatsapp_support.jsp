<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.whatsapp_support" %> 

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
whatsapp_support si = new whatsapp_support(); 
	Render(si , out , request, response , Myglobals , objectState , pageName1);

%>
<%@ include file="../Main/footer.jsp"%> 