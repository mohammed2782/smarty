package com.app.cases;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.app.bussframework.FlowUtils;
import com.app.util.Utilities;

import smarty.core.CoreMgr;
import smarty.core.CoreUtilities;
import smarty.core.ExcelParser;
import smarty.db.mysql;

public class NewCasesViaUpload extends CoreMgr{
	private boolean error;
	private String errorMsg;
	private String doneMsg = "";
	private boolean done;
	public NewCasesViaUpload() {

		MainSql = "select  p_excelbatch.*, '' del, now() as systime, "
				+ " (eb_uploadeddt+INTERVAL 2 hour) as timetodel, '' as qstep "
				+ " from p_excelbatch where eb_branch ={userstorecode} order by 1 desc";
		
		canNew = true;
		//canDelete = true;
		mainTable = "p_excelbatch_qi";
		keyCol= "eb_id";
		
		userDefinedCaption = " 2 أستيراد شحنات من الأكسل";
		
		userDefinedNewColsHtmlType.put("eb_filename", "IMAGE");
		UserDefindNewFormEnctype = "enctype='multipart/form-data'";
		
		userDefinedNewCols.add("eb_shipmentdate");
		userDefinedNewCols.add("eb_filename");
		userDefinedNewCols.add("eb_customer");

		//userDefinedNewCols.add("qstep");
		
		userDefinedColsMustFill.add("eb_pickupagent");
		userDefinedColsMustFill.add("eb_shipmentdate");
		userDefinedColsMustFill.add("eb_filename");
		userDefinedColsMustFill.add("eb_customer");

		//userDefinedColsMustFill.add("qstep");
		
		userDefinedColLabel.put("qstep", "المرحلة");
		userDefinedColLabel.put("eb_shipmentdate", "تاريخ الوجبة");
		userDefinedColLabel.put("eb_filename", "ملف الأكسل");
		userDefinedColLabel.put("eb_filenamebeforechange", "ملف الأكسل");
		userDefinedColLabel.put("eb_uploadedby", "تم الرفع عن طريق");
		userDefinedColLabel.put("eb_customer", "اسم العميل");
		userDefinedColLabel.put("del", "");
		
		userDefinedGridCols.add("eb_shipmentdate");
		userDefinedGridCols.add("eb_filenamebeforechange");
		userDefinedGridCols.add("eb_uploadedby");
		userDefinedGridCols.add("del");
		
		userModifyTD.put("del", "showDel({systime},{timetodel},{eb_id})");
		
		userDefinedLookups.put("qstep", "select stp_code, stp_name from kbstep where stp_code in ('instorage','NEW_ONWAY')");
		userDefinedLookups.put("eb_customer","select cust_id , cust_name from kbcustomers where cust_branch={userstorecode}  ");

		myhtmlmgr.refreshPageOnDelete = true;
		userDefinedNewFormColNo = 2;
		userDefinedNewColsHtmlType.put("eb_pickupagent", "DROPLIST");
		userDefinedNewColsHtmlType.put("eb_customer", "DROPLIST");
	}
	public String showDel (HashMap<String,String> hashy) {
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date inputdate = null;
		Date sysdate = null;
		try {
			inputdate = formatter.parse(hashy.get("timetodel"));
			sysdate = formatter.parse(hashy.get("systime"));
			//System.out.println("inputdate = "+inputdate+" 	sysdate = "+sysdate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (inputdate!=null&&sysdate!=null) {
			if(inputdate.compareTo(sysdate) > 0){
				//System.out.println("hashy.get('eb_id') = "+hashy.get("eb_id"));
				return "<td align='center' style='vertical-align: middle;'>"
					+"<button type='button' "
					+ " onclick=\"link=false; "
					+ " var rs =doDeleteSmarty(this,'هل تريد حذف شيت الاكسل ؟' ,'eb_id','"+hashy.get("eb_id")+"' , 'com.app.cases.NewCasesViaUpload' ); return rs;\" class='btn btn-danger btn-xs'>"
							+ "<li class='fa fa-trash'></li></button>";
			}
		}
		return "<td></td>";
	}
	
