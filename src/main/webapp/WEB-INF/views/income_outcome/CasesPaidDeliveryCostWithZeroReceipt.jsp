<%@page import="com.app.util.UtilitiesFeqar,com.app.financials.*"%>
<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.DisplayCasesWithPaidDelivery, com.app.incomeoutcome.PaymentsOfPaidCasesInAdvance"%>
<%@ page
	import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet,java.text.DecimalFormat"%>

<%
Connection conn1 = null;
PreparedStatement pst = null;
ResultSet rs = null;
UtilitiesFeqar ut = new UtilitiesFeqar();
DecimalFormat numFormat = new DecimalFormat("#,###,###.##");

int totalRtnNotCollectedFromAgent = 0, dlvNotPaid = 0, withAgentStill = 0, withAgentPostponed=0;
HashMap<StandardFinCurrency, Long> accountBoxBalance = new HashMap<StandardFinCurrency, Long>();
AccountantBoxBean accountantBoxBean = null;
try{
	conn1 = mysql.getConn();
	accountantBoxBean = UtilitiesSafeFinancials.GetAccountantBox(conn1, user.getUsid(), user.getBranchCode());
}catch (Exception e) {
	e.printStackTrace();
}finally {
	try {conn1.close();} catch (Exception e) {}
}
	String pageName1 = this.getClass().getPackage().getName() + "." + this.getClass().getSimpleName();
	DisplayCasesWithPaidDelivery  displayCasesWithPaidDelivery = new DisplayCasesWithPaidDelivery ();
	Render(displayCasesWithPaidDelivery, out, request, response, Myglobals, objectState, pageName1);
	
	PaymentsOfPaidCasesInAdvance  paymentsOfPaidCasesInAdvance = new PaymentsOfPaidCasesInAdvance ();
	Render(paymentsOfPaidCasesInAdvance, out, request, response, Myglobals, objectState, pageName1);

%>
<%@ include file="../Main/footer.jsp"%>
<script>

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

function calculateSumOfSelectedCases(){
	var totalNetAmtIqd = 0, totalNetAmtUsd=0;
	$('[id^=pmtcheck_]').each(function() {
		if ($(this).prop("checked") == true){
		    var caseid = this.id.split('_').pop();
		    totalNetAmtIqd += parseInt($("#td_netamt_iqd_"+caseid).attr("data-netval"));
		}
	});
	
	showTotalSelectedAmtForCustomers(totalNetAmtIqd.toLocaleString()+' د.ع ');
	
	$("#amount_topay_iqd").val(totalNetAmtIqd);
	$("#totalamountshouldbepaid_iqd").html(totalNetAmtIqd.toLocaleString());
}
function checkBoxPmtClicked(){
	calculateSumOfSelectedCases();
}
$('#cases-paid-delivery-balance-settle-form').submit(function(e){
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
});

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
			        data: { 'p_newValue': newShipmentCost, 'p_caseId':caseId , 'p_screenName':'حسابات مدفوعة التوصيل', 'p_columnToChange' :'shipment_cost'},
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