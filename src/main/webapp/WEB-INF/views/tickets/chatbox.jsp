<body
	class="horizontal-layout horizontal-menu content-left-sidebar chat-application "
	data-open="hover" data-menu="horizontal-menu"
	data-col="content-left-sidebar">

	<%@page import="com.app.util.Utilities"%>
	<%
String displayChatBoxInline = (String)request.getParameter("displayChatBoxInline");
%>
	<%@ include file="../Main/Main-popup.jsp"%>
	<%@ page
		import="com.app.tickets.Chats, com.app.tickets.ChatBean, com.app.tickets.TicketsUtilities, 
com.app.tickets.TicketBean, com.app.beans.UserBean"%>
<%
	String chatTicketId = (String)request.getParameter("chatTicketId");
	if (chatTicketId !=null){
		Myglobals.smartyGlobalsAssArr.put("chatTicketId", (String)chatTicketId);
	}else if (Myglobals.smartyGlobalsAssArr.containsKey("chatTicketId") && Myglobals.smartyGlobalsAssArr.get("chatTicketId")!=null){
		chatTicketId = (String)Myglobals.smartyGlobalsAssArr.get("chatTicketId");
	}
	Connection conn = null;
	TicketsUtilities tu = new TicketsUtilities();
	LinkedList<ChatBean> chatList = new LinkedList<ChatBean>();
	TicketBean ticketBean = new TicketBean();
	Chats chats = new Chats();
	ArrayList<UserBean> ticketsAgentsList = new ArrayList<UserBean>();
	String whoChangeTicketOwnership = null;
	try{
		conn = mysql.getConn();
		chatList = chats.getChatsList(conn, Integer.parseInt(chatTicketId), user.getBranchCode());
		ticketBean = tu.getSingleTicketInfo(conn, Integer.parseInt(chatTicketId));
		if (user.getBranchCode() == 1){
			ticketsAgentsList = Utilities.getTicketsHelpDeskAgents(conn, user.getUsid());
			whoChangeTicketOwnership = TicketsUtilities.getLastPersonWhoChangedTicketOwner(conn, Integer.parseInt(chatTicketId));
		}
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		try{conn.close();}catch(Exception e){}
	}
	boolean isChatWithBranchOtherThanBaghdadFound = false;
