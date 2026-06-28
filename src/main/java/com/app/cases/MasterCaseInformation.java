package com.app.cases;

public class MasterCaseInformation {
	private int masterCustomerId;
	private String masterCustHp1;
	/*private int caseMasterId;*/
	private int custId;
	private String custName;
	private String hp;
	private String mapLat;
	private String mapLongt;

	private int branch;
	

	private String city;
	private String district;
	private String parties = "N";
	private String locationDetails;
	
	public String toString() {
		String s = "custName="+this.custName;
		s +=", hp="+this.hp;
		s +=", mapLat="+this.mapLat;
		s +=", mapLongt="+this.mapLongt;
		s +=", branch="+this.branch;
		s +=", city="+this.city;
		s +=", district="+this.district;
		s +=", parties="+this.parties;
		s +=", locationDetails="+this.locationDetails;
		return s;
	}
	
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getHp() {
		return hp;
	}
	public void setHp(String hp) {
		this.hp = hp;
	}
	public String getMapLat() {
		return mapLat;
	}
	public void setMapLat(String mapLat) {
		this.mapLat = mapLat;
	}
	public String getMapLongt() {
		return mapLongt;
	}
	public void setMapLongt(String mapLongt) {
		this.mapLongt = mapLongt;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getParties() {
		return parties;
	}
	public void setParties(String parties) {
		this.parties = parties;
	}
	public String getLocationDetails() {
		return locationDetails;
	}
	public void setLocationDetails(String locationDetails) {
		this.locationDetails = locationDetails;
	}
/*
	public int getCaseMasterId() {
		return caseMasterId;
	}

	public void setCaseMasterId(int caseMasterId) {
		this.caseMasterId = caseMasterId;
	}*/
	public int getBranch() {
		return branch;
	}

	public void setBranch(int branch) {
		this.branch = branch;
	}

	public int getMasterCustomerId() {
		return masterCustomerId;
	}

	public void setMasterCustomerId(int masterCustomerId) {
		this.masterCustomerId = masterCustomerId;
	}

	public int getCustId() {
		return custId;
	}

	public void setCustId(int custId) {
		this.custId = custId;
	}

	public String getMasterCustHp1() {
		return masterCustHp1;
	}

	public void setMasterCustHp1(String masterCustHp1) {
		this.masterCustHp1 = masterCustHp1;
	}
}