	@Override
	public String doDelete(HttpServletRequest rqs){
		String Msg ="";
		PreparedStatement pst = null;
		String keyVal= rqs.getParameter("eb_id");
		//System.out.println("keyVal = "+keyVal );
		String userid = replaceVarsinString(" {userid} ", arrayGlobals).trim();
		Connection conn = null;
		try {
			conn = mysql.getConn();
			//bakcup first
			//bakcup first
			pst = conn.prepareStatement("update p_cases set c_deletedby = ?, "
					+ "c_deleteddt=now() where  c_excelnumber = ? and c_settled = 'NO' and c_agentpmtid=0");
			pst.setString(1,userid );
			pst.setString(2,keyVal );
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			
			pst = conn.prepareStatement("insert into p_cases_deleted select p_cases.* "
					+ "from p_cases where c_excelnumber = ? and c_settled = 'NO' and c_agentpmtid=0");
			pst.setString(1,keyVal );
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}


			pst = conn.prepareStatement("delete from p_cases where c_excelnumber = ? and c_settled='NO' and c_agentpmtid=0");
			pst.setString(1, keyVal);
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			
			//delete the excel
			pst = conn.prepareStatement("delete from p_excelbatch where eb_id=? and eb_id not in "
					+ " (select c_excelnumber from p_cases where c_excelnumber=? and c_settled='NO'"
					+ " and c_agentpmtid=0) ");
			pst.setString(1, keyVal);
			pst.setString(2, keyVal);
			pst.executeUpdate();
			try {pst.close();}catch(Exception e) {}
			
			conn.commit();
		}catch (Exception e) {
			try{conn.rollback();}catch (Exception eRollBack){eRollBack.printStackTrace();}
			e.printStackTrace();
		    Msg = "Error";
		    deleteErrorFlag = true;
		    
		}finally {
			try {pst.close();}catch(Exception e) {}
			try {conn.close();}catch(Exception e) {}
		}
		return Msg;
	}
	
	@Override 
	public String doInsert (HttpServletRequest rqs, boolean autoCommit ) {
		String msg = "";
		System.out.println("do insert -------------------");
		FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory); 
        List<FileItem> items = null;
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int noOfRecsImported=0;
        ExcelParser ep = new ExcelParser();
        try {
        	conn = mysql.getConn();
			items = upload.parseRequest(rqs);
			Iterator<FileItem> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem fileItem = iter.next();
				if (fileItem.isFormField()) {
					inputMap_ori.put(fileItem.getFieldName(), new String []{fileItem.getString("UTF-8")});
				} else {
					inputFilesMap.put(fileItem.getFieldName(),fileItem);
				}   	
			}
			int currentBranch_G = Integer.parseInt(replaceVarsinString("{userstorecode}", arrayGlobals).trim());
			String upLocation = rqs.getServletContext().getInitParameter("mds.excel.upload.location")+"/"+currentBranch_G+"/";
			String fileName = uploadFile(inputFilesMap.get("eb_filename"),upLocation );
			System.out.println("fileName -------------------"+fileName);
			
			int userId_G = Integer.parseInt(replaceVarsinString("{userid}", arrayGlobals).trim());
			pst = conn.prepareStatement("insert into p_excelbatch"
			+ "(eb_uploadedby, eb_filename, eb_shipmentdate, eb_filenamebeforechange, eb_branch) "
			+ " values("+CoreUtilities.getQuestionMarks(5)+")", Statement.RETURN_GENERATED_KEYS);
			pst.setInt(1, userId_G);
			pst.setString(2, fileName);
			pst.setString(3, inputMap_ori.get("eb_shipmentdate")[0]);
			pst.setString(4,  inputFilesMap.get("eb_filename").getName());
			pst.setInt(5, currentBranch_G);
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			int batchId = 0;
			if (rs.next()) {
				batchId = rs.getInt(1);
			}
			System.out.println("created record in  -------------------p_excelbatch");
			LinkedHashMap <Integer , LinkedHashMap<Integer,String>> excelData=
					ep.readBooksFromExcelFile(upLocation+""+fileName, 0);
			
			System.out.println("finished reading excel  -------------------");
			ArrayList<CaseInformation> ciList = convertExcelToCaseInformation(excelData);
			System.out.println("converted to ciList----"+ciList.size());
			noOfRecsImported = importCases(conn, 
					batchId , 
					ciList , 
					userId_G,
					inputMap_ori.get("eb_shipmentdate")[0], 
					currentBranch_G,Integer.parseInt(inputMap_ori.get("eb_customer")[0]));
			if(noOfRecsImported>0) {
				conn.commit();
			}else {
				try {conn.rollback();}catch(Exception eRoll) {/**/}
				msg = "لم يتم إدخال إي شحنة";
			}
			msg = doneMsg;
        }catch(Exception e) {
        	e.printStackTrace();
        	msg = "Error "+e.getMessage();
        	try {conn.rollback();}catch(Exception eRoll) {/**/}
        }finally {
        	try{rs.close();}catch(Exception e){/*IGNORE*/}
			try{pst.close();}catch(Exception e){/*IGNORE*/}
			try{conn.close();}catch(Exception e){/*IGNORE*/}
        }
		return msg;
		
	}
	
	public String  uploadFile(FileItem myFile , String upLocation) throws Exception{
		Utilities ut = new Utilities();
		String uploadedFileName ="";
		if (myFile.getName().endsWith("xls") || myFile.getName().endsWith("xlsx")){
			;
		}else{
			setError(true);
			throw new Exception (" this is not Excel File");
		}
		if (!isError()){
			
			InputStream is = myFile.getInputStream();
			try {
				String uploadName = new String( myFile.getName().getBytes(), "utf-8" );
				uploadedFileName = ut.writeToFileServer (is, uploadName,upLocation);
				
			} catch (IOException e) {
				
				e.printStackTrace();
				setError(true);
				throw new Exception(e.getMessage());
			}catch (Exception e) {
				setError(true);
				throw new Exception(e.getMessage());
			}finally {
				try {is.close();}catch(Exception e) {/*ignore*/}
			}
		}
		setDone(true);
		return uploadedFileName;
	}
	
	private  ArrayList<CaseInformation> convertExcelToCaseInformation(
			LinkedHashMap <Integer , LinkedHashMap<Integer,String>> a_excelData) throws Exception{
		 ArrayList<CaseInformation> ciList = new ArrayList<CaseInformation>();
		 CaseInformation ci = null;
		 int excelRowNo = 0;
		
		 for (int key : a_excelData.keySet()){
			excelRowNo++;
			ci = new CaseInformation();
			if (a_excelData.get(key).get(3) ==null || a_excelData.get(key).get(3).trim().isEmpty()) {
				if (a_excelData.get(key).get(5) ==null || a_excelData.get(key).get(5).trim().isEmpty()) {
					if (a_excelData.get(key).get(6) ==null || a_excelData.get(key).get(6).trim().isEmpty()) {
						//no record 
						break;
					}
				}
			}
			
			//cust name
			if (a_excelData.get(key).get(0) !=null && !a_excelData.get(key).get(0).trim().isEmpty()) {
				ci.setReceiverName(a_excelData.get(key).get(0));
			}
			
			// receipt amt IQD
			if (a_excelData.get(key).get(1) !=null && !a_excelData.get(key).get(1).trim().isEmpty()) {
				try {
					ci.setReceiptAmtIqd(Long.parseLong(a_excelData.get(key).get(1)));
				}catch(Exception e) {
					ci.setReceiptAmtIqd(0);
				}
			}else {
				ci.setReceiptAmtIqd(0);
			}
			
			// receipt amt USD
			if (a_excelData.get(key).get(2) !=null && !a_excelData.get(key).get(2).trim().isEmpty()) {
				try {
					ci.setReceiptAmtUsd(Long.parseLong(a_excelData.get(key).get(2)));
				}catch(Exception e) {
					ci.setReceiptAmtUsd(0);
				}
			}else {
				ci.setReceiptAmtUsd(0);
			}
		
			
			// receipt no
			if (a_excelData.get(key).get(3) !=null && !a_excelData.get(key).get(3).trim().isEmpty()) {
				ci.setCustReceiptNoOri(a_excelData.get(key).get(3));
			}else {
				throw new Exception ("لا يوجد رقم وصل في السطر رقم "+excelRowNo);
			}
			
			
			// state code
			if (a_excelData.get(key).get(5) !=null && !a_excelData.get(key).get(5).trim().isEmpty()) {
				
				PreparedStatement pst = null;
				ResultSet rs = null;
				String stateCode = "";
				try {
					pst = conn.prepareStatement("select st_code from kbstate where st_code=?");
					pst.setString(1, a_excelData.get(key).get(5).trim());
					rs = pst.executeQuery();
					if (rs.next())
						stateCode = rs.getString("st_code");
				} catch (Exception e) {
					throw e;
				} finally {
					try {
						rs.close();
					} catch (Exception e) {
						/* ignore */}
					try {
						pst.close();
					} catch (Exception e) {
						/* ignore */}
				}
				
				ci.setState(stateCode);
			}else {
				throw new Exception ("لا يوجد رمز المحافظة "+excelRowNo);
			}
						
			
			// receive hp1
			if (a_excelData.get(key).get(6) !=null && !a_excelData.get(key).get(6).trim().isEmpty()) {
				ci.setReceiverHp1(a_excelData.get(key).get(6).trim());
			}else {
				throw new Exception ("لا يوجد رقم هاتف في السطر رقم "+excelRowNo);
			}
			
			//rmk
			if (a_excelData.get(key).get(7) !=null && !a_excelData.get(key).get(7).trim().isEmpty()) {
				ci.setRmk(a_excelData.get(key).get(7).trim());
			}
	
			ciList.add(ci);
			
				
		}
		 System.out.println("ciList --->"+ciList.size());
		return ciList;
	}
	
	private  int importCases(
			Connection a_conn, 
			int a_batchId ,
			ArrayList<CaseInformation> a_ciList,
			int a_userId, 
			String a_creationDate,
			int a_branchId,int senderId)throws Exception{
		PreparedStatement pst = null, pstCustomerId = null, pstCreateNewCustomer = null;
		ResultSet rs = null;
		int recNo = 0,excelRowNo=1, caseId = 0;
		boolean custFound = false;
		Utilities ut = new Utilities();
		FlowUtils fu = new FlowUtils();
		double shipmentCharges = 0;
		try{
	
			pst = a_conn.prepareStatement("insert into p_cases "
			+ " (c_rmk			   , c_rcv_hp1		, c_rcv_addr_rmk, c_receiptamt_usd, c_receiptamt,  "
			+ "  c_custreceiptnoori, c_custid 		, c_rcv_name 	, c_createdby     , c_rcv_state,"
			+ "  c_rcv_district	   , c_shipment_cost, c_branchcode  , c_excelnumber   , c_mastercustid,"
			+ "	 c_createddt)"
			+ " values  ("+CoreUtilities.getQuestionMarks(15)+", now())",Statement.RETURN_GENERATED_KEYS);
			int masterCutomerId = Utilities.getMasterCustomerIdFromCustomerId(a_conn, senderId);
			for (CaseInformation ci : a_ciList){
				String destState = ci.getState();
				int unIdentifiedDistrictId = Utilities.getUnSpecifiedDistrictId(a_conn, destState);
				shipmentCharges = ut.calcShipmentChargesBasedOnDestCity(a_conn, destState, false , 0, senderId, a_branchId);
				pst.setString(1, ci.getRmk());
				pst.setString(2, ci.getReceiverHp1().trim());
				pst.setString(3, "");
				pst.setDouble(4, ci.getReceiptAmtUsd());
				pst.setDouble(5, ci.getReceiptAmtIqd());
				pst.setString(6, ci.getCustReceiptNoOri());
				pst.setInt(7, senderId);
				pst.setString(8, ci.getReceiverName());
				pst.setInt(9, a_userId);
				pst.setString(10, ci.getState());
				pst.setInt(11, unIdentifiedDistrictId);
				pst.setDouble(12,shipmentCharges);
				pst.setInt(13, a_branchId);
				pst.setInt(14, a_batchId);
				pst.setInt(15, masterCutomerId);
				pst.executeUpdate();
				rs = pst.getGeneratedKeys();
				if (rs.next())
					caseId = rs.getInt(1);
				pst.clearParameters();
				try {rs.close();}catch(Exception e) {/*ignore*/}
				if (caseId>0) {
					fu.createNewCaseInQueue(a_conn,caseId, a_branchId);
				}
				recNo++;
			}
			doneMsg +="تم ادخال "+recNo+" شحنه</br>";
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception ("خطا في السطر رقم "+recNo+", "+e.getMessage());
		}finally{
			try{rs.close();}catch(Exception e){/*IGNORE*/}
			try{pst.close();}catch(Exception e){/*IGNORE*/}
			try{pstCustomerId.close();}catch(Exception e){/*IGNORE*/}
			try{pstCreateNewCustomer.close();}catch(Exception e){/*IGNORE*/}
		}
		return recNo;
	}



	public boolean isError() {
		return error;
	}


	public void setError(boolean error) {
		this.error = error;
	}


	public boolean isDone() {
		return done;
	}


	public void setDone(boolean done) {
		this.done = done;
	}
}