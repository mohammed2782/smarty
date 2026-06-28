package com.app.financials;

public class StandardMultiEntryBean {
	private int transactionId;
	private double debitor;
	private double creditor;
	private String account;
	private String currency;
	private String desc;
	private int createdBy;
	private int branchId;
	public int getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}
	public double getDebitor() {
		return debitor;
	}
	public void setDebitor(double debitor) {
		this.debitor = debitor;
	}
	public double getCreditor() {
		return creditor;
	}
	public void setCreditor(double creditor) {
		this.creditor = creditor;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	public int getBranchId() {
		return branchId;
	}
	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}
}
