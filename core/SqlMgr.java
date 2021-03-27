package com.app.core;

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

import com.app.db.mysql;

public class SqlMgr {
	private LinkedHashMap<String , LinkedHashMap<String , String>> colsLookupValues;
	private HashMap<String , String> sqlColsTypes;
	private HashMap<String , Integer> sqlColsSizes;
	private String logErrorMsg;
	private String myClassBean;
	private String executedSQL; // the currently Executed Sql  in LoadData function
	private String DBMSType;
	private String colCollectionDelimmiter =":";
	public static final List<String> blobList = Arrays.asList("TINYBLOB" , "BLOB" , "MEDIUMBOLB" , "LONGBLOB");
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
			 try{rs.close();}catch(SQLException e){}
			 try{pst.close();}catch (SQLException e){}	 
		}
		return sqlCols;
	}
	/*
	 * Will get the mapping for each column using the sql set in the java.
	 */
	public  LinkedHashMap<String , LinkedHashMap<String , String>> loadAllLookups(Connection conn,HashMap <String , String > ColsLookup){
		PreparedStatement pstLookup = null;
		ResultSet rs_lookup  = null;
		String sqlLookup="";
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
			 try{rs_lookup.close();}catch(SQLException e){}
			 try{pstLookup.close();}catch (SQLException e){}
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
							  ArrayList<String> userDefinedFilterColsUsingLike
							  ){
		int startwith = PageRows*(currentpage-1);
		int endwith = PageRows*(currentpage);

		if (userDefinedWhere== null){
			userDefinedWhere = "";
		}
		
		String WhereClause ="";
		try{
			// Enahancement to be done, 
			// 1- to include having.
			// 2- inner where clause treatement
			// 3- i need to give the power to the user to be able to manipulate the where clause
			// for implement DateRange
			
			if (userDefinedWhere == ""){
					if (Search_Param!=null){
						WhereClause = BuildWhereCluase(Search_Param , userDefinedHTMLType, userDefinedFilterColsUsingLike);
						// we need to define the location of the where clause
						if (WhereClause!=null && !WhereClause.trim().equalsIgnoreCase("")){
							if (MainSql.contains("where")){
								MainSql = MainSql.replace("where", " where "+WhereClause+" and ");
							}else if((MainSql.contains("group by"))){
								
								MainSql = MainSql.replace("group by", " where "+WhereClause+" group by ");
							}else{
								MainSql = MainSql+ " where "+WhereClause;
							}
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
				setExecutedSQL(MainSql);
				// Get Connection and Statement 
				 pst = conn.prepareStatement(MainSql);
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
	/*
	 * Build the Where Clause. 
	 */
	public  String BuildWhereCluase(HashMap <String,String[]> SearchFieldsVals , HashMap <String,String> userDefinedHTMLType , ArrayList<String> userDefinedFilterColsUsingLike ){
		String WhereClause = "";
		String innerCol ="";
		boolean found_param =false;
		boolean foundVal  = false;
		// normally the syntax is (col1='') and (col2='')
		// BUT if you have more than one value for the same col so the syntax should be
		// (col1='' or col1='') and (col2='') , so you don't colse the paranthesis unless the col is changed
		String prevCol="";
		for(String parameter : SearchFieldsVals.keySet()) { 
			innerCol = "";
			if (!parameter.equals("filter") && SearchFieldsVals.get(parameter)!=null){
				found_param = true;
				innerCol +="(";
				for (String value : SearchFieldsVals.get(parameter))
			    {	
					foundVal = false;
			    	if ((value !=null) && (!value.equals("")))
					{		    
			    		//System.out.println("userDefinedHTMLType.get(parameter)===>"+userDefinedHTMLType.get(parameter));
						foundVal = true;
						if (userDefinedHTMLType!=null && userDefinedHTMLType.containsKey(parameter)
								&& (userDefinedHTMLType.get(parameter).equalsIgnoreCase("CHECKBOX") || 
										userDefinedHTMLType.get(parameter).equalsIgnoreCase("MULTILIST"))){
							// if collection then we need to use like
							innerCol +=parameter+" like '%"+value+colCollectionDelimmiter+"%' or ";
						}else if (userDefinedHTMLType!=null && userDefinedHTMLType.containsKey(parameter)
								&& userDefinedHTMLType.get(parameter).equalsIgnoreCase("TIMESTAMP")) {
							innerCol +="date("+parameter+")"+"='"+value+"' or ";
						}else{
							if (userDefinedFilterColsUsingLike!=null && userDefinedFilterColsUsingLike.size()>0 && userDefinedFilterColsUsingLike.contains(parameter)) {// here we search for the exact 
								innerCol +=parameter+" like '%"+value+"%' or ";
								//System.out.println("this is --->"+parameter);
							}else
								innerCol +=parameter+"='"+value+"' or ";
							// and we should search for the like
						}
					} 
			    }
				if(foundVal){
					innerCol = innerCol.substring(0,innerCol.length()-3);
					innerCol +=") and ";
				}else{
					innerCol = "";
				}
			}
			if (found_param && foundVal)
				WhereClause+=innerCol;
		}
		if (!found_param){
			return null;
		}
		if (WhereClause.length()>=5)
			WhereClause = WhereClause.substring(0,WhereClause.length()-5);
		return WhereClause;
	}
	
	/*
	 * Do automatic update
	 */
	public String doUpdate(Connection conn,
						   LinkedHashMap<String , String[]> inputMap,
						   LinkedHashMap<String, FileItem> inputFilesMap,
						   HashMap<String,String>userDefinedStoreFileNameColumns,
						   Map<String,String> coltypesDB, 
						   String keyVal, 
						   String mainTable, 
						   String keyCol,
						   HashMap<String,String>userDefinedNewHTMLType,
						   boolean autoCommit)throws Exception{
		String upd_sql = genUpdateStatment(inputMap ,inputFilesMap,userDefinedStoreFileNameColumns,  mainTable , keyCol);
		//System.out.println("upd_sql=>"+upd_sql);
		String All_values = "" , Msg="Record Updated";
		PreparedStatement pst  = null;
		try{
			pst = conn.prepareStatement(upd_sql);
			setExecutedSQL(upd_sql); // we need to log the paramter values also
			int paramCount =1;//the number of paramters.
			boolean first = true;
			boolean moreThanOneRow = false;
			
			for(String field : inputMap.keySet()) {
				first = true;
				moreThanOneRow = false;
				if (inputMap.get(field)!=null){
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
							userDefinedNewHTMLType.get(field).equalsIgnoreCase("CHECKBOX")))
						All_values +=colCollectionDelimmiter;
				}
				//System.out.println("field==>"+field+", all_values--->"+All_values);
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
				/*}else if (coltypesDB.get(field).equalsIgnoreCase("BLOB")){
					InputStream is = new ByteArrayInputStream(All_values.getBytes());					
					pst.setBinaryStream(paramCount, is);
					
				*/
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
			for (String field : inputFilesMap.keySet()){
				pst.setBinaryStream(paramCount,
						((FileItem)inputFilesMap.get(field)).getInputStream() ,
						(int)((FileItem)inputFilesMap.get(field)).getSize());					
				//pst.setBinaryStream(paramCount, is);
				paramCount++;
				pst.setString(paramCount , ((FileItem)inputFilesMap.get(field)).getName());
				paramCount++;
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
	public String genUpdateStatment(LinkedHashMap<String , String[]>input,
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
		for(String cols : input.keySet()) {
			sqlUpdate += cols+"=? , ";
		}
		for(String cols : inputFielsMap.keySet()) {
			sqlUpdate += cols+"=? , ";
			sqlUpdate += userDefinedStoreFileNameColumns.get(cols)+"=? , ";
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
			 if (rs.first()){
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
			 if (!rRow.first())
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
}
