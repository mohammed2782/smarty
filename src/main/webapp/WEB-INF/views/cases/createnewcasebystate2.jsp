<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.cases.NewCasesByState2" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet" %>
<%

 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	NewCasesByState2 ncbs = new NewCasesByState2(); 
 	Render(ncbs  , out , request, response , Myglobals , objectState , pageName1); 

%> 
<%@ include file="../Main/footer.jsp"%>
<script>
var RCVno = 1;
var rowNum = 1;
var RCVtable = document.getElementById("rcv_dtls");
$("#custid_smartyNewRow_"+RCVno).focus(); 
$("#newcustomer_smartyNewRow_"+RCVno).css('display','none'); 
$('#mastercustid_smartyNewRow_'+RCVno).parent().hide();
$("#mastercustnameshowonly_smartyNewRow_"+RCVno).prop("disabled", true);
$(document).ready(function() {
	$('#add_rcv_dtls').click(function(){
		RCVno++;
		rowNum++;
		dataToSend = {};
		var state = $("#rcv_city").val();
		if(state === ''){
			state= "BGD";
		}
		//var row  =RCVtable.insertRow(rowNum);
		$.get('../../MultiRowsByStateSRVLT2?state='+state+'&loadRcvRow='+RCVno , dataToSend,function(data, status){ 
			if (status=='success'){
				
				$('#rcv_dtls > tbody:last-child').append(data);
				
				$(".select2").select2({
				    // the following code is used to disable x-scrollbar when click in select input and
				    // take 100% width in responsive also
				    dropdownAutoWidth: true,
				    width: '100%'
				  });
				$("#custid_smartyNewRow_"+RCVno).focus();
				$("#newcustomer_smartyNewRow_"+RCVno).css('display','none'); 
				$('#mastercustid_smartyNewRow_'+RCVno).parent().hide();
				$("#mastercustnameshowonly_smartyNewRow_"+RCVno).prop("disabled", true);
	
				init_InputMask();
				loadDistrict(rowNum);
				loadAgent(rowNum);
			}
			
		});
	});
});


function remove_row( rownum){
	$('table#rcv_dtls tr#smartyNewRow_'+rownum).remove();
}
function calcShipmentCost(seq){
	// not used now
}
function districtChanged(seq){
	// not used now
}

//when state changes 
$(document).ready(function() {
	$('#rcv_city').on('change',function (){
		for (i=1; i<=rowNum; i++){
			loadDistrict(i);
			loadAgent(i);
		}
	});
});


function loadDistrict(seq){
	var destCity= $("#rcv_city").val();
	var keyCol = "rcv_district_smartyNewRow_"+seq;
	var req = "Y";
	var lookupSql = "select cdi_id, cdi_name from "
	+ "(select '' as cdi_id, '' as cdi_name from dual " 
	+"	union select cdi_id, cdi_name from kbcity_district where  cdi_stcode = '"+destCity+"' and cdi_name='بلا عنوان' " 
	+ " union select cdi_id, cdi_name from kbcity_district where  cdi_stcode = '"+destCity+"'  and cdi_name !='بلا عنوان' )ttt ";
	
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("rcv_district_smartyNewRow_"+seq);
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'POST',
		cache: false,
		url:'../../myajax.jsp',  
		data: dataToSend,
		error:function(){ alert("some error occurred") },
		success: function(data, status){
				if (status=='success'){ 
					targetHTMLElement.innerHTML=data;
				}
		}
	});
}

