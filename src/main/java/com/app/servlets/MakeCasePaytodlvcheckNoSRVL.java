package com.app.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.printables.PrintSellBillPDF;
import com.app.util.Utilities;
import com.app.util.UtilitiesNafie;

import smarty.db.mysql;
import smarty.security.LoginUser;


/**
 * Servlet implementation class PrintSellBillSRVL
 */
@WebServlet("/MakeCasePaytodlvcheckNoSRVL")
public class MakeCasePaytodlvcheckNoSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MakeCasePaytodlvcheckNoSRVL() {
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
		int caseId = Integer.parseInt(request.getParameter("caseid"));
		Connection conn = null;
		UtilitiesNafie ut = new UtilitiesNafie();
		try {
			conn = mysql.getConn();
		ut.makeCasePayCheckNoUtil(conn, caseId);
			conn.commit();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {}
		}
	}
}
