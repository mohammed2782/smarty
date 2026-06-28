package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.db.mysql;
import com.app.util.Utilities;

public class SetupDistricts extends CoreMgr{
	Utilities ut;
	public SetupDistricts () {
		MainSql = "select '' as noagent, cdi_id,  ifnull(dbr_rural, 'N') as dbr_rural , "
				+ " cdi_name ,GROUP_CONCAT(ifnull(agdi_usid,'') SEPARATOR ',') as agentsids , ifnull(dbr_agentshare,'') as dbr_agentshare, "
				+ " GROUP_CONCAT(ifnull(us_name,'') SEPARATOR ',') as agentsname , "
				+ " GROUP_CONCAT(ifnull(agdi_agentshare,'') SEPARATOR ',') as agentsshare , "
				+ " '' as assignagent,  '' as agdi_usid , '' as cdi_rural, '' as cdi_agentshare "
				+ " from kbcity_district  "
				+ " join kbbranches on cdi_stcode = branch_state  "
				+ " left join kbagent_district on agdi_districtcode = cdi_id and  agdi_branchcode = {userstorecode} "
				+ " left join kbusers on us_id = agdi_usid and us_active = 'Y' and us_branchcode={userstorecode} "
				+ " left join kbdistrict_branch_r on dbr_branchid =  {userstorecode} and dbr_districtcode = cdi_id "
				+ " where branch_id ={userstorecode} group by cdi_id ";

		userDefinedEditCols.add("cdi_rural");
		userDefinedEditCols.add("cdi_agentshare");
		
		userDefinedNewColsHtmlType.put("assignagent", "CHECKBOX");
		userDefinedEditMockUpCols.put("assignagent", " ");
		userDefinedLookups.put("assignagent", "select us_id, us_name from kbusers where us_rank ='DLVAGENT' and us_branchcode={userstorecode} ");
		
		canFilter = true;		
		canEdit = true;
		
		mainTable = "kbcity_district";
		keyCol = "cdi_id";
		UserDefinedPageRows = 500;
		
		userDefinedFilterCols.add("agdi_usid");
		userDefinedFilterCols.add("noagent");
		userDefinedFilterColsHtmlType.put("noagent", "CHECKBOX");
		
		userDefinedLookups.put("agdi_usid", "select us_id, us_name from kbusers where us_rank ='DLVAGENT' and us_branchcode={userstorecode} ");
		userDefinedLookups.put("noagent", "select 'Y', 'غير مسندوب للمندوب'  from dual ");
		
		userDefinedColLabel.put("noagent", " ");
		
		userDefinedGridCols.add("cdi_name");
		userDefinedGridCols.add("dbr_agentshare");
		userDefinedGridCols.add("dbr_rural");
		userDefinedGridCols.add("agentsids");
		
		
		userDefinedColsMustFill.add("cdi_stcode");
		userDefinedColsMustFill.add("cdi_name");
		userDefinedColsMustFill.add("cdi_rural");
		
		userDefinedColLabel.put("agdi_usid","مندوب التوصيل");
		userDefinedColLabel.put("assignagent","مندوب التوصيل");
		
		userDefinedColLabel.put("cdi_name","إسم المنطقه");
		userDefinedColLabel.put("cdi_id","مناطق لم تسند إلى مندوب");
		userDefinedColLabel.put("cdi_ctycode","المدينه");
		userDefinedColLabel.put("dbr_rural","منطقة طرفية");
		userDefinedColLabel.put("cdi_rural","منطقة طرفية");
		userDefinedColLabel.put("agents","المندوب");
		userDefinedColLabel.put("agentsids"," ");
		userDefinedColLabel.put("dbr_agentshare","أجرة النقل الخاصة بالمنطقة");
		userDefinedColLabel.put("cdi_agentshare","أجرة النقل الخاصة بالمنطقة");
		//userDefinedFilterCols.add("cdi_id");
		
		userDefinedLookups.put("cdi_id", "select cdi_id, cdi_name from kbcity_district "
				+ " join kbbranches on cdi_stcode = branch_state "
				+ " where branch_id ={userstorecode}  ");
		
		
		userDefinedFilterColsHtmlType.put("cdi_id", "DROPLIST");
		
		userDefinedLookups.put("dbr_rural", "select kbcode, kbdesc from kbgeneral where kbcat1='YESNO'");
		userDefinedLookups.put("cdi_rural", "select kbcode, kbdesc from kbgeneral where kbcat1='YESNO'");
		
		userDefinedNewColsDefualtValues.put("cdi_stcode", new String [] {"BGD"});
		userModifyTD.put("agentsids", "showAgents({cdi_id}, {agentsids},{agentsname}, {agentsshare} )");
		userDefinedNewColsDefualtValues.put("dbr_agentshare", new String[] {"0"});
		userDefinedNewColsDefualtValues.put("cdi_agentshare", new String[] {"0"});
		
		userDefinedColsMustFill.add("dbr_rural");
		userDefinedColsMustFill.add("dbr_agentshare");
		userDefinedCaption = "المناطق";
		
	}
	
	
	
