package smarty.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import smarty.db.mysql;

public class SqlMgr {
	private LinkedHashMap<String , LinkedHashMap<String , String>> colsLookupValues;
	private HashMap<String , String> sqlColsTypes;
	private HashMap<String , Integer> sqlColsSizes;
	private HashMap<String , String> arbicToEnglishNumbers;
	private HashMap<String , String> englishToArabicNumbers;
	private String logErrorMsg;
	private String myClassBean;
	private String executedSQL; // the currently Executed Sql  in LoadData function
	private String DBMSType;
	private String colCollectionDelimmiter =":";
	public static final List<String> blobList = Arrays.asList("TINYBLOB" , "BLOB" , "MEDIUMBOLB" , "LONGBLOB");
	public static final List<String> numberList = Arrays.asList("BIGINT", "DOUBLE" , "INT", "FLOAT");
	//protected Connection conn;
	
	// Constructor
	public SqlMgr(String myClassBean){
		//rs = null;
		//rRow = null;
		sqlColsTypes     = new HashMap<String , String>();
		sqlColsSizes     = new HashMap<String , Integer>();
		executedSQL      = "";
		this.myClassBean = myClassBean;
		this.DBMSType = "MySql";
	}

	/*
	 * This function return all the cols of the query
	 */
	public ArrayList<String> getMetaDataSqlColsList(Connection conn , String sql){
		PreparedStatement pst = null;
		ResultSetMetaData rsmd = null;
		ResultSet rs = null;
		ArrayList<String> sqlCols = new ArrayList<String>();
		sql = "select * from ("+sql+")myfaketable where 1=0";
		try{
			// Get Connection and Statement 
			 setExecutedSQL(sql);
			 pst = conn.prepareStatement(sql);
			 rs = pst.executeQuery();
	    	 rsmd = rs.getMetaData();
	    	 for (int coli=1 ;coli<=rsmd.getColumnCount() ; coli++){
	    		 sqlCols.add(rsmd.getColumnName(coli));
	    		 sqlColsTypes.put(rsmd.getColumnName(coli),rsmd.getColumnTypeName(coli));
	    		 sqlColsSizes.put(rsmd.getColumnName(coli) , rsmd.getColumnDisplaySize(coli));
	    	 }
		}catch (Exception e ){
			logErrorMsg = "query origin is from class=>"+myClassBean+",sql=>"+getExecutedSQL()+
					",Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			logErrorMsg = "";
			e.printStackTrace();
			System.out.println("Error At Function getMetaDataSqlColsList@SqlMgr.java ,executing thr query=>"+sql);
		}finally{
			 try{rs.close();}catch(Exception e){}
			 try{pst.close();}catch (Exception e){}	 
		}
		return sqlCols;
	}
	/*
	 * Will get the mapping for each column using the sql set in the java.
	 */
	public  LinkedHashMap<String , LinkedHashMap<String , String>> loadAllLookups(Connection conn,HashMap <String , String > ColsLookup){
		PreparedStatement pstLookup = null;
		ResultSet rs_lookup  = null;
		String sqlLookup="select 1";
		Map<String , String>lookupMap = new HashMap<String , String>();
		colsLookupValues = new LinkedHashMap<String , LinkedHashMap<String , String>>();
		try{
			pstLookup = conn.prepareStatement(sqlLookup);
			for (String keyCol : ColsLookup.keySet()){
				sqlLookup = ColsLookup.get(keyCol);
				if (!sqlLookup.startsWith("!")){//if query starts with !, that means we need to do hot lookup, else it's normal lookup
					setExecutedSQL(sqlLookup);
					rs_lookup = pstLookup.executeQuery(sqlLookup);
					lookupMap.clear();
					pstLookup.clearParameters();
					colsLookupValues.put(keyCol, new LinkedHashMap<String , String>());
					//System.out.println("ColsLookup===>"+ColsLookup);
					while(rs_lookup.next()){
						lookupMap.put(rs_lookup.getString(1), rs_lookup.getString(2));
						//System.out.println ("rs_lookup.getString(2)=>"+rs_lookup.getString(2));
						colsLookupValues.get(keyCol).put(rs_lookup.getString(1), rs_lookup.getString(2));
					}
				}else{//generate java script to call the myajax.jsp file which will do hot lookup
					//this to be done on the Core.mgr
					//nafi on (9-feb-2014) but we have problem for the grid view lookup.
				}
			}
		
		}catch (Exception e){
			logErrorMsg = "query origin is from class=>"+myClassBean+",sql=>"+getExecutedSQL()+
					",Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			logErrorMsg = "";
			e.printStackTrace();
		}finally{
			 try{rs_lookup.close();}catch(Exception e){}
			 try{pstLookup.close();}catch (Exception e){}
		}
		return colsLookupValues;
	}
	
