<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="java.sql.PreparedStatement, java.sql.ResultSet,
 com.app.bussframework.SingleQueueFactory, com.app.bussframework.SingleQueue " %>
<% 
 
	String stg_code = (String)request.getParameter("stg_code");
	String stp_code = (String)request.getParameter("stp_code");
	String queueName = "";
	if (stg_code !=null){
		Myglobals.smartyGlobalsAssArr.put("stg_code", (String)stg_code);
		Myglobals.smartyGlobalsAssArr.put("stp_code", (String)stp_code);
	}else if (Myglobals.smartyGlobalsAssArr.containsKey("stg_code") && Myglobals.smartyGlobalsAssArr.get("stg_code")!=null){
		stg_code = (String)Myglobals.smartyGlobalsAssArr.get("stg_code");
		stp_code = (String)Myglobals.smartyGlobalsAssArr.get("stp_code");
	}

	Connection conn = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	try{
		conn = mysql.getConn();
		pst = conn.prepareStatement("select stg_name , stp_name from kbstage join kbstep on (stg_code=stp_stgcode)"+
				 " where stg_code=? and stp_code =?");
		pst.setString(1, stg_code);
		pst.setString(2, stp_code);
		rs = pst.executeQuery();
		if (rs.next())
			queueName = rs.getString("stg_name")+" - "+rs.getString("stp_name");
		
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
            		<div class='col-xs-6'><h6>الحالة الحالية للطلبات : <%=queueName%></h6></div>
            		<div class='col-xs-6'><h6></h6></div>
            	  </div>
            	
            </div>
        </div>
	</div>
</div>
<%
	SingleQueueFactory sqf = new SingleQueueFactory();
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SingleQueue sq = sqf.getSingleQueuObj(stg_code, stp_code); 
	Render(sq  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />