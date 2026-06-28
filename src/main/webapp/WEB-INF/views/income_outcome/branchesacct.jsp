<%@page
	import="com.app.util.UtilitiesFeqar, java.text.DecimalFormat, com.app.financials.*"%>
<%@ include file="../Main/Main.jsp"%>
<%@ page autoFlush="true" buffer="2048kb"%>
<%@ page import="com.app.incomeoutcome.BranchesTransactions"%>
<%@ page
	import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet,
	com.app.incomeoutcome.BranchesBalance,com.app.incomeoutcome.LiaisonOrPickUpAgentSharePayments"%>
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
String branchToPayTo = (String)request.getParameter("branchToPayTo");
long debt = 0;
DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
BranchesBalance branchesBalance = new BranchesBalance();
if (branchToPayTo !=null){
	Myglobals.smartyGlobalsAssArr.put("BRANCH_TO_PAY_TO_G", (String)branchToPayTo);
	
}else if (Myglobals.smartyGlobalsAssArr.containsKey("BRANCH_TO_PAY_TO_G") && Myglobals.smartyGlobalsAssArr.get("BRANCH_TO_PAY_TO_G")!=null){
	branchToPayTo = (String)Myglobals.smartyGlobalsAssArr.get("BRANCH_TO_PAY_TO_G");
}
int userBranch = (int)Myglobals.smartyGlobalsAssArr.get("userstorecode");
LinkedHashMap<String,String> branchesList = new LinkedHashMap<String,String>();
LinkedHashMap<String,String> branchesInfo = new LinkedHashMap<String,String>();
HashMap<StandardFinCurrency, Long> entityBalance = new HashMap<StandardFinCurrency, Long>();
AccountantBoxBean accountantBoxBean = null;
try{
	conn1 = mysql.getConn();
	
	accountantBoxBean = UtilitiesSafeFinancials.GetAccountantBox(conn1, user.getUsid(), user.getBranchCode());
	branchesList = Utilities.getListOfBranches(conn1, userBranch);
	if (branchToPayTo!=null){
		branchesInfo= Utilities.getBranchesInfo(conn1, branchToPayTo); 
		entityBalance= ut.getEntityDebtBalanceUpToSpecificPayment(conn1, FinOperationEntity.BRANCH, 
		Integer.parseInt(branchToPayTo), 0, user.getBranchCode());
		//shipInfo = ut.calcBranchShipmentsInfo(conn1, userBranch+"", branchToPayTo);
		for(String key:shipInfo.keySet())
	denomanator += shipInfo.get(key);
		//dlvdPercentage = Math.round(((double)shipInfo.get("dlvd")/denomanator)*100*100.0)/100.0;
		//cnclPercentage = Math.round(((double)shipInfo.get("cncl")/denomanator)*100*100.0)/100.0;
		//underProcessPercentage = Math.round(((double)shipInfo.get("underprocess")/denomanator)*100*100.0)/100.0;
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
	try{rs.close();}catch(Exception e){}
	try{pst.close();}catch(Exception e){}
	try{conn1.close();}catch(Exception e){}
}
if (entityBalance !=null && !entityBalance.isEmpty()){
	branchesBalance.setDebtIqd(entityBalance.get(StandardFinCurrency.IQD));
	branchesBalance.setDebtUsd(entityBalance.get(StandardFinCurrency.USD));
}
%>
<!-- page content -->
<div class="row row-cols-1 row-cols-md-2 row-cols-xl-4"style='margin-top: 10px;'>
	<div class="col-9">
		<form action="??toPayToBranchSearch=1" method="post" class="form-horizontal form-label-left">
			<div class="row row-cols-1 row-cols-md-2 row-cols-xl-2">
				<div class="col">
					<select class='select2' id='branchToPayTo' style="width: 200px;" name='branchToPayTo'>
						<option value=''></option>
						<%
						for (String branchId : branchesList.keySet()){
						                  			if (branchToPayTo!=null && branchToPayTo.equalsIgnoreCase(branchId)){
						%>
								<option value='<%=branchId%>' selected><%=branchesList.get(branchId)%></option>
						<%
						}else{
						%>
								<option value='<%=branchId%>'><%=branchesList.get(branchId)%></option>
							<%
							}
							                		}
							%>
					</select>
				</div>
				<div class="col">
					<button type='submit' class=" btn btn-purple px-5" style='margin-right: 10px' type="button">
						عرض التفاصيل المالية للفرع<i class="fa fa-search m-right-xs"></i>
					</button>
				</div>
				<div class="col-md-5 col-sm-6 col-xs-12 ">
					<div class="card radius-10  bg-gradient" style="background-color: #0dcaf0;">
						<div class="card-body" style="cursor: pointer"
							onclick="popitup('../bank/FinBoxDtlsPopUp?finboxacctid=<%=accountantBoxBean.getBoxId()%>', 'Transactions' , 1150,700)" style="width:30px; ">
							<div class="d-flex align-items-center" style="justify-content: center;">
								<div class="text-white mb-0 " style="margin-left: auto;margin-right: auto;">
									<p class="text-dark mb-0">المبلغ في الصندوق د.ع</p>
									<h6 class="number-font  text-dark mb-0 "><%=numFormat.format(accountantBoxBean.getCurrentBalanceIqd())%>
									</h6>
								</div>
								<div class="text-white mb-0" style="margin-left: auto;margin-right: auto;">
									<p class="text-dark mb-0">المبلغ في الصندوق $</p>
									<h6 class="number-font  text-dark mb-0 "><%=numFormat.format(accountantBoxBean.getCurrentBalanceUsd())%>
									</h6>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
	<%
	if (branchToPayTo!=null && !branchToPayTo.trim().equalsIgnoreCase("")){
	%>
	<div class="col-md-3 col-sm-6 col-xs-12 ">
		<div class="card radius-10  bg-gradient" style="background-color: #f8bad9 !important">
			<div class="card-body" style="cursor: pointer"
				onclick="popitup ('branchDebtsPopUp?reportBranchDebtOnly=Y&otherBranchTransEntity=<%=branchToPayTo%>' , '' , 1000 ,600);">
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
	</div>
</div>
<hr>
<%-- 	
<div class="col">

			<div class="card">
                     <div class="card-content">
                         <div class="media align-items-stretch">
                             <div class="p-2 text-center bg-danger rounded-left">
                                 <i class="fa fa-thumbs-down font-large-2 text-white"></i>
                             </div>
                             <div class="p-2 media-body">
                                 <h5>شحنات راجعة</h5>
                                 <h5 class="text-bold-400 mb-0">
                                 <%=numFormat.format(shipInfo.get("cncl")) %>
                                 <%=cnclPercentage %> %</h5>
                             </div>
                         </div>
                     </div>
                 </div>
	</div>
	
<div class="col">
	<a href="#" onclick ="popitup ('branch_underprocess_shipmentPopUp?shipmentUnderProcessFromBranch=<%=user.getBranchCode()%>&shipmentUnderProcessToBranch=<%=BRANCH_TO_PAY_TO_G%>' , '' , 1000 ,600);">
		<div class="card">
			<div class="card-content">
			    <div class="media align-items-stretch">
			        <div class="p-2 text-center bg-amber rounded-left" style='background-color:#10213b'>
			    		<i class="fa fa-truck font-large-2 text-white"></i>
					</div>
		           	<div class="p-2 media-body">
		                 <h5>قيد التوصيل</h5>
		                 <h5 class="text-bold-400 mb-0">
		                 <%=numFormat.format(shipInfo.get("underprocess")) %></h5>
		             </div>
		        </div>
		    </div>
		</div>
	</a>
</div>

<div class="col">
	<div class="card">
		<div class="card-content">
		    <div class="media align-items-stretch">
		        <div class="p-2 text-center bg-success rounded-left" style='background-color:#10213b'>
		    		<i class="fa fa-thumbs-up font-large-2 text-white"></i>
				</div>
	           	<div class="p-2 media-body">
	                 <h5>سلمت بنجاح لم يتم محاسبتها</h5>
	                 <h5 class="text-bold-400 mb-0">
	                 <%=numFormat.format(shipInfo.get("dlvd")) %></h5>
	             </div>
	        </div>
	    </div>
	</div>
</div> --%>


<div class="col-xl-12 col-lg-12">
	<div class="card">
		<div class="card-content">
			<div class="card-body">
				<ul class="nav nav-tabs nav-underline">
					<li class="nav-item"><a class="nav-link"
						id="a-receipts-balance" data-toggle="tab"
						onclick='changeActiveTab("receipts-balance")'
						href="#receipts-balance" aria-controls="homeIcon21"
						aria-expanded="true"><i class="la la-align-justify"></i>حسابات
							الوصولات</a></li>
					<li class="nav-item"><a class="nav-link"
						id='a-branches-transactions' data-toggle="tab"
						onclick='changeActiveTab("branches-transactions")'
						href="#branches-transactions" aria-controls="profileIcon21"
						aria-expanded="false"><i class="la la-header"></i>حركات مالية
							بين فرعين</a></li>
					<li class="nav-item"><a class="nav-link"
						id='a-liaisonagnet-transactions' data-toggle="tab"
						onclick='changeActiveTab("liaisonagent-transactions")'
						href="#liaisonagent-transactions" aria-controls="profile"
						aria-expanded="false"><i class="la la-header"></i>دفعة مندوب أرتباط</a></li>
				</ul>
				<div class="tab-content px-1 pt-1">
					<div role="tabpanel" class="tab-pane" id="receipts-balance"
						aria-labelledby="homeIcon2-tab1" aria-expanded="true">
						<%
						String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
							          		Render(branchesBalance  , out , request, response , Myglobals , objectState , pageName1);
							          		if (branchesBalance.getRecords() == 0){
							          			out.println("<h4>لاتوجد وصولات حاليا</h4>");
							          		}
						
						%>
					</div>
					<div class="tab-pane" id="branches-transactions" role="tabpanel"
						aria-labelledby="profileIcon2-tab1" aria-expanded="false">
						<%
						BranchesTransactions branchesTransactions = new BranchesTransactions(); 
							           		Render(branchesTransactions  , out , request, response , Myglobals , objectState , pageName1);
						%>
					</div>
				<div class="tab-pane" id="liaisonagent-transactions" role="tabpanel"
					aria-labelledby="profile" aria-expanded="false">
					<%
					LiaisonOrPickUpAgentSharePayments liaisonOrPickUpAgentSharePayments = 
					new LiaisonOrPickUpAgentSharePayments(); 
					liaisonOrPickUpAgentSharePayments.setEntityType(FinOperationEntity.LIAISON_AGENT);
					Render(liaisonOrPickUpAgentSharePayments  , out , request, response , Myglobals , objectState , pageName1);
					%>
				</div>
				</div>
			</div>
		</div>
	</div>
</div>

<%} %>

<%@ include file="../Main/footer.jsp"%>

<script>
<%
if (branchesBalance.isFoundRepeated()){%>
	generalErrorPrettyMsg("هنالك وصولات مشتبه بتكرارها");
<%}%>
$('#barcode_checker').focus();
var input = document.getElementById("barcode_checker");
var old_receipt = '';
var scannedCounter = 1;
var sumOfSelectedCases = 0;

var debtIqd = <%=entityBalance.get(StandardFinCurrency.IQD)%>
var debtUsd = <%=entityBalance.get(StandardFinCurrency.USD)%>

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

if(input){
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
		            scrollTop: $("#"+barcodeScanned).offset().top -80
		        }, 500);
			  
			  $("#pmtcheck_"+caseid).prop('checked', true);
			  //$("#pmtcheck_"+caseid).prop.addClass("checked", true);
	
			  $(old_receipt).parent().css({ 
		            "background-color": "rgba(249,210,179,0.37)", 
		            "border": "2px solid #dc2b2b",
		            
		   		});
			  old_receipt = reciept; 
			  scannedCounter++;
			  calculateSumOfSelectedCases();
		  }	 
		  input.value = '';
		  $('#barcode_checker').focus();
	  } 
	});
}



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

