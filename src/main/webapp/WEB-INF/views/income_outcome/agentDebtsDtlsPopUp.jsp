<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.incomeoutcome.AgentDebtsDtlsPopUp" %>
<%   

String agentAccountDebts = (String)request.getParameter("agentaccountdebts");

if (agentAccountDebts !=null){
	Myglobals.smartyGlobalsAssArr.put("agentaccountdebts", (String)agentAccountDebts);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("agentaccountdebts") && Myglobals.smartyGlobalsAssArr.get("agentaccountdebts")!=null){
	agentAccountDebts = (String)Myglobals.smartyGlobalsAssArr.get("agentaccountdebts");
}
String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
AgentDebtsDtlsPopUp agentDebtsDtlsPopUp = new AgentDebtsDtlsPopUp(); 
Render(agentDebtsDtlsPopUp  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />