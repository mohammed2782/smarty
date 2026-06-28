package com.app.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.cases.NewCasesByState3;
import com.app.cases.NewCasesFlat;

import smarty.security.LoginUser;

/**
 * Servlet implementation class MultiRowsFlatSRVL
 */
@WebServlet("/MultiRowsFlatSRVL")
public class MultiRowsFlatSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MultiRowsFlatSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	NewCasesFlat ncbs = new NewCasesFlat();		
		int branchCode = ((LoginUser)request.getSession().getAttribute("lu")).getBranchCode();
		LoginUser lu = ((LoginUser)request.getSession().getAttribute("lu"));
		ncbs.setLu(lu);
		String state = request.getParameter("state");
		ncbs.setUserStoreCode(branchCode);
		response.getWriter().append(ncbs.getRCVDetailsRow(Integer.parseInt(request.getParameter("loadRcvRow")),branchCode, state));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
