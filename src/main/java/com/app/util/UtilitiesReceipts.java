package com.app.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.app.beans.GeneratedReceiptBean;

public class UtilitiesReceipts extends Utilities {
	/*
	 * get list of rcp numbers
	 */
	public ArrayList<GeneratedReceiptBean> getRceiptsGeneratedListPerSet (Connection conn, int setId ) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<GeneratedReceiptBean> receiptsList = new ArrayList<GeneratedReceiptBean>();
		try{
			pst = conn.prepareStatement("select rec_full_receipt_id, rec_set_prefix, rec_receipt_book_no   from p_receipts"
					+ " where rec_set_prefix = (select rs_full_prefix from p_receipts_set where rs_id=?)");
			pst.setInt(1, setId);
			rs = pst.executeQuery();
			while (rs.next()){
				GeneratedReceiptBean grb = new GeneratedReceiptBean();
				grb.setReceiptNo(rs.getString("rec_full_receipt_id"));
				grb.setSetName(rs.getString("rec_set_prefix"));
				grb.setBookNo(rs.getInt("rec_receipt_book_no"));
				receiptsList.add(grb);
			}
		}catch (Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {/*ignore*/}
			try {pst.close();}catch(Exception e) {/*ignore*/}
		}
		return receiptsList;
	}
	
}
