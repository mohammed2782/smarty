<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page
	import="smarty.security.*"%> 
<% 
LoginUser user = new LoginUser();
HttpSession sessionRQS = request.getSession();
boolean error = false;
String errorMsg ="";
if (sessionRQS.getAttribute("lu")!=null){
	user = (LoginUser)sessionRQS.getAttribute("lu");
} 
	if(user.isLoggedIn()){
		String redirectURL = "";
		if (user.getRank_code().equalsIgnoreCase("MASTERCUSTOMER")){
			redirectURL ="./Mainctrl/custcases/allcustcases";
		}else if (user.getRank_code().equalsIgnoreCase("PICKUPAGENT")){
			redirectURL ="./Mainctrl/cases/PickUpAgentCasesViewOnly";
		}else if (user.getRank_code().equalsIgnoreCase("DLVAGENT")){
			redirectURL ="./Mainctrl/AgentHome/singleAGENTOP_ONWAY";
		}else{
			redirectURL ="./Mainctrl/home/home";
		}
		response.sendRedirect(redirectURL); 
	}else{
		error = true;
		errorMsg = user.getErrorMsg();
	}
%>
<!DOCTYPE html>
<html class="loading" lang="en" data-textdirection="rtl">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimal-ui">
  <meta name="description" content="Modern admin is super flexible, powerful, clean &amp; modern responsive bootstrap 4 admin template with unlimited possibilities with bitcoin dashboard.">
  <meta name="keywords" content="admin template, modern admin template, dashboard template, flat admin template, responsive admin template, web app, crypto dashboard, bitcoin dashboard">
  <meta name="author" content="PIXINVENT">
  <title>MDS
  </title>
  <link rel="apple-touch-icon" href="./smartyresources/app-assets/images/logo/logo-xsm.png">
  <link rel="shortcut icon" type="image/x-icon" href="./smartyresources/app-assets/images/logo/logo-xsm.png">
  <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,300i,400,400i,600,600i,700,700i|Quicksand:300,400,500,700"
  rel="stylesheet">
  <link href="https://maxcdn.icons8.com/fonts/line-awesome/1.1/css/line-awesome.min.css"
  rel="stylesheet">
  <!-- BEGIN VENDOR CSS-->
  <link rel="stylesheet" type="text/css" href="./smartyresources/app-assets/css-rtl/vendors.css">
  <link rel="stylesheet" type="text/css" href="./smartyresources/app-assets/vendors/css/forms/icheck/icheck.css">
  <link rel="stylesheet" type="text/css" href="./smartyresources/app-assets/vendors/css/forms/icheck/custom.css">
  <!-- END VENDOR CSS-->
  <!-- BEGIN MODERN CSS-->
  <link rel="stylesheet" type="text/css" href="./smartyresources/app-assets/css-rtl/app.css">
  <link rel="stylesheet" type="text/css" href="./smartyresources/app-assets/css-rtl/custom-rtl.css">
  <!-- END MODERN CSS-->
  <!-- BEGIN Page Level CSS-->
  <link rel="stylesheet" type="text/css" href="./smartyresources/app-assets/css-rtl/core/menu/menu-types/horizontal-menu.css">
  <link rel="stylesheet" type="text/css" href="./smartyresources/app-assets/css-rtl/core/colors/palette-gradient.css">
  <link rel="stylesheet" type="text/css" href="./smartyresources/app-assets/css-rtl/pages/login-register.css">
  <!-- END Page Level CSS-->
  <!-- BEGIN Custom CSS-->
  <link rel="stylesheet" type="text/css" href="./smartyresources/assets/css/style-rtl.css">
  <!-- END Custom CSS-->
