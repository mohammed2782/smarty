/*
 * Property of mohammed Nafi , mohammed2782@gmial.com
 * 1- need to do ordering in displaying the htmlform based on the configuration
 * 2- 
 */

package com.app.core;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.app.site.security.LoginUser;

public class CoreMgr {
	/*
	 * Static Finals
	 */
	//public  static final String APPPATH="/WMS";
	public  static final List<String> dateList = Arrays.asList("DATETIME", "DATE","TIMESTAMP");
	public  static final List<String> TextList = Arrays.asList("VARCHAR");
	public  static final List<String> numberList = Arrays.asList("BIGINT", "DOUBLE" , "INT");
	public  static final String ALIGN_GRID="center";// to align the html to the right (for arabic)
	public  static final String ALIGN_EDIT="right";// to align the html to the right (for arabic)
	public  static final String ALIGN_NEW="right";// to align the html to the right (for arabic)
	private static final String SMARTYKEY = "SMARTYKEY";
	public static final List<String> blobList = Arrays.asList("TINYBLOB" , "BLOB" , "MEDIUMBLOB" , "LONGBLOB");
	/* objects to be used in the mgr class*/
	protected SqlMgr  mysqlmgr;
	protected HTMLmgr myhtmlmgr;
	protected JSMgr   jsmgr;

	protected Connection conn;
	protected int currentpage = 1; // initial current page of the Grid
	protected HttpServletRequest httpSRequest; //servelet request
	protected int UserDefinedPageRows = 30; // no of rows to be displayed in one page
	protected int userDefinedNewFormColNo=1; // no of column the New from will have, if 2 then we display two inputs next to each other 
	protected int userDefinedEditFormColNo=1;// no of column the edit from will have, if 2 then we display two inputs next to each other
	/*
	 * HashMaps
	 * Some of them are facilities
	 */
	protected HashMap arrayGlobals; // array to hold the global variables
	protected HashMap <String , String> userDefinedColLabel ;//names of the cols to be displayed on the screen
	protected HashMap <String , String> userDefinedLookups;// lookups if there is for the list , new and eidt.
	protected HashMap <String , String> userDefinedEditLookups;// if there is lookup for edit then use this one.(overide the lookup in userDefinedLookups).
	
	protected HashMap <String , String> userDefinedNewLookups; // same idea as userDefinedEditLookups but for new form
	
	/* protected HashMap <String , String> userDefinedFilterLookups
	 * lookups for filters , in case one column have normal lookup and filter then the framework will use 
	 * the lookup when generating filters 
	 */
	protected HashMap <String , String>   userDefinedFilterLookups;
	protected HashMap <String , String[]> userDefinedNewColsDefualtValues;// new columns form// support globals for now , use {}
	protected HashMap <String , String[]> userDefinedEditColsDefualtValues;// Edit columns form// support globals for now , use {}
	// when you want to show columns that are not in the database, 
	//like cols used in calculations so the first part is the colname , second part is what to be injected as SQL
	protected HashMap<String,String> userDefinedEditMockUpCols;
	protected HashMap <String , String >  userDefinedNewColsHtmlType;// the user defined list of html types to be assigned to each col,if it have a lookuup
	protected HashMap <String , String >  userDefinedEditColsHtmlType;// the user defined list of html types to be assigned to each col,if it have a lookuup
	protected HashMap <String , String >  userDefinedFilterColsHtmlType;// the user defined list of html types to be assigned to each col,if it have a lookuup
	protected HashMap <String , String>   allSqlColsTypes ;// types of the columns returned from the select query
	protected HashMap <String , String>   userDefinedColsTypes ;// types of the columns returned from the select query
	protected HashMap <String , Integer>  sqlColsSizes; // column sizes in the database , to be used for new and edit form input size,user can overid this.
	//results for the map of the lookup
	protected   LinkedHashMap <String , LinkedHashMap<String , String>> colMapValues; 
	//results for the map of the lookup only for filters
	protected   LinkedHashMap <String , LinkedHashMap<String , String>> filterColMapValues;
	// NOT USED protected HashMap <String , String>   EditColError = new HashMap<String , String>();
	protected HashMap <String , String[]> search_paramval;
	protected HashMap <String , String>   userModifyTD;//col and function to reflect the modification on the td
	protected HashMap <String , String>   userDefinedImageDisplayServlet; // to be used inside <img src=""> to display image , upd form
	protected HashMap <String , String> userColHintEDIT;//display hint when the user in edit or New mode
	protected LinkedHashMap <String , FileItem> inputFilesMap; // map for the BLOB fields in FORMS;
	protected LinkedHashMap <String , String []> inputFieldsMap; // Map for the values from the FORMS
	protected LinkedHashMap <String , String[]> inputMap_ori;
	/*
	 * For multi Edit Purpose, some times you want to check if the cell is allowed to be 
	 * edited or not, so writer your own method and make sure the method return 
	 * boolean , if true , then editing will be allowed, false,
	 * editing will not be allowed, so use this hashmap for that
	 * put putting ("colName","methodName({param1},{param2}")
	 */
	protected HashMap<String,String>userDefinedMultiEditCondition;
	/*protected HashMap<String,String>userDefinedStoreFileNameColumns
	 * this is a very very important column, you can not upload or download a file
	 * with out specifying the column name that is going to hold the file.
	 * so each file column will have have a correspondent column to hold the file name
	 */
	protected HashMap<String,String>userDefinedStoreFileNameColumns;
	/*
	 * Array Lists
	 */
	protected ArrayList<String> userDefinedReadOnlyNewCols;//read only columns in new form
	protected ArrayList<String> userDefinedReadOnlyEditCols;// read only columns in edit form
	
	protected ArrayList<String> userDefinedDisabledNewCols;//disabled New cols
	protected ArrayList<String> userDefinedDisabledEditCols;//disabled Edit cols, when Edit Cols is empty, it will cope from userDefinedDisabledNewCols
	protected ArrayList <String> userDefinedHiddenNewCols;//hidden cols in the new form
	protected ArrayList <String> userDefinedHiddenEditCols;//hidden cols in the Edit form
	protected ArrayList<String> userDefinedEditCols;//edit columns form
	protected ArrayList<String> userDefinedColsMustFill;// must fill columns//Edit,New form
	protected ArrayList<String> userDefinedColsMustFillFilter;// must fill columns in filter
	protected ArrayList<String> userDefinedFilterCols ;// filter columns
	protected ArrayList<String> userDefinedFilterColsUsingLike;
	protected ArrayList<String> userDefinedGridCols ;// columns to be displayed on the screen

	protected ArrayList<String> userDefinedExportCols ;// columns to be displayed on the screen
	protected ArrayList<String> userDefinedArabicCols; // columns which hold arabic font.
	
	protected ArrayList<String> userDefinedNewCols;// new columns form
	protected ArrayList<String> userDefinedSumCols;// new columns form
	protected ArrayList<String> excludeKeyWords;
	private   ArrayList<String> allSqlCols ; // all cols returned from the select query
	// use this to put a line before the column in the edit form
	protected ArrayList<String> userDefinedEditColsLineSeperator;
	
	// for fieldset
	protected HashMap<String,String> userDefinedFieldSetCols;//map , contains col,legend
	protected ArrayList<String> userDefinedFieldSetEndWithCols;//if there is a fieldset then it will close after this column
	
	private HashMap  <String , Double> groupSumCols = new HashMap <String , Double>();
	/*
	 * Strings
	 */
	protected String userDefinedGlobalClickRowID;
	protected String userDefinedGroupByCol;
	protected String GroupTitle;
	private String prevGroupVal;
	private String groupDisplayVal;
	protected String userDefinedSlidingGroupValue = ""; //this is a complex on, use it when the groupbycol is complex and there is a sliding group, now the sliding group java script wont work so   put a col name where sliding group will depened upon
	protected String userDefinedGroupColsOrderBy;
	protected String editCaption="Edit Form";
	protected String UserDefindNewFormEnctype="";/** Developer can put the desired enctype for eg. UserDefindNewFormEnctype = "enctype='multipart/form-data'"*/
	protected String UserDefindEditFormEnctype="";/** Developer can put the desired enctype for eg. UserDefindEditFormEnctype = "enctype='multipart/form-data'"*/
	private   String APPPATH;
	protected String jspName;
	protected String jspNameWithoutDot;
	protected String newCaption="Create New Record";
	protected String updCaption="Update Infomation";
	protected String groupSumCaption = "";// caption used for grouping when summing columns, to be shown at the numbering col.
	protected String userDefinedCaption="Smarty";
	protected String userDefinedCapationWithOutHtml ="smarty"; // if in edit mode, and we want to add operation, it will make a problem when there is export
	protected String userDefined_x_panelclass =""; //use this string to put a class name in it and change the class of the table in the smarty class
	
	public String getUserDefinedCaption() {
		return userDefinedCaption;
	}

	public void setUserDefinedCaption(String userDefinedCaption) {
		this.userDefinedCaption = userDefinedCaption;
	}

	protected String MainSql;
	protected String orderByCols;
	private   String sortColName ="";
	private   String sortMode = "";
	protected String errorvalidation;
	protected String keyCol;
	protected String hiddenKeyCol;
	protected String mainTable;
	protected String myClassBean;
	//protected String clickableRowLocation;
	protected String userDefinedWhere; // the user define the where cluse;
	protected String logErrorMsg;//error msg to be logged to Logger
	protected String userDefinedGroupClass = "GroupingClass";
	protected String userDefinedGroupRowClass = "GroupingRowClass";
	protected String userDefinedGroupSumColStyle = "groupSumColStyle";
	protected String displayMode;//this defined what is the current mode the display is in , now we have three modes , EDITSINGLE,LIST,NEWSINGLE
	protected String userDefineddltConfirmMsg = "Are you sure you want to delete this record?";
	protected String userDefinedGroupSortMode = "asc";
	protected String userDefinedGroupFooterFunction=null;//sometimes you want to create footer for each group
	protected String userDefinedMultiNewRowExtension="smartyNewRow";//this is used when we have multi new row
	//this is used to call a method that will append a row at the end of the grid.
	// you should put a method name the receives a STRING (which is the colName) to use this facility
	protected String userDefinedPageFooterFunction = null;
	protected String keyVal;
	private   String DBMSType;
	protected LoginUser lu;
	protected String userDefinedcolCollectionDelimmiter=":";
	/*
	 * Boolean
	 */
	protected boolean Debug = false;
	public    boolean canNew;
	public    boolean canDelete;
	public    boolean canEdit;
	public    boolean canFilter;
	public    boolean clickableRow;
	protected boolean canExport;
	protected boolean userDefinedExportLandScape= false;//to export in landscape
	protected boolean pdfExport;
	protected boolean wordExport;
	protected boolean userDefinedHideRowNumInGrid;
	
	protected boolean deleteErrorFlag = false;
	protected boolean insertErrorFlag = false;
	public boolean isDeleteErrorFlag() {
		return deleteErrorFlag;
	}

	public void setDeleteErrorFlag(boolean deleteErrorFlag) {
		this.deleteErrorFlag = deleteErrorFlag;
	}

	public boolean isInsertErrorFlag() {
		return insertErrorFlag;
	}

	public void setInsertErrorFlag(boolean insertErrorFlag) {
		this.insertErrorFlag = insertErrorFlag;
	}

	public boolean isUpdateErrorFlag() {
		return updateErrorFlag;
	}

	public void setUpdateErrorFlag(boolean updateErrorFlag) {
		this.updateErrorFlag = updateErrorFlag;
	}

	protected boolean updateErrorFlag = false;
	
	protected boolean slidingGroups = false;
	private boolean updateAction = false;
	private boolean insertAction = false;
	
	protected DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
	protected DateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy");
	protected DateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	public boolean isDebug() {
		return Debug;
	}

	public void setDebug(boolean debug) {
		Debug = debug;
	}
	/*
	 * Constructor
	 */
	public CoreMgr(){
		mysqlmgr  = new SqlMgr(myClassBean);
		myhtmlmgr = new HTMLmgr();
		jsmgr	  = new JSMgr();
		userDefinedHideRowNumInGrid = false;
		setcurrentpage(1);
		userDefinedFieldSetCols			= new HashMap<String,String>();
		userDefinedStoreFileNameColumns = new HashMap<String,String>();
		userDefinedFieldSetEndWithCols  = new ArrayList<String>();
		userDefinedFilterCols      		= new ArrayList<String>();
		userDefinedFilterColsUsingLike	=  new ArrayList<String>();
		userDefinedGridCols        		= new ArrayList<String>();
		userDefinedExportCols      		= new ArrayList<String>();
		userDefinedArabicCols      		= new ArrayList<String>();
		userDefinedNewCols         		= new ArrayList<String>();
		userDefinedReadOnlyNewCols 		= new ArrayList<String>();
		userDefinedReadOnlyEditCols	    = new ArrayList<String>();
		userDefinedDisabledNewCols 		= new ArrayList<String>();
		userDefinedDisabledEditCols		= new ArrayList<String>();
		userDefinedHiddenNewCols		= new ArrayList<String>();
		userDefinedHiddenEditCols		= new ArrayList<String>();
		userDefinedEditCols        		= new ArrayList<String>();
		userDefinedEditColsLineSeperator= new ArrayList<String>();
		allSqlCols      		   		= new ArrayList<String>();
		
		excludeKeyWords 		   		= new ArrayList<String>();
		userDefinedColsMustFill    		= new ArrayList<String>();
		userDefinedColsMustFillFilter   = new ArrayList<String>();
		userDefinedSumCols				= new ArrayList<String>();
		allSqlColsTypes			   		= new HashMap<String,String>();
		userDefinedColsTypes			= new HashMap<String,String>();
		userModifyTD					= new HashMap<String,String>();
		userDefinedNewColsHtmlType 		= new HashMap<String , String>();
		userDefinedEditColsHtmlType     = new HashMap<String , String>();
		userDefinedEditMockUpCols		= new HashMap<String , String>();
		userDefinedFilterColsHtmlType   = new HashMap<String , String>();
		userDefinedColLabel        		= new HashMap<String , String>();
		userDefinedLookups   	   	    = new HashMap<String , String>();
		userDefinedFilterLookups		= new HashMap<String , String>();
		userDefinedEditLookups			= new HashMap<String , String>();
		userDefinedNewLookups			= new HashMap<String , String>();
		userDefinedNewColsDefualtValues = new HashMap<String,String[]>();
		userDefinedEditColsDefualtValues = new HashMap<String,String[]>();
		userColHintEDIT					= new HashMap <String , String>();
		userDefinedImageDisplayServlet  = new HashMap <String , String>();
		userDefinedMultiEditCondition   = new HashMap<String ,String>();
		inputFieldsMap 					= new LinkedHashMap <String , String[]>();
		inputMap_ori					= new LinkedHashMap <String , String[]>();
		inputFilesMap 					= new LinkedHashMap<String , FileItem>();
		orderByCols  = "";
		userDefinedGroupByCol = null;
		userDefinedGroupColsOrderBy = null;
		displayMode = "LIST";
		canNew 		 = false;
		canDelete    = false;
		canEdit 	 = false;
		clickableRow = false;
		canFilter    = false;
		canExport    = false;
		wordExport   = false;
		pdfExport	 = false;
		deleteErrorFlag = false;
		insertErrorFlag = false;
		updateErrorFlag = false;
		updateAction = false;
		insertAction = false;
		userDefinedCapationWithOutHtml = userDefinedCaption;
	}
	/*
	 * Get the intersection elements between two lists
	 */
	public <T> List<T> intersection(List<T> list1, List<T> list2) {
	        List<T> list = new ArrayList<T>();

	        for (T t : list1) {
	            if(list2.contains(t)) {
	                list.add(t);
	            }
	        }

	 return list; 
	 }
	/*
	 * Replace Default values
	 * How it works : pass the Map that have key and string which contain params with %
	 */
	@SuppressWarnings("null")
	public HashMap<String,String[]> replaceDefualtValues (HashMap<String,String[]> DefualtValues ){
		String [] NewDefValue;
		HashMap<String,String[]> clonned =   (HashMap<String, String[]>) DefualtValues.clone();
		int arrSize;
		int i = 0;
		for (String key : clonned.keySet()){
			arrSize = clonned.get(key).length;
			NewDefValue = new String[arrSize];
			DefualtValues.remove(key);
			i = 0;
			for (String def : clonned.get(key)){
				NewDefValue[i] = replaceVarsinString(def , arrayGlobals);
				def =NewDefValue[i]; 
				//System.out.println(NewDefValue[i]);
				if (def.startsWith("%")){
					NewDefValue[i] = mysqlmgr.getValuefromSql(conn,def.replaceFirst("%", ""));
				}
				i++;
			} 
			DefualtValues.put(key, NewDefValue);
		}

		Map<String,String[]>requestMap = httpSRequest.getParameterMap();
		if (requestMap!=null){
			for (String key : requestMap.keySet()){
				if (requestMap.containsKey(key) && requestMap.get(key)!=null){
					arrSize = requestMap.get(key).length;
					NewDefValue = new String[arrSize];
					DefualtValues.remove(key);
					i = 0;
					for (String def : requestMap.get(key)){
						NewDefValue[i] = def;
						i++;
					} 
					DefualtValues.put(key, NewDefValue);
				}
			}
		}
		return DefualtValues;
	}
	/*
	 * this method will be called after actions and before displaying
	 * since initialize method must be called before actions, 
	 * if we need to do anything after actions then we call this method and the son class can overrid
	 */
	public void processAfterActionsAndBeforeDisplaying(HashMap smartyStateMap) {
		
	}
	
