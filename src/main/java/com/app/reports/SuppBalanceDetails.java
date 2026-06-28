package com.app.reports;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;

import smarty.core.CoreMgr;
import smarty.core.smartyLogAndErrorHandling;
public class SuppBalanceDetails extends CoreMgr{
	public SuppBalanceDetails(){
		MainSql = " select rmk , pmtamt , tranamt , trandate , concat(Month(trandate),' - ',year(trandate)) as MonthYear " +
				" from (select '' as rmk ,  '' as pmtamt ,sum(mbd_totalamt)-sum(coalesce(mb_discountamt,0)) as tranamt , mb_billdt as trandate " +
				  " from mbills_in join mbilldtl_in on mbills_in.mb_id = mbilldtl_in.mbd_billin_id "+ 
				  " where mb_suppid = {transrptsuppid} group by mb_billdt "+ 
				  " union "+
				  " select pmt_rmk as rmk, sum(pmt_amt) as pmtamt , '' as tranamt, pmt_paidon as trandate  From m_payments_debit " +
				  "where pmt_to ={transrptsuppid} group by pmt_paidon , pmt_rmk" +
				  ") " +
				  " as tranTBL order by trandate asc";
		
		userDefinedColLabel.put("rmk", "ملاحظات التسديدات");
		userDefinedColLabel.put("pmtamt", "تسديدات");
		userDefinedColLabel.put("tranamt", "ديون");
		userDefinedColLabel.put("trandate", "تاريخ");
		
		userDefinedGridCols.add("rmk");
		userDefinedGridCols.add("pmtamt");
		userDefinedGridCols.add("tranamt");
		userDefinedGridCols.add("trandate");
		
		userDefinedColsTypes.put("tranamt", "DOUBLE");
		userDefinedColsTypes.put("pmtamt", "DOUBLE");
		
		userDefinedGroupByCol = "MonthYear";
		userDefinedGroupSortMode = "desc";
		userDefinedGroupColsOrderBy = "trandate";// this is important special case feature , because i cant sort by MonthYear , since it's a character
												  // so month 10 will come before month 2 , and thats wrong, since we are deriving the MonthYear
												  // col from the date , then we can sort by the date column which used for deriving MonthYear.
	
		userDefinedSumCols.add("tranamt");
		userDefinedSumCols.add("pmtamt");
	}
	public void initialize(HashMap smartyStateMap){
		ResultSet rs = null;
		Statement s  = null;
		userDefinedCaption = "select supp_name from kbsupplier where supp_id={transrptsuppid}";
		
		userDefinedCaption = userDefinedCaption.toLowerCase();
		userDefinedCaption = replaceVarsinString(userDefinedCaption, arrayGlobals);
		 try{
			 s  = conn.createStatement();
			 rs = s.executeQuery(userDefinedCaption);
			 if (rs.first()){
				 userDefinedCaption= rs.getString(1);
			 }else{
				 userDefinedCaption ="";
			 }
		 }catch(Exception e){
			logErrorMsg = "class=>"+myClassBean+",userDefinedCaption=>"+userDefinedCaption+",Exception Msg=>"+e.getMessage(); 
			smartyLogAndErrorHandling.logError(myClassBean, Level.SEVERE, logErrorMsg ,e);
			logErrorMsg = "";
			e.printStackTrace();
		 }finally{
			 if (rs !=null){
				 try{
					 rs.close();
				 }catch(SQLException e){}
			 }
			 if (s !=null){
				 try{
					 s.close();
				 }catch (SQLException e){}
			 }
		 }
		 super.initialize(smartyStateMap);
	}
}
