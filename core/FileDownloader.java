package com.app.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.app.db.mysql;
import com.app.site.security.LoginUser;

/**
 * Servlet implementation class FileDownloader
 */
@WebServlet("/FileDownloader")
public class FileDownloader extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int BUFFER_SIZE = 4096;	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileDownloader() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String className = request.getParameter("className");
		String fileCol = request.getParameter("fileCol");
		String keyVal = request.getParameter("keyVal");
		Class userClass;
		CoreMgr mgr = null;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String msg = "err";
		Blob fileData=null;
		try {
			userClass = Class.forName(className);
			mgr = (CoreMgr) userClass.newInstance();
			HttpSession session = request.getSession();
			LoginUser lu = (LoginUser) session.getAttribute("lu");
			if (mgr.keyCol!=null && !mgr.keyCol.isEmpty()){
				conn = mysql.getConn();
				mgr.setConn(conn);
				String fileNameTypeCol = mgr.userDefinedStoreFileNameColumns.get(fileCol);
				String fileNameType="";
				//System.out.println("select "+fileCol+" from "+mgr.mainTable+" where "+mgr.keyCol+"=?");
				pst = conn.prepareStatement("select "+fileCol+" , "+fileNameTypeCol+" from "+mgr.mainTable+" where "+mgr.keyCol+"=?");
				pst.setString(1, keyVal);
				rs = pst.executeQuery();
				if(rs.next()){
					fileData = rs.getBlob(fileCol);
					fileNameType = rs.getString(fileNameTypeCol);
				}
				String mimeType = request.getServletContext().getMimeType(fileNameType);
				InputStream inputStream = fileData.getBinaryStream();
				int fileLength = inputStream.available();
				response.setContentType(mimeType);
				//System.out.println("fileLength==>"+fileLength);
				response.setContentLength(fileLength);
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", fileNameType);
				response.setHeader(headerKey, headerValue);

				// writes the file to the client
				OutputStream outStream = response.getOutputStream();
				
				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesRead = -1;
				
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				
				inputStream.close();
				outStream.close();				
			}
						
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (InstantiationException | IllegalAccessException e){
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try{rs.close();}catch(Exception e){/*ignore*/}
			try{pst.close();}catch(Exception e){/*ignore*/}
			try{conn.close();}catch(Exception e){/*ignore*/}
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
