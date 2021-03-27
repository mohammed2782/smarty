package com.app.core.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class HTMLShapes {
	protected  String textAlign = "right";
	protected String colName  = "";
	protected String extraMultiEditName="";
	protected int multiEditRowNum=0;
	protected String shapeClass ="form-control col-md-7 col-xs-12";
	public HTMLShapes(String extraMultiEditName , int multiEditRowNum , String colName , String txtAlign){
		this.textAlign = txtAlign;
		this.colName = colName;
		this.extraMultiEditName = extraMultiEditName;
		this.multiEditRowNum = multiEditRowNum;
	}
	
	public abstract StringBuilder getHtmlInput(
						  HashMap<String , Integer> sqlColsSizes , 
						  HashMap <String , String > userDefinedColsHtmlType,
						  LinkedHashMap<String ,  LinkedHashMap<String , String>> colMapValues, 
						  ArrayList<String> defValue ,
						  String Readonly,
						  boolean disabled_attr,
						  String BGcolor,
						  boolean hidden,
						  boolean required,
						  boolean multiEdit);
}
