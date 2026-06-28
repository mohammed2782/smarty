package com.app.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;

import com.app.util.Utilities;
import com.app.util.UtilitiesNafie;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class ForcePushToCompulsaryDlvSRVL
 */
@WebServlet("/ForcePushToCompulsaryDlvSRVL")
public class ForcePushToCompulsaryDlvSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ForcePushToCompulsaryDlvSRVL() {
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
		int caseId = Integer.parseInt(request.getParameter("caseid"));
		String aRmk = request.getParameter("q_rmk");
		LoginUser lu  = (LoginUser) request.getSession().getAttribute("lu");
		String pageName = request.getParameter("page_name");
		if(pageName==null) {
			pageName="default";
		}
		Connection conn = null;
		UtilitiesNafie ut = new UtilitiesNafie();
		try {
			conn = mysql.getConn();
			if (Utilities.checkPermissionOfSpecialOperation(conn, "FORCE_DLV", lu, pageName)){
				ut.forceCasesToBeDelivered(conn, caseId, lu.getUsid(), aRmk, lu.getBranchCode());
			}
			conn.commit();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {}
		}
	}
}
