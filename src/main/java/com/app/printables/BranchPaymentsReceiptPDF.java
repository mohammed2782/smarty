package com.app.printables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;

import javax.swing.border.Border;

import java.util.LinkedHashMap;

import com.app.beans.BranchPaymentBean;
import com.app.cases.CaseInformation;
import com.app.financials.UtilitiesFinancials;

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
import com.app.financials.*;

public class BranchPaymentsReceiptPDF {
	private Font fontHeaders;
	private  String docPath;
	private String docsDir;
	private String fullFilePathName;
	private String fileName;
	public String docDir="";// the document directory
	public String docLang = "en-US";// arabic kuwait
	public String docExtension = ".pdf";
	String ctxPath = "";
	BranchPaymentBean papb;
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
            	//cb.addImage(image,PageSize.A4.getWidth()-510,0,0,70,40,document.top()+5);
            	if(pageNo == 1)
            		cb.addImage(image,PageSize.A4.getWidth()-540,0,0,50,30,document.top()+10);
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
	public void prepareDocument(  int a_transactionId, int a_userBranch, String a_ctxPath) {
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
			String imgPath = ctxPath+Utilities.getBranchLogoForPrinting(conn, a_userBranch);
			papb =  ut.getbranchPaymentInfo(conn, a_transactionId, a_userBranch);
			
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
	        Font font3 = new Font(baseFont);
	        Font font4 = new Font(baseFont);
	        font2.setSize(12);
	        font2.setColor(new BaseColor(252, 252,252));
	        font2.setStyle(1);
	        font3.setSize(12);
	        font3.setColor(new BaseColor(252, 252,252));
	        font3.setStyle(2);
	        ColumnText ct = new ColumnText(pdf.getDirectContent());

	        ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        
	        font4.setSize(16);
	        font4.setStyle(1);
	        font4.setColor(new BaseColor(252, 252,252));
	        Paragraph par = new Paragraph("حساب الفرع",font4);
	        par.setAlignment(Element.ALIGN_CENTER);
	       
	        Rectangle rect = new Rectangle(/*starting x point*/10, /*starting y point*/570 , /*width*/850 , /*height*/100);
	        rect.setBorder(Rectangle.BOX);
	        ct.setSimpleColumn(rect);
	        ct.go();
	        
	        float [] relativeWidths = null;
        	int noOfHeaders = 5;
	        relativeWidths = new float[noOfHeaders]; 
	       
	        relativeWidths [0] = 10; // status
	        relativeWidths [1] = 10; // qty
	        relativeWidths [2] = 30; // net amount
	        relativeWidths [3] = 10;
	        relativeWidths [4] = 15; 
	        PdfPTable table = new  PdfPTable(relativeWidths);
	        
	        PdfPTable tableShipments ;
	        table.setWidthPercentage(100);
	       
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        PdfPCell cell;
	       
	        BaseColor bc = new BaseColor(0,0,139); 
	        par = new Paragraph("الى فرع : ",font2);
	        cell = new PdfPCell(par);
 			cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            LinkedHashMap<String,String> toBrancheInfo = new LinkedHashMap<String,String>();
            toBrancheInfo= Utilities.getBranchesInfo(conn, papb.getStandardTransactionBean().getEntityId()+"");
            
            par = new Paragraph(toBrancheInfo.get("name"),font3);
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
            
            par = new Paragraph("تاريخ الدفع: ",font2);
	        cell = new PdfPCell(par);
 			cell.setBackgroundColor(bc);
 			cell.setPadding(5);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph(papb.getStandardTransactionBean().getCreatedDateTime(),font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("مبلغ الوصولات بعد التوصيل:",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph(""+numFormat.format(papb.getStandardTransactionBean().getReceiptsAmtIqd()),font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            LinkedHashMap<String,String> payerBrancheInfo = new LinkedHashMap<String,String>();
            payerBrancheInfo= Utilities.getBranchesInfo(conn, papb.getStandardTransactionBean().getInitiatedInBranchId()+""); 
            
            par = new Paragraph("دفعة مالية من فرع "+payerBrancheInfo.get("name"),font4);
	        cell = new PdfPCell(par);
 			cell.setBackgroundColor(bc);
 			cell.setPadding(5);
 			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("إيصال دفع رقم : ",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph(""+a_transactionId,font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            tableShipments = getShipmentsTable(conn, papb,font);
            
            par = new Paragraph("المبلغ المستلم : ",font2);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(5);
 			cell.setPaddingBottom(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph(numFormat.format(papb.getStandardTransactionBean().getAmountRecievedActuallyIqd()),font3);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(5);
 			cell.setPaddingBottom(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            double debt = ut.getSendBranchBalanceWithReceiverBranch (conn,a_userBranch, papb.getPayerBranch());
            
            par = new Paragraph(" ",font2);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(5);
 			cell.setPaddingBottom(5);
 			cell.setBackgroundColor(bc);
 			//cell.setColspan(2);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("عدد الشحنات:",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph(""+numFormat.format(totalShimpments),font3);
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
            
            document.add(getSummaryTable(conn, papb, a_userBranch));
            
            document.add(par);
 
	        document.add(tableShipments);

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
	
	private PdfPTable getSummaryTable(Connection a_conn ,BranchPaymentBean a_papb , int a_userBranch ) {
		PdfPTable table = null;
		int noOfHeaders = 0;
	    float [] relativeWidths = null;   
     	noOfHeaders = 7;
	    relativeWidths = new float[noOfHeaders];
	    relativeWidths [0] = 20; // notes
        relativeWidths [1] = 10; // date entry
        relativeWidths [2] = 20; // qty
        relativeWidths [3] = 5; //status
        relativeWidths [4] = 20; // net amt
        relativeWidths [5] = 10; // shipment cost
        relativeWidths [6] = 20; 
        
        table = new PdfPTable(relativeWidths);
        table.setPaddingTop(20);
        table.setWidthPercentage(100);
        
        Font fontHeader  = new Font(baseFont);  
        Font fontData = new Font(baseFont);
        fontHeader.setSize(12);
        fontHeader.setColor(new BaseColor(0, 0,0));
        fontHeader.setStyle(1);
        fontData.setSize(11);
        fontData.setColor(new BaseColor(0, 0,0));
        fontData.setStyle(2);
        
        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
		Phrase ph; 
		PdfPCell cell;
        try {
        	// header
        	ph = new Phrase("دينار عراقي",fontHeader);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setColspan(3);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			
			ph = new Phrase(" ",fontHeader);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setBorder(cell.NO_BORDER);
			table.addCell(cell);
			
			ph = new Phrase("دولار أمريكي",fontHeader);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setColspan(3);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			
			// operation , value, status
			Utilities ut = new Utilities();
			HashMap<StandardFinCurrency, Long> entityBalanceBeforePayment = new HashMap<StandardFinCurrency, Long>();
			entityBalanceBeforePayment = ut.getEntityDebtBalanceUpToSpecificPayment(
					a_conn, 
					FinOperationEntity.BRANCH,
					a_papb.getStandardTransactionBean().getInitiatedInBranchId(),
					a_papb.getStandardTransactionBean().getId() - 1/* to get before the payment */,
					a_userBranch);
			ph = new Phrase("الرصيد قبل العملية",fontHeader);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(entityBalanceBeforePayment.get(StandardFinCurrency.IQD)),fontData);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			table.addCell(cell);
			
			ph = new Phrase("مدين",fontData);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			table.addCell(cell);
			
			ph = new Phrase(" ",fontData);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setBorder(cell.NO_BORDER);
			table.addCell(cell);
			
			ph = new Phrase("الرصيد قبل العملية",fontHeader);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(entityBalanceBeforePayment.get(StandardFinCurrency.USD)),fontData);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			table.addCell(cell);
			
			ph = new Phrase("مدين",fontData);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			table.addCell(cell);
			
			// second row
			ph = new Phrase("المبلغ المدفوع من الفرع",fontHeader);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(a_papb.getStandardTransactionBean().getAmountPaidActuallyIqd()),fontData);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			table.addCell(cell);
			
			ph = new Phrase("دائن",fontData);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			table.addCell(cell);
			
			ph = new Phrase(" ",fontData);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setBorder(cell.NO_BORDER);
			table.addCell(cell);
			
			ph = new Phrase("المبلغ المدفوع من الفرع",fontHeader);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(a_papb.getStandardTransactionBean().getAmountPaidActuallyUsd()),fontData);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			table.addCell(cell);
			
			ph = new Phrase("دائن",fontData);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			table.addCell(cell);
			
			// 3rd row
			System.out.println("a_papb.getStandardTransactionBean().isBranchReceivedPayment()==>"+a_papb.getStandardTransactionBean().isBranchReceivedPayment());
			System.out.println("trans_id==>"+a_papb.getStandardTransactionBean().getId());
			
			if (a_papb.getStandardTransactionBean().isBranchReceivedPayment()) {
				ph = new Phrase("المبلغ المستلم من الفرع",fontHeader);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(numFormat.format(a_papb.getStandardTransactionBean().getAmountRecievedActuallyIqd()),fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(" ",fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(" ",fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBorder(cell.NO_BORDER);
				table.addCell(cell);
				
				ph = new Phrase("المبلغ المستلم من الفرع",fontHeader);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(numFormat.format(a_papb.getStandardTransactionBean().getAmountRecievedActuallyUsd()),fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(" ",fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				// 4th row
				ph = new Phrase("الفرق بين المبلغ المستلم والمدفوع",fontHeader);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				long differenceIqd = a_papb.getStandardTransactionBean().getAmountRecievedActuallyIqd() -
						a_papb.getStandardTransactionBean().getAmountPaidActuallyIqd();
				String differenceLabelIqd ="مدين";
				
				if (differenceIqd>0) {
					differenceLabelIqd ="دائن";
					if (differenceIqd !=  a_papb.getStandardTransactionBean().getCreditIqd() ) {
						throw new Exception ("Error credit and difference are not the same IQD, diff ="+differenceIqd+", "
								+ "while in database creditIqd = "+a_papb.getStandardTransactionBean().getCreditIqd());
					}
				}else if (differenceIqd<0) {
					differenceLabelIqd ="مدين";
					if (differenceIqd !=  (a_papb.getStandardTransactionBean().getDebitIqd()*-1) ) {
						throw new Exception ("Error debit and difference are not the same IQD, diff ="+differenceIqd+", "
								+ "while in database debitIqd = "+a_papb.getStandardTransactionBean().getDebitIqd());
					}
				}else {
					if(a_papb.getStandardTransactionBean().getDebitIqd()!=0 || 
							a_papb.getStandardTransactionBean().getCreditIqd()!=0) {
						throw new Exception ("Error debit and credit suppose to be zero in database, "
								+ "credit ="+a_papb.getStandardTransactionBean().getCreditIqd()+", "
								+ "while in database debitIqd = "+a_papb.getStandardTransactionBean().getDebitIqd());
					}
					differenceLabelIqd ="متعادل";
				}
				ph = new Phrase(numFormat.format(differenceIqd),fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(differenceLabelIqd,fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(" ",fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBorder(cell.NO_BORDER);
				table.addCell(cell);
				
				ph = new Phrase("الفرق بين المبلغ المستلم والمدفوع",fontHeader);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				long differenceUsd = a_papb.getStandardTransactionBean().getAmountRecievedActuallyUsd() -
						a_papb.getStandardTransactionBean().getAmountPaidActuallyUsd();
				String differenceLabelUsd ="مدين";
				
				if (differenceUsd>0) {
					differenceLabelUsd ="دائن";
					if (differenceUsd !=  a_papb.getStandardTransactionBean().getCreditUsd() ) {
						throw new Exception ("Error credit and difference are not the same USD, diff ="+differenceUsd+", "
								+ "while in database creditUsd = "+a_papb.getStandardTransactionBean().getCreditUsd());
					}
				}else if (differenceUsd<0) {
					differenceLabelUsd ="مدين";
					if (differenceUsd !=  (a_papb.getStandardTransactionBean().getDebitUsd()*-1) ) {
						throw new Exception ("Error debit and difference are not the same USD, diff ="+differenceUsd+", "
								+ "while in database debitUsd = "+a_papb.getStandardTransactionBean().getDebitUsd());
					}
				}else {
					if(a_papb.getStandardTransactionBean().getDebitUsd()!=0 || 
							a_papb.getStandardTransactionBean().getCreditUsd()!=0) {
						throw new Exception ("Error debit and credit suppose to be zero in database, "
								+ "creditUsd ="+a_papb.getStandardTransactionBean().getCreditUsd()+", "
								+ "while in database debitUsd = "+a_papb.getStandardTransactionBean().getDebitUsd());
					}
					differenceLabelUsd ="متعادل";
				}
				ph = new Phrase(numFormat.format(differenceUsd),fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(differenceLabelUsd,fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				//5th row
				ph = new Phrase("الرصيد الجديد",fontHeader);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				long newBalanceIqd = entityBalanceBeforePayment.get(StandardFinCurrency.IQD) + differenceIqd;
				String newBalanceLabelIqd ="مدين";
				
				if (newBalanceIqd>0) {
					newBalanceLabelIqd ="دائن";
				}else if (newBalanceIqd<0) {
					newBalanceLabelIqd ="مدين";
				}else {
					newBalanceLabelIqd ="متعادل";
				}
				ph = new Phrase(numFormat.format(newBalanceIqd),fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(newBalanceLabelIqd,fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(" ",fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBorder(cell.NO_BORDER);
				table.addCell(cell);
				
				
				ph = new Phrase("الرصيد الجديد",fontHeader);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
	
				long newBalanceUsd = entityBalanceBeforePayment.get(StandardFinCurrency.USD) + differenceUsd;
				String newBalanceLabelUsd ="مدين";
				
				if (newBalanceUsd>0) {
					newBalanceLabelUsd ="دائن";
				}else if (newBalanceUsd<0) {
					newBalanceLabelUsd ="مدين";
				}else {
					newBalanceLabelUsd ="متعادل";
				}
				ph = new Phrase(numFormat.format(newBalanceUsd),fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(newBalanceLabelUsd,fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
			}
			
			// 6th row
			// payer remarks
			ph = new Phrase("ملاحظات المرسل",fontData);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			table.addCell(cell);
			
			ph = new Phrase(a_papb.getStandardTransactionBean().getRemarks(),fontData);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setColspan(6);
			table.addCell(cell);
			
			// 7th row
			// payer remarks
			if (a_papb.getStandardTransactionBean().isBranchReceivedPayment()) {
				ph = new Phrase("ملاحظات المستلم",fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(a_papb.getStandardTransactionBean().getReceiverRemarks(),fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setColspan(6);
				table.addCell(cell);
			}else {
				ph = new Phrase("لم يتم الأستلام بعد",fontData);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setColspan(7);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
			}
			
        }catch (Exception e){
        	e.printStackTrace();
        }
        return table;
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
	
	private  PdfPTable getShipmentsTable(Connection conn ,BranchPaymentBean papb,Font font){
		ArrayList<CaseInformation> shipments=null;
	      
        PdfPTable table = null;
       
        try{
        	shipments = papb.getShipments();
	      
	        int noOfHeaders = 0;
	        float [] relativeWidths = null;
	       
        	noOfHeaders = 12;
	        relativeWidths = new float[noOfHeaders]; 
	       
	        relativeWidths [0] = 17; // notes
	        relativeWidths [1] = 12; // date entry
	        relativeWidths [2] = 15; //status
	        relativeWidths [3] = 11; // net amt
	        relativeWidths [4] = 11; // shipment cost
	        relativeWidths [5] = 11; // receipt amt usd
	        relativeWidths [6] = 11; // receipt amt iqd
	        relativeWidths [7] = 13; // rcv number
	        relativeWidths [8] = 15; // address
	        relativeWidths [9] = 14; // cust name
	        relativeWidths [10] = 9; // receipt no 
	        relativeWidths [11] = 5; // seq
	        
	        table = new PdfPTable(relativeWidths);
	        table.setPaddingTop(20);
	        table.setWidthPercentage(100);
	        
	        font.setSize(10);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        // headers
	        //insertHeaders(PdfPTable table, LinkedList<String> headersList, Font font)
	        LinkedList<String> headers = new LinkedList<String>();
	        headers.add("#");
	        headers.add("رقم الوصل");
	        headers.add("المتجر");
	        headers.add("العنوان");
	        headers.add("هاتف الزبون");
	        headers.add("مبلغ الوصل د.ع");
	        headers.add("مبلغ الوصل$");
	        headers.add("أجرة التوصيل");
	        headers.add("الصافي");
	        headers.add("الحاله");
	        headers.add("التاريخ");
	        headers.add("ملاحظات");
	        
	       
	        insertHeaders (table, headers, font);
	        font.setColor(0,0,0);
			table.setHeaderRows(1);
			Phrase ph; 
			PdfPCell cell;
			int i=1;
            long totNetAmountIqd = 0, totNetAmountUsd = 0;
            double netAmtPerOrder = 0;
            double totShipmentCost = 0;
            long totAmtReceiptIqd = 0, totAmtReceiptUsd=0;
            
            BaseColor bcOdd = new BaseColor(241, 253, 241); 
            BaseColor bcEven = new BaseColor(253, 253, 253); 
            BaseColor bc;
        	for (CaseInformation ci  : shipments){
        		totalShimpments ++;
        		if (i%2==1)
        			bc = bcOdd;
        		else
        			bc = bcEven;
        		netAmtPerOrder = 0;
            	ph = new Phrase(Integer.toString(i),font);
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
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getReceiverHp1(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				//receipt amount IQD
				if(ci.getStatus().equalsIgnoreCase("dlv")) {
					ph = new Phrase(numFormat.format(ci.getReceiptAmtIqd()),font);
					totAmtReceiptIqd += ci.getReceiptAmtIqd();
					totAmtReceiptUsd += ci.getReceiptAmtUsd();
				}else
					ph = new Phrase("-",font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				//receipt amount USD
				ph = new Phrase(numFormat.format(ci.getReceiptAmtUsd()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				//shipment charges
				if(ci.getStatus().equalsIgnoreCase("dlv"))
					ph = new Phrase(numFormat.format(ci.getShipmentCharge()),font);
				else if (ci.getStatus().equalsIgnoreCase("canceled") && ci.getShipmentChargesPaidBysender().equalsIgnoreCase("Y"))
					ph = new Phrase(numFormat.format(ci.getShipmentCharge()),font);
				else
					ph = new Phrase("ERROR",font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				totShipmentCost += ci.getShipmentCharge();
				
				//System.out.println(numFormat.format(ci.getShipmentCharge()));
				
				if(ci.getStatus().equalsIgnoreCase("dlv")) {
					netAmtPerOrder = ci.getReceiptAmtIqd() - ci.getShipmentCharge();
				}else if (ci.getStatus().equalsIgnoreCase("canceled") && ci.getShipmentChargesPaidBysender().equalsIgnoreCase("Y")) {
					netAmtPerOrder -=  ci.getShipmentCharge();
				}else {
					netAmtPerOrder = 0;
				}
				ph = new Phrase(numFormat.format(netAmtPerOrder),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);

				totNetAmountIqd +=netAmtPerOrder;
				totNetAmountUsd +=ci.getReceiptAmtUsd();
				//status
				if(ci.getStatus().equalsIgnoreCase("dlv"))
					ph = new Phrase("تم التسليم",font);
				else if (ci.getStatus().equalsIgnoreCase("canceled") && ci.getShipmentChargesPaidBysender().equalsIgnoreCase("Y"))
					ph = new Phrase("راجع مع دفع مبلغ التوصيل من صاحب المحل",font);
				else
					ph = new Phrase("ERROR",font);
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
        	BaseColor bcTotal = new BaseColor(0,0,139);
        	
        	Font fontTotal = new Font(baseFont);
	        
        	fontTotal.setSize(12);
        	fontTotal.setColor(new BaseColor(252, 252,252));
        	fontTotal.setStyle(1);
        	
        	ph = new Phrase("ألمجموع",fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(bcTotal);
			cell.setColspan(5);
			table.addCell(cell);
			
        	ph = new Phrase(numFormat.format(totAmtReceiptIqd),fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setPaddingLeft(10);
 			cell.setBackgroundColor(bcTotal);
			cell.setColspan(1);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(totNetAmountUsd),fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setPaddingLeft(10);
 			cell.setBackgroundColor(bcTotal);
			cell.setColspan(1);
			table.addCell(cell);
			
			
        	ph = new Phrase(numFormat.format(totShipmentCost),fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBackgroundColor(bcTotal);
 			cell.setPaddingRight(10);
			cell.setColspan(2);
			table.addCell(cell);
			
        	ph = new Phrase(" ",fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBackgroundColor(bcTotal);
			table.addCell(cell);
			
        	ph = new Phrase(" ",fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBackgroundColor(bcTotal);
			table.addCell(cell);
			
        	ph = new Phrase(" ",fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBackgroundColor(bcTotal);
			table.addCell(cell);
			
        	ph = new Phrase(" ",fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
 			cell.setBackgroundColor(bcTotal);
			table.addCell(cell);
			
        	
        	
        	ph = new Phrase("مبلغ الوصولات بعد التوصيل",fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(bcTotal);
			cell.setColspan(4);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(totNetAmountIqd) + " دينار عراقي",fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(bcTotal);	
 			cell.setColspan(4);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(totNetAmountUsd) + " دولار أمريكي",fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(bcTotal);	
 			cell.setColspan(4);
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
    	BaseColor bcHeader = new BaseColor(0,0,139);
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
	public String getDocPath() {
		return docPath;
	}


	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}

}


