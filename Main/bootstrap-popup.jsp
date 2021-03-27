<%@page import="com.app.site.security.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page
	import="com.app.db.mysql,java.util.List,java.util.HashMap,java.util.ArrayList,java.util.Iterator,
	java.util.Map,java.io.File"%>
<jsp:useBean id="MenuBean" class="com.app.core.menu" scope="session" />
<jsp:useBean id="Myglobals" class="com.app.core.smartyglobals" scope="session" />
<%
/*the next two lines are used for the arabic characters */ 
request.setCharacterEncoding("UTF-8"); 
response.setCharacterEncoding("UTF-8");
HttpSession sessionRQS = request.getSession();
LoginUser user = new LoginUser();
if (sessionRQS.getAttribute("lu")!=null){
	user = (LoginUser)sessionRQS.getAttribute("lu");
	Myglobals.smartyGlobalsAssArr.put("useridlogin", user.getUserID());
	Myglobals.smartyGlobalsAssArr.put("userRank",user.getRank_code());
	Myglobals.smartyGlobalsAssArr.put("superRank",user.getSuperRank());
	Myglobals.smartyGlobalsAssArr.put("superItRank",user.getSuperIT());
	Myglobals.smartyGlobalsAssArr.put("userstorecode",user.getStoreCode());
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
<html lang="en">
<head>
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Meta, title, CSS, favicons, etc. -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Clemance</title>

   
    <!-- Bootstrap -->
    <link href="<%=mainProjectPath%>/smartyresources/css/bootstrap-rtl.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="<%=mainProjectPath%>/smartyresources/css/font-awesome.min.css" rel="stylesheet">
    <link href="<%=mainProjectPath%>/smartyresources/css/skins/flat/green.css" rel="stylesheet">
  
	<link rel="stylesheet" href="<%=mainProjectPath%>/smartyresources/css/steel.css" />
	<link rel="stylesheet" href="<%=mainProjectPath%>/smartyresources/css/calendarcss/jscal2.css" />
	<link rel="stylesheet/less" href="<%=mainProjectPath%>/smartyresources/css/normalize.less" />
    <!-- Custom Theme Style -->
    <link href="<%=mainProjectPath%>/smartyresources/css/custom.min.css" rel="stylesheet">
	<script src="<%=mainProjectPath%>/smartyresources/js/jquery-2.1.3.min.js"></script>
	<!-- deprecated
	<script src="<%=mainProjectPath%>/smartyresources/js/calendarscript/jscal2.js" ></script>
	<script src="<%=mainProjectPath%>/smartyresources/js/calendarscript/lang/en.js"></script>
	-->
	<script src="<%=mainProjectPath%>/smartyresources/js/gen_validatorv4.js"  ></script>
	<link href="<%=mainProjectPath%>/smartyresources/css/select2.min.css" rel="stylesheet">
	
	<!-- DateTimePicker  -->
    <link href="<%=mainProjectPath%>/smartyresources/css/bootstrap-datetimepicker.css" rel="stylesheet">
     <script src="<%=mainProjectPath%>/smartyresources/js/moment.min.js"></script>
    <script src="<%=mainProjectPath%>/smartyresources/js/bootstrap-datetimepicker.min.js"></script>
   
   <style>         
      .datepicker {
        direction: rtl;
      }             
    .datepicker.dropdown-menu {
right: initial;             
      }     
    </style>
</head>

<script>
/*
 * * deprected in favor of bootstrap-datetimepicker.css
 
var cal = Calendar.setup({
    onSelect: function(cal) { cal.hide(); },
    showTime: true
});
*/
</script>

	<body class="nav-md PopUpBody" dir="rtl">
    <div class="" role="main">
 	<div class="">
            <div class="clearfix"></div>


