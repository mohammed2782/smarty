package com.app.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.cases.NewCasesByCustomer;
import com.app.cases.NewCasesByCustomer3;

import smarty.security.LoginUser;

/**
 * Servlet implementation class MultiRowByCusomterReceiptsSRVL3
 */
@WebServlet("/MultiRowByCusomterReceiptsSRVL3")
public class MultiRowByCusomterReceiptsSRVL3 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MultiRowByCusomterReceiptsSRVL3() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		NewCasesByCustomer3 ncbc = new NewCasesByCustomer3();		
		LoginUser lu= ((LoginUser)request.getSession().getAttribute("lu"));
		ncbc.setUserStoreCode(lu.getBranchCode());
		ncbc.setLu(lu);
		response.getWriter().append(ncbc.getRCVDetailsRow(
				Integer.parseInt(request.getParameter("loadRcvRow")),lu.getBranchCode()));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
