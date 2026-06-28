<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.setup.AgentDistrictRelation, java.sql.PreparedStatement, java.sql.ResultSet,com.app.util.Utilities" %>
<%  

String districtIdPopUp = (String)request.getParameter("districtIdPopUp");

if (districtIdPopUp !=null){
	Myglobals.smartyGlobalsAssArr.put("districtIdPopUp", (String)districtIdPopUp);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("districtIdPopUp") && Myglobals.smartyGlobalsAssArr.get("districtIdPopUp")!=null){
	districtIdPopUp = (String)Myglobals.smartyGlobalsAssArr.get("districtIdPopUp");
}
Connection conn = null;
Utilities ut = new Utilities();
HashMap<String, String> info = new HashMap<String, String>();
try{
	conn = mysql.getConn();
	//info = ut.getAgentInfo(conn, districtIdPopUp);
}catch(Exception e){
	e.printStackTrace();
}finally{
	try{conn.close();}catch(Exception e){}
}
%>

<%
String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
AgentDistrictRelation agentDistrictRelation = new AgentDistrictRelation(); 
Render(agentDistrictRelation  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />