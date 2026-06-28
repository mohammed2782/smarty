package com.app.beans;

public class CustomerInfoBean {
	private int custId;
	private int masterCustId;
	private String custName;
	private int custBelongToBranch;
	private int pickUpAgent;
	private String custHp;
	public int getCustId() {
		return custId;
	}
	public void setCustId(int custId) {
		this.custId = custId;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public int getPickUpAgent() {
		return pickUpAgent;
	}
	public void setPickUpAgent(int pickUpAgent) {
		this.pickUpAgent = pickUpAgent;
	}
	public int getCustBelongToBranch() {
		return custBelongToBranch;
	}
	public void setCustBelongToBranch(int custBelongToBranch) {
		this.custBelongToBranch = custBelongToBranch;
	}
	public String getCustHp() {
		return custHp;
	}
	public void setCustHp(String custHp) {
		this.custHp = custHp;
	}
	public int getMasterCustId() {
		return masterCustId;
	}
	public void setMasterCustId(int masterCustId) {
		this.masterCustId = masterCustId;
	}
}
