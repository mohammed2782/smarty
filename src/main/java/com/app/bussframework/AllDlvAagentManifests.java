package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;

public class AllDlvAagentManifests extends CoreMgr{
	private static final String manifestStageCases ="select count(*)as ctr, q_stage,q_step "
			+ " from p_cases   "
			+ " where c_dlvagent_manifestid=? group by q_stage,q_step order by q_stage   ";
	
	
	PreparedStatement pst=null;
	Connection myconn = null;
	LinkedHashMap<Integer, String> progressPercentageColor;
	DlvAgentManifestBean damb = new DlvAgentManifestBean();
	LinkedHashMap<String,String> stagesColor;
	HashMap<String, StepBean> steps;
	Utilities ut;
	int manifestDlvCtr =0, manifestCnclCtr=0, manifestOnWayCtr=0, ManifestPostponedCtr=0, ManifestRtnOnWayCtr, ManifestRetryCtr=0, total=0, totalProcessed=0;
	public AllDlvAagentManifests() {
		ut = new Utilities();
		//total = 0; totalProcessed=0;
		try {
			myconn = mysql.getConn();
			stagesColor = ut.getStagesColors(myconn);
			steps = ut.getStepsFromDB(myconn);
			pst = myconn.prepareStatement(manifestStageCases);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MainSql = " select dam_id, dam_agentid, dam_manifest_date , '' as dlvd, '' as rtn,'' as agentop, '' as init, "
				+ " '' as completed , '' as tot, '' as progress, '' as prtmanifest from " + 
				"  p_dlvagentmanifest where dam_branchid={userstorecode}   ";
		
		orderByCols = " dam_id desc ";
		userDefinedGridCols.add("dam_id");
		userDefinedGridCols.add("dam_manifest_date");
		userDefinedGridCols.add("dam_agentid");
		userDefinedGridCols.add("tot");
		userDefinedGridCols.add("dlvd");
		userDefinedGridCols.add("rtn");
		userDefinedGridCols.add("agentop");
		//userDefinedGridCols.add("prtmanifest");
		///userDefinedGridCols.add("inprocess");
		//userDefinedGridCols.add("postponed");
		//userDefinedGridCols.add("retry");
		userDefinedGridCols.add("progress");
		
		userDefinedColLabel.put("dam_id", "المنفيست");
		userDefinedColLabel.put("dam_manifest_date", "تاريخ المنفيست");
		userDefinedColLabel.put("dam_agentid", "المندوب");
		userDefinedColLabel.put("tot", "عدد الشحنات");
		userDefinedColLabel.put("dlvd", "تم التسليم");
		userDefinedColLabel.put("rtn", "راجع نهائي");
		userDefinedColLabel.put("prtmanifest", "");
		userDefinedColLabel.put("agentop", "عند المندوب");
		userDefinedColLabel.put("progress", "المنجز");
		
		//userDefinedColLabel.put("progress", " ");
		//userModifyTD.put("prtmanifest", "showPrtManifest({dam_id})");
		userModifyTD.put("tot", "showTot({dam_id})");
		userModifyTD.put("dlvd", "showDelivered({dam_id})");
		userModifyTD.put("rtn", "showRtn({dam_id})");
		userModifyTD.put("rtnwithagent", "showRtnWithAgent({dam_id})");
		userModifyTD.put("agentop", "showInProcess({dam_id})");
		userModifyTD.put("postponed", "showPostponed({dam_id})");
		userModifyTD.put("retry", "showRetry({dam_id})");
		userModifyTD.put("progress", "showProgressBar({dam_id})");
		
		progressPercentageColor = new LinkedHashMap<Integer, String> ();
		progressPercentageColor.put(25, "bg-danger");
		progressPercentageColor.put(50, "bg-warning");
		progressPercentageColor.put(75, "bg-info");
		progressPercentageColor.put(100, "bg-success");

		canFilter = true;
		userDefinedFilterCols.add("dam_id");
		userDefinedFilterCols.add("dam_agentid");
		userDefinedFilterCols.add("dam_manifest_date");
		userDefinedFilterColsHtmlType.put("dam_agentid", "DROPLIST");
		userDefinedLookups.put("dam_agentid", "select us_id, us_name from kbusers where us_rank = 'DLVAGENT' and us_branchcode={userstorecode} ");
	}
	
	/*public String showPrtManifest (HashMap<String,String> hashy) {
		
		
		return "<td style='width:20%'>"+printButton+"</td>";
		
	}*/
	public String showTot (HashMap<String,String> hashy) {
		ResultSet rs = null;
		damb = new DlvAgentManifestBean(); 
		total=0;
		totalProcessed = 0;
		try {
			pst.setString(1, hashy.get("dam_id"));
			rs = pst.executeQuery();
			String currentStage = null;
			StageBean sb = new StageBean();
			StepBean stpBean = new StepBean();
			ArrayList<StepBean> stepsList = new ArrayList<StepBean>();
			HashMap<String,StageBean> stagesMap = new HashMap<String,StageBean>();
			while(rs.next()) {
				stpBean = new StepBean();
				if (currentStage ==null) {
					currentStage = rs.getString("q_stage");
					sb = new StageBean();
					sb.setStageCode(currentStage);
				}
					
				if (rs.getString("q_stage").equalsIgnoreCase(currentStage))
					;
				else {
					sb.setStepsList(stepsList);
					stagesMap.put(currentStage, sb);
					sb = new StageBean();
					currentStage = rs.getString("q_stage");
					sb.setStageCode(currentStage);
					stepsList = new ArrayList<StepBean>();
				}
				stpBean.setStepCode(rs.getString("q_step"));
				stpBean.setCurrentCasesCtr(rs.getInt("ctr"));
				total +=rs.getInt("ctr");
				stepsList.add(stpBean);
			}
			if (currentStage !=null) {
				sb.setStepsList(stepsList);
				stagesMap.put(currentStage, sb);
			}
			//total = manifestDlvCtr+manifestCnclCtr+manifestOnWayCtr+ManifestPostponedCtr+ManifestRtnOnWayCtr+ManifestRetryCtr;
			totalProcessed = manifestDlvCtr+manifestCnclCtr;
			pst.clearParameters();
			damb.setStagesMap(stagesMap);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {}
		}
		String popUpBtn="<button type=\"button\" class=\"btn btn-secondary btn-min-width mr-1 mb-1\" "
				+ "onclick=\"popitup ('dlvAgentManifestUpdateablePopUp?dlvagent_manifestid="+hashy.get("dam_id")+"' , '' , 1300 ,600);\">"
						+ "معاينة الشحنات <i class=\"fa fa-eye\"></i></button>";
		String printButton = "<a href=\"../../PrintDlevieryAgentManifestSRVL?manifestId="+hashy.get("dam_id")+"\" >"
				 + "<input type=\"button\" value=\" طباعة كشف الشحنات \" class=\"btn btn-danger btn-sm\" ></a>'";	
		return "<td>"+total+"</br>"+popUpBtn+"</br>"+printButton+"</td>";
	}
	
	public String showDelivered(HashMap<String,String> hashy) {
		StageBean sb = damb.getStagesMap().get("DLV");
		return "<td style='width:20%'>"+getStepsButtons(sb, hashy.get("dam_id"))+"</td>";
	}
	
	public String showRtn (HashMap<String,String> hashy) {
		StageBean sb = damb.getStagesMap().get("CNCL");
		return "<td style='width:20%'>"+getStepsButtons(sb, hashy.get("dam_id"))+"</td>";
	}
	public String showInProcess (HashMap<String,String> hashy) {
		StageBean sb = damb.getStagesMap().get("AGENTOP");
		return "<td style='width:20%'>"+getStepsButtons(sb, hashy.get("dam_id"))+"</td>";
	}
	public String showRtnWithAgent (HashMap<String,String> hashy) {
		return "<td>"+ManifestRtnOnWayCtr+"</td>";
	}
	public String showPostponed (HashMap<String,String> hashy) {
		return "<td>"+ManifestPostponedCtr+"</td>";
	}
	public String showRetry (HashMap<String,String> hashy) {
		return "<td>"+ManifestRetryCtr+"</td>";
	}
	
	public String showProgressBar(HashMap<String,String> hashy) {
		double percentage= 0.0;
		percentage = ((double)totalProcessed/total) * 100;
		percentage  = round(percentage, 2);
		String colorClass="";
		for (int limitPer : progressPercentageColor.keySet()) {
			if (percentage<=limitPer) {
				colorClass = progressPercentageColor.get(limitPer);
				break;
			}
		}
		String td="<td style='width: 25%;'>"
				+ " <div class='progress mb-3' style='height:7px;'>"
				+ " <div class='progress-bar "+colorClass+"' role='progressbar' style='width:"+percentage+"%' aria-valuenow='"+percentage+"' "
						+ " aria-valuemin='"+percentage+"' aria-valuemax='100' ></div></div>"
						+ "<small>"+percentage+"% منجز</small>"
				+ " </td>";
		return td;
	}
	public  double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	public void releaseResources() {
		try {pst.close();} catch (SQLException e) {e.printStackTrace();}
		try {myconn.close();} catch (SQLException e) {e.printStackTrace();}
		super.releaseResources();
	}
	
	
	private String getStepsButtons(StageBean sb, String damid) {
		String ul = "<ul style='list-style: none;padding:5px;'>";
		if (sb != null) {
			for (StepBean stpBean : sb.getStepsList()) {
				if (sb.getStageCode().equalsIgnoreCase("DLV") || sb.getStageCode().equalsIgnoreCase("RTN"))
					totalProcessed += stpBean.getCurrentCasesCtr();
				//<a href="#" class="btn btn-primary btn-xs"><i class="fa fa-folder"></i> View </a>
				ul +="<li style='margin-bottom:5px;'>"
						+ "<a href='casesinqueue?stg_code="+sb.getStageCode()+"&stp_code="+stpBean.getStepCode()+"&filterby=c_dlvagent_manifestid&filtervalue="+damid+"' "
						+ " class=\"btn  btn-xs\" style='font-size: 12px;    padding: .1rem .1rem; background-color:"+steps.get(stpBean.getStepCode()).getStepColor()+";color: white;width: 100%;'>"
						+ " "+steps.get(stpBean.getStepCode()).getStpName()+"<span class=\"badge bg-dark rounded-pill\" style='margin-top: 4px;float:left'>"+stpBean.getCurrentCasesCtr()+"</span></a></li>";
			}
		}
		 ul += "</ul>";
		 return ul;
	}
}
