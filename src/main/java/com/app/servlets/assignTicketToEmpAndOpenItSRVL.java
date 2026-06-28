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
 * Servlet implementation class assignTicketToEmpAndOpenItSRVL
 */
@WebServlet("/assignTicketToEmpAndOpenItSRVL")
public class assignTicketToEmpAndOpenItSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public assignTicketToEmpAndOpenItSRVL() {
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
		int userId = Integer.parseInt(request.getParameter("userId"));
		int ticketId = Integer.parseInt(request.getParameter("ticketId"));
		Connection conn = null;
		TicketsUtilities ticketsUtilities = new TicketsUtilities();
		LoginUser user = (LoginUser)request.getSession().getAttribute("lu");
		boolean assigned = false;
		try {
			conn = mysql.getConn();
			assigned = ticketsUtilities.assignAndOpenTicket(conn, userId, ticketId, user.getBranchCode());
			conn.commit();
		}catch(Exception e) {
			e.printStackTrace();
			try {conn.rollback();}catch(Exception eRoll) {}
		}finally {
			try {conn.close();}catch(Exception eRoll) {}
		}
		response.getWriter().append(assigned+"");
	}

}
