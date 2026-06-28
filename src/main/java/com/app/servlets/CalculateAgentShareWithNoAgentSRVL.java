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

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class CalculateAgentShareWithNoAgentSRVL
 */
@WebServlet("/CalculateAgentShareWithNoAgentSRVL")
public class CalculateAgentShareWithNoAgentSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CalculateAgentShareWithNoAgentSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String destState = request.getParameter("destCity");
		int district = Integer.parseInt(request.getParameter("rcvdistrict"));
		double agentShare = 0.0;
		Connection conn = null;
		boolean rural = false;
		Utilities ut = new Utilities();
		String agentId = null;
		if(request.getParameter("agentid") != null)
			agentId = request.getParameter("agentid");
		try {
			conn = mysql.getConn();
			LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
			rural = ut.isRuralDistrict(conn, district, lu.getBranchCode());
			//Connection conn, int branchCode, String destState, int districtCode , boolean rural, String agentId 
			agentShare = ut.calcAgentShipmentChargesShare(conn, lu.getBranchCode(),  destState,district, rural, agentId );
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
		}
		PrintWriter out = response.getWriter();
		out.write(agentShare + "");
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
