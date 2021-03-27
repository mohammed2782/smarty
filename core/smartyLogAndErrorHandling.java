package com.app.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.app.db.mysql;


public  class smartyLogAndErrorHandling {
	private static final Logger logger =Logger.getLogger( smartyLogAndErrorHandling.class.getName());
	public static String logMethodInvoker;
	public static String logErrLineNumber;
	
	public static void logError(String className,Level lvl , String errorMsg , Exception exception){
		try{
			logMethodInvoker=	exception.toString();
			String pathName = System.getProperty("catalina.base");
			//System.out.println("catlina home=>"+pathName);
			FileHandler fh = new FileHandler(pathName+"/logs/smartyLog.log");
			
			
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			Logger.getLogger("").addHandler(fh);
			//logger.log(lvl, "class=>"+className +"-"+"Method=>"+logMethodInvoker+",errorMsg=>"+errorMsg);
			logger.logp(lvl, className, "", "class=>"+className +"-"+"Method=>"+logMethodInvoker+",errorMsg=>"+errorMsg, exception);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
	}
	public static void logErrorInDB(Connection conn , String logCat , String className,String URLPath,HashMap<String,String> Parameters,Exception exception){
		
		PreparedStatement pst =null;
		try{
			//conn = mysql.getConn();
			pst = conn.prepareStatement("insert into m_syslog (log_cat , log_msg) values(?,?)");
			pst.setString(1, logCat);
			pst.setString(2, "className=>"+className+", URLPath=>"+URLPath+", Parameters=>"+Parameters+",exception=>"+exception.getMessage());
			pst.executeUpdate();
			conn.commit();
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			try{
				pst.close();
			}catch(Exception eConn){
				/*ignore*/
			}
		}
		
		 
	}
}
