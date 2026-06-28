package com.app.incomeoutcome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.financials.AccountantBoxBean;
import com.app.financials.FinOperationCategory;
import com.app.financials.FinOperationCode;
import com.app.financials.FinOperationEntity;
import com.app.financials.PaymentImpactOnSafe;
import com.app.financials.SafePaymentTypeMetaInfoBean;
import com.app.financials.StandardTransactionBean;
import com.app.financials.UtilitiesFinancials;
import com.app.financials.UtilitiesSafeFinancials;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.util.Utilities;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;
import smarty.db.mysql;

public class LiaisonAgentSharePaymentsForOutBoundCases extends CoreMgr  {
	protected FinOperationEntity entityType;
	protected FinOperationCode m_operationCode;
	protected int m_branchWeAreHandling;
	
	public FinOperationEntity getEntityType() {
		return entityType;
	}

	public void setEntityType(FinOperationEntity entityType) {
		this.entityType = entityType;
	}
	public LiaisonAgentSharePaymentsForOutBoundCases() {
		
		MainSql = "select trans_id, lpas_for_which_trans_id, trans_amount_paid_actually_iqd,"
				+ " lpas_tot_cases, lpas_tot_cases_center,  lpas_tot_cases_rural, "
				+ " lpas_type_of_share, lpas_share_center , lpas_share_rural, "
				+ " lpas_share_calculated_profit_center, "
				+ " (lpas_total_calculated_profit_center - lpas_share_calculated_profit_center) as other_share_center, "
				+ " lpas_share_calculated_profit_rural, "
				+ " (lpas_total_calculated_profit_rural - lpas_share_calculated_profit_rural) as other_share_rural, "
				+ " trans_rmk,trans_createdby, trans_createddt, '' as fake, '' as  showdel "
				+ " from p_fin_transactions"
				+ " join p_liaison_pickup_agent_share on lpas_trans_id = trans_id  "
				+ " where 1=0 "
						+ " and trans_operationcode = "+m_operationCode+" "
				+ " and trans_initiated_in_branch_id = {userstorecode} "
				+ " and trans_entity_id = "+m_branchWeAreHandling+" "
				+ " and trans_deleted='N' order by trans_id desc ";
		
		keyCol = "trans_id";
		mainTable = "trans_operationentity";
		canNew = true;
		
		// Grid columns
		userDefinedGridCols.add("trans_id");
		userDefinedGridCols.add("lpas_for_which_trans_id");
		userDefinedGridCols.add("lpas_type_of_share");
		userDefinedGridCols.add("trans_amount_paid_actually_iqd");
		userDefinedGridCols.add("lpas_tot_cases");
		userDefinedGridCols.add("lpas_share_center");
		userDefinedGridCols.add("lpas_share_calculated_profit_center");
		userDefinedGridCols.add("other_share_center");
		userDefinedGridCols.add("trans_rmk");
		userDefinedGridCols.add("trans_createdby");
		userDefinedGridCols.add("trans_createddt");
		
		userDefinedGridCols.add("showdel");
		
		userDefinedColLabel = UtilitiesFinancials.getFinancialTableColumnsName();
		userDefinedColLabel.put("trans_amount_paid_actually_iqd", "حصة الشريك الكلية");
		userDefinedColLabel.put("lpas_for_which_trans_id", "عن دفعة فرع مستلمة");
		userDefinedColLabel.put("lpas_type_of_share", "نوع مبلغ الشراكة");
		userDefinedColLabel.put("lpas_share_center", "نسبة / مبلغ");
		
		userDefinedColLabel.put("lpas_tot_cases", "عدد الشحنات الكلي");
		userDefinedColLabel.put("lpas_tot_cases_center", "عدد الشحنات ");
		userDefinedColLabel.put("lpas_share_calculated_profit_center", "حصة الشريك");
		userDefinedColLabel.put("other_share_center", "باقي المبلغ");
		
		userDefinedLookups.put("trans_createdby", "select us_id, us_name from kbusers where us_branchcode={userstorecode}");
		userDefinedLookups.put("trans_operationcode", "select kbcode, kbdesc from kbgeneral "
				+ " where kbcat1 = 'BRANCH' and kbcat2= 'PMTTYPE' ");
		userDefinedLookups.put("lpas_type_of_share", "select kbcode, kbdesc from kbgeneral "
				+ " where kbcat1='PICKUP_LIAISON_SHARE' and kbcat2='PMTTYPE' and kbcat3='SHARE_TYPE' " );
		userDefinedNewColsHtmlType.put("trans_createddt", "DATE");
		
		userModifyTD.put("trans_id", "printPmtReceipt({trans_id})");
		userModifyTD.put("showdel", "showDel({trans_id}, {trans_did_branch_receive})");
		
		userDefinedNewCols.add("lpas_for_which_trans_id");
		userDefinedNewCols.add("lpas_type_of_share");
		userDefinedNewCols.add("lpas_share_center");
		userDefinedNewCols.add("trans_rmk");
		
		userDefinedColsMustFill.add("lpas_for_which_trans_id");
		userDefinedColsMustFill.add("lpas_type_of_share");
		userDefinedColsMustFill.add("lpas_share_center");
		
		userDefinedNewFormColNo = 2;
		
		myhtmlmgr.refreshPageOnDelete = true;
		userDefinedTableHeadersClass = "bg-purple bg-lighten-1 white";
		
		userDefinedNewColsHtmlType.put("lpas_for_which_trans_id", "DROPLIST");
		//userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
		
	}// end of constructor customer_payment

