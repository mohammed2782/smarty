package com.app.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.app.cases.CaseInformation;

public class IntegrationUtil {
	
	public void updateIntegrationSentCases(Connection conn, Map<String,String> cases, String sentToSystem)throws Exception{
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("update p_cases set c_sentto_system=?, c_sentto_caseid=? where c_id=?");
			for (String oriCaseId : cases.keySet()) {
				pst.setString(1, sentToSystem);
				pst.setString(2, cases.get(oriCaseId));
				pst.setString(3, oriCaseId);
				pst.executeUpdate();
				pst.clearParameters();
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {pst.close();}catch(Exception e) {}
		}
	}
	
	public String getIntegratingSystemBasedOnDlvAgent(Connection conn, String dlvAgent) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		String systemCode = "";
		try {
			pst = conn.prepareStatement("select is_code from kbintegrating_systems where is_dlvagent=?");
			pst.setString(1, dlvAgent);
			rs = pst.executeQuery();
			if (rs.next()) {
				systemCode = rs.getString("is_code");
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return systemCode;
	}
	
	public void updateRtnCasesInIntegratedSystems(Connection conn, LinkedList <Integer> cases ) throws Exception{
		HashMap<String, LinkedList<String>> originatingCasesMap = new HashMap<String,LinkedList<String>>();
		Utilities ut = new Utilities();
		String originatingCompanyCode = "";
		String orireceiptNo = "";
		try {
			for (int caseid : cases) {
				originatingCompanyCode = ut.getOriginationSystemCodeOfCases(conn, caseid);
				orireceiptNo = ut.getReceiptNoOri(conn, caseid+"");
				if (originatingCasesMap.containsKey(originatingCompanyCode)) {
					;
				}else {
					originatingCasesMap.put(originatingCompanyCode, new LinkedList<String>());
				}
				originatingCasesMap.get(originatingCompanyCode).add(orireceiptNo);
			}
			for (String originatingCompany : originatingCasesMap.keySet()) {
				System.out.println("ste 2 int");
				updateInOriginatingSystem(conn,"RTN", originatingCompany, originatingCasesMap.get(originatingCompany));
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void updateInOriginatingSystem (Connection conn, String status, String originatingSystem , LinkedList<String> receiptsNo) throws Exception{
		HttpURLConnection httpCon = null;
		OutputStream outputStream = null;
		IntegratingSystemBean integratingSystemBean = null;
		try { 
			integratingSystemBean = getIntegratingSystemInfo(conn, originatingSystem);
			if (integratingSystemBean.getUrl() !=null && integratingSystemBean.getUrl().trim().length()>0) {
				JSONArray array = new JSONArray();
				JSONObject dataObj = new JSONObject();
				int i =0;
				for (String receiptNo : receiptsNo){
					dataObj = new JSONObject();
					dataObj.put("custReceiptNoOri",receiptNo);
					array.put(i, dataObj);
					i++;
				}
				dataObj = new JSONObject();
				dataObj.put("casesList", array);
				dataObj.put("source", "PRIME");
				
				URL url = new URL(integratingSystemBean.getUrl()+"/updateRtnCases/"+integratingSystemBean.getToken());
				httpCon = (HttpURLConnection)url.openConnection();
				httpCon.setReadTimeout(10000);
				httpCon.setConnectTimeout(15000);
				httpCon.setUseCaches(false);
				httpCon.setDoOutput(true);
				httpCon.setDoInput(true);
				httpCon.setRequestProperty("Content-Type", "application/json");
				httpCon.setRequestProperty("Accept", "application/json");
				httpCon.setRequestMethod("POST");
			   //	System.out.println(dataObj.toString());
				byte[] sendBytes = dataObj.toString().getBytes("UTF-8");
				httpCon.setFixedLengthStreamingMode(sendBytes.length);
				outputStream = httpCon.getOutputStream();
				outputStream.write(sendBytes);
				int httpResponse = httpCon.getResponseCode();
				String jsonResponse;
				//System.out.println("httpResponse: " + httpResponse);
				if (  httpResponse >= HttpURLConnection.HTTP_OK  && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
					Scanner scanner = new Scanner(httpCon.getInputStream(), "UTF-8");
					jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
					scanner.close();
			   	}else {
			   		Scanner scanner = new Scanner(httpCon.getErrorStream(), "UTF-8");
			   		jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
			   		scanner.close();
			   	}
			}
			try {outputStream.close();}catch(Exception e) {}
			try{httpCon.disconnect();}catch(Exception e) {}
		}catch(Exception e) {
				e.printStackTrace();
				throw e;
		}finally {
			try{outputStream.close();}catch(Exception e) {}
		}
	}
	
	public IntegratingSystemBean getIntegratingSystemInfo(Connection conn, String systemCode) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		IntegratingSystemBean integratingSystemBean = new IntegratingSystemBean();
		try {
			pst = conn.prepareStatement("select is_connection_url , is_token_topushcases  from kbintegrating_systems where is_code=?");
			pst.setString(1, systemCode);
			rs = pst.executeQuery();
			if (rs.next()) {
				integratingSystemBean.setUrl(rs.getString("is_connection_url"));
				integratingSystemBean.setToken(rs.getString("is_token_topushcases"));
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return integratingSystemBean;
	}


	public ArrayList<CaseInformation> getCasesInformationList(Connection conn, String caseIdCommaSeperated) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		ArrayList<CaseInformation> caseInfoList = new ArrayList<CaseInformation>();
		try {
			pst = conn.prepareStatement("select "
					+ " p_cases.c_id as caseid, c_rcv_name  , c_rcv_hp1      , c_custhp      , c_custreceiptnoori,"
					+ " c_qty				  , c_rcv_state	, c_receiptamt  , c_rcv_addr_rmk, c_rmk			  , "
					+ " c_name				  , c_fragile"
					+ " from p_cases "
					+ " join kbcustomers on c_custid = kbcustomers.c_id where p_cases.c_id in ("+caseIdCommaSeperated+")");
			rs = pst.executeQuery();
			CaseInformation ci = new CaseInformation();
			while (rs.next()) {
				ci.setCustReceiptNoOri(rs.getString("c_custreceiptnoori"));
				ci.setCaseid(rs.getInt("caseid"));
				ci.setQty(rs.getInt("c_qty"));
				ci.setReceiptAmtIqd(rs.getDouble("c_receiptamt"));
				ci.setReceiverName(rs.getString("c_rcv_name"));
				ci.setReceiverHp1(rs.getString("c_rcv_hp1"));
				ci.setSenderHp(rs.getString("c_custhp"));
				ci.setSenderName(rs.getString("c_name"));
				ci.setState(rs.getString("c_rcv_state"));
				ci.setFragile(rs.getString("c_fragile"));
				//ci.setDistrict(rs.getString("c_rcv_district"));
				ci.setLocationDetails(rs.getString("c_rcv_addr_rmk"));
				ci.setRmk(rs.getString("c_rmk"));
				
				caseInfoList.add(ci);
				ci = new CaseInformation();
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return caseInfoList;
	}

	
	public HashMap<String,String> loadStatesMap(Connection conn, String systemCode) throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<String,String> stateMap = new HashMap<String,String>();
		try {
			pst = conn.prepareStatement("select intsc_mysystemcode, intsc_othersystemcode from kbintegration_statecode where intsc_syscode=?");
			pst.setString(1, systemCode);
			rs = pst.executeQuery();
			while(rs.next()) {
				stateMap.put(rs.getString("intsc_mysystemcode"), rs.getString("intsc_othersystemcode"));
			}
		}catch(Exception e) {
			throw e;
		}finally {
			try {rs.close();}catch(Exception e){}
			try {pst.close();}catch(Exception e){}
		}
		return stateMap;
	}
	
	public void updatePaidCasesInSource(Connection conn, ArrayList<CaseInformation> ciList, String pmtId, String pmtDate)  throws Exception {
		
		HttpURLConnection httpURLConnection = null;
		OutputStream outputStream = null;
		
		IntegratingSystemBean integratingSystemBean = new IntegratingSystemBean();
		integratingSystemBean = getIntegratingSystemInfo(conn, "FSM");
		System.out.println("here we go---------------------------");
		try {
			if (integratingSystemBean.getUrl()!=null &&  integratingSystemBean.getUrl().trim().length()>0) {

				JSONArray array = new JSONArray();
				JSONObject dataObj = new JSONObject();
				int i =0;
				for (CaseInformation ci: ciList) {
					dataObj = new JSONObject();
					dataObj.put("custReceiptNoOri",ci.getCustReceiptNoOri());
					dataObj.put("state",ci.getState());
					dataObj.put("receiptAmt",ci.getReceiptAmtIqd());
					array.put(i, dataObj);
					i++;
				}
				dataObj = new JSONObject();
				dataObj.put("pmtid",pmtId );
				dataObj.put("pmtdate",pmtDate );
				dataObj.put("source", "PRIME");
				dataObj.put("casesList",array);
				
				URL url = new URL(integratingSystemBean.getUrl()+"/updatePaidCases/"+integratingSystemBean.getToken());
				
				httpURLConnection = (HttpURLConnection)url.openConnection();
				httpURLConnection.setReadTimeout(10000);
				httpURLConnection.setConnectTimeout(15000);
				httpURLConnection.setUseCaches(false);
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				httpURLConnection.setInstanceFollowRedirects(true);
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setRequestProperty("Content-Type", "application/json");
				httpURLConnection.setRequestProperty("Accept", "application/json");
				byte[] sendBytes = dataObj.toString().getBytes("UTF-8");
				httpURLConnection.setFixedLengthStreamingMode(sendBytes.length);
				outputStream = httpURLConnection.getOutputStream();
				outputStream.write(sendBytes);
				int httpResponse = httpURLConnection.getResponseCode();
				String jsonResponse;
				System.out.println("httpResponse: " + httpResponse);
				if (httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
					Scanner scanner = new Scanner(httpURLConnection.getInputStream(), "UTF-8");
					jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
					
					scanner.close();
				}else {
					Scanner scanner = new Scanner(httpURLConnection.getErrorStream(), "UTF-8");
					jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
					scanner.close();
				}
				System.out.println("jsonResponse:\n" + jsonResponse);
			}
			
			try {outputStream.close();}catch(Exception e) {}
			try{httpURLConnection.disconnect();}catch(Exception e) {}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try{outputStream.close();}catch(Exception e) {}
		}
	}
}
