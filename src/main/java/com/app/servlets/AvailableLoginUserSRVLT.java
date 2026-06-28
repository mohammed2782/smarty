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

/**
 * Servlet implementation class AvailableLoginUserSRVLT
 */
@WebServlet("/AvailableLoginUserSRVLT")
public class AvailableLoginUserSRVLT extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AvailableLoginUserSRVLT() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Utilities ut= new Utilities();
		String loginId = request.getParameter("loginId");
		String actionName = request.getParameter("actionName");
		boolean wrong = true;
		Connection conn = null;
		try {
			conn = mysql.getConn();
			wrong = ut.checkUserLoginIdExistOrSmall(conn, loginId, actionName );
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {}
		}
		response.getWriter().append(wrong+"");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
