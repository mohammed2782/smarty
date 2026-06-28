package com.app.tickets;

import smarty.core.CoreMgr;

public class CreateNewTicket extends CoreMgr{
	public CreateNewTicket() {
		MainSql = "select * from p_tickets where 1= 0 ";
		canNew = true;
		displayMode = "NEWSINGLE";
		userDefinedNewFormColNo = 2;
		
		mainTable = "p_tickets";
		
		userDefinedNewCols.add("tkt_subject");
		userDefinedNewCols.add("tkt_description");
		userDefinedNewCols.add("tkt_priority");
		userDefinedNewCols.add("tkt_relatedcustomer");
		userDefinedNewCols.add("tkt_relatedshop");
		userDefinedNewCols.add("tkt_relatedbranch");
		userDefinedNewCols.add("tkt_relatedagent");
		userDefinedNewCols.add("tkt_createdby");
		userDefinedNewCols.add("tkt_ownerbranch");
		userDefinedNewCols.add("tkt_creatortype");
		
		userDefinedColLabel.put("tkt_subject", "سبب التذكرة");
		userDefinedColLabel.put("tkt_description", "وصف المشكلة");
		userDefinedColLabel.put("tkt_priority", "الأهمية");
		userDefinedColLabel.put("tkt_relatedcustomer", "الزبون ذو الصلة");
		userDefinedColLabel.put("tkt_relatedshop", "المتجر ذو الصلة");
		userDefinedColLabel.put("tkt_relatedbranch", "الفرع ذو الصلة");
		userDefinedColLabel.put("tkt_relatedagent", "مندوب التوصيل ذو الصلة");
		userDefinedColLabel.put("tkt_createdby", "أنشئت بواسطة");
		userDefinedColLabel.put("tkt_ownerbranch", "أنشأ في فرع");
		userDefinedColLabel.put("tkt_creatortype", "الجهة المنشئة");
		userDefinedFormSizeClass = "col-xl-7 col-md-10 col-sm-12 mx-auto";
		
		userDefinedLookups.put("tkt_subject", "select kbcode, kbdesc from kbgeneral where kbcat1='TICKET' and kbcat2 = 'SUBJECTS' ");
		userDefinedLookups.put("tkt_priority", "select kbcode, kbdesc from kbgeneral where kbcat1='TICKET' and kbcat2 = 'PRIORITY' ");
		userDefinedLookups.put("tkt_relatedcustomer", "select mcust_id, mcust_name  from kb_mastercustomer");
		userDefinedLookups.put("tkt_relatedshop", "!select cust_id, cust_name  from kbcustomers where cust_mastercustid={tkt_relatedcustomer}");
		userDefinedLookups.put("tkt_createdby", "select us_id , us_name from kbusers");
		userDefinedLookups.put("tkt_relatedbranch", "select branch_id, branch_name from kbbranches ");
		userDefinedLookups.put("tkt_relatedagent", "!select us_id, us_name from kbusers where us_rank ='DLVAGENT' and us_branchcode={tkt_relatedbranch} ");
		userDefinedLookups.put("tkt_ownerbranch", "select branch_id, branch_name from kbbranches ");
		
		userDefinedColsMustFill.add("tkt_subject");
		userDefinedColsMustFill.add("tkt_priority");
		userDefinedColsMustFill.add("tkt_createdby");
		userDefinedColsMustFill.add("tkt_description");
		
		userDefinedNewColsHtmlType.put("tkt_description", "TEXTAREA");
		userDefinedNewColsHtmlType.put("tkt_relatedcustomer", "DROPLIST");
		userDefinedNewColsHtmlType.put("tkt_relatedshop", "DROPLIST");
		userDefinedNewColsHtmlType.put("tkt_relatedbranch", "DROPLIST");
		userDefinedNewColsHtmlType.put("tkt_relatedagent", "DROPLIST");
		userDefinedNewColsHtmlType.put("tkt_createdby", "DROPLIST");
		userDefinedNewColsHtmlType.put("tkt_ownerbranch", "DROPLIST");
		userDefinedReadOnlyNewCols.add("tkt_ownerbranch");
		userDefinedReadOnlyNewCols.add("tkt_createdby");
		userDefinedReadOnlyNewCols.add("tkt_creatortype");
		
		userDefinedNewColsDefualtValues.put("tkt_createdby", new String [] {"{usid}"});
		userDefinedNewColsDefualtValues.put("tkt_ownerbranch", new String [] {"{userstorecode}"});
		userDefinedNewColsDefualtValues.put("tkt_creatortype", new String [] {"STAFF"});
		userDefinedNewCaption = "إنشاء تذكرة";
	}
	
}
