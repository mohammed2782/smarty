package com.app.incomeoutcome;

public class CustomerStatementBean {
	private String custName;
	private String custId;
	private double balance;
	private String tranName;
	private double credit;
	private double debit;
	private String tranDate;
	private String fromDate;
	private String toDate;
	private String rmk;
	private double totalReceiptsAmt;
	private double amtPaidActually;
	private int branchId;
	
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public String getTranName() {
		return tranName;
	}
	public void setTranName(String tranName) {
		this.tranName = tranName;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}
	public double getDebit() {
		return debit;
	}
	public void setDebit(double debit) {
		this.debit = debit;
	}
	public String getTranDate() {
		return tranDate;
	}
	public void setTranDate(String tranDate) {
		this.tranDate = tranDate;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getRmk() {
		return rmk;
	}
	public void setRmk(String rmk) {
		this.rmk = rmk;
	}
	public double getTotalReceiptsAmt() {
		return totalReceiptsAmt;
	}
	public void setTotalReceiptsAmt(double totalReceiptsAmt) {
		this.totalReceiptsAmt = totalReceiptsAmt;
	}
	public double getAmtPaidActually() {
		return amtPaidActually;
	}
	public void setAmtPaidActually(double amtPaidActually) {
		this.amtPaidActually = amtPaidActually;
	}
	public int getBranchId() {
		return branchId;
	}
	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}
}
