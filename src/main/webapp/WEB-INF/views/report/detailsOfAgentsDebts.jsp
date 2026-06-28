<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.reports.DetailsOfAllAgentsDebts" %>
<%   

String branch_code = (String)request.getParameter("branch_code");

if (branch_code !=null){
	Myglobals.smartyGlobalsAssArr.put("branch_code_details_agents_debts", (String)branch_code);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("branch_code_details_agents_debts") && Myglobals.smartyGlobalsAssArr.get("branch_code_details_agents_debts")!=null){
	branch_code = (String)Myglobals.smartyGlobalsAssArr.get("branch_code_details_agents_debts");
}
String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
DetailsOfAllAgentsDebts detailsOfAllAgentsDebts = new DetailsOfAllAgentsDebts(); 
Render(detailsOfAllAgentsDebts  , out , request, response , Myglobals , objectState , pageName1);
%> 
<jsp:include page="../Main/footer-popup.jsp" />