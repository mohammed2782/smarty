 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.advancedsetup.UpdateCasesConditionControl" %>
<%
if (user.getUsid() == 1 || user.getUsid() == 16706){
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	UpdateCasesConditionControl updateCasesConditionControl = new UpdateCasesConditionControl(); 
	Render(updateCasesConditionControl  , out , request, response , Myglobals , objectState , pageName1);
}else{
	out.println("<h1>منطقة محظورة</h1>");
}
%> 
<script>
function activeNotificationControl(that, kbId, flag, catigory3){
	var showMassage = true;
	if( document.querySelector('.jconfirm-open') != null){
		const div = document.querySelector('.jconfirm-open');
		showMassage = !div.classList.contains('jconfirm'); // true
		
	}
	if(showMassage){
		$("#loading").css("display", "flex");
		var massageRegulater = $(that).prop('checked');
		var titMsg = 'Ø£ÙØºØ§Ø¡ ØªÙØ¹ÙÙ '+catigory3;
		var conMsg = 'Ø³ÙÙ ÙØªÙ Ø§ÙØºØ§Ø¡ ØªÙØ¹ÙÙ '+catigory3+' ÙÙ Ø­Ø§Ù Ø£Ø®ØªØ±Øª ÙØ¹Ù';
		if(massageRegulater){
			titMsg = 'ØªÙØ¹ÙÙ '+catigory3;
			conMsg = 'Ø³ÙÙ ÙØªÙ ØªÙØ¹ÙÙ '+catigory3+' ÙÙ Ø­Ø§Ù Ø£Ø®ØªØ±Øª ÙØ¹Ù';
		}
		$.confirm({ 
		    title: titMsg,
		    content: conMsg,
		    buttons: {
		        confirm:{
		        	text :'ÙØ¹Ù',
		        	action : function () {
		        		
		        		activeOrDeactiveNotificationControl(massageRegulater, kbId, flag, catigory3);
		        	}
		        },
		        cancel:{
		        	text :'ÙØ§',
		        	action : function () {
		        		location.reload();
		        	}
		        }
		    }
		});
	}
}

function activeOrDeactiveNotificationControl(massageRegulater, kbId, flag, catigory3){
	console.log(catigory3);
	var contMsg = 'ØªÙ Ø§ÙØºØ§Ø¡ ØªÙØ¹ÙÙ '+catigory3;
	var dataToSend = {"flag":flag,"kbId":kbId};
	console.log(dataToSend);
	$.post('../../ActiveDeactiveNotificationControlSRVL' , dataToSend).done(function() { 
		//console.log(status);
		if(massageRegulater)
			contMsg = 'ØªÙ ØªÙØ¹ÙÙ '+catigory3;
			$.confirm({
			    title: '',
			    content: contMsg,
			    buttons: {
			        confirm:{
			        	text :'ÙØ¹Ù',
			        	action : function () {
			        		location.reload();
			        		
			        	}
			        }

			    }
			});
			
			hideLoader();
 	});
	
}
</script>
<jsp:include page="../Main/footer.jsp" /> 