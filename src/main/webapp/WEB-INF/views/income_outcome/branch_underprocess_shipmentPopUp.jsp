<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.incomeoutcome.UnderProcessInBranchShipmentPopUp" %>
<%   

String shipmentUnderProcessFromBranch = (String)request.getParameter("shipmentUnderProcessFromBranch");
String shipmentUnderProcessToBranch = (String)request.getParameter("shipmentUnderProcessToBranch");

if (shipmentUnderProcessFromBranch !=null){
	Myglobals.smartyGlobalsAssArr.put("shipmentUnderProcessFromBranch", (String)shipmentUnderProcessFromBranch);
	Myglobals.smartyGlobalsAssArr.put("shipmentUnderProcessToBranch", (String)shipmentUnderProcessToBranch);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("shipmentUnderProcessFromBranch") && Myglobals.smartyGlobalsAssArr.get("shipmentUnderProcessFromBranch")!=null){
	shipmentUnderProcessFromBranch = (String)Myglobals.smartyGlobalsAssArr.get("shipmentUnderProcessFromBranch");
	shipmentUnderProcessToBranch = (String)Myglobals.smartyGlobalsAssArr.get("shipmentUnderProcessToBranch");
}
String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
UnderProcessInBranchShipmentPopUp underProcessInBranchShipmentPopUp = new UnderProcessInBranchShipmentPopUp(); 
Render(underProcessInBranchShipmentPopUp  , out , request, response , Myglobals , objectState , pageName1);
%> 

<jsp:include page="../Main/footer-popup.jsp" />