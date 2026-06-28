package smarty.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
public class ExcelParser {
	
	
	public LinkedHashMap<Integer , LinkedHashMap <Integer,String> > 
			readBooksFromExcelFile(String excelFilePath , int sheetNo) throws IOException {
		
		LinkedHashMap<Integer , LinkedHashMap<Integer,String> > excelMatrix 
							= new LinkedHashMap<Integer , LinkedHashMap<Integer,String> >();
	    FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
		boolean xls = true;
		if (excelFilePath.endsWith("xlsx"))
			xls = false;
	
	    Sheet sheet = null;
	    if (!xls) {
	    	Workbook workbook = new XSSFWorkbook(inputStream);
	    	sheet = workbook.getSheetAt(sheetNo);
	    }else {
	    	HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
	    	sheet = workbook.getSheetAt(sheetNo);
	    }
	 
	    Iterator<Row> iterator = sheet.iterator();
	    int i =1;
	    // skip the header
        if (iterator.hasNext())
        	iterator.next();
	    while (iterator.hasNext()) {
	        Row nextRow = iterator.next();
	        Iterator<Cell> cellIterator = nextRow.cellIterator();
	        LinkedHashMap <Integer,String> cols=  new LinkedHashMap<Integer,String>();
	       
	        while (cellIterator.hasNext()) {
	            Cell nextCell = cellIterator.next();
	            int columnIndex = nextCell.getColumnIndex();
	            cols.put(columnIndex, getCellValue(nextCell));  
	        }
	        excelMatrix.put(i, cols);
	        i++;
	    }
	    inputStream.close();
	    return excelMatrix;
	}
	
	
	private String getCellValue(Cell cell) {
	    switch (cell.getCellType()) {
	    case Cell.CELL_TYPE_STRING:
	        return cell.getStringCellValue().trim();
	 
	    case Cell.CELL_TYPE_NUMERIC:
	        return new BigDecimal(cell.getNumericCellValue())+"";
	    }
	 
	    return null;
	}
}
