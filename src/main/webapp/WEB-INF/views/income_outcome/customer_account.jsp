<%@page
	import="com.app.util.UtilitiesFeqar, com.app.util.UtilitiesAyatLina, com.app.financials.*"%>
<%@ include file="../Main/Main.jsp"%>
<%@ page
	import="java.text.DecimalFormat,com.app.util.Utilities,java.sql.PreparedStatement,
	java.sql.ResultSet,com.app.incomeoutcome.CustomerBalance,com.app.incomeoutcome.CustomerTransactions"%>
<%
if (user.isHaveFullServices()){
Connection conn1 = null;
PreparedStatement pst = null; 
ResultSet rs = null; 
UtilitiesFeqar ut = new UtilitiesFeqar();
UtilitiesAyatLina utal = new UtilitiesAyatLina();

String CUSTOMER_ACCOUNT_FIN_G = (String)request.getParameter("mycustomersearch");

if (request.getParameter("mycustomersearch")!=null){
	%>
	<script>
	localStorage.removeItem("activeTab");
	</script>
	<%
}

int denomanator = 0;
DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
if (CUSTOMER_ACCOUNT_FIN_G !=null){
	Myglobals.smartyGlobalsAssArr.put("CUSTOMER_ACCOUNT_FIN_G", (String)CUSTOMER_ACCOUNT_FIN_G);
	
}else if (Myglobals.smartyGlobalsAssArr.containsKey("CUSTOMER_ACCOUNT_FIN_G") && Myglobals.smartyGlobalsAssArr.get("CUSTOMER_ACCOUNT_FIN_G")!=null){
	CUSTOMER_ACCOUNT_FIN_G = (String)Myglobals.smartyGlobalsAssArr.get("CUSTOMER_ACCOUNT_FIN_G");
}
LinkedHashMap<String,String> customersList = new LinkedHashMap<String,String>();
CustomerBalance cbl = new CustomerBalance(); 
HashMap<String, Integer> shipInfo = new HashMap<String, Integer>();
HashMap<String, Integer> shipAmt = new HashMap<String, Integer>();
AccountantBoxBean accountantBoxBean = null;
double underProcessAmt = 0;
HashMap<StandardFinCurrency, Long> entityBalance = new HashMap<StandardFinCurrency, Long>();
HashMap<StandardFinCurrency, Long> entityBalanceUnderProcess = new HashMap<StandardFinCurrency, Long>();
	try{
		conn1 = mysql.getConn();
		accountantBoxBean = UtilitiesSafeFinancials.GetAccountantBox(conn1, user.getUsid(), user.getBranchCode());
		customersList = ut.getListOfMasterCustomers(conn1,(int)Myglobals.smartyGlobalsAssArr.get("userstorecode"));
		if (CUSTOMER_ACCOUNT_FIN_G !=null){
			entityBalance= ut.getEntityDebtBalanceUpToSpecificPayment(conn1, 
					FinOperationEntity.CUSTOMER, Integer.parseInt(CUSTOMER_ACCOUNT_FIN_G), 0,
					user.getBranchCode());
			cbl.setDebtIqd(entityBalance.get(StandardFinCurrency.IQD));
			cbl.setDebtUsd(entityBalance.get(StandardFinCurrency.USD)); 
			shipInfo = ut.calcMasterCustomerShipmentsInfo(conn1, CUSTOMER_ACCOUNT_FIN_G);
			entityBalanceUnderProcess= utal.calcMasterCustomerShipmentsAmt(conn1, CUSTOMER_ACCOUNT_FIN_G);
			
			for(String key:shipInfo.keySet()){
				if(!key.equalsIgnoreCase("underprocess"))
			denomanator += shipInfo.get(key);
			}
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
<div class="row">
	<div class="col-9">
		<form action="?searchcustomer=1" method="post"
			name="search_cust_form" class="form-horizontal form-label-left">
			<div class="row">
				<div class="col-4">
					<select class='select2' id='mycustomersearch'
						style='width: 200px;' name='mycustomersearch'>
						<option value=''></option>
						<%
						for (String custid : customersList.keySet()){
							if (CUSTOMER_ACCOUNT_FIN_G!=null && CUSTOMER_ACCOUNT_FIN_G.equalsIgnoreCase(custid)){
							%>
							<option value='<%=custid%>' selected><%=customersList.get(custid)%></option>
							<%
							}else{
							%>
							<option value='<%=custid%>'><%=customersList.get(custid)%></option>
							<%}
						 }
						%>
					</select>
				</div>
				<div class='col-8'>
					<button type='submit' class="btn btn-warning"
						style='margin-right: 10px' type="button">
						عرض التفاصيل الماليه للعميل <i class="fa fa-search m-right-xs"></i>
					</button>
				</div>
			</div>
		</form>
	</div>
</div>	
<%
if(CUSTOMER_ACCOUNT_FIN_G != null && CUSTOMER_ACCOUNT_FIN_G.trim().length()>0){
	%>
<div class="row row-cols-2 row-cols-md-2 row-cols-xl-2"
	style='margin-top: 10px;'>
	<div class="col">
		<div class="card">
			<div class="card-content">
				<div class="card-body">
					<div class="media d-flex">
						<div class="align-self-center">
							<i class="icon-pencil blue-grey font-large-2 float-left"></i>
						</div>
						<div class="media-body text-right">
							<h3><%=cbl.numFormat.format(entityBalance.get(StandardFinCurrency.IQD))%>د.ع
							</h3>
							<h3><%=cbl.numFormat.format(entityBalance.get(StandardFinCurrency.USD))%>دولار أمريكي
							</h3>
							<span>ديون سابقة</span>
						</div>
					</div>
				</div>
			</div>
		</div>

	</div>

	<div class="col">
		<div class="card" style="width: 95%;">
			<div class="card-content">
				<div class="card-body" style="cursor: pointer"
					onclick="popitup('./customerUnderProcessCasesPopUp', 'وصولات العميل قيد التوصيل' , 1150,700)">
					<div class="media d-flex">
						<div class="align-self-center">
							<i class="icon-graph success font-large-1 float-left"></i>
						</div>
						<div class="media-body text-center">
							<h4><%=numFormat.format(shipInfo.get("underprocess"))%></h4>
							<span>قيد التوصيل</span>
						</div>
						<div class="align-self-left">
						<h5><%=cbl.numFormat.format(entityBalanceUnderProcess.get(StandardFinCurrency.IQD))%> د.ع 
							</h5>
							<h5><%=cbl.numFormat.format(entityBalanceUnderProcess.get(StandardFinCurrency.USD))%> $
							</h5>
							<span>مبلغ الوصولات</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="col ">
			<div class="card radius-10  bg-gradient"
				style="background-color: #0dcaf0;">

				<div class="card-body" style="cursor: pointer"
					onclick="popitup('../bank/FinBoxDtlsPopUp?finboxacctid=<%=accountantBoxBean.getBoxId()%>', 'Transactions' , 1150,700)"
					style="width:30px; ">
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
	<div class='col'>
		<button type="button" class="btn btn-info px-5"
			onclick="changeAllCasesShipmentCostsBackDated('<%=CUSTOMER_ACCOUNT_FIN_G%>');">تعديل مبلغ الشحنات بأثر رجعي</button>
	</div>
</div>
<%

	}
%>
<%

if(CUSTOMER_ACCOUNT_FIN_G != null && CUSTOMER_ACCOUNT_FIN_G.trim().length()>0){
%>
<div class="col-xl-12 col-lg-12">
	<div class="card">
		<div class="card-content">
			<div class="card-body">
				<ul class="nav nav-tabs nav-underline">
					<li class="nav-item"><a class="nav-link" id="a-cust-balance"
						data-toggle="tab" onclick='changeActiveTab("cust-balance")'
						href="#cust-balance" aria-controls="homeIcon21"
						aria-expanded="true"><i class="la la-align-justify"></i>الشحنات</a>
					</li>
					<li class="nav-item"><a class="nav-link" id='a-cust-debts'
						data-toggle="tab" onclick='changeActiveTab("cust-debts")'
						href="#cust-debts" aria-controls="profileIcon21"
						aria-expanded="false"><i class="la la-header"></i>حركات مالية</a>
					</li>
				</ul>
				<div class="tab-content px-1 pt-1">
					<div role="tabpanel" class="tab-pane" id="cust-balance"
						aria-labelledby="homeIcon2-tab1" aria-expanded="true">
						<%
						String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
						Render(cbl  , out , request, response , Myglobals , objectState , pageName1);
						if (cbl.getRecords() == 0){
							out.println("<h4>لا توجد مبالغ مستحقة الدفع حاليا</h4>");
						}
						%>
					</div>
					<div class="tab-pane" id="cust-debts" role="tabpanel"
						aria-labelledby="profileIcon2-tab1" aria-expanded="false">
						<%CustomerTransactions cd = new CustomerTransactions();
                    cd.setCustomerId(Integer.parseInt(CUSTOMER_ACCOUNT_FIN_G));
                    Render(cd  , out , request, response , Myglobals , objectState , pageName1);
					%>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<%
	}
}else{
	out.println("<h1>منطقة محظورة</h1>");
}
%>
<%@ include file="../Main/footer.jsp"%>
<script>
$(document).ready(function() {
	$("#heading-elements-com_app_incomeoutcome_CustomerBalance").css("display","none");
});


console.log($("#dlvd_percentage").val());
var sumOfSelectedCases = 0;
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

function checkAllCust(that){
	var check = false;
	if ($(that).prop("checked"))
		check = true;
	var dateVal = $(that).attr("data-val");
	console.log("----dateVal--"+dateVal);
	$("input[data-group-date-"+dateVal+"]").each(function() {
		$(this).attr("checked", check);
	});
	calculateSumOfSelectedCases();
}
function calculateSumOfSelectedCases(){
	var totalNetAmtIqd = 0, totalNetAmtUsd=0;
	$('[id^=pmtcheck_]').each(function() {
		if ($(this).prop("checked") == true){
		    var caseid = this.id.split('_').pop();
		    totalNetAmtIqd += parseInt($("#td_netamt_iqd_"+caseid).attr("data-netval"));
		    totalNetAmtUsd += parseInt($("#td_netamt_usd_"+caseid).attr("data-netval"));
		}
	});
	
	showTotalSelectedAmtForCustomers(totalNetAmtIqd.toLocaleString()+' د.ع </br> '+totalNetAmtUsd.toLocaleString()+' $' );
	
	$("#amount_topay_iqd").val(totalNetAmtIqd);
	$("#amount_topay_usd").val(totalNetAmtUsd);
	$("#totalamountshouldbepaid_iqd").html(totalNetAmtIqd.toLocaleString());
	$("#totalamountshouldbepaid_usd").html(totalNetAmtUsd.toLocaleString());
}
function checkBoxPmtClicked(){
	calculateSumOfSelectedCases();
}
$('#customer-balance-settle-form').submit(function(e){
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
	if ($("#selected_casesto_pay").val().length>0){
		this.submit();
		
	}else{
		e.preventDefault();
		generalErrorPrettyMsg("لم يتم تحديد أي وصل");
	}
	
	//console.log("selected_casesto_pay---->"+$("#selected_casesto_pay").val());
});




function changeActiveTab( tab){
	 localStorage.setItem('activeTab', tab);
}
$(function (){
	var selectedTab = localStorage.getItem('activeTab');
	if(selectedTab){
        $('#'+selectedTab).addClass('active show');
        $('#a-'+selectedTab).addClass('active');
    }else{
    	 $('#cust-balance').addClass('active show');
         $('#a-cust-balance').addClass('active');
    }
})
</script>




<script>
function changeAllCasesShipmentCostsBackDated(masterCustId){
	$("#loading").css("display", "flex");
	
	$.confirm({ 
	    title: 'تعديل أجور الشحن للعميل بأثر رجعي؟',
	    content: 'سيتم تعديل أسعار الشحن للعميل لكل الشحنات الغير محاسب عليها مع العميل . اختر نعم للتاكيد',
	    buttons: {
	        confirm:{
	        	text :'نعم',
	        	action : function () {
	        		confirmedChangeCasesShipmentCost (masterCustId);
	        	}
	        },
	        cancel:{
	        	text :'لا',
	        	action : function () {
	        		hideLoader();
	        		}
	        }
	    }
	});
}
function confirmedChangeCasesShipmentCost(masterCustId){
	
	var dataToSend = {"masterCustId":masterCustId};
	$.post('../../ChangeMasterCustomerShipmentCostSRVL' , dataToSend, function(data, status){ 
		if (status=='success'){
			$.alert({
			    title: '',
			    content: 'تم التغيير',
			});
			hideLoader();
		}else{
			alert("Error, please contact Mohammed Nafie");
			hideLoader();
		}
 	});
	
}
$('#barcode_checker').focus();
var input = document.getElementById("barcode_checker");
var old_receipt = '';
var scannedCounter = 1;
var sumOfSelectedCases = 0;
var inputKeyCodeArr = [];
input.addEventListener("keyup", function(event) {
	  if (event.keyCode === 13) {
		  $(input).val(inputKeyCodeArr.join(""));
		  inputKeyCodeArr = [];
		  var barcodeScanned = ((input.value).replace("/", "forwardslash")).replace(/ /g,"");
		  if (barcodeScanned !== null && barcodeScanned !== undefined){
			  var reciept = document.getElementById(barcodeScanned);
			  $(reciept).parent().css({ 
		            "background-color": "rgba(194 , 214 , 245 , 0.7)", 
		            "border": "2px solid rgb(43 73 220)",
		   		});
			  var caseid = $(reciept).attr("caseid");
			  if (caseid){
				  $('html, body').stop().animate({
			            scrollTop: $("#"+barcodeScanned).offset().top - 80
			        }, 500);
				  $("#pmtcheck_"+caseid).prop('checked', true);
				  $(old_receipt).parent().css({ 
			            "background-color": "rgba(249,210,179,0.37)", 
			            "border": "2px solid #dc2b2b",});
				  old_receipt = reciept; 
				  scannedCounter++;
				  calculateSumOfSelectedCases();
			  }else{
				  generalErrorPrettyMsg(" هذا الوصل غير موجود "+barcodeScanned);
			  }
		  }	 
		  input.value = '';
		  $('#barcode_checker').focus();
	  }else{
		  if (event.keyCode >= 48 && event.keyCode <= 57){
			  inputKeyCodeArr.push(String.fromCharCode((96 <= event.keyCode && event.keyCode <= 105)? event.keyCode-48 : event.keyCode));
		  }else if (event.keyCode >= 65 && event.keyCode <= 90){
			  inputKeyCodeArr.push(String.fromCharCode((96 <= event.keyCode && event.keyCode <= 105)? event.keyCode-48 : event.keyCode));
		   }else if (event.keyCode >= 96 && event.keyCode <= 105){
			   inputKeyCodeArr.push(String.fromCharCode((96 <= event.keyCode && event.keyCode <= 105)? event.keyCode-48 : event.keyCode));
		   }
	  }  
	});
	
function syncCustomerPaymentsWithSource(customerPmtId, userId){
	$("#loading").css("display", "flex");
	
	$.confirm({ 
	    title: 'مزامنة دفعة العميل مع نظام العميل',
	    content: 'سيتم مزامنة كل الشحنات للعميل. اختر نعم للتاكيد',
	    buttons: {
	        confirm:{
	        	text :'نعم',
	        	action : function () {
	        		//alert(userId);
	        		confirmredSyncCustomerPaymentsWithSource (customerPmtId, userId);
	        	}
	        },
	        cancel:{
	        	text :'لا',
	        	action : function () {
	        		hideLoader();
	        		}
	        }
	    }
	});
}
function confirmredSyncCustomerPaymentsWithSource(customerPmtId, userId){
	
	var dataToSend = {fromcustomerpmt:'Y',pmtId: customerPmtId, actionUserId:userId };
	$.post('../../UpdateSuccessInOriginSystemSRVL' , dataToSend, function(data, status){ 
		if (status=='success'){
			$.alert({
			    title: '',
			    content: 'تمت المزامنة',
			});
			hideLoader();
		}else{
			alert("Error, please contact Mohammed Nafie");
			hideLoader();
		}
 	});
	
}

function changeShipmentCost(caseId){
 	Swal.fire({
		title: 'تعديل تكلفة الشحن (يرجى ادخال الاصفار)',
		input: 'number',
	    showCancelButton: true,
	    confirmButtonText: "موافق",
	    cancelButtonText: "الغاء",
	    buttonsStyling: true,
		  inputAttributes: {
		    id: 'newShipmentCost'
		  },
	}).then((result)=> {

		if (result.isConfirmed) {
			var newShipmentCost = $("#newShipmentCost").val();
			if(newShipmentCost && $("#newShipmentCost").val()>=0){
			    $.ajax({
			        type: "POST",
			        url: "../../ChangeSingleCaseSingleInformationSRVL",
			        data: { 'p_newValue': newShipmentCost, 'p_caseId':caseId , 'p_screenName':'حسابات العميل', 'p_columnToChange' :'shipment_cost'},
			        cache: false,
			        success: function(data) {
			        	console.log(data);
			        	Swal.fire(
			            "تمت العملية بنجاح!",
			            "تم الحفظ!",
			            "success"
			        	).then((result) => {
			        		
				        		$("#td-shipment-cost-caseid-"+caseId).html(newShipmentCost);
				        		$("#td-shipment-cost-caseid-"+caseId).digits();
				        		$("#td-shipment-cost-caseid-"+caseId).attr("data-val", newShipmentCost);
				        		$("#receipt-amt-iqd-"+caseId).attr("data-val");
				        		var newNetVal = $("#receipt-amt-iqd-"+caseId).attr("data-val") - newShipmentCost;
				        		//console.log("newNetVal--->"+newNetVal);
				        		$("#td_netamt_iqd_"+caseId).html(newNetVal);
				        		$("#td_netamt_iqd_"+caseId).digits();
				        		$("#td_netamt_iqd_"+caseId).attr("data-netval", newNetVal);
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
				      title: 'تم الالغاء',
				   	  confirmButtonText: 'نعم'
				    });
			}
		}
	}, 
	{
	}) 
}
</script>