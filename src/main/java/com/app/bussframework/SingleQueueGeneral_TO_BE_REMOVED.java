package com.app.bussframework;
public class SingleQueueGeneral_TO_BE_REMOVED extends SingleQueue  {

	
	/*
	
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		PreparedStatement pst = null , pstNotATT=null;
		ResultSet rs = null;
		
		String userid = replaceVarsinString("{userid}", arrayGlobals).trim();
		int userstorecode = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		//super.doUpdate(rqs,false);
		keyVal = parseUpdateRqs(rqs);
		FlowUtils fu = new FlowUtils();
		int rowsNo =0;
		if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
			rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
		
		ArrayList<Integer> cIdList= new ArrayList<Integer>();
		HashMap <Integer , String> actionsMap = new HashMap<Integer, String>();
		String action = "";
		int id =0;
		for (int i=1 ; i<=rowsNo ; i++){
			action = inputMap_ori.get("q_action_smartyrow_"+i)[0];
			if (action!=null && !action.trim().isEmpty() && !action.trim().equalsIgnoreCase("")
					&& !action.trim().equalsIgnoreCase("null")) {
				id = Integer.parseInt(inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0]);
				cIdList.add(id);
				actionsMap.put(id, action);
			}
		}
		try{
			pst = conn.prepareStatement("update p_cases set q_action=?, q_action_takenby=? where c_id=?");
			for (int cid :cIdList){
				//update the action and is take by who
				pst.setString(1, actionsMap.get(cid));
				pst.setString(2, userid);
				pst.setInt(3, cid);
				pst.executeUpdate();
				pst.clearParameters();
				fu.MoveDecisionStepNext(conn, cid, userstorecode);
			}
		
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}finally{
			try{pst.close();}catch(Exception e){}
		}
				
		return "Saved";
	}*/
}
