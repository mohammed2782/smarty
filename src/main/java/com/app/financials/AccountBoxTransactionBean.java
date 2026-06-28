package com.app.financials;

public class AccountBoxTransactionBean {
	private int id;
	private int paymentIdInSource;
	private String sourceTableOfPayment;
	private long paymentIqd;
	private long paymentUsd;
	private PaymentImpactOnSafe paymentImpactOnSafe; //DB - CR - NO_IMPACT
	private int branchId;
	private String paymentDesc;
	private String lookUpPkColName;
	private int LookUpPkColVal;
	private String lookupTableColumnDesc;
	private String lookupTable;
	
	private int movedToSafeId;
	
	private String entityFullNameWithTransId; // like agent name, customer name, expenses name, pickupagent name ..etc + trans id from p_fin_transactions
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getPaymentIqd() {
		return paymentIqd;
	}
	public void setPaymentIqd(long paymentIqd) {
		this.paymentIqd = paymentIqd;
	}
	public long getPaymentUsd() {
		return paymentUsd;
	}
	public void setPaymentUsd(long paymentUsd) {
		this.paymentUsd = paymentUsd;
	}
	public int getBranchId() {
		return branchId;
	}
	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}
	public String getPaymentDesc() {
		return paymentDesc;
	}
	public void setPaymentDesc(String paymentDesc) {
		this.paymentDesc = paymentDesc;
	}
	
	public String getLookupTableColumnDesc() {
		return lookupTableColumnDesc;
	}
	public void setLookupTableColumnDesc(String lookupTableColumnDesc) {
		this.lookupTableColumnDesc = lookupTableColumnDesc;
	}
	public String getLookupTable() {
		return lookupTable;
	}
	public void setLookupTable(String lookupTable) {
		this.lookupTable = lookupTable;
	}
	public int getPaymentIdInSource() {
		return paymentIdInSource;
	}
	public void setPaymentIdInSource(int paymentIdInSource) {
		this.paymentIdInSource = paymentIdInSource;
	}
	public String getSourceTableOfPayment() {
		return sourceTableOfPayment;
	}
	public void setSourceTableOfPayment(String sourceTableOfPayment) {
		this.sourceTableOfPayment = sourceTableOfPayment;
	}
	
	public PaymentImpactOnSafe getPaymentImpactOnSafe() {
		return paymentImpactOnSafe;
	}
	public void setPaymentImpactOnSafe(PaymentImpactOnSafe paymentImpactOnSafe) {
		this.paymentImpactOnSafe = paymentImpactOnSafe;
	}
	public String getLookUpPkColName() {
		return lookUpPkColName;
	}
	public void setLookUpPkColName(String lookUpPkColName) {
		this.lookUpPkColName = lookUpPkColName;
	}
	public int getLookUpPkColVal() {
		return LookUpPkColVal;
	}
	public void setLookUpPkColVal(int lookUpPkColVal) {
		LookUpPkColVal = lookUpPkColVal;
	}
	public String getEntityFullNameWithTransId() {
		return entityFullNameWithTransId;
	}
	public void setEntityFullNameWithTransId(String entityFullNameWithTransId) {
		this.entityFullNameWithTransId = entityFullNameWithTransId;
	}
	public int getMovedToSafeId() {
		return movedToSafeId;
	}
	public void setMovedToSafeId(int movedToSafeId) {
		this.movedToSafeId = movedToSafeId;
	}
	
	
	
}
