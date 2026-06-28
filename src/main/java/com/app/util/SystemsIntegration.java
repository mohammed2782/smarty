package com.app.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.app.cases.CaseInformation;

public class SystemsIntegration  {
	
	public SystemsIntegration (String sysCode) {
		this.systemCode = sysCode;
	}
	private String systemCode;
	
	public void updatePaidCasesInSource(Connection conn, ArrayList<CaseInformation> ciList, String pmtId, String pmtDate)  throws Exception {
		IntegrationUtil iu = new IntegrationUtil();
		try {
			iu.updatePaidCasesInSource(conn, ciList, pmtId, pmtDate);
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			
		}
	}
	
	public Map<String, String> pushCases(Connection conn, ArrayList<String> cases) throws Exception {
		Map<String, String> hMapData = new HashMap<String, String>();
		HttpURLConnection con = null;
		OutputStream outputStream = null;
		Utilities ut = new Utilities();
		IntegrationUtil iu = new IntegrationUtil();
		IntegratingSystemBean integratingSystemBean = new IntegratingSystemBean();
		HashMap<String,String> stateMap;
		try { 
			integratingSystemBean = iu.getIntegratingSystemInfo(conn, systemCode);
			if (integratingSystemBean.getUrl()!=null &&  integratingSystemBean.getUrl().trim().length()>0) {
				stateMap = iu.loadStatesMap(conn, systemCode);
				String casesCommaSeperated = ut.getSingleQuoteCommaSeperated(cases).toString();
				ArrayList<CaseInformation> ciList = iu.getCasesInformationList(conn, casesCommaSeperated);
				JSONArray array = new JSONArray();
				JSONObject dataObj = new JSONObject();
				int i =0;
				
				for (CaseInformation ci: ciList) {
					dataObj = new JSONObject();
					dataObj.put("custReceiptNoOri",ci.getCustReceiptNoOri());
					dataObj.put("senderSystemCaseId",ci.getCaseid()) ;
					dataObj.put("qty",ci.getQty()) ;
					dataObj.put("receiptAmt",ci.getReceiptAmtIqd()) ;
					dataObj.put("receiverName",ci.getReceiverName()) ;
					dataObj.put("receiverHp1",ci.getReceiverHp1()) ;
					dataObj.put("senderHp",ci.getSenderHp()) ;
					dataObj.put("senderName",ci.getSenderName()) ;
					dataObj.put("state",stateMap.get(ci.getState())) ;
					dataObj.put("rmk",ci.getRmk()) ;
					dataObj.put("fragile", ci.getFragile());
					dataObj.put("locationDetails", ci.getLocationDetails());
					array.put(i, dataObj);
					i++;
				}
				
				//http://localhost:8080/tlmbapp/webapi/IntegrationWs
				//URL url = new URL("https://tl.transporter-iq.com/tlmbapp/webapi/IntegrationWs/createCases/asdhfgkj0786t543gsewwxq342");
				URL url = new URL(integratingSystemBean.getUrl()+"/"+integratingSystemBean.getToken());
				
				con = (HttpURLConnection)url.openConnection();
				con.setReadTimeout(10000);
				con.setConnectTimeout(15000);
				con.setUseCaches(false);
				con.setDoOutput(true);
				con.setDoInput(true);
				con.setInstanceFollowRedirects(true);
			   	con.setRequestProperty("Content-Type", "application/json");
			   	con.setRequestProperty("Accept", "application/json");
			   	con.setRequestMethod("POST");
			   
				byte[] sendBytes = array.toString().getBytes("UTF-8");
				con.setFixedLengthStreamingMode(sendBytes.length);
				outputStream = con.getOutputStream();
				outputStream.write(sendBytes);
				int httpResponse = con.getResponseCode();
				String jsonResponse;
				
				
				if (httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
					Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
					jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
					scanner.close();
					// now get back the data
					jsonResponse = jsonResponse.replace("}", "").replace("{", "").replace("\"", "");
					String parts[] = jsonResponse.split(",");
					
					for(String part : parts){
			            //split the returned data by : to get original caseid and receiver caseid
			            String singleCase[] = part.split(":");
			            String senderCaseId = singleCase[0].trim();
			            String receiverCaseid = singleCase[1].trim();
			            //add to map
			            hMapData.put(senderCaseId, receiverCaseid);
			        }
				}else {
					Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
					jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
					scanner.close();
				}
				
			}
			
			try {outputStream.close();}catch(Exception e) {}
			try{con.disconnect();}catch(Exception e) {}
		}catch(Exception e) {
				e.printStackTrace();
				throw e;
		}finally {
			try{outputStream.close();}catch(Exception e) {}
		}
		return hMapData;
	}
	public String getSystemCode() {
		return systemCode;
	}
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
	
}