	/*
	 * Get Two Dimension HashMap for specific Sql./ normally used for lookups
	 */
	public LinkedHashMap<String,String> GetTwoDimMapData(Connection conn,String sql){
		LinkedHashMap<String, String> TwoDimMap = new LinkedHashMap<String ,  String>();
		PreparedStatement pstTwoDimMap = null;
		ResultSet rs_TwoDimMap  = null;
		try{
			pstTwoDimMap = conn.prepareStatement(sql);
			setExecutedSQL(sql);
			rs_TwoDimMap = pstTwoDimMap.executeQuery();
			TwoDimMap.clear();
			while(rs_TwoDimMap.next()){
				TwoDimMap.put(rs_TwoDimMap.getString(1), rs_TwoDimMap.getString(2));
			}
		}catch(Exception e){
			logErrorMsg = "query origin is from class=>"+myClassBean+",sql=>"+getExecutedSQL()+
					",Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			logErrorMsg = "";
			e.printStackTrace();
			System.out.println("Error At SqlMgr Class , GetTwoDimMapData Method, sql=>"+sql);
		}finally{
			 try{rs_TwoDimMap.close();}catch(SQLException e){}
			 try{pstTwoDimMap.close();}catch (SQLException e){}
			 
		 }	
		return TwoDimMap; 
	}
	/*
	 * Load Data from DB , Return ResultSet Object.
	 */
	public ResultSet LoadData(Connection conn,
							  PreparedStatement pst,
							  ResultSet rs,
							  int PageRows , 
							  int currentpage ,
							  String MainSql, 
							  HashMap<String,String[]> Search_Param ,
							  String userDefinedWhere ,
							  String orderByCols,
							  String sortColName ,
							  String sortMode,
							  String userDefinedGroupByCol,
							  String userDefinedGroupSortMode,
							  String userDefinedGroupColsOrderBy,
							  HashMap <String,String> userDefinedHTMLType,
							  ArrayList<String> userDefinedFilterColsUsingLike,
							  ArrayList<String> userDefinedFilterColsUsingIn,
							  HashMap <String , String>   allSqlColsType
							  ){
		int startwith = PageRows*(currentpage-1);
		int endwith = PageRows*(currentpage);

		if (userDefinedWhere== null){
			userDefinedWhere = "";
		}
		
		WhereClauseResult whereClauseResult = null;
		try{
			// Enahancement to be done, 
			// 1- to include having.
			// 2- inner where clause treatement
			// 3- i need to give the power to the user to be able to manipulate the where clause
			// for implement DateRange
			
			if (userDefinedWhere == ""){
					if (Search_Param!=null){
						whereClauseResult = BuildWhereClause(Search_Param , userDefinedHTMLType, userDefinedFilterColsUsingLike, userDefinedFilterColsUsingIn, allSqlColsType);
						// we need to define the location of the where clause
						if (whereClauseResult != null && whereClauseResult.getClause()!=null && !whereClauseResult.getClause().trim().equalsIgnoreCase("")){
							String WhereClause = whereClauseResult.getClause();
							MainSql = insertSearchWhereClause(MainSql, WhereClause);
						}
					}
			}else{
				MainSql = MainSql + userDefinedWhere;
			}
			
			if (userDefinedGroupByCol != null){
				if (userDefinedGroupColsOrderBy !=null)
					MainSql = "select * From ("+MainSql+")smartyGrouped order by "+userDefinedGroupColsOrderBy+" "+ userDefinedGroupSortMode;// must add alias in mysql that the reason we have smartyGrouped
				else
					MainSql = "select * From ("+MainSql+")smartyGrouped order by "+userDefinedGroupByCol+ " "+ userDefinedGroupSortMode;// must add alias in mysql that the reason we have smartyGrouped
	    	
			}else{
				if (orderByCols!=null)
					if (!orderByCols.equals(""))
						MainSql= MainSql + " order by "+orderByCols;
			}
			
			
			//if the connection is oracle
			if (DBMSType.equalsIgnoreCase("oracle")){
				if (sortColName !=null)
					if (!sortColName.equals(""))
						MainSql = "select * From ("+MainSql+")mainTable order by "+sortColName+" "+sortMode;
			
				MainSql = "select *	from ( select a.*, ROWNUM rnum  from"
			                          +" ("+MainSql+") a" 
			                          +" where "
			                          + " ROWNUM <="+endwith+")"
			                          + " where "
			                          + "rnum  >="+startwith;
			}else{
			// if the connection is mysql
			  MainSql= MainSql+ " limit "+startwith+" , "+PageRows;
			// the sorintg is done per page , if the user wants to sort all the result by specified column
						// then he need to specify that in the sql;
				if (sortColName !=null)
					if (!sortColName.equals(""))
				        MainSql = "select * From ("+MainSql+")mainTable order by "+sortColName+" "+sortMode;
			}
			
			try{
				//System.out.println("MainSql b4 exec==>"+MainSql);
				//Save the Currenty Executing SLQ
				setExecutedSQL(formatSqlWithParameters(MainSql, whereClauseResult));
				// Get Connection and Statement 
				 pst = conn.prepareStatement(MainSql);
				 bindSearchParameters(pst, whereClauseResult);
				 rs = pst.executeQuery();
			
			}catch (Exception e ){
				logErrorMsg = "query origin is from class=>"+myClassBean+",sql=>"+getExecutedSQL()+
						",Exception Msg=>"+e.getMessage(); 
				smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
						Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
				logErrorMsg = "";
				e.printStackTrace();
				System.out.println("Error At Function LoadData@SqlMgr.java ,executing thr query=>"+MainSql);
			}
		}catch(Exception e){
			logErrorMsg = "query origin is from class=>"+myClassBean+",Higher level,sql=>"+getExecutedSQL()+
					",Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			logErrorMsg = "";
			e.printStackTrace();
			System.out.println("error at main sql load data Method,MainSql=>"+MainSql);
		}	
		return rs;
	}
	public static class WhereClauseResult {
		private final String clause;
		private final List<Object> parameters;

