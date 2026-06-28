<%
String mainProjectPathFooter = getServletContext().getContextPath()+"";
%>
		</div>
	</div>
</div>
   <!-- BEGIN VENDOR JS-->
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/vendors.min.js" type="text/javascript"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/forms/select/select2.full.min.js"></script>
  <!-- BEGIN VENDOR JS-->
  <!-- BEGIN PAGE VENDOR JS-->
  <script type="text/javascript" src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/ui/jquery.sticky.js"></script>
  <script type="text/javascript" src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/charts/jquery.sparkline.min.js"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/charts/chart.min.js" type="text/javascript"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/charts/raphael-min.js" type="text/javascript"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/charts/morris.min.js" type="text/javascript"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/charts/jvector/jquery-jvectormap-2.0.3.min.js"
  type="text/javascript"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/charts/jvector/jquery-jvectormap-world-mill.js"
  type="text/javascript"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/data/jvector/visitor-data.js" type="text/javascript"></script>
  <!-- END PAGE VENDOR JS-->
  <!-- BEGIN MODERN JS-->
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/js/core/app-menu.js" type="text/javascript"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/js/core/app.js" type="text/javascript"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/js/scripts/customizer.js" type="text/javascript"></script>
  <!-- END MODERN JS-->
  <!-- BEGIN PAGE LEVEL JS-->
  <script type="text/javascript" src="<%=mainProjectPathFooter%>/smartyresources/app-assets/js/scripts/ui/breadcrumbs-with-stats.js"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/js/scripts/pages/dashboard-sales.js" type="text/javascript"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/js/scripts/forms/select/form-select2.js"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/lobibox/lobibox.js"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/js/scripts/pages/app-chat.js"></script>
  <!-- END PAGE LEVEL JS-->
	
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
	if (!smarty_preventSingleSelectRender){
		$('.single-select').select2({
			theme: 'bootstrap4',
			width: $(this).data('width') ? $(this).data('width') : $(this).hasClass('w-100') ? '100%' : 'style',
			placeholder: $(this).data('placeholder'),
			allowClear: Boolean($(this).data('allow-clear')),
		});
	}
	$('.multiple-select').select2({
		theme: 'bootstrap4',
		width: $(this).data('width') ? $(this).data('width') : $(this).hasClass('w-100') ? '100%' : 'style',
		placeholder: $(this).data('placeholder'),
		allowClear: Boolean($(this).data('allow-clear')),
	});
}
);


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

	
	
	