	public String showAgents(HashMap<String,String> hashy) {
		
		ArrayList <String> arrِAgentsId = Utilities.SplitStringToArrayList(hashy.get("agentsids"),",");
		ArrayList <String> arrِAgentsName = Utilities.SplitStringToArrayList(hashy.get("agentsname"),",");
		ArrayList <String> arrِAgentsShares = Utilities.SplitStringToArrayList(hashy.get("agentsshare"),",");
		String td = "<td>";
		int i = 0;
		for (String name : arrِAgentsName) {
			td +="<div class='row' style='margin-bottom:5px;' >";
			td +="<div class='col-7'>"+name+"</div>";
			if (!arrِAgentsShares.get(i).equalsIgnoreCase("0")) {
				td +="<div class='col-3'><span class=\"badge bg-primary\" style=\"font-size:12px;\">"+arrِAgentsShares.get(i)+"</span></div>";
			}
			
			td +="</div>";
			i++;
		}
		td +="<div class='row'><div class='col-7 offset-5'><button type=\"button\" class=\"btn btn-sm btn-warning\" "
				+ "onclick=\"popitup ('assignAgentToDistrictPopUp?districtIdPopUp="+hashy.get("cdi_id")+"' , 'تعيين مندوب' , 1000 ,600);\">تعيين مندوب</button>"
						+ "</div></div>";
		td += "</td>";
		
		return td;
	}
	
	
	public void initialize(HashMap smartyStateMap){
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		userDefinedFilterLookups.put("cdi_id", "select cdi_id, cdi_name from kbcity_district "
				+ " join kbbranches on cdi_stcode = branch_state "
				+ " where branch_id ="+currentBranch+" "
				+ " and cdi_id not in (select agdi_districtcode from kbagent_district where agdi_branchcode = "+currentBranch+")  ");
		super.initialize(smartyStateMap);
		
		String fromdt = "ALL";
		String todt = "ALL";
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {
					if (parameter.equals("noagent")) {
						fromdt =  value;
						foundSearch = true;
						//search_paramval.remove("fromdt");
					} 
				}
			}
		}
		if (foundSearch) {
			MainSql = "select '' as noagent, cdi_id, dbr_rural, cdi_name ,GROUP_CONCAT(ifnull(agdi_usid,'') SEPARATOR ',') as agentsids ,"
					+ " dbr_agentshare, "
					+ " GROUP_CONCAT(ifnull(us_name,'') SEPARATOR ',') as agentsname , "
					+ " GROUP_CONCAT(ifnull(agdi_agentshare,'') SEPARATOR ',') as agentsshare , "
					+ " '' as assignagent,  '' as agdi_usid, '' as cdi_rural, '' as cdi_agentshare "
					+ " from kbcity_district  "
					+ " join kbbranches on cdi_stcode = branch_state  "
					+ " left join kbagent_district on agdi_districtcode = cdi_id and  agdi_branchcode = "+currentBranch+" "
					+ " left join kbusers on us_id = agdi_usid and us_active = 'Y' and us_branchcode="+currentBranch+"  "
					+ " left join kbdistrict_branch_r on dbr_branchid = "+currentBranch+" and dbr_districtcode = cdi_id "
					+ " where branch_id ="+currentBranch
					+ " and cdi_id not in (select agdi_districtcode from kbagent_district where agdi_branchcode ="+currentBranch+" ) "
					+ " group by cdi_id ";
				
			}
	}
	@Override 
	public StringBuilder genListing() {
		search_paramval.remove("noagent");
		return super.genListing();
	}
	
	@Override 
	 public String doUpdate (HttpServletRequest rqs , boolean commit) {
		 Connection conn = null;
		 PreparedStatement pst = null;
		 int userId = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		 int branchCode = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		 try {
			 conn = mysql.getConn();
			 String keyCol = parseUpdateRqs(rqs);
			
			 pst = conn.prepareStatement("update kbdistrict_branch_r set dbr_rural= ? , dbr_agentshare=?  where dbr_branchid=? and dbr_districtcode=?");
			 pst.setString(1, inputMap_ori.get("cdi_rural")[0]);
			 pst.setString(2, inputMap_ori.get("cdi_agentshare")[0]);
			 pst.setInt(3, branchCode);
			 pst.setString(4, keyCol);
			 int updatedRows = pst.executeUpdate();
			 
			 try {pst.close();}catch(Exception e) {}
			 if (updatedRows ==0) {
				 pst = conn.prepareStatement("insert into kbdistrict_branch_r "
					 		+ "(dbr_branchid, dbr_districtcode, dbr_agentshare, dbr_rural, dbr_createdby ) "
					+ " values (? 			, ?  			  , ?			  , ?		 , ?)");
				 pst.setInt(1, branchCode);
				 pst.setString(2, keyCol);
				 pst.setString(3, inputMap_ori.get("cdi_agentshare")[0]);
				 pst.setString(4, inputMap_ori.get("cdi_rural")[0]);
				 pst.setInt(5, userId);
				 pst.executeUpdate();
			 }
			 conn.commit();
			 
		 }catch(Exception e) {
			 try {conn.rollback();}catch(Exception eRoll) {}
			 e.printStackTrace();
		 }finally {
			 try {pst.close();}catch(Exception e) {}
			 try {conn.close();}catch(Exception e) {}
		 }
		 return "";
	 }
}
