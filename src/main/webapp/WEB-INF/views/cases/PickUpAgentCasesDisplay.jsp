<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.PickUpAgentCasesViewOnly" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	PickUpAgentCasesViewOnly pickUpAgentCasesViewOnly = new PickUpAgentCasesViewOnly(); 
  	Render(pickUpAgentCasesViewOnly , out , request, response , Myglobals , objectState , pageName1);
 %>
<%@ include file="../Main/footer.jsp"%>