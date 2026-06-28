package com.app.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import com.app.financials.StandardFinCurrency;
import com.app.tablesbeans.CasesBean;

import smarty.core.CoreUtilities;

public class UtilitiesAyatLina extends Utilities{
		
	public void changeShipmentCostByCaseId(Connection conn, int a_caseId, String a_whichColumn, String a_newValue, String a_fromWhere, int a_userId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String oldValue="";
		try {
			CasesBean.getInstance();
			String dbColumnName = CasesBean.columnsCodeNameToDbNameMap.get(a_whichColumn);
			pst = conn.prepareStatement("select "+dbColumnName+" from p_cases where c_id = ?");
			pst.setInt(1, a_caseId);
			rs = pst.executeQuery();
			if(rs.next()) {
				oldValue = rs.getString(dbColumnName);				
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			

			pst = conn.prepareStatement("update p_cases set "+dbColumnName+" = ?  where c_id = ? ");
			pst.setString(1, a_newValue);
			pst.setInt(2, a_caseId);
			pst.executeUpdate();
			String another = CasesBean.columnsCodeNameToDbNameMap.get("case_id");
			// to record the update operation on log table
			CoreUtilities.logChanges(conn, "P_CASES", "c_id", a_caseId, dbColumnName, oldValue, a_newValue, "update", a_fromWhere, a_userId);
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}	
	
	}// end of method changeShipmentCostByCaseId
	
	
		public HashMap<StandardFinCurrency, Long> calcMasterCustomerShipmentsAmt(Connection conn, String masterCustId) throws Exception{
			// to get the total amount of underprocess cases
			PreparedStatement pst = null;
			ResultSet rs = null;
			double underProcessAmt = 0;
			HashMap<StandardFinCurrency, Long> entityBalance = new HashMap<StandardFinCurrency, Long>();
			try {
				String sql = "select (sum(c_receiptamt) - sum(c_shipment_cost)) as underprocessAmtIqd, "
						+ "sum(c_receiptamt_usd) as underprocessAmtUsd "
						   + " from p_cases where c_mastercustid = ? "
						   + " and  q_stage not in ('CNCL', 'DLV') ";
				pst = conn.prepareStatement(sql);
				pst.setString(1, masterCustId);
				rs = pst.executeQuery();
				if(rs.next()) {
					entityBalance.put(StandardFinCurrency.IQD, rs.getLong("underprocessAmtIqd"));
					entityBalance.put(StandardFinCurrency.USD, rs.getLong("underprocessAmtUsd"));
				}
			}catch (Exception e) {
				throw e;
			}finally {
				try {rs.close();}catch(Exception e) {/*ignore*/}
				try {pst.close();}catch(Exception e) {/*ignore*/}
			}
			return entityBalance;
		}
			
		
}// end of class UtilitiesAyatLina
