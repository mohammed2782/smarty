package smarty.core.html;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class NumberField extends HTMLShapes {

	public NumberField(String extraMultiEditName, int multiEditRowNum,String colName, String txtAlign) {
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
									  boolean multiEdit,
									  HashMap<String,String> minValMap,
									  HashMap<String,String> maxValMap,
									  String htmlAttr) {
		int maxLength = 20;
		int colDBSize = 10;
		NumberFormat format = NumberFormat.getCurrencyInstance();
		String step = "";
		String min = "";
		String max = "";
		
		if (minValMap !=null && minValMap.containsKey(colName))
			min = "min='"+minValMap.get(colName)+"'";
		
		if (maxValMap !=null && maxValMap.containsKey(colName))
			max = "max='"+maxValMap.get(colName)+"'";
		
		if (sqlColsSizes.get(colName)!=null){
			maxLength = sqlColsSizes.get(colName);
			colDBSize= sqlColsSizes.get(colName);
			
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
    			backgroundColor="#eeeeee";
        
    	if (disabled_attr){
			Disabled = "disabled='disabled'";
			backgroundColor="#eeeeee";
    	}
    	String colNameToSave=colName;
		if (multiEdit)
			colNameToSave += extraMultiEditName;
		
    	String DisplayValue="";
    	if (defValue!=null){
    		if (!defValue.isEmpty()) {
    			DisplayValue = defValue.get(0).replace(",", "");
    			
    		}
    		if (DisplayValue==null){
    			DisplayValue="";
    		}
    	}    	
    	style = "text-align:"+textAlign+"; ";
    	String req="";
    	if (required)
    		req= "required='required'";
    	
 //   	System.out.println("DisplayValue------>"+DisplayValue);
    	
    	if (! hidden){
    		if (userDefinedColsHtmlType.get(colName).equalsIgnoreCase("DOUBLE")
    					|| userDefinedColsHtmlType.get(colName).equalsIgnoreCase("FLOAT")
    					|| userDefinedColsHtmlType.get(colName).equalsIgnoreCase("NUMBER"))
    			step="step='0.001'";
    		
    		if (userDefinedColsHtmlType.get(colName).equalsIgnoreCase("NUMBER_WITH_COMMAS")) {
    			step="step='1'";
    			if (minValMap !=null && minValMap.containsKey(colName))
    				min = "minimumValue:"+minValMap.get(colName)+",";
    			if (maxValMap !=null && maxValMap.containsKey(colName))
    				max = "maximumValue:"+maxValMap.get(colName)+",";
    			TextHtml.append("<input dir='ltr' type='text' style='"+style+"'  name ='"+colNameToSave+"' " +
		        		"id='"+colNameToSave+"' class='"+shapeClass+"' value='"+DisplayValue+"' "+Readonly+" "+Disabled
		        		+" "+req+" "+maxLengthHtml+" "+inputSizeHtml+" "+step+" ></input>"
		        				+ "<script>$(function() {" + 
		        				"    new AutoNumeric('#"+colNameToSave+"', {" + 
		        						min + 
		        						max +
		        						"    unformatOnSubmit: AutoNumeric.options.unformatOnSubmit.unformat, "+
		        						" allowDecimalPadding: false " +
		        						"});" + 
		        				"});</script>");
    		}else {
    			TextHtml.append("<input dir='ltr' type='number' style='"+style+"'  name ='"+colNameToSave+"' " +
			        		"id='"+colNameToSave+"' class='"+shapeClass+"' value='"+DisplayValue+"' "+Readonly+" "+Disabled
			        		+" "+req+" "+maxLengthHtml+" "+inputSizeHtml+" "+step+" " +min+" "+max+"></input>");
    		}
    	}else{
    		TextHtml.append("<input type='hidden'  "+htmlAttr+" class='input-text-global'  name ='"+colNameToSave+"' id='"+colNameToSave+"' value='"+DisplayValue+"'></input>");
    	}
   
        return TextHtml;
	}

}
