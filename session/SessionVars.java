package com.app.session;

public class SessionVars {
	private  String lang;
	private  String bodyDir; // direction of the page body : rtl or ltr
	private  String division;
	private  String dep;
	private  String projectPath;
	private  String currentPageURL;
	private  String userID="";
	
	public SessionVars(){
		this.lang =null;
		this.bodyDir=null;
		this.division = null;
		this.dep = null;
		this.projectPath = null;
		this.currentPageURL = null;
		this.userID = null;
	}
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getBodyDir() {
		return bodyDir;
	}

	public void setBodyDir(String bodyDir) {
		this.bodyDir = bodyDir;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getDep() {
		return dep;
	}

	public void setDep(String dep) {
		this.dep = dep;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getCurrentPageURL() {
		return currentPageURL;
	}

	public void setCurrentPageURL(String currentPageURL) {
		this.currentPageURL = currentPageURL;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

}
