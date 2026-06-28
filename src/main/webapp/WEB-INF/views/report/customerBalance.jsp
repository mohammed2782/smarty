<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.CustomerBalance, com.app.reports.CustomerBalanceDetails" %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	CustomerBalance cb = new CustomerBalance(); 
 	Render(cb  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<div class='clearfix'></div>
<br>
<div class='row'>
	<div class='col-md-12'>
	<%
		if (request.getParameter("transrptcustid")!=null){
			if (!request.getParameter("transrptcustid").isEmpty()){
				Myglobals.smartyGlobalsAssArr.put("transrptcustid", request.getParameter("transrptcustid"));
			}else{
				Myglobals.smartyGlobalsAssArr.remove("transrptcustid");
			}
		}		
		if (Myglobals.smartyGlobalsAssArr.get("transrptcustid")!=null){
			CustomerBalanceDetails cbd = new CustomerBalanceDetails();
			Render(cbd  , out , request, response , Myglobals , objectState , pageName1); 
		}
	%>
</div>
 </div>
<%@ include file="../Main/footer.jsp"%>

