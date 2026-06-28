package com.app.returnables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.bussframework.FlowUtils;
import com.app.bussframework.SingleQueue;
import com.app.bussframework.SingleQueueFactory;
import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;

public class IsolateReceiptsByBarcode extends CoreMgr{
	public IsolateReceiptsByBarcode() {
		MainSql = "select  '' as receiptno, '' as ownerbranch, '' as cust, '' as addr, '' as rtnqty, '' as amt, '' as del from dual where 1=0";
		
		userDefinedColLabel.put("receiptno", "رقم الوصل");
		userDefinedColLabel.put("ownerbranch", "الفرع صاحب الوصل");
		userDefinedColLabel.put("cust", "العميل - المتجر");
		userDefinedColLabel.put("addr", "العنوان");
		userDefinedColLabel.put("rtnqty", "عدد القطع الراجعه");
		userDefinedColLabel.put("amt", "مبلغ الوصل");
		userDefinedColLabel.put("del", " ");
		
		canEdit = true;
		mainTable = "p_cases";
		keyCol = "c_id";
		displayMode ="GRIDEDIT";
	}
	@Override
	public void initialize(HashMap smartyStateMap){
		super.initialize(smartyStateMap);
		
		
		userDefinedCaption ="<input type='hidden' style='color:#424242;background-color:#E9E5E5;' value=0 id='numberofrowsscanned' name='numberofrowsscanned' />";
	}
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		int userid = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		parseUpdateRqs(rqs);
		Connection conn = null;
		FlowUtils fu = new FlowUtils();
		Utilities ut = new Utilities();
		int rowsNo =0, caseId= 0;
		String q_stage, q_step, action;
		String msg ="تم تحديث الحالات";
		double dlvCost = 0.0;
		try {
			conn = mysql.getConn();
			rowsNo = Integer.parseInt(inputMap_ori.get("numberofrowsscanned")[0]);
			int cid=0;
			ArrayList<Integer> cIdList = new ArrayList<Integer> ();
			SingleQueueFactory sqf = new SingleQueueFactory();
			HashMap<Integer, String> actionsMap = new HashMap<Integer, String>();
			//doUpdateStepActions(conn, cIdList, actionsMap, queueActionsParamsBean, userid,  currentBranch, "BRANCHES", "RTN_INSTORE_WAITLIAISON");
			for (int i=1 ; i<=rowsNo ; i++){
				if (inputMap_ori.containsKey("c_id_row_"+i)) {
					cid = Integer.parseInt(inputMap_ori.get("c_id_row_"+i)[0]);
					q_stage = inputMap_ori.get("q_stagecode_row_"+i)[0];
					q_step = inputMap_ori.get("q_stepcode_row_"+i)[0];
					if (	(q_step.equalsIgnoreCase("PART_SUCC") && q_stage.equalsIgnoreCase("DLV")) || 
							(q_step.equalsIgnoreCase("RTN_INSTORE_WAITLIAISON") && q_stage.equalsIgnoreCase("BRANCHES"))
						) {
						cIdList.add(cid);
						actionsMap.put(cid, "RTN_READY_LIAISON");
					}
				}
			}
			SingleQueue sq = sqf.getSingleQueuObj("BRANCHES", "RTN_INSTORE_WAITLIAISON");
			sq.setActionsMap(actionsMap);
			sq.setcIdList(cIdList);
			sq.processData(conn, userid, currentBranch, "BRANCHES", "RTN_INSTORE_WAITLIAISON", new HashMap<Integer, String> () );
			conn.commit();
		}catch (Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {};
			e.printStackTrace();
			msg = "حصل هنالك خطأ الرجاء الأتصال بمحمد نافع "+e.getMessage();
		}finally {
			try {conn.close();}catch(Exception e) {};
		}
		return msg;
	}
}
