package com.app.financials;

public class SafePaymentTypeMetaInfoBean {
	private PaymentType dbOrCr;
	private PaymentImpactOnSafe safeImpact;
	private String name;
	private FinOperationCategory finCategore;
	private FinOperationEntity   finEntity;
	private FinOperationCode	 finOperationCode;
	
	public FinOperationCategory getFinCategore() {
		return finCategore;
	}
	public void setFinCategore(FinOperationCategory finCategore) {
		this.finCategore = finCategore;
	}
	public FinOperationEntity getFinEntity() {
		return finEntity;
	}
	public void setFinEntity(FinOperationEntity finEntity) {
		this.finEntity = finEntity;
	}
	public FinOperationCode getFinOperationCode() {
		return finOperationCode;
	}
	public void setFinOperationCode(FinOperationCode finOperationCode) {
		this.finOperationCode = finOperationCode;
	}
	public PaymentType getDbOrCr() {
		return dbOrCr;
	}
	public void setDbOrCr(PaymentType dbOrCr) {
		this.dbOrCr = dbOrCr;
	}
	public PaymentImpactOnSafe getSafeImpact() {
		return safeImpact;
	}
	public void setSafeImpact(PaymentImpactOnSafe safeImpact) {
		this.safeImpact = safeImpact;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
