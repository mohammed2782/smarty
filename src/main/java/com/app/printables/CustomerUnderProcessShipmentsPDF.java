package com.app.printables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.logging.Level;

import com.app.beans.MasterCustomerShipmentBackBean;
import com.app.cases.CaseInformation;
import com.app.printables.CustomerReturnBackedUpPDF.MyFooter;
import com.app.util.Utilities;
import com.app.util.UtilitiesFeqar;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import smarty.core.smartyLogAndErrorHandling;
import smarty.db.mysql;

public class CustomerUnderProcessShipmentsPDF {
	private Font fontHeaders;
	private  String docPath;
	private String docsDir;
	private String fullFilePathName;
	private String fileName;
	public String docDir="";// the document directory
	public String docLang = "en-US";// arabic kuwait
	public String docExtension = ".pdf";
	String ctxPath = "";
	public DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
	public static final Font FONT = new Font(FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYWHITE);
	File file = null;
	boolean newPage = true;
	 int totalStdNo=0;
	class MyFooter extends PdfPageEventHelper {
        Font ffont = new Font(Font.FontFamily.UNDEFINED, 6, Font.ITALIC);
        private String imgPath;
        private Image image;
        int pageNo = 0;
       
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
        
        public void onEndPage(PdfWriter writer, Document document) {
        	pageNo ++;
        	PdfContentByte cb = writer.getDirectContent();
            try {
            	if(pageNo == 1)
            		cb.addImage(image,PageSize.A4.getWidth()-540,0,0,50,30,document.top()-40);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Date dt = new Date();
            Phrase header = new Phrase(dt.toString(), ffont);
            Phrase footer = new Phrase("Page "+pageNo, ffont);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            		new Phrase(" ", ffont),
                    (document.right() - document.left())/3  + document.leftMargin(),
                    document.bottom() - 25, 0);
        }
        public void onStartPage(PdfWriter writer, Document document) {
        	
        	newPage = true;
        }
    }
	public File getManifestTable(int a_customerId, int a_branchCode, String a_dateRequest, String ctxPath) throws Exception{		
		this.ctxPath = ctxPath;
		docsDir = ctxPath+"/"+"PDFDocs";
		fileName= "smarty_test"+docExtension;
		fullFilePathName = docsDir+"//"+fileName;
		createDir(docsDir);
		setDocPath(fullFilePathName);
		Connection conn =null;
		try {
			conn = mysql.getConn();
			String imgPath = ctxPath+Utilities.getBranchLogoForPrinting(conn, a_branchCode);
			
			ArrayList<CaseInformation> shipments =  
					Utilities.getCustomerCasesUnderDelivery(conn, 
							a_customerId,
							a_dateRequest,
							a_branchCode);
			
			Rectangle pageSize = new Rectangle(PageSize.A4);
			pageSize.setBackgroundColor(new BaseColor(255, 255, 255 ));
			
			Document document = new Document(pageSize, 10, 10, 20, 10);
		    PdfWriter pdf = null;
		    MyFooter event = new MyFooter();
		    event.setImagePath(imgPath);
		    
		    try {
		    	pdf = PdfWriter.getInstance(document, new FileOutputStream(fullFilePathName));
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		    event.onStartPage(pdf, document);
		    pdf.setPageEvent(event);
		    pdf.getPageNumber();
		    document.open();		       
		    BaseFont baseFont = null;
			try {
				baseFont = BaseFont.createFont("../../Fonts/arial.ttf", BaseFont.IDENTITY_H, true);
			} catch (DocumentException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}catch (Exception e){
				System.out.println("at the font level");
				e.printStackTrace();
			}
			
	        Font font  = new Font(baseFont); 
	        fontHeaders  = new Font(baseFont); 
	        Font font2 = new Font();
	        font2.setSize(30);
	        
	        ColumnText ct = new ColumnText(pdf.getDirectContent());
	        String months[] = {"كانون ااثاني", "شباط", "أذار", "نيسان", "أيار", "حزيران", "تموز", "أب", "أيلول", 
	                "تشرين الأول", "تشرين الثاني", "كانون الأول"};
	        GregorianCalendar gcalendar = new GregorianCalendar();
	        int date = gcalendar.get(Calendar.DATE);
	        int year = gcalendar.get(Calendar.YEAR);
	        String arMonth = months[gcalendar.get(Calendar.MONTH)];
	        String datestr = Integer.toString(date)+" "+arMonth+" "+Integer.toString(year);
	       
	        ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	      
	        PdfPTable table = new  PdfPTable(1);
	        table.setWidthPercentage(100);
	       
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        PdfPCell cell;
	        Paragraph par;
            par = new Paragraph("تاريخ : "+datestr,font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(10);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            document.add(table);
            
            par = new Paragraph(" ",font);
            document.add(par);
            
	        table = getShipmentsTable(conn, shipments, font);
	        document.add(table);
	        
        	table = new PdfPTable(2);
        	table.setWidthPercentage(90);
        	font.setSize(12);
 	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
         	
        	document.add(table);
	        
	        document.close();
	        pdf.flush();
	        file = new File(fullFilePathName);
           
	        // if failed to produce pdf make the docPath null;
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		} catch(Exception e){
			System.out.println("general error");
			e.printStackTrace();
			throw e;
		}finally{
			try{conn.close();}catch(Exception e){}
		}
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
				String logErrorMsg = "class=>PDFResults,Exception Msg=>"+e.getMessage(); 
				smartyLogAndErrorHandling.logError("PDFResults", Level.SEVERE, logErrorMsg , e);
				logErrorMsg = "";
	            e.printStackTrace();
	        }        
	        if(!result) {    
	            System.out.println("DIR Failed to be created");  
	        }
	    }
	    
	}
	
