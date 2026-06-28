package com.app.setup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.db.mysql;

import org.apache.commons.lang3.StringUtils;
public class CustomerBooks extends CoreMgr{
	boolean firstBook = true;
	public CustomerBooks () {
		MainSql = "select 1 from dual";
		canFilter = true;
		
		userDefinedNewCols.add("b_bookbranch");
		userDefinedNewCols.add("b_noofrcp");
		userDefinedNewCols.add("b_rmk");
		
		userDefinedGridCols.add("b_id");
		userDefinedGridCols.add("b_bookbranch");
		userDefinedGridCols.add("b_usedinsystem");
		userDefinedGridCols.add("b_noofrcp");
		userDefinedGridCols.add("b_createddt");
		userDefinedGridCols.add("b_createdby");
		userDefinedGridCols.add("b_rmk");
		userDefinedGridCols.add("showranges");
		userDefinedGridCols.add("printPDF");
		userDefinedGridCols.add("printPDFNoDesign");
		
		
		userDefinedColLabel.put("b_bookbranch","الفرع");
		userDefinedColLabel.put("b_id","رقم الدفتر");
		userDefinedColLabel.put("b_noofrcp","عدد الإيصالات");
		userDefinedColLabel.put("b_createddt","تاريخ الخلق");
		userDefinedColLabel.put("b_createdby","المستخدم");
		userDefinedColLabel.put("b_rmk","ملاحظات");
		userDefinedColLabel.put("printPDF","طباعه");
		userDefinedColLabel.put("b_usedinsystem","تم إستخدامه في النظام؟");
		userDefinedColLabel.put("showranges", "التقسيمات");
		userDefinedEditCols.add("b_rmk");
		
		userDefinedColsMustFill.add("b_noofrcp");
		userDefinedColsMustFill.add("b_bookbranch");
		userDefinedLookups.put("b_bookbranch", "select kbcode , kbdesc from kbgeneral where kbcat1='RCPBOOKS' and kbcat2='BRANCH'");
		userDefinedLookups.put("b_usedinsystem", "select kbcode , kbdesc from kbgeneral where kbcat1='YESNO'");
		myhtmlmgr.refreshPageOnDelete = true;
		
		userModifyTD.put("printPDF", "printReceiptsBook({b_id}, {totused})");
		userModifyTD.put("printPDFNoDesign", "printReceiptsBookNoDesign({b_id}, {totused})");
		
		userDefinedCaption ="دفاتر الوصولات";
		
		userDefinedFilterCols.add("b_id");
		userDefinedFilterCols.add("b_usedinsystem");
		
		userModifyTD.put("del_edit","showEditDeleteBtn({b_id},{b_usedinsystem})");
		userModifyTD.put("showranges","showRangesPopUp({b_id})");
		myhtmlmgr.refreshPageOnDelete = true;
	}
	
	@Override
	public void initialize(HashMap smartyStateMap) {
		System.out.println("lu.getBranchCode()-->"+lu.getBranchCode());
		if (lu.getBranchCode() == 1) {
			MainSql = "select p_books_tlk.* , '' as printPDF, '' as printPDFNoDesign , '' as del_edit, "
					+ "'' as showranges from p_books_tlk order by b_id desc";
		}else if (lu.getBranchCode() == 34) {
			MainSql = "select p_books_tlr.* , '' as printPDF, '' as printPDFNoDesign , '' as del_edit, "
					+ "'' as showranges from p_books_tlr order by b_id desc";
		}
		super.initialize(smartyStateMap);
	}
	//
	
	public String printReceiptsBook(HashMap<String, String> hashy) {
		String btn = "<a href=\"../TLRPrintCustomerRcpBook?showrecieptdesign=Y&book_id="+hashy.get("b_id")+"\" "
				+ " class='btn btn-xs btn-success' > طباعة دفتر الأيصالات<i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
	}
	
	public String printReceiptsBookNoDesign(HashMap<String, String> hashy) {
		String btn = "<a href=\"../TLRPrintCustomerRcpBook?showrecieptdesign=N&book_id="+hashy.get("b_id")+"\" "
				+ " class='btn btn-xs btn-dark' > طباعة دفتر الأيصالات بدون تصميم<i class=\"fa fa-print fa-lg\"></i></a>";
		return "<td>" + btn + "</td>";
	}
	
	public String showRangesPopUp(HashMap<String, String> hashy) {
		String html = "";
		html = "<td>";
		html +="<button type=\"button\" class=\"btn btn-xs btn-info\" onclick=\"popitup ('assignReceiptsPopUp_old?bookid="+hashy.get("b_id")+"' , '' , 1000 ,600);\">تقسيمات</button>";
		html +="</td>";
		return html;
	}
	
	public String showEditDeleteBtn(HashMap<String,String> hashy ) {
		String s="<td align=\"center\">";
		if (hashy.get("b_usedinsystem").equalsIgnoreCase("N") && firstBook) {
			s +="<button type='button' onclick=\"link=false; var rs =doDeleteSmarty(this,'هل تريد حذف هذ ا الدفتر؟' ,'b_id','"+hashy.get("b_id")+"' , 'com.app.setup.CustomerBooks' ); return rs;\" "
					+ " class='btn btn-danger btn-xs'><li class='fa fa-trash'></li></button>";
			firstBook = false;
		}
		return s+"</td>";
	}
}
