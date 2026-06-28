<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.reports.CustomersWithSpecialPricesDetailPopup" %> 

<%  
String cust_id = (String)request.getParameter("cust_id");
String cust_name = (String)request.getParameter("cust_name");

if (cust_id !=null){
	Myglobals.smartyGlobalsAssArr.put("cust_id", (String)cust_id);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("cust_id") && Myglobals.smartyGlobalsAssArr.get("cust_id")!=null){
	cust_id = (String)Myglobals.smartyGlobalsAssArr.get("cust_id");
}
	String pageName1 = cust_name;
	CustomersWithSpecialPricesDetailPopup mce = new CustomersWithSpecialPricesDetailPopup(); 
	Render(mce , out , request, response , Myglobals , objectState , pageName1);

%>

<jsp:include page="../Main/footer-popup.jsp" />
<script>


$('#us_loginid').prop("autocomplete","off");
function checkLoginId (action){
	
	var currentValue = $("#us_loginid").val();
	
	if (currentValue.length<4){
		
		$("#us_loginid").css("border-color", "#f41127");
		if ($('#validate_usloginid').length)
			$("#validate_usloginid").remove();

		$("#us_loginid").after("<span id ='validate_usloginid' style='color: #ff8989;'>معرف الدخول يجب أن يحتوي على الأقل أربع أحرف من دون فراغ <i class='bx bx-error'></i></span>");
	}else{
		$.get('../../AvailableLoginUserSRVLT?loginId='+currentValue+"&actionName="+action,function(data, status){ 
			if (status=='success'){
				if (data == 'true'){
					$("#validate_usloginid").remove();
					$("#us_loginid").css("border-color", "#f41127");
					$("#us_loginid").after("<span id ='validate_usloginid' style='color: #ff8989;'>معرف الدخول مستخدم سابقا. جرب معرف دخول أخر<i class='bx bx-error'></i></span>");
				}else{
					$("#us_loginid").css("border-color", "");
					$("#validate_usloginid").remove();
				}
			}
			
		});
	}
}
</script>