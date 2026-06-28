package com.app.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.app.beans.NotificationBean;

import smarty.db.mysql;



public class Notifications {
	
	public HashMap<Integer, ArrayList<String>> getOneSignalIdsPerUsers(ArrayList<Integer> usersList)throws Exception{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<Integer, ArrayList<String>> userIdOneSignalMap = new HashMap<Integer, ArrayList<String>>();
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select upod_playerid from kbusers_onesignalplayerid where upod_userid = ?");
			ArrayList<String> signalId = null;
			for(int userId : usersList) {
				signalId = new ArrayList<String>();
				pst.setInt(1, userId);
				rs = pst.executeQuery();
				while (rs.next()) {
					signalId.add(rs.getString("upod_playerid"));
				}
				try {rs.close();}catch(Exception e) {}
				pst.clearParameters();
				userIdOneSignalMap.put(userId, signalId);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		return userIdOneSignalMap;
	}
	
	public HashMap<Integer, ArrayList<String>> getDLVAgentOneSignalInfoToSendNotificationToPerCases(int caseId)throws Exception{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		int userId = 0;
		HashMap<Integer, ArrayList<String>> userIdOneSignalMap = new HashMap<Integer, ArrayList<String>>();
		try {
			conn = mysql.getConn();
			
			pst = conn.prepareStatement("select upod_playerid, upod_userid from kbusers_onesignalplayerid where upod_userid in (select c_assignedagent from p_cases where c_id =?)");
			ArrayList<String> signalId = new ArrayList<String>();
			pst.setInt(1, caseId);
			rs = pst.executeQuery();
			while (rs.next()) {
				signalId.add(rs.getString("upod_playerid"));
				userId = rs.getInt("upod_userid");
			}
			userIdOneSignalMap.put(userId, signalId);
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		return userIdOneSignalMap;
	}
	
	public HashMap<Integer, ArrayList<String>> getCustomerOneSignalInfoToSendNotificationToPerCases(int caseId)throws Exception{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		int custId = 0, masterCustId = 0;
		HashMap<Integer, ArrayList<String>> userIdOneSignalMap = new HashMap<Integer, ArrayList<String>>();
		ArrayList<Integer> usersList = new ArrayList<Integer>();
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select c_mastercustid, c_custid  from p_cases where c_id=?");
			pst.setInt(1, caseId);
			rs = pst.executeQuery();
			if (rs.next()) {
				masterCustId = rs.getInt("c_mastercustid");
				custId = rs.getInt("c_custid");
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			pst = conn.prepareStatement("select us_id from kbusers where us_mastercustid = ? "
					+ " and ( us_workingoncustomers like '%:"+custId+":%' "
					+ " or us_workingoncustomers like '"+custId+":%' or us_workingoncustomers is null) and us_rank not in ('DLVAGENT') ");
			pst.setInt(1, masterCustId);
			rs = pst.executeQuery();
			while(rs.next()) {
				usersList.add(rs.getInt("us_id"));
			}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			pst = conn.prepareStatement("select upod_playerid from kbusers_onesignalplayerid where upod_userid = ?");
			ArrayList<String> signalId = null;
			for(int userId : usersList) {
				signalId = new ArrayList<String>();
				pst.setInt(1, userId);
				rs = pst.executeQuery();
				while (rs.next()) {
					signalId.add(rs.getString("upod_playerid"));
				}
				try {rs.close();}catch(Exception e) {}
				pst.clearParameters();
				userIdOneSignalMap.put(userId, signalId);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		return userIdOneSignalMap;
	}
	
	public void sendNotificationToUser ( 
			HashMap<Integer, ArrayList<String>> usersIdOneSignalIdMap,
			  String aToWhichRankCode,
			  String aNotificationTitle,
			  String aNotificatioBody, 
			  String aType,
			  int aKeyVal,
			  HashMap<String,String> aExtraDataMap,
			  int a_branchId)throws Exception{
		//System.out.println("------------start sending-----------------------");
		Connection conn = null;
		PreparedStatement pst = null, pstUpdateSent = null;
		ResultSet rs = null;
		try {
			conn = mysql.getConn();
			
			String whichMobileApp = "CUSTOMER";
			String restAuth = "";
			String appId = "";
			if  (aToWhichRankCode.equalsIgnoreCase("DLVAGENT") ) {
				whichMobileApp = "AGENT";
				restAuth = GlobalVars.BRANCH_AGENT_WEBAPI_KEY.get(a_branchId);
				appId = GlobalVars.BRANCH_AGENT_APPID_KEY.get(a_branchId);
			}else if (aToWhichRankCode.equalsIgnoreCase("CUSTOMER") ) { 
				whichMobileApp = "CUSTOMER";
				restAuth = GlobalVars.BRANCH_CUSTOMER_WEBAPI_KEY.get(a_branchId);
				appId = GlobalVars.BRANCH_CUSTOMER_APPID_KEY.get(a_branchId);
				//System.out.println("restAuth--->"+restAuth);
				//System.out.println("appId--->"+appId);
			}
			// insert the notification
			pst = conn.prepareStatement("insert into p_notification"
				+ "(not_userid, not_type, not_keycolval, not_title, not_desc, not_whichmobileapp) "
				+ "values(?		  , ?	    , ?			   , ?	   	  , ?		, ?)" , Statement.RETURN_GENERATED_KEYS);
			pstUpdateSent = conn.prepareStatement("update p_notification set not_sent_to_onesignal = 'Y' where not_id = ?");
			int notificationId = 0;
			for (int userId : usersIdOneSignalIdMap.keySet()) {
				pst.setInt(1, userId);
				pst.setString(2, aType);
				pst.setInt(3, aKeyVal);
				pst.setString(4, aNotificationTitle);
				pst.setString(5, aNotificatioBody);
				pst.setString(6, whichMobileApp);
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				rs.next();
				notificationId = rs.getInt(1);
				try {rs.close();}catch(Exception e) {}
				pst.clearParameters();
				try {
					sendSingleUserNotification (restAuth, appId, usersIdOneSignalIdMap.get(userId), aNotificationTitle, aNotificatioBody,aExtraDataMap, notificationId);
					pstUpdateSent.setInt(1, notificationId);
					pstUpdateSent.executeUpdate();
					pst.clearParameters();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			conn.commit();
		} catch (Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {}
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
	}


	public void sendSingleUserNotification(String aRestAPIAuthorization, 
				String aAppID, 
				ArrayList<String> aUserOneSignalIds, 
				String aTitle,
				String aMsg, 
				HashMap<String,String> aUserExtraDataMap,
				int aNotificationId) throws Exception{
		try {
			String jsonResponse;
			Utilities ut = new Utilities();
			URL url = new URL("https://onesignal.com/api/v1/notifications");
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setUseCaches(false);
			con.setDoOutput(true);
			con.setDoInput(true);
			//lang = lang.toLowerCase();
			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestProperty("Authorization", aRestAPIAuthorization);
			con.setRequestMethod("POST");
			
			StringBuilder extraData = new StringBuilder();
			extraData.append("\"notificationid\":\""+aNotificationId+"\"");
			for (String key :aUserExtraDataMap.keySet()) {
				extraData.append(", \""+key+"\" : \""+aUserExtraDataMap.get(key)+"\"");
			}

			String strJsonBody = "{"
			  +   "\"app_id\": \""+aAppID+"\","
			  +   "\"include_player_ids\": ["+ut.getDoubleQuoteCommaSeperated(aUserOneSignalIds)+"],"
			  +   "\"data\": {"+extraData.toString()+"},"
			  +   "\"headings\": {\"en\": \""+aTitle+"\"},"
			  +   "\"contents\": {\"en\": \""+aMsg+"\"},"
			  +   "\"priority\": 100,"
			  +   "\"android_sound\": \"notification\""
			  + "}";		
			//System.out.println("strJsonBody:\n" + strJsonBody);
			byte[] sendBytes = strJsonBody.getBytes("UTF-8");
			con.setFixedLengthStreamingMode(sendBytes.length);
			OutputStream outputStream = con.getOutputStream();
			outputStream.write(sendBytes);
			int httpResponse = con.getResponseCode();
			if (  httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
				Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
				jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
				scanner.close();
			}else {
				Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
				jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
				scanner.close();
			}
				/* if (httpResponse != 200)
				throw new Exception("Can't send to one signal");*/
				//System.out.println("jsonResponse:\n" + jsonResponse);
				
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	
	
	/*public boolean shouldSendNotificationToCustomer(Connection conn, String stageCode, String stepCode, String decision) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select stpd_sendnotification from kbstep_decision where "
					+ " stpd_stpid in (select stp_id from kbstep where stp_stgcode=? and stp_code=? ) and stpd_code= ?");
			pst.setString(1, stageCode);
			pst.setString(2, stepCode);
			pst.setString(3, decision);
			rs = pst.executeQuery();
			if(rs.next()) {
				if (rs.getString("stpd_sendnotification").equalsIgnoreCase("Y"))
					return true;
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw new AccessServiceException(0);
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		return false;
	}
	*/
	public  ArrayList<NotificationBean> GetListNotifications (int aUserId, int aStart, int aOffSet)throws Exception{
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilitiesNafie utn = new UtilitiesNafie();
		ArrayList<NotificationBean> notificationsList= new ArrayList<NotificationBean>();
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select not_id, not_type, not_keycolval, not_title, not_desc, "
					+ " not_datetime, timestampdiff(MINUTE,not_datetime ,now()) as timeDiff, "
					+ " not_user_clicked from p_notification where not_userid = ?  order by not_id desc limit ?, ?");
			pst.setInt(1, aUserId);
			pst.setInt(2, aStart);
			pst.setInt(3, aOffSet);
			rs = pst.executeQuery();
			NotificationBean notificationBean = null;
			String timeAgo;
			while(rs.next()) {
				timeAgo = null;
				notificationBean = new NotificationBean();
				notificationBean.setId(rs.getInt("not_id"));
				notificationBean.setType(rs.getString("not_type"));
				notificationBean.setTypeVal(rs.getInt("not_keycolval"));
				notificationBean.setTitle(rs.getString("not_title"));
				notificationBean.setDesc(rs.getString("not_desc"));
				// date time
				timeAgo = utn.getTimeAgo(rs.getLong("timeDiff"));
				if (timeAgo !=null) {
					notificationBean.setDateTime(timeAgo);
				}else {
					notificationBean.setDateTime(rs.getString("not_datetime"));
				}
				notificationBean.setUserClicked(rs.getString("not_user_clicked"));
				notificationsList.add(notificationBean);
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		return notificationsList;
	}
	
	public void FlagOpenedNotification (int notificationId) throws Exception{
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("update p_notification set not_user_clicked='Y' where not_id=? ");
			pst.setInt(1, notificationId);
			pst.executeUpdate();
			conn.commit();
		}catch(Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {}
			e.printStackTrace();
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
	}
	
	public void FlagUnseenNotificationAsSeen (int userId) throws Exception{
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("update p_notification set not_user_saw='Y' where not_userid=? and not_user_saw='N'");
			pst.setInt(1, userId);
			pst.executeUpdate();
			conn.commit();
		}catch(Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {}
			e.printStackTrace();
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
	}
	
	public int getUnseenNotifications (int userId) throws Exception{
		int cnt = 0;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = mysql.getConn();
			pst = conn.prepareStatement("select count(*) from p_notification where not_userid=? and not_user_saw='N'");
			pst.setInt(1, userId);
			rs = pst.executeQuery();
			if(rs.next()) {
				cnt = rs.getInt(1);
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		return cnt;
	}
	
	
	
	/*public void sendNotificationToUser ( 
								  ArrayList<Integer> aUsersList,
								  String aRankCode,
								  String aTitle,
								  String aMsg, 
								  String aType,
								  int aKeyVal,
								  HashMap<Integer,  HashMap<String,String>> aUserExtraDataMap)throws Exception{
		UtilitiesNafie utn = new UtilitiesNafie();
		HashMap<Integer, ArrayList<String>> usersIdOneSignalIdMap = new HashMap<Integer,  ArrayList<String>>(); 
		
		Connection conn = null;
		PreparedStatement pst = null, pstUpdateSent = null;
		ResultSet rs = null;
		try {
			conn = mysql.getConn();
			String whichMobileApp = "CUSTOMER";
			String restAuth = GlobalVars.mbCustRestAuthorization;
			String appId = GlobalVars.mbCustAppId;
			
			if  (aRankCode.equalsIgnoreCase("DLVAGENT") ) {
				whichMobileApp = "AGENT";
				restAuth = GlobalVars.mbagentRestAuthorization;
				appId = GlobalVars.mbagentAppId;
			}
			for (int userId : aUsersList) {
				usersIdOneSignalIdMap.put(userId, utn.getSingleUserPlayerIds (conn, userId ));
			}
			// insert the notification
			pst = conn.prepareStatement("insert into p_notification"
					+ "(not_userid, not_type, not_keycolval, not_title, not_desc, not_whichmobileapp) "
			  + "values(?		  , ?	    , ?			   , ?	   	  , ?		, ?)" , Statement.RETURN_GENERATED_KEYS);
			pstUpdateSent = conn.prepareStatement("update p_notification set not_sent_to_onesignal = 'Y' where not_id = ?");
			int notificationId = 0;
			for (int userId : usersIdOneSignalIdMap.keySet()) {
				
				pst.setInt(1, userId);
				pst.setString(2, aType);
				pst.setInt(3, aKeyVal);
				pst.setString(4, aTitle);
				pst.setString(5, aMsg);
				pst.setString(6, whichMobileApp);
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				rs.next();
				notificationId = rs.getInt(1);
				try {rs.close();}catch(Exception e) {}
				pst.clearParameters();
				try {
					sendSingleUserNotification (restAuth, appId, usersIdOneSignalIdMap.get(userId), aTitle, aMsg, aUserExtraDataMap.get(userId), notificationId);
					pstUpdateSent.setInt(1, notificationId);
					pstUpdateSent.executeUpdate();
					pst.clearParameters();
				}catch(Exception e) {
					mean one signal couldnot send
				}
			}
			conn.commit();
		} catch (Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {}
			e.printStackTrace();
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
	}
	
	
	public void sendSingleUserNotification(String aRestAPIAuthorization, 
			  String aAppID, 
			  ArrayList<String> aUserOneSignalIds, 
			  String aTitle,
			  String aMsg, 
			  HashMap<String,String> aUserExtraDataMap,
			  int aNotificationId) throws Exception{
		try {
			String jsonResponse;
			Utilities ut = new Utilities();
			URL url = new URL("https://onesignal.com/api/v1/notifications");
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setUseCaches(false);
			con.setDoOutput(true);
			con.setDoInput(true);
			//lang = lang.toLowerCase();
			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestProperty("Authorization", aRestAPIAuthorization);
			con.setRequestMethod("POST");
			
			StringBuilder extraData = new StringBuilder();
			 extraData.append("\"notificationid\":\""+aNotificationId+"\"");
			 for (String key :aUserExtraDataMap.keySet()) {
				   extraData.append(", \""+key+"\" : \""+aUserExtraDataMap.get(key)+"\"");
			 }
			
			System.out.println("receives-----------------------------"+aUserOneSignalId);
			System.out.println("extraData-----------------------------"+extraData);
			String strJsonBody = "{"
			            +   "\"app_id\": \""+aAppID+"\","
			            +   "\"include_player_ids\": ["+ut.getDoubleQuoteCommaSeperated(aUserOneSignalIds)+"],"
			            +   "\"data\": {"+extraData.toString()+"},"
			            +   "\"headings\": {\"en\": \""+aTitle+"\"},"
			            +   "\"contents\": {\"en\": \""+aMsg+"\"},"
			            +   "\"priority\": 100,"
			            +   "\"android_sound\": \"notification\""
			            + "}";
			
			
			//System.out.println("strJsonBody:\n" + strJsonBody);
			
			byte[] sendBytes = strJsonBody.getBytes("UTF-8");
			con.setFixedLengthStreamingMode(sendBytes.length);
			
			OutputStream outputStream = con.getOutputStream();
			outputStream.write(sendBytes);
			
			int httpResponse = con.getResponseCode();
			
			
			if (  httpResponse >= HttpURLConnection.HTTP_OK
					&& httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
				Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
				jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
				scanner.close();
			}else {
				Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
				jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
				scanner.close();
			}
			 if (httpResponse != 200)
			throw new Exception("Can't send to one signal");
			//System.out.println("jsonResponse:\n" + jsonResponse);
			
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	public void sendBatchNotifications(String aRestAPIAuthorization, 
								  String aAppID, 
								  HashMap<Integer, String> aUsersIdOneSignalIdMap, 
								  String aMsg, 
								  String aTitle,
								  HashMap<Integer,  HashMap<String,String>> aUserExtraDataMap,
								  HashMap<Integer, Integer> aNotificationIdOneSignalIdMap) throws Exception{
		try {
			   String jsonResponse;
			   URL url = new URL("https://onesignal.com/api/v1/notifications");
			   HttpURLConnection con = (HttpURLConnection)url.openConnection();
			   con.setUseCaches(false);
			   con.setDoOutput(true);
			   con.setDoInput(true);
			   //lang = lang.toLowerCase();
			   con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			   con.setRequestProperty("Authorization", aRestAPIAuthorization);
			   con.setRequestMethod("POST");
			   StringBuilder receives = new StringBuilder();
			   StringBuilder extraData = new StringBuilder();
			   boolean firstReceiver = true;
			   HashMap <String, String> extraKeyVal;
			   for (int userId : aUsersIdOneSignalIdMap.keySet()) {
				   if (!firstReceiver) {
					   receives.append(", ");
					   extraData.append(", ");
					 }
				   receives.append("\""+aUsersIdOneSignalIdMap.get(userId)+"\"");
				   extraData.append("\"notificationid\":\""+aNotificationIdOneSignalIdMap.get(userId)+"\"");
				   if (aUserExtraDataMap!=null && aUserExtraDataMap.containsKey(userId) && aUserExtraDataMap.get(userId)!=null) {
					   extraKeyVal =  aUserExtraDataMap.get(userId);
					   for (String key :extraKeyVal.keySet()) {
						   extraData.append(", \""+key+"\" : \""+extraKeyVal.get(key)+"\"");
					   }
				   }
				   
				   firstReceiver = false;
			   }
			   //System.out.println("receives-----------------------------"+receives.toString());
			   //System.out.println("extraData-----------------------------"+extraData);
			   String strJsonBody = "{"
			                      +   "\"app_id\": \""+aAppID+"\","
			                      +   "\"include_player_ids\": ["+receives.toString()+"],"
			                      +   "\"data\": {"+extraData.toString()+"},"
			                      +   "\"headings\": {\"en\": \""+aTitle+"\"},"
			                      +   "\"contents\": {\"en\": \""+aMsg+"\"}"
			                      + "}";
			         
			   
			   //System.out.println("strJsonBody:\n" + strJsonBody);

			   byte[] sendBytes = strJsonBody.getBytes("UTF-8");
			   con.setFixedLengthStreamingMode(sendBytes.length);

			   OutputStream outputStream = con.getOutputStream();
			   outputStream.write(sendBytes);

			   int httpResponse = con.getResponseCode();
			  

			   if (  httpResponse >= HttpURLConnection.HTTP_OK
			      && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
			      Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
			      jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
			      scanner.close();
			   }
			   else {
			      Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
			      jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
			      scanner.close();
			   }
			   if (httpResponse != 200)
				   throw new Exception("Can't send to one signal");
			  // System.out.println("jsonResponse:\n" + jsonResponse);
			   
			} catch(Exception e) {
			   e.printStackTrace();
			   throw e;
			}
	}*/
}
