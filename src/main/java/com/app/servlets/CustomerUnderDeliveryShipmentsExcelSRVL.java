package com.app.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.printables.CustomerPaymentExcel;
import com.app.printables.CustomerUnderProcessShipmentsExcel;

import smarty.security.LoginUser;

/**
 * Servlet implementation class CustomerUnderDeliveryShipmentsExcelSRVL
 */
@WebServlet("/CustomerUnderDeliveryShipmentsExcelSRVL")
public class CustomerUnderDeliveryShipmentsExcelSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CustomerUnderDeliveryShipmentsExcelSRVL() {
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
		int customerId =Integer.parseInt(request.getParameter("customerId"));
		String dateRequest = "ALL";
		if (request.getParameter("c_created_date_only")!=null
			&& !request.getParameter("c_created_date_only").isEmpty()) {
			dateRequest = request.getParameter("c_created_date_only");
		}

		String docType = "xls";
		CustomerUnderProcessShipmentsExcel customerUnderProcessShipmentsExcel =  new CustomerUnderProcessShipmentsExcel();
		String contentType = "application/vnd.ms-excel";
		response.setContentType(contentType);
		
		String fileName = "customer_"+customerId+"_under_deliveryShipments.xls";
		response.setHeader("Content-Disposition","attachment;filename="+fileName);
		
		String ctxPath = this.getClass().getResource("/").getPath();
		ctxPath = ctxPath.replaceAll("WEB-INF/classes/", "");
		ctxPath = ctxPath.replaceAll("%20", " ");
		
		File file = customerUnderProcessShipmentsExcel.getManifestTable(customerId, 
				lu.getBranchCode(),
				dateRequest, ctxPath);
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
