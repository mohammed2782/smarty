package com.app.printables;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import com.app.cases.CaseInformation;
import com.app.util.Utilities;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;

import smarty.db.mysql;

import com.itextpdf.text.Image;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;


public class DriverManifestExcel {
	private  String docPath;
	private String docsDir;
	private String fullFilePathName;
	private String fileName;
	private Image image;
	private String imgPath;
	public String docDir="";// the document directory
	public String docLang = "en-US";// arabic kuwait
	public String docExtension = ".xls";
	String ctxPath = "";
	
public File getManifestTable( String driverId, String stgCode, String stpCode,  int storeCode, String fromdt, String todt,String ctxPath) throws SQLException{		
	String FILE_NAME = "manifest_"+driverId+"_"+storeCode+"_.xls";
	String imgPath = ctxPath+"/smartyresources/img/logo_krchsm.png";
	File file = null;
	this.ctxPath = ctxPath;
	docsDir = ctxPath+"/"+"ExcelDocs";
	fileName= "smarty_test"+docExtension;
	fullFilePathName = docsDir+"//"+fileName; 
	createDir(docsDir);
	try {
		Connection conn =null;
		
		
		ArrayList<CaseInformation> dlvs=null;
        Utilities ut = new Utilities();
        conn = mysql.getConn();
        dlvs = ut.getItemsPerDriver(conn,  driverId,  stgCode,  stpCode, storeCode, fromdt,todt);

    	HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        
     // header array
        String[] columns = {"تاريخ الشحنه", "قابل للكسر", "عدد", "ملاحظات", "رقم الهاتف","أسم المستلم", "رقم الشحنه","العنوان", "المبلغ المطلوب","رقم الوصل ", "إسم صاحب المحل" };
        Font headerFont = workbook.createFont();
        
     // header style 
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);      

        // Create a Row for header
        Row headerRow = sheet.createRow(0);
        for (int i2 = 0; i2 < columns.length; i2++) {
          Cell cell = headerRow.createCell(i2);
          cell.setCellValue(columns[i2]);
          cell.setCellStyle(headerCellStyle);
        }

     // set column width for each cell in the sheet 
        sheet.setColumnWidth(10, 4300);
        sheet.setColumnWidth(9, 4300);
        sheet.setColumnWidth(8, 3000);
        sheet.setColumnWidth(7, 3800);
        sheet.setColumnWidth(6, 7000);
        sheet.setColumnWidth(5, 3000);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(0, 3000);

        // applying style for cells width
        HSSFCellStyle cellStyle = workbook.createCellStyle();  
        cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        
     // Create all rows
     //   stream = response.getOutputStream();
        int colNum = 1;
        for (CaseInformation ci  : dlvs){
        	Row row2 = sheet.createRow(colNum++);
        	 Cell cell1 = row2.createCell(10);
        	cell1.setCellValue((String) ci.getSenderName());
        	cell1.setCellStyle(cellStyle );
	        cellStyle.setWrapText(true);
	        
        	Cell cell2 = row2.createCell(9);
        	cell2.setCellValue((String) ci.getCustReceiptNoOri());
        	cell2.setCellStyle(cellStyle );
	        cellStyle.setWrapText(true);
	        
        	Cell cell3 = row2.createCell(8);
        	 cell3.setCellValue((Double) ci.getReceiptAmt());
        	 cell3.setCellStyle(cellStyle );
 	        cellStyle.setWrapText(true);
 	        
        	Cell cell4 = row2.createCell(7);
        	 cell4.setCellValue((String) ci.getLocationDetails());
        	 cell4.setCellStyle(cellStyle );
 	        cellStyle.setWrapText(true);
 	        
        	Cell cell5 = row2.createCell(6);
        	 cell5.setCellValue((Integer) ci.getCaseid()); 
        	 cell5.setCellStyle(cellStyle );
 	        cellStyle.setWrapText(true);
 	        
        	 Cell cell6 = row2.createCell(5);
        	 cell6.setCellValue((String) ci.getReceiverName());
        	 cell6.setCellStyle(cellStyle );
 	        cellStyle.setWrapText(true);
 	        
        	 Cell cell7 = row2.createCell(4);
        	 cell7.setCellValue((String) ci.getReceiverHp1());
        	 cell7.setCellStyle(cellStyle );
 	        cellStyle.setWrapText(true);
 	        
        	 Cell cell8 = row2.createCell(3);
        	 cell8.setCellValue((String) ci.getRmk()); 
        	 cell8.setCellStyle(cellStyle );
 	        cellStyle.setWrapText(true);
 	        
        	 Cell cell9 = row2.createCell(2);
        	 cell9.setCellValue((Integer) ci.getQty());
        	 cell9.setCellStyle(cellStyle );
 	         cellStyle.setWrapText(true);
 	        
        	 Cell cell10 = row2.createCell(1);
        	 cell10.setCellValue((String) ci.getFragile());
        	 cell10.setCellStyle(cellStyle );
 	         cellStyle.setWrapText(true);
 	         
 	        Cell cell11 = row2.createCell(0);
 	        cell11.setCellValue((String) ci.getCreateddt());
 	        cell11.setCellStyle(cellStyle );
	        cellStyle.setWrapText(true);
 	        
        	}

            file = new File(fullFilePathName);
            FileOutputStream outputStream = new FileOutputStream(fullFilePathName);
         //   response.getOutputStream();
            workbook.write(outputStream);
            outputStream.close(); 
            conn.close();
        }catch (Exception e){
			e.printStackTrace();
		} 
        System.out.println("Done");	
        return file;
   }

public void createDir(String dirName){
    File theDir = new File(dirName);
    // if the directory does not exist, create it
    if (!theDir.exists()) {
        boolean result = false;
        try{
            theDir.mkdir();
            result = true;
        }catch(Exception e){
			String logErrorMsg = "class=>ExcelResults,Exception Msg=>"+e.getMessage(); 
			logErrorMsg = "";
            e.printStackTrace();
        }        
        if(!result) {    
            System.out.println("DIR Failed to be created");  
        }
    }
}
public void setImagePath(String imgPath){
	this.imgPath = imgPath;
	  try {
			 image = Image.getInstance(imgPath);
		} catch (BadElementException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        image.scaleAbsolute(PageSize.A4);
        image.setAbsolutePosition(0, 0);
        //PdfContentByte canvas = writer.getDirectContentUnder();
        image.scaleToFit(100,100);
}
	  	  
}
