<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="java.sql.PreparedStatement, java.sql.ResultSet,
 com.app.bussframework.AgentShipmentsPopUp " %>
<% 

	String c_assignedagent = (String)request.getParameter("c_assignedagent");
	String q_branch = (String)request.getParameter("q_branch");
	String agentName = "";
	if (c_assignedagent !=null){
		Myglobals.smartyGlobalsAssArr.put("c_assignedagent", (String)c_assignedagent);
		Myglobals.smartyGlobalsAssArr.put("q_branch", (String)q_branch);
	}else if (Myglobals.smartyGlobalsAssArr.containsKey("c_assignedagent") && Myglobals.smartyGlobalsAssArr.get("c_assignedagent")!=null){
		c_assignedagent = (String)Myglobals.smartyGlobalsAssArr.get("c_assignedagent");
		q_branch = (String)Myglobals.smartyGlobalsAssArr.get("q_branch");
	}
	
	Connection conn = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	try{
		conn = mysql.getConn();
		pst = conn.prepareStatement("select us_name from kbusers where us_id=?");
		pst.setString(1, c_assignedagent);
		
		rs = pst.executeQuery();
		if (rs.next())
			agentName = rs.getString("us_name");
		
		}catch(Exception e){
		e.printStackTrace();
	}finally{
		try{rs.close();}catch(Exception e){}
		try{pst.close();}catch(Exception e){}
		try{conn.close();}catch(Exception e){}
	}

%>
<div class="row">
<div class="col-md-12">
          <div class="panel panel-warning">
            <div class="panel-heading">
            	<div class='row'>
	            	<div class='col-xs-12'>
	              		<h5></h5>
	            	</div>
	            </div>
            </div>
            <div class="panel-body" style='padding:3px;'>
            	<div class='row'>
            		<div class='col-xs-6'><h6>شحنات الوكيل : <%=agentName%></h6></div>
            		<div class='col-xs-6'><h6></h6></div>
            	  </div>
            	
            </div>
        </div>
	</div>
</div>
<%
	AgentShipmentsPopUp asp = new AgentShipmentsPopUp();
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName(); 
	Render(asp  , out , request, response , Myglobals , objectState , pageName1);
%> 
<div class="row"><div class="col-md-offset-4 col-md-2 col-xs-offset-6 col-xs-3">
<input type="button" class="btn btn-danger" onclick="window.close();" value="غلق النافذه" /></div></div>
<jsp:include page="../Main/footer-popup.jsp" />
<script>
window.onunload = refreshParent;
function refreshParent() {window.opener.location.reload();}
</script>