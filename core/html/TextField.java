package com.app.core.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TextField extends HTMLShapes {

	public TextField(String extraMultiEditName, int multiEditRowNum,String colName, String txtAlign) {
		super(extraMultiEditName, multiEditRowNum , colName, txtAlign);
	}
	@Override
	public StringBuilder getHtmlInput(HashMap<String, Integer> sqlColsSizes,
			HashMap <String , String > userDefinedColsHtmlType, 
			LinkedHashMap<String, LinkedHashMap<String, String>> colMapValues,
									  ArrayList<String> defValue, 
									  String Readonly, 
									  boolean disabled_attr,
									  String BGcolor, 
									  boolean hidden,
									  boolean required,
									  boolean multiEdit) {
		int maxLength = 20;
		int colDBSize = 10;
		if (sqlColsSizes.get(colName)!=null){
			maxLength = sqlColsSizes.get(colName);
			colDBSize= sqlColsSizes.get(colName);
			//System.out.println("colName"+colName+", col size===>"+sqlColsSizes.get(colName));
		}
		
		if (maxLength==0){
			maxLength = 10;
		}
		int inputSize = 0;
    	if (colDBSize >= 21)
    	    inputSize = 20;
    	else
    		inputSize = maxLength;
		
    	StringBuilder TextHtml = new StringBuilder("");
    	String backgroundColor=BGcolor;
    	String Disabled = "";
    	String style    = "";
    	String maxLengthHtml = " maxlength='"+maxLength+"' ";
    	String inputSizeHtml = " size='"+inputSize+"' ";
    	
    	if (Readonly !=null)
    		if (Readonly!="")
    			backgroundColor="#E9E5E5";
        
    	if (disabled_attr){
			Disabled = "disabled='disabled'";
			backgroundColor="#E9E5E5";
    	}
    	String colNameToSave=colName;
		if (multiEdit)
			colNameToSave += extraMultiEditName;
		
    	String DisplayValue="";
    	if (defValue!=null){
    		if (!defValue.isEmpty())
    			DisplayValue = defValue.get(0); 
    		if (DisplayValue==null){
    			DisplayValue="";
    		}
    	}    	
    	style = "text-align:"+textAlign+"; background-color:"+backgroundColor+"; color: #424242;  ";
    	String req="";
    	if (required)
    		req= "required='required'";
    	
    	if (! hidden){
    		TextHtml.append("<input type='text' style='"+style+"'  name ='"+colNameToSave+"' " +
			        		"id='"+colNameToSave+"' class='"+shapeClass+"' value='"+DisplayValue+"' "+Readonly+" "+Disabled
			        		+" "+req+" "+maxLengthHtml+" "+inputSizeHtml+"></input>");
    	}else{
    		TextHtml.append("<input type='hidden'   name ='"+colNameToSave+"' id='"+colNameToSave+"' value='"+DisplayValue+"'></input>");
    	}
   
        return TextHtml;
	}

}
