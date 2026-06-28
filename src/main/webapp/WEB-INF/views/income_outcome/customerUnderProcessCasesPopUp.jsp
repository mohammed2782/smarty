<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.incomeoutcome.UnderProcessCustomerDetails" %>
<%   

String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
UnderProcessCustomerDetails underProcessCustomerDetails = new UnderProcessCustomerDetails(); 
Render(underProcessCustomerDetails  , out , request, response , Myglobals , objectState , pageName1);
%>
<jsp:include page="../Main/footer-popup.jsp" />