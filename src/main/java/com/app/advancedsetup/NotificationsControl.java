package com.app.advancedsetup;



import java.util.HashMap;

import smarty.core.CoreMgr;


public class NotificationsControl extends CoreMgr{
	public NotificationsControl() {
		MainSql = "select * from kbgeneral where kbcat1='NOTIFI_CONTROL'";
		//canNew = true;
		//canDelete = true;
		
		mainTable = "kbgeneral";
		keyCol = "kbid";
		
		userDefinedGridCols.add("kbcat1");
		userDefinedGridCols.add("kbcat2");
		userDefinedGridCols.add("kbcat3");
		userDefinedGridCols.add("kbcode");
		
		userDefinedColLabel.put("kbcat1", "Catigory1");
		userDefinedColLabel.put("kbcat2", "Catigory2");
		userDefinedColLabel.put("kbcat3", "Catigory3");
		userDefinedColLabel.put("kbcode", "Active");
		
		userDefinedNewCols.add("kbcat1");
		userDefinedNewCols.add("kbcat2");
		userDefinedNewCols.add("kbcat3");
		userDefinedNewCols.add("kbcode");
		userDefinedNewCols.add("kbdesc");
		
		userDefinedNewColsDefualtValues.put("kbcat1", new String []{"NOTIFI_CONTROL"});
		userDefinedNewColsDefualtValues.put("kbcat2", new String []{"CUSTOMER"});
		userDefinedNewColsDefualtValues.put("kbcode", new String []{"Y"});
		userDefinedNewColsDefualtValues.put("kbdesc", new String []{"Notification_Control"});
		
		
		userDefinedColsMustFill.add("kbcat1");
		userDefinedColsMustFill.add("kbcat2");
		userDefinedColsMustFill.add("kbcat3");
		userDefinedColsMustFill.add("kbcode");
		userDefinedColsMustFill.add("kbdesc");
		
		userDefinedEditCols.add("kbcode");
		
		userModifyTD.put("kbcode", "putSwitchButtonFlag({kbid},{kbcode},{kbcat3})");
		
		userDefinedNewCaption = "Add New Notification Control";
		userDefinedCaption = "Notification Control";
		
	}
	
	public String putSwitchButtonFlag(HashMap< String, String> hashy) {
		if(hashy.get("kbcode").equalsIgnoreCase("Y")){
			String switchButton ="<label class=\"switch\">"
					+ "	<input class=\"switch-input\" type=\"checkbox\" id = 'active_notification' "
					+ "onclick=\"activeNotificationControl(this, '"+hashy.get("kbid")+"', 'N', '"+hashy.get("kbcat3")+"');\" checked />"
					+ "	<span class=\"switch-label\" data-on=\"On\" data-off=\"Off\"></span> "
					+ "	<span class=\"switch-handle\"></span> "
					+ "</label>";
			return "<td>"+switchButton+"";
		}else {
			String switchButton ="<label class=\"switch\">"
					+ "	<input class=\"switch-input\" type=\"checkbox\" id = 'active_notification' "
					+ "onclick=\"activeNotificationControl(this, '"+hashy.get("kbid")+"', 'Y', '"+hashy.get("kbcat3")+"');\" />"
					+ "	<span class=\"switch-label\" data-on=\"On\" data-off=\"Off\"></span> "
					+ "	<span class=\"switch-handle\"></span> "
					+ "</label>";
			return "<td>"+switchButton+"";
		}
		
	}

}
