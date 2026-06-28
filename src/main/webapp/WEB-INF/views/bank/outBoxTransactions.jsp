<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.bank.OutBoxTransactions" %> 
<% 
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	OutBoxTransactions outBoxTransactions = new OutBoxTransactions(); 
 	Render(outBoxTransactions , out , request, response , Myglobals , objectState , pageName1);
 %>
<%@ include file="../Main/footer.jsp"%>  
