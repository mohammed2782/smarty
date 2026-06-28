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
 * Servlet implementation class CheckUserLoginIdAndPassSRVL
 */
@WebServlet("/CheckUserLoginIdAndPassSRVL")
public class CheckUserLoginIdAndPassSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckUserLoginIdAndPassSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		Utilities ut = new Utilities();
		String pass = "", loginid = "";
		
		if(request.getParameter("us_password") != null)
			pass = request.getParameter("us_password");
		if(request.getParameter("us_loginid") != null)
			loginid = request.getParameter("us_loginid");
		String errorMassege = "3";
		try {
			
			conn = mysql.getConn();
			if(!pass.isEmpty()) {
				if(Utilities.checkPasswordSmallOrContainSpace(pass))
					errorMassege = "1";
			}
			if(!loginid.isEmpty()) {
				if(ut.checkUserLoginIdExistOrSmall(conn, loginid,"INSERT"))
					errorMassege = "2";
			}

		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
		}
		PrintWriter out = response.getWriter();
		out.write(errorMassege + "");
		out.close();
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
