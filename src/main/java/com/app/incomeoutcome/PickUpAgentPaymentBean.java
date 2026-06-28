package com.app.incomeoutcome;

import java.util.ArrayList;

import com.app.cases.CaseInformation;
import com.app.financials.StandardTransactionBean;

public class PickUpAgentPaymentBean {
	private String pickUpAgentName;
	private String pmtId;
	private String pmtDate;
	private String pmtRmk;
	private double pmtAmt;
	private StandardTransactionBean standardTransactionBean;
	private ArrayList<CaseInformation> shipments;
	
	
	
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
	public double getPmtAmt() {
		return pmtAmt;
	}
	public void setPmtAmt(double pmtAmt) {
		this.pmtAmt = pmtAmt;
	}
	
	public ArrayList<CaseInformation> getShipments() {
		return shipments;
	}
	public void setShipments(ArrayList<CaseInformation> shipments) {
		this.shipments = shipments;
	}
	public String getPickUpAgentName() {
		return pickUpAgentName;
	}
	public void setPickUpAgentName(String pickUpAgentName) {
		this.pickUpAgentName = pickUpAgentName;
	}
	public StandardTransactionBean getStandardTransactionBean() {
		return standardTransactionBean;
	}
	public void setStandardTransactionBean(StandardTransactionBean standardTransactionBean) {
		this.standardTransactionBean = standardTransactionBean;
	}
}
