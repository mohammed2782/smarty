 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.DlvStatesDtlsRpt" %> 

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
    DlvStatesDtlsRpt dsdr = new DlvStatesDtlsRpt(); 
	Render(dsdr , out , request, response , Myglobals , objectState , pageName1);

%>
<%@ include file="../Main/footer.jsp"%>  