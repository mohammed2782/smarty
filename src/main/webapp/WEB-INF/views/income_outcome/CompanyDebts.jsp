 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.CompanyDebts" %> 
<% 
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
  	CompanyDebts mp = new CompanyDebts(); 
   	Render(mp  , out , request, response , Myglobals , objectState , pageName1);
 %>
<%@ include file="../Main/footer.jsp"%>  