$(function() {
	"use strict";

  

	 $(".mobile-search-icon").on("click", function() {
		$(".search-bar").addClass("full-search-bar")
	}), $(".search-close").on("click", function() {
		$(".search-bar").removeClass("full-search-bar")
	}), $(".mobile-toggle-menu").on("click", function() {
		$(".wrapper").addClass("toggled")
	}), $(".toggle-icon").click(function() {
		$(".wrapper").hasClass("toggled") ? ($(".wrapper").removeClass("toggled"), $(".sidebar-wrapper").unbind("hover")) : ($(".wrapper").addClass("toggled"), $(".sidebar-wrapper").hover(function() {
			$(".wrapper").addClass("sidebar-hovered")
		}, function() {
			$(".wrapper").removeClass("sidebar-hovered")
		}))
	}), $(document).ready(function() {
		$(window).on("scroll", function() {
			$(this).scrollTop() > 300 ? $(".back-to-top").fadeIn() : $(".back-to-top").fadeOut()
		}), $(".back-to-top").on("click", function() {
			return $("html, body").animate({
				scrollTop: 0
			}, 600), !1
		})
	}),

	$(document).ready(function () {
			$(window).on("scroll", function () {
				if ($(this).scrollTop() > 60) {
					$('.topbar').addClass('bg-dark');
				} else {
					$('.topbar').removeClass('bg-dark');
				}
			});
			$('.back-to-top').on("click", function () {
				$("html, body").animate({
					scrollTop: 0
				}, 600);
				return false;
			});
		});


	$(function() {
		for (var e = window.location, o = $(".metismenu li a").filter(function() {
				return this.href == e
			}).addClass("").parent().addClass("mm-active"); o.is("li");) o = o.parent("").addClass("mm-show").parent("").addClass("mm-active")
	}), $(function() {
		;
	}), $(".chat-toggle-btn").on("click", function() {
		$(".chat-wrapper").toggleClass("chat-toggled")
	}), $(".chat-toggle-btn-mobile").on("click", function() {
		$(".chat-wrapper").removeClass("chat-toggled")
	}), $(".email-toggle-btn").on("click", function() {
		$(".email-wrapper").toggleClass("email-toggled")
	}), $(".email-toggle-btn-mobile").on("click", function() {
		$(".email-wrapper").removeClass("email-toggled")
	}), $(".compose-mail-btn").on("click", function() {
		$(".compose-mail-popup").show()
	}), $(".compose-mail-close").on("click", function() {
		$(".compose-mail-popup").hide()
	}),
	
	
	$(".switcher-btn").on("click", function() {
		$(".switcher-wrapper").toggleClass("switcher-toggled")
	}), $(".close-switcher").on("click", function() {
		$(".switcher-wrapper").removeClass("switcher-toggled")
	}),


	$('#theme1').click(theme1);
    $('#theme2').click(theme2);
    $('#theme3').click(theme3);
    $('#theme4').click(theme4);
    $('#theme5').click(theme5);
    $('#theme6').click(theme6);
    $('#theme7').click(theme7);
    $('#theme8').click(theme8);
    $('#theme9').click(theme9);
    $('#theme10').click(theme10);
    $('#theme11').click(theme11);
    $('#theme12').click(theme12);
    $('#theme13').click(theme13);
    $('#theme14').click(theme14);
    $('#theme15').click(theme15);

    function theme1() {
      $('body').attr('class', 'bg-theme bg-theme1');
    }

    function theme2() {
      $('body').attr('class', 'bg-theme bg-theme2');
    }

    function theme3() {
      $('body').attr('class', 'bg-theme bg-theme3');
    }

    function theme4() {
      $('body').attr('class', 'bg-theme bg-theme4');
    }
	
	function theme5() {
      $('body').attr('class', 'bg-theme bg-theme5');
    }
	
	function theme6() {
      $('body').attr('class', 'bg-theme bg-theme6');
    }

    function theme7() {
      $('body').attr('class', 'bg-theme bg-theme7');
    }

    function theme8() {
      $('body').attr('class', 'bg-theme bg-theme8');
    }

    function theme9() {
      $('body').attr('class', 'bg-theme bg-theme9');
    }

    function theme10() {
      $('body').attr('class', 'bg-theme bg-theme10');
    }

    function theme11() {
      $('body').attr('class', 'bg-theme bg-theme11');
    }

    function theme12() {
      $('body').attr('class', 'bg-theme bg-theme12');
    }

	function theme13() {
		$('body').attr('class', 'bg-theme bg-theme13');
	  }
	  
	  function theme14() {
		$('body').attr('class', 'bg-theme bg-theme14');
	  }
	  
	  function theme15() {
		$('body').attr('class', 'bg-theme bg-theme15');
	  }
	  
	  if (localStorage.getItem('bodyClass'))
		 	 $('body').attr('class', localStorage.getItem('bodyClass'));
});


function generalErrorPrettyMsg(msg) {
	Lobibox.notify('error', {
		pauseDelayOnHover: true,
		continueDelayOnInactiveTab: false,
		position: 'top center',
		icon: 'bx bx-x-circle',
		title:'خطأ',
		size: 'normal',
		msg: msg,

	});
}

function receiptNotFoundAlert(msg) {
	Lobibox.notify('error', {
		pauseDelayOnHover: true,
		continueDelayOnInactiveTab: false,
		position: 'top center',
		icon: 'bx bx-x-circle',
		title:'خطأ',
		size: 'normal',
		msg: msg,

	});
}

function towReceiptsWithSameNumberAlert(msg) {
	Lobibox.notify('warning', {
		pauseDelayOnHover: true,
		continueDelayOnInactiveTab: false,
		position: 'center top',
		title:'تحذير',
		size: 'normal',
		icon: 'bx bx-error',
		msg: msg,

	});
}

function showTotalSelectedAmtForCustomers(msg){
	Lobibox.notify('info', {
		pauseDelayOnHover: true,
		continueDelayOnInactiveTab: false,
		position: 'center top',
		title:'المبلغ المحدد للان',
		size: 'normal',
		icon: 'bx bx-info-circle',
		msg: msg,

	});
}	
</script>
</html>