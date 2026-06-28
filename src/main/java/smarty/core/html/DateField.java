package smarty.core.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DateField extends  HTMLShapes{

	public DateField(String extraMultiEditName, int multiEditRowNum , String colName, String txtAlign) {
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
			  boolean multiEdit,
			  HashMap<String,String> minValMap,
			  HashMap<String,String> maxValMap,
			  String htmlAttr) {
		int size = 17;
		String calFormat = "%Y-%m-%d %H:%M:%S";
		String inputType= "datetime-local";
		if (userDefinedColsHtmlType.get(colName).equalsIgnoreCase("DATE")){
			size = 10;
			calFormat = "%Y-%m-%d";
			inputType= "date";
		}
		
		String colNameToSave=colName;
		if (multiEdit)
			colNameToSave += extraMultiEditName;
		
		String DisplayValue="";
    	String style ="";
    	
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
    	
    	
    	if (Readonly !=null && Readonly.trim().length()>0)
    		;
    	else if(!required)
    		Readonly = "readonly='readonly'";
    	
    	
    	style = "text-align:"+textAlign+"; ";
    	String min="", max="";
    	if (minValMap !=null) {
	    	if (minValMap.containsKey(colNameToSave) && minValMap.get(colNameToSave)!=null) {
	    		// min must be in this format (yyyy-mm-dd)
	    		min = "min='"+minValMap.get(colNameToSave)+"'";
	    	}
    	}
    	if (maxValMap !=null) {
	    	if (maxValMap.containsKey(colNameToSave) && minValMap.get(colNameToSave)!=null) {
	    		// max must be in this format (yyyy-mm-dd)
	    		min = "max='"+maxValMap.get(colNameToSave)+"'";
	    	}
    	}
    	
    	StringBuilder HtmlDateBox= new StringBuilder("<input type='"+inputType+"' class='"+shapeClass+"'  "+htmlAttr+" size='"+size+"' "+max+" "+min+" "
				+ "style='"+style+"' "+req+" name='"+colNameToSave+"' value='"+DisplayValue+"' id='"+colNameToSave+"'>");
    	
    	/* replace by html 5 date field
    	StringBuilder HtmlDateBox= new StringBuilder(""
    			+ "<input type='text' id='"+colNameToSave+"' size='"+size+"' name='"+colNameToSave+"' class='input-text-global form-control js-datetimepicker' "+Readonly+" "
    					+ " style='"+style+"' "+req+" value='"+DisplayValue+"' />"
    					+ ""); 	
    	HtmlDateBox.append("<script> $('#"+colNameToSave+"').datetimepicker({" + 
    			"  format: 'YYYY-MM-DD',"+
    			"  ignoreReadonly: true," + 
    			"  allowInputToggle: true"
    			+ "    });"
    			+ ""
    			+ "$('#"+colNameToSave+"').on('keydown paste', function(e){" + 
    			"        e.preventDefault();" + 
    			"    });"
    			+ "</script>"); // to behave like readonly
    	*/
    	return HtmlDateBox;
	}


}
