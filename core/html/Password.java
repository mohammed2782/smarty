package com.app.core.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Password extends HTMLShapes {

	public Password(String extraMultiEditName , int multiEditRowNum,String colName, String txtAlign) {
		super(extraMultiEditName ,multiEditRowNum , colName, txtAlign);
	}

	@Override
	public StringBuilder getHtmlInput( 	HashMap<String , Integer> sqlColsSizes , 
			HashMap <String , String > userDefinedColsHtmlType, 
			LinkedHashMap<String ,  LinkedHashMap<String , String>> colMapValues, 
			  ArrayList<String> defValue ,
			  String Readonly,
			  boolean disabled_attr,
			  String BGcolor,
			  boolean hidden,
			  boolean required,
			  boolean multiEdit) {
		int maxLength = sqlColsSizes.get(colName);
		int inputSize = 0;
    	int colDBSize = sqlColsSizes.get(colName).intValue();
    	if (colDBSize >= 21)
    	    inputSize = 20;
    	else
    		inputSize = maxLength;
    	
    	String colNameToSave=colName;
		if (multiEdit)
			colNameToSave += extraMultiEditName;
		
    	StringBuilder TextPassword = new StringBuilder("");
		String backgroundColor=BGcolor;
		String Disabled = "";
		String style    = "";
		String onFocus  = " onfocus=\"this.style.boxShadow='1px 1px 5px #0101DF';\"";//#DF0101-red color
		String onBlur   = " onblur=this.style.boxShadow=''; ";
		String maxLengthHtml = " maxlength='"+maxLength+"' ";
		String inputSizeHtml = " size='"+inputSize+"'";
	
		if (Readonly !=null)
		if (Readonly!="")
			backgroundColor="#E9E5E5";
		
		if (disabled_attr){
		Disabled = "disabled='disabled'";
		backgroundColor="#E9E5E5";
		}	
		String DisplayValue="";
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
		style = "text-align:"+textAlign+"; background-color:"+backgroundColor+"; ";
		
		if (! hidden){
			TextPassword.append("<input type='password' style='"+style+"'  name ='"+colNameToSave+"' " +
		     		"id='"+colNameToSave+"' value='"+DisplayValue+"' class='"+shapeClass+"' "+Readonly+" "+Disabled
		     		+" "+onFocus+" "+onBlur+" "+maxLengthHtml+" "+inputSizeHtml+" "+req+">");
		}else{
			TextPassword.append("<input type='hidden'   name ='"+colNameToSave+"' id='"+colNameToSave+"' value='"+DisplayValue+"'></input>");
		}

		return TextPassword;
	
	}

}
