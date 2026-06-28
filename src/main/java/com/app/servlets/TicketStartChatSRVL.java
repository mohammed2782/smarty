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

import smarty.security.LoginUser;
/**
 * Servlet implementation class StartTicketChatSRVL
 */
@WebServlet("/TicketStartChatSRVL")
public class TicketStartChatSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TicketStartChatSRVL() {
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
		String chatWithWho = request.getParameter("chatWithWho");
		String customerId = request.getParameter("customerId");
		/*String branchDlvAgent = request.getParameter("branchDlvAgent");*/
		String dlvAgent = request.getParameter("dlvAgent");
		String onlyBranch = request.getParameter("onlyBranch");
		String masterCustomer = request.getParameter("masterCustomer");
		int otherPartyId = 0;
		int ticketId = Integer.parseInt(request.getParameter("ticketId"));
		int startedBy = Integer.parseInt(request.getParameter("startedBy"));
		LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
		Writer out = response.getWriter();
		try {
			conn = mysql.getConn();
			ChatBean chatBean = new ChatBean();
			if (chatWithWho.equalsIgnoreCase("WITHDLVAGENT")) {
				if(dlvAgent.trim().length()>0) {
					chatBean.setChatWithRank("DLVAGENT");
					otherPartyId = Integer.parseInt(dlvAgent);
				}else {
					throw new Exception("لم يتم أختيار مندوب التوصيل");
				}
			}else if (chatWithWho.equalsIgnoreCase("BRANCH")) {
				if(onlyBranch.trim().length()>0) {
					chatBean.setChatWithRank("BRANCH");
					otherPartyId = Integer.parseInt(onlyBranch);
				}else {
					throw new Exception("لم يتم أختيار مندوب الفرع");
				}
			}else if (chatWithWho.equalsIgnoreCase("WITHCUSTOMER")) {
				if(masterCustomer.trim().length()>0) {
					chatBean.setMasterCustomer(Integer.parseInt(masterCustomer));
					otherPartyId = Integer.parseInt(masterCustomer);
					if (customerId.trim().length()>0) {
						chatBean.setChatWithRank("CUSTOMER");
						otherPartyId = Integer.parseInt(customerId);
					}else {
						chatBean.setChatWithRank("MASTERCUSTOMER");
					}
				}else {
					throw new Exception("لم يتم أختيار العميل");
				}
			}
			
			
			chatBean.setChatWithId(otherPartyId);
			chatBean.setTicketId(ticketId);
			chatBean.setStartedById(startedBy);
			chatBean.setChatStartedByBranch(lu.getBranchCode());
			chatBean = ticketsUtilities.createChat(conn, chatBean);
			LinkedList<ChatBean> chatList = new LinkedList<ChatBean>();
			chatList.add(chatBean);
			conn.commit();
			JSONObject dataObj = new JSONObject();
			dataObj.put("newChat", chatList);
			out.write(dataObj.toString());
			out.flush();

		}catch(Exception e) {
			e.printStackTrace();
			response.setStatus(500);
			out.write(e.getMessage());
			out.flush();
			try {conn.rollback();}catch(Exception eRoll) {/**/}
		}finally {
			try {conn.close();}catch(Exception e) {}
			out.close();
		}
	}

}
