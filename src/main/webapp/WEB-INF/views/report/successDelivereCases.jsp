<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.SuccessDelivereCases " %>

	<%
		String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		SuccessDelivereCases sdc = new SuccessDelivereCases(); 
 		Render(sdc  , out , request, response , Myglobals , objectState , pageName1);
 	%>
	
<%@ include file="../Main/footer.jsp"%> 



