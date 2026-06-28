<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.returnables.CustomerReturnBacked" %> 
<%@ page import="com.app.util.Utilities,java.sql.PreparedStatement,java.sql.ResultSet,com.app.returnables.CustomerReturn" %>
<%
Connection conn1 = null;
PreparedStatement pst = null;
Utilities ut = new Utilities();

String custAccountReturnProcess = (String)request.getParameter("custAccountReturnProcess");
if (custAccountReturnProcess !=null){
	Myglobals.smartyGlobalsAssArr.put("custAccountReturnProcess", (String)custAccountReturnProcess);
	
}else if (Myglobals.smartyGlobalsAssArr.containsKey("custAccountReturnProcess") && Myglobals.smartyGlobalsAssArr.get("custAccountReturnProcess")!=null){
	custAccountReturnProcess = (String)Myglobals.smartyGlobalsAssArr.get("custAccountReturnProcess");
}
LinkedHashMap<String,String> customersList = new LinkedHashMap<String,String>();
try{
	conn1 = mysql.getConn();
	customersList = ut.getListOfMasterCustomers (conn1,(int)Myglobals.smartyGlobalsAssArr.get("userstorecode"));

}catch(Exception e){
	e.printStackTrace();
}finally{
	try{pst.close();}catch(Exception e){}
	try{conn1.close();}catch(Exception e){}
}
CustomerReturn agl = new CustomerReturn(); 
%>
<!-- page content -->
<div class="row">
	<div class="col-12">
    	          
			 <form action="?mycustsearch=1" method="post" name="search_cust_form" class="form-horizontal form-label-left" >
				<div class='row col-12'>
					<div class="col-6 ">
                 
                  <select class='select2 form-control' id='custAcc'  name ='custAccountReturnProcess' >
                  	<option value='' ></option>
                    <%
                    
                    	for (String custid : customersList.keySet()){
                    		if (custAccountReturnProcess!=null && custAccountReturnProcess.equalsIgnoreCase(custid)){%>
                    		
                    			<option value='<%=custid%>' selected><%=customersList.get(custid) %></option>
                    		<%}else{%>
                    			<option value='<%=custid%>' ><%=customersList.get(custid) %></option>
                    		<% 
                    		}
                    	}
                    %>
                    	</select>
	                </div>
	                <div class='col-6'>
                      <button type='submit' class="btn btn-dark btn-md" type="button">عرض تفاصيل الرواجع للمتجر<i class="fa fa-search m-right-xs"></i>
                      </button>
	                </div>
                </div>
			</form>
 </div>
 <div class='col-12'>
	<hr>
	</div>
	<div class='col-12'>
      <%if (custAccountReturnProcess!=null && !custAccountReturnProcess.trim().equalsIgnoreCase("")){%>

             <%
             String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
            
         	Render(agl  , out , request, response , Myglobals , objectState , pageName1);
         	

         	if (agl.getRecords() == 0){
         		
         		out.println("<h4>لا توجد رواجع حاليا</h4>");
         	}
         	
         	CustomerReturnBacked crb = new CustomerReturnBacked(); 
          	Render(crb  , out , request, response , Myglobals , objectState , pageName1); 
             %>
                           
       
    
        <!-- /page content -->

<%} %>
</div>
</div>
<%@ include file="../Main/footer.jsp"%>
<script>

function checkAllCust(that, custId){
	var check = false;
	if ($(that).prop("checked"))
		check = true;
	$("input[data-single-check-custid-"+custId+"]").each(function() {
		$(this).attr("checked", check);
	});
}

function changeToPrepairAll(){
	var check = false;
	if ($("#allprepair").prop("checked"))
		check = true;
	$('[id^=pmtcheck_]').each(function() {
		$(this).attr("checked", check);
	});

}

$('#customer-prepare-return-form').submit(function(e){
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


$('#barcode_checker').focus();
var input = document.getElementById("barcode_checker");
var old_receipt = '';
var scannedCounter = 1;
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
		            scrollTop: $("#"+barcodeScanned).offset().top - 100
		        }, 500);
			  if ($("#pmtcheck_"+caseid).attr("checked")=='checked')
				  generalErrorPrettyMsg("هذا الوصل تم جرده سابقا");
			  else{
			  	$("#pmtcheck_"+caseid).attr("checked", true);
			  	$(old_receipt).parent().css({ 
		            "background-color": "rgba(249,210,179,0.37)", 
		            "border": "2px solid #dc2b2b",});
			  	old_receipt = reciept; 
			  	scannedCounter++;
			  }
		  }else{
			  // check where is this receipt
			   getNotFoundReceipt(barcodeScanned, <%=custAccountReturnProcess%>);
			  //alert(barcodeScanned);
		  }
	  }	 
	  input.value = '';
	  $('#barcode_checker').focus();
	  //<tr smartykeycolval="18942" class="pointer"><td align="right" width="3%" class="cell">1</td><td style="" caseid="18942" id="101">101</td><td align="right" dir="ltr">1</td><td align="right" dir="ltr">18,942</td><td align="right" dir="ltr">10/12/2021</td><td align="right" dir="ltr"></td><td style="">07826596469</td><td align="right" dir="ltr">بغداد - </td><td>راجع</td><td align="right" dir="ltr"></td><td><input type="checkbox" id="pmtcheck_18942" data-single-check-custid-1="1" onclick="checkBoxPmtClicked(this, 18942)"></td></tr>
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

