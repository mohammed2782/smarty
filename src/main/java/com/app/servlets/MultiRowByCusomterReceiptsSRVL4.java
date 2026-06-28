package com.app.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.cases.NewCasesByCustomer3;
import com.app.cases.NewCasesByCustomer4;

import smarty.security.LoginUser;

/**
 * Servlet implementation class MultiRowByCusomterReceiptsSRVL4
 */
@WebServlet("/MultiRowByCusomterReceiptsSRVL4")
public class MultiRowByCusomterReceiptsSRVL4 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MultiRowByCusomterReceiptsSRVL4() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		NewCasesByCustomer4 ncbc = new NewCasesByCustomer4();		
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
