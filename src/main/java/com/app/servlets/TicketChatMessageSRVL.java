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

import org.json.JSONArray;
import org.json.JSONObject;

import com.app.tickets.ChatMsgBean;
import com.app.tickets.TicketsUtilities;
import com.app.util.UtilitiesNafie;

import smarty.db.mysql;

/**
 * Servlet implementation class TicketChatMessageSRVL
 */
@WebServlet("/TicketChatMessageSRVL")
public class TicketChatMessageSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TicketChatMessageSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		TicketsUtilities ticketsUtilities = new TicketsUtilities();
		Connection conn = null;
		JSONObject dataObj = new JSONObject();
		Writer out = response.getWriter();
		int chatId = Integer.parseInt(request.getParameter("chatId"));
		try {
			conn = mysql.getConn();
			LinkedList<ChatMsgBean> chatList = ticketsUtilities.getChatMsgs(conn, chatId);
			dataObj.put("chatList", chatList);
			out.append(dataObj.toString());
			out.flush();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {/*ignore*/}
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
