<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.tickets.ShowAllTickets , com.app.tickets.TicketsUtilities,com.app.util.Utilities,
java.sql.Connection, com.app.tickets.TicketBean" %> 
<%
TicketsUtilities ticketsUtilities = new TicketsUtilities();
Connection conn1 = null;
LinkedList<TicketBean> newTicketsList = new LinkedList<TicketBean>();
LinkedList<TicketBean> closedTicketsList = new LinkedList<TicketBean>();
LinkedList<TicketBean> underProcessTicketsList = new LinkedList<TicketBean>();
HashMap<String, String> statesList = new HashMap<String, String>();
List<String> selectedTicketsStateslist = null;
String selectedTicketsStatesCommaSeperated = "";
String ticektsStatesSearchMode = "in_states";

if(request.getParameter("search_mode")!=null){
	ticektsStatesSearchMode = (String)request.getParameter("search_mode");
	request.getSession().setAttribute("ticketsStatesSearchMode_G",(String)ticektsStatesSearchMode);
}else{
	 if (request.getSession().getAttribute("ticketsStatesSearchMode_G") != null) {
		 ticektsStatesSearchMode = (String) Myglobals.smartyGlobalsAssArr.get("ticketsStatesSearchMode_G");
	}
}

if(request.getParameter("selectedTicketsStateslist")!=null){
	selectedTicketsStateslist = new ArrayList<>(Arrays.asList(request.getParameterValues("selectedTicketsStateslist")));
	Myglobals.smartyGlobalsAssArr.put("selectedUsersAccountslist_G", (List<String>) selectedTicketsStateslist);
}else{
	if (request.getParameter("TicketsStateSearch")!=null && request.getParameter("TicketsStateSearch").equalsIgnoreCase("1")){
		selectedTicketsStateslist = null;
	}else if (Myglobals.smartyGlobalsAssArr.containsKey("selectedUsersAccountslist_G")
			&& Myglobals.smartyGlobalsAssArr.get("selectedUsersAccountslist_G") != null) {
		selectedTicketsStateslist = (List<String>) Myglobals.smartyGlobalsAssArr.get("selectedUsersAccountslist_G");
	}
}
if (selectedTicketsStateslist !=null){
	selectedTicketsStatesCommaSeperated = "'"+String.join("','", selectedTicketsStateslist)+"'";
	request.getSession().setAttribute("selectedTicketsStatesCommaSeperated_G", selectedTicketsStatesCommaSeperated);
}else{
	selectedTicketsStatesCommaSeperated = null;
	request.getSession().removeAttribute("selectedTicketsStatesCommaSeperated_G");
}
try{
	conn1 = mysql.getConn();
	statesList = Utilities.getStatesList(conn1);
	closedTicketsList  = ticketsUtilities.getEmplpyeeTicketsClosedLessThanXDays(conn1, user.getBranchCode(), user.getUsid(), 3);
	underProcessTicketsList = ticketsUtilities.getEmployeeUnderProcessTickets(conn1, user.getBranchCode(), user.getUsid());
}catch(Exception e){
	e.printStackTrace();
}finally{
	try{conn1.close();}catch(Exception e){}
}
%>

<style>
.underprocess-list {
    position: relative;
	max-height: 700px;
	min-height: 700px;
}
.closed-list{
  	position: relative;
	max-height: 700px;
	min-height: 700px;
}

</style>
<%if (user.getBranchCode()==1){ %>
<form action="?TicketsStateSearch=1" method="post" name="search_tickets_state_form">
	<div class='row'>
		<div class = 'col-6'>
			<select class='select2' id='selectedTicketsStateslist' class='multiple-select' multiple='multiple' 
			style="width: 200px;" name='selectedTicketsStateslist'>
			<%for (String stateCode : statesList.keySet()) {
				if (selectedTicketsStateslist != null 
						&& selectedTicketsStateslist.contains(stateCode)) {%>
					<option value='<%=stateCode%>' selected><%=statesList.get(stateCode)%></option>
				<%} else {%>
					<option value='<%=stateCode%>'><%=statesList.get(stateCode)%></option>
				<%}%>
			<%}%>
			</select>
		</div>
		<div class = 'col-3'>
			<div class='row'>
				<div class = 'col-12'>
					<button type='submit' class="btn btn-primary btn-darken-4" style='margin-right: 10px; background: #623da5 !important;'
						name='search_mode'	value="in_states"> عرض التذاكر المنتمية للمحافظات المختارة<i class="fa fa-search m-right-xs"></i>
					</button>
				</div>
				<div class = 'col-12'>
					<button type='submit' class="btn btn-warning btn-darken-4" style='margin-right: 10px;'
						name='search_mode'	value="not_in_states"> التذاكر التي لا تنتمي للمحافظات المختارة<i class="fa fa-search m-right-xs"></i>
					</button>
				</div>
			</div>
		</div>
		<div class = 'col-3'>
			<div class='row'>
				<div class = 'col-12'>
					<button type='submit' class="btn btn-info btn-darken-4" style='margin-right: 10px;'
						name='search_mode'	value="no_cases_attached">عرض التذاكر بدون شحنات<i class="fa fa-search m-right-xs"></i>
					</button>
				</div>
			</div>
		</div>
</div>
</form>
<%} %>

