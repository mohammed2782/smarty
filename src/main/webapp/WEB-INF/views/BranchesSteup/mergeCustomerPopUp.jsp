<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.setup.MergeCustomerPopUp,java.sql.PreparedStatement,java.sql.ResultSet,com.app.util.Utilities" %>
<%
String custidreassign = (String)request.getParameter("custidreassign");
String mastercustidreassign = (String)request.getParameter("mastercustidreassign");
if (custidreassign !=null){
	Myglobals.smartyGlobalsAssArr.put("custidreassign", (String)custidreassign);
	Myglobals.smartyGlobalsAssArr.put("mastercustidreassign", (String)mastercustidreassign);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("custidreassign") && Myglobals.smartyGlobalsAssArr.get("custidreassign")!=null){
	custidreassign = (String)Myglobals.smartyGlobalsAssArr.get("custidreassign");
	mastercustidreassign = (String)Myglobals.smartyGlobalsAssArr.get("mastercustidreassign");
}

Connection conn = null;
Utilities ut = new Utilities();
String custName = "";
try{
	conn = mysql.getConn();
	custName = ut.getCustomerName(conn, Integer.parseInt(custidreassign));
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
		              <h5><%=custName%></h5>
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
MergeCustomerPopUp mergeCustomerPopUp = new MergeCustomerPopUp(); 
Render(mergeCustomerPopUp  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
window.onunload = refreshParent;
function refreshParent() {
    var loc = window.opener.location;
    window.opener.location = loc;
    //window.opener.location.reload();
}

</script>
<div class='row'><div  class='col-xs-4 '><input type='button' style='margin-right: 10%;' class='btn btn-sm btn-danger' onclick='window.close()' value='أغلاق الشاشة' ></div></div>
<jsp:include page="../Main/footer-popup.jsp" />