%>
<div class='row'>
	<div class='col-3'>
		<div class="sidebar-left">
			
				<!-- app chat sidebar start -->
				<div class="chat-sidebar card show" style='height: 100% !important;'>
					<div class='row m-1'>
						<div class='col-12'>
							<div class="chat-sidebar-search">
								
									<div class='col-12 badge badge-danger btn p-50 mb-1'>
										<span><%=chatTicketId %></span> <i class="la la-tag"></i>
									</div>
									<div class='col-12 badge badge-info btn p-50 mb-1'>
										<% if (ticketBean.getTktCaseId()>0){ %>
											 <a 
											onclick="popitup ('../cases/displaySingleCaseInfo?smarty_PlayPageInPopUpMode=true&amp;auditcaseid=<%=ticketBean.getTktCaseId()%>' , '' , 1350 ,700);"
											data-bs-toggle="pill" href="javascript:;"><%=ticketBean.getOriReceiptNo()%>  <i class="la la-eye"></i></a>
											
										<%}else{%>
											
												<medium>لا توجد شحنة</medium>
										 <%}%>
									</div>
									<div class='col-12 badge badge-glow badge-pill badge-warning btn p-50 mb-1'>
										<a  data-bs-toggle="pill" href="javascript:;" onclick="closeTicket(<%=chatTicketId%>)">
												<medium>إغلاق التذكرة</medium> <i class='ft-x'></i>
										</a>
									</div>
								</div>
						</div>
					</div>
					<div class='row m-1'>
						<div class="col-12 chat-sidebar-list-wrapper pt-2" style='height:350px !important; margin-top:9rem;'>
						
							<ul class="chat-sidebar-list" style='list-style: none;'>
							<% 
								int i = 1; String activeClass = "active";
						  		for(ChatBean chatBean : chatList){ 
							   		if (chatBean.getChatWithRank().equalsIgnoreCase("BRANCH") && chatBean.getChatWithId()== user.getBranchCode()){
								   		isChatWithBranchOtherThanBaghdadFound = true;
							   		}
								    if (i>1){
								    	activeClass="";
								    }else{
							    	%>
							    	<script >
							    	$(document).ready(function() {
							    		getChatMsg(<%=chatBean.getChatId()%>,<%=chatBean.getChatWithId()%>);
							    	});
							    	</script>
							    	<% 
							    	}
						     		%>
								<li class='<%=activeClass%>'>
									<h6 class="mb-0">
										<a href="javascript:;" 
										 id="a-header-chat-id-<%=chatBean.getChatId()%>" 
										 onclick='getChatMsg(<%=chatBean.getChatId()%>, <%=chatBean.getChatWithId()%>)'>
											<div class="d-flex">
												
												<div class="flex-grow-1 ms-2 position-relative" >
													<h6 class="mb-0 chat-title" style="margin-top:5px; font-size:11px;"><%=chatBean.getChatWithName()%></h6>
													<% if (chatBean.getSeenByControl().equalsIgnoreCase("N")){ %>
													<span id='little-red-circle-chatid-<%=chatBean.getChatId()%>' class="position-absolute top-0 start-100 translate-middle badge border border-light rounded-circle bg-danger p-1"><span class="visually-hidden">unread messages</span></span>
													<%} %>
												</div>
											</div>
										</a>
									</h6>
								</li>
								
								<% 
							    	}
						     		%>
							</ul>
						</div>
	
					</div>
				</div>
				<!-- app chat sidebar ends -->
	
			</div>
	
	</div>
	<div class='col-9' style = 'padding: 1px;'>
		<div class="content-right" style='width: 100%;'>
			<div class="content-overlay"></div>
			<div class="content-wrapper">
				<div class="content-header row"></div>
				<div class="content-body">
					<!-- app chat overlay -->
					<div class="chat-overlay"></div>
					<!-- app chat window start -->
					<section class="chat-window-wrapper">
						<div class="chat-start">
							
						</div>
						<div class="chat-area d-none">
	
							<!-- chat card start -->
							<div class="card chat-wrapper shadow-none mb-0">
								<div class="card-content">
									<div class="card-body chat-container">
										<div class="chat-content" id= 'chat-content'>
											
											
										</div>
									</div>
								</div>
								<%if (!ticketBean.getTktStatusCode().equalsIgnoreCase("CLS")){ %>
									<div class="card-footer chat-footer px-2 py-1 pb-0">
										<div class="d-flex align-items-center">
											<textarea class="form-control chat-message-send mx-1"  rows="1"  id='msgtosend' name="msgtosend"></textarea>
											<!-- <input type="text" id='msgtosend' class="form-control" placeholder="Type a message"> -->
											<button type="button" onclick ='sendmsg()' class='btn btn-primary glow send d-lg-flex'>
											<i class="ft-play" style='transform: rotate(180deg);display: block;'></i> 
											<span class="d-none d-lg-block mx-50">أرسال</span>
											</button>
										</div>
									</div>
								<%} %>
								
							</div>
							<!-- chat card ends -->
						</div>
					</section>
					<!-- app chat window ends -->
	
	
				</div>
			</div>
		</div>
	</div>
</div>

</body>
<jsp:include page="../Main/footer-popup.jsp" />
<script>

var activeChatId = 0;

if (<%=isChatWithBranchOtherThanBaghdadFound%>){
	$("#master-customer-new-conversation-from-branch").css("display", "none");
}

