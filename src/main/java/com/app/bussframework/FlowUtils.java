package com.app.bussframework;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import com.app.util.Utilities;


public class FlowUtils {
	
	public void forceCasesToQueue(Connection conn, int caseId, String action, int actionTakenBy , 
			String stageCode, String stepCode, int branch, String qRmk) throws Exception{
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		String finalStep="", sqltoExecuteB4EnterStep="", sqltoExecuteAfterEnterStep="";
		try {
			if (Utilities.isThereAnyPaymentMadeForTheShipment(conn, caseId)) {
				System.out.println("there is payment made on the shipment,"
						+ " so we can't forceCasesToQueue next->"+caseId);
				return ;
			}
			pst1 = conn.prepareStatement("update p_cases set q_action=?, q_action_takenby=?, q_rmk=? , q_action_datetime=now() where c_id=?");
			pst1.setString(1, action);
			pst1.setInt(2, actionTakenBy);
			pst1.setString(3, qRmk);
			pst1.setInt(4, caseId);
			pst1.executeUpdate();
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			
			pst1 = conn.prepareStatement("select stp_execquery_b4enter, stp_execquery_afterenter  , stp_finaldestination  "
					+ " from  kbstep where stp_stgcode =? and stp_code=?");
			pst1.setString(1, stageCode);
			pst1.setString(2, stepCode);
			rs1 = pst1.executeQuery();
			if (rs1.next()) {
				finalStep	  = rs1.getString("stp_finaldestination");
				sqltoExecuteB4EnterStep = rs1.getString("stp_execquery_b4enter");
				sqltoExecuteAfterEnterStep = rs1.getString("stp_execquery_afterenter");
			}	
			if (sqltoExecuteB4EnterStep !=null && sqltoExecuteB4EnterStep.length()>0){
				exectueCode(conn, sqltoExecuteB4EnterStep, caseId);
			}
			String nextQ_Code = stageCode+"__"+stepCode;
			try {rs1.close();}catch(Exception e) {/*ignore*/}
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			
			archiveQ (conn, caseId);
			
			pst1  = conn.prepareStatement("update p_cases set  "
							+ "q_code=?, q_stage=?, q_step=?,  q_branch=?,  "
							+ "q_previous_action_taken_by=?, q_previous_action=?,q_enterdate=now(), q_status='ACTV', "
							+ "q_previous_rmk = ?, q_action=null, q_action_takenby=null where c_id=?");
			pst1.setString(1, nextQ_Code);
			pst1.setString(2, stageCode);
			pst1.setString(3, stepCode);
			pst1.setInt(4, branch);
			pst1.setInt(5, actionTakenBy);
			pst1.setString(6, action);
			pst1.setString(7, qRmk);
			pst1.setInt(8, caseId);
			pst1.executeUpdate();
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			if (finalStep.equalsIgnoreCase("Y")) {
				pst1 = conn.prepareStatement("update p_cases set q_status = ? where c_id=?");
				pst1.setString(1, "END");
				pst1.setInt(2, caseId);
				pst1.executeUpdate();
			}
			
			if (sqltoExecuteAfterEnterStep !=null && sqltoExecuteAfterEnterStep.length()>0){
				exectueCode(conn, sqltoExecuteAfterEnterStep, caseId);
			}
			
		}catch(Exception e) {
			//log
			throw e;
		}finally {
			try {rs1.close();}catch(Exception e) {/*ignore*/}
			//try {rsCheck.close();}catch(Exception e) {/*ignore*/}
			//try {pstCheck.close();}catch(Exception e) {/*ignore*/}
			try {pst1.close();}catch(Exception e) {/*ignore*/}
		
	}
	}
	
	
	public void createNewCaseInQueue (Connection conn,int caseId, String stageCode,String stepCode , int branch) throws Exception{
			
			PreparedStatement pst = null;
			
			String rankCode = "", q_code="";
			try {
			
				q_code = stageCode+"__"+stepCode;
				
				pst  = conn.prepareStatement("update p_cases "
						+ "set q_code=?, q_stage=?, q_step=?, q_rank=?, q_branch=?, q_comingfrombranch=?, q_status='ACTV',"
						+ " q_enterdate=now() where c_id=?");
				pst.setString(1, q_code);
				pst.setString(2, stageCode);
				pst.setString(3, stepCode);
				pst.setString(4, rankCode);
				pst.setInt(5, branch);
				pst.setInt(6, branch);
				pst.setInt(7, caseId);
				pst.executeUpdate();
				
			}catch(Exception e) {
				//log
				throw e;
			}finally {
				try {pst.close();}catch(Exception e) {/*ignore*/}
				
			}
		}
	
	
	/**
	 * 1- create a new queue in p_cases for the case
	 * 
	 * @return q_id 
	 */
	public void createNewCaseInQueue (Connection conn,int caseId , int branch) throws Exception{
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		String stageCode="", stepCode="" , rankCode = "", q_code="";
		try {
		
			pst  = conn.prepareStatement("select stg_code,stp_code, stp_rank from kbstage join kbstep on (stg_code = stp_stgcode)"
					+ " where stg_order=1 and stp_order=1");//ge the first stage
			rs = pst.executeQuery();
			if (rs.next()) {
				stageCode = rs.getString("stg_code");
				stepCode  = rs.getString("stp_code");
				rankCode  = rs.getString("stp_rank");
			}
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
			
			q_code = stageCode+"__"+stepCode;
			
			pst  = conn.prepareStatement("update p_cases "
					+ "set q_code=?, q_stage=?, q_step=?, q_rank=?, q_branch=?, q_comingfrombranch=?, q_status='ACTV', q_enterdate=now() where c_id=?");
			pst.setString(1, q_code);
			pst.setString(2, stageCode);
			pst.setString(3, stepCode);
			pst.setString(4, rankCode);
			pst.setInt(5, branch);
			pst.setInt(6, branch);
			pst.setInt(7, caseId);
			pst.executeUpdate();
			
		}catch(Exception e) {
			//log
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
	}
	/*
	 * move next only for Decision Steps , takes the qid and move to the next step in the queue and change the branch
	 */
	public void MoveDecisionStepNext (Connection conn , int caseId , String action, int actionTakenBy,
			HashMap<String,String> queueColsToUpdate, int toBranch, String stage, String step, String qRmk) throws Exception{
		PreparedStatement pst1 = null;
		
		try {
			MoveDecisionStepNext (conn , caseId, action, actionTakenBy , toBranch, stage, step, qRmk);
			String colToUpdate="";
			boolean firstCol = true;
			int colNo=1;
			if (queueColsToUpdate.size()>0) {
				for (String col :queueColsToUpdate.keySet()) {
					if (!firstCol)
						colToUpdate+=", ";
					colToUpdate += col+"=? ";
					firstCol = false;
				}
				
				String sql = "update p_cases set "+colToUpdate+" , q_action_datetime=now()  where c_id=?";
				pst1 = conn.prepareStatement(sql);
				for (String col :queueColsToUpdate.keySet()) {
					pst1.setString(colNo, queueColsToUpdate.get(col));
					colNo++;
				}
				pst1.setInt(colNo, caseId);
				pst1.executeUpdate();
			}
		}catch(Exception e) {
			//log
			throw e;
		}finally {
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			
		}
	}
	
	public int MoveDecisionStepNext (Connection conn , int caseId, String action, int actionTakenBy, String stage, String step, String qRmk) throws Exception{
		return MoveDecisionStepNext (conn , caseId, action, actionTakenBy, 0, 0, stage, step, qRmk);
	}
	
	public int MoveDecisionStepNext (Connection conn , int caseId, String action, int actionTakenBy, int toBranch, String stage, String step, String qRmk) throws Exception{
		return MoveDecisionStepNext (conn , caseId, action, actionTakenBy, toBranch,0, stage, step, qRmk);
	}
	/*
	 * move next only for Decision Steps , takes the qid and move to the next step in the queue and change the branch
	 */
	public int MoveDecisionStepNext (Connection conn , int caseId, String action, int actionTakenBy , int toBranch, int assignedTo, 
			String stage, String step, String qRmk) throws Exception{
		PreparedStatement pst1 = null, pstCheck = null;
		ResultSet rs1 = null, rsCheck = null;
		String  nextStepCode = null, nextStageCode = null, 
				currAction = null, currQ_Code = null , nextQ_Code, nextRank = null , finalStep=null,
				prevActionTakenBy = null,
				conditionStep1 = "", conditionStep2= "", sqltoExecuteB4EnterStep = "", sqltoExecuteAfterEnterStep = "" , decisionExecSql="";
		int goStep1=0, goStep2=0;
		int chosenStep = 0;
		int  currentBranch=0, stepid = 0 , caseid = 0 , newQid=0;
		boolean allowMove = false;
		try {
			if (Utilities.isThereAnyPaymentMadeForTheShipment(conn, caseId)) {
				System.out.println("there is payment made on the shipment, so we can't move in decision next->"+caseId);
				return 0;
			}
			// before anything must check if the case is in this step and stage when updated 
			pstCheck = conn.prepareStatement("select 1 from p_cases where c_id = ? and q_stage=? and q_step=? limit 1");
			pstCheck.setInt(1, caseId);
			pstCheck.setString(2, stage);
			pstCheck.setString(3, step);
			rsCheck = pstCheck.executeQuery();
			if (rsCheck.next())
				if (rsCheck.getInt(1) == 1)
					allowMove = true;
			if (!allowMove)
				return 0;
				
			/* 0 - update the action
			 * 1- get the current q info.
			 * 2- check the decision made
			 * 3- update the current q to CLS.
			 * 4- insert the new queue based on the decision.  
			 */
			pst1 = conn.prepareStatement("update p_cases set q_action=?, q_action_takenby=?, q_rmk=?, q_action_datetime=now() where c_id=?");
			pst1.setString(1, action);
			pst1.setInt(2, actionTakenBy);
			pst1.setString(3, qRmk);
			pst1.setInt(4, caseId);
			pst1.executeUpdate();
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			

			String executeCodeB4ExitStep = "";
			pst1  = conn.prepareStatement("select stp_executecode_b4exit, q_action_takenby, q_branch , q_code, stp_id, q_stage, q_step , q_action  from p_cases "
					+ " join kbstep on (q_stage= stp_stgcode and q_step = stp_code)  where c_id=?  and q_status !='CLS'");
			pst1.setInt(1, caseId);
			rs1 = pst1.executeQuery();
			if (rs1.next()) {
				currQ_Code = rs1.getString("q_code");
				/*currStepCode = rs1.getString("q_step");
				currStageCode = rs1.getString("q_stage");*/
				currAction = rs1.getString("q_action");
				stepid = rs1.getInt("stp_id");
				currentBranch = rs1.getInt("q_branch");
				prevActionTakenBy = rs1.getString("q_action_takenby");
				executeCodeB4ExitStep = rs1.getString("stp_executecode_b4exit");
			}else {
				return 0;// nothing to do, means the queue already moved by another sesssion
			}
			try {rs1.close();}catch(Exception e) {/*ignore*/}
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			
			pst1 = conn.prepareStatement("select stpd_executequery, stpd_conditiongostep1, stpd_gotostep1,"
					+ " stpd_gotostep2, stpd_conditiongostep2"
					+ " from kbstep_decision where stpd_deleted='N' and stpd_stpid =? and stpd_code=?");
			pst1.setInt(1, stepid);
			pst1.setString(2, currAction);
			rs1 = pst1.executeQuery();
			if (rs1.next()) {
				conditionStep1 = rs1.getString("stpd_conditiongostep1"); 
				conditionStep2 = rs1.getString("stpd_conditiongostep2");
				goStep1		   = rs1.getInt("stpd_gotostep1"); 
				goStep2 	   = rs1.getInt("stpd_gotostep2");
				decisionExecSql = rs1.getString("stpd_executequery");
			}
			try {rs1.close();}catch(Exception e) {/*ignore*/}
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			
			if (goStep1 == 0)//if no step to go to then return 
				return 0;
			if (goStep1 !=0 && goStep2 ==0 ) {
				if (conditionStep1 !=null && !conditionStep1.trim().isEmpty()) {
					pst1 = conn.prepareStatement(conditionStep1);
					pst1.setInt(1, caseId);
					rs1 = pst1.executeQuery();
					if (rs1.next()) {
						if (rs1.getInt(1) == 1) {
							chosenStep =goStep1;
						}
					}
					try {rs1.close();}catch(Exception e) {/*ignore*/}
					try {pst1.close();}catch(Exception e) {/*ignore*/}
				}else {
					chosenStep =goStep1;
				}
			}else{
				if (goStep1 !=0 && goStep2 !=0) {
					// we need to validate the conditions
					pst1 = conn.prepareStatement(conditionStep1);
					pst1.setInt(1, caseId);
					rs1 = pst1.executeQuery();
					if (rs1.next()) {
						if (rs1.getInt(1) == 1) {
							chosenStep =goStep1;
						}
					}
					try {rs1.close();}catch(Exception e) {/*ignore*/}
					try {pst1.close();}catch(Exception e) {/*ignore*/}
					if (chosenStep == 0) {// if chosen step still == 0 then we check condition 2
						pst1 = conn.prepareStatement(conditionStep2);
						pst1.setInt(1, caseId);
						rs1 = pst1.executeQuery();
						if (rs1.next()) {
							if (rs1.getInt(1) == 1) {
								chosenStep =goStep2;
							}
						}
					}
				}
			}
			try {rs1.close();}catch(Exception e) {/*ignore*/}
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			if (chosenStep ==0)// if still we can't find route
				return 0;

			if (executeCodeB4ExitStep !=null && executeCodeB4ExitStep.length()>0){
				exectueCode(conn, executeCodeB4ExitStep, caseId);
			}
			
			if (decisionExecSql !=null && decisionExecSql.length()>0){
				exectueCode(conn, decisionExecSql, caseId);
			}
			
			/*
			 * Step Details
			 */
			// now get the step details
			pst1 = conn.prepareStatement("select stp_executecode_b4exit, stp_execquery_b4enter, stp_execquery_afterenter  , stp_finaldestination , stp_code, stp_stgcode, stp_rank "
					+ " from  kbstep where stp_id =?");
			pst1.setInt(1, chosenStep);
			rs1 = pst1.executeQuery();
			if (rs1.next()) {
				nextStepCode  = rs1.getString("stp_code");
				nextStageCode = rs1.getString("stp_stgcode");
				nextRank      = rs1.getString("stp_rank");
				finalStep	  = rs1.getString("stp_finaldestination");
				sqltoExecuteB4EnterStep = rs1.getString("stp_execquery_b4enter");
				sqltoExecuteAfterEnterStep = rs1.getString("stp_execquery_afterenter");
				executeCodeB4ExitStep = rs1.getString("stp_executecode_b4exit");
			}	
			if (sqltoExecuteB4EnterStep !=null && sqltoExecuteB4EnterStep.length()>0){
				exectueCode(conn, sqltoExecuteB4EnterStep, caseId);
			}
			nextQ_Code = nextStageCode+"__"+nextStepCode;
			try {rs1.close();}catch(Exception e) {/*ignore*/}
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			if (toBranch ==0)
				toBranch = currentBranch;
			
			archiveQ (conn, caseId);
			
			pst1  = conn.prepareStatement("update p_cases set  "
							+ "q_code=?			 , q_stage=?				   , q_step=?			, q_rank=?			   , q_branch=?, "
							+ "q_previous_qcode=?, q_previous_action_taken_by=?, q_previous_action=?, q_comingfrombranch=? , q_assigned_to=?, "
							+ "q_enterdate=now() , q_status='ACTV'			   , q_action=null		, q_action_takenby=null, q_rmk='', "
							+ "q_previous_rmk=?  , q_action_datetime = null where c_id=?");
			pst1.setString(1, nextQ_Code);
			pst1.setString(2, nextStageCode);
			pst1.setString(3, nextStepCode);
			pst1.setString(4, nextRank);
			pst1.setInt(5, toBranch);
			pst1.setString(6, currQ_Code);
			pst1.setString(7, prevActionTakenBy);
			pst1.setString(8, currAction);
			pst1.setInt(9, currentBranch);
			pst1.setInt(10, assignedTo);
			pst1.setString(11, qRmk);
			pst1.setInt(12, caseId);
			pst1.executeUpdate();
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			if (finalStep.equalsIgnoreCase("Y")) {
				pst1 = conn.prepareStatement("update p_cases set q_status = ? where c_id=?");
				pst1.setString(1, "END");
				pst1.setInt(2, caseId);
				pst1.executeUpdate();
			}
			
			if (sqltoExecuteAfterEnterStep !=null && sqltoExecuteAfterEnterStep.length()>0){
				exectueCode(conn, sqltoExecuteAfterEnterStep, caseId);
			}
		}catch(Exception e) {
			//log
			throw e;
		}finally {
			try {rs1.close();}catch(Exception e) {/*ignore*/}
			try {rsCheck.close();}catch(Exception e) {/*ignore*/}
			try {pstCheck.close();}catch(Exception e) {/*ignore*/}
			try {pst1.close();}catch(Exception e) {/*ignore*/}
			
		}
		return 0;
	}
	
	public void  archiveQ (Connection conn, int caseId)throws Exception{
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("insert into p_queue_hist "
			+ " 		(q_caseid		 , q_code  		, q_stage	  	   , q_step     			   , q_rank,"
			+ "			 q_branch		 , q_action		, q_previous_qcode , q_enterdate			   , q_status,"
			+ "			 q_action_takenby, q_assigned_to, q_previous_action, q_previous_action_taken_by, q_comingfrombranch,"
			+ "			 q_manifest_id   , q_postopnedto, q_postponedoption, q_rmk					   , q_previous_rmk,"
			+ "          q_action_datetime)  "
			+ " select   c_id  			 , q_code  		, q_stage	  	   , q_step     			   , q_rank," + 
			"			 q_branch		 , q_action		, q_previous_qcode , q_enterdate			   , q_status," + 
			"			 q_action_takenby, q_assigned_to, q_previous_action, q_previous_action_taken_by, q_comingfrombranch,"
			+ "			 q_manifest_id   , q_postopnedto, q_postponedoption, q_rmk 					   , q_previous_rmk,"
			+ "          q_action_datetime "
			+ " from p_cases where c_id =? ");
			pst.setInt(1, caseId);
			pst.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
	}
	
	public void exectueCode (Connection conn, String q, int caseId) throws Exception{
		PreparedStatement pst = null;
		try {
			if (q.startsWith("JAVA::") || q.startsWith("java::")) {
				HashMap<String,String> hashy = new HashMap<String,String>();
				hashy.put("c_id", caseId+"");
				ExecuteSpecialRoutinesInFLow esrf = new ExecuteSpecialRoutinesInFLow();
				
				String funcName    = q.replaceFirst("JAVA::", "").trim().replaceAll(" ", "");
				 Method mymethod = esrf.getClass().getMethod(funcName, Connection.class, HashMap.class);
				 mymethod.invoke(esrf , conn, hashy);	
			}else {
				q = q.replaceFirst("SQL::", "");
				q = q.replaceAll("\\?", caseId+"");
				pst = conn.prepareStatement(q);
				pst.executeUpdate();
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
	}
}