 <%@page import="com.app.util.UtilitiesFeqar"%>
<%@ include file="../Main/Main.jsp"%>
<%@ page autoFlush="true" buffer="2048kb"%>
<%@ page import="com.app.cust.reports.Payments" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet,com.app.cust.reports.Financials,java.text.DecimalFormat,
com.app.cust.reports.Debts" %>
<%
Connection conn1 = null;
PreparedStatement pst = null; 
ResultSet rs = null; 
UtilitiesFeqar ut = new UtilitiesFeqar();
String shopsCommaSeperated = "";
String userRank = "";
int customerBalance = 0;
DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
HashMap<String, Integer> totShipInfo = new HashMap<String, Integer>();
	 
if (Myglobals.smartyGlobalsAssArr.containsKey("shopsCommaSeperated") && Myglobals.smartyGlobalsAssArr.get("shopsCommaSeperated")!=null){
	shopsCommaSeperated = (String)Myglobals.smartyGlobalsAssArr.get("shopsCommaSeperated");
}
if (Myglobals.smartyGlobalsAssArr.containsKey("userRank") && Myglobals.smartyGlobalsAssArr.get("userRank")!=null){
	userRank = (String)Myglobals.smartyGlobalsAssArr.get("userRank");
} 
LinkedHashMap<String,String> masterCustomerList = new LinkedHashMap<String,String>();
Financials cbl = new Financials(); 
	try{
		conn1 = mysql.getConn();
		if (shopsCommaSeperated !=null){
			totShipInfo = ut.getTotalShipmentsInfo(conn1, shopsCommaSeperated);
			if (userRank.length()>0 && !userRank.equalsIgnoreCase("") && !userRank.isEmpty() && userRank.equalsIgnoreCase("MASTERCUSTOMER"))
				customerBalance = ut.getMasterCustomerBalance(conn1, (int)Myglobals.smartyGlobalsAssArr.get("mastercustidlogin"));
			cbl.setDebt(customerBalance);
		}
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		try{rs.close();}catch(Exception e){}
		try{pst.close();}catch(Exception e){}
		try{conn1.close();}catch(Exception e){}
	}

%>
<!-- page content -->
<div class="row row-cols-1 row-cols-md-2 row-cols-xl-4" style='margin-top:10px;'>
      <%
      	if (userRank.length()>0 && !userRank.equalsIgnoreCase("") && !userRank.isEmpty() && userRank.equalsIgnoreCase("MASTERCUSTOMER")){
      %>
	<div class="col">
		<div class="card radius-10 bg-danger" style='border:1px solid #ffc107'>
			<div class="card-body" style=' padding-bottom: 1.9rem;'>
				<div class="d-flex align-items-center">
					<div>
						<p class="mb-0 text-white">ديون سابقة</p>
						<h4 class="my-1 text-white"><%=cbl.numFormat.format(customerBalance)%></h4>
						<!-- <p class="mb-0 font-13 text-white"><i class="bx bxs-down-arrow align-middle"></i>$34 from last week</p> -->
					</div>
					<div class="widgets-icons bg-white text-danger ms-auto"><i class="bx bx-dollar"></i>
					</div>
				</div>
			</div>
		</div>
	</div>
	<%} %>
	<div class="col">
					   <div class="card radius-10" style='border:1px solid #ffc107'>
						 <div class="card-body">
						   <div class="d-flex align-items-center">
						   <div class="">
							<h3 class="mt-3 mb-0"><%=numFormat.format(totShipInfo.get("dlvd"))%></h3>
							  <p class="mb-0">واصل</p>
						   </div>
							 <div class="card-content dash-array-chart-box ms-auto">
							  <div id="dlvnotpaid-cases"></div>
							 </div>
						   </div>
						 </div>
					   </div>
	</div>
	<div class="col">
					   <div class="card radius-10" style='border:1px solid #ffc107'>
						 <div class="card-body">
						   <div class="d-flex align-items-center">
						   <div class="">
							<h3 class="mt-3 mb-0"><%=numFormat.format(totShipInfo.get("cncl"))%></h3>
							  <p class="mb-0">راجع</p>
						   </div>
							 <div class="card-content dash-array-chart-box ms-auto">
							  <div id="rtn-cases"></div>
							 </div>
						   </div>
						 </div>
					   </div>
	</div>
	<div class="col">
					   <div class="card radius-10" style='border:1px solid #ffc107'>
						 <div class="card-body">
						   <div class="d-flex align-items-center">
						   <div class="">
							<h3 class="mt-3 mb-0"><%=numFormat.format(totShipInfo.get("underprocess"))%></h3>
							  <p class="mb-0">قيد التوصيل</p>
						   </div>
							 <div class="card-content dash-array-chart-box ms-auto">
							  <div id="inprocess-cases"></div>
							 </div>
						   </div>
						 </div>
					   </div>
	</div>
</div>
<div class="col">
<hr/>
<div class="card">
	<div class="card-body">
		<ul class="nav nav-pills mb-3" role="tablist">
			<li class="nav-item" role="presentation">
				<a class="nav-link" onclick='changeActiveTab("cust-balance")' id='a-cust-balance' data-bs-toggle="pill" href="#cust-balance" role="tab" aria-selected="true" >
			<div class="d-flex align-items-center">
				<div class="tab-icon"><i class='bx bx-home font-18 me-1'></i>
				</div>
				<div class="tab-title">المتاجر</div>
			</div>
		</a>
	</li>
	<li class="nav-item" role="presentation">
		<a class="nav-link" onclick='changeActiveTab("cust-debts")' id='a-cust-debts' data-bs-toggle="pill" href="#cust-debts" role="tab" aria-selected="false">
			<div class="d-flex align-items-center">
				<div class="tab-icon"><i class='bx bx-user-pin font-18 me-1'></i>
				</div>
				<div class="tab-title">كل الحركات المالية</div>
			</div>
		</a>
	</li>
	
</ul>
								
									
						
      <div class="tab-content" id="pills-tabContent">
           <div class="tab-pane fade" id="cust-balance" role="tabpanel">
	        <!-- start recent activity -->
	        <%
	        	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	           
	         	Render(cbl  , out , request, response , Myglobals , objectState , pageName1);
	         	
	         	if (cbl.getRecords() == 0){
	         		out.println("<h4>لا توجد مبالغ مستحقة الدفع حاليا</h4>");
	         	}

	          	if (userRank.length()>0 && !userRank.equalsIgnoreCase("") && !userRank.isEmpty() && userRank.equalsIgnoreCase("MASTERCUSTOMER")){
	
		            Payments payments = new Payments(); 
		          	Render(payments  , out , request, response , Myglobals , objectState , pageName1);
	          	}
	        %>
	        </div>
	        <div class="tab-pane fade " id="cust-debts" role="tabpanel">
 			 <%
 			if (userRank.length()>0 && !userRank.equalsIgnoreCase("") && !userRank.isEmpty() && userRank.equalsIgnoreCase("MASTERCUSTOMER")){
			 	Debts cd = new Debts();
			 	cd.setMasterCustomerId((int)Myglobals.smartyGlobalsAssArr.get("mastercustidlogin"));
	          	Render(cd  , out , request, response , Myglobals , objectState , pageName1);
 			}
	        %> 
			</div>
			
        </div>
   		
			</div>
		</div>
	</div>
                           

<%@ include file="../Main/footer.jsp"%>
<script>

function changeActiveTab( tab){
	 localStorage.setItem('activeTab', tab);
	
}
</script>
