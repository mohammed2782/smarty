 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.Outcome" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	Outcome ouc = new Outcome(); 
  	Render(ouc  , out , request, response , Myglobals , objectState , pageName1);
 %>
<%@ include file="../Main/footer.jsp"%>