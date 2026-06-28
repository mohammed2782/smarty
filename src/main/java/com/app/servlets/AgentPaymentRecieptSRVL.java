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

import com.app.printables.AgentPaymentRecieptPDF;

import smarty.security.LoginUser;

/**
 * Servlet implementation class PaymentRecieptSRVL
 */
@WebServlet("/AgentPaymentReceiptSRVL")
public class AgentPaymentRecieptSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AgentPaymentRecieptSRVL() {
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
			int transId =Integer.parseInt(request.getParameter("trans_id"));
			String fileName = "paymentreciept_agent_"+transId+"_.pdf"; 
			String docType = "pdf";
			AgentPaymentRecieptPDF pdfRs =  new AgentPaymentRecieptPDF();
			String contentType = "application/pdf";
			
			response.setContentType(contentType);
			
			response.setHeader("Content-Disposition","attachment;filename="+fileName);
			
			String ctxPath = this.getClass().getResource("/").getPath();
			ctxPath = ctxPath.replaceAll("WEB-INF/classes/", "");
			ctxPath = ctxPath.replaceAll("%20", " ");
			LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
			
			File file = getFileExported (pdfRs, transId, lu.getBranchCode() , ctxPath);
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
	
		public File getFileExported(AgentPaymentRecieptPDF a_pdfRs , int a_transId, int a_branchCode, String a_ctxPath){
		// move the file to be input stream.
		a_pdfRs.prepareDocument (a_transId, a_branchCode, a_ctxPath);
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
