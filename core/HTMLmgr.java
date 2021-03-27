package com.app.core;


import com.app.core.html.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HTMLmgr {
	public String APPPATH;
	private Map<String , String > userDefinedNewColsHtmlType;
	private SqlMgr mysqlmgr;
	private String myClassBean;
	private String resourcesLocation = "smartyresources";
	//flags 
	//change to true if you want the page to be deleted,now why would you do that , if the deleted record may impact
	//the screen , may be another table with parent child relationshi
	public boolean refreshPageOnDelete=false; 
	public boolean refreshPageOnInsert=false;
	public boolean refreshPageOnUpdate=false;
	
	public static final List<String> numberList = Arrays.asList("BIGINT", "DOUBLE" , "INT");
	// HTML classes for tags
	public String tableClass = "table table-striped  table-bordered jambo_table";
	public String trClass ="pointer";
	
	public String getMyClassBean() {
		return myClassBean;
	}

	public void setMyClassBean(String myClassBean) {
		this.myClassBean = myClassBean;
	}

	public SqlMgr getMysqlmgr() {
		return mysqlmgr;
	}

	public void setMysqlmgr(SqlMgr mysqlmgr) {
		this.mysqlmgr = mysqlmgr;
	}

	public Map<String, String> getUserDefinedNewColsHtmlType() {
		return userDefinedNewColsHtmlType;
	}

	public void setUserDefinedNewColsHtmlType(
			Map<String, String> userDefinedNewColsHtmlType) {
		this.userDefinedNewColsHtmlType = userDefinedNewColsHtmlType;
	}

	public String getAPPPATH() {
		return APPPATH;
	}

	public void setAPPPATH(String aPPPATH) {
		APPPATH = aPPPATH;
	}

	
	private String mustFillBGColor = "#FFFFB8";
	private String normalBGColor   = "#F0FFF0";
	private String textAlign = "left";
	private JSMgr jsmgr;
	/* user may select from the following 
	 * DROPLIST -- drop down list
	 * RADIOBUTTON -- Radio Button
	 */
	// constructor
	public HTMLmgr(){
		//APPPATH = path;
		//System.out.println("string path=>"+APPPATH);
	}
	
	public JSMgr getJsmgr() {
		return jsmgr;
	}

	public void setJsmgr(JSMgr jsmgr) {
		this.jsmgr = jsmgr;
	}
	
	
	public StringBuilder getCaptionTableAndHeaders(String userDefinedCaption 		, String userDefinedCapationWithOutHtml, boolean canEdit, 
			   boolean canDelete         					, boolean canNew,
			   int currentpage			 					, ArrayList<String> userDefinedGridCols , 
			   Map<String , String> userDefinedColLabel     , String sortColName,
			   String keyCol								, String sortMode,
			   boolean editableGrid							, boolean isExport,
			   boolean wordExport							, boolean pdfExport,
			   ArrayList <String> userDefinedExportCols		, ArrayList <String> userDefinedArabicCols,
			   boolean userDefinedExportLandScape 			, HashMap<Integer ,HashMap<String,String>> MultiRowDisplayValuesForExport,
			   int noOfRows 								, boolean userDefinedHideRowNumInGrid,
			   boolean haveGrouping							, boolean haveSumCols,
			   String userDefined_x_panelclass				, String UserDefindEditFormEnctype,
			   int userDefinedPageRows){
				StringBuilder view = new StringBuilder();
				String data = "";
				String prevPage ="" , colname ="" ;
				view.append("<div class='row'>"
				+ "<div class='col-md-12 col-sm-12 col-xs-12'>"
				+ "<div class='x_panel "+userDefined_x_panelclass+"'>");
				if (editableGrid) {
					view.append("<form name=\""+myClassBean+"\" action='?myClassBean="+myClassBean+"&upd=1' method='POST' "+UserDefindEditFormEnctype+"> \n");//Form
					
				}
				view.append("<div class='x_title'>");
				
				view.append("<ul class='nav navbar-right col-md-9'><li><h5>"+userDefinedCaption+"</h5></li></ul>"
							+ "<ul class='nav navbar-left panel_toolbox'>");
				
				
				if (canNew) 
					view.append("<li  style='padding-left:10px;padding-right:10px;'>"+getNewButton(myClassBean ,editableGrid )+"</li>&nbsp;&nbsp;&nbsp;");
				
				//which is stil wrong, we have to find solution 2 - jan - 2020
				if (isExport){ //editable grid is a porblem as it will make form inside form so we seperate it in another function // which is stil wrong, we have to find solution 2 - jan - 2020
					if (wordExport || pdfExport){
						String val="";
						view.append("<li><form action='../DocumentWriter?myClassBean="+this.myClassBean+"' method='POST'>");
						view.append("<input type='hidden' name='smarty_userDefinedExportLandScape' value='"+userDefinedExportLandScape+"' />");
						String htmlSection ="";
						for (Integer rowNum : MultiRowDisplayValuesForExport.keySet()){
							for (String col : MultiRowDisplayValuesForExport.get(rowNum).keySet()){
								if (!mysqlmgr.isFile(col)){
									try {
										val = "";
										if (MultiRowDisplayValuesForExport.get(rowNum).containsKey(col)
												&& MultiRowDisplayValuesForExport.get(rowNum).get(col)!=null)
											val = MultiRowDisplayValuesForExport.get(rowNum).get(col);
						
										data = URLDecoder.decode(val , "UTF-8");
										data = data.replaceAll("<td>","");
										data = data.replaceAll("</td>","");
										if (data.contains("<")|| data.contains(">")) {
											while(data.contains("<")|| data.contains(">")) {
												htmlSection = data.substring(data.indexOf("<"), data.indexOf(">")+1);
												data= data.replaceAll(htmlSection, " ");
											}
											data = data.trim();
											String[] parsedData = data.split(" ");
											data = "";
											for (int i =0; i<parsedData.length; i++) {
								
												if (!parsedData[i].trim().equalsIgnoreCase("")) {
													if(i>0)
														data +=", ";
													data +=parsedData[i];
												}
											}
										}
									}catch (UnsupportedEncodingException e) {
										e.printStackTrace();
									}
									view.append("<input type='hidden' name='smartyrownum_"+rowNum+"_smartycol_"+col+"' "
											+ "value=\""+data+"\" >");
								}
							}
						}
				if (editableGrid)
					view.append("<input type='hidden' name='userDefinedCaption' value='"+userDefinedCapationWithOutHtml+"'>");
				else
					view.append("<input type='hidden' name='userDefinedCaption' value='"+userDefinedCaption+"'>");
				
				view.append("<input type='hidden' name='smartycoltoexport' value='");
				for (int i=0; i<userDefinedExportCols.size(); i++){
					if (!mysqlmgr.isFile(userDefinedExportCols.get(i)))
						view.append(userDefinedExportCols.get(i)+",");
				}
				view.append("' >");
				
				if (!userDefinedArabicCols.isEmpty()){
					view.append("<input type='hidden' name='arabicsmartycoltoexport' value='");
					for (int i=0; i<userDefinedArabicCols.size(); i++){
						if (i>0)
							view.append(",");
						view.append(userDefinedArabicCols.get(i));
					}
				
					view.append("' >");
				}
				for (int i=0; i< userDefinedExportCols.size(); i++){
					if (!mysqlmgr.isFile(userDefinedExportCols.get(i)))
						view.append("<input type='hidden' name='"+userDefinedExportCols.get(i)+"_collabel' "
								+ "value='"+userDefinedColLabel.get(userDefinedExportCols.get(i))+"' >");
				
				}
				
				view.append("<input type='hidden' name='UserDefinedPageRows' value='"+noOfRows+"' >");
				if (pdfExport)
					view.append("<li><button type='submit' value='pdf' class='btn btn-danger btn-sm'  name='pdf'><li class='fa fa-file-pdf-o fa-lg' aria-hidden='true'></li></button></li>");
				if (wordExport)
					view.append("<button type='submit' value='pdf' class='btn btn-primary btn-sm' name='docx' ><li class='fa fa-file-word-o fa-lg' aria-hidden='true'></li></button>");
				view.append("</form></li>");
					}
				}
				
				if (currentpage-1 >=1)
				prevPage = getPreviousButton(myClassBean,currentpage-1);
				
				if (userDefinedPageRows>noOfRows)
				view.append( "<li style='padding-top:7px;padding-left:15px;padding-right:5px;'>"+currentpage+"</li><li>"+prevPage+"</li>");
				else
				view.append( "<li style='padding-left:15px;padding-right:5px;'>"+getNextButton(myClassBean,currentpage+1)+"</li><li style='padding-top:7px;padding-left:5px;padding-right:5px;'>"+currentpage+"</li><li style='padding-left:5px;padding-right:15px;'>"+prevPage+"</li>");
				if (editableGrid) {
				view.append("<li><button type='submit' style ='margin-left:20px;padding:10px;margin-top:0px;color:black;' class='fa fa-save fa-lg' value='save'/></li>");
				}
				view.append("</ul>");
				view.append("<div class='clearfix'></div></div>");
				view.append("<div class='x_content'>");
				
				view.append("<div class='table-responsive'>"
				+"<table id='smarty_table_"+myClassBean.replace(".", "_dot_")+"' class='"+tableClass+"'>");
				if (!haveGrouping)
				view.append(getHeaderRow( userDefinedHideRowNumInGrid,  keyCol , 
				editableGrid,   canDelete ,   canEdit,
				haveGrouping,  haveSumCols,
				userDefinedGridCols,
				userDefinedColLabel,
				sortColName, sortMode  , false , null));
				
				return view;
	}
	public StringBuilder getHeaderRow(
			boolean userDefinedHideRowNumInGrid, String keyCol , 
			boolean editableGrid,  boolean canDelete ,  boolean canEdit,
			boolean haveGrouping, boolean haveSumCols,
			ArrayList<String> userDefinedGridCols,
			Map<String,String> userDefinedColLabel,
			String sortColName,String sortMode,
			boolean slidingRow , String groupTitle){
		String colname;
		//System.out.println("in get header sildingRow=>"+slidingRow);
		String style = "style=\"display: none;\"";
		
		if (!slidingRow){
			style="";
			groupTitle ="";
		}
		StringBuilder header = new StringBuilder("<tr class='headings' "+style+" "+groupTitle+" >");
		if (!userDefinedHideRowNumInGrid)
			header.append("<th class='column-title'></th>");
		/* i stopped this bcoze it fucks up the grouping when hide row num
		 * else{
			if (haveGrouping && haveSumCols){ // show the td for the total grouping
				header.append("<th class='column-title'></th>");
			}
		}*/
		for (String key : userDefinedGridCols){
		    colname = userDefinedColLabel.get(key);
			if (colname==null)
			    colname=key;
			
			if (sortColName.equals(key)){
				header.append("<th class='column-title'>"+colname+"&nbsp;"+getSortingButton(myClassBean,key,sortMode)+"</th>");
			}else{
				header.append("<th class='column-title'>"+colname+"&nbsp;"+getSortingButton(myClassBean,key,"normal")+"</th>");
			}
		}
		header.append(getUpdateDelete( keyCol , editableGrid,  canDelete ,  canEdit , true , "",""));
		header.append("</tr>");
		return header;
	}
	
	public StringBuilder getUpdateDelete(String keyCol 	  ,boolean editableGrid, 
										boolean canDelete ,boolean canEdit , 
										boolean isTH	  ,String keyColVal,
										String userDefineddltConfirmMsg){
		StringBuilder view = new StringBuilder(); 
		String th_td ="td";
		if (keyCol!=null && !keyCol.isEmpty()){
			if (isTH){
				th_td = "th";
				if (editableGrid){
					if (canDelete)
						view.append("<"+th_td+" align ='center'></"+th_td+">");
				 }else if(canEdit || canDelete )
					 view.append("<"+th_td+" align ='center'></"+th_td+">");
			}else{
				if (!editableGrid){
					if(canEdit || canDelete){
						view.append("<"+th_td+" align ='center'>");
						if (canDelete)
							view.append(getDeleteButton(myClassBean ,keyCol,(keyColVal) , userDefineddltConfirmMsg));
						
						if (canEdit)
							view.append(getUpdateButton(myClassBean , keyCol,(keyColVal)));
						view.append("</"+th_td+">");
					}
					 
				}else{
					if (canDelete){
						view.append("<"+th_td+" align ='center'>");
						view.append(getDeleteButton(myClassBean ,keyCol,(keyColVal) , userDefineddltConfirmMsg));
						view.append("&nbsp;&nbsp;&nbsp;</"+th_td+">");
					}
				}
			}
		}
		return view;
	}
	
	public String getDataTR(String GroupTitle  , String rowDisplay,String keyCol,boolean clickableRow , String userDefinedGlobalClickRowID){
		String clickableRowCode = "";
		if (clickableRow && !keyCol.isEmpty())
			clickableRowCode =jsmgr.getClickableRowCode(userDefinedGlobalClickRowID,keyCol);
		return "<tr  smartyKeyColVal='"+keyCol+"' class="+trClass+" "+GroupTitle+" "+clickableRowCode+" "+rowDisplay+">";
	}
	public StringBuilder getDebugSqlArea(){
		StringBuilder view= new StringBuilder();
		String backgroundColor="#E9E5E5;";
		view.append("<div align='center' ><textarea name='debug' readonly align='center'  width ='100%' style='background-color:"
						+backgroundColor+"' >"+mysqlmgr.getExecutedSQL()+"</textarea></div>");
		return view;
	}
	
	/*
	 * Gen Sorting Buttons.
	 */
	public  String getSortingButton(String myClassBean,String ColName,String currentSorting){
		String html="";
		String img ="";
		String mode="";
		String img_opp ="";
		if(currentSorting.equals("normal")){
			img = "sort_norm_ori.gif";//sort_norm_ori.gif
			img_opp = "sort_asc.gif";
			mode="asc";
			
		}else if(currentSorting.equals("desc")){
			img = "sort_dsc.gif";
			img_opp ="sort_asc.gif";
			mode="asc";
		}else if(currentSorting.equals("asc")){
			img = "sort_asc.gif";
			img_opp ="sort_dsc.gif";
			mode="desc";
		}	
		
		String onMouseOver = "onmouseover=\"this.src='"+APPPATH+"/"+resourcesLocation+"/img/"+img_opp+"'\"";
		String onMouseOut  = "onmouseout=\"this.src='"+APPPATH+"/"+resourcesLocation+"/img/"+img+"'\"";
		html=html+"<a href='?myClassBean="+myClassBean+"&sortingby="+ColName+"&sortmode="+mode+"'>" +
				"<img src='"+APPPATH+"/"+resourcesLocation+"/img/"+img+"'"+
				onMouseOver+" "+ onMouseOut +"height =11 width=8 border=0></img></a>";
		
		return html;
	}
	
	/*
	 * Gen Previous Page Button.
	 */
	public  String getPreviousButton(String myClassBean,int pageNo){
		String html="<a href=?myClassBean="+myClassBean+"&page="+pageNo+"><i class='fa fa-caret-right'></i></a>";
		return html;
	}
	/*
	 * Gen Next Page Button.
	 */
	public  String getNextButton(String myClassBean,int pageNo){
		String html="<a href=?myClassBean="+myClassBean+"&page="+pageNo+"><i class='fa fa-caret-left'></i></a>";
		return html;
	}
	
	/*
	 * Gen Update Button
	 */
	public  String getUpdateButton(String myClassBean,String idname,String ID){
		String html="<a  href='?myClassBean="+myClassBean+"&"+idname+"="+ID+"&op=upd' "
				+ "class='btn btn-edit btn-xs'><li class='fa fa-pencil'></li></a>";
		return html;
	}
	/*
	 * Gen Delete Button
	 */
	public  String getDeleteButton(String myClassBean,String idname, String ID , String dltCofrmMsg){
		String html = "";
		html="<button type='button'  onclick=\"link=false; var rs =doDeleteSmarty(this,'"+dltCofrmMsg+"' ,'"+idname+"','"+ID+"' , '"+myClassBean+"' ); "
			+ "return rs;\" class='btn btn-danger btn-xs' ><li class='fa fa-trash'></li>" +
			"</button>";
		return html;
	}
	public String getDownloadFileButton(String fileCol, String keyVal , String myClassBean){
		String fileDownloaderBtn="";
		if (keyVal!=null && !keyVal.isEmpty() && !keyVal.trim().equalsIgnoreCase(""))
		fileDownloaderBtn = "<a href='../../FileDownloader?className="+myClassBean+"&fileCol="+fileCol+"&keyVal="+keyVal+"'>"
				+ "<li class='fa fa-download fa-lg' aria-hidden='true'></li></a>";
		return fileDownloaderBtn;
	}
	
	/*
	 * Gen New Form Button.
	 */
	public  String getNewButton(String myClassBean, boolean editableGrid){
		String html = "";
		if (editableGrid)
			html="<a href='?op=new&smarty_newformbtn=newform&myClassBean="+myClassBean+"' >"
					+"<button type='button'  class='btn btn-dark btn-sm fa fa-plus fa-lg' style='margin-right:0px;padding:9px;'>"
					+ "</button></a>";
		else
			html="<form action='?' method='post'><input type='hidden' name='op' value='new'><input type='hidden' name='myClassBean' value='"+myClassBean+"'>"
				+ "<button type='submit' class='btn btn-dark btn-sm' name='smarty_newformbtn' value='newform'><li class='fa fa-plus'></li></button></form>";
	
		return html;
	}
	/*
	 * Generate Search Form
	 */
	public StringBuilder genrateFilter(ArrayList<String>userDefinedFilterCols, 
								HashMap <String , String > coltoName, 
								HashMap <String , String > userDefinedFilterColsHtmlType, 
								HashMap<String, String[]> fltr_vals,
								LinkedHashMap<String , LinkedHashMap<String , String>> colMapValues,
								LinkedHashMap<String , LinkedHashMap<String , String>> filterColMapValues,
								HashMap<String , Integer> sqlColsSizes,
								String myClassBean,
								HashMap <String , String > userDefinedLookups,
								ArrayList<String>userDefinedColsMustFillFilter,
								HashMap<String , String> allSqlColsTypes
								){
		if (userDefinedFilterCols.isEmpty()){
			return new StringBuilder("");
		}
		int i =1;
		
		ArrayList<String> defValue = new ArrayList<String>();
		LinkedHashMap <String , LinkedHashMap<String , String>> lookupMapVal = colMapValues;
		StringBuilder InnerFilterHtml= new StringBuilder("");
		String BackGroundColor="";
		String jsValidatorMustFill ="";
		String jsValidatorNumeric ="";
		String formName = "smartyFilter_"+myClassBean;
		String colSpan = "";
		boolean required=false;

		StringBuilder filters = new StringBuilder("<div class='row'><div class='col-md-1 col-sm-1 col-xs-1'></div>"
				+ "<div class='col-md-12 col-sm-12 col-xs-12'><div class='x_panel'><div class='x_title'><h2></h2>"
				+ "<ul class='nav navbar-right panel_toolbox'>"
				+ "<li><a class='collapse-link'><i class='fa fa-chevron-up'></i></a></li></ul>");
		filters.append("<div class='clearfix'></div></div><div class='x_content content-search'><br />");
		//String filters ="<table align=\"center\" class = 'smartyBox' id='smarty_filter_"+myClassBean+"'><tr><td><div>" +
		filters.append("<form name=\""+formName+"\" action=\"?filter=1\" method=\"post\" "
				+ "data-parsley-validate class='form-horizontal form-label-left' >");
		//if (userDefinedFilterCols.size() < 2)
			//colSpan= "colspan='2'";
		
		for (String key :userDefinedFilterCols)
			{  	
				required = false;
				if (userDefinedColsMustFillFilter.contains(key)){
					required = true;
				}
			    if (i==1 ){
			    	InnerFilterHtml.append("<div class='form-group'>");
			    }
			    defValue.clear();
			    if (fltr_vals!=null){
				    if (fltr_vals.get(key)!=null){
				    	for (int jj =0; jj<fltr_vals.get(key).length;jj++){
				    		defValue.add(fltr_vals.get(key)[jj]);
				    	}
				    }
			    }
			    if (userDefinedColsMustFillFilter.contains(key)){//check for the must fill
					BackGroundColor=mustFillBGColor;
				}else{
					BackGroundColor=normalBGColor;
				}
			    if (filterColMapValues !=null){
				    if (filterColMapValues.containsKey(key)){
				    	lookupMapVal = filterColMapValues;
				    	//userDefinedLookups.put(key, "fake");
				    }else{
				    	lookupMapVal = colMapValues;
				    }
			    }else{
			    	lookupMapVal = colMapValues;
			    }
			    //System.out.println("lookupMapVal====>"+lookupMapVal);
			    InnerFilterHtml.append("<label class='control-label col-md-1 col-sm-1 col-xs-12' >"+coltoName.get(key));
			    if(required)
			    	InnerFilterHtml.append("<span class='required'>*</span>");
			    InnerFilterHtml.append("</label>");
			    InnerFilterHtml.append("<div class='col-md-3 col-sm-3 col-xs-12'>");
			    InnerFilterHtml.append( GetHtmlInput(userDefinedFilterColsHtmlType, lookupMapVal 		, key,
			    									   defValue 				    , sqlColsSizes 	    , "",
			    									   false/*for now*/ 		    , userDefinedLookups, BackGroundColor, 
			    									   false					    , null				, required,
			    									   false,
			    									   0));
			    InnerFilterHtml.append("</div>");
			    if (numberList.contains(allSqlColsTypes.get(key))){
					jsValidatorNumeric = jsValidatorNumeric +jsmgr.genJSNumericValidation(key , coltoName.get(key));
				}
			    if (i==3 ){
			    	InnerFilterHtml.append("</div>");
				    i=0;
			    }
			    i++;
			    InnerFilterHtml.append(genHotLookupsjs(key , userDefinedLookups , userDefinedFilterColsHtmlType , required));
		    }
		if (i>1)
			InnerFilterHtml.append("</div>");
		InnerFilterHtml.append("<input type='hidden' name='myClassBean' value='"+myClassBean+"'/>");
		InnerFilterHtml.append("<div class='ln_solid'></div>");
		InnerFilterHtml.append(" <div class='form-group'>");
		InnerFilterHtml.append("<div class='col-md-5 col-sm-5 col-xs-12 col-md-offset-5'>");
		/*InnerFilterHtml.append("<input type='submit' class='btn btn-primary' smartyStat='init' "
				+ " id='smarty_cancelFilter' value='cancel' type='submit' name='cancelfilter_"+myClassBean+"'"
				+ " onclick=\"javascript:window.location.href='?'; return false;\" value='Ø£Ù„ØºØ§Ø¡' />");
		*/
		InnerFilterHtml.append("<button type='submit' id='"+myClassBean+"' name=\"dosearch\" value=\"Search\" class='btn btn-success btn-xs'>Search</button>");
		InnerFilterHtml.append("&nbsp;&nbsp;&nbsp;<button id='smarty_cancelFilter' type='submit' name='cancelfilter_"+myClassBean+""
				+ "' onclick=\"this.smartyStat='cancelfilter'\" class='btn btn-warning btn-xs' smartyStat='init' value='Cancel'/>Reset Filter</button>");
		InnerFilterHtml.append("</div>");
		InnerFilterHtml.append("</div>");
		InnerFilterHtml.append("</form>");//End of Form								 	
		filters.append(InnerFilterHtml); 
		//filters.append(jsmgr.genValudationJS(formName , userDefinedColsMustFillFilter));
		filters.append ("<script> var frmvalidator  = new Validator('"+formName+"');\n");
		filters.append(jsValidatorNumeric);
		for (String param : jsmgr.colsUsedInLookupsForOtherCols.keySet()){
			filters.append(jsmgr.getHotLookupCallingScript(param));
		}
		filters.append("</script>");
		filters.append("</div></div></div></div>");
		return filters;
	}
	
	
	public String genHotLookupsjs (String keyCol , HashMap<String,String> userDefinedLookups , 
			HashMap<String,String> userDefinedNewColsHtmlType , boolean required){
		String js="<script>";
		String lookup = userDefinedLookups.get(keyCol);
		if (lookup!=null){
			if (lookup.startsWith("!")){
				js = js+jsmgr.genHotLookup(lookup , keyCol , userDefinedNewColsHtmlType , userDefinedLookups , required);
			}
		}
		
		js += jsmgr.getHotLookupCallingScript(keyCol);
		
		js = js+"</script>";
		return js;
	}
	
	/*
	 * Gen HTML input
	 *  TO-DO : enhance this method to use Polymorphisim in generating the html input.
	 */
	public StringBuilder GetHtmlInput(HashMap <String , String > userDefinedColsHtmlType , 
			LinkedHashMap<String , LinkedHashMap<String , String>> colMapValues,
								String colName , 
								ArrayList<String> defValue , 
								HashMap<String , Integer> sqlColsSizes , 
								String Readonly,
								boolean disabled,
								HashMap <String , String > userDefinedLookups,
								String BGcolor , 
								boolean hidden,
								String extraMultiEditName,
								boolean required,
								boolean multiEdit,
								int multiEditRowNum){
		
				HTMLFactory factory = new HTMLFactory();
				HTMLShapes  shape   = factory.getShape(colName, userDefinedColsHtmlType, userDefinedLookups, extraMultiEditName , multiEditRowNum);
				return shape.getHtmlInput(sqlColsSizes,userDefinedColsHtmlType, colMapValues, defValue, Readonly, disabled, BGcolor, hidden, required,multiEdit);
		    }
} 
	
    