	private  PdfPTable getShipmentsTable(Connection conn ,
			ArrayList<CaseInformation> a_shipments,Font font){
        PdfPTable table = null;
        try{
	        int noOfHeaders = 0;
	        float [] relativeWidths = null;
	        
	        noOfHeaders = 8;
	        relativeWidths = new float[noOfHeaders]; 
	        relativeWidths [0] = 12; // net
	        relativeWidths [1] = 11; // shipment charges
	        relativeWidths [2] = 9; // receipt amt usd
	        relativeWidths [3] = 9; // receipta amt
	        relativeWidths [4] = 10; // date 
	        relativeWidths [5] = 8; // custreceiptnoori
	        relativeWidths [6] = 15; // cust name
	        relativeWidths [7] = 5; // seq
	       	        
	        table = new PdfPTable(relativeWidths);
	        table.setPaddingTop(20);
	        table.setWidthPercentage(100);
	        
	        font.setSize(9.0f);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        // headers
	        //insertHeaders(PdfPTable table, LinkedList<String> headersList, Font font)
	        LinkedList<String> headers = new LinkedList<String>();
	        headers.add("#");
	        headers.add("أسم المتجر");
	        headers.add("رقم الوصل");
	        headers.add("تاريخ الأدخال");
	        headers.add("مبلغ د.ع");
	        headers.add("مبلغ$");
	        headers.add("مبلغ الشحن");
	        headers.add("الصافي للعميل د.ع");
	        
	        insertHeaders (table, headers, font);
	        font.setColor(0,0,0);
			table.setHeaderRows(1);
			Phrase ph; 
			PdfPCell cell;
			int i=1;
            boolean errorFlag = false;
            BaseColor bcOdd = new BaseColor(251, 193, 239); 
            BaseColor bcEven = new BaseColor(253, 253, 253); 
            BaseColor bc;
            long totalUsd = 0 , totalIqd = 0 , totalNetIqd = 0;
        	for (CaseInformation ci  : a_shipments){
        		if (i%2==1)
        			bc = bcOdd;
        		else
        			bc = bcEven;
            	ph = new Phrase(Integer.toString(i),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);

				ph = new Phrase(ci.getSenderName(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getCustReceiptNoOri(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getCreateddt(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(numFormat.format(ci.getReceiptAmtIqd()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(numFormat.format(ci.getReceiptAmtUsd()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				totalIqd += ci.getReceiptAmtIqd();
				totalUsd += ci.getReceiptAmtUsd();
				totalNetIqd += ci.getReceiptAmtIqd() - ci.getShipmentCharge();
				ph = new Phrase(numFormat.format(ci.getShipmentCharge()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(numFormat.format(ci.getReceiptAmtIqd() - ci.getShipmentCharge()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);

				i++;
        	}
        	ph = new Phrase("المجموع",font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(5);
			cell.setColspan(2);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(totalIqd) + " دينار عراقي",font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(5);
			cell.setColspan(2);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(totalUsd)+ " دولار أمريكي",font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(5);
			cell.setColspan(2);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(totalNetIqd)+ " الصافي دينار عراقي",font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(5);
			cell.setColspan(3);
			table.addCell(cell);
        }catch (Exception e){
			e.printStackTrace();
		}
        return table;
	}
	
	private void insertHeaders(PdfPTable table, LinkedList<String> headersList, Font font){
		Phrase ph;
    	PdfPCell cell;
    	BaseColor bcHeader = new BaseColor(75, 1, 73);
    	fontHeaders.setColor(255, 216, 243);
        for (String header : headersList){
            ph = new Phrase(header,fontHeaders);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(bcHeader);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setPaddingBottom(5);
			table.addCell(cell);		           
        }
	}
	public String getDocPath() {
		return docPath;
	}
	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}
}