		public WhereClauseResult(String clause, List<Object> parameters) {
			this.clause = clause;
			this.parameters = parameters != null ? parameters : new ArrayList<Object>();
		}

		public String getClause() {
			return clause;
		}

		public List<Object> getParameters() {
			return parameters;
		}
	}

	private static boolean isAllowedSearchColumn(String column,
			HashMap<String, String> userDefinedHTMLType,
			ArrayList<String> userDefinedFilterColsUsingLike,
			ArrayList<String> userDefinedFilterColsUsingIn,
			HashMap<String, String> allSqlColsType) {
		if (column == null || column.equals("filter") || !isValidSqlIdentifier(column)) {
			return false;
		}
		if (allSqlColsType != null && allSqlColsType.containsKey(column)) {
			return true;
		}
		if (userDefinedHTMLType != null && userDefinedHTMLType.containsKey(column)) {
			return true;
		}
		if (userDefinedFilterColsUsingLike != null && userDefinedFilterColsUsingLike.contains(column)) {
			return true;
		}
		return userDefinedFilterColsUsingIn != null && userDefinedFilterColsUsingIn.contains(column);
	}

	private static boolean isValidSqlIdentifier(String identifier) {
		return identifier != null && identifier.matches("^[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)?$");
	}

	private static boolean isSafeNumericLiteral(String value) {
		return value != null && value.matches("-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?");
	}

	private static boolean isSafeDateLiteral(String value) {
		return value != null && value.matches("^\\d{4}-\\d{2}-\\d{2}( \\d{2}:\\d{2}(:\\d{2})?)?$");
	}

	private static Number parseNumericValue(String value) {
		if (value.contains(".") || value.toLowerCase().contains("e")) {
			return Double.valueOf(value);
		}
		return Long.valueOf(value);
	}

