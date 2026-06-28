package com.app.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;

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
 * Servlet implementation class RestoreForcedDlvCaseSRVL
 */
@WebServlet("/RestoreForcedDlvCaseSRVL")
public class RestoreForcedDlvCaseSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RestoreForcedDlvCaseSRVL() {
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
		LoginUser lu  = (LoginUser) request.getSession().getAttribute("lu");	
		String pageName = request.getParameter("page_name");
		if(pageName==null) {
			pageName="default";
		}
		Connection conn = null;
		UtilitiesNafie ut = new UtilitiesNafie();
		Utilities ut_main = new Utilities();
		try {
			conn = mysql.getConn();
			HashMap<String,String> caseInfo =ut_main.getCaseInfo(conn, caseId);
			String previous_action_taken_by=caseInfo.get("q_previous_action_taken_by");
			if (
				(pageName.equalsIgnoreCase("default") && 
						Utilities.checkPermissionOfSpecialOperation(conn, "FORCE_DLV", lu, pageName))
				|| (Utilities.checkPermissionOfSpecialOperation(conn, "FORCE_DLV", lu, pageName)
						&& previous_action_taken_by.equalsIgnoreCase(lu.getUsid()+"")
					)
				){
				ut.restoreCasesForcedDeliveredByManagement(conn, caseId, lu.getUsid());
				conn.commit();
			}			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {}
		}
		
	}
}
