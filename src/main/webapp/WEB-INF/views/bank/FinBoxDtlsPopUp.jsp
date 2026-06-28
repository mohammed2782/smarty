<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.incomeoutcome.bank.FinBoxDtlsPopUp" %>
<%
String finboxacctid = (String)request.getParameter("finboxacctid");
if (finboxacctid !=null){
	Myglobals.smartyGlobalsAssArr.put("FIN_BOX_ACCT_ID_G", (String)finboxacctid);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("FIN_BOX_ACCT_ID_G") && Myglobals.smartyGlobalsAssArr.get("FIN_BOX_ACCT_ID_G")!=null){
	finboxacctid = (String)Myglobals.smartyGlobalsAssArr.get("FIN_BOX_ACCT_ID_G");
}

String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
FinBoxDtlsPopUp fbd = new FinBoxDtlsPopUp(); 
Render(fbd  , out , request, response , Myglobals , objectState , pageName1);
%> 
<jsp:include page="../Main/footer-popup.jsp" />
