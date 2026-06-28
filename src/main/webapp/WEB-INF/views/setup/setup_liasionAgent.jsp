 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.SetupLiaisonAgent" %> 

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SetupLiaisonAgent setupLiaisonAgent = new SetupLiaisonAgent(); 
	Render(setupLiaisonAgent , out , request, response , Myglobals , objectState , pageName1);

%>

<%@ include file="../Main/footer.jsp"%> 