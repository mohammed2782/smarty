package com.app.cust.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.cases.NewCases;
import com.app.cust.cases.CustomerCreateNewCases;

import smarty.security.LoginUser;

/**
 * Servlet implementation class CustCreateCasesMultiRowsSRVLT
 */
@WebServlet("/CustCreateCasesMultiRowsSRVLT")
public class CustCreateCasesMultiRowsSRVLT extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CustCreateCasesMultiRowsSRVLT() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CustomerCreateNewCases ccnc = new CustomerCreateNewCases();
		
		int branchCode = ((LoginUser)request.getSession().getAttribute("lu")).getBranchCode();
		int masterCustId = ((LoginUser)request.getSession().getAttribute("lu")).getMasterCustId();
		int shopMultiRows = Integer.parseInt(request.getParameter("shopmultirows"));
		ccnc.setUserStoreCode(branchCode);
		ccnc.setMasterCustId(masterCustId);
		response.getWriter().append(ccnc.getRCVDetailsRow(Integer.parseInt(request.getParameter("loadRcvRow")), shopMultiRows));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
