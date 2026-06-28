 <%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.reports.FinancialStatus, com.app.util.Utilities,
 java.sql.PreparedStatement, java.sql.ResultSet,  java.text.DecimalFormat,
 com.app.financials.*" %>
<%
if (user.isHaveFullServices()){

Connection conn1 = null; 
PreparedStatement pst = null;
ResultSet rs = null;
DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
LinkedList<LinkedHashMap<String, HashMap<StandardFinCurrency, Long>>> whichColumn = new LinkedList<LinkedHashMap<String, HashMap<StandardFinCurrency, Long>>>();
LinkedHashMap<String, HashMap<StandardFinCurrency, Long>> valuesBlue = new LinkedHashMap<String, HashMap<StandardFinCurrency, Long>>();
LinkedHashMap<String, HashMap<StandardFinCurrency, Long>> valuesRed = new LinkedHashMap<String, HashMap<StandardFinCurrency, Long>>();
HashMap<String, String> tran = new HashMap<String, String>();
HashMap<String, String> icon = new HashMap<String, String>();
HashMap<String, String> cardColor = new HashMap<String, String>();
HashMap<String, String> popup = new HashMap<String, String>();
HashMap<StandardFinCurrency, Long> allAccountantBoxes = new HashMap<StandardFinCurrency, Long>();
HashMap<StandardFinCurrency, Long> customersBalance = new HashMap<StandardFinCurrency, Long>();
HashMap<StandardFinCurrency, Long> agentsBalance = new HashMap<StandardFinCurrency, Long>();
HashMap<StandardFinCurrency, Long> moneyToBranches = new HashMap<StandardFinCurrency, Long>();
HashMap<StandardFinCurrency, Long> moneyWithBranches = new HashMap<StandardFinCurrency, Long>();
HashMap<StandardFinCurrency, Long> safeBalance = new HashMap<StandardFinCurrency, Long>();
HashMap<StandardFinCurrency, Long> customersDebts = new HashMap<StandardFinCurrency, Long>();
HashMap<StandardFinCurrency, Long> agentsDebts = new HashMap<StandardFinCurrency, Long>();
try{
	conn1 = mysql.getConn();
	
	int currentBranch = (int)Myglobals.smartyGlobalsAssArr.get("userstorecode");
	safeBalance = Utilities.getSafeBalance(conn1, currentBranch);
	allAccountantBoxes = UtilitiesSafeFinancials.GetAllAccountantBoxesBalancesInBranch(conn1, currentBranch);
	agentsBalance = UtilitiesFinancials.getMoneyWithDlvAgents(conn1, currentBranch);
	customersBalance = UtilitiesFinancials.getMoneytoBePaidToCustomers(conn1, currentBranch);
	moneyWithBranches = UtilitiesFinancials.getMoneyWithBranches(conn1, currentBranch);
	moneyToBranches = UtilitiesFinancials.getMonyToBranches(conn1, currentBranch);
	customersDebts = UtilitiesFinancials.getCustomerDebts(conn1, currentBranch);
	agentsDebts = UtilitiesFinancials.getDlvAgentsDebts(conn1, currentBranch);
	//branchesDebts = UtilitiesFinancials.getBranchesDebts(conn1, currentBranch);
	
	valuesBlue.put("safeBalance", safeBalance);
	valuesBlue.put("moneyInBoxes", allAccountantBoxes);
	valuesRed.put("tobePaidToCustomers", customersBalance);
	valuesRed.put("moneyToBranches", moneyToBranches);
	valuesBlue.put("moneyWithAgents", agentsBalance);
	valuesBlue.put("moneyWithBranches", moneyWithBranches);
	valuesBlue.put("customersDebts", customersDebts);
	valuesBlue.put("agentsDebts", agentsDebts);
	whichColumn.add(valuesBlue);
	whichColumn.add(valuesRed);
	
	popup.put("safeBalance","");
	popup.put("moneyInBoxes","");
	popup.put("moneyWithAgents","detailOfMoneyWithAgents?branch_code="+currentBranch);
	popup.put("tobePaidToCustomers","detailOfTobePaidToCustomers?branch_code="+currentBranch);
	popup.put("moneyWithBranches","detailOfMoneyWithCompanies?branch_code="+currentBranch);
	popup.put("moneyToBranches","detailOfMoneyToCompanies?branch_code="+currentBranch);
	popup.put("customersDebts","detailsOfCustomersDebts?branch_code="+currentBranch);
	popup.put("agentsDebts","detailsOfAgentsDebts?branch_code="+currentBranch);
	
	tran.put("safeBalance", "القاصة حاليا");
	tran.put("moneyInBoxes", "صناديق مالية");
	tran.put("moneyWithAgents", "مبالغ عند المندوبين");
	tran.put("tobePaidToCustomers","دفوعات مستحقة للزبائن");
	tran.put("moneyWithBranches", "مبالغ وصولات عند الفروع");
	tran.put("moneyToBranches", "دفوعات وصولات مستحقة للفروع");
	tran.put("customersDebts", "ديون على الزبائن");
	tran.put("agentsDebts", "ديون على مندوبين التوصيل");
	
	icon.put("safeBalance", "fa fa-bank");
	icon.put("moneyInBoxes", "fa fa-upload");
	icon.put("moneyWithAgents", "fa fa-truck");
	icon.put("tobePaidToCustomers", "fa fa-user");
	icon.put("moneyWithBranches", "bx bx-building");
	icon.put("moneyToBranches", "bx bx-building");
	icon.put("customersDebts", "fa fa-users");
	icon.put("agentsDebts", "fa fa-users");
	
	cardColor.put("safeBalance", "bg-info");
	cardColor.put("moneyInBoxes", "bg-info");
	cardColor.put("moneyWithAgents", "bg-info");
	cardColor.put("tobePaidToCustomers", "bg-danger");
	cardColor.put("moneyWithBranches", "bg-info");
	cardColor.put("moneyToBranches", "bg-danger");
	cardColor.put("customersDebts", "bg-info");
	cardColor.put("agentsDebts", "bg-info");
	
}catch(Exception e){
	e.printStackTrace();
}finally{ 
	try{rs.close();}catch(Exception e){}
	try{pst.close();}catch(Exception e){}
	try{conn1.close();}catch(Exception e){}
}
%>

<div id="crypto-stats-1" class="row">
<%for (LinkedHashMap<String, HashMap<StandardFinCurrency, Long>> valuesColor : whichColumn){%>
	<div class='col-xl-6 col-md-6 col-12'>
		<div class='row'>
			<%for (String key : valuesColor.keySet()){%>
				<div class="col-xl-6 col-md-6 col-12">
					<div class="card <%=cardColor.get(key)%>">
	        		<% if(popup.containsKey(key) && !popup.get(key).equalsIgnoreCase("")){ %>
						<a href="#" onclick ="popitup ('<%= popup.get(key)%>' , '' , 1000 ,600);">
					<% } %>
						<div class="card-content">
	            			<div class="card-body pb-1">
	            				<div class="row">
	                    			<div class="col-6">
	                        			<h5 class="text-white mb-1">
	                        				<i class="<%=icon.get(key) %>" style='margin-left:5px'></i><%=tran.get(key) %> </h5>
	                           
	                        		</div>
	                        		<div class="col-6 text-right">
	                           			<h5 class="text-white mb-1"><%=numFormat.format(valuesColor.get(key).get(StandardFinCurrency.IQD))%> د.ع</h5>
	                           			<h5 class="text-white"><%=numFormat.format(valuesColor.get(key).get(StandardFinCurrency.USD))%> $</h5>
	                        		</div>
	                     		</div>
	                 		</div>
	             		</div>
					<% if(popup.containsKey(key) && !popup.get(key).equalsIgnoreCase("")){ %>
						</a>
					<% } %>
					</div>
				</div>
		<%}%>
	</div>
	</div>
	<%}%>
</div>
<%}%>
<%@ include file="../Main/footer.jsp"%>  