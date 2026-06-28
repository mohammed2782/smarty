package com.app.financials;

public class AccountantBoxBean {
	private int boxId;
	private long currentBalanceIqd;
	private long currentBalanceUsd;
	private int boxUserId;
	private String boxUserName;
	private int boxInBranch;
	
	
	public int getBoxId() {
		return boxId;
	}
	public void setBoxId(int boxId) {
		this.boxId = boxId;
	}
	public int getBoxUserId() {
		return boxUserId;
	}
	public void setBoxUserId(int boxUserId) {
		this.boxUserId = boxUserId;
	}
	public String getBoxUserName() {
		return boxUserName;
	}
	public void setBoxUserName(String boxUserName) {
		this.boxUserName = boxUserName;
	}
	public int getBoxInBranch() {
		return boxInBranch;
	}
	public void setBoxInBranch(int boxInBranch) {
		this.boxInBranch = boxInBranch;
	}
	public long getCurrentBalanceIqd() {
		return currentBalanceIqd;
	}
	public void setCurrentBalanceIqd(long currentBalanceIqd) {
		this.currentBalanceIqd = currentBalanceIqd;
	}
	public long getCurrentBalanceUsd() {
		return currentBalanceUsd;
	}
	public void setCurrentBalanceUsd(long currentBalanceUsd) {
		this.currentBalanceUsd = currentBalanceUsd;
	}
}
