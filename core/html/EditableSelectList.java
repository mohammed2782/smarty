package com.app.core.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class EditableSelectList extends HTMLShapes {

	public EditableSelectList(String extraMultiEditName, int multiEditRowNum,String colName, String txtAlign) {
		super(extraMultiEditName , multiEditRowNum , colName, txtAlign);
	}
/*
 * this will return a drop down list with a hidden field that will hold the real value
 * */
	@Override
	public StringBuilder getHtmlInput(	HashMap<String , Integer> sqlColsSizes , HashMap <String , String > userDefinedColsHtmlType, 
			LinkedHashMap<String ,  LinkedHashMap<String , String>> colMapValues, 
			  ArrayList<String> defValue ,
			  String Readonly,
			  boolean disabled_attr,
			  String BGcolor,
			  boolean hidden,
			  boolean required,
			  boolean multiEdit) {
		StringBuilder editSelect= new StringBuilder("");
		String Disabled = "";
		String backgroundColor=BGcolor;
		String DisabledValue ="";
		String ColNameToSave = colName;
		String styleClass = "form-control";
    	String style    = "";
    	String req="";
    	
    	if (required)
    		req= "required='required'";
    	if (Readonly !=null)
    		if (Readonly!="")
    			disabled_attr = true;// drop down don't have readonly , so we make it disabled, as a work around
    	
		if (disabled_attr){
			Disabled = "disabled='disabled'";
			backgroundColor="#E9E5E5;";
			ColNameToSave = "smarty_showonly_"+colName;
		}
		String minWidth="min-width:200px;";
		if (colMapValues!=null){
			if (colMapValues.get(colName)!=null){
				minWidth= ""; 
			}
		}
		if (multiEdit){
			ColNameToSave = colName+extraMultiEditName;
			minWidth = "";
			styleClass="";
		}
		style = "text-align:"+textAlign+"; background-color:"+backgroundColor+"; padding: 0 10px 0 10px;"+
    			"  color: #424242; border: 1px solid #7dc6dd;"+minWidth;

		editSelect.append("<span id='span_es_"+ColNameToSave+"'><input type='hidden' name='"+ColNameToSave+"' id='"+ColNameToSave+"'/> <select class='"+styleClass+" form-control' id='editable_"+ColNameToSave+"' "
				+ "name='"+ColNameToSave+"_smarty_editable_list' "+Disabled+ " style='"+style+"' "+req+"> \n");
		String selectedItem="";
		
		if (colMapValues!=null){
			if (colMapValues.get(colName)!=null){
				Map <String , String> lookupsmap = colMapValues.get(colName);
				if (lookupsmap !=null){
					if (!lookupsmap.isEmpty()){
						for (String code : lookupsmap.keySet()){
							selectedItem = "";
							if (defValue!=null){
								if (!defValue.isEmpty())
									if (defValue.contains(code)){
										selectedItem="selected";
										DisabledValue = code;
									}
							}
							editSelect.append("<option value='"+code+"' "+selectedItem+">"
							+lookupsmap.get(code)+"</option> \n");
						}
					}else{
						editSelect.append("<option value='' "+selectedItem+">"+selectedItem+"</option>");
					}
				}else{
					for(int i=0; i<defValue.size() ; i++ )
						editSelect.append("<option value='"+defValue.get(i)+"' selected>"
					   +defValue.get(i)+"</option>");
				}
			}else{
				for  (int i=0; i<defValue.size() ; i++ )
					editSelect.append("<option value='"+defValue.get(i)+"' selected>"+defValue.get(i)+"</option>");
			}
		}else{
			for  (int i=0; i<defValue.size() ; i++ )
				editSelect.append("<option value='"+defValue.get(i)+"' selected>"+defValue.get(i)+"</option>");
		}
		
		editSelect.append("</select></span> \n");
		
		editSelect.append("<script>$('#editable_"+ColNameToSave+"').editableSelect({trigger:'manual'});</script>");
		return editSelect;
	}

}
