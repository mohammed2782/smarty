package com.app.cust.logistics;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.bussframework.FlowUtils;
import smarty.core.CoreMgr;

public class ReadyToPickUp extends CoreMgr{
	private int row=1;
	private int checkBoxSeq = 1;
	public ReadyToPickUp () {
		
		
		MainSql = "select c_products, c_id, c_custreceiptnoori,c_custid, cust_name, c_productinfo, c_qty, c_rcv_name,  c_receiptamt,'' as q_action,   "
				+ " concat(st_name_ar,' - ', ifnull(cdi_name,''),' ' ,c_rcv_addr_rmk) as address,  '' as printreceipt, '' as c_rcv_state, "
				+ " '' as c_rcv_district, date(c_createddt) as c_createddt, c_rmk, '' as checkbox, {userid} as printedby, ifnull(c_billprinted,'N') as c_billprinted "
				+ " from p_cases "
				+ " join kbstate on (c_rcv_state = st_code and st_branch=c_branchcode)  "
				+ " join kbcustomers on (c_custid = cust_id )"
				+ " left join kbcity_district on (cdi_stcode =st_code and cdi_id = c_rcv_district) "
				+ " where q_stage = 'NEWCUSTLOGI' and q_step = 'READYTOPICKUP' and c_custid in ({shopsCommaSeperated})";
		
		userDefinedGridCols.clear();
		
		userDefinedColLabel.put("c_custreceiptnoori", "رقم الوصل");
		userDefinedColLabel.put("cust_name", "المتجر");
		userDefinedColLabel.put("c_rcv_name", "الزبون");
		userDefinedColLabel.put("c_productinfo", "البضاعة");
		userDefinedColLabel.put("c_qty", "العدد");
		userDefinedColLabel.put("c_receiptamt", "المبلغ مع التوصيل");
		userDefinedColLabel.put("address", "العنوان");
		userDefinedColLabel.put("c_createddt", "تاريخ الإنشاء");
		userDefinedColLabel.put("printreceipt", " ");
		userDefinedColLabel.put("q_action", "العملية    ");
		userDefinedColLabel.put("checkbox", " ");
		userDefinedColLabel.put("c_rcv_hp1", "هاتف الزبون");
		
		userDefinedGridCols.add("c_custreceiptnoori");
		userDefinedGridCols.add("cust_name");
		userDefinedGridCols.add("c_rcv_name");
		userDefinedGridCols.add("c_productinfo");
		userDefinedGridCols.add("c_qty");
		userDefinedGridCols.add("c_receiptamt");
		userDefinedGridCols.add("address");
		userDefinedGridCols.add("c_createddt");
		//userDefinedGridCols.add("printreceipt");
		userDefinedGridCols.add("q_action");
		//userDefinedGridCols.add("checkbox");
		
		userDefinedFilterCols.add("c_rcv_hp1");
		userDefinedFilterCols.add("c_custreceiptnoori");
		userDefinedFilterCols.add("c_rcv_state");
		userDefinedFilterCols.add("c_custid");
		userDefinedFilterCols.add("c_products");
		userDefinedFilterColsHtmlType.put("c_rcv_state", "MULTILIST");
		userDefinedFilterColsHtmlType.put("c_custid", "MULTILIST");
		userDefinedFilterColsHtmlType.put("c_products", "MULTILIST");
		userDefinedNewColsHtmlType.put("c_products", "MULTILIST");
		userDefinedEditCols.add("q_action");
	
		userDefinedLookups.put("c_rcv_state", "select st_code, st_name_ar from kbstate where st_branch={userstorecode}");
		userDefinedLookups.put("c_custid", "select cust_id, cust_name from kbcustomers  where cust_mastercustid={mastercustidlogin} and cust_id in ({shopsCommaSeperated})");
		userDefinedLookups.put("c_products", "select cg_id, cg_goodsdesc from kbcustomer_goods "
				+ "  where cg_mastercustid={mastercustidlogin} ");
		
		userDefinedLookups.put("q_action", "select stpd_code, stpd_desc from kbstep_decision where "
				+ " stpd_stpid in (select stp_id from kbstep where stp_code='READYTOPICKUP' and stp_stgcode='NEWCUSTLOGI') and stpd_onlymbapp='N' "
				+ " and stpd_forrank like '%{userRank}%' ");
		canEdit = true;
		canFilter = true;
		
		displayMode = "GRIDEDIT";
		keyCol = "c_id";
		mainTable = "p_cases";
		userModifyTD.put("printreceipt", "PrintSellBillButton({c_id}, {printedby}, {c_billprinted})");
		userModifyTD.put("checkbox", "showConfirmCaseCheckBox({c_id})");
	}
	
