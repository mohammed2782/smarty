<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.SetupDistricts " %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet" %>
<%
//getBranchesInfo


Connection conn1 = null;
PreparedStatement pst = null; 
ResultSet rs = null; 
Utilities ut = new Utilities();
String stateCode = "";
	try{
		conn1 = mysql.getConn();
		stateCode = ut.getBranchesInfo(conn1, user.getBranchCode()+"").get("stateCode");
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		try{rs.close();}catch(Exception e){}
		try{pst.close();}catch(Exception e){}
		try{conn1.close();}catch(Exception e){}
	}

%>
<div class='row'>
	
	
	<div  class='col-4 offset-8'>
	<button type="button" class="btn btn-sm btn-dark" onclick="changeAllCasesDistrictsBackDated('<%=stateCode%>');">تعديل المناطق الطرفيه والغير طرفيه لكل الشحنات</button>
	</div>
</div>
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SetupDistricts sd = new SetupDistricts(); 
 	Render(sd  , out , request, response , Myglobals , objectState , pageName1); 
 	
%>


<script>
function changeAllCasesDistrictsBackDated(stateCode){
	$("#loading").css("display", "flex");
	
	$.confirm({ 
	    title: 'تعديل كل المناطق الطرفيه والغير الطرفيه لكل الشحنات ؟',
	    content: 'سوف يتم تعديل المناطق الطرفيه والغير الطرفيه في كل الشحنات بأثر رجعي ما عدا الشحنات المحاسب عليها مع المندوب ومع العميل . اختر نعم للتاكيد',
	    buttons: {
	        confirm:{
	        	text :'نعم',
	        	action : function () {
	        		confirmedChangeCasesDistricts (stateCode);
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
function confirmedChangeCasesDistricts(stateCode){
	
	var dataToSend = {"stateCode":stateCode};
	$.post('../../FixAllRuralDistrictPerStateSRVL' , dataToSend, function(data, status){ 
		if (status=='success'){
			$.alert({
			    title: '',
			    content: 'تم التغيير',
			});
			hideLoader();
		}else{
			alert("Error, please contact Mohammed Nafie");
			hideLoader();
		}
 	});
	
}

</script>

<%@ include file="../Main/footer.jsp"%>