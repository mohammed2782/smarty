<%@page import="com.app.util.UtilitiesFeqar"%>
<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.bank.Safe, java.sql.PreparedStatement, java.sql.ResultSet,  java.text.DecimalFormat" %> 

			<% 
	 			String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
				Safe safe = new Safe();
	 			Render(safe , out , request, response , Myglobals , objectState , pageName1);
	 		%>
	 	
<%@ include file="../Main/footer.jsp"%> 
<script>
function InventorySafe(userId,branchId){
	var showMassage = true;
	if( document.querySelector('.jconfirm-open') != null){
		const div = document.querySelector('.jconfirm-open');
		showMassage = !div.classList.contains('jconfirm'); // true
		
	}
	if(showMassage){
		$("#loading").css("display", "flex");
		
		$.confirm({ 
		    title: 'جرد كل حركات ومبالغ القاصة',
		    content: 'سوف يتم ارشفة كل حركات القاصة في حال أخترت نعم',
		    buttons: {
		        confirm:{
		        	text :'نعم',
		        	action : function () {
		        		
		        		doArchiveSafeTransaction(userId,branchId);
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
}
function doArchiveSafeTransaction(userId, branchId){
	
	var dataToSend = {"userId":userId,"branchId":branchId};
	$.post('../../ArchiveSafeTransactionSRVL' , dataToSend).done(function() { 
		//console.log(status);
			$.confirm({
			    title: '',
			    content: 'تمت الارشفة',
			    buttons: {
			        confirm:{
			        	text :'نعم',
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