	/*
	 * Initialize class parameters
	 */
	public void initialize(HashMap smartyStateMap){
		myhtmlmgr.setAPPPATH(APPPATH);
		jsmgr.setAPPPATH(APPPATH);
		
		MainSql = replaceVarsinString(MainSql , arrayGlobals);
		//MainSql = MainSql.toLowerCase(); // do i really need it?
		userDefinedCaption = replaceVarsinString(userDefinedCaption, arrayGlobals);
		myhtmlmgr.setJsmgr(jsmgr);
		userDefinedWhere = "";
		//print_request(httpSRequest);
		
		if (httpSRequest.getParameter("DEBUG")!=null)
			if(httpSRequest.getParameter("DEBUG").equals("1")){
				Debug = true;
			}
		
		currentpage= getCurrentPage(smartyStateMap);
		prepareSorting(smartyStateMap);
		
		if (((HashMap<String,HashMap>)smartyStateMap.get(myClassBean)).get("sortby")!=null ){
			if ( ((HashMap<String,HashMap>)smartyStateMap.get(myClassBean)).get("sortby").get("sortingby")!=null ){
				sortColName = (String) ((HashMap<String,HashMap>)smartyStateMap.get(myClassBean)).get("sortby").get("sortingby");
			}
			if ( ((HashMap<String,HashMap>)smartyStateMap.get(myClassBean)).get("sortby").get("sortmode")!=null ){
				sortMode = (String) ((HashMap<String,HashMap>)smartyStateMap.get(myClassBean)).get("sortby").get("sortmode");
			}
		}
		prepareSearchData(smartyStateMap);
		userDefinedNewColsDefualtValues  = replaceDefualtValues ((HashMap<String, String[]>) userDefinedNewColsDefualtValues);
		userDefinedEditColsDefualtValues = replaceDefualtValues ((HashMap<String, String[]>) userDefinedEditColsDefualtValues);
		
		/*
		if (updateAction && !userDefinedEditCols.isEmpty()){
			String sql_load_row ="select ";
			boolean first = true;
			for(String parameter : userDefinedEditCols) {
				if (!first)
					sql_load_row = sql_load_row+" , ";
				if (userDefinedEditMockUpCols.containsKey(parameter)){
					if (userDefinedEditMockUpCols.get(parameter).trim().equals(""))
						sql_load_row += " '' as "+parameter;
					else
						sql_load_row += " "+userDefinedEditMockUpCols.get(parameter)+" as "+parameter;
				}else
					sql_load_row +=parameter;
				first = false;
			}
			allSqlCols      = mysqlmgr.getMetaDataSqlColsList(conn,sql_load_row+" from "+mainTable+" where 1=0");
			allSqlColsTypes = mysqlmgr.getsqlColsTypes();
			sqlColsSizes    = mysqlmgr.getsqlColsSizes();
		}else if (insertAction && !userDefinedNewCols.isEmpty()){
			String sql_load_row ="select ";
			boolean first = true;
			for(String parameter : userDefinedNewCols) {
				if (!first)
					sql_load_row = sql_load_row+" , ";
				sql_load_row +=parameter;
				first = false;
			}
			allSqlCols      = mysqlmgr.getMetaDataSqlColsList(conn,sql_load_row+" from "+mainTable+" where 1=0");
			allSqlColsTypes = mysqlmgr.getsqlColsTypes();
			sqlColsSizes    = mysqlmgr.getsqlColsSizes();
		}else{*/
		//the above commented code, cause a problem when there are fake columns in new or update so i removed it, i don't know why i wrote it in the first place.
			allSqlCols      = mysqlmgr.getMetaDataSqlColsList(conn,MainSql);
			allSqlColsTypes = mysqlmgr.getsqlColsTypes();
			sqlColsSizes    = mysqlmgr.getsqlColsSizes();
		//}
		//userDefinedEditLookups
		//System.out.println("userDefinedColsTypes=====>"+userDefinedColsTypes);
		HashMap <String , String > tempHash = new HashMap<String,String>();
		for (String keyCol :  allSqlColsTypes.keySet()){
			if (userDefinedColsTypes.containsKey(keyCol)){
				tempHash.put(keyCol, userDefinedColsTypes.get(keyCol));
			}else{
				tempHash.put(keyCol, allSqlColsTypes.get(keyCol));
			}
		}
		allSqlColsTypes = tempHash;
		
		if (displayMode.equalsIgnoreCase("EDITSINGLE")){
		    for (String lookupKey :userDefinedEditLookups.keySet()){
			    if (userDefinedLookups.containsKey(lookupKey.trim())){
			    	userDefinedLookups.remove(lookupKey.trim());
			    	userDefinedLookups.put(lookupKey.trim(),userDefinedEditLookups.get(lookupKey));
			    }else
			    	userDefinedLookups.put(lookupKey.trim(),userDefinedEditLookups.get(lookupKey));
			}
		}else if (displayMode.equalsIgnoreCase("NEWSINGLE")){
			  for (String lookupKey :userDefinedNewLookups.keySet()){
				    if (userDefinedLookups.containsKey(lookupKey.trim())){
				    	userDefinedLookups.remove(lookupKey.trim());
				    	userDefinedLookups.put(lookupKey.trim(),userDefinedNewLookups.get(lookupKey));
				    }else
				    	userDefinedLookups.put(lookupKey.trim(),userDefinedNewLookups.get(lookupKey));
				}
		}
		String lookupStringBefore ="";
		tempHash = new HashMap<String,String>();
	
		for (String lookupKey :userDefinedLookups.keySet()){
		   	lookupStringBefore = userDefinedLookups.get(lookupKey.trim());
		   	tempHash.put(lookupKey.trim(),replaceVarsinString(lookupStringBefore , arrayGlobals));
		   	
		}
		userDefinedLookups.clear();
		userDefinedLookups = tempHash;
		
		if (!userDefinedLookups.isEmpty())// only when the lookup map is not empty , then load it.
	    	 colMapValues= mysqlmgr.loadAllLookups(conn,userDefinedLookups);
		
		//System.out.println("colMapValues===>"+colMapValues);
		 
		if (!userDefinedFilterLookups.isEmpty()){
			filterColMapValues = mysqlmgr.loadAllLookups(conn,userDefinedFilterLookups);
		}
		
		if (userDefinedGridCols.isEmpty())
			userDefinedGridCols=allSqlCols;
		if (canExport){
			if (userDefinedExportCols.isEmpty())
				userDefinedExportCols = (ArrayList<String>) userDefinedGridCols.clone();
		}
		
		if (userDefinedNewCols.isEmpty())
			userDefinedNewCols=allSqlCols;
		
		if (userDefinedFilterCols.isEmpty()){
			for (int i = 0 ; i < allSqlCols.size() ; i++)
				if (!mysqlmgr.isFile(allSqlCols.get(i)))
					userDefinedFilterCols.add(allSqlCols.get(i));
		}else{
			for (int i = 0 ; i < userDefinedFilterCols.size() ; i++)
				if (mysqlmgr.isFile(userDefinedFilterCols.get(i)))
					userDefinedFilterCols.remove(i);
		}
		if (userDefinedEditCols.isEmpty())
			userDefinedEditCols = allSqlCols;
	
		if (keyCol == null || keyCol.isEmpty()) 
			keyCol="";
		
		if (userDefinedGlobalClickRowID == null || userDefinedGlobalClickRowID.isEmpty()) 
			userDefinedGlobalClickRowID=SMARTYKEY;
		else
			clickableRow = true;
			
		userDefinedGlobalClickRowID = userDefinedGlobalClickRowID.toLowerCase();
		// when we do update we use hidden field to hold the key paramter,this one 
		hiddenKeyCol = "smarty_"+keyCol+"_hidden";
		excludeKeyWords.add("new");
		excludeKeyWords.add("op");
		excludeKeyWords.add("myClassBean");
		excludeKeyWords.add("upd");
		excludeKeyWords.add(hiddenKeyCol);
		excludeKeyWords.add("className");
		excludeKeyWords.add("pageName");
		
		// default the names of the columns to the names of the table cols, in case the user did not define them
		for (String key : mysqlmgr.getsqlColsTypes().keySet()){
			if (!userDefinedNewColsHtmlType.containsKey(key)){
				userDefinedNewColsHtmlType.put(key, mysqlmgr.getsqlColsTypes().get(key));
			}
			
		}
		for (String key :userDefinedNewColsHtmlType.keySet()){//this is big mistake, because if we dont have new form the search will be impacted
			if (!userDefinedFilterColsHtmlType.containsKey(key)){
				userDefinedFilterColsHtmlType.put(key, userDefinedNewColsHtmlType.get(key));
			}
			if (!userDefinedEditColsHtmlType.containsKey(key)){
				userDefinedEditColsHtmlType.put(key, userDefinedNewColsHtmlType.get(key));
			}
		}
		
		for (String key : allSqlCols){
			if (!userDefinedColLabel.containsKey(key)){
				userDefinedColLabel.put(key, key);
			}
		}
		if (!userDefinedDisabledEditCols.isEmpty())
			userDefinedDisabledEditCols = userDefinedDisabledNewCols;
		
		userDefinedCapationWithOutHtml = userDefinedCaption;
	}// End of Method initialize
	/*
	 * replace variables in string
	 * How it works : pass the string and map of the vars you want to replace
	 * so it will search for {} and replace the string in between with the value of that key string in the map
	 */
	public String replaceVarsinString(String myString , HashMap arrayGlobals){// to replace curley brackets
		// i replace the globals in any string when it have {}
		int startIndex;
		int endIndex;
		String keyParamter;
		String globalParamName;
		
		while (myString.contains("{")){
			startIndex = myString.indexOf("{");
			endIndex = myString.indexOf("}");
			globalParamName =myString.substring(startIndex+1, endIndex);
			
			if (arrayGlobals.get(globalParamName)!=null){
				keyParamter =  arrayGlobals.get(globalParamName).toString();
				myString = myString.replace("{"+globalParamName+"}", keyParamter);
				myString = myString.replace("{"+globalParamName+"}", keyParamter);
				//System.out.println("my new String=>"+myString);
			}else{
				myString = myString.replace("{"+globalParamName+"}", "#<"+globalParamName+"#>");
			}	
		}
		myString = myString.replace("#<", "{");
		myString = myString.replace("#>", "}");
		
		return myString;
	}
	 /*
	  * To display filters.
	  * Caller : JSP main page.
	  */
	public StringBuilder DisplayFilters(){
		if (canFilter){
			return myhtmlmgr.genrateFilter(userDefinedFilterCols    , userDefinedColLabel,
										  userDefinedFilterColsHtmlType, search_paramval , 
										  colMapValues				, filterColMapValues,
										  sqlColsSizes 				, myClassBean, 
										  userDefinedLookups		, userDefinedColsMustFillFilter , 
										  allSqlColsTypes);
		}else{
			return new StringBuilder ("");
		}
	}
	/*
	 * Print HTTP servlet Request
	 * usage : for debugging purpose
	 */
	public  String print_request(HttpServletRequest rqs){
		String Request_str = "</br>Start printing Request</br>";
		Map<String, String[]> parameters = rqs.getParameterMap();
		for(String parameter : parameters.keySet()) {
			Request_str =Request_str +parameter+"=>"; 
		    for (String value : parameters.get(parameter)){
		    	Request_str =Request_str +value+"</br>";
		    }    
		}
		return Request_str;
	}
	/* filter the request object
	 * this function to filter out the unwanted parameters from the request object
	 */
	public LinkedHashMap<String, String[]> filterRequest(HttpServletRequest rqs){ 
		LinkedHashMap<String, String[]> newMap = new LinkedHashMap <String , String[]>();
		Map<String, String[]> parameters = rqs.getParameterMap();
		for(String parameter : parameters.keySet()) {
			if (!excludeKeyWords.contains(parameter)){
				newMap.put(parameter, parameters.get(parameter));
			}
		}
		return newMap;
	}
	
