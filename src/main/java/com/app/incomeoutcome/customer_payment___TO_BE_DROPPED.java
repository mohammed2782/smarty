package com.app.incomeoutcome;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.Set;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.core.smartyLogAndErrorHandling;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

public class customer_payment___TO_BE_DROPPED extends CoreMgr {

	public customer_payment___TO_BE_DROPPED() {

		/*
		 * to define main sql statement, key column and main table that do this
		 * operations
		 */
		MainSql = " select cp_createddt, cp_id ,cp_mastercustid, cp_totreceiptsamt,cp_amount_paid_actually, cp_debt , cp_credit,  cp_createdby ,cp_rmk, '' as fake "
				+ ",'' as sync from  p_customer_payments where cp_mastercustid = {masterCustomerAccount} and cp_pmttype='CASES' order by cp_id desc ";

		keyCol = "cp_id";
		mainTable = "p_customer_payments";

		// ///////////////
		userDefinedGridCols.add("cp_id");
		userDefinedGridCols.add("cp_mastercustid");
		userDefinedGridCols.add("cp_totreceiptsamt");
		userDefinedGridCols.add("cp_amount_paid_actually");
		userDefinedGridCols.add("cp_credit");
		userDefinedGridCols.add("cp_createddt");
		userDefinedGridCols.add("cp_createdby");
		userDefinedGridCols.add("cp_rmk");
		userDefinedGridCols.add("sync");
		userDefinedGridCols.add("fake");
	
		// //////////////
		userDefinedCaption = "تسديدات للعملاء";
		userDefinedColLabel.put("cp_id", "رقم الأيصال");
		userDefinedColLabel.put("cp_amount_paid", "المبلغ المدفوع");
		userDefinedColLabel.put("cp_totreceiptsamt", "مبلغ الوصولات");
		userDefinedColLabel.put("cp_amount_paid_actually", "المبلغ المسدد للعميل");
		userDefinedColLabel.put("cp_credit", "أستقطاع دين");
		userDefinedColLabel.put("cp_mastercustid", "العميل");
		userDefinedColLabel.put("cp_createddt", "تاريخ الدفع");
		userDefinedColLabel.put("cp_rmk", " ملاحظات");
		userDefinedColLabel.put("cp_createdby", "أنشئ بواسطة ");
		userDefinedColLabel.put("fake", "طباعة ايصال الدفع ");
		userDefinedColLabel.put("price", "المبلغ المطلوب دفعه");
		userDefinedColLabel.put("sync", " ");
		
		canDelete = false;
		userModifyTD.put("fake", "printPmtReceipt({cp_id})");
		userModifyTD.put("sync", "doSync({cp_id})");
	
		userDefinedLookups.put("cp_mastercustid","select mcust_id ,mcust_name  From kb_mastercustomer where mcust_id={masterCustomerAccount}");
		userDefinedLookups.put("cp_createdby","select us_id, us_name from kbusers");
		myhtmlmgr.refreshPageOnDelete = true;
		
		UserDefinedPageRows = 10;
		userDefinedTableHeadersClass = "text-white  bg-gradient-x-cyan";
	}// end of constructor customer_payment  

	public String doSync(HashMap<String, String> hashy) {
		int userId = (int) arrayGlobals.get("usid");
		String html ="<button type=\"button\" class=\"btn btn-outline-info btn-sm radius-30\" "
				+ "onclick=\"syncCustomerPaymentsWithSource('"+hashy.get("cp_id")+"','"+userId+"');\">مزامنة الوصولات مع نظام العميل</button>";
		
		return "<td>" + html + "</td>";
	}
	
	public String printPmtReceipt(HashMap<String, String> hashy) {
		String btn = "<a href=\"../../PaymentReceiptSRVL?cp_id="+hashy.get("cp_id")+"\" "
				+ " class='btn btn-sm btn-cyan' >طباعة أيصال دفع <i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
	}
	
	@Override
	public void initialize(HashMap smartyStateMap) {
		super.initialize(smartyStateMap);
		
		String rank = (String) arrayGlobals.get("userRank");
		String superItRank = (String) arrayGlobals.get("superItRank");
 		if (rank.equalsIgnoreCase("FIN_OP_MGR")  || superItRank.equalsIgnoreCase("Y")) {
			canDelete = false;
		}
			
	}
	
