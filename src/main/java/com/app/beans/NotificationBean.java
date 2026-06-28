package com.app.beans;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NotificationBean {
	public NotificationBean() {}
	private Integer id;
	private String title;
	private String desc;
	private Integer userId;
	private Integer agentId;
	private Integer customerId;
	private String type;
	private int typeVal;
	private String dateTime;
	private String userSaw;
	private String userClicked;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getAgentId() {
		return agentId;
	}
	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getUserSaw() {
		return userSaw;
	}
	public void setUserSaw(String userSaw) {
		this.userSaw = userSaw;
	}
	public String getUserClicked() {
		return userClicked;
	}
	public void setUserClicked(String userClicked) {
		this.userClicked = userClicked;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getTypeVal() {
		return typeVal;
	}
	public void setTypeVal(int typeVal) {
		this.typeVal = typeVal;
	}
	
}
