package com.app.incomeoutcome.bank;

import java.util.HashMap;

import smarty.core.CoreMgr;

public class PayDebtToSafe extends CoreMgr{
	public PayDebtToSafe() {
		MainSql = "select saf_tranentity, sum(totdebt) as totdebt, sum(totpay) as totpay, '' as details, "
				+ "(sum(totdebt)-sum(totpay)) restamount, '' as pay, '' as dummy  from ( "
				+ "select sum(case when saf_trantype = 'DB' then saf_amount_iqd else 0 end) as totdebt, saf_tranentity,"
				+ " sum(case when saf_trantype = 'CR' then saf_amount_iqd else 0 end) as totpay "
				+ " from p_safe "
				+ " where saf_branchid={userstorecode} and saf_tranname = 'CASH' group by saf_tranentity"
				+ " union "
				+ " select sum(case when saf_trantype = 'DB' then saf_amount_iqd else 0 end) as totdebt, saf_tranentity,"
				+ " sum(case when saf_trantype = 'CR' then saf_amount_iqd else 0 end) as totpay"
				+ " from p_safe_hist "
				+ " where saf_branchid={userstorecode} and saf_tranname = 'CASH' group by saf_tranentity"
				+ " ) abc group by saf_tranentity";
		
		userDefinedGroupByCol = "dummy";
		groupSumCaption = "المجموع";
		userDefinedSumCols.add("totdebt");
		userDefinedSumCols.add("totpay");
		userDefinedSumCols.add("restamount");
		
		//Grid cols
		//////////////////////////////
		userDefinedGridCols.add("saf_tranentity");
		userDefinedGridCols.add("totdebt");
		userDefinedGridCols.add("totpay");
		userDefinedGridCols.add("restamount");
		userDefinedGridCols.add("details");
		userDefinedGridCols.add("pay");
		
		//Label
		///////////////////////////
		userDefinedColLabel.put("totdebt","مبالغ الدين" );
		userDefinedColLabel.put("totpay","المبالغ المسددة" );
		userDefinedColLabel.put("restamount","المتبقي" );
		userDefinedColLabel.put("saf_tranentity","طرف المعاملة" );
		userDefinedColLabel.put("pay","تسديد" );
		userDefinedColLabel.put("details"," " );
		
		userModifyTD.put("pay", "paydebt({saf_tranentity}, {restamount})");
		userModifyTD.put("details", "showDetails({saf_tranentity})");
		
		userDefinedLookups.put("saf_tranentity", "select us_id,us_name from kbusers ");
		
		userDefinedCaption = "تسديد الديون النقدية";
	}
	public String showDetails(HashMap<String, String> hashy) {
		String html = "<td style='text-align: center;'>";
		html +="<button type=\"button\" class=\"btn btn-xs btn-info\" onclick=\"popitup('payAndDeptDtlsPopUp?tranentitysafe="+hashy.get("saf_tranentity")+"' , '' , 1000 ,600);\">التفاصيل</button>";
		html +="</div></td>";
		return html;
	}
	public String paydebt(HashMap<String, String> hashy) {
		String html = "<td>";
		double restAmount = Double.parseDouble(hashy.get("restamount"));
		if(restAmount>0) {
			html += ("<a href='javascript:payDebt("+restAmount+", "+hashy.get("saf_tranentity")+")'>"
					+ "		<div class=\"widgets-icons\" style='margin-left:auto;margin-right:auto'>"
					+ "			<i class=\"bx bxs-wallet\"></i>"
					+ "		</div>"
					+ "</a>");
		}else if(restAmount == 0) {
			html += "سَدِدِ المبلغ"; 
		}else {
			html += "حصل خطأ";
		}
		
		html += "</td>";
		return html;
		
	}

}
