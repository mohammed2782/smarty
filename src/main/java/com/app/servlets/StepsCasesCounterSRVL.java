package com.app.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.util.Utilities;

import smarty.db.mysql;

/**
 * Servlet implementation class StepsCasesCounterSRVL
 */
@WebServlet("/StepsCasesCounterSRVL")
public class StepsCasesCounterSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StepsCasesCounterSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		Utilities ut = new Utilities();
		
		String stageCode = request.getParameter("stageCode");
		String stepCode = request.getParameter("stepCode");
		int branchCode = Integer.parseInt(request.getParameter("branchCode"));
		int ctr = 0;
		try {
			
			conn = mysql.getConn();
			ctr  = ut.countCasesInQueue(conn, stageCode, stepCode, branchCode); 
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
		}
		PrintWriter out = response.getWriter();
		out.write(ctr + "");
		out.close();
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
