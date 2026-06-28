<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.PostponedOptions" %> 

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	PostponedOptions postponedOptions = new PostponedOptions(); 
	Render(postponedOptions , out , request, response , Myglobals , objectState , pageName1);
	
%> 

<%@ include file="../Main/footer.jsp"%> 