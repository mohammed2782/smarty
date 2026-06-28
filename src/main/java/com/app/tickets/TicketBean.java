package com.app.tickets;

public class TicketBean {
	private int tktId;
	private String tktDate;
	private String tktTitleCode;
	private String tktTitle;
	private String tktOtherTitle;
	private String tktDesc;
	private String tktStatusCode;
	private String tktStatus;
	private String tktOwnerBranch;
	private String tktOwnerBranchName;
	private int tktOwnerBranchId;
	private int tktCaseId;
	private String tktReceiptNo;
	private String tktPriorityCode;
	private String tktPriority;
	private String tktCreatedByName;
	private int tktCreatedById; 
	private int chatsInTicketNotSeen;
	private int assignedAgent;
	private String oriReceiptNo;
	private String reOpened = "N";
	private String createdFromSystem;
	
	public int getTktId() {
		return tktId;
	}
	public void setTktId(int tktId) {
		this.tktId = tktId;
	}
	public String getTktDate() {
		return tktDate;
	}
	public void setTktDate(String tktDate) {
		this.tktDate = tktDate;
	}
	public String getTktTitleCode() {
		return tktTitleCode;
	}
	public void setTktTitleCode(String tktTitleCode) {
		this.tktTitleCode = tktTitleCode;
	}
	public String getTktTitle() {
		return tktTitle;
	}
	public void setTktTitle(String tktTitle) {
		this.tktTitle = tktTitle;
	}
	public String getTktOtherTitle() {
		return tktOtherTitle;
	}
	public void setTktOtherTitle(String tktOtherTitle) {
		this.tktOtherTitle = tktOtherTitle;
	}
	public String getTktDesc() {
		return tktDesc;
	}
	public void setTktDesc(String tktDesc) {
		this.tktDesc = tktDesc;
	}
	public String getTktStatusCode() {
		return tktStatusCode;
	}
	public void setTktStatusCode(String tktStatusCode) {
		this.tktStatusCode = tktStatusCode;
	}
	public String getTktStatus() {
		return tktStatus;
	}
	public void setTktStatus(String tktStatus) {
		this.tktStatus = tktStatus;
	}
	public String getTktOwnerBranch() {
		return tktOwnerBranch;
	}
	public void setTktOwnerBranch(String tktOwnerBranch) {
		this.tktOwnerBranch = tktOwnerBranch;
	}
	public String getTktOwnerBranchName() {
		return tktOwnerBranchName;
	}
	public void setTktOwnerBranchName(String tktOwnerBranchName) {
		this.tktOwnerBranchName = tktOwnerBranchName;
	}
	public int getTktCaseId() {
		return tktCaseId;
	}
	public void setTktCaseId(int tktCaseId) {
		this.tktCaseId = tktCaseId;
	}
	public String getTktReceiptNo() {
		return tktReceiptNo;
	}
	public void setTktReceiptNo(String tktReceiptNo) {
		this.tktReceiptNo = tktReceiptNo;
	}
	public String getTktPriorityCode() {
		return tktPriorityCode;
	}
	public void setTktPriorityCode(String tktPriorityCode) {
		this.tktPriorityCode = tktPriorityCode;
	}
	public String getTktPriority() {
		return tktPriority;
	}
	public void setTktPriority(String tktPriority) {
		this.tktPriority = tktPriority;
	}
	public String getTktCreatedByName() {
		return tktCreatedByName;
	}
	public void setTktCreatedByName(String tktCreatedByName) {
		this.tktCreatedByName = tktCreatedByName;
	}
	public int getTktCreatedById() {
		return tktCreatedById;
	}
	public void setTktCreatedById(int tktCreatedById) {
		this.tktCreatedById = tktCreatedById;
	}
	public int getChatsInTicketNotSeen() {
		return chatsInTicketNotSeen;
	}
	public void setChatsInTicketNotSeen(int chatsInTicketNotSeen) {
		this.chatsInTicketNotSeen = chatsInTicketNotSeen;
	}
	public int getAssignedAgent() {
		return assignedAgent;
	}
	public void setAssignedAgent(int assignedAgent) {
		this.assignedAgent = assignedAgent;
	}
	public String getOriReceiptNo() {
		return oriReceiptNo;
	}
	public void setOriReceiptNo(String oriReceiptNo) {
		this.oriReceiptNo = oriReceiptNo;
	}
	public int getTktOwnerBranchId() {
		return tktOwnerBranchId;
	}
	public void setTktOwnerBranchId(int tktOwnerBranchId) {
		this.tktOwnerBranchId = tktOwnerBranchId;
	}
	public String getReOpened() {
		return reOpened;
	}
	public void setReOpened(String reOpened) {
		this.reOpened = reOpened;
	}
	public String getCreatedFromSystem() {
		return createdFromSystem;
	}
	public void setCreatedFromSystem(String createdFromSystem) {
		this.createdFromSystem = createdFromSystem;
	}
	
}
