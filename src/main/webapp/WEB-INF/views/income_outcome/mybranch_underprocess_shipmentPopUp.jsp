<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.incomeoutcome.UnderProcessMyBranchShipmentPopUp" %>
<%   

String shipmentUnderProcessFromMyBranch = (String)request.getParameter("shipmentUnderProcessFromMyBranch");
String shipmentUnderProcessToOtherBranch = (String)request.getParameter("shipmentUnderProcessToOtherBranch");


if (shipmentUnderProcessFromMyBranch !=null){
	Myglobals.smartyGlobalsAssArr.put("shipmentUnderProcessFromMyBranch", (String)shipmentUnderProcessFromMyBranch);
	Myglobals.smartyGlobalsAssArr.put("shipmentUnderProcessToOtherBranch", (String)shipmentUnderProcessToOtherBranch);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("shipmentUnderProcessFromMyBranch") && Myglobals.smartyGlobalsAssArr.get("shipmentUnderProcessFromMyBranch")!=null){
	shipmentUnderProcessFromMyBranch = (String)Myglobals.smartyGlobalsAssArr.get("shipmentUnderProcessFromMyBranch");
	shipmentUnderProcessToOtherBranch = (String)Myglobals.smartyGlobalsAssArr.get("shipmentUnderProcessToOtherBranch");
}
String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
UnderProcessMyBranchShipmentPopUp underProcessMyBranchShipmentPopUp = new UnderProcessMyBranchShipmentPopUp(); 
Render(underProcessMyBranchShipmentPopUp  , out , request, response , Myglobals , objectState , pageName1);
%> 

<jsp:include page="../Main/footer-popup.jsp" />