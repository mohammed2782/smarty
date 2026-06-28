 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cust.settings.MyProfile" %> 
 
<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	MyProfile myProfile = new MyProfile(); 
	Render(myProfile , out , request, response , Myglobals , objectState , pageName1);

%>

<%@ include file="../Main/footer.jsp"%> 