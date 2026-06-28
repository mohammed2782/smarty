package com.app.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.util.UtilitiesNafie;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class FollowUpDoActionSRVL
 */
@WebServlet("/FollowUpDoActionSRVL")
public class FollowUpDoActionSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FollowUpDoActionSRVL() {
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
		int aCaseId = Integer.parseInt(request.getParameter("acaseId"));
		String aFollowUpAction = request.getParameter("aFollowUpAction");
		String aFollowUpRemakrs = request.getParameter("aFollowUpRemakrs");
		Connection conn = null;
		UtilitiesNafie utn= new UtilitiesNafie();
		try {
			LoginUser lu = (LoginUser)request.getSession().getAttribute("lu");
			conn = mysql.getConn();
			utn.registerFollowUpAction(conn, aCaseId, lu.getUsid(), aFollowUpAction, aFollowUpRemakrs);
			conn.commit();
		}catch(Exception e) {
			e.printStackTrace();
			try {conn.rollback();}catch(Exception eRoll) {/**/}
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
		}
	}

}
