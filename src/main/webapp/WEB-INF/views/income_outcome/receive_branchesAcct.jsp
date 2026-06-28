 <%@ include file="../Main/Main.jsp"%>
 <%@ page autoFlush="true" buffer="2048kb"%>
<%@ page import="com.app.incomeoutcome.ReceivedBranchPayments" %> 
<%@ page import="com.app.util.UtilitiesNafie, com.app.util.Utilities, 
java.sql.PreparedStatement,java.sql.ResultSet, com.app.beans.BranchPaymentBean,
com.app.incomeoutcome.LiaisonAgentSharePaymentsForOutBoundCases,
 com.app.financials.*" %>
<%
Connection conn1 = null;
PreparedStatement pst = null;
ResultSet rs = null; 
UtilitiesNafie ut = new UtilitiesNafie(); 
String branchToReceiveFrom = (String) request.getParameter("branchToReceiveFrom");
ArrayList<BranchPaymentBean> branchPaymentList = new ArrayList<BranchPaymentBean>();
if (branchToReceiveFrom != null) {
	Myglobals.smartyGlobalsAssArr.put("BRANCH_TO_RECEIVE_FROM_G", (String) branchToReceiveFrom);

} else if (Myglobals.smartyGlobalsAssArr.containsKey("BRANCH_TO_RECEIVE_FROM_G")
		&& Myglobals.smartyGlobalsAssArr.get("BRANCH_TO_RECEIVE_FROM_G") != null) {
	branchToReceiveFrom = (String) Myglobals.smartyGlobalsAssArr.get("BRANCH_TO_RECEIVE_FROM_G");
}
int userBranch = (int)Myglobals.smartyGlobalsAssArr.get("userstorecode");
LinkedHashMap<String,String> branchesList = new LinkedHashMap<String,String>();
LinkedHashMap<String,String> branchesInfo = new LinkedHashMap<String,String>();
try{
	conn1 = mysql.getConn();
	branchesList = Utilities.getListOfBranches(conn1, userBranch);
	branchPaymentList = UtilitiesNafie.getBranchPaymentsNotReceivedYet(conn1, userBranch);
	if (branchToReceiveFrom!=null){
		branchesInfo= Utilities.getBranchesInfo(conn1, branchToReceiveFrom); 
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
	try{rs.close();}catch(Exception e){}
	try{pst.close();}catch(Exception e){}
	try{conn1.close();}catch(Exception e){}
}
int i = 0;
String cardClass="card mb-5 mb-lg-0 bg-light";
String buttonClass="btn btn-danger round btn-min-width mr-1 mb-1 waves-effect waves-light";
%>
<div class="pricing-table">
<div class="row ">
<% for (BranchPaymentBean branchPaymentBean : branchPaymentList){ %>
	<div class="col-3" style='margin-top:10px'>
	<% if (i%4==0){
		cardClass="card mb-5 mb-lg-0 bg-gradient-x-info";
		buttonClass="btn btn-info round btn-min-width mr-1 mb-1 waves-effect waves-light";
	}else if (i%4==1){
		cardClass="card mb-5 mb-lg-0 bg-gradient-x-danger";
		buttonClass="btn btn-danger round btn-min-width mr-1 mb-1 waves-effect waves-light";
	}else if (i%4==2){
		cardClass="card mb-5 mb-lg-0 bg-gradient-x-success";
		buttonClass="btn btn-success round btn-min-width mr-1 mb-1 waves-effect waves-light";
	}else if (i%4==3){
		cardClass="card mb-5 mb-lg-0 bg-gradient-x-warning";
		buttonClass="btn btn-warning round btn-min-width mr-1 mb-1 waves-effect waves-light";
	}
	%>
	<div class="<%=cardClass%>">
			<div class="card-body">
				<h5 class="card-title text-white text-uppercase text-center"><%=branchPaymentBean.getPayerBranchName()%>
				<a href="../../branchPaymentsReceiptSRVL?trans_id=<%=branchPaymentBean.getPaymentId()%>&userbranch=<%=userBranch %>"
				style='color:white' ><i class="fa fa-print fa-lg"></i>(<%=branchPaymentBean.getPaymentId() %>)</a></h5>
				<h6 class="card-price text-white text-center" style="font-size: 1.5rem;font-weight: bold;"><%=ut.numFormat.format(branchPaymentBean.getPaidAmountIqd())%><span class="term" style='font-size:0.7rem'>دينار عراقي /المبلغ المسدد</span></h6>
				<h6 class="card-price text-white text-center" style="font-size: 1.5rem;font-weight: bold;"><%=ut.numFormat.format(branchPaymentBean.getPaidAmountUsd())%><span class="term" style='font-size:0.7rem'> دولار أمريكي /المبلغ المسدد</span></h6>
				
				<ul class="list-group list-group-flush">
					<li class="list-group-item bg-transparent" style='padding-bottom: 0px;padding-top: 5px;'>
						<div class='mb-3'><i class='fa fa-money text-white' style='font-size: xx-large;'></i>
						<h1 class="pricing-card-title text-white"><%=branchPaymentBean.getReceiptsAmountIqd()%>
						<small class="text-muted" style='color:rgb(255 255 255 / 72%) !important'>دينار / مبلغ الوصولات</small></h1>
						</div>
					</li>
					<li class="list-group-item bg-transparent" style='padding-bottom: 0px;padding-top: 5px;'>
						<div class='mb-3'><i class='fa fa-dollar text-white' style='font-size: xx-large;'></i>
						<h1 class="pricing-card-title text-white"><%=branchPaymentBean.getReceiptsAmountUsd()%>
						<small class="text-muted" style='color:rgb(255 255 255 / 72%) !important'>دولار / مبلغ الوصولات</small></h1>
						</div>
					</li>
					<li class="list-group-item bg-transparent" style='padding-bottom: 0px;padding-top: 5px;'><div class='mb-3'><i class='bx bx-check me-2'></i>
					<span class='price h5 text-white'><%=branchPaymentBean.getPaymentDate()%></span><span class="term text-white" style='font-size: .775rem;'> / تاريخ التسديد</span>
					</div></li>
					<li class="list-group-item bg-transparent" id='pmt-div-<%=branchPaymentBean.getPaymentId()%>' style='display:none;    border-bottom: none;'>
					
					<div class="col-12">
						<div class="row">
							<div class="col-7" style="margin-bottom:5px;color: white;" >
								<label class="form-label" style="font-size: .875rem;color: white;">المستلم فعليا - دينار <i class="fa fa-money"></i></label>
								<div class="input-group">
					 				<input type="text" value="" class="form-control" 
					 				style="text-align:right; background-color:white " 
					 				name="c_receiptamt_iqd_smartyNewRow_<%=branchPaymentBean.getPaymentId()%>" 
					 				id="c_receiptamt_iqd_smartyNewRow_<%=branchPaymentBean.getPaymentId()%>" required="" onkeyup="formatMe(this);">
					 			</div>
					 			 <script>$(function() {
					 				 new AutoNumeric("#c_receiptamt_iqd_smartyNewRow_<%=branchPaymentBean.getPaymentId()%>", { 
		        						unformatOnSubmit: AutoNumeric.options.unformatOnSubmit.unformat,
		        						allowDecimalPadding: false
		        						}); 
		        				});</script>
					 		</div>
							<div class="col-5" style="margin-bottom:5px;color: white;backgorund-color:red" >
								<label class="form-label" style="font-size: .875rem;color: white;">المستلم فعليا - دولار <i class="fa fa-dollar"></i></label>
								<div class="input-group">
					 				<input type="number" value="" class="form-control" 
					 				style="text-align:right; background-color:white " 
					 				name="c_receiptamt_usd_smartyNewRow_<%=branchPaymentBean.getPaymentId()%>" 
					 				id="c_receiptamt_usd_smartyNewRow_<%=branchPaymentBean.getPaymentId()%>" required="" onkeyup="formatMe(this);">
					 			</div>
					 		</div>
				 		</div>
			 		</div>
					<div class="col-12" style="margin-bottom:5px;color: white;">
						<label class="form-label" style="font-size: .875rem; color: white;">ملاحظات</label>
						<div class="input-group">
							<textarea class="form-control"  style='background-color:white'
							name="rcv_rmk_smartyNewRow_<%=branchPaymentBean.getPaymentId()%>" 
							id="rcv_rmk_smartyNewRow_<%=branchPaymentBean.getPaymentId()%>"></textarea>
						</div>
					</div>
				</li>
			</ul>
				<div class="d-grid"> 
				<a href="#" onclick="showHidePmtForm(<%=branchPaymentBean.getPaymentId()%>)" 
				id="showhideform-btn-pmtid-<%=branchPaymentBean.getPaymentId()%>" class="<%=buttonClass%>">أستلام</a>
				</div>
				<div class="form-group" style='display:none;' id='submit-cancel-buttons-<%=branchPaymentBean.getPaymentId()%>'> 
					<div class="row row-cols-auto g-3">
						<div class="col-5">
							<button type="button"  onclick="submitReceivedPmt(<%=branchPaymentBean.getPaymentId()%>)" value="save" 
							class="btn btn-primary">حفظ<i class="fa fa-thumbs-up" style="margin-right: 7px;"></i></button>
						</div>
					<div class="col-5">
						<button type="button"  onclick="showHidePmtForm(<%=branchPaymentBean.getPaymentId()%>)" 
						class="btn btn-warning btn-sm" onclick="" value="cancel">إلغاء
						<i class="icon-ban" style="margin-right: 7px;"></i></button>
						
					</div>
					</div>
				</div>
			</div>
		</div>
	</div>
<% i++;} %>
</div>
</div>
<hr>
<div class="row">

<form action="?mypickupagentsearch=1" method="post" name="search_cust_form" class="form-horizontal form-label-left" >
	<div class='row col-12'>
			<div class="col-6 ">
             <select class='select2' id='branchToReceiveFrom' style="width: 200px;" name ='branchToReceiveFrom' >
             	<option value='' ></option>
               <%
               for (String branchId : branchesList.keySet()){
               	if (branchToReceiveFrom!=null && branchToReceiveFrom.equalsIgnoreCase(branchId)){
               %>
             		<option value='<%=branchId%>' selected><%=branchesList.get(branchId)%></option>
             		<%
             		}else{
             		%>
             			<option value='<%=branchId%>' ><%=branchesList.get(branchId)%></option>
             		<%
             		}
             		                    		                    		                    		                    		                    	}
             		%>
               </select>
                   </div>
	        <div class='col-6'>
            	<button type='submit' class=" btn btn-light px-5" style='margin-right:10px' type="button">عرض الدفوعات المرسلة من الفرع<i class="fa fa-search m-right-xs"></i></button>
            </div>
           
     </div>
 </form>
 </div>
      <%
if (branchToReceiveFrom!=null && !branchToReceiveFrom.trim().equalsIgnoreCase("")){  
	
%>
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
						ReceivedBranchPayments rbp = new ReceivedBranchPayments(); 
						Render(rbp  , out , request, response , Myglobals , objectState , pageName1);
						%>
					</div>
					
				<div class="tab-pane" id="liaisonagent-transactions" role="tabpanel"
					aria-labelledby="profile" aria-expanded="false">
					<%
					LiaisonAgentSharePaymentsForOutBoundCases liaisonAgentSharePaymentsForOutBoundCases = 
					new LiaisonAgentSharePaymentsForOutBoundCases(); 
					liaisonAgentSharePaymentsForOutBoundCases.setEntityType(FinOperationEntity.LIAISON_AGENT);
					liaisonAgentSharePaymentsForOutBoundCases.setM_branchWeAreHandling(Integer.parseInt(branchToReceiveFrom));
					liaisonAgentSharePaymentsForOutBoundCases.setM_operationCode(FinOperationCode.LIAISON_SHARE_OUT_BOUND_CASES);
					Render(liaisonAgentSharePaymentsForOutBoundCases  , out , request, response , Myglobals , objectState , pageName1);
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
function showHidePmtForm(pmtId){
	 $("#pmt-div-"+pmtId).toggle("slide");
	 $("#showhideform-btn-pmtid-"+pmtId).toggle("slide");
	 $("#submit-cancel-buttons-"+pmtId).toggle("slide");
}

function submitReceivedPmt(pmtId){
	var  receivedAmtIqd = $("#c_receiptamt_iqd_smartyNewRow_"+pmtId).val();
	var  receivedAmtUsd = $("#c_receiptamt_usd_smartyNewRow_"+pmtId).val();
	var receivedRmk = $('#rcv_rmk_smartyNewRow_'+pmtId).val();
	var dataToSend = {"branchpmtid":pmtId,"receivedAmtIqd":receivedAmtIqd, "receivedAmtUsd":receivedAmtUsd,"receivedRmk":receivedRmk};
	$.post('../../ReceiveBranchPaymentSRVL' , dataToSend, function(data, status){ 
	
		if (status=='success'){
			Lobibox.notify('success', {
				pauseDelayOnHover: true,
				continueDelayOnInactiveTab: false,
				position: 'top center',
				icon: 'bx bx-check-circle',
				title:'',
				size: 'normal',
				msg: "تم تسجيل أستلام الدفعة",
			});
			 $("#submit-cancel-buttons-"+pmtId).toggle("slide");
			 $("#c_receiptamt_iqd_smartyNewRow_"+pmtId).prop( "disabled", true );
			 $("#c_receiptamt_usd_smartyNewRow_"+pmtId).prop( "disabled", true );
			 $("#rcv_rmk_smartyNewRow_"+pmtId).prop( "disabled", true );
			
		}else{
			Lobibox.notify('error', {
				pauseDelayOnHover: true,
				continueDelayOnInactiveTab: false,
				position: 'top center',
				icon: 'bx bx-x-circle',
				title:'خطأ',
				size: 'normal',
				msg: msg,

			});
		}
 	});
}

function changeActiveTab( tab){
	localStorage.setItem('activeTab_received_payments_from_branches', tab);
}

$(function (){
	var selectedTab = localStorage.getItem('activeTab_received_payments_from_branches');
	if(selectedTab){
       $('#'+selectedTab).addClass('active show');
       $('#a-'+selectedTab).addClass('active');
   }else{
   	 $('#receipts-balance').addClass('active show');
        $('#a-receipts-balance').addClass('active');
   }
});
</script>