function checkAllDate(that){
	var check = false;
	
	if ($(that).prop("checked")){
		check = true;
	}
	var dateRec = $(that).attr("attr-value");
	$("input[data-single-check-date-"+dateRec+"]").each(function() {
		$(this).attr("checked", check);
	});
	calculateSumOfSelectedCases();
}

function calculateSumOfSelectedCases(){
	var totalreceiptsNetAmtIqd = 0, totalReceiptsNetAmtUsd = 0;
	$('[id^=pmtcheck_]').each(function() {
		if ($(this).prop("checked") == true){
		    var caseid = this.id.split('_').pop();
		    totalreceiptsNetAmtIqd += parseInt($("#td_netamt_iqd_"+caseid).attr("data-netval"));
		    totalReceiptsNetAmtUsd += parseInt($("#td_netamt_usd_"+caseid).attr("data-netval"));
		}
	});
	
	showTotalSelectedAmtForCustomers(totalreceiptsNetAmtIqd.toLocaleString()+' د.ع </br> '+totalReceiptsNetAmtUsd.toLocaleString()+' $' );
	
	var totalAmountToPayIqd = totalreceiptsNetAmtIqd+debtIqd;
	var totalAmountToPayUsd = totalReceiptsNetAmtUsd+debtUsd;
	console.log("totalAmountToPayIqd-->"+totalAmountToPayIqd);
	
	$("#receipts_amount_topay_iqd").val(totalreceiptsNetAmtIqd);
	$("#receipts_amount_topay_usd").val(totalReceiptsNetAmtUsd);
	
	$("#receipts_and_debt_amount_topay_iqd").html(totalAmountToPayIqd.toLocaleString());
	$("#receipts_and_debt_amount_topay_usd").html(totalAmountToPayUsd.toLocaleString());
	
	$("#total_receipts_amount_to_be_paid_iqd").html((totalreceiptsNetAmtIqd).toLocaleString());
	$("#total_receipts_amount_to_be_paid_usd").html((totalReceiptsNetAmtUsd).toLocaleString());
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


$(document).ready(function() {
	calculateSumOfSelectedCases ();
	$("#smarty_new_submit_com_app_incomeoutcome_BranchesBalance").css("display", "none");
});

function changeActiveTab( tab){
	localStorage.setItem('activeTab_branches', tab);
}

$(function (){
	var selectedTab = localStorage.getItem('activeTab_branches');
	if(selectedTab){
       $('#'+selectedTab).addClass('active show');
       $('#a-'+selectedTab).addClass('active');
   }else{
   	 $('#receipts-balance').addClass('active show');
        $('#a-receipts-balance').addClass('active');
   }
});


//change agent share on the fly
function changeBranchShareCost(caseChainId, caseId){
	Swal.fire({
		title: 'تعديل أجرة الشحن للفرع (يرجى ادخال الاصفار)',
		input: 'number',
	    showCancelButton: true,
	    confirmButtonText: "موافق",
	    cancelButtonText: "الغاء",
	    buttonsStyling: true,
		  inputAttributes: {
		    id: 'newBranchShare'
		  },
	}).then((result)=> {

		if (result.isConfirmed) {
			var newBranchShare = $("#newBranchShare").val();
			var maxAllowedShare = $("#td-shipment-cost-caseid-"+caseId).attr("data-val-max-allowed");
			maxAllowedShare = maxAllowedShare -0;
			if(newBranchShare && $("#newBranchShare").val()>=0){
				if (newBranchShare <= maxAllowedShare){
				    $.ajax({
				        type: "POST",
				        url: "../../__SmartyUpdateSingleDataOnTheFlySRVL",
				        data: { 't': 'p_caseschain', 'k':'cc_id',  'nv': newBranchShare, 'kv':caseChainId , 
				        	'fw':'محاسبة الفروع', 'cto' :'cc_pathcost'},
				        cache: false,
				        success: function(data) {
				        	console.log(data);
				        	Swal.fire(
				            "تمت العملية بنجاح!",
				            "تم الحفظ!",
				            "success"
				        	).then((result) => {
				        		$("#td-shipment-cost-caseid-"+caseId).html(newBranchShare);
				        		$("#td-shipment-cost-caseid-"+caseId).digits();
				        		$("#td-shipment-cost-caseid-"+caseId).attr("data-val", newBranchShare);
				        		$("#td-shipment-cost-caseid-"+caseId).append("<a href='javascript:changeBranchShareCost("+caseChainId+","+caseId+")'><li class='fa fa-pencil'></li></a>");
				        		//change net amt
				        		var newNetIqd = $("#receipt-amt-iqd-"+caseId).attr("data-val")-newBranchShare;
				        		$("#td_netamt_iqd_"+caseId).html(newNetIqd);
				        		$("#td_netamt_iqd_"+caseId).digits();
				        		$("#td_netamt_iqd_"+caseId).attr("data-netval", newNetIqd);
				        		calculateSumOfSelectedCases();
				        	});
				            
				        },
				        error: function () {
				        	Swal.fire(
				            "Internal Error",
				            "Oops, your update was not saved.",
				            "error"
				            )
				        }
				    });
				}else{
					Swal.fire({
					      title: 'لا يمكن أن تكون أجرة الفرع أكبر من أجرة الشحن المستقطعة من المتجر ',
					   	  confirmButtonText: 'نعم'
					    });
				}
			}else{
				Swal.fire({
				      title: 'تم الالغاء',
				   	  confirmButtonText: 'نعم'
				    });
			}
		}
	}, 
	{
	}); 
}
</script>
