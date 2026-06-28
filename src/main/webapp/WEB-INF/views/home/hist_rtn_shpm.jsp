<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.HistoryRtnShipments" %>

	
	<%
		String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		HistoryRtnShipments hrs = new HistoryRtnShipments(); 
 		Render(hrs  , out , request, response , Myglobals , objectState , pageName1);
 	 %>
	

<%@ include file="../Main/footer.jsp"%> 

