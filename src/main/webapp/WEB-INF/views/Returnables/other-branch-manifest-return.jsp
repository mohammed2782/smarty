<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.returnables.OtherBranchManifestReturn" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet" %>
<%
Connection conn1 = null;
	PreparedStatement pst = null;
	ResultSet rs = null; 
	Utilities ut = new Utilities(); 
	String otherBranchManifestReturn = (String)request.getParameter("otherBranchManifestReturn");
	if (otherBranchManifestReturn !=null){
		Myglobals.smartyGlobalsAssArr.put("otherBranchManifestReturn", (String)otherBranchManifestReturn);
	}else if (Myglobals.smartyGlobalsAssArr.containsKey("otherBranchManifestReturn") && Myglobals.smartyGlobalsAssArr.get("otherBranchManifestReturn")!=null){
		otherBranchManifestReturn = (String)Myglobals.smartyGlobalsAssArr.get("otherBranchManifestReturn");
	}
	LinkedHashMap<String,String> branchesList = new LinkedHashMap<String,String>();
	LinkedHashMap<String,String> branchInfo = new LinkedHashMap<String,String>();
	try{
		conn1 = mysql.getConn();
		branchesList = Utilities.getListOfBranches(conn1, (int)Myglobals.smartyGlobalsAssArr.get("userstorecode"));
		if (otherBranchManifestReturn!=null)
	branchInfo= Utilities.getBranchesInfo(conn1, otherBranchManifestReturn); 
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		try{rs.close();}catch(Exception e){}
		try{pst.close();}catch(Exception e){}
		try{conn1.close();}catch(Exception e){}
	}
%>
<!-- page content -->
	<div class="row turquoise_div mb-5">
		<div class="page-title m-auto">
			<form action="?otherBranchSearch=1" method="post" name="search_branch_manifest_form" class="form-horizontal form-label-left" >
				<div class='row col-12'>
					<div class="col-6 ">
						
						<select class='single-select form-control' id='otherotherBranchManifestReturn' style="width: 200px;"  name ='otherBranchManifestReturn' >
							<option value='' ></option>
                    		<%for (String branchId : branchesList.keySet()){
                    		  	if (otherBranchManifestReturn!=null && otherBranchManifestReturn.equalsIgnoreCase(branchId)){
                    		%>
                    			<option value='<%=branchId%>' selected><%=branchesList.get(branchId)%></option>
                    			<%}else{
                    		%>
                    			<option value='<%=branchId%>' ><%=branchesList.get(branchId)%></option>
                    			<%}
                    		}%>
                    	</select>
	                </div>
	                <div class='col-6'>
	                	<button type='submit' class="btn btn-dark btn-md" type="button">عرض منفيست راجع الفروع<i class="fa fa-search m-right-xs ml-1"></i></button>
	                </div>
                </div>
			</form>
		</div>
	</div>
<%
if (otherBranchManifestReturn!=null && !otherBranchManifestReturn.trim().equalsIgnoreCase("")){
%>

         <%
        	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
         	OtherBranchManifestReturn obmr = new OtherBranchManifestReturn(); 
         	Render(obmr  , out , request, response , Myglobals , objectState , pageName1); 
         %>
<%}%>

<%@ include file="../Main/footer.jsp"%>

        