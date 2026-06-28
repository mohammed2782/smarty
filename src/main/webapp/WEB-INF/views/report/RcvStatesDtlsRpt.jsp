 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.RcvStatesDtlsRpt" %> 
<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
    RcvStatesDtlsRpt rsdr = new RcvStatesDtlsRpt(); 
	Render(rsdr , out , request, response , Myglobals , objectState , pageName1);
%>
<%@ include file="../Main/footer.jsp"%>   