package com.app.returnables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.app.bussframework.FlowUtils;
import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;

public class pickupAgentReturnBacked extends CoreMgr{
		public pickupAgentReturnBacked() {
			MainSql = "select '' as showdel, pir_id,pir_pickupagentid, pir_createdby, pir_retuneddt, pir_rmk, '' as fake,'{userstorecode}' as userbranch, "
					+ " (pir_createddt) as pir_createddt, pir_integrationsync, pir_closed from p_pickupagent_return "
					+ " where pir_pickupagentid={pickupAgentRtnShipments} "
					+ " and pir_deleted='N' "
					+ " order by pir_id desc ";
			
			keyCol = "pir_id";
			mainTable = "p_pickupagent_return";
			
			// ///////////////
			userDefinedGridCols.add("pir_id");
			userDefinedGridCols.add("pir_retuneddt");
			userDefinedGridCols.add("pir_createddt");
			userDefinedGridCols.add("pir_createdby");
			userDefinedGridCols.add("pir_rmk");
			userDefinedGridCols.add("fake");
			userDefinedGridCols.add("pir_closed");
			userDefinedGridCols.add("pir_integrationsync");
			userDefinedGridCols.add("showdel");

			// //////////////
			userDefinedCaption = "الرواجع لمندوبي التسليم";
			userDefinedColLabel.put("pir_id", "رقم الأيصال");
			userDefinedColLabel.put("pir_retuneddt", "تاريخ التسليم");
			userDefinedColLabel.put("pir_createddt", "تاريخ التسليم الفعلي");
			userDefinedColLabel.put("pir_rmk", " ملاحظات");
			userDefinedColLabel.put("pir_createdby", "أنشئ بواسطة ");
			userDefinedColLabel.put("fake", "طباعة ايصال الاستلام ");
			userDefinedColLabel.put("showdel", " ");
			userDefinedColLabel.put("pir_integrationsync", " ");
			userDefinedColLabel.put("pir_closed", " ");
			
			//canDelete = true;
			userModifyTD.put("fake", "printPmtReceipt({pir_id},{userbranch})");
			userModifyTD.put("showdel", "showDel({pir_id}, {pir_integrationsync})");
			userModifyTD.put("pir_integrationsync", "doSync({pir_id}, {pir_integrationsync})");
			userModifyTD.put("pir_closed", "closeRtnManifest({pir_id},{pir_closed})");
			myhtmlmgr.refreshPageOnDelete = true;

		}// end of constructor customer_payment
		
		
		public String closeRtnManifest(HashMap<String, String> hashy) {
			String html ="";
			if (hashy.get("pir_closed").equalsIgnoreCase("N")) {
				html ="<button type=\"button\" id='btn-close-rtn-"+hashy.get("pir_id")+"' class=\"btn btn-dark px-5\" "
						+ "onclick=\"closeManifestRtnBtn('"+hashy.get("pir_id")+"');\">أغلاق المنفيست وتسليم نهائي</button>";
			}else {
				html = "<span class=\"badge rounded-pill bg-secondary\">مغلق</span>";
			}
			return "<td id='td-close-rtn-"+hashy.get("pir_id")+"'>" + html + "</td>";
		}
		
		public String showDel (HashMap<String,String> hashy) {
			if (hashy.get("pir_integrationsync").equalsIgnoreCase("Y")) {
				return "<td></td>";
			}else {
				return "<td align='center' style='vertical-align: middle;'>"
				+"<button id='pmt_del_btn_"+hashy.get("pir_id")+"' type='button' "
				+ " onclick=\"link=false; "
				+ " var rs =doDeleteSmarty(this,'هل تريد حذف الراجع  المستلم ؟' ,'pir_id','"+hashy.get("pir_id")+"',"
						+ " 'com.app.returnables.pickupAgentReturnBacked' ); return rs;\" class='btn btn-danger btn-xs'>"
				+ "<li class='fa fa-trash'></li></button></td>";
			}
		}
		
		public String doSync(HashMap<String, String> hashy) {
			String html ="<button type=\"button\" class=\"btn btn-outline-info px-5 radius-30\" "
					+ "onclick=\"integrateRtnBtn('"+hashy.get("pir_id")+"');\">مزامنة الراجع مع نظام العميل</button>";
			
			return "<td>" + html + "</td>";
		}
		
		public String printPmtReceipt(HashMap<String, String> hashy) {
			String userbranch = replaceVarsinString(" {userstorecode} ", arrayGlobals).trim();
			String btn = "<a href=\"../../CustomerBackedReturnSRVL?regulator=frompickup&pir_id="+hashy.get("pir_id")+"&userbranch="+userbranch+"\" "
					+ " class='btn btn-xs btn-warning' >طباعة أيصال الراجع <i class=\"fa fa-print fa-lg\"></i></a>";
			return "<td>" + btn + "</td>";
		}
		
		@Override
		public String doDelete(HttpServletRequest rqs) {
			String keyVal = rqs.getParameter("pir_id");
			PreparedStatement pst = null;
			String userLoginId = replaceVarsinString(" {useridlogin} ", arrayGlobals).trim();
			try {
		
				pst = conn.prepareStatement("update p_pickupagent_return set pir_deleted='Y', pir_deleteddt=now(), pir_deletedby=? where pir_id=? ");
				pst.setString(1, userLoginId);
				pst.setString(2, keyVal);
				pst.executeUpdate();
				try {pst.close();} catch (Exception e) {}
				
				pst = conn.prepareStatement("update p_cases set  c_pickupagent_rtnid=0  "
						+ "where c_pickupagent_rtnid=?");
				pst.setString(1, keyVal);
				pst.executeUpdate();
				
				conn.commit();

			} catch (Exception e) {
				try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
				logErrorMsg = "";
				e.printStackTrace();
			} finally {
				try {pst.close();} catch (Exception e) {}
			}

			return "";
		}// end of doDelete*/

	}
