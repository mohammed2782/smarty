<%@page import="smarty.security.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page 
	import="smarty.db.mysql,java.util.List,java.util.HashMap,java.util.ArrayList,java.util.Iterator,
	java.util.Map,java.io.File"%>

<jsp:useBean id="Myglobals" class="smarty.core.smartyglobals" scope="session" />
<%
/*the next two lines are used for the arabic characters */ 
request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");
HttpSession sessionRQS = request.getSession();
LoginUser user = new LoginUser();
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
	
}else if (Myglobals.smartyGlobalsAssArr.get("user)")!=null){
	sessionRQS.setAttribute("lu", (LoginUser)Myglobals.smartyGlobalsAssArr.get("user"));
	
}else{
	// kick out
	//response.sendRedirect("./index.jsp");
	//return;
}
ServletContext servletContext = getServletContext();
String mainProjectPath = servletContext.getContextPath()+"";

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
	 <script src="<%=mainProjectPath%>/smartyresources/assets/js/jquery.min.js"></script>
	<link rel="apple-touch-icon" href="<%=mainProjectPath%>/smartyresources/app-assets/images/ico/apple-icon-120.png">
  <link rel="shortcut icon" type="image/x-icon" href="<%=mainProjectPath%>/smartyresources/app-assets/images/ico/favicon.ico">
  <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,300i,400,400i,600,600i,700,700i|Quicksand:300,400,500,700"
  rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Cairo:wght@200;300;400&family=Tajawal:wght@500;700&display=swap" rel="stylesheet">
  <link href="https://maxcdn.icons8.com/fonts/line-awesome/1.1/css/line-awesome.min.css"
  rel="stylesheet">
  <!-- BEGIN VENDOR CSS-->
   <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/vendors/css/vendors-rtl.min.css">
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/vendors.css">
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/vendors/css/forms/selects/select2.min.css">
  <!-- END VENDOR CSS-->
  <!-- BEGIN Page Level CSS-->
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/core/menu/menu-types/material-horizontal-menu.css">
  <%-- <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/core/menu/menu-types/horizontal-menu.css"> --%>
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/core/colors/palette-gradient.css">
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/vendors/css/charts/jquery-jvectormap-2.0.3.css">
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/vendors/css/charts/morris.css">
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/fonts/simple-line-icons/style.css">
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/core/colors/palette-gradient.css">
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/fonts/line-awesome/css/line-awesome.min.css">
  <link rel="stylesheet" href="https://cdnjs.Cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" />
  
    <!-- BEGIN: Theme CSS-->
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/material.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/components.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/bootstrap-extended.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/colors.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/material-extended.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/material-colors.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/custom-rtl.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/fonts/material-icons/material-icons.css">
    <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/vendors/css/lobibox/lobibox.css">
    <!-- END: Theme CSS-->
  <!-- END Page Level CSS-->
  <!-- BEGIN Custom CSS-->
   <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/app-assets/css-rtl/pages/app-chat.css">
  <link rel="stylesheet" type="text/css" href="<%=mainProjectPath%>/smartyresources/assets/css/style-rtl.css">
  <!-- END Custom CSS-->


</head>

<script>
var smarty_submitButton_allow_disable = true;
var smarty_updatePageTitle = true;
var smarty_preventSingleSelectRender = false;
/*
 * * deprected in favor of bootstrap-datetimepicker.css
 
var cal = Calendar.setup({
    onSelect: function(cal) { cal.hide(); },
    showTime: true
});
*/
</script>


	<body class="bg-theme bg-theme1" dir="rtl">
    <div class="app-content content">
    <div class="content-wrapper" style='padding: 0rem 0rem 0;'>
     
      <div class="content-body">
    


