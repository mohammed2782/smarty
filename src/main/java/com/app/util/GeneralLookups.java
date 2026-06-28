package com.app.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GeneralLookups {
	private static final String followUpSql = "select kbcode, kbdesc from kbgeneral where kbcat1 = 'FOLLOWUP' and kbcat2 = 'ACTIONS' order by kbcat_seq";
	
	public LinkedHashMap<String,String> getFollowUpActions(Connection aConn)throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String,String> followUpActions = new LinkedHashMap<String,String>();
		try {
			pst = aConn.prepareStatement(followUpSql);
			rs  = pst.executeQuery();
			while (rs.next()) {
				followUpActions.put(rs.getString("kbcode"), rs.getString("kbdesc"));
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {}
		}
		return followUpActions;
	}
}
