<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.tickets.ShowAllTickets" %> 


<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	ShowAllTickets allTickets = new ShowAllTickets(); 
 	Render(allTickets  , out , request, response , Myglobals , objectState , pageName1); 

%>
<%@ include file="../Main/footer.jsp"%>
<%-- <script>
$(document).ready(function() {
	
	var closeTicketInParent = function (ticketId) {
		alert('heeello');
		$.confirm({ 
		    title: 'سيتم أغلاق هذه التذكرة بشكل نهائي '+ticketId,
		    content: 'أنتبه. لا يمكن التراجع عن هذه الخطوه . اذا كنت متأكد اختر نعم',
		    type: 'red',
		    buttons: {
		        confirm:{
		        	text :'نعم',
		        	action : function () {
		        		
		        		 var userId = <%=user.getUsid()%>;
		        		 var dataToSend = {ticketId : ticketId, closedBy : userId };
		        		 $.post('../../TicketsCloseSRVL' , dataToSend, function(data, status){ 
		        			if (status=='success'){
		        				location.reload();
		        			}else{
		        				alert("Error, please contact Mohammed Nafie");
		        				
		        			}
		        		});
		        	}
		        },
		        cancel:{
		        	text :'لا',
		        	action : function () {
		        	;
		        	}
		        }
		    }
		});
	}
}); --%>
</script>