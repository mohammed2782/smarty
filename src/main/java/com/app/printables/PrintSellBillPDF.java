package com.app.printables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.TimeZone;
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
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class PrintSellBillPDF {
	
	private Font fontHeaders;
	private  String docPath;
	private String docsDir;
	private String fullFilePathName;
	private String fileName;
	public String docDir="";// the document directory
	public String docLang = "en-US";// arabic kuwait
	public String docExtension = ".pdf";
	String ctxPath = "";
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public DecimalFormat numFormat = new DecimalFormat("#,###,###.##");
	public static final Font FONT = new Font(FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYWHITE);
	 BaseFont baseFont = null;
	boolean newPage = true;
	 int totalShimpments=0;
	 String months[] = {"كانون الثاني", "شباط", "أذار", "نيسان", "أيار", "حزيران", "تموز", "أب", "أيلول", 
             "تشرين الأول", "تشرين الثاني", "كانون الأول"};
     String arDay[] = {"الأحد","الأثنين","الثلاثاء","الأربعاء","الخميس","الجمعة","السبت"};
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
		        image.scaleAbsolute(PageSize.A5);
		        image.setAbsolutePosition(0, 0);
		        //PdfContentByte canvas = writer.getDirectContentUnder();
		        image.scaleToFit(100,100);
        }
        
        public void onEndPage(PdfWriter writer, Document document) {
        	pageNo ++;
        	PdfContentByte cb = writer.getDirectContent();
        	try {

            	cb.addImage(image,image.getScaledWidth(),
            			0,
            			0,
            			image.getScaledHeight(),
            			(document.right()-document.left())/2 - 30,
            			document.top()-50);
			} catch (DocumentException e) {

				e.printStackTrace();
			}
            Date dt = new Date();
            
            Phrase rightFooter = new Phrase(dt.toString(), ffont);
            Phrase leftFooter = new Phrase("Page "+pageNo, ffont);
            
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    rightFooter,
                    (document.right() - document.left()) + document.leftMargin(),
                    document.bottom()+10, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                    leftFooter,
                    document.left(),
                    document.bottom()+10, 0);

        }
        public void onStartPage(PdfWriter writer, Document document) {
        	newPage = true;
        }   
        
    }
	 
	public void prepareDocument(  String caseIds, int a_printedby, int a_branchCode, String ctxPath) {
		this.ctxPath = ctxPath;
		
		docsDir = ctxPath+"/"+"PDFDocs";
		fileName= "smarty_mboid"+docExtension;
		fullFilePathName = docsDir+"//"+fileName; 
		createDir(docsDir);
		setDocPath(fullFilePathName);
		Connection conn =null;
		PreparedStatement pst = null;
		Utilities ut = new Utilities();
		
		ArrayList<String> billsList= Utilities.SplitStringToArrayList(caseIds, ",");
		try {
			conn = mysql.getConn();
			String imgPath = ctxPath+Utilities.getBranchLogoForPrinting (conn, a_branchCode);
			String branchName = Utilities.getBranchesInfo(conn, a_branchCode+"").get("name");
			Rectangle pageSize = new Rectangle(PageSize.A5);
			Document document = new Document(pageSize, 10, 10,10,10);
			
		    PdfWriter pdf = null;
		    MyFooter event = new MyFooter();
		    event.setImagePath(imgPath);
		    
		    pdf = PdfWriter.getInstance(document, new FileOutputStream(fullFilePathName));
			
		    event.onStartPage(pdf, document);
		    pdf.setPageEvent(event);
		    pdf.getPageNumber();
		    document.open();		       
			baseFont = BaseFont.createFont("../../Fonts/JannaLTRegular.ttf", BaseFont.IDENTITY_H, true);
			
	        Font font  = new Font(baseFont); 
	        fontHeaders  = new Font(baseFont); 
	        Font font2 = new Font(baseFont);
	        Font font3 = new Font(baseFont);
	        Font font4 = new Font(baseFont);
	        
	        font2.setSize(12);
	        font2.setColor(new BaseColor(9, 11, 13));
	        font2.setFamily("serif");
	        
	        font3.setSize(10);
	        font3.setColor(BaseColor.BLACK);
	        font3.setStyle(Font.NORMAL);
	       
	        font4.setSize(16);
	        font4.setStyle(1);
	        font4.setColor(new BaseColor(252, 252,252));
	        
	        GregorianCalendar gcalendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+3"));
	        int date = gcalendar.get(Calendar.DATE);
	        int year = gcalendar.get(Calendar.YEAR);
	       
	        String datestr = arDay[(gcalendar.get(Calendar.DAY_OF_WEEK))-1]+" "+
	        		Integer.toString(date)+"/"+(gcalendar.get(Calendar.MONTH)+1)+"/"+Integer.toString(year)+", "+gcalendar.get(Calendar.HOUR_OF_DAY)+":"+gcalendar.get(Calendar.MINUTE);
	       
	        Barcode128 code128 = new Barcode128();
	        code128.setGenerateChecksum(true);
	        
	        BarcodeQRCode qrCode;
	        Image codeQrImage;
	        pst = conn.prepareStatement("update p_cases set c_billprinted='Y', c_printedby=?, c_printedat=now() where c_id=?");
	    	for (String caseId : billsList) {
	        	pst.setInt(1, a_printedby);
	        	pst.setString(2, caseId);
	        	pst.executeUpdate();
	        	pst.clearParameters();	
	        	
	    		CaseInformation caseInfoBean = ut.getSinglCaseInformation(conn, caseId); 
			    code128.setCode(caseInfoBean.getCustReceiptNoOri());
			    PdfContentByte cb = pdf.getDirectContent();
			   
		    	Image barCodeImage = code128.createImageWithBarcode(pdf.getDirectContent(), null,BaseColor.WHITE);
		    	barCodeImage.setBorderColor(BaseColor.BLACK);
		    	
		    	cb.addImage(barCodeImage,PageSize.A5.rotate().getWidth()-490,0,0,45,25,document.top()-55);		    	 
					
			    // QrCode
		    	qrCode = new BarcodeQRCode(caseInfoBean.getCustReceiptNoOri(),1000,1000, null);
		    	codeQrImage = qrCode.getImage();
		    	codeQrImage.setBorderColor(BaseColor.BLACK);
		    	codeQrImage.scaleToFit(100, 100);
		    	codeQrImage.scaleAbsolute(100, 100);
		    	cb.addImage(codeQrImage,50,0,0,50,350,document.bottom()+200);
		    	
			    float [] relativeWidths = null;
		        int noOfHeaders = 2;
		        relativeWidths = new float[noOfHeaders]; 
		        relativeWidths [0] = 10; 
		        relativeWidths [1] = 10; 
		        
		        PdfPTable table = new  PdfPTable(relativeWidths);
		        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
		        table.setWidthPercentage(100);
		        
		        Paragraph par = null;
		        PdfPCell cell= null;
		        
		        par = new Paragraph(" ",font2);
		        cell = new PdfPCell(par);
	 			cell.setPadding(7);
	 			cell.setColspan(2);
	 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
	 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);

	            par = new Paragraph(branchName,font2);
		        cell = new PdfPCell(par);
	 			cell.setPadding(7);
	 			cell.setColspan(2);
	 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
	 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);

	            par = new Paragraph("وقت وتاريخ الطبع: ",font2);
		        cell = new PdfPCell(par);
	 			cell.setPadding(8);
	 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
	 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
		            
	            par = new Paragraph(caseInfoBean.getCustReceiptNoOri(),font2);
		        cell = new PdfPCell(par);
	 			cell.setPadding(7);
	 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
	 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
	            
	            par = new Paragraph(datestr,font2);
		        cell = new PdfPCell(par);
	 			cell.setPadding(5);
	 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
	 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
		            
	            par = new Paragraph(" ",font2);
		        cell = new PdfPCell(par);
	 			cell.setPadding(7);
	 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
	 			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
		            
	            document.add(table);
	            
	            par = new Paragraph(" ",font);
	            document.add(par);
		            
	            table = getBillDetialsTable(caseInfoBean ,font);
		        document.add(table);
		        
	            par = new Paragraph(" ",font);
	            document.add(par);
	            Phrase ph;
		            
	            table = new PdfPTable(2);
	        	table.setWidthPercentage(85);
	        	font.setSize(12);
	 	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
		 	        
	        	document.add(table);
	        	document.newPage();
		        	
		    }
		    conn.commit();
	        document.close();
	        pdf.flush();
		} catch(Exception e){
			e.printStackTrace();
		}finally{
			try{pst.close();}catch(Exception e){}
			try{conn.close();}catch(Exception e){}
		}
	}
	
	
	private  PdfPTable getBillDetialsTable(CaseInformation caseInfoBean , Font font){
		
        PdfPTable table = null;
      
        try{
	            
	        int noOfHeaders = 0;
	        float [] relativeWidths = null;
	       
        	noOfHeaders = 4;
	        relativeWidths = new float[noOfHeaders]; 
	       
	        relativeWidths [0] =15; // total item price
	        relativeWidths [1] =15; // price 
	        relativeWidths [2] =40; // qty
	        relativeWidths [3] =25;// item name 
	       
	        
	        table = new PdfPTable(relativeWidths);
	        table.setPaddingTop(20);
	        table.setWidthPercentage(95);
	        
	        font.setSize(11);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        // headers
	        Paragraph par;
        	PdfPCell cell;
            font.setSize(10);
        	
            
            Font font4 = new Font(baseFont);
	       
	        font4.setSize(11);
	        font4.setStyle(1);
	        font4.setColor(new BaseColor(252, 252,252));
            
            par = new Paragraph("المتجر",font4);
	        cell = new PdfPCell(par);
 			cell.setPadding(8);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBackgroundColor(BaseColor.DARK_GRAY);
            table.addCell(cell);
          
            par = new Paragraph(caseInfoBean.getSenderName() ,font);
	        cell = new PdfPCell(par);
	        cell.setPadding(8);
 			cell.setColspan(3);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            par = new Paragraph("هاتف المحل",font4);
	        cell = new PdfPCell(par);
 			cell.setPadding(8);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBackgroundColor(BaseColor.DARK_GRAY);
            table.addCell(cell);
          
            par = new Paragraph(caseInfoBean.getSenderHp(),font);
	        cell = new PdfPCell(par);
	        cell.setPadding(8);
 			cell.setColspan(3);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            
            par = new Paragraph("إسم الزبون",font4);
	        cell = new PdfPCell(par);
 			cell.setPadding(8);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBackgroundColor(BaseColor.DARK_GRAY);
            table.addCell(cell);
            
            par = new Paragraph(caseInfoBean.getReceiverName(),font);
	        cell = new PdfPCell(par);
 			cell.setPadding(8);
 			cell.setColspan(3);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            
            par = new Paragraph("هاتف الزبون",font4);
	        cell = new PdfPCell(par);
	        cell.setPadding(8);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBackgroundColor(BaseColor.DARK_GRAY);
            table.addCell(cell);
          
            par = new Paragraph(caseInfoBean.getReceiverHp1()+", "+caseInfoBean.getReceiverHp2() ,font);
	        cell = new PdfPCell(par);
 			cell.setPadding(8);
 			cell.setColspan(3);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            par = new Paragraph("عنوان الزبون",font4);
	        cell = new PdfPCell(par);
 			cell.setPadding(8);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBackgroundColor(BaseColor.DARK_GRAY);
            table.addCell(cell);
          
            par = new Paragraph(caseInfoBean.getLocationDetails() ,font);
	        cell = new PdfPCell(par);
 			cell.setPadding(8);
 			cell.setColspan(3);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            
        	par = new Paragraph("المبلغ الكلي للوصل",font4);
			cell = new PdfPCell(par);
			cell.setPadding(8);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(BaseColor.DARK_GRAY);
			cell.setColspan(2);
			table.addCell(cell);
			
			par = new Paragraph(numFormat.format(caseInfoBean.getReceiptAmtIqd()),font4);
			cell = new PdfPCell(par);
			cell.setPadding(8);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(BaseColor.DARK_GRAY);
 			cell.setColspan(2);
			table.addCell(cell);
			
			
			
			par = new Paragraph("الملاحظات",font4);
	        cell = new PdfPCell(par);
 			cell.setPadding(8);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
 			cell.setBackgroundColor(BaseColor.DARK_GRAY);
            table.addCell(cell);
          
            par = new Paragraph(caseInfoBean.getRmk() ,font);
	        cell = new PdfPCell(par);
 			cell.setPadding(8);
 			cell.setColspan(3);
 			cell.setVerticalAlignment(Element.ALIGN_LEFT);
 			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

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
	
	
	
	
	private void insertHeaders(PdfPTable table, LinkedList<String> headersList, Font font){
		Phrase ph;
    	PdfPCell cell;
    	BaseColor bcHeader = new BaseColor(165, 126, 2);
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
