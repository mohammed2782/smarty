package com.app.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.printables.CustomerPaymentExcel;

import smarty.security.LoginUser;

import javax.servlet.ServletOutputStream;
/**
 * Servlet implementation class CustomerPaymentExcelSRVL
 */
@WebServlet("/CustomerPaymentExcelSRVL")
public class CustomerPaymentExcelSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CustomerPaymentExcelSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedInputStream buf = null;
		ServletOutputStream stream = null;
		LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
		try {
		int transId =Integer.parseInt(request.getParameter("trans_id"));
		String docType = "xls";
		CustomerPaymentExcel customerPaymentExcel =  new CustomerPaymentExcel();
		String contentType = "application/vnd.ms-excel";
		response.setContentType(contentType);
		
		String fileName = "customer_payment_"+transId+".xls";
		response.setHeader("Content-Disposition","attachment;filename="+fileName);
		
		String ctxPath = this.getClass().getResource("/").getPath();
		ctxPath = ctxPath.replaceAll("WEB-INF/classes/", "");
		ctxPath = ctxPath.replaceAll("%20", " ");
		
		File file = customerPaymentExcel.getManifestTable(transId, lu.getBranchCode(), ctxPath);
		response.setContentLength((int) file.length());
		FileInputStream fileIn = new FileInputStream(file);
		
	    buf = new BufferedInputStream(fileIn);
	    int readBytes = 0;
	    stream = response.getOutputStream();
	    while ((readBytes = buf.read()) != -1)// while there is still data in the buffer.loops byte by byte
		    stream.write(readBytes);//write it.
		} catch (IOException ioe) {
			ioe.printStackTrace();
		  throw new ServletException(ioe.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (stream != null){stream.close();}
			if (buf != null){buf.close();}
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
