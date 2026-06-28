<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.util.Utilities, java.sql.PreparedStatement,java.sql.ResultSet, java.sql.Connection, 
com.app.cases.CaseInformation, java.text.DecimalFormat, java.text.DecimalFormatSymbols, java.util.Locale" %> 
<% 
Connection conn1 = null;
PreparedStatement pst = null;
ResultSet rs = null;
Utilities ut = new Utilities();
String searchGlobal = null;
ArrayList<CaseInformation> ciList = new ArrayList<CaseInformation>();
DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("en", "UK"));
symbols.setDecimalSeparator('.');
symbols.setGroupingSeparator(',');
DecimalFormat numFormat = new DecimalFormat("#,###,###.##", symbols);
if (request.getParameter("globalSerachParamter") !=null )
	searchGlobal = request.getParameter("globalSerachParamter");
if (searchGlobal !=null && searchGlobal.length()>0){
	try{
		conn1 = mysql.getConn();
		ciList = ut.getCaseIdBasedOnGlobalSearch(conn1, searchGlobal, user.getRank_code(), user.getShopsCommaSepereated());
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		try{rs.close();}catch(Exception e){}
		try{pst.close();}catch(Exception e){}
		try{conn1.close();}catch(Exception e){}
	}
}
if(ciList.size()==1){ 
	String custReceiptNoOri = "";
	String caseId = "";
	for (CaseInformation ci : ciList){
		caseId = ci.getCaseid()+"";
		custReceiptNoOri = ci.getCustReceiptNoOri();
	}
	String url = "displaySingleCaseInfo?auditcaseid="+caseId+"&auditreceiptnoori="+custReceiptNoOri;
	//out.println(url);
%>
<script>
var url = "<%=url%>";
//console.log(url);
window.location.replace(url);
</script>
<%
}else if(ciList.size()>1){
%>
	<div class='row'>
		<div class="row row-cols-1 row-cols-md-3 row-cols-lg-3 row-cols-xl-3">
		<%for (CaseInformation ci : ciList){ %>
			<div class="col">
				<div class="card">
					<div class="card-body">
						<div>
							<h5 class="card-title">رقم الوصل : <%=ci.getCustReceiptNoOri()%> </h5>
						</div>
						<p class="card-text">مبلغ الوصل د.ع : <%=numFormat.format(ci.getReceiptAmtIqd())%> &nbsp;&nbsp;&nbsp; _ رقم الهاتف : <%=ci.getReceiverHp1()%></p>
						<p class="card-text">تفاصيل البضاعة : <%=ci.getProductInfo()%></p>	
						<p class="card-text">العنوان : <%=ci.getReceiverAddress()%></p>	
						<%String hrefLocal = "./displaySingleCaseInfo?auditcaseid="+ci.getCaseid()+"&auditreceiptnoori="+ci.getCustReceiptNoOri();%>
						<a href=<%=hrefLocal%> class="btn btn-light">تفاصيل الشحنة </a><i style="float: left;margin-top: 17px;"><%=ci.getCreateddt()%></i>
					</div>
				</div>
			</div>
			<%} %>
		</div>
	</div>				
<%}else{%>
	<div class="alert border-0 border-start border-5 border-white alert-dismissible fade show py-2">
		<div class="d-flex align-items-center">
			<div class="font-35 text-white">
				<i class='bx bx-info-square'></i>
			</div>
			<div class="ms-3">
				<h6 class="mb-0 text-white">لاتوجد نتائج بحث ل (<%=searchGlobal%>)</h6>
			</div>
		</div>
	</div>

<%}%>

<%@ include file="../Main/footer.jsp"%>