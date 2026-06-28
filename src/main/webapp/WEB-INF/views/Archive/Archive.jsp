<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.archv.ArchiveData" %> 
<% 
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	ArchiveData archiveData = new ArchiveData(); 
 	Render(archiveData , out , request, response , Myglobals , objectState , pageName1);
 %>
<%@ include file="../Main/footer.jsp"%>