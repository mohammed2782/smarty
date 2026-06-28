<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.bussframework.Stages, com.app.bussframework.StepsSettings" %> 
<%
if (user.getUsid() == 1 || user.getRank_code().equalsIgnoreCase("ITBOSS")){
String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	 Stages stg = new Stages(); 
 	Render(stg  , out , request, response , Myglobals , objectState , pageName1); 
	
 	if (request.getParameter("stg_code")!=null){
		if (!request.getParameter("stg_code").isEmpty()){
			Myglobals.smartyGlobalsAssArr.put("stg_code", request.getParameter("stg_code"));
		}else{
			Myglobals.smartyGlobalsAssArr.remove("stg_code");
		}
	} 
 	 
 		if (Myglobals.smartyGlobalsAssArr.get("stg_code")!=null){
 			StepsSettings stpSet = new StepsSettings();
 			Render(stpSet  , out , request, response , Myglobals , objectState , pageName1); 
 			
 		}
}else{
	out.println("<h1>منطقة محظورة</h1>");
}	
 %> 
<%@ include file="../Main/footer.jsp"%>