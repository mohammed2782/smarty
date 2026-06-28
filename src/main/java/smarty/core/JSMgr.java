package smarty.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
 
public class JSMgr {
	public  String APPPATH;
	public static final List<String> dateList = Arrays.asList("DATETIME", "DATE");
	public static final List<String> TextList = Arrays.asList("VARCHAR");
	public static final List<String> numberList = Arrays.asList("BIGINT", "DOUBLE" , "INT", "FLOAT", "NUMBER_WITH_COMMAS");
	
	// here the key is the column that have the lookup and the arraay list are all the columns when changed impact the key col.
	public HashMap <String , LinkedHashSet<String>> colsDependsOn;
	// here we list the column that when changed impage which columns in th array list
	public HashMap <String , LinkedHashSet<String>> colsUsedInLookupsForOtherCols;
	//public HashMap <String , String>  paramScriptOnchange;
	
	public HashMap <String, String >userDefinedColsHtmlType;
	public JSMgr(){
		//APPPATH = path;
		colsDependsOn = new HashMap<String ,  LinkedHashSet<String>>();
		colsUsedInLookupsForOtherCols =  new HashMap<String ,  LinkedHashSet<String>>();
		//paramScriptOnchange		   = new  HashMap<String , String>();
		userDefinedColsHtmlType = new HashMap<String,String>();
	}
	
	public String getAPPPATH() {
		return APPPATH;
	}

	public void setAPPPATH(String aPPPATH) {
		APPPATH = aPPPATH;
	}

	public String showHideScript(String ID){
		String showHideItem="";
		showHideItem ="function SmartyShow_"+ID+"(id) {"+
					"document.getElementById(id).style.display = 'block';"+
		    		"}";
		return showHideItem;
	}
	/*
	 * generate must fill validation
	 */
	public String genValudationJS (String formName , ArrayList <String> userDefinedColsMustFill){
		String js ="<script>  \n";
		// Must Fill Section
		formName = formName.replace(".", "__");
		js = js + " function validateMustFill(){ \n var x =false;";
		for (String key :userDefinedColsMustFill){
			js = js + "x= validate_mustfill_"+formName+"_"+key+"(); if (!x) return false;\n";
		}
		js = js+" return true; \n } \n";
		
		for (String key :userDefinedColsMustFill){
			js= js + "  function validate_mustfill_"+formName+"_"+key+" (){ \n"+
							" var x=document.getElementById('"+key+"');\n"+//document.getElementById
							" if (x.value==null || x.value==''){\n"+
							" 		x.style.backgroundColor='#FDD7E4';\n"+
							"return false;\n"+
							" }\n"+
							" return true;\n" +
						" }\n";
		}
		js = js+"</script>";
		return js;
	}
	
	/*
	 *  Generate Must fill validation function for every column
	 */
	public String genJSMustFill(String fieldID , String labelName){
		return "frmvalidator.addValidation('"+fieldID+"','req','Please Insert "+labelName+"');\n";	
	}
	/*
	 *  Generate numeric validation function for every column
	 */
	public String genJSNumericValidation(String fieldID , String labelName){
		return "frmvalidator.addValidation('"+fieldID+"','numeric' ,'اonly numbers are allowed ' );\n";	
	}
	
	public String getClickableRowCode(String key , String value){
		return "onclick=\"if(link) javascript:window.location.assign('?"+key+"="+value+"'); else link=true;\""; 
	}
	
	public String getSlidingGroupJS(){
		/*return "<script>"
				+ "function showHideGroups(groupDisplayVal){"
				+ " $(\"[grouptitle*=\"+groupDisplayVal+\"]\").toggle();"
				+ "}</script>";
				*/
		return "<script>$(\".special_sliding_group\").click(function (event) { "
				+ " event.preventDefault(); "
				+ " var controlgroup = $(this).attr('controlgroup'); "
				+ " $(\"[grouptitle-\"+controlgroup+\"=\"+controlgroup+\"]\").toggle(300,'linear');"
				+ " });</script>";
				}
	
