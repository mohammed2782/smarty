package com.app.tickets;

public class ChatMsgBean {
	private int msgId;
	private String msg;
	private String msgDate;
	private int senderId;
	private String senderName;
	private String senderRank;
	private String notificationSent;
	private String seenByReceiver;
	private String msgFromController;
	private String communitcationMedium;
	private int chatId;
	
	public int getMsgId() {
		return msgId;
	}
	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getMsgDate() {
		return msgDate;
	}
	public void setMsgDate(String msgDate) {
		this.msgDate = msgDate;
	}
	public String getNotificationSent() {
		return notificationSent;
	}
	public void setNotificationSent(String notificationSent) {
		this.notificationSent = notificationSent;
	}
	public String getSeenByReceiver() {
		return seenByReceiver;
	}
	public void setSeenByReceiver(String seenByReceiver) {
		this.seenByReceiver = seenByReceiver;
	}
	public int getSenderId() {
		return senderId;
	}
	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}
	
	public String getMsgFromController() {
		return msgFromController;
	}
	public void setMsgFromController(String msgFromController) {
		this.msgFromController = msgFromController;
	}
	public String getCommunitcationMedium() {
		return communitcationMedium;
	}
	public void setCommunitcationMedium(String communitcationMedium) {
		this.communitcationMedium = communitcationMedium;
	}
	public int getChatId() {
		return chatId;
	}
	public void setChatId(int chatId) {
		this.chatId = chatId;
	}
	public String getSenderRank() {
		return senderRank;
	}
	public void setSenderRank(String senderRank) {
		this.senderRank = senderRank;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
}
