package com.app.servlets;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import com.app.tickets.ChatBean;
import com.app.tickets.TicketsUtilities;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class TicketsMarkChatAsSeenSRVL
 */
@WebServlet("/TicketsMarkChatAsSeenSRVL")
public class TicketsMarkChatAsSeenSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TicketsMarkChatAsSeenSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		TicketsUtilities ticketsUtilities = new TicketsUtilities();
		int chatId = Integer.parseInt(request.getParameter("chatId"));
		LoginUser lu = (LoginUser)request.getSession().getAttribute("lu");
		try {
			conn = mysql.getConn();
			ticketsUtilities.setChatMesagesAsSeen(conn, chatId, lu.getBranchCode());
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
