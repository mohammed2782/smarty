package com.app.core.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DropList extends HTMLShapes {
	
	public DropList(String extraMultiEditName, int multiEditRowNum,String colName, String txtAlign) {
		super(extraMultiEditName,multiEditRowNum , colName, txtAlign);
	}

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
		StringBuilder DropDownHtml= new StringBuilder("");
		String Disabled = "";
		String backgroundColor=BGcolor;
		String DisabledValue ="";
		String ColNameToSave = colName;
		
    	String style    = "";
    	String req="";
    	String styleClass="select2_single";
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
		
		if (multiEdit)
			DropDownHtml.append("<select class='"+styleClass+" form-control' onchange=\"change_"+colName+"(this, '"+multiEditRowNum+"')\" id='"+ColNameToSave+"' "
					+ "name='"+ColNameToSave+"' "+Disabled+
						   " style='"+style+"' "+req+"> \n");
		else
			DropDownHtml.append("<select class='"+styleClass+" form-control' id='"+ColNameToSave+"' "
					+ "name='"+ColNameToSave+"' "+Disabled+
						   " style='"+style+"' "+req+"> \n");
		String selectedItem="";
		
		//if (!required)
			DropDownHtml.append("<option value=''></option> \n");
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
							DropDownHtml.append("<option value='"+code+"' "+selectedItem+">"
							+lookupsmap.get(code)+"</option> \n");
						}
					}else{
						DropDownHtml.append("<option value='' "+selectedItem+">"+selectedItem+"</option>");
					}
				}else{
					for(int i=0; i<defValue.size() ; i++ )
						DropDownHtml.append("<option value='"+defValue.get(i)+"' selected>"
					   +defValue.get(i)+"</option>");
				}
			}else{
				for  (int i=0; i<defValue.size() ; i++ )
					DropDownHtml.append("<option value='"+defValue.get(i)+"' selected>"+defValue.get(i)+"</option>");
			}
		}else{
			for  (int i=0; i<defValue.size() ; i++ )
				DropDownHtml.append("<option value='"+defValue.get(i)+"' selected>"+defValue.get(i)+"</option>");
		}
		
		DropDownHtml.append("</select> \n");
		if (Disabled!=""){
			DropDownHtml.append("<input type='hidden' name ="+colName+" value='"+DisabledValue+"'/> \n");
		}
		return DropDownHtml;
	}

}
