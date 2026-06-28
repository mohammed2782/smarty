<%@ include file="../Main/Main-popup.jsp"%>
<%@ page import="com.app.bussframework.UpdateDistrictFromNewInStorePopUp" %>
<% 
	
String caseidfromnewinstore = (String)request.getParameter("caseidfromnewinstore");
if (caseidfromnewinstore !=null){
	Myglobals.smartyGlobalsAssArr.put("caseidfromnewinstore", (String)caseidfromnewinstore);
}else if (Myglobals.smartyGlobalsAssArr.containsKey("caseidfromnewinstore") && Myglobals.smartyGlobalsAssArr.get("caseidfromnewinstore")!=null){
	caseidfromnewinstore = (String)Myglobals.smartyGlobalsAssArr.get("caseidfromnewinstore");
}
	 

	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	UpdateDistrictFromNewInStorePopUp updateDistrictFromNewInStorePopUp = new UpdateDistrictFromNewInStorePopUp(); 
	Render(updateDistrictFromNewInStorePopUp  , out , request, response , Myglobals , objectState , pageName1);
%> 
<div class="row"><div class="col-md-offset-4 col-md-2 col-xs-offset-6 col-xs-3">
<input type="button" class="btn btn-danger" onclick="window.close();" value="إغلاق" /></div></div>
<jsp:include page="../Main/footer-popup.jsp" />
<script>
window.onunload = refreshParent;
function refreshParent() {window.opener.location.reload();}

</script>