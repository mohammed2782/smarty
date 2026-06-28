/* class description: used to setup menu,
 * created by: lina - smarty framework team member,
 * created date: 21/4/2018 6:28 PM.
 */
package com.app.setup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import smarty.core.CoreMgr;

public class setup_sub_menu extends CoreMgr{
	public setup_sub_menu(){
		/*
		 * to define main sql statement, key column and main table that do this operations
		 */
		MainSql =" select kbmenu_subtabs.* from kbmenu_subtabs join kbmenu_tabs on sm_menucode = mt_code where mt_id={mt_id} ";
		mainTable = "kbmenu_subtabs";
		keyCol ="sm_id";
		
		/*
		 * to define user gridviews caption
		 */
		userDefinedCaption = "إعدادات القائمة الفرعية";
		userDefinedNewCaption = "إضافة بيانات القائمة الفرعية";
		userDefinedEditCaption = "تعديل بيانات القائمة الفرعية";		
		
		/*
		 * to enable/disable basic operations 
		 */
		search_paramval = null;
		canNew =true;
		canDelete = true;
		canEdit = true;
		clickableRow =false;
		canFilter = false;
		
		/*
		 * to define gridview columns that want to show to user
		 */
		userDefinedGridCols.add("sm_id");
		userDefinedGridCols.add("sm_menucode");
		userDefinedGridCols.add("sm_submenu_name");
		userDefinedGridCols.add("sm_submenucode");
		userDefinedGridCols.add("sm_seq");
		userDefinedGridCols.add("sm_branches");
		
		/*
		 * to define gridview label that want to show to user
		 */
		userDefinedColLabel.put("sm_id", "رقم القائمة الفرعية");
		userDefinedColLabel.put("sm_menucode", "كود القائمة الرئيسية");
		userDefinedColLabel.put("sm_submenu_name", "إسم القائمة الفرعية");
		userDefinedColLabel.put("sm_submenucode", "كود القائمة الفرعية");
		userDefinedColLabel.put("sm_seq", "تسلسل القائمة الفرعية");
		userDefinedColLabel.put("sm_branches", "الفروع <input class='form-check-input' type='checkbox' id='all_branchs' name='sm_branches' value='ALL' onclick='checkUncheckAllBranches()'>");

		/*
		 * to define new columns for insert operation
		 */
		userDefinedNewCols.add("sm_menucode");
		userDefinedNewCols.add("sm_submenu_name");
		userDefinedNewCols.add("sm_submenucode");
		userDefinedNewCols.add("sm_seq");
		userDefinedNewCols.add("sm_branches");
		userDefinedReadOnlyNewCols.add("sm_menucode");
		userDefinedNewColsDefualtValues.put("sm_menucode",new String[] {"%select mt_code , mt_name from  kbmenu_tabs where mt_id={mt_id}"});		
				
		/*
		 * to define user must fill columns 
		 */
		userDefinedColsMustFill.add("sm_submenu_name");
		userDefinedColsMustFill.add("sm_submenucode");
		userDefinedColsMustFill.add("sm_seq");
		
		/*
		 * to define edit coulmns for update opeartion
		 */	
		userDefinedEditCols.add("sm_menucode");
		userDefinedEditCols.add("sm_submenu_name");
		userDefinedEditCols.add("sm_submenucode");
		userDefinedEditCols.add("sm_seq");	
		userDefinedEditCols.add("sm_branches");
		userDefinedReadOnlyEditCols.add("sm_menucode");
		
		userDefinedNewColsHtmlType.put("sm_branches", "CHECKBOX");
		userDefinedLookups.put("sm_branches", "SELECT branch_id,branch_name FROM `kbbranches` where branch_active='Y' union select 'ALL','كل الفروع' from dual "); //
		userDefinedEditLookups.put("sm_branches", "SELECT branch_id,branch_name FROM `kbbranches` where branch_active='Y' ");
		userDefinedNewLookups.put("sm_branches", "SELECT branch_id,branch_name FROM `kbbranches` where branch_active='Y' ");

		userDefinedUpdateCaption ="تعديل القائمة الفرعية";
	}//end of constructor setup_sub_menu	

	@Override
	public void initialize(HashMap smartyStateMap){
		Map<String, String[]> parameters = httpSRequest.getParameterMap();
		for(String parameter : parameters.keySet()) {
			if (!excludeKeyWords.contains(parameter)){
				if (parameter.equalsIgnoreCase("op") && parameters.get("op")!=null && parameters.get("op")[0].equalsIgnoreCase("upd") && parameters.get("sm_id")!=null) {
					PreparedStatement pst = null;
					ResultSet rs = null;
					try {
						pst = conn.prepareStatement("select sm_branches from kbmenu_subtabs where sm_id=?");
						pst.setString(1, parameters.get("sm_id")[0]);
						rs = pst.executeQuery();
						if(rs.next()) {
							userDefinedUpdateCaption = "<div class='col-md-12 col-sm-6 col-xs-6' style='margin-bottom: 30px;'>"+this.userDefinedUpdateCaption+"</div>"
									+ "<input type='hidden' class='input-text-global' id='sm_branches_hidden' value='"+rs.getString("sm_branches")+"'>";
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally {
						try {rs.close();} catch (Exception e) {}
						try {pst.close();} catch (Exception e) {}
					}
				}
			}
		}
		super.initialize(smartyStateMap);
	}
}//end of class setup_sub_menu

