<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.setup.CustomerBooks, java.sql.PreparedStatement, java.sql.ResultSet,com.app.util.Utilities" %>
<%  

String custidbook = (String)request.getParameter("custidbook");

if (custidbook !=null){
	Myglobals.smartyGlobalsAssArr.put("custidbook", (String)custidbook);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("custidbook") && Myglobals.smartyGlobalsAssArr.get("custidbook")!=null){
	custidbook = (String)Myglobals.smartyGlobalsAssArr.get("custidbook");
}
Connection conn = null;
Utilities ut = new Utilities();
HashMap<String, String> info = new HashMap<String, String>();
try{
	conn = mysql.getConn();
	//info = ut.getAgentInfo(conn, districtsusid);
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
		              <h5></h5>
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
CustomerBooks cb = new CustomerBooks(); 
Render(cb  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />