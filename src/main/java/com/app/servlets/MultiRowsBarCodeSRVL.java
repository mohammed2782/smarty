package com.app.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.cases.NewCases;

import smarty.security.LoginUser;

/**
 * Servlet implementation class MultiRowsBarCodeSRVL
 */
@WebServlet("/MultiRowsBarCodeSRVL")
public class MultiRowsBarCodeSRVL extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1418200813852373820L;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public MultiRowsBarCodeSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int branchCode = ((LoginUser)request.getSession().getAttribute("lu")).getBranchCode();
		
		NewCases nc = new NewCases();
		nc.setUserStoreCode(branchCode);
		boolean errorFlag = false;
    	String errorMsg = "";
		try {
			response.getWriter().append(nc.getRCVDetailsRow(
					Integer.parseInt(request.getParameter("loadRcvRow")),request.getParameter("barCodeRcpNo")
					, branchCode));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			errorFlag = true;
			errorMsg = e.getMessage();
		}
		if (errorFlag) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().append(errorMsg);
		}else
			response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
