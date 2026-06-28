 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.tickets.SetupGroupHelpDeskEmpOnStates" %> 

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SetupGroupHelpDeskEmpOnStates setupGroupHelpDeskEmpOnStates = new SetupGroupHelpDeskEmpOnStates(); 
	Render(setupGroupHelpDeskEmpOnStates, out , request, response , Myglobals , objectState , pageName1);
%>
<%@ include file="../Main/footer.jsp"%> 
