package com.app.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.util.Utilities;

import smarty.db.mysql;

/**
 * Servlet implementation class ArchiveSafeTransactionSRVL
 */
@WebServlet("/ArchiveSafeTransactionSRVL")
public class ArchiveSafeTransactionSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ArchiveSafeTransactionSRVL() {
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
		Utilities ut= new Utilities();
		String userId = request.getParameter("userId");
		int branchId = Integer.parseInt(request.getParameter("branchId"));
		HashMap<Boolean, String> hashy = new HashMap<Boolean, String>();
		try {
			conn = mysql.getConn();
			hashy = ut.ArchiveAllSafeTransaction(conn, userId, branchId);
			if(hashy.get(true) != null)
				conn.commit();
			else
				throw new Exception(hashy.get(false));
		}catch(Exception e) {
			e.printStackTrace();
			try {conn.rollback();}catch(Exception eRoll) {/**/}
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
		}
		doGet(request, response);
	}

}
