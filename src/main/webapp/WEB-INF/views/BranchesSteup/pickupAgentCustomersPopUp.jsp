<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.setup.AgentsCustomers, java.sql.PreparedStatement, java.sql.ResultSet,com.app.util.Utilities" %>
<%   

String customersusid = (String)request.getParameter("customersusid");

if (customersusid !=null){
	Myglobals.smartyGlobalsAssArr.put("customersusid", (String)customersusid);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("customersusid") && Myglobals.smartyGlobalsAssArr.get("customersusid")!=null){
	customersusid = (String)Myglobals.smartyGlobalsAssArr.get("customersusid");
}
Connection conn = null;
Utilities ut = new Utilities();
HashMap<String, String> info = new HashMap<String, String>();
try{
	conn = mysql.getConn();
	info = ut.getAgentInfo(conn, customersusid);
}catch(Exception e){
	e.printStackTrace();
}finally{
	try{conn.close();}catch(Exception e){}
}
%>
<div class="row">
<div class="col-md-12">
          <div class="panel panel-warning">
            <div class="panel-heading">
            	<div class='row'>
		            <div class='col-xs-5'>
		              <h5><%=info.get("name") %></h5>
		            </div>
		            <div class='col-xs-5'>
		              <h5></h5>
		            </div>
		            
	            </div>
            </div>
        </div>
	</div>
</div>
<%
String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
AgentsCustomers agentCustomer = new AgentsCustomers(); 
Render(agentCustomer  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />