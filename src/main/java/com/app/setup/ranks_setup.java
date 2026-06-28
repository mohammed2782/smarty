package com.app.setup;
import smarty.core.CoreMgr;

public class ranks_setup extends CoreMgr {
	public ranks_setup(){
		MainSql = " select * From kbranks";
		
	    userDefinedCaption = "Ø¥Ø¹Ø¯Ø§Ø¯Ø§Øª Ø§Ù„Ù…Ø±ØªØ¨Ø©";
	    userDefinedNewCaption = "Ø¥Ø¶Ø§Ù�Ø© Ø¨ÙŠØ§Ù†Ø§Øª Ø§Ù„Ù…Ø±ØªØ¨Ø©";
	    userDefinedEditCaption = "ØªØ¹Ø¯ÙŠÙ„ Ø¨ÙŠØ§Ù†Ø§Øª Ø§Ù„Ù…Ø±ØªØ¨Ø©";
		
	    keyCol = "r_id";
		mainTable ="kbranks";
		
		search_paramval = null;
		canNew = true;
		canFilter = false;
		canEdit = true;
		canDelete = true;
		clickableRow = false;    
	    
		userDefinedGridCols.add("r_id");
		userDefinedGridCols.add("r_level");
		userDefinedGridCols.add("r_desc");

	    userDefinedColLabel.put("r_id", "Ø±Ù‚Ù… Ø§Ù„Ù…Ø±ØªØ¨Ø©");
	    userDefinedColLabel.put("r_level", "Ø§Ù„Ø±Ù‚Ù… Ø§Ù„ØªØ³Ù„Ø³Ù„ÙŠ Ù„Ù„Ù…Ø±ØªØ¨Ø©");
	    userDefinedColLabel.put("r_desc", "Ø¥Ø³Ù… Ø§Ù„Ù…Ø±ØªØ¨Ø©");
	    
	   userDefinedNewCols.add("r_level");
	   userDefinedNewCols.add("r_desc");
	   
	   userDefinedColsMustFill.add("r_level");
	   userDefinedColsMustFill.add("r_desc");
	   
	   userDefinedEditCols.add("r_level");
	   userDefinedEditCols.add("r_desc");
	    
	}

}
