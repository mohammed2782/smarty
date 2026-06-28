<%@ page
	import=" java.sql.Connection, smarty.db.mysql,java.util.List,java.util.HashMap,java.util.ArrayList,java.util.Iterator,
	java.util.Map,java.io.File"%>
<%@ page import="smarty.security.*"%>
<%
String mainProjectPathFooter = getServletContext().getContextPath();
HashMap<String,String> ticketSubjects = null;
HashMap<String,String> activeBranches = null;
LoginUser myLoginUser = new LoginUser();
if (request.getSession().getAttribute("lu")!=null){
	ticketSubjects = (HashMap<String,String>)request.getSession().getAttribute("TICKET_SUBJECTS_GLOBAL");
	activeBranches = (HashMap<String,String>)request.getSession().getAttribute("ACTIVE_BRANCHES_GLOBAL");
	myLoginUser = (LoginUser)request.getSession().getAttribute("lu");
}else{// kick out
	response.sendRedirect("../../DoLogout");
	return;
}
%>
		
      </div>
    </div>
  </div>
	<!-- END: Content-->

    <div class="sidenav-overlay"></div>
    <div class="drag-target"></div>
 <!-- BEGIN: Footer-->
    <footer class="footer footer-static footer-light navbar-shadow">
        <p class="clearfix blue-grey lighten-2 text-sm-center mb-0 px-2"><span class="float-md-left d-block d-md-inline-block">Copyright &copy; 2022 <a class="text-bold-800 grey darken-2" href="javascript:;" target="_blank">AlNAFI3</a></span><span class="float-md-right d-none d-lg-block">Hand-crafted & Made with<i class="ft-heart pink"></i><span id="scroll-top"></span></span></p>
    </footer>
    <!-- END: Footer-->

  <!-- BEGIN VENDOR JS-->
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/vendors.min.js" type="text/javascript"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/forms/select/select2.full.min.js"></script>
  <!-- BEGIN VENDOR JS-->
  <!-- BEGIN PAGE VENDOR JS-->
  <script type="text/javascript" src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/ui/jquery.sticky.js"></script>
  <script type="text/javascript" src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/charts/jquery.sparkline.min.js"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/charts/chart.min.js" type="text/javascript"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/charts/raphael-min.js" type="text/javascript"></script>
  
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
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/js/scripts/forms/select/form-select2.js"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/lobibox/lobibox.js"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/extensions/jquery.knob.min.js"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/app-assets/vendors/js/forms/toggle/bootstrap-switch.min.js"></script>
  
  <script src="<%=mainProjectPathFooter%>/smartyresources/assets/js/jquery-confirm.min.js"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/assets/js/sweetalert.min.js"></script>
  <script src="<%=mainProjectPathFooter%>/smartyresources/assets/js/sweetalert2.min.js"></script>
  
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
		position: 'left top',
		title:'المبلغ المحدد للان',
		size: 'mini',
		icon: 'bx bx-info-circle',
		msg: msg,

	});
}


$(document).ready(function() {
	//if (smarty_updatePageTitle){
		setTimeout(function(){ 
			console.log($("#menu > li.mm-active > ul.mm-show> li.mm-active >a").text());
			document.title  = $("#menu > li.mm-active > ul.mm-show> li.mm-active >a").text();
		},500);
	//}
	
	hideLoader();
});
function hideLoader() {
    $('#loading').css("display","none");
}
//Strongly recommended: Hide loader after 20 seconds, even if the page hasn't finished loading
setTimeout(hideLoader, 10 * 1000);




var globalSearchInput = document.getElementById("globalSerachParamter");

globalSearchInput.addEventListener("keyup", function(event) {
	
  if (event.keyCode === 13) {
	  window.location.replace('../cases/displayGlobalSearchResults?globalSerachParamter='+($(this).val()));
  } 
});



function displayGlobalSearchResults(){
	window.location.replace('../cases/displayGlobalSearchResults?globalSerachParamter='+$("#globalSerachParamter").val());
}

