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
import com.app.incomeoutcome.AgentPaymentBean;
import smarty.core.smartyLogAndErrorHandling;
import smarty.db.mysql;

import com.app.util.GlobalVars;
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

public class AgentPaymentRecieptPDF {
	private Font fontHeaders;
	private  String docPath;
	private String docsDir;
	private String fullFilePathName;
	private String fileName;
	public String docDir="";// the document directory
	public String docLang = "en-US";// arabic kuwait
	public String docExtension = ".pdf";
	String ctxPath = "";
	AgentPaymentBean apb;
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
            	cb.addImage(image,PageSize.A4.getWidth()-550,0,0,40,40,document.top()+40);
			} catch (DocumentException e) {

				e.printStackTrace();
			}
            cb.setColorStroke(BaseColor.GRAY);
            cb.moveTo(document.left(), document.top()+35);
            cb.lineTo(document.right(), document.top()+35);
            cb.setLineWidth(2);
            cb.closePathStroke();
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
	public void prepareDocument(int a_transId, int a_branchCode, String ctxPath) {
		this.ctxPath = ctxPath;
		
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
			String imgPath = ctxPath+Utilities.getBranchLogoForPrinting(conn, a_branchCode);
			String branchName = Utilities.getBranchesInfo(conn, a_branchCode+"").get("name");
			apb =  ut.getAgentPaymentInfo(conn, a_transId, a_branchCode);
			
			Rectangle pageSize = new Rectangle(PageSize.A4);
			Document document = new Document(pageSize, 10, 10, 85, 20);
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
				baseFont = BaseFont.createFont("../../Fonts/JannaLTRegular.ttf", BaseFont.IDENTITY_H, true);
			} catch (DocumentException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}catch (Exception e){
				System.out.println("at the font level");
				e.printStackTrace();
			}
			
			Font font  = new Font(baseFont);
	        font.setSize(9);
	        font.setColor(BaseColor.DARK_GRAY);
	        
	        fontHeaders  = new Font(baseFont); 
	        Font font2 = new Font(baseFont);
	        Font font3 = new Font(baseFont);
	        Font font4 = new Font(baseFont);
	        font2.setSize(11);
	        font2.setColor(new BaseColor(0, 0,0));
	        font2.setStyle(Font.NORMAL);
	        font3.setSize(10);
	        font3.setColor(BaseColor.DARK_GRAY);
	        font3.setStyle(Font.NORMAL);
	        
	        Font font5 = new Font(baseFont);
	        font5.setSize(12);
	        font5.setColor(255, 255, 255);
	        font5.setStyle(Font.NORMAL);
	        
	        font4.setSize(14);
	        font4.setStyle(Font.NORMAL);
	        font4.setColor(BaseColor.DARK_GRAY);
	        
	        ColumnText ct = new ColumnText(pdf.getDirectContent());
	        String months[] = {"كانون ااثاني", "شباط", "أذار", "نيسان", "أيار", "حزيران", "تموز", "أب", "أيلول", 
	                "تشرين الأول", "تشرين الثاني", "كانون الأول"};
	        GregorianCalendar gcalendar = new GregorianCalendar();
	        int date = gcalendar.get(Calendar.DATE);
	        int year = gcalendar.get(Calendar.YEAR);
	        String arMonth = months[gcalendar.get(Calendar.MONTH)];
	        String datestr = Integer.toString(date)+" "+arMonth+" "+Integer.toString(year);
	       
	        ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	      
	        Paragraph par = new Paragraph("إيصال قبض رقم "+a_transId,font);
	        par.setAlignment(Element.ALIGN_CENTER);
	        ct.addElement(par);
 
	        
	        int noOfHeaders = 3;
	        float [] relativeWidths = null;
	        relativeWidths = new float[noOfHeaders]; 
	        relativeWidths [0] = 40; // address 
	        relativeWidths [1] = 40; // address 
	        relativeWidths [2] = 40; // address 
	        PdfPTable table = new  PdfPTable(relativeWidths);
	        table.setWidthPercentage(95);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        PdfPCell cell;
	        // row 1
	        par = new Paragraph(branchName,font);
	        cell = new PdfPCell(par);
	        cell.setPaddingTop(-20);
	        cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("",font2);
	        cell = new PdfPCell(par);
	        cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("",font2);
	        cell = new PdfPCell(par);
	        cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            // row 2
            par = new Paragraph("توصيل إلى جميع أنحاء العراق",font);
	        cell = new PdfPCell(par);
	        cell.setPaddingTop(-10);
	        cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
 			cell.setBorderWidthBottom(0.1f);
 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            
            par = new Paragraph("وصل قبض مالي",font4);
	        cell = new PdfPCell(par);
	        cell.setPaddingBottom(15);
	        cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBorder(cell.NO_BORDER);
 			cell.setBorderWidthBottom(0.1f);
 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            
            par = new Paragraph("",font2);
	        cell = new PdfPCell(par);
	        cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBorder(cell.NO_BORDER);
 			cell.setBorderWidthBottom(0.1f);
 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            
            document.add(table);
	        
            noOfHeaders = 5;
	        relativeWidths = new float[noOfHeaders]; 
	        relativeWidths [0] = 30; // status
	        relativeWidths [1] = 25; // status
	        relativeWidths [2] = 60; // qty
	        relativeWidths [3] = 30; // net amount 
	        relativeWidths [4] = 30; // net amount 
	        table = new  PdfPTable(relativeWidths);
	        table.setWidthPercentage(95);
	       
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	         
	        BaseColor bc = new BaseColor(0, 103, 0); 
	        par = new Paragraph("وصل قبض رقم:",font2);
	        cell = new PdfPCell(par);
 			cell.setPadding(7);
 			//cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
 			cell.setBorderWidthBottom(0.1f);
 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            
            par = new Paragraph(a_transId+"",font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
 			cell.setBorderWidthBottom(0.1f);
 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            
            par = new Paragraph("",font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
 			cell.setBorderWidthBottom(0.1f);
 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            
            par = new Paragraph("تاريخ العملية: ",font2);
	        cell = new PdfPCell(par);
 			//cell.setBackgroundColor(bc);
 			cell.setPadding(7);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
 			cell.setBorderWidthBottom(0.1f);
 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            document.add(table);
            
            par = new Paragraph(apb.getStandardTransactionBean().getCreatedDateTime(),font3);
	        cell = new PdfPCell(par);
 			//cell.setBackgroundColor(bc);
 			cell.setPadding(7);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
 			cell.setBorderWidthBottom(0.1f);
 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            document.add(table);
            
            noOfHeaders = 5;
	        relativeWidths = new float[noOfHeaders]; 
	        relativeWidths [0] = 30; // status
	        relativeWidths [1] = 25; // status
	        relativeWidths [2] = 60; // qty
	        relativeWidths [3] = 30; // net amount 
	        relativeWidths [4] = 30; // net amount 
	        table = new  PdfPTable(relativeWidths);
	        table.setWidthPercentage(95);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	         
            par = new Paragraph("السيد:",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
 			//cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
 			cell.setBorderWidthBottom(0.1f);
 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
           
            par = new Paragraph(apb.getAgentName(),font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
 			cell.setBorderWidthBottom(0.1f);
 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            
            par = new Paragraph("",font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
 			cell.setBorderWidthBottom(0.1f);
 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            
            par = new Paragraph("نوع الدفعة:",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
 			cell.setBorderWidthBottom(0.1f);
 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            
            par = new Paragraph("محاسبة وصولات",font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
 			cell.setBorderWidthBottom(0.1f);
 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            
            par = new Paragraph("",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
 			//cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
 			//cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            par = new Paragraph("",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
 			//cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
	        par = new Paragraph("المبلغ المقبوض:",font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
 			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
                        
            par = new Paragraph(numFormat.format(apb.getStandardTransactionBean().getAmountPaidActuallyIqd()),font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(7);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
 			cell.setBorder(cell.NO_BORDER);
 			table.addCell(cell);
            
            document.add(table);
           
            
            noOfHeaders = 4;
	        relativeWidths = new float[noOfHeaders]; 
	        relativeWidths [0] = 10; // status
	        relativeWidths [1] = 10; // qty
	        relativeWidths [2] = 10; // qty
	        relativeWidths [3] = 10; // qty
	        table = new  PdfPTable(relativeWidths);
	        table.setWidthPercentage(95);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        
            par = new Paragraph("ملاحظات:",font2);
	        cell = new PdfPCell(par);
 			//cell.setPaddingTop(0);
 			//cell.setPaddingBottom(5);
	        cell.setPaddingTop(7);
 			cell.setPaddingBottom(7);
 			//cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
                        
            par = new Paragraph(apb.getPmtRmk(),font3);
	        cell = new PdfPCell(par);
	        cell.setPaddingTop(7);
	        cell.setPaddingBottom(7);
	        cell.setColspan(3);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
 			
            table.addCell(cell);
            
            document.add(table);
            
            par = new Paragraph(" ",font);
            document.add(par);
            
         // fin table
            noOfHeaders = 5;
	        relativeWidths = new float[noOfHeaders]; 
	        relativeWidths [0] = 5; // status
	        relativeWidths [1] = 5; // qty
	        relativeWidths [2] = 5; // qty
	        relativeWidths [3] = 5; // qty
	        relativeWidths [4] = 10; // qty
	        table = new  PdfPTable(relativeWidths);
	        table.setWidthPercentage(100);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	     // row 1
 			par = new Paragraph("العملية",font3);
 	        cell = new PdfPCell(par);
  			//cell.setPaddingTop(0);
  			//cell.setPaddingBottom(5);
  			cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
         // row 1
 			par = new Paragraph("مدين دينار عراقي",font3);
 	        cell = new PdfPCell(par);
  			//cell.setPaddingTop(0);
  			//cell.setPaddingBottom(5);
  			cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
         // row 1
 			par = new Paragraph("دائن دينار عراقي",font3);
 	        cell = new PdfPCell(par);
  			//cell.setPaddingTop(0);
  			//cell.setPaddingBottom(5);
  			cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            
            // row 1
 			par = new Paragraph("مدين دولار أمريكي",font3);
 	        cell = new PdfPCell(par);
  			//cell.setPaddingTop(0);
  			//cell.setPaddingBottom(5);
  			cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
         // row 1
 			par = new Paragraph("دائن دولار أمريكي",font3);
 	        cell = new PdfPCell(par);
  			//cell.setPaddingTop(0);
  			//cell.setPaddingBottom(5);
  			cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
	        
 			// row 1
 			par = new Paragraph("الرصيد السابق",font3);
 	        cell = new PdfPCell(par);
  			//cell.setPaddingTop(0);
  			//cell.setPaddingBottom(5);
  			cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            
            HashMap<StandardFinCurrency, Long> entityBalanceBeforePayment = new HashMap<StandardFinCurrency, Long>();
            entityBalanceBeforePayment = ut.getEntityDebtBalanceUpToSpecificPayment(
					conn, 
					FinOperationEntity.AGENT,
					apb.getStandardTransactionBean().getEntityId(),
					apb.getStandardTransactionBean().getId() - 1/* to get before the payment */,
					apb.getStandardTransactionBean().getInitiatedInBranchId());
            long debtIqd= 0, creditIqd = 0, debtUsd= 0, creditUsd = 0;
            
            if (entityBalanceBeforePayment.get(StandardFinCurrency.IQD)>0) {
            	debtIqd = entityBalanceBeforePayment.get(StandardFinCurrency.IQD);
            }else if (entityBalanceBeforePayment.get(StandardFinCurrency.IQD) < 0) {
            	creditIqd = entityBalanceBeforePayment.get(StandardFinCurrency.IQD);
            }
            
            if (entityBalanceBeforePayment.get(StandardFinCurrency.USD)>0) {
            	debtUsd = entityBalanceBeforePayment.get(StandardFinCurrency.USD);
            }else if (entityBalanceBeforePayment.get(StandardFinCurrency.USD) < 0) {
            	creditUsd = entityBalanceBeforePayment.get(StandardFinCurrency.USD);
            }

            par = new Paragraph(numFormat.format(debtIqd),font3);
 	        cell = new PdfPCell(par);
 	        cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			table.addCell(cell);
  			
  			par = new Paragraph(numFormat.format(creditIqd),font3);
 	        cell = new PdfPCell(par);
 	        cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            par = new Paragraph(numFormat.format(debtUsd),font3);
 	        cell = new PdfPCell(par);
 	        cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			table.addCell(cell);
  			
  			par = new Paragraph(numFormat.format(creditUsd),font3);
 	        cell = new PdfPCell(par);
 	        cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            //////////////////////////////////////////////
  			
  			 // row 2
            par = new Paragraph("صافي مبلغ الوصولات",font3);
 	        cell = new PdfPCell(par);
  			cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            
            if (apb.getStandardTransactionBean().getCode() == FinOperationCode.CASES) {
            	par = new Paragraph(numFormat.format(apb.getStandardTransactionBean().getReceiptsAmtIqd()),font3);
            }else {
            	par = new Paragraph("-",font3);
            }
 	        cell = new PdfPCell(par);
 	        cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			table.addCell(cell);
  			
  			par = new Paragraph("-",font2);
  	        cell = new PdfPCell(par);
   			cell.setPadding(7);
   			cell.setVerticalAlignment(Element.ALIGN_LEFT);
   			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            if (apb.getStandardTransactionBean().getCode() == FinOperationCode.CASES) {
            	par = new Paragraph(numFormat.format(apb.getStandardTransactionBean().getReceiptsAmtUsd()),font3);
            }else {
            	par = new Paragraph("-",font3);
            }
 	        cell = new PdfPCell(par);
 	        cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			table.addCell(cell);
  			
  			par = new Paragraph("-",font2);
  	        cell = new PdfPCell(par);
   			cell.setPadding(7);
   			cell.setVerticalAlignment(Element.ALIGN_LEFT);
   			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            /////////////////////////////////////////////////////
            // row 2
            par = new Paragraph("المبلغ المقبوض من المندوب",font3);
 	        cell = new PdfPCell(par);
  			cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            
            par = new Paragraph("-",font3);
 	        cell = new PdfPCell(par);
 	        cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			table.addCell(cell);

  			par = new Paragraph(numFormat.format(apb.getStandardTransactionBean().getAmountPaidActuallyIqd()),font3);
 	        cell = new PdfPCell(par);
 	        cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			table.addCell(cell);
  			
  			par = new Paragraph("-",font3);
 	        cell = new PdfPCell(par);
 	        cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			table.addCell(cell);

  			par = new Paragraph(numFormat.format(apb.getStandardTransactionBean().getAmountPaidActuallyUsd()),font3);
 	        cell = new PdfPCell(par);
 	        cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			table.addCell(cell);
  			//////////////////////////////////////////////////////////////////
  			// new balance
  			long currBalanceIqd = 
  					entityBalanceBeforePayment.get(StandardFinCurrency.IQD) 
  					+ apb.getStandardTransactionBean().getReceiptsAmtIqd() 
  					- apb.getStandardTransactionBean().getAmountPaidActuallyIqd();
  			long currBalanceUsd = 
  					entityBalanceBeforePayment.get(StandardFinCurrency.USD) 
  					+ apb.getStandardTransactionBean().getReceiptsAmtUsd() 
  					- apb.getStandardTransactionBean().getAmountPaidActuallyUsd();
            par = new Paragraph("المتبقيى على المندوب إو الشركة",font3);
 	        cell = new PdfPCell(par);
  			cell.setPadding(7);
  			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            debtIqd = 0 ;creditIqd = 0;debtUsd = 0 ;creditUsd = 0;
            if (currBalanceIqd>0) {
            	debtIqd = currBalanceIqd;
            }else if (currBalanceIqd < 0) {
            	creditIqd = currBalanceIqd;
            }
            
            if (currBalanceUsd>0) {
            	debtUsd = currBalanceUsd;
            }else if (currBalanceUsd < 0) {
            	creditUsd = currBalanceUsd;
            }
            
            par = new Paragraph(numFormat.format(debtIqd),font3);
 	        cell = new PdfPCell(par);
 	        cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			table.addCell(cell);
  			
  			par = new Paragraph(numFormat.format(creditIqd),font3);
 	        cell = new PdfPCell(par);
 	        cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            par = new Paragraph(numFormat.format(debtUsd),font3);
 	        cell = new PdfPCell(par);
 	        cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
  			table.addCell(cell);
  			
  			par = new Paragraph(numFormat.format(creditUsd),font3);
 	        cell = new PdfPCell(par);
 	        cell.setPadding(7);
  			cell.setVerticalAlignment(Element.ALIGN_LEFT);
  			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            
            document.add(table);
            
            par = new Paragraph(" ",font);
            document.add(par);
            
            table = new PdfPTable(3);
        	table.setWidthPercentage(95);
        	font.setSize(12);
 	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
         	
 	        par = new Paragraph("توقيع المحاسب : ..................",font);
 			cell = new PdfPCell(par);
 			cell.setPaddingTop(10);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph(" ",font);
 			cell = new PdfPCell(par);
 			cell.setPaddingTop(10);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("توقيع المندوب : ..................",font);
 			cell = new PdfPCell(par);
 			cell.setPaddingTop(10);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            document.add(table);
            
            
            if (apb.getStandardTransactionBean().getCode() == FinOperationCode.CASES) {
	            noOfHeaders = 3;
		        relativeWidths = null;
		        relativeWidths = new float[noOfHeaders]; 
		        relativeWidths [0] = 40; // address 
		        relativeWidths [1] = 40; // address 
		        relativeWidths [2] = 40; // address 
		        table = new  PdfPTable(relativeWidths);
		        table.setWidthPercentage(90);
		        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
		        
		        // row 1
		        par = new Paragraph(" ",font4);
		        cell = new PdfPCell(par);
		        cell.setPaddingBottom(10);
		        cell.setVerticalAlignment(Element.ALIGN_CENTER);
	 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 			cell.setBorder(cell.NO_BORDER);
	 			cell.setBorderWidthBottom(0.1f);
	 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
	            table.addCell(cell);
	            
	            par = new Paragraph("تفاصيل الشحنات",font4);
		        cell = new PdfPCell(par);
		        cell.setPaddingBottom(10);
		        cell.setVerticalAlignment(Element.ALIGN_CENTER);
	 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 			cell.setBorder(cell.NO_BORDER);
	 			cell.setBorderWidthBottom(0.1f);
	 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
	            table.addCell(cell);
	            
	            par = new Paragraph("",font2);
		        cell = new PdfPCell(par);
		        cell.setVerticalAlignment(Element.ALIGN_RIGHT);
	 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	 			cell.setBorder(cell.NO_BORDER);
	 			cell.setBorderWidthBottom(0.1f);
	 			cell.setBorderColor(BaseColor.LIGHT_GRAY);
	            table.addCell(cell);
	            
	            document.add(table);
	            
		        table = getShipmentsTable(conn, apb,font);
		        document.add(table);
		        
	        	table = new PdfPTable(2);
	        	table.setWidthPercentage(90);
	        	font.setSize(12);
	 	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        	document.add(table);
            }
	        
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
	
	private  PdfPTable getShipmentsTable(Connection conn ,AgentPaymentBean apb,Font font){
		ArrayList<CaseInformation> shipments=null;
      
        PdfPTable table = null;
        try{
        	shipments = apb.getShipments();
	        int noOfHeaders = 0;
	        float [] relativeWidths = null;
	        noOfHeaders = 11;
	        relativeWidths = new float[noOfHeaders]; 
	       
	        relativeWidths [0] = 15; // status
	        relativeWidths [1] = 8; // net amount
	        relativeWidths [2] = 8; // send money color, green
	        relativeWidths [3] = 10; // hp
	        relativeWidths [4] = 11; // city
	        relativeWidths [5] = 10; // cust name
	        relativeWidths [6] = 7; // receipt no
	        relativeWidths [7] = 8; // date 
	        relativeWidths [8] = 7; // date given to agent 
	        relativeWidths [9] = 10; // seq
	        relativeWidths [10] = 4; // seq
	        
	        table = new PdfPTable(relativeWidths);
	        table.setPaddingTop(20);
	        table.setWidthPercentage(100);
	        
	        font.setSize(8f);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        // headers
	        //insertHeaders(PdfPTable table, LinkedList<String> headersList, Font font)
	        LinkedList<String> headers = new LinkedList<String>();
	        headers.add("#");
	        headers.add("تاريخ الإعطاء");
	        headers.add("رقم الوصل");
	        headers.add("المتجر");
	        headers.add("العنوان");
	        headers.add("الهاتف");
	        headers.add("مبلغ الوصل د.ع");
	        headers.add("مبلغ الوصل $");
	        headers.add("للمندوب");
	        headers.add("الصافي للشركة");
	        headers.add(" ");
	        
	       
	        insertHeaders (table, headers, font);
	        font.setColor(0,0,0);
			table.setHeaderRows(1);
			Phrase ph; 
			PdfPCell cell;
			int i=1;
            double agentCharges = 0;
            double receiptAmtIqd = 0, receiptAmtUsd = 0;
            double netCompany = 0;
            double netCompanyTotal = 0;
            double agentChargesTotal = 0;
            long receiptAmtTotIqd = 0, receiptAmtTotUsd = 0;
            boolean errorFlag = false;
            BaseColor bcOdd = new BaseColor(251, 193, 239); 
            BaseColor bcEven = new BaseColor(253, 253, 253); 
            
            BaseColor bc;
        	for (CaseInformation ci  : shipments){
        		if (i%2==1)
        			bc = bcOdd;
        		else
        			bc = bcEven;
        		agentCharges = 0;
            	ph = new Phrase(Integer.toString(i),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getDlvAgentManifestDate(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getCustReceiptNoOri(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getSenderName(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);

				ph = new Phrase(ci.getLocationDetails(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				if (ci.getRural().equalsIgnoreCase("Y"))
					cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				else
					cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getReceiverHp1(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);

				//all charges
				if(ci.getStageCode().equalsIgnoreCase("DLV")) {
					ph = new Phrase(numFormat.format(ci.getReceiptAmtIqd()),font);
					receiptAmtIqd = ci.getReceiptAmtIqd();
				} else {
					receiptAmtIqd=0;
					ph = new Phrase(("-"),font);
				}
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				receiptAmtTotIqd +=receiptAmtIqd;
				
				if(ci.getStageCode().equalsIgnoreCase("DLV")) {
					ph = new Phrase(numFormat.format(ci.getReceiptAmtUsd()),font);
					receiptAmtUsd = ci.getReceiptAmtUsd();
				} else {
					receiptAmtUsd=0;
					ph = new Phrase(("-"),font);
				}
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				receiptAmtTotUsd +=receiptAmtUsd;
				
				//agent charges
				if(ci.getStageCode().equalsIgnoreCase("DLV")) {
					agentCharges = ci.getAgentShare();
					ph = new Phrase(numFormat.format(agentCharges),font);
				} else {
					agentCharges = 0;
					ph = new Phrase(("-"),font);
					
				}
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				agentChargesTotal +=agentCharges;

				//net to company
				netCompany = 0;
				if(ci.getStageCode().equalsIgnoreCase("DLV")) {
					netCompany = ci.getReceiptAmtIqd() - agentCharges;
				} else {
						netCompany=0;
				}
				ph = new Phrase(numFormat.format(netCompany),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				netCompanyTotal +=netCompany;
				
				
				cell = new PdfPCell();
				cell.setBackgroundColor(bc);
				String status = "";
				if (ci.getStageCode().equalsIgnoreCase("DLV")) {
					if (ci.getStepCode().equalsIgnoreCase("SUCC_CHANGEPRICE")) {
						status +="تم التسليم مع ";
						status += Utilities.getChangedPriceMessage(
								ci.getChangedPrice()   , ci.getChangedPriceUsd(), 
								ci.getReceiptAmtB4Change()+""   , ci.getReceiptAmtIqd()+"",
								ci.getReceiptAmtUsdB4Change()+"", ci.getReceiptAmtUsd()+""
								);
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
					}else if (ci.getStepCode().equalsIgnoreCase("PART_SUCC")) {
						if (ci.getChangedPrice().equalsIgnoreCase("Y")) {
							status +="تسليم جزئي مع ";
							status += Utilities.getChangedPriceMessage(
									ci.getChangedPrice()   , ci.getChangedPriceUsd(), 
									ci.getReceiptAmtB4Change()+""   , ci.getReceiptAmtIqd()+"",
									ci.getReceiptAmtUsdB4Change()+"", ci.getReceiptAmtUsd()+""
									);
							cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						}else 
							status = "تسليم جزئي ";
					}else {
						if (ci.getChangedPrice().equalsIgnoreCase("Y")|| ci.getChangedPriceUsd().equalsIgnoreCase("Y")) {
							status = "تسليم بنجاح ";
							status += Utilities.getChangedPriceMessage(
									ci.getChangedPrice()   , ci.getChangedPriceUsd(), 
									ci.getReceiptAmtB4Change()+""   , ci.getReceiptAmtIqd()+"",
									ci.getReceiptAmtUsdB4Change()+"", ci.getReceiptAmtUsd()+""
									);
							cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						}else {
							status = "تسليم بنجاح";
						}
					}
				}else {
					status =  "خطأ في النظام";
					errorFlag = true;
					
				}
				ph =  new Phrase(status+" / "+ci.getRmk(),font);
				cell.setPhrase(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				i++;
        	}
        	double amountToBeCollectedFromAgent = netCompanyTotal;//totalChargeCount + shipmentChargeCount - agentChargesTotal;
        	//if (amountToBeCollectedFromAgent <0 ) amountToBeCollectedFromAgent = 0;
        	ph = new Phrase(" المجموع ",font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setPaddingRight(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(new BaseColor(210, 185, 205));
			cell.setColspan(6);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(receiptAmtTotIqd),font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(4);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(new BaseColor(210, 185, 205));
 			cell.setColspan(1);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(receiptAmtTotUsd),font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(4);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(new BaseColor(210, 185, 205));
 			cell.setColspan(1);
			table.addCell(cell);
			
					
			ph = new Phrase(numFormat.format(agentChargesTotal),font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(4);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(new BaseColor(210, 185, 205));
 			cell.setColspan(1);
			table.addCell(cell);
			
			
			ph = new Phrase(numFormat.format(amountToBeCollectedFromAgent),font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(4);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(new BaseColor(210, 185, 205));
 			cell.setColspan(1);
			table.addCell(cell);
			
			ph = new Phrase(  );
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(4);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(new BaseColor(210, 185, 205));
 			cell.setColspan(1);
			table.addCell(cell);
			
			/*ph = new Phrase(" المبلغ المطلوب أستلامه من المندوب ",font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setPaddingRight(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(new BaseColor(210, 185, 205));
			cell.setColspan(6);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(amountToBeCollectedFromAgent),font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(4);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(new BaseColor(210, 185, 205));
 			cell.setColspan(3);
			table.addCell(cell);
			
			
			ph = new Phrase(  );
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(4);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(new BaseColor(210, 185, 205));
 			cell.setColspan(1);
			table.addCell(cell);
			*/

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
    	fontHeaders.setSize(9f);
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
