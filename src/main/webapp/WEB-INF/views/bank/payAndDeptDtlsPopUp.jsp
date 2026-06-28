<%@ include file="../Main/Main-popup.jsp"%>
<%@page import="com.app.incomeoutcome.bank.AllPayDetailsPopUp"%>
<%@ page import="com.app.incomeoutcome.bank.AllDebtDetailsPopUp,java.sql.PreparedStatement,java.sql.ResultSet,com.app.util.Utilities" %>
<%
String tranentitysafe = (String)request.getParameter("tranentitysafe");
if (tranentitysafe !=null){
	Myglobals.smartyGlobalsAssArr.put("tranentitysafe", (String)tranentitysafe);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("tranentitysafe") && Myglobals.smartyGlobalsAssArr.get("tranentitysafe")!=null){
	tranentitysafe = (String)Myglobals.smartyGlobalsAssArr.get("tranentitysafe");
}

Connection conn1 = null;
Utilities ut = new Utilities();
String userName = "";
try{
	conn1 = mysql.getConn();
	userName = ut.getDriverName(conn1, tranentitysafe);
	
}catch(Exception e){
	e.printStackTrace();
}finally{ 
	try{conn1.close();}catch(Exception e){}
}
%>
<div class="row">
<div class="col-md-12">
          <div class="panel panel-warning">
            <div class="panel-heading">
            	<div class='row'>
		            <div class='col-xs-5'>
		              <h5>طرف المعاملة : <%=userName%></h5>
		            </div>
		            <div class='col-xs-5'>
		              <h5></h5>
		            </div>
		            
	            </div>
            </div>
        </div>
	</div>
</div>
<div class="row">
	<div class="col-lg-5" >
		<%
		String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		AllDebtDetailsPopUp allDebtDetailsPopUp = new AllDebtDetailsPopUp(); 
		Render(allDebtDetailsPopUp  , out , request, response , Myglobals , objectState , pageName1);
		%> 
	</div> 
	<div class="col-lg-2">
	</div>
	<div class="col-lg-5">
		<%
		AllPayDetailsPopUp allpayDetailsPopUp = new AllPayDetailsPopUp(); 
		Render(allpayDetailsPopUp  , out , request, response , Myglobals , objectState , pageName1);
		%>
	</div>
</div>
<script>


</script>
<jsp:include page="../Main/footer-popup.jsp" />