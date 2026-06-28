package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.filefilter.CanReadFileFilter;

import com.app.beans.CustomerInfoBean;
import com.app.beans.MasterCustomerInfoBean;
import com.app.beans.ReceiptsBookBean;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;
import smarty.db.mysql;

public class AssignReceiptsToCustomer extends CoreMgr{
	public AssignReceiptsToCustomer () {
		MainSql  = "select rbook_id, rbook_assigned_customer,  rbook_assigned_master_cust , rbook_no , rs_full_prefix , rs_id, rbook_used "
				+ "from p_receipts_books join p_receipts_set on rbook_setid = rs_id where 1=0";
		userDefinedFilterCols.add("rs_id");
		canEdit = true;
		mainTable = "p_receipts_books";
		keyCol = "rbook_id";
		UserDefinedPageRows = 200;
		canFilter = true;
		
		userDefinedGridCols.add("rs_full_prefix");
		userDefinedGridCols.add("rbook_no");
		//userDefinedGridCols.add("rbook_assinged_master_cust");
		userDefinedGridCols.add("rbook_assigned_customer");
		userDefinedGridCols.add("rbook_used");
		
		userDefinedColLabel.put("rs_id", "المجموعة");
		userDefinedColLabel.put("rs_full_prefix", "المجموعة");
		userDefinedColLabel.put("rbook_no", "رقم الدفتر");
		userDefinedColLabel.put("rbook_assigned_master_cust", "العميل");
		userDefinedColLabel.put("rbook_assigned_customer", "الزبون");
		userDefinedColLabel.put("rbook_used", "هل تم أستعمالة؟");
		userDefinedLookups.put("rbook_used", "select kbcode, kbdesc from kbgeneral where kbcat1 = 'YESNO' " );
		
		userDefinedEditCols.add("rbook_no");
		userDefinedEditCols.add("rbook_assigned_customer");
		//userDefinedEditCols.add("rbook_assinged_master_cust");
		
		
		userDefinedEditCaption = "أسناد دفتر للعميل";
		userDefinedCaption = "دفاتر الوصولات";
		userDefinedReadOnlyEditCols.add("rbook_no");
	}
	@Override
	public void initialize(HashMap smartyStateMap){
		int currentBranch = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		
		userDefinedLookups.put("rs_id", "select rs_id, rs_full_prefix  from p_receipts_set where rs_branch = "+currentBranch+"   ");
		userDefinedLookups.put("rbook_assigned_master_cust", "select mcust_id, mcust_name from kb_mastercustomer "
				+ "where mcust_branchcode= "+currentBranch+"   ");
		
		userDefinedLookups.put("rbook_assigned_customer", "select cust_id, cust_name from kbcustomers "
				+ "where cust_branch= "+currentBranch+"   ");
		userDefinedFilterColsHtmlType.put("rs_id", "DROPLIST");
		userDefinedEditColsHtmlType.put("rbook_assigned_customer", "DROPLIST");
		super.initialize(smartyStateMap);

		
		boolean foundSearch = false;
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null) && (!value.equals(""))) {
					if (parameter.equals("rs_id")) {
						foundSearch = true;
					}
				}
			}
		}
		if (search_paramval!=null && !search_paramval.isEmpty() && foundSearch) {
			MainSql  = "select rbook_id,  rbook_assigned_customer,  rbook_assigned_master_cust , rbook_no , rs_full_prefix , rs_id, rbook_used "
					+ " from p_receipts_books "
					+ " join p_receipts_set on rbook_setid = rs_id where rs_branch="+currentBranch;
		}
		
	}
	@Override
	public String doUpdate(HttpServletRequest rqs, boolean autoCommit) {
		PreparedStatement pst = null; 
		ResultSet rs = null;
		String rbookId = parseUpdateRqs(rqs);
		String msg = "تم التعديل بنجاح";
		String custid = "";
		int userid_G = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
		int currentBranch_G = Integer.parseInt(replaceVarsinString("{userstorecode}",arrayGlobals).trim());
		try{
			
			CustomerInfoBean customerInfoBean= Utilities.getCustomerInfo(conn,
					Integer.parseInt(inputMap_ori.get("rbook_assigned_customer")[0]));
			if(customerInfoBean ==null) {
				throw new Exception ( "could not find cusomter"
						+ " with id = "+inputMap_ori.get("rbook_assigned_customer")[0]);
			}
			if (customerInfoBean.getCustBelongToBranch()!=currentBranch_G) {
				throw new Exception ( "This customer ("+customerInfoBean.getCustId()+")"
						+ " belongs to another branch, currentBranch = "+currentBranch_G);
			}
			ReceiptsBookBean receiptsBookBean=Utilities.getReceiptsBookInfo(conn, Integer.parseInt(rbookId));
			
			if (receiptsBookBean ==null) {
				throw new Exception ( "book not found");
			}
			if (receiptsBookBean.isUsed()) {
				if (receiptsBookBean.isTherePaymentMadeForAnyReceipt()) {
					throw new Exception ( "لقد تم استعمال هذا الدفتر مسبقا وهنالك وصولات تم محاسبة العميل عليها ولا يمكن اسناد لمتجر له");
				}
			}
			System.out.println("started updating p_receipts=====================>"
					+ "prefix = "+receiptsBookBean.getSetPrefix() + " , book no =  "+receiptsBookBean.getBookNo());
			pst = conn.prepareStatement("update  p_receipts "
					+ " set rec_assigned_master_cust=? , rec_assigned_customer=? "
					+ " where rec_set_prefix=? and rec_receipt_book_no= ? ");
			pst.setInt(1, customerInfoBean.getMasterCustId());
			pst.setInt(2, customerInfoBean.getCustId());
			pst.setString(3, receiptsBookBean.getSetPrefix());
			pst.setInt(4, receiptsBookBean.getBookNo());
			int updatedRows = pst.executeUpdate();
			if (updatedRows != Utilities.receiptsInBook) {
				throw new Exception ( "Error, rows count mismatch,  "
						+ "could not update receipts, book no = "+receiptsBookBean.getBookNo()+", "
								+ "setPrefix= "+receiptsBookBean.getSetPrefix());
			}
			System.out.println("finished updating p_receipts=====================>");
			try {pst.close();}catch(Exception ex) {}
			
			pst = conn.prepareStatement("update p_receipts_books "
					+ " set rbook_assigned_master_cust=?,  "
					+ " rbook_assigned_customer=? "
					+ " where  rbook_id=?");
			pst.setInt(1, customerInfoBean.getMasterCustId());
			pst.setInt(2, customerInfoBean.getCustId());
			pst.setInt(3, receiptsBookBean.getId());
			updatedRows = pst.executeUpdate();
			try {pst.close();}catch(Exception ex) {}
			
			if (updatedRows != 1) {
				throw new Exception ( "Error updating book, rows count mismatch, "
						+ "  book id = "+receiptsBookBean.getId()+", "
								+ "setPrefix= "+receiptsBookBean.getSetPrefix()+", update rows = "+updatedRows);
			}
			//update related cases
			System.out.println("started updating cases=====================>");
			pst = conn.prepareStatement("update p_cases join p_receipts on c_id = rec_caseid    set "
					+ " c_custid = ?, c_mastercustid = ? "
					+ " where rec_set_prefix  =? and rec_receipt_book_no = ? and rec_caseid is not null "
					+ " and c_pmtid  = 0 and c_pickupagentpmtid = 0");
			pst.setInt(1, customerInfoBean.getCustId());
			pst.setInt(2, customerInfoBean.getMasterCustId());
			pst.setString(3, receiptsBookBean.getSetPrefix());
			pst.setInt(4, receiptsBookBean.getBookNo());
			pst.executeUpdate();
			System.out.println("finished updating cases=====================>");
			
            conn.commit();
            setUpdateErrorFlag(false);
		}catch(Exception e){
			e.printStackTrace();
			msg = "Error at updated data, error("+ e.getMessage() +") ";
			 try{conn.rollback();}catch(Exception ex){}//end of inner catch
			 setUpdateErrorFlag(true);
		}finally{
			try {pst.close();}catch(Exception ex) {}		
		}//end of finally
		
		return msg;
	}
}
