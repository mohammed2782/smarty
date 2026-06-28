<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.bank.SafeCashTransactionsReport" %> 
<% 
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SafeCashTransactionsReport sctr = new SafeCashTransactionsReport(); 
 	Render(sctr , out , request, response , Myglobals , objectState , pageName1);
 %>
<%@ include file="../Main/footer.jsp"%>  