	/*
	 * Generate Listing View
	 */
	public StringBuilder genListing(){
		//System.out.println("this is show listing");
		myhtmlmgr.setMyClassBean(myClassBean);
		myhtmlmgr.setMysqlmgr(mysqlmgr);
		myhtmlmgr.setUserDefinedNewColsHtmlType(userDefinedNewColsHtmlType);
		StringBuilder view= new StringBuilder() ;
		StringBuilder gridHtml= new StringBuilder() ;
		StringBuilder headerTableHtml= new StringBuilder() ;
		prevGroupVal = "";
		groupDisplayVal="";
		String displyValue="" , colToSearchBy="" , displaySumVal= "" , keyColVal="";
		int noOfCols = userDefinedGridCols.size(), colSpan = noOfCols+1, i =1;
		boolean haveLookup = false, userModifiedTD = false;
	    HashMap <String , String> originalValue = new HashMap<String , String>();
	    HashMap <String , String> lastOriginalValueForEndGrouping = new HashMap<String , String>();
	    HashMap <Integer , HashMap<String,String>> MultiRowDisplayValuesForExport = new HashMap<Integer , HashMap<String,String>>();
	    HashMap <String , String> SingleRowDisplayDataForExport = new HashMap<String,String>();
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		PreparedStatement pst = null;
		boolean haveGrouping = (userDefinedGroupByCol!=null);
		boolean haveSumCols = (!userDefinedSumCols.isEmpty());
		if(canEdit || canDelete ){ colSpan++;}
		try{
			rs = mysqlmgr.LoadData(conn  				   , pst				  , rs			, UserDefinedPageRows , currentpage , MainSql  ,  search_paramval , 
								   userDefinedWhere 	   , orderByCols 		  ,	sortColName , sortMode ,  userDefinedGroupByCol, 
								   userDefinedGroupSortMode, userDefinedGroupColsOrderBy , userDefinedNewColsHtmlType , userDefinedFilterColsUsingLike);
			rsmd = rs.getMetaData();
		}catch(Exception e){e.printStackTrace();}
		if (isDebug()){//to be changed later to if itadmin or debug
			gridHtml.append(myhtmlmgr.getDebugSqlArea());
		}
		try{
			gridHtml.append("<tbody>");
			String tdAlign ="right" , keyVal="" , rowDisplay ="";
			boolean isLastRow = false;
			while(rs.next()){//start looping the data
				rowDisplay ="";
				isLastRow = rs.isLast();
				if (keyCol!=null && !keyCol.isEmpty())
					keyVal = rs.getString(keyCol);
				if (!originalValue.isEmpty())
					lastOriginalValueForEndGrouping = (HashMap<String, String>) originalValue.clone();
				originalValue.clear();
				for (int col=1 ; col<=rsmd.getColumnCount() ; col++)
	        		originalValue.put(rsmd.getColumnLabel(col), rs.getString(rsmd.getColumnLabel(col)));
				gridHtml.append(getGrouping(rs,i , colSpan , lastOriginalValueForEndGrouping, numFormat , false));	
				if (!clickableRow && slidingGroups) {
					if (userDefinedSlidingGroupValue !=null && !userDefinedSlidingGroupValue.trim().equalsIgnoreCase(""))
						GroupTitle = "GroupTitle='"+originalValue.get(userDefinedSlidingGroupValue).replace(" ", "_").replace(".", "_")+"'";
					else
						GroupTitle = "GroupTitle='"+groupDisplayVal.replace(" ", "_").replace(".", "_")+"'";
				}else {
					GroupTitle = "";
				}
				//System.out.println("GroupTitle==>"+GroupTitle);
				if (slidingGroups)
					rowDisplay = "style=\"display: none;\"";
				gridHtml.append(myhtmlmgr.getDataTR(GroupTitle, rowDisplay , originalValue.get(keyCol),clickableRow,userDefinedGlobalClickRowID));
				if (!userDefinedHideRowNumInGrid)
					gridHtml.append("<td  width='3%' align='right' class='cell'>"+i+"</td>");
				else{
					if (haveGrouping && haveSumCols)  // show the td for the total grouping
						gridHtml.append("<td  width='3%' align='right' class='cell'></td>");
				}
				SingleRowDisplayDataForExport = new HashMap<String, String>();
				for (String key : userDefinedGridCols){ //this loop is for columns
					haveLookup = false; 
					userModifiedTD = false;
					displyValue = rs.getString(key);
					if (colMapValues!=null){// this will work only for normal lookups
					   if(colMapValues.containsKey(key)){//get the column name , then get the code to get the description
						   if (userDefinedNewColsHtmlType!=null && userDefinedNewColsHtmlType.containsKey(key) 
								    && (userDefinedNewColsHtmlType.get(key).equals("CHECKBOX") || userDefinedNewColsHtmlType.get(key).equals("MULTILIST"))){
							   ArrayList<String> colsInSql =mysqlmgr.getMetaDataSqlColsList(conn,userDefinedLookups.get(key));
								if (colsInSql.size()==1)
									displyValue = "error in lookup"+key;
								colToSearchBy = colsInSql.get(0);
								
							   displyValue = getCollectionColumnData(conn, userDefinedLookups.get(key), displyValue, colToSearchBy);
						   }else
						   displyValue = colMapValues.get(key).get(rs.getString(key));
						   haveLookup = true;
						}
				    }
				    //now we need to have a treatment for the hot lookups.
				    try{
					   if (userDefinedLookups!=null)
						   if (userDefinedLookups.get(key)!=null)
							   if (userDefinedLookups.get(key).startsWith("!")){
								   haveLookup = true; 
								   displyValue = GenHotLookupValForListing(conn, userDefinedLookups.get(key),  key , 
										   displyValue , originalValue);
								}
				    }catch(Exception e){
						logErrorMsg = "query origin is from class=>"+myClassBean+",Exception Msg=>"+e.getMessage(); 
						smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
						Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
						logErrorMsg = "";
						System.out.println("Error at generateing hotlookup for key="+key+",at genListing");
						e.printStackTrace();
					}
					if (rs.wasNull()) displyValue ="";
					
					if (userModifyTD !=null)
						if (userModifyTD.containsKey(key)){
							displyValue = getValueFromUserMethodForThisColumn(key,originalValue);
							userModifiedTD = true;
						}
					if (userModifiedTD){
						gridHtml.append(displyValue);
					}else{// i changed to display only when there is not modify td, because i want to control the whole TD
						tdAlign = "right";
						if (!userModifiedTD)
						if (mysqlmgr.isFile(key)){
							boolean showLink = false;
							 if (userDefinedStoreFileNameColumns!=null)
								 if (userDefinedStoreFileNameColumns.get(key)!=null){
									 if (originalValue.get(userDefinedStoreFileNameColumns.get(key))!=null
											 &&  !originalValue.get(userDefinedStoreFileNameColumns.get(key)).trim().equalsIgnoreCase("")){
										 displyValue = myhtmlmgr.getDownloadFileButton(key ,keyVal , myClassBean);
										 showLink = true;
										 tdAlign = "center";
									 }
								 }
							 if (!showLink)
								 displyValue="";
						}
						//format if the data is number and never had lookup and never been modified
						
						if (numberList.contains(allSqlColsTypes.get(key))&& (!haveLookup) && (!userModifiedTD)){
							if (displyValue == null){
								displyValue = "0.0";
							}else if (displyValue==""){displyValue = "0.0";}
							displyValue = numFormat.format(Double.parseDouble(displyValue));
							tdAlign = "right";
						}else if ((dateList.contains(allSqlColsTypes.get(key))) && (!haveLookup) && (!userModifiedTD)){
							if (displyValue == null){
								displyValue = "0.0";
							}else if (displyValue==""){
								displyValue = "0.0";
							}
							if (rs.getDate(key)!=null)
								if (allSqlColsTypes.get(key).equalsIgnoreCase("DATETIME")
										 || allSqlColsTypes.get(key).equalsIgnoreCase("TIMESTAMP"))
									displyValue = dateTimeFormat.format(rs.getTimestamp(key));
								else
									displyValue = dateformat.format(rs.getDate(key));
							else 
								displyValue = "";
							tdAlign = "right";
						}
						gridHtml.append("<td align='"+tdAlign+"' dir='ltr'>");
						gridHtml.append( (displyValue!=null && !displyValue.equalsIgnoreCase("null"))?displyValue: "");
						gridHtml.append("</td>");
					} 
					if(userDefinedExportCols.contains(key))
						SingleRowDisplayDataForExport.put(key, displyValue);
			   }
			
			   if (keyCol !=null && !keyCol.isEmpty()){ 
				   keyColVal = originalValue.get(keyCol); 
				   gridHtml.append(myhtmlmgr.getUpdateDelete( keyCol , false,  canDelete ,  canEdit , false , keyColVal,userDefineddltConfirmMsg));
			   }	     
			   gridHtml.append("</tr>");
			   if (i == UserDefinedPageRows || isLastRow){ // last col, if have grouping then display
				   if (userDefinedSumCols !=null){
					   if (userDefinedSumCols.size()!=0){
						   gridHtml.append("<tr>");
						   if (!userDefinedHideRowNumInGrid)//if we hide the number, then where to show the sum caption
							   gridHtml.append("<td class='"+userDefinedGroupSumColStyle+"' align='center'>"+groupSumCaption+"</td>");// this is for the numbering col
						   for (String keySumCols : userDefinedGridCols){
							   displaySumVal = "";
							   if (groupSumCols.containsKey(keySumCols)){
								   displaySumVal = String.valueOf(groupSumCols.get(keySumCols));
								   displaySumVal = numFormat.format(Double.parseDouble(displaySumVal));
							   }
							   gridHtml.append("<td class='"+userDefinedGroupSumColStyle+"' align='center' dir='ltr'>"+displaySumVal+"</td>");
						   }		   
						   if (canDelete ||canEdit ){
							   gridHtml.append("<td class='"+userDefinedGroupSumColStyle+"'  width='4%'></td>");
						   }
						   gridHtml.append("</tr>");
						   groupSumCols.clear();	
					   }
				   }
				   if (userDefinedGroupByCol!=null)
					   if(userDefinedGroupFooterFunction!=null){
						   gridHtml.append(getGroupFooterRow(userDefinedGroupFooterFunction , originalValue));
					   }
			   }

			   if (userDefinedPageFooterFunction!=null){
				   if (isLastRow){ //if at the last row
					   gridHtml.append("<tr class='GridFooterClass'>");
					   if (!userDefinedHideRowNumInGrid)
						   gridHtml.append("<td></td>");//add to make the table consistent
					   for (String key : userDefinedGridCols)//loop through all the columns
						   gridHtml.append(getPageFooterRow(userDefinedPageFooterFunction,key));
					   if (canEdit || canDelete)
						   gridHtml.append("<td></td>");//add to make the table consistent
					   gridHtml.append("</tr>");
					}
			  }
			   //System.out.println("SingleRowDisplayDataForExport===>"+SingleRowDisplayDataForExport);
			   MultiRowDisplayValuesForExport.put(i, SingleRowDisplayDataForExport);
			   i++;
			}
			if(userDefinedGroupByCol !=null )
				if(slidingGroups)
					gridHtml.append(jsmgr.getSlidingGroupJS());
		}catch(Exception e){
			logErrorMsg = "query origin is from class=>"+myClassBean+",Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
			Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			logErrorMsg = "";
			e.printStackTrace();
			System.out.println("this error in generating the view");
		}finally{
			try {rs.close();} catch (Exception e) { /* ignored */} 
			if (rsmd != null) {rsmd = null;}
			try {pst.close();} catch (Exception e) { /* ignored */}
		}
		
		headerTableHtml.append(myhtmlmgr.getCaptionTableAndHeaders(userDefinedCaption, userDefinedCapationWithOutHtml 	,  canEdit, 
				canDelete   		 ,  canNew,
				currentpage			 ,  userDefinedGridCols , 
				userDefinedColLabel  ,  sortColName,
				keyCol				 ,  sortMode,
				false				 ,  canExport,
				wordExport			 ,  pdfExport,
				userDefinedExportCols, userDefinedArabicCols,
				userDefinedExportLandScape, MultiRowDisplayValuesForExport,
				i-1				     , userDefinedHideRowNumInGrid,
				haveGrouping		 , haveSumCols,
				userDefined_x_panelclass, "",
				UserDefinedPageRows));
		view.append(headerTableHtml);
		gridHtml.append("</tbody>");
		view.append(gridHtml);
		
		view.append("</table>");
		view.append("</div></div></div></div></div><script>var link=true;</script>");
		return view;
	}// End of Gen listing
	/*
	 * Get the grouping HTML but not the last Group
	 */
	private StringBuilder getGrouping(ResultSet rs        , int rowNum , 
									  int colSpan  		  , HashMap<String, String> lastOriginalValueForEndGrouping,
									  DecimalFormat numFormat , boolean editableGrid)throws Exception{
		StringBuilder view = new StringBuilder();
		String displaySumVal;
		Double groupSumVals= 0.0;
		String myGroupTitle = "";
		String controlGroup = "";
		boolean haveSumCols = (!userDefinedSumCols.isEmpty());
		
		 if (userDefinedGroupByCol !=null ){
	    	   if (rs.getString(userDefinedGroupByCol.trim()) != null){
	    		   if (rowNum==1){//if first row
	    			   
	    			   groupDisplayVal = prevGroupVal = rs.getString(userDefinedGroupByCol);
	        		   if ( colMapValues !=null && colMapValues.get(userDefinedGroupByCol)!=null)
	        			   if (colMapValues.get(userDefinedGroupByCol).get (prevGroupVal)!=null)
	        				   groupDisplayVal = colMapValues.get(userDefinedGroupByCol).get (prevGroupVal);
	        			   if (clickableRow){
	        				   groupDisplayVal = "<a href='?"+userDefinedGlobalClickRowID+"="
	        						   			+rs.getString(userDefinedGroupByCol)+"'>"+groupDisplayVal+"</a>";
	        			   }	
	        			   if (slidingGroups){
	        				   if (userDefinedSlidingGroupValue !=null && !userDefinedSlidingGroupValue.trim().equalsIgnoreCase("")) {
		       						myGroupTitle = "GroupTitle='"+rs.getString(userDefinedSlidingGroupValue).replace(" ", "_").replace(".", "_")+"'";
		       						controlGroup = "controlgroup='"+rs.getString(userDefinedSlidingGroupValue).replace(" ", "_").replace(".", "_")+"'";
     						 }else {
		       						myGroupTitle = "GroupTitle='"+groupDisplayVal.replace(" ", "_").replace(".", "_")+"'";
		       						controlGroup = "controlgroup='"+groupDisplayVal.replace(" ", "_").replace(".", "_")+"'";
     						 }
		        			view.append("<tr class='"+userDefinedGroupRowClass+"'>"
		        					+ "<td colspan='"+colSpan+"' class='"+userDefinedGroupClass+"'>"
		        					+ " <a href='#' class='"+userDefinedGroupClass+" special_sliding_group'  "
		        							+ controlGroup+ ">"+groupDisplayVal+"</a>"
		        					+ "</td></tr>");
	        				   view.append(myhtmlmgr.getHeaderRow
	        						   		(userDefinedHideRowNumInGrid,  keyCol , 
	        						   				editableGrid,   canDelete ,   canEdit,
		        									 true,  haveSumCols,
		        									 userDefinedGridCols,
		        									 userDefinedColLabel,
		        									 sortColName, sortMode , true , 
		        									 myGroupTitle));
	        			   } else{
	        				   view.append("<tr class='"+userDefinedGroupRowClass+"'><td colspan='"+colSpan+"' class='"+userDefinedGroupClass+"'>"
	        						   +"<span>"+groupDisplayVal+"</span></td></tr>");
		        				   view.append(myhtmlmgr.getHeaderRow
		        						   (userDefinedHideRowNumInGrid,  keyCol , 
		        							editableGrid,   canDelete ,   canEdit,
		        									 true,  haveSumCols,
		        									 userDefinedGridCols,
		        									 userDefinedColLabel,
		        									 sortColName, sortMode, false,null));
	        			   }
	        			}
	    		   //System.out.println("prevGroupVal===>"+prevGroupVal+", and current userDefinedGroupByCol "+rs.getString(userDefinedGroupByCol));
	        		if (!prevGroupVal.equals(rs.getString(userDefinedGroupByCol))){// if new value
	        				groupDisplayVal= prevGroupVal = rs.getString(userDefinedGroupByCol);
      					if (colMapValues!=null && colMapValues.get(userDefinedGroupByCol)!=null)
      						if (colMapValues.get(userDefinedGroupByCol).get (prevGroupVal)!=null)
      							groupDisplayVal = colMapValues.get(userDefinedGroupByCol).get (prevGroupVal);
      					if (userDefinedSumCols !=null){
          					if (userDefinedSumCols.size()!=0){
          						view.append("<tr>");
          						if (!userDefinedHideRowNumInGrid)//if we hide the number, then where to show the sum caption
          							view.append("<td class='"+userDefinedGroupSumColStyle+"' align='center'>"+groupSumCaption+"</td>");// this is for the numbering col
		        				for (String keySumCols : userDefinedGridCols){
		        					displaySumVal = "";
		        					if (groupSumCols.containsKey(keySumCols)){
		        						displaySumVal = String.valueOf(groupSumCols.get(keySumCols));
		        						displaySumVal = numFormat.format(Double.parseDouble(displaySumVal));
		        					}
		        					view.append("<td class='"+userDefinedGroupSumColStyle+"' align='center' dir='ltr'>"+displaySumVal+"</td>");
		        				}
		        				if ((canDelete ||canEdit ) && (!editableGrid)){
          						    view.append("<td class='"+userDefinedGroupSumColStyle+"' width = '4%'></td>");
          						}
		        				view.append("</tr>");	
		        				groupSumCols.clear();
          					}
      					}
  						if(userDefinedGroupFooterFunction!=null){
  							view.append(getGroupFooterRow(userDefinedGroupFooterFunction , lastOriginalValueForEndGrouping));
  						}
      					if (clickableRow){
	        					groupDisplayVal = "<a href='?"+userDefinedGlobalClickRowID+"="+rs.getString(userDefinedGroupByCol)+"'>"+groupDisplayVal+"</a>";
	        			}
      					// to-do : move the style into css
      					 if (slidingGroups){
      						 if (userDefinedSlidingGroupValue !=null && !userDefinedSlidingGroupValue.trim().equalsIgnoreCase("")) {
		       						myGroupTitle = "GroupTitle='"+rs.getString(userDefinedSlidingGroupValue).replace(" ", "_").replace(".", "_")+"'";
		       						controlGroup = "controlgroup='"+rs.getString(userDefinedSlidingGroupValue).replace(" ", "_").replace(".", "_")+"'";
   						 }else {
		       						myGroupTitle = "GroupTitle='"+groupDisplayVal.replace(" ", "_").replace(".", "_")+"'";
		       						controlGroup = "controlgroup='"+groupDisplayVal.replace(" ", "_").replace(".", "_")+"'";
   						 }
		        			view.append("<tr class='"+userDefinedGroupRowClass+"'>"
		        					+ "<td colspan='"+colSpan+"' class='"+userDefinedGroupClass+"'>"
		        					+ " <a href='#' class='"+userDefinedGroupClass+" special_sliding_group'  "
		        							+ controlGroup+ ">"+groupDisplayVal+"</a>"
		        					+ "</td></tr>");
		        			
		        			 view.append(myhtmlmgr.getHeaderRow
	        						   (userDefinedHideRowNumInGrid,  keyCol , 
	        							editableGrid,   canDelete ,   canEdit,
	        									 true,  haveSumCols,
	        									 userDefinedGridCols,
	        									 userDefinedColLabel,
	        									 sortColName, sortMode, true , 
	        									 myGroupTitle));
      					 }else{
		        			view.append("<tr class='"+userDefinedGroupRowClass+"'>"
		        					+ "<td colspan='"+colSpan+"' class='"+userDefinedGroupClass+"'><span>"+groupDisplayVal+"</span></td></tr>");
		        			 view.append(myhtmlmgr.getHeaderRow
	        						   (userDefinedHideRowNumInGrid,  keyCol , 
	        								   editableGrid,   canDelete ,   canEdit,
	        									 true,  haveSumCols,
	        									 userDefinedGridCols,
	        									 userDefinedColLabel,
	        									 sortColName, sortMode,false , null));
	        			}
	        		}
	        	}
	        	/*
  				 * First Sum the previous cols if any
  				 */
  				if (userDefinedSumCols !=null){
  					if (userDefinedSumCols.size()!=0){
  						for (String KeySum : userDefinedSumCols){
  							if (groupSumCols.containsKey(KeySum)){
  								groupSumVals = groupSumCols.get(KeySum); 
  							}else{
  								groupSumVals = 0.0;
  							}					
  							if (rs.getString(KeySum)== null){
  								groupSumVals = groupSumVals + 0;
  							}else{
  								if (rs.getString(KeySum).trim()== null || rs.getString(KeySum).trim().equals("") )
  									groupSumVals = groupSumVals + 0;
  								else
  									groupSumVals = groupSumVals + Double.parseDouble(rs.getString(KeySum)); 
  							}
  							// need to put handle here if the value is not number
  							groupSumCols.remove(KeySum);
  							groupSumCols.put(KeySum, groupSumVals);
  						}
  					}
  				}
	        }
		 return view;
	}
	//-------------------------------------------------------------------------------------------------------------------
	/*
	 * function will return value from a Method implemented by the user(Developer) 
	 * in a class extending CoreMgr
	 */
	private String getValueFromUserMethodForThisColumn(String key , HashMap<String,String>originalValue ){
		String value="";
		String functionToCall = null;
		 int startIndex;
		 int endIndex;
		 String ParamName= null , funcName=null , replaceCurleyBrackets = null;
		 HashMap<String , String> ParamterVal = new HashMap<String,String>();
		 try{
			    //originalValue
			 replaceCurleyBrackets = funcName = userModifyTD.get(key).trim();
			 //System.out.println("=====>"+userModifyTD.get(key));
			 while (replaceCurleyBrackets.contains("{")){
					startIndex  = replaceCurleyBrackets.indexOf("{");
					endIndex    = replaceCurleyBrackets.indexOf("}");
					ParamName   = replaceCurleyBrackets.substring(startIndex+1, endIndex);
					replaceCurleyBrackets = replaceCurleyBrackets.substring(endIndex+1);
					ParamterVal.put(ParamName.toLowerCase(),originalValue.get(ParamName.toLowerCase()));
				}
			 funcName    = userModifyTD.get(key).substring(0, funcName.indexOf("("));
			 Method mymethod = this.getClass().getMethod(funcName, HashMap.class);
			 value = (String) mymethod.invoke(this , ParamterVal);	
			}catch (Exception e){
				logErrorMsg = "query origin is from class=>"+myClassBean+",Exception Msg=>"+e.getMessage(); 
				smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
						Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
				logErrorMsg = "";
				value ="ERR:COREMGR class , "+functionToCall;
				e.printStackTrace();
			
			}
		return value;
	}
	
