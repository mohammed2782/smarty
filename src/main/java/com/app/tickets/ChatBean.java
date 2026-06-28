package com.app.tickets;

import java.util.List;

public class ChatBean {
	private int chatId;
	private int ticketId;
	private String chatWithName;
	private int chatWithId;
	private String pushNotificationPlayerId;
	private String chatWithRank;
	private String chatStartedDate;
	private String imgUrl;
	private int startedById;
	private String imgReplacementLetters;
	private String seenByControl;
	private int masterCustomer;
	private int chatStartedByBranch;
	
	private List<ChatMsgBean> msgList ;
	public int getChatId() {
		return chatId;
	}
	public void setChatId(int chatId) {
		this.chatId = chatId;
	}
	public int getTicketId() {
		return ticketId;
	}
	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}
	public int getChatWithId() {
		return chatWithId;
	}
	public void setChatWithId(int chatWithId) {
		this.chatWithId = chatWithId;
	}
	
	public String getChatStartedDate() {
		return chatStartedDate;
	}
	public void setChatStartedDate(String chatStartedDate) {
		this.chatStartedDate = chatStartedDate;
	}
	public List<ChatMsgBean> getMsgList() {
		return msgList;
	}
	public void setMsgList(List<ChatMsgBean> msgList) {
		this.msgList = msgList;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	public String getChatWithName() {
		return chatWithName;
	}
	public void setChatWithName(String chatWithName) {
		this.chatWithName = chatWithName;
	}
	public String getChatWithRank() {
		return chatWithRank;
	}
	public void setChatWithRank(String chatWithRank) {
		this.chatWithRank = chatWithRank;
	}
	public int getStartedById() {
		return startedById;
	}
	public void setStartedById(int startedById) {
		this.startedById = startedById;
	}
	public String getImgReplacementLetters() {
		return imgReplacementLetters;
	}
	public void setImgReplacementLetters(String imgReplacementLetters) {
		this.imgReplacementLetters = imgReplacementLetters;
	}
	public String getPushNotificationPlayerId() {
		return pushNotificationPlayerId;
	}
	public void setPushNotificationPlayerId(String pushNotificationPlayerId) {
		this.pushNotificationPlayerId = pushNotificationPlayerId;
	}
	public String getSeenByControl() {
		return seenByControl;
	}
	public void setSeenByControl(String seenByControl) {
		this.seenByControl = seenByControl;
	}
	public int getMasterCustomer() {
		return masterCustomer;
	}
	public void setMasterCustomer(int masterCustomer) {
		this.masterCustomer = masterCustomer;
	}
	public int getChatStartedByBranch() {
		return chatStartedByBranch;
	}
	public void setChatStartedByBranch(int chatStartedByBranch) {
		this.chatStartedByBranch = chatStartedByBranch;
	}
	
}
