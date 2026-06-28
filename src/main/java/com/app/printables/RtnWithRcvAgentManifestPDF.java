package com.app.printables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.logging.Level;

import com.app.cases.CaseInformation;
import smarty.core.smartyLogAndErrorHandling;
import smarty.db.mysql;

import com.app.util.Utilities;
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

public class RtnWithRcvAgentManifestPDF {
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
				//cb.addImage(image ,PageSize.A4.getWidth()-400,0,0,80,0,document.top()+20 );
            	cb.addImage(image,PageSize.A4.getWidth()-490,0,0,80,20,document.top());
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Date dt = new Date();
            Phrase header = new Phrase(dt.toString(), ffont);
            Phrase footer = new Phrase("Page "+pageNo, ffont);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    header,
                    (document.right() - document.left()) + document.leftMargin(),
                    document.top() +50, 0);
            
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            		new Phrase(" MDS (Modern Delivery Sys.), is a system developed by AlNafi3 soft, mohammed2782@gmail.com", ffont),
                    (document.right() - document.left())/3  + document.leftMargin(),
                    document.bottom() - 20, 0);
        }
        public void onStartPage(PdfWriter writer, Document document) {
        	
        	newPage = true;
        }
    }
	public void prepareDocument( String driverId, String stgCode, String stpCode, String storeCode, String fromdt, String ctxPath) {
		this.ctxPath = ctxPath;
		String imgPath = ctxPath+"/smartyresources/app-assets/images/logo/logo-sm.png";
		docsDir = ctxPath+"/"+"PDFDocs";
		fileName= "smarty_test"+docExtension;
		fullFilePathName = docsDir+"//"+fileName; 
		int i = 1;
		createDir(docsDir);
		setDocPath(fullFilePathName);
		Connection conn =null;
		Utilities ut = new Utilities();
		try {
			conn = mysql.getConn();
			
			Document document = new Document(PageSize.A4.rotate(), 50, 50, 85, 50);
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
				//baseFont = BaseFont.createFont("../../Fonts/ARIALUNI.TTF", BaseFont.IDENTITY_H, true);//this cause jvm out of memory
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
	        Font font2 = new Font();
	        font2.setSize(30);
	        
	        ColumnText ct = new ColumnText(pdf.getDirectContent());
	        String months[] = {"كانون الثاني", "شباط", "أذار", "نيسان", "أيار", "حزيران", "تموز", "أب", "أيلول", 
	                "تشرين الأول", "تشرين الثاني", "كانون الأول"};
	        GregorianCalendar gcalendar = new GregorianCalendar();
	        int date = gcalendar.get(Calendar.DATE);
	        int year = gcalendar.get(Calendar.YEAR);
	        String arMonth = months[gcalendar.get(Calendar.MONTH)];
	        String datestr = Integer.toString(date)+" "+arMonth+" "+Integer.toString(year);
	       
	        ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	      
	        Paragraph par = new Paragraph("بسم الله الرحمن الرحيم",font);
	        par.setAlignment(Element.ALIGN_CENTER);
	        ct.addElement(par);
 
	       
	        Rectangle rect = new Rectangle(/*starting x point*/30, /*starting y point*/810 , /*width*/600 , /*height*/70);
	        rect.setBorder(Rectangle.BOX);
	        ct.setSimpleColumn(rect);
	        ct.go();
	        
	        PdfPTable table = new  PdfPTable(1);
	        table.setWidthPercentage(100);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        PdfPCell cell;
	        
	        String driverName = "غير معروف";
	        if (!driverId.equalsIgnoreCase("NA"))
	        	driverName = ut.getDriverName(conn, driverId);
	       
	        par = new Paragraph("مندوب الأستلام :  "+driverName,font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(10);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            
            par = new Paragraph(" بتاريخ : "+datestr,font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(10);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
  
            par = new Paragraph("قائمه بالشحنات الراجعه ",font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(10);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
      
            document.add(table);
            
            par = new Paragraph(" ",font);
            document.add(par);
            
	        table = getManifestTable(conn, driverId , stgCode , stpCode, storeCode, fromdt ,font);
	        document.add(table);
	        
	        /*
        	table = new PdfPTable(2);
        	table.setWidthPercentage(90);
        	font.setSize(12);
 	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
         	
 	        Phrase ph;
         	ph = new Paragraph("توقيع أمين المخزن : ..................",font);
 			cell = new PdfPCell(ph);
 			cell.setPaddingTop(40);
 			cell.setPaddingBottom(20);
 			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            ph = new Paragraph("توقيع الناقل  : ..................",font);
 			cell = new PdfPCell(ph);
 			cell.setPaddingTop(40);
 			cell.setPaddingBottom(20);
 			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
        	document.add(table);
	        */
	        document.close();
	        pdf.flush();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch(Exception e){
			System.out.println("general error");
			e.printStackTrace();
		}finally{
			try{conn.close();}catch(Exception e){}
		}
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
	
	private  PdfPTable getManifestTable(Connection conn , String driverId, String stgCode, String stpCode,  String storeCode, 
			String fromdt, Font font){
		ArrayList<CaseInformation> dlvs=null;
      
        PdfPTable table = null;
        Utilities ut = new Utilities();
        try{
        	dlvs = ut.getRtnWithRcvAgent(conn,  driverId,  stgCode,  stpCode, storeCode, fromdt);
	      
	        int noOfHeaders = 0;
	        float [] relativeWidths = null;
	       
        	noOfHeaders = 11;
	        relativeWidths = new float[noOfHeaders]; 
	       
	        relativeWidths [0] = 20; // fragile, color red
	        relativeWidths [1] = 4; // qty
	        relativeWidths [2] = 18; // rmk
	        relativeWidths [3] = 12; // hp
	        relativeWidths [4] = 7; // rcv name
	        relativeWidths [5] = 10; // cid
	        relativeWidths [6] = 17; // address
	        relativeWidths [7] = 12; // amount to collect
	        relativeWidths [8] = 7; // receipt no ori
	        relativeWidths [9] = 15; // sender name
	        relativeWidths [10] = 5; // seq no
	        
	        table = new PdfPTable(relativeWidths);
	        table.setPaddingTop(20);
	        table.setWidthPercentage(110);
	        
	        font.setSize(10);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        // headers
        	Phrase ph;
        	PdfPCell cell;
            font.setSize(10);
        	ph = new Phrase("#",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
            table.addCell(cell);
  
            ph = new Phrase("إسم صاحب المحل",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
            table.addCell(cell);
            
            ph = new Phrase("رقم الوصل",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
            table.addCell(cell);
            
            ph = new Phrase("المبلغ المطلوب",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);
            
            ph = new Phrase("العنوان",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
            table.addCell(cell);
            
            ph = new Phrase("رقم الشحنه",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
            table.addCell(cell);
            
            ph = new Phrase("أسم المستلم",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);
            
            
            
            ph = new Phrase("رقم الهاتف",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);
			
			 ph = new Phrase("ملاحظات",font);
				cell = new PdfPCell(ph);
				cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell.setPaddingBottom(5);
				table.addCell(cell);
			
			
			
			ph = new Phrase("عدد",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);
			
				
			ph = new Phrase("تاريخ ووقت الأرجاع",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);
			
			table.setHeaderRows(1);

            int i=1;
            double amountToCollect = 0;
        	for (CaseInformation ci  : dlvs){
        		
            	ph = new Phrase(Integer.toString(i),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(ci.getSenderName(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(ci.getCustReceiptNoOri(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				//balance
				ph = new Phrase(numFormat.format(ci.getReceiptAmt()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(ci.getLocationDetails(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(Integer.toString(ci.getCaseid()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(ci.getReceiverName(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);

				
				
				ph = new Phrase(ci.getReceiverHp1(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				//rmk
				ph = new Phrase(ci.getRmk(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				
				
				//qty
				ph = new Phrase(Integer.toString(ci.getQty()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				
				ph = new Phrase(ci.getCreateddt(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				amountToCollect +=ci.getReceiptAmt();
				
				i++;
        	}
        	
        	

        }catch (Exception e){
			e.printStackTrace();
		}
        return table;
	}
	
	
	private void insertHeaders(PdfPTable table, LinkedList<String> headersList, Font font){
		Phrase ph;
    	PdfPCell cell;
        for (String header : headersList){
            ph = new Phrase(header,font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
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
