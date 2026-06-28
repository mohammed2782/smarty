package com.app.servlets;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.app.cases.CaseInformation;
import com.app.util.Utilities;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class getAllReceiptInfoOfSameReceiptNumberInQueueSRVL
 */
@WebServlet("/getAllReceiptInfoOfSameReceiptNumberInQueueSRVL")
public class getAllReceiptInfoOfSameReceiptNumberInQueueSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public getAllReceiptInfoOfSameReceiptNumberInQueueSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String c_custreceiptnoori = request.getParameter("c_custreceiptnoori");
		String a_whichScreen = request.getParameter("ReceiveAnyShipment");
		String step = request.getParameter("step");
		String stage = request.getParameter("stage");
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control","no-cache");
		Utilities ut = new Utilities ();
		LoginUser lu = (LoginUser)(request.getSession().getAttribute("lu"));
		JSONArray array = new JSONArray();
		
		
		Writer out = response.getWriter();
		Connection conn = null;
		
		try {
			conn = mysql.getConn();
			
				List<CaseInformation >casesInformationList = ut.getAllReceiptInfoOfSameReceiptNumberInQueue(conn, c_custreceiptnoori, 
						stage, step, lu.getBranchCode(), a_whichScreen);
				int i = 0;
				for (CaseInformation caseInformation : casesInformationList) {
					
					JSONObject dataObj = new JSONObject();
					dataObj.put("stepCode", caseInformation.getStepCode());
					dataObj.put("stageCode", caseInformation.getStageCode());
					dataObj.put("custName",caseInformation.getSenderName());
					dataObj.put("agentname",caseInformation.getAssignedAgentName());
					dataObj.put("hp", caseInformation.getReceiverHp1());
					dataObj.put("receiptamt", caseInformation.getReceiptAmtIqd());
					dataObj.put("receiptamtUsd", caseInformation.getReceiptAmtUsd());
					dataObj.put("originatedInBranch", caseInformation.getOriginatinBranchName());
					dataObj.put("address", caseInformation.getLocationDetails());
					dataObj.put("rcvname", caseInformation.getReceiverName());
					dataObj.put("caseid", caseInformation.getCaseid());
					dataObj.put("actionNeeded", caseInformation.getAction());
					dataObj.put("qty", caseInformation.getQty());
					dataObj.put("createddt", caseInformation.getCreateddt());
					dataObj.put("rural", caseInformation.getRural());
					dataObj.put("state", caseInformation.getState());
					array.put(i, dataObj);
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

