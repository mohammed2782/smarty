<%@page import="com.app.util.UtilitiesFeqar,com.app.financials.*"%>
<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.AgentsPayments"%>
<%@ page
	import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet,
java.text.DecimalFormat,com.app.incomeoutcome.AgentsBalance"%>

<%
if (user.isHaveFullServices()){
Connection conn1 = null;
PreparedStatement pst = null;
ResultSet rs = null;
UtilitiesFeqar ut = new UtilitiesFeqar();
DecimalFormat numFormat = new DecimalFormat("#,###,###.##");

int totalRtnNotCollectedFromAgent = 0, dlvNotPaid = 0, withAgentStill = 0, withAgentPostponed=0;
String searchAgent = (String) request.getParameter("myagentsearch");
String agentAcct = (String) request.getParameter("agentAcct");
int denomanator = 0;
if (agentAcct != null) {
	Myglobals.smartyGlobalsAssArr.put("agentAcct", (String) agentAcct);
} else if (Myglobals.smartyGlobalsAssArr.containsKey("agentAcct")
		&& Myglobals.smartyGlobalsAssArr.get("agentAcct") != null) {
	agentAcct = (String) Myglobals.smartyGlobalsAssArr.get("agentAcct");

}
LinkedHashMap<String, String> agentsList = new LinkedHashMap<String, String>();
LinkedHashMap<String, String> agentInfo = new LinkedHashMap<String, String>();
int acctId = Integer.parseInt(Myglobals.smartyGlobalsAssArr.get("usid") + "");
HashMap<StandardFinCurrency, Long> entityBalance = new HashMap<StandardFinCurrency, Long>();
HashMap<StandardFinCurrency, Long> accountBoxBalance = new HashMap<StandardFinCurrency, Long>();
AccountantBoxBean accountantBoxBean = null;
try {

	conn1 = mysql.getConn();
	accountantBoxBean = UtilitiesSafeFinancials.GetAccountantBox(conn1, user.getUsid(), user.getBranchCode());
	agentsList = ut.getListOfAgents(conn1, (int) Myglobals.smartyGlobalsAssArr.get("userstorecode"));
	if (agentAcct != null) {
		entityBalance = ut.getEntityDebtBalanceUpToSpecificPayment(conn1, FinOperationEntity.AGENT,
		Integer.parseInt(agentAcct), 0, user.getBranchCode());

		totalRtnNotCollectedFromAgent = ut.getRtnNotReceived(conn1, Integer.parseInt(agentAcct));
		dlvNotPaid = ut.getAgentDeliveredNoPaid(conn1, Integer.parseInt(agentAcct));
		withAgentStill = ut.getWithAgent(conn1, Integer.parseInt(agentAcct));
		withAgentPostponed = ut.getWithAgentPostponed(conn1, Integer.parseInt(agentAcct));
		if (denomanator == 0)
	denomanator = 1;
	}
} catch (Exception e) {
	e.printStackTrace();
} finally {
	try {
		rs.close();
	} catch (Exception e) {
	}
	try {
		pst.close();
	} catch (Exception e) {
	}
	try {
		conn1.close();
	} catch (Exception e) {
	}
}
AgentsBalance agl = new AgentsBalance();
if (entityBalance != null && !entityBalance.isEmpty()) {
	agl.setDlvAgentDebtIqd(entityBalance.get(StandardFinCurrency.IQD));
	agl.setDlvAgentDebtUsd(entityBalance.get(StandardFinCurrency.USD));
}
%>

<form action="?myagentsearch=1" method="post" name="search_cust_form"
	style='background-color: transparent; margin-bottom: 5px;'>
	<div class="row">
		<div class="col-3">
			<select class='select2' id='agentAcct' style="width: 200px;"
				name='agentAcct'>
				<option value=''></option>
				<%
				for (String custid : agentsList.keySet()) {
					if (agentAcct != null && agentAcct.equalsIgnoreCase(custid)) {
				%>
				<option value='<%=custid%>' selected><%=agentsList.get(custid)%></option>
				<%
				} else {
				%>
				<option value='<%=custid%>'><%=agentsList.get(custid)%></option>
				<%
				}
				}
				%>
			</select>
		</div>
		<div class="col-3">
			<button type='submit' class="btn btn-primary btn-darken-4"
				style='margin-right: 10px; background: #623da5 !important;'
				type="button">
				عرض التفاصيل الماليه لمندوب التوصيل<i
					class="fa fa-search m-right-xs"></i>
			</button>
		</div>
		<div class="col-md-4 col-sm-6 col-xs-12 ">
			<div class="card radius-10  bg-gradient"
				style="background-color: #0dcaf0;">

				<div class="card-body" style="cursor: pointer"
					onclick="popitup('../bank/FinBoxDtlsPopUp?finboxacctid=<%=accountantBoxBean.getBoxId()%>', 'Transactions' , 1150,700)"
					style="width:30px; ">
					<div class="d-flex align-items-center"
						style="justify-content: center;">
						<div class="text-white mb-0 "
							style="margin-left: auto; margin-right: auto;">
							<p class="text-dark mb-0">المبلغ في الصندوق د.ع</p>
							<h6 class="number-font  text-dark mb-0 "><%=numFormat.format(accountantBoxBean.getCurrentBalanceIqd())%>
							</h6>
						</div>
						<div class="text-white mb-0"
							style="margin-left: auto; margin-right: auto;">
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
<%
if (agentAcct != null && !agentAcct.trim().equalsIgnoreCase("")) {

	final String CARD_STYLE_1COL = "col-lg-1 col-md-2 col-sm-12 card-gradient-md-border border-right-amber border-right-lighten-5";
	final String CARD_STYLE_2COL = "col-lg-2 col-md-4 col-sm-12 card-gradient-md-border border-right-info border-right-lighten-5";
	final String CARD_STYLE_3COL = "col-lg-3 col-md-3 col-sm-12 card-gradient-md-border border-right-info border-right-lighten-5";
	final String CARD_STYLE_4COL = "col-lg-4 col-md-6 col-sm-12 card-gradient-md-border border-right-amber border-right-lighten-5";
%>
<div class="row">
	<div class="col-12">
		<div class="card bg-gradient-x-primary">
			<div class="card-content">
				<div class="row">
					<div class="<%=CARD_STYLE_4COL%>">
						<a href="#"
							onclick="popitup ('agentDebtsDtlsPopUp?agentaccountdebts=<%=agentAcct%>' , '' , 1000 ,600);">
							<div class="card-body text-center">
								<div class='row'>
									<div class='col-6'>
										<h1 class="display-4 text-white" style='font-size: 2.3rem;'>
											<%=agl.numFormat.format(entityBalance.get(StandardFinCurrency.IQD))%>
											د.ع
										</h1>
									</div>
									<div class='col-6'>
										<h1 class="display-4 text-white" style='font-size: 2.3rem;'>
											<%=agl.numFormat.format(entityBalance.get(StandardFinCurrency.USD))%>
											$
										</h1>
									</div>
								</div>
								<span class="text-white">الديون</span>
							</div>
						</a>
					</div>
					<div class="<%=CARD_STYLE_2COL%>">
						<div class="card-body text-center">
							<h1 class="display-4 text-white" style='font-size: 2.3rem;'>
								<i class="fa fa-thumbs-up font-large-2"></i>
								<%=dlvNotPaid%></h1>
							<span class="text-white">واصل لم يتم محاسبة المندوب</span>
						</div>
					</div>
					<div class="<%=CARD_STYLE_2COL%>">
						<div class="card-body text-center">
							<h1 class="display-4 text-white" style='font-size: 2.3rem;'>
								<i class="fa fa-truck font-large-2"></i>
								<%=withAgentStill%></h1>
							<span class="text-white">قيد التوصيل</span>
						</div>
					</div>
					<div class="<%=CARD_STYLE_2COL%>">
						<div class="card-body text-center">
							<h1 class="display-4 text-white" style='font-size: 2.3rem;'>
								<i class="fa fa-hourglass font-large-2"></i>
								<%=withAgentPostponed%></h1>
							<span class="text-white">مؤجل</span>
						</div>
					</div>
					<div class="<%=CARD_STYLE_2COL%>">
						<form id="forrediredcttortnnotreceived"
							action="../Returnables/agentReturnables?myagentsearch=1"
							method="post">
							<a href="javascript:;"
								onclick="document.getElementById('forrediredcttortnnotreceived').submit();">
								<input type="hidden" name="agentAccountReturnProcess"
								value='<%=agentAcct%>' />
								<div class="card-body text-center">
									<h1 class="display-4 text-white" style='font-size: 2.3rem;'>
										<i class="fa fa-thumbs-down font-large-2"></i>
										<%=totalRtnNotCollectedFromAgent%></h1>
									<span class="text-white">راجع لم يتم أستلامه من المندوب</span>
								</div>
							</a>
						</form>
					</div>
				</div>
				<!--end row-->
			</div>
		</div>
	</div>
</div>
<%
String pageName1 = this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName();
Render(agl, out, request, response, Myglobals, objectState, pageName1);
if (agl.getRecords() == 0) {
%>
<div
	class="alert border-0 border-start border-5 border-white alert-dismissible fade show py-2">
	<div class="d-flex align-items-center">
		<div class="font-35 text-dark">
			<i class="bx bx-info-square"></i>
		</div>
		<div class="ms-3">
			<h6 class="mb-0 text-dark">لاتوجد وصولات للمحاسبة</h6>
			<div class="font-35 text-dark">كل شحنات المندوب الواصلة تمت
				المحاسبة عليها</div>
		</div>
	</div>
	<%-- <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button> --%>
</div>
<script>
			$("#main_row_com_app_incomeoutcome_AgentsBalance").css("display","none");
		</script>
<%
}
AgentsPayments ap = new AgentsPayments();
Render(ap, out, request, response, Myglobals, objectState, pageName1);
%>
<!-- end recent activity -->
<%
}
}else{
	out.println("<h1>منطقة محظورة</h1>");
}
%>

