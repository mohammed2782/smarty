package com.app.bussframework;

import java.sql.PreparedStatement;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;

public class liaisonAgentBarcodeCheckerPrintManifest extends CoreMgr{
	int i = 1;
	public liaisonAgentBarcodeCheckerPrintManifest() {
		
		MainSql  = "select c_qty, c_rmk, c_shipment_cost, c_receiptamt, c_fragile, cc_frombranch, "
				+ " c_rcv_name, c_rcv_hp1,c_rcv_state, concat(st_name_ar,' - ',ifnull(cdi_name,''), ' - ', ifnull(c_rcv_addr_rmk,'')) as addr ,"
				+ " c_custreceiptnoori, c_custid,'' as pmtCheckBox, cc_id, c_id, "
				+ " '' as checkbarcod"
				+ " from p_cases "
				+ " join p_caseschain on c_id = cc_caseid and cc_frombranch = {popuprtnfrombranch_chain} and cc_tobranch = {userstorecode} and cc_liaisoncheckedbarcode = 'N'"
				+ " join kbstate on (c_rcv_state = st_code and st_branch={userstorecode})  "
				+ " left join kbcity_district on (cdi_stcode = st_code and cdi_id = c_rcv_district) "
				+ " where ("
				+ "  (q_branch={userstorecode} and q_stage= 'BRANCHES' and q_step='RTN_MANIFEST_LIAISON' and q_status ='ACTV')"
				+ ")";
		
		userDefinedGridCols.add("cc_frombranch");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("pmtCheckBox");

		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");
		
		userDefinedColLabel.put("cc_frombranch", "إلى فرع");
		userDefinedColLabel.put("c_custreceiptnoori","رقم الوصل");
		userDefinedColLabel.put("c_custid","المتجر");
		userDefinedColLabel.put("pmtCheckBox", "");
		
		userDefinedLookups.put("pmtCheckBox", "select 'Y','' from dual");
		userDefinedLookups.put("c_custid", "select cust_id , cust_name from kbcustomers");
		userDefinedLookups.put("cc_frombranch", "select branch_id, branch_name from kbbranches");
		
		canEdit = true;
		mainTable = "p_caseschain";
		keyCol = "cc_id";
		userDefinedEditCols.add("pmtCheckBox");
		
		userDefinedHiddenEditCols.add("c_id");
		userDefinedEditColsHtmlType.put("pmtCheckBox", "CHECKBOX");
				
		displayMode = "GRIDEDIT";
		
		userModifyTD.put("pmtCheckBox", "displayCheckBox({c_id}, {c_custid})");
		userModifyTD.put("c_custreceiptnoori", "modifyRecieptNo({c_custreceiptnoori},{c_id})");
		
		userDefinedCaption = "استلام راجع مندوب الاستلام باركود";
		
		UserDefinedPageRows  =1000;
	}
	
	
	public String displayCheckBox (HashMap<String, String> hashy) {
		String s = "<td><input type=\"checkbox\"  "
				+ " id=\"printmanifestcheck_"+hashy.get("c_id")+"\" "
						+ " name=\"printmanifestcheck_"+i+"\" "
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
			pst = conn.prepareStatement("update p_caseschain set cc_liaisoncheckedbarcode = 'Y', cc_liaisoncheckeddt = now(),"
					+ " cc_liaisoncheckedby = ? where cc_id=? ");
			
			for(int i=1 ; i<= rowsNo ; i++) {
				if(inputMap_ori.containsKey("printmanifestcheck_"+i) && inputMap_ori.get("printmanifestcheck_"+i)!=null && inputMap_ori.get("printmanifestcheck_"+i)[0].equalsIgnoreCase("on")) {
					pst.setInt(1, userid);
					pst.setString(2, inputMap_ori.get("smarty_cc_id_hidden_smartyrow_"+i)[0]);
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