<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.bussframework.SingleQueue_BRANCHES_RTN_MANIFEST_LIAISON" %> 
 <title></title>
<div class="row">

	<div class="col-md-12">
    	<div class="panel panel-warning" style="margin-bottom:0px;">
    		<div class="panel-heading">
            	<div class='row'>
	            	<div class='col-xs-12' style='text-align:center'>
	              		<h4>النقل بين الفروع - راجع في المخزن : منفيست رواجع الفروع</h4>
	            	</div>
	            </div>
            </div>
        </div>
	</div>
</div>
<%  
	String stg_code = "BRANCHES";
	String stp_code = "RTN_MANIFEST_LIAISON";
	if (stg_code !=null){
		Myglobals.smartyGlobalsAssArr.put("stg_code", (String)stg_code);
		Myglobals.smartyGlobalsAssArr.put("stp_code", (String)stp_code);
	}
	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	SingleQueue_BRANCHES_RTN_MANIFEST_LIAISON singleQueue_BRANCHES_RTN_MANIFEST_LIAISON = new SingleQueue_BRANCHES_RTN_MANIFEST_LIAISON(); 
	singleQueue_BRANCHES_RTN_MANIFEST_LIAISON.setUserDefinedCaption(" ");
	Render(singleQueue_BRANCHES_RTN_MANIFEST_LIAISON , out , request, response , Myglobals , objectState , pageName1);
%>
<%@ include file="../Main/footer.jsp"%> 