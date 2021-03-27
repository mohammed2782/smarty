<%@page import="com.app.db.mysql"%>
<%@ page language="java" pageEncoding="utf-8"%>
<%@ page
	import="java.util.List , java.util.HashMap , java.util.ArrayList , java.util.Iterator , java.util.LinkedHashMap , java.util.Enumeration
			,java.sql.Connection , java.sql.PreparedStatement , java.sql.ResultSet , com.app.core.html.*"%>
<jsp:useBean id="Myglobals" class="com.app.core.smartyglobals" scope="session" />
<%
request.setCharacterEncoding("UTF-8");
response.setCharacterEncoding("UTF-8");
String HTMLResult = "";
Connection conn =null;
PreparedStatement pst = null;
ResultSet rs = null;
try{ 
	String sqllookup = request.getParameter("sqllookup");
	String htmlID = request.getParameter("id");
	String htmlName = request.getParameter("name");
	String HTMLtype = request.getParameter("HTMLtype");
	String mustFill = request.getParameter("mustfill");
	
	Enumeration<String> enumParams =	request.getParameterNames();
	String param="";
	String param_val ="";
	System.out.println(sqllookup);
	boolean queryParametersEmpty = false;
	while (enumParams.hasMoreElements()){
		 param = (String) enumParams.nextElement();
		 
		if ( (!param.equalsIgnoreCase("id")) &&  (!param.equalsIgnoreCase("name")) && (!param.equalsIgnoreCase("sqllookup")) && (!param.equalsIgnoreCase("HTMLtype")) && (!param.equalsIgnoreCase("mustfill"))){
				param_val = request.getParameter(param);

			if (param_val !=null && !param_val.trim().equalsIgnoreCase("") ){
				//System.out.println("param_val before checking not null, "+param+"----->"+param_val);
				sqllookup = sqllookup.replace("{"+param+"}",param_val );
				//System.out.println("sqllookup"+sqllookup+"----->"+param_val);
			}else{ // if null or rempty then we should not run the sql
				//System.out.println("param_val before checking, null, "+param+"----->"+param_val);
				sqllookup = sqllookup.replace("{"+param+"}","" );
				queryParametersEmpty = true;
			}
		}
	}
	if (!queryParametersEmpty){
		conn = mysql.getConn();
		//System.out.println("this is ===>"+sqllookup);
		pst = conn.prepareStatement(sqllookup);
		rs = pst.executeQuery();
	}else{
		rs = null;
	}
	if (HTMLtype.equals("DROPLIST")){
		HTMLResult = "<select id ='"+htmlID+"' name='"+htmlName+"'>";
		if (!mustFill.equalsIgnoreCase("Y"))
			HTMLResult +="<option value=''></option>";
		if (rs!=null)
			while (rs.next()){
				HTMLResult = HTMLResult+"<option value='"+rs.getString(1)+"'>"+rs.getString(2)+"</option>";				
			}	
		HTMLResult = HTMLResult+"</select>";
	}else if  (HTMLtype.equals("CHECKBOX")){
		LinkedHashMap<String , LinkedHashMap<String , String>> colMapValues = new LinkedHashMap<String , LinkedHashMap<String,String>>();
		LinkedHashMap<String ,  String> colMapValuesInsider = new LinkedHashMap<String , String>();
		if (rs !=null)
			while (rs.next()){
				colMapValuesInsider.put(rs.getString(1), rs.getString(2));
			}
		
		colMapValues.put(htmlID, colMapValuesInsider);
			//needs fixing
		CheckBox chkbx = new CheckBox("",0,htmlName,"left");
		HTMLResult =(chkbx.getHtmlInput(new HashMap<String , Integer>() ,new HashMap<String , String>() , 
					colMapValues , new ArrayList<String>(),"",false , "" , false , false , false)).toString();
			
		System.out.println(HTMLResult);
	}else{
		try{
			if (rs !=null)
				if (rs.next())
					HTMLResult = rs.getString(1);
		}catch (Exception e){
			HTMLResult ="";
			System.out.println("ERR:myajax.jsp, get the string for Text,rs.getString(1)"); 
			e.printStackTrace();
		}
	}
}catch(Exception e){
	e.printStackTrace();
	HTMLResult ="";
}finally{
	try{rs.close();}catch(Exception e){}
	try{pst.close();}catch(Exception e){}
	try{conn.close();}catch(Exception e){}
}
%>
<%=HTMLResult%>