<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.bussframework.StepsDecisions,	java.sql.PreparedStatement, java.sql.ResultSet " %>
<% 

String stp_id = (String)request.getParameter("stp_id");

if (stp_id !=null){
	Myglobals.smartyGlobalsAssArr.put("stp_id", (String)stp_id);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("stp_id") && Myglobals.smartyGlobalsAssArr.get("stp_id")!=null){
	stp_id = (String)Myglobals.smartyGlobalsAssArr.get("stp_id");
	stp_id = (String)Myglobals.smartyGlobalsAssArr.get("stp_id");
} 
 
%>

<%

	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	StepsDecisions sd = new StepsDecisions(); 
	Render(sd  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />