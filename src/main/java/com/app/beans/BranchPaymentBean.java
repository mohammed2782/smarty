package com.app.beans;

import java.util.ArrayList;

import com.app.cases.CaseInformation;
import com.app.financials.StandardTransactionBean;

public class BranchPaymentBean {
	private String imageUrl;
	private int paymentId;
	
	private int payerBranch;
	private String payerBranchName;
	private String paymentDate;
	
	private long receiptsAmountIqd;
	private long receiptsAmountUsd;
	
	private long paidAmountIqd;
	private long paidAmountUsd;
	
	private String payerBranchRmk;
	private int paidBy;
	private String paidByName;
	
	private String receiverBranchName;
	private int receiverBranchId;
	private String receiverBranchRmk;
	private String receivedDate;
	private String received;
	private long receivedAmountIqd;
	private long receivedAmountUsd;
	
	private int receivedBy;
	private int paymentIsLateByXdays;
	private int maxAllowedLatePayment;
	
	private long debtIqd;
	private long creditIqd;
	
	private long debtUsd;
	private long creditUsd;
	
	// this bean should cancel the above attributes
	private StandardTransactionBean standardTransactionBean;
	private ArrayList<CaseInformation> shipments;
	
	public int getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(int paymentId) {
		this.paymentId = paymentId;
	}
	public int getPayerBranch() {
		return payerBranch;
	}
	public void setPayerBranch(int payerBranch) {
		this.payerBranch = payerBranch;
	}
	public String getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	
	public String getPayerBranchRmk() {
		return payerBranchRmk;
	}
	public void setPayerBranchRmk(String payerBranchRmk) {
		this.payerBranchRmk = payerBranchRmk;
	}
	public int getPaidBy() {
		return paidBy;
	}
	public void setPaidBy(int paidBy) {
		this.paidBy = paidBy;
	}
	public String getReceiverBranchRmk() {
		return receiverBranchRmk;
	}
	public void setReceiverBranchRmk(String receiverBranchRmk) {
		this.receiverBranchRmk = receiverBranchRmk;
	}
	public String getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}
	public String getReceived() {
		return received;
	}
	public void setReceived(String received) {
		this.received = received;
	}
	
	public int getReceivedBy() {
		return receivedBy;
	}
	public void setReceivedBy(int receivedBy) {
		this.receivedBy = receivedBy;
	}
	public long getReceiptsAmountIqd() {
		return receiptsAmountIqd;
	}
	public void setReceiptsAmountIqd(long receiptsAmountIqd) {
		this.receiptsAmountIqd = receiptsAmountIqd;
	}
	public long getReceiptsAmountUsd() {
		return receiptsAmountUsd;
	}
	public void setReceiptsAmountUsd(long receiptsAmountUsd) {
		this.receiptsAmountUsd = receiptsAmountUsd;
	}
	public long getPaidAmountIqd() {
		return paidAmountIqd;
	}
	public void setPaidAmountIqd(long paidAmountIqd) {
		this.paidAmountIqd = paidAmountIqd;
	}
	public long getPaidAmountUsd() {
		return paidAmountUsd;
	}
	public void setPaidAmountUsd(long paidAmountUsd) {
		this.paidAmountUsd = paidAmountUsd;
	}
	public long getDebtIqd() {
		return debtIqd;
	}
	public void setDebtIqd(long debtIqd) {
		this.debtIqd = debtIqd;
	}
	public long getCreditIqd() {
		return creditIqd;
	}
	public void setCreditIqd(long creditIqd) {
		this.creditIqd = creditIqd;
	}
	public long getDebtUsd() {
		return debtUsd;
	}
	public void setDebtUsd(long debtUsd) {
		this.debtUsd = debtUsd;
	}
	public long getCreditUsd() {
		return creditUsd;
	}
	public void setCreditUsd(long creditUsd) {
		this.creditUsd = creditUsd;
	}
	public String getPaidByName() {
		return paidByName;
	}
	public void setPaidByName(String paidByName) {
		this.paidByName = paidByName;
	}
	public String getPayerBranchName() {
		return payerBranchName;
	}
	public void setPayerBranchName(String payerBranchName) {
		this.payerBranchName = payerBranchName;
	}
	public int getReceiverBranchId() {
		return receiverBranchId;
	}
	public void setReceiverBranchId(int receiverBranchId) {
		this.receiverBranchId = receiverBranchId;
	}
	public String getReceiverBranchName() {
		return receiverBranchName;
	}
	public void setReceiverBranchName(String receiverBranchName) {
		this.receiverBranchName = receiverBranchName;
	}
	public ArrayList<CaseInformation> getShipments() {
		return shipments;
	}
	public void setShipments(ArrayList<CaseInformation> shipments) {
		this.shipments = shipments;
	}
	public long getReceivedAmountIqd() {
		return receivedAmountIqd;
	}
	public void setReceivedAmountIqd(long receivedAmountIqd) {
		this.receivedAmountIqd = receivedAmountIqd;
	}
	public long getReceivedAmountUsd() {
		return receivedAmountUsd;
	}
	public void setReceivedAmountUsd(long receivedAmountUsd) {
		this.receivedAmountUsd = receivedAmountUsd;
	}
	public int getPaymentIsLateByXdays() {
		return paymentIsLateByXdays;
	}
	public void setPaymentIsLateByXdays(int paymentIsLateByXdays) {
		this.paymentIsLateByXdays = paymentIsLateByXdays;
	}
	public int getMaxAllowedLatePayment() {
		return maxAllowedLatePayment;
	}
	public void setMaxAllowedLatePayment(int maxAllowedLatePayment) {
		this.maxAllowedLatePayment = maxAllowedLatePayment;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public StandardTransactionBean getStandardTransactionBean() {
		return standardTransactionBean;
	}
	public void setStandardTransactionBean(StandardTransactionBean standardTransactionBean) {
		this.standardTransactionBean = standardTransactionBean;
	}
}
