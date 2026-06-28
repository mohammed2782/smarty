package com.app.reports;

import java.util.HashMap;

import smarty.core.CoreMgr;
import com.app.util.UtilitiesFeqar;

public class TrackingChanges extends CoreMgr {
	public TrackingChanges() {
		MainSql = "select log_changes.*, '' as fromdt, '' as todt from log_changes where 1!=1";
		
		canFilter = true;
		
		userDefinedFilterCols.add("log_actionby");
		userDefinedFilterCols.add("fromdt");
		userDefinedFilterCols.add("todt");
		
		userDefinedFilterColsHtmlType.put("fromdt", "DATE");
		userDefinedFilterColsHtmlType.put("todt", "DATE");
		
		userDefinedLookups.put("log_actionby", "select us_id, us_name from kbusers order by us_name");
		
		UserDefinedPageRows = 200;
		
		userDefinedGridCols.add("log_id");
		userDefinedGridCols.add("log_table");
		userDefinedGridCols.add("log_keycolname");
		userDefinedGridCols.add("log_keycolid");
		userDefinedGridCols.add("log_colnamechanged");
		userDefinedGridCols.add("log_old_value");
		userDefinedGridCols.add("log_new_value");
		userDefinedGridCols.add("log_actioname");
		userDefinedGridCols.add("log_screenname");
		userDefinedGridCols.add("log_actionby");
		userDefinedGridCols.add("log_action_timestamp");
		
		userDefinedColLabel.put("fromdt", "بتاريخ");
		userDefinedColLabel.put("todt", "الى تاريخ");
		userDefinedColLabel.put("log_actionby", "اسم المستخدم");
		userDefinedColLabel.put("log_action_timestamp", "الوقت والتاريخ");
		userDefinedColLabel.put("log_screenname", "اسم النافذة");
		userDefinedColLabel.put("log_actioname", "نوع التعديل");
		userDefinedColLabel.put("log_old_value", "القيمة قبل التعديل");
		userDefinedColLabel.put("log_new_value", "القيمة بعد التعديل");
		userDefinedColLabel.put("log_colnamechanged", "العامود المعدل عليه");
		userDefinedColLabel.put("log_keycolid", "KeyColId");
		userDefinedColLabel.put("log_keycolname", "KeyColName");
		userDefinedColLabel.put("log_table", "اسم الجدول");
		userDefinedColLabel.put("log_id", "LogId");
		
		userDefinedCaption = "تتبع التغييرات";
		
		userDefinedLookups.put("log_actioname", "select trans_engdesc, trans_arabdesc from kbtranslator where trans_section = 'log_actioname'");
		userDefinedLookups.put("log_table", "select trans_engdesc, trans_arabdesc from kbtranslator where trans_section = 'log_table'");
		userDefinedLookups.put("log_keycolname", "select trans_engdesc, trans_arabdesc from kbtranslator where trans_section = 'log_keycolname'");
		
		userDefinedColsMustFillFilter.add("fromdt");
		
	}
	
	@Override
	public void initialize(HashMap smartyStateMap){
		super.initialize(smartyStateMap);
		boolean foundSearch = false;
		UtilitiesFeqar utf = new UtilitiesFeqar();
		String userIdFromSearch = "log_actionby";
		String fromdt = "ALL", todt = "ALL";
		for (String parameter : search_paramval.keySet()) {
			for (String value : search_paramval.get(parameter)) {
				if (!parameter.equals("filter") && (value != null)
						&& (!value.equals(""))) {
					if (parameter.equals("log_actionby")) {
						userIdFromSearch=value;
						
					}
					if (parameter.equals("fromdt")) {
						fromdt =  value;
						foundSearch = true;
						//search_paramval.remove("fromdt");
					} 
					if (parameter.equals("todt")) {
						todt =  value;
						//search_paramval.remove("todate");
						//foundSearch = true;
					} 
				}
			}

		}
		if(!fromdt.equalsIgnoreCase("ALL") && todt.equalsIgnoreCase("ALL")) {
			todt = fromdt;
		}
		userDefinedWhere = " and 1=0";
		if (foundSearch) {
			MainSql = "select * from log_changes "
					+ " where log_actionby = "+userIdFromSearch+" and (date(log_action_timestamp)>='"+fromdt+"') and (date(log_action_timestamp)<='"+todt+"' )";
			userDefinedWhere = " and 1=1 order by log_id desc";
			
		}
	}
	
	@Override 
	public StringBuilder genListing() {
		search_paramval.remove("fromdt");
		search_paramval.remove("todt");
		return super.genListing();
	}
	
}
