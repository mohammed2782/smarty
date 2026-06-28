package com.app.setup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import com.app.util.Utilities;

public class MergeCustomerPopUp extends CoreMgr {
	public MergeCustomerPopUp () {
		MainSql = "select '' as pullfromcust , '' as pushtocust from dual";
		
		canNew = true;
		mainTable = "kbcustomers";
		
		
		userDefinedNewCols.add("pullfromcust");
		userDefinedNewCols.add("pushtocust");
		userDefinedColLabel.put("pullfromcust", "نقل الشحنات من هذا الزبون");
		userDefinedColLabel.put("pushtocust", "إلى هذا الزبون");
		
		userDefinedLookups.put("pullfromcust", "select cust_id , cust_name from kbcustomers where cust_id !={custidreassign} and cust_deleted='N' and cust_branch={userstorecode}");
		userDefinedNewLookups.put("pushtocust", "select cust_id , cust_name from kbcustomers ");
		
		userDefinedReadOnlyNewCols.add("pushtocust");
		userDefinedNewColsDefualtValues.put("pushtocust", new String [] {"{custidreassign}"});
		
		userDefinedColsMustFill.add("pullfromcust");
		userDefinedColsMustFill.add("pushtocust");
		
		displayMode = "NEWSINGLE";
		
		userDefinedNewCaption = "دمج شحنات زبائن";
	}
	
	@Override
	public String doInsert(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg = "Cases moved";
		PreparedStatement pst = null;
		ResultSet rs = null;
		String fromCust= rqs.getParameter("pullfromcust");
		String toCust= rqs.getParameter("pushtocust");
		String newHP1 ="", newHP2 = "";
		int userId = Integer.parseInt(replaceVarsinString( "{userid}" , arrayGlobals));
		try{
			pst = conn.prepareStatement("select cust_phone1 , cust_phone2 from kbcustomers where cust_id=? and cust_deleted='N'");
			pst.setString(1,toCust);
			rs = pst.executeQuery();
			if (rs.next()) {
				newHP1 = rs.getString("cust_phone1");
				newHP2 = rs.getString("cust_phone2");
			}
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
			
			pst = conn.prepareStatement("update p_cases set c_custid=? , c_custhp=? where c_custid = ? ");
			pst.setString(1, toCust);
			pst.setString(2, newHP1);
			pst.setString(3, fromCust);
			pst.executeUpdate();
			try{pst.close();}catch(Exception e){/* ignore*/}

			// TODO merge the receipts in p_receipts
			int userBranch_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
			pst = conn.prepareStatement("update p_books_rcp set br_custid = ? where br_branchid =? and br_custid =? ");
			pst.setString(1, toCust);
			pst.setInt(2, userBranch_G);
			pst.setString(3, fromCust);
			pst.executeUpdate();
			try{pst.close();}catch(Exception e){/* ignore*/}
			
			// TODO merge the financial transactions
			pst = conn.prepareStatement("update p_fin_transactions set trans_entity_id = ? where trans_operationentity = 'CUSTOMER' and trans_entity_id =? ");
			pst.setString(1, toCust);
			pst.setString(2, fromCust);
			pst.executeUpdate();
			try{pst.close();}catch(Exception e){/* ignore*/}
			
			pst = conn.prepareStatement("update kbcustomers set cust_deletedby=?, cust_deleteddt = now() where cust_id = ? ");
			pst.setInt(1, userId);
			pst.setString(2, fromCust);
			pst.executeUpdate();
			try{pst.close();}catch(Exception e){/* ignore*/}
			
			pst = conn.prepareStatement("insert into deleted_customers select kbcustomers.*  from kbcustomers where cust_id = ? ");
			pst.setString(1, fromCust);
			pst.executeUpdate();
			try{pst.close();}catch(Exception e){/* ignore*/}
			
			pst = conn.prepareStatement("delete from kbcustomers where cust_id=?");
			pst.setString(1, fromCust);
			pst.executeUpdate();
			conn.commit();
		}catch(Exception e){
			/*Log Error*/
			statusMsg = "Error at moving cases, "+e.getMessage();
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){/*ignore*/}
		}finally{
			try{rs.close();}catch(Exception e){/* ignore*/}
			try{pst.close();}catch(Exception e){/* ignore*/}
		}
		return statusMsg;
		
	}
}
