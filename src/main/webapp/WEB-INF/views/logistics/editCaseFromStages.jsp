<%@ include file="../Main/bootstrap-popup.jsp"%>
<%@page import="com.app.cases.AllCasesPassedByBranch"%>
<%@page import="com.app.cases.Updatecase"%>
<%@page import="smarty.db.mysql"%>
‏
<%@ page 
	import="java.util.List , java.util.HashMap, java.util.Map , smarty.core.*,java.util.Set, java.net.URLEncoder, java.net.URLDecoder , java.util.Iterator  ,javax.servlet.jsp.JspWriter"%>
<%@ page  
	import="java.sql.Connection, smarty.core.CoreMgr , smarty.core.FilesExport , java.util.List , java.util.HashMap , 
	java.util.ArrayList , java.util.Iterator , java.util.*  , javax.* , java.io.FileInputStream , java.io.BufferedInputStream,
	java.io.IOException"%>
<jsp:useBean id="objectState" class="smarty.core.smartyState" scope="session" />
<jsp:useBean id="myCore" class="smarty.core.CoreMgr" scope="request" />
<% 
Connection conn1 = null;
String caseidfromstage = (String)request.getParameter("caseidfromstage");
String branchidfromstage = (String)request.getParameter("branchidfromstage");
String caneditfromstage = (String)request.getParameter("caneditfromstage");
if (caseidfromstage !=null){
	Myglobals.smartyGlobalsAssArr.put("caseidfromstage", (String)caseidfromstage);
	Myglobals.smartyGlobalsAssArr.put("branchidfromstage", (String)branchidfromstage);
	Myglobals.smartyGlobalsAssArr.put("caneditfromstage", (String)caneditfromstage);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("caseidfromstage") && Myglobals.smartyGlobalsAssArr.get("caseidfromstage")!=null){
	caseidfromstage = (String)Myglobals.smartyGlobalsAssArr.get("caseidfromstage");
	branchidfromstage = (String)Myglobals.smartyGlobalsAssArr.get("branchidfromstage");
	caneditfromstage = (String)Myglobals.smartyGlobalsAssArr.get("caneditfromstage");
}
try{
	conn1 =  mysql.getConn();
	if(Integer.parseInt(branchidfromstage) == (int)Myglobals.smartyGlobalsAssArr.get("userstorecode") && caneditfromstage.equalsIgnoreCase("true")){
		Updatecase updcase = new Updatecase();
		updcase.setCaseId(Integer.parseInt(caseidfromstage));
		//Render(updcase  , out , request, response , Myglobals , objectState , pageName1);
		mainProjectPath = servletContext.getContextPath()+"";
		
		String ValidationMsg="";
		String pageName = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		String pageNameWithoutDot =pageName.replace("." , "DOT").toUpperCase();
		
		//Class userClass = Class.forName(className);
		//CoreMgr updcase = (CoreMgr) userClass.newInstance();
		updcase.setAPPPATH(mainProjectPath);
		//System.out.println("------>"+mainProjectPath);
		updcase.setConn(conn1);
		updcase.setHTTPSRequest(request);
		updcase.setLu((LoginUser)request.getSession().getAttribute("lu"));
		updcase.setarrayGlobals(Myglobals.smartyGlobalsAssArr);
		String className = updcase.getClass().getCanonicalName();
		updcase.setmyClassBean(className);
		
		updcase.setJspName(pageName);
		updcase.setJspNameWithoutDot(pageNameWithoutDot);
		updcase.initialize(objectState.smartyStateMap);

		if (request.getParameter("upd")!=null && request.getParameter("myClassBean")!=null ){
			if (request.getParameter("myClassBean").equals(className) && request.getParameter("upd").equals("1")){
				ValidationMsg= updcase.doUpdate(request,true);
				//caneditfromstage=true&branchidfromstage=1&caseidfromstage=31584
				out.println("<script>location.replace('?caneditfromstage="+caneditfromstage+"&branchidfromstage="+branchidfromstage+"&caseidfromstage="+caseidfromstage+"');</script>");
			}
		}else{
			out.print(updcase.getUpdForm(request, caseidfromstage));
		}
	}else if (caneditfromstage.equalsIgnoreCase("true")){
		AllCasesPassedByBranch updcaseToMyBranch = new AllCasesPassedByBranch();
		updcaseToMyBranch.setCaseId(Integer.parseInt(caseidfromstage));
		//Render(updcase  , out , request, response , Myglobals , objectState , pageName1);
		mainProjectPath = servletContext.getContextPath()+"";
		
		String ValidationMsg="";
		String pageName = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		String pageNameWithoutDot =pageName.replace("." , "DOT").toUpperCase();
		
		//Class userClass = Class.forName(className);
		//CoreMgr updcase = (CoreMgr) userClass.newInstance();
		updcaseToMyBranch.setAPPPATH(mainProjectPath);
		//System.out.println("------>"+mainProjectPath);
		updcaseToMyBranch.setConn(conn1);
		updcaseToMyBranch.setHTTPSRequest(request);
		updcaseToMyBranch.setLu((LoginUser)request.getSession().getAttribute("lu"));
		updcaseToMyBranch.setarrayGlobals(Myglobals.smartyGlobalsAssArr);
		String className = updcaseToMyBranch.getClass().getCanonicalName();
		updcaseToMyBranch.setmyClassBean(className);
		
		updcaseToMyBranch.setJspName(pageName);
		updcaseToMyBranch.setJspNameWithoutDot(pageNameWithoutDot);
		updcaseToMyBranch.initialize(objectState.smartyStateMap);
		if (request.getParameter("upd")!=null && request.getParameter("myClassBean")!=null ){
			if (request.getParameter("myClassBean").equals(className) && request.getParameter("upd").equals("1")){
				ValidationMsg= updcaseToMyBranch.doUpdate(request,true);
				out.println("<script>location.replace('?caneditfromstage="+caneditfromstage+"&branchidfromstage="+branchidfromstage+"&caseidfromstage="+caseidfromstage+"');</script>");
			}
		}else{
			out.print(updcaseToMyBranch.getUpdForm(request, caseidfromstage));
		}
		
		
	}else{
		out.print("لايمكن التعديل على الشحنة لانها متحاسب عليها");
	}
}catch (Exception e){
	try{conn1.rollback();}catch(Exception erool){}
}finally{
	try{conn1.close();}catch(Exception erool){}
}
	
%> 
<div class="row"><div class="col-md-offset-4 col-md-2 col-xs-offset-6 col-xs-3">
<input type="button" class="btn btn-danger" onclick="window.close();" value="إغلاق" /></div></div>
<jsp:include page="../Main/footer-popup.jsp" />
<script>
window.onunload = refreshParent;
function refreshParent() {window.opener.location.reload();}

$("#c_rcv_state").on("change", enableDesableAgent);

function enableDesableAgent(){
	console.log('state_changed');
	if(document.getElementsByName("com.app.cases.Updatecase").length){
	//$('#c_assignedagent').removeAttr('required');
		var destCity= $("#c_rcv_state").val();
		
	 	if (destCity !== undefined && destCity != null ){
	 		var dataToSend = {"destState":destCity};
			$.post('../../HideAgentByEditeCaseSRVL' , dataToSend, function(data, status){ 
					//alert("Data: " + data +", Status:" + status);
					if (status=='success'){
						if(data === 'false'){
							$('#c_assignedagent').removeAttr('required');
						}else{
							$('#c_assignedagent').attr("required",true);
						}
							
					}else{
						alert("Error, please contact MR.NAFIE");
					}
			 });
		}
	}
}
</script>