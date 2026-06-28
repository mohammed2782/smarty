package com.app.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.app.tickets.ChatMsgBean;
import com.app.tickets.TicketBean;
import com.app.tickets.TicketsUtilities;

import smarty.db.mysql;
import smarty.security.LoginUser;


/**
 * Servlet implementation class TicketsOpenNewSRVL
 */
@WebServlet("/TicketsOpenNewSRVL")
public class TicketsOpenNewSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TicketsOpenNewSRVL() {
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
		LoginUser lu = (LoginUser)request.getSession().getAttribute("lu");
		String subjectCode = (String)request.getParameter("ticketSubject");
		String details  = (String) request.getParameter("details");
		int toBranch  = Integer.parseInt(request.getParameter("ticketToBranch"));
		int caseId = Integer.parseInt(request.getParameter("caseId"));
		TicketsUtilities ticketsUtilities = new TicketsUtilities();
		
		TicketBean  ticketBean = new TicketBean();
		Connection conn = null;
		int ticketId = 0;
		try {
			conn = mysql.getConn();
			ticketBean.setTktCaseId(caseId);
			ticketBean.setTktTitleCode(subjectCode);
			ticketBean.setTktDesc(details);
			ticketBean.setTktPriorityCode("MED");
			ticketBean.setTktOwnerBranchId(lu.getBranchCode());
			ticketId = ticketsUtilities.OpenNewTicket(conn, lu.getUsid(), ticketBean, lu.getRank_code(), toBranch, lu.getBranchCode());
			
			conn.commit();
		}catch(Exception e) {
			try {conn.rollback();}catch(Exception eRoll) {}
		}finally {
			try {conn.close();}catch(Exception e) {}
		}
		//OpenNewTicket
		//WriteChatMsg
		try(PrintWriter printWriter= response.getWriter()){
			printWriter.append(ticketId+"");
		}
		//response.getWriter().append(ticketId+"");
	}

}
