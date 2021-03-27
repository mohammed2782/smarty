package com.app.core;

import java.io.File;
import java.util.logging.Level;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment; 

import com.app.core.smartyLogAndErrorHandling;

public class WordDocSettings {
	public String docDir="";// the document directory
	public String docLang = "ar-KW";// arabic kuwait
	public String docExtension = ".docx";
	public ParagraphAlignment cellAlignment;
	public ParagraphAlignment paraGraphAlignment;
	public WordDocSettings(){
		cellAlignment      = ParagraphAlignment.CENTER;
		paraGraphAlignment = ParagraphAlignment.RIGHT;
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
	
	public void setdocDir (String docDir){
		this.docDir = docDir;
	}
	public String getdocDir (){
		return this.docDir;
	}

}
