package com.app.beans;

import java.util.ArrayList;

import com.app.cases.CaseInformation;

public class MasterCustomerShipmentBackBean {
	private String pickupAgentName;
	private String masterCustName;
	private String backedDate;
	private String backedRmk;
	private ArrayList<CaseInformation> shipments;
	
	public String getMasterCustName() {
		return masterCustName;
	}
	public void setMasterCustName(String masterCustName) {
		this.masterCustName = masterCustName;
	}
	public String getBackedDate() {
		return backedDate;
	}
	public void setBackedDate(String backedDate) {
		this.backedDate = backedDate;
	}
	public String getBackedRmk() {
		return backedRmk;
	}
	public void setBackedRmk(String backedRmk) {
		this.backedRmk = backedRmk;
	}
	public ArrayList<CaseInformation> getShipments() {
		return shipments;
	}
	public void setShipments(ArrayList<CaseInformation> shipments) {
		this.shipments = shipments;
	}
	public String getPickupAgentName() {
		return pickupAgentName;
	}
	public void setPickupAgentName(String pickupAgentName) {
		this.pickupAgentName = pickupAgentName;
	}
	
	

}
