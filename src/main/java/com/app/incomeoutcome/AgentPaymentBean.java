package com.app.incomeoutcome;

import java.util.ArrayList;

import com.app.cases.CaseInformation;
import com.app.financials.StandardTransactionBean;

public class AgentPaymentBean {
	private int agentId;
	private String agentName;
	private String pmtId;
	private String pmtDate;
	private String pmtRmk;
//	private double agentShare;
//	private double casesTotalAmt;
//	private double amtReceived;
//	private double amtReaminaing;
//	private String pmtTypeCode;
//	private String pmtTypeDesc;
//	private double balanceBeforePayement;
	private StandardTransactionBean standardTransactionBean;
	private ArrayList<CaseInformation> shipments;

	public StandardTransactionBean getStandardTransactionBean() {
		return standardTransactionBean;
	}
	public void setStandardTransactionBean(StandardTransactionBean standardTransactionBean) {
		this.standardTransactionBean = standardTransactionBean;
	}
	public ArrayList<CaseInformation> getShipments() {
		return shipments;
	}
	public void setShipments(ArrayList<CaseInformation> shipments) {
		this.shipments = shipments;
	}
	public int getAgentId() {
		return agentId;
	}
	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
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
}