	public String showConfirmCaseCheckBox(HashMap<String,String> hashy) {
		StringBuilder sb = new StringBuilder("<td>");
		sb.append("<input class='form-check-input confirmCheckBoxclass_"+hashy.get("c_id")+"'"
				+ "  type=\"checkbox\" onclick='checkBoxChecked(this, "+checkBoxSeq+")' value=\"\" id='confirmCheckBox_"+hashy.get("c_id")+"' data-check-seq = '"+checkBoxSeq+"'>");
		sb.append("</td>");
		checkBoxSeq ++;
		return sb.toString();
	}
	
	public String PrintSellBillButton(HashMap<String,String> hashy) {
	
		String html ="<td>";
		String btClass = "btn btn-sm btn-success";
		String text = "طباعة الوصل";
		
		if (hashy.get("c_billprinted").equalsIgnoreCase("N")) {
			btClass = "btn btn-sm btn-warning";
			text += " (غير مطبوع) ";
		}
		html += "<a id='printbtn_"+hashy.get("c_id")+"' onclick='changePrintBtnColor("+hashy.get("c_id")+")' href='../../PrintSellBillSRVL?printedby="+hashy.get("printedby")+"&c_id="+hashy.get("c_id")+"' "
				+ " class='"+btClass+"' >"+text+"<i class=\"fa fa-print fa-lg\"></i></a>";
		html +="</td>";
		row ++;
		return html;
	}
	@Override
	public void initialize(HashMap smartyStateMap){
		super.initialize(smartyStateMap);
		int branch = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
		
		
		userDefinedCaption = "جاهز للتسليم لشركة النقل";
		String printGlobalBtn = "<button type='input' onclick='globalSellBillPrintBtn(this, event)' "
				+ " class='btn btn-sm btn-danger' >طباعة كشف الوصولات الجاهزة للبيك آب<i class=\"fa fa-print fa-lg\"></i></button>";
		
		String assignProces = "<input type=\"checkbox\" id='selectAllGlobal' class=\"flat\" onclick='sendAllBackToPrintStage(this)'>"+
				"                        <label for=\"allassign\">" + 
				"                           إرجاع الكل لمرحلة جاهز للطبع" + 
				"                        </label>"; 
			
		userDefinedCaption ="<div class='col-md-4 col-sm-2 col-xs-2'>"+this.userDefinedCaption+"</div>"
				+"<div class='col-md-4 col-sm-4 col-xs-4'>"+assignProces+"</div>"
						+ "<div class='col-md-4 '>"+printGlobalBtn+"</div>";
	}
	@Override
	public String doUpdate (HttpServletRequest rqs , boolean commit) {
		PreparedStatement pst = null ;
		ResultSet rs = null;
		int userId = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
		parseUpdateRqs(rqs);
		FlowUtils fu = new FlowUtils();
		int rowsNo =0;
		if (inputMap_ori.get("smartyhiddenmultieditrowsno")!=null)
			rowsNo = Integer.parseInt(inputMap_ori.get("smartyhiddenmultieditrowsno")[0]);
		
		ArrayList<Integer> cIdList= new ArrayList<Integer>();
		HashMap <Integer , String> actionsMap = new HashMap<Integer, String>();
		String action = "";
		int id = 0;
		for (int i=1 ; i<=rowsNo ; i++){
			action = inputMap_ori.get("q_action_smartyrow_"+i)[0];
			if (action!=null && !action.trim().isEmpty() && !action.trim().equalsIgnoreCase("")&& !action.trim().equalsIgnoreCase("null")) {
				id =Integer.parseInt(inputMap_ori.get(hiddenKeyCol+"_smartyrow_"+i)[0]);
				cIdList.add(id);
				actionsMap.put(id , action);
				
			}
		}
		
		try{
			
			for (int cid :cIdList){
				
				fu.MoveDecisionStepNext(conn, cid, actionsMap.get(cid), userId, "NEWCUSTLOGI", "READYTOPICKUP", "" );
			}
			conn.commit();
		}catch(Exception e){
			e.printStackTrace();
			try{conn.rollback();}catch(Exception eRoll){}
			return "Error";
		}finally{
			try{rs.close();}catch(Exception e){}
			try{pst.close();}catch(Exception e){}
			
			
			
		}
				
		return "Saved";
	}
}