function getNotFoundReceipt(receiptNo, masterCustId ){
$.get('../../SearchReceiptInfoSRVL?receiptNo='+receiptNo+'&masterCustId='+masterCustId ,function(data, status){
	if (status=='success'){
		if (data && data.length>0){
			if (data.length==1){
				if (data[0].custRtnId>0){
					Swal.fire({
						  icon: 'error',
						  title: 'خطأ',
						  text: 'الوصل رقم :'+receiptNo,
						  footer: 'هذا الوصل تم أرجاعه للمتجر مسبقا, رقم كشف الراجع :'+data[0].custRtnId
						});
				}else if (data[0].pickUpAgentRtnId>0){
					Swal.fire({
						  icon: 'error',
						  title: 'خطأ',
						  text: 'الوصل رقم :'+receiptNo,
						  footer: 'هذا الوصل تم أرجاعه لمندوب الإستلام مسبقا, رقم كشف الراجع :'+data[0].pickUpAgentRtnId
						});
				}else{// add to the table
					if (data[0].allowRtnCustomer == 'Y'){
					
						var cell0 = "<td align='right' width='3%' class='cell'></td>";
						var cell1 = "<td caseid="+data[0].caseId+" id="+receiptNo+">"+receiptNo+"</td>";
						var cell2 = "<td align='right' dir='ltr'>"+data[0].rtnQty+"</td>";
						var cell3 = "<td align='right' dir='ltr'>"+data[0].caseId+"</td>";	
						var cell4 = "<td align='right' dir='ltr'>"+data[0].ccreatedDt+"</td>";
						var cell5 = "<td align='right' dir='ltr'>"+data[0].receiverName+"</td>";
						var cell6 = "<td>"+data[0].receiverHp1+"</td>";
						var cell7 = "<td align='right' dir='ltr'>"+data[0].address+"</td>";
						var cell8 = "<td>"+data[0].status+"</td>";
						var cell9 = "<td align='right' dir='ltr'>"+data[0].rmk+"</td>";
						var cell10 = "<td><input type='checkbox' id='pmtcheck_"+data[0].caseId+"' data-single-check-custid-"+masterCustId+"="+masterCustId+" onclick='checkBoxPmtClicked(this, "+data[0].caseId+")' checked='checked'></td>";
						var row = "<tr smartykeycolval="+data[0].caseId+" class='pointer' style='background-color: rgba(194, 214, 245, 0.7); border: 2px solid rgb(43, 73, 220);''>";
						row += cell0 + cell1 + cell2 + cell3 + cell4 + cell5 + cell6 + cell7 + cell8 + cell9 + cell10 ;
						row += "</tr>";
						
						 $('#smarty_table_com_dot_app_dot_returnables_dot_CustomerReturn >tbody tr:first' ).before(row);
					}else{
						Swal.fire({
							  icon: 'error',
							  title: 'خطأ',
							  text: 'الوصل رقم :'+receiptNo,
							  footer: 'هذا الوصل في مرحلة :'+data[0].stageName+' - '+data[0].stepName+", في فرع :"+data[0].currentBranchName
							});
					}
				}
			}else{
				
				towReceiptsWithSameNumberAlert("هنالك اكثر من وصل بنفس الرقم: "+receiptNo);
			}
		}else{
			generalErrorPrettyMsg('هذا الوصل غير متوفر في النظام او لا يتبع لهذا المتجر');
		}
		
	}else{
		alert('error');
	}
});
}
</script>



<script>
function integrateRtnBtn(acrId){
	$("#loading").css("display", "flex");
	
	$.confirm({ 
	    title: 'مزامنة الراجع مع نظام العميل',
	    content: 'سيتم مزامنة الراجع مع نظام العميل المرسل لهذة الشحنات. اختر نعم للتاكيد',
	    buttons: {
	        confirm:{
	        	text :'نعم',
	        	action : function () {
	        		confirmedToSyncToCustomerSystem(acrId);
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
function confirmedToSyncToCustomerSystem(acrId){
	var dataToSend = {"acrId":acrId , "type":"maincust"};
	$.post('../../SyncRtnCasesWithClientSystemSRVL' , dataToSend, function(data, status){ 
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


/* Close Manifest */
function closeManifestRtnBtn(acrId){
	$("#loading").css("display", "flex");
	$.confirm({ 
	    title: 'إغلاق منفسيت الراجع',
	    content: 'سيتم أغلاق منفيست الراجع ويتعبر المنفيست تم تسليمه بشكل نهائي للعميل',
	    buttons: {
	        confirm:{
	        	text :'نعم',
	        	action : function () {
	        		confirmedCloseRtnManifest(acrId);
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
function confirmedCloseRtnManifest(acrId){
	var dataToSend = {"manifestRtnId":acrId , "type":"maincust"};
	$.post('../../CloseRtnManifestSRVL' , dataToSend, function(data, status){ 
		if (status=='success'){
			$.alert({
			    title: '',
			    content: 'تم إغلاق منفيست الراجع',
			});
			$("#td-close-rtn-"+acrId).html("<span class=\"badge rounded-pill bg-secondary\">مغلق</span>");
			hideLoader();
		}else{
			alert("Error, please contact Mohammed Nafie");
			hideLoader();
		}
 	});
}


$("#heading-elements-com_app_returnables_CustomerReturn").css("display","none");
</script>
        