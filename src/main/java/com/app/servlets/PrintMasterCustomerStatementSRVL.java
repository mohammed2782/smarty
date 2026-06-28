package com.app.servlets;

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

import com.app.printables.MasterCustomerStatementPDF;

/**
 * Servlet implementation class PrintMasterCustomerStatementSRVL
 */
@WebServlet("/PrintMasterCustomerStatementSRVL")
public class PrintMasterCustomerStatementSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PrintMasterCustomerStatementSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedInputStream buf = null;
		ServletOutputStream stream = null;
		try{
			int masterCustId = Integer.parseInt(request.getParameter("masterCustId"));
			String fromDate = request.getParameter("fromdate");
			String toDate = request.getParameter("todate");
			MasterCustomerStatementPDF pdfRs =  new MasterCustomerStatementPDF();
			String contentType = "application/pdf";
			
			response.setContentType(contentType);
			String fileName = "master_cust_account_statement_"+masterCustId+"_.pdf"; 
			response.setHeader("Content-Disposition","attachment;filename="+fileName);
			
			String ctxPath = this.getClass().getResource("/").getPath();
			ctxPath = ctxPath.replaceAll("WEB-INF/classes/", "");
			ctxPath = ctxPath.replaceAll("%20", " ");
	
			File file = getFileExported (pdfRs, masterCustId, fromDate, toDate, ctxPath);
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
			} finally {			
			  if (stream != null){
				  stream.close();
			  }
		      if (buf != null){    
		    	  buf.close();
		      }
		   }
		
	}
	
		public File getFileExported(MasterCustomerStatementPDF pdfRs, int masterCustId, String fromDate, String toDate, String ctxPath){
		// move the file to be input stream.
		pdfRs.prepareDocument (masterCustId, fromDate, toDate, ctxPath);
		// get the file location, and prepare file object refers to it.
		File file = null;
		try{ 
			file = new File(pdfRs.getDocPath());}
		catch (Exception e){e.printStackTrace();}
		return file;
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
