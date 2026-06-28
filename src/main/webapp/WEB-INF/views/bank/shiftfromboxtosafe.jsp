<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.incomeoutcome.bank.ShiftFromBoxToSafe" %> 
<%@ page import="com.app.incomeoutcome.bank.ShiftFromBoxToSafeWhenSafeOff" %>
<% 
 	String pageName1 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
	ShiftFromBoxToSafe shiftFromBoxToSafe = new ShiftFromBoxToSafe(); 
 	Render(shiftFromBoxToSafe , out , request, response , Myglobals , objectState , pageName1);
 %>
 <div class = "row">
	<hr style="margin-top:0.9rem;height: 10px;border: 0; box-shadow: 0 10px 10px -10px #8c8b8b inset;">
</div>
<div class = "row">
	<div class = "col-4">
		<%
		
			String pageName2 = this.getClass().getPackage().getName()+"."+this.getClass().getSimpleName();
			ShiftFromBoxToSafeWhenSafeOff sfbtswso = new ShiftFromBoxToSafeWhenSafeOff(); 
			
			Render(sfbtswso  , out , request, response , Myglobals , objectState , pageName2);
			
		%> 
	</div>
</div>
<%@ include file="../Main/footer.jsp"%>  
