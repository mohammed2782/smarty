package com.app.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.financials.StandardFinCurrency;
import com.app.financials.UtilitiesFinancials;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class ChangeReceiptByCaseIdSRVL
 */
@WebServlet("/ChangeReceiptByCaseIdSRVL")
public class ChangeReceiptByCaseIdSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangeReceiptByCaseIdSRVL() {
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
		int caseId = Integer.parseInt(request.getParameter("caseId"));
		double newReceiptAmount = Double.parseDouble(request.getParameter("newReceiptAmount"));
		String fromWhere = request.getParameter("screenName");
		String currency = request.getParameter("currency");
		LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
		Connection conn1 = null;
		try {
			conn1 = mysql.getConn();
			Utilities.changeReceiptByCaseId(conn1, caseId, newReceiptAmount, lu.getUsid(), fromWhere,
					StandardFinCurrency.valueOf(currency));
			conn1.commit();
		}catch(Exception e) {
			e.printStackTrace();
			try {conn1.rollback();}catch(Exception eRoll) {/**/}
		}finally {
			try {conn1.close();}catch(Exception e) {/**/}
		}
		doGet(request, response);
	}

}
