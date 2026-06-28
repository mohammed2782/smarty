package com.app.incomeoutcome;

import java.util.ArrayList;

import com.app.cases.CaseInformation;
import com.app.financials.StandardTransactionBean;

public class CustomerPaymentBean {
	private String customerName;
	private int masterCustId;
	private String pmtId;
	private String pmtDate;
	private String pmtRmk;
	private double pmtAmtIqd;
	private double pmtAmtUsd;
	private long balance;
	private long credit;
	private StandardTransactionBean standardTransactionBean;
	private ArrayList<CaseInformation> shipments;
	
	
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getPmtId() {
		return pmtId;
	}
	public void setPmtId(String pmtId) {
		this.pmtId = pmtId;
	}
	public String getPmtDate() {
		return pmtDate;
	}
	public void setPmtDate(String pmtDate) {
		this.pmtDate = pmtDate;
	}
	public String getPmtRmk() {
		return pmtRmk;
	}
	public void setPmtRmk(String pmtRmk) {
		this.pmtRmk = pmtRmk;
	}
	public ArrayList<CaseInformation> getShipments() {
		return shipments;
	}
	public void setShipments(ArrayList<CaseInformation> shipments) {
		this.shipments = shipments;
	}
	public long getBalance() {
		return balance;
	}
	public void setBalance(long balance) {
		this.balance = balance;
	}
	public int getMasterCustId() {
		return masterCustId;
	}
	public void setMasterCustId(int masterCustId) {
		this.masterCustId = masterCustId;
	}
	public long getCredit() {
		return credit;
	}
	public void setCredit(long credit) {
		this.credit = credit;
	}
	public double getPmtAmtIqd() {
		return pmtAmtIqd;
	}
	public void setPmtAmtIqd(double pmtAmtIqd) {
		this.pmtAmtIqd = pmtAmtIqd;
	}
	public double getPmtAmtUsd() {
		return pmtAmtUsd;
	}
	public void setPmtAmtUsd(double pmtAmtUsd) {
		this.pmtAmtUsd = pmtAmtUsd;
	}
	public StandardTransactionBean getStandardTransactionBean() {
		return standardTransactionBean;
	}
	public void setStandardTransactionBean(StandardTransactionBean standardTransactionBean) {
		this.standardTransactionBean = standardTransactionBean;
	}
}
