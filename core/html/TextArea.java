package com.app.core.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TextArea extends HTMLShapes{
	public TextArea(String extraMultiEditName, int multiEditRowNum ,String colName, String txtAlign) {
		super(extraMultiEditName ,multiEditRowNum , colName, txtAlign);
	}

	@Override
	public StringBuilder getHtmlInput(
			HashMap<String, Integer> sqlColsSizes,
			HashMap <String , String > userDefinedColsHtmlType, 
			LinkedHashMap<String, LinkedHashMap<String, String>> colMapValues,
			ArrayList<String> defValue, 
			String Readonly, 
			boolean disabled_attr,
			String BGcolor, 
			boolean hidden,
			boolean required,
			boolean multiEdit) {
		StringBuilder TextHtml    = new StringBuilder("");
    	String backgroundColor=BGcolor;
    	String Disabled = "";
    	String style    = "";
    	String onFocus  = " onfocus=\"this.style.boxShadow='1px 1px 5px #0101DF';\"";//#DF0101-red color
    	String onBlur   = " onblur=this.style.boxShadow=''; ";
    	int maxLength = 200;
    	int colDBSize = sqlColsSizes.get(colName);
    	
		int inputSize = 0;
    	if (colDBSize >= 1)
    	    inputSize = colDBSize;
    	else
    		inputSize = maxLength;
    	
    	String maxLengthString = " maxlength='"+inputSize+"' ";
    			
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
    	
    	String colNameToSave=colName;
		if (multiEdit)
			colNameToSave += extraMultiEditName;
    	
    	style = "text-align:"+textAlign+"; background-color:"+backgroundColor+";";
   
    	if (! hidden){
    		TextHtml.append("<textarea name ='"+colNameToSave+"' id='"+colNameToSave+"'  rows='2' cols='20' "+Readonly+" "+Disabled+
			           "class='"+shapeClass+"' style='"+style+"' "+req+" "+onFocus+" "+onBlur+" "+maxLengthString+">"+DisplayValue+"</textarea>");
    	}else{
    		TextHtml.append("<input type='hidden'   name ='"+colNameToSave+"' id='"+colNameToSave+"' value='"+DisplayValue+"'></input>");
    	}
    	
        return TextHtml;
	}
}
