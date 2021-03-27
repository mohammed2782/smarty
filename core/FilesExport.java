package com.app.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class FilesExport {
	private  String docPath;
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
	public abstract void prepareDocument( Map<String, String[]> dataToExport , int noOfRows ,ArrayList<String> colsList,
			HashMap<String,String> colLabel , ArrayList<String> arabicColsList, String ctxPath , boolean landscape,
			String userDefinedCaption);
	public String getDocPath() {
		return docPath;
	}
	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}
}
