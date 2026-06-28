package com.app.bussframework;

import java.util.HashMap;

public class DlvAgentManifestBean {
	private int dlvAgentId;
	private String dlvAgentName;
	private int manifestId;
	private String manifestDate;
	private int totalCases;
	private int finalizedCases;
	private HashMap<String,StageBean> stagesMap;
	public int getDlvAgentId() {
		return dlvAgentId;
	}
	public void setDlvAgentId(int dlvAgentId) {
		this.dlvAgentId = dlvAgentId;
	}
	public String getDlvAgentName() {
		return dlvAgentName;
	}
	public void setDlvAgentName(String dlvAgentName) {
		this.dlvAgentName = dlvAgentName;
	}
	public int getManifestId() {
		return manifestId;
	}
	public void setManifestId(int manifestId) {
		this.manifestId = manifestId;
	}
	public String getManifestDate() {
		return manifestDate;
	}
	public void setManifestDate(String manifestDate) {
		this.manifestDate = manifestDate;
	}
	public int getTotalCases() {
		return totalCases;
	}
	public void setTotalCases(int totalCases) {
		this.totalCases = totalCases;
	}
	public int getFinalizedCases() {
		return finalizedCases;
	}
	public void setFinalizedCases(int finalizedCases) {
		this.finalizedCases = finalizedCases;
	}
	public HashMap<String, StageBean> getStagesMap() {
		return stagesMap;
	}
	public void setStagesMap(HashMap<String, StageBean> stagesMap) {
		this.stagesMap = stagesMap;
	}
	
}
