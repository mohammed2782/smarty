<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.SetupMasterCustEmpRanks" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
SetupMasterCustEmpRanks sr = new SetupMasterCustEmpRanks(); 
 	Render(sr , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%>