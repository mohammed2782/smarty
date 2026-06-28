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

public class CustomersSpecialPrices extends CoreMgr {
	public CustomersSpecialPrices() {
		MainSql = "select  *  from kbcustomer_specialprices where sp_custid='{custidspecialprice}'";
		canNew = true;
		canDelete = true;
		
		mainTable = "kbcustomer_specialprices";
		keyCol = "sp_id";
		
		//userDefinedNewCols.add("sp_custid");
		userDefinedNewCols.add("sp_statecode");
		userDefinedNewCols.add("sp_price");
		userDefinedNewCols.add("sp_rural_price");
		
		userDefinedGridCols.add("sp_statecode");
		userDefinedGridCols.add("sp_price");
		userDefinedGridCols.add("sp_rural_price");
		
		
		userDefinedColLabel.put("sp_statecode","المحافظة");
		userDefinedColLabel.put("sp_price","مبلغ الشحن");
		userDefinedColLabel.put("sp_rural_price","مبلغ الشحن للأقضيه");
		
		userDefinedColsMustFill.add("sp_statecode");
		userDefinedColsMustFill.add("sp_price");
		userDefinedColsMustFill.add("sp_rural_price");
		userDefinedNewColsDefualtValues.put("sp_custid", new String[] {"{custidspecialprice}"});
		myhtmlmgr.refreshPageOnDelete = true;
		
		userDefinedNewColsHtmlType.put("sp_statecode", "CHECKBOX");
	}
	
	@Override
	public void initialize(HashMap smartyStateMap){
		String custId = replaceVarsinString(" {custidspecialprice} ", arrayGlobals).trim();
		userDefinedNewLookups.put("sp_statecode", "select st_code, st_name_ar from kbstate where st_code not in "
				+ " (select sp_statecode from kbcustomer_specialprices where sp_custid='"+custId+"')");
		userDefinedLookups.put("sp_statecode", "select st_code, st_name_ar from kbstate  ");
		super.initialize(smartyStateMap);
		
	}
	
	 @Override 
	 public String doInsert (HttpServletRequest rqs , boolean commit) {
		 Connection conn = null;
		 PreparedStatement pst = null;
		 String custId = replaceVarsinString(" {custidspecialprice} ", arrayGlobals).trim();
		 String userId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		 try {
			 conn = mysql.getConn();
			 inputMap_ori = filterRequest(rqs);
			 pst = conn.prepareStatement("insert into kbcustomer_specialprices (sp_custid , sp_statecode, sp_price, sp_rural_price, sp_createdby) "
				 		+ " values (? , ? , ? , ?, ? )");
			 for (String state : inputMap_ori.get("sp_statecode")){
				 pst.setString(1, custId);
				 pst.setString(2, state);
				 pst.setString(3, rqs.getParameter("sp_price"));
				 pst.setString(4, rqs.getParameter("sp_rural_price"));
				 pst.setString(5, userId);
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
			pst = conn.prepareStatement("delete from kbcustomer_specialprices where sp_id=?");
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
