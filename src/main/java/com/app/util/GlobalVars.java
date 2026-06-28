package com.app.util;

import java.util.HashMap;

public class GlobalVars {
	public static final String  PATH_LOGO_FOR_PRINTING = "/smartyresources/app-assets/images/logo";
	public static final String DEFAULT_PATH_LOGO_FOR_PRINTING = PATH_LOGO_FOR_PRINTING+"/logo-sm.png";
	
//	public static final String mbagentAppId = "f1396ae8-525a-4094-bea4-1b24392c0047";
//	public static final String mbagentRestAuthorization = "NTUwMDY0OGUtZmFhYS00NWVmLWFmZWItNDgwYjI0ODA0YWRm";
//	public static final String mbCustAppId = "b7a146cc-cafc-48ce-9ad9-c40ea4ca6099";
//	public static final String mbCustRestAuthorization = "MWI1ZWExOWUtZDQwYy00ZGE5LTllYjAtNjQ0MDRlZmQ3MTY0";
//	
	
	public static final HashMap<Integer,String> BRANCH_CUSTOMER_APPID_KEY = 
			new HashMap<Integer,String>(){
				private static final long serialVersionUID = -4947865631575446535L;
				{
					put(4, "b7a146cc-cafc-48ce-9ad9-c40ea4ca6099");
					put(67, "b7a146cc-cafc-48ce-9ad9-c40ea4ca6099");
					put(68, "b7a146cc-cafc-48ce-9ad9-c40ea4ca6099");
					put(5, "4323592a-7514-4a56-aa1e-14ecb5ba56fe");
					put(13, "4323592a-7514-4a56-aa1e-14ecb5ba56fe" );
					put(11, "4323592a-7514-4a56-aa1e-14ecb5ba56fe"); 
					put(12, "4323592a-7514-4a56-aa1e-14ecb5ba56fe");

					put(21, "fa009cf6-0001-499b-8164-40a238f23e7c"); 
					put(31, "fa009cf6-0001-499b-8164-40a238f23e7c");
	
					put(6, "bc0b4ca7-1446-4168-92b3-9997ae6ba2a2");
					
					//shokur
					put(54, "4330e71a-a159-4e5c-81e2-9144323257a9");
					//fahood
					put(52, "24f990c9-ccd5-4e44-8385-7278dbd94dee");

					
				}
			};
			
	public static final HashMap<Integer,String> BRANCH_CUSTOMER_WEBAPI_KEY = 
			new HashMap<Integer,String>(){
				private static final long serialVersionUID = -4947865631575446535L;
				{	
					put(4, "MjAyNjFhMjUtOTljYy00YjcxLTg1ZTYtYjBmNzU5OTM2ODFm");
					put(67, "MjAyNjFhMjUtOTljYy00YjcxLTg1ZTYtYjBmNzU5OTM2ODFm");
					put(68, "MjAyNjFhMjUtOTljYy00YjcxLTg1ZTYtYjBmNzU5OTM2ODFm");
					put(5, "MjhjMjc3OGEtYjQ2Ni00ZDA1LWFjZWUtM2MyOGIxNmNjNGJi");
					put(13, "MjhjMjc3OGEtYjQ2Ni00ZDA1LWFjZWUtM2MyOGIxNmNjNGJi");
					put(11, "MjhjMjc3OGEtYjQ2Ni00ZDA1LWFjZWUtM2MyOGIxNmNjNGJi");
					put(12, "MjhjMjc3OGEtYjQ2Ni00ZDA1LWFjZWUtM2MyOGIxNmNjNGJi");
					
					put(21, "MDM4NDViZjgtNjBiMy00NzhkLWFlZDEtZjQ2Yjg5ZTQ1ZGU3");
					put(31, "MDM4NDViZjgtNjBiMy00NzhkLWFlZDEtZjQ2Yjg5ZTQ1ZGU3");

					put(6, "YmU4ODRmNzQtNzI4NS00MDRiLWJjMjktMDA4MjUzYmNiZmU3");

					put(54, "YzJkZmE2MzMtNzI1Ni00MzkxLWE2ZTItMjBhYzU5NWFhMTI5");
					
					put(52, "MThhMjAwODctMDk1Zi00MTIyLTkzZWUtNDkwZGM1OTk4M2I1");

				}
			};
			
	public static final HashMap<Integer,String> BRANCH_AGENT_APPID_KEY = 
			new HashMap<Integer,String>(){
				private static final long serialVersionUID = -4947865631575446535L;
				{
					put(4, "37cb2fe3-f4b3-474a-b322-642dd4b57d8d");//not working
				}
			};
			
	public static final HashMap<Integer,String> BRANCH_AGENT_WEBAPI_KEY = 
			new HashMap<Integer,String>(){
				private static final long serialVersionUID = -4947865631575446535L;
				{
					put(4, "ODY2MjhhMzAtZDRhNC00MWI1LTgxZWEtNDQ2ZTcxMmI5Y2E3");//Not working
				}
			};
	
}