	private String insertSearchWhereClause(String mainSql, String whereClause) {
		String mainSqlLower = mainSql.toLowerCase();
		int fromIndex = mainSqlLower.lastIndexOf(" from ");
		if (fromIndex < 0) {
			return mainSql + " where " + whereClause;
		}

		String suffixLower = mainSqlLower.substring(fromIndex);
		int whereInSuffix = suffixLower.indexOf(" where ");
		if (whereInSuffix >= 0) {
			int insertAt = fromIndex + whereInSuffix + " where ".length();
			return mainSql.substring(0, insertAt) + whereClause + " and " + mainSql.substring(insertAt);
		}

		int groupByInSuffix = suffixLower.indexOf(" group by ");
		if (groupByInSuffix >= 0) {
			int insertAt = fromIndex + groupByInSuffix;
			return mainSql.substring(0, insertAt) + " where " + whereClause + " " + mainSql.substring(insertAt);
		}

		return mainSql + " where " + whereClause;
	}

	private void bindSearchParameters(PreparedStatement pst, WhereClauseResult whereClauseResult) throws SQLException {
		if (whereClauseResult == null || whereClauseResult.getParameters().isEmpty()) {
			return;
		}
		int paramIndex = 1;
		for (Object param : whereClauseResult.getParameters()) {
			if (param instanceof Number) {
				pst.setObject(paramIndex++, param);
			} else {
				pst.setString(paramIndex++, param != null ? param.toString() : null);
			}
		}
	}

	private String formatSqlWithParameters(String sql, WhereClauseResult whereClauseResult) {
		if (whereClauseResult == null || whereClauseResult.getParameters().isEmpty()) {
			return sql;
		}
		return sql + " /* params: " + whereClauseResult.getParameters() + " */";
	}

	private boolean appendInClauseCondition(StringBuilder innerCol, List<Object> parameters, String parameter,
			String value, HashMap<String, String> allSqlColsType) {
		ArrayList<String> inValues = CoreUtilities.SplitStringToArrayList(value, ":");
		List<Object> validValues = new ArrayList<Object>();
		for (String inValue : inValues) {
			if (inValue == null || inValue.equals("")) {
				continue;
			}
			if (allSqlColsType != null && allSqlColsType.containsKey(parameter) && allSqlColsType.get(parameter) != null
					&& numberList.contains(allSqlColsType.get(parameter))) {
				if (!isSafeNumericLiteral(inValue)) {
					continue;
				}
				validValues.add(parseNumericValue(inValue));
			} else {
				validValues.add(inValue);
			}
		}
		if (validValues.isEmpty()) {
			return false;
		}
		innerCol.append(parameter).append(" in (");
		for (int i = 0; i < validValues.size(); i++) {
			if (i > 0) {
				innerCol.append(",");
			}
			innerCol.append("?");
		}
		innerCol.append(") or ");
		parameters.addAll(validValues);
		return true;
	}

