 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.setup_customers, com.app.setup.MasterCustomers  " %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	MasterCustomers mc = new MasterCustomers(); 
	Render(mc  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>
<%
if (request.getParameter("mcust_id")!=null){
	if (!request.getParameter("mcust_id").isEmpty()){
		Myglobals.smartyGlobalsAssArr.put("mcust_id", request.getParameter("mcust_id"));
	}else{
		Myglobals.smartyGlobalsAssArr.remove("mcust_id");
	}
}

if (Myglobals.smartyGlobalsAssArr.get("mcust_id")!=null){
	setup_customers sc = new setup_customers(); 
	Render(sc  , out , request, response , Myglobals , objectState , pageName1); 
}
 	
%>
<script>
function changeCustomerShipmentsCostBackDated(custId){
	$("#loading").css("display", "flex");
	
	$.confirm({ 
	    title: 'تعديل مبلغ شحنات صاحب المحل',
	    content: 'سوف يتم تعديل مبالغ كل شحنات صاحب المحل في حال أخترت نعم',
	    buttons: {
	        confirm:{
	        	text :'نعم',
	        	action : function () {
	        		
	        		doChangeShipmentsCosts (custId);
	        	}
	        },
	        cancel:{
	        	text :'لا',
	        	action : function () {
	        		hideLoader();
	        		}
	        }
	    }
	});
}
function doChangeShipmentsCosts(custId){
	
	var dataToSend = {"custId":custId};
	$.post('../ChangeCustomerShipmentsCostSRVL' , dataToSend, function(data, status){ 
		if (status=='success'){
			$.alert({
			    title: '',
			    content: 'تم التغيير',
			});
			hideLoader();
		}else{
			alert("Error, please contact Alnafi3 soft");
			hideLoader();
		}
 	});
	
}

</script>
<%@ include file="../Main/footer.jsp"%>