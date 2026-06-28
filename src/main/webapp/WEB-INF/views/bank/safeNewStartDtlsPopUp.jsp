<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.incomeoutcome.bank.SafeNewStartDtlsPopUp" %>
<%
String safeid = (String)request.getParameter("safeid");

if (safeid !=null){
	Myglobals.smartyGlobalsAssArr.put("SHOW_UP_TO_SAFEID_G", (String)safeid);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("SHOW_UP_TO_SAFEID_G") && Myglobals.smartyGlobalsAssArr.get("SHOW_UP_TO_SAFEID_G")!=null){
	safeid = (String)Myglobals.smartyGlobalsAssArr.get("SHOW_UP_TO_SAFEID_G");
}
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SafeNewStartDtlsPopUp safeNewStartDtlsPopUp = new SafeNewStartDtlsPopUp();
	Render(safeNewStartDtlsPopUp  , out , request, response , Myglobals , objectState , pageName1);
	
%> 
<jsp:include page="../Main/footer-popup.jsp" />