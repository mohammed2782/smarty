package com.app.printables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;

import com.app.beans.MonthlyProfitDetailsBeen;
import com.app.cases.CaseInformation;
import com.app.printables.PrintMonthlyProfitPDF.MyFooter;
import smarty.core.smartyLogAndErrorHandling;
import smarty.db.mysql;

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

public class PrintMonthlyProfitPDF {
	
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
	 BaseFont baseFont = null;
	boolean newPage = true;
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
            	cb.addImage(image,110,0,0,30, 50.0f,document.top()+25);
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
	public void prepareDocument(String branchCode, String fromdt, String todt, String ctxPath) {
		this.ctxPath = ctxPath;
		String imgPath = ctxPath+"/smartyresources/app-assets/images/logo/logo-sm.png";
		docsDir = ctxPath+"/"+"PDFDocs";
		fileName= "smarty_test"+docExtension;
		fullFilePathName = docsDir+"//"+fileName; 
		createDir(docsDir);
		setDocPath(fullFilePathName);
		Connection conn =null;
		UtilitiesFeqar ut = new UtilitiesFeqar();
		ArrayList<MonthlyProfitDetailsBeen> mpsb = new ArrayList<MonthlyProfitDetailsBeen>();
		try {
			conn = mysql.getConn();
			mpsb = ut.getMonthlyProfitPerDate(conn, branchCode, fromdt, todt);
			Rectangle pageSize = new Rectangle(PageSize.A4);
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
	        //font4.setColor(new BaseColor(92, 38,140));
	        font4.setColor(new BaseColor(252, 252,252));
	        Paragraph par = new Paragraph("حساب الفرع",font4);
	        par.setAlignment(Element.ALIGN_CENTER);
	       // ct.addElement(par);
 
	        
	        Rectangle rect = new Rectangle(/*starting x point*/10, /*starting y point*/570 , /*width*/850 , /*height*/100);
	        rect.setBorder(Rectangle.BOX);
	        ct.setSimpleColumn(rect);
	        ct.go();
	        
	        float [] relativeWidths = null;
        	int noOfHeaders = 5;
	        relativeWidths = new float[noOfHeaders]; 
	       
	        relativeWidths [0] = 10; 
	        relativeWidths [1] = 6; 
	        relativeWidths [2] = 20; 
	        relativeWidths [3] = 10;
	        relativeWidths [4] = 6; 
	        PdfPTable table = new  PdfPTable(relativeWidths);
	        
	        PdfPTable profit ;
	        table.setWidthPercentage(100);
	       
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        PdfPCell cell;
	       
	        
	        BaseColor bc = new BaseColor(0,0,139); 
	        par = new Paragraph("من تاريخ : ",font2);
	        cell = new PdfPCell(par);
 			//cell.setPaddingTop(0);
 			//cell.setPaddingBottom(5);
 			cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            LinkedHashMap<String,String> branchesInfo = new LinkedHashMap<String,String>();
            branchesInfo= ut.getBranchesInfo(conn, branchCode);
            
            par = new Paragraph(fromdt,font3);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            
            par = new Paragraph("كشف ارباح فرع "+branchesInfo.get("name"),font4);
	        cell = new PdfPCell(par);
 			cell.setBackgroundColor(bc);
 			cell.setPadding(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("الى تاريخ : ",font2);
	        cell = new PdfPCell(par);
	        cell.setPadding(5);
 			cell.setBackgroundColor(bc);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            par = new Paragraph(todt,font3);
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
            
            profit = getProfitsTable(conn, mpsb,font);
	        document.add(profit);

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
	
	private  PdfPTable getProfitsTable(Connection conn ,ArrayList<MonthlyProfitDetailsBeen> mpdb ,Font font){
	      
        PdfPTable table = null;
       
        try{
        	
	        int noOfHeaders = 0;
	        float [] relativeWidths = null;
	       
        	noOfHeaders = 5;
	        relativeWidths = new float[noOfHeaders]; 
	       
	        relativeWidths [0] = 15; // 
	        relativeWidths [1] = 15; // 
	        relativeWidths [2] = 15; // 
	        relativeWidths [3] = 15; //  
	        relativeWidths [4] = 5; // seq
	        
	        table = new PdfPTable(relativeWidths);
	        table.setPaddingTop(20);
	        table.setWidthPercentage(100);
	        
	        font.setSize(10);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        // headers
	        //insertHeaders(PdfPTable table, LinkedList<String> headersList, Font font)
	        LinkedList<String> headers = new LinkedList<String>();
	        headers.add("#");
	        headers.add("تاريخ");
	        headers.add("الايرادات");
	        headers.add("المصروفات");
	        headers.add("الربح");
	        
	       
	        insertHeaders (table, headers, font);
	        font.setColor(0,0,0);
			table.setHeaderRows(1);
			Phrase ph; 
			PdfPCell cell;
			int i=1;
            double totIncome = 0;
            double totExpnces = 0;
            double totProfit = 0;
            
            BaseColor bcOdd = new BaseColor(241, 253, 241); 
            BaseColor bcEven = new BaseColor(253, 253, 253); 
            BaseColor bc;
        	for (MonthlyProfitDetailsBeen mp  : mpdb){
        		if (i%2==1)
        			bc = bcOdd;
        		else
        			bc = bcEven;
            	ph = new Phrase(Integer.toString(i),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				
				
				ph = new Phrase(mp.getProfitDate(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(numFormat.format(mp.getIncome()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				totIncome += mp.getIncome();
				
				ph = new Phrase(numFormat.format(mp.getExpense()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				totExpnces += mp.getExpense();
				
				ph = new Phrase(numFormat.format(mp.getProfit()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				totProfit += mp.getProfit();
				
				
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
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBackgroundColor(bcTotal);
			cell.setColspan(2);
			table.addCell(cell);
			
			
			
        	ph = new Phrase(numFormat.format(totIncome),fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBackgroundColor(bcTotal);
			table.addCell(cell);
			
        	ph = new Phrase(numFormat.format(totExpnces),fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBackgroundColor(bcTotal);
			table.addCell(cell);
			
        	ph = new Phrase(numFormat.format(totProfit),fontTotal);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBackgroundColor(bcTotal);
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