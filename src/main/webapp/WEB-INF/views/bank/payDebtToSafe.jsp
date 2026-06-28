<%@page import="com.app.util.UtilitiesFeqar"%>
<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.bank.PayDebtToSafe, java.sql.PreparedStatement, java.sql.ResultSet,  java.text.DecimalFormat" %> 
<%
Connection conn1 = null; 
PreparedStatement pst = null;
ResultSet rs = null; 
UtilitiesFeqar utf = new UtilitiesFeqar();
double allDebt = 0.0;
boolean active = false;
DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
int usid = (int)Myglobals.smartyGlobalsAssArr.get("userid");
int branchid = (int)Myglobals.smartyGlobalsAssArr.get("userstorecode");
try{
	conn1 = mysql.getConn();
	allDebt = utf.getAllDebtFromSafe(conn1, branchid); 
}catch(Exception e){
	e.printStackTrace();
}finally{ 
	try{rs.close();}catch(Exception e){}
	try{pst.close();}catch(Exception e){}
	try{conn1.close();}catch(Exception e){}
}
%> 

<div class="x_content">

<%if(allDebt<0){ %>
<div class="row">
	<h2 style="text-align:center;color:#ffc107;text-shadow:1px 1px #755a5a;">مبلغ الديون اقل من الحد الادنى يرجى الاتصال بفريق الدعم فوراً</h2>
</div>
<%} %>
	<div class="row">
		<div class="col-md-9 col-sm-12 col-xs-12">
			<%
			String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
					PayDebtToSafe pdtf = new PayDebtToSafe();
				 			Render(pdtf , out , request, response , Myglobals , objectState , pageName1);
			%>
	 	</div>
	  	<div class="col-md-3 col-sm-12 col-xs-12 profile_left" style=" margin-top:83px;padding-right:15px;padding-left:0px;">
			<div class="row top_tiles">
		    	<div class="animated flipInY col-lg-12 col-md-12 col-sm-12 col-xs-12">
	                <div class="col">
						<div class="card radius-10 bg-warning">
							<div class="card-body" style=' padding-bottom: 1.2rem;'>
								<div class="d-flex align-items-center">
									<div>
										<p class="mb-0 text-dark">كل الديون</p>
										<h4 class="my-1 text-dark"><%=numFormat.format(allDebt)%></h4>
	<!-- 									<p class="mb-0 font-13 text-white"><i class="bx bxs-down-arrow align-middle"></i>$34 from last week</p>
	 -->								</div>
									<div class="widgets-icons bg-white text-dark ms-auto"><i class="bx bx-dollar"></i>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
function payDebt(restAmount,tranEntity){
	 var form = `
			<div class="swal2-html-container" style="display: block;">مبلغ التسديد (يرجى ادخال الاصفار)</div>
	      	<input class="swal2-input" step="1" style="min-width: 50%;
										    max-width: 100%;
									        padding: 0.375em 0.625em;
									        background: inherit;
									        color: inherit;
									        font-size: 1.125em;
											margin:1em 0em 1em;" 
				id="payamt" name="payamt" placeholder="أَكتب ملاحظة رجاءً" type="number" style="display: flex;" required>
			<div class="swal2-html-container" style="display: block;">الملاحظات</div>
			<textarea class="swal2-input" style="min-width: 50%;
										    max-width: 100%;
									        padding: 0.375em 0.625em;
									        background: inherit;
									        color: inherit;
									        font-size: 1.125em;
											margin:1em 0em 1em;" 
				id="saf_rmk" name="saf_rmk" placeholder="أَكتب ملاحظة رجاءً" style="display: flex;" step="1" min="0">
		          `;
 	Swal.fire({
		//title: 'مبلغ التسديد (يرجى ادخال الاصفار)',
		html: form,
	    showCancelButton: true,
	    //confirmButtonColor: "#1FAB45",
	    confirmButtonText: "تسجيل الدفعة",
	    cancelButtonText: "الغاء",
	    buttonsStyling: true
	}).then(function () {
		console.log($("#payamt").val()+" ---------- "+$('#saf_rmk').val());
		if($("#payamt").val()>0 && $("#payamt").val() <= restAmount){
		    $.ajax({
		        type: "POST",
		        url: "../../PayDeptToSafeSRVL",
		        data: { 'payamt': $("#payamt").val(), 'tranEntity':tranEntity, 'rmk':$('#saf_rmk').val() },
		        cache: false,
		        success: function(data) {
		        	console.log(data);
		        	Swal.fire(
		            "تمت العملية بنجاح!",
		            "تم الحفظ!",
		            "تمت العملية بنجاح"
		        	).then((result) => {
		        		  location.reload();
		        		});
		            
		        },
		        error: function () {
		        	Swal.fire(
		            "Internal Error",
		            "Oops, your note was not saved.",
		            "error"
		            )
		        }
		    });
		}else{
		    Swal.fire({
			      title: 'يجب ان يكون المبلغ المسدد اقل او يساوي مبلغ الدين',
			   	  confirmButtonText: 'تفهمت الامر'
			    });
		}
	}, 
	{
	}) 
}
//$(function(){new AutoNumeric('#payamt',{unformatOnSubmit: AutoNumeric.options.unformatOnSubmit.unformat,allowDecimalPadding:false});});

</script>

<%@ include file="../Main/footer.jsp"%>  
