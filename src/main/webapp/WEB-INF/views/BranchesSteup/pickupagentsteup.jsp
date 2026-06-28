 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.SetupPickUpAgent" %> 

<%  
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SetupPickUpAgent setupPickUpAgent = new SetupPickUpAgent(); 
	Render(setupPickUpAgent , out , request, response , Myglobals , objectState , pageName1);

%>

<%@ include file="../Main/footer.jsp"%> 

<script>
function tieCustomersWithPickupAgentAndChangeShipmentsCostBackDated(pickUpAgentId){
	$("#loading").css("display", "flex");
	$.confirm({
	    title: 'ربط الشحنات بمندوب الإستلام بإثر رجعي',
	    content: 'سوف يتم تعديل مبالغ كل شحنات إصحاب المحلات التابعين لهذا المندوب في حال أخترت نعم',
	    buttons: {
	        confirm:{
	        	text :'نعم',
	        	action : function () {
	        		doChangeShipmentsCosts (pickUpAgentId);
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
function doChangeShipmentsCosts(agentId){
	var dataToSend = {"pickupagentid":agentId};
	$.post('../../TiePickUpAgentToCustomersBackDatedSRVL' , dataToSend, function(data, status){ 
		if (status=='success'){
			$.alert({
			    title: '',
			    content: 'تم التغيير',
			});
			hideLoader();
		}else{
			alert("Error, الرجاء الأتصال بمحمد نافع");
			hideLoader();
		}
 	});
}

</script>