	/*
	 * Build the Where Clause. 
	 */
	public WhereClauseResult BuildWhereClause(HashMap <String,String[]> SearchFieldsVals , HashMap <String,String> userDefinedHTMLType , 
			ArrayList<String> userDefinedFilterColsUsingLike, ArrayList<String> userDefinedFilterColsUsingIn ,HashMap<String,String> allSqlColsType){
		String whereClause = "";
		List<Object> parameters = new ArrayList<Object>();
		StringBuilder innerCol;
		boolean found_param =false;
		boolean hasCondition  = false;
		// normally the syntax is (col1='') and (col2='')
		// BUT if you have more than one value for the same col so the syntax should be
		// (col1='' or col1='') and (col2='') , so you don't colse the paranthesis unless the col is changed
		for(String parameter : SearchFieldsVals.keySet()) { 
			innerCol = new StringBuilder();
			hasCondition = false;
			if (!parameter.equals("filter") && SearchFieldsVals.get(parameter)!=null
					&& isAllowedSearchColumn(parameter, userDefinedHTMLType, userDefinedFilterColsUsingLike, userDefinedFilterColsUsingIn, allSqlColsType)){
				innerCol.append("(");
				for (String value : SearchFieldsVals.get(parameter))
			    {	
			    	if ((value !=null) && (!value.equals("")))
					{
						if (userDefinedHTMLType!=null && userDefinedHTMLType.containsKey(parameter)
								&& (userDefinedHTMLType.get(parameter).equalsIgnoreCase("CHECKBOX") || 
										userDefinedHTMLType.get(parameter).equalsIgnoreCase("MULTILIST"))){
							if (userDefinedFilterColsUsingIn!=null && userDefinedFilterColsUsingIn.size()>0 && userDefinedFilterColsUsingIn.contains(parameter)) {
								if (appendInClauseCondition(innerCol, parameters, parameter, value, allSqlColsType)) {
									hasCondition = true;
								}
							}else {
								innerCol.append(parameter).append(" like ? or ");
								parameters.add("%"+value+colCollectionDelimmiter+"%");
								innerCol.append(parameter).append("=? or ");
								parameters.add(value);
								hasCondition = true;
							}
						}else if (userDefinedHTMLType!=null && userDefinedHTMLType.containsKey(parameter)
								&& userDefinedHTMLType.get(parameter).equalsIgnoreCase("TIMESTAMP")) {
							if (!isSafeDateLiteral(value)) {
								continue;
							}
							innerCol.append("date(").append(parameter).append(")=? or ");
							parameters.add(value);
							hasCondition = true;
						}else if (userDefinedHTMLType!=null && userDefinedHTMLType.containsKey(parameter)
								&& (userDefinedHTMLType.get(parameter).equalsIgnoreCase("PHONE"))){
									arbicToEnglishNumbers = getArabicToEnglishNumbersMap();
									String otherValue= "";
									if (arbicToEnglishNumbers.containsKey(value.charAt(0)+"")) //then the input is arabic number
										for (int i=0 ; i<value.length(); i++) {
											otherValue += arbicToEnglishNumbers.get(value.charAt(i)+"");
										}
									else {
										englishToArabicNumbers = getEnglishToArabicNumbersMap();
										for (int i=0 ; i<value.length(); i++) {
											otherValue += englishToArabicNumbers.get(value.charAt(i)+"");
										}
									}
									innerCol.append("(").append(parameter).append("=? or ").append(parameter).append("=? ) or ");
									parameters.add(value);
									parameters.add(otherValue);
									hasCondition = true;
						}else{
							if (userDefinedFilterColsUsingLike!=null && userDefinedFilterColsUsingLike.size()>0 && userDefinedFilterColsUsingLike.contains(parameter)) {// here we search for the exact 
								innerCol.append(parameter).append(" like ? or ");
								parameters.add("%"+value+"%");
								hasCondition = true;
							}else {
								if ( (allSqlColsType!=null && allSqlColsType.containsKey(parameter) && allSqlColsType.get(parameter)!=null && numberList.contains(allSqlColsType.get(parameter))) 
										) {
									if (!isSafeNumericLiteral(value)) {
										continue;
									}
									innerCol.append(parameter).append("=? or ");
									parameters.add(parseNumericValue(value));
									hasCondition = true;
								} else {
									innerCol.append(parameter).append("=? or ");
									parameters.add(value);
									hasCondition = true;
								}
							}
						}
					} 
			    }
				if(hasCondition){
					found_param = true;
					innerCol.setLength(innerCol.length()-3);
					innerCol.append(") and ");
					whereClause += innerCol.toString();
				}
			}
		}
		if (!found_param){
			return null;
		}
		if (whereClause.length()>=5)
			whereClause = whereClause.substring(0,whereClause.length()-5);
		/*System.out.println(whereClause);*/
		return new WhereClauseResult(whereClause, parameters);
	}
	
