<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.bussframework.CasesInQueue, com.app.bussframework.LateCases, com.app.bussframework.RedundantCases" %>
<div class="row">
	<div class="col-lg-4">
		<%
		String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		RedundantCases rc = new RedundantCases(); 
 		Render(rc  , out , request, response , Myglobals , objectState , pageName1);
 		%>
	</div>
	<div class="col-lg-4">
		<%
		LateCases lc = new LateCases(); 
 		Render(lc  , out , request, response , Myglobals , objectState , pageName1);
 		%>
	</div>
</div>
<%@ include file="../Main/footer.jsp"%> 



