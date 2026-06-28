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
 * Servlet implementation class ChangeBranch
 */
@WebServlet("/ChangeBranchSRVL")
public class ChangeBranchSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangeBranchSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int branchCode = Integer.parseInt(request.getParameter("current_branch"));
		Connection conn = null;
		boolean ok = false;
		Utilities ut = new Utilities();
		try {
			LoginUser lu = (LoginUser)request.getSession().getAttribute("lu");
			conn = mysql.getConn();
			String rankCode = lu.getRankCodeWhenBranchChanges(conn, lu.getUsid(), branchCode);
			lu.setBranchCode(branchCode);
			lu.setRank_code(rankCode);
			lu.setBranchName(ut.getBranchesInfo(conn, branchCode+"").get("name"));
			lu.LoadPermissions(conn, rankCode, branchCode+"");
			ok = true;
		}catch(Exception e){
			e.printStackTrace();
			ok = false;
			request.getSession().invalidate();
			
			response.sendRedirect("login.jsp");
		}finally {
			try {conn.close();}catch(Exception e) {}
		}
		if (ok)
			response.sendRedirect(request.getHeader("referer"));
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
