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

import com.app.printables.CustomerUnderProcessShipmentsPDF;
import com.app.printables.ReturnBetweenBranchesPDF;

import smarty.security.LoginUser;

/**
 * Servlet implementation class PrintReturnedBetweenBranchesSRVL
 */
@WebServlet("/PrintReturnedBetweenBranchesSRVL")
public class PrintReturnedBetweenBranchesSRVL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PrintReturnedBetweenBranchesSRVL() {
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
			
			int ccRtnManifestId =Integer.parseInt(request.getParameter("cc_rtnmanifestid"));
			String docType = "pdf";
			ReturnBetweenBranchesPDF returnBetweenBranchesPDF = new ReturnBetweenBranchesPDF();
			String contentType = "application/pdf";
			response.setContentType(contentType);
			
			String fileName = "Returned_"+ccRtnManifestId+"_between_branches.pdf";
			response.setHeader("Content-Disposition","attachment;filename="+fileName);
			
			String ctxPath = this.getClass().getResource("/").getPath();
			ctxPath = ctxPath.replaceAll("WEB-INF/classes/", "");
			ctxPath = ctxPath.replaceAll("%20", " ");
			
			File file = returnBetweenBranchesPDF.getManifestTable(ccRtnManifestId, lu.getBranchCode(), ctxPath);
			response.setContentLength((int) file.length());
			FileInputStream fileIn = new FileInputStream(file);
			
		    buf = new BufferedInputStream(fileIn);
		    int readBytes = 0;
		    stream = response.getOutputStream();
		    while ((readBytes = buf.read()) != -1)// while there is still data in the buffer.loops byte by byte
			    stream.write(readBytes);//write it.
		}catch (Exception e) {
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
		
	}

}
