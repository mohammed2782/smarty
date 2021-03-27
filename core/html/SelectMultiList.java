package com.app.core.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SelectMultiList extends HTMLShapes{

	public SelectMultiList(String extraMultiEditName, int multiEditRowNum,String colName, String txtAlign) {
		super(extraMultiEditName, multiEditRowNum , colName, txtAlign);
	}
	@Override
	public StringBuilder getHtmlInput(HashMap<String, Integer> sqlColsSizes,
			HashMap<String, String> userDefinedColsHtmlType,
			LinkedHashMap<String, LinkedHashMap<String, String>> colMapValues, ArrayList<String> defValue,
			String Readonly, boolean disabled_attr, String BGcolor, boolean hidden, boolean required
			,boolean multiEdit) {
		// TODO Auto-generated method stub
		
		String Disabled = "";
		String backgroundColor=BGcolor;
		String DisabledValue ="";
		String colNameToSave = colName;
    	String style    = "";
    	String req="";
    	String disabledForReadOnly = "";
		if (required)
    		req= "required='required'";
    	if (Readonly !=null && !Readonly.equalsIgnoreCase("")) {
    			//disabled_attr = true;// drop down don't have readonly , so we make it disabled, as a work around
    			backgroundColor="#E9E5E5;";
    			style = " background-color:"+backgroundColor+"; ";
    			disabledForReadOnly ="disabled";
    			
    		}
		if (disabled_attr){
			Disabled = "disabled='disabled'";
			backgroundColor="#E9E5E5;";
			colNameToSave = "smarty_showonly_"+colName;
		}
		if (multiEdit)
			colNameToSave += extraMultiEditName;
		StringBuilder html = 
				new StringBuilder("<select id='"+colNameToSave+"' name='"+colNameToSave+"'"
						+ " class='form-control' multiple='multiple' "+Readonly+" "+style+"");
		html.append("<option value=''></option> \n");
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
							html.append("<option value='"+code+"' "+selectedItem+" "+disabledForReadOnly+">"+lookupsmap.get(code)+"</option> \n");
						}
					}else{
						html.append("<option value='' "+selectedItem+" "+disabledForReadOnly+">"+selectedItem+"</option>");
					}
				}else{
					for(int i=0; i<defValue.size() ; i++ )
						html.append("<option value='"+defValue.get(i)+"' selected "+disabledForReadOnly+">"+defValue.get(i)+"</option>");
				}
			}else{
				for (int i=0; i<defValue.size() ; i++ )
					html.append("<option value='"+defValue.get(i)+"' selected "+disabledForReadOnly+">"+defValue.get(i)+"</option>");
			}
		}else{
			for (int i=0; i<defValue.size() ; i++ )
				html.append("<option value='"+defValue.get(i)+"' selected "+"+disabledForReadOnly+"+">"+defValue.get(i)+"</option>");
		}
		html.append("</select> \n");
		if (Disabled!=""){
			html.append("<input type='hidden' name ="+colNameToSave+" value='"+DisabledValue+"'/> \n");
		}
		return html;
	}

}
