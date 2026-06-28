package com.app.bussframework;

public class PathBean {
	private int pathId;
	private String toState;
	private int fromStore;
	private int toStore;
	private int liasionId;
	private double cost;
	private int caseId;
	
	public int getPathId() {
		return pathId;
	}
	public void setPathId(int pathId) {
		this.pathId = pathId;
	}
	public String getToState() {
		return toState;
	}
	public void setToState(String toState) {
		this.toState = toState;
	}
	public int getFromStore() {
		return fromStore;
	}
	public void setFromStore(int fromStore) {
		this.fromStore = fromStore;
	}
	public int getToStore() {
		return toStore;
	}
	public void setToStore(int toStore) {
		this.toStore = toStore;
	}
	public int getLiasionId() {
		return liasionId;
	}
	public void setLiasionId(int liasionId) {
		this.liasionId = liasionId;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public int getCaseId() {
		return caseId;
	}
	public void setCaseId(int caseId) {
		this.caseId = caseId;
	}
}
