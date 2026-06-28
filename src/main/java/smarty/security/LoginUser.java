package smarty.security;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import smarty.core.MenuPermissions;
import smarty.core.smartyLogAndErrorHandling;
import smarty.db.mysql;
import com.app.util.Utilities;

public class LoginUser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7837149504400428833L;
	private int masterCustId = 0;
	private int     usid = 0;
	private  String userID = "";
	private  String userName_ar ="";
	private  String userName_en ="";
	
	private  boolean firstTimeLogin = false;
	private  String superRank="N";
	private  String superIT  = "N";
	private  String rank_code ="";
	private  String div_code =""; 
	private  String dep_code ="";
	private  int branchCode = 0;
	private  String branchStateCode ="";
	private  String branchName = "";
	//private  String userProfileID = "";
	private  String errorMsg ="";
	private  String warningMsg ="";
	private	String longitude ="";
	private String latitude = "";
	private  boolean errorFlag = false;
	private  boolean warningFlag = false;
	private  boolean loggedIn = false;
	private  boolean canEdit  = false;
	private  boolean canDelete = false;
	private  boolean canNew    = false;
	private  boolean canApprove = false;
	private  boolean staff	 = false;
	private  boolean haveDashBoard = false;
	private  boolean haveFullServices = true;
	private String imageUrl;
	public  HashMap <String ,String> credentials =null;
	private ArrayList<Integer> shopsList = new ArrayList<Integer>(); 
	private LinkedHashMap<Integer, String> shopsMap = new LinkedHashMap<Integer, String>(); 
	private String shopsCommaSepereated= "";
	private String company;
	private  LinkedHashMap<String, MenuPermissions> menuPermissionsList= new LinkedHashMap<String, MenuPermissions>(); 
	
	public  void doLogout(){
		setLoggedIn(false);	
	}
	
	public void logUserAccess(Connection a_conn, int a_userId, String a_loginFromWhichSystem,  String a_long, String a_lat) throws Exception {
		PreparedStatement pst = null;
		try {
			this.setLatitude(a_lat);
			this.setLongitude(a_long);
			pst = a_conn.prepareStatement("insert into log_user_logins"
					+ " (lul_userid, lul_longitude, lul_latitude, lul_loginfrom) values(?,?,?,?)");
			pst.setInt(1, a_userId);
			pst.setString(2, a_long);
			pst.setString(3, a_lat);
			pst.setString(4, a_loginFromWhichSystem);
			pst.executeUpdate();
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			try{pst.close();}catch(Exception e){}
		}
	}
	
	public String getRankCodeWhenBranchChanges(Connection conn, int usId, int branchCode) throws Exception {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String rankCode = "";
		boolean found = false;
		try {
			pst = conn.prepareStatement("select  ubr_userrank from kbusers_branches_r where ubr_userid=? and ubr_branchid=? ");
			pst.setInt(1, usId);
			pst.setInt(2, branchCode);
			rs = pst.executeQuery();
			if (rs.next()) {
				rankCode = rs.getString("ubr_userrank");
				found = true;
			}
			if (!found) {
				try{rs.close();}catch(Exception e){}
				try{pst.close();}catch(Exception e){}
				pst = conn.prepareStatement("select  us_rank from kbusers where us_id=? and us_branchcode=? ");
				pst.setInt(1, usId);
				pst.setInt(2, branchCode);
				rs = pst.executeQuery();
				if (rs.next()) {
					rankCode = rs.getString("us_rank");
					found = true;
				}
				if (!found )
					throw new Exception("No Such Branch-->"+branchCode);
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			try{rs.close();}catch(Exception e){}
			try{pst.close();}catch(Exception e){}
		}
		return rankCode;
	}
	

	public  ArrayList<String> SplitStringToArrayList(String StrWithSeperator , String seperator){
		ArrayList<String> convertedList = new ArrayList<String>();
		if (StrWithSeperator!=null && StrWithSeperator.trim()!=null && !StrWithSeperator.trim().equals("")){
			//System.out.println("StrWithSeperator===>"+StrWithSeperator);
			String [] myArr = StrWithSeperator.split(seperator.trim());
			for (int i=0 ; i<myArr.length ; i++)
				convertedList.add(myArr[i]);		
		}
		return convertedList;
	}
	
	public void LoadPermissions(Connection conn, String rank, String branch) throws Exception {
		PreparedStatement pst = null, pstSubMenu= null;
		ResultSet rs = null, rsSubMenu = null;
		boolean subHaveMenu = false; 
		try {
			menuPermissionsList = new LinkedHashMap<String, MenuPermissions>();
			MenuPermissions mp = new MenuPermissions();
			MenuPermissions subMenu = new MenuPermissions();
			LinkedHashMap<String, MenuPermissions> subMenuList = new LinkedHashMap<String, MenuPermissions>();
			//String subMenuIds = "";
			pstSubMenu = conn.prepareStatement("select sm_showimenu,  sm_id, sm_submenu_name , sm_submenucode   " +
						 		"from kbmenu_subtabs where sm_menucode =?  and (sm_branches like ? or sm_branches like 'ALL:') order by sm_seq asc");
			
			pst = conn.prepareStatement("select mt_name,mt_code , p_submenuids,mt_iconclass, mt_id " +
			 		"FROM kbmenu_tabs join kbpermission on p_menuid = mt_code and p_rank_code=?"+
			 		" where p_branchid=? order by mt_seq ");
			pst.setString(1,rank);
			pst.setInt(2,getBranchCode());
			//System.out.println(pst);
			rs = pst.executeQuery();
			while (rs.next()){
				mp.setMenuCode(rs.getString("mt_code"));
				mp.setMenuId(rs.getInt("mt_id"));
				mp.setIcon(rs.getString("mt_iconclass"));
				mp.setSubMenuIds(rs.getString("p_submenuids"));
				mp.setMenuName(rs.getString("mt_name"));

				ArrayList<String> subIdsList =SplitStringToArrayList( rs.getString("p_submenuids"), ":");
				subMenuList= new LinkedHashMap<String, MenuPermissions>();
				//System.out.println("rsSubMenu="+pstSubMenu);
				pstSubMenu.setString(1, rs.getString("mt_code"));
				pstSubMenu.setString(2,"%"+branch+":%");
				//System.out.println("rsSubMenu="+pstSubMenu);
				rsSubMenu = pstSubMenu.executeQuery();
				while (rsSubMenu.next()) {
					//System.out.println(rs.getString("mt_code"));
					subHaveMenu=true;
					if (subIdsList.contains(rsSubMenu.getString("sm_id"))) {
						subMenu.setMenuId(rsSubMenu.getInt("sm_id"));
						subMenu.setMenuName(rsSubMenu.getString("sm_submenu_name"));
						subMenu.setMenuCode(rsSubMenu.getString("sm_submenucode"));
						if (rsSubMenu.getString("sm_showimenu").equalsIgnoreCase("N"))
							subMenu.setShowInMenu(false);
						subMenuList.put(rsSubMenu.getString("sm_submenucode"), subMenu);
					}
					mp.setSubMenuList(subMenuList);
					subMenu = new MenuPermissions();
				}
				try {rsSubMenu.close();}catch(Exception e) {}
				pstSubMenu.clearParameters();
				if(subHaveMenu) {
					menuPermissionsList.put(rs.getString("mt_code"),mp);
				}
				mp = new MenuPermissions();
				subHaveMenu=false;
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			try{rsSubMenu.close();}catch(Exception e){}
			try{rs.close();}catch(Exception e){}
			try{pstSubMenu.close();}catch(Exception e){}
			try{pst.close();}catch(Exception e){}
			
		}
	}
	
	
	/**
	 * Nafie
	 * @param String
	 * @return boolean logged in or not
	 * 
	 */
	public boolean doLoginCustomer (String pass) {
		setLoggedIn(false);
		Connection conn = null;
		PreparedStatement pst = null, pstPagesList=null, pstPagesForMaster=null;
		ResultSet rs = null, rsPages = null;
		String MD5pass ="";
		Utilities ut = new Utilities();
		try{
			conn = mysql.getConn();
			setStaff(false);
			pst = conn.prepareStatement("select md5(?) from dual");
			pst.setString(1, pass);
			rs = pst.executeQuery();
			if (rs.next()){
				MD5pass = rs.getString(1);
			}
			try{rs.close();}catch(Exception e){}
			try{pst.close();}catch(Exception e){}
			
			pstPagesForMaster = conn.prepareStatement("select cust_id, cust_name from kbcustomers "
					+ " where cust_mastercustid=? and cust_allowlogin='Y' and cust_active='Y' limit 1000 ");
			
			
			pst = conn.prepareStatement("select mcust_branchcode, mcust_name, us_id, us_name, us_rank, us_active, us_mastercustid, us_workingoncustomers "
					+ " from kbusers join kb_mastercustomer on us_mastercustid = mcust_id  where us_loginid=? and us_password=?");
			String pagesColonSeperated ="", pagesCommaSeperated = "";
			if (!loggedIn) {
				pst.setString(1,getUserID());
				pst.setString(2, MD5pass);
				rs = pst.executeQuery();
				if (rs.next()){
					setLoggedIn(true);
					
					if (rs.getString("us_active").trim().equalsIgnoreCase("Y")){
						this.setRank_code(rs.getString("us_rank"));
						this.setUsid(rs.getInt("us_id"));
						this.setBranchCode(rs.getInt("mcust_branchcode"));
						userName_ar = rs.getString("us_name");
						userName_en = rs.getString("us_name");
						pagesColonSeperated	= rs.getString("us_workingoncustomers");
						superRank   = "N";
						setSuperIT("N"); 
						
						setMasterCustId(rs.getInt("us_mastercustid"));
						if (rs.getString("us_rank").equalsIgnoreCase("MASTERCUSTOMER")) {
							userName_ar = rs.getString("mcust_name");
							userName_en = rs.getString("mcust_name");
							pstPagesForMaster.setInt(1, rs.getInt("us_mastercustid"));
							rsPages = pstPagesForMaster.executeQuery();
							while (rsPages.next()) {
								shopsList.add(rsPages.getInt("cust_id"));
								shopsMap.put(rsPages.getInt("cust_id"), rsPages.getString("cust_name"));
							}
						}else {
							pagesCommaSeperated =  ut.getSingleQuoteCommaSeperated(ut.SplitStringToArrayList(pagesColonSeperated, ":")).toString();
							pstPagesList = conn.prepareStatement("select cust_id, cust_name from kbcustomers "
									+ " where cust_mastercustid=? and cust_id in ("+pagesCommaSeperated+")"
									+ " and cust_allowlogin='Y' and cust_active='Y' limit 100 ");
							pstPagesList.setInt(1, rs.getInt("us_mastercustid"));
							rsPages = pstPagesList.executeQuery();
							while (rsPages.next()) {
								shopsList.add(rsPages.getInt("cust_id"));
								shopsMap.put(rsPages.getInt("cust_id"), rsPages.getString("cust_name"));
							}
						}
						shopsCommaSepereated = ut.getCommaSeperated(shopsList).toString();
						try{rsPages.close();}catch(Exception e){}
						LoadPermissions(conn, this.getRank_code(), this.getBranchCode()+"");
					}else{
						
						setErrorMsg("This User is not Active, Call Admin to activate");
						setErrorFlag(true);
						setLoggedIn(false);
						return loggedIn;
					}
					return loggedIn;
				}
				
			}
			
			
			setErrorMsg("Wrong ID or Password");
			setErrorFlag(true);
			setLoggedIn(false);
			return loggedIn;
		}catch(Exception e){
			/*Login the error please*/
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName(), 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			setErrorMsg("System Error , Please Tell the Admin");
			setErrorFlag(true);
			setLoggedIn(false);
			e.printStackTrace();	
		}finally{
			try{rs.close();}catch(Exception e){}
			try {rsPages.close();}catch(Exception e) {}
			try{pst.close();}catch(Exception e){}
			try {pstPagesList.close();}catch(Exception e) {}
			try{conn.close();}catch(Exception e){}
		}
		return loggedIn;
	}
	

	public  boolean doLogin(String pass){
		setLoggedIn(false);
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String MD5pass ="";
		try{
			conn = mysql.getConn();
			pst = conn.prepareStatement("select md5(?) from dual");
			pst.setString(1, pass);
			rs = pst.executeQuery();
			if (rs.next()){
				MD5pass = rs.getString(1);
			}
			try{rs.close();}catch(Exception e){}
			try{pst.close();}catch(Exception e){}
			pst = conn.prepareStatement("select * from kbusers where us_loginid=? and us_password=? and us_active='Y' and us_mastercustid=0");
			pst.setString(1,getUserID().toLowerCase());
			pst.setString(2, MD5pass);
			rs = pst.executeQuery();
			if (rs.next()){
				setLoggedIn(true);
				if (rs.getString("us_active").trim().equalsIgnoreCase("Y")){
					setStaff(true);
					if (LoadUserCredentials(conn,rs.getInt("us_id"))){// have error
						setLoggedIn(false);
						return loggedIn;
					}
				}else{
					setStaff(false);
					setErrorMsg("This User is not Active, Call Admin to activate");
					setErrorFlag(true);
					setLoggedIn(false);
					return loggedIn;
				}
				return loggedIn;
			}else { // may be customer
				if (doLoginCustomer(pass)) {
					return true;
				}
			}
			try{rs.close();}catch(Exception e){}
			try{pst.close();}catch(Exception e){}
			//if not staff
			
			
			setErrorMsg("Wrong ID or Password");
			setErrorFlag(true);
			setLoggedIn(false);
			return loggedIn;
		}catch(Exception e){
			/*Login the error please*/
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName(), 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			setErrorMsg("System Error , Please Tell the Admin");
			setErrorFlag(true);
			setLoggedIn(false);
			e.printStackTrace();	
		}finally{
			try{rs.close();}catch(Exception e){}
			try{pst.close();}catch(Exception e){}
			try{conn.close();}catch(Exception e){}
		}
		return loggedIn;
	}
	
	
	/*
	 * 
	 * TO load the staff credentials
	 */
	public  boolean LoadUserCredentials(Connection conn, int usId){
		//System.out.println("---------->loading credentials");
		PreparedStatement pst = null;
		ResultSet rs = null;
		setErrorFlag(false);
		try{
			pst = conn.prepareStatement(
					"select branch_have_full_services, us_img,  us_id, us_name ,rank_super ,rank_super_it, us_rank, us_branchcode, branch_name,"
					+ " branch_state, us_company "
					+ " from kbusers "
					+ " join kbrank on rank_code = us_rank"
					+ " join kbbranches on us_branchcode = branch_id "
					+ " where us_id=? ");
			pst.setInt(1, usId);
			
			rs = pst.executeQuery();
			while (rs.next()){
				userName_ar = rs.getString("us_name");
				userName_en = rs.getString("us_name");
				superRank   = rs.getString("rank_super");
				setRank_code(rs.getString("us_rank"));
				setSuperIT(rs.getString("rank_super_it")); 
				setBranchCode(rs.getInt("us_branchcode"));
				setBranchName(rs.getString("branch_name"));
				setBranchStateCode(rs.getString("branch_state"));
				setCompany(rs.getString("us_company"));
				setUsid(rs.getInt("us_id"));
				setImageUrl("/primeimg/staff/"+rs.getString("us_img"));
				if (rs.getString("branch_have_full_services").equalsIgnoreCase("N")) {
					setHaveFullServices(false);
				}
				
			}
			LoadPermissions(conn, this.getRank_code(), this.getBranchCode()+"");
			
			if (superRank.equals("Y")){
				setHaveDashBoard(true);
				setErrorFlag(false);
				return isErrorFlag();// no need to load any Credentials.
			}else{
				if ((rank_code ==null) || (rank_code.trim().equals(""))){
					errorMsg = "rank_code is missing for this User";
					setErrorFlag(true);
					return isErrorFlag();
				}
			}
		}catch(Exception e){
			/*Login the error please*/
			smartyLogAndErrorHandling.logErrorInDB(conn,"ERR", Thread.currentThread().getStackTrace()[1].getClassName(), 
					Thread.currentThread().getStackTrace()[1].getMethodName(), null, e);
			e.printStackTrace();
			
		}finally{
			try{rs.close();}catch(Exception e){}
			try{pst.close();}catch(Exception e){}
		}
		return isErrorFlag();
	}

	
	public HashMap<Integer,String> getUserBranches(Connection conn)throws Exception{
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<Integer,String> branchedAllowed = new HashMap<Integer,String>();
		try {
			
			pst = conn.prepareStatement(""
					+ " select branch_id, branch_name from " + 
					"	 kbusers join kbbranches on branch_id = us_branchcode where us_id=? "
					+ " union "
					+ " select branch_id, branch_name from "
					+ " kbusers_branches_r join kbbranches on branch_id = ubr_branchid where ubr_userid=? ");
			pst.setInt(1, this.usid);
			pst.setInt(2, this.usid);
			rs = pst.executeQuery();
			while (rs.next()) {
				branchedAllowed.put(rs.getInt("branch_id"), rs.getString("branch_name"));
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}finally {
			try {rs.close();}catch(Exception e) {}
			try {pst.close();}catch(Exception e) {}
		}
		return branchedAllowed;
	}
	
	public  String getUserID() {
		return userID;
	}

	public  void setUserID(String userID) {
		this.userID = userID;
	}

	

	public  boolean isLoggedIn() {
		return loggedIn;
	}

	public  void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	

	public  String getDiv_code() {
		return div_code;
	}

	public  void setDiv_code(String div_code) {
		this.div_code = div_code;
	}

	public  String getDep_code() {
		return dep_code;
	}

	public  void setDep_code(String dep_code) {
		this.dep_code = dep_code;
	}

	/*
	public  String getUserProfileID() {
		return userProfileID;
	}

	public  void setUserProfileID(String userProfileID) {
		Users.userProfileID = userProfileID;
	}
*/
	public  String getRank_code() {
		return rank_code;
	}

	public  void setRank_code(String rank_code) {
		this.rank_code = rank_code;
	}

	public  String getErrorMsg() {
		return errorMsg;
	}

	public  void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public  String getWarningMsg() {
		return warningMsg;
	}

	public  void setWarningMsg(String warningMsg) {
		this.warningMsg = warningMsg;
	}

	public  String getSuperRank() {
		return superRank;
	}

	public  void setSuperRank(String superRank) {
		this.superRank = superRank;
	}

	public  boolean isErrorFlag() {
		return errorFlag;
	}

	public  void setErrorFlag(boolean errorFlag) {
		this.errorFlag = errorFlag;
	}

	public  boolean isWarningFlag() {
		return warningFlag;
	}

	public  void setWarningFlag(boolean warningFlag) {
		this.warningFlag = warningFlag;
	}

	public  boolean isCanEdit() {
		return canEdit;
	}

	public  void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	public  boolean isCanDelete() {
		return canDelete;
	}

	public  void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

	public  boolean isCanNew() {
		return canNew;
	}

	public  void setCanNew(boolean canNew) {
		this.canNew = canNew;
	}
	
	public  String getGreetings(String lang){
		if (lang.equalsIgnoreCase("AR")){
			return userName_ar;
		}else{
			return userName_en;
		}
		
	}

	public  String getSuperIT() {
		return superIT;
	}

	public  void setSuperIT(String superIT) {
		this.superIT = superIT;
	}

	public  boolean isStaff() {
		return staff;
	}

	public  void setStaff(boolean staff) {
		this.staff = staff;
	}

	public  boolean isCanApprove() {
		return canApprove;
	}

	public  void setCanApprove(boolean canApprove) {
		this.canApprove = canApprove;
	}

	public boolean isHaveDashBoard() {
		return haveDashBoard;
	}

	public void setHaveDashBoard(boolean haveDashBoard) {
		this.haveDashBoard = haveDashBoard;
	}
	public boolean isFirstTimeLogin() {
		return firstTimeLogin;
	}

	public void setFirstTimeLogin(boolean firstTimeLogin) {
		this.firstTimeLogin = firstTimeLogin;
	}
	/**
	 * @return the usid
	 */
	public int getUsid() {
		return usid;
	}

	/**
	 * @param usid the usid to set
	 */
	public void setUsid(int usid) {
		this.usid = usid;
	}
	public int getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(int branchCode) {
		this.branchCode = branchCode;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public int getMasterCustId() {
		return masterCustId;
	}
	public void setMasterCustId(int masterCustId) {
		this.masterCustId = masterCustId;
	}

	

	public ArrayList<Integer> getShopsList() {
		return shopsList;
	}



	public void setShopsList(ArrayList<Integer> shopsList) {
		this.shopsList = shopsList;
	}



	public LinkedHashMap<Integer, String> getShopsMap() {
		return shopsMap;
	}



	public void setShopsMap(LinkedHashMap<Integer, String> shopsMap) {
		this.shopsMap = shopsMap;
	}



	public String getShopsCommaSepereated() {
		return shopsCommaSepereated;
	}



	public void setShopsCommaSepereated(String shopsCommaSepereated) {
		this.shopsCommaSepereated = shopsCommaSepereated;
	}



	public String getUserName_ar() {
		return userName_ar;
	}



	public void setUserName_ar(String userName_ar) {
		this.userName_ar = userName_ar;
	}



	public String getUserName_en() {
		return userName_en;
	}



	public void setUserName_en(String userName_en) {
		this.userName_en = userName_en;
	}

	public HashMap<String, String> getCredentials() {
		return credentials;
	}
	public void setCredentials(HashMap<String, String> credentials) {
		this.credentials = credentials;
	}
	public LinkedHashMap<String, MenuPermissions> getMenuPermissionsList() {
		return menuPermissionsList;
	}

	public void setMenuPermissionsList(LinkedHashMap<String, MenuPermissions> menuPermissionsList) {
		this.menuPermissionsList = menuPermissionsList;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getBranchStateCode() {
		return branchStateCode;
	}

	public void setBranchStateCode(String branchStateCode) {
		this.branchStateCode = branchStateCode;
	}

	public boolean isHaveFullServices() {
		return haveFullServices;
	}

	public void setHaveFullServices(boolean haveFullServices) {
		this.haveFullServices = haveFullServices;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
}


