<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.partnerShareProfiteReport" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	partnerShareProfiteReport psfr = new partnerShareProfiteReport(); 
  	Render(psfr  , out , request, response , Myglobals , objectState , pageName1);
 %>
<%@ include file="../Main/footer.jsp"%> 