<div class='row' style='max-height'>
	<div class="col-12 col-xl-10 d-flex" style='    padding-right: 1px;padding-left: 10px;'>
		<div class="card" style='width:100%'>
			<div class="card-body">
				<ul class="nav nav-tabs" role="tablist">
					<li class="nav-item" role="presentation">
						<a class="nav-link active" data-bs-toggle="tab" href="#primaryhome" role="tab" aria-selected="true">
							<div class="d-flex align-items-center">
								<div class="tab-icon"><i class='bx bx-home font-18 me-1'></i>
								</div>
								<div class="tab-title">قيد العمل</div>
							</div>
						</a>
					</li>
					<li class="nav-item" role="presentation">
						<a class="nav-link" data-bs-toggle="tab" href="#primaryprofile" role="tab" aria-selected="false">
							<div class="d-flex align-items-center">
								<div class="tab-icon"><i class='bx bx-user-pin font-18 me-1'></i>
								</div>
								<div class="tab-title">مغلقة في أخر 3 أيام</div>
							</div>
						</a>
					</li>
					
				</ul>
				<div class="tab-content py-0" style='padding-bottom: 0px!important;'>
					<div class="tab-pane fade show active" id="primaryhome" role="tabpanel">
						<!-- under process List -->
						<div class='row' style=''>
							<div class="col-12 col-xl-3 d-flex">
								<div class="card radius-10 w-100">
									
									<div class="underprocess-list p-0 mb-3 "
									 id='underprocess-list' ondrop="drop(event)" 
									 ondragover="allowDrop(event)" style='overflow-y: auto;'>
									<% for(TicketBean underProcessTicket :  underProcessTicketsList) {
										out.println(ticketsUtilities.getSingleTicketHtml(underProcessTicket, true, false));
										} %>
									</div>
								</div>
							</div>
							
							<div class="col-12 col-xl-9 d-flex" style = 'padding-right: 0px;padding-left: 0px;'>
								<div class="card" style='width:100%'>
									<div class="card-body" id='under-process-chat-div' style = 'padding-right: 0px;padding-top: 0px;padding-bottom: 0px;padding-left: 0px;'>
										<!-- CHAT -->
										<iframe id='underprocess_iframe' src="" style="border:0px;width: -webkit-fill-available;height: 100%"></iframe>
									
									
									</div>
								</div>
							</div>
						</div>
						<!--End of under process List -->
					</div>
					<div class="tab-pane fade" id="primaryprofile" role="tabpanel">
						<!-- under process List -->
						<div class='row'>
							<div class="col-12 col-xl-4 d-flex">
								<div class="card radius-10 w-100">
									
									<div class="closed-list p-3 mb-3" style='overflow-y: auto;'>
									<% for(TicketBean closedTicket :  closedTicketsList) {
										out.println(ticketsUtilities.getSingleTicketHtml(closedTicket, true, false));
										} %>
									</div>
								</div>
							</div>
							<div class="col-12 col-xl-8 d-flex">
							</div>
						</div>
						<!--End of under process List -->
					</div>
					
				</div>
			</div>
		</div>
	</div>
	<div class="col-12 col-xl-2 d-flex" style='padding-right: 1px;padding-left: 1px;'>
		<div class="card radius-10 w-100">
			<div class="card-body" style='max-height:27px; '>
				<div class="d-flex align-items-center">
					<div><h5 class="mb-0">تذاكر جديدة</h5></div>
					
				</div>
			</div>
			<div class="new-tickets-list p-1 mb-1" style = 'min-height:80vh;'>
				<%-- <%
				for(TicketBean newTicket :  newTicketsList) {
					out.println(ticketsUtilities.getSingleTicketHtml(newTicket, false, true));
						
				} %> --%>
			</div>
		</div>
	</div>

