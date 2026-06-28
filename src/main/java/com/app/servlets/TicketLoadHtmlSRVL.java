package com.app.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.tickets.TicketBean;
import com.app.tickets.TicketsUtilities;

import smarty.db.mysql;

/**
 * Servlet implementation class TicketLoadHtmlSRVL
 */
@WebServlet("/TicketLoadHtmlSRVL")
public class TicketLoadHtmlSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TicketLoadHtmlSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		TicketsUtilities ticketUtilities = new TicketsUtilities();
		Connection conn = null;
		int ticketId = Integer.parseInt(request.getParameter("ticketId"));
		String htmlResponse = "";
		try {
			conn = mysql.getConn();
			TicketBean ticketBean  = ticketUtilities.getSingleTicketInfo(conn, ticketId);
			htmlResponse = ticketUtilities.getSingleTicketHtml(ticketBean,false, true);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {}
		}
		try(PrintWriter printWriter= response.getWriter()){
			printWriter.append(htmlResponse);
		}
		//response.getWriter().append(htmlResponse);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
