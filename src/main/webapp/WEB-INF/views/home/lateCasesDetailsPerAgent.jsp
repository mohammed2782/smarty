<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.bussframework.AgentLateCases" %>
<%  

String agentIdLateCases = (String)request.getParameter("agentIdLateCases");

if (agentIdLateCases !=null){
	Myglobals.smartyGlobalsAssArr.put("agentIdLateCases", (String)agentIdLateCases);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("agentIdLateCases") && Myglobals.smartyGlobalsAssArr.get("agentIdLateCases")!=null){
	agentIdLateCases = (String)Myglobals.smartyGlobalsAssArr.get("agentIdLateCases");
}

String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
AgentLateCases agentLateCases = new AgentLateCases(); 
Render(agentLateCases  , out , request, response , Myglobals , objectState , pageName1);
%> 

<jsp:include page="../Main/footer-popup.jsp" />