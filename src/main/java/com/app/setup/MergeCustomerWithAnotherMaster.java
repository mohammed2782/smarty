package com.app.setup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;

import com.app.util.Utilities;

public class MergeCustomerWithAnotherMaster extends CoreMgr {
	public MergeCustomerWithAnotherMaster() {
		MainSql = "select '' as pullfrommastercust , '' as pushtomastercust from dual";
		
		canNew = true;
		mainTable = "kbcustomers";
		
		
		userDefinedNewCols.add("pullfrommastercust");
		userDefinedNewCols.add("pushtomastercust");
		userDefinedColLabel.put("pullfrommastercust", "نقل المتجر من هذا العميل ");
		userDefinedColLabel.put("pushtomastercust", "إلى هذا العميل");
		
		userDefinedLookups.put("pullfrommastercust", "select mcust_id , mcust_name from kb_mastercustomer where mcust_id={masterCustOutOff} ");
		userDefinedNewLookups.put("pushtomastercust", "select mcust_id , mcust_name from kb_mastercustomer where mcust_branchcode = {userstorecode}");
		
		userDefinedReadOnlyNewCols.add("pullfrommastercust");
		userDefinedNewColsDefualtValues.put("pullfrommastercust", new String [] {"{masterCustOutOff}"});
		//userDefinedNewColsHtmlType.put("pullfrommastercust", "DROPLIST");
		
		userDefinedColsMustFill.add("pullfrommastercust");
		userDefinedColsMustFill.add("pushtomastercust");
		
		displayMode = "NEWSINGLE";
		
		userDefinedNewCaption = "دمج متجر مع عميل اخر";
	}
	
	@Override
	public String doInsert(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg = "تم نقل المتجر";
		PreparedStatement pst = null;
		String fromMasterCust= rqs.getParameter("pullfrommastercust");
		String toMasterCust= rqs.getParameter("pushtomastercust");
		int userId = Integer.parseInt(replaceVarsinString( "{userid}" , arrayGlobals));
		int custId = Integer.parseInt(replaceVarsinString( "{mergCustIdWithAnotherMasterId}" , arrayGlobals));
		//System.out.println("custId = "+custId);
		Utilities ut = new Utilities();
		try{
			
			pst = conn.prepareStatement("update p_cases set c_mastercustid=?  where c_custid = ? and c_pmtid = 0 and c_settled != 'FULL'");
			pst.setString(1, toMasterCust);
			pst.setInt(2, custId);
			pst.executeUpdate();
			try{pst.close();}catch(Exception e){/* ignore*/}

			
			pst = conn.prepareStatement("update kbcustomers set cust_mastercustid=? where cust_id = ? ");
			pst.setString(1, toMasterCust);
			pst.setInt(2, custId);
			pst.executeUpdate();
			try{pst.close();}catch(Exception e){/* ignore*/}
			
			CoreUtilities.logChanges(conn, 
					"kbcustomers".toUpperCase(), 
					"cust_id", 
					custId, 
					"cust_mastercustid", 
					fromMasterCust, 
					toMasterCust, 
					"update", 
					"دمج متجر مع عميل اخر", 
					userId);
			conn.commit();
		}catch(Exception e){
			/*Log Error*/
			statusMsg = "Error at moving cases, "+e.getMessage();
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){/*ignore*/}
		}finally{
			try{pst.close();}catch(Exception e){/* ignore*/}
		}
		return statusMsg;
		
	}

}
