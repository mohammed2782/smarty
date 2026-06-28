package smarty.core.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RadioButton extends HTMLShapes{

	public RadioButton(String extraMultiEditName, int multiEditRowNum ,String colName, String txtAlign) {
		super(extraMultiEditName ,  multiEditRowNum , colName, txtAlign);
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
			boolean multiEdit,
			HashMap<String,String> minValMap,
			HashMap<String,String> maxValMap,
			String htmlAttr) {
		StringBuilder RadioButtonHTML= new StringBuilder("");
		String Disabled = "";	
		String DisabledValue ="";
		String colNameToSave = colName;
    	if (Readonly !=null)
    		if (Readonly!="")
    			disabled_attr = true;// drop down don't have readonly , so we make it disabled, as a work around
    	
		if (disabled_attr){
			Disabled = "disabled='disabled'";
			colNameToSave = "smarty_showonly_"+colName;
		}
		
		if (multiEdit)
			colNameToSave += extraMultiEditName;
		
		String req="";
    	if (required)
    		req= "required='required'";
		/*<label>
        <input type="radio" class="flat" checked name="iCheck"> Checked
      </label>*/
		/*RadioButtonHTML.append("<div class='btn-group' data-toggle='buttons' id='"+ColNameToSave+"' "
								+"name='"+ColNameToSave+"' "+Disabled);*/
		String selectedItem="";
		//RadioButtonHTML.append("<option value=''></option> \n");
		if (colMapValues!=null && colMapValues.get(colName)!=null){
			Map <String , String> lookupsmap = colMapValues.get(colName);
			if (lookupsmap !=null && !lookupsmap.isEmpty()){
				if (!required)
					RadioButtonHTML.append("<label>"
							+ "<input type='radio' id='"+colNameToSave+"' class='flat'"
							+ " "+selectedItem+" value='' name='"+colNameToSave+"'></label><span style='maring-left:5px;margin-right:5px;'></span>");	
				for (String code : lookupsmap.keySet()){
					selectedItem = "";
					if (defValue!=null && !defValue.isEmpty())
						if (defValue.contains(code)){
							selectedItem="checked";
							DisabledValue = code;
						}
					RadioButtonHTML.append("<label>"
							+ "<input type='radio' id='"+colNameToSave+"' class='flat'"
							+ " "+selectedItem+" value='"+code+"' "+req+" name='"+colNameToSave+"'> "
							+lookupsmap.get(code)+"</label><span style='maring-left:5px;margin-right:5px;'></span>");	
				}
			}
		}
		if (Disabled!=""){
			RadioButtonHTML.append("<input type='hidden' name ="+colNameToSave+" value='"+DisabledValue+"'/> \n");
		}
		return RadioButtonHTML;
	}

}