	/*
	 * Generate ajax based functionality.
	 */
	public String genHotLookup(String lookup , String keyCol , 
			Map <String , String > userDefinedNewColsHtmlType , Map <String , String>userDefinedLookups,
			boolean required){
		String jsHotLookup="";
		boolean MoreCurlyBraces = false;
		int startIndex , endIndex;
		String ParamName;
		String js_values="" , vars_toreplace="";
		String original_lookup = lookup.substring(1,lookup.length() );//remove the first char
		
		if ((lookup.contains("{")) &&  (lookup.contains("}"))){
			MoreCurlyBraces = true;
		}
		ParamName = "";
		//i used LinkedHashSet to prevent duplication , may be the same query might have 
		//reoccurence of the same col multitimes
		LinkedHashSet<String> dependsOnColumns  = new LinkedHashSet<String>();
		while (MoreCurlyBraces){
			if (lookup.contains("{")){
				startIndex = lookup.indexOf("{");
				endIndex = lookup.indexOf("}");
				ParamName =lookup.substring(startIndex+1, endIndex).trim();
				dependsOnColumns.add(ParamName);
				lookup = lookup.substring(0, startIndex)+lookup.substring(endIndex+1, lookup.length());
				vars_toreplace += ParamName+":"+"Elem_"+ParamName+"_val, ";
				//System.out.println(ParamName+", this.userDefinedColsHtmlType.get(ParamName)==>"+this.userDefinedColsHtmlType.get(ParamName));
				//$("#span_es_c_cust_name > ul > li.selected").attr("value")
				if (this.userDefinedColsHtmlType.get(ParamName)!=null && this.userDefinedColsHtmlType.get(ParamName).equalsIgnoreCase("EDITABLE_SELECT"))
					js_values = js_values + "	var Elem_"+ParamName+"_val = $('#span_es_"+ParamName+" > ul > li.selected').attr('value'); \n";
				else
					js_values = js_values + "	var Elem_"+ParamName+"_val = $('#"+ParamName+"').val(); \n";
			}else{
				MoreCurlyBraces =false;
			}
		} 
		vars_toreplace = vars_toreplace.substring(0 , vars_toreplace.length()-2);//remove the last plus sign and "
		colsDependsOn.put(keyCol , dependsOnColumns);
		Iterator<String> iterator = dependsOnColumns.iterator();
		String col="";
		while (iterator.hasNext()) {
			col = iterator.next();
			if (colsUsedInLookupsForOtherCols.containsKey(col)){
				colsUsedInLookupsForOtherCols.get(col).add(keyCol);
			}else{
				LinkedHashSet<String> ss = new LinkedHashSet<String>();
				ss.add(keyCol);
				colsUsedInLookupsForOtherCols.put(col,ss);
			}
		}
	
		String req= "N";
		if (required)
			req = "Y";
		
		String dataToSend = "{sqllookup:\""+original_lookup+"\", name :\""+keyCol+"\", id :\""+keyCol+"\", mustfill:\""+req+"\", "
				+ "HTMLtype:\""+userDefinedNewColsHtmlType.get(keyCol)+"\" , "+vars_toreplace+" }";
		jsHotLookup = jsHotLookup+
				" function hotlookup_values_"+keyCol+"(){\n" +
				" 	var self = this; \n"+
				js_values;
		if (userDefinedNewColsHtmlType.get(keyCol).equalsIgnoreCase("CHECKBOX")) {
			jsHotLookup +="	var targetHTMLElement_"+keyCol+"_div = document.getElementById('"+keyCol+"_div'); \n";
		}else {
			jsHotLookup +="	var targetHTMLElement_"+keyCol+" = document.getElementById('"+keyCol+"'); \n";
		}
				
		jsHotLookup +="	$.ajax\n" 
				+"	({\n"
				+"   headers: {'cache-control': 'no-cache' },"
				+"	 type:'POST',\n"
				+"   cache: false,"
				+"	 url:'../../myajax.jsp',\n"  
				+"	 data: "+dataToSend+",\n"
				+"    error:function(){ alert(\"some error occurred\") },"
				+ "  success: function(data, status){\n" + 
							"       //alert(\"Data: \" + data + \"\\nStatus: \" + status);\n"
							+ " if (status=='success'){\n"; 
									if (userDefinedNewColsHtmlType.get(keyCol).equals("TEXT")  || userDefinedNewColsHtmlType.get(keyCol).equals("TEXTAREA") ||
											numberList.contains(userDefinedNewColsHtmlType.get(keyCol))){
										if (userDefinedNewColsHtmlType.get(keyCol).equals("NUMBER_WITH_COMMAS")) {
											jsHotLookup = jsHotLookup + " AutoNumeric.getAutoNumericElement('#"+keyCol+"').set(data.trim()) \n";
										}else {
											jsHotLookup = jsHotLookup + " targetHTMLElement_"+keyCol+".value=data.trim(); \n";
										}
									}else if (userDefinedNewColsHtmlType.get(keyCol).equalsIgnoreCase("CHECKBOX")) {
										jsHotLookup = jsHotLookup+"targetHTMLElement_"+keyCol+"_div.innerHTML=data; \n";
									}else{
										jsHotLookup = jsHotLookup+"targetHTMLElement_"+keyCol+".innerHTML=data; \n";
									}
									if (userDefinedNewColsHtmlType.get(keyCol).equalsIgnoreCase("DROPLIST")) {
										jsHotLookup = jsHotLookup+("$('#"+keyCol+"').trigger('change');  \n");
									}else {
										jsHotLookup = jsHotLookup+("$('#"+keyCol+"').change();  \n");
									}
									jsHotLookup += "}\n"
								+ "}\n"
					+ "});";
				jsHotLookup +="$('#"+keyCol+"').change(); \n";
				jsHotLookup = jsHotLookup+" } \n";
		//System.out.println("this is clemance jsmgr----------->"+jsHotLookup);
		return jsHotLookup;
		
		
	}
	
	
	public StringBuilder getHotLookupForMultiGrid (ArrayList<String>userDefinedEditCols ,
													HashMap<String, String> userModifiedTD , 
													HashMap<String,String> userDefinedLookups , 
													HashMap<String,String> userDefinedEditLookups,
													Map<String,String> userDefinedEditColsHtmlType,
													ArrayList<String> userDefinedColsMustFill) {
		StringBuilder jsHotLookup= new StringBuilder("<script>");
		boolean MoreCurlyBraces = false;
		int startIndex , endIndex;
		String ParamName;
		LinkedHashSet<String> dependsOnColumns  = new LinkedHashSet<String>();
		LinkedHashSet<String> impactingCols  = new LinkedHashSet<String>();
		HashMap<String ,LinkedHashSet<String> > colsImpactedByDependentCols= new HashMap<String , LinkedHashSet<String>>();
		String js_values="" , vars_toreplace="";
		for (int i = 0 ; i < userDefinedEditCols.size() ; i++) {
			MoreCurlyBraces = false;
			if (userModifiedTD.containsKey(userDefinedEditCols.get(i))) continue;// if the user handles the td then skip
			if (!userDefinedLookups.containsKey(userDefinedEditCols.get(i))) continue; // if there is no lookup then skip
			if (!userDefinedLookups.get(userDefinedEditCols.get(i)).startsWith("!")) continue; // if it's not hotlookup then skip
				
			// now we start the process
			dependsOnColumns  = new LinkedHashSet<String>();
			String lookup = userDefinedLookups.get(userDefinedEditCols.get(i));
			String original_lookup = lookup.substring(1,lookup.length() );//remove the first char
			if ((lookup.contains("{")) &&  (lookup.contains("}"))){
				MoreCurlyBraces = true;
			}
			if (!MoreCurlyBraces) continue; // if there is no dependency columns then skip
			
			ParamName = "";
			
			
			while (MoreCurlyBraces){
				if (lookup.contains("{")){
					startIndex = lookup.indexOf("{");
					endIndex = lookup.indexOf("}");
					ParamName =lookup.substring(startIndex+1, endIndex).trim();
					dependsOnColumns.add(ParamName);
					if (!impactingCols.contains(ParamName))
						impactingCols.add(ParamName);
					lookup = lookup.substring(0, startIndex)+lookup.substring(endIndex+1, lookup.length());
				}else{
					MoreCurlyBraces =false;
				}
			}
			
			colsImpactedByDependentCols.put(userDefinedEditCols.get(i), dependsOnColumns);
			
		}
		
		
		for (String key: impactingCols) {
			jsHotLookup.append("\n function change_"+key+" (el, rowNum){\n");
			for (String dependentKey : colsImpactedByDependentCols.keySet()) {
				if (colsImpactedByDependentCols.get(dependentKey).contains(key))
					jsHotLookup.append("multigrid__"+dependentKey+"call_to_hotlookup(rowNum);\n");
			}
			jsHotLookup.append("  }\n");
		}
		
		
		String req= "N";
		vars_toreplace = "";
		for (String dependentKey : colsImpactedByDependentCols.keySet()) {
			jsHotLookup.append("\n function multigrid__"+dependentKey+"call_to_hotlookup(rowNum){\n");
			for (String baseCol : colsImpactedByDependentCols.get(dependentKey)) {
				jsHotLookup.append(" //alert('inside the mlti '+rowNum);\n"+
							  " var Elem_"+baseCol+"_val = $('#"+baseCol+"_smartyrow_'+rowNum).val();\n "
							  		+ " "
							  		+ "//alert(Elem_"+baseCol+"_val);\n");
				vars_toreplace += baseCol+":"+"Elem_"+baseCol+"_val, ";
			}
			vars_toreplace = vars_toreplace.substring(0 , vars_toreplace.length()-2);//remove the last plus sign and "
			req ="N";
			if (userDefinedColsMustFill.contains(dependentKey))
				req = "Y";
			String htmlType ="DROPLIST";
			if (!dateList.contains(userDefinedEditColsHtmlType.get(dependentKey))
					&& !numberList.contains(userDefinedEditColsHtmlType.get(dependentKey))
						&& !TextList.contains(userDefinedEditColsHtmlType.get(dependentKey))
					) {
				htmlType =userDefinedEditColsHtmlType.get(dependentKey);
			}
			String original_lookup = userDefinedLookups.get(dependentKey).substring(1, userDefinedLookups.get(dependentKey).length() );//remove the first char

			original_lookup = original_lookup.replaceAll("'", "\\\\'");
			
			String dataToSend = "{sqllookup:\""+original_lookup+"\", name :\""+dependentKey+"_smartyrow_\"+rowNum, id :\""+dependentKey+"_smartyrow_\"+rowNum, mustfill:\""+req+"\", "
					+ "HTMLtype:\""+htmlType+"\" , "+vars_toreplace+" }";
			jsHotLookup.append(
					"	var targetHTMLElement_"+dependentKey+" = document.getElementById('"+dependentKey+"_smartyrow_'+rowNum); \n"+
					"	 $.ajax\n"
					   + "({\n"
					   + " type:'GET',\n"
					   + " url:'../../myajax.jsp' ,\n"
					   + " data: '"+dataToSend+"',\n"
					   + " success : function(data, status){\n" + 
									"  //alert(\"Data: \" + data + \"\\nStatus: \" + status);\n"
									+ " if (status=='success'){\n "); 
										if (userDefinedEditColsHtmlType.get(dependentKey).equals("TEXT") || 
												userDefinedEditColsHtmlType.get(dependentKey).equals("TEXTAREA") ||
												numberList.contains(userDefinedEditColsHtmlType.get(dependentKey))
												){
											jsHotLookup.append(" targetHTMLElement_"+dependentKey+".value=data.trim(); \n");
										}else{
											jsHotLookup.append("targetHTMLElement_"+dependentKey+".innerHTML=data; \n");
										}
										jsHotLookup.append( "}\n"
									+ "}"
									+ "}"
									+ ");");
				
			//System.out.println(jsHotLookup);
			jsHotLookup.append( "}\n");
		}
		// finally to prevent javascript errors we create dummy functions for the columns that are editable and does not have impact on any other column
		for (int i = 0 ; i <userDefinedEditCols.size() ; i++) {
			if (!impactingCols.contains(userDefinedEditCols.get(i))){
				jsHotLookup.append("function change_"+userDefinedEditCols.get(i)+" (el, rowNum){} ");
			}
		}
		jsHotLookup.append("</script>");
		return jsHotLookup;
		
	}
	
