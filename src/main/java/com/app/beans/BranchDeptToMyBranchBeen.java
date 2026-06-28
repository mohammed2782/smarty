package com.app.beans;

import java.util.ArrayList;

import com.app.cases.CaseInformation;

public class BranchDeptToMyBranchBeen {
	private String senderBranchName;
	private String receiverBranchName;
	
	private double netAmt;
	private int totShipments; 
	
	private ArrayList<CaseInformation> shipments;
	
	
	public String getSenderBranchName() {
		return senderBranchName;
	}

	public void setSenderBranchName(String senderBranchName) {
		this.senderBranchName = senderBranchName;
	}

	public String getReceiverBranchName() {
		return receiverBranchName;
	}

	public void setReceiverBranchName(String receiverBranchName) {
		this.receiverBranchName = receiverBranchName;
	}

	public double getNetAmt() {
		return netAmt;
	}

	public void setNetAmt(double netAmt) {
		this.netAmt = netAmt;
	}

	public ArrayList<CaseInformation> getShipments() {
		return shipments;
	}

	public void setShipments(ArrayList<CaseInformation> shipments) {
		this.shipments = shipments;
	}

	public int getTotShipments() {
		return totShipments;
	}

	public void setTotShipments(int totShipments) {
		this.totShipments = totShipments;
	}


	
	
	

}
