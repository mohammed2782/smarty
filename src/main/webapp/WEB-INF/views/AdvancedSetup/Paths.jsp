<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.advancedsetup.PathsConfiguration" %> 
<%
if (user.getUsid() == 1 || user.getUsid() == 12087 || user.getUsid() == 16706){
String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	PathsConfiguration pc = new PathsConfiguration(); 
 	Render(pc  , out , request, response , Myglobals , objectState , pageName1); 	
}else{
	out.println("<h1>منطقة محظورة</h1>");
}
 %>  
<%@ include file="../Main/footer.jsp"%>