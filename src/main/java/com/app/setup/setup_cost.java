/* class description: used to setup customers informations,
 * created by: lina - SMARTYJ FrameWork team member,
 * created date: 27/4/2018 2:49 PM.
 */
package com.app.setup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;

import smarty.core.CoreMgr;

public class setup_cost extends CoreMgr{
	
	
	private LinkedHashMap<String, String> state = null;
	
	public setup_cost(){
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */
		MainSql = "select distinct(de_to) from kbdestination order by de_id";					
		
		userDefinedGridCols.add("de_from");
		userDefinedColLabel.put("de_from", "من");
		

	}//end of constructor setup_cost

	@Override
	public void initialize(HashMap smartyStateMap){
		PreparedStatement pst = null;
		ResultSet rs = null;
		String stateSql ="" , stateCols = "";
		state = new LinkedHashMap<String,String>();
		boolean first = true;
		try{
			stateSql = "select distinct(de_from)as state from kbdestination order by state";					
			pst = conn.prepareStatement(stateSql);
			rs = pst.executeQuery();
			
			while(rs.next()){
				userDefinedGridCols.add(rs.getString("state"));
				//userDefinedColLabel.put(rs.getString("state") ,rs.getString("state"));
				if (!first)
					stateCols += ",";
				
				stateCols += " '"+ rs.getString("state")+"' as "+ rs.getString("state")+" ";
				first = false;
			}//end of loop while
		}catch(Exception e){
				
			e.printStackTrace();
		}finally{
			try{rs.close();}catch(Exception e){}
			try{pst.close();}catch(Exception e){}
		}
		
		MainSql = "select "+stateCols+" ,de_from from kbdestination group by de_from order by de_id";					

		super.initialize(smartyStateMap);
	}//end of method initialize
		
}//end of class setup_cost

