package com.app.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;






import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

//import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorderPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocDefaults;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLanguage; 
//import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd.Enum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTFtnEdnImpl;
//import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShortHexNumber;

//import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;


import com.app.db.mysql;

import com.app.core.SqlMgr;
import com.app.core.smartyLogAndErrorHandling;

import java.sql.*;
public class MicrosoftWordExporter extends FilesExport {
	private String MSdocsDir;
	private String fullFilePathName;
	private String fileName;
	private mysql Mysqlconn;
	private SqlMgr mySqlMgr;
	private String billID;
	private ResultSet rs;
	private String logErrorMsg;
	public String docDir="";// the document directory
	public String docLang = "en-US";// arabic kuwait
	public String docExtension = ".docx";
	public ParagraphAlignment cellAlignment;
	public ParagraphAlignment paraGraphAlignment;
	
	
	public  void prepareDocument(Map<String, String[]> dataToExport,int noOfRows ,ArrayList<String> colsList, 
			HashMap<String,String>colLabel ,ArrayList<String> arabicColsList, String ctxPath , boolean landscape,
			String userDefinedCaption){
		cellAlignment      = ParagraphAlignment.CENTER;
		paraGraphAlignment = ParagraphAlignment.CENTER;
		MSdocsDir = docDir+"MSDocs";
		fileName= "smarty_test"+docExtension;
		fullFilePathName = MSdocsDir+"\\"+fileName; 
		int i = 1;
		createDir(MSdocsDir);
	    // set the connection
		
		XWPFDocument document = null;
		document = new XWPFDocument();
		/*createParagraph( document , custName);
		createParagraph( document , billDate);
		createParagraph( document , billNo);
		*/
		
		XWPFTable poiTable = document.createTable();
		CTTbl table        = poiTable.getCTTbl();
		CTTblPr tblpr      = table.getTblPr();//table properties
		// shading PCT_10 means 10%
		CTShd shade = tblpr.addNewShd();
		shade.setVal(STShd.PCT_10);
		shade.setColor("auto");
		shade.setFill("auto");
		tblpr.setShd(shade);
		// align right
		CTJc jc            = tblpr.addNewJc();		
		jc.setVal(STJc.CENTER);
		tblpr.setJc(jc);
		// Autofit to document
		CTTblWidth  tblW = tblpr.getTblW();
		tblW.setW(BigInteger.valueOf(5000));
		tblW.setType(STTblWidth.PCT);
		tblpr.setTblW(tblW);
		
		table.setTblPr(tblpr);
		/*End of Alignement*/

		
		XWPFTableRow tableRow = poiTable.getRow(0);
		//tableRow.removeCell(0);
		for (int c=0 ; c <colsList.size() ; c++){
			CreateTableCell (tableRow ,  colLabel.get(colsList.get(c)) ,true,false, c);
		}
		
		for (int j=1 ; j<=noOfRows ; j++){
			tableRow = poiTable.createRow();
			for (int c=0 ; c <colsList.size() ; c++){
				CreateTableCell (tableRow ,  dataToExport.get("smartyrownum_"+j+"_smartycol_"+colsList.get(c))[0] ,false,false, c);
			}
		}
		
        FileOutputStream outStream = null;
        try {
        	System.out.println("fullFilePathName=>"+fullFilePathName);
            outStream = new FileOutputStream(fullFilePathName);
            
        } catch (FileNotFoundException e) {
			logErrorMsg = "class=>GenBillDoc,FileNotFoundException,Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logError("GenBillDoc", Level.SEVERE, logErrorMsg , e);
			logErrorMsg = "";
            e.printStackTrace();
        }

       try {
           document.write(outStream);
           outStream.close();
       } catch (FileNotFoundException e) {
			logErrorMsg = "class=>GenBillDoc,FileNotFoundException, writing outstream,Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logError("GenBillDoc", Level.SEVERE, logErrorMsg , e);
			logErrorMsg = "";
           e.printStackTrace();
       } catch (IOException e) {
			logErrorMsg = "class=>GenBillDoc,IOException,Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logError("GenBillDoc", Level.SEVERE, logErrorMsg , e);
			logErrorMsg = "";
           e.printStackTrace();
       }finally{
    	   try {
			outStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       }
       
       File directory = new File (".");
       try {
           System.out.println ("Current directory's canonical path: " + directory.getCanonicalPath()); 
           System.out.println ("Current directory's absolute  path: " + directory.getAbsolutePath());
           setDocPath(directory.getCanonicalPath()+"\\"+fullFilePathName);
           System.out.println ("docPath: " + getDocPath());
       }catch(Exception e) {
           System.out.println("Exceptione is ="+e.getMessage());
       }
       String currentDir = System.getProperty("user.dir");
       System.out.println("Current dir using System:" +currentDir);
   }
	
	//Add Table Cell
	public void CreateTableCell (XWPFTableRow tableRow , String text ,boolean firstRow ,boolean lastRow , int pos){
		XWPFTableCell cell    = null;
		XWPFParagraph cellPar = null;
		XWPFRun cellParRun    = null;
		CTR cellCTR           = null;
		CTRPr cellrpr		  = null;
		CTLanguage lang       = null;
		CTShd shade			  = null;
		boolean bold          = false;
		Enum valShade		  = STShd.PCT_5;
		if (firstRow && (pos !=0))
			cell = tableRow.addNewTableCell();
		else
			cell = tableRow.getCell(pos);
		
		if (firstRow){
			bold = true;
			valShade = STShd.PCT_40;
		}else if (lastRow){
			valShade = STShd.PCT_20;
			bold = true;
		}
        cell.removeParagraph(0);		
        cellPar       = cell.addParagraph();
        cellPar.setAlignment(cellAlignment);
        cellPar.setSpacingAfter(120);
        cellPar.setSpacingBefore(120);
        cellParRun    = cellPar.createRun();
        cellCTR       = cellParRun.getCTR();
        cellrpr       = cellCTR.addNewRPr();
        //cellrpr.addnewsp
		lang          = cellrpr.addNewLang();
		lang.setVal(docLang);
		cellCTR.setRPr(cellrpr);
		cellParRun.setBold(bold);
		cellParRun.setText(text);
        shade = cell.getCTTc().addNewTcPr().addNewShd();
        shade.setVal(valShade);
		shade.setColor("auto");
		shade.setFill("auto");
	}

	// Add Paragraph
	public void createParagraph(XWPFDocument document , String text){
		XWPFParagraph par     = document.createParagraph();
		par.setAlignment(paraGraphAlignment);
		XWPFRun parRun        = par.createRun();
		CTLanguage lang       = null;
		CTR r                 = parRun.getCTR();
		CTRPr rpr             = r.addNewRPr();
		lang                  = rpr.addNewLang();
		lang.setVal(docLang);
		r.setRPr(rpr);
		parRun.setBold(true);
		parRun.setText(text);
	}
	
	// Create Page Borders
	public void addBorder(XWPFDocument document){
		
		CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
		CTPageBorders pageBorder = sectPr.addNewPgBorders();
		// top
		CTBorder borTop =  pageBorder.addNewTop();
		borTop.setVal(STBorder.SINGLE);
		borTop.setSpace(BigInteger.valueOf(24));
		borTop.setSz(BigInteger.valueOf(2));
		// bottom
		CTBorder borBott =  pageBorder.addNewBottom();
		borBott.setVal(STBorder.SINGLE);
		borBott.setSpace(BigInteger.valueOf(24));
		borBott.setSz(BigInteger.valueOf(2));
		//left
		CTBorder borLeft =  pageBorder.addNewLeft();
		borLeft.setVal(STBorder.SINGLE);
		borLeft.setSpace(BigInteger.valueOf(24));
		borLeft.setSz(BigInteger.valueOf(2));
		//right
		CTBorder borRight =  pageBorder.addNewRight();
		borRight.setVal(STBorder.SINGLE);
		borRight.setSpace(BigInteger.valueOf(24));
		borRight.setSz(BigInteger.valueOf(2));
	}
	
	public mysql getConn() {
		return Mysqlconn;
	}

	public void setConn(mysql conn) {
		this.Mysqlconn = conn;
	}

	public String getBillID() {
		return billID;
	}

	public void setBillID(String billID) {
		this.billID = billID;
	}

	public String getFullFilePathName() {
		return fullFilePathName;
	}

	public void setFullFilePathName(String fullFilePathName) {
		this.fullFilePathName = fullFilePathName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}




}

