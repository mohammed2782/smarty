 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cust.settings.CustomerGoodsSetup" %> 

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	CustomerGoodsSetup cgs = new CustomerGoodsSetup(); 
	Render(cgs , out , request, response , Myglobals , objectState , pageName1);

%>

<%@ include file="../Main/footer.jsp"%> 
