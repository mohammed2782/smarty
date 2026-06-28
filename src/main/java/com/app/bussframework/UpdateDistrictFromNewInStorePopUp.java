package com.app.bussframework;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;

import com.app.util.Utilities;

public class UpdateDistrictFromNewInStorePopUp extends CoreMgr{
	public UpdateDistrictFromNewInStorePopUp() {
		MainSql = "select c_id, c_custreceiptnoori,c_rcv_district, c_rcv_addr_rmk from p_cases where c_id={caseidfromnewinstore}";
		mainTable = "p_cases";
		keyCol = "c_id";
		
		canEdit = true;
		
		displayMode = "GRIDEDIT";
		
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_rcv_district");
		userDefinedGridCols.add("c_rcv_addr_rmk");
		
		userDefinedEditCols.add("c_rcv_district");
		userDefinedEditCols.add("c_rcv_addr_rmk");
		
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_rcv_district", "المنطقة");
		userDefinedColLabel.put("c_rcv_addr_rmk", "تفاصيل المنطقة");
		
		  userDefinedLookups.put("c_rcv_district","select cdi_id, cdi_name from kbcity_district where cdi_stcode " +
		  "in (select c_rcv_state from p_cases where c_id={caseidfromnewinstore}) ");
		 
		
		userDefinedEditColsHtmlType.put("c_rcv_district", "DROPLIST");
		userDefinedEditColsHtmlType.put("c_rcv_addr_rmk", "TEXTEREA");
		
		userDefinedCaption = "تعديل المنطقة من داخل المخزن";
		
	}
	
	@Override
	public String doUpdate(HttpServletRequest rqs, boolean autoCommit) {
		PreparedStatement pst = null; 
		ResultSet rs = null;
		Utilities ut = new Utilities();
		parseUpdateRqs(rqs);
		String msg = "تم التعديبل بنجاح";
		
		int userid = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		HashMap <String , String> dataMapFromDB = new HashMap<String, String>();
		try{
			String caseid = inputMap_ori.get("smarty_c_id_hidden_smartyrow_1")[0];
			
			/*
			 * for(String key:inputMap_ori.keySet())
			 * System.out.println("key = "+key+"=============>  value = "+inputMap_ori.get(
			 * key)[0]+"   caseid ="+caseid);
			 */
            
			
			String district = "";
			if (inputMap_ori.containsKey("c_rcv_district_smartyrow_1") && inputMap_ori.get("c_rcv_district_smartyrow_1")[0]!=null
					&&  !inputMap_ori.get("c_rcv_district_smartyrow_1")[0].trim().equalsIgnoreCase("")) {
				district = inputMap_ori.get("c_rcv_district_smartyrow_1")[0];
			}else {
				setUpdateErrorFlag(false);
				return "يجب أختيار المنطقه";
			}
            pst = conn.prepareStatement("select c_rcv_district, c_rcv_addr_rmk from p_cases where c_id =?");
            pst.setString(1, caseid);
            rs = pst.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
            	for(int i=1 ; i<=rsmd.getColumnCount();i++) {
            		dataMapFromDB.put(rsmd.getColumnName(i), rs.getString(rsmd.getColumnName(i)));
            	}
            }
            try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception ex) {}
			
			//log changes
			String dataFromDB;
			String dataFromScreen;
			for(String key: dataMapFromDB.keySet()) {
				dataFromDB = "";
				dataFromScreen = "";
				//System.out.println("key =================================>"+key);
				if (dataMapFromDB.containsKey(key) && inputMap_ori.containsKey(key+"_smartyrow_1")) {
					if (dataMapFromDB.get(key)!=null)
						dataFromDB = dataMapFromDB.get(key);
					if(inputMap_ori.get(key+"_smartyrow_1")[0]!=null)
						dataFromScreen = inputMap_ori.get(key+"_smartyrow_1")[0].trim();
					//System.out.println("dataFromDB = "+dataFromDB+"  dataFromScreen = "+dataFromScreen);
	        		if(!dataFromScreen.equalsIgnoreCase(dataFromDB))
	        			CoreUtilities.logChanges(conn, "P_CASES", "c_id", Integer.parseInt(caseid), key, dataFromDB, dataFromScreen,
									"update", "تعديل المنطقة من داخل المخزن", userid);
				}
			}	
            pst = conn.prepareStatement("update p_cases set c_rcv_district=?, c_rcv_addr_rmk=? where c_id = ?");
            pst.setString(1, district);
            pst.setString(2, inputMap_ori.get("c_rcv_addr_rmk_smartyrow_1")[0]);
            pst.setString(3, caseid);
            pst.executeUpdate();

            conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			msg = "Error at updated data, error("+ e.getMessage() +") ";
			 try{conn.rollback();}catch(Exception ex){}//end of inner catch
			 setUpdateErrorFlag(true);
		}finally{
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception ex) {}		
		}//end of finally
		
		return msg;
	}
}