	@Override
	public String doDelete(HttpServletRequest rqs) {
		String keyVal = rqs.getParameter(keyCol);
		PreparedStatement pst = null;
		ResultSet rs = null;
		String userId = replaceVarsinString(" {userid} ", arrayGlobals).trim();
		int branchId = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		UtilitiesFeqar utf = new UtilitiesFeqar();
		int deletedMasterCustId = 0;
		try {
			// first backup the cases that hadd payment and the payment also
			boolean safeActive = utf.getSafeActiveCondition(conn, branchId);
			
			pst = conn.prepareStatement("insert into  p_deletedpayment_cases (dpc_dpid , dpc_cid, dpc_createdby) "
					+ " select c_pmtid , c_id , ? from p_cases where c_pmtid =?");
			pst.setString(1, userId);
			pst.setString(2, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			pst = conn.prepareStatement("insert into del_customer_payments "
					+ " (dcp_cpid, dcp_mastercustid, dcp_totreceiptsamt, dcp_amount_paid_actually, dcp_debt, dcp_credit, dcp_rmk, dcp_pmttype, "
					+ "  dcp_createdby, dcp_createddt, dcp_deletedby)" + 
					" SELECT cp_id, cp_mastercustid, cp_totreceiptsamt , cp_amount_paid_actually , cp_debt , cp_credit , cp_rmk , cp_pmttype , "
					+ "  cp_createdby, cp_createddt, ?  from p_customer_payments " + 
					" where cp_id = ?");
			pst.setString(1, userId);
			pst.setString(2, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			// second update the cases back to NO, and c_pmtid = 0
			pst = conn.prepareStatement("update p_cases set c_pmtid=0 , "
					+ "  c_settled=(case when c_paidinadvance ='REFUNDED' then 'FULL' else 'NO' end),"
					+ "  c_paidinadvance=(case when c_paidinadvance ='REFUNDED' then 'YES' else c_paidinadvance end)"
					+ "  where c_pmtid =?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			if(safeActive) {
				//catch deleted Payment
				Double deletedPayment = 0.0;
				pst = conn.prepareStatement("select cp_amount_paid_actually, cp_mastercustid from p_customer_payments where cp_id=?");
				pst.setString(1, keyVal);
				rs = pst.executeQuery();
				if(rs.next()) {
					deletedPayment = rs.getDouble("cp_amount_paid_actually");
					deletedMasterCustId = rs.getInt("cp_mastercustid");
				}
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
				
				//check accountant box found
				Double acctCurrentBalunce = 0.0;
				int countOfBox = 0;
				int acctBoxId = 0;
				pst = conn.prepareStatement("select acb_currentbalunce, acb_id from p_accountantbox where acb_usid = ? and acb_userbranchid = ?");
				pst.setString(1, userId);
				pst.setInt(2, branchId);
				rs = pst.executeQuery();
				while(rs.next()) {
					acctCurrentBalunce = rs.getDouble("acb_currentbalunce");
					acctBoxId = rs.getInt("acb_id");
					countOfBox +=1;
					if(countOfBox>1) 
						break;
				}
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
				
				if(countOfBox>1) 
					return "حصل خطأ : هناك اكثر من صندوق لنفس المحاسب الرجاء الاتصال بسوفتيكا";
				if(countOfBox==0) {
					pst = conn.prepareStatement("insert into p_accountantbox (acb_usid,acb_currentbalunce, acb_userbranchid) values (?,?,?)",Statement.RETURN_GENERATED_KEYS);
					pst.setString(1, userId);
					pst.setDouble(2, 0);
					pst.setInt(3, branchId);
					pst.executeUpdate();
					rs = pst.getGeneratedKeys();
					rs.next();
					acctBoxId = rs.getInt(1);
					try {rs.close();}catch(Exception e) {/**/}
					try {pst.close();}catch(Exception e) {/**/}
				}

				if(deletedPayment<0)
					if(acctCurrentBalunce<-deletedPayment)
						return "لايوجد مبلغ مالي في الصندوق كافي لاسترداد المبلغ المحذوف";
				
				
				//back payment to the accountant box
				pst  = conn.prepareStatement("update p_accountantbox set acb_currentbalunce = acb_currentbalunce+? where acb_id=? and acb_userbranchid = ?");
				pst.setDouble(1, deletedPayment);
				pst.setInt(2, acctBoxId);
				pst.setInt(3, branchId);
				pst.executeUpdate();	
				try {pst.close();}catch(Exception e) {}	
	
				utf.acctBoxTransactions(conn, 
						Integer.parseInt(keyVal), 
						"p_customer_payments".toUpperCase(), 
						"CR", 
						userId, 
						acctBoxId, 
						acctCurrentBalunce, 
						deletedPayment, 
						branchId,
						"مسح دفعة عميل",
						"mcust_id",
						"mcust_name",
						deletedMasterCustId,
						"kb_mastercustomer");
			}
			
			
			// last thing , delete
			pst = conn.prepareStatement("delete from p_customer_payments  where cp_id = ?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			conn.commit();

		} catch (Exception e) {
			try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
			logErrorMsg = "";
			e.printStackTrace();
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {pst.close();} catch (Exception e) {}
		}

		return "";
	}// end of doDelete*/

	

}// end of class customer_payment
