package com.app.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.beans.BranchPaymentBean;
import com.app.financials.UtilitiesStandardFinancials;
import com.app.util.UtilitiesNafie;

import smarty.db.mysql;
import smarty.security.LoginUser;

/**
 * Servlet implementation class ReceiveBranchPaymentSRVL
 */
@WebServlet("/ReceiveBranchPaymentSRVL")
public class ReceiveBranchPaymentSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReceiveBranchPaymentSRVL() {
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
		try {
			conn = mysql.getConn();
			LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
			BranchPaymentBean branchPaymentBean = new BranchPaymentBean();
			branchPaymentBean.setPaymentId(Integer.parseInt(request.getParameter("branchpmtid")));
			branchPaymentBean.setReceivedAmountIqd(Long.parseLong(request.getParameter("receivedAmtIqd").replace(",", "")));
			branchPaymentBean.setReceivedAmountUsd(Long.parseLong(request.getParameter("receivedAmtUsd").replace(",", "")));
			branchPaymentBean.setReceiverBranchRmk(request.getParameter("receivedRmk"));
			branchPaymentBean.setReceivedBy(lu.getUsid());
			UtilitiesStandardFinancials.receivePmtFromBranch(conn,lu.getBranchCode(), branchPaymentBean);
			conn.commit();
		}catch(Exception e) {
			e.printStackTrace();
			try {conn.rollback();}catch(Exception eRoll) {}
		}finally {
			try {conn.close();}catch(Exception e) {}
		}
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

}
