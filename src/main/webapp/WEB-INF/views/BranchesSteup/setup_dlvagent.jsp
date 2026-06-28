 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.SetupDeliverAgent" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet" %>

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SetupDeliverAgent setupDeliverAgent = new SetupDeliverAgent(); 
	Render(setupDeliverAgent , out , request, response , Myglobals , objectState , pageName1);

%>


<script>
//$("#us_loginid").focus({preventScroll:true});
smarty_submitButton_allow_disable = false;
$("#save_new_form_com_dot_app_dot_setup_dot_SetupDeliverAgent").click(function(e){
	$('#us_dlvparentid').prop('required',false);
	if($( "#us_rank" ).val() == 'SUB_DLVAGENT'){
		$('#us_dlvparentid').prop('required',true);
		 $( "#us_dlvparentid" ).focus();
	}
    e.preventDefault();
	var us_loginid =   $( "#us_loginid" ).val();
	var us_password =   $( "#us_password" ).val();
	if(us_loginid != '' && us_password != ''){
		//console.log('us_loginid = '+us_loginid);
		var dataToSend = {"us_loginid":us_loginid,"us_password":us_password};
		$.post('../../CheckUserLoginIdAndPassSRVL' , dataToSend, function(data, status){ 
			//alert("Data: " + data +", Status:" + status);
			if (status=='success'){ 
				//console.log(data);
				if(data == '2'){
				    $([document.documentElement, document.body]).animate({
				        scrollTop: $("#us_loginid").offset().top
				    }, 0);
				    $( "#us_loginid" ).focus();
				    document.getElementById('us_loginid').title = 'قد يحوي الاسم على اقل من اربع احرف اويحوي مسافة فارغة او الاسم مستخدم مسبقاً';
				    $("#us_loginid").tooltip();

				}else if(data == '1'){
				    $([document.documentElement, document.body]).animate({
				        scrollTop: $("#us_password").offset().top
				    }, 0);
				    $( "#us_password" ).focus();
				    document.getElementById('us_password').title = 'قد يحوي الرمز على اقل من اربع احرف اويحوي مسافة فارغة';
				    $("#us_password").tooltip();
					
				}else if(data == 3){
					if($("#com_dot_app_dot_setup_dot_SetupDeliverAgent")[0].checkValidity())
						$("#com_dot_app_dot_setup_dot_SetupDeliverAgent").submit();
					else{
						window.scrollTo(0, 0);
					}
				}
			}else{
				alert("Error, please contact Mr.Nafie");
			}
		});
	}
});



</script>

<%@ include file="../Main/footer.jsp"%> 