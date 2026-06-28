<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.reports.DetailOfMoneyWithCompanies" %>
<%   

String branch_code = (String)request.getParameter("branch_code");

if (branch_code !=null){
	Myglobals.smartyGlobalsAssArr.put("branch_code_finanicalstatus_popup", (String)branch_code);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("branch_code_finanicalstatus_popup") && Myglobals.smartyGlobalsAssArr.get("branch_code_finanicalstatus_popup")!=null){
	branch_code = (String)Myglobals.smartyGlobalsAssArr.get("branch_code_finanicalstatus_popup");
}
String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
DetailOfMoneyWithCompanies detailOfMoneyWithCompanies = new DetailOfMoneyWithCompanies(); 
Render(detailOfMoneyWithCompanies  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />