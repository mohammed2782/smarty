package com.app.beans;

import java.util.ArrayList;

import com.app.cases.CaseInformation;

public class LiaisonAgentBackBean {
	private String liaisonName;
	private String backDate;
	private String fromBranch;
	private String toBranch;
	private String backBy;
	private String rmk;
	private int totShipments;
	private int pathId;
	
	private ArrayList<CaseInformation> shipments;
	
	
	public String getRmk() {
		return rmk;
	}

	public void setRmk(String rmk) {
		this.rmk = rmk;
	}

	public int getTotShipments() {
		return totShipments;
	}

	public void setTotShipments(int totShipments) {
		this.totShipments = totShipments;
	}

	public int getPathId() {
		return pathId;
	}

	public void setPathId(int pathId) {
		this.pathId = pathId;
	}



	public String getLiaisonName() {
		return liaisonName;
	}

	public void setLiaisonName(String liaisonName) {
		this.liaisonName = liaisonName;
	}

	public String getBackDate() {
		return backDate;
	}

	public void setBackDate(String backDate) {
		this.backDate = backDate;
	}

	public String getFromBranch() {
		return fromBranch;
	}

	public void setFromBranch(String fromBranch) {
		this.fromBranch = fromBranch;
	}

	public String getToBranch() {
		return toBranch;
	}

	public void setToBranch(String toBranch) {
		this.toBranch = toBranch;
	}

	public String getBackBy() {
		return backBy;
	}

	public void setBackBy(String backBy) {
		this.backBy = backBy;
	}

	public ArrayList<CaseInformation> getShipments() {
		return shipments;
	}

	public void setShipments(ArrayList<CaseInformation> shipments) {
		this.shipments = shipments;
	}
	
	
}
