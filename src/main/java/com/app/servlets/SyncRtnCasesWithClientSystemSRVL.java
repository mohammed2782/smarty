package com.app.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.util.IntegrationUtil;
import com.app.util.UtilitiesFeqar;
import com.app.util.UtilitiesNafie;

import smarty.db.mysql;

/**
 * Servlet implementation class SyncRtnCasesWithClientSystemSRVL
 */
@WebServlet("/SyncRtnCasesWithClientSystemSRVL")
public class SyncRtnCasesWithClientSystemSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SyncRtnCasesWithClientSystemSRVL() {
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
		int acrId = Integer.parseInt(request.getParameter("acrId"));
		String type = request.getParameter("type");
		Connection conn = null;
		IntegrationUtil iu = new IntegrationUtil();
		UtilitiesNafie utn =  new UtilitiesNafie ();
		LinkedList <Integer> rtnCasesIntegrationList = new LinkedList<Integer>();
		try {
			conn = mysql.getConn();
			if (type.equalsIgnoreCase("maincust"))
				rtnCasesIntegrationList = utn.getRtnCasesPerId(conn, acrId);
			else if (type.equalsIgnoreCase("pickupagent"))
				rtnCasesIntegrationList = utn.getRtnCasesPerPickUpAgentRtnId(conn, acrId);
				
			iu.updateRtnCasesInIntegratedSystems(conn, rtnCasesIntegrationList);
			utn.updateSyncRtnCases(conn, acrId);
			conn.commit();
		}catch(Exception e) {
			e.printStackTrace();
			try {conn.rollback();}catch(Exception eRoll) {/**/}
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
		}
		doGet(request, response);
	}

}
