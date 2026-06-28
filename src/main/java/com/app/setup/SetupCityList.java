package com.app.setup;

import smarty.core.CoreMgr;

public class SetupCityList extends CoreMgr{
	public SetupCityList(){
	
		System.out.println("you inside Setup city constructor");		
		MainSql ="select * from kbcity";

		userDefinedCaption = "Ø¥Ø¹Ø¯Ø§Ø¯Øª Ø§Ù„Ù…Ø¯ÙŠÙ†Ø©";
		userDefinedNewCaption = "Ø¥Ø¶Ø§Ù�Ø© Ø¨ÙŠØ§Ù†Ø§Øª Ø§Ù„Ù…Ø¯ÙŠÙ†Ø©";
		userDefinedEditCaption = "ØªØ¹Ø¯ÙŠÙ„ Ø¨ÙŠØ§Ù†Ø§Øª Ø§Ù„Ù…Ø¯ÙŠÙ†Ø©";
		
		keyCol = "ct_id";
		mainTable ="kbcity";
		
		search_paramval = null;
		canNew = true;
		canFilter = true;
		canEdit = true;
		canDelete = true;
		clickableRow = false;
		
		userDefinedGridCols.add("ct_id");
		userDefinedGridCols.add("ct_name");
		
		userDefinedColLabel.put("ct_id", "Ø±Ù‚Ù… Ø§Ù„Ù…Ø¯ÙŠÙ†Ø©");
		userDefinedColLabel.put("ct_name", "Ø¥Ø³Ù… Ø§Ù„Ù…Ø¯ÙŠÙ†Ø©");

		userDefinedNewCols.add("ct_name");

		userDefinedFilterCols.add("ct_name");
		userDefinedFilterLookups.put("ct_name", "select ct_name , ct_name from kbcity");

		userDefinedColsMustFill.add("ct_name");

		userDefinedEditCols.add("ct_name");
 }	
}