	private String getCollectionColumnData(Connection conn , String loo , String displayValue , String colToSearchBy){
		String sqlLookup="";
		String dbValue;
		ResultSet rsHotLookup = null;
		if (displayValue ==null)
			return "";
		
	
		String[] MultiOptions = displayValue.split(userDefinedcolCollectionDelimmiter);
		displayValue="<ul dir='rtl'>";
		for (int j=0 ; j< MultiOptions.length ; j++){												 
			try{
				sqlLookup    = "select * from ("+loo+")myFakeLookup  where "+colToSearchBy+"='"+MultiOptions[j]+"'";
				//System.out.println(sqlLookup);
				rsHotLookup  = mysqlmgr.getRow(conn,sqlLookup);
				if (rsHotLookup!=null){
					if (rsHotLookup.getString(2)!=null){
						dbValue = rsHotLookup.getString(2);
					}else{
						dbValue = MultiOptions[j]+"-Error Mapping Lookup";
					}
				}else{
					dbValue = MultiOptions[j]+"-Error Mapping";
				}
				if (!MultiOptions[j].trim().equalsIgnoreCase(""))
					displayValue= displayValue+"<li align='right'>"+dbValue+"</li>";
			    try {rsHotLookup.close();} catch (Exception e) {/*ignore*/}
			  }catch (Exception e){
				logErrorMsg = "query origin is from class=>"+myClassBean+",sqlLookup=>"+sqlLookup+
				"Exception Msg=>"+e.getMessage(); 
				smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
						Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
				logErrorMsg = "";
				e.printStackTrace();
				System.out.println("Error at Parsing for CheckBoxes");
			  }finally{
				  try {rsHotLookup.close();} catch (Exception e) {/*ignore*/}
			  }
		 }
		 displayValue=displayValue+"</ul>";
		 return displayValue;
	}
	
	private String GenHotLookupValForListing(Connection conn , String loo ,String key ,String displayValue , HashMap<String ,String> originalValue){
		String logErrorMsg ="";
		if (displayValue== null)
			return displayValue;
		
		if(displayValue.trim().equals("")){
			return displayValue;
		}
		//System.out.println("displayValue==="+displayValue.length());
		
		String colToSearchBy;
		String ParamName="";
		String dbValue ="";
		int startIndex ,endIndex;
		ResultSet rsHotLookup = null;
		loo = loo.replaceFirst("!", "");
		while (loo.contains("{")){
			startIndex = loo.indexOf("{");
			endIndex = loo.indexOf("}");
			ParamName =loo.substring(startIndex+1, endIndex);
			loo = loo.replace("{"+ParamName+"}", originalValue.get(ParamName));
		}		
		ArrayList<String> colsInSql =mysqlmgr.getMetaDataSqlColsList(conn,loo);
		if (colsInSql.size()==1)
			return displayValue;
		
		colToSearchBy = colsInSql.get(0);
		//Hot lookup for Check Boxes (May be any collection later)
		if (userDefinedNewColsHtmlType.get(key).equals("CHECKBOX")){
			displayValue = getCollectionColumnData(conn , loo , displayValue , colToSearchBy);
		 }else{
			 loo = "select * from ("+loo+")myFakeLookup where "+colToSearchBy+"='"+displayValue+"'";
			 try{
				// System.out.println("loo==============>"+loo);
				 rsHotLookup = mysqlmgr.getRow(conn, loo);
				 if (rsHotLookup!=null){
						if (rsHotLookup.getString(2)!=null){
							displayValue = rsHotLookup.getString(2);
						}else{
							displayValue = displayValue+"-Error Mapping Lookup";
						}
					}else{
						displayValue = displayValue+"-Error Mapping";
					}
				 //displayValue= mysqlmgr.getRow(loo).getString(2);
			 }catch (Exception e){
				logErrorMsg = "query origin is from class=>"+myClassBean+",loo=>"+loo+
						"Exception Msg=>"+e.getMessage(); 
				smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
						Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
				logErrorMsg = "";
				e.printStackTrace();
				System.out.println("Error at Parsing ,Hot Lookup value not Found");
				displayValue="";
			 }
		 }
		 if (rsHotLookup != null)
		     try{
			     rsHotLookup.close();
			 }catch (SQLException e){
				 /*ignore*/
			 }
	 return displayValue;
	}
	/*
	 * for multiEdit purpose for now
	 * check if cell is allowed to be editied by calling this method which will
	 * call a method defined by you
	 */
	private boolean CheckifEditingIsAllwoed(String funcName,HashMap <String, String>originalValue){
		boolean allowEditing = true; 
		String functionToCall = null;
		int startIndex;
		int endIndex;
		String logErrorMsg= "";
		String ParamName= null , replaceCurleyBrackets = null; 
		// currently the smarty supports only strings, may be we change it to support arrays also
		HashMap<String , String> ParamterVal = new HashMap<String,String>();
		try{
			replaceCurleyBrackets = funcName;
			while (replaceCurleyBrackets.contains("{")){
				startIndex  = replaceCurleyBrackets.indexOf("{");
				endIndex    = replaceCurleyBrackets.indexOf("}");
				ParamName   = replaceCurleyBrackets.substring(startIndex+1, endIndex);
				replaceCurleyBrackets = replaceCurleyBrackets.substring(endIndex+1);
				ParamterVal.put(ParamName.toLowerCase(),originalValue.get(ParamName.toLowerCase()));
			}
			funcName    = funcName.substring(0, funcName.indexOf("("));
			Method mymethod = this.getClass().getMethod(funcName, HashMap.class);//may be we should change the param to hashmap
			allowEditing = (boolean) mymethod.invoke(this , ParamterVal);	
		}catch (Exception e){
			logErrorMsg = "query origin is from class=>"+myClassBean+",Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
			Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			logErrorMsg = "";
			e.printStackTrace();	
		}
		return allowEditing;
	}
	
	/*
	 * the method will return page row at the end of the page, just call a method
	 * currently im passing the colName as a string parameter to the method invoked
	 */
	private String getPageFooterRow(String funcName,String colName){
		 String functionToCall = null , dislplayFooter=null;
		 try{
			 funcName    = funcName.substring(0, funcName.indexOf("("));
			 Method mymethod = this.getClass().getMethod(funcName , String.class);//may be we should change the param to hashmap
			 dislplayFooter = (String) mymethod.invoke(this , colName);	
			}catch (Exception e){
				logErrorMsg = "query origin is from class=>"+myClassBean+",Exception Msg=>"+e.getMessage(); 
				smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
						Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
				logErrorMsg = "";
				dislplayFooter ="ERR:COREMGR class , "+functionToCall;
				e.printStackTrace();	
			}
		 return dislplayFooter;
	 }
	/*
	 * will add footer for each Group, the method to be called must return <tr></tr>
	 */
	private String getGroupFooterRow(String funcName , HashMap <String, String>originalValue){
		 String functionToCall = null;
		 int startIndex;
		 int endIndex;
		 String logErrorMsg= "";
		 String ParamName= null , replaceCurleyBrackets = null , displyFooter;
		 // currently the smarty supports only strings, may be we change it to support arrays also
		 HashMap<String , String> ParamterVal = new HashMap<String,String>();
		 try{
			 replaceCurleyBrackets = funcName;
			 while (replaceCurleyBrackets.contains("{")){
					startIndex  = replaceCurleyBrackets.indexOf("{");
					endIndex    = replaceCurleyBrackets.indexOf("}");
					ParamName   = replaceCurleyBrackets.substring(startIndex+1, endIndex);
					replaceCurleyBrackets = replaceCurleyBrackets.substring(endIndex+1);
					ParamterVal.put(ParamName.toLowerCase(),originalValue.get(ParamName.toLowerCase()));
				}
			 funcName    = funcName.substring(0, funcName.indexOf("("));
			 Method mymethod = this.getClass().getMethod(funcName, HashMap.class);//may be we should change the param to hashmap
			 displyFooter = (String) mymethod.invoke(this , ParamterVal);	
			}catch (Exception e){
				logErrorMsg = "query origin is from class=>"+myClassBean+",Exception Msg=>"+e.getMessage(); 
				smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
						Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
				logErrorMsg = "";
				displyFooter ="ERR:COREMGR class , "+functionToCall;
				e.printStackTrace();	
			}
		 return displyFooter;
	 }
	
