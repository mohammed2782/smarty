package com.app.printables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;

import com.app.incomeoutcome.CustomerStatementBean;
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
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class MasterCustomerStatementPDF {
	private Font fontHeaders;
	private  String docPath;
	private String docsDir;
	private String fullFilePathName;
	private String fileName;
	public String docDir="";// the document directory
	public String docLang = "en-US";// arabic kuwait
	public String docExtension = ".pdf";
	String ctxPath = "";
	LinkedList<CustomerStatementBean> csbList;
	public DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
	protected DateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy");
	public static final Font FONT = new Font(FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYWHITE);

	boolean newPage = true;
	 int totalShimpments=0;
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
            	cb.addImage(image,PageSize.A4.getWidth()-540,0,0,30,40,document.top()+30);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Date dt = new Date();
            Phrase header = new Phrase(dt.toString(), ffont);
            Phrase footer = new Phrase("Page "+pageNo, ffont);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    header,
                    (document.right() - document.left())/6 -5,
                    document.bottom() - 10, 0);
            
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) /2 +document.leftMargin(),
                    document.bottom() - 10, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            		new Phrase(" ", ffont),
                    (document.right() - document.left())/3 +20,
                    document.bottom()-20 , 0);
        }
        public void onStartPage(PdfWriter writer, Document document) {
        	
        	newPage = true;
        }
    }
	public void prepareDocument( int masterCustId, String fromDate, String toDate, String ctxPath) {
		this.ctxPath = ctxPath;
		String imgPath = ctxPath+"/smartyresources/app-assets/images/logo/logo-sm.png";
		docsDir = ctxPath+"/"+"PDFDocs";
		fileName= "smarty_test"+docExtension;
		fullFilePathName = docsDir+"//"+fileName; 
		createDir(docsDir);
		setDocPath(fullFilePathName);
		Connection conn =null;
		Utilities ut = new Utilities();
		try {
			conn = mysql.getConn();
			csbList = new LinkedList<CustomerStatementBean>();
			csbList =  ut.getMasterCustomerStatement(conn, masterCustId, fromDate, toDate);
			 
			Rectangle pageSize = new Rectangle(PageSize.A4);
			//pageSize.setBackgroundColor(new BaseColor(245, 245, 245));
			Document document = new Document(pageSize.rotate(), 50, 50, 80, 30);
			
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
	        fontHeaders  = new Font(baseFont);
	        Font numberFont = new Font(baseFont);
	        Font font2 = new Font(baseFont);
	        Font font3 = new Font(baseFont);
	        Font fontCredit = new Font(baseFont);
	        Font fontDebit = new Font(baseFont);
	        Font font5 = new Font(baseFont);
	        Font font4 = new Font(baseFont);
	        Font tabsTitle = new Font(baseFont);
	        tabsTitle.setSize(11);
	        tabsTitle.setColor(new BaseColor(252, 252,252));
	        
	        numberFont.setSize(8);
	        numberFont.setColor(new BaseColor(11, 11,11));
	        
	        font2.setSize(10);
	        font2.setColor(new BaseColor(11, 11,11));
	        font2.setStyle(2);
	        
	        font3.setSize(10);
	        font3.setColor(new BaseColor(252, 252,252));
	        font3.setStyle(2);
	        
	        fontCredit.setSize(10);
	        fontCredit.setColor(new BaseColor(0, 117, 0));
	        fontCredit.setStyle(2);
	        
	        fontDebit.setSize(10);
	        fontDebit.setColor(new BaseColor(200, 0, 0));
	        fontDebit.setStyle(2);
	        
	        font5.setSize(11);
	        font5.setColor(new BaseColor(252, 252,252));
	        font5.setStyle(3);
	        ColumnText ct = new ColumnText(pdf.getDirectContent());

	        ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        
	        font4.setSize(16);
	        font4.setStyle(1);
	        //font4.setColor(new BaseColor(92, 38,140));
	        font4.setColor(new BaseColor(252, 252,252));
	        Paragraph par = new Paragraph("كشف حساب العميل",font4);
	        par.setAlignment(Element.ALIGN_CENTER);
	       // ct.addElement(par);
 
	        
	        Rectangle rect = new Rectangle(/*starting x point*/10, /*starting y point*/550 , /*width*/850 , /*height*/100);
	        rect.setBorder(Rectangle.BOX);
	        ct.setSimpleColumn(rect);
	        ct.go();
	        
	        PdfPCell cell;
	        PdfPTable table;
	        Paragraph ph;
	        float [] relativeWidths = null;
        	
	        table = new  PdfPTable(3);
	        
	        PdfPTable tableShipments ;
	        table.setWidthPercentage(100);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);	       
	        String custName = ut.getMasterCustomerName(conn, masterCustId);
	        BaseColor bc = new BaseColor(59,174,218); 
	        par = new Paragraph("السيد: "+custName,font);
	        cell = new PdfPCell(par);
 			//cell.setPaddingTop(0);
 			//cell.setPaddingBottom(5);
 			cell.setPadding(15);
 			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("كشف حساب",font);
	        cell = new PdfPCell(par);
 			//cell.setPaddingTop(0);
 			//cell.setPaddingBottom(5);
 			cell.setPadding(15);
 			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
             
            par = new Paragraph(" ",font);
	        cell = new PdfPCell(par);
 			//cell.setPaddingTop(0);
 			//cell.setPaddingBottom(5);
 			cell.setPadding(15);
 			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            document.add(table);
           
        	table = new PdfPTable(1);
        	table.setWidthPercentage(100);
        	font.setSize(12);
 	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL); 
            ph = new Paragraph(" ",font);
 			cell = new PdfPCell(ph);
 			cell.setPaddingTop(-4);
 			cell.setPaddingBottom(-5);
 			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
        	document.add(table);
           
            // table for the statement
            BaseColor bcOdd = new BaseColor(243,243,243); 
            BaseColor bcEven = new BaseColor(255,255,255); 
            
            relativeWidths = new float[10]; 
            relativeWidths [0] = 25; // status
        	relativeWidths [1] = 13; // status
	        relativeWidths [2] = 13; // qty
	        relativeWidths [3] = 12; // date
	        relativeWidths [4] = 13; // credit
	        relativeWidths [5] = 13; // debit
	        relativeWidths [6] = 15; // actually paid
	        relativeWidths [7] = 15; // #//total receipts amt
	        relativeWidths [8] = 20; // #tranname
	        relativeWidths [9] = 5; // #
	        
	        table = new PdfPTable(relativeWidths);
	        table.setPaddingTop(5);
        	table.setWidthPercentage(105);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        LinkedList<String> headers = new LinkedList<String>();
	        BaseColor bcHeader = new BaseColor(29,43,54);
	        BaseColor borderRight = new BaseColor(183,183,183);
	        headers.add("ت");
	        headers.add("العمليه");
	        headers.add("مبلغ الوصولات");
	        headers.add("المبلغ المسدد للعميل");
	        headers.add("مدين");
	        headers.add("دائن");
	        headers.add("تاريخ");
	        headers.add("رصيد مدين");
	        headers.add("رصيد دائن");
	        headers.add("ملاحظات");
	        insertHeaders (table, headers, font, bcHeader);
	        font.setColor(0,0,0);
	        font3.setColor(0,0,0);
	        int i = 0;
	        double balanceDebit  = 0;
	        double balanceCredit  = 0;
	        int padding = 7;
	        for (CustomerStatementBean csb : csbList) {
	        	i++;
	        	bc = bcOdd;
	        	if (i%2 ==1)
	        		bc = bcEven;
	        	
        	    par = new Paragraph(i+"",numberFont);
		        cell = new PdfPCell(par);
		        cell.setPadding(5);
	 			cell.setBackgroundColor(bc);
	 			cell.setVerticalAlignment(Element.ALIGN_CENTER);
	 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	            table.addCell(cell);
	        	
		        par = new Paragraph(csb.getTranName(),font2);
		        cell = new PdfPCell(par);
		        cell.setPadding(padding);
	 			cell.setBackgroundColor(bc);
	            table.addCell(cell);
	            
	            par = new Paragraph(numFormat.format(csb.getTotalReceiptsAmt()),font2);
		        cell = new PdfPCell(par);
		        cell.setPadding(padding);
	 			cell.setBackgroundColor(bc);
	            table.addCell(cell);
	            
	            par = new Paragraph(numFormat.format(csb.getAmtPaidActually()),font2);
		        cell = new PdfPCell(par);
		        cell.setPadding(padding);
	 			cell.setBackgroundColor(bc);
	            table.addCell(cell);
	            
	            
	            
	            par = new Paragraph(numFormat.format(csb.getDebit()),fontDebit);
		        cell = new PdfPCell(par);
		        cell.setPadding(padding);
	 			cell.setBackgroundColor(bc);
	 			table.addCell(cell);
	 	        
	 	        par = new Paragraph(numFormat.format(csb.getCredit()),fontCredit);
		        cell = new PdfPCell(par);
		        cell.setPadding(padding);
	 			cell.setBackgroundColor(bc);
	 			table.addCell(cell);
	 	       
	 			par = new Paragraph(csb.getTranDate(),font3);
		        cell = new PdfPCell(par);
		        cell.setPadding(padding);
	 			cell.setBackgroundColor(bc);
	            table.addCell(cell);
	            
	            balanceDebit = 0;
	            balanceCredit = 0;
	            if (csb.getBalance() <=0)
	            	balanceDebit = -1*csb.getBalance();
	            else
	            	balanceCredit = csb.getBalance();
	            
	            par = new Paragraph(numFormat.format(balanceDebit),fontDebit);
		        cell = new PdfPCell(par);
		        cell.setPadding(padding);
	 			cell.setBackgroundColor(bc);
	            table.addCell(cell);
	            
	            par = new Paragraph(numFormat.format(balanceCredit),fontCredit);
		        cell = new PdfPCell(par);
		        cell.setPadding(padding);
	 			cell.setBackgroundColor(bc);
	 			table.addCell(cell);
	 			
	 			par = new Paragraph(csb.getRmk(),font2);
		        cell = new PdfPCell(par);
		        cell.setPadding(padding);
	 			cell.setBackgroundColor(bc);
	            table.addCell(cell);
	        }
           
	        
            document.add(table);
	        
	        
        	
	        
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
	
	
	
	
	private void insertHeaders(PdfPTable table, LinkedList<String> headersList, Font font, BaseColor bcHeader ){
		Phrase ph;
    	PdfPCell cell;
    	
    	fontHeaders.setColor(252, 252,252);
    	fontHeaders.setSize(12);
    	
        for (String header : headersList){
            ph = new Phrase(header,fontHeaders);
			cell = new PdfPCell(ph);
			cell.setPaddingTop(5);
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

	class RoundRectangle implements PdfPCellEvent {
	    public void cellLayout(PdfPCell cell, Rectangle rect,
	            PdfContentByte[] canvas) {
	        PdfContentByte cb = canvas[PdfPTable.LINECANVAS];
	       
	        cb.roundRectangle(
	            rect.getLeft()+1.5f, rect.getBottom(), rect.getWidth(),
	            rect.getHeight(), 4);
	        cb.setRGBColorFill(59,174,218);
	        cb.fill();
	       
	    }
	}
}
