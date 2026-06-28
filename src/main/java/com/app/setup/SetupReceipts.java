package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

import com.app.util.Utilities;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;
import smarty.db.mysql;

public class SetupReceipts extends CoreMgr {
	private static HashMap<Character,Integer> letterToNumberMap
		= new HashMap<Character,Integer>() {
		private static final long serialVersionUID = 7305703089067053723L;
		{
			put('A',1);
			put('B',2);
			put('C',3);
			put('D',4);
			put('E',5);
			put('F',6);
			put('G',7);
			put('H',8);
			put('I',9);
		}
	};
	
	public SetupReceipts() {
		MainSql = "select rs_id, rs_branch, rs_2letters,  rs_last_digit_year, rs_createddate , rs_printed , '' as print   from p_receipts_set";	
		userDefinedGridCols.add("rs_branch");
		userDefinedGridCols.add("rs_2letters");
		userDefinedGridCols.add("rs_last_digit_year");
		userDefinedGridCols.add("rs_createddate");
		userDefinedGridCols.add("rs_printed");
		userDefinedGridCols.add("print");
		
		userDefinedColLabel.put("rs_branch", "الفرع");
		userDefinedColLabel.put("rs_2letters", "الحرفين");
		userDefinedColLabel.put("rs_last_digit_year", "السنة");
		userDefinedColLabel.put("rs_createddate", " ");
		userDefinedColLabel.put("rs_printed", "مطبوع؟");
		userDefinedColLabel.put("print", " ");
		
		userDefinedFilterCols.add("rs_branch");
		userDefinedFilterCols.add("rs_last_digit_year");
		
		userDefinedLookups.put("rs_branch", "select branch_id, branch_name From kbbranches");
		userDefinedLookups.put("rs_2letters", "select (letters) as letters1 , (letters) as letters2 From p_receipts_letters");
		userDefinedLookups.put("rs_last_digit_year", "select kbdesc_ar, kbdesc From kbgeneral where kbcat1='YEAR' and kbcat2='YEAR'");
		userDefinedLookups.put("rs_printed", "select kbcode, kbdesc From kbgeneral where kbcat1='YESNO' ");
		
		
		userDefinedNewLookups.put("rs_2letters", "!select (letters) as letters1 , (letters) as letters2 From p_receipts_letters "
				+ " where letters not in ("
				+ " select distinct(rs_2letters) from p_receipts_set where rs_branch={rs_branch} and rs_last_digit_year={rs_last_digit_year})  ");
		
		canNew = true;
		canFilter = true;
		canEdit = true;
		keyCol = "rs_id";
		
		mainTable = "p_receipts_set";
		userDefinedNewCols.add("rs_last_digit_year");
		userDefinedNewCols.add("rs_branch");
		userDefinedNewCols.add("rs_2letters");
		
		userDefinedNewColsHtmlType.put("rs_branch", "DROPLIST");
		userDefinedNewColsHtmlType.put("rs_2letters", "CHECKBOX");
		
		userDefinedFilterColsHtmlType.put("rs_branch", "DROPLIST");
		userDefinedFilterColsHtmlType.put("rs_2letters", "DROPLIST");
		
		userDefinedEditCols.add("rs_printed");
		
		
		
		userModifyTD.put("print", "printReceiptsBook({rs_id})");
	}
	public String printReceiptsBook(HashMap<String, String> hashy) {
		String btn = "<a href=\"../../PrintReceiptsSetSRVL?rs_id="+hashy.get("rs_id")+"\" "
				+ " class='btn btn-xs btn-success' > طباعة دفتر الأيصالات<i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
	}
	
