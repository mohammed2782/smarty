<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.CaseInfoBySearch" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	CaseInfoBySearch caseInfoBySearch = new CaseInfoBySearch(); 
  	Render(caseInfoBySearch  , out , request, response , Myglobals , objectState , pageName1);
 %>
<%@ include file="../Main/footer.jsp"%>
<script>


$('#c_custreceiptnoori').focus();


</script>