	/*
	 * Generate MultiEditGrid
	 */
	public StringBuilder getMultiEditGrid(){
		
		myhtmlmgr.setMyClassBean(myClassBean);
		myhtmlmgr.setMysqlmgr(mysqlmgr);
		if (userDefinedEditCols.isEmpty()){
			userDefinedEditCols = (ArrayList<String>) userDefinedGridCols.clone();
		}
		jsmgr.userDefinedColsHtmlType = userDefinedEditColsHtmlType;
		/*	Vars Definition */
		boolean haveGrouping = (userDefinedGroupByCol!=null);
		boolean haveSumCols = (!userDefinedSumCols.isEmpty());
		boolean required = false;//not fixed yet
		StringBuilder view = new StringBuilder(),gridHtml = new StringBuilder() ;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		PreparedStatement pst=null;
		//boolean haveLookup = false;
		boolean userModifiedTD = false;
		ArrayList<String> displayValue = new ArrayList<String>();
    	String rowDisplay ="";
    	String GroupTitle = "";
    	String readOnly = "";
    	String backGroundColor ="";
    	String keyColVal;
    	String  displaySumVal= "";
    	boolean allowEdit = true;
    	boolean disabled=false;
    	boolean haveLookup = false;
    	HashMap <String , String> SingleRowDisplayDataForExport = new HashMap<String,String>();
    	int noOfCols = userDefinedGridCols.size(), colSpan = noOfCols+1;
		HashMap <String , String> originalValue = new HashMap<String , String>();
		HashMap <String , String> lastOriginalValueForEndGrouping = new HashMap<String , String>();
		HashMap<Integer ,HashMap<String,String>> MultiRowDisplayValuesForExport  = new  HashMap<Integer ,HashMap<String,String>>();
		int rowsno=0;
		String tdAlign = "right";
		//UserDefindEditFormEnctype = "enctype='multipart/form-data'";
		
		
		try{
			rs = mysqlmgr.LoadData(conn  					, pst				  , rs			, UserDefinedPageRows , currentpage , MainSql  ,  search_paramval , 
								   userDefinedWhere 		, orderByCols 		  ,	sortColName , sortMode ,  userDefinedGroupByCol , 
								   userDefinedGroupSortMode , userDefinedGroupColsOrderBy , userDefinedEditColsHtmlType, userDefinedFilterColsUsingLike);
			rsmd = rs.getMetaData();
		}catch(Exception e){
			e.printStackTrace();
		}
		if (isDebug()){//to be changed later to if itadmin or debug
			view.append(myhtmlmgr.getDebugSqlArea());
		}
		int i = 1;
		try {
			boolean isLastRow = false;
			gridHtml.append("<tbody>");
			while(rs.next()){
				allowEdit = true;
				isLastRow = rs.isLast();
				rowsno++;
				
				if (!originalValue.isEmpty())
					lastOriginalValueForEndGrouping = (HashMap<String, String>) originalValue.clone();
				originalValue.clear();
				for (int col=1 ; col<=rsmd.getColumnCount() ; col++){
					originalValue.put(rsmd.getColumnLabel(col), rs.getString(rsmd.getColumnLabel(col)));
			    }
				gridHtml.append(getGrouping(rs,i , colSpan , lastOriginalValueForEndGrouping, numFormat , true));	
				if (!clickableRow && slidingGroups)
					if (userDefinedSlidingGroupValue !=null && !userDefinedSlidingGroupValue.trim().equalsIgnoreCase(""))
						GroupTitle = "GroupTitle='"+originalValue.get(userDefinedSlidingGroupValue).replace(" ", "_").replace(".", "_")+"'";
					else
						GroupTitle = "GroupTitle='"+groupDisplayVal.replace(" ", "_").replace(".", "_")+"'";
					
				else
					GroupTitle = "";
				
		        if (slidingGroups)
		        	rowDisplay = "style=\"display: none;\"";
		        
		        gridHtml.append(myhtmlmgr.getDataTR(GroupTitle, rowDisplay , originalValue.get(keyCol),clickableRow,userDefinedGlobalClickRowID));
		        if (!userDefinedHideRowNumInGrid)
		        	gridHtml.append("<td  width='3%' align='right' class='cell'>"+i+"</td>");
		        SingleRowDisplayDataForExport = new HashMap<String, String>();
				for (String key : userDefinedGridCols){ //this loop is for columns
					haveLookup = false; 
					userModifiedTD = false;
					required = false;
					
					displayValue.clear();
					
					if (rs.getString(key)!=null)
						displayValue.add(rs.getString(key));
					else{
						if (userDefinedEditColsDefualtValues.containsKey(key))
							if(userDefinedEditColsDefualtValues.get(key)!=null)
								for (String val : userDefinedEditColsDefualtValues.get(key))
									displayValue.add(val);
					}
					//System.out.println(i+", at beg for "+key+", displayValue "+displayValue);
					if(userDefinedReadOnlyEditCols.contains(key))
						readOnly = "readonly";
					else
						readOnly = "";
						
					if (userDefinedDisabledNewCols.contains(key))
						disabled = true;
					else
						disabled = false;
					
					if (userDefinedColsMustFill.contains(key)){//check for the must fill
						backGroundColor="#fcfae5";
						required=true;
					}else{
						backGroundColor="#F0FFF0";
					}
					
					if (userDefinedMultiEditCondition.containsKey(key)){
						allowEdit = CheckifEditingIsAllwoed(userDefinedMultiEditCondition.get(key),originalValue);
					}
					
					
					if (userModifyTD !=null) {// i the user choose to change the cell then we transfer the control to him
						if (userModifyTD.containsKey(key)){
							gridHtml.append(getValueFromUserMethodForThisColumn(key,originalValue));
							userModifiedTD = true;
						}
					}
					if (!userModifiedTD){ // if no modify td and have lookups and in user defined edit cols
						
						if (userDefinedLookups!=null && userDefinedLookups.get(key)!=null){
							haveLookup = true;
							// this is for hot lookup
							if (userDefinedLookups.get(key).startsWith("!")) {
								//System.out.println(key+" have hot lookup");
								GenHotLookupForUpdForm(key , originalValue);
								
							}//End of Hot Lookup Treatment
							else { // this is for NORMAL lookup
								// if the column in edit mode then let the GetHtmlInput Method
								// handle the mapping to lookup
								if (!userDefinedEditCols.contains(key) || !allowEdit)
									if (colMapValues!=null){// this will work only for normal lookups
										if(colMapValues.containsKey(key)){
											//get the column name , then get the code to get the description
											displayValue.clear();
											if (colMapValues.get(key).get(rs.getString(key)) !=null 
													&& !colMapValues.get(key).get(rs.getString(key)).equalsIgnoreCase(""))
												displayValue.add(colMapValues.get(key).get(rs.getString(key)));
											else
												displayValue.add("");
											//System.out.println(colMapValues.get(key).get(rs.getString(key)));
											//haveLookup = true;
										}
									}
							}
						}
					}
					
					tdAlign = "right";
					
					if ((!haveLookup) && (!userModifiedTD)) {
						if (displayValue !=null && !displayValue.isEmpty()) {
							if (numberList.contains(allSqlColsTypes.get(key))){
								
									if (displayValue.get(0) == null){
										displayValue.add("0.0");
									}else if (displayValue.isEmpty()){displayValue.add("0.0");}
								
									displayValue.add(0,numFormat.format(Double.parseDouble(displayValue.get(0))));
									tdAlign = "right";
								
							}else if (dateList.contains(allSqlColsTypes.get(key))){
								if (displayValue.get(0) == null){
									displayValue.add("");
								}
								if (!userDefinedEditCols.contains(key))
									if (allSqlColsTypes.get(key).equalsIgnoreCase("DATETIME")
											 || allSqlColsTypes.get(key).equalsIgnoreCase("TIMESTAMP"))
										displayValue.add(0,dateTimeFormat.format(rs.getTimestamp(key)));
									else
										displayValue.add(0,dateformat.format(rs.getDate(key)));
								tdAlign = "right";
							}
						}
					}
					//System.out.println(i+", at middle for "+key+", displayValue "+displayValue);
					if (!userModifiedTD) {
						if (userDefinedEditCols.contains(key) && allowEdit && canEdit){
							//System.out.println(i+", for "+key+", displayValue "+displayValue);
							gridHtml.append("<td>"+myhtmlmgr.GetHtmlInput(
														   userDefinedEditColsHtmlType, colMapValues,
														   key		 				 , displayValue, 
														   sqlColsSizes 			 , readOnly , 
														   disabled					 , userDefinedLookups, 
														   backGroundColor 			 , false , 
														   "_smartyrow_"+i  		 ,required,
														   true						 , i
														   )+"</td>");
						}else{
							gridHtml.append("<td  align='"+tdAlign+"'>");
							if (displayValue!=null && displayValue.size()>0)
								gridHtml.append(displayValue.get(0));
							
							gridHtml.append("</td>");
						}
					}
					if(userDefinedExportCols.contains(key))
						if (displayValue.size()>0) {
							for (int z = 0 ; z<displayValue.size(); z++)
								
							if (colMapValues.containsKey(key)) { // if have map value
								if (colMapValues.get(key).get(displayValue.get(0))!=null)
									SingleRowDisplayDataForExport.put(key, colMapValues.get(key).get(displayValue.get(0)));
								else
									SingleRowDisplayDataForExport.put(key, displayValue.get(0));
							}else {
								SingleRowDisplayDataForExport.put(key, displayValue.get(0));
							}
						}else
							SingleRowDisplayDataForExport.put(key, "");
					
				}
				keyColVal = originalValue.get(keyCol); 
				if (!keyCol.isEmpty()){ 	
					gridHtml.append(myhtmlmgr.getUpdateDelete( keyCol 	  , true,  
							 								canDelete , canEdit , 
							 								false 	  , keyColVal,
							 								userDefineddltConfirmMsg));
				}
				gridHtml.append("<input type='hidden' value='"+keyColVal+"' name='"+hiddenKeyCol+"_smartyrow_"+i+"'/>");
				
				gridHtml.append("</tr>");
				
				 if (i == UserDefinedPageRows || isLastRow){ // last col, if have grouping then display
					   if (userDefinedSumCols !=null){
						   if (userDefinedSumCols.size()!=0){
							   gridHtml.append("<tr>");
							   if (!userDefinedHideRowNumInGrid)//if we hide the number, then where to show the sum caption
								   gridHtml.append("<td class='"+userDefinedGroupSumColStyle+"' align='center'>"+groupSumCaption+"</td>");// this is for the numbering col
							   for (String keySumCols : userDefinedGridCols){
								   displaySumVal = "";
								   if (groupSumCols.containsKey(keySumCols)){
									   displaySumVal = String.valueOf(groupSumCols.get(keySumCols));
									   displaySumVal = numFormat.format(Double.parseDouble(displaySumVal));
								   }
								   gridHtml.append("<td class='"+userDefinedGroupSumColStyle+"' align='center' dir='ltr'>"+displaySumVal+"</td>");
							   }		   
							   if (canDelete ){
								   gridHtml.append("<td class='"+userDefinedGroupSumColStyle+"'  width='4%'></td>");
							   }
							   gridHtml.append("</tr>");
							   groupSumCols.clear();	
						   }
					   }
					   if (userDefinedGroupByCol!=null)
						   if(userDefinedGroupFooterFunction!=null){
							   gridHtml.append(getGroupFooterRow(userDefinedGroupFooterFunction , originalValue));
						   }
				   }
				 
				 MultiRowDisplayValuesForExport.put(i, SingleRowDisplayDataForExport);
				i++;
			}//end of loop
			if (userDefinedPageFooterFunction!=null && !userDefinedPageFooterFunction.trim().isEmpty()){
				gridHtml.append("<tr class='GridFooterClass'>");
					   if (!userDefinedHideRowNumInGrid)
						   gridHtml.append("<td></td>");//add to make the table consistent
					   for (String key : userDefinedGridCols)//loop through all the columns
						   gridHtml.append(getPageFooterRow(userDefinedPageFooterFunction,key));
					   if (canDelete)
						   gridHtml.append("<td></td>");//add to make the table consistent
					   gridHtml.append("</tr>");
					
			  }
			 if(userDefinedGroupByCol !=null )
					if(slidingGroups)
						gridHtml.append(jsmgr.getSlidingGroupJS());
			 
			 
			 
				/*	Get Header Table and caption */
				//System.out.println("MultiRowDisplayValuesForExport----->"+MultiRowDisplayValuesForExport);
				view.append(myhtmlmgr.getCaptionTableAndHeaders(userDefinedCaption  , userDefinedCapationWithOutHtml 	,  canEdit, 
																canDelete   		,  canNew,
																currentpage			,  userDefinedGridCols , 
																userDefinedColLabel ,  sortColName,
																keyCol				,  sortMode,
																true				,  canExport,
																wordExport			,  pdfExport,
																userDefinedExportCols, userDefinedArabicCols,
																userDefinedExportLandScape, MultiRowDisplayValuesForExport,
																i-1 , userDefinedHideRowNumInGrid,
																haveGrouping		, haveSumCols,
																userDefined_x_panelclass, UserDefindEditFormEnctype,
																UserDefinedPageRows));
			view.append(gridHtml);
			view.append("</tbody></table>");
	
			view.append("</div><input type='hidden' name='smartyhiddenmultieditrowsno' value='"+rowsno+"'/>"
					+ "<button type='submit' style ='margin-right:20px;padding:10px; float:left' class=' fa fa-save fa-lg' value='save'/>");
			view.append("</div>");
			
			view.append(jsmgr.getHotLookupForMultiGrid (userDefinedEditCols , userModifyTD , userDefinedLookups , userDefinedEditLookups, userDefinedEditColsHtmlType,
					userDefinedColsMustFill));
			view.append("</form></div></div></div>");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {rs.close();} catch (Exception e) { /* ignored */} 
			if (rsmd != null) {rsmd = null;}
			try {pst.close();} catch (Exception e) { /* ignored */}
		}
		return view;
	}
	/*
	 * Generate Single Record Update Form.
	 */
	@SuppressWarnings("unchecked")
	public StringBuilder getUpdForm(HttpServletRequest rqs){
		int rowNum = 0;
		if (userDefinedEditCols.isEmpty())//To check if the userDefinedEditCols Had not been Set
			return new StringBuilder("The Edit Columns List is Not Set");
		
		StringBuilder editForm = new StringBuilder("");
		String keyVal= rqs.getParameter(keyCol)  ,  BackGroundColor="" , 
			   jsValidatorMustFill ="" , jsValidatorNumeric ="" , Readonly="" , sql_load_row = "select ",
			    labelName="";
		boolean Disabled = false , first = true , hidden = false , required = false;
		ResultSet rsload=null ;
		ResultSetMetaData rsmd = null;
		PreparedStatement pst = null;
		ArrayList<String> displayValue = new ArrayList<String>();	
		HashMap <String, String> originalValue = new HashMap<String,String>();
		jsmgr.userDefinedColsHtmlType = userDefinedEditColsHtmlType;
		for(String parameter : userDefinedEditCols) {
			if (!first)
				sql_load_row = sql_load_row+" , ";
			
			if (userDefinedEditMockUpCols.containsKey(parameter)){
				if (userDefinedEditMockUpCols.get(parameter).trim().equals(""))
					sql_load_row += " '' as "+parameter;
				else
					sql_load_row += " "+userDefinedEditMockUpCols.get(parameter)+" as "+parameter;
			}else
				sql_load_row +=parameter;
			first = false;
		}
		//allSqlCols      = mysqlmgr.getMetaDataSqlColsList(conn,sql_load_row+" from "+mainTable+" where 1=0");
		//allSqlColsTypes = mysqlmgr.getsqlColsTypes();
		//sqlColsSizes    = mysqlmgr.getsqlColsSizes();
		
		sql_load_row = sql_load_row+" from "+mainTable+" where "+keyCol+" =?";
		//System.out.println("sql_load_row=============================>"+sql_load_row);
		String labelClass ="control-label col-md-2 col-sm-2 col-xs-12";
		String inputClass ="col-md-4 col-sm-4 col-xs-12";
		if (userDefinedEditFormColNo>=3){
			labelClass="control-label col-md-1 col-sm-1 col-xs-12";
			inputClass = "col-md-3 col-sm-3 col-xs-12";
		}
		
		try{
			pst = conn.prepareStatement(sql_load_row);
			//System.out.println("sql_load_row--->"+sql_load_row);
			pst.setString(1, keyVal);
			rsload= pst.executeQuery();
			rsmd = rsload.getMetaData();
		}catch (Exception e){
			logErrorMsg = "query origin is from class=>"+myClassBean+",sql_load_row=>"+sql_load_row+
					"Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			logErrorMsg = "";
			System.out.println("error at publishEditFormData,sql_load_row=>"+sql_load_row);
			e.printStackTrace();
		}
		if (UserDefindEditFormEnctype ==null || UserDefindEditFormEnctype.trim().isEmpty() || UserDefindEditFormEnctype.equalsIgnoreCase(""))
			for (String key :userDefinedEditCols){
				 if (blobList.contains(allSqlColsTypes.get(key)) || blobList.contains(userDefinedEditColsHtmlType.get(key)) ){
					 if (!userDefinedStoreFileNameColumns.containsKey(key)){
						 try{rsload.close();}catch(Exception e){/*ignore*/}
						 try{pst.close();}catch(Exception e){/*ignore*/}
						 return new StringBuilder("Col=>"+key+", is a blob , and does not have symetric col to save the name"
						 		+ "</br> please set the userDefinedStoreFileNameColumns");
					 }
					 UserDefindEditFormEnctype = "enctype='multipart/form-data'";
					 break;
				 }
			}
		editForm.append("<div class='row'><div class='col-md-12 col-sm-12 col-xs-12'><div class='x_panel'>");
		editForm.append("<div class='x_title'> <h2>"+updCaption+"</h2><div class='clearfix'></div></div>");
		editForm.append("<div class='x_content'><br />"
		+ "<form id='"+myClassBean+"' name='"+myClassBean+"' action='?myClassBean="+myClassBean+"&upd=1' method='POST'"
		+ " data-parsley-validate class='form-horizontal form-label-left' "+UserDefindEditFormEnctype+" "
				+ "onsubmit=\"$('#smarty_btn_save_update').prop(\'disabled\', true); return true;\">");
		boolean startFieldSet = false;
		for (String removeFileNameCol : userDefinedStoreFileNameColumns.keySet()){
			if (userDefinedNewCols.contains(removeFileNameCol)){
				if (userDefinedEditCols.contains(userDefinedStoreFileNameColumns.get(removeFileNameCol)))
					userDefinedEditCols.remove(userDefinedStoreFileNameColumns.get(removeFileNameCol));
				else{
					try{rsload.close();}catch(Exception e){/*ignore*/}
					try{pst.close();}catch(Exception e){/*ignore*/}
					return new StringBuilder(removeFileNameCol+" does not have userDefinedStoreFileNameColumns column");
				}
			}
		}
		//System.out.println(userDefinedEditCols);
		try{
			if (rsload.next()){
				for (String key :userDefinedEditCols){
					//save the original values
					originalValue.clear();
					for (int col=1 ; col<=rsmd.getColumnCount() ; col++)
		        		originalValue.put(rsmd.getColumnLabel(col), rsload.getString(rsmd.getColumnLabel(col)));
					
					if (!userDefinedHiddenEditCols.contains(key)){//if not hidden
						 if(userDefinedFieldSetCols!=null)
							 if (userDefinedFieldSetCols.containsKey(key)){
								 editForm.append("<fieldset class='scheduler-border'>");
								 editForm.append("<legend class='scheduler-border'>"+userDefinedFieldSetCols.get(key)+"</legend>");
								 startFieldSet = true;
							 }
						if (userDefinedEditFormColNo==1){
							editForm.append("<div class='form-group'>");
						}else if (rowNum%userDefinedEditFormColNo==0 || rowNum==0){
							editForm.append("<div class='form-group'>");
						}	 
						rowNum++; 
						displayValue.clear();
						required = false;
						if (userDefinedColsMustFill.contains(key)){//check for the must fill						
							required = true;	
						}	
						if (!userDefinedColLabel.containsKey(key))
							labelName = key;
						else
							labelName = userDefinedColLabel.get(key);
						
						if (numberList.contains(allSqlColsTypes.get(key))){
							jsValidatorNumeric = jsValidatorNumeric +jsmgr.genJSNumericValidation(key, labelName);
						}
						if (userDefinedEditColsLineSeperator.contains(key))
							editForm.append("<div class='divider-dashed'></div>");
						
						editForm.append("<label id='"+key+"_label' class='"+labelClass+"' >"+labelName+" ");
						if(required)
							editForm.append("<span class='required'>*</span>");
						editForm.append("</label>");
						
						if (userDefinedEditColsDefualtValues.containsKey(key))
							if(userDefinedEditColsDefualtValues.get(key)!=null)
								for (String val : userDefinedEditColsDefualtValues.get(key))
									displayValue.add(val);

						if (rsload.getString(key) !=null){
							displayValue.add(rsload.getString(key));
						}
						//System.out.println(userDefinedLookups);
						if (userDefinedLookups!=null && userDefinedLookups.get(key)!=null){ 
							if (userDefinedLookups.get(key).startsWith("!")){
								//displayValue.clear();
								//displayValue = GenHotLookupForUpdForm(key , rsload);
								GenHotLookupForUpdForm(key , originalValue);
							}//End of Hot Lookup Treatment
							else if (userDefinedEditColsHtmlType!=null && 
									userDefinedEditColsHtmlType.containsKey(key) &&
								(userDefinedEditColsHtmlType.get(key).equalsIgnoreCase("CHECKBOX"))){
								// if collection then we need to split the string to defvalue
								ArrayList<String> newDefValue = new ArrayList<String>();
								for (int i =0 ; i < displayValue.size() ; i++){
									String [] arr = displayValue.get(i).split(userDefinedcolCollectionDelimmiter);
									for (int j =0;j< arr.length ; j++)
										newDefValue.add(arr[j]);
								}
								displayValue.clear();
								displayValue = newDefValue;	
							}
						}
						Readonly="";
						if(userDefinedReadOnlyEditCols.contains(key))
							Readonly = "readonly";
						else
							Readonly = "";
							
						if (userDefinedDisabledEditCols.contains(key))
							Disabled = true;
						else
							Disabled = false;
						hidden = false;
						if (userDefinedColsMustFill.contains(key)){//check for the must fill
							BackGroundColor="#fcfae5";
						}else{
							BackGroundColor="#F0FFF0";
						}
						editForm.append("<div class='"+inputClass+"' div_forupd_input_smarty='smarty_updcol_"+key+"'>");
						if (userDefinedEditColsHtmlType!=null && userDefinedEditColsHtmlType.get(key)!=null 
								&& userDefinedEditColsHtmlType.get(key).equals("IMAGE")){
							//may be this need to be changed to come from shapeFactory
							if ((userDefinedImageDisplayServlet.get(key)) !=null && (!userDefinedImageDisplayServlet.get(key).trim().equals(""))){
								editForm.append("<img src='"+userDefinedImageDisplayServlet.get(key)+"?"+keyCol+"="+keyVal+"'></img>");
							}
						}
						if (mysqlmgr.isFile(key) || blobList.contains(userDefinedEditColsHtmlType.get(key))){
							displayValue.add(rsload.getString(userDefinedStoreFileNameColumns.get(key)));
						}
						//System.out.println("key===>"+key+"========>"+displayValue);
						editForm.append(
								myhtmlmgr.GetHtmlInput(userDefinedEditColsHtmlType, colMapValues,
													   key						 , displayValue, 
													   sqlColsSizes 			 , Readonly , 
													   Disabled					 , userDefinedLookups, 
													   BackGroundColor 			 , hidden,
													   null						 , required,
													   false,0));	
						editForm.append("</div>");
						if (userDefinedEditFormColNo==1){
							editForm.append("</div>"); //end of form group	 
						}else if ((rowNum)%userDefinedEditFormColNo==0 && rowNum>1){
							editForm.append("</div>");
						}
						if (startFieldSet){
							if (userDefinedFieldSetEndWithCols.contains(key)){
							 startFieldSet = false;
							 editForm.append("</fieldset>");
							 rowNum = userDefinedEditFormColNo;
							}
						}
					}else{// if hidden
						if (userDefinedEditColsDefualtValues.containsKey(key))
							if(userDefinedEditColsDefualtValues.get(key)!=null)
								for (String val : userDefinedEditColsDefualtValues.get(key))
									displayValue.add(val);
						if (!rsload.wasNull()){
							displayValue.add(rsload.getString(key));
						}
						//a treatment for the hot lookups.
						String loo="";
						String colToSearchBy="";
						if (userDefinedLookups!=null && userDefinedLookups.get(key)!=null){
							if (userDefinedLookups.get(key).startsWith("!")){
								displayValue.clear();
								displayValue = GenHotLookupForUpdForm(key , originalValue);
							}
						}
						hidden = true;
						//System.out.println("keyHidden===========>"+key);
						editForm.append(
							myhtmlmgr.GetHtmlInput(userDefinedEditColsHtmlType, colMapValues,
												   key						 , displayValue , 
												   sqlColsSizes 			 , Readonly , 
												   Disabled				     , userDefinedLookups,
												   BackGroundColor			 , hidden,
												   null,false,false,0));
					}//end if hidden
			
			editForm.append(genHotLookupsjs(key , required , true));
			}
		}//end of cols loop
		editForm.append("<input type='hidden' value='"+keyVal+"' name='"+hiddenKeyCol+"'/>");
		editForm.append("<div class='ln_solid_special'></div>");
		editForm.append(" <div class='form-group'>");
		editForm.append("<div class='col-md-6 col-sm-6 col-xs-12 col-md-offset-3'>");
		editForm.append("<button type='submit' id='smarty_btn_save_update'  value='save' class='btn btn-success save_form_btn'>حفظ</button>");
		editForm.append("<button type='submit' class='btn btn-primary cancel_form_btn' onclick=\"javascript:window.location.href='?'; return false;\" value='cancel'>رجوع</button>");
		
		editForm.append("</div>");
		editForm.append("</div>");
		editForm.append("</form>");//End of Form	
		editForm.append("<script> var frmvalidator  = new Validator('"+myClassBean+"');\n");
		editForm.append(jsValidatorNumeric);
		
		for (String param : jsmgr.colsUsedInLookupsForOtherCols.keySet()){
			editForm.append(jsmgr.getHotLookupCallingScript(param));
		}
		editForm.append("</script>");		
		}catch(Exception e){
			logErrorMsg = "query origin is from class=>"+myClassBean+",Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			logErrorMsg = "";
			e.printStackTrace();
		}finally{
			try {rsload.close();} catch (SQLException e) { /* ignored */}
			try {pst.close();} catch (SQLException e) { /* ignored */}
		}
		return editForm;
	}
	
