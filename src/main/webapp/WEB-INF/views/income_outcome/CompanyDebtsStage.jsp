 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.CompanyDebts_StageAgent" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	CompanyDebts_StageAgent cdsa = new CompanyDebts_StageAgent(); 
 	Render(cdsa  , out , request, response , Myglobals , objectState , pageName1); 
 
%> 
<%@ include file="../Main/footer.jsp"%> 