var currentlySendingMsg = 0;
function sendmsg(){
	var sendMsg = $("#msgtosend").val();
	if (sendMsg !=='' && currentlySendingMsg==0){
		currentlySendingMsg = 1;
		var dataToSend = {chatId :activeChatId, senderId :<%=user.getUsid()%>, senderRank :'<%=user.getRank_code()%>', communicationMedium :'SYSTEM', msg: sendMsg };
		$.ajax ({
			headers: {'cache-control': 'no-cache' },
			type:'POST',
			cache: false,
			url:'../../TicketChatAddMsgSRVL',  
			data: dataToSend,
			error:function(){ alert("some error occurred, add msg") },
			success: function(data, status){ 
					if (status=='success'){ 
						
						var conversationHtml ="<div class='chat-content-leftside' id='chat-msg-id-"+data+"'>";
						conversationHtml +="<div class='d-flex'>";
						conversationHtml +='<div class="flex-grow-1 ms-2">';
						conversationHtml +='<p class="mb-0 chat-time">'+new Date().toLocaleTimeString()+'</p>';
						conversationHtml +='<p class="chat-left-msg" style="white-space: pre-wrap">'+sendMsg+'</p>';
						conversationHtml +="</div></div></div>";
						console.log($('#chat-id-'+activeChatId).last() );
						$('#chat-id-'+activeChatId).append(conversationHtml);
						$("#chat-content").scrollTop($("#chat-content")[0].scrollHeight);
						$("#msgtosend").val('');
					}
			}
		});
		currentlySendingMsg =0;
	}
	
}	

function getChatMsg (chatId, a_chatWithWhichBranchId){
	
	var currentBranch = <%=user.getBranchCode()%>;
	
	activeChatId = chatId;
	$.get('../../TicketChatMessageSRVL?chatId='+chatId ,function(data, status){ 
		if (status=='success'){
			var chatList = JSON.parse(data).chatList;
			var conversationHtml = "<div  id ='chat-id-"+chatId+"'/>";
			chatList.forEach(function (elem, index){
				if (elem.msgFromController =='N'){
					conversationHtml +="<div class='chat'  id='chat-msg-id-"+elem.msgId+"'>";
					//conversationHtml +='<p class="mb-0 chat-time text-end">'+elem.senderName+' , '+elem.msgDate+'</p>';
					
				}else{
					conversationHtml +="<div class='chat chat-left' id='chat-msg-id-"+elem.msgId+"'>";
				}
				conversationHtml +='<div class="chat-body"><div class="chat-message"><p>'+elem.msg+'</p>';
				conversationHtml +='<span class="chat-time">'+elem.senderName+' , '+elem.msgDate+'</span>';
				conversationHtml +='</div></div></div>';
				console.log(elem);
			});
			conversationHtml +="</div>"
			$("#chat-content").html(conversationHtml);
			$("#chat-content").scrollTop($("#chat-content")[0].scrollHeight);
			//list-group-item
			$(".list-group-item").each(function (index, elem){
				$(this).removeClass("active");
			});
			$("#a-header-chat-id-"+chatId).addClass("active");
		}
	});
	// masrk as seen
	
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'POST',
		cache: false,
		url:'../../TicketsMarkChatAsSeenSRVL',  
		data: {chatId: chatId},
		error:function(){ alert("some error occurred, mark as seen") },
		success: function(data, status){
			// remove the span red
			$("#little-red-circle-chatid-"+chatId).remove();
		}
	});
	
}


function loadOnlyBranches(){
	var keyCol = "branch-only-droplist";
	var req = "N";
	var lookupSql = "select branch_id, branch_name from kbbranches where branch_active = 'Y' ";
	const caseId = <%=ticketBean.getTktCaseId()%>;
	if( caseId > 0){
		lookupSql += 'and ( (branch_id in (select c_branchcode from p_cases where c_id='+caseId+' and c_branchcode !=1  and  q_branch !=1))';
		lookupSql += ' or (branch_id in (select cc_tobranch from p_caseschain where cc_caseid ='+caseId+' and cc_tobranch!=1 ) ))'; 
		
	}
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("branch-only-droplist");
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'POST',
		cache: false,
		url:'../../smarty_ajax_no_touch.jsp',  
		data: dataToSend,
		error:function(){ alert("some error occurred, load branches") },
		success: function(data, status){
			if (status=='success'){ 
				targetHTMLElement.innerHTML=data;
				$("#branch-new-conversation").css("display","block");
				$("#branch-dlvagent-new-conversation").css("display","none");
				$("#master-customer-new-conversation").css("display","none");
			}
		}
	});
}