	/*
	 * Display Single Record
	 */
	public StringBuilder displaySingleRecordForm(HttpServletRequest rqs){
		int rowNum = 0;
		StringBuilder singleForm = new StringBuilder("");
		String keyVal= rqs.getParameter(keyCol)  ,  BackGroundColor=""  , Readonly="", labelName="";
		boolean Disabled = false , hidden = false , required = false , haveLookup =false, userModifiedTD=false; 
		ResultSet rsload=null;
		PreparedStatement pst = null;
		ResultSetMetaData rsmd =null;
		String displayValue, colToSearchBy;	
		String labelClass ="control-label col-md-2 col-sm-2 col-xs-12";
		String inputClass ="col-md-4 col-sm-4 col-xs-12 display_single_data";
		HashMap <String , String> originalValue = new HashMap<String , String>();
		if (userDefinedEditFormColNo>=3){
			labelClass="control-label col-md-1 col-sm-1 col-xs-12";
			inputClass = "col-md-3 col-sm-3 col-xs-12 display_single_data";
		}
		try{
			search_paramval.put(keyCol, new String [] {keyVal});
			rsload = mysqlmgr.LoadData(conn    , pst				 , rsload		   , UserDefinedPageRows , currentpage , MainSql  ,  search_paramval , 
					   userDefinedWhere 	   , orderByCols 		 , sortColName , sortMode ,  userDefinedGroupByCol, 
					   userDefinedGroupSortMode, userDefinedGroupColsOrderBy , userDefinedNewColsHtmlType ,userDefinedFilterColsUsingLike );
			rsmd = rsload.getMetaData();
		}catch (Exception e){
			logErrorMsg = "query origin is from class=>"+myClassBean+", method displaySingleRecordForm,MainSql=>"+MainSql+
					"Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			logErrorMsg = "";
			System.out.println("error at CoreMgr.displaySingleRecordForm,MainSql=>"+MainSql);
			e.printStackTrace();
		}
		
		singleForm.append("<div class='row'>"
				+ "<div class='col-md-12 col-sm-12 col-xs-12'>"
				+ "<div class='x_panel'>");
		singleForm.append("<div class='x_title'> <h2>"+userDefinedCaption+"</h2><div class='clearfix'></div></div>");
		singleForm.append("<div class='x_content'><form class='form-horizontal form-label-left'>");
		boolean startFieldSet = false;

		try{
			if (rsload.next()){
				originalValue.clear();
				for (int col=1 ; col<=rsmd.getColumnCount() ; col++)
	        		originalValue.put(rsmd.getColumnLabel(col), rsload.getString(rsmd.getColumnLabel(col)));
				for (String key :userDefinedGridCols){
					haveLookup = false; 
					userModifiedTD = false;
					displayValue = rsload.getString(key);
					 
					if (colMapValues!=null){// this will work only for normal lookups
						
					   if(colMapValues.containsKey(key)){//get the column name , then get the code to get the description
						   
						   if (userDefinedNewColsHtmlType!=null && userDefinedNewColsHtmlType.containsKey(key) 
								   && userDefinedNewColsHtmlType.get(key).equals("CHECKBOX")){
							   ArrayList<String> colsInSql =mysqlmgr.getMetaDataSqlColsList(conn,userDefinedLookups.get(key));
								if (colsInSql.size()==1)
									displayValue = "error in lookup"+key;
								colToSearchBy = colsInSql.get(0);
							   displayValue = getCollectionColumnData(conn, userDefinedLookups.get(key), displayValue, colToSearchBy);
						   }else {
							 //  System.out.println("bef key=>"+key+", displayValue=>"+displayValue);
							   displayValue = colMapValues.get(key).get(rsload.getString(key));
							 //  System.out.println("af key=>"+key+", displayValue=>"+displayValue);
						   }   
						   haveLookup = true;
						}
				    }
					//System.out.println("after   key=>"+key+", displayValue=>"+displayValue);
				    //now we need to have a treatment for the hot lookups.
					if (userDefinedLookups!=null && userDefinedLookups.get(key)!=null && userDefinedLookups.get(key).startsWith("!")){
						haveLookup = true; 
						displayValue = GenHotLookupValForListing(conn, userDefinedLookups.get(key),  key , displayValue , originalValue);
					}
				    
					if (rsload.wasNull()) displayValue ="";
					
					if (userModifyTD !=null)// the TD here will not work
						if (userModifyTD.containsKey(key)){
							displayValue = getValueFromUserMethodForThisColumn(key,originalValue);
							userModifiedTD = true;
						}
					if (!userModifiedTD)
						if (mysqlmgr.isFile(key)){
							boolean showLink = false;
							if (userDefinedStoreFileNameColumns!=null)
								if (userDefinedStoreFileNameColumns.get(key)!=null){
									if (originalValue.get(userDefinedStoreFileNameColumns.get(key))!=null){
										displayValue = myhtmlmgr.getDownloadFileButton(key ,keyVal , myClassBean);
										showLink = true;
									}
								}
							if (!showLink)
								 displayValue="N/A";
						}
					//format if the data is number and never had lookup and never been modified
					if (numberList.contains(allSqlColsTypes.get(key))&& (!haveLookup) && (!userModifiedTD)){
						if (displayValue == null){
							displayValue = "0.0";
						}else if (displayValue==""){
							displayValue = "0.0";
						}
						displayValue = numFormat.format(Double.parseDouble(displayValue));
							
					}else if ((dateList.contains(allSqlColsTypes.get(key.toUpperCase())))
								&& (!haveLookup) && (!userModifiedTD)){
						if (displayValue == null){
							displayValue = "0.0";
						}else if (displayValue==""){
							displayValue = "0.0";
						}
						//System.out.println("b4 format==>"+displayValue);
						displayValue = dateformat.format(rsload.getDate(key));
						//System.out.println("after format==>"+displayValue);
					}	
					
					if(userDefinedFieldSetCols!=null)
						if (userDefinedFieldSetCols.containsKey(key)){
							singleForm.append("<fieldset class='scheduler-border'>");
							singleForm.append("<legend class='scheduler-border'>"+userDefinedFieldSetCols.get(key)+"</legend>");
							startFieldSet = true;
						}
					if (userDefinedEditFormColNo==1){
						singleForm.append("<div class='form-group' style='padding-top:10px;margin-bottom:15px'>");
					}else if (rowNum%userDefinedEditFormColNo==0 || rowNum==0){
						singleForm.append("<div class='form-group' style='padding-top:10px;'>");
					}	 
					rowNum++; 
					
					if (!userDefinedColLabel.containsKey(key))
						labelName = key;
					else
						labelName = userDefinedColLabel.get(key);
					if (userDefinedEditColsLineSeperator.contains(key))
						singleForm.append("<div class='divider-dashed'></div>");	
					singleForm.append("<label id='"+key+"_label' class='"+labelClass+"' >"+labelName+" </label>");

					if (userDefinedNewColsHtmlType!=null && userDefinedNewColsHtmlType.get(key)!=null 
								&& userDefinedNewColsHtmlType.get(key).equals("IMAGE")){
								//may be this need to be changed to come from shapeFactory
						if ((userDefinedImageDisplayServlet.get(key)) !=null 
								&& (!userDefinedImageDisplayServlet.get(key).trim().equals(""))){
							singleForm.append("<img src='"+userDefinedImageDisplayServlet.get(key)+"?"+keyCol+"="+keyVal+"'></img>");
						}
					}
					if (mysqlmgr.isFile(key) || blobList.contains(userDefinedEditColsHtmlType.get(key))){
						displayValue = rsload.getString(userDefinedStoreFileNameColumns.get(key));
					}
					singleForm.append("<div class='"+inputClass+"' smarty_singledispalycol='smarty_singledispalycol_"+key+"'>");
					singleForm.append(displayValue);
					singleForm.append("</div>");
					if (userDefinedEditFormColNo==1){
						singleForm.append("</div>"); //end of form group	 
					}else if ((rowNum)%userDefinedEditFormColNo==0 && rowNum>1){
						singleForm.append("</div>");
					}
					if (startFieldSet){
						if (userDefinedFieldSetEndWithCols.contains(key)){
							startFieldSet = false;
							singleForm.append("</fieldset>");
							rowNum = userDefinedEditFormColNo;
						}
					}
				}
		}//end of cols loop
		
		singleForm.append("</div>");//close content div
		}catch(Exception e){
			logErrorMsg = "query origin is from class=>"+myClassBean+",Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			logErrorMsg = "";
			e.printStackTrace();
		}finally{
			try {rsload.close();} catch (Exception e) { /* ignored */}
			try {pst.close();} catch (Exception e) { /* ignored */}
		}
		singleForm.append("</form></div></div></div>");
		return singleForm;
	}
	/*
	 * Get New form to generate new form
	 */
	public StringBuilder getNewForm(){//Generte THe New Form
		if (userDefinedNewCols.isEmpty())//To check if the userDefinedNewCols Had not been Set
			return new StringBuilder("The New Columns List is Not Set");
		String labelName=null;
		int rowNum = 0 ;
		boolean required=false;
		jsmgr.userDefinedColsHtmlType = userDefinedNewColsHtmlType;
		StringBuilder newForm = new StringBuilder("");
		String jsValidatorNumeric="" , BackGroundColor="";
		ArrayList<String> displayValue = new ArrayList<String>();
		HashMap<String , String> tipsList = new HashMap<String ,String>();
		String Readonly="";
		boolean Disabled = false , hidden =false;
		String labelClass ="control-label col-md-2 col-sm-2 col-xs-12";
		String inputClass ="col-md-4 col-sm-4 col-xs-12";
		String myClassBeanNoDots = myClassBean.replace(".", "_dot_");
		if (userDefinedNewFormColNo>=3){
			labelClass="control-label col-md-1 col-sm-1 col-xs-12";
			inputClass = "col-md-3 col-sm-3 col-xs-12";
		}
		if (UserDefindNewFormEnctype ==null || !UserDefindNewFormEnctype.trim().isEmpty() || UserDefindNewFormEnctype.equalsIgnoreCase(""))
			//System.out.println("should go here");
			for (String key :userDefinedNewCols){
				 if (blobList.contains(allSqlColsTypes.get(key)) || blobList.contains(userDefinedNewColsHtmlType.get(key))){
					 if (!userDefinedStoreFileNameColumns.containsKey(key))
						 return new StringBuilder("Col=>"+key+", is a blob , and does not have symetric col to save the name"
						 		+ "</br> please set the userDefinedStoreFileNameColumns");
					 UserDefindNewFormEnctype = "enctype='multipart/form-data'";
					 break;
				 }
			}
		newForm.append("<div class='row'><div class='col-md-12 col-sm-12 col-xs-12'><div class='x_panel'>");
		newForm.append("<div class='x_title'> <h2>"+newCaption+"</h2><div class='clearfix'></div></div>");
		newForm.append("<div class='x_content'><br />"
		+ "<form id='"+myClassBeanNoDots+"' name='"+myClassBean+"' action='?myClassBean="+myClassBean+"&new=1' method='POST'"
		+ " data-parsley-validate class='form-horizontal form-label-left' "+UserDefindNewFormEnctype+">");
		boolean startFieldSet = false;
		//System.out.println(userDefinedNewCols);
		
		for (String removeFileNameCol : userDefinedStoreFileNameColumns.keySet()){
			if (userDefinedNewCols.contains(removeFileNameCol)){
				if (userDefinedNewCols.contains(userDefinedStoreFileNameColumns.get(removeFileNameCol)))
					userDefinedNewCols.remove(userDefinedStoreFileNameColumns.get(removeFileNameCol));
				else
					return new StringBuilder(removeFileNameCol+" does not have userDefinedStoreFileNameColumns column");
			}
		}
		//System.out.println(userDefinedNewCols);
		//System.out.println("userDefinedNewCols==============>"+userDefinedNewCols);
		for (String key :userDefinedNewCols){
			 if (!userDefinedHiddenNewCols.contains(key)){// if not hidden	
				 
				 if(userDefinedFieldSetCols!=null)
					 if (userDefinedFieldSetCols.containsKey(key)){
						 newForm.append("<fieldset class='scheduler-border'>");
						 newForm.append("<legend class='scheduler-border'>"+userDefinedFieldSetCols.get(key)+"</legend>");
						 startFieldSet = true;
					 }
				 if (userDefinedNewFormColNo==1){
					 newForm.append("<div class='form-group'>");
				 }else if (rowNum%userDefinedNewFormColNo==0 || rowNum==0){
					 newForm.append("<div class='form-group'>");
				 }	 
				 rowNum++; 
				if (!userDefinedColLabel.containsKey(key))
					labelName = key;
				else
					labelName = userDefinedColLabel.get(key);

				if (numberList.contains(allSqlColsTypes.get(key)))
					jsValidatorNumeric = jsValidatorNumeric +jsmgr.genJSNumericValidation(key , userDefinedColLabel.get(key));
				
				required = false;
				if (userDefinedColsMustFill.contains(key)){//check for the must fill
					required = true;
					//HtmlInForm = HtmlInForm + " <font size='4' color='red'>*</font>";
					//jsValidatorMustFill= jsValidatorMustFill+jsmgr.genJSMustFill(key , userDefinedColLabel.get(key));
				}
				newForm.append("<label id='"+key+"_label' class='"+labelClass+"' >"+labelName);
				if(required)
					newForm.append("<span class='required'> *</span>");
				newForm.append("</label>");
				newForm.append("<div class='"+inputClass+"' div_fornew_input_smarty='smarty_newcol_"+key+"'>");
				if (userColHintEDIT !=null){
					if (userColHintEDIT.containsKey(key)){
						    tipsList.put(key, "a_left_"+key);
						    newForm.append("<a id='a_left_"+key+"' href='#' >"+
									"<img src='../img/help.jpg' height =17 width=15 border=0></img></a>"+
									"<div id='tip1_left_"+key+"' style='display:none;'>"+
									"<pre class='tip'>"+userColHintEDIT.get(key)+"</pre></div>");
					}
				}
				displayValue.clear();
				if (userDefinedNewColsDefualtValues.containsKey(key))
					if(userDefinedNewColsDefualtValues.get(key)!=null)
						for (String val : userDefinedNewColsDefualtValues.get(key))
							displayValue.add(val);
				
				Readonly="";
				if(userDefinedReadOnlyNewCols.contains(key))
					Readonly = "readonly";
				else
					Readonly = "";
					
				if (userDefinedDisabledNewCols.contains(key))
					Disabled = true;
				else
					Disabled = false;
				
				hidden = false;
				
				if (userDefinedColsMustFill.contains(key)){//check for the must fill
					BackGroundColor="#fcfae5";
				}else{
					BackGroundColor="#FFFAFF";
				}
				newForm.append(myhtmlmgr.GetHtmlInput(userDefinedNewColsHtmlType, colMapValues,
												   key						 , displayValue , 
												   sqlColsSizes 			 , Readonly , 
												   Disabled				     , userDefinedLookups,
												   BackGroundColor			 , hidden,
												   null						 , required,
												   false,0));		
				newForm.append("</div>");
				if (userDefinedNewFormColNo==1){
					 newForm.append("</div>"); //end of form group	 
				 }else if ((rowNum)%userDefinedNewFormColNo==0 && rowNum>1){
					 newForm.append("</div>");
				 }
				 if (startFieldSet){
					 if (userDefinedFieldSetEndWithCols.contains(key)){
						 startFieldSet = false;
						 newForm.append("</fieldset>");
						 rowNum = userDefinedNewFormColNo;
					 }
				 }
			}else{// if hidden
				displayValue.clear();
				if (userDefinedNewColsDefualtValues.containsKey(key))
					if(userDefinedNewColsDefualtValues.get(key)!=null)
						for (String val : userDefinedNewColsDefualtValues.get(key))
							displayValue.add(val);
				hidden = true;
				newForm.append( 
						myhtmlmgr.GetHtmlInput(userDefinedNewColsHtmlType, colMapValues,
											   key						 , displayValue , 
											   sqlColsSizes 			 , Readonly , 
											   Disabled				     , userDefinedLookups,
											   BackGroundColor			 , hidden,
											   null						 , false,
											   false,0));
			}//end of hidden
			 newForm.append(genHotLookupsjs(key , required , false));
			 
		}//end of cols loop
		if (!((rowNum)%userDefinedNewFormColNo==0 && rowNum>1)){
			 newForm.append("</div>");
		}
		if (startFieldSet){
			newForm.append("</fieldset>");
		}
		newForm.append("<div class='ln_solid'></div>");
		newForm.append(" <div class='form-group'>");
			newForm.append("<div class='col-md-6 col-sm-6 col-xs-12 col-md-offset-3'>");
				newForm.append("<button type='submit' id='save_new_form_"+myClassBeanNoDots+"' value='save' class='btn btn-success save_form_btn'>حفظ</button>");
				newForm.append("<button type='submit' class='btn btn-primary cancel_form_btn' onclick=\"javascript:window.location.href='?'; return false;\" value='cancel'>رجوع</button>");
				
		newForm.append("</div>");
		newForm.append("</div>");
		newForm.append("</form>");//End of Form
		
		newForm.append("<script> var frmvalidator  = new Validator('"+myClassBean+"');\n");  
		newForm.append(jsValidatorNumeric);
		for (String param : jsmgr.colsUsedInLookupsForOtherCols.keySet()){
			newForm.append(jsmgr.getHotLookupCallingScript(param));
		}
		
		newForm.append("</script>");
		if (tipsList !=null){
			newForm.append("<script>$(document).ready(function() {");
			for (String key : tipsList.keySet()){
			//"a1_left_"+key
				newForm.append("$('#"+tipsList.get(key)+"').bubbletip($('#tip1_left_"+key+"'), {"+
							"deltaDirection: 'right',"+
							"animationDuration: 100,"+
							"offsetLeft: -20"+
						"});");
			}
			newForm.append("});</script>");
		}
		newForm.append("</div></div></div></div>");
		return newForm;
	}
	/*
	 * Generate HOTLOOKUP Java script
	 */
	public String genHotLookupsjs (String keyCol , boolean required , boolean isEdit){
		String js="<script>";
		String lookup = userDefinedLookups.get(keyCol);
		if (lookup!=null){
			if (lookup.startsWith("!")){
				if (isEdit)
					js = js+jsmgr.genHotLookup(lookup , keyCol , userDefinedEditColsHtmlType , userDefinedLookups , required);
				else
					js = js+jsmgr.genHotLookup(lookup , keyCol , userDefinedNewColsHtmlType , userDefinedLookups , required);
				//System.out.println("keyCol====>"+keyCol+", js==>"+js);
			}
		}
		js = js+"</script>";
		return js;
	}
	/*
	 * Generate values for listing from HotLookup sql
	 */
	private String GenHotLookupValForListing(String key ,String displayValue , HashMap<String ,String> originalValue){
		if (displayValue== null)
			return displayValue;
		
		if(displayValue.trim().equals("")){	return displayValue; }
		//System.out.println("displayValue==="+displayValue.length());
		String loo = userDefinedLookups.get(key);
		String colToSearchBy;
		String ParamName="";
		String dbValue ="";
		int startIndex ,endIndex ;
		ResultSet rsHotLookup = null  ;
		
		loo = loo.replaceFirst("!", "");
		while (loo.contains("{")){
			startIndex = loo.indexOf("{");
			endIndex = loo.indexOf("}");
			ParamName =loo.substring(startIndex+1, endIndex);
			loo = loo.replace("{"+ParamName+"}", originalValue.get(ParamName));
		}		
		ArrayList<String> colsInSql =mysqlmgr.getMetaDataSqlColsList(conn,loo);
		
		if (colsInSql.size()==1)
			return displayValue;
		
		colToSearchBy = colsInSql.get(0);
		
		//Hot lookup for Check Boxes (May be any collection later)
		if (userDefinedNewColsHtmlType.get(key).equals("CHECKBOX")){
			String sqlLookup="";
			
			String[] MultiOptions = displayValue.split(userDefinedcolCollectionDelimmiter);
			displayValue="<lu>";
			for (int j=0 ; j< MultiOptions.length ; j++){												 
				try{
					rsHotLookup = null;
					sqlLookup    = "select * from ("+loo+")myFakeLookup  where "+colToSearchBy+"='"+MultiOptions[j]+"'";
					rsHotLookup  = mysqlmgr.getRow(conn,sqlLookup);
					if (rsHotLookup!=null){
						if (rsHotLookup.getString(2)!=null){
							dbValue = rsHotLookup.getString(2);
						}else{
							dbValue = MultiOptions[j]+"-Error Mapping Lookup";
						}
					}else{
						dbValue = MultiOptions[j]+"-Error Mapping";
					}
					
				    displayValue= displayValue+"<li align='center'>"+dbValue+"</li>";
				  }catch (Exception e){
					logErrorMsg = "query origin is from class=>"+myClassBean+",sqlLookup=>"+sqlLookup+
					"Exception Msg=>"+e.getMessage(); 
					smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
							Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
					logErrorMsg = "";
					e.printStackTrace();
					System.out.println("Error at Parsing for CheckBoxes");
				  }
			 }
			 displayValue=displayValue+"</lu>";
		 }else{
			 loo = "select * from ("+loo+")myFakeLookup where "+colToSearchBy+"='"+displayValue+"'";
			 try{
				// System.out.println("loo==============>"+loo);
				 rsHotLookup = mysqlmgr.getRow(conn, loo);
				 if (rsHotLookup!=null){
						if (rsHotLookup.getString(2)!=null){
							displayValue = rsHotLookup.getString(2);
						}else{
							displayValue = displayValue+"-Error Mapping Lookup";
						}
					}else{
						displayValue = displayValue+"-Error Mapping";
					}
				 //displayValue= mysqlmgr.getRow(loo).getString(2);
			 }catch (Exception e){
				logErrorMsg = "query origin is from class=>"+myClassBean+",loo=>"+loo+
						"Exception Msg=>"+e.getMessage(); 
				smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
						Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
				logErrorMsg = "";
				e.printStackTrace();
				System.out.println("Error at Parsing ,Hot Lookup value not Found");
				displayValue="";
			 }finally{
				 try{rsHotLookup.close();}catch (SQLException e){/*ignore*/}
			 }
		 }
		 try{rsHotLookup.close();}catch (SQLException e){/*ignore*/}
	 return displayValue;
	}
	/*
	 * Gen Hot lookup for upd from
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<String>  GenHotLookupForUpdForm(String key , HashMap<String,String> rsload){
		 // this is treatment for hot lookup
		ArrayList<String> displyValue = new ArrayList<String>();
		displyValue.clear();
		String colToSearchBy;
		try{
			String loo = userDefinedLookups.get(key);
			String ParamName="";
			int startIndex ,endIndex ;
			loo = loo.replace("!", "");
			while (loo.contains("{")){
				startIndex = loo.indexOf("{");
				endIndex = loo.indexOf("}");
				ParamName =loo.substring(startIndex+1, endIndex);
				if (rsload.containsKey(ParamName) && rsload.get(ParamName)!=null)
					loo = loo.replace("{"+ParamName+"}", rsload.get(ParamName));
				else
					loo = loo.replace("{"+ParamName+"}", "");
				//loo = loo.replace("{"+ParamName+"}", rsload.get(ParamName));
			}
			ArrayList<String> colsInSql =mysqlmgr.getMetaDataSqlColsList(conn,loo);
			if (colsInSql.size()==1){
				displyValue.add(rsload.get(key));
				return displyValue;
			}
			colToSearchBy = colsInSql.get(0);			 
			LinkedHashMap<String,String> HotLookUpMap = new LinkedHashMap<String,String>();
			 // for check boxes
			 if (userDefinedEditColsHtmlType.get(key).equals("CHECKBOX")){
				 String[] MultiOptions = rsload.get(key).split(userDefinedcolCollectionDelimmiter); 
				 for (int j=0 ; j< MultiOptions.length ; j++){												 
					 displyValue.add(MultiOptions[j]);
				 }
				 
				 HotLookUpMap =  mysqlmgr.GetTwoDimMapData(conn,loo);
			 }else{	 
				 //loo = "select * from ("+loo+")smarty_lookup where "+colToSearchBy+"='"+rsload.getString(key)+"'";
				 loo = "select * from ("+loo+")smarty_lookup";
				// System.out.println(loo+", for column "+key);
				 try{
					 displyValue.add(rsload.get(key));
					 //HotLookUpMap.put(rsload.getString(key) , mysqlmgr.getRow(conn,loo).getString(1));
					 HotLookUpMap =  mysqlmgr.GetTwoDimMapData(conn,loo);
					 //System.out.println("--------------------------"+HotLookUpMap);
				 }catch (Exception e){
					logErrorMsg = "query origin is from class=>"+myClassBean+",loo=>"+loo+
							"Exception Msg=>"+e.getMessage(); 
					smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
							Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
					logErrorMsg = "";
					displyValue.clear();
					//System.out.println("clear the display value  for key="+key+",at GenHotLookupForUpdForm Method");
					e.printStackTrace();
				 } 
			 }
			 colMapValues.put(key,HotLookUpMap);
			// System.out.println(colMapValues+" now");
		}catch(Exception e){
			logErrorMsg = "query origin is from class=>"+myClassBean+",Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			logErrorMsg = "";
			e.printStackTrace();
		}
		return displyValue;
	}
	/*
	 * Delete Method
	 */
	public String doDelete(HttpServletRequest rqs){
		httpSRequest = rqs;
		String Msg ="";
		String keyVal= rqs.getParameter(keyCol);
		String sqlDelete = "delete from "+mainTable+" where "+keyCol+"=?";
		PreparedStatement pst = null;
		try{
		    pst = conn.prepareStatement(sqlDelete);
		    pst.setString(1, keyVal);
		    pst.executeUpdate();
		    conn.commit();
	    }catch (Exception e){
			logErrorMsg = "query origin is from class=>"+myClassBean+",sqlDelete=>"+sqlDelete+
					"Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			logErrorMsg = "";
		    e.printStackTrace();
		    Msg = "Error";
		    deleteErrorFlag = true;
		    try{conn.rollback();}catch (Exception eRollBack){eRollBack.printStackTrace();}
		}finally{
			try{pst.close();}catch(Exception e){/*ignore*/}
		}
		return Msg;
	}
	
