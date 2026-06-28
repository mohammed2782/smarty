package com.app.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.app.tickets.TicketsUtilities;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class TicketChangeOwnerSRVL
 */
@WebServlet("/TicketChangeOwnerSRVL")
public class TicketChangeOwnerSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TicketChangeOwnerSRVL() {
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
		int ticketId = Integer.parseInt(request.getParameter("ticketId"));
		int changedTo = Integer.parseInt(request.getParameter("changedTo"));
		LoginUser user = ((LoginUser) request.getSession().getAttribute("lu"));
		
		try {
			conn = mysql.getConn();
			TicketsUtilities.changeTicketOwenr(conn, ticketId, changedTo, user.getUsid());
			conn.commit();
		}catch(Exception e) {
			e.printStackTrace();
			response.setStatus(500);
			try {conn.rollback();}catch(Exception eRoll) {/**/}
		}finally {
			try {conn.close();}catch(Exception e) {}
		}
	}

}