function loadBranchesForAgents(){
	var keyCol = "branch-dlvagent-droplist";
	var req = "N";
	
	var alreadyFoundAgentId = <%=ticketBean.getAssignedAgent()%>
	if (alreadyFoundAgentId>0){
		var lookupSql = "select branch_id, branch_name from kbbranches where branch_id = (select us_branchcode from kbusers where us_id = "+alreadyFoundAgentId+" ) ";
	}else{
		var lookupSql = "select branch_id, branch_name from kbbranches where 1=1 ";
		
	}
	
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("branch-dlvagent-droplist");
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'POST',
		cache: false,
		url:'../../smarty_ajax_no_touch.jsp',  
		data: dataToSend,
		error:function(){ alert("some error occurred, load dlv agent") },
		success: function(data, status){
			if (status=='success'){ 
				targetHTMLElement.innerHTML=data;
				$("#branch-new-conversation").css("display","none");
				$("#branch-dlvagent-new-conversation").css("display","block");
				$("#master-customer-new-conversation").css("display","none");
			}
		}
	});
}

function loadAgents(){
	var keyCol = "dlvagent-droplist";
	var req = "N";
	var branchCode = $("#branch-dlvagent-droplist").val();
	var alreadyFoundAgentId = <%=ticketBean.getAssignedAgent()%>
	if (alreadyFoundAgentId>0){
		var lookupSql = "select us_id, us_name from kbusers where us_rank = 'DLVAGENT' and us_id="+alreadyFoundAgentId;
	}else{
		var lookupSql = "select us_id, us_name from kbusers where us_rank = 'DLVAGENT' and us_branchcode ="+branchCode;
	}
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("dlvagent-droplist");
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'POST',
		cache: false,
		url:'../../smarty_ajax_no_touch.jsp',  
		data: dataToSend,
		error:function(){ alert("some error occurred, loaing delivery agents") },
		success: function(data, status){
			if (status=='success'){ 
				targetHTMLElement.innerHTML=data;
				
			}
		}
	});
}


function loadMasterCustomers(){
	var keyCol = "master-customer-droplist";
	var req = "N";
	var lookupSql = "select mcust_id, mcust_name from kb_mastercustomer";
	const caseId = <%=ticketBean.getTktCaseId()%>;
	if( caseId > 0){
		lookupSql += " where mcust_id in (select c_mastercustid from p_cases where c_id="+caseId+" )";
	}
	
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("master-customer-droplist");
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'POST',
		cache: false,
		url:'../../smarty_ajax_no_touch.jsp',  
		data: dataToSend,
		error:function(){ alert("some error occurred, load customers") },
		success: function(data, status){
			if (status=='success'){ 
				targetHTMLElement.innerHTML=data;
				$("#branch-new-conversation").css("display","none");
				$("#branch-dlvagent-new-conversation").css("display","none");
				$("#master-customer-new-conversation").css("display","block");
				
			}
		}
	});
}

function loadCustomers(){
	var keyCol = "customer-droplist";
	var req = "N";
	var masterCust = $("#master-customer-droplist").val();
	var lookupSql = "select cust_id, cust_name from kbcustomers where cust_mastercustid ="+masterCust;
	
	var dataToSend = {sqllookup:lookupSql, name :keyCol, id :keyCol, mustfill:req, HTMLtype:'DROPLIST' };
	var targetHTMLElement = document.getElementById("customer-droplist");
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'POST',
		cache: false,
		url:'../../smarty_ajax_no_touch.jsp',  
		data: dataToSend,
		error:function(){ alert("some error occurred, loading customers") },
		success: function(data, status){
			if (status=='success'){ 
				targetHTMLElement.innerHTML=data;
				
			}
		}
	});
}


