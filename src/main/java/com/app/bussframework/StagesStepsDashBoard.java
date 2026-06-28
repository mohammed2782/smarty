package com.app.bussframework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class StagesStepsDashBoard {
	private Connection conn;
	
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	private static final String stage = "select * from kbstage order by stg_order ";
	private static final String step  = "select * from kbstep where stp_stgcode = ? and stp_finaldestination ='N' and stp_type='DECISION' "
			+ " and stp_rank like ?  order by stp_order";
	public ArrayList<StageBean> getAllStages(String rank) throws Exception{ 
		PreparedStatement pst = null;
		ArrayList<StageBean> stgList = new ArrayList<StageBean>();
		ResultSet rs = null;
		StageBean stageBean = new StageBean();
		try {
			pst = conn.prepareStatement(stage);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				stageBean.setStageCode(rs.getString("stg_code"));
				stageBean.setStageName(rs.getString("stg_name"));
				stageBean.setStageColor(rs.getString("stg_color"));
				stgList.add(stageBean);
				stageBean = new StageBean();
			}
			try {rs.close();}catch(Exception e) {};
			try {pst.close();}catch(Exception e) {};
			pst = conn.prepareStatement(step);
			int index = 0;
			ArrayList<StepBean> stplist = null;
			for (StageBean sb : stgList) {
				stplist = new ArrayList<StepBean>();
				StepBean stepBean = new StepBean();
				pst.setString(1, sb.getStageCode());
				pst.setString(2, "%"+rank+"%");
				rs = pst.executeQuery();
				while (rs.next()) {
					stepBean.setStepCode(rs.getString("stp_code"));
					stepBean.setStpName(rs.getString("stp_name"));
					stepBean.setStepIcon(rs.getString("stp_icon"));
					stepBean.setStepColor(rs.getString("stp_color"));
					stepBean.setStepFontColor(rs.getString("stp_fontcolor"));
					stepBean.setCountCases(rs.getString("stp_countcases"));
					stplist.add(stepBean);
					stepBean = new StepBean();
				}
				try {rs.close();}catch(Exception e) {};
				pst.clearParameters();
				stgList.get(index).setStepsList(stplist);
				index++;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {};
			try {pst.close();}catch(Exception e) {};
		}
		return stgList;
	}

}
