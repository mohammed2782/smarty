<%@page import="com.app.db.mysql"%>
<%@ include file="bootstrap-popup.jsp"%>
<%@ page 
	import="java.util.List , java.util.HashMap, java.util.Map , com.app.core.*,java.util.Set , java.util.Iterator  ,javax.servlet.jsp.JspWriter"%>
<%@ page 
	import="java.sql.Connection, com.app.core.CoreMgr , com.app.core.FilesExport,java.net.URLEncoder , java.util.List , java.util.HashMap , 
	java.util.ArrayList , java.util.Iterator , java.util.*  , javax.* , java.io.FileInputStream , java.io.BufferedInputStream,
	java.io.IOException"%>

<jsp:useBean id="objectState" class="com.app.core.smartyState" scope="session" />
<jsp:useBean id="myCore" class="com.app.core.CoreMgr" scope="request" />


<%
/*the next two lines are used for the arabic characters */
request.setCharacterEncoding("UTF-8"); 
response.setCharacterEncoding("UTF-8");

%>
<%! public void Render(CoreMgr mgr , 
					   JspWriter myout ,   
					   HttpServletRequest request ,
					   HttpServletResponse response ,
					   smartyglobals Myglobals,
					   smartyState currentState,
					   String pageName){
	
	Connection conn = null;
	String className = mgr.getClass().getCanonicalName();
	try{
	
		conn =  mysql.getConn();
		ServletContext servletContext = getServletContext();
		String mainProjectPath = servletContext.getContextPath()+"";
		
		String ValidationMsg="";
		String pageNameWithoutDot =pageName.replace("." , "DOT").toUpperCase();
		
		//Class userClass = Class.forName(className);
		//CoreMgr mgr = (CoreMgr) userClass.newInstance();
		mgr.setAPPPATH(mainProjectPath);
		
		mgr.setConn(conn);
		mgr.setHTTPSRequest(request);
		mgr.setLu((LoginUser)request.getSession().getAttribute("lu"));
		mgr.setarrayGlobals(Myglobals.smartyGlobalsAssArr);
		mgr.setmyClassBean(className);
		
		mgr.setJspName(pageName);
		mgr.setJspNameWithoutDot(pageNameWithoutDot);
		
		if (mgr.isDebug()){
			myout.println("<p>Class Name is=>"+className+"</p>");
		}
		
		String whichAction = null; 
		if (request.getParameter("op")!=null && request.getParameter("myClassBean")!=null ){
			if (request.getParameter("myClassBean").equals(className)){
				whichAction = request.getParameter("op");
			}
		}
		
		if (whichAction!=null){
			if (whichAction.equals("new")){
				mgr.setDisplayMode("NEWSINGLE");
			}else if (whichAction.equals("upd")){
				mgr.setDisplayMode("EDITSINGLE");
			}
		}
		if (request.getParameter("new")!=null && request.getParameter("myClassBean")!=null ){
			if (request.getParameter("myClassBean").equals(className) && request.getParameter("new").equals("1")){
				mgr.setDisplayMode("NEWSINGLE"); // to reload the same settings when we stay on the same new form
			}
		}
		if (request.getParameter("upd")!=null && request.getParameter("myClassBean")!=null ){
			if (request.getParameter("myClassBean").equals(className) && request.getParameter("upd").equals("1")){
				mgr.setUpdateAction(true);
			}
		}
		if (request.getParameter("new")!=null && request.getParameter("myClassBean")!=null ){
			if (request.getParameter("myClassBean").equals(className) && request.getParameter("new").equals("1")){
				mgr.setInsertAction(true);
			}
		}
		mgr.initialize(currentState.smartyStateMap);
		if (request.getParameter("op")!= null){
			if (request.getParameter("op").equals("del")){
				if (request.getParameter("myClassBean").equals(className)){
					ValidationMsg= mgr.doDelete(request);
					myout.println("<script>"+
					    "window.onunload = refreshParent;"+
							" function refreshParent() {"+
							   " window.opener.location.reload();"+
							"}</script>");
					whichAction = null;	
				}
			}
		}
		if (request.getParameter("smartyValidationMsg")!=null){
 			myout.println("<div align='center'><p><font color='red'>"+request.getParameter("smartyValidationMsg")+"</font></p></div>");
 		}
		if (mgr.isInsertAction()){
			whichAction = null;
			ValidationMsg= mgr.doInsert(request,true);
			myout.println("<div align='center'><p><font color='red'>"+ValidationMsg+"</font></p></div>");
			if (!mgr.isInsertErrorFlag()){
				myout.println("<script>window.onunload = refreshParent;"+
							" function refreshParent() {"+
							   " window.opener.location.reload();"+
							"}</script>");
				String encodedRedirectUrl = URLEncoder.encode(ValidationMsg, java.nio.charset.StandardCharsets.UTF_8.toString());
				response.sendRedirect(request.getRequestURI()+"?smartyValidationMsg="+encodedRedirectUrl);
				return;
			}
			
			whichAction = "new";	
		}
		if (mgr.isUpdateAction()){
			whichAction = null;
			ValidationMsg= mgr.doUpdate(request,true);
			if (!mgr.isUpdateErrorFlag()){
				myout.println("<script>window.onunload = refreshParent;"+
							" function refreshParent() {"+
							   " window.opener.location.reload();"+
							"}</script>");
				String encodedRedirectUrl = URLEncoder.encode(ValidationMsg, java.nio.charset.StandardCharsets.UTF_8.toString());
				response.sendRedirect(request.getRequestURI()+"?smartyValidationMsg="+encodedRedirectUrl);
				return;
			}
			
			myout.println("<div align='center'><p><font color='red'>"+ValidationMsg+"</font></p></div>");
		}
		
		
		//myout.print("---->"+mgr.getDisplayMode());
		if (mgr.getDisplayMode()!=null && !mgr.getDisplayMode().equalsIgnoreCase("")){
			if (mgr.getDisplayMode().equalsIgnoreCase("NEWSINGLE")){	
				showNewForm (mgr ,   myout ,  request);
			}else if (mgr.getDisplayMode().equalsIgnoreCase("EDITSINGLE")){
				showUpdForm (mgr ,   myout ,  request);
			}else if (mgr.getDisplayMode().equalsIgnoreCase("GRIDEDIT")){
				if (mgr.canFilter){
					myout.print(mgr.DisplayFilters()); 	
				}
				myout.print(mgr.getMultiEditGrid()); 
			}else if (mgr.getDisplayMode().equalsIgnoreCase("DISPLAYSINGLE")){
				myout.print(mgr.displaySingleRecordForm(request));
			}else if (mgr.getDisplayMode().equalsIgnoreCase("LIST")){
				showListing( mgr ,   myout ,  request , ValidationMsg );
			}else{
				myout.println("<h3 style='color:red'>Dispaly Mode is "+mgr.getDisplayMode()+" which is not valid ,set, contact Mohammed Nafie</h3>");
			}
		}else{
			myout.println("<h3 style='color:red'>Dispaly Mode is not set, contact Mohammed Nafie</h3>");
		}
				
		
	}catch (Exception e){
		e.printStackTrace();
	}finally{
		try{
			conn.close();
		}catch(Exception e){
			/*ignore*/
		}
	}
	
}
%>

