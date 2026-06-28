 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.ShipmentsDtlsRpt" %> 

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
    ShipmentsDtlsRpt sdr = new ShipmentsDtlsRpt(); 
	Render(sdr , out , request, response , Myglobals , objectState , pageName1);

%>
<%@ include file="../Main/footer.jsp"%>  