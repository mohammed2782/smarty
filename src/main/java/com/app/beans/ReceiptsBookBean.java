package com.app.beans;

public class ReceiptsBookBean {
	private int id;
	private int setId;
	private String setPrefix;
	private int bookNo;
	private boolean used;
	private int assignedMasterCustomer;
	private int assignedCustomer;
	private boolean isTherePaymentMadeForAnyReceipt;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getSetPrefix() {
		return setPrefix;
	}
	public void setSetPrefix(String setPrefix) {
		this.setPrefix = setPrefix;
	}
	public int getBookNo() {
		return bookNo;
	}
	public void setBookNo(int bookNo) {
		this.bookNo = bookNo;
	}
	public boolean isUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
	public int getAssignedMasterCustomer() {
		return assignedMasterCustomer;
	}
	public void setAssignedMasterCustomer(int assignedMasterCustomer) {
		this.assignedMasterCustomer = assignedMasterCustomer;
	}
	public int getSetId() {
		return setId;
	}
	public void setSetId(int setId) {
		this.setId = setId;
	}
	public int getAssignedCustomer() {
		return assignedCustomer;
	}
	public void setAssignedCustomer(int assignedCustomer) {
		this.assignedCustomer = assignedCustomer;
	}
	public boolean isTherePaymentMadeForAnyReceipt() {
		return isTherePaymentMadeForAnyReceipt;
	}
	public void setTherePaymentMadeForAnyReceipt(boolean isTherePaymentMadeForAnyReceipt) {
		this.isTherePaymentMadeForAnyReceipt = isTherePaymentMadeForAnyReceipt;
	}
}
