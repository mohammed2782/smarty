package com.app.setup;

import smarty.core.CoreMgr;

public class SetupMasterCustEmpRanks extends CoreMgr {
	public SetupMasterCustEmpRanks(){
		MainSql = " select * From kbrank where rank_formastercustomeremp ='Y'";
		canFilter = true;
		canEdit = true;
		canNew = true;
		canDelete = true;
		mainTable = "kbrank";
	    keyCol = "rank_id";
	    userDefinedCaption = "Ranks";
	    
	    
	    userDefinedGridCols.add("rank_name_en");
	    userDefinedGridCols.add("rank_name_ar");
	    userDefinedGridCols.add("rank_code");
	  
	    
	    
	    userDefinedColLabel.put("rank_name_en", "Rank Name");
	    userDefinedColLabel.put("rank_name_ar", "أسم المرتبه عربي");
	    userDefinedColLabel.put("rank_code", "Rank Code");
	    userDefinedColLabel.put("rank_super", "Super");
	    userDefinedColLabel.put("rank_super_it", "Super It");
	    
	    
	   userDefinedNewCols.add("rank_name_en");
	   userDefinedNewCols.add("rank_name_ar");
	   userDefinedNewCols.add("rank_code");
	   userDefinedNewCols.add("rank_formastercustomeremp");
	   userDefinedColLabel.put("rank_super", "Super");
	   userDefinedColLabel.put("rank_formastercustomeremp", "مرتبة خاصة بموظفي العميل");
	   
	   userDefinedReadOnlyNewCols.add("rank_formastercustomeremp");
	   
	   userDefinedEditCols.add("rank_name_en");
	   userDefinedEditCols.add("rank_name_ar");
	   userDefinedEditCols.add("rank_code");
	   
	   userDefinedNewColsDefualtValues.put("rank_formastercustomeremp", new String[] {"Y"});
	   
	   
	   userDefinedFilterCols.add("rank_code");
	   userDefinedFilterLookups.put("rank_code", "select rank_code , rank_code from kbrank");
	   
	   userDefinedColsMustFill.add("rank_name_en");
	   userDefinedColsMustFill.add("rank_name_ar");
	   userDefinedColsMustFill.add("rank_code");
	   
	}

}