function startChat(withWho, withId){
	var customer = $("#customer-droplist").val();
	var masterCust = $("#master-customer-droplist").val();
	var dlvAgentBranch =  $("#branch-dlvagent-droplist").val();
	var dlvAgent = $("#dlvagent-droplist").val();
	var onlyBranch = <%=user.getBranchCode()%>
	
	if (withId == 0){
		onlyBranch = $("#branch-only-droplist").val();
	}
	var nameOfChatWith ='';
	if (masterCust){
		nameOfChatWith = $("#master-customer-droplist").find(":selected").text();
		if (customer){
			nameOfChatWith = $("#customer-droplist").find(":selected").text();
		}
	}
	if (dlvAgent){
		nameOfChatWith = $("#dlvagent-droplist").find(":selected").text();
	}
	if (withWho == 'BRANCH'){
		nameOfChatWith = $("#branch-only-droplist").find(":selected").text();
		if (withId != 0){
			$("#master-customer-new-conversation-from-branch").css("display", "none");
			nameOfChatWith = "<%=user.getBranchName()%>";
		}
	}
	var dataToSend = {chatWithWho : withWho , masterCustomer : masterCust, customerId: customer, branchDlvAgent : dlvAgentBranch, 
			dlvAgent: dlvAgent, onlyBranch: onlyBranch, ticketId:<%=chatTicketId%>, startedBy:<%=user.getUsid()%> };
   
	$.ajax ({
		headers: {'cache-control': 'no-cache' },
		type:'POST',
		dataType:'json',
		cache: false,
		url:'../../TicketStartChatSRVL',  
		data: dataToSend,
		error:function(xhr, textStatus, message){ 
			console.log(xhr);
		},
		success: function(data, status){
			if (status=='success'){
				$("input[id^='a-header-chat-id-']").each(function(){
					   $(this).removeClass("active");
				});
				
				var newChat = data.newChat[0];
				activeChatId = newChat.chatId;
				var newChatHtml = "<a href='javascript:;' class='list-group-item active' id='a-header-chat-id-"+newChat.chatId+"' " +
				 					" onclick='getChatMsg("+newChat.chatId+" )'>";
				newChatHtml += "<div class='d-flex'>";
				newChatHtml += "<div class='chat-user'>";
				newChatHtml += "</div>";
				newChatHtml += "<div class='flex-grow-1 ms-2'>";
				newChatHtml += "<h6 class='mb-0 chat-title' style='margin-top:5px; font-size:11px;'>"+nameOfChatWith+"</h6>";
				newChatHtml += "</div></div></a>";
				$("#chat-with-title").prepend(newChatHtml);
				getChatMsg(newChat.chatId, withId);
			}
		}
		});
}


function changeTicketOwner(ticketId){
	var changeToAgent = $("#helpDesk-Agents-droplist").val();
	$.confirm({ 
	    title: 'سيتم تحويل التذكرة الى عميل اخر '+ticketId,
	    content: 'أنتبه. لا يمكن التراجع عن هذه الخطوه . اذا كنت متأكد اختر نعم',
	    type: 'red',
	    buttons: {
	        confirm:{
	        	text :'نعم',
	        	action : function () {
	        		 var dataToSend = {ticketId : ticketId, changedTo : changeToAgent };
	        		 $.post('../../TicketChangeOwnerSRVL' , dataToSend, function(data, status){ 
	        			if (status=='success'){
	        				var event = new CustomEvent('changeTicketOwnerEvent', )
	        				window.parent.document.dispatchEvent(event);
	        				window.close();
	        			}else{
	        				alert("Error change ticket owner, please contact Mohammed Nafie");
	        				
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

function closeTicket(ticketId){
	
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
	        				window.close();
	        			}else{
	        				alert("Error closing ticket, please contact Mohammed Nafie");
	        				
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
	
	var ticketIdToClose = { ticketIdToClose: ticketId }
	var event = new CustomEvent('closeTicketEvent', { detail: ticketIdToClose })
	window.parent.document.dispatchEvent(event)
	
}
</script>