<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.SetupGoodsCategories" %> 

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SetupGoodsCategories sgc = new SetupGoodsCategories(); 
	Render(sgc , out , request, response , Myglobals , objectState , pageName1);

%>
<%@ include file="../Main/footer.jsp"%> 