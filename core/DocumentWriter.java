package com.app.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class DocumentWriter
 */
@WebServlet("/DocumentWriter")
public class DocumentWriter extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DocumentWriter() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedInputStream buf = null;
		ServletOutputStream stream = null;
		try{
			
			Map <String , String[]> dataToExport = request.getParameterMap();
			String docType = "";
			
			//new String (s.getBytes ("iso-8859-1"), "UTF-8");
			//System.out.println("this is writer===>"+new String (dataToExport.get("smartyrownum_1_smartycol_us_name_ar")[0].getBytes("iso-8859-1"),"UTF-8"));
			if (request.getParameter("docx")!=null){
				docType = "docx";
			}else if (request.getParameter("pdf")!=null){
				docType = "pdf";
			}
			//System.out.println(dataToExport);
			String cols = request.getParameter("smartycoltoexport");
			String exportLandScape = request.getParameter("smarty_userDefinedExportLandScape");
			boolean landscape = false;
			//System.out.println("exportLandScape===>"+exportLandScape);
			if (exportLandScape.equalsIgnoreCase("true"))
				landscape = true;
				
			String[] arabiColsArr = new String[0];
			if (request.getParameter("arabicsmartycoltoexport")!=null)
				arabiColsArr = request.getParameter("arabicsmartycoltoexport").split(",");
			ArrayList <String> arabicColsList = new ArrayList<String>();
			for (int i = 0 ; i<arabiColsArr.length ; i++){
				arabicColsList.add(arabiColsArr[i]);
				
			}
			
			String[] colsArr = cols.split(",");
			LinkedHashMap<String , String > colLabel = new LinkedHashMap<String , String>();
			ArrayList<String> colsList = new ArrayList<String>(); 
			for (int i = 0 ; i<colsArr.length ; i++){
				colsList.add(colsArr[i]);
				colLabel.put(colsArr[i], request.getParameter(colsArr[i]+"_collabel").replace("</br>","").replace("</BR>",""));
			}
			int rowsToExport = Integer.parseInt(request.getParameter("UserDefinedPageRows"));
			String contentType="";
			FilesExport  fe=null;
			if (docType.equalsIgnoreCase("docx")){
				fe =  new MicrosoftWordExporter();
				contentType = "application/msword";
			}else if (docType.equalsIgnoreCase("pdf")){
				fe =  new PDFExporter();
				contentType = "application/pdf";
			}
			response.setContentType(contentType);
			
			String userDefinedCaption = request.getParameter("userDefinedCaption").replace("</br>","").replace("</BR>","");
			
			String fileName = request.getParameter("userDefinedCaption").replace(" ", "_").replace("</br>","").replace("</BR>","");
			
			fileName +="."+docType;
			fileName= URLEncoder.encode(fileName,"UTF-8");
			
			// name to be shown in the dialogue box 
			response.setHeader("Content-Disposition","attachment;filename*=UTF-8''"+fileName);
			
			File file = getFileExported(fe , dataToExport , rowsToExport , colsList , colLabel , 
					arabicColsList , request.getRealPath("/") , landscape,userDefinedCaption);
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
	public File getFileExported(FilesExport fe , Map<String, String[]> dataToExport , 
				int noOfRows ,ArrayList<String> colsList,
				HashMap<String,String> colLabel, ArrayList<String> arabicColsList ,
				String ctxPath , boolean landscape , String userDefinedCaption){
		// move the file to be input stream. 
		
		fe.prepareDocument(dataToExport , noOfRows , colsList , colLabel , arabicColsList,ctxPath , landscape , userDefinedCaption);
		// get the file location, and prepare file object refers to it.
		File file = new File(fe.getDocPath());
		return file;
	}

}
