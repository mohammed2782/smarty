package com.app.printables;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

public class DlevieryAgentManifestPDF {
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
	boolean fromINIT_PRINTMANIFEST = false;
	boolean newPage = true;
	int totalStdNo=0; 
	String manifestAgentId, manifestDate = "", manifestBranchId = "";
	
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
            	cb.addImage(image,PageSize.A4.getWidth()-540,0,0,40,20,document.top()+10);
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
            		new Phrase(" ", ffont),
                    (document.right() - document.left())/3  + document.leftMargin(),
                    document.bottom() - 20, 0);
        }
        public void onStartPage(PdfWriter writer, Document document) {
        	
        	newPage = true;
        }
    }
	public void prepareDocument(String manifestId, String ctxPath) {
		this.ctxPath = ctxPath;
		docsDir = ctxPath+"/"+"PDFDocs";
		fileName= "smarty_test"+docExtension;
		fullFilePathName = docsDir+"//"+fileName; 
		int i = 1;
		createDir(docsDir);
		setDocPath(fullFilePathName);
		Connection conn =null;
		Utilities ut = new Utilities();
		PreparedStatement pst = null;
		ResultSet rs = null;
		String PDFTitle = "", BGColor = "#ffffff";

		try {
			conn = mysql.getConn();
			
			pst = conn.prepareStatement("select dam_agentid, date(dam_manifest_date) as manifestDate, dam_branchid from p_dlvagentmanifest where dam_id = ?");
			pst.setString(1, manifestId);
			rs = pst.executeQuery();
			if(rs.next()) {
				manifestAgentId = rs.getString("dam_agentid");
				manifestDate = rs.getString("manifestDate");
				manifestBranchId = rs.getString("dam_branchid");
			}
			String imgPath = ctxPath+Utilities.getBranchLogoForPrinting(conn, Integer.parseInt(manifestBranchId));
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
			
			// System.out.println("BGColor = "+BGColor);
			// here generate the manifest or get it from the db for all the cases 
			
			Rectangle pageSize = new Rectangle(PageSize.A4.rotate());

			pageSize.setBackgroundColor(new BaseColor(
		            Integer.valueOf( BGColor.substring( 1, 3 ), 16 ),
		            Integer.valueOf( BGColor.substring( 3, 5 ), 16 ),
		            Integer.valueOf( BGColor.substring( 5, 7 ), 16 ) ));
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
	      /*  String months[] = {"كانون الثاني", "شباط", "أذار", "نيسان", "أيار", "حزيران", "تموز", "أب", "أيلول", 
	                "تشرين الأول", "تشرين الثاني", "كانون الأول"};*/
	        GregorianCalendar gcalendar = new GregorianCalendar();
	        int date = gcalendar.get(Calendar.DATE);
	        int year = gcalendar.get(Calendar.YEAR);
	       // String arMonth = months[gcalendar.get(Calendar.MONTH)];
	        String arDay[] = {"الأحد","الأثنين","الثلاثاء","الأربعاء","الخميس","الجمعة","السبت"};
	        String datestr =  arDay[(gcalendar.get(Calendar.DAY_OF_WEEK))-1] +", "+Integer.toString(date)+"/"+(gcalendar.get(Calendar.MONTH)+1)+"/"+Integer.toString(year);
	       
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
	        par = new Paragraph("رقم المنفيست :  "+ manifestId ,font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(10);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("مندوب التوصيل :  "+ ut.getDriverName(conn, manifestAgentId),font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(10);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            
            par = new Paragraph(" بتاريخ : "+ manifestDate,font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(10);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
  
            
            par = new Paragraph(PDFTitle,font);
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
            
	        table = getManifestTable(conn, manifestId, manifestBranchId, font);
	        document.add(table);
	        
	        document.close();
	        pdf.flush();
	        conn.commit();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch(Exception e){
			System.out.println("general error");
			try {conn.rollback();}catch(Exception eRoll) {}
			e.printStackTrace();
		}finally{
			try{conn.close();}catch(Exception e){}
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
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
	
	private  PdfPTable getManifestTable(Connection conn , String mainfestId, String manifestBranchId, Font font){
		ArrayList<CaseInformation> dlvs=null;
		
        PdfPTable table = null;
        Utilities ut = new Utilities();
        //System.out.println("fromINIT_PRINTMANIFEST = "+fromINIT_PRINTMANIFEST);
        
        try{
        	dlvs = ut.getItemsPerManifest(conn, mainfestId, manifestBranchId);
        	// let it update all the cases

        	int noOfHeaders = 9;
	        float [] relativeWidths = new float[noOfHeaders]; 
		        relativeWidths [0] = 18; // rmk
		        relativeWidths [1] = 10; // amount to collect
		        relativeWidths [2] = 12; // amount to collect
		        relativeWidths [3] = 10; // sender phone
		        relativeWidths [4] = 15; // sender name
		        relativeWidths [5] = 12; // rcv phone
		        relativeWidths [6] = 7; // 	receipt no ori       
		        relativeWidths [7] = 17; // address
		        relativeWidths [8] = 5; //seq
	        	
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

            ph = new Phrase("العنوان",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
            table.addCell(cell);
           
            ph = new Phrase("رقم الوصل",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
            table.addCell(cell);

            ph = new Phrase("هاتف المستلم",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);

            ph = new Phrase("إسم المتجر",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
            table.addCell(cell);           
            
            ph = new Phrase("هاتف المتجر",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);
						
            ph = new Phrase("المبلغ المطلوب د.ع",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);
			
			ph = new Phrase("المبلغ المطلوب $",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);

			ph = new Phrase("ملاحظات",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);			
			
			table.setHeaderRows(1);

            int i=1;
            double amountToCollectIqd = 0, amountToCollectUsd = 0;

            BaseColor bcOdd = new BaseColor(240, 240, 240); 
            BaseColor bcEven = new BaseColor(252, 252, 253); 
            BaseColor bc;
            
        	for (CaseInformation ci  : dlvs){
        		
        		if (i%2==1)
        			bc = bcEven;
        		else
        			bc = bcOdd;
        		
            	ph = new Phrase(Integer.toString(i),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				ph = new Phrase(ci.getLocationDetails(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getCustReceiptNoOri(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);				

				ph = new Phrase(ci.getReceiverHp1(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);

				ph = new Phrase(ci.getSenderName(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);

				ph = new Phrase(ci.getSenderHp(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				//balanceIqd
				ph = new Phrase(numFormat.format(ci.getReceiptAmtIqd()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				//balanceUsd
				ph = new Phrase(numFormat.format(ci.getReceiptAmtUsd()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);

				//rmk
				ph = new Phrase(ci.getRmk(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
															
				amountToCollectIqd +=ci.getReceiptAmtIqd();
				amountToCollectUsd +=ci.getReceiptAmtUsd();
				i++;
        	}
        	
        	ph = new Phrase("المبلغ الكلي المطلوب",font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setColspan(6);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(amountToCollectIqd),font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(amountToCollectUsd),font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			
			ph = new Phrase(" ",font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
 			if(fromINIT_PRINTMANIFEST)
 				cell.setColspan(7);
 			else
 				cell.setColspan(8);
			table.addCell(cell);

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
