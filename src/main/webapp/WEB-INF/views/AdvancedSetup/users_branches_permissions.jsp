 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.advancedsetup.usersBranchesPermissions" %>
<%
if (user.getUsid() == 1 || user.getRank_code().equalsIgnoreCase("ITBOSS")){
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	usersBranchesPermissions ubp = new usersBranchesPermissions(); 
	Render(ubp  , out , request, response , Myglobals , objectState , pageName1);
}else{
	out.println("<h1>منطقة محظورة</h1>");
}	
%> 

<jsp:include page="../Main/footer.jsp" /> 