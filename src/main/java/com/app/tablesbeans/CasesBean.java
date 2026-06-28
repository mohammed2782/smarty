package com.app.tablesbeans;

import java.util.HashMap;

public  class CasesBean {
	 //static block initialization for exception handling
    static{
        try{
            instance = new CasesBean();
        }catch(Exception e){
        	e.printStackTrace();
            throw new RuntimeException("Exception occured in creating CasesBean instance");
        }
    }
	public static HashMap<String,String> columnsCodeNameToDbNameMap;
	private static  CasesBean instance;
	private CasesBean() {
		columnsCodeNameToDbNameMap = new HashMap<String,String>();
		columnsCodeNameToDbNameMap.put("case_id", "c_id");
		columnsCodeNameToDbNameMap.put("shipment_cost", "c_shipment_cost");
		columnsCodeNameToDbNameMap.put("agent_share", "c_agentshare");
		columnsCodeNameToDbNameMap.put("case_rmk", "c_rmk");
	}
	public static CasesBean getInstance() {
		return instance;
	}
}
