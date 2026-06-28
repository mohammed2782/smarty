package com.app.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.util.Utilities;
import com.app.util.UtilitiesNafie;

import smarty.db.mysql;

/**
 * Servlet implementation class UpdateSuccessIntegrationFSMSRVL
 */
@WebServlet("/UpdateSuccessInOriginSystemSRVL")
public class UpdateSuccessInOriginSystemSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateSuccessInOriginSystemSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String paymentBillId = request.getParameter("pmtId");
		String userId = request.getParameter("actionUserId");
		String fromCustomer = request.getParameter("fromcustomerpmt");
	
		Connection conn = null;
		UtilitiesNafie ut = new UtilitiesNafie();
		try {
			conn = mysql.getConn();
			if (fromCustomer.equalsIgnoreCase("N"))
				ut.doUpdateSuccessCasesPickupAgentFsm(conn, paymentBillId, userId);
			else
				ut.doUpdateSuccessCasesCustomerPmtFsm(conn, paymentBillId, userId);
			conn.commit();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {}
		}
	}

}
