<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.setup.KbgeneralSetup, com.app.setup.TikectsSubjects" %> 
<div class="row row-cols-1 row-cols-md-2 row-cols-xl-2" >
	<div class="col">
		<%
		 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
			KbgeneralSetup ecm = new KbgeneralSetup(); 
		 	Render(ecm  , out , request, response , Myglobals , objectState , pageName1); 
		 	
		%>
	</div>
	<div class="col">
		<%
			TikectsSubjects tikectsSubjects = new TikectsSubjects(); 
		 	Render(tikectsSubjects  , out , request, response , Myglobals , objectState , pageName1); 
		 	
		%>
	</div>
</div>
<%@ include file="../Main/footer.jsp"%>
