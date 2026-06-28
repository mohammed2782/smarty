package com.app.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.util.Utilities;
import com.app.util.UtilitiesNafie;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class SubmitQueueActionFromAuditSRVL
 */
@WebServlet("/SubmitQueueActionFromAuditSRVL")
public class SubmitQueueActionFromAuditSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubmitQueueActionFromAuditSRVL() {
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
		String stageCode = request.getParameter("stageCode");
		String stepCode = request.getParameter("stepCode");
		String action  = request.getParameter("action");
		String qRemarks = request.getParameter("remarks");
		Connection conn = null;
		UtilitiesNafie ut= new UtilitiesNafie();
		try {
			LoginUser lu = (LoginUser)request.getSession().getAttribute("lu");
			int userBranch = lu.getBranchCode();
			conn = mysql.getConn();
			ut.moveCaseInsideQueueFromAudit(conn, caseId, stageCode, stepCode, action, lu.getUsid(), userBranch, qRemarks);
			conn.commit();
		}catch(Exception e) {
			e.printStackTrace();
			try {conn.rollback();}catch(Exception eRoll) {/**/}
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
		}
	}

}
