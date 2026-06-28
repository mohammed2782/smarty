package com.app.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class EnableDiableAgentByEditeCaseSRVL
 */
@WebServlet("/HideAgentByEditeCaseSRVL")
public class HideAgentByEditeCaseSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HideAgentByEditeCaseSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String destState = request.getParameter("destState");
		Connection conn = null;
		UtilitiesFeqar ut = new UtilitiesFeqar();
		boolean belongToMyBranch = false;
		try {
			conn = mysql.getConn();
			LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
			belongToMyBranch = ut.checkCaseBelongToMyBranch(conn,lu.getBranchCode(), destState);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
		}
		PrintWriter out = response.getWriter();
		out.write(belongToMyBranch + "");
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
