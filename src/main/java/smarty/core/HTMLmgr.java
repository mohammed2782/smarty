package smarty.core;


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

import smarty.core.CoreMgr.OnFlyEditParams;
import smarty.core.html.*;

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
	private String normalBGColor   = "#efe9e942";
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
	
	
	public StringBuilder getCaptionTableAndHeaders(
		String a_UserDefinedCaption			  	, String a_UserDefinedCapationWithOutHtml	, boolean canEdit, 
		boolean canDelete 		 			  	, boolean canNew							, int currentpage, 
		ArrayList<String> userDefinedGridCols 	, Map<String , String> a_UserDefinedColLabel, String sortColName,
		String keyCol						  	, String sortMode							, boolean a_IsExport,
		boolean a_WordExport				  	, boolean a_PdfExport						, ArrayList <String> a_UserDefinedExportCols, 
		ArrayList<String>a_UserDefinedArabicCols, boolean userDefinedExportLandScape 		, HashMap<Integer ,HashMap<String,String>> a_MultiRowDisplayValuesForExport,
		int a_noOfRows 							, boolean userDefinedHideRowNumInGrid		, boolean haveGrouping, 
		boolean haveSumCols						, String a_UserDefinedTableClass			, String UserDefindEditFormEnctype,
		int userDefinedPageRows					, String a_UserDefinedTableHeadersClass     , boolean a_EditableGrid,
		HashMap<String , OnFlyEditParams> a_userDefinedOnFlyEditCols){
	  	StringBuilder view = new StringBuilder();
		
		view.append(
		"<section class='row' attr='table' id='main_row_"+myClassBean.replaceAll("\\.", "_")+"'>");
		view.append(
			"<div class=\"col-12\">"+ 
				"<div class=\"card\" id='card-"+myClassBean.replaceAll("\\.", "_")+"'>" + 
					"<div class=\"card-head\" id='card-head-"+myClassBean.replaceAll("\\.", "_")+"'>" + 
						"<div class=\"card-header\"  id='card-header-"+myClassBean.replaceAll("\\.", "_")+"'>" + 
							"<h4 id='h4-card-header-"+myClassBean.replaceAll("\\.", "_")+"' class=\"card-title\">"+a_UserDefinedCaption+"</h4> "
						  + "<div id='heading-elements-"+myClassBean.replaceAll("\\.", "_")+"' "
						  		+ "class=\"heading-elements\" style='display: -webkit-box; '>"+
							(canNew ? getNewButton(myClassBean ,a_EditableGrid ) : ""));
		view.append(getExportButton(
			a_IsExport	  				      , a_WordExport		, a_PdfExport, 
			a_EditableGrid				   	  , a_noOfRows			, a_MultiRowDisplayValuesForExport,
			a_UserDefinedCapationWithOutHtml  , a_UserDefinedCaption, a_UserDefinedExportCols,
			a_UserDefinedArabicCols, a_UserDefinedColLabel));
		// get prev pagination
		if (currentpage-1 >=1)
			view.append(getPagnationButton("السابق",myClassBean,currentpage-1,true));
		if (userDefinedPageRows<=a_noOfRows)
			view.append(getPagnationButton("اللاحق",myClassBean,currentpage+1,true));
		if (a_EditableGrid) {
			view.append("<button type='submit' style ='margin-top:5px;margin-right:10px;padding:3px; float:left;color:white;' " + 
					"	class='btn btn-primary px-3 radius-30'>حفظ <i class='fa fa-save fa-lg'></i></button>");
		}
		view.append(
			  	"</div>"//end of heading elements
			 +"</div>"// end of card-header
		+ "</div>");// end of card head
		view.append(
		"<div class=\"card-content\">" + 
		"<div class=\"card-body\" style='padding: 0rem 0rem; !important'>"
		+ "<div class=\"table-responsive\">"
		+"<table id='smarty_table_"+myClassBean.replace(".", "_dot_")+"' class='"+a_UserDefinedTableClass+"' style='width: 100%; max-width: 100%;'>");
		if (!haveGrouping)
			view.append(getHeaderRow(userDefinedHideRowNumInGrid,  keyCol , 
									a_EditableGrid,   canDelete ,   canEdit,
									haveGrouping,  haveSumCols,
									userDefinedGridCols,
									a_UserDefinedColLabel,
									sortColName, sortMode  , false , null, a_UserDefinedTableHeadersClass, a_userDefinedOnFlyEditCols));
				
		return view;
	}
	
	private String getExportButton(
			boolean a_IsExport	  				      , boolean a_WordExport		, boolean a_PdfExport, 
			boolean a_editableGrid				   	  , int a_noOfRows				, HashMap<Integer,HashMap<String,String>> a_MultiRowDisplayValuesForExport,
			String a_UserDefinedCapationWithOutHtml	  , String a_UserDefinedCaption , ArrayList <String> a_UserDefinedExportCols,
			ArrayList <String> a_UserDefinedArabicCols, Map<String,String> a_UserDefinedColLabel) {
		StringBuilder view = new StringBuilder("");
		//which is still wrong, we have to find solution 2 - jan - 2020
		String data = "";
		if (a_IsExport){ //editable grid is a problem as it will make form inside form so we seperate it in another function // which is stil wrong, we have to find solution 2 - jan - 2020
			if (a_WordExport || a_PdfExport){
				String val="";
				view.append("<form action='../../DocumentWriter?myClassBean="+this.myClassBean+"' method='POST'>");
				view.append("<input type='hidden' name='smarty_userDefinedExportLandScape' value='"+a_MultiRowDisplayValuesForExport+"' />");
				String htmlSection ="";
				for (Integer rowNum : a_MultiRowDisplayValuesForExport.keySet()){
					for (String col : a_MultiRowDisplayValuesForExport.get(rowNum).keySet()){
						if (!mysqlmgr.isFile(col)){
							try {
								val = "";
								if (a_MultiRowDisplayValuesForExport.get(rowNum).containsKey(col)
										&& a_MultiRowDisplayValuesForExport.get(rowNum).get(col)!=null)
									val = a_MultiRowDisplayValuesForExport.get(rowNum).get(col);
				
								data = URLDecoder.decode(val , "UTF-8");
								data = data.replaceAll("<td>","");
								data = data.replaceAll("</td>","");
								if (data.contains("<")|| data.contains(">")) {
									while(data.contains("<")|| data.contains(">")) {
										data = data.replaceAll("<.*?>" , " ").replaceAll("&.*?;", "");
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
							view.append("<input type='hidden' name='smartyrownum_"+rowNum+"_smartycol_"+col+"' value=\""+data+"\" >");
						}
					}
				}
				if (a_editableGrid)
					view.append("<input type='hidden' name='userDefinedCaption' value='"+a_UserDefinedCapationWithOutHtml+"'>");
				else
					view.append("<input type='hidden' name='userDefinedCaption' value='"+a_UserDefinedCaption+"'>");
				
				view.append("<input type='hidden' name='smartycoltoexport' value='");
				for (int i=0; i<a_UserDefinedExportCols.size(); i++){
					if (!mysqlmgr.isFile(a_UserDefinedExportCols.get(i)))
						view.append(a_UserDefinedExportCols.get(i)+",");
				}
				view.append("' >");
				
				if (!a_UserDefinedArabicCols.isEmpty()){
					view.append("<input type='hidden' name='arabicsmartycoltoexport' value='");
					for (int i=0; i<a_UserDefinedArabicCols.size(); i++){
						if (i>0)
							view.append(",");
						view.append(a_UserDefinedArabicCols.get(i));
					}
					view.append("' >");
				}
				for (int i=0; i< a_UserDefinedExportCols.size(); i++){
					if (!mysqlmgr.isFile(a_UserDefinedExportCols.get(i)))
						view.append("<input type='hidden' name='"+a_UserDefinedExportCols.get(i)+"_collabel' "
								+ "value='"+a_UserDefinedColLabel.get(a_UserDefinedExportCols.get(i))+"' >");
				
				}
				view.append("<input type='hidden' name='UserDefinedPageRows' value='"+a_noOfRows+"' >");
				if (a_PdfExport)
					view.append("<button type='submit' value='pdf' class='btn btn-danger waves-effect waves-light btn-sm'  name='pdf'><i class='fa fa-file-pdf-o fa-lg' aria-hidden='true'></i></button>");
				if (a_WordExport)
					view.append("<button type='submit' value='pdf' class='btn btn-primary btn-sm' name='docx' ><i class='fa fa-file-word-o fa-lg' aria-hidden='true'></i></button>");
				view.append("</form>");
			}
		} 
		return view.toString();
	}
	
	public StringBuilder getHeaderRow(
			boolean userDefinedHideRowNumInGrid, String keyCol , 
			boolean editableGrid,  boolean canDelete ,  boolean canEdit,
			boolean haveGrouping, boolean haveSumCols,
			ArrayList<String> userDefinedGridCols,
			Map<String,String> userDefinedColLabel,
			String sortColName,String sortMode,
			boolean slidingRow , String groupTitle, String a_UserDefinedTableHeadersClass,
			HashMap<String , OnFlyEditParams> a_userDefinedOnFlyEditCols){
		String colname;
		//System.out.println("in get header sildingRow=>"+slidingRow);
		String style = "style=\"display: none;\"";
		
		if (!slidingRow){
			style="";
			groupTitle ="";
		}
		StringBuilder header = new StringBuilder("<thead><tr class='"+a_UserDefinedTableHeadersClass+"' "+style+" "+groupTitle+" >");
		if (!userDefinedHideRowNumInGrid)
			header.append("<th  class='column-title'>#</th>");
		
		
		for (String key : userDefinedGridCols){
		    colname = userDefinedColLabel.get(key);
			if (colname==null)
			    colname=key;
			String tableName= "";
			String keyColName = "";
			if (a_userDefinedOnFlyEditCols!=null && a_userDefinedOnFlyEditCols.containsKey(key)) {
				tableName = a_userDefinedOnFlyEditCols.get(key).tableName;
				keyColName = a_userDefinedOnFlyEditCols.get(key).pkName;
			}
			header.append("<th id='smarty-th-id-"+key+"' data-t-name='"+tableName+"' data-pkc-name='"+keyColName+"' class='column-title'>"+colname+"</th>");
		}
		header.append(getUpdateDelete( keyCol , editableGrid,  canDelete ,  canEdit , true , "",""));
		header.append("</tr></thead>");
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
		view.append("<div align='center' ><textarea cols='100' name='debug' readonly align='center'  width ='100%' style='background-color:"
						+backgroundColor+"' >"+mysqlmgr.getExecutedSQL()+"</textarea></div>");
		return view;
	}
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
				"<img src='"+APPPATH+"/"+resourcesLocation+"/img/"+img+"' alt='' "+
				onMouseOver+" "+ onMouseOut +" height =11 width=8 /></a>";
		
		return html;
	}
	
	public  String getPagnationButton( String title, String myClassBean,int a_GoToPageNo,boolean active){
		return  "<a  "
				+ "class='btn btn-warning btn-sm waves-effect waves-light' "
				+ "href='?myClassBean="+myClassBean+"&page="+a_GoToPageNo+"' tabindex='-1' >"+title+"</a>";
		
	}
	
	public  String getUpdateButton(String myClassBean,String idname,String ID){
		String html="<a  href='?myClassBean="+myClassBean+"&"+idname+"="+ID+"&op=upd' "
				+ "class='btn btn-info btn-sm'><li class='fa fa-pencil'></li></a>";
		return html;
	}
	
	public  String getDeleteButton(String myClassBean,String idname, String ID , String dltCofrmMsg){
		String html = "";
		html="<button type='button'  onclick=\"link=false; var rs =doDeleteSmarty(this,'"+dltCofrmMsg+"' ,'"+idname+"','"+ID+"' , '"+myClassBean+"' ); "
			+ "return rs;\" class='btn btn-danger  btn-sm' ><li class='fa fa-trash'></li>" +
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
					+"<button type='button' class=\"btn btn-primary btn-sm waves-effect waves-light\">"
					+ "<i class='ft-plus white'> إضافة</i></button></a>";
		else
			html="<form action='?' method='post'>"
					+ "<input type='hidden' name='op' value='new'><input type='hidden' name='myClassBean' value='"+myClassBean+"'>"
				+ "<button type='submit' id ='smarty_new_submit_"+myClassBean.replace(".","_")+"' "
				+ "class=\"btn btn-primary btn-sm waves-effect  waves-light\" value='newform'>"
				+ "<i class='ft-plus white'>إضافة</i></button></form>";
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
								HashMap<String , String> allSqlColsTypes,
								HashMap<String , String> userDefinedFilterColHtmlAttr
								){
		if (userDefinedFilterCols.isEmpty()){
			return new StringBuilder("");
		}
		int i =1;
		
		ArrayList<String> defValue = new ArrayList<String>();
		LinkedHashMap <String , LinkedHashMap<String , String>> lookupMapVal = colMapValues;
		StringBuilder InnerFilterHtml= new StringBuilder("");
		String BackGroundColor="";
		
		String jsValidatorNumeric ="";
		String formName = "smartyFilter_"+myClassBean;
	
		boolean required=false;

		StringBuilder filters = new StringBuilder("<div class='row' attr='search'>"
				+ "<div class='col-xl-12 mx-auto'>"
				+ "<div class='card border-top border-0 border-4 border-white'>");
		filters.append("<div class='card-body p-1'>");
		filters.append("<form name=\""+formName+"\" action=\"?filter=1\" method=\"post\" "
				+ "data-parsley-validate class='form-horizontal form-label-left' >");
		InnerFilterHtml.append("<div class='row g-3'>");
		for (String key :userDefinedFilterCols){  	
			required = false;
			if (userDefinedColsMustFillFilter.contains(key)){
				required = true;
			}
			if (i==1 ){
			   /* InnerFilterHtml.append("<div class='row g-3'>");*/
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
				}else{
				   	lookupMapVal = colMapValues;
				}
			}else{
			    lookupMapVal = colMapValues;
			}
			InnerFilterHtml.append("<div class='col-xl-2 col-md-2 col-sm-6 col-xsm-12 form-group'> <label class='form-label' >"+coltoName.get(key));
			if(required)
				InnerFilterHtml.append("<span class='required'>*</span>");
			    InnerFilterHtml.append("</label>");
			    //InnerFilterHtml.append("<div class='col-md-2 col-sm-3 col-xs-12'>");
			    InnerFilterHtml.append( GetHtmlInput(userDefinedFilterColsHtmlType, lookupMapVal 		, key,
			    									   defValue 				    , sqlColsSizes 	    , "",
			    									   false/*for now*/ 		    , userDefinedLookups, BackGroundColor, 
			    									   false					    , null				, required,
			    									   false,
			    									   0, null , null, userDefinedFilterColHtmlAttr.get(key)));
			    InnerFilterHtml.append("</div>");
			    /*if (numberList.contains(allSqlColsTypes.get(key))){
					jsValidatorNumeric = jsValidatorNumeric +jsmgr.genJSNumericValidation(key , coltoName.get(key));
				}*/
			   /* if (i==5 ){
			    	InnerFilterHtml.append("</div>");
				    i=0;
			    }*/
			    i++;
			    InnerFilterHtml.append(genHotLookupsjs(key , userDefinedLookups , userDefinedFilterColsHtmlType , required));
		    }
		/*System.out.println("i--->"+i);
		if (i>1)
			while((6-i)>0) {
				InnerFilterHtml.append("<div class='col'></div>"); 
				i++;
			}
		if (i>1)
			InnerFilterHtml.append("</div>");*/
		InnerFilterHtml.append("</div>");
		InnerFilterHtml.append("<input type='hidden' name='myClassBean' value='"+myClassBean+"'/>");
		InnerFilterHtml.append(" <div class='row'>");
		InnerFilterHtml.append("<div class='col-7 form-group offset-5'>");
		InnerFilterHtml.append("<button type='submit' id='"+myClassBean+"' name=\"dosearch\" value=\"Search\" class='btn btn-success btn-min-width btn-sm box-shadow-2 mr-1 mb-1'> <i class=\"icon-magnifier\"></i> بحث</button>");
		InnerFilterHtml.append("&nbsp;&nbsp;&nbsp;<button id='smarty_cancelFilter' type='submit' name='cancelfilter_"+myClassBean+""
				+ "' onclick=\"this.smartyStat='cancelfilter'\" class='btn btn-warning btn-min-width mr-1 mb-1 btn-sm' smartyStat='init' value='Cancel'> <i class='icon-ban'></i> إلغاء البحث</button>");
		InnerFilterHtml.append("</div>");
		InnerFilterHtml.append("</div>");
		InnerFilterHtml.append("</form>");//End of Form								 	
		filters.append(InnerFilterHtml); 
		//filters.append(jsmgr.genValudationJS(formName , userDefinedColsMustFillFilter));
		//filters.append ("<script> var frmvalidator  = new Validator('"+formName+"');\n");
		filters.append("<script>$(document).ready(function() {");
		for (String param : jsmgr.colsUsedInLookupsForOtherCols.keySet()){
			filters.append(jsmgr.getHotLookupCallingScript(param));
		}
		filters.append("});</script>");
		filters.append("</div></div></div></div>");
		//System.out.println(filters);
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
	public StringBuilder GetHtmlInput(HashMap <String , String> userDefinedColsHtmlType , 
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
								int multiEditRowNum,
								HashMap<String,String> userDefinedMinValMap,
								HashMap<String,String> userDefinedMaxValMap,
								String userDefinedEditColHtmlAttr){
		
				HTMLFactory factory = new HTMLFactory();
				HTMLShapes  shape   = factory.getShape(colName, userDefinedColsHtmlType, userDefinedLookups, extraMultiEditName , multiEditRowNum);
				return shape.getHtmlInput(sqlColsSizes,
						userDefinedColsHtmlType, 
						colMapValues, defValue, 
						Readonly, disabled, BGcolor, hidden, required,multiEdit,
						userDefinedMinValMap,
						userDefinedMaxValMap,
						userDefinedEditColHtmlAttr);
		    }
} 