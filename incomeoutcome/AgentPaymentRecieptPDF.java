package com.app.incomeoutcome;

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
import com.app.core.smartyLogAndErrorHandling;
import com.app.db.mysql;
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
				
            	cb.addImage(image,PageSize.A4.getWidth()-525,0,0,65,35,document.top()-50);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Date dt = new Date();
            Phrase header = new Phrase(dt.toString(), ffont);
            Phrase footer = new Phrase("Page "+pageNo, ffont);
           /* ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    header,
                    (document.right() - document.left()) + document.leftMargin(),
                    document.top() +50, 0);
           */ 
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
            		new Phrase(" Delivery on Time System (DoTS), is a system developed by Softecha www.softecha.com", ffont),
                    (document.right() - document.left())/3  + document.leftMargin(),
                    document.bottom() - 25, 0);
        }
        public void onStartPage(PdfWriter writer, Document document) {
        	
        	newPage = true;
        }
    }
	public void prepareDocument(  int ap_id, String ctxPath) {
		this.ctxPath = ctxPath;
		String imgPath = ctxPath+"/smartyresources/img/logo_sm.png";
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
			apb =  ut.getAgentPaymentInfo(conn, ap_id);
			
			Rectangle pageSize = new Rectangle(PageSize.A4.rotate());
			pageSize.setBackgroundColor(new BaseColor(254, 235, 250 ));
			
			Document document = new Document(pageSize, 50, 50, 20, 50);
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
	      
	        Paragraph par = new Paragraph("إيصال دفع رقم "+ap_id,font);
	        par.setAlignment(Element.ALIGN_CENTER);
	        ct.addElement(par);
 
	       
	        //Rectangle rect = new Rectangle(/*starting x point*/30, /*starting y point*/810 , /*width*/600 , /*height*/70);
	        /*rect.setBorder(Rectangle.BOX);
	        ct.setSimpleColumn(rect);
	        ct.go();
	        */
	        PdfPTable table = new  PdfPTable(1);
	        table.setWidthPercentage(100);
	       
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        PdfPCell cell;
	       
	        
	        par = new Paragraph("إسم العميل: "+apb.getAgentName(),font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(10);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("إيصال دفع رقم: "+ap_id,font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(10);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("تاريخ الدفع: "+apb.getPmtDate(),font);
	        cell = new PdfPCell(par);
 			cell.setPaddingTop(10);
 			cell.setPaddingBottom(5);
 			cell.setVerticalAlignment(Element.ALIGN_RIGHT);
 			cell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            par = new Paragraph("المبلغ الصافي: "+numFormat.format(apb.getPmtAmt()),font);
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
            
	        table = getShipmentsTable(conn, apb,font);
	        document.add(table);
	        
        	table = new PdfPTable(2);
        	table.setWidthPercentage(90);
        	font.setSize(12);
 	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
         	
 	        Phrase ph;
         	ph = new Paragraph("توقيع المحاسب : ..................",font);
 			cell = new PdfPCell(ph);
 			cell.setPaddingTop(40);
 			cell.setPaddingBottom(20);
 			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
            ph = new Paragraph("توقيع العميل  : ..................",font);
 			cell = new PdfPCell(ph);
 			cell.setPaddingTop(40);
 			cell.setPaddingBottom(20);
 			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBorder(cell.NO_BORDER);
            table.addCell(cell);
            
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
	
	private  PdfPTable getShipmentsTable(Connection conn ,AgentPaymentBean apb,Font font){
		ArrayList<CaseInformation> shipments=null;
      
        PdfPTable table = null;
        Utilities ut = new Utilities();
        try{
        	shipments = apb.getShipments();
	      
	        int noOfHeaders = 0;
	        float [] relativeWidths = null;
	       
	        noOfHeaders = 10;
	        relativeWidths = new float[noOfHeaders]; 
	       
	        relativeWidths [0] = 20; // status
	        relativeWidths [1] = 8; // net amount
	        relativeWidths [2] = 8; // send money color, green
	        relativeWidths [3] = 12; // hp
	        relativeWidths [4] = 11; // city
	        relativeWidths [5] = 15; // cust name
	        relativeWidths [6] = 7; // receipt no
	        relativeWidths [7] = 8; // date 
	        relativeWidths [8] = 10; // date 
	        relativeWidths [9] = 5; // seq
	        
	        table = new PdfPTable(relativeWidths);
	        table.setPaddingTop(20);
	        table.setWidthPercentage(100);
	        
	        font.setSize(10);
	        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
	        // headers
	        //insertHeaders(PdfPTable table, LinkedList<String> headersList, Font font)
	        LinkedList<String> headers = new LinkedList<String>();
	        headers.add("#");
	        headers.add("تاريخ الأدخال");
	        headers.add("رقم الوصل");
	        headers.add("أسم صاحب المحل");
	        headers.add("العنوان");
	        headers.add("رقم الهاتف");
	        headers.add("مبلغ الوصل");
	        headers.add("مبلغ الشحن");
	        headers.add("الصافي للوكيل");
	        headers.add("الحاله");
	        
	       
	        insertHeaders (table, headers, font);
	        font.setColor(0,0,0);
			table.setHeaderRows(1);
			Phrase ph; 
			PdfPCell cell;
			int i=1;
			double shipmentCharge = 0;
            double agentCharges = 0;
            double totalCharge = 0;
            double totMoneySent = 0;
            double agentChargesCount = 0;
            double shipmentChargeCount = 0;
            double totalChargeCount = 0;
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
				
				ph = new Phrase(ci.getCreateddt(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getCustReceiptNoOri(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getName(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);

				ph = new Phrase(ci.getLocationDetails(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				ph = new Phrase(ci.getHp(),font);
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				
				
				//all charges
				if(ci.getStatus().equalsIgnoreCase("dlv")) {
					ph = new Phrase(numFormat.format(ci.getReceiptAmt()),font);
					totalCharge = ci.getReceiptAmt();
				} else {
					totalCharge=0;
					ph = new Phrase(("-"),font);
					
				}
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				totalChargeCount +=totalCharge;
				
				
				//shipment charges
				if(ci.getStatus().equalsIgnoreCase("dlv")) {
					ph = new Phrase(numFormat.format(ci.getShipmentCharge()),font);
				    shipmentCharge = 0;
				} else {
					if (ci.getShipmentChargesPaidByCustomer().equalsIgnoreCase("Y") ) {
						shipmentCharge = ci.getShipmentCharge();
						ph = new Phrase(numFormat.format(ci.getShipmentCharge()),font);
					}else {
						shipmentCharge=0;
						ph = new Phrase(("-"),font);
					}
				}
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				shipmentChargeCount +=shipmentCharge;
				
				
				//agent charges
				if(ci.getStatus().equalsIgnoreCase("dlv")) {
					agentCharges = ci.getAgentShare();
					ph = new Phrase(numFormat.format(agentCharges),font);
				} else {
					if (ci.getShipmentChargesPaidByCustomer().equalsIgnoreCase("Y") || ci.getShipmentChargesPaidBysender().equalsIgnoreCase("Y")) {
						agentCharges = ci.getAgentShare();
						ph = new Phrase(numFormat.format(agentCharges),font);
					}else {
						agentCharges = 0;
						ph = new Phrase(("-"),font);
					}
				}
				cell = new PdfPCell(ph);
				cell.setPaddingBottom(10);
				cell.setBackgroundColor(bc);
				table.addCell(cell);
				agentChargesCount +=agentCharges;


				
				if (ci.getStatus().equalsIgnoreCase("dlv")) {
					ph = new Phrase("تم التسليم",font);
				}else {
					if (ci.getShipmentChargesPaidByCustomer().equalsIgnoreCase("Y") 
							&& ci.getShipmentChargesPaidBysender().equalsIgnoreCase("N")) {
						ph = new Phrase("راجع مع دفع أجور النقل",font);
					}else if (ci.getShipmentChargesPaidByCustomer().equalsIgnoreCase("N") 
							&& ci.getShipmentChargesPaidBysender().equalsIgnoreCase("Y")) {
						ph = new Phrase("راجع مع دفع أجور النقل من صاحب المحل",font);
					}else if (ci.getShipmentChargesPaidByCustomer().equalsIgnoreCase("Y") 
							&& ci.getShipmentChargesPaidBysender().equalsIgnoreCase("Y")) {
						ph = new Phrase("خطأ في النظام أتصل بسوفتيكا",font);
						errorFlag = true;
					}else {
						ph = new Phrase("مرتجع",font);
					}
					
					
				}
				cell = new PdfPCell(ph);
				cell.setBackgroundColor(bc);
				cell.setPaddingBottom(10);
				table.addCell(cell);
				
				i++;
        	}
        	double amountToBeCollectedFromAgent = totalChargeCount + shipmentChargeCount - agentChargesCount;
        	if (amountToBeCollectedFromAgent <0 ) amountToBeCollectedFromAgent = 0;
        	ph = new Phrase(" المجموع ",font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(10);
			cell.setPaddingRight(10);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(new BaseColor(210, 185, 205));
			cell.setColspan(6);
			table.addCell(cell);
			
			ph = new Phrase(numFormat.format(totalChargeCount),font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(4);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(new BaseColor(210, 185, 205));
 			cell.setColspan(1);
			table.addCell(cell);
			
					
			ph = new Phrase(numFormat.format(shipmentChargeCount),font);
			cell = new PdfPCell(ph);
			cell.setPaddingBottom(4);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
 			cell.setBackgroundColor(new BaseColor(210, 185, 205));
 			cell.setColspan(1);
			table.addCell(cell);
			
			
			ph = new Phrase(numFormat.format(agentChargesCount),font);
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
			
			ph = new Phrase(" المبلغ المطلوب أستلامه من الوكيل ",font);
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
