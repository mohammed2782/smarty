package com.app.core.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DateField_old extends  HTMLShapes{

	public DateField_old(String extraMultiEditName, int multiEditRowNum , String colName, String txtAlign) {
		super(extraMultiEditName , multiEditRowNum , colName, txtAlign);
	}

	@Override
	public StringBuilder getHtmlInput(	HashMap<String , Integer> sqlColsSizes , 
			HashMap <String , String > userDefinedColsHtmlType, 
			LinkedHashMap<String ,  LinkedHashMap<String , String>> colMapValues, 
			  ArrayList<String> defValue ,
			  String Readonly,
			  boolean disabled_attr,
			  String BGcolor,
			  boolean hidden,
			  boolean required,
			  boolean multiEdit) {
		int size = 17;
		String calFormat = "%Y-%m-%d %H:%M:%S";
		if (userDefinedColsHtmlType.get(colName).equalsIgnoreCase("DATE")){
			size = 10;
			calFormat = "%Y-%m-%d";
		}
		
		String colNameToSave=colName;
		if (multiEdit)
			colNameToSave += extraMultiEditName;
		
		String DisplayValue="";
    	String style ="";
    	String onFocus  = " onfocus=\"this.style.boxShadow='1px 1px 5px #0101DF';\"";//#DF0101-red color
    	String onBlur   = " onblur=this.style.boxShadow=''; ";
    	if (defValue!=null){
    		if (!defValue.isEmpty())
    			DisplayValue = defValue.get(0); 
    		if (DisplayValue==null){
    			DisplayValue="";
    		}
    	}
    	String req="";
    	if (required)
    		req= "required='required'";
    	
    	style = "text-align:"+textAlign+"; background-color:"+BGcolor+"; color: #424242;   ";
    	
    	StringBuilder HtmlDateBox= new StringBuilder("<input size='"+size+"' type='text' "
    			+ "id='"+colNameToSave+"'  class='"+shapeClass+"' style='"+style+"' "
    						+" "+req+" name='"+colNameToSave+"' value='"+DisplayValue+"' "+Readonly+"/>"); 	
    	HtmlDateBox.append("<script>cal.manageFields(\""+colName+"\", \""+colName+"\", '"+calFormat+"');</script>");
    	HtmlDateBox.append("<script>$(\"#"+colNameToSave+"\").keydown(function(e){ e.preventDefault();})</script>"); // to behave like readonly
   
    	return HtmlDateBox;
	}


}
