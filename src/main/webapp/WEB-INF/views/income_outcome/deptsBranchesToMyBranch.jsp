 <%@page import="com.app.util.UtilitiesFeqar, java.text.DecimalFormat, com.app.financials.*"%>
<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet,com.app.incomeoutcome.DebtsBranchesToMyBranch" %>
<%
Connection conn1 = null;
PreparedStatement pst = null;
ResultSet rs = null; 
UtilitiesFeqar ut = new UtilitiesFeqar(); 
HashMap<String, Integer> shipInfo = new HashMap<String, Integer>();
double dlvdPercentage = 0;
double cnclPercentage = 0;
double underProcessPercentage = 0;
int denomanator = 0;
String branchesAcct = (String)request.getParameter("branchesAcct");
long debt = 0;
DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
String branchesAcctReport = (String)request.getParameter("branchesAcctReport");
DebtsBranchesToMyBranch branchesBalance = new DebtsBranchesToMyBranch();
if (branchesAcctReport !=null){
	Myglobals.smartyGlobalsAssArr.put("branchesAcctReport", (String)branchesAcctReport);
	
}else if (Myglobals.smartyGlobalsAssArr.containsKey("branchesAcctReport") && Myglobals.smartyGlobalsAssArr.get("branchesAcctReport")!=null){
	branchesAcctReport = (String)Myglobals.smartyGlobalsAssArr.get("branchesAcctReport");
}
int userBranch = (int)Myglobals.smartyGlobalsAssArr.get("userstorecode");
LinkedHashMap<String,String> branchesList = new LinkedHashMap<String,String>();
LinkedHashMap<String,String> branchesInfo = new LinkedHashMap<String,String>();
HashMap<StandardFinCurrency, Long> entityBalance = new HashMap<StandardFinCurrency, Long>();
try{
	conn1 = mysql.getConn();
	 
	branchesList = Utilities.getListOfBranches(conn1, userBranch);
	if (branchesAcctReport!=null){
		branchesInfo= Utilities.getBranchesInfo(conn1, branchesAcctReport); 
		entityBalance= ut.getEntityDebtBalanceUpToSpecificPayment(conn1, FinOperationEntity.BRANCH, 
				user.getBranchCode(), 0, Integer.parseInt(branchesAcctReport));
		shipInfo = ut.calcDeptBranchToMyBranchShipmentsInfo(conn1, userBranch+"", branchesAcctReport);
		for(String key:shipInfo.keySet())
			denomanator += shipInfo.get(key);
		dlvdPercentage = Math.round(((double)shipInfo.get("dlvd")/denomanator)*100*100.0)/100.0;
		cnclPercentage = Math.round(((double)shipInfo.get("cncl")/denomanator)*100*100.0)/100.0;
		underProcessPercentage = Math.round(((double)shipInfo.get("underprocess")/denomanator)*100*100.0)/100.0;
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
	try{rs.close();}catch(Exception e){}
	try{pst.close();}catch(Exception e){}
	try{conn1.close();}catch(Exception e){}
}
branchesBalance.setDebt(debt);
%>
<!-- page content -->
<form action="?mypickupagentsearch=1" method="post" name="search_cust_form" class="form-horizontal form-label-left" >
	<div class='row col-12'>
			<div class="col-6 ">	
			<select class='select2' id='branchesAcctReport' style="width: 200px;" name ='branchesAcctReport' >
            	<option value='' ></option>
                <%for (String usid : branchesList.keySet()){
                  	if (branchesAcctReport!=null && branchesAcctReport.equalsIgnoreCase(usid)){
                    %>
                    <option value='<%=usid%>' selected><%=branchesList.get(usid)%></option>
                    <%
                    }else{
                    %>
                    <option value='<%=usid%>' ><%=branchesList.get(usid)%></option>
                    <%}
                }%>
			</select>
             </div>
	        <div class='col-6'>
            	<button type='submit' class=" btn btn-primary px-5" style='margin-right:10px' type="button">عرض الديون المالية على للفرع<i class="fa fa-search m-right-xs"></i></button>
            </div>
           
     </div>
 </form>
          
      <%
      if (branchesAcctReport!=null && !branchesAcctReport.trim().equalsIgnoreCase("")){
      %>
<div class="row row-cols-1 row-cols-md-2 row-cols-xl-4" style='margin-top:10px;'>
 <div class="col">
 	
					<div class="card radius-10  bg-gradient" style="background-color: #f8bad9 !important">
						<div class="card-body" style="cursor: pointer"
							onclick="popitup ('branchDebtsPopUp?reportBranchDebtOnly=N&otherBranchTransEntity=<%=branchesAcctReport%>' , '' , 1000 ,600);">
							<div class="d-flex align-items-center" style="justify-content: center;">
								<div class="text-white mb-0 " style="margin-left: auto;margin-right: auto;">
									<p class="text-dark mb-0"> د.ع</p>
									<h6 class="number-font  text-dark mb-0 "><%=numFormat.format(entityBalance.get(StandardFinCurrency.IQD))%>
									</h6>
								</div>
								<div class="text-white mb-0" style="margin-left: auto;margin-right: auto;">
									<p class="text-dark mb-0">دولار أمريكي</p>
									<h6 class="number-font  text-dark mb-0 "><%=numFormat.format(entityBalance.get(StandardFinCurrency.USD))%>
									</h6>
								</div>
							</div>
						</div>
					</div>
		
		<%-- <div class="card radius-10 bg-danger">
		<a href="#" onclick ="popitup ('branchDebtsPopUp?reportBranchDebtOnly=N&otherBranchTransEntity=<%=branchesAcctReport%>' , '' , 1000 ,600);">
			<div class="card-body" style=' padding-bottom: 1.9rem;'>
				<div class="d-flex align-items-center">
					<div>
						<p class="mb-0 text-white">ديون سابقة</p>
						<h4 class="my-1 text-white"><%=branchesBalance.numFormat.format(debt)%></h4>
						<p class="mb-0 font-13 text-white"><i class="bx bxs-down-arrow align-middle"></i></p>
					</div>
					<div class="widgets-icons bg-white text-danger ms-auto"><i class="bx bx-dollar"></i>
					</div>
				</div>
			</div>
		</a>
		</div> --%>
	</div>
	
<div class="col">
		<div class="card bg-danger">
			<div class="card-content">
				<div class="card-body">
					<div class="media d-flex">
						<div class="align-self-center">
							<i class="icon-dislike text-white font-large-2 float-left"></i>
						</div>
						<div class="media-body text-white text-right">
							<h3 class="text-white"><%=numFormat.format(shipInfo.get("cncl"))%></h3>
							<span>شحنات راجعة</span>
						</div>
					</div>
				</div>
			</div>
		</div>
		<%-- <div class="card radius-10" style = "background-color:#f57f7f; color:white ">
			<div class="card-body" style=' padding-bottom: 1.9rem;'>
				<div class="d-flex align-items-center">
					<div>
						<p class="mb-0 text-white">شحنات راجعة</p>
						<h4 class="my-1 text-white"><%=numFormat.format(shipInfo.get("cncl")) %></h4>
						<p class="mb-0 font-13 text-white"><i class="bx bxs-down-arrow align-middle"></i><%=cnclPercentage %> %</p>
					</div>
					<div class="widgets-icons bg-white text-danger ms-auto"><i class="fa fa-thumbs-down"></i>
					</div>
				</div>
			</div>
		</div> --%>
	</div>
	
<div class="col">
	<a href="#" onclick ="popitup ('mybranch_underprocess_shipmentPopUp?shipmentUnderProcessFromMyBranch=<%=user.getBranchCode()%>&shipmentUnderProcessToOtherBranch=<%=branchesAcctReport%>' , '' , 1000 ,600);">
		<div class="card bg-warning">
			<div class="card-content">
				<div class="card-body">
					<div class="media d-flex">
						<div class="align-self-center">
							<i class="la la-car text-white font-large-2 float-left"></i>
						</div>
						<div class="media-body text-white text-right">
							<h3 class="text-white"><%=numFormat.format(shipInfo.get("underprocess"))%></h3>
							<span>قيد التوصيل</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</a>
		<%-- <div class="card radius-10 " style="background-color:#ffc107; color:black ">
			<div class="card-body" style=' padding-bottom: 1.9rem;'>
				<div class="d-flex align-items-center">
					<div>
						<p class="mb-0 text-white">قيد التوصيل</p>
						<h4 class="my-1 text-white"><%=numFormat.format(shipInfo.get("underprocess")) %></h4>
						<p class="mb-0 font-13 text-white"><i class="bx bxs-down-arrow align-middle"></i><%=underProcessPercentage %> %</p>
					</div>
					<div class="widgets-icons bg-white text-dark ms-auto"><i class="fa fa-truck"></i>
					</div>
				</div>
			</div>
		</div> --%>
	</div>
	
<div class="col">
<div class="card bg-success">
			<div class="card-content">
				<div class="card-body">
					<div class="media d-flex">
						<div class="align-self-center">
							<i class="la la-thumbs-up text-white font-large-2 float-left"></i>
						</div>
						<div class="media-body text-white text-right">
							<h3 class="text-white"><%=numFormat.format(shipInfo.get("dlvd")) %></h3>
							<span>سلمت بنجاح لم يتم محاسبتها</span>
						</div>
					</div>
				</div>
			</div>
		</div>
		<%-- <div class="card radius-10" style='background-color:#1ea57f; color:white '>
			<div class="card-body" style=' padding-bottom: 1.9rem;'>
				<div class="d-flex align-items-center">
					<div>
						<p class="mb-0 text-white">سلمت بنجاح لم يتم محاسبتها</p>
						<h4 class="my-1 text-white"><%=numFormat.format(shipInfo.get("dlvd")) %></h4>
						<p class="mb-0 font-13 text-white"><i class="bx bxs-down-arrow align-middle"></i><%=dlvdPercentage %> %</p>
					</div>
					<div class="widgets-icons bg-white text-success ms-auto"><i class="fa fa-thumbs-up"></i>
					</div>
				</div>
			</div>
		</div> --%>
	</div>
</div>
           <!-- start recent activity -->
           <%
           String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
          	
          	Render(branchesBalance  , out , request, response , Myglobals , objectState , pageName1);
          	

          	if (branchesBalance.getRecords() == 0){
          		
          		out.println("<h4>لاتوجد وصولات حاليا</h4>");
          	}
          	
          	//BranchPayments bp = new BranchPayments(); 
           //	Render(bp  , out , request, response , Myglobals , objectState , pageName1);
           %>
<%} %>

<%@ include file="../Main/footer.jsp"%>
<script>
$('#barcode_checker').focus();
var input = document.getElementById("barcode_checker");
var old_receipt = '';
var scannedCounter = 1;
var sumOfSelectedCases = 0;

function checkBoxPmtClicked (){
	var selectedCases ="";
	var first = true;
	
	$('[id^=pmtcheck_]').each(function() {
		console.log($(this).parent());
		if ($(this).parent().hasClass("checked") == true){
			
		    var number = this.id.split('_').pop();
		    if (!first){
		    	selectedCases +=",";
		    }
		    selectedCases +=number;
		    first = false;
		}
	});
	$("#selected_casesto_pay").val('');
	$("#selected_casesto_pay").val($("#selected_casesto_pay").val()+(selectedCases));	
}

input.addEventListener("keyup", function(event) {
  if (event.keyCode === 13) {
	  var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
	 
	  if (barcodeScanned !== null && barcodeScanned !== undefined){
		  
		  var reciept = document.getElementById(barcodeScanned);
		  $(reciept).parent().css({ 
	            "background-color": "rgba(194 , 214 , 245 , 0.7)", 
	            "border": "2px solid rgb(43 73 220)",
	   		});
		  var caseid = $(reciept).attr("caseid");
		  $('html, body').stop().animate({
	            scrollTop: $("#"+barcodeScanned).offset().top
	        }, 500);
		  
		  $("#pmtcheck_"+caseid).parent().addClass("checked");

		  $(old_receipt).parent().css({ 
	            "background-color": "rgba(249,210,179,0.37)", 
	            "border": "2px solid #dc2b2b",
	            
	   		});
		  old_receipt = reciept; 
		  scannedCounter++;
	  }	 
	  input.value = '';
	  $('#barcode_checker').focus();
  } 
});



function checkAll(){
	var check = false;
	if ($("#checkboxall").prop("checked"))
		check = true;
	$('[id^=pmtcheck_]').each(function() {
		$(this).attr("checked", check);
	});
	$('[id^=pmtcheck_]').each(function() {
		$(this).attr("checked", check);
	});
	$('[id^=check-customer-]').each(function() {
		console.log(this.id);
		$(this).attr("checked", check);
	});
	calculateSumOfSelectedCases();
}

function checkAllCust(that, custId){
	var check = false;
	if ($(that).prop("checked"))
		check = true;
	$("input[data-single-check-custid-"+custId+"]").each(function() {
		$(this).attr("checked", check);
	});
	calculateSumOfSelectedCases();
}
function calculateSumOfSelectedCases(){
	var totalNetAmt = 0;
	$('[id^=pmtcheck_]').each(function() {
		if ($(this).prop("checked") == true){
		    var caseid = this.id.split('_').pop();
		    totalNetAmt += parseInt($("#td_netamt_"+caseid).attr("data-netval"));
		}
	});
	
	showTotalSelectedAmtForCustomers(totalNetAmt.toLocaleString());
	
	$("#amount_topay").val(totalNetAmt);
	$("#totalamountshouldbepaid").html(totalNetAmt.toLocaleString());
}
function checkBoxPmtClicked(){
	calculateSumOfSelectedCases();
}
$('#branch-balance-settle-form').submit(function(e){
	
	var selectedCases ="";
	var receiptNo = "";
	var first = true;
	
	$('[id^=pmtcheck_]').each(function() {
		if ($(this).prop("checked") == true){
		    var number = this.id.split('_').pop();
		    if (!first){
		    	selectedCases +=",";
		    }
		    selectedCases +=number;
		    first = false;
		}
	});
	
	$("#selected_casesto_pay").val('');
	$("#selected_casesto_pay").val(selectedCases);
	//alert(selectedCases);
	if ($("#selected_casesto_pay").val().length>0){
		this.submit();
	}else{
		e.preventDefault();
		generalErrorPrettyMsg("لم يتم تحديد أي وصل");
	}
	
	//console.log("selected_casesto_pay---->"+$("#selected_casesto_pay").val());
});
</script>
