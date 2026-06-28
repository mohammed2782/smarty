<%@page import="com.app.util.UtilitiesFeqar, java.text.DecimalFormat"%>
<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.PickUpAgentShareBalance" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet,
com.app.incomeoutcome.PickUpAgentSharePayments " %>
<%
      String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
      PickUpAgentShareBalance pickUpAgentShareBalance = new PickUpAgentShareBalance(); 
     	Render(pickUpAgentShareBalance  , out , request, response , Myglobals , objectState , pageName1);
     	 %>
<%@ include file="../Main/footer.jsp"%>

