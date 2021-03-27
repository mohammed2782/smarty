package com.app.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;



public class PDFExporter extends FilesExport{
	private String docsDir;
	private String fullFilePathName;
	private String fileName;
	public String docDir="";// the document directory
	public String docLang = "en-US";// arabic kuwait
	public String docExtension = ".pdf";
	String ctxPath = "";

	public static final Font FONT = new Font(FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYWHITE);
	
	class MyFooter extends PdfPageEventHelper {
        Font ffont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);
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
            PdfContentByte cb = writer.getDirectContent();
            pageNo ++;
            try {
            	cb.addImage(image,PageSize.A4.getWidth()-525,0,0,65,35,document.top()+10);
            	//cb.addImage(image,PageSize.A4.getWidth()-430,0,0,55,20,document.top()+20);
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
        }
    }
	
	@SuppressWarnings("null")
	@Override
	public void prepareDocument(
			Map<String, String[]> dataToExport,
			int noOfRows, 
			ArrayList<String> colsList,
			HashMap<String, String> colLabel,
			ArrayList <String> arabicColsList,
			String ctxPath , boolean landscape,
			String userDefinedCaption) {
			this.ctxPath = ctxPath;
			String imgPath = ctxPath+"/smartyresources/img/logo_sm.png";
			
			docsDir = ctxPath+"//"+"PDFDocs";
			fileName= "smarty_test"+docExtension;
			fullFilePathName = docsDir+"//"+fileName; 
			int i = 1;
			createDir(docsDir);
			setDocPath(fullFilePathName);
			try { 
				// Initialize document
				Document document = null;
				if (landscape)
					document = new Document(PageSize.A4.rotate(), 50, 50, 85, 50);
				else
					document = new Document(PageSize.A4, 50, 50, 85, 50);
		        //Document document = new Document();
		        PdfWriter pdf = null;
		        MyFooter event = new MyFooter();
		        event.setImagePath(imgPath);
		        try {
					 pdf = PdfWriter.getInstance(document, new FileOutputStream(fullFilePathName));
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        pdf.setPageEvent(event);
		        document.open();
		        //PdfContentByte canvas = pdf.getDirectContentUnder();
		       
		        BaseFont baseFont = null;
		      // System.out.println(ctxPath+"Fonts\\NotoNaskhArabic-Regular.ttf");
				try {
					baseFont = BaseFont.createFont("../../Fonts/arial.ttf", BaseFont.IDENTITY_H, true);
					//baseFont = FontFactory.getFontImp().defaultEncoding = BaseFont.IDENTITY_H;
				} catch (DocumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        Font font  = new Font(baseFont); 
		        Font font2 = new Font();
		        font2.setSize(30);
		        ColumnText ct = new ColumnText(pdf.getDirectContent());
		        ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
			        
		        Paragraph par = new Paragraph(userDefinedCaption,font);
		        par.setAlignment(Element.ALIGN_CENTER);
		        ct.addElement(par);
		        //Rectangle rect = new Rectangle(/*starting x point*/10, /*starting y point*/600 , /*width*/850 , /*height*/30);
		        Rectangle rect;
		        if (landscape)
		        	rect = new Rectangle(/*starting x point*/10, /*starting y point*/570 , /*width*/850 , /*height*/100);
		        else
		        	rect = new Rectangle(/*starting x point*/30, /*starting y point*/810 , /*width*/600 , /*height*/70);
		        rect.setBorder(Rectangle.BOX);
		        ct.setSimpleColumn(rect);
		        try {
					ct.go();
				} catch (DocumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        PdfPTable table = new  PdfPTable(1);
		        table.setWidthPercentage(100);
		        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
		        PdfPCell cell;
		        par = new Paragraph(" ",font);
		        cell = new PdfPCell(par);
	 			cell.setPaddingTop(10);
	 			cell.setPaddingBottom(5);
	 			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	 			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	 			cell.setBorder(cell.NO_BORDER);
	            table.addCell(cell);
	            try {
					document.add(table);
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	            font.setSize(9);
		        float [] relativeWidths = new float[colsList.size()+1];
		        //relativeWidths [0] =3;
		        for (int m =0 ; m<colsList.size() ; m++){
		        	relativeWidths [m] = 10;
		        }
		        relativeWidths [colsList.size()] = 3;
		        table = new PdfPTable(relativeWidths);
		        table.setWidthPercentage(110);
		        table.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
		        table.setHeaderRows(1);
		        Phrase ph = new Phrase("",font);
		        cell = new PdfPCell(ph);
		        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
		        cell.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
		        cell.setPadding(5);
		        table.addCell(cell);
		        
		        for (String key : colLabel.keySet()){
		        	if (arabicColsList.contains(key)){
		        		ph = new Phrase(colLabel.get(key),font);
						cell = new PdfPCell(ph);
						cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
						cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
		        	}else{
			        	ph = new Phrase(colLabel.get(key),font);
			        	cell = new PdfPCell(ph);
			        	cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			            
		        	}
		        	cell.setPadding(8);
		        	table.addCell(cell);
		        }
		        table.setHeaderRows(1);
		        for (int j=1 ; j<=noOfRows ; j++){
		        	ph = new Phrase(Integer.toString(j),font);
		        	cell = new PdfPCell(ph);
		            table.addCell(cell);
					for (int c=0 ; c <colsList.size() ; c++){
						if (arabicColsList.contains(colsList.get(c))){
							ph = new Phrase(dataToExport.get("smartyrownum_"+j+"_smartycol_"+colsList.get(c))[0],font);
							cell = new PdfPCell(ph);
							cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);			
						}else{
							ph = new Phrase(dataToExport.get("smartyrownum_"+j+"_smartycol_"+colsList.get(c))[0],font);
							cell = new PdfPCell(ph);
						}
						cell.setPadding(8);
						table.addCell(cell);
					}
				}
		        try {
					document.add(table);
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       
		        document.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
	

	public void createDir(String dirName){
		System.out.println("dirName==>"+dirName);
	    File theDir = new File(dirName);
	    // if the directory does not exist, create it
	    if (!theDir.exists()) {
	        boolean result = false;
	        try{
	            theDir.mkdir();
	            result = true;
	        }catch(Exception e){
				String logErrorMsg = "class=>WordDocSettings,Exception Msg=>"+e.getMessage(); 
				smartyLogAndErrorHandling.logError("WordDocSettings", Level.SEVERE, logErrorMsg , e);
				logErrorMsg = "";
	            e.printStackTrace();
	        }        
	        if(!result) {    
	            System.out.println("DIR Failed to be created");  
	        }
	    }
	}
	
 
}
