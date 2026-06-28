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

import com.app.printables.MasterCustManifestPDF;

import smarty.security.LoginUser;

/**
 * Servlet implementation class PrintDriverManifest
 */
@WebServlet("/PrintMasterCustManifestSRVL")
public class PrintMasterCustManifestSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PrintMasterCustManifestSRVL() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		BufferedInputStream buf = null;
		ServletOutputStream stream = null;
		try{
			int userId = ((LoginUser) request.getSession().getAttribute("lu")).getUsid();
			int mastercustId = Integer.parseInt(request.getParameter("mastercustid"));
			int storeCode = Integer.parseInt(request.getParameter("storecode"));
			
			String fromdt ="ALL";
			String todt ="ALL";
			String states = "";
			if (request.getParameter("stdate")!=null && !request.getParameter("stdate").trim().isEmpty())
				fromdt = request.getParameter("stdate");
			if (request.getParameter("todate")!=null && !request.getParameter("todate").trim().isEmpty())
				todt = request.getParameter("todate");
			if (request.getParameter("states")!=null && !request.getParameter("states").trim().isEmpty())
				states = request.getParameter("states");
			
			if (!fromdt.equalsIgnoreCase("ALL")) {
				if (todt.equalsIgnoreCase("ALL"))
					todt = 	fromdt;
			}
		
			String docType = "pdf";
			MasterCustManifestPDF masterCustManifestPDF =  new MasterCustManifestPDF();
			String contentType = "application/pdf";
			
			response.setContentType(contentType);
			
			String fileName = "manifest_"+mastercustId+"_"+storeCode+"_.pdf";
			response.setHeader("Content-Disposition","attachment;filename="+fileName);
			
			String ctxPath = this.getClass().getResource("/").getPath();
			ctxPath = ctxPath.replaceAll("WEB-INF/classes/", "");
			ctxPath = ctxPath.replaceAll("%20", " ");
			
			File file = getFileExported (masterCustManifestPDF , mastercustId, storeCode, fromdt, todt, states, userId, ctxPath);
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
	
		public File getFileExported(MasterCustManifestPDF pdfRs , int mastercustId, int storeCode, 
				String fromdt, String todt, String states, int userId, String ctxPath){
		// move the file to be input stream.
		
		pdfRs.prepareDocument (mastercustId, storeCode, fromdt, todt, states, userId, ctxPath);
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
