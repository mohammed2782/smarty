<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.profit, com.app.reports.profit_dtls" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	profit pr = new profit(); 
 	Render(pr  , out , request, response , Myglobals , objectState , pageName1); 	
%>
<div class='clearfix'></div>
<br>
<div class='row'>
	<div class='col-md-12'> 
	<%
		if (request.getParameter("sellbillid_rpt")!=null){
			if (!request.getParameter("sellbillid_rpt").isEmpty()){
				Myglobals.smartyGlobalsAssArr.put("sellbillid_rpt", request.getParameter("sellbillid_rpt"));
			}else{
				Myglobals.smartyGlobalsAssArr.remove("sellbillid_rpt");
			}
		}
		
		if (Myglobals.smartyGlobalsAssArr.get("transrptcustid")!=null){
			profit_dtls prd = new profit_dtls();
			Render(prd  , out , request, response , Myglobals , objectState , pageName1); 
		}
	%>
</div>
 </div>
<%@ include file="../Main/footer.jsp"%>
