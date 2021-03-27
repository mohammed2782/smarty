<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<body id = "admin_body">

<%

String pageName = request.getParameter("pageName").toUpperCase();
String pageNameWithoutDot =pageName.replace("." , "DOT").toUpperCase();

%>
<img id="top" src="../img/top.png" alt="">
		
<div id="smartySettings_<%=pageNameWithoutDot%>" style ='display:none;'>

	<div id="sidebar">
		<ul id="navigation">
					<li id="GEN"> <a href="#"  onclick="display_<%=pageNameWithoutDot%>('GEN');">General</a></li>
					<li id="GRID">  <a href="#"  onclick="display_<%=pageNameWithoutDot%>('GRID');">Grid</a></li>
					<li id="FORMS">  <a href="#"  onclick="display_<%=pageNameWithoutDot%>('FORMS');">FORMS</a></li>
					<li id="COLS"><a href="#"  onclick="display_<%=pageNameWithoutDot%>('COLS');">Columns Settings</a></li>		
		</ul>
	</div> 
		
	<div id="contents_<%=pageNameWithoutDot%>">
		<h1><a>Untitled Form</a></h1>
		
	</div> <!-- /#contents -->
	
</div> <!-- /#page -->
<img id="bottom" src="../img/bottom.png" alt="">
<script>
// this function will when setings button is clicked
function loadSettings(){
	var pageName = "<%=pageName%>";
	var pageNameWithoutDot = "<%=pageNameWithoutDot%>";
	var contentsDIV = document.getElementById("contents_"+pageNameWithoutDot);
	contentsDIV.className = "contents";
	
	if (window.XMLHttpRequest) {
		self.xmlHttpReq = new XMLHttpRequest(); 
	}else if (window.ActiveXObject) { 
		self.xmlHttpReq = new ActiveXObject('Microsoft.XMLHTTP');
	}
	self.xmlHttpReq.open('POST', '../SettingsLoader', false);
	self.xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	self.xmlHttpReq.onreadystatechange = function() {
		if (self.xmlHttpReq.readyState == 4) {
			if (self.xmlHttpReq.status == 200) { // OK response) 
				var rs = self.xmlHttpReq.responseText;
				contentsDIV.innerHTML = "<table><tr><td>"+rs+"</td></tr></table>";
			}else{
				alert("error "+self.xmlHttpReq.status+" , "+self.xmlHttpReq.statusText);
			}
		} 
	};
	//alert('sending');
	self.xmlHttpReq.send("jspName="+pageName+"&pageNameWithoutDot="+pageNameWithoutDot+"&screen="+screen);		
}

// will execute when anchors are clicked
function display_<%=pageNameWithoutDot%>(screen){
	document.getElementById("GEN").className = "";
	document.getElementById("GRID").className = "";
	document.getElementById("COLS").className = "";
	document.getElementById("FORMS").className = "";
	var liSelected = document.getElementById(screen);
	liSelected.className = "selected";
	
	var pageNameWithoutDot = "<%=pageNameWithoutDot%>";
	
	var inputs = document.getElementsByTagName("div");
	for(var i = 0; i < inputs.length; i++) {
		if(inputs[i].id.indexOf('DIV_'+pageNameWithoutDot) == 0) {
			document.getElementById(inputs[i].id).style.display = 'none';
			//alert(inputs[i].id);
		}
	}
	//alert('DIV_'+pageNameWithoutDot+'_'+screen);
	document.getElementById('DIV_'+pageNameWithoutDot+'_'+screen).style.display = 'block';
			
}

function hideform_<%=pageNameWithoutDot%>(s){
		// alert('hide'+s);
		  var formHide = document.getElementById(s);
		  var settingsDiv = document.getElementById("smartySettings_<%=pageNameWithoutDot%>");
		  settingsDiv.className = "smartySettings";
		  //alert('got its  ');
		  formHide.style.display='none';
		  settingsDiv.style.display='block';
		  loadSettings();
		  
		  var liSelected = document.getElementById("GEN");
		  liSelected.className = "selected";
		  var pageNameWithoutDot = "<%=pageNameWithoutDot%>";
		  document.getElementById('DIV_'+pageNameWithoutDot+'_GEN').style.display = 'block';
		  
	}

function diaplyInnerTab_<%=pageNameWithoutDot%>(tab , subtab, specific){
	var pageNameWithoutDot = "<%=pageNameWithoutDot%>";
	//alert('DIV_'+pageNameWithoutDot+'_'+tab+'_'+subtab);
	var selected_subtab_contents = document.getElementById('DIV_'+pageNameWithoutDot+'_'+tab+'_'+subtab+'_'+specific);
	var selected_subtab_LI = document.getElementById('LI_'+pageNameWithoutDot+'_'+tab+'_'+subtab);
	// Hide all DIVS
	var inputsDIV = document.getElementsByTagName('DIV');
	for(var i = 0; i < inputsDIV.length; i++) {
		//alert("id:"+inputsDIV[i].id);
		//alert("Target ids:"+'DIV_'+pageNameWithoutDot+'_'+tab+'_'+subtab);
		if(inputsDIV[i].id.indexOf('DIV_'+pageNameWithoutDot+'_'+tab+'_') == 0) {
			document.getElementById(inputsDIV[i].id).style.display = 'none';
		}
	}
	
	selected_subtab_contents.style.display = 'block';
	selected_subtab_contents.className = "content_subtabs";
	
	// Reset all LI class Name 
	var inputsLI = document.getElementsByTagName("li");
	for(var i = 0; i < inputsLI.length; i++) {
		if(inputsLI[i].id.indexOf('LI_'+pageNameWithoutDot) == 0) {
			document.getElementById(inputsLI[i].id).className = '';
			//alert(inputs[i].id);
		}
	}
	selected_subtab_LI.className = "selectedLI";
	
	}
</script>

</body>
</html>

