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
import smarty.security.LoginUser;

/**
 * Servlet implementation class CalculateShipmentChargesSRVLT
 */
@WebServlet("/CalculateShipmentChargesSRVLT")
public class CalculateShipmentChargesSRVLT extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CalculateShipmentChargesSRVLT() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		Utilities ut = new Utilities();
		
		String destState = request.getParameter("destState");
		
		int custId = Integer.parseInt(request.getParameter("custid"));
		int MastercustId = Integer.parseInt(request.getParameter("mastercustid"));
		int rcvDistrict = Integer.parseInt(request.getParameter("rcvdistrict"));
		double shipmentCharges = 0.0;
		try {
			
			conn = mysql.getConn();

			LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
			boolean ruralArea = ut.isRuralDistrict(conn, rcvDistrict, lu.getBranchCode());
			//Connection conn, String destState ,boolean rural , int masterCustid, int custId ,  int branchCode
			shipmentCharges  = ut.calcShipmentChargesBasedOnDestCity(conn, destState,  ruralArea, MastercustId, custId, lu.getBranchCode()); 
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {conn.close();}catch(Exception e) {/**/}
		}
		PrintWriter out = response.getWriter();
		out.write(shipmentCharges + "");
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
