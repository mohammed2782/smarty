package com.app.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.util.Utilities;

import smarty.db.mysql;

/**
 * Servlet implementation class LoadDistrictSRVLT
 */
@WebServlet("/TLRLoadDistrictSRVLT")
public class LoadDistrictSRVLT extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadDistrictSRVLT() {
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
		Utilities ut = new Utilities();
		StringBuilder sb = new StringBuilder();
		String destCity = request.getParameter("destCity");
		try {
			conn = mysql.getConn();
			LinkedHashMap<Integer,String> district  = ut.getDistrictOfState(conn,destCity); 
			for (Integer key : district.keySet()) {
				sb.append("<option value='"+key+"'>"+district.get(key)+"</option>");				
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
		}
		PrintWriter out = response.getWriter();
		out.write(sb + "");
		out.close();
	}

}
