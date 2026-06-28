package smarty.core.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CheckBox extends HTMLShapes{
	
	public CheckBox(String extraMultiEditName, int multiEditRowNum,String colName, String txtAlign) {
		super(extraMultiEditName, multiEditRowNum , colName, txtAlign);
		// TODO Auto-generated constructor stub
	}

	public StringBuilder getHtmlInput( 	HashMap<String , Integer> sqlColsSizes , 
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
			  String htmlAttr)
	{
		 ArrayList<String> defValueParesed = new  ArrayList<String>();
		String req="";
    	if (required)
    		req= "required";
		StringBuilder CheckBoxHtml= new StringBuilder("<div id='"+colName+"_div' class='form-check' "+req+"'>");
		String colNameToSave=colName;
		if (multiEdit)
			colNameToSave += extraMultiEditName;
		
		String  checked="";
		for (String defVal : defValue){
			String [] myArr = defVal.split(":");
			for (int i=0 ; i<myArr.length ; i++)
				defValueParesed.add(myArr[i]);		
		}
		if (colMapValues!=null){
			if (colMapValues.get(colName)!=null){
				Map <String , String> lookupsmap = colMapValues.get(colName);
				if (lookupsmap !=null){
					if (!lookupsmap.isEmpty()){
						CheckBoxHtml.append("<table class='special_nohover_checkbox' style='border: 0px solid #f7f7f71f;'>");
						int i =0;
						for (String code : lookupsmap.keySet()){
							if (i==0)
								CheckBoxHtml.append("<tr>");
							if(i%2==0){
								CheckBoxHtml.append("</tr><tr>");
							}
							checked = "";
							if(defValueParesed !=null)
								if (defValueParesed.contains(code)){
									checked = "checked";
								}
							CheckBoxHtml.append(
								"<td>"
								+ "<input class='form-check-input' type='checkbox' "+checked
								+ " id='"+colNameToSave+"' name='"+colNameToSave+"' value='"+code+"' > "
										+ "<label class='form-check-label' style='margin-left: 20px;'>"+lookupsmap.get(code)+"&nbsp;&nbsp;</label>"
										+ "</td>");
							i++;
						}
						CheckBoxHtml.append("</tr></table>");
						}else{
							CheckBoxHtml.append("NO LOOKUP");
						}
					}else{
						CheckBoxHtml.append("NO LOOKUP");
					}
			}else{
				CheckBoxHtml.append("NO LOOKUP");
			}
		}else{
			CheckBoxHtml.append("NO LOOKUP");
		}
		CheckBoxHtml.append("</div>");
		if (required)// need to add jquery fix for validation as the required HTML5 , is not working right
			;//CheckBoxHtml.append("<scrip>$('div.checkbox-group.required :checkbox:checked').length > 0</script>");
		return CheckBoxHtml;
	}
}
