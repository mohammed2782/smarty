<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.setup_menu ,com.app.setup.setup_sub_menu " %> 
<%
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
    setup_menu sm = new setup_menu(); 
 	Render(sm  , out , request, response , Myglobals , objectState , pageName1);  	
%>

<div class='clearfix'></div>
<br>
<div class='row'>
	<div class='col-md-12'> 
	<%
		if (request.getParameter("mt_id")!=null){
			if (!request.getParameter("mt_id").isEmpty()){
				Myglobals.smartyGlobalsAssArr.put("mt_id", request.getParameter("mt_id"));
			}else{
				Myglobals.smartyGlobalsAssArr.remove("mt_id");
			}
		}
		
		if (Myglobals.smartyGlobalsAssArr.get("mt_id")!=null){
			setup_sub_menu ssm = new setup_sub_menu();
			Render(ssm  , out , request, response , Myglobals , objectState , pageName1); 
		}
	%>
</div>
 </div>
<%@ include file="../Main/footer.jsp"%>
<script>
function checkUncheckAllBranches(){
	var itemIds ='';
	if ($("#all_branches").is(':checked')) {
		$('input[name^="sm_branches"]').each(function (){
			$(this).prop( "checked", true );
		});
	}else{
		$('input[name^="sm_branches"]').each(function (){
			if (this.value !== undefined){
				$(this).prop( "checked", false );
			}
		});
	}
}

//$("#com.app.setup.setup_sub_menu").submit(function(){
jQuery('form[name="com.app.setup.setup_sub_menu"]').submit(function(){
	if ($("#all_branches").is(':checked')) {
		$('input[name^="sm_branches"]').each(function (){
			$(this).prop( "checked", false );
		});
		$("#all_branches").prop( "checked", true );
	}
});

$(document).ready(function() {
	if($('#sm_branches_hidden').val()=="ALL:"){
		$('input[name^="sm_branches"]').each(function (){
			$(this).prop( "checked", true );
		});
	}
});
</script>