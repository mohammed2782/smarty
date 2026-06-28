package com.app.beans;

import java.util.LinkedHashMap;

import jakarta.xml.bind.annotation.XmlRootElement;



@XmlRootElement
public class ActionsBean {
	public ActionsBean() {}
	private String code;
	private String text;
	private String icon;
	private String iconColor;
	private String sendNotifications;
	private String notificationTitleForCustomer;
	private String notificationBodyForCustomer;
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getIconColor() {
		return iconColor;
	}
	public void setIconColor(String iconColor) {
		this.iconColor = iconColor;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getSendNotifications() {
		return sendNotifications;
	}
	public void setSendNotifications(String sendNotifications) {
		this.sendNotifications = sendNotifications;
	}
	public String getNotificationTitleForCustomer() {
		return notificationTitleForCustomer;
	}
	public void setNotificationTitleForCustomer(String notificationTitleForCustomer) {
		this.notificationTitleForCustomer = notificationTitleForCustomer;
	}
	public String getNotificationBodyForCustomer() {
		return notificationBodyForCustomer;
	}
	public void setNotificationBodyForCustomer(String notificationBodyForCustomer) {
		this.notificationBodyForCustomer = notificationBodyForCustomer;
	}
	
	
}