</head>
<body class="horizontal-layout horizontal-menu 1-column  bg-cyan bg-lighten-2 menu-expanded fixed-navbar"
data-open="hover" data-menu="horizontal-menu" style="background-color: #c35b31 !important" data-col="1-column">
  <!-- fixed-top-->
  <nav class="header-navbar navbar-expand-md navbar navbar-with-menu navbar-without-dd-arrow fixed-top navbar-light navbar-brand-center">
    <div class="navbar-wrapper">
      <div class="navbar-header">
        <ul class="nav navbar-nav flex-row">
          <li class="nav-item mobile-menu d-md-none mr-auto"><a class="nav-link nav-menu-main menu-toggle hidden-xs" href="#"><i class="ft-menu font-large-1"></i></a></li>
          
          <li class="nav-item d-md-none">
            <a class="nav-link open-navbar-container" data-toggle="collapse" data-target="#navbar-mobile"><i class="la la-ellipsis-v"></i></a>
          </li>
        </ul>
      </div>
      <div class="navbar-container">
        <div class="collapse navbar-collapse justify-content-end" id="navbar-mobile">
          <ul class="nav navbar-nav">
          
            <li class="dropdown nav-item">
              
            </li>
          </ul>
        </div>
      </div>
    </div>
  </nav>
  <!-- ////////////////////////////////////////////////////////////////////////////-->
  <div class="app-content content">
    <div class="content-wrapper">
      <div class="content-header row">
      </div>
      <div class="content-body">
        <section class="flexbox-container">
          <div class="col-12 d-flex align-items-center justify-content-center">
            <div class="col-md-4 col-10 box-shadow-2 p-0">
              <div class="card border-grey border-lighten-3 m-0">
                <div class="card-header border-0">
                  <div class="card-title text-center">
                    <img src="./smartyresources/app-assets/images/logo/logo-m.png" alt="branding logo">
                  </div>
                  <h6 class="card-subtitle line-on-side text-muted text-center font-small-3 pt-2">
                    <span>Login with Modern Delivery -  MDS </span>
                  </h6>
                </div>
                <div class="card-content">
                  <div class="card-body">
                    <form action='./DoLogin'  class="form-horizontal" method="post"" novalidate>
                      <fieldset class="form-group position-relative has-icon-left">
                        <input type="text" class="form-control input-lg" id="userid" name="userid" placeholder="معرف الدخول"
                        tabindex="1" required data-validation-required-message="Please enter your username.">
                        <div class="form-control-position">
                          <i class="ft-user"></i>
                        </div>
                        <div class="help-block font-small-3"></div>
                      </fieldset>
                      <fieldset class="form-group position-relative has-icon-left">
                        <input type="password" class="form-control input-lg" id="password" name ='userpassword' placeholder="كلمة المرور"
                        tabindex="2" required data-validation-required-message="Please enter valid passwords.">
                        <div class="form-control-position">
                          <i class="la la-key"></i>
                        </div>
                        <div class="help-block font-small-3"></div>
                      </fieldset>
                      
                      <button type="submit" style='background-color: #d6d7d9;' class="btn btn-block btn-lg"><i class="ft-unlock"></i> Login</button>
                    </form>
                  </div>
                </div>
                <div class="card-footer border-0">
                  <p class="card-subtitle line-on-side text-muted text-center font-small-3 mx-2 my-1">
                    <span>MDS</span>
                  </p>
                 
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
  <!-- ////////////////////////////////////////////////////////////////////////////-->
  <footer class="footer fixed-bottom footer-dark navbar-shadow">
    <p class="clearfix blue-grey lighten-2 text-sm-center mb-0 px-2">
      <span class="float-md-left d-block d-md-inline-block">Copyright &copy; 2022 <a class="text-bold-800 grey darken-2" href="#"
        target="_blank">NAFI# </a>, All rights reserved. </span>
      <span class="float-md-right d-block d-md-inline-blockd-none d-lg-block">Hand-crafted & Made with <i class="ft-heart pink"></i></span>
    </p>
  </footer>
  <!-- BEGIN VENDOR JS-->
  <script src="./smartyresources/app-assets/vendors/js/vendors.min.js" type="text/javascript"></script>
  <!-- BEGIN VENDOR JS-->
  <!-- BEGIN PAGE VENDOR JS-->
  <script type="text/javascript" src="./smartyresources/app-assets/vendors/js/ui/jquery.sticky.js"></script>
  <script type="text/javascript" src="./smartyresources/app-assets/vendors/js/charts/jquery.sparkline.min.js"></script>
  <script src="./smartyresources/app-assets/vendors/js/forms/validation/jqBootstrapValidation.js"
  type="text/javascript"></script>
  <script src="./smartyresources/app-assets/vendors/js/forms/icheck/icheck.min.js" type="text/javascript"></script>
  <!-- END PAGE VENDOR JS-->
  <!-- BEGIN MODERN JS-->
  <script src="./smartyresources/app-assets/js/core/app-menu.js" type="text/javascript"></script>
  <script src="./smartyresources/app-assets/js/core/app.js" type="text/javascript"></script>
  <script src="./smartyresources/app-assets/js/scripts/customizer.js" type="text/javascript"></script>
  <!-- END MODERN JS-->
  <!-- BEGIN PAGE LEVEL JS-->
  <script type="text/javascript" src="./smartyresources/app-assets/js/scripts/ui/breadcrumbs-with-stats.js"></script>
  <script src="./smartyresources/app-assets/js/scripts/forms/form-login-register.js" type="text/javascript"></script>
  <!-- END PAGE LEVEL JS-->
</body>
<script>

function getLocation() {   
    console.log('getLocation was called') 
    if(navigator.geolocation) {
       navigator.geolocation.getCurrentPosition(showPosition, 
       positionError);
    } else {
       console.log('Geolocation is not supported by this device')
    }
}

function positionError() {    
	console.log("failed");
}
function showPosition(position){
    console.log('posititon accepted,'+position.coords.latitude+", long==>"+position.coords.longitude)
}
getLocation();
</script>
</html>


