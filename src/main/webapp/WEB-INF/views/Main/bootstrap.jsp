<%@ page pageEncoding="utf-8" %>
<%@ page autoFlush="true" buffer="2048kb"%>
<%@ page import="smarty.security.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%> 
<%@ page
	import=" java.sql.Connection, smarty.db.mysql,java.util.List,java.util.HashMap,java.util.ArrayList,java.util.Iterator,
	java.util.Map,java.io.File"%>

<jsp:useBean id="Myglobals" class="smarty.core.smartyglobals" scope="session" />

<%
/*the next two lines are used for the arabic characters */
request.setCharacterEncoding("UTF-8"); 
response.setCharacterEncoding("UTF-8");

	
HttpSession sessionRQS = request.getSession(false);
LoginUser user = new LoginUser();
if (sessionRQS.getAttribute("lu")!=null){
	user = (LoginUser)sessionRQS.getAttribute("lu");
}
String [] pathArr = request.getRequestURI().replaceFirst("/", "").split("/");
String jspFile = pathArr[pathArr.length -1];
String folder = pathArr[pathArr.length -2];
if (user.getMenuPermissionsList().containsKey(folder)){
	  if ((jspFile).equalsIgnoreCase("casesinqueue.jsp") || (jspFile).equalsIgnoreCase("displayGlobalSearchResults.jsp") ||
			  (jspFile).equalsIgnoreCase("displaySingleCaseInfo.jsp") ||
			  user.getMenuPermissionsList().get(folder).getSubMenuList().containsKey(jspFile.replaceFirst(".jsp", ""))){
		 ;
	  }else{
		  request.getSession().removeAttribute("lu");
		  request.getSession().removeAttribute("user");
   	  	  //System.out.println("hrererererer");
   		  response.sendRedirect("../../DoLogout");
   	  	  
   		  return;
	  }
}else{
	 Myglobals.smartyGlobalsAssArr.remove("user");
	 request.getSession().removeAttribute("lu");
	 request.getSession().removeAttribute("user");
		response.sendRedirect("../../DoLogout");
	return;
}

if (sessionRQS.getAttribute("lu")!=null){
	user = (LoginUser)sessionRQS.getAttribute("lu");
	Myglobals.smartyGlobalsAssArr.put("usid", user.getUsid());
	Myglobals.smartyGlobalsAssArr.put("userid", user.getUsid());
	Myglobals.smartyGlobalsAssArr.put("useridlogin", user.getUserID());
	Myglobals.smartyGlobalsAssArr.put("userRank",user.getRank_code());
	Myglobals.smartyGlobalsAssArr.put("superRank",user.getSuperRank());
	Myglobals.smartyGlobalsAssArr.put("superItRank",user.getSuperIT());
	Myglobals.smartyGlobalsAssArr.put("userstorecode",user.getBranchCode());
	Myglobals.smartyGlobalsAssArr.put("username",user.getGreetings("EN"));
	if (user.getMasterCustId()>0)
		Myglobals.smartyGlobalsAssArr.put("mastercustidlogin",user.getMasterCustId());
	Myglobals.smartyGlobalsAssArr.put("shopsList",user.getShopsList());
	Myglobals.smartyGlobalsAssArr.put("shopsMap",user.getShopsMap());
	Myglobals.smartyGlobalsAssArr.put("shopsCommaSeperated",user.getShopsCommaSepereated());
	Myglobals.smartyGlobalsAssArr.put("user",user);
	
}else if (Myglobals.smartyGlobalsAssArr.get("user")!=null){
	sessionRQS.setAttribute("lu", (LoginUser)Myglobals.smartyGlobalsAssArr.get("user"));
	
}else{// kick out
	//System.out.println("kick");
	response.sendRedirect("../../DoLogout");
	return;
}
ServletContext servletContext = getServletContext();
String mainProjectPath = servletContext.getContextPath();
if (mysql.error){ 
	out.println("Error in my sql conn"+mysql.errorMsg);
}	
%>