function loadAgent(seq){
	var branchCode = <%=user.getBranchCode()%>
	var destCity= $("#rcv_city").val();
	var keyCol = "c_assignedagent_smartyNewRow_"+seq;
	var req = "N";
	var lookupSql = "select us_id , us_name from kbusers where us_rank='DLVAGENT' "
		+ " and us_to_state like '%"+destCity+"%' and us_branchcode='"+branchCode+"'  order by us_name ";
	
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("c_assignedagent_smartyNewRow_"+seq);
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'POST',
		cache: false,
		url:'../../myajax.jsp',  
		data: dataToSend,
		error:function(){ alert("some error occurred") },
		success: function(data, status){
				if (status=='success'){ 
					targetHTMLElement.innerHTML=data;
				}
		}
	});
}
function hideshowbasedoncheck(seq){	
	$("#newcustomerflag_smartyNewRow_"+seq).on("change", function(){
		console.log($(this).prop("checked"));
		
			if($(this).prop("checked") == true){
				
				$('#newcustomerflag_smartyNewRow_'+seq).val('Y');
				
				$('#custid_smartyNewRow_'+seq).parent().hide();
				$('#mastercustnameshowonly_smartyNewRow_'+seq).parent().hide();
				$("#newcustomer_smartyNewRow_"+seq).css('display','block');
				$('#mastercustid_smartyNewRow_'+seq).parent().show();
				
				$('#custid_smartyNewRow_'+seq).prop('required',false);
				$('#mastercustnameshowonly_smartyNewRow_'+seq).prop('required',false);
				$("#newcustomer_smartyNewRow_"+seq).prop('required',true);		
				$("#mastercustid_smartyNewRow_"+seq).prop('required',true);		

				$('#custid_smartyNewRow_'+seq).empty();
				$('#mastercustnameshowonly_smartyNewRow_'+seq).empty();
				$('#custhp_smartyNewRow_'+seq).val('');
				
				$("#newcustomer_smartyNewRow_"+RCVno).focus(); 
				
				loadMaster(seq);
			}else{
				
				$('#newcustomerflag_smartyNewRow_'+seq).val('N');

				$('#custid_smartyNewRow_'+seq).parent().show();
				$('#mastercustnameshowonly_smartyNewRow_'+seq).parent().show();
				$("#newcustomer_smartyNewRow_"+seq).css('display','none');
				$('#mastercustid_smartyNewRow_'+seq).parent().hide();
		
				$('#custid_smartyNewRow_'+seq).prop('required',true);
				$('#mastercustnameshowonly_smartyNewRow_'+seq).prop('required',true);
				$("#newcustomer_smartyNewRow_"+seq).prop('required',false);
				$("#mastercustid_smartyNewRow_"+seq).prop('required',false);		

				$("#newcustomer_smartyNewRow_"+seq).val('');
				$('#custhp_smartyNewRow_'+seq).val('');
				$('#mastercustid_smartyNewRow_'+seq).empty();

				loadCustomer(seq);
			  }				
	});		
}

//when customer changes
function getCustomerHP(seq){
	var custId= $("#custid_smartyNewRow_"+seq).val();
	
	var dataToSend = {"custId":custId};
	
	$.post('../../GetCustomerHPSRVLT' , dataToSend, function(data, status){ 
			//alert("Data: " + data +", Status:" + status);
			if (status=='success'){ 
				$("#custhp_smartyNewRow_"+seq).val(data);
			}else{
				alert("Error, please contact us!");
			}
	 });
}

function loadCustomer(seq){ 
	var branchCode = <%=user.getBranchCode()%>
	var keyCol = "custid_smartyNewRow_"+seq;
	var req = "N";
	var lookupSql = "select cust_id , cust_name from kbcustomers where cust_branch = '" + branchCode + " ' ";
			
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("custid_smartyNewRow_"+seq);
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'POST',
		cache: false,
		url:'../../myajax.jsp',  
		data: dataToSend,
		error:function(){ alert("some error occurred") },
		success: function(data, status){
				if (status=='success'){ 
					targetHTMLElement.innerHTML=data;
				}
		}
	});
}

function loadMasterCustomer(seq){ 
	var custId= $("#custid_smartyNewRow_"+seq).val();
	var keyCol = "mastercustnameshowonly_smartyNewRow_"+seq;
	var req = "Y";
	var lookupSql = "select mcust_name, mcust_name from kb_mastercustomer "
		 		  + "where mcust_id in (select cust_mastercustid from kbcustomers where  cust_id = '"+custId+"') "
		 		  + " and mcust_branchcode='"+<%=user.getBranchCode()%>+"' ";
			
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("mastercustnameshowonly_smartyNewRow_"+seq);
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'POST',
		cache: false,
		url:'../../myajax.jsp',  
		data: dataToSend,
		error:function(){ alert("some error occurred") },
		success: function(data, status){
				if (status=='success'){ 
					targetHTMLElement.innerHTML=data;
				}
		}
	});
}

function loadMaster(seq){ 
	var branchCode = <%=user.getBranchCode()%>
	var keyCol = "mastercustid_smartyNewRow_"+seq;
	var req = "N";
	var lookupSql = "select mcust_id , mcust_name from kb_mastercustomer where mcust_branchcode = '" + branchCode + "' and mcust_active='Y' ";
			
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("mastercustid_smartyNewRow_"+seq);
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'POST',
		cache: false,
		url:'../../myajax.jsp',  
		data: dataToSend,
		error:function(){ alert("some error occurred") },
		success: function(data, status){
				if (status=='success'){ 
					targetHTMLElement.innerHTML=data;
				}
		}
	});
}

function disableF5(e) {
	  if ((e.which || e.keyCode) == 116){
		  e.preventDefault();
		  document.getElementById('save_new_form_com_dot_app_dot_cases_dot_NewCasesByState2').click();
	  }
	};

	function disableF1(e) {
		  if ((e.which || e.keyCode) == 112 ){
			  e.preventDefault();
			  document.getElementById('add_rcv_dtls').click();
		  }
	};

	$(document).ready(function(){
		//$(document).on("keydown", disableF5);
		//$(document).on("keydown", disableF1);
	});
</script>
