package com.app.cust.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.cust.printables.DeliveryOrderPDF;
import com.app.util.UtilitiesFeqar;

import smarty.db.mysql;
import smarty.security.LoginUser;

import java.sql.Connection;

/**
 * Servlet implementation class PrintDeliveryOrderPdfSRVL
 */
@WebServlet("/PrintDeliveryOrderPdfSRVL")
public class PrintDeliveryOrderPdfSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PrintDeliveryOrderPdfSRVL() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		BufferedInputStream buf = null;
		ServletOutputStream stream = null;
		UtilitiesFeqar utf = new UtilitiesFeqar();
		Connection conn1 = null;
		try {
			int manifestId = 0;
			String casesToPrint = request.getParameter("casesToPrint");
			String printedby = request.getParameter("printedby");
			int mastercustomerid = Integer.parseInt(request.getParameter("mastercustomerid"));

			try {
				conn1 = mysql.getConn();
				manifestId = utf.getMnifestIdForPickupAgent(conn1, casesToPrint);
			} catch (Exception e) {
				try {
					conn1.rollback();
				} catch (Exception eRoll) {
					/**/}
				e.printStackTrace();
			} finally {
				try {
					conn1.close();
				} catch (Exception e) {
				}
			}

			String docType = "pdf";
			DeliveryOrderPDF deliveryManifestPdf = new DeliveryOrderPDF();
			String contentType = "application/pdf";

			response.setContentType(contentType);

			String fileName = "pickupmanifest_" + manifestId + ".pdf";
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

			String ctxPath = this.getClass().getResource("/").getPath();
			ctxPath = ctxPath.replaceAll("WEB-INF/classes/", "");
			ctxPath = ctxPath.replaceAll("%20", " ");

			File file = getFileExported(deliveryManifestPdf, mastercustomerid, printedby, casesToPrint, manifestId, ctxPath);
			response.setContentLength((int) file.length());
			FileInputStream fileIn = new FileInputStream(file);

			buf = new BufferedInputStream(fileIn);
			int readBytes = 0;
			stream = response.getOutputStream();

			while ((readBytes = buf.read()) != -1)// while there is still data in the buffer.loops byte by byte
				stream.write(readBytes);// write it.

		} catch (Exception ioe) {

			ioe.printStackTrace();
			throw new ServletException(ioe.getMessage());
		} finally {

			if (stream != null) {
				stream.close();
			}
			if (buf != null) {
				buf.close();
			}

		}

	}

	public File getFileExported(DeliveryOrderPDF pdfRs, int mastercustomerid, String printedby, String casesToPrint, int manifestId, String ctxPath) throws Exception {
		// move the file to be input stream.

		pdfRs.prepareDocument(mastercustomerid, printedby, casesToPrint, manifestId, ctxPath);
		// get the file location, and prepare file object refers to it.
		File file = null;
		try {
			file = new File(pdfRs.getDocPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}