<!DOCTYPE html>
<html lang = 'ar-SA'  dir = "rtl">
<head>
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta charset="utf-8">
    <link rel="icon" href="../../smartyresources/logo/iconxsm.png" type="image/png" /> 
    
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
	 <script src="<%=mainProjectPath%>/smartyresources/app-assets/js/core/libraries/jquery.min.js"></script>
	<link rel="apple-touch-icon" href="<%=mainProjectPath%>/smartyresources/app-assets/images/ico/apple-icon-120.png">
  <link rel="shortcut icon" type="image/x-icon" href="<%=mainProjectPath%>/smartyresources/app-assets/images/logo/logo-xsm.png">
  <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,300i,400,400i,600,600i,700,700i|Quicksand:300,400,500,700"
  rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Cairo:wght@200;300;400&family=Tajawal:wght@500;700&display=swap" rel="stylesheet">
  <link href="https://maxcdn.icons8.com/fonts/line-awesome/1.1/css/line-awesome.min.css"
  rel="stylesheet">
  <!-- BEGIN VENDOR CSS-->
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/vendors.css">
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/vendors/css/forms/selects/select2.min.css">
  <!-- END VENDOR CSS-->
  <!-- BEGIN Page Level CSS-->
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/core/menu/menu-types/material-horizontal-menu.css">
  <%-- <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/core/menu/menu-types/horizontal-menu.css"> --%>
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/core/colors/palette-gradient.css">
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/vendors/css/charts/jquery-jvectormap-2.0.3.css">
  
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/fonts/simple-line-icons/style.css">
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/core/colors/palette-gradient.css">
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/fonts/line-awesome/css/line-awesome.min.css">
  <link rel="stylesheet" href="https://cdnjs.Cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" />
  <script src="<%=mainProjectPath%>/smartyresources/assets/plugins/autoNumeric/AutoNumeric.js"></script>
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/vendors/css/forms/toggle/bootstrap-switch.min.css">
    <!-- BEGIN: Theme CSS-->
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/material.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/components.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/bootstrap-extended.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/material-extended.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/material-colors.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/custom-rtl.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/fonts/material-icons/material-icons.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/vendors/css/lobibox/lobibox.css">
    <!-- END: Theme CSS-->
  <!-- END Page Level CSS-->
  <!-- BEGIN Custom CSS-->
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/assets/css/style-rtl.css">
  <!-- END Custom CSS-->
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/colors.css">
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/pages/card-statistics.css">
  <link rel="stylesheet" href="<%=mainProjectPath%>/smartyresources/assets/js/jquery-confirm.min.css">
  <link  href="<%=mainProjectPath%>/smartyresources/assets/css/sweetalert2.min.css" rel="stylesheet">
   
</head>

