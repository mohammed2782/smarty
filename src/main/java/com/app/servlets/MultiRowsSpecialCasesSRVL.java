package com.app.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.cases.NewSpecialCases;

import smarty.security.LoginUser;

/**
 * Servlet implementation class TLKMultiRowsSpecialCases
 */
@WebServlet("/MultiRowsSpecialCasesSRVL")
public class MultiRowsSpecialCasesSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MultiRowsSpecialCasesSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		NewSpecialCases nspc = new NewSpecialCases();

		int branchCode = ((LoginUser)request.getSession().getAttribute("lu")).getBranchCode();
		nspc.setUserStoreCode(branchCode);
		response.getWriter().append(nspc.getRCVDetailsRow(Integer.parseInt(request.getParameter("loadRcvRow")),branchCode));

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
