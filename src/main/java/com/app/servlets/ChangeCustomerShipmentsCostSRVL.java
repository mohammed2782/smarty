package com.app.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.util.Utilities;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class ChangeCustomerShipmentsCostSRVL
 */
@WebServlet("/ChangeCustomerShipmentsCostSRVL")
public class ChangeCustomerShipmentsCostSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangeCustomerShipmentsCostSRVL() {
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
		String custId = request.getParameter("custId");
		Connection conn = null;
		Utilities ut= new Utilities();
		try {
			conn = mysql.getConn();
			LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
			ut.updateAllCustomerShipmentsCost(conn, custId, lu.getBranchCode());
			conn.commit();
		}catch(Exception e) {
			e.printStackTrace();
			try {conn.rollback();}catch(Exception eRoll) {/**/}
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
		}
		doGet(request, response);
	}

}
