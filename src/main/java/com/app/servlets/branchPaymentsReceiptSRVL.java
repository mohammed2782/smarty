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

import com.app.printables.BranchPaymentsReceiptPDF;

import smarty.security.LoginUser;

/**
 * Servlet implementation class branchPaymentsReceiptSRVL
 */
@WebServlet("/branchPaymentsReceiptSRVL")
public class branchPaymentsReceiptSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public branchPaymentsReceiptSRVL() {
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
			int trans_id =Integer.parseInt(request.getParameter("trans_id"));
			LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
			String docType = "pdf";
			BranchPaymentsReceiptPDF pdfRs =  new BranchPaymentsReceiptPDF();
			String contentType = "application/pdf";
			
			response.setContentType(contentType);
			String fileName = "paymentreciept_"+trans_id+"_.pdf"; 
			response.setHeader("Content-Disposition","attachment;filename="+fileName);
			
			String ctxPath = this.getClass().getResource("/").getPath();
			ctxPath = ctxPath.replaceAll("WEB-INF/classes/", "");
			ctxPath = ctxPath.replaceAll("%20", " ");
	
			File file = getFileExported (pdfRs, trans_id, lu.getBranchCode() , ctxPath);
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
	
		public File getFileExported(BranchPaymentsReceiptPDF a_pdfRs , int a_transId, int a_userBranch, String a_ctxPath){
		// move the file to be input stream.
		a_pdfRs.prepareDocument (a_transId, a_userBranch, a_ctxPath);
		// get the file location, and prepare file object refers to it.
		File file = null;
		try{ 
			file = new File(a_pdfRs.getDocPath());}
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
