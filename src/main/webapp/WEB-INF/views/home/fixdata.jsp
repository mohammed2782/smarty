<%@ include file="../Main/Main.jsp"%>
<%@ page import="com.app.bussframework.FixData" %>
<%
System.out.println("here");
if ("POST".equalsIgnoreCase(request.getMethod())) {
	
		FixData fd = new FixData();
		System.out.println("calling-----------"+request.getParameter("fixparentchaineId"));
		fd.buildParentRelationInCain(Integer.parseInt(request.getParameter("startcaseid")), Integer.parseInt(request.getParameter("endcaseid")));
		System.out.println("after calling");
}
%>

<form method= 'post' action =''>
<input type='number' name='startcaseid'/>
<input type='number' name='endcaseid'/>
<button type='submit' name='fixparentchaineId'>Fix parent data in chain</button>
</form>

<%@ include file="../Main/footer.jsp"%> 