<%@ include file="../Main/footer.jsp"%>
<script>
$(document).ready(function() {
	$("#heading-elements-com_app_incomeoutcome_AgentsBalance").css("display","none");
});

function checkAll(){
	var check = false;
	if ($("#checkboxall").prop("checked") == true)
		check = true;
	$('[id^=pmtcheck_]').each(function() {
		
		$(this).attr("checked", check);
	});
}

function checkBoxPmtClicked ( ){
	var selectedCases ="";
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
	$("#selected_casesto_pay").val($("#selected_casesto_pay").val()+(selectedCases));	
}

$('#barcode_checker').focus();
var input = document.getElementById("barcode_checker");
var old_receipt = '';
var scannedCounter = 1;
var inputKeyCodeArr = [];
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
		  if (caseid){
			  $('html, body').stop().animate({
		            scrollTop: $("#"+barcodeScanned).offset().top - 80 }, 500);
			  $("#pmtcheck_"+caseid).prop('checked', true);
			  $(old_receipt).parent().css({ 
		            "background-color": "rgba(249,210,179,0.37)", 
		            "border": "2px solid #dc2b2b",});
			  old_receipt = reciept; 
			  scannedCounter++;
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
</script>

<script>
function changeAllCasesAgentShareBackDated(agentId){
	$("#loading").css("display", "flex");
	
	$.confirm({ 
	    title: 'تعديل أجور المندوب  بأثر رجعي؟',
	    content: 'سيتم تعديل أجور المندوب لكل الشحنات الغير محاسب عليها مع مندوب التوصيل . اختر نعم للتاكيد',
	    buttons: {
	        confirm:{
	        	text :'نعم',
	        	action : function () {
	        		confirmedChangeAgentShare(agentId);
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
function confirmedChangeAgentShare(agentId){
	var dataToSend = {"agentId":agentId};
	$.post('../../ChangeAgentShareBackDatedSRVL' , dataToSend, function(data, status){ 
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


//change agent share on the fly
function changeAgentShareCost(caseId){
	Swal.fire({
		title: 'تعديل أجرة المندوب (يرجى ادخال الاصفار)',
		input: 'number',
	    showCancelButton: true,
	    confirmButtonText: "موافق",
	    cancelButtonText: "الغاء",
	    buttonsStyling: true,
		  inputAttributes: {
		    id: 'newAgentShare'
		  },
	}).then((result)=> {

		if (result.isConfirmed) {
			var newAgentShare = $("#newAgentShare").val();
			if(newAgentShare && $("#newAgentShare").val()>=0){
			    $.ajax({
			        type: "POST",
			        url: "../../__SmartyUpdateSingleDataOnTheFlySRVL",
			        data: { 't': 'p_cases', 'k':'c_id',  'nv': newAgentShare, 'kv':caseId , 
			        	'fw':'حسابات مندوب التوصيل', 'cto' :'c_agentshare'},
			        cache: false,
			        success: function(data) {
			        	console.log(data);
			        	Swal.fire(
			            "تمت العملية بنجاح!",
			            "تم الحفظ!",
			            "success"
			        	).then((result) => {
			        		$("#agent-share-caseid-"+caseId).html(newAgentShare);
			        		$("#agent-share-caseid-"+caseId).digits();
			        		$("#agent-share-caseid-"+caseId).attr("data-val", newAgentShare);
			        		$("#agent-share-caseid-"+caseId).append("<a href='javascript:changeAgentShareCost("+caseId+")'><li class='fa fa-pencil'></li></a>");
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
	}); 
}

</script>