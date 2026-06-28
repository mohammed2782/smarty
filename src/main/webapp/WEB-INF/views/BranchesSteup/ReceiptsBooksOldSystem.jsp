 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.CustomerBooks, java.sql.PreparedStatement, java.sql.ResultSet,com.app.util.Utilities" %>

<%
String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
CustomerBooks cb = new CustomerBooks(); 
Render(cb  , out , request, response , Myglobals , objectState , pageName1);
%> 

<jsp:include page="../Main/footer.jsp" /> 