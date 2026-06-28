package com.app.bussframework;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.LinkedList;

import com.app.util.IntegrationUtil;

import smarty.db.mysql;

import java.sql.PreparedStatement;

public class FixData {
	public void buildParentRelationInCain(int startCaseId, int endCaseId){
		Connection conn = null;
		PreparedStatement pst = null, pst2 = null, pstUpdate = null;
		ResultSet rs = null, rs2 = null;
		try {
			int caseId = 0;
			conn = mysql.getConn();
			
			pst = conn.prepareStatement("select cc_caseid from p_caseschain where cc_caseid >=? and cc_caseid <=? and cc_parentchainid = 0 group by cc_caseid");
			pst2 = conn.prepareStatement("select cc_id from p_caseschain where cc_caseid= ? order by cc_id asc");
			pstUpdate = conn.prepareStatement("update p_caseschain set cc_parentchainid =? where cc_id = ? ");
			
			pst.setInt(1, startCaseId);
			pst.setInt(2, endCaseId);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				caseId = rs.getInt("cc_caseid");
				
				pst2.setInt(1, caseId);
				rs2 = pst2.executeQuery();
				int i=1, parentCaseId= 0;
				while (rs2.next()) {
					if (i>1) {
						pstUpdate.setInt(1, parentCaseId);
						pstUpdate.setInt(2, rs2.getInt("cc_id"));
						pstUpdate.executeUpdate();
						try {pstUpdate.clearParameters();}catch(Exception e) {}
					}
					
					parentCaseId = rs2.getInt("cc_id");
					i++;
				}
				try {rs2.close();}catch(Exception e) {}
			}
			System.out.println("end--------------------------------------------------");
			conn.commit();
		}catch(Exception e) {
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll) {}
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {rs2.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {pst2.close();}catch(Exception e) {}
			try {pstUpdate.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
	}
}