	/*
	 * Do automatic update
	 */
	public String doUpdate(Connection conn,
						   ArrayList<String> userDefinedEditCols,
						   LinkedHashMap<String , String[]> inputMap,
						   LinkedHashMap<String, FileItem> inputFilesMap,
						   HashMap<String,String>userDefinedStoreFileNameColumns,
						   Map<String,String> coltypesDB, 
						   String keyVal, 
						   String mainTable, 
						   String keyCol,
						   HashMap<String,String>userDefinedNewHTMLType,
						   boolean autoCommit)throws Exception{
		String upd_sql = genUpdateStatment(userDefinedEditCols, inputMap ,inputFilesMap,userDefinedStoreFileNameColumns,  mainTable , keyCol);
		//System.out.println("upd_sql=>"+upd_sql);
		String All_values = "" , Msg="Record Updated";
		PreparedStatement pst  = null;
		try{
			pst = conn.prepareStatement(upd_sql);
			setExecutedSQL(upd_sql); // we need to log the paramter values also
			int paramCount =1;//the number of paramters.
			boolean first = true;
			boolean moreThanOneRow = false;
			
			for(String field : userDefinedEditCols) {
				first = true;
				moreThanOneRow = false;
				if (inputFilesMap.containsKey(field)) {
					pst.setBinaryStream(paramCount,
							((FileItem)inputFilesMap.get(field)).getInputStream() ,
							(int)((FileItem)inputFilesMap.get(field)).getSize());					
					paramCount++;
					pst.setString(paramCount , ((FileItem)inputFilesMap.get(field)).getName());
					paramCount++;
				}else {
					if (inputMap.get(field)!=null){
						for (String value : inputMap.get(field)){
					    	if (!first){
					    		All_values +=colCollectionDelimmiter;
					    		moreThanOneRow = true;
					    	}
					    	All_values = All_values+value;
					    	first = false;
					    }
						if (moreThanOneRow ||
								(userDefinedNewHTMLType!=null && 
								userDefinedNewHTMLType.get(field).equalsIgnoreCase("CHECKBOX")))
							All_values +=colCollectionDelimmiter;
					}
					//My sql Col Formats{BIGINT,DATETIMEDATE,BIGINT,DOUBLE,VARCHAR}
					if (coltypesDB.get(field).equals("DATE")){
						if (All_values.equals("")){
							pst.setNull(paramCount,Types.DATE);
						}else{
							java.util.Date javaDate= new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(All_values);
							Date date = new Date (javaDate.getTime());
							pst.setDate(paramCount,date);
						}	
					}else if (coltypesDB.get(field).equals("VARCHAR")){ 
						if (All_values.equals("")){
							pst.setNull(paramCount,Types.CHAR);
						}else{
							pst.setString(paramCount,All_values);
						}
					}else{
						if (All_values.equals("")){
							pst.setNull(paramCount,Types.NULL);
						}else{
							pst.setString(paramCount,All_values);
						}
					}
			    	All_values ="";
			    	paramCount++;
				}
			}
			pst.setString(paramCount,keyVal);
			pst.executeUpdate();
			if (autoCommit)
				conn.commit();
		}catch (Exception e){
			logErrorMsg = "query origin is from class=>"+myClassBean+",sql=>"+getExecutedSQL()+
					",Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			logErrorMsg = "";
			e.printStackTrace();
			System.out.println("error at updating");
			Msg = "ERROR At Updaing "+e.getMessage();
			try{conn.rollback();}catch(Exception eRoll) {}
			throw e;
		}finally{
			 try{pst.close();}catch(SQLException e){}
			 
		 }
		return Msg;
	}
	/*
	 * Generate the update statement.
	 */
	public String genUpdateStatment(ArrayList<String> userDefinedEditCols,
									LinkedHashMap<String , String[]>input,
									LinkedHashMap<String,FileItem>inputFielsMap, 
									HashMap<String,String>userDefinedStoreFileNameColumns,
									String mainTable, 
									String keyCol){
		input.remove("myClassBean");
		String sqlUpdate = "update";
		sqlUpdate= sqlUpdate+" "+mainTable+" set ";// work around becaue dailyrazor having issue with adding the dbname
		/*if (mainTable.toUpperCase().contains(SCHEMANAME+"."))
			sqlUpdate= sqlUpdate+" "+mainTable+" set ";
		else
			sqlUpdate= sqlUpdate+" "+SCHEMANAME+"."+mainTable+" set ";
		*/ 
		for(String cols : userDefinedEditCols) {
			if (inputFielsMap.containsKey(cols)) {
				sqlUpdate += cols+"=? , ";
				sqlUpdate += userDefinedStoreFileNameColumns.get(cols)+"=? , ";
			}else {
				sqlUpdate += cols+"=? , ";
			}
		}
		sqlUpdate = sqlUpdate.substring(0, sqlUpdate.length()-2);
		sqlUpdate = sqlUpdate+" where "+keyCol+"=? ";
		return sqlUpdate;
	}
	/*
	 * Do insert
	 */
	public String doInsert(Connection conn,
						   HttpServletRequest httpSRequest , 
						   LinkedHashMap<String , String[]> inputMap,
						   LinkedHashMap<String, FileItem> inputFilesMap,
						   HashMap<String,String>userDefinedStoreFileNameColumns,
						   Map<String,String> coltypesDB, 
						   String mainTable,
						   HashMap <String,String> userDefinedNewHTMLType,
						   boolean autoCommit) throws Exception{
		
		String sqlInsert = genInsertStatment(inputMap,inputFilesMap,userDefinedStoreFileNameColumns , mainTable);
		setExecutedSQL(sqlInsert);//need to log the parameters also.
		String All_values = "";
		PreparedStatement pst  =null;
		ResultSet rs = null;
		int PrimKey=-1;
		boolean first = true;
		boolean moreThanOneRow = false;
		try{
			int paramCount =1;//the number of paramters.
			pst = conn.prepareStatement(sqlInsert , Statement.RETURN_GENERATED_KEYS);
			for(String field : inputMap.keySet()) {
				first = true;
				moreThanOneRow = false;
				
				for (String value : inputMap.get(field)){
			    	if (!first){
			    		All_values +=colCollectionDelimmiter;
			    		moreThanOneRow = true;
			    	}
			    	All_values = All_values+value;
			    	first = false;
			    	//System.out.println(field+"====="+value);
			    }
				if (moreThanOneRow || 
						(userDefinedNewHTMLType!=null && 
						 userDefinedNewHTMLType.get(field) !=null &&
						 userDefinedNewHTMLType.get(field).equalsIgnoreCase("CHECKBOX")))
					All_values +=colCollectionDelimmiter;
				//My sql Col Formates{BIGINT,DATETIMEDATE,BIGINT,DOUBLE,VARCHAR}
				if (coltypesDB.get(field).equals("DATE")){
					if (All_values.equals("")){
						pst.setNull(paramCount,Types.DATE);
					}else{
						java.util.Date javaDate= new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(All_values);
						Date date = new Date (javaDate.getTime());
						pst.setDate(paramCount,date);
					}	
				}else if (coltypesDB.get(field).equals("VARCHAR")){
					if (All_values.equals("")){
						pst.setNull(paramCount,Types.CHAR);
					}else{
						pst.setString(paramCount,All_values);
					}
				}else{
					if (All_values.equals("")){
						pst.setNull(paramCount,Types.NULL);
					}else{
						pst.setString(paramCount,All_values);
					}
					
				} 
				All_values ="";
				paramCount++;
			}
			for (String file : inputFilesMap.keySet()){
				pst.setBinaryStream(paramCount,
						((FileItem)inputFilesMap.get(file)).getInputStream() ,
						(int)((FileItem)inputFilesMap.get(file)).getSize());					
				//pst.setBinaryStream(paramCount, is);
				paramCount++;
				pst.setString(paramCount , ((FileItem)inputFilesMap.get(file)).getName());	
				paramCount++;
			}
			pst.executeUpdate();
			if (autoCommit)
				conn.commit();
			rs = null;
			rs=pst.getGeneratedKeys();
			if (rs.next())
				PrimKey = rs.getInt(1);
		}catch (Exception e){
			try{conn.rollback();}catch(Exception eRoll){/**/}
			logErrorMsg = "query origin is from class=>"+myClassBean+",sql=>"+getExecutedSQL()+
					",Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			logErrorMsg = "";
			e.printStackTrace();
			throw e;
		}finally{
			try{rs.close();}catch (SQLException e) {/*ignore*/}
			try{pst.close();}catch (SQLException e) {/*ignore*/}
		}
		return Integer.toString(PrimKey);
	}
	
