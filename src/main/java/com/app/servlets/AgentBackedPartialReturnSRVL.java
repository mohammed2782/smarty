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

import com.app.printables.AgentReturnedItmesPDF;

/**
 * Servlet implementation class AgentBackedPartialReturnSRVL
 */
@WebServlet("/AgentBackedPartialReturnSRVL")
public class AgentBackedPartialReturnSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AgentBackedPartialReturnSRVL() {
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
			int apr_id =Integer.parseInt(request.getParameter("apr_id"));
			int userBranchId =Integer.parseInt(request.getParameter("userbranch"));
			String docType = "pdf";
			AgentReturnedItmesPDF pdfRs =  new AgentReturnedItmesPDF();
			String contentType = "application/pdf";
			
			response.setContentType(contentType);
			String fileName = "partialreturn_agent_"+apr_id+"_.pdf"; 
			response.setHeader("Content-Disposition","attachment;filename="+fileName);
			
			String ctxPath = this.getClass().getResource("/").getPath();
			ctxPath = ctxPath.replaceAll("WEB-INF/classes/", "");
			ctxPath = ctxPath.replaceAll("%20", " ");
	
			File file = getFileExported (pdfRs, apr_id, userBranchId, ctxPath);
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
	
		public File getFileExported(AgentReturnedItmesPDF pdfRs , int apr_id, int userBranchId, String ctxPath){
		// move the file to be input stream.
		pdfRs.prepareDocument (apr_id, userBranchId, ctxPath);
		// get the file location, and prepare file object refers to it.
		File file = null;
		try{ 
			file = new File(pdfRs.getDocPath());}
		catch (Exception e){e.printStackTrace();}
		return file;
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
