<%@ include file="../Main/Main.jsp"%>
<%@ page import=" com.app.tickets.CreateNewTicket" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	CreateNewTicket createNewTicket = new CreateNewTicket(); 
 	Render(createNewTicket  , out , request, response , Myglobals , objectState , pageName1); 
%>
<%@ include file="../Main/footer.jsp"%>
