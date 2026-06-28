package com.app.bussframework;

import java.sql.PreparedStatement;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;

public class DriverAgentBarcodeCheckerFromPrintManifest extends CoreMgr{
	int i = 1;
	public DriverAgentBarcodeCheckerFromPrintManifest(){
		MainSql =  "select cust_name, c_qty, c_rmk, c_shipment_cost, c_receiptamt, c_fragile,"
				+ " c_rcv_name, c_rcv_hp1,c_rcv_state, concat(st_name_ar,' - ',ifnull(cdi_name,''), ' - ', ifnull(c_rcv_addr_rmk,'')) as addr ,"
				+ " c_custreceiptnoori, c_custid,'' as pmtCheckBox, c_id "
				+ " from p_cases "
				+ " left join kbcustomers on cust_id = c_custid "
				+ " left join kbcity_district on cdi_id = c_rcv_district and cdi_stcode = c_rcv_state "
				+ " left join kbstate on st_code = c_rcv_state and st_branch = {userstorecode}"
				+ " where q_stage= 'INIT' and q_step='PRINTMANIFEST' and q_status='ACTV' "
				+ " and (q_comingfrombranch = {userstorecode} )"
				+ " and c_agentcheckedbarcode = 'N' and c_assignedagent = {agintidassignedto} ";
		
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_id");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("pmtCheckBox");
		
		
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");
		
		
		userDefinedColLabel.put("cust_name", "اسم الزبون");
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("c_id", "رقم الشحنة");
		userDefinedColLabel.put("c_rcv_name", "اسم المستلم");
		userDefinedColLabel.put("addr", "عنوان المستلم");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف المستلم");
		userDefinedColLabel.put("c_receiptamt", "مبلغ الوصل");
		userDefinedColLabel.put("c_qty", "العدد");
		userDefinedColLabel.put("c_custid","المتجر");
		userDefinedColLabel.put("pmtCheckBox", "");
		
		userDefinedLookups.put("pmtCheckBox", "select 'Y','' from dual");
		userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers");
		
		canEdit = true;
		mainTable = "p_cases";
		keyCol = "c_id";
		userDefinedEditCols.add("pmtCheckBox");
		
		userDefinedHiddenEditCols.add("c_id");
		userDefinedEditColsHtmlType.put("pmtCheckBox", "CHECKBOX");
				
		displayMode = "GRIDEDIT";
		
		userModifyTD.put("pmtCheckBox", "displayCheckBox({c_id}, {c_custid})");
		userModifyTD.put("c_custreceiptnoori", "modifyRecieptNo({c_custreceiptnoori},{c_id})");
		
		userDefinedCaption = "استلام راجع مندوب الاستلام باركود";
		
		UserDefinedPageRows = 1000;
	}
	
	
	public String displayCheckBox (HashMap<String, String> hashy) {
		String s = "<td><input type=\"checkbox\"  "
				+ " id=\"prepairmanifest_check_"+hashy.get("c_id")+"\" "
						+ " name=\"prepairmanifest_check_"+i+"\" "
						+ " onclick=\"checkBoxPmtClicked(this, "+hashy.get("c_id")+")\">";
		s +="</td>";
		i++;
		return s;	
	}
	
	public String modifyRecieptNo(HashMap<String, String> hashy) {

		String s = "<td caseid='"+hashy.get("c_id")+"' id='"+hashy.get("c_custreceiptnoori")+"'>"+hashy.get("c_custreceiptnoori");
		s +="</td>";
		return s;
	}
	
	@Override
	public String doUpdate(HttpServletRequest rqs , boolean autoCommit){
		String Msg ="";
		PreparedStatement pst = null;
		keyVal = parseUpdateRqs(rqs);
		int rowsNo =0;
		if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
			rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
		int userid = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		
		try {
//			
//			System.out.println("rowsNo----------->"+rowsNo);
//			for(String key:inputMap_ori.keySet())
//				System.out.println("key = "+key+"    value = "+inputMap_ori.get(key)[0]);
//			
			pst = conn.prepareStatement("update p_cases set c_agentcheckedbarcode = 'Y', c_agentbarcodecheckedby = ? "
					+ ", c_agentbarcodecheckeddt = now()  where c_id=? ");
			
			for(int i=1 ; i<= rowsNo ; i++) {
				if(inputMap_ori.containsKey("prepairmanifest_check_"+i) && inputMap_ori.get("prepairmanifest_check_"+i)!=null && inputMap_ori.get("prepairmanifest_check_"+i)[0].equalsIgnoreCase("on")) {
					pst.setInt(1, userid);
					pst.setString(2, inputMap_ori.get("smarty_c_id_hidden_smartyrow_"+i)[0]);
					pst.executeUpdate();
					pst.clearParameters();
				}
			}
			
			conn.commit();	
		}catch(Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {}
			e.printStackTrace();
			setUpdateErrorFlag(true);
		}finally {
			try {pst.close();}catch (Exception e) {}
		}
		return Msg;
	}
}