	/*
	 * Build the insert statement.
	 */
	public String genInsertStatment(LinkedHashMap<String , String[]> input,
									LinkedHashMap<String,FileItem>inputFielsMap,
									HashMap<String,String>userDefinedStoreFileNameColumns,
									String mainTable){
		String sqlInsert;
		input.remove("myClassBean");
		sqlInsert= "insert into "+mainTable+" (";
		/*
		if (mainTable.toUpperCase().contains(SCHEMANAME+"."))
			sqlInsert= "insert into "+mainTable+" (";
		else
			sqlInsert= "insert into "+SCHEMANAME+"."+mainTable+" (";
		*/
		boolean first = true;
		for(String parameter : input.keySet()) {
			if (!first)
				sqlInsert = sqlInsert+" , ";
			sqlInsert =sqlInsert +parameter;
			first = false;
		}
		for (String fileParam:inputFielsMap.keySet()){
			if (!first)
				sqlInsert = sqlInsert+" , ";
			sqlInsert =sqlInsert +fileParam;
			sqlInsert =sqlInsert +" , "+userDefinedStoreFileNameColumns.get(fileParam);
			first = false;
		}
		sqlInsert = sqlInsert +") values (";
		first = true;
		for(String parameter : input.keySet()) {
		    //for (String value : input.get(parameter)){
		    	if (!first)
					sqlInsert = sqlInsert+" , ";
				sqlInsert =sqlInsert +"?";
				first = false;
		    //}
		}
		for (String fileParam:inputFielsMap.keySet()){
			if (!first)
				sqlInsert = sqlInsert+" , ";
			sqlInsert =sqlInsert +"? , ? ";
			first = false;
		}
		sqlInsert = sqlInsert+")";
		return sqlInsert;
	}
	/*
	 * Get the first cell from sql as string
	 */
	public String getValuefromSql (Connection conn,String sql){
		String val ="";
		ResultSet rs = null;
		PreparedStatement pst  = null;
		//Prepar.out.println(sql);
		try{
			pst  = conn.prepareStatement(sql);
			setExecutedSQL(sql);
			 rs = pst.executeQuery();
			 if (rs.next()){
				 val= rs.getString(1);
			 }
		 }catch(Exception e){
				logErrorMsg = "query origin is from class=>"+myClassBean+",sql=>"+getExecutedSQL()+
						",Exception Msg=>"+e.getMessage(); 
				smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
						Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
				logErrorMsg = "";
			 e.printStackTrace();
			 System.out.println("error getValuefromSql , the sql=>"+sql);
		 }finally{
			 try{rs.close();}catch (SQLException e){/*ignore*/}
			 try{pst.close();}catch (SQLException e){/*ignore*/}
		 }
		return val;
	}
	/*
	 * Get frist row only.
	 */
	public ResultSet getRow (Connection conn, String sql){
		PreparedStatement pst = null;
		ResultSet rRow = null;
		try{
			 pst = conn.prepareStatement(sql);
			 rRow = null;
			 setExecutedSQL(sql);
			 rRow = pst.executeQuery();
			 if (!rRow.next())
				  rRow = null;
			 
		 }catch(Exception e){
				logErrorMsg = "query origin is from class=>"+myClassBean+",sql=>"+getExecutedSQL()+
						",Exception Msg=>"+e.getMessage(); 
				smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName()+",myClassBean=>"+myClassBean, 
						Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
				logErrorMsg = "";
			 System.out.println("error getValuefromSql , the sql=>"+sql);
			 e.printStackTrace();
		 }
		return rRow;
	}
	
	
	public boolean isFile(String colName){
		boolean file = false;
		if (blobList.contains(sqlColsTypes.get(colName)))
			file = true;
		return file;
	}
	/*
	 * Getters and Setters
	 */
	public HashMap<String ,  String> getsqlColsTypes(){
		return sqlColsTypes;
	}
		
