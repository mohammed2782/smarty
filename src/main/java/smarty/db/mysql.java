package smarty.db;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public  class mysql {
	//public static java.sql.Connection conn;
	
	public static boolean error = false;
	public static String  errorMsg="";
	private static DataSource ds;
	
	static{
		error = false;
		try {
			Context ctx = new InitialContext();
			 ds = (DataSource)ctx.lookup("java:comp/env/jdbc/MDSCORE");
			// Get Connection and Statement		
		}catch (Exception e){
			error = true;
			e.printStackTrace();
			errorMsg = e.getMessage(); 
			//smartyLogAndErrorHandling.logError("mysql" , Level.SEVERE, errorMsg , e);
		}
	}
	
	public static Connection getConn() throws SQLException {
		Connection conn = null;
		try{
		   conn = ds.getConnection();
		   Statement st = conn.createStatement();
			st.execute("SET SESSION time_zone='+3:00'");
			st.close();
		   conn.setAutoCommit(false);
		   
		}catch(Exception e){
			error = true;
			e.printStackTrace();
			errorMsg = e.getMessage(); 
		}
		
		return conn;
	}
	
	
	
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	
	
}