$.fn.digits = function(){ 
    return this.each(function(){ 
        $(this).text( $(this).text().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,") ); 
    })
};



var __smarty_editSingleDataOnTheFly = function (that){
	var parentTd = $(that).parent();
	
	var thisColName = $(parentTd).attr('data-this-col');
	var kv = $(parentTd).attr('data-k-val');
	var thisColTh = $('#smarty-th-id-'+thisColName);
	var tName = $(thisColTh).attr('data-t-name');
	var pCol = $(thisColTh).attr('data-pkc-name');
	console.log(kv);
	var thisColTitle = $('#smarty-th-id-'+thisColName).text();
	console.log(thisColTitle);
 	Swal.fire({
		title: thisColTitle,
		input: 'text',
	    showCancelButton: true,
	    confirmButtonText: "موافق",
	    cancelButtonText: "الغاء",
	    buttonsStyling: true,
		  inputAttributes: {
		    id: thisColName
		  },
	}).then((result)=> {
		if (result.isConfirmed) {
			var newVal = $("#"+thisColName).val();
			if(newVal){
			    $.ajax({
			        type: "POST",
			        url: "../../__SmartyUpdateSingleDataOnTheFlySRVL",
			        data: {'nv': newVal, 't': tName, 'k': pCol, 'kv': kv, 'fw': 'حسابات مندوب التوصيل', 'cto' : thisColName},
			        cache: false,
			        success: function(data) {
			        	console.log(data);
			        	Swal.fire(
			            "تمت العملية بنجاح!",
			            "تم الحفظ!",
			            "success"
			        	).then((result) => {
			        		console.log(result);
			        		$(parentTd).html(newVal+" <a href='javascript:;;' onclick='__smarty_editSingleDataOnTheFly(this);'><li class=\"fa fa-pencil\"></li></a>");
			        		/* 
				        		$("#shipment_cost_"+caseId).html(newShipmentCost);
				        		$("#shipment_cost_"+caseId).digits();
				        		$("#shipment_cost_"+caseId).attr("data-netval", newShipmentCost);
				        		newNetVal = $("#receipt_amt_"+caseId).attr("data-netval") - newShipmentCost;
				        		$("#td_netamt_"+caseId).html(newNetVal);
				        		$("#td_netamt_"+caseId).digits();
				        		$("#td_netamt_"+caseId).attr("data-netval", newNetVal);
				        		calculateSumOfSelectedCases(); */
			        	});
			            
			        },
			        error: function () {
			        	Swal.fire(
			            "Internal Error",
			            "Oops, your update was not saved.",
			            "error"
			            )
			        }
			    });
			}else{
				Swal.fire({
				      title: 'تم الالغاء',
				   	  confirmButtonText: 'نعم'
				    });
			}
		}
	}, 
	{
	});
}

function confirmReceiptPrices(caseId){
	$.ajax({
        type: "POST",
        url: "../../ChangeCanPayToDlvFlagSRVL",
        data: {'caseId':caseId},
        cache: false,
        success: function(data) {
        	//console.log(data);
        	Swal.fire(
            "تمت العملية بنجاح!",
            "تم تأكيد مبلغ الوصل!",
            "success"
        	).then((result) => {
        		 ;
        		});
        },
        error: function () {
        	Swal.fire(
            "Internal Error",
            "Oops, your note was not saved.",
            "error"
            )
        }
    });
}

function changeCanPayFlag(receipt, caseId, currency, a_fromWhichScreen){
	Swal.fire({
		  title: 'يرجى تأكيد مبلغ الوصل' + currency,
		  showDenyButton: true,
		  showCancelButton: true,
		  confirmButtonText: 'تأكيد مبلغ الوصل (لا يمكن التراجع)',
		  cancelButtonText: "الغاء",
		  denyButtonText: `تغيير مبلغ الوصل`,
		}).then((result) =>  {
		  /* Read more about isConfirmed, isDenied below */
		  if (result.isConfirmed) {
			  confirmReceiptPrices(caseId);
			  $("#td-checkbox-caseid-"+caseId).html("<input type='checkbox' id='pmtcheck_"+caseId+"' onclick='checkBoxPmtClicked(this, "+caseId+")'>");
			 $("#a-href-check-or-confirm-iqd-"+caseId).remove();
			 $("#a-href-check-or-confirm-usd-"+caseId).remove();
		  } else if (result.isDenied) {
			  doChangeReceiptAmt(receipt,caseId, currency, a_fromWhichScreen)
		  }
		});
}

function doChangeReceiptAmt(receipt,caseId , currency, a_fromWhichScreen){
 	Swal.fire({
		title: 'مبلغ الوصل (يرجى ادخال الاصفار)' + currency,
		input: 'number',
	    showCancelButton: true,
	    //confirmButtonColor: "#1FAB45",
	    confirmButtonText: "تعديل السعر",
	    cancelButtonText: "الغاء",
	    buttonsStyling: true,
		  inputAttributes: {
		    id: 'newreceipt'
		  },
 	}).then((result) =>  {
		if (result.isConfirmed) {
			var newReceiptAmt = $("#newreceipt").val();
			if($("#newreceipt").val()){
			    $.ajax({
			        type: "POST",
			        url: "../../ChangeReceiptByCaseIdSRVL",
			        data: {'currency': currency , 'newReceiptAmount': $("#newreceipt").val(), 
			        	'caseId':caseId , 'screenName':a_fromWhichScreen},
			        cache: false,
			        success: function(data) {
			        	//console.log(data);
			        	Swal.fire(
			            "تمت العملية بنجاح!",
			            "تم الحفظ!",
			            "تمت العملية بنجاح"
			        	).then((result) => {
			        		if (currency==='IQD'){
			        			if ($("#td_netamt_iqd_"+caseId).length){
					        		if ($("#td-shipment-cost-caseid-"+caseId).length){
					        			var newNetIqd = 
					        				newReceiptAmt  - $("#td-shipment-cost-caseid-"+caseId).attr("data-val");
					        			$("#td_netamt_iqd_"+caseId).attr("data-netval", newNetIqd);
					        		}
					        		$("#td_netamt_iqd_"+caseId).html(newNetIqd);
					        	}
			        			$("#receipt-amt-iqd-"+caseId).html(newReceiptAmt);
			        			$("#receipt-amt-iqd-"+caseId).attr("data-val", newNetIqd);
			        		}else if (currency==='USD'){
			        			if ($("#td_netamt_usd_"+caseId).length){
					        		$("#td_netamt_usd_"+caseId).attr("data-netval", newReceiptAmt);
					        		$("#td_netamt_usd_"+caseId).html(newReceiptAmt);
					        	}	
			        			$("#receipt-amt-usd-"+caseId).html(newReceiptAmt);
			        			$("#receipt-amt-usd-"+caseId).attr("data-val", newReceiptAmt);
			        		}
			        		});
			        	
			        	$('#a-href-check-or-confirm-iqd-'+caseId)
	        			.attr("href","javascript:changeCanPayFlag("+newReceiptAmt+", "+caseId+", 'IQD')");
	        			$('#badge-caseid-usd-'+caseId).attr("class","badge  badge-warning");
	        			$('#badge-caseid-iqd-'+caseId).attr("class","badge  badge-warning");
	        			
	        			 $("#td-checkbox-caseid-"+caseId).html(
	        			" <div id='div-of-cases-needs-confirmation-"+caseId+"' "
						+ " class=\"badge badge-warning\">مطلوب تأكيد مبلغ الوصل</div> "); 
			        },
			        error: function () {
			        	Swal.fire(
			            "Internal Error",
			            "Oops, your note was not saved.",
			            "error"
			            )
			        }
			    });
			}else{
			    Swal.fire({
				      title: 'يرجى اختيار مبلغ الوصل',
				   	  confirmButtonText: 'نعم'
				    });
			}
		}
	}, 
	{
	});
}



function auditCheckStatusTagUntag(a_clickedButton, buttonNewVal){
	console.log($(a_clickedButton));
	var caseId = $(a_clickedButton).attr("data-caseid");
	var oldVal = $(a_clickedButton).attr("data-val");
	
	var newVal = buttonNewVal;
	if (oldVal =='OK' || oldVal =='NOTOK'){
		newVal = 'N';
	}
	console.log("newVal="+newVal);
	console.log("oldVal="+oldVal);
	var tName ="p_cases";
	var pCol = "c_id";
	var kv = caseId;
	var thisColName = $(a_clickedButton).attr('data-this-col');
	console.log(caseId);
	$.ajax({
        type: "POST",
        url: "../../__SmartyUpdateSingleDataOnTheFlySRVL",
        data: {'nv': newVal, 't': tName, 'k': pCol, 'kv': kv, 'fw': 'تدقيق الشحنات', 'cto' : thisColName},
        cache: false,
        success: function(data) {
        	console.log(data);
        	Swal.fire(
            "تمت العملية بنجاح!",
            "تم الحفظ!",
            "success"
        	).then((result) => {
        		$("#audit-status-btn-"+caseId+"-OK").addClass('btn-light').removeClass('btn-success');
    			$("#audit-status-btn-"+caseId+"-NOTOK").addClass('btn-light').removeClass('btn-danger');
    			$("#audit-status-btn-"+caseId+"-OK").attr("data-val", "N");
    			$("#audit-status-btn-"+caseId+"-NOTOK").attr("data-val", "N");
        		if (newVal == 'OK'){
        			$("#audit-status-btn-"+caseId+"-OK").addClass('btn-success').removeClass('btn-light');
        			$("#audit-status-btn-"+caseId+"-OK").attr("data-val", newVal);
        		}else if (newVal == 'NOTOK'){
        			$("#audit-status-btn-"+caseId+"-NOTOK").addClass('btn-danger').removeClass('btn-light');
        			$("#audit-status-btn-"+caseId+"-NOTOK").attr("data-val", newVal);
        		}
        	});
        },
        error: function () {
        	Swal.fire(
            "Internal Error",
            "Oops, your update was not saved.",
            "error"
            )
        }
    });
}

<%
if (myLoginUser.getLatitude()==null 
|| myLoginUser.getLatitude().length()==0
|| myLoginUser.getLatitude().trim().equalsIgnoreCase("")
|| myLoginUser.getLatitude().trim().equalsIgnoreCase("0")){%>
	$(document).ready(function(){	
		function getLocation() {   
		    console.log('getLocation was called') 
		    if(navigator.geolocation) {
		        navigator.geolocation.getCurrentPosition(showPosition, positionError,{enableHighAccuracy: true});
		    } else {
		        hideLoadingDiv()
		        console.log('Geolocation is not supported by this device')
		    }
		}
		
		function positionError() {  
		    alert("الرجاء تفعيل اكتشاف الموقع في المتصفح");
		    window.location.href="../../DoLogout";
		}
		
		function showPosition(position){
			var dataToSend = {"longitude":position.coords.longitude, "latitude": position.coords.latitude , "whichInterface":"MAIN_SYS"};
			$.post('../../RegisterLongitudeLatitudeSRVL' , dataToSend, function(data, status){ 
				if (status=='success'){
					;
				}else{
					alert("Error, please contact Mohammed Nafie");
				}
		 	});
		}
		getLocation();
	});
<%} %>

</script>
</html>