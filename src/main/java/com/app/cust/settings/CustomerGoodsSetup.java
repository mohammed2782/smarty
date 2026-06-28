package com.app.cust.settings;

import smarty.core.CoreMgr;

public class CustomerGoodsSetup extends CoreMgr{
	public CustomerGoodsSetup() {
		MainSql = "select * from kbcustomer_goods where cg_mastercustid= {mastercustidlogin}";
		mainTable = "kbcustomer_goods";
		keyCol = "cg_id";
		canEdit = true;
		canNew = true;
		canDelete = true;
		
		userDefinedNewCols.add("cg_mastercustid");
		userDefinedNewCols.add("cg_goodscat");
		userDefinedNewCols.add("cg_goodsdesc");
		userDefinedNewCols.add("cg_createdby");
		userDefinedReadOnlyNewCols.add("cg_mastercustid");
		userDefinedReadOnlyNewCols.add("cg_createdby");
		
		userDefinedEditCols.add("cg_goodscat");
		userDefinedEditCols.add("cg_goodsdesc");
		
		
		userDefinedColLabel.put("cg_goodscat", "الصنف");
		userDefinedColLabel.put("cg_mastercustid", "العميل");
		userDefinedColLabel.put("cg_goodsdesc", "وصف البضاعة");
		userDefinedColLabel.put("cg_createdby", "أنشاء بواسطة");
		
		userDefinedNewColsHtmlType.put("cg_goodscat", "DROPLIST");
		userDefinedLookups.put("cg_goodscat", "select catg_id, catg_name from kbcategorygoods");
		userDefinedLookups.put("cg_mastercustid", "select mcust_id , mcust_name from kb_mastercustomer where mcust_id = {mastercustidlogin} ");
		userDefinedLookups.put("cg_createdby", "select us_id , us_name from kbusers");
		
		userDefinedNewColsDefualtValues.put("cg_mastercustid", new String [] {"{mastercustidlogin}"});
		userDefinedNewColsDefualtValues.put("cg_createdby", new String [] {"{usid}"});
		
		userDefinedGridCols.add("cg_goodscat");
		userDefinedGridCols.add("cg_goodsdesc");
		
		userDefinedColsMustFill.add("cg_goodsdesc");
		
		userDefinedNewColsHtmlType.put("cg_mastercustid", "DROPLIST");
		userDefinedNewColsHtmlType.put("cg_createdby", "DROPLIST");
		
		userDefinedCaption = "بضاعتي";
		userDefinedNewCaption = "اضافة بضاعة";
	}

}
