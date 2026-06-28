<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.finreports.ShipmentsProfits " %>
	<%
		String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		ShipmentsProfits shipmentsProfits = new ShipmentsProfits(); 
 		Render(shipmentsProfits  , out , request, response , Myglobals , objectState , pageName1);
 	%>
<%@ include file="../Main/footer.jsp"%> 



