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
import com.app.tickets.TicketsUtilities;
import com.app.util.UtilitiesNafie;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class TicketChatAddMsgSRVL
 */
@WebServlet("/TicketChatAddMsgSRVL")
public class TicketChatAddMsgSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TicketChatAddMsgSRVL() {
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
		TicketsUtilities ticketsUtilities = new TicketsUtilities();
		Connection conn = null;
		int msgId = 0;
		LoginUser lu = (LoginUser)request.getSession().getAttribute("lu");
		try {
			conn = mysql.getConn();
			ChatMsgBean chatMsgBean = new ChatMsgBean();
			int chatId = Integer.parseInt(request.getParameter("chatId"));
			String senderRank = request.getParameter("senderRank");
			String communicationMedium = request.getParameter("communicationMedium"); 
			String msg = request.getParameter("msg");
			//System.out.println("msg --- >"+msg+", has new ---->"+msg.contains("\n")+", ---->"+msg.contains("\\n"));
			chatMsgBean.setChatId(chatId);
			chatMsgBean.setCommunitcationMedium(communicationMedium);
			chatMsgBean.setMsg(msg);
			chatMsgBean.setSenderId(lu.getUsid());
			chatMsgBean.setSenderRank(senderRank);
			
			msgId = ticketsUtilities.saveChatMsg(conn, chatMsgBean, lu.getBranchCode());
			conn.commit();
		}catch(Exception e) {
			e.printStackTrace();
			try {conn.rollback();}catch(Exception eRoll) {}
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
		}
		try(PrintWriter printWriter= response.getWriter()){
			printWriter.append(""+msgId);
		}
	}

}
