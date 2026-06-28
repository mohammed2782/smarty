package com.app.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.util.UtilitiesFeqar;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class ChangeCanPayToDlvFlagSRVL
 */
@WebServlet("/ChangeCanPayToDlvFlagSRVL")
public class ChangeCanPayToDlvFlagSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangeCanPayToDlvFlagSRVL() {
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
		Connection conn1 = null;
		UtilitiesFeqar ut= new UtilitiesFeqar();
		try {
			conn1 = mysql.getConn();
			LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
			ut.changeCanPaytoDlvFlag(conn1, caseId, lu.getUsid());
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
