package com.app.core.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class UploadButton extends HTMLShapes{

	public UploadButton(String extraMultiEditName, int multiEditRowNum ,String colName, String txtAlign) {
		super(extraMultiEditName , multiEditRowNum, colName, txtAlign);
	}

	@Override
	public StringBuilder getHtmlInput(HashMap<String, Integer> sqlColsSizes,
			HashMap <String , String > userDefinedColsHtmlType, 
			LinkedHashMap<String, LinkedHashMap<String, String>> colMapValues,
			ArrayList<String> defValue, String Readonly, boolean disabled_attr,
			String BGcolor, boolean hidden,boolean required,boolean multiEdit) {
		// TODO Auto-generated method stub
		/*
		 * <div class="bootstrap-filestyle input-group">
<input class="form-control " type="text" disabled="" placeholder="">
<span class="group-span-filestyle input-group-btn" tabindex="0">
<label class="btn btn-default " for="filestyle-2">
<span class="icon-span-filestyle glyphicon glyphicon-folder-open"></span>
<span class="buttonText">Find file</span>
</label>
</span>
</div>
		 */
		String req="";
    	if (required)
    		req= "required='required'";
    	
    	String placeHolder = "";
    	if (defValue!=null && defValue.size()>0)
    		if (defValue.get(0)!=null && !defValue.get(0).isEmpty()){
    			req ="";
    			if (defValue.size()>1)
    				placeHolder = defValue.get(1);
    		}
    	//System.out.println(colName+"========="+placeHolder);
		StringBuilder sb = new StringBuilder();
		String colNameToSave=colName;
		if (multiEdit)
			colNameToSave += extraMultiEditName;
		sb.append( "<div><a class='close fileinput-exists' onclick=\"clearFieStyleSelector('"+colNameToSave+"','"+placeHolder+"' , '"+required+"');\" style='float: none' data-dismiss='fileinput' href='#'>×</a>"
				+ "<input type='file' name='"+colNameToSave+"' id='"+colNameToSave+"' data-placeholder='"+placeHolder+"'"
						+ "class='filestyle' data-classButton='btn btn-primary' data-size='sm'"
				+ " data-classIcon='icon-plus' data-buttonText='Upload file.' "+req+" >"
				+ ""
				+ "</div>");
		return sb;
	}

}
