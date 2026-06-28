 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.setup_users" %> 

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	setup_users su = new setup_users(); 
	Render(su , out , request, response , Myglobals , objectState , pageName1);

%>
</script>
<%@ include file="../Main/footer.jsp"%> 