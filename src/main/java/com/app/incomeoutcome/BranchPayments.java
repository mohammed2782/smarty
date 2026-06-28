package com.app.incomeoutcome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;


public class BranchPayments extends CoreMgr{
	public BranchPayments() {

		/*
		 * to define main sql statement, key column and main table that do this
		 * operations
		 */
		MainSql = " select '' as transamt , trans_operationcode ,  trans_id , bp_from_branchid,bp_amount_paid ,bp_paymentdt , "
				+ " bp_createdby , bp_rmk, '' as fake, '' as del, bp_received, bp_safeid "
				+ " from  p_fin_transactions "
				+ " where trans_operationentity='BRANCH' and trans_entity_id={BRANCH_TO_PAY_TO_G} and trans_initiated_in_branch_id = {userstorecode} order by trans_id desc ";

		keyCol = "bp_id";
		mainTable = "p_branch_payments";
		canNew = true;

		// ///////////////
		userDefinedGridCols.add("bp_id");
		userDefinedGridCols.add("bp_transtype");
		userDefinedGridCols.add("bp_amount_paid");
		userDefinedGridCols.add("bp_paymentdt");
		userDefinedGridCols.add("bp_createdby");
		userDefinedGridCols.add("bp_rmk");
		userDefinedGridCols.add("fake");
		userDefinedGridCols.add("del");

		// //////////////
		//userDefinedCaption = "الحركات المالية";
		userDefinedColLabel.put("bp_id", "رقم الأيصال");
		userDefinedColLabel.put("bp_amount_paid", "المبلغ المدفوع");
		userDefinedColLabel.put("bp_from_branchid", "مندوب الإستلام");
		userDefinedColLabel.put("bp_paymentdt", "تاريخ الدفع");
		userDefinedColLabel.put("bp_rmk", " ملاحظات");
		userDefinedColLabel.put("bp_createdby", "أنشئ بواسطة ");
		userDefinedColLabel.put("fake", "طباعة ايصال الدفع ");
		userDefinedColLabel.put("del", "");
		userDefinedColLabel.put("bp_transtype", "نوع المعاملة");
		userDefinedLookups.put("bp_createdby", "select us_id, us_name from kbusers");
		//canDelete = true;
		userModifyTD.put("fake", "printPmtReceipt({bp_id},{bp_from_branchid}, {bp_transtype})");
		userModifyTD.put("del", "showDel({bp_id},{bp_received},{bp_safeid},{bp_transtype})");
	
		userDefinedNewCols.add("bp_transtype");
		userDefinedNewCols.add("transamt");
		userDefinedNewCols.add("bp_rmk");
		userDefinedLookups.put("bp_transtype", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'BRANCH' and kbcat2= 'TRANSTYPE' ");
		userDefinedNewLookups.put("bp_transtype", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'BRANCH' and kbcat2= 'TRANSTYPE' and kbcode in ('DEBT_SETTLE') ");
		
		//userDefinedLookups.put("cp_custid","select c_id ,c_name  From kbcustomers");
		myhtmlmgr.refreshPageOnDelete = true;
		userDefinedTableHeadersClass = "bg-purple bg-lighten-1 white";
	}// end of constructor customer_payment

	
	@Override
	public void initialize(HashMap smartyStateMap) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		int branchReceiver = Integer.parseInt(replaceVarsinString(" {BRANCH_TO_PAY_TO_G} ", arrayGlobals).trim());
		int branchId = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		String branchName = "", receiverBranchName = "";
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select branch_name from kbbranches where branch_id = ?");
			pst.setInt(1, branchId);
			rs = pst.executeQuery();
			if(rs.next()) {
				branchName = rs.getString("branch_name");
			}
			try {rs.close();}catch(Exception e) {}
			pst.clearParameters();
			
			pst.setInt(1, branchReceiver);
			rs = pst.executeQuery();
			while(rs.next()) {
				receiverBranchName = rs.getString("branch_name");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		
		super.initialize(smartyStateMap);
		userDefinedCaption = "الحركات المالية بين فرع: "+branchName+" وفرع: "+receiverBranchName;
	}
	
	
	
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autoCommit) {
		String statusMsg = " تم تسجيل الدفعه ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		int userId = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		int branchId = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		int branchReceiver = Integer.parseInt(replaceVarsinString(" {BRANCH_TO_PAY_TO_G} ", arrayGlobals).trim());
		inputMap_ori = filterRequest(rqs);
		UtilitiesFeqar utf = new UtilitiesFeqar();
		int countOfBox = 0;
		int acctBoxId = 0;
		int pmtId = 0;
		try {
			String rmk = inputMap_ori.get("bp_rmk")[0];
			String transType = inputMap_ori.get("bp_transtype")[0];
			Double transAmt = Double.parseDouble(inputMap_ori.get("transamt")[0]);
			if (!transType.equalsIgnoreCase("DEBT_SETTLE"))
				throw new Exception ("العملية يجب أن تكون سداد دين للفرع");
			
			if(utf.getSafeActiveCondition(conn, branchId)) {
				Double acctCurrentBalunce = 0.0;
				ps = conn.prepareStatement("select acb_currentbalunce, acb_id from p_accountantbox where acb_usid = ? and acb_userbranchid = ?");
				ps.setInt(1, userId);
				ps.setInt(2, branchId);
				rs = ps.executeQuery();
				while(rs.next()) {
					acctCurrentBalunce = rs.getDouble("acb_currentbalunce");
					acctBoxId = rs.getInt("acb_id");
					countOfBox +=1;
					if(countOfBox>1) 
						break;
				}
				try {rs.close();} catch (Exception e) {}
				try {ps.close();} catch (Exception e) {}
				
				if(countOfBox>1) 
					throw new Exception ("حصل خطأ : هناك اكثر من صندوق لنفس المحاسب");
				if(countOfBox==0) {
					ps = conn.prepareStatement("insert into p_accountantbox (acb_usid,acb_currentbalunce, acb_userbranchid) values (?,?,?)",Statement.RETURN_GENERATED_KEYS);
					ps.setInt(1, userId);
					ps.setDouble(2, 0);
					ps.setInt(3, branchId);
					ps.executeUpdate();
					rs = ps.getGeneratedKeys();
					rs.next();
					acctBoxId = rs.getInt(1);
					try {rs.close();}catch(Exception e) {/**/}
					try {ps.close();}catch(Exception e) {/**/}
				}
				if(acctCurrentBalunce<transAmt)
					throw new Exception ( "لايوجد مبلغ مالي كافي في الصندوق");
				//back payment to the accountant box
				ps  = conn.prepareStatement("update p_accountantbox set acb_currentbalunce = acb_currentbalunce-? where acb_id=? and acb_userbranchid = ?");
				ps.setDouble(1, transAmt);
				ps.setInt(2, acctBoxId);
				ps.setInt(3, branchId);
				ps.executeUpdate();	
				try {ps.close();}catch(Exception e) {}	
			}
			ps = conn.prepareStatement( "insert into p_branch_payments "
					+ " 		(bp_from_branchid	 , bp_receiptsamt, bp_amount_paid, bp_createdby ,bp_rmk, "
					+ "  		 bp_received_branchid, bp_debt       , bp_credit 	 , bp_transtype)"
					+ "values	(?		         	 , ?			 , ?		     , ?		    , ?	 , "
					+ "			 ? 					 , ?			 , ?			 , ?)");
			ps.setInt(1, branchId);   
			ps.setLong(2, 0);
			ps.setDouble(3, transAmt);
			ps.setInt(4, userId);
			ps.setString(5, rmk);
			ps.setInt(6, branchReceiver);
			ps.setLong(7, 0);
			ps.setDouble(8, 0);
			ps.setString(9, transType);
			ps.executeUpdate();
			
			
			conn.commit();
		} catch (Exception e) {
			statusMsg = "Error at payment creation, error (" + e.getMessage()+ ")";
			setInsertErrorFlag(true);
			try {conn.rollback();} catch (Exception ignoreE) {}
			e.printStackTrace();
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {ps.close();} catch (Exception e) {}
		}
		return statusMsg;
	}
	
	
	
	public String printPmtReceipt(HashMap<String, String> hashy) {
		String btn = "";
		if (!hashy.get("bp_transtype").equalsIgnoreCase("ASSIGNDEBT")) {
			btn = "<a href=\"../../branchPaymentsReceiptSRVL?bp_id="+hashy.get("bp_id")+"&userbranch="+hashy.get("bp_from_branchid")+"\""
				+ " class='btn btn-xs btn-warning' >طباعة أيصال دفع <i class=\"fa fa-print fa-lg\"></i></a>";
		}
		return "<td>" + btn + "</td>";
	}
	
	public String showDel(HashMap<String, String> hashy) {
		String rank = (String) arrayGlobals.get("userRank");
		String superItRank = (String) arrayGlobals.get("superItRank");
		int safeId = Integer.parseInt(hashy.get("bp_safeid"));
		String showDel = "<td align='center' style='vertical-align: middle;'>";
		if (hashy.get("bp_transtype").equalsIgnoreCase("CASES")|| hashy.get("bp_transtype").equalsIgnoreCase("DEBT_SETTLE")) {
			if((rank.equalsIgnoreCase("FIN_OP_MGR")  || superItRank.equalsIgnoreCase("Y") || rank.equalsIgnoreCase("BRANCHMGR")) 
					&& hashy.get("bp_received").equalsIgnoreCase("N") && safeId == 0) {
				showDel += "<button type='button' onclick=\"link=false; "
						+ "var rs =doDeleteSmarty(this,'هل تريد حذف هذه الدفعة ؟' ,'bp_id','"+hashy.get("bp_id")+"' , 'com.app.incomeoutcome.BranchPayments' ); return rs;\" class='btn btn-danger btn-sm'> "
								+ "<li class='fa fa-trash'></li></button></td>";
				
				return showDel;	
			}
		}
		return "<td></td>";
	}

	@Override
	public String doDelete(HttpServletRequest rqs) {
		String keyVal = rqs.getParameter(keyCol);
		PreparedStatement pst = null;
		int userId = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		int branchId = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		UtilitiesFeqar utf = new UtilitiesFeqar();
		ResultSet rs = null;
		int deletedPaymentBranch = 0;
		try {
			// first backup the cases that had payment and the payment also
			
			pst = conn.prepareStatement("insert into p_branch_payments_dlt "
					+ " (select * , now(), ? from p_branch_payments where bp_id = ?)");
			pst.setInt(1, userId);
			pst.setString(2, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			// second update the cases back to NO, and c_pickupagentpmtid = 0
			pst = conn.prepareStatement("update p_caseschain set cc_branchpmtid=0  where cc_branchpmtid =?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();} catch (Exception e) {}
			
			boolean safeActive = utf.getSafeActiveCondition(conn, branchId);
			if(safeActive) {
				//catch deleted Payment
				Double deletedPayment = 0.0;
				pst = conn.prepareStatement("select bp_amount_paid, bp_received_branchid from p_branch_payments where bp_id=?");
				pst.setString(1, keyVal);
				rs = pst.executeQuery();
				if(rs.next()) {
					deletedPayment = rs.getDouble("bp_amount_paid");
					deletedPaymentBranch = rs.getInt(rs.getInt("bp_received_branchid"));
				}
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
				
				//check accountant box found
				Double acctCurrentBalunce = 0.0;
				int countOfBox = 0;
				int acctBoxId = 0;
				pst = conn.prepareStatement("select acb_currentbalunce, acb_id from p_accountantbox where acb_usid = ? and acb_userbranchid = ?");
				pst.setInt(1, userId);
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
				if(countOfBox==0) 
					return "لايوجد صندوق مالي للمستخدم";

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
						"p_branch_payments_dlt".toUpperCase(), 
						"DB", 
						userId+"", 
						acctBoxId, 
						acctCurrentBalunce, 
						deletedPayment, 
						branchId,
						"حذف دفعة للفروع",
						"branch_id",
						"branch_name",
						deletedPaymentBranch,
						"kbbranches");
			}
			
			// last thing , delete
			pst = conn.prepareStatement("delete from p_branch_payments  where bp_id = ?");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			
			conn.commit();

		} catch (Exception e) {
			try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
			logErrorMsg = "";
			e.printStackTrace();
		} finally {
			try {rs.close();}catch(Exception e) {/**/}
			try {pst.close();} catch (Exception e) {}
		}

		return "";
	}// end of doDelete*/

	

}// end of class customer_payment