<%!
 public void showListing(CoreMgr mgr ,  JspWriter myout , HttpServletRequest request , String validationMsg){
	String OPParam = null;
	try{
		if (request.getParameter("op")!= null){
			OPParam = request.getParameter("op");
		}	
		Map<String, String[]> fltr_param = new HashMap <String , String[]>();
		if (request.getParameter("filter")!=null){
		 
		}
		if (mgr.canFilter){
			myout.print(mgr.DisplayFilters()); 
			
		}
		try{
			if (validationMsg!=null)
				if (!validationMsg.equals(""))
					myout.println("<div align='center' style='width:40%;margin-left:auto;margin-right:auto;' ><p  style='background-color:white;height:1cm;'><font color='red'>"+validationMsg+"</font></p></div>");
			myout.print(mgr.genListing()); 
				
		}catch(Exception e){
			e.printStackTrace();
		}
	}catch(Exception e){
		e.printStackTrace();
		System.out.println("in the showlising fucntion@Main.jsp");
	}
}
%>
 
<%!
public void showNewForm( CoreMgr mgr ,  JspWriter myout , HttpServletRequest request){
	try{
		myout.print(mgr.getNewForm() );
	}catch(Exception e){
		e.printStackTrace();
		System.out.println("Error at showNewForm Method-Main.jsp");
	}
}

public void showUpdForm( CoreMgr mgr ,  JspWriter myout , HttpServletRequest request){
	System.out.println("Main.jsp-Method:showUpdForm");
	try{
		myout.print(mgr.getUpdForm(request)); 
	}catch(Exception e){
		e.printStackTrace();
		System.out.println("Error at showNewForm Method-Main.jsp");
	}
}


%>
<script>
function doDeleteSmarty(xThis , msg ,keyCol, keyVal , className){
	
	if (!confirm (msg)) return false;
	var data = keyCol+'='+ encodeURIComponent(keyVal) + '&className='+ encodeURIComponent(className);
	//alert(data);
	$.ajax({
		url: "../GeneralDelete",
		type: "POST",
		data: data,
		cache: false,
	    processData: false,
		beforeSend: function(){
			//alert('ok');
			//form.prepend( form_status.html('<p><i class="fa fa-spinner fa-spin"></i> Application in progrees...</p>').fadeIn() );
		}
	}).done(function(data){
		if (data=='err'){
			alert('an Error Happened While Deleting, Please contact IT officer');
		}else if(data=='ref'){
			location.reload();
		}else if(data != ""){
			alert(data);
		}else{
			$("tr[smartykeycolval='" + keyVal + "']").delay(200).fadeOut();	
		}
	});
	return false;
}
function clearFieStyleSelector(colId , defVal , req){
	var elem = $("[placeholder='"+defVal+"']");
	var attrVal = elem.attr('placeholder');
	if (req=='false'){
		elem.attr('placeholder','');
	}else{// if required
		if (attrVal!='') //and have value 
			elem.attr('required','');
	}
	$("#"+colId).filestyle('clear');
	return false;
}
</script>
