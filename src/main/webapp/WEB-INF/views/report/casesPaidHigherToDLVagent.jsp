<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.CasesPaidHigherToDLVagent " %>

	<%
	if (user.isHaveFullServices()){
		String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		CasesPaidHigherToDLVagent cphtd = new CasesPaidHigherToDLVagent(); 
 		Render(cphtd  , out , request, response , Myglobals , objectState , pageName1);
	}else{
		out.println("<h1>منطقة محظورة</h1>");
	}
 	%>
	
<%@ include file="../Main/footer.jsp"%> 



