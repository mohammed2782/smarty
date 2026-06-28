<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.incomeoutcome.BranchDebtsDtlsPopUp" %>
<%   

String otherBranchTransEntity = (String)request.getParameter("otherBranchTransEntity");
String reportBranchDebtOnly = (String)request.getParameter("reportBranchDebtOnly");

if (otherBranchTransEntity !=null){
	Myglobals.smartyGlobalsAssArr.put("otherBranchTransEntity", (String)otherBranchTransEntity);
	Myglobals.smartyGlobalsAssArr.put("reportBranchDebtOnly", (String)reportBranchDebtOnly);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("otherBranchTransEntity") && Myglobals.smartyGlobalsAssArr.get("otherBranchTransEntity")!=null){
	otherBranchTransEntity = (String)Myglobals.smartyGlobalsAssArr.get("otherBranchTransEntity");
	reportBranchDebtOnly = (String)Myglobals.smartyGlobalsAssArr.get("reportBranchDebtOnly");
}
String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
BranchDebtsDtlsPopUp branchDebtsDtlsPopUp = new BranchDebtsDtlsPopUp(); 
Render(branchDebtsDtlsPopUp  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />