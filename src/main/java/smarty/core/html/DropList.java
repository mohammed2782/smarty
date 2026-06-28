package smarty.core.html;

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
			  boolean multiEdit,
			  HashMap<String,String> minValMap,
			  HashMap<String,String> maxValMap,
			  String htmlAttr) {
		StringBuilder DropDownHtml= new StringBuilder("");
		String Disabled = "";
		String backgroundColor=BGcolor;
		String DisabledValue ="";
		String ColNameToSave = colName;
		
		
    	String style    = "min-width: 120px; background-color:white";
    	String req="";
    	String styleClass="select2 form-control";
    	if (userDefinedColsHtmlType!=null && userDefinedColsHtmlType.containsKey(colName)) {
    		if (userDefinedColsHtmlType.get(colName).equalsIgnoreCase("CLASSIC_SELECT")) {
    			styleClass="form-control";
    		}
    	}
    	
    	if (required)
    		req= "required='required'";
    	if (Readonly !=null)
    		if (Readonly!="")
    			disabled_attr = true;// drop down don't have readonly , so we make it disabled, as a work around
    	
		if (disabled_attr){
			Disabled = "disabled='disabled'";
			backgroundColor="";
			ColNameToSave = "smarty_showonly_"+colName;
			style +=" color:black; background-color:#97999b; ";
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
			
		}
		
		
		if (multiEdit)
			DropDownHtml.append("<select class='"+styleClass+"' "+htmlAttr+" onchange=\"change_"+colName+"(this, '"+multiEditRowNum+"')\" id='"+ColNameToSave+"' "
					+ "name='"+ColNameToSave+"' "+Disabled+
						   " style='"+style+"' "+req+"> \n");
		else
			DropDownHtml.append("<select dir='rtl' "+htmlAttr+" class='"+styleClass+" form-control' id='"+ColNameToSave+"' "
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
			if (multiEdit)
				DropDownHtml.append("<input type='hidden' name ="+ColNameToSave+" value='"+DisabledValue+"'/> \n");
			else
				DropDownHtml.append("<input type='hidden' name ="+colName+" value='"+DisabledValue+"'/> \n");
		}
		
	/*	DropDownHtml.append("$('.single-select').select2({" + 
				"			theme: 'bootstrap4',\r\n" + 
				"			width: $(this).data('width') ? $(this).data('width') : $(this).hasClass('w-100') ? '100%' : 'style',\r\n" + 
				"			placeholder: $(this).data('placeholder'),\r\n" + 
				"			allowClear: Boolean($(this).data('allow-clear')),\r\n" + 
				"		});");*/
		return DropDownHtml;
	}

}
