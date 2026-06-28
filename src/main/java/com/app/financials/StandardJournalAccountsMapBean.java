package com.app.financials;

public class StandardJournalAccountsMapBean {
	private StandardFinType finType; //CREDITOR || DEBITOR
	private String accountNumber;
	private String operationEntity; // AGENT || PIKUPAGENT || CUSTOEMR || BRANCH || EXPENSES, should be mapped to kbgeneral kbcat1
	private String operationCat; // PMTTYPE , etc should be mapped to kbgeneral kbcat2
	private String operationCode; // should be mapped to kbgeneral code;
	private long amount;
	private StandardFinCurrency currency; //IQD || USD
	private int seq;
	private int branchId;
	private int createdBy;
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getOperationEntity() {
		return operationEntity;
	}
	public void setOperationEntity(String operationEntity) {
		this.operationEntity = operationEntity;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getOperationCat() {
		return operationCat;
	}
	public void setOperationCat(String operationCat) {
		this.operationCat = operationCat;
	}
	public String getOperationCode() {
		return operationCode;
	}
	public void setOperationCode(String operationCode) {
		this.operationCode = operationCode;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public StandardFinCurrency getCurrency() {
		return currency;
	}
	public void setCurrency(StandardFinCurrency currency) {
		this.currency = currency;
	}
	public int getBranchId() {
		return branchId;
	}
	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}
	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	public StandardFinType getFinType() {
		return finType;
	}
	public void setFinType(StandardFinType finType) {
		this.finType = finType;
	}
}
