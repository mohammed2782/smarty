package com.app.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.app.cases.CaseInformation;

public class BranchUtilities {
	public static boolean mustGiveToAgentOnlyWithCheckedBracode(Connection a_conn, int a_branchCode, ManifestsTypes a_manifestType)throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String sql = "select kbdesc from kbgeneral where kbcat1='BRANCHSETTINGS' and kbcat2='FLOW' "
					+ " and kbcat3='NO_MOVE_WITHOUT_CHECK' and kbcat4 =? and kbcode=?";
			pst = a_conn.prepareStatement(sql);
			pst.setInt(1, a_branchCode);
			pst.setString(2, a_manifestType.toString());
			rs = pst.executeQuery();
			if (rs.next()) {
				if (rs.getString("kbdesc").equalsIgnoreCase("Y")) {
					return true;
				}
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return false;
	}
}
