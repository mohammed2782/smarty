<%
String mainProjectPathFooter = getServletContext().getContextPath();
%>
</div></div></div>
        <!-- footer content -->
        <footer>
          <div class="pull-left">
            Delivery OnTime System (DOTS) developed by softecha - Powered by SMARTYJ FrameWork Author Mohammed Nafie
            
          </div>
          <div class="clearfix"></div>
        </footer>
        <!-- /footer content -->
      

    <!-- jQuery -->
    
    <!-- Bootstrap -->
    <script src="<%=mainProjectPathFooter%>/smartyresources/js/bootstrap-rtl.min.js"></script>
   
    <!-- iCheck -->
    <script src="<%=mainProjectPathFooter%>/smartyresources/js/icheck.min.js"></script>
    <!-- sparkline -->
    <script src="<%=mainProjectPathFooter%>/smartyresources/js/jquery.sparkline.min.js"></script>
	 <!-- NProgress -->
    <script src="<%=mainProjectPathFooter%>/smartyresources/js/nprogress.js"></script>
    <!-- NProgress -->
    <script src="<%=mainProjectPathFooter%>/smartyresources/js/bootstrap-progressbar.min.js"></script>
    <!-- Custom Theme Scripts -->
    <script src="<%=mainProjectPathFooter%>/smartyresources/js/custom.min.js"></script>
 	<script src="<%=mainProjectPathFooter%>/smartyresources/js/bootstrap-filestyle.min.js"></script>
    <script src="<%=mainProjectPathFooter%>/smartyresources/js/select2.full.min.js"></script>
    <!-- Chart.js -->
    <script src="<%=mainProjectPathFooter%>/smartyresources/js/dist/Chart.min.js"></script>   
    <script src="<%=mainProjectPathFooter%>/smartyresources/js/jquery-confirm.min.js"></script>
   
	
</body>
<script>
function popitup (url , title , w , h){
	  // Fixes dual-screen position                         Most browsers      Firefox
  var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
  var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;

  var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
  var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

  var left = ((width / 2) - (w / 2)) + dualScreenLeft;
  var top = ((height / 2) - (h / 2)) + dualScreenTop;
  var newWindow = window.open(url, title, 'scrollbars=yes, width=' + w + ', height=' + h + ', top=' + top + ', left=' + left);

  // Puts focus on the newWindow
  if (window.focus) {
      newWindow.focus();
  }
}


$(document).ready(function() {
  $(".select2_single").select2({
    placeholder: "Type to Search",
    allowClear: true
  });
  
  init_InputMask();
}
);

/* INPUT MASK */

function init_InputMask() {
	
	if( typeof ($.fn.inputmask) === 'undefined'){ return; }
		console.log('init_InputMask');
		$(":input").inputmask();
		
};

if (smarty_submitButton_allow_disable){
jQuery(function() {
	  $("form").submit(function() {
			// submit more than once return false
			$(this).submit(function() {
				return false;
			});
			// submit once return true
			return true;
		});
	});

	/* or */
jQuery('form').submit(function(){
	$(this).find("button[type='submit'][name='dosearch']").attr( 'disabled','disabled' );
	$(this).find("button[type='submit'][value='save']").attr( 'disabled','disabled' );
	$(this).find("button[type='submit'][value='cancel']").attr( 'disabled','disabled' );
	});
}

function hideLoader() {
    $('#loading').hide();
}

$(window).on("load", hideLoader);

// Strongly recommended: Hide loader after 20 seconds, even if the page hasn't finished loading
setTimeout(hideLoader, 20 * 1000);
</script>
</html>