	public StringBuilder getHotLookupCallingScript(String colUsedInLookup){
		
		LinkedHashSet<String> impactedCols = colsUsedInLookupsForOtherCols.get(colUsedInLookup);
		if (impactedCols == null)
			return new StringBuilder("");
		StringBuilder hottieFunc = new StringBuilder("");//\n var Elem_"+colUsedInLookup+" = document.getElementById('"+colUsedInLookup+"'); \n ");
		if (this.userDefinedColsHtmlType.get(colUsedInLookup)!=null && this.userDefinedColsHtmlType.get(colUsedInLookup).equalsIgnoreCase("EDITABLE_SELECT"))
			// blur is important for safari to work
			hottieFunc.append(" $('#editable_"+colUsedInLookup+"').on('blur change', function (){ "); //this id must be initialized with editable_
		
		else if (this.userDefinedColsHtmlType.get(colUsedInLookup)!=null && this.userDefinedColsHtmlType.get(colUsedInLookup).equalsIgnoreCase("DATE"))
			// blur is important for safari to work
			hottieFunc.append("$('#"+colUsedInLookup+"').datetimepicker().on('dp.change',function(e){" ); //this id must be initialized with editable_
		
		else	
			hottieFunc.append(" $('#"+colUsedInLookup+"').on('change', function (){");	
		Iterator<String> iterator = impactedCols.iterator();
		String col="";
		while (iterator.hasNext()) {
			col = iterator.next();
			hottieFunc.append(" hotlookup_values_"+col+"(); \n");
				//hottieFunc += " while(!isItReady){alert(isItReady);}isItReady = false;";
		}
		hottieFunc.append("});\n");
		return hottieFunc;
	}
	
	
}
