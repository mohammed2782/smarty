package com.app.servlets;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;

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
 * Servlet implementation class GetReturnableSingleReceiptSRVL
 */
@WebServlet("/GetReturnableReceiptsSRVL")
public class GetReturnableReceiptsSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetReturnableReceiptsSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String c_custreceiptnoori = request.getParameter("c_custreceiptnoori");
		int branchCode = Integer.parseInt(request.getParameter("userBranchCode"));
		LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control","no-cache");
		UtilitiesNafie utn = new UtilitiesNafie ();
		
		JSONArray array = new JSONArray();
		JSONObject dataObj = new JSONObject();
		
		Writer out = response.getWriter();
		Connection conn = null;
		ArrayList<CaseInformation> ciList = new ArrayList<CaseInformation>();
		try {
			if (branchCode != lu.getBranchCode()) {
				throw new Exception ("Branch Code Error");
			}
			conn = mysql.getConn();
			ciList = utn.getReturnableSingleReceiptInfoInQueue(conn, c_custreceiptnoori, branchCode);
			int i=0;
			for (CaseInformation ci : ciList) {
				dataObj.put("originatingBranchCode", ci.getOrigintingBranch());
				dataObj.put("originatingBranchName", ci.getOriginatinBranchName());
				dataObj.put("stageCode", ci.getStageCode());
				dataObj.put("stepcode", ci.getStepCode());
				dataObj.put("caseId", ci.getCaseid());
				dataObj.put("masterCustomerName", ci.getSenderName());
				dataObj.put("receiptAmt", ci.getReceiptAmtIqd());
				dataObj.put("receiptNo", ci.getCustReceiptNoOri());
				dataObj.put("currentBranchCode", ci.getBranchCode());
				dataObj.put("address", ci.getLocationDetails());
				dataObj.put("agentrtnid", ci.getAgentRtnId());
				dataObj.put("partialRtnCCToBranch", ci.getPartialRtnCCToBranch());
				dataObj.put("partialRtnQty", ci.getPartialRtn_Qty());
				array.put(i, dataObj);
				dataObj = new JSONObject();
				i++;
			}
			//System.out.println("array.toString()==>"+array.toString());
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
