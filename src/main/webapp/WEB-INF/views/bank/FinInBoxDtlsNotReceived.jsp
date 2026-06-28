<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.incomeoutcome.bank.inFinBoxDtlsNotReceivedPopUp" %>
<%
	String inboxnotrcvcreatedby = (String)request.getParameter("inboxnotrcvcreatedby");
	
	
if (inboxnotrcvcreatedby !=null){
	Myglobals.smartyGlobalsAssArr.put("inboxnotrcvcreatedby", (String)inboxnotrcvcreatedby);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("inboxnotrcvcreatedby") && Myglobals.smartyGlobalsAssArr.get("inboxnotrcvcreatedby")!=null){
	inboxnotrcvcreatedby = (String)Myglobals.smartyGlobalsAssArr.get("inboxnotrcvcreatedby");
}

	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	inFinBoxDtlsNotReceivedPopUp finBoxDtlsNotReceivedPopUp = new inFinBoxDtlsNotReceivedPopUp(); 
	
	Render(finBoxDtlsNotReceivedPopUp  , out , request, response , Myglobals , objectState , pageName1);
	
%> 

<jsp:include page="../Main/footer-popup.jsp" />
<script>


</script>