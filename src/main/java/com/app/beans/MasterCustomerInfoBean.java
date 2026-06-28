package com.app.beans;

public class MasterCustomerInfoBean {
	private int id;
	private String name;
	private int belongToBranch;
	private int pickUpagent;
	private String phone1;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getBelongToBranch() {
		return belongToBranch;
	}
	public void setBelongToBranch(int belongToBranch) {
		this.belongToBranch = belongToBranch;
	}
	public int getPickUpagent() {
		return pickUpagent;
	}
	public void setPickUpagent(int pickUpagent) {
		this.pickUpagent = pickUpagent;
	}
	public String getPhone1() {
		return phone1;
	}
	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}
}
