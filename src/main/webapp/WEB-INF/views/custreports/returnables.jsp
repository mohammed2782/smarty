<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cust.reports.ReturnedAlreadyBatches" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet,com.app.cust.reports.ToBeReturnedCases" %>
<%
Connection conn1 = null;
PreparedStatement pst = null;
Utilities ut = new Utilities();
String shopsCommaSeperated = "";
String userRank = "";
if (Myglobals.smartyGlobalsAssArr.containsKey("shopsCommaSeperated") && Myglobals.smartyGlobalsAssArr.get("shopsCommaSeperated")!=null){
	shopsCommaSeperated = (String)Myglobals.smartyGlobalsAssArr.get("shopsCommaSeperated");
}
if (Myglobals.smartyGlobalsAssArr.containsKey("userRank") && Myglobals.smartyGlobalsAssArr.get("userRank")!=null){
	userRank = (String)Myglobals.smartyGlobalsAssArr.get("userRank");
}

try{
	conn1 = mysql.getConn();
	//masterCustomerList = ut.getListOfMasterCustomers(conn1,(int)Myglobals.smartyGlobalsAssArr.get("userstorecode"));

}catch(Exception e){
	e.printStackTrace();
}finally{
	try{pst.close();}catch(Exception e){}
	try{conn1.close();}catch(Exception e){}
}
ToBeReturnedCases agl = new ToBeReturnedCases(); 
%>
<!-- page content -->
     
          <div class="row turquoise_div">
      <%if (shopsCommaSeperated!=null && !shopsCommaSeperated.trim().equalsIgnoreCase("") && shopsCommaSeperated.length()>0){%>

             <%
             String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
            
         	Render(agl  , out , request, response , Myglobals , objectState , pageName1);
         	

         	if (agl.getNoOfRtnitems() == 0){
         		
         		out.println("<h4>لا توجد رواجع حاليا</h4>");
         	}
         	if (userRank.length()>0 && !userRank.equalsIgnoreCase("") && !userRank.isEmpty() && userRank.equalsIgnoreCase("MASTERCUSTOMER")){
	         	ReturnedAlreadyBatches crb = new ReturnedAlreadyBatches(); 
	          	Render(crb  , out , request, response , Myglobals , objectState , pageName1); 
         	}
             %>
                           
       
    
        <!-- /page content -->

<%} %>
</div>
<%@ include file="../Main/footer.jsp"%>
<script>

</script>
        