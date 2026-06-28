<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.ShipmentReturnReasons" %> 

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	ShipmentReturnReasons shipmentReturnReasons = new ShipmentReturnReasons(); 
	Render(shipmentReturnReasons , out , request, response , Myglobals , objectState , pageName1);
	
%> 

<%@ include file="../Main/footer.jsp"%> 