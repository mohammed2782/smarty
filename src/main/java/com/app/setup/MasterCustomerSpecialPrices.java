package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;

public class MasterCustomerSpecialPrices extends CoreMgr {
	public MasterCustomerSpecialPrices() {
		MainSql = "select  *  from kbmastercustomer_specialprices where mcsp_mastercustid={mastercustspecialprice}";
		canNew = true;
		canDelete = true;
		
		mainTable = "kbmastercustomer_specialprices";
		keyCol = "mcsp_id";
		
		//userDefinedNewCols.add("sp_custid");
		userDefinedNewCols.add("mcsp_statecode");
		userDefinedNewCols.add("mcsp_price");
		userDefinedNewCols.add("mcsp_rural_price");
		
		userDefinedGridCols.add("mcsp_statecode");
		userDefinedGridCols.add("mcsp_price");
		userDefinedGridCols.add("mcsp_rural_price");
		
		
		userDefinedColLabel.put("mcsp_statecode","المحافظة");
		userDefinedColLabel.put("mcsp_price","مبلغ الشحن");
		userDefinedColLabel.put("mcsp_rural_price","مبلغ الشحن للأقضيه");
		
		userDefinedColsMustFill.add("mcsp_statecode");
		userDefinedColsMustFill.add("mcsp_price");
		userDefinedColsMustFill.add("mcsp_rural_price");
		userDefinedNewColsDefualtValues.put("mcsp_mastercustid", new String[] {"{mastercustspecialprice}"});
		myhtmlmgr.refreshPageOnDelete = true;
		
		userDefinedNewColsHtmlType.put("mcsp_statecode", "CHECKBOX");
	}
	
	@Override
	public void initialize(HashMap smartyStateMap){
		int masterCustId = Integer.parseInt(replaceVarsinString(" {mastercustspecialprice} ", arrayGlobals).trim());
		userDefinedNewLookups.put("mcsp_statecode", "select st_code, st_name_ar from kbstate where st_code not in "
				+ " (select mcsp_statecode from kbmastercustomer_specialprices where mcsp_mastercustid="+masterCustId+")");
		userDefinedLookups.put("mcsp_statecode", "select st_code, st_name_ar from kbstate  ");
		super.initialize(smartyStateMap);
		
	}
	
	 @Override 
	 public String doInsert (HttpServletRequest rqs , boolean commit) {
		 Connection conn = null;
		 PreparedStatement pst = null;
		 int masterCustId = Integer.parseInt(replaceVarsinString(" {mastercustspecialprice} ", arrayGlobals).trim());
		 int userId = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		 try {
			 conn = mysql.getConn();
			 inputMap_ori = filterRequest(rqs);
			 pst = conn.prepareStatement("insert into kbmastercustomer_specialprices "
			 			+ "    (mcsp_mastercustid , mcsp_statecode, mcsp_price, mcsp_rural_price, mcsp_createdby) "
				 	+ " values (? 				  , ? 			  , ? 		  , ?				, ? )");
			 for (String state : inputMap_ori.get("mcsp_statecode")){
				 pst.setInt(1, masterCustId);
				 pst.setString(2, state);
				 pst.setString(3, rqs.getParameter("mcsp_price"));
				 pst.setString(4, rqs.getParameter("mcsp_rural_price"));
				 pst.setInt(5, userId);
				 pst.executeUpdate();
			 }
			 conn.commit();
			 
		 }catch(Exception e) {
			 try {conn.rollback();}catch(Exception eRoll) {/**/}
			 e.printStackTrace();
		 }finally {
			 try {pst.close();}catch(Exception e) {/**/}
			 try {conn.close();}catch(Exception e) {/**/}
		 }
		 return "";
	 }
	 
	 @Override 
	 public String doDelete (HttpServletRequest rqs) {
		 String keyVal = rqs.getParameter(keyCol);
		 PreparedStatement pst = null;
		 try {
			pst = conn.prepareStatement("delete from kbmastercustomer_specialprices where mcsp_id=?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			conn.commit();
		 }catch (Exception e) {
			try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
			logErrorMsg = "";
			e.printStackTrace();
		}finally {
			try {pst.close();} catch (Exception e) {}
		}
		 return ""; 
	}
}
