<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.setup.MergeCustomerWithAnotherMaster, java.sql.PreparedStatement, java.sql.ResultSet,com.app.util.Utilities" %>
<%  

String mergCustIdWithAnotherMasterId = (String)request.getParameter("mergCustIdWithAnotherMasterId");
String masterCustOutOff = (String)request.getParameter("masterCustOutOff");
if (mergCustIdWithAnotherMasterId !=null){
	Myglobals.smartyGlobalsAssArr.put("mergCustIdWithAnotherMasterId", (String)mergCustIdWithAnotherMasterId);
	Myglobals.smartyGlobalsAssArr.put("masterCustOutOff", (String)masterCustOutOff);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("mergCustIdWithAnotherMasterId") && Myglobals.smartyGlobalsAssArr.get("mergCustIdWithAnotherMasterId")!=null){
	mergCustIdWithAnotherMasterId = (String)Myglobals.smartyGlobalsAssArr.get("mergCustIdWithAnotherMasterId");
	masterCustOutOff = (String)Myglobals.smartyGlobalsAssArr.get("masterCustOutOff");
}

Connection conn = null;
Utilities ut = new Utilities();
String custName = "";
try{
	conn = mysql.getConn();
	custName = ut.getCustomerName(conn, Integer.parseInt(mergCustIdWithAnotherMasterId));
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
		              <h5><%=custName %></h5>
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
MergeCustomerWithAnotherMaster mergeCustomerWithAnotherMaster = new MergeCustomerWithAnotherMaster(); 
Render(mergeCustomerWithAnotherMaster  , out , request, response , Myglobals , objectState , pageName1);
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