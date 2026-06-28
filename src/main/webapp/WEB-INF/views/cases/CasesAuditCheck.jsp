<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.CasesAudit" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	CasesAudit casesAudit = new CasesAudit(); 
 	Render(casesAudit  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%>
<script>

var input = document.getElementById("c_custreceiptnoori");
input.value = '';
$('#c_custreceiptnoori').focus();
input.addEventListener("keyup", function(event) {
	  if (event.keyCode === 13) {
		  input.value = '';
	  } 
	});
</script>