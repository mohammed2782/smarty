package com.app.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.Barcode128;
public class BarCode {
	
	public void generateBarCode (String code) {
		 Document document = new Document(new Rectangle(PageSize.A4));    
		    PdfWriter writer = null;
			try {
				writer = PdfWriter.getInstance(document, new FileOutputStream("D:/Java4s_BarCode_128.pdf"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    

		    document.open();
			  

				    Barcode128 code128 = new Barcode128();
				    code128.setGenerateChecksum(true);
				    code128.setCode("1234554321Format_Java4s.com");
				  

			    try {
					document.add(code128.createImageWithBarcode(writer.getDirectContent(), null,BaseColor.WHITE));
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    document.close();

		    System.out.println("Document Generated...!!!!!!");
	}
}
