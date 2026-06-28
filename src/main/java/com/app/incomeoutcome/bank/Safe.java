package com.app.incomeoutcome.bank;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;
import smarty.db.mysql;
import com.app.financials.AccountantBoxBean;
import com.app.financials.PaymentImpactOnSafe;
import com.app.financials.PaymentType;
import com.app.financials.SafePaymentTypeMetaInfoBean;
import com.app.financials.StandardFinCurrency;
import com.app.financials.UtilitiesFinancials;
import com.app.financials.UtilitiesSafeFinancials;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.util.Utilities;

public class Safe extends CoreMgr{
	private double balanceIqd;
	private double balanceUsd;
	NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
	private HashMap<StandardFinCurrency, Long> entityBalance;
	public Safe () {
		MainSql = "select saf_id, "
				+ " saf_iqd_before_transaction, saf_iqd_after_transaction, saf_usd_before_transaction, saf_usd_after_transaction, "
				+ " saf_trantype, saf_tranname, saf_tranentity,"
				+ " DATE_FORMAT(date(saf_trandate),'%Y-%m-%d') as saf_trandate,"
				+ " saf_createdby, saf_rmk, saf_amount_iqd, saf_amount_usd, "
				+ " DATE_FORMAT(saf_createddt,'%Y-%m-%d %H:%i') as saf_createddt, '' as del, '' as details "
				+ " from p_safe "
				+ " where saf_branchid = {userstorecode} order by 1 desc";
		mainTable = "p_safe";
		keyCol = "saf_id";
		
		//canDelete = true;
		
		// New From///////////////////////////
		userDefinedNewFormColNo = 3;
		canNew = true;
		userDefinedNewCols.add("saf_trantype");
		userDefinedNewCols.add("saf_tranname");
		userDefinedNewCols.add("saf_tranentity");
		//userDefinedNewCols.add("saf_trandate");
		
		userDefinedNewCols.add("saf_amount_iqd");
		userDefinedNewCols.add("saf_amount_usd");
		
		userDefinedNewCols.add("saf_rmk");
		userDefinedNewCols.add("saf_createdby");
		userDefinedNewCols.add("saf_iqd_before_transaction");
		userDefinedNewCols.add("saf_usd_before_transaction");
		userDefinedNewCols.add("saf_branchid");
		
		//Grid cols
		//////////////////////////////
		userDefinedGridCols.add("saf_id");
		userDefinedGridCols.add("saf_iqd_before_transaction");
		userDefinedGridCols.add("saf_amount_iqd");
		userDefinedGridCols.add("saf_iqd_after_transaction");
		
		userDefinedGridCols.add("saf_trantype");
		userDefinedGridCols.add("saf_tranname");
		
		userDefinedGridCols.add("saf_usd_before_transaction");
		userDefinedGridCols.add("saf_amount_usd");
		userDefinedGridCols.add("saf_usd_after_transaction");
		
		userDefinedGridCols.add("saf_tranentity");
		userDefinedGridCols.add("saf_trandate");
		userDefinedGridCols.add("saf_createdby");
		userDefinedGridCols.add("saf_rmk");
		//userDefinedGridCols.add("details");
		userDefinedGridCols.add("del");
		
		userDefinedColLabel = UtilitiesFinancials.getSafeTableColumnsName();
		
		
		
		// user Defined Lookups
		userDefinedLookups.put("saf_trantype", "select kbcode,kbdesc from kbgeneral where kbcat1='SAFE_TRANSACTION' and kbcat2 ='TYPE' ");
		userDefinedNewLookups.put("saf_tranname", "!select kbcode,kbdesc from kbgeneral where kbcat1='{saf_trantype}' and kbcat2 ='PMTTYPE' "
				+ " and kbcode not in('TRANSFERFROMFINBOX','RESTARTNEW')  order by kbcat_seq");
		
		userDefinedLookups.put("saf_tranname", "!select kbcode,kbdesc from kbgeneral where kbcat1='{saf_trantype}' and kbcat2 ='PMTTYPE'");
		userDefinedNewLookups.put("saf_tranname", "!select kbcode,kbdesc "
				+ "from kbgeneral where kbcat1='{saf_trantype}' and kbcat2 ='PMTTYPE' and kb_show_on_new_form ='Y'");
		userDefinedLookups.put("saf_tranentity",
				"!select us_id as id,us_name as name from kbusers "
				+ "where '{saf_tranname}' <> 'EXPANDITURE' and (us_branchcode = {userstorecode} "
				+ " or us_id in (select ubr_userid from kbusers_branches_r where ubr_branchid = {userstorecode}))"
				+ " union"
				+ " select co_id as id, co_name as name from kbcost_type where '{saf_tranname}' = 'EXPANDITURE' ");
		userDefinedLookups.put("saf_createdby", "select us_id,us_name from kbusers ");
		userDefinedLookups.put("saf_branchid", "select branch_id, branch_name from kbbranches");
		
		userDefinedNewColsHtmlType.put("saf_rmk", "TEXTAREA");		
		userDefinedNewColsHtmlType.put("saf_tranname", "DROPLIST");
		userDefinedNewColsHtmlType.put("saf_tranentity", "DROPLIST");
		userDefinedColsMustFill.add("saf_tranname");
		userDefinedColsMustFill.add("saf_tranentity");
		userDefinedColsMustFill.add("saf_trantype");
		userDefinedColsMustFill.add("saf_amount_iqd");
		//userDefinedColsMustFill.add("saf_amount_usd");
		userDefinedColsMustFill.add("saf_trandate");
	
		userDefinedNewColsDefualtValues.put("saf_amount_usd", new  String [] {"0"});
		
		
		userDefinedReadOnlyNewCols.add("saf_createdby");
		userModifyTD.put("del", "showDelButton({saf_id},{saf_tranname})");
		
		myhtmlmgr.refreshPageOnDelete = true;
		
		userDefinedNewColsDefualtValues.put("saf_createdby", new  String [] {"{userid}"});
		userDefinedNewColsDefualtValues.put("saf_branchid", new  String [] {"{userstorecode}"});
		userDefinedReadOnlyNewCols.add("saf_branchid");
		userDefinedNewColsHtmlType.put("saf_trandate", "DATE");
		userDefinedNewColsHtmlType.put("saf_createdby", "DROPLIST");
		userDefinedNewColsHtmlType.put("saf_branchid", "DROPLIST");
		userDefinedNewColsHtmlType.put("saf_amount_iqd", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("saf_amount_usd", "NUMBER_WITH_COMMAS");
		userDefinedNewColsHtmlType.put("saf_before_transaction", "NUMBER_WITH_COMMAS");
		
		userDefinedNewColHtmlAttr.put("saf_amount_iqd", "autocomplete=\"off\"");
		userDefinedNewColHtmlAttr.put("saf_amount_usd", "autocomplete=\"off\"");
		
		userModifyTD.put("details", "detailsPopUp({saf_tranname},{saf_id})");
		
		myhtmlmgr.refreshPageOnDelete = true;
		
		userDefinedNewCaption = "اضافة الى القاصة";
		
		UserDefinedPageRows = 1000;
	}
	public String detailsPopUp(HashMap<String, String> hashy){
		String text = hashy.get("saf_tranname");
		if(text.equalsIgnoreCase("RESTARTNEW")) {
			return "<td><a href='#' class='btn btn-xs btn-dark' "
			+ " onclick=\"popitup('./safeNewStartDtlsPopUp?safeid="+hashy.get("saf_id")+"', 'Transactions' , 800,700)\">التفاصيل</a></td>";
		}
		return "<td></td>";
	}
	
	public String showDelButton (HashMap<String,String> hashy) {
		boolean showDelete = false;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		int safeId = 0;
		int branchId = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select saf_id from p_safe where saf_branchid = ? order by 1 desc limit 1");
			pst.setInt(1, branchId);
			rs = pst.executeQuery();
			if(rs.next()) {
				if(Integer.parseInt(hashy.get("saf_id"))==rs.getInt("saf_id"))
					if(!hashy.get("saf_tranname").equalsIgnoreCase("RESTARTNEW") ) {
						showDelete = true;
						safeId = rs.getInt("saf_id");
					}
			}
		}catch(Exception e) {
			e.printStackTrace();
			try {conn.rollback();}catch(Exception eRoll) {/**/}
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
			
		if (showDelete) {
			return "<td align='center' style='vertical-align: middle;'>"
			+"<button id='pmt_del_btn_"+hashy.get("saf_id")+"' type='button' "
			+ " onclick=\"link=false; "
			+ " var rs =doDeleteSmarty(this,'هل تريد حذف هذه العملية ؟' ,'saf_id','"+hashy.get("saf_id")+"' , 'com.app.incomeoutcome.bank.Safe' ); return rs;\" class='btn btn-danger btn-xs'>"
			+ "<li class='fa fa-trash'></li></button></td>";
		}
		return "<td></td>";
	}
	@Override
	public void initialize(HashMap smartyStateMap) {
		
		int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		try {
			entityBalance = Utilities.getSafeBalance(conn, branchId_G);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int userId_G = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		String InventorySafe =
				"<div class='row'>"
		+ "<div class='col-5'>"
		+ "<button type=\"button\" "
		+ " class=\"btn btn-warning btn-min-width box-shadow-4 mr-1 mb-1 waves-effect waves-light\" "
		+ "onclick=\"InventorySafe('"+userId_G+"', '"+branchId_G+"');\">جرد القاصة</button>"
		+ "</div>"
		+ "<div class='col-3'>"
		+ "<span class=\"badge badge-warning\" style='font-size: 1.4rem;'>دينار عراقي "+numFormat.format(entityBalance.get(StandardFinCurrency.IQD))+"</span>"
		+ "</div>"
		+ "<div class='col-3'>"
		+ "<span class=\"badge badge-success\" style='font-size: 1.4rem;'>دولار أمريكي "+numFormat.format(entityBalance.get(StandardFinCurrency.USD))+"</span>"
		+ "</div>"
		+ "</div>";
		userDefinedCaption = InventorySafe;
		userDefinedNewLookups.put("saf_tranentity", "!select code,val from ("
				+ " select co_id as code ,co_name as val from kbcost_type where  '{saf_tranname}' = 'EXPANDITURE' and '{saf_trantype}' = 'DB' "
				+ " union"
				+ " select us_id as code, us_name as val from kbusers "
				+ " where '{saf_tranname}' !='EXPANDITURE' and us_active = 'Y' and us_rank not in ('LIAISONAGENT','MASTERCUSTOMER','DLVAGENT','PICKUPAGENT') "
				+ " and (us_branchcode = "+branchId_G+" or us_id in (select ubr_userid from kbusers_branches_r where ubr_branchid = "+branchId_G+")) ) abc ");
		
		userDefinedReadOnlyNewCols.add("saf_iqd_before_transaction");
		userDefinedNewColsDefualtValues.put("saf_iqd_before_transaction", 
				new  String [] {numFormat.format(entityBalance.get(StandardFinCurrency.IQD))});
		
		userDefinedReadOnlyNewCols.add("saf_usd_before_transaction");
		userDefinedNewColsDefualtValues.put("saf_usd_before_transaction",
				new  String [] {numFormat.format(entityBalance.get(StandardFinCurrency.USD))});
		
		super.initialize(smartyStateMap);
	}
	
	@Override
	public String doDelete(HttpServletRequest rqs){
		
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String msg = "";
		try {
			
			int safeIdToBeDeleted = Integer.parseInt(rqs.getParameter("saf_id"));
			conn = mysql.getConn();
			int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
			pst = conn.prepareStatement("select max(saf_id) as saf_id from p_safe where saf_branchid = ? ");
			pst.setInt(1, branchId_G);
			rs = pst.executeQuery();
			int latestSafeid = 0;
			if(rs.next()) {
				latestSafeid = rs.getInt("saf_id");	
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			if (latestSafeid != safeIdToBeDeleted ) {
				throw new Exception("العملية المطلوب مسحها هي ليست أخر عملية في القاصة ولذلك تعذر حذفها");
			}
			pst = conn.prepareStatement("select saf_acctboxid, saf_acctbox_transaction_id from p_safe where saf_id= ? and saf_branchid = ?");
			pst.setInt(1, safeIdToBeDeleted);
			pst.setInt(2, branchId_G);
			rs = pst.executeQuery();
			int accountBoxTransactionIdToBeDeleted= 0;
			int accountBoxId = 0;
			if (rs.next()) {
				accountBoxTransactionIdToBeDeleted=  rs.getInt("saf_acctbox_transaction_id");
				accountBoxId = rs.getInt("saf_acctboxid");
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			int userId_G = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
			if (accountBoxTransactionIdToBeDeleted>0) {
				UtilitiesSafeFinancials.unMarkAccountBoxTransactionMovedToSafe(conn, safeIdToBeDeleted,
						accountBoxId, branchId_G);
				UtilitiesSafeFinancials.revertAcctBoxTransactionForSafe(conn, accountBoxId, accountBoxTransactionIdToBeDeleted, userId_G, branchId_G, "مسح عملية مالية من القاصة");
			}
			pst = conn.prepareStatement("insert into p_safe_deleted select p_safe.* , ? , now() from p_safe where saf_id=?");
			pst.setInt(1, userId_G);
			pst.setInt(2, safeIdToBeDeleted);
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("delete from p_safe where saf_id=?");
			pst.setInt(1, safeIdToBeDeleted);
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			
			conn.commit();
			msg = "تم المسح";
		}catch(Exception e) {
			e.printStackTrace();
			try {conn.rollback();}catch(Exception eRoll) {/**/}
			msg = "Error. "+e.getMessage();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		return msg; 
	}
	
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autocommit) {
		
		Connection conn = null;
		PreparedStatement pst = null;
		String msg = "تم الحفظ";
		ResultSet rs = null;
		try {
			parseUpdateRqs(rqs);
			conn = mysql.getConn(); 
			
			long tranAmtIqd = Long.parseLong( inputMap_ori.get("saf_amount_iqd")[0].replaceAll(",", ""));
			long tranAmtUsd = Long.parseLong( inputMap_ori.get("saf_amount_usd")[0].replaceAll(",", ""));
			
			// protect the safe
			int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
			entityBalance  = Utilities.getSafeBalance(conn, branchId_G);
			SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = 
					UtilitiesSafeFinancials.getSafePaymentTypeMetaInfoKbgeneral(conn,
							inputMap_ori.get("saf_trantype")[0], "PMTTYPE" , inputMap_ori.get("saf_tranname")[0]);
			
			if (safePaymentTypeMetaInfoBean.getDbOrCr() == PaymentType.DB) {
				if (tranAmtIqd > entityBalance.get(StandardFinCurrency.IQD)) {
					return "المبلغ المسحوب د.ع أكبر من ما موجود في القاصة";
				}
				if (tranAmtUsd > entityBalance.get(StandardFinCurrency.USD)) {
					return "المبلغ المسحوب دولار أمريكي أكبر من ما موجود في القاصة";
				}
			}
			if(tranAmtIqd<0 || tranAmtUsd<0) {
				return "لايمكن ان يكون المبلغ بالسالب";
			}
			if(tranAmtIqd == 0 && tranAmtUsd == 0) {
				return "مبلغ المعاملة يجب أن يكون أكبر من الصفر";
			}
			

			int transactionWithEntity = Integer.parseInt(inputMap_ori.get("saf_tranentity")[0]);			
			int userId_G = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
			pst = conn.prepareStatement("insert into p_safe "
			+ "(saf_iqd_before_transaction, saf_usd_before_transaction, saf_amount_iqd, saf_amount_usd, saf_iqd_after_transaction, saf_usd_after_transaction , "
			+ " saf_trantype 			  , saf_tranname    		  , saf_tranentity, saf_createdby , saf_branchid , "
			+ " saf_rmk					  , saf_createddt ) "
			+ " values ("+CoreUtilities.getQuestionMarks(12)+" , now() )",Statement.RETURN_GENERATED_KEYS);
			
			long balanceIqdBefore = Long.parseLong(inputMap_ori.get("saf_iqd_before_transaction")[0].replaceAll(",", "")) ;
			long balanceUsdBefore = Long.parseLong(inputMap_ori.get("saf_usd_before_transaction")[0].replaceAll(",", "")) ;
			
			entityBalance = Utilities.getSafeBalance(conn, branchId_G);
			
			if (balanceIqdBefore != entityBalance.get(StandardFinCurrency.IQD)) {
				throw new Exception ("رصيد القاصة دينار عراقي تغير قبل أتمام العملية");
			}
			if (balanceUsdBefore != entityBalance.get(StandardFinCurrency.USD)) {
				throw new Exception ("رصيد القاصة دولار أمريكي تغير قبل أتمام العملية");
			}
			
			long balanceIqdAfter = 0;
			long balanceUsdAfter = 0;
			if (safePaymentTypeMetaInfoBean.getDbOrCr() == PaymentType.DB) {
				balanceIqdAfter = balanceIqdBefore - tranAmtIqd;
				balanceUsdAfter = balanceUsdBefore - tranAmtUsd;
			}else {
				balanceIqdAfter = balanceIqdBefore + tranAmtIqd;
				balanceUsdAfter = balanceUsdBefore + tranAmtUsd;
			}
			if (balanceIqdAfter <0 || balanceUsdAfter<0) {
				return "الرصيد في القاصة غير كافي لاتمام العملية";
			}
			pst.setLong(1, balanceIqdBefore);
			pst.setLong(2, balanceUsdBefore);
			pst.setLong(3, tranAmtIqd);
			pst.setLong(4, tranAmtUsd);
			pst.setLong(5, balanceIqdAfter);
			pst.setLong(6, balanceUsdAfter);
			pst.setString(7, inputMap_ori.get("saf_trantype")[0]);
			pst.setString(8, inputMap_ori.get("saf_tranname")[0]);
			pst.setInt(9, transactionWithEntity);
			pst.setInt(10, userId_G);
			pst.setInt(11, branchId_G);
			pst.setString(12, inputMap_ori.get("saf_rmk")[0]);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			int safeId = 0;
			rs.next();
			safeId = rs.getInt(1);
			
			try {pst.close();}catch(Exception e) {/**/}
			int accountBoxTransactionId = 0;
			if(inputMap_ori.get("saf_tranname")[0].equalsIgnoreCase("TRANSFERFROMOUTBOX")
					||inputMap_ori.get("saf_tranname")[0].equalsIgnoreCase("DEPOSIT_OUTBOX")) {
				PaymentImpactOnSafe impactOnFinBox = PaymentImpactOnSafe.ADD_SAFE;
				// if the impact on safe is CR then the impact on box is DB
				if (safePaymentTypeMetaInfoBean.getSafeImpact()== PaymentImpactOnSafe.ADD_SAFE) {
					impactOnFinBox = PaymentImpactOnSafe.DEDUCT_SAFE;
				}
				AccountantBoxBean accountantBoxBean= UtilitiesSafeFinancials.GetAccountantBox(conn, transactionWithEntity , branchId_G);
				accountBoxTransactionId = UtilitiesSafeFinancials.createAcctBoxTransactions(
						conn, 
						safeId, 
						"p_safe".toUpperCase(), 
						impactOnFinBox, 
						transactionWithEntity, 
						tranAmtIqd,
						tranAmtUsd,
						branchId_G,
						safePaymentTypeMetaInfoBean.getName() + " - " + "حسابات القاصة", 
						"kb_userse",
						"us_id",
						transactionWithEntity,
						"us_name",
						"",
						userId_G);
				pst = conn.prepareStatement("update p_safe set saf_acctboxid=?, saf_acctbox_transaction_id=? where saf_id=? and saf_branchid = ?");
				pst.setInt(1, accountantBoxBean.getBoxId());
				pst.setInt(2, accountBoxTransactionId);
				pst.setInt(3, safeId);
				pst.setInt(4, branchId_G);
				pst.executeUpdate();
				try {pst.close();}catch(Exception e) {/**/}
				
				//now mark all the box account transactions that it had moved to the safe
				UtilitiesSafeFinancials.markAccountBoxTransactionMovedToSafe(conn, safeId, accountantBoxBean.getBoxId(), accountBoxTransactionId, branchId_G);
				
			}
			
			conn.commit();
		}catch(Exception e) {
			try {conn.close();}catch(Exception eRoll) {/**/}
			e.printStackTrace();
			msg = "خطأ في النظام "+e.getMessage();
		}finally {
			try {rs.close();}catch(Exception e) {/**/}
			try {pst.close();}catch(Exception e) {/**/}
			try {conn.close();}catch(Exception e) {/**/}
		}
		return msg;
	}
	/**
	 * @return the balanceIqd
	 */
	public double getBalanceIqd() {
		return balanceIqd;
	}
	/**
	 * @param balanceIqd the balanceIqd to set
	 */
	public void setBalanceIqd(double balanceIqd) {
		this.balanceIqd = balanceIqd;
	}
	/**
	 * @return the balanceUsd
	 */
	public double getBalanceUsd() {
		return balanceUsd;
	}
	/**
	 * @param balanceUsd the balanceUsd to set
	 */
	public void setBalanceUsd(double balanceUsd) {
		this.balanceUsd = balanceUsd;
	}
	public HashMap<StandardFinCurrency, Long> getEntityBalance() {
		return entityBalance;
	}
	public void setEntityBalance(HashMap<StandardFinCurrency, Long> entityBalance) {
		this.entityBalance = entityBalance;
	}
}
