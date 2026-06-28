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

import com.app.printables.CustomerReturnBackedUpPDF;

import smarty.security.LoginUser;

/**
 * Servlet implementation class AgentBackedPartialReturnSRVL
 */
@WebServlet("/CustomerBackedReturnSRVL")
public class CustomerBackedReturnSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CustomerBackedReturnSRVL() {
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
			int acr_id = 0;
			String regulator = request.getParameter("regulator");
			if(regulator.equalsIgnoreCase("frompickup")) {
				acr_id =Integer.parseInt(request.getParameter("pir_id"));
			}else {
				acr_id =Integer.parseInt(request.getParameter("acr_id"));
			}
			
			LoginUser lu = (LoginUser) request.getSession().getAttribute("lu");
			
			String docType = "pdf";
			CustomerReturnBackedUpPDF pdfRs =  new CustomerReturnBackedUpPDF();
			String contentType = "application/pdf";
			
			response.setContentType(contentType);
			String fileName = "";
			if(regulator.equalsIgnoreCase("frompickup")) 
				fileName = "return_pickup_"+acr_id+"_.pdf";
			else
				fileName = "return_customer_"+acr_id+"_.pdf"; 
			response.setHeader("Content-Disposition","attachment;filename="+fileName);
			
			String ctxPath = this.getClass().getResource("/").getPath();
			ctxPath = ctxPath.replaceAll("WEB-INF/classes/", "");
			ctxPath = ctxPath.replaceAll("%20", " ");

			File file = getFileExported (pdfRs, regulator, acr_id, lu.getBranchCode(), ctxPath);
			
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
		} catch (Exception  e) {
	    	e.printStackTrace();
	    	throw new ServletException(e.getMessage());
		} finally {			
			  if (stream != null){
				  stream.close();
			  }
		      if (buf != null){    
		    	  buf.close();
		      }
		   }
	}
	
	public File getFileExported(CustomerReturnBackedUpPDF pdfRs , String regulator,
				int apr_id, int userBranchId, String ctxPath)throws Exception{
		// move the file to be input stream.
		pdfRs.prepareDocument (regulator, apr_id, userBranchId, ctxPath);
		// get the file location, and prepare file object refers to it.
		File file = null;
		try{ 
			file = new File(pdfRs.getDocPath());}
		catch (Exception e){
			e.printStackTrace();
			throw e;
		}
		return file;
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
