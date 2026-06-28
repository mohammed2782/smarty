package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.db.mysql;

public class DlvAgentSpecialPrices extends CoreMgr{
	public DlvAgentSpecialPrices() {
		MainSql = "select  *  from kb_pickupagent_prices where sp_agentid='{dlvagentidpopup}'";
		canNew = true;
		canDelete = true;
		
		mainTable = "kb_pickupagent_prices";
		keyCol = "sp_id";
		
		//userDefinedNewCols.add("sp_custid");
		userDefinedNewCols.add("sp_statecode");
		userDefinedNewCols.add("sp_price");
		userDefinedNewCols.add("sp_rural_price");
		userDefinedNewCols.add("sp_superpriority");
		
		userDefinedGridCols.add("sp_statecode");
		userDefinedGridCols.add("sp_price");
		userDefinedGridCols.add("sp_rural_price");
		userDefinedGridCols.add("sp_superpriority");
		
		
		userDefinedColLabel.put("sp_statecode","المحافظة");
		userDefinedColLabel.put("sp_price","مبلغ الشحن");
		userDefinedColLabel.put("sp_rural_price","مبلغ الشحن للأقضيه");
		userDefinedColLabel.put("sp_superpriority","أولوبة السعر أهم من أولوية سعر الزبون");
		
		userDefinedColsMustFill.add("sp_statecode");
		userDefinedColsMustFill.add("sp_price");
		userDefinedColsMustFill.add("sp_rural_price");
		userDefinedNewColsDefualtValues.put("sp_custid", new String[] {"{custidspecialprice}"});
		myhtmlmgr.refreshPageOnDelete = true;
		userDefinedLookups.put("sp_statecode", "select st_code, st_name_ar from kbstate");
		userDefinedLookups.put("sp_superpriority", "select kbcode, kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedCaption = "أسعار نقل خاصه للمندوب";
	}
	
	@Override
	public void initialize(HashMap smartyStateMap){
		String sp_agentid = replaceVarsinString(" {dlvagentidpopup} ", arrayGlobals).trim();
		userDefinedNewLookups.put("sp_statecode", "select st_code, st_name_ar from kbstate where st_code not in "
				+ " (select sp_statecode from kb_pickupagent_prices where sp_agentid='"+sp_agentid+"')");
		super.initialize(smartyStateMap);
	}
	
	 @Override 
	 public String doInsert (HttpServletRequest rqs , boolean commit) {
		 Connection conn = null;
		 PreparedStatement pst = null;
		 String sp_agentid = replaceVarsinString(" {dlvagentidpopup} ", arrayGlobals).trim();
		 String userId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
		 try {
			 conn = mysql.getConn();
			 pst = conn.prepareStatement("insert into kb_pickupagent_prices "
			 		+ "(sp_agentid , sp_statecode, sp_price, sp_rural_price, sp_createdby, sp_superpriority) "
			 		+ " values (? , ? , ? , ?, ? , ?)");
			 pst.setString(1, sp_agentid);
			 pst.setString(2, rqs.getParameter("sp_statecode"));
			 pst.setString(3, rqs.getParameter("sp_price"));
			 pst.setString(4, rqs.getParameter("sp_rural_price"));
			 pst.setString(5, userId);
			 pst.setString(6, rqs.getParameter("sp_superpriority"));
			 
			 pst.executeUpdate();
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
			pst = conn.prepareStatement("delete from kb_pickupagent_prices where sp_id=?");
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