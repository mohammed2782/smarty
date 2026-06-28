package com.app.printables;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;

import com.app.cases.CaseInformation;
import com.app.incomeoutcome.CustomerPaymentBean;
import com.app.util.Utilities;
import com.itextpdf.text.Font;

import smarty.db.mysql;

public class CustomerPaymentExcel {
	private CustomerPaymentBean customerPaymentBean;
	private  String docPath;
	private String docsDir;
	private String fullFilePathName;
	private String fileName;
	public String docDir="";// the document directory
	public String docLang = "en-US";// arabic kuwait
	public String docExtension = ".xls";
	String ctxPath = "";
	public File getManifestTable(int a_transId, int a_branchCode, String ctxPath) throws SQLException{		
	String FILE_NAME = "payment_customer_"+a_transId+".xls";
	File file = null;
	this.ctxPath = ctxPath;
	docsDir = ctxPath+"/"+"ExcelDocs";
	fileName= "smarty_test"+docExtension;
	fullFilePathName = docsDir+"//"+fileName; 
	createDir(docsDir);
	try {
		Connection conn =null;
        Utilities ut = new Utilities();
        conn = mysql.getConn();
        customerPaymentBean =  ut.getCustomerPaymentInfo(conn, a_transId);

    	HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        
     // header array
        String[] columns = {"تاريخ الشحنه","ملاحظات", "رقم الهاتف","العنوان",
        		"الصافي بعد التوصيل",  "مبلغ التوصيل", "مبلغ الوصل دولار" , "مبلغ الوصل د.ع","رقم الوصل ", "إسم صاحب المحل" };
        HSSFFont headerFont = workbook.createFont();
        
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
        sheet.setColumnWidth(9, 3800);
        sheet.setColumnWidth(8, 7000);
        sheet.setColumnWidth(7, 3000);
        sheet.setColumnWidth(6, 4000);
        sheet.setColumnWidth(5, 8000);
        sheet.setColumnWidth(4, 8000);
        sheet.setColumnWidth(3, 8000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(0, 3000);

        // applying style for cells width
        HSSFCellStyle cellStyle = workbook.createCellStyle();  
        cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        
     // Create all rows
     //   stream = response.getOutputStream();
        int colNum = 1;
        for (CaseInformation ci  : customerPaymentBean.getShipments()){
        	Row row = sheet.createRow(colNum++);
        	setCellData(row, 9, cellStyle, ci.getSenderName());
        	setCellData(row, 8, cellStyle, ci.getCustReceiptNoOri());
        	setCellData(row, 7, cellStyle, ci.getReceiptAmtIqd()+"");
        	setCellData(row, 6, cellStyle, ci.getReceiptAmtUsd()+"");
        	setCellData(row, 5, cellStyle, ci.getShipmentCharge()+"");
        	setCellData(row, 4, cellStyle, ci.getNetPrice()+"");
        	setCellData(row, 3, cellStyle, ci.getLocationDetails());
        	setCellData(row, 2, cellStyle, ci.getReceiverHp1());
        	setCellData(row, 1, cellStyle, ci.getRmk());
        	setCellData(row, 0, cellStyle, ci.getCreateddt());
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
	
	public void setCellData(Row a_row, int a_cellNo, HSSFCellStyle a_cellStyle, String a_text) {
		Cell cell = a_row.createCell(a_cellNo);
	    cell.setCellValue((String) a_text);
	    cell.setCellStyle(a_cellStyle);
	    a_cellStyle.setWrapText(true);
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
}
