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

/**
 * Servlet implementation class ActiveDeactiveNotificationControlSRVL
 */
@WebServlet("/ActiveDeactiveNotificationControlSRVL")
public class ActiveDeactiveNotificationControlSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ActiveDeactiveNotificationControlSRVL() {
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
		Connection conn = null;
		UtilitiesFeqar ut= new UtilitiesFeqar();
		String kbId = request.getParameter("kbId");
		String flag = request.getParameter("flag");
		try {
			conn = mysql.getConn();
			ut.changeNotificationControlActiveCondition(conn, kbId, flag);
			conn.commit();
		}catch(Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {/**/}
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
		}
		doGet(request, response);
	}

}