	public HashMap<String ,  Integer> getsqlColsSizes(){
		return this.sqlColsSizes;
	}
	
	public <T> T nvl(T arg0, T arg1) {
		return (arg0 == null)?arg1:arg0;
	}

	public String getExecutedSQL() {
		return executedSQL;
	}

	public void setExecutedSQL(String executedSQL) {
		this.executedSQL = executedSQL;
	}
	
	
	public String getDBMSType() {
		return DBMSType;
	}

	public void setDBMSType(String dBMSType) {
		DBMSType = dBMSType;
	}
	public HashMap<String,String> getArabicToEnglishNumbersMap (){
		 HashMap<String,String> hash = new  HashMap<String,String>();
		 hash.put("٠", "0");
		 hash.put("١", "1");
		 hash.put("٢", "2");
		 hash.put("٣", "3");
		 hash.put("٤", "4");
		 hash.put("٥", "5"); 
		 hash.put("٦", "6");
		 hash.put("٧", "7");
		 hash.put("٨", "8");
		 hash.put("٩", "9");
		 return hash;
	}
	
	public HashMap<String,String> getEnglishToArabicNumbersMap (){
		 HashMap<String,String> hash = new  HashMap<String,String>();
		 hash.put("0", "٠");
		 hash.put("1", "١");
		 hash.put("2", "٢");
		 hash.put("3", "٣");
		 hash.put("4", "٤");
		 hash.put("5", "٥"); 
		 hash.put("6", "٦");
		 hash.put("7", "٧");
		 hash.put("8", "٨");
		 hash.put("9", "٩");
		 return hash;
	}
}
