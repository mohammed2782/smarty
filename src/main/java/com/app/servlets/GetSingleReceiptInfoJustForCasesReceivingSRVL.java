package com.app.servlets;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.app.util.UtilitiesNafie;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class GetSingleReceiptInfoJustForCasesReceivingSRVL
 */
@WebServlet("/GetSingleReceiptInfoJustForCasesReceivingSRVL")
public class GetSingleReceiptInfoJustForCasesReceivingSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetSingleReceiptInfoJustForCasesReceivingSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String a_custreceiptnoori = request.getParameter("c_custreceiptnoori");
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
				List<CaseInformation >casesInformationList = Utilities.getSingleReceiptInfoInAnyStageStepRelatedToReceiving
						(conn, a_custreceiptnoori,lu.getBranchCode(), lu.getUsid(), a_whichScreen);
				int i = 0;
				for (CaseInformation caseInformation : casesInformationList) {
					JSONObject dataObj = new JSONObject();
					dataObj.put("stepCode", caseInformation.getStepCode());
					dataObj.put("stageCode", caseInformation.getStageCode());
					dataObj.put("custName",caseInformation.getSenderName());
					dataObj.put("agentname",caseInformation.getAssignedAgentName());
					dataObj.put("hp", caseInformation.getReceiverHp1());
					dataObj.put("receiptamt", caseInformation.getReceiptAmtIqd());
					dataObj.put("address", caseInformation.getLocationDetails());
					dataObj.put("caseid", caseInformation.getCaseid());
					dataObj.put("qty", caseInformation.getQty());
					dataObj.put("createddt", caseInformation.getCreateddt());
					dataObj.put("state", caseInformation.getState());
					dataObj.put("stateName", caseInformation.getStateName());
					dataObj.put("originatedInBranch", caseInformation.getOriginatinBranchName());
					dataObj.put("actionNeeded", caseInformation.getAction());
					dataObj.put("whenItWasScanned", caseInformation.getWhenItWasScannedByBarCodel());
					dataObj.put("receiptNo", caseInformation.getCustReceiptNoOri());
					dataObj.put("currentBranch", caseInformation.getCurrentBranch());
					//System.out.println("currentBranch==========>"+caseInformation.getCurrentBranch());
					array.put(i, dataObj);
					i++;
				}
				if (i<1) { // means no record was found, we search it else where
					UtilitiesNafie utn = new UtilitiesNafie();
					HashMap<String,String> searchMap = new HashMap<String,String>();
					//
					searchMap.put("c_custreceiptnoori", a_custreceiptnoori);
					ArrayList<CaseInformation> ciList = utn.getCasesAdvancedSearch(conn, searchMap);
					i=0;
				
					for (CaseInformation ci : ciList) {
						JSONObject dataObj = new JSONObject();
						dataObj.put("currentBranchName", ci.getCurrentBranchName());
						dataObj.put("stageCode", ci.getStageCode());
						dataObj.put("stageName", ci.getStageName());
						dataObj.put("stepCode", ci.getStepCode());
						dataObj.put("stepName", ci.getStepName());
						dataObj.put("receiptNo", a_custreceiptnoori);
						dataObj.put("currentBranch", ci.getCurrentBranch());
						dataObj.put("state", ci.getState());
						array.put(i, dataObj);
						dataObj = new JSONObject();
						i++;
						break;//get only the first one
					}
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
