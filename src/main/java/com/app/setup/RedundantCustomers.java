package com.app.setup;

import smarty.core.CoreMgr;

public class RedundantCustomers extends CoreMgr{
	public  RedundantCustomers () {
		MainSql = "select c_phone1, c_name, count(*) as red from kbcustomers group by c_phone1 having count(*) >1";
		userDefinedGridCols.add("c_name");
		userDefinedGridCols.add("c_phone1");
		userDefinedGridCols.add("red");
		
		userDefinedColLabel.put("c_name", "إسم صاحب المحل");
		userDefinedColLabel.put("c_phone1", "هاتف");
		userDefinedColLabel.put("red", "تكرار");
	}
}
