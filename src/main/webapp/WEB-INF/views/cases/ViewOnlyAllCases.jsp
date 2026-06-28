<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.ViewOnlyAllCases" %> 


<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
 ViewOnlyAllCases uc = new ViewOnlyAllCases(); 
  	Render(uc  , out , request, response , Myglobals , objectState , pageName1);

 %>

<%@ include file="../Main/footer.jsp"%>
<script>


$('#c_custreceiptnoori').focus();




function forceDlv(caseId){
	Swal.fire({
		title: 'سبب الواصل الأجباري',
		input: 'text',
	    showCancelButton: true,
	    confirmButtonText: "تأكيد",
	    cancelButtonText: "الغاء",
	    buttonsStyling: true,
		  inputAttributes: {
		    id: 'force_dlv_rmk'
		  },
	}).then((result)=> {
			if (result.isConfirmed){
				var aRmk = $('#force_dlv_rmk').val();
				if (aRmk.trim()) {
					console.log("aRmk--->"+aRmk);
					confirmedForceDlv (caseId, aRmk);
				}else{
					$('#force_dlv_rmk').addClass('is-invalid');
				    Swal.fire({title: 'يجب أدخال السبب',confirmButtonText: 'نعم'});
				}
			}else{
				;
			}
		}, 
	{});
}

function confirmedForceDlv(caseId, rmk){
	var userId = <%=user.getUsid()%>;
	var dataToSend = {caseid : caseId, q_rmk :  rmk };
	$.post('../../ForcePushToCompulsaryDlvSRVL' , dataToSend, function(data, status){ 
		if (status=='success'){
			$.alert({
			    title: '',
			    content: 'تم أعتباره تسجيل الطلب كواصل',
			});
			hideLoader();
		}else{
			alert("Error, please contact Mohammed Nafie");
			hideLoader();
		}
 	});
}


function restoreFromforceDlv(caseId){
	Swal.fire({
		title: '',
	    showCancelButton: true,
	    //confirmButtonColor: "#1FAB45",
	    confirmButtonText: "تراجع عن واصل أجباري",
	    cancelButtonText: "الغاء",
	    buttonsStyling: true,
		  inputAttributes: {
		    id: 'force_dlv_rmk'
		  },
	}).then((result)=> {
			if (result.isConfirmed){
				confirmedRestoreFromForcedDlv (caseId);
			}else{
				;
			}
		}, 
	{});
}

function confirmedRestoreFromForcedDlv(caseId){
	var userId = <%=user.getUsid()%>;
	var dataToSend = {caseid : caseId, page_name : "casesPassedByBranch" };
	$.post('../../RestoreForcedDlvCaseSRVL' , dataToSend, function(data, status){ 
		if (status=='success'){
			$.alert({
			    title: '',
			    content: 'تم  التراجع عن الواصل أجباري',
			});
			hideLoader();
		}else{
			alert("Error, please contact Mohammed Nafie");
			hideLoader();
		}
 	});
}
</script>