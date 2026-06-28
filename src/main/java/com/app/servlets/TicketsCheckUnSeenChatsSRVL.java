package com.app.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import com.app.tickets.ChatBean;
import com.app.tickets.TicketsUtilities;
import com.app.util.Utilities;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class TicketsCheckUnSeenChatsSRVL
 */
@WebServlet("/TicketsCheckUnSeenChatsSRVL")
public class TicketsCheckUnSeenChatsSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TicketsCheckUnSeenChatsSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		JSONObject dataObj = new JSONObject();
		TicketsUtilities ticketsUtilities = new TicketsUtilities();
		LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
		int assignedEmp = lu.getUsid();
		PrintWriter printWriter = response.getWriter();
		String statesCommaSeperated = (String) request.getSession().getAttribute("selectedTicketsStatesCommaSeperated_G");
		String ticketsStatesSearchMode = (String) request.getSession().getAttribute("ticketsStatesSearchMode_G");
		try {
			ArrayList<Integer> chatUnSeenInticketsList = null;
			ArrayList<Integer> ticketsNewNotAssigned = null;
			conn = mysql.getConn();
				chatUnSeenInticketsList = ticketsUtilities.checkTicketsUnseenChatsByController(conn, assignedEmp, lu.getBranchCode());
				ticketsNewNotAssigned = ticketsUtilities.getNewTicketsNotAssigned(conn, statesCommaSeperated, ticketsStatesSearchMode, lu.getBranchCode());
				dataObj.put("ticketsList", chatUnSeenInticketsList);
				dataObj.put("ticketsNewNotAssigned", ticketsNewNotAssigned);
				printWriter.append(dataObj.toString());
				printWriter.flush();
		
		}catch(Exception e) {
			e.printStackTrace();
			response.setStatus(500);
		}finally {
			try {conn.close();}catch(Exception e) {}
			printWriter.close();
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
