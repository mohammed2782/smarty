package com.app.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Utilities_Old {

	/*
	 * get customer info
	 */
	public LinkedHashMap<String, String> getcustomerInfo(Connection conn, String c_id) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		LinkedHashMap<String, String> customerInfo = new LinkedHashMap<String, String>();
		try {
			pst = conn.prepareStatement(
					"select c_name , c_phone1, date(cust_createddt) as joineddate from kbcustomers  where cust_id=?");
			pst.setString(1, c_id);
			rs = pst.executeQuery();
			while (rs.next()) {
				customerInfo.put("name", rs.getString("c_name"));
				customerInfo.put("hp", rs.getString("c_phone1"));
				customerInfo.put("joineddate", rs.getString("joineddate"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return customerInfo;
	}

	/*
	 * get list of rcp numbers
	 */
	public ArrayList<String> getRcpNoList(Connection conn, int bookId) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<String> rcpIdList = new ArrayList<String>();
		try {
			pst = conn.prepareStatement("select br_rcp_no from p_books_rcp where br_bid=?");
			pst.setInt(1, bookId);
			rs = pst.executeQuery();
			while (rs.next()) {
				rcpIdList.add(rs.getString("br_rcp_no"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return rcpIdList;
	}

	/*
	 * @ changes comma seperated by seperator like : or , to list new method added
	 * on 15/Mar/2017 by Nafie
	 */
	public static ArrayList<String> SplitStringToArrayList(String StrWithSeperator, String seperator) {
		ArrayList<String> convertedList = new ArrayList<String>();
		if (StrWithSeperator != null && StrWithSeperator.trim() != null && !StrWithSeperator.trim().equals("")) {
			// System.out.println("StrWithSeperator===>"+StrWithSeperator);
			String[] myArr = StrWithSeperator.split(seperator.trim());
			for (int i = 0; i < myArr.length; i++)
				convertedList.add(myArr[i]);
		}
		return convertedList;
	}

	/*
	 * @ change array list string to 'str1','str2',..etc_
	 */
	public StringBuilder getSingleQuoteCommaSeperated(ArrayList<String> array) {
		boolean first = true;
		StringBuilder sb = new StringBuilder("");
		for (String item : array) {
			if (!first)
				sb.append(",");

			sb.append("'" + item + "'");
			first = false;
		}
		return sb;
	}

	public boolean checkGeneratedReceipt(Connection conn, String generatedReceiptNo, int custId) throws Exception {
		boolean ok = true;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			// check if the user exists
			pst = conn.prepareStatement("select 1 from p_books_rcp where br_rcp_no=?");
			pst.setString(1, generatedReceiptNo);
			rs = pst.executeQuery();
			if (rs.next()) {
				ok = true;
			} else {
				ok = false;
				throw new Exception("الوصل رقم " + generatedReceiptNo + " غير متولد من النظام");
			}
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}

			// check if the receipt is used before
			pst = conn.prepareStatement("select br_cid from p_books_rcp where br_rcp_no=? and br_cid >0");
			pst.setString(1, generatedReceiptNo);
			rs = pst.executeQuery();
			if (rs.next()) {
				ok = false;
				throw new Exception("الوصل رقم " + generatedReceiptNo + " تم أستعماله سابقا");
			}
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}

			// check if the receipt is under another customer
			pst = conn.prepareStatement("select br_custid from p_books_rcp where br_rcp_no=?");
			pst.setString(1, generatedReceiptNo);
			rs = pst.executeQuery();
			if (rs.next()) {

				if (rs.getInt("br_custid") > 0)
					if (custId != rs.getInt("br_custid")) {
						ok = false;
						throw new Exception("هذا الوصل " + generatedReceiptNo + " ملك لزبون أخر  ");
					}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return ok;
	}

	public int getOwnerOfReceipt(Connection conn, String receiptNo, int a_branchCode) throws Exception {

		PreparedStatement pst = null;
		ResultSet rs = null;
		int custId = 0;
		try {
			// check if the receipt is under another customer
			pst = conn.prepareStatement("select br_custid from p_books_rcp where br_rcp_no=? and br_branchid=?");
			pst.setString(1, receiptNo);
			pst.setInt(2, a_branchCode);
			rs = pst.executeQuery();
			if (rs.next()) {
				custId = rs.getInt("br_custid");
			} else {
				new Exception("الوصل رقم " + receiptNo + " لم يتولد من النظام");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return custId;
	}

	public boolean checkIfReceiptGeneratedFromSystem(Connection conn, String receiptNo, int a_branchCode) throws Exception {
		boolean ok = true;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select 1 from p_books_rcp where br_rcp_no=? and br_branchid=?");
			pst.setString(1, receiptNo);
			pst.setInt(2, a_branchCode);
			rs = pst.executeQuery();
			if (rs.next()) {
				ok = true;
			} else {
				ok = false;
				throw new Exception("الوصل رقم " + receiptNo + " غير متولد من النظام");
			}
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return ok;
	}

	public boolean checkIfReceiptUsedBefore(Connection conn, String receiptNo, int a_branchCode) throws Exception {
		boolean ok = false;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = conn.prepareStatement("select br_cid from p_books_rcp where br_rcp_no=? and br_cid >0 and br_branchid=?");
			pst.setString(1, receiptNo);
			pst.setInt(2, a_branchCode);
			rs = pst.executeQuery();
			if (rs.next()) {
				ok = true;
				throw new Exception("الوصل رقم " + receiptNo + " تم أستعماله سابقا");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return ok;
	}

	public int getPickupAgentOfGeneratedReceipt(Connection conn, String generatedReceiptNo) throws Exception {
		int pickupAgent = 0;
		String branchOfReceipt = "";
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			pst = conn.prepareStatement(
					"select b_bookbranch from p_books_rcp join p_books on br_bid =b_id and br_rcp_no=?");
			pst.setString(1, generatedReceiptNo);
			rs = pst.executeQuery();
			if (rs.next()) {
				branchOfReceipt = rs.getString("b_bookbranch");
			}
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}

			// check if the receipt is under another customer
			pst = conn.prepareStatement(
					"select kbdesc from kbgeneral where kbcat1 = 'BRANCH' and kbcat2='PICKUPAGENT' and kbcode=?");
			pst.setString(1, branchOfReceipt);
			rs = pst.executeQuery();
			if (rs.next()) {
				pickupAgent = rs.getInt("kbdesc");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				/* ignore */}
			try {
				pst.close();
			} catch (Exception e) {
				/* ignore */}
		}
		return pickupAgent;
	}
}
