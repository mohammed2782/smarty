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

import com.app.printables.PrintReturnBranchShipmentsByManifestRtnId;

import smarty.security.LoginUser;

/**
 * Servlet implementation class PrintReturnBranchShipmentsByRtnManifestIdSRVL
 */
@WebServlet("/PrintReturnBranchShipmentsByRtnManifestIdSRVL")
public class PrintReturnBranchShipmentsByRtnManifestIdSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PrintReturnBranchShipmentsByRtnManifestIdSRVL() {
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
			int liasionAgentId =Integer.parseInt(request.getParameter("liaisonagentid"));
			int rtnManifestId =Integer.parseInt(request.getParameter("rtnmanifestid"));
		
			PrintReturnBranchShipmentsByManifestRtnId printReturnBranchShipmentsByManifestRtnId =  new PrintReturnBranchShipmentsByManifestRtnId();
			String contentType = "application/pdf";
			
			response.setContentType(contentType);
			
			String fileName = "returnmanifestid_"+rtnManifestId+"_.pdf";
			response.setHeader("Content-Disposition","attachment;filename="+fileName);
			
			String ctxPath = this.getClass().getResource("/").getPath();
			ctxPath = ctxPath.replaceAll("WEB-INF/classes/", "");
			ctxPath = ctxPath.replaceAll("%20", " ");
			
			File file = getFileExported (printReturnBranchShipmentsByManifestRtnId , liasionAgentId, rtnManifestId, ctxPath);
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
	
		public File getFileExported(PrintReturnBranchShipmentsByManifestRtnId pdfRs, int liasionAgentId,int rtnManifestId, String ctxPath){
		// move the file to be input stream.
		
		pdfRs.prepareDocument (liasionAgentId, rtnManifestId , ctxPath);
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
