package com.app.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import smarty.db.mysql;
import smarty.security.LoginUser;
import com.app.util.UtilitiesAyatLina;

/**
 * Servlet implementation class ChangeReceiptByCaseIdSRVL
 */
@WebServlet("/ChangeSingleCaseSingleInformationSRVL")
public class ChangeSingleCaseSingleInformationSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangeSingleCaseSingleInformationSRVL() {
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
		Connection conn1 = null;
		UtilitiesAyatLina utal = new UtilitiesAyatLina();
		LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");

		int caseId = Integer.parseInt(request.getParameter("p_caseId"));
		String columnToChange = request.getParameter("p_columnToChange");
		String newValue = request.getParameter("p_newValue");
		String fromWhere = request.getParameter("p_screenName");
		try {
			conn1 = mysql.getConn();
			utal.changeShipmentCostByCaseId(conn1, caseId, columnToChange, newValue , fromWhere,  lu.getUsid());
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
