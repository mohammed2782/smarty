package com.app.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class SplitingCasesUtils {

	public static long createNewCaseIdByCopy(Connection a_conn, long a_copyFromCaseId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		long newCopiedCaseId = 0;
		try{
			pst = a_conn.prepareStatement("insert into p_cases ("+StaticUtils.PCASES_COLS_WIHOUT_PK+")"
					+ " select "+StaticUtils.PCASES_COLS_WIHOUT_PK+" from p_cases where c_id = ? ",
					Statement.RETURN_GENERATED_KEYS);
			pst.setLong(1, a_copyFromCaseId);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			if (rs.next()) {
				newCopiedCaseId =  rs.getInt(1);
			}
			long lastetChainIdForCopiedCaseId = createCopyOfCaseChains(a_conn, a_copyFromCaseId, newCopiedCaseId);
			try {pst.close();}catch(Exception e) {}
			// if price is less than or equal to zero then change it when splitted
			pst = a_conn.prepareStatement("update p_cases set c_lastchainid=?,"
					+ "  c_receiptamt = (case when (c_priceb4change - c_receiptamt )>=0 then (c_priceb4change - c_receiptamt) else c_priceb4change end)  where c_id= ?");
			pst.setLong(1, lastetChainIdForCopiedCaseId);
			pst.setLong(2, newCopiedCaseId);
			pst.executeUpdate();
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return newCopiedCaseId;
	}
	private static long createCopyOfCaseChains(Connection a_conn, long a_copyFromCaseId, long a_copyToCaseId) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = a_conn.prepareStatement(
			"insert into p_caseschain ("+StaticUtils.PCASESCHAIN_COLS_WITHOUT_PK+")"
			+ " select "+StaticUtils.PCASESCHAIN_COLS_WITHOUT_PK.replaceFirst("cc_caseid", a_copyToCaseId+"")
			+" from p_caseschain where cc_caseid = ? ");
			pst.setLong(1, a_copyFromCaseId);
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			
			// get the latest chain id
			pst = a_conn.prepareStatement("select max(cc_id) from p_caseschain where cc_caseid=?");
			pst.setLong(1, a_copyToCaseId);
			rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getLong(1);
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return 0;
	}
}
