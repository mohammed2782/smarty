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
import com.app.util.UtilitiesNafie;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class CloseRtnManifestSRVL
 */
@WebServlet("/CloseRtnManifestSRVL")
public class CloseRtnManifestSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CloseRtnManifestSRVL() {
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
		int manifestRtnId = Integer.parseInt(request.getParameter("manifestRtnId"));
		String type = request.getParameter("type");
		Connection conn = null;
		UtilitiesNafie utn =  new UtilitiesNafie ();
		LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
		try {
			conn = mysql.getConn();
			if (type.equalsIgnoreCase("maincust"))
				utn.closeCustomerRtnManifest(conn, manifestRtnId, lu.getUsid());
			else if (type.equalsIgnoreCase("pickupagent"))
				utn.closePickUpAgentRtnManifest(conn, manifestRtnId, lu.getUsid());
		
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
