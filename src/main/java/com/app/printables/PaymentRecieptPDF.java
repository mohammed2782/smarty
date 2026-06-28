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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;

import com.app.cases.CaseInformation;
import com.app.financials.FinOperationCode;
import com.app.financials.FinOperationEntity;
import com.app.financials.StandardFinCurrency;
import com.app.incomeoutcome.CustomerPaymentBean;
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


public class PaymentRecieptPDF {
	private Font fontHeaders;
	private  String docPath;
	private String docsDir;
	private String fullFilePathName;
	private String fileName;
	public String docDir="";// the document directory
	public String docLang = "en-US";// arabic kuwait
	public String docExtension = ".pdf";
	String ctxPath = "";
	CustomerPaymentBean customerPaymentBean;
	public DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
	public static final Font FONT = new Font(FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYWHITE);
	 BaseFont baseFont = null;
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
            	cb.addImage(image,PageSize.A4.getWidth()-540,0,0,50,30,document.top()+30);
            	//cb.addImage(image,PageSize.A4.getWidth()-540,0,0,20,40,document.top()+30);
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
            
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            		new Phrase(" ", ffont),
                    (document.right() - document.left())/3  + document.leftMargin(),
                    document.bottom() - 20, 0);
        }
        public void onStartPage(PdfWriter writer, Document document) {
        	
        	newPage = true;
        }
    }
	public void prepareDocument(int a_transId, String a_ctxPath) {
		this.ctxPath = a_ctxPath;
		docsDir = ctxPath+"/"+"PDFDocs";
		fileName= "smarty_test"+docExtension;
		fullFilePathName = docsDir+"//"+fileName; 
		createDir(docsDir);
		setDocPath(fullFilePathName);
		Connection conn =null;
		Utilities ut = new Utilities();
		try {
			conn = mysql.getConn();
			customerPaymentBean =  ut.getCustomerPaymentInfo(conn, a_transId);
			String imgPath = ctxPath+Utilities.getBranchLogoForPrinting
					(conn, customerPaymentBean.getStandardTransactionBean().getInitiatedInBranchId());
			Rectangle pageSize = new Rectangle(PageSize.A4.rotate());
			//pageSize.setBackgroundColor(new BaseColor(245, 245, 245));
			Document document = new Document(pageSize, 50, 50, 85, 50);
			
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
	        Font font2 = new Font(baseFont);
	        Font font4 = new Font(baseFont);
	        font2.setSize(12);
	        font2.setColor(new BaseColor(252, 252,252));
	        font2.setStyle(1);
	        
	        ColumnText ct = new ColumnText(pdf.getDirectContent());

	        ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        
	        font4.setSize(16);
	        font4.setStyle(1);
	        //font4.setColor(new BaseColor(92, 38,140));
	        font4.setColor(new BaseColor(252, 252,252));
	        Paragraph par = new Paragraph("كشف حساب  الواصل للعميل",font4);
	        par.setAlignment(Element.ALIGN_CENTER);
	       
	        Rectangle rect = new Rectangle(/*starting x point*/10, /*starting y point*/570 , /*width*/850 , /*height*/100);
	        rect.setBorder(Rectangle.BOX);
	        ct.setSimpleColumn(rect);
	        ct.go();
	        PdfPTable tableShipments ;
	        tableShipments = getShipmentsTable(conn,font);
	        
	        float [] relativeWidths = null;
        	int noOfHeaders = 5;
	        relativeWidths = new float[noOfHeaders]; 
	       
	        relativeWidths [0] = 20; // status
	        relativeWidths [1] = 10; // qty
	        relativeWidths [2] = 30; // net amount
	        relativeWidths [3] = 20;
	        relativeWidths [4] = 15; 
	        PdfPTable table = new  PdfPTable(relativeWidths);
	        
	        table.setWidthPercentage(110);
	       
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        PdfPCell cell;
	       
	        BaseColor bc = new BaseColor(0, 103, 0); 
	        par = new Paragraph("إسم العميل: ",font2);
	        cell = new PdfPCell(par);
 			//cell.setPaddingTop(0);
 			//cell.setPaddingBottom(5);
 			cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            String entityName = "وصولات مدفوعة التوصيل";
            if (customerPaymentBean.getStandardTransactionBean().getEntityId()>0) {
            	entityName =  Utilities.getMasterCustomerName(conn, customerPaymentBean.getStandardTransactionBean().getEntityId());
            }
            par = new Paragraph(entityName,font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            if (customerPaymentBean.getStandardTransactionBean().getEntityId()>0) {
            	par = new Paragraph("كشف حساب الواصل للعميل",font4);
            }else {
            	par = new Paragraph("",font4);
            }
	        cell = new PdfPCell(par);
 			cell.setBackgroundColor(bc);
 			cell.setPadding(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
 
            
            
            par = new Paragraph("تاريخ العملية: ",font2);
	        cell = new PdfPCell(par);
 			cell.setBackgroundColor(bc);
 			cell.setPadding(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph(customerPaymentBean.getStandardTransactionBean().getCreatedDateTime(),font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("عدد الشحنات:",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph(""+numFormat.format(totalShimpments),font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("",font);
	        cell = new PdfPCell(par);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);

            par = new Paragraph("إيصال دفع رقم: ",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            par = new Paragraph(""+a_transId,font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
             
            
            
            document.add(table);
            par = new Paragraph(" ",font);
            document.add(par);
            
            Font fontSummaryTable = new Font(baseFont);
            fontSummaryTable.setSize(12);
            fontSummaryTable.setColor(new BaseColor(10,10,10));
            fontSummaryTable.setStyle(2);
            document.add(getSummaryTable(conn,  
            		customerPaymentBean.getStandardTransactionBean().getInitiatedInBranchId(),
            		fontSummaryTable));
            par = new Paragraph(" ",font);
            document.add(par);
            
	        document.add(tableShipments);
	        document.close();
	        pdf.flush();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch(Exception e){
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
	
	private  PdfPTable getShipmentsTable(Connection conn ,Font font){
		ArrayList<CaseInformation> shipments=null;
	      
        PdfPTable table = null;
       
        try{
        	shipments = customerPaymentBean.getShipments();
	      
	        int noOfHeaders = 0;
	        float [] relativeWidths = null;
	       
        	noOfHeaders = 12;
	        relativeWidths = new float[noOfHeaders]; 
	       
	        relativeWidths [0] = 12; // notes
	        relativeWidths [1] = 8; // date
	        relativeWidths [2] = 18; // qty
	        relativeWidths [3] = 8; // status
	        relativeWidths [4] = 8; // net
	        relativeWidths [5] = 8; // shipment cost
	        relativeWidths [6] = 8; // receipt amt
	        relativeWidths [7] = 11; // hp
	        relativeWidths [8] = 13; // address
	        relativeWidths [9] = 8; // receipt no 
	        relativeWidths [10] = 12; // receipt no
	        relativeWidths [11] = 5; // seq
	        
	        table = new PdfPTable(relativeWidths);
	        table.setPaddingTop(20);
	        table.setWidthPercentage(110);
	        
	        font.setSize(9);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        // headers
	        //insertHeaders(PdfPTable table, LinkedList<String> headersList, Font font)
	        LinkedList<String> headers = new LinkedList<String>();
	        headers.add("#");
	        headers.add("المتجر");
	        headers.add("رقم الوصل");
	        headers.add("العنوان");
	        headers.add("رقم الهاتف");
	        headers.add("مبلغ الوصل د.ع");
	        headers.add("مبلغ الوصل$");
	        headers.add("أجرة التوصيل");
	        headers.add("الصافي للعميل");
	        headers.add("الحاله");
	        headers.add("تاريخ الأدخال");
	        headers.add("ملاحظات");
	        
	       
	        insertHeaders (table, headers, font);
	        font.setColor(0,0,0);
			table.setHeaderRows(1);
			Phrase ph; 
			PdfPCell cell;
			int i=1;
            long totNetAmountIqd = 0, totNetAmountUsd=0;
            double netAmtPerOrderIqd = 0, netAmtPerOrderUsd=0;
            BaseColor bcOdd = new BaseColor(241, 253, 241); 
            BaseColor bcEven = new BaseColor(253, 253, 253); 
            BaseColor bc;
        	for (CaseInformation ci  : shipments){
        		totalShimpments ++;
        		if (i%2==1)
        			bc = bcOdd;
        		else
        			bc = bcEven;
        		netAmtPerOrderIqd = 0;
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
				
				ph = new Phrase(ci.getLocationDetails(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getReceiverHp1(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				//receipt amount
				if(ci.getReceiptAmtIqd() !=0.0)
					ph = new Phrase(numFormat.format(ci.getReceiptAmtIqd()),font);
				else
					ph = new Phrase("-",font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				//receipt amount
				if(ci.getReceiptAmtUsd() !=0.0)
					ph = new Phrase(numFormat.format(ci.getReceiptAmtUsd()),font);
				else
					ph = new Phrase("-",font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
        		ph = new Phrase(numFormat.format(ci.getShipmentCharge()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				
				netAmtPerOrderIqd = ci.getReceiptAmtIqd() - ci.getShipmentCharge();
					
				ph = new Phrase(numFormat.format(netAmtPerOrderIqd),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				totNetAmountIqd +=netAmtPerOrderIqd;
				totNetAmountUsd += ci.getReceiptAmtUsd();
				if(ci.getStatus().equalsIgnoreCase("DLV")) {
					String s = "تم التسليم";
					if(ci.getStepCode().equalsIgnoreCase("PART_SUCC"))
						s += " جزئياً";
					if ( ci.getChangedPrice().equalsIgnoreCase("Y") && ci.getReceiptAmtIqd() != ci.getReceiptAmtB4Change()) {
						s += " - مع تغيير سعر الوصل من "+numFormat.format(ci.getReceiptAmtB4Change())+", إلى "+numFormat.format(ci.getReceiptAmtIqd());
						
					}
					ph = new Phrase(s,font);
				}
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getCreateddt(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getRmk(),font);
				cell = new PdfPCell(ph);
				
				cell.setBackgroundColor(bc);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				i++;
        	}
        	BaseColor bcTotal = new BaseColor(0, 103, 0);
        	
        	Font fontTotal = new Font(baseFont);
	        
        	fontTotal.setSize(12);
        	fontTotal.setColor(new BaseColor(252, 252,252));
        	fontTotal.setStyle(1);
        	ph = new Phrase("مبلغ الوصولات بعد التوصيل",fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(bcTotal);
			cell.setColspan(4);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(totNetAmountIqd)+" دينار عراقي",fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(bcTotal);
 			cell.setColspan(4);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(totNetAmountUsd)+" دولار أمريكي",fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(bcTotal);
 			cell.setColspan(2);
			table.addCell(cell);
			
			
			ph = new Phrase(" ",fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(bcTotal);
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
    	BaseColor bcHeader = new BaseColor(0, 103, 0);
    	fontHeaders.setColor(252, 252,252);
    	fontHeaders.setSize(11);
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
	
	private PdfPTable getSummaryTable(Connection a_conn, int a_branchCode, Font a_font)throws Exception {
		Utilities ut = new Utilities();
		// fin table
        int noOfHeaders = 5;
        float []relativeWidths = new float[noOfHeaders]; 
        relativeWidths [0] = 5; // status
        relativeWidths [1] = 5; // qty
        relativeWidths [2] = 5; // qty
        relativeWidths [3] = 5; // qty
        relativeWidths [4] = 10; // qty
        PdfPTable table = new  PdfPTable(relativeWidths);
        table.setWidthPercentage(100);
        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        PdfPCell cell;
        Paragraph par;
     // row 1
			par = new Paragraph("العملية",a_font);
	        cell = new PdfPCell(par);
			//cell.setPaddingTop(0);
			//cell.setPaddingBottom(5);
			cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
     // row 1
			par = new Paragraph("مدين دينار عراقي",a_font);
	        cell = new PdfPCell(par);
			//cell.setPaddingTop(0);
			//cell.setPaddingBottom(5);
			cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
     // row 1
			par = new Paragraph("دائن دينار عراقي",a_font);
	        cell = new PdfPCell(par);
			//cell.setPaddingTop(0);
			//cell.setPaddingBottom(5);
			cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
        
        // row 1
			par = new Paragraph("مدين دولار أمريكي",a_font);
	        cell = new PdfPCell(par);
			//cell.setPaddingTop(0);
			//cell.setPaddingBottom(5);
			cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
     // row 1
			par = new Paragraph("دائن دولار أمريكي",a_font);
	        cell = new PdfPCell(par);
			//cell.setPaddingTop(0);
			//cell.setPaddingBottom(5);
			cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
        
			// row 1
			par = new Paragraph("الرصيد السابق",a_font);
	        cell = new PdfPCell(par);
			//cell.setPaddingTop(0);
			//cell.setPaddingBottom(5);
			cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
        
        HashMap<StandardFinCurrency, Long> entityBalanceBeforePayment = new HashMap<StandardFinCurrency, Long>();
        System.out.println(customerPaymentBean.getStandardTransactionBean().getId());
		entityBalanceBeforePayment = ut.getEntityDebtBalanceUpToSpecificPayment(
				a_conn, 
				FinOperationEntity.CUSTOMER,
				customerPaymentBean.getStandardTransactionBean().getEntityId(),
				(customerPaymentBean.getStandardTransactionBean().getId() - 1)/* to get before the payment */,
				customerPaymentBean.getStandardTransactionBean().getInitiatedInBranchId());
        double debtIqd= 0, creditIqd = 0, debtUsd= 0, creditUsd = 0;
//        System.out.println("a_branchCode==>"+a_branchCode);
//        System.out.println("customerPaymentBean.getStandardTransactionBean().getInitiatedInBranchId()==>"+customerPaymentBean.getStandardTransactionBean().getInitiatedInBranchId()); 
//        		System.out.println("entityBalanceBeforePayment.get(StandardFinCurrency.IQD)==>"+entityBalanceBeforePayment.get(StandardFinCurrency.IQD));
//        System.out.println("entityBalanceBeforePayment.get(StandardFinCurrency.USD)==>"+entityBalanceBeforePayment.get(StandardFinCurrency.USD));
        if (entityBalanceBeforePayment.get(StandardFinCurrency.IQD)>0) {
        	debtIqd = entityBalanceBeforePayment.get(StandardFinCurrency.IQD);
        }else if (entityBalanceBeforePayment.get(StandardFinCurrency.IQD) < 0) {
        	creditIqd = -1 * entityBalanceBeforePayment.get(StandardFinCurrency.IQD);
        }
        
        if (entityBalanceBeforePayment.get(StandardFinCurrency.USD)>0) {
        	debtUsd = entityBalanceBeforePayment.get(StandardFinCurrency.USD);
        }else if (entityBalanceBeforePayment.get(StandardFinCurrency.USD) < 0) {
        	creditUsd = -1 * entityBalanceBeforePayment.get(StandardFinCurrency.USD);
        }

        par = new Paragraph(numFormat.format(debtIqd),a_font);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(cell);
			
			par = new Paragraph(numFormat.format(creditIqd),a_font);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        
        par = new Paragraph(numFormat.format(debtUsd),a_font);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(cell);
			
			par = new Paragraph(numFormat.format(creditUsd),a_font);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        //////////////////////////////////////////////
			
			 // row 2
        par = new Paragraph("صافي مبلغ الوصولات",a_font);
	        cell = new PdfPCell(par);
			cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
        
        par = new Paragraph("-",a_font);
        cell = new PdfPCell(par);
		cell.setPadding(7);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(cell);
        
        if (customerPaymentBean.getStandardTransactionBean().getCode() == FinOperationCode.CASES) {
        	par = new Paragraph(numFormat.format(customerPaymentBean.getStandardTransactionBean().getReceiptsAmtIqd()),a_font);
        }else {
        	par = new Paragraph("-",a_font);
        }
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(cell);
				
			par = new Paragraph("-",a_font);
	        cell = new PdfPCell(par);
			cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        
        if (customerPaymentBean.getStandardTransactionBean().getCode() == FinOperationCode.CASES) {
        	par = new Paragraph(numFormat.format(customerPaymentBean.getStandardTransactionBean().getReceiptsAmtUsd()),a_font);
        }else {
        	par = new Paragraph("-",a_font);
        }
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(cell);
        /////////////////////////////////////////////////////
        // row 2
        par = new Paragraph("المبلغ المدفوع",a_font);
	        cell = new PdfPCell(par);
			cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
        
        par = new Paragraph(numFormat.format(customerPaymentBean.getStandardTransactionBean().getAmountPaidActuallyIqd()),a_font);
        cell = new PdfPCell(par);
        cell.setPadding(7);
		cell.setVerticalAlignment(Element.ALIGN_LEFT);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.addCell(cell);
		
        par = new Paragraph("-",a_font);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(cell);
			
			par = new Paragraph(numFormat.format(customerPaymentBean.getStandardTransactionBean().getAmountPaidActuallyUsd()),a_font);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(cell);
			
			par = new Paragraph("-",a_font);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(cell);

			
			//////////////////////////////////////////////////////////////////
			// new balance
			long currBalanceIqd = 
					customerPaymentBean.getStandardTransactionBean().getReceiptsAmtIqd() 
					- customerPaymentBean.getStandardTransactionBean().getAmountPaidActuallyIqd()
					- entityBalanceBeforePayment.get(StandardFinCurrency.IQD) ;
			long currBalanceUsd = 
					customerPaymentBean.getStandardTransactionBean().getReceiptsAmtUsd() 
					- customerPaymentBean.getStandardTransactionBean().getAmountPaidActuallyUsd()
					- entityBalanceBeforePayment.get(StandardFinCurrency.USD) ;
        par = new Paragraph("الرصيد الجديد",a_font);
	        cell = new PdfPCell(par);
			cell.setPadding(7);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        
        debtIqd = 0 ;creditIqd = 0;debtUsd = 0 ;creditUsd = 0;
        if (currBalanceIqd>0) {
        	creditIqd = currBalanceIqd;
        }else if (currBalanceIqd < 0) {
        	debtIqd = -1 * currBalanceIqd;
        }
        
        if (currBalanceUsd>0) {
        	creditUsd = currBalanceUsd;
        }else if (currBalanceUsd < 0) {
        	debtUsd = -1 * currBalanceUsd;
        }
        
        par = new Paragraph(numFormat.format(debtIqd),a_font);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(cell);
			
			par = new Paragraph(numFormat.format(creditIqd),a_font);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        
        par = new Paragraph(numFormat.format(debtUsd),a_font);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(cell);
			
			par = new Paragraph(numFormat.format(creditUsd),a_font);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        return table;
	}
	
	public String getDocPath() {
		return docPath;
	}

	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}

}
