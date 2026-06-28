package com.app.servlets;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.app.cases.CaseInformation;
import com.app.util.UtilitiesNafie;

import smarty.db.mysql;
import smarty.security.LoginUser;
/**
 * Servlet implementation class SearchReceiptInfoSRVL
 */
@WebServlet("/SearchReceiptInfoSRVL")
public class SearchReceiptInfoSRVL extends HttpServlet {
	// this map , maps the db cols to the search params received from servlet to protect it against sql injection
	private HashMap<String,String> searchParamsToDbCols = new HashMap<String,String>(){
		private static final long serialVersionUID = 438661656312765293L;
		{//c_custid, c_mastercustid, c_pickupagent, c_pickupagent_rtnid , c_cust_rtnid
			put("masterCustId","c_mastercustid"); // master customer id
			put("custId","c_custid");			  // customer id
			put("custRtnId","c_cust_rtnid");	  // customer return id
			put("pickUpAgentid","c_pickupagent"); // pickup agent id
			put("pickUpAgentRtnId","c_pickupagent_rtnid");// pickupagent rtn id
			put("receiptNo","c_custreceiptnoori");        // receipt no
			put("caseid" , "c_id");
		}
	};
		 
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchReceiptInfoSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
		HashMap<String, String> actualSearchMap = new HashMap<String, String>();
		for (String key : request.getParameterMap().keySet()) {
			if (searchParamsToDbCols.containsKey(key)) {
				actualSearchMap.put(searchParamsToDbCols.get(key), request.getParameter(key));
			}
		}
		
		
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control","no-cache");
		UtilitiesNafie utn = new UtilitiesNafie ();
		
		JSONArray array = new JSONArray();
		JSONObject dataObj = new JSONObject();
		
		Writer out = response.getWriter();
		Connection conn = null;
		ArrayList<CaseInformation> ciList = new ArrayList<CaseInformation>();
		try {
			
			conn = mysql.getConn();
			ciList = utn.getCasesAdvancedSearch(conn, actualSearchMap);
			int i=0;
			for (CaseInformation ci : ciList) {
				dataObj.put("originatingBranchCode", ci.getOrigintingBranch());
				dataObj.put("originatingBranchName", ci.getOriginatinBranchName());
				dataObj.put("currentBranchName", ci.getCurrentBranchName());
				dataObj.put("stageCode", ci.getStageCode());
				dataObj.put("stageName", ci.getStageName());
				dataObj.put("stepCode", ci.getStepCode());
				dataObj.put("stepName", ci.getStepName());
				dataObj.put("caseId", ci.getCaseid());
				dataObj.put("masterCustomerName", ci.getSenderName());
				dataObj.put("receiptAmt", ci.getReceiptAmtIqd());
				dataObj.put("receiptNo", ci.getCustReceiptNoOri());
				dataObj.put("currentBranchCode", ci.getBranchCode());
				dataObj.put("address", ci.getLocationDetails());
				dataObj.put("agentrtnid", ci.getAgentRtnId());
				dataObj.put("partialRtnCCToBranch", ci.getPartialRtnCCToBranch());
				dataObj.put("partialRtnQty", ci.getPartialRtn_Qty());
				dataObj.put("custRtnId", ci.getCustReturnId());
				dataObj.put("pickUpAgentRtnId", ci.getPickupAgentRtnId());
				dataObj.put("rtnQty", ci.getRtnQty());
				dataObj.put("createdDt", ci.getCreateddt());
				dataObj.put("receiverName",ci.getReceiverName());
				dataObj.put("receiverHp1", ci.getReceiverHp1());
				dataObj.put("status", ci.getStatus());
				dataObj.put("rmk", ci.getRmk());
				dataObj.put("allowRtnCustomer", ci.getAllowRtnCustRtn());
				array.put(i, dataObj);
				dataObj = new JSONObject();
				i++;
			}
			out.append(array.toString());
				
			out.flush();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