</div>
				 
<%-- <%
	 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
		ShowAllTickets showAllTickets = new ShowAllTickets(); 
	 	Render(showAllTickets  , out , request, response , Myglobals , objectState , pageName1); 
	%> --%>
<%@ include file="../Main/footer.jsp"%>
<script>

jQuery(document).ready( 
	$(window).bind('beforeunload', function(e){
		sourceTicketNotification.close();
    	var confirmationMessage = "\o/";   
    	e.returnValue = confirmationMessage;
    	return confirmationMessage;   
	})
);

new PerfectScrollbar('#underprocess_iframe');
new PerfectScrollbar('.new-tickets-list');
/* 
if(typeof(EventSource)!=="undefined"){
	
	var source = new EventSource("../../CheckTicketsSRVL");
	source.onmessage = function(event) {
	  console.log(event.data);
	};
}else{
	console.log("no support");
}
 */
var currentChosenTicketId = 0;
function reloadIframeChat(ticketId){
	// update chat as seen
	$('#clickable-ahref-ticket-id-'+currentChosenTicketId).addClass('bg-light');
	currentChosenTicketId = ticketId;
	$('#clickable-ahref-ticket-id-'+currentChosenTicketId).removeClass('bg-light');
	var url    = 'chatbox?chatTicketId='+ticketId+'&displayChatBoxInline=true';
	$("#little-red-circle-"+ticketId).remove();
	document.getElementById('underprocess_iframe').src = url;
}



function allowDrop(ev) {
	  ev.preventDefault();
	}

function drag(ev) {
	console.log(ev);
  ev.dataTransfer.setData("text", ev.target.id);
}

function drop(ev) {
  ev.preventDefault();
  var data = ev.dataTransfer.getData("text");
  var ticketId = $("#"+data).attr("ticket-id-attr");
  assignTiecketToEmp (ticketId);
 
 
}

function assignTiecketToEmp(ticketId){
	 $.confirm({ 
		    title: 'بدء العمل على التذكر رقم '+ticketId,
		    content: 'أنتبه. لا يمكن التراجع عن هذه الخطوه . اذا كنت متأكد اختر نعم',
		    type: 'orange',
		    buttons: {
		        confirm:{
		        	text :'نعم',
		        	action : function () {
		        		  employeeStartWorkingOnTicket(<%=user.getUsid()%>, ticketId);
		        		  
		        	}
		        },
		        cancel:{
		        	text :'لا',
		        	action : function () {
		        		$('#ticket-id-'+ticketId).hide('slow', function(){ $('#ticket-id-'+ticketId).remove(); });
		        	}
		        }
		    }
		});
}

function doThisLikeDrag(ticketId){
	  assignTiecketToEmp (ticketId);
	
}

function employeeStartWorkingOnTicket (empId, ticketId){
	var userId = <%=user.getUsid()%>;
	var dataToSend = {ticketId : ticketId, userId : userId };
	$.post('../../assignTicketToEmpAndOpenItSRVL' , dataToSend, function(data, status){ 
		if (status=='success'){
			console.log(data);
			if (data === 'false'){
				$('#ticket-id-'+ticketId).hide('slow', function(){ $('#ticket-id-'+ticketId).remove(); });
				$.alert({
				    title: '',
				    content: 'لقد تم أسناد هذه التذكرة الى موظف أخر',
				});
				
			}else{
				$("#underprocess-list").prepend(document.getElementById("ticket-id-"+ticketId));
				$("#clickable-ahref-ticket-id-"+ticketId).attr('onclick', "reloadIframeChat("+ticketId+")");
				$('#open-ticket-btn-'+ticketId).remove();
				//<span id="little-red-circle-1113565" class="">!</span>
				$("#clickable-ahref-ticket-id-"+ticketId).append("<span id='little-red-circle-"+ticketId+"' class='badge badge-pill badge-danger badge-up badge-glow' style='top: -10px;left: -10px;padding-left: 10px;padding-right: 10px;' >!</span>");
			}
		}else{
			alert("Error, please contact Mohammed Nafie");
			
		}
 	});
}

$(document).ready(function() {
	$(".wrapper").hasClass("toggled") ? ($(".wrapper").removeClass("toggled"), $(".sidebar-wrapper").unbind("hover")) : 
		($(".wrapper").addClass("toggled"), $(".sidebar-wrapper").hover(function() {
		$(".wrapper").addClass("sidebar-hovered")
	}, function() {
		$(".wrapper").removeClass("sidebar-hovered")
	}));
});

