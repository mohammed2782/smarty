<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.bank.AcctBox" %> 
<% 
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	AcctBox acctBox = new AcctBox(); 
 	Render(acctBox , out , request, response , Myglobals , objectState , pageName1);
 %>
<%@ include file="../Main/footer.jsp"%>