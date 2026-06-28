package com.app.bussframework;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;

public class LiaisonAgentBarcodeCheckerRtnManifestLiaison extends CoreMgr{
	int i = 1;
	public LiaisonAgentBarcodeCheckerRtnManifestLiaison() {
		
		MainSql =  "select cc_frombranch, c_custreceiptnoori, c_custid,'' as pmtCheckBox, c_id, cc_id, "
				+ " c_rcv_name, c_rcv_hp1, c_receiptamt, c_qty, "
				+ "concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) as addr"
				+ " from p_cases "
				+ " join p_caseschain on cc_caseid = c_id and  cc_frombranch = q_comingfrombranch"
				+ " and cc_tobranch = {tobranchManifest} and "
				+ " cc_liaisonbar_printmanifest = 'N' and cc_liaisonagentid = {liaisonfromManifest}"
				+ " join kbstate on (c_rcv_state = st_code and st_branch={userstorecode})  "
				+ " left join kbcity_district on (cdi_stcode = st_code and cdi_id = c_rcv_district) "
				+ " left join kbusers on cc_liaisonagentid = us_id "
				+ " where (q_stage= 'BRANCHES' and q_step='MANIFEST_BRANCHES' and q_status='ACTV') or (q_stage = 'DLV' and c_partial_return = 'Y') "
				+ " and (q_comingfrombranch = {userstorecode} )";
		
		userDefinedGridCols.add("c_custid");
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("c_id");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("addr");
		userDefinedGridCols.add("c_rcv_hp1");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("pmtCheckBox");
		
		
		userDefinedColsTypes.put("c_custreceiptnoori", "VARCHAR");
		
		
		userDefinedColLabel.put("c_custid", "اسم الزبون");
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
		mainTable = "p_caseschain";
		keyCol = "cc_id";
		userDefinedEditCols.add("pmtCheckBox");
		
		userDefinedHiddenEditCols.add("cc_id");
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
			pst = conn.prepareStatement("update p_caseschain set cc_liaisonbar_printmanifest = 'Y', cc_liaisonbarby_printmanifest = ? "
					+ ", cc_liaisonbardt_printmanifest = now()  where cc_id=? ");
			
			for(int i=1 ; i<= rowsNo ; i++) {
				if(inputMap_ori.containsKey("prepairmanifest_check_"+i) && inputMap_ori.get("prepairmanifest_check_"+i)!=null && inputMap_ori.get("prepairmanifest_check_"+i)[0].equalsIgnoreCase("on")) {
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