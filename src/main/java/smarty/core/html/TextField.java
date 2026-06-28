package smarty.core.html;

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
									  boolean multiEdit,
									  HashMap<String,String> minValMap,
									  HashMap<String,String> maxValMap,
									  String htmlAttr) {
		int maxLength = 27;
		int colDBSize = 10;
		String style    = "";
		
		if (sqlColsSizes.get(colName)!=null){
			maxLength = sqlColsSizes.get(colName);
			colDBSize= sqlColsSizes.get(colName);
			//System.out.println("colName"+colName+", col size===>"+sqlColsSizes.get(colName));
		}
		
		if (maxLength==0){
			maxLength = 27;
		}
		int inputSize = 0;
    	if (colDBSize >= 35)
    	    inputSize = 35;
    	else
    		inputSize = maxLength;
		String mask = "";
    	
    	StringBuilder TextHtml = new StringBuilder("");
    	String backgroundColor="#fff";
    	String Disabled = "";
    	
    	
    	
    	if (Readonly !=null)
    		if (Readonly!="")
    			backgroundColor="#dad8d8";
        
    	if (disabled_attr){
			Disabled = "disabled='disabled'";
			backgroundColor="#dad8d8";
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
    	String type =  "type='text'";
    	String pattern = "";
    	String dataRole = "";
    	if (userDefinedColsHtmlType !=null 
    			&& userDefinedColsHtmlType.containsKey(colName) ) {
    		if (userDefinedColsHtmlType.get(colName).equalsIgnoreCase("PHONE")) {
    		//mask = "data-inputmask=\"'mask': '9999 999 9999'\""; mask cause a problem when copy arabic numbers
   		 		maxLength = 11;
   		 		inputSize = 11;
   		 		style = "text-align:"+textAlign+"; direction: ltr;";
   		 		type =  "type='tel'";
   		 		pattern = "pattern='[0-9\u0660-\u0669]{11}'";
   		 	}else if (userDefinedColsHtmlType.get(colName).equalsIgnoreCase("TAGS")) {
   		 		dataRole = "data-role=\"tagsinput\" ";
   		 	}else {
   		 		style = "text-align:"+textAlign+"; ";
   		 	}
    		
    	}else {
    		style = "text-align:"+textAlign+";";
    	}
    	String maxLengthHtml = " maxlength='"+maxLength+"' ";
    	String inputSizeHtml = " size='"+inputSize+"' ";
   	
    	String req="";
    	if (required)
    		req= "required='required'";
    	
    	TextHtml.append("<input "+htmlAttr+" "+type+"  "+mask+"   name ='"+colNameToSave+"' " + dataRole +
			        		"id='"+colNameToSave+"' class='"+shapeClass+"' value='"+DisplayValue+"' "+Readonly+" "+Disabled
			        		+" "+req+" "+maxLengthHtml+" "+inputSizeHtml+" "+pattern+"/>");
        return TextHtml;
	}

}