	@Override
	public void initialize(HashMap smartyStateMap) {
		MainSql = "select trans_id, lpas_for_which_trans_id, trans_amount_paid_actually_iqd,"
				+ " lpas_tot_cases, lpas_tot_cases_center,  lpas_tot_cases_rural, "
				+ " lpas_type_of_share, lpas_share_center , lpas_share_rural, "
				+ " lpas_share_calculated_profit_center, "
				+ " (lpas_total_calculated_profit_center - lpas_share_calculated_profit_center) as other_share_center, "
				+ " lpas_share_calculated_profit_rural, "
				+ " (lpas_total_calculated_profit_rural - lpas_share_calculated_profit_rural) as other_share_rural, "
				+ " trans_rmk,trans_createdby, trans_createddt, '' as fake, '' as  showdel "
				+ " from p_fin_transactions"
				+ " join p_liaison_pickup_agent_share on lpas_trans_id = trans_id  "
				+ " where trans_operationentity='"+entityType.toString()+"'"
						+ " and trans_operationcode = '"+m_operationCode+"' "
				+ " and trans_initiated_in_branch_id ={userstorecode} and trans_entity_id="+m_branchWeAreHandling+" "
				+ " and trans_deleted='N' order by trans_id desc ";
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
		String  receiverBranchName = "";
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select branch_name from kbbranches where branch_id = ?");			
			pst.setInt(1, m_branchWeAreHandling);
			rs = pst.executeQuery();
			if(rs.next()) {
				receiverBranchName = rs.getString("branch_name");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		if (FinOperationEntity.LIAISON_AGENT == entityType) {
			userDefinedNewLookups.put("lpas_for_which_trans_id", 
			"select trans_id, concat(trans_id, '- مبلغ الدفعة (' , trans_receipts_amt_iqd , ')')  "
			+ " from p_fin_transactions where trans_operationentity = 'BRANCH' "
			+ " and trans_entity_id = "+branchId_G+"  and trans_operationcode = 'CASES' "
			+ " and trans_initiated_in_branch_id = "+m_branchWeAreHandling+""
			+ " and trans_id not in (select lpas_for_which_trans_id from p_liaison_pickup_agent_share)"
			+ " and trans_deleted = 'N' ");
		}
		super.initialize(smartyStateMap);
		userDefinedCaption = "دفعات مندوب الأرتباط للوصولات المرسلة الى فرع   "+receiverBranchName;
		userDefinedNewCaption = "دفعات مندوب الأرتباط للوصولات المرسلة الى فرع   "+receiverBranchName;
	}

	public String printPmtReceipt(HashMap<String, String> hashy) {
		String btn ="<a href='../../LiaisonAgentSharePaymentReceiptSRVL?trans_id="+hashy.get("trans_id")+"'"
				+ " class='btn btn-xs btn-warning' >طباعة أيصال دفع "+hashy.get("trans_id")+"<i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
	}
	
	public String showDel(HashMap<String, String> hashy) {
		String rank = (String) arrayGlobals.get("userRank");
		String superItRank = (String) arrayGlobals.get("superItRank");
		String showDel = "<td align='center' style='vertical-align: middle;'>";
		if((rank.equalsIgnoreCase("FIN_OP_MGR")  || superItRank.equalsIgnoreCase("Y") 
				|| rank.equalsIgnoreCase("BRANCHMGR") 
				|| rank.equalsIgnoreCase("SYSMANAGER")
				|| rank.equalsIgnoreCase("GA"))  ) {
			showDel += "<button type='button' onclick=\"link=false; "
						+ "var rs =doDeleteSmarty(this,'هل تريد حذف هذه الدفعة ؟' ,'trans_id','"+hashy.get("trans_id")+"' , 'com.app.incomeoutcome.LiaisonOrPickUpAgentSharePayments' ); return rs;\" class='btn btn-danger btn-sm'> "
								+ "<li class='fa fa-trash'></li></button></td>";
				
			return showDel;	
		}
		return "<td></td>";
	}

	@Override
	public String doDelete(HttpServletRequest rqs) {
		int transactionToBeDeleted = Integer.parseInt(rqs.getParameter(keyCol));
		PreparedStatement pst = null;
		String msg = "تم المسح";
		try {
			int userId_G = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals));
			int branchId_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
			UtilitiesStandardFinancials.deleteTransaction(conn, transactionToBeDeleted, userId_G, branchId_G, 
					"مسح عملية مالية - حسابات مندوب الأرتباط لوصولات صادرة ");
			pst = conn.prepareStatement("delete from p_liaison_pickup_agent_share where lpas_trans_id=? ");
			pst.setInt(1, transactionToBeDeleted);
			pst.executeUpdate();
			
			conn.commit();
		} catch (Exception e) {
			try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
			logErrorMsg = "";
			msg = "خطأ, "+e.getMessage();
			e.printStackTrace();
		} finally {
			try {pst.close();} catch (Exception e) {}
		}
		return msg;
	}// end of doDelete*/
	
	private HashMap<String, Long> calculateTotalShareAmtFromBranchPayment (Connection a_conn, int a_transId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		 HashMap<String, Long> map = new  HashMap<String, Long>();
		try {
			ps = a_conn.prepareStatement("select count(*),  sum(c_shipment_cost - cc_pathcost ) as profit "
					+ " from p_cases join p_caseschain on c_id = cc_caseid  "
					+ " where cc_branchpmtid = ?");
			ps.setInt(1, a_transId);
			rs = ps.executeQuery();
			if (rs.next()) {
				map.put("total_cases", rs.getLong(1));
				map.put("total_amount", rs.getLong(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {rs.close();} catch (Exception e) {}
			try {ps.close();} catch (Exception e) {}
		}
		return map;
	}
	
	@Override
	public String doInsert(HttpServletRequest rqs, boolean autoCommit) {
		String statusMsg = " تم تسجيل الدفعه ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		inputMap_ori = filterRequest(rqs);
		try {
			int paymentForTransactionId = Integer.parseInt( inputMap_ori.get("lpas_for_which_trans_id")[0]);
			String shareTypePercentageOrDirectAmount = inputMap_ori.get("lpas_type_of_share")[0];
			int shareAmountCenter = Integer.parseInt( inputMap_ori.get("lpas_share_center")[0]);
			
			long centerCasesCount = 0 ;
			long totalAmountCenterCases = 0, totalShareAmountToPayForCenterCases =0 ;
			if(entityType == FinOperationEntity.LIAISON_AGENT) {
				HashMap<String, Long> centerCasesMap = 
						calculateTotalShareAmtFromBranchPayment (conn, paymentForTransactionId);
				totalAmountCenterCases = centerCasesMap.get("total_amount");
				centerCasesCount =  centerCasesMap.get("total_cases");
				
			}
			if (shareTypePercentageOrDirectAmount.equalsIgnoreCase("PERCENTAGE")) {
				totalShareAmountToPayForCenterCases = ((totalAmountCenterCases * shareAmountCenter) / 100);
			}
			SafePaymentTypeMetaInfoBean safePaymentTypeMetaInfoBean = 
							UtilitiesSafeFinancials.getSafePaymentTypeMetaInfoKbgeneral(conn, 
									entityType.toString(), "PMTTYPE" , m_operationCode.toString());
					
			if (safePaymentTypeMetaInfoBean.getSafeImpact() != PaymentImpactOnSafe.DEDUCT_SAFE) {
				throw new Exception ("Error, Transaction type is not DB, it's "+safePaymentTypeMetaInfoBean.getDbOrCr());
			}
				
			int userId_G = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
			int branchId_G = Integer.parseInt(replaceVarsinString(" {userstorecode} ", arrayGlobals).trim());
			AccountantBoxBean accountantBoxBean= UtilitiesSafeFinancials.GetAccountantBox(conn, userId_G, branchId_G);
			int accountBoxTransactionId = UtilitiesSafeFinancials.createAcctBoxTransactions(
					conn, 0, "p_fin_transactions".toUpperCase(), safePaymentTypeMetaInfoBean.getSafeImpact(), 
					userId_G, (totalShareAmountToPayForCenterCases), 
					0, branchId_G, safePaymentTypeMetaInfoBean.getName() + " - " + " - دفع مبلغ مالي", 
					"kbbranches", "branch_id",  m_branchWeAreHandling, "branch_name",
					Utilities.getBranchesInfo(conn, m_branchWeAreHandling+"").get("name"), userId_G);
					
			StandardTransactionBean standardTransactionBean = new StandardTransactionBean();
			standardTransactionBean.setEntity(entityType);
			standardTransactionBean.setEntityId(m_branchWeAreHandling);
			standardTransactionBean.setCategory(FinOperationCategory.PMTTYPE);
			standardTransactionBean.setCode(m_operationCode);
			standardTransactionBean.setInitiatedInBranchId(branchId_G);
			standardTransactionBean.setWhichScreen("دفع حصة مندوب أرتباط لوصولات صادرة لفرع ");
			standardTransactionBean.setTransactionAmountIqd((totalShareAmountToPayForCenterCases ));
			standardTransactionBean.setAmountPaidActuallyIqd((totalShareAmountToPayForCenterCases ));
			standardTransactionBean.setPayerBox(accountantBoxBean.getBoxId());
			standardTransactionBean.setPayerBoxTransactionId(accountBoxTransactionId);
			standardTransactionBean.setRemarks(inputMap_ori.get("trans_rmk")[0]);
			int standardStransactionId = 
					UtilitiesStandardFinancials.buildStandardTransaction(
							conn, standardTransactionBean, branchId_G,  userId_G );
			ps = conn.prepareStatement("insert into p_liaison_pickup_agent_share"
			+ "(lpas_trans_id	  	   , lpas_tot_cases   	  , lpas_tot_cases_center, "
			+ " lpas_type_of_share	   , lpas_share_center	  , lpas_share_calculated_profit_center, "
			+ " lpas_for_which_trans_id, lpas_for_which_branch, lpas_total_calculated_profit_center) "
			+ " values("+CoreUtilities.getQuestionMarks(9)+") ");
			ps.setInt(1, standardStransactionId);
			ps.setLong(2, centerCasesCount);
			ps.setLong(3, centerCasesCount);
			ps.setString(4, shareTypePercentageOrDirectAmount);
			ps.setLong(5, shareAmountCenter);
			ps.setLong(6, totalShareAmountToPayForCenterCases);
			ps.setInt(7, paymentForTransactionId);
			ps.setInt(8, m_branchWeAreHandling);
			ps.setLong(9, totalAmountCenterCases);
			ps.executeUpdate();
			conn.commit();
		} catch (Exception e) {
			statusMsg = "Error at payment creation, error (" + e.getMessage()+ ")";
			setInsertErrorFlag(true);
			try {conn.rollback();} catch (Exception ignoreE) {}
			e.printStackTrace();
		} finally {
			try {ps.close();} catch (Exception e) {}
		}
		return statusMsg;
	}

	public FinOperationCode getM_operationCode() {
		return m_operationCode;
	}

	public void setM_operationCode(FinOperationCode m_operationCode) {
		this.m_operationCode = m_operationCode;
	}

	public int getM_branchWeAreHandling() {
		return m_branchWeAreHandling;
	}

	public void setM_branchWeAreHandling(int m_branchWeAreHandling) {
		this.m_branchWeAreHandling = m_branchWeAreHandling;
	}
		
	
}// end of class
