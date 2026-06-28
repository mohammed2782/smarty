package com.app.setup;

import java.sql.PreparedStatement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;

public class AgentsCustomers extends CoreMgr {
	public AgentsCustomers () {
		MainSql = "select * from kbcustomers where cust_assigned_pickup_agent='{customersusid}'"
				+ " and cust_branch = {userstorecode} ";
		userDefinedCaption = "زبائن المندوب";
		userDefinedColLabel.put("cust_name","الزبون");
		canDelete = true;
		canNew = true;
		
		mainTable = "kbcustomers";
		keyCol = "cust_id";
		
		userDefinedNewCols.add("cust_id");
		userDefinedGridCols.add("cust_id");
		//userDefinedNewCols.add("agdi_districtcode");
		
		//userDefinedNewColsDefualtValues.put("agdi_usid", new String[] {"{districtsusid}"});
		
		//userDefinedReadOnlyNewCols.add("agdi_usid");
		
		userDefinedColLabel.put("cust_id", "الزبون");
		//userDefinedColLabel.put("agdi_districtcode", "المنطقه");
		
		//userDefinedNewLookups.put("agdi_districtcode", "select cdi_code, cdi_name from kbcity_district  ");
		userDefinedLookups.put("cust_id", "select cust_id, cust_name from kbcustomers where  cust_branch = {userstorecode}");
		myhtmlmgr.refreshPageOnDelete = true;
		
		userDefinedNewLookups.put("cust_id", "select cust_id, cust_name from"
				+ " kbcustomers where (cust_assigned_pickup_agent is null or cust_assigned_pickup_agent = '0')"
				+ " and  cust_branch = {userstorecode} ");
		myhtmlmgr.refreshPageOnDelete = true;
		userDefinedNewColsHtmlType.put("cust_id", "DROPLIST");
	}
	
	@Override
	public String doInsert(HttpServletRequest rqs , boolean autoCommit){
		String statusMsg= "customer added";
		String agentId =  replaceVarsinString(" {customersusid} ", arrayGlobals).trim();
		Map <String , String[]> inputMap = filterRequest (rqs);
		PreparedStatement pst = null;
		
		try{
			pst = conn.prepareStatement(" update kbcustomers set cust_assigned_pickup_agent =? where cust_id=?");
			pst.setString(1, agentId);
			pst.setString(2, inputMap.get("cust_id")[0]);
			pst.executeUpdate();
			conn.commit();
		}catch(Exception e){
			/*log error*/
			statusMsg = "Error at user creation, error ("+e.getMessage()+")";
			try{conn.rollback();}catch(Exception ignoreE){}
			e.printStackTrace();
		}finally{
			try{pst.close();}catch(Exception e){/* ignore*/}
		}
		
		return statusMsg;
	}
	
	@Override
	public String doDelete(HttpServletRequest rqs){
		String statusMsg= "customer removed";
		Map <String , String[]> inputMap = filterRequest (rqs);
		PreparedStatement pst = null;
		
		try{
			pst = conn.prepareStatement(" update kbcustomers set cust_assigned_pickup_agent =null where cust_id=?");
			pst.setString(1, inputMap.get("cust_id")[0]);
			pst.executeUpdate();
			conn.commit();
		}catch(Exception e){
			/*log error*/
			statusMsg = "Error at user creation, error ("+e.getMessage()+")";
			try{conn.rollback();}catch(Exception ignoreE){}
			e.printStackTrace();
		}finally{
			try{pst.close();}catch(Exception e){/* ignore*/}
		}
		
		return statusMsg;
	}
}
