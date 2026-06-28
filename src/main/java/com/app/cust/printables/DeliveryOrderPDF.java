package com.app.cust.printables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.logging.Level;

import com.app.cases.CaseInformation;
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

public class DeliveryOrderPDF {
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
	private int totalReceipts = 0;
	boolean newPage = true;
	 int totalStdNo=0;
	private double netMoney = 0;
	private double grossMoney = 0;
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
            	//cb.addImage(image,PageSize.A4.getWidth()-490,0,0,80,20,document.top());
            	;
			} catch (Exception e) {
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
            		new Phrase("", ffont),
                    (document.right() - document.left())/3  + document.leftMargin(),
                    document.bottom() - 20, 0);
        }
        public void onStartPage(PdfWriter writer, Document document) {
        	
        	newPage = true;
        }
    }
	/**
	 * @param dlvCompanyId
	 * @param stgCode
	 * @param stpCode
	 * @param faceBookId
	 * @param fromdt
	 * @param todt
	 * @param ctxPath
	 * @throws Exception 
	 */
	public void prepareDocument(int mastercustomerid,  String printedby, String casesToPrint, int manifestId, String ctxPath) throws Exception {
		this.ctxPath = ctxPath;
		String imgPath = ctxPath+"/smartyresources/logo/logosm.png";
		docsDir = ctxPath+"/"+"PDFDocs";
		fileName= "smarty_test"+docExtension;
		fullFilePathName = docsDir+"//"+fileName; 
		createDir(docsDir);
		setDocPath(fullFilePathName);
		Connection conn2 =null;
		Utilities ut = new Utilities();
		FileOutputStream fos = null;
		try {
			String masterName = "";
			try {
				conn2= mysql.getConn();
				masterName = ut.getMasterCustomerName(conn2, mastercustomerid);

				Document document = new Document(PageSize.A4.rotate(), 50, 50, 30, 50);
			    PdfWriter pdf = null;
			    MyFooter event = new MyFooter();
			    event.setImagePath(imgPath);
			    
			    try {
			    	try {
						fos = new FileOutputStream(fullFilePathName);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	pdf = PdfWriter.getInstance(document, fos);
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
					e.printStackTrace();
				}
				
		        Font font  = new Font(baseFont); 
		        Font font2 = new Font();
		        font2.setSize(30);
		        
		        ColumnText ct = new ColumnText(pdf.getDirectContent());
		        String months[] = {"كانون الثاني", "شباط", "أذار", "نيسان", "أيار", "حزيران", "تموز", "أب", "أيلول", 
		                "تشرين الأول", "تشرين الثاني", "كانون الأول"};
		        GregorianCalendar gcalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+3"));
		        int date = gcalendar.get(Calendar.DATE);
		        int year = gcalendar.get(Calendar.YEAR);
		        String arMonth = months[gcalendar.get(Calendar.MONTH)];
		        String arDay[] = {"الأحد","الأثنين","الثلاثاء","الأربعاء","الخميس","الجمعة","السبت"};
		        String datestr = arDay[(gcalendar.get(Calendar.DAY_OF_WEEK))-1]+" "+
		        		Integer.toString(date)+"/"+(gcalendar.get(Calendar.MONTH)+1)+"/"+Integer.toString(year)+", "+gcalendar.get(Calendar.HOUR_OF_DAY)+":"+gcalendar.get(Calendar.MINUTE);
		        Calendar cl =Calendar.getInstance();
		        
		        
		        Paragraph par;
	        	totalReceipts = 0;
		        ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
		        
		       
		        Rectangle rect = new Rectangle(/*starting x point*/10, /*starting y point*/570 , /*width*/850 , /*height*/100);
	        	rect.setBorder(Rectangle.BOX);
	        	ct.setSimpleColumn(rect);
	        	ct.go();
	  	        
		        PdfPTable table = new  PdfPTable(2);
		        table.setWidthPercentage(100);
		        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
		        PdfPCell cell;
		        par = new Paragraph("شركة النقل : خط الناقل للتوصيل",font);
		        cell = new PdfPCell(par);
	 			cell.setPaddingTop(10);
	 			cell.setPaddingBottom(5);
	 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
	 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
	            
	
				par = new Paragraph("العميل : "+masterName,font);
		        cell = new PdfPCell(par);
	 			cell.setPaddingTop(10);
	 			cell.setPaddingBottom(5);
	 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
	 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
	            
	            
	            par = new Paragraph("الشحنات المسلمة إلى شركة النقل",font);
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
	 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
	 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
	            
	            par = new Paragraph(" ",font);
	            document.add(par);
	            
	            PdfPTable table2 = getManifestTable(conn2, manifestId , printedby , casesToPrint, font);
		        
		        par = new Paragraph(" عدد الشحنات : "+totalReceipts,font);
		        cell = new PdfPCell(par);
	 			cell.setPaddingTop(5);
	 			cell.setPaddingBottom(10);
	 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
	 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
	            
	            par = new Paragraph("رقم المنفيست: "+manifestId,font);
		        cell = new PdfPCell(par);
		        cell.setPaddingTop(5);
	 			cell.setPaddingBottom(10);
	 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
	 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
	            
	            par = new Paragraph("المبلغ الكلي للشحنات: "+numFormat.format(grossMoney),font);
		        cell = new PdfPCell(par);
		        cell.setPaddingTop(5);
	 			cell.setPaddingBottom(10);
	 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
	 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
	            
	            par = new Paragraph("",font);
		        cell = new PdfPCell(par);
		        cell.setPaddingTop(5);
	 			cell.setPaddingBottom(10);
	 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
	 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
	            
	            document.add(table);
	            
		        document.add(table2);
		        
		        document.newPage();
		        
		        document.close();
		        pdf.flush();
			} catch (Exception e) {
				throw e;
			}finally {
				try {conn2.close();}catch(Exception e) {/*ignore*/}
			}
		}catch (BadElementException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	private  PdfPTable getManifestTable(Connection conn , int manifestId, String printedby, String casesToPrint, Font font){
		ArrayList<CaseInformation> dlvs=null;
      
        PdfPTable table = null;
        UtilitiesFeqar utf = new UtilitiesFeqar();
        try{
        	dlvs = utf.getDelevaryOrdersInfo(conn,  casesToPrint);
        	
	        int noOfHeaders = 0;
	        float [] relativeWidths = null;
	       
        	noOfHeaders = 9;
	        relativeWidths = new float[noOfHeaders];
	        relativeWidths [0] = 8; // bill amount
	        relativeWidths [1] = 12; // rmk
	        relativeWidths [2] = 20; // items
	        relativeWidths [3] = 5; // qty
	        relativeWidths [4] = 8; // phone
	        relativeWidths [5] = 15; // address
	        relativeWidths [6] = 8; // bill no
	        relativeWidths [7] = 10; // cust name
	        relativeWidths [8] = 5; // seq no
	        
	        table = new PdfPTable(relativeWidths);
	        table.setPaddingTop(20);
	        table.setWidthPercentage(110);
	        
	        font.setSize(9.5f);
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
            
            ph = new Phrase("المتجر",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);
            
            ph = new Phrase("رقم الوصل",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
            table.addCell(cell);
            

            ph = new Phrase("العنوان",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
            table.addCell(cell);
            
            ph = new Phrase("رقم الهاتف",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);
			
            ph = new Phrase("القطع",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);
			
			ph = new Phrase("البضاعة",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);
			
			ph = new Phrase("ملاحظات",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);
            
			ph = new Phrase("مبلغ الوصل",font);
			cell = new PdfPCell(ph);
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setPaddingBottom(5);
			table.addCell(cell);
			
			table.setHeaderRows(1);

            int i=1;
            double amountToCollect = 0;
            grossMoney = 0;
            
            BaseColor bcOdd = new BaseColor(236, 236, 236); 
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
				
				ph = new Phrase(ci.getReceiverAddress(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getReceiverHp1(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getQty()+"",font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getProductInfo(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getRmk(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
			
				ph = new Phrase(numFormat.format(ci.getReceiptAmt()),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				grossMoney +=ci.getReceiptAmt();
				
				i++;
				totalReceipts++;
        	}
        	netMoney = amountToCollect;
        	ph = new Phrase("المبلغ الكلي المطلوب",font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setColspan(4);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(amountToCollect),font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(11);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
 			cell.setColspan(3);
			table.addCell(cell);
			
			
			conn.commit();
        }catch (Exception e){
			e.printStackTrace();
			try {conn.rollback();}catch(Exception eRoll) {}
		}
        return table;
	}
	
	

	public String getDocPath() {
		return docPath;
	}


	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}

}