	/*
	 * ParseRqs For Update
	 */
	public String parseUpdateRqs(HttpServletRequest rqs){
		keyVal ="";
		boolean isMultiPart = ServletFileUpload.isMultipartContent(httpSRequest);
		inputMap_ori = new LinkedHashMap<String,String[]>();
		inputFilesMap= new LinkedHashMap<String, FileItem>();
        if (isMultiPart) {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory); 
            List<FileItem> items = null;
			try {
				items = upload.parseRequest(httpSRequest);
			} catch (FileUploadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Iterator<FileItem> iter = items.iterator();
            while (iter.hasNext()) {
                FileItem fileItem = iter.next();
                if (fileItem.isFormField()) {
                	if (fileItem.getFieldName().equalsIgnoreCase(hiddenKeyCol)){
	                	try {
							keyVal = fileItem.getString("UTF-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}else{
                		try {
                			if (inputMap_ori.containsKey(fileItem.getFieldName())){
                				int size = inputMap_ori.get(fileItem.getFieldName()).length;
                				String [] arr = new String[size+1];
                				for (int i =0 ; i < size; i++)
                					arr[i] =inputMap_ori.get(fileItem.getFieldName())[i];
                				arr[size]= fileItem.getString("UTF-8");
                				inputMap_ori.remove(fileItem.getFieldName());
                				inputMap_ori.put(fileItem.getFieldName(),arr);
                			}else{
                				inputMap_ori.put(fileItem.getFieldName(), new String []{fileItem.getString("UTF-8")});
                			}
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                } else {
                	if (fileItem.getSize()>0)//to prevent update of the file
                		inputFilesMap.put(fileItem.getFieldName(),fileItem);
                }   	
            }
        }else{
		    keyVal= rqs.getParameter(hiddenKeyCol);
		    inputMap_ori = filterRequest(rqs);
        }
        return keyVal;
	}
	/*
	 * Do update Method	
	 */
	public String doUpdate(HttpServletRequest rqs , boolean autoCommit){
		String Msg ="";
		try {
				LinkedHashMap<String, FileItem> inputFilesMapOneRow = new LinkedHashMap<String, FileItem>();
				if (inputMap_ori==null || inputMap_ori.isEmpty())
					keyVal = parseUpdateRqs(rqs);
		
		        // treatement for checkboxes , as the checkboxes only submitted when they are ticked
		        // so if you have checkbox field in the update/new form, it will be added if not found 
		        for (int i=0 ; i< userDefinedEditCols.size() ; i++){
		        	if (!inputMap_ori.containsKey(userDefinedEditCols.get(i))){
		        		if (userDefinedEditColsHtmlType.containsKey(userDefinedEditCols.get(i))){
		        		    if(userDefinedEditColsHtmlType.get(userDefinedEditCols.get(i)).equalsIgnoreCase("CHECKBOX"))
		        			inputMap_ori.put(userDefinedEditCols.get(i), null);
		        		}
		        	}
		        }
				
				//System.out.println("hello===>"+keyVal);
				LinkedHashMap <String , String[]> inputMap = new LinkedHashMap <String , String[]>();
				String colName="";
				int rowsNo =0;
				if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
					rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
				if (getDisplayMode().equalsIgnoreCase("GRIDEDIT")){
					for (int row = 1 ; row<=rowsNo ; row++){ //UserDefinedPageRows should be changed to max rows displayed
						for (int col =0 ; col<userDefinedEditCols.size() ; col++){
							colName = userDefinedEditCols.get(col);
							if (inputFilesMap.containsKey(colName)){
								inputFilesMapOneRow.put(colName, inputFilesMap.get(colName));
							}else if (inputMap_ori.containsKey(colName+"_smartyrow_"+row)){
								inputMap.put(colName, inputMap_ori.get(colName+"_smartyrow_"+row));
								if (inputMap_ori!=null)
									if (inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+row)[0]!=null)
										keyVal = inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+row)[0];
							}
						}// end of columns loop
						
						Msg = mysqlmgr.doUpdate(conn,
												inputMap,
												inputFilesMapOneRow,
												userDefinedStoreFileNameColumns,
												allSqlColsTypes,
												keyVal,
												mainTable,
												keyCol,
												userDefinedEditColsHtmlType,
												autoCommit);
						inputMap.clear();
						inputFilesMapOneRow.clear();
					}// end of row loop
				}else{ // normal edit
					Msg = mysqlmgr.doUpdate(conn,
											inputMap_ori,
											inputFilesMap,
											userDefinedStoreFileNameColumns,
											allSqlColsTypes,
											keyVal,
											mainTable,
											keyCol,
											userDefinedEditColsHtmlType,
											autoCommit);
				}
		}catch(Exception e) {
			e.printStackTrace();
			setUpdateErrorFlag(true);
		}
		return Msg;
	}
	/*
	 * Do insert Method	
	 */
	public String doInsert(HttpServletRequest rqs , boolean autoCommit) {
		boolean isMultiPart = ServletFileUpload.isMultipartContent(httpSRequest);
		inputMap_ori = new LinkedHashMap<String,String[]>();
		inputFilesMap= new LinkedHashMap<String, FileItem>();
		LinkedHashMap<String, FileItem> inputFilesMapOneRow = new LinkedHashMap<String, FileItem>();
		String Msg ="";
        try {
			if (isMultiPart) {
	            FileItemFactory factory = new DiskFileItemFactory();
	            ServletFileUpload upload = new ServletFileUpload(factory); 
	            List<FileItem> items = null;
				items = upload.parseRequest(httpSRequest);
				Iterator<FileItem> iter = items.iterator();
				while (iter.hasNext()) {
	                FileItem fileItem = iter.next();
	                if (fileItem.isFormField()) {
	                	try {
	                		//System.out.println("fileItem.getString(\"UTF-8\")====>"+fileItem.getString("UTF-8")+", fileItem.getFieldName()-->"+fileItem.getFieldName());
	                		if (inputMap_ori.containsKey(fileItem.getFieldName())) {
	                			String [] newVa = new String [inputMap_ori.get(fileItem.getFieldName()).length+1];
	                			for (int mm =0 ; mm<inputMap_ori.get(fileItem.getFieldName()).length; mm++) {
	                				newVa[mm] = inputMap_ori.get(fileItem.getFieldName())[mm];
	                				
	                			}
	                			newVa[inputMap_ori.get(fileItem.getFieldName()).length] = fileItem.getString("UTF-8");
	                			inputMap_ori.put(fileItem.getFieldName(), newVa);
	                		}else {
	                			inputMap_ori.put(fileItem.getFieldName(), new String []{fileItem.getString("UTF-8")});
	                		}
	                	}catch (UnsupportedEncodingException e) {
	                		e.printStackTrace();
						}
	                } else {
	                	inputFilesMap.put(fileItem.getFieldName(),fileItem);
	                }   	
				}
		    }else{
				    inputMap_ori = filterRequest(rqs);
				   /* for (String key:inputMap_ori.keySet()){
				    	System.out.println(key+"<====>"+inputMap_ori.get(key));
				    }*/
		        }
			
			LinkedHashMap <String , String[]> inputMap = new LinkedHashMap <String , String[]>();
			String colName="";
			//System.out.println(inputMap_ori);
			if (getDisplayMode().equalsIgnoreCase("NEWMULTI")){
				for(int row = 1 ; row<=UserDefinedPageRows ; row++){ //UserDefinedPageRows should be changed to max rows displayed
					for(int col =0 ; col<userDefinedNewCols.size() ; col++){
						colName = userDefinedNewCols.get(col);
						if (inputFilesMap.containsKey(colName)){
							inputFilesMapOneRow.put(colName, inputFilesMap.get(colName));
						}else{
							inputMap.put(colName, inputMap_ori.get(colName+"_smartyrow_"+row));
						}
					}// end of columns loop
					Msg =  mysqlmgr.doInsert(conn,
										 httpSRequest ,
										 inputMap,
										 inputFilesMapOneRow,
										 userDefinedStoreFileNameColumns , 
										 allSqlColsTypes, 
										 mainTable,
										 userDefinedNewColsHtmlType,
										 autoCommit);
					inputMap.clear();
					inputFilesMapOneRow.clear();
				}// end of row loop
			}else{ // normal insert
				Msg =  mysqlmgr.doInsert(conn,
									 httpSRequest,
									 inputMap_ori,
									 inputFilesMap, 
									 userDefinedStoreFileNameColumns,
									 allSqlColsTypes, 
									 mainTable,
									 userDefinedNewColsHtmlType,
									 autoCommit);
			}
        }catch (Exception e) {
				e.printStackTrace();
				setInsertErrorFlag(true);
		}
		return Msg;
	}
	/*
	 * setters and getters
	 */
	public void setmyClassBean(String BeanName){
		this.myClassBean = BeanName;
	}
	
	public void setcurrentpage(int currentpage) {
		this.currentpage = currentpage;
	}
	public void setConn(Connection conn){
		this.conn = conn;
//		mysqlmgr.setConn(this.conn);
	}
	public Connection getConn(){
		return this.conn;
	}
	
	
	public int getcurrentpage() {
		return this.currentpage;
	}

	public void setCurrentpage(int currentpage) {
		this.currentpage = currentpage;
	}	
	public ArrayList<String> getFltr_col_name(){
		return userDefinedFilterCols;
	}
	public Map <String , String[]> getSearch_paramval() {
		return search_paramval;
	}
	/*
	 * set the filter map and remove unwanted paramters which are already used by the framework
	 */
	public void setSearch_paramval(Map <String , String[]> search_paramval) {
		this.search_paramval =new HashMap <String , String[]>();
		// copy the map , reason behind that is to have more control over the map,
		// so we can remove key or update them , if i copy the exact map , it's locked map.
		if (search_paramval!=null){
			for (String key :search_paramval.keySet()){
				this.search_paramval.put(key, search_paramval.get(key));
			}
			this.search_paramval.remove("dosearch");
			this.search_paramval.remove("cancelfilter_"+myClassBean);
			this.search_paramval.remove("myClassBean");
			this.search_paramval.remove("filter");
			this.search_paramval.remove("op");
			this.search_paramval.remove("pageName");
			this.search_paramval.remove("className");
		}
	}

	public String getErrorvalidation() {
		return errorvalidation;
	}

	public void setarrayGlobals(HashMap arrayGlobals){
		this.arrayGlobals = arrayGlobals;
	}
	
	public void setErrorvalidation(String errorvalidation) {
		this.errorvalidation = errorvalidation;
	}
	
	/*public Map <String , String> getEditColError() {
		return EditColError;
	}	
	public String GetEditErrMSG(String p_Key){
		return EditColError.get(p_Key);
	}
	public void setEditColError(HashMap <String , String> EditColError) {
		this.EditColError = EditColError;
	}
	*/
	public Map <String , String> getuserDefinedColLabel() {
		return userDefinedColLabel;
	}
	public SqlMgr getmysqlmgr(){
		return this.mysqlmgr;
	}
	
	public HTMLmgr getmyhtmlmgr(){
		return this.myhtmlmgr;
	}
	public String getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(String displayMode) {
		this.displayMode = displayMode;
	}
	
	public void setHTTPSRequest(HttpServletRequest httpSRequest){
		this.httpSRequest = httpSRequest;
	}
	
	public int getCurrentPage(HashMap smartyStateMap){
		int page=1;
		HashMap<String , Integer> pagesMap = new HashMap<String,Integer>();
		if (smartyStateMap.get(myClassBean)!=null){
			if (((HashMap<String, Integer>) smartyStateMap.get(myClassBean)).get("page")==null){
					pagesMap.put("page", 1);
					smartyStateMap.put(myClassBean,pagesMap);
			}	
		}else{
			pagesMap.put("page", 1);
			smartyStateMap.put(myClassBean,pagesMap);
		}
		
		if (httpSRequest!=null)
			if (httpSRequest.getParameter("myClassBean")!=null)
			if (httpSRequest.getParameter("myClassBean").equals(myClassBean))//Make sure the update of the page is only for that class
				if (httpSRequest.getParameterMap().containsKey("page"))
					if (httpSRequest.getParameter("page")!=null){
						page =Integer.parseInt(httpSRequest.getParameter("page"));
						((HashMap<String,Integer>) smartyStateMap.get(myClassBean)).put("page", page);
					}else{
						pagesMap.put("page", 1);
						smartyStateMap.put(myClassBean,pagesMap);
					}
		
		
		page =((HashMap<String,Integer>) smartyStateMap.get(myClassBean)).get("page"); 
		return page;
	}
	/*
	 * Prepare search data, this method is used to grab all the search data from the global map.
	 * and set or reset the search
	 */
	private void prepareSearchData(HashMap smartyStateMap){
		HashMap<String , String> sortingColMode = new HashMap<String , String>();
		
		if (httpSRequest!=null)
			if (httpSRequest.getParameter("myClassBean")!=null)
				if (httpSRequest.getParameter("myClassBean").equals(myClassBean)){
					if (httpSRequest.getParameter("cancelfilter_"+myClassBean)!=null){
						//search_paramval.clear();
						( (HashMap<String,HashMap>) smartyStateMap.get(myClassBean) ).remove("filter");
						httpSRequest.removeAttribute("smarty_cancelfilter");
					}else if (httpSRequest.getParameter("filter")!=null){
						setSearch_paramval(httpSRequest.getParameterMap());
						((HashMap<String,HashMap>) smartyStateMap.get(myClassBean)).put("filter", search_paramval);
					}
				}
		setSearch_paramval(((HashMap<String,HashMap>) smartyStateMap.get(myClassBean)).get("filter"));		
	}
	/*
	 * Preapre the sorting , currently we are saving the sorting mode in a global parameter.
	 */
	private void prepareSorting(HashMap smartyStateMap){
		sortColName ="";
		sortMode = "";
		HashMap<String , String> sortingColMode = new HashMap<String , String>(); 
			
		if (httpSRequest!=null)
			if (httpSRequest.getParameter("myClassBean")!=null)
			if (httpSRequest.getParameter("myClassBean").equals(myClassBean))//Make sure the update of the page is only for that class
				if (httpSRequest.getParameterMap().containsKey("sortingby"))
					if (httpSRequest.getParameter("sortingby")!=null){
						String sortMode =httpSRequest.getParameter("sortmode");
						sortingColMode.put("sortingby", httpSRequest.getParameter("sortingby"));
						sortingColMode.put("sortmode", sortMode);
						((HashMap<String,HashMap>) smartyStateMap.get(myClassBean)).put("sortby", sortingColMode);
					}
	}
	public void overRideCellData(){
		
	}

	public String getAPPPATH() {
		return APPPATH;
	}

	public void setAPPPATH(String aPPPATH) {
		APPPATH = aPPPATH;
	}
	public String getJspNameWithoutDot() {
		return jspNameWithoutDot;
	}

	public void setJspNameWithoutDot(String jspNameWithoutDot) {
		this.jspNameWithoutDot = jspNameWithoutDot;
	}

	public String getJspName() {
		return jspName;
	}

	public void setJspName(String jspName) {
		this.jspName = jspName;
	}

	public String getDBMSType() {
		return DBMSType;
	}

	public void setDBMSType(String dBMSType) {
		DBMSType = dBMSType;
		mysqlmgr.setDBMSType(dBMSType);
	}
	public LoginUser getLu() {
		return lu;
	}

	public void setLu(LoginUser lu) {
		this.lu = lu;
	}

	public boolean isUpdateAction() {
		return updateAction;
	}

	public void setUpdateAction(boolean updateAction) {
		this.updateAction = updateAction;
	}

	public boolean isInsertAction() {
		return insertAction;
	}

	public void setInsertAction(boolean insertAction) {
		this.insertAction = insertAction;
	}

}