<body class="horizontal-layout horizontal-menu 2-columns   menu-expanded" data-open="hover"
data-menu="horizontal-menu" data-col="2-columns">
<script>
var smarty_submitButton_allow_disable = true;
var smarty_updatePageTitle = true;
var smarty_preventSingleSelectRender = false;
</script>
	<div id="loading" class='loader'>
		<div class="loader-container">
			<div class="line-scale loader-warning">
				<div></div>
				<div></div>
				<div></div>
				<div></div>
				<div></div>
			</div>
		</div>
	</div>
	<!-- fixed-top-->
  <nav class="header-navbar navbar-expand-md navbar navbar-with-menu navbar-without-dd-arrow navbar-static-top navbar-semi-light navbar-brand-center">
    <div class="navbar-wrapper">
      <div class="navbar-header">
        <ul class="nav navbar-nav flex-row">
          <li class="nav-item mobile-menu d-md-none mr-auto"><a class="nav-link nav-menu-main menu-toggle hidden-xs" href="#"><i class="ft-menu font-large-1"></i></a></li>
          <li class="nav-item" style="margin: auto;margin-top: 10px;">
          
             <!--  <img class="brand-logo" style="width:70px;" alt="modern admin logo" src="../../smartyresources/app-assets/images/logo/logo-m.png"> -->
             
          </li>
          <li class="nav-item d-md-none">
            <a class="nav-link open-navbar-container" data-toggle="collapse" data-target="#navbar-mobile"><i class="la la-ellipsis-v"></i></a>
          </li>
        </ul>
      </div>
      <div class="navbar-container content">
        <div class="collapse navbar-collapse" id="navbar-mobile">
          <ul class="nav navbar-nav mr-auto float-left">
            
            <li class="nav-item d-none d-md-block"><a class="nav-link nav-link-expand" href="#"><i class="ficon ft-maximize"></i></a></li>
            
            <li class="nav-item nav-search"><a class="nav-link nav-link-search" href="#"><i class="ficon ft-search"></i></a>
              <div class="search-input">
                <input class="input" type="text" id = 'globalSerachParamter' placeholder="Explore Modern...">
              </div>
            </li>
          </ul>
          <ul class="nav navbar-nav float-right">
            <li class="dropdown dropdown-user nav-item">
              <a class="dropdown-toggle nav-link dropdown-user-link" href="#" data-toggle="dropdown">
                <span class="mr-1">Hello,
                  <span class="user-name text-bold-700"><%=user.getGreetings("EN")%></span>
                  <%if (user.isStaff()) %>
									<p class="user-name mb-0"> <%=user.getBranchName()%></p>
                </span>
               
              </a>
              <div class="dropdown-menu dropdown-menu-right">
              <% 
 					Connection conn = null;
 					
 					try{
 						conn = mysql.getConn();
 						HashMap<Integer,String> userBranches = user.getUserBranches(conn);
 						if (userBranches.size()>1){
 							
 							for (int branchCode : userBranches.keySet()){
 								out.println("<a class='dropdown-item' href='../../ChangeBranchSRVL?current_branch="+branchCode+"'><i class='ft-user'></i><span>"+userBranches.get(branchCode)+"</span></a>");			
 	 						}
 							
 						}
 					}catch(Exception e){
 						e.printStackTrace();
 						out.println("<script>alert('Error Loading Branches');</script>");
 					}finally{
 						try{conn.close();}catch(Exception close){}
 					}
 					%>
                <div class="dropdown-divider"></div>
                <form action='../../DoLogout' id='logoutform' method='post' style="display:inline;padding-left:5px; padding-right:5px;">
					<button type='submit' class="dropdown-item" ><i class='ft-power'></i><span>خروج</span></button>
				</form>
              </div>
            </li>
           
            
          </ul>
        </div>
      </div>
    </div>
  </nav>


<!-- START OF THE PAGE BODY -->

<div class="header-navbar navbar-expand-sm navbar navbar-horizontal navbar-fixed navbar-dark navbar-without-dd-arrow navbar-shadow"
  role="navigation" data-menu="menu-wrapper">
    <div class="navbar-container main-menu-content" data-menu="menu-container">
      <ul class="nav navbar-nav" id="main-menu-navigation" data-menu="menu-navigation">
      	<%for (String mp : user.getMenuPermissionsList().keySet()){%>
      	
		    <li class="dropdown nav-item" data-menu="dropdown">
				<a class="dropdown-toggle nav-link" href="javascript:;" data-toggle="dropdown"><i class="<%=user.getMenuPermissionsList().get(mp).getIcon()%>"></i>
            		<span><%=user.getMenuPermissionsList().get(mp).getMenuName() %></span></a>
            	<ul class="dropdown-menu">
				<%if (user.getMenuPermissionsList().get(mp).getSubMenuList() !=null){
                      for (String subMp: user.getMenuPermissionsList().get(mp).getSubMenuList().keySet()){ 
	                      out.println("<li data-menu=''>");
	                      out.println("<a class='dropdown-item' data-toggle='dropdown' href='../"+mp+"/"+subMp+"'>"
	                      +user.getMenuPermissionsList().get(mp).getSubMenuList().get(subMp).getMenuName()+"</a> ");
	                      out.println("</li>");
                       } 
                    }
	               %>
	              </ul>
               </li>  
			<%
			}
		%>
       </ul>
    </div>
</div>
  <div class="app-content content">
    <div class="content-wrapper" style='padding: 1.0rem 0.7rem 0;'>
     
      <div class="content-body">
      
       
        
  <!-- ////////////////////////////////////////////////////////////////////////////-->



			

