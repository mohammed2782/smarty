<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.NewCasesViaUpload" %>
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	NewCasesViaUpload uc = new NewCasesViaUpload(); 
 	Render(uc  , out , request, response , Myglobals , objectState , pageName1);
%>
<%@ include file="../Main/footer.jsp"%>
