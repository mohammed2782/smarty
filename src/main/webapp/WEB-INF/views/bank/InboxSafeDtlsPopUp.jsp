<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.incomeoutcome.bank.InboxSafeDtlsPopUp" %>
<%
	String safeid = (String)request.getParameter("safeid");
	
if (safeid !=null){
	Myglobals.smartyGlobalsAssArr.put("safeid", (String)safeid);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("safeid") && Myglobals.smartyGlobalsAssArr.get("safeid")!=null){
	safeid = (String)Myglobals.smartyGlobalsAssArr.get("safeid");
	safeid = (String)Myglobals.smartyGlobalsAssArr.get("safeid");
}

	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	InboxSafeDtlsPopUp inboxSafeDtlsPopUp = new InboxSafeDtlsPopUp(); 
	
	Render(inboxSafeDtlsPopUp  , out , request, response , Myglobals , objectState , pageName1);
	
%> 

<jsp:include page="../Main/footer-popup.jsp" />
<script>


</script>