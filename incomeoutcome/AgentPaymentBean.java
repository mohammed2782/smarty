package com.app.incomeoutcome;

import java.util.ArrayList;

import com.app.cases.CaseInformation;

public class AgentPaymentBean {
	private String agentName;
	private String pmtId;
	private String pmtDate;
	private String pmtRmk;
	private double pmtAmt;
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
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
}