function checkUnSeenMsgsForALlOpenedTickets(){
	/* console.log("here");
	var ticketId = 0;
	var first = true;
	var foundAnyTicketsOpenAssignedToEmp = false;
	var allTicketsNeedsToBeChecked = "";
	/* $('div[id^="ticket-id"]').each(function() {
	    ticketId = this.id.split('-').pop();
	    if (!first)
	    	allTicketsNeedsToBeChecked +=","
	    allTicketsNeedsToBeChecked += ticketId;
	    foundAnyTicketsOpenAssignedToEmp = true;
	});
	if (foundAnyTicketsOpenAssignedToEmp){
		// call server to check for un seen chats
	} */
	 
	// check if any ticket have unseen chat
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'GET',
		dataType:'json',
		cache: false,
		url:'../../TicketsCheckUnSeenChatsSRVL',  
		data: dataToSend,
		error:function(xhr, textStatus, message){ 
			console.log(xhr);
		},
		success: function(data, status){
			if (status=='success'){
				// highlight tickets
				var chatList = JSON.parse(data).chatList;
				chatList.forEach(function (elem, index){
					console.log(elem);
				});
			}
		}
	});
}

window.document.addEventListener('changeTicketOwnerEvent', handleMoveTicketToAnotherAgentEvent, false);
function handleMoveTicketToAnotherAgentEvent(e) {
	location.reload();
}

window.document.addEventListener('closeTicketEvent', handleCloseEvent, false)
function handleCloseEvent(e) {
	 var ticketId = e.detail.ticketIdToClose;
	$.confirm({ 
	    title: 'سيتم أغلاق هذه التذكرة بشكل نهائي '+ticketId,
	    content: 'أنتبه. لا يمكن التراجع عن هذه الخطوه . اذا كنت متأكد اختر نعم',
	    type: 'red',
	    buttons: {
	        confirm:{
	        	text :'نعم',
	        	action : function () {
	        		
	        		 var userId = <%=user.getUsid()%>;
	        		 var dataToSend = {ticketId : ticketId, closedBy : userId };
	        		 $.post('../../TicketsCloseSRVL' , dataToSend, function(data, status){ 
	        			if (status=='success'){
	        				location.reload();
	        			}else{
	        				alert("Error, please contact Mohammed Nafie");
	        				
	        			}
	        		});
	        	}
	        },
	        cancel:{
	        	text :'لا',
	        	action : function () {
	        	;
	        	}
	        }
	    }
	});
	
}

window.onload = OnPageLoad();
setInterval(OnPageLoad, 30000);
function OnPageLoad(){
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'GET',
		dataType:'json',
		cache: false,
		url:'../../TicketsCheckUnSeenChatsSRVL',  
		error:function(xhr, textStatus, message){ 
			console.log(xhr);
		},
		success: function(data, status){
			if (status=='success'){
				console.log("back again ------------------");
				var ticketsList = data.ticketsList;
				  ticketsList.forEach(function (elem, index){
					  console.log("new unread msg,ticket id--"+elem);
					  if($("#little-red-circle-"+elem).length != 0) {
						  console.log("already have rerd circle ");
						}else{
							console.log("put red circle");
							$("#underprocess-list").prepend(document.getElementById("ticket-id-"+elem));
							$("#clickable-ahref-ticket-id-"+elem).append("<span id='little-red-circle-"+elem+"' class='badge badge-pill badge-danger badge-up badge-glow' style='top: -10px;left: -10px;padding-left: 10px;padding-right: 10px;'>!</span>");
						}
						
						//
					});
				  console.log("new not assigned ");
				  var ticketsNewNotAssigned =  data.ticketsNewNotAssigned;
				  ticketsNewNotAssigned.forEach(function (elem, index){
					  console.log("inside second loop");
					  if($("#ticket-id-"+elem).length != 0) {
						}else{
							$.ajax ({
								headers: {'cache-control': 'no-cache' },
								type:'GET',
								cache: false,
								url:'../../TicketLoadHtmlSRVL?ticketId='+elem,  
								data: {},
								error:function(xhr, textStatus, message){ 
									console.log(xhr);
								},
								success: function(data, status){
									if (status=='success'){
										$(".new-tickets-list").prepend(data);
									}
								}
							});
						}
					});
			}
		}
	});
	
}

</script>

