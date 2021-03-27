package com.app.core.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HTMLFactory {
	public static final List<String> dateList = Arrays.asList("DATETIME", "DATE" , "TIMESTAMP");
	public static final List<String> TextList = Arrays.asList("VARCHAR");
	public static final List<String> numberList = Arrays.asList("BIGINT", "DOUBLE" , "INT");
	public static final List<String> blobList = Arrays.asList("TINYBLOB" , "BLOB" , "MEDIUMBOLB" , "LONGBLOB");
	
	public HTMLShapes getShape(
								String colName,
								HashMap <String , String > userDefinedColsHtmlType,
								HashMap <String , String > userDefinedLookups,
								String extraMultiEditName,
								int multiEditRowNum)
	{
		
		String txtAlign = "right";
		if (userDefinedColsHtmlType!=null && userDefinedColsHtmlType.get(colName)!=null &&
				userDefinedColsHtmlType.get(colName).equals("DROPLIST")){
			return new DropList(extraMultiEditName, multiEditRowNum,colName ,txtAlign );
		
		}else if (userDefinedColsHtmlType!=null && userDefinedColsHtmlType.get(colName)!=null &&
					userDefinedColsHtmlType.get(colName).equals("MULTILIST")){
			return new SelectMultiList(extraMultiEditName, multiEditRowNum,colName ,txtAlign);
		
		}else if (userDefinedColsHtmlType!=null && userDefinedColsHtmlType.get(colName)!=null &&
					userDefinedColsHtmlType.get(colName).equals("EDITABLE_SELECT")){
			return new EditableSelectList(extraMultiEditName,multiEditRowNum,colName ,txtAlign);
				
				
		}else if (userDefinedColsHtmlType!=null && userDefinedColsHtmlType.get(colName)!=null &&
				userDefinedColsHtmlType.get(colName).equals("PASSWORD")){
			return new Password(extraMultiEditName, multiEditRowNum,colName ,txtAlign);
				
		}else if (userDefinedColsHtmlType!=null && userDefinedColsHtmlType.get(colName)!=null &&
				userDefinedColsHtmlType.get(colName).equals("CHECKBOX")){
			return new CheckBox(extraMultiEditName,multiEditRowNum ,colName ,txtAlign);
		
		}else if (userDefinedColsHtmlType!=null && userDefinedColsHtmlType.get(colName)!=null &&
				userDefinedColsHtmlType.get(colName).equals("RADIO")){
			return new RadioButton(extraMultiEditName, multiEditRowNum,colName ,txtAlign);
			
		}else if (userDefinedColsHtmlType!=null && userDefinedColsHtmlType.get(colName)!=null &&
				(userDefinedColsHtmlType.get(colName).equals("TEXT")||userDefinedColsHtmlType.get(colName).equals("TEXTAREA"))){
	    	
	    	if (userDefinedColsHtmlType.get(colName).equalsIgnoreCase("TEXTAREA")){
	    		return new TextArea(extraMultiEditName, multiEditRowNum,colName,txtAlign);
	    	}else{
	    		return new TextField(extraMultiEditName, multiEditRowNum,colName, txtAlign);
	    	}
		
		}else if (userDefinedColsHtmlType!=null && userDefinedColsHtmlType.get(colName)!=null && 
				 dateList.contains(userDefinedColsHtmlType.get(colName))){
			return new DateField(extraMultiEditName, multiEditRowNum,colName,txtAlign);
		
		}else  if (userDefinedColsHtmlType!=null && userDefinedColsHtmlType.get(colName)!=null && 
				 userDefinedLookups!=null &&  userDefinedLookups.get(colName) !=null){
			return new DropList(extraMultiEditName, multiEditRowNum,colName, txtAlign);
			
		}else if (	userDefinedColsHtmlType!=null && userDefinedColsHtmlType.get(colName)!=null &&
				 (	(numberList.contains(userDefinedColsHtmlType.get(colName))) || userDefinedColsHtmlType.get(colName).equals("NUMBER") )
				 ){
	    	return new NumberField(extraMultiEditName , multiEditRowNum ,colName , txtAlign);
	    	
		}else if (userDefinedColsHtmlType!=null && userDefinedColsHtmlType.get(colName)!=null && (TextList.contains(userDefinedColsHtmlType.get(colName)))) {
	    	return new TextField(extraMultiEditName , multiEditRowNum ,colName , txtAlign);
		
		}else if (userDefinedColsHtmlType!=null && userDefinedColsHtmlType.get(colName)!=null && 
				(blobList.contains(userDefinedColsHtmlType.get(colName)) || userDefinedColsHtmlType.get(colName).equalsIgnoreCase("IMAGE"))) {
			//System.out.println("colNameExtraMultiEditName-----------------"+colNameExtraMultiEditName);
			return new UploadButton(extraMultiEditName, multiEditRowNum ,colName, txtAlign);
		
	    }else{
	    	return new TextField(extraMultiEditName , multiEditRowNum ,colName, txtAlign);
	    	//return "undefined Column Type,so smarty don't know what to display,for type=>"+userDefinedNewColsHtmlType.get(ColName);
	    }
	}
}
