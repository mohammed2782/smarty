package com.app.reports;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;

import smarty.core.CoreMgr;
import smarty.core.smartyLogAndErrorHandling;
public class CustomerBalanceDetails extends CoreMgr{
	public CustomerBalanceDetails(){
		MainSql = " select * from (select '' as rmk ,  '' as pmtamt ,sum(mbd_totalamount)-sum(coalesce(mb_discountamt,0))+ sum(coalesce(mb_addprofit,0)) as tranamt , mb_billdt as trandate " +
				  " from mbills_out join mbilldtl_out on mbills_out.mb_id = mbilldtl_out.mbd_bill_id "+ 
				  " where mb_custid = {transrptcustid} group by mb_billdt "+ 
				  " union "+
				  " select pmt_rmk as rmk, sum(pmt_amt) as pmtamt , '' as tranamt, pmt_paidon as trandate  From m_payments_credit " +
				  "where pmt_by ={transrptcustid} group by pmt_paidon , pmt_rmk" +
				  ") " +
				  " as tranTBL order by trandate";
		
		userDefinedColLabel.put("rmk", "ملاحظات التسديدات");
		userDefinedColLabel.put("pmtamt", "تسديدات");
		userDefinedColLabel.put("tranamt", "ديون");
		userDefinedColLabel.put("trandate", "تاريخ");
		
		userDefinedColsTypes.put("tranamt", "DOUBLE");
		userDefinedColsTypes.put("pmtamt", "DOUBLE");
	}
	public void initialize(HashMap smartyStateMap){
		ResultSet rs = null;
		Statement s  = null;
		userDefinedCaption = "select c_name from kbcustomers where c_id={transrptcustid}";
		
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
