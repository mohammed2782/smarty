<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.incomeoutcome.IncomeOutcomeDtlsPopUp , java.sql.PreparedStatement, java.sql.ResultSet" %>
<%  
String trancode = (String)request.getParameter("trancode").trim();
String trandate = (String)request.getParameter("trandate").trim();
String todate = (String)request.getParameter("todate").trim();
String accttranuserid = (String)request.getParameter("accttranuserid").trim();


if (trancode !=null){
	Myglobals.smartyGlobalsAssArr.put("trancode", (String)trancode);
	Myglobals.smartyGlobalsAssArr.put("trandate", (String)trandate);
	Myglobals.smartyGlobalsAssArr.put("todate", (String)todate);
	Myglobals.smartyGlobalsAssArr.put("accttranuserid", (String)accttranuserid);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("trancode") &&Myglobals.smartyGlobalsAssArr.containsKey("todate")
		&& Myglobals.smartyGlobalsAssArr.get("trancode")!=null){
	trancode = (String)Myglobals.smartyGlobalsAssArr.get("trancode");
	trandate = (String)Myglobals.smartyGlobalsAssArr.get("trandate");
	todate = (String)Myglobals.smartyGlobalsAssArr.get("todate");
	accttranuserid = (String)Myglobals.smartyGlobalsAssArr.get("accttranuserid");
}

%>

<%
String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
IncomeOutcomeDtlsPopUp iodp = new IncomeOutcomeDtlsPopUp(); 
Render(iodp  , out , request, response , Myglobals , objectState , pageName1);
%> 
<script>
   /* window.onunload = refreshParent;
    function refreshParent() {
        window.opener.location.reload();
    }
    */
</script>
<jsp:include page="../Main/footer-popup.jsp" />