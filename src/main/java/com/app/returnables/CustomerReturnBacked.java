package com.app.returnables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import smarty.core.CoreMgr;
import smarty.db.mysql;

import com.app.util.Utilities;

public class CustomerReturnBacked extends CoreMgr{
		public CustomerReturnBacked() {
			MainSql = "select '' as showdel, acr_id,acr_mastercustid, acr_createdby, acr_integrationsync , acr_rmk, '' as fake,"
					+ " acr_createddt, acr_closedby, acr_closed, acr_closeddate "
					+ " from p_customer_return where acr_mastercustid={custAccountReturnProcess} "
					+ " and acr_deleted='N' "
					+ " order by acr_id desc ";
			
			keyCol = "acr_id";
			mainTable = "p_customer_return";
			
			//myhtmlmgr.tableClass = "table table-striped  table-bordered turquoise_table";

			// ///////////////
			userDefinedGridCols.add("acr_id");
			
			userDefinedGridCols.add("acr_createddt");
			userDefinedGridCols.add("acr_createdby");
			userDefinedGridCols.add("acr_rmk");
			userDefinedGridCols.add("fake");
			userDefinedGridCols.add("acr_closed");
			userDefinedGridCols.add("acr_integrationsync");
			userDefinedGridCols.add("showdel");

			// //////////////
			userDefinedCaption = "الرواجع للزبائن";
			userDefinedColLabel.put("acr_id", "رقم الأيصال");
			userDefinedColLabel.put("acr_createddt", "تاريخ الاستلام الفعلي");
			userDefinedColLabel.put("acr_rmk", " ملاحظات");
			userDefinedColLabel.put("acr_createdby", "أنشئ بواسطة ");
			userDefinedColLabel.put("fake", "طباعة ايصال الاستلام ");
			userDefinedColLabel.put("showdel", " ");
			userDefinedColLabel.put("acr_integrationsync", " ");
			
			//canDelete = true;
			userModifyTD.put("fake", "printPmtReceipt({acr_id},{userbranch})");
			userModifyTD.put("showdel", "showDel({acr_id} , {acr_integrationsync})");
			userModifyTD.put("acr_integrationsync", "doSync({acr_id}, {acr_integrationsync})");
			userModifyTD.put("acr_closed", "closeRtnManifest({acr_id},{acr_closed})");
			//userModifyTD.put("receivedamount", "receivedAmount({acr_id})");
			
			userDefinedLookups.put("acr_createdby", "select us_id, us_name from kbusers");
			
			myhtmlmgr.refreshPageOnDelete = true;

		}// end of constructor customer_payment
		public String showDel (HashMap<String,String> hashy) {
			if (hashy.get("acr_integrationsync").equalsIgnoreCase("Y")) {
				return "<td></td>";
			}else {
				return "<td align='center' style='vertical-align: middle;'>"
				+"<button id='pmt_del_btn_"+hashy.get("acr_id")+"' type='button' "
				+ " onclick=\"link=false; "
				+ " var rs =doDeleteSmarty(this,'هل تريد حذف الراجع  المستلم ؟' ,'acr_id','"+hashy.get("acr_id")+"',"
						+ " 'com.app.returnables.CustomerReturnBacked' ); return rs;\" class='btn btn-danger btn-xs'>"
				+ "<li class='fa fa-trash'></li></button></td>";
			}
		}
		
		public String closeRtnManifest(HashMap<String, String> hashy) {
			String html ="";
			if (hashy.get("acr_closed").equalsIgnoreCase("N")) {
				html ="<button type=\"button\" id='btn-close-rtn-"+hashy.get("acr_id")+"' class=\"btn btn-dark px-5\" "
						+ "onclick=\"closeManifestRtnBtn('"+hashy.get("acr_id")+"');\">أغلاق المنفيست وتسليم نهائي</button>";
			}else {
				html = "<span class=\"badge rounded-pill bg-secondary\">مغلق</span>";
			}
			return "<td id='td-close-rtn-"+hashy.get("acr_id")+"'>" + html + "</td>";
		}
		
		public String doSync(HashMap<String, String> hashy) {
			String html ="<button type=\"button\" class=\"btn btn-outline-info px-5 radius-30\" "
					+ "onclick=\"integrateRtnBtn('"+hashy.get("acr_id")+"');\">مزامنة الراجع مع نظام العميل</button>";
			
			return "<td>" + html + "</td>";
		}
		
		public String printPmtReceipt(HashMap<String, String> hashy) {
			String userbranch = replaceVarsinString(" {userstorecode} ", arrayGlobals).trim();
			String btn = "<a href=\"../../CustomerBackedReturnSRVL?regulator=fromcustomer&acr_id="+hashy.get("acr_id")+"&userbranch="+userbranch+"\" "
					+ " class='btn btn-xs btn-warning' >طباعة أيصال الراجع <i class=\"fa fa-print fa-lg\"></i></a>";
			return "<td>" + btn + "</td>";
		}

		
		@Override
		public String doDelete(HttpServletRequest rqs) {
			String keyVal = rqs.getParameter("acr_id");
			PreparedStatement pst = null;
			ResultSet rs = null;
			int userId = Integer.parseInt(replaceVarsinString(" {userid} ", arrayGlobals).trim());
			try {
				boolean allowDelete = false;
				// first check if the integration had been synced
				pst = conn.prepareStatement("select acr_integrationsync from p_customer_return where acr_id=?");
				pst.setString(1, keyVal);
				rs = pst.executeQuery();
				if(rs.next()) {
					if (rs.getString("acr_integrationsync").equalsIgnoreCase("N"))
						allowDelete = true;
				}
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
				
				if (allowDelete) {
					pst = conn.prepareStatement("update p_customer_return set acr_deleted='Y', acr_deleteddt=now(), acr_deletedby=? "
							+ "where acr_id=? ");
					pst.setInt(1, userId);
					pst.setString(2, keyVal);
					pst.executeUpdate();
					try {pst.close();} catch (Exception e) {}
					
					// back up the cases
					pst = conn.prepareStatement("insert into del_customer_return_cases (dcrc_rtnid, dcrc_caseid) "
							+ " select c_cust_rtnid,  c_id from p_cases where c_cust_rtnid=?");
					pst.setString(1, keyVal);
					pst.executeUpdate();
					try {pst.close();} catch (Exception e) {}

					// now re-initialize
					pst = conn.prepareStatement("update p_cases set c_cust_rtnid=0 where c_cust_rtnid=?");
					pst.setString(1, keyVal);
					pst.executeUpdate();
				}
				
				conn.commit();

			} catch (Exception e) {
				try {conn.rollback();} catch (Exception eRollBack) {/*ignore*/}
				logErrorMsg = "";
				e.printStackTrace();
			} finally {
				try {rs.close();} catch (Exception e) {}
				try {pst.close();} catch (Exception e) {}
			}

			return "";
		}// end of doDelete*/

	}
