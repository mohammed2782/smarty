<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.RecycleBin" %> 


<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	RecycleBin rcb = new RecycleBin(); 
 	Render(rcb  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%@ include file="../Main/footer.jsp"%>
<script>


$('#c_custreceiptnoori').focus();
</script>