	 @Override 
	 public String doInsert (HttpServletRequest rqs , boolean commit) {
		 Connection conn = null;
		 PreparedStatement pstCreateSet = null, pstCreateBook = null, pstAddReceipts = null, pstCheckIfCreatedAlready = null;
		 ResultSet rs = null, rs__CheckIfSetIsCreated = null;
		 int userId = Integer.parseInt(replaceVarsinString(" {usid} ", arrayGlobals).trim());
		 
		 int totalReceiptsPerBatch = 10000;
		 String lastDigitYear = rqs.getParameter("rs_last_digit_year");
		 int cnt = 0;
		 try {
			 conn = mysql.getConn();
			 pstCreateSet = conn.prepareStatement("insert into p_receipts_set "
			 		+ "(rs_last_digit_year, rs_branch, rs_2letters, rs_full_prefix) " + " values ("+CoreUtilities.getQuestionMarks(4)+")", Statement.RETURN_GENERATED_KEYS);
			
			 pstCreateBook = conn.prepareStatement("insert into p_receipts_books"
			 		+ "(rbook_setid, rbook_setprefix, rbook_no) "+ " values("+CoreUtilities.getQuestionMarks(3)+")" , Statement.RETURN_GENERATED_KEYS);
			 
			 pstAddReceipts = conn.prepareStatement("insert into p_receipts"
			 		+ " (rec_no, rec_full_receipt_id, rec_set_prefix, rec_receipt_book_no) "+ "values("+CoreUtilities.getQuestionMarks(4)+")");
			 
			 pstCheckIfCreatedAlready = conn.prepareStatement("select 1 from p_receipts_set where rs_full_prefix=?");
			 for (String branch : rqs.getParameterValues("rs_branch")) { // loop through branches
				 for (String twoletters : rqs.getParameterValues("rs_2letters")) {
					 String setFullPrefix = branch+lastDigitYear+twoletters;
					 pstCheckIfCreatedAlready.setString(1, setFullPrefix);
					 rs__CheckIfSetIsCreated = pstCheckIfCreatedAlready.executeQuery();
					 if(rs__CheckIfSetIsCreated.next()) {
						 if (rs__CheckIfSetIsCreated.getInt(1)==1) {
							 try {rs.close();}catch(Exception e) {}
							 continue;
						 }
					 }
					 String setFullPrefixTranslatedToNumbers = 
							 branch+lastDigitYear+translateLettersToNumber(twoletters);
					 
					 pstCreateSet.setString(1, lastDigitYear);
					 pstCreateSet.setString(2, branch);
					 pstCreateSet.setString(3, twoletters);
					 pstCreateSet.setString(4, setFullPrefix);
					 pstCreateSet.executeUpdate();
					 rs = pstCreateSet.getGeneratedKeys();
					 rs.next();
					 int setId = rs.getInt(1);
					 try {rs.close();}catch(Exception e) {}
					 
					 for (int bookId = 1; bookId<=(totalReceiptsPerBatch/Utilities.receiptsInBook); bookId++) {
						 pstCreateBook.setInt(1, setId);
						 pstCreateBook.setString(2, setFullPrefix);
						 pstCreateBook.setInt(3, bookId);
						 pstCreateBook.executeUpdate();
						
						 for (int receiptNo= ((bookId-1)*Utilities.receiptsInBook)+1;  receiptNo<=(bookId*Utilities.receiptsInBook) ; receiptNo++) {
							 pstAddReceipts.setInt(1, receiptNo);
							 pstAddReceipts.setString(2, setFullPrefixTranslatedToNumbers+receiptNo);
							 pstAddReceipts.setString(3, setFullPrefix);
							 pstAddReceipts.setInt(4, bookId);
							 pstAddReceipts.addBatch();
						 }
						 pstAddReceipts.executeBatch();
						 cnt +=50;
					 }
					 conn.commit();
				 }
			 }
			 conn.commit();
			 
		 }catch(Exception e) {
			 try {conn.rollback();}catch(Exception eRoll) {/**/}
			 e.printStackTrace();
		 }finally {
			 try {rs.close();}catch(Exception e) {/**/}
			 try {rs__CheckIfSetIsCreated.close();}catch(Exception e) {/**/}
			 
			 try {pstCreateSet.close();}catch(Exception e) {/**/}
			 try {pstCreateBook.close();}catch(Exception e) {/**/}
			 try {pstAddReceipts.close();}catch(Exception e) {/**/}
			 try {pstCheckIfCreatedAlready.close();}catch(Exception e) {/**/}
			 
			 try {conn.close();}catch(Exception e) {/**/}
		 }
		 return cnt+" تم توليد";
	 }
	 
	 private String translateLettersToNumber(String a_2Letters) {
		 return letterToNumberMap.get(a_2Letters.charAt(0))+""+letterToNumberMap.get(a_2Letters.